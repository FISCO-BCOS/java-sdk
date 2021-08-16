package org.fisco.bcos.sdk.contract.precompiled.wasm;

import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.RetCode;
import org.fisco.bcos.sdk.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.utils.Hex;

public class DeployWasmService {
    private final DeployWasmPrecompiled deployWasmPrecompiled;
    private String currentVersion;

    public DeployWasmService(Client client, CryptoKeyPair cryptoKeyPair) {
        this.deployWasmPrecompiled =
                DeployWasmPrecompiled.load(
                        PrecompiledAddress.DEPLOY_WASM_PRECOMPILED_ADDRESS, client, cryptoKeyPair);
        this.currentVersion = client.getNodeInfo().getSupportedVersion();
    }

    public RetCode deployWasm(byte[] code, byte[] params, String path, String jsonAbi)
            throws ContractException {
        String x = Hex.toHexString(code);
        String y = Hex.toHexString(params);
        return ReceiptParser.parseTransactionReceipt(
                deployWasmPrecompiled.deployWasm(code, params, path, jsonAbi));
    }
}
