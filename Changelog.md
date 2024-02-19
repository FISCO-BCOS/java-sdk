## v3.6.0
(2024-02-08)

请阅读Java SDK v3.x+文档：

- [中文用户手册](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### 新增

- 新增[FISCO BCOS 3.6.0](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.6.0) 版本新的交易字段的支持，包括：`value`, `gasPrice`, `gasLimit`, `maxFeePerGas`, `maxPriorityFeePerGas`，新字段对应的交易版本号升级到 `v1`。详情见： https://github.com/FISCO-BCOS/java-sdk/pull/841 。
- 新增 `TransactionManager` 类，默认发交易模式子类 `DefaultTransactionManager`, 以及可支持签名拼装的子类 `ProxySignTransactionManager`。这些类型默认发送`v1`版本号的交易，支持[FISCO BCOS 3.6.0](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.6.0) 版本新增的所有交易字段。详情见：https://github.com/FISCO-BCOS/java-sdk/pull/847 。
- 新增 `AssembleTransactionService`类，以及`AssembleEIP1559TransactionService`类，提供开发者从合约调用参数到拼装整个交易的服务接口。此外，还提供了`TransactionManager`的Setter接口，开发者可以自定义多种交易拼装模式。详情见：https://github.com/FISCO-BCOS/java-sdk/pull/852 。
- 新增 `TransferTransactionService` 类，提供用户发送原生转账交易。详情见：https://github.com/FISCO-BCOS/java-sdk/pull/847 。
- 新增 `GasProvider` 和 `NonceAndBlockLimitProvider` 的接口，开发者可以自定义交易的`gasPrice`, `gasLimit`, `maxFeePerGas`, `maxPriorityFeePerGas`，`nonce`，`blockLimit`等字段。**注意：目前只在`TransactionManager`的接口中使用**。详情见：https://github.com/FISCO-BCOS/java-sdk/pull/863 。
- 新增 `BalanceService`类，用户可以通过这些接口更改账户余额。详情见：https://github.com/FISCO-BCOS/java-sdk/pull/854 。
- 新增 `Contract`类的事件订阅功能，在合约文件生成Java文件之后，用户可以通过这些接口订阅合约的事件。详情见：https://github.com/FISCO-BCOS/java-sdk/pull/855 。
- 新增 `Contract`类切换 `TransactionManager` 发送交易的方式，开发者可以手动切换TransactionManager，发起 `v1` 版本的交易。详情见：https://github.com/FISCO-BCOS/java-sdk/pull/855 。
- 新增支持合约的payable关键字，在合约文件生成Java文件之后，用户可以通过接口调用合约并带上转账value。详情见：
- 新增 `JsonTransactionResponse`类 `encodeTransactionData`, `decodeTransaction`的方法，提供用户交易Data字节计算，解码Tars编码后的交易。详情见：https://github.com/FISCO-BCOS/java-sdk/pull/857 。
- 新增 `TransactionReceipt`、`BcosBlockHeader` 的哈希计算方法，用户可以通过这些接口计算交易回执、区块头的哈希。

### 更新

- 将 `ContractCodec` 中包含`CryptoSuite`的构造函数置为@Deprecated，推荐开发者使用包含`Hash`的构造函数。详情见：https://github.com/FISCO-BCOS/java-sdk/pull/844 。
- 将 `JsonTransactionResponse`类的 `readFromHexString` 和 `writeToHexString`置为 **Deprecated**，推荐开发者使用 `encodeTransactionData`, `decodeTransaction`的方法。

### 修复

- 修复 `Config` 在初始化时出现异常的报错信息。详情见：https://github.com/FISCO-BCOS/java-sdk/pull/875 。

### 兼容性说明

- FISCO BCOS 2.0+ 版本请使用 `org.fisco.bcos.sdk:bcos-java-sdk:2.9.3` 版本，代码分支为 `master-2.0` 。
- 兼容java-sdk v3.0+的历史版本
- 支持[FISCO BCOS 3.6.0](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.6.0)版本，以及3.0.0正式版以来的所有版本。
- 账户权限管理接口、BFS新增的list分页接口与link接口只在 FISCO BCOS 3.1.0及以上支持使用。
- 新增的CRUD接口，如条件范围遍历查询、修改、删除等接口，只在FISCO BCOS 3.2.0及以上支持使用。
- 新增的ShardService, 只在FISCO BCOS 3.3.0及以上支持使用。
- Call With Sign接口只在FISCO BCOS 3.4.0及以上有效果，低于3.4.0版本的节点可正常调用但不会处理签名。
- 新增的Tars Client，只在FISCO BCOS 3.5.0及以上支持使用。
- 新增的交易字段，交易版本号升级为 `v1`，以及配套的`TransactionManager`, `AssembleTransactionService`, `TransferTransactionService`等，都只能在FISCO BCOS 3.6.0及以上支持使用。
- 新增的BalanceService只能在3.6.0中使用。

### 遗留问题说明

在工作量与收益之间做平衡之后，目前还遗留以下几种场景的编解码仍然可能会有问题，欢迎社区用户贡献解决方案或实现代码 :-)
- 三维及以上的数组作为输入输出参数时，使用`contract2java`编译成Java文件后，方法接口可能会出现调用错误。
- 在使用类似 `bytes[2][]` 这样的动态数组套静态数组，且基础类型仍然是动态类型的类型时，使用`contract2java`编译成Java文件后，方法接口调用时编解码可能会出现问题。
- 在使用liquid合约时，如果使用上一条所述的类型作为输入输出参数，在合约方法接口调用时编解码可能会出现问题。
- 在使用liquid合约时，将u256与i256类型的输入输出参数，如果输入最大值，BigInteger生成的bytes会超过大小限制。
- 在使用liquid合约时，因为liquid合约的事件编码与Solidity合约的事件编码不同，所以在使用liquid合约的事件时，会出现Java sdk解析失败的问题。

---

## v3.5.0
(2023-10-16)

请阅读Java SDK v3.x+文档：

- [中文用户手册](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### 新增

- 新增Tars Client类 `org.fisco.bcos.sdk.v3.client.TarsClient` ，支持使用Tars RPC协议发交易上链，_目前只支持在x86 Linux平台上使用_。
- 新增系统Feature类 `org.fisco.bcos.sdk.v3.contract.precompiled.sysconfig.SystemConfigFeature` ，支持查询链上Feature开关状态。
- 新增Client接口 `getNodeListByType` ，支持查询链上各种类型的节点列表。
- 在Client类中新增 `nodeToSendRequest` 成员，并提供Getter/Setter接口，用于用户自定义节点发送请求。

### 更新

- 更新依赖 `bcos-sdk-jni` 版本到3.5.0，解决SDK侧性能差的问题。
- 删除不必要的依赖： `com.google.guava` 。
- 更新依赖 `jackson-databind` 版本到2.14.3。

### 修复

- 修复ABI编码 `encodeMethodFromString` 在输入错误位数的数字类型时仍然能够编码成功的问题。
- 修复在调用call with sign接口时没有正确处理回滚信息的问题。
- 修复在用户指定节点发送请求时，有些RPC请求没有正确发到指定节点的问题。

### 兼容性说明

- FISCO BCOS 2.0+ 版本请使用 `org.fisco.bcos.sdk:bcos-java-sdk:2.9.3` 版本，代码分支为 `master-2.0` 。 
- 兼容java-sdk v3.0+的历史版本
- 支持[FISCO BCOS 3.5.0](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.5.0)版本，以及3.0.0正式版以来的所有版本。
- 账户权限管理接口、BFS新增的list分页接口与link接口只在 FISCO BCOS 3.1.0及以上支持使用。
- 新增的CRUD接口，如条件范围遍历查询、修改、删除等接口，只在FISCO BCOS 3.2.0及以上支持使用。
- 新增的ShardService, 只在FISCO BCOS 3.3.0及以上支持使用。
- Call With Sign接口只在FISCO BCOS 3.4.0及以上有效果，低于3.4.0版本的节点可正常调用但不会处理签名。
- 新增的Tars Client，只在FISCO BCOS 3.5.0及以上支持使用。

### 遗留问题说明

在工作量与收益之间做平衡之后，目前还遗留以下几种场景的编解码仍然可能会有问题，欢迎社区用户贡献解决方案或实现代码 :-)
- 三维及以上的数组作为输入输出参数时，使用`contract2java`编译成Java文件后，方法接口可能会出现调用错误。
- 在使用类似 `bytes[2][]` 这样的动态数组套静态数组，且基础类型仍然是动态类型的类型时，使用`contract2java`编译成Java文件后，方法接口调用时编解码可能会出现问题。
- 在使用liquid合约时，如果使用上一条所述的类型作为输入输出参数，在合约方法接口调用时编解码可能会出现问题。
- 在使用liquid合约时，将u256与i256类型的输入输出参数，如果输入最大值，BigInteger生成的bytes会超过大小限制。
- 在使用liquid合约时，因为liquid合约的事件编码与Solidity合约的事件编码不同，所以在使用liquid合约的事件时，会出现Java sdk解析失败的问题。

