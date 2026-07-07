# AGENTS.md

This file provides guidance to AI coding agents (Claude Code, Codex, and others) when working with
code in this repository. It is the single source of truth; `CLAUDE.md` imports it.

## What this is

The **FISCO BCOS Java SDK (v3.x)** — a Java client library for the FISCO BCOS 3.0+ blockchain.
It is published as the Maven artifact `org.fisco-bcos.java-sdk:fisco-bcos-java-sdk`. It is a
**library, not an application**: there is no `main`. Consumers call `BcosSDK.build(...)` and use the
returned handles. The v3.x branch is **not** compatible with FISCO BCOS 2.x (that lives on `master-2.0`).

- Java 8 source/target (`sourceCompatibility = 1.8`). Do not use Java 9+ APIs.
- Root package: `org.fisco.bcos.sdk.v3`. Tests live under `org.fisco.bcos.sdk.v3.test.*`.

## Build, test, format

Uses the Gradle wrapper (Gradle 6.3). Use `./gradlew` (or `gradlew.bat` on Windows).

```bash
./gradlew build                 # compile + run unit tests + assemble jar
./gradlew test                  # unit tests only
./gradlew test --tests "*ABICodecTest"            # run a single test class (wildcard)
./gradlew test --tests "org.fisco.bcos.sdk.v3.test.codec.ABICodecTest"   # fully-qualified
./gradlew jacocoTestReport      # coverage report -> build/reports/jacoco/ (also runs via `check`)
./gradlew verifyGoogleJavaFormat  # check formatting (fails build if violated)
./gradlew googleJavaFormat      # auto-apply formatting
```

**Formatting is enforced**: Google Java Format, **AOSP style** (4-space indent). `*Test.java`,
`Test*.java`, and `Mock*.java` are excluded from formatting. `verifyGoogleJavaFormat` also installs
the repo's git pre-commit hook (`copyHooks` task copies from `hooks/`).

### Integration tests require a running chain