---

## v3.5.0
(2023-10-16)

Please refer to the Java SDK v3.x+ documentation:

- [Chinese User Manual](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### Added

- Added Tars Client class `org.fisco.bcos.sdk.v3.client.TarsClient`, which supports sending transactions to the chain using the Tars RPC protocol. _Currently, it is only supported on x86 Linux platforms_.
- Added System Feature class `org.fisco.bcos.sdk.v3.contract.precompiled.sysconfig.SystemConfigFeature`, which allows querying the on-chain Feature toggle status.
- Added Client interface `getNodeListByType`, which supports querying lists of various types of nodes on the chain.
- Added a new member `nodeToSendRequest` to the Client class, along with Getter/Setter interfaces, for customizing node requests.

### Updates

- Updated the dependency to `bcos-sdk-jni` version 3.5.0 to resolve performance issues on the SDK side.
- Removed unnecessary dependency on `com.google.guava`.
- Updated the dependency to `jackson-databind` version 2.14.3.

### Fixed

- Fixed the ABI encoding issue in `encodeMethodFromString`, where it could successfully encode incorrect-length numerical types.
- Fixed the issue where the rollback information was not correctly handled when calling the `call with sign` interface.
- Fixed the issue where some RPC requests were not correctly sent to the specified nodes when sending requests to user-specified nodes.

### Compatibility Notes

- For FISCO BCOS 2.0+ versions, please use version `org.fisco.bcos.sdk:bcos-java-sdk:2.9.3`, with the `master-2.0` code branch.
- Compatible with historical versions of java-sdk v3.0+.
- Supports [FISCO BCOS 3.5.0](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.5.0) and all versions since the official release of 3.0.0.
- Account permission management interface, BFS new list pagination interface, and link interface are only supported in FISCO BCOS 3.1.0 and above.
- New CRUD interfaces, such as conditional range traversal queries, modifications, deletions, and others, are only supported in FISCO BCOS 3.2.0 and above.
- The new ShardService is only supported in FISCO BCOS 3.3.0 and above.
- The Call With Sign interface is effective only in FISCO BCOS 3.4.0 and above. Nodes with versions lower than 3.4.0 can call it normally but won't handle signatures.
- The new Tars Client is only supported in FISCO BCOS 3.5.0 and above.

### Known Issues

After balancing the workload and benefits, there are still potential issues in the following scenarios for encoding and decoding. Community users are welcome to contribute solutions or implementation code :-)

- When using three-dimensional arrays or higher as input/output parameters, after compiling into Java files using `contract2java`, method interfaces may result in incorrect calls.
- When using dynamic arrays nested within static arrays, such as `bytes[2][]`, and the underlying type remains dynamic, using `contract2java` to compile into Java files may lead to encoding and decoding issues when calling method interfaces.
- When using the liquid contract and using the types described above as input/output parameters, encoding and decoding may result in problems when calling method interfaces.
- When using the liquid contract and using u256 and i256 types as input/output parameters, if the input maximum value is reached, the bytes generated by BigInteger may exceed the size limit.
- When using the liquid contract, due to differences in event encoding between the liquid contract and Solidity contracts, there may be issues with Java SDK parsing when using liquid contract events.

---

## v3.4.0
(2023-06-09)

请阅读Java SDK v3.x+文档：

- [中文用户手册](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### 新增

- 新增call with sign接口，支持在发起static call请求时使用私钥对请求体(to+data)进行签名，在节点侧将会对应恢复出签名对应的用户地址，合约中可以取到call请求时的tx.origin和msg.sender，达到用户身份认证的目的。
- `Client`类新增`isEnableCommittee()`方法，与`isAuthCheck()`进行区分，用于判断当前链是否启用了治理委员会。
- `Client`类新增 `getChainCompatibilityVersion()`，用于获取当前链的数据兼容版本号。

### 更新

- 将`Client`类的`getChainVersion()`设置为@Deprecated，建议使用`getChainCompatibilityVersion()`。

### 修复

- 修复了开启治理委员会后，发起 `addSealerProposal` 提案时可能出现的观察节点未达到块高差值门限的问题。
- 修复回执中状态数据与节点不匹配的问题。

### 兼容性说明

- 不兼容 FISCO BCOS 2.0+ 版本
- 兼容java-sdk v3.0+的历史版本
- 支持[FISCO BCOS 3.4.0](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.4.0)版本，以及3.0.0正式版以来的所有版本。
- 账户权限管理接口、BFS新增的list分页接口与link接口只在 FISCO BCOS 3.1.0支持使用。
- 新增的CRUD接口，如条件范围遍历查询、修改、删除等接口，只在FISCO BCOS 3.2.0支持使用。
- 新增的ShardService, 只在FISCO BCOS 3.3.0支持使用。

### 遗留问题说明

在工作量与收益之间做平衡之后，目前还遗留以下几种场景的编解码仍然可能会有问题，欢迎社区用户贡献解决方案或实现代码 :-)
- 三维及以上的数组作为输入输出参数时，使用`contract2java`编译成Java文件后，方法接口可能会出现调用错误。
- 在使用类似 `bytes[2][]` 这样的动态数组套静态数组，且基础类型仍然是动态类型的类型时，使用`contract2java`编译成Java文件后，方法接口调用时编解码可能会出现问题。
- 在使用liquid合约时，如果使用上一条所述的类型作为输入输出参数，在合约方法接口调用时编解码可能会出现问题。
- 在使用liquid合约时，将u256与i256类型的输入输出参数，如果输入最大值，BigInteger生成的bytes会超过大小限制。
- 在使用liquid合约时，因为liquid合约的事件编码与Solidity合约的事件编码不同，所以在使用liquid合约的事件时，会出现Java sdk解析失败的问题。

---

## v3.4.0
(2023-06-09)
Please refer to the Java SDK v3.x+ documentation:

- [Chinese User Manual](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### Added

- Added the "call with sign" interface, which supports signing the request body (to+data) with a private key when initiating a static call request. The corresponding user address will be recovered with the signature on the node side, and the tx.origin and msg.sender at the time of the call request can be obtained in the contract, achieving user identity authentication.
- Added the `isEnableCommittee()` method to the `Client` class, which is used to determine whether the current chain has enabled the governance committee, differentiating it from `isAuthCheck()`.
- Added the `getChainCompatibilityVersion()` method to the `Client` class, which is used to obtain the data compatibility version of the current chain.

### Updated

- Deprecated the `getChainVersion()` method of the `Client` class, and recommend using `getChainCompatibilityVersion()` instead.

### Fixed

- Fixed the issue where when the governance committee is enabled, there might be a problem with the observer nodes not reaching the block height difference threshold when initiating the `addSealerProposal` proposal.
- Fixed the issue where the status data in the receipt does not match the chain node.

### Compatibility Notes

- Not compatible with FISCO BCOS 2.0+ versions.
- Compatible with historical versions of java-sdk v3.0+.
- Supports [FISCO BCOS 3.4.0](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.4.0) version and all versions since the official release of 3.0.0.
- The account permission management interface, list pagination interface, and link interface added by BFS are only supported in FISCO BCOS 3.1.0.
- The newly added CRUD interfaces, such as conditional range traversal query, modification, deletion, etc., are only supported in FISCO BCOS 3.2.0.
- The newly added ShardService is only supported in FISCO BCOS 3.3.0.

### Known Issues

After balancing the workload and benefits, there are still potential issues with the following scenarios in the encoding and decoding process. Community users are welcome to contribute solutions or implementation code :-)
- When using arrays of three dimensions or more as input or output parameters, there may be invocation errors in the method interface after compiling into Java files using `contract2java`.
- When using a dynamic array nested in a static array, such as `bytes[2][]`, and the base type is still a dynamic type, there may be issues with encoding and decoding in the method interface when compiling into Java files using `contract2java`.
- When using the liquid contract, if the aforementioned types are used as input or output parameters, there may be issues with encoding and decoding in the method interface when calling the contract.
- When using the liquid contract, if the u256 and i256 types are used as input or output parameters and the maximum value is input, the bytes generated by BigInteger may exceed the size limit.
- When using the liquid contract, because the event encoding of the liquid contract is different from that of Solidity contract, there may be issues with Java SDK parsing when using the events of the liquid contract.

---

## v3.3.0
(2023-04-17)

请阅读Java SDK v3.x+文档：