`./gradlew integrationTest` (EVM/Solidity) **cannot run without a live FISCO BCOS node**. CI
provisions this in `.ci/ci_check.sh`: it starts three local 4-node chains at once with
`build_chain.sh` on disjoint ports — a pinned v3.7.3 ECDSA chain (rpc 20200), an ECDSA chain on the
**latest release** (rpc 20210, tag auto-resolved from the GitHub `releases/latest` redirect), and an
SM-crypto chain on the latest release (rpc 20220). All chains run with **SSL disabled on the RPC
endpoint** (`disable_ssl=true` / `enable_ssl=false` in each node's `[rpc]` config), so the SDK
connects **without certificates** (`enableSsl = "false"` in the rendered
`src/integration-test/resources/config.toml`; `useSMCrypto` toggled per round). `integrationTest`
then runs once per chain. (`./gradlew integrationWasmTest` still exists but is no longer exercised
in CI.) Do not expect these tasks to pass in a bare checkout.

CI entrypoint is `.github/workflows/workflow.yml` → `.ci/ci_check.sh` (Linux/macOS run the three
integration rounds; Windows runs only `./gradlew.bat build`).

### Native dependency note

Node networking is **not** implemented in Java. It is delegated through JNI to native libraries
(`org.fisco-bcos:bcos-sdk-jni`, `org.fisco-bcos:fisco-bcos-tars-sdk`). Anything that actually talks to
a chain therefore requires a platform with the bundled native libs loadable, plus (in CI) OpenSSL 1.1 /
TASSL. Pure-Java work (crypto, codec, transaction assembly, contract wrappers) needs none of this.

## Architecture

### Entry point and lifecycle

`BcosSDK` (`org.fisco.bcos.sdk.v3.BcosSDK`) is the root handle:

1. `BcosSDK.build(tomlPath)` → `Config.load()` parses a TOML file into a `ConfigOption`.
2. The constructor builds a native `BcosSDKJniObj` from `configOption.getJniConfig()`.
3. From one `BcosSDK` you obtain per-group/feature handles, each backed by the shared native pointer:
   - `getClient(groupId)` / `getClient()` → `Client` (JSON-RPC + tx submission for one group)
   - `getTarsClient(groupId)` → `TarsClient` (Tars-RPC transport variant of `Client`)
   - `getAmop()` → `Amop` (Advanced Messages Onchain Protocol)
   - `getEventSubscribe(...)` → contract event push
   - `getFilter(...)` → eth-style log filters

### `Client` — the per-group RPC surface

`org.fisco.bcos.sdk.v3.client.Client` is an interface, built via
`Client.build(groupId, configOption, nativePointer)`. It exposes the JSON-RPC API (block/transaction
queries, `call`, `sendTransaction`, group/node/consensus info, system config, log filters). Requests
and responses are modeled under `client/protocol/{request,response,model}`. A `Client` is bound to a
single group; one `BcosSDK` can serve many groups.

### `CryptoSuite` — the crypto strategy, injected everywhere

`org.fisco.bcos.sdk.v3.crypto.CryptoSuite` bundles a `Signature` + `Hash` + `CryptoKeyPair`, selected
by an `int` crypto type from `model/CryptoType`:

- `ECDSA_TYPE = 0` → secp256k1 + Keccak256 (standard Ethereum-style)
- `SM_TYPE = 1` → SM2 + SM3 (Chinese national / 国密 crypto)
- `ED25519_VRF_TYPE = 2`, `HSM_TYPE = 3` (hardware security module)

`Client`, transaction processors, and contract wrappers all take a `CryptoSuite`. **When adding code
that signs or hashes, route it through `CryptoSuite` rather than picking an algorithm directly** — this
is what keeps the SDK dual-mode (ECDSA vs. SM) from a single config flag (`useSMCrypto` in TOML).
`crypto/keystore` handles PEM/P12 account files; `crypto/keypair`, `crypto/signature`, `crypto/hash`,
`crypto/vrf` hold the implementations.

### `codec` — two serialization schemes (largest module)

This package is ~half the codebase because it implements two independent encodings:

- `codec/abi` — Ethereum **ABI** encoding for Solidity/EVM contracts.
- `codec/scale` — **SCALE** encoding for WASM/Liquid contracts (`reader`/`writer` subpackages).
- `codec/datatypes` — the Solidity type system (`Uint`, `Int`, `Bytes`, `DynamicArray`, `Struct`, ...).
  `codec/datatypes/generated` (~150 files) holds the programmatically-generated fixed-width types
  (`Uint8..Uint256`, `Int8..Int256`, `Bytes1..Bytes32`) — **do not hand-edit these**.
- `ContractCodec` is the high-level encode/decode entry. `codec/abi/tools` (`ContractAbiUtil`,
  `TopicTools`) supports the Solidity→Java contract codegen and event-topic computation.

### `transaction` — two generations of the tx-building API

- **Legacy** (`transaction/manager`): `AssembleTransactionProcessor` and
  `AssembleTransactionWithRemoteSignProcessor`, created via `TransactionProcessorFactory`.
- **Current** (`transaction/manager/transactionv1`): a `TransactionManager` interface with
  `DefaultTransactionManager` / `ProxySignTransactionManager`, plus services
  `AssembleTransactionService`, `AssembleEIP1559TransactionService` (EIP-1559 / gas-priced txs),
  and `TransferTransactionService`. Build requests with `TransactionRequestBuilder`; DTOs live in
  `transaction/manager/transactionv1/dto`.

  Prefer the `transactionv1` API for new work. Supporting pieces: `transaction/signer` (local + remote
  signing), `transaction/nonce`, `transaction/gasProvider`, `transaction/codec`, `transaction/pusher`.

### `contract` — contract wrappers and built-in (precompiled) contracts

- `contract/Contract.java` is the **base class extended by generated Java contract classes** (compiled
  from Solidity ABI/bin). When you see a generated contract, it derives from this.
- `contract/precompiled` wraps FISCO BCOS **system (precompiled) contracts**. Each subsystem follows a
  `*Precompiled` (low-level ABI binding) + `*Service` (high-level helper) pairing:
  `consensus`, `sysconfig`, `bfs` (on-chain file system / BFS), `crud` (KV + table storage),
  `balance`, `sharding`. Fixed addresses live in `precompiled/model/PrecompiledAddress`.
- `contract/auth` is committee-based permission/governance management (`manager`, `contracts`, `po`).

### Other modules

- `config` — TOML → `ConfigOption` (`network`, `account`, `cryptoMaterial`, `threadPool` sections;
  see `src/test/resources/config-example.toml`).
- `amop` — peer-to-peer on-chain messaging. `eventsub` — contract event subscription/push.
  `filter` — log filter polling. `model` — shared enums/DTOs/callbacks. `utils` — hex/byte/address helpers.

## Conventions

- **Commit messages**: `<type>(scope): message`, e.g. `<fix>(codec): fix abi decode issue`.
  `.ci/ci_check_commit.sh` enforces: ≤100 commits per PR, unique commit subjects, ≤2 merge commits
  (rebase instead of merge), and runs a Cobra security scan on changed files.
- **Per-project rule**: do **not** add `Co-Authored-By` / AI-authorship trailers to commit messages.
- Apache 2.0 license header is present on source files; keep it on new files.