- [中文用户手册](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### 新增

- 新增密码机支持。
- 新增 `ShardingService` ，提供链上分区服务接口。
- 新增适配链上动态权限配置项接口，新增初始化权限治理的接口。
- `AssembleTransactionProcessor` 新增异步Call接口，支持`Contract2java`脚本生成的Java文件有异步Call接口。
- BFS新增 `isExist` 接口。
- 新增大量场景的集成测试CI用例。

### 更新

- 使用新的SDK JNI，减少内存拷贝与RPC请求次数，以提升Java SDK请求发送性能。
- 将 `ContractCodec`中基于类型反射的编解码方法设为 `deprecated`，这些方法会出现struct数组、多维数组等的解码错误，具体方法有如下：
  - ContractCodec.decodeMethodAndGetInputObject
  - ContractCodec.decodeMethodInput
  - ContractCodec.decodeMethodAndGetOutputObject
  - ContractCodec.decodeMethodByABIDefinition
  - ContractCodec.decodeMethod
- 将 `transaction.model.dto.TransactionResponse` 和 `transaction.model.dto.CallResponse` 类中使用反射解码的 `results` 字段设置为 **deprecated**。**目前为了兼容仍然会将解码结果设置在该字段，请使用 `returnObject` 和 `returnABIObject` 做类型判断。**
- 依赖包 `jackson-databind` 更新到 `2.4.12` 版本。

### 修复

- 修复 `ContractCodec` 在解码ABI struct、多维数组、嵌套struct数组时出现的解码错误，分别修复ABI编码、Scale编码的编解码问题。
- 修复 `AssembleTransactionWithRemoteSignProcessor` 中的一些错误逻辑。
- 修复 `TransactionEncoderService` 中存在交易哈希计算错误的问题，解决发送交易导致节点报错 `transaction hash mismatching`的问题。
- 修复 `TransactionDecoderService` 中部分接口不返回事件、解析事件出错的问题。
- 修复 `EventEncoder` 在解码struct与数组时出现的解码错误。
- 修复 `TableCRUDService` RetCode没有包含交易回执的bug。
- 修复 `ConsensusService` 向观察节点发送新增Sealer请求时的bug。

### 兼容性说明

- 不兼容 FISCO BCOS 2.0+ 版本
- 兼容java-sdk v3.0+的历史版本
- 支持[FISCO BCOS 3.3.0](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.3.0)版本
- 账户权限管理接口、BFS新增的list分页接口与link接口只在 FISCO BCOS 3.1.0支持使用。
- 新增的CRUD接口，如条件范围遍历查询、修改、删除等接口，只在FISCO BCOS 3.2.0支持使用。
- 新增的ShardService, 只在FISCO BCOS 3.3.0支持使用。

### 遗留问题说明

在工作量与收益之间做平衡之后，目前还遗留以下几种场景的编解码仍然可能会有问题，欢迎社区用户贡献解决方案或实现代码：
- 三维及以上的数组作为输入输出参数时，使用`contract2java`编译成Java文件后，方法接口可能会出现调用错误。
- 在使用类似 `bytes[2][]` 这样的动态数组套静态数组，且基础类型仍然是动态类型的类型时，使用`contract2java`编译成Java文件后，方法接口调用时编解码可能会出现问题。
- 在使用liquid合约时，如果使用上一条所述的类型作为输入输出参数，在合约方法接口调用时编解码可能会出现问题。
- 在使用liquid合约时，将u256与i256类型的输入输出参数，如果输入最大值，BigInteger生成的bytes会超过大小限制。

---

## v3.3.0
(2023-04-17)

Please refer to the Java SDK v3.x+ documentation:

- [Chinese User Manual](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### Added

- Added support for password machine.
- Added `ShardingService` to provide on-chain sharding services interface.
- Added interfaces for adapting to on-chain dynamic permission configurations, and added interfaces for initializing permission governance.
- Added asynchronous Call interface to `AssembleTransactionProcessor`, supporting asynchronous Call interface for Java files generated by `Contract2java` scripts.
- Added `isExist` interface to BFS.
- Added a large number of integration test cases for various scenarios.

### Updated

- Used a new SDK JNI to reduce memory copying and RPC request times, improving the sending performance of Java SDK requests.
- Deprecated the encoding and decoding methods based on type reflection in `ContractCodec`. These methods will have decoding errors for struct arrays, multidimensional arrays, etc. The specific methods are as follows:
  - ContractCodec.decodeMethodAndGetInputObject
  - ContractCodec.decodeMethodInput
  - ContractCodec.decodeMethodAndGetOutputObject
  - ContractCodec.decodeMethodByABIDefinition
  - ContractCodec.decodeMethod
- Deprecated the `results` field in the `transaction.model.dto.TransactionResponse` and `transaction.model.dto.CallResponse` classes, which use reflection decoding. **Currently, the decoding results are still set in this field for compatibility. Please use `returnObject` and `returnABIObject` for type judgment.**
- Updated the dependency package `jackson-databind` to version `2.4.12`.

### Fixed

- Fixed decoding errors in `ContractCodec` for ABI struct, multidimensional arrays, and nested struct arrays, respectively, and fixed encoding and decoding problems for ABI encoding and Scale encoding.
- Fixed some erroneous logic in `AssembleTransactionWithRemoteSignProcessor`.
- Fixed the problem of transaction hash calculation errors in `TransactionEncoderService`, solving the problem of node reporting `transaction hash mismatching` error when sending transactions.
- Fixed the bug that `TableCRUDService` RetCode does not include transaction receipts.
- Fixed the bug when `ConsensusService` sends a new Sealer request to the observation node.

### Compatibility Notes

- Not compatible with FISCO BCOS 2.0+ versions
- Compatible with historical versions of java-sdk v3.0+
- Supports [FISCO BCOS 3.2.0](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.2.0) version
- Account permission management interface, BFS's newly added list pagination interface and link interface are only supported in FISCO BCOS 3.1.0.
- The newly added CRUD interfaces, such as conditional range traversal query, modification, deletion, etc., are only supported in FISCO BCOS 3.2.0.

### Known Issues

After balancing the workload and benefits, there are still several scenarios where encoding and decoding may have problems. Community users are welcome to contribute solutions or implementation code:
- When using three-dimensional or higher arrays as input and output parameters, there may be calling errors in the method interface after compiling Java files with `contract2java`.
- When using a dynamic array nested in a static array, such as `bytes[2][]`, and the underlying type is still a dynamic type, there may be encoding and decoding problems when calling the method interface after compiling Java files with `contract2java`.
- When using liquid contracts, if the aforementioned types are used as input and output parameters, there may be encoding and decoding problems when calling the contract method interface.
- When using liquid contracts, if u256 and i256 types are used as input and output parameters, and the input is the maximum value, the bytes generated by BigInteger will exceed the size limit.

----

## v3.2.0
(2023-01-17)

请阅读Java SDK v3.x+文档：

- [中文用户手册](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### 新增

- 新增交易数据解码，用户可以通过已编码的交易十六进制数据解码出交易数据结构，详情参考 `org.fisco.bcos.sdk.v3.client.protocol.model.JsonTransactionResponse`的 `readFromHexString`、 `writeToHexString` 接口。
- 新增交易回执数据编解码的接口，用户可以对交易回执进行编解码，详情参考 `org.fisco.bcos.sdk.v3.model.TransactionReceipt` 的 `readFromHexString` 、 `writeToHexString` 接口。
- 新增交易回执哈希计算的接口，用户可以对交易回执计算正确的回执哈希，详情参考 `org.fisco.bcos.sdk.v3.model.TransactionReceipt` 的 `calculateReceiptHash` 接口。
- 新增CRUD全新接口，适配FISCO BCOS 3.2.0的CRUD接口，查询的Condition实现更多匹配规则，支持主键按照数字序排序。详情参考 `org.fisco.bcos.sdk.v3.contract.precompiled.crud.TableCRUDService`。
- 新增订阅事件接口，详情参考 `org.fisco.bcos.sdk.v3.eventsub` 中的`subscribeEvent`接口。
- 新增合约废止管理接口，详情参考 `org.fisco.bcos.sdk.v3.contract.auth.manager.AuthManager`。
- 新增Merkle树验证工具，用户可以根据链上返回的Merkle证明与交易哈希进行验证，详情参考 `org.fisco.bcos.sdk.v3.utils.MerkleCalculator`。

### 更新

- 变更交易类与回执类中的Merkle proof数据结构与字段名，原有的字段名设置为deprecated，但仍支持解析。transactionProof => txProof，receiptProof => txReceiptProof。
- 因安全与兼容原因，默认将 `com.google.code.gson` 版本指定为 `2.10`，`org.slf4j:slf4j-api` 版本降为 `1.7.36`。
- 适配 `org.fisco-bcos:bcos-sdk-jni:3.2.0` 

### 修复

- 修复治理委员提案发起接口 `createSetConsensusWeightProposal` 在设置观察节点的权重时出现的错误解析问题。
- 修复 `AssembleTransactionProcessor` 在发起call请求时，如果有多个函数重载返回值有概率使用错误的函数返回值类型解码的问题。

### 兼容性说明

- 不兼容 FISCO BCOS 2.0+ 版本
- 兼容java-sdk v3.0+的历史版本
- 支持[FISCO BCOS 3.2.0](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.2.0)版本
- 账户权限管理接口、BFS新增的list分页接口与link接口只在 FISCO BCOS 3.1.0支持使用。
- 新增的CRUD接口，如条件范围遍历查询、修改、删除等接口，只在FISCO BCOS 3.2.0支持使用。

----

## v3.2.0
(2023-01-17)

Please read the Java SDK v3.x+ documentation:

- [Chinese User Manual](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### New

- Added transaction data decoding. Users can decode the transaction data structure through the encoded transaction hexadecimal data. For details, refer to `readFromHexString` of `org.fisco.bcos.sdk.v3.client.protocol.model.JsonTransactionResponse` , `writeToHexString` interface.
- Added an interface for encoding and decoding transaction receipt data. Users can encode and decode transaction receipts. For details, refer to `readFromHexString` and `writeToHexString` interfaces of `org.fisco.bcos.sdk.v3.model.TransactionReceipt`.
- Added an interface for transaction receipt hash calculation. Users can calculate the correct receipt hash for transaction receipts. For details, refer to the `calculateReceiptHash` interface of `org.fisco.bcos.sdk.v3.model.TransactionReceipt`.
- Added a new CRUD interface, adapted to the CRUD interface of FISCO BCOS 3.2.0, the Condition of the query implements more matching rules, and supports primary keys to be sorted in numerical order. For details, refer to `org.fisco.bcos.sdk.v3.contract.precompiled.crud.TableCRUDService`.
- Added an interface for subscribing to events. For details, refer to the `subscribeEvent` interface in `org.fisco.bcos.sdk.v3.eventsub`.
- Added contract revocation management interface, for details, refer to `org.fisco.bcos.sdk.v3.contract.auth.manager.AuthManager`.
- A new Merkle tree verification tool is added. Users can verify according to the Merkle certificate and transaction hash returned on the chain. For details, refer to `org.fisco.bcos.sdk.v3.utils.MerkleCalculator`.

### renew

- Change the Merkle proof data structure and field name in the transaction class and receipt class. The original field name is set to deprecated, but still supports parsing. transactionProof => txProof, receiptProof => txReceiptProof.
- For security and compatibility reasons, the `com.google.code.gson` version is specified as `2.10` by default, and the `org.slf4j:slf4j-api` version is downgraded to `1.7.36`.
- Adapt to `org.fisco-bcos:bcos-sdk-jni:3.2.0`

### fix

- Fixed the incorrect parsing problem when setting the weight of the observation node in the governance committee proposal initiation interface `createSetConsensusWeightProposal`.
- Fix `AssembleTransactionProcessor` when initiating a call request, if there are multiple function overloaded return values, there is a probability that the wrong function return value type will be decoded.

### Compatibility Notes

- Not compatible with FISCO BCOS version 2.0+
- Compatible with historical versions of java-sdk v3.0+
- Support [FISCO BCOS 3.2.0](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.2.0) version
- The account authority management interface, the newly added list paging interface and link interface of BFS are only supported in FISCO BCOS 3.1.0.
- Newly added CRUD interfaces, such as conditional scope traversal query, modification, deletion, etc., are only supported in FISCO BCOS 3.2.0.## v3.2.0
  (2023-01-17)

Please read the Java SDK v3.x+ documentation:

- [Chinese User Manual](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### New

- Added transaction data decoding. Users can decode the transaction data structure through the encoded transaction hexadecimal data. For details, refer to `readFromHexString` of `org.fisco.bcos.sdk.v3.client.protocol.model.JsonTransactionResponse` , `writeToHexString` interface.
- Added an interface for encoding and decoding transaction receipt data. Users can encode and decode transaction receipts. For details, refer to `readFromHexString` and `writeToHexString` interfaces of `org.fisco.bcos.sdk.v3.model.TransactionReceipt`.
- Added an interface for transaction receipt hash calculation. Users can calculate the correct receipt hash for transaction receipts. For details, refer to the `calculateReceiptHash` interface of `org.fisco.bcos.sdk.v3.model.TransactionReceipt`.
- Added a new CRUD interface, adapted to the CRUD interface of FISCO BCOS 3.2.0, the Condition of the query implements more matching rules, and supports primary keys to be sorted in numerical order. For details, refer to `org.fisco.bcos.sdk.v3.contract.precompiled.crud.TableCRUDService`.
- Added an interface for subscribing to events. For details, refer to the `subscribeEvent` interface in `org.fisco.bcos.sdk.v3.eventsub`.
- Added contract revocation management interface, for details, refer to `org.fisco.bcos.sdk.v3.contract.auth.manager.AuthManager`.
- A new Merkle tree verification tool is added. Users can verify according to the Merkle certificate and transaction hash returned on the chain. For details, refer to `org.fisco.bcos.sdk.v3.utils.MerkleCalculator`.

### renew

- Change the Merkle proof data structure and field name in the transaction class and receipt class. The original field name is set to deprecated, but still supports parsing. transactionProof => txProof, receiptProof => txReceiptProof.
- For security and compatibility reasons, the `com.google.code.gson` version is specified as `2.10` by default, and the `org.slf4j:slf4j-api` version is downgraded to `1.7.36`.
- Adapt to `org.fisco-bcos:bcos-sdk-jni:3.2.0`

### fix

- Fixed the incorrect parsing problem when setting the weight of the observation node in the governance committee proposal initiation interface `createSetConsensusWeightProposal`.
- Fix `AssembleTransactionProcessor` when initiating a call request, if there are multiple function overloaded return values, there is a probability that the wrong function return value type will be decoded.

### Compatibility Notes

- Not compatible with FISCO BCOS version 2.0+
- Compatible with historical versions of java-sdk v3.0+
- Support [FISCO BCOS 3.2.0](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.2.0) version
- The account authority management interface, the newly added list paging interface and link interface of BFS are only supported in FISCO BCOS 3.1.0.
- Newly added CRUD interfaces, such as conditional scope traversal query, modification, deletion, etc., are only supported in FISCO BCOS 3.2.0.

## v3.1.2
(2023-01-04)

请阅读Java SDK v3.x+文档：

- [中文用户手册](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### 新增

- 发送交易支持设置`extraData`字段

### 兼容性说明

- 兼容java-sdk v3.0+的历史版本
- 仅支持[FISCO BCOS 3.0.0](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0)及以上版本


## v3.1.1
(2022-12-8)

请阅读Java SDK v3.x+文档：

- [中文用户手册](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### 修复

- 修复Client发送异步请求节点出现执行外的错误时，没有正确解析错误码和错误信息的问题。

### 兼容性说明

- 兼容java-sdk v3.0+的历史版本
- 仅支持[FISCO BCOS 3.0.0](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0)及以上版本

## v3.1.0
(2022-11-21)

请阅读Java SDK v3.x+文档：

- [中文用户手册](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### 新增

- 新增账户权限管理接口，详情参考 `org.fisco.bcos.sdk.v3.contract.auth.manager.AuthManager` ，包括冻结、解冻、废除账户地址接口。
- 新增BFS接口，`list`接口支持分页逻辑；`link`接口可以在BFS `/apps` 下任意目录下创建链接文件，不再限制输入目录格式。
- 增加解析交易input的接口，详情参考 `org.fisco.bcos.sdk.v3.codec.ContractCodec`。
- 交易回执新增 `checksumContractAddress` 字段，用于[EIP-55](https://github.com/ethereum/EIPs/blob/master/EIPS/eip-55.md)的checksum计算。

### 更新

- 变更加入共识节点逻辑，只有观察节点才能加入共识节点；游离节点/新增节点加入共识节点，必须先加入观察节点，且跟上目前链区块高度。
- 变更节点版本号判断，在做兼容性判断时请参考 `org.fisco.bcos.sdk.v3.model.EnumNodeVersion`

### 修复

- 修复获取合约事件接口的bug，issue: [#654](https://github.com/FISCO-BCOS/java-sdk/issues/654)
- 新增单元测试、CodeQL代码扫描覆盖，修复大多数错误。


### 兼容性说明

- 不兼容 FISCO BCOS 2.0+ 版本
- 兼容java-sdk v3.0+的历史版本
- 支持[FISCO BCOS 3.1.0](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.1.0)版本
- 账户权限管理接口、BFS新增的list分页接口与link接口只在 FISCO BCOS 3.1.0支持使用。

----

## v3.1.0
(2022-11-21)

Please read the Java SDK v3.x+ documentation:

- [Chinese User Manual](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### New

- Added account authority management interface, for details, refer to `org.fisco.bcos.sdk.v3.contract.auth.manager.AuthManager`, including freezing, unfreezing, and abolishing account address interfaces.
- Added BFS interface, the `list` interface supports pagination logic; the `link` interface can create link files in any directory under BFS `/apps`, and no longer restricts the input directory format.
- Add an interface for parsing transaction input. For details, refer to `org.fisco.bcos.sdk.v3.codec.ContractCodec`.
- The transaction receipt adds `checksumContractAddress` field, which is used for the checksum calculation of [EIP-55](https://github.com/ethereum/EIPs/blob/master/EIPS/eip-55.md).

### renew

- Change the logic of joining the consensus node. Only the observation node can join the consensus node; when the free node/new node joins the consensus node, it must first join the observation node and keep up with the current chain block height.
- Change node version number judgment, please refer to `org.fisco.bcos.sdk.v3.model.EnumNodeVersion` when making compatibility judgment

### fix

- Fix the bug of getting contract event interface, issue: [#654](https://github.com/FISCO-BCOS/java-sdk/issues/654)
- Added unit tests, CodeQL code scanning coverage, and fixed most errors.


### Compatibility Notes

- Not compatible with FISCO BCOS version 2.0+
- Compatible with historical versions of java-sdk v3.0+
- Support [FISCO BCOS 3.1.0](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.1.0) version
- The account authority management interface, the newly added list paging interface and link interface of BFS are only supported in FISCO BCOS 3.1.0.

## v3.0.1
(2022-9-26)

请阅读Java SDK v3.x+文档：

- [中文用户手册](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### 新增

- `TransactionProcessor`新增 `asyncExecuteCall` 的异步读接口
- 增加WASM的集成测试，补充UT，增加代码覆盖率

### 更新

- 重构项目结构，不再分子模块

### 修复

- 修复decodeABI时负数报错的问题
- 修复Client类 `getBlockByHashAsync` 接口不可用的问题

### 兼容性说明

- 不兼容 FISCO BCOS 2.0+ 版本
- 兼容java-sdk v3.0+的历史版本
- 支持[FISCO BCOS 3.0.1](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.1)版本

----

Please read documentation of Java SDK v3.x.

* English User Handbook：Working in progress...
* [Chinese User Handbook](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### Add

- `TransactionProcessor` adds the asynchronous read interface of `asyncExecuteCall`
- Increase the integration test of WASM, supplement UT, and increase code coverage

### renew

- Refactored project structure, no more sub-modules

### Fixes

- Fixed the problem of negative number error when decodeABI
- Fixed the problem that the `getBlockByHashAsync` interface of the Client class was unavailable

### Compatibility Notes

- Not compatible with FISCO BCOS version 2.0+
- Compatible with the historical version of java-sdk v3.0+
- Support [FISCO BCOS 3.0.1](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.1) version


## v3.0.0
(2022-8-23)

请阅读Java SDK v3.x+文档：

- [中文用户手册](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### 新增

- 新增 `Curve25519VRF` 的VRF功能接口
- 新增 `TransactionProcessor` 使用自定KeyPair发交易的接口，方便切换用户密钥
- 新增 `disableSSL` 配置选项，支持与节点无SSL通信

### 更新

- 重构 `sdk-codegen` 模块，代码抽出作为单独项目：https://github.com/FISCO-BCOS/code-generator
- 更新Logger等的Java依赖
- `sdk-codec` 更新128定长的静态数组类型

### 修复

- 修复Crypto KeyPair公钥带前缀的问题
- 修复向游离节点发送 `getGroupPeers` 请求导致空指针异常的问题
- 修复ABI字符串JSON反序列化异常的问题

### 兼容性说明

- 不兼容 FISCO BCOS 2.0+ 版本
- 兼容java-sdk v3.0+的历史版本
- 支持[FISCO BCOS 3.0.0](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0)版本

----

Please read documentation of Java SDK v3.x.

* English User Handbook：Working in progress...
* [Chinese User Handbook](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### Added

- Added VRF function interface of `Curve25519VRF`
- Added `TransactionProcessor` to send transactions using a custom KeyPair interface, which is convenient for switching user keys
- Added `disableSSL` configuration option to support communication with nodes without SSL

### Changed

- Refactor the `sdk-codegen` module to extract the code as a separate project: https://github.com/FISCO-BCOS/code-generator
- Update Java dependencies for Logger etc.
- `sdk-codec` update 128 fixed-length static array types

### Fixed

- Fixed the issue that Crypto KeyPair public key was prefixed
- Fixed the issue that sending `getGroupPeers` request to free node caused null pointer exception
- Fixed ABI string JSON deserialization exception

### Compatibility Notes

- Not compatible with FISCO BCOS version 2.0+
- Compatible with the historical version of java-sdk v3.0+
- Support [FISCO BCOS 3.0.0](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0) version

## v3.0.0-rc4
(2022-7-1)

请阅读Java SDK v3.x+文档：

- [中文用户手册](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### 新增

* 新增`TableCRUDService`，适配v3.0.0-rc4节点的CRUD Table的接口
* `AuthManger`
  * 新增共识节点变更提案、系统配置变更提案、升级计算逻辑提案等的接口
  * 新增冻结、解冻合约功能接口
  * 新增创建提案、修改合约ACL状态的异步接口
  * 新增批量获取提案的接口
  * 新增提案执行结果的事件监听接口

### 更新

* 使用密码库`webank-blockchain-java-crypto`替代`key-mini-toolkit`
* 升级`bcos-sdk-jni`到`3.0.0-rc4`

### 修复

* 修复合约编解码相关的bug
* 修复ReceiptParser获取预编译合约返回值时出现的编码问题
* 增加单测覆盖率，修复大部分不合理的代码

### 兼容性说明

- 不兼容 FISCO BCOS 2.0+ 版本
- 兼容java-sdk v3.x的历史版本
- 支持[3.0.0-rc4](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0-rc4)版本

----

Please read documentation of Java SDK v3.x.

* English User Handbook：Working in progress...
* [Chinese User Handbook](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### Added

* Added `TableCRUDService` to adapt the interface of CRUD Table of FISCO BCOS v3.0.0-rc4
* `AuthManger`
  * Add consensus node changes and system configuration changes to the governance contract system, upgrade calculation logic of governance contract, etc.
  * Added contract freeze/unfreeze contract interface
  * Added interface for creating proposals and modifying contract ACL status
  * Added interface to get proposals in batches
  * Added event listener interface for proposal execution result

### Changed

* Use the cryptographic library `webank-blockchain-java-crypto` instead of `key-mini-toolkit`
* Upgrade `bcos-sdk-jni` to `3.0.0-rc4`

### Fixed

* Fix bugs related to contract encoding and decoding
* Fix the encoding bug of ReceiptParser for the getting return value of precompiled contracts
* Increase single test coverage and fix unreasonable codes

### Compatibility Notes

- Not compatible with FISCO BCOS version 2.0+
- Compatible with the historical version of java-sdk v3.x
- Support [3.0.0-rc4](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0-rc4)


## v3.0.0-rc3
(2022-03-31)

请阅读Java SDK v3.x+文档：

- [中文用户手册](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### 新增

* 支持Solidity合约并行冲突字段解析
* 添加getABI接口
### 更新

* 调用`bcos-c-sdk`的接口实现交易编解码功能
* 调用`bcos-c-sdk`的接口实现密码算法
* 升级`bcos-sdk-jni`到`3.0.0-rc3`
* `org.fisco.bcos.sdk`包路径修改为`org.fisco.bcos.sdk.v3`
* sdk-abi代码重构
* BFS link取代CNS

### 修复

* 修复AMOP `getTopics`返回null导致的异常
* 修复[#issue 510](https://github.com/FISCO-BCOS/java-sdk/issues/510)，解决windows环境无法找不到`libbcos-sdk-jni.dll`动态库的问题
* 修复ABI/Scale编解码相关的bug


### 兼容性说明

- 不兼容 FISCO BCOS 2.0+ 版本
- 兼容java-sdk v3.x的历史版本
- 支持[3.0.0-rc3](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0-rc3)版本和[3.0.0-rc2](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0-rc2)版本的FISCO BCOS区块链
- 不支持[3.0.0-rc1](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0-rc1)版本的FISCO BCOS区块链

----

Please read documentation of Java SDK v3.x.

* English User Handbook：Working in progress...
* [Chinese User Handbook](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### Added

* Support Solidity contract parallel conflict field analyze
* support getABI interface
### Changed

* Call the interface of `bcos-c-sdk` to implement transaction encoding and decoding functions
* Call the interface of `bcos-c-sdk` to implement the cryptographic algorithm
* Upgrade `bcos-sdk-jni` to `3.0.0-rc3`
* `org.fisco.bcos.sdk` package path is modified to `org.fisco.bcos.sdk.v3`
* sdk-abi code refactor
* use BFS link to replace CNS

### Fixed

* Fix exception caused by AMOP `getTopics` returning null
* Fix [#issue 510](https://github.com/FISCO-BCOS/java-sdk/issues/510) to solve the problem that `libbcos-sdk-jni.dll` dynamic library cannot be found in windows environment
* Fix bugs related to ABI/Scale codec
### Compatibility Notes

- Not compatible with FISCO BCOS version 2.0+
- Incompatible with the historical version of java-sdk v3.x
- Support [3.0.0-rc3](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0-rc3) version and [3.0.0-rc2](https://github .com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0-rc2) FISCO BCOS
- [3.0.0-rc1](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0-rc1) FISCO BCOS blockchain is not supported

## v3.0.0-rc2
(2022-02-23)

请阅读Java SDK v3.x+文档：

- [中文用户手册](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### 更新

* 升级`bcos-sdk-jni`到`3.0.0-rc2`
* 修改SDK的默认群组为`group0`
* 交易由`Base64`编码修改为十六进制编码
* 对齐WBC-Liquid，修改`bytesN`类型数据的Scale编码

### 修复

* 修复Scale编码 struct数组编码丢失泛型类型数据的问题

### 兼容性说明

- 不兼容 FISCO BCOS 2.0+ 版本
- 兼容java-sdk v3.x的历史版本
- 支持[3.0.0-rc2](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0-rc2)版本的FISCO BCOS区块链
- 不支持[3.0.0-rc1](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0-rc1)版本的FISCO BCOS区块链

----

Please read documentation of Java SDK v3.x.

* English User Handbook：Working in progress...
* [Chinese User Handbook](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### Changed

* Upgrade `bcos-sdk-jni` to `3.0.0-rc2`
* Modify the default group of the SDK to `group0`
* Transaction modified from `Base64` encoding to hexadecimal encoding
* Align with WBC-Liquid, modify the Scale encoding of `bytesN`

### Fixed

* Fix the problem that Scale encoding struct array loses generic type data

### Compatibility Notes

- Not compatible with FISCO BCOS version 2.0+
- Compatible with historical versions of `java-sdk v3.x`
- Support [3.0.0-rc2](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0-rc2) version of FISCO BCOS blockchain
- [3.0.0-rc1](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0-rc1) version of FISCO BCOS blockchain is not supported


## v3.0.0-rc1
(2021-12-10)

请阅读Java SDK文档：

- [中文用户手册](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### 新特性
Java SDK包含如下几个新特性：

* 完全支持 [FISCO BCOS 3.0+](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/)，不兼容 FISCO BCOS 2.0+ 版本。FISCO BCOS 2.0+ 版本请使用对应版本的Java SDK。
* 支持Liquid合约使用，包含部署Liquid合约，发起Liquid合约交易。
* 支持Scale的编解码解析。
* 使用JNI封装后的[FISCO BCOS C SDK](https://github.com/FISCO-BCOS/bcos-c-sdk)接口与区块链交互。

### 功能
* 提供合约编译功能，将Solidity合约文件转换成Java合约文件。
* 支持Liquid合约使用，包含部署Liquid合约，发起Liquid合约交易。
* 提供Java SDK API,提供访问FISCO BCOS JSON-RPC 的功能，并支持预编译（Precompiled）合约调用。
* 提供自定义构造和发送交易功能。
* 提供AMOP功能。
* 支持合约事件推送。
* 支持ABI和Scale的编解码解析。
* 提供账户和权限管理接口。

### 兼容性说明

- 不兼容 FISCO BCOS 2.0+ 版本
- 支持[3.0.0-rc1](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0-rc1)版本的FISCO BCOS区块链

----

Please read documentation of Java SDK.

* English User Handbook：Working in progress...
* [Chinese User Handbook](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)

### New Features

Java SDK includes the following new features:

* Support [FISCO BCOS 3.0+](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/). No longer support for FISCO BCOS 2.0+, if you want to use Java SDK to connect FISCO BCOS 2.0+, please use Java SDK in 2.0+ versions.
* Support Liquid usage, including deploy contracts, sending transactions.
* Support Scale encoding and decoding data.
* Use JNI wrapper of [FISCO BCOS C SDK](https://github.com/FISCO-BCOS/bcos-c-sdk) interface, which connecting blockchain nodes.

### Functions
* Solidity contract compiling.
* Liquid contract support, including deploy contracts, sending transactions.
* Interacting with FISCO BCOS JSON-RPC interface.
* Constructing and sending transactions.
* Advanced Messages Onchain Protocol(AMOP) functions.
* Contract event subscription.
* Encoding and decoding data with ABI and Scale.
* Account Management.
* Authority Management.

### compatibility

- Not compatible with FISCO BCOS version 2.0+.
- Support [3.0.0-rc1](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v3.0.0-rc1) version of FISCO BCOS blockchain.

## v2.8.0
(2021-07-27)
Added:
* Add Hardware Secure Module(HSM), use hardware protect your private key, speed up crypto procedure.
* Support use PCI crypto card or crypto machine to make SM2 SM3 calculation.
* Support use HSM internal key to make TLS connection with FISCO BCOS nodes.
* Support use HSM internal key to sign transaction.

Update:
* Update crypto dependency version of sdk-crypto module.
* Support to read the certificate from the jar package.
* The interface that sent the transaction returns the transaction hash.
* Add VRF random number generation and random number verification interface.

----
添加:
* 新增硬件加密模块，使用硬件保护您的私钥，提升密码运算速度。
* 支持使用PCI加密卡/加密机进行SM2，SM3运算。
* 支持使用密码卡/密码机内部密钥与FISCO BCOS节点建立连接。
* 支持使用密码卡/密码机内部密钥进行交易签名。

更新：
* 更新sdk-crypto模块所使用的密码算法库版本。
* 支持从jar包中读取证书。
* 发送交易的接口返回交易哈希。
* 添加VRF随机数生成和随机数验证接口。

## v2.7.2
(2021-03-24)
Please read documentation of Java SDK.

* [English User Handbook](https://fisco-bcos-documentation.readthedocs.io/en/latest/docs/sdk/java_sdk/index.html)
* [Chinese User Handbook](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk/index.html#)
* [Chinese WIKI](https://github.com/FISCO-BCOS/java-sdk/wiki)

Added:
* Add an interface that supports remotely calling signature services to assemble and send transactions(AssembleTransactionWithRemoteSignProcessor, RemoteSignProviderInterface)

Fixed:
 * Fix the problem that the ContractLoader of the java sdk is loaded incorrectly in some scenarios, which causes the deployment and call of the contract to fail [#264](https://github.com/FISCO-BCOS/java-sdk/issues/264)
 * Fix the problem of errors when loading certificates and private keys from the classpath path in the windows environment [#260](https://github.com/FISCO-BCOS/java-sdk/issues/260)
 * Fix the problem that the FromBlock of EventLogParams does not take effect [#253](https://github.com/FISCO-BCOS/java-sdk/issues/253)
 * Fix the problem that the Java SDK can only read the private key generated by openssl with the -key name parameter added [#249](https://github.com/FISCO-BCOS/java-sdk/issues/249)


Compatibility

* FISCO BCOS v2.0+
* AMOP Not compatible with Web3SDK
----
请参考文档：
* [英文版用户手册](https://fisco-bcos-documentation.readthedocs.io/en/latest/docs/sdk/java_sdk/index.html)
* [中文版用户手册](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk/index.html#)
* [中文版WIKI](https://github.com/FISCO-BCOS/java-sdk/wiki)

添加:
* 增加支持远程调用签名服务来组装并发送交易的接口(AssembleTransactionWithRemoteSignProcessor, RemoteSignProviderInterface)


修复：

 * 修复某些场景下Java SDK的ContractLoader加载出错，导致部署和调用合约失败的问题 [#264](https://github.com/FISCO-BCOS/java-sdk/issues/264)
 * 修复windows环境下从classpath路径下加载证书和私钥出错的问题 [#260](https://github.com/FISCO-BCOS/java-sdk/issues/260)
 * 修复EventLogParams的FromBlock不生效的问题 [#253](https://github.com/FISCO-BCOS/java-sdk/issues/253)
 * 修复Java SDK只能读取openssl添加-key name参数的生成的私钥的问题 [#249](https://github.com/FISCO-BCOS/java-sdk/issues/249)

 
适配性：
* 适配FISCO BCOS v2.0+
* AMOP功能不兼容Web3SDK

## v2.7.1
(2020-12-24)
Please read documentation of Java SDK.

* [English User Handbook](https://fisco-bcos-documentation.readthedocs.io/en/latest/docs/sdk/java_sdk/index.html)
* [Chinese User Handbook](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk/index.html#)
* [Chinese WIKI](https://github.com/FISCO-BCOS/java-sdk/wiki)

Added:
* Support JDK 15
* AMOP module supports hexadecimal public key and private key as parameters
* Support constructing `AssembleTransactionProcessor` through the contents of `bin` and `abi`
* `AssembleTransactionProcessor` supports the construction of signed transactions

Changed：
* Rename the jar package of Java SDK to `fisco-bcos-java-sdk`
* Support loading certificates and account private keys from the `resources` path
* `ABICodecJsonWrapper` supports the representation of `bytes` type parameters in Base64 and hexadecimal encoding, and distinguishes encoding types with prefixes of `base64://` and `hex://`

Compatibility

* FISCO BCOS v2.0+
* AMOP Not compatible with Web3SDK
----
请参考文档：
* [英文版用户手册](https://fisco-bcos-documentation.readthedocs.io/en/latest/docs/sdk/java_sdk/index.html)
* [中文版用户手册](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk/index.html#)
* [中文版WIKI](https://github.com/FISCO-BCOS/java-sdk/wiki)

添加:
* 支持JDK 15
* AMOP模块支持传入十六进制的公钥和私钥
* 支持通过`bin`和`abi`的内容构造`AssembleTransactionProcessor`
* `AssembleTransactionProcessor`支持构造带有签名的交易


修改：
 * Java SDK的jar包重命名为`fisco-bcos-java-sdk`
 * 支持从`resources`路径加载证书和账户私钥
 * `ABICodecJsonWrapper`支持以Base64、十六进制编码形式表示`bytes`类型参数，并以`base64://`、`hex://`前缀区分编码类型
 
适配性：
* 适配FISCO BCOS v2.0+
* AMOP功能不兼容Web3SDK

## v2.7.0
(2020-11-20)
Please read documentation of Java SDK.

* [English User Handbook](https://fisco-bcos-documentation.readthedocs.io/en/latest/docs/sdk/java_sdk/index.html)
* [Chinese User Handbook](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk/index.html#)
* [Chinese WIKI](https://github.com/FISCO-BCOS/java-sdk/wiki)

Added:
 * Added `getBatchReceiptsByBlockNumberAndRange` and `getBatchReceiptsByBlockHashAndRange` interfaces to support batch pull block transaction receipts
 * Added the `getNodeInfo` interface to obtain subscribed topics information
 * Added `revokeManager` interface to revoke contract life cycle management authority
 * `ChainGovernanceService` adds `queryVotesOfMember` and `queryVotesOfThreshold` interfaces to support query voting status

Changed：
 * Separate `sdk-demo` as a `java-sdk-demo` project

Fixed:
 * Fix the exception of contract receipt parsing null pointer exception when deploying constructor with event

Compatibility

* FISCO BCOS v2.0+
* AMOP Not compatible with Web3SDK

----
请参考文档：
* [英文版用户手册](https://fisco-bcos-documentation.readthedocs.io/en/latest/docs/sdk/java_sdk/index.html)
* [中文版用户手册](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk/index.html#)
* [中文版WIKI](https://github.com/FISCO-BCOS/java-sdk/wiki)

添加:
 * 添加`getBatchReceiptsByBlockNumberAndRange`和`getBatchReceiptsByBlockHashAndRange`接口，支持批量拉取区块交易回执
 * 添加`getNodeInfo`接口，获取订阅的topics信息
 * 添加`revokeManager`接口，撤销合约生命周期管理权限
 * `ChainGovernanceService`添加`queryVotesOfMember`和`queryVotesOfThreshold`接口，支持查询投票情况

修改：
 * 将`sdk-demo`独立为`java-sdk-demo`项目

修复:
 * 修复部署构造函数带有event的合约异常时，合约回执解析空指针异常
 
适配性：
* 适配FISCO BCOS v2.0+
* AMOP功能不兼容Web3SDK

## v2.6.1
(2020-10-29)
Please read documentation of Java SDK.

* [English User Handbook](https://fisco-bcos-documentation.readthedocs.io/en/latest/docs/sdk/java_sdk/index.html)
* [Chinese User Handbook](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk/index.html#)
* [Chinese WIKI](https://github.com/FISCO-BCOS/java-sdk/wiki)

Changed：
* Hidden the amop private topic suffix in the callback function.
* Added amop demo required resources.
* Upgraded two dependencies that netty-sm-ssl-context to version v1.2.0 and netty to version v4.1.53.Final.
* Improved the documentation of send transaction without bin file.
* Removed unused arguments(abi file) in the functions like encodeMethodByInterface of ABI module.

Fixed:
* Solved the problem of crypto module cannot call by multiple tasks.
* Solved invalid block limit problem, when console delete data and restart.
* Fixed an event decode bug that cannot decode multiple events of one transaction.
* Fixed the AMOP message data truncated bug.

Compatibility

* FISCO BCOS v2.0+
* AMOP Not compatible with Web3SDK

----
请参考文档：
* [英文版用户手册](https://fisco-bcos-documentation.readthedocs.io/en/latest/docs/sdk/java_sdk/index.html)
* [中文版用户手册](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk/index.html#)
* [中文版WIKI](https://github.com/FISCO-BCOS/java-sdk/wiki)

修改：
 * 隐藏AMOP私有话题的话题前缀。
 * 添加AMOP Demo所需的资源文件。
 * 升级两个依赖版本，netty-sm-ssl-context到v1.2.0，netty 到 v4.1.53.Final.
 * 完善不使用bin文件构造交易的方法文档。
 * 在ABI模块中的encodeMethodByInterface等方法中删除无用的参数（abi文件）。
 
修复:
 * 解决加密模块无法被多进程访问的问题。
 * 解决控制台删除数据后重启会出现block limit不合法异常的问题。
 * 解决同一个交易中如果有多个Event，只能解析一个无法解析多个的问题。
 * 解决AMOP消息数据被截断的问题。
 
适配性：
* 适配FISCO BCOS v2.0+。
* 不适配Web3SDK。意思是，如果两个用户想使用AMOP功能进行通信，他们必须要么同时使用Java SDK，要么同时使用Web3SDK。
 
## v2.6.1-rc1
(2020-09-30)
Please read documentation of Java SDK.

* [English User Handbook](https://fisco-bcos-documentation.readthedocs.io/en/latest/docs/sdk/java_sdk/index.html)
* [Chinese User Handbook](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk/index.html#)
* [Chinese WIKI](https://github.com/FISCO-BCOS/java-sdk/wiki)

Support functions are:

* Contract compiling.
* Interacting with FISCO BCOS JSON-RPC interface.
* constructing and sending transactions.
* Advanced Messages Onchain Protocol(AMOP) functions.
* Contract event subscription.
* Encoding and decoding data with ABI. 
* Account Management.

This java sdk is a code refactoring version base on Web3SDK 2.6.1. It includes the following new features:

* Support Toml config file, simplify configuration options.
* Support connecting with nodes of different groups.
* Support AMOP subscription and unsubscription at any time instead of just before start service.
* ABI module add support of encode and decode of struct type data.
* Use the common crypto tools of WeBank.
* Add a new module called group management to help applications manage nodes which java SDK connecting with.
* Use modular design, each module can use independently. For example, you can create crypto.jar file if only crypto module is needed.

Compatibility

* FISCO BCOS v2.0+.
* Not compatible with Web3SDK. When two users want to use AMOP functions to talk with each other, they should either both use Java SDK, or both use Web3SDK. 

----

请参考文档：
* [英文版用户手册](https://fisco-bcos-documentation.readthedocs.io/en/latest/docs/sdk/java_sdk/index.html)
* [中文版用户手册](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk/index.html#)
* [中文版WIKI](https://github.com/FISCO-BCOS/java-sdk/wiki)

支持功能：
* 合约编译。
* FISCO BCOS JSON-RPC 调用。
* 构造和发送交易。
* AMOP功能。
* 合约事件订阅。
* ABI 数据编解码。
* 账户管理。

Java SDK是基于Web3SDK重构的，它包含的新特性有：
* 支持Toml文件配置，简化配置项。
* 支持同时连接不同群组的节点。
* 支持动态订阅和取消订阅AMOP话题。
* ABI模块支持对结构体的编解码。
* 使用WeBank的通用加密工具。
* 新增群组管理模块，帮助用户管理不同群组的节点，简化应用代码。
* 使用模块化设计，便于组装再造。用户可以根据需要下载和使用必要的模块。

适配性：
* 适配FISCO BCOS v2.0+。
* 不适配Web3SDK。意思是，如果两个用户想使用AMOP功能进行通信，他们必须要么同时使用Java SDK，要么同时使用Web3SDK。
