package org.fisco.bcos.sdk.contract.precompiled.bfs;

import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.RetCode;
import org.fisco.bcos.sdk.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

public class BFSService {
    private final BFSPrecompiled bfsPrecompiled;
    private String currentVersion;

    public BFSService(Client client, CryptoKeyPair credential) {
        this.bfsPrecompiled =
                BFSPrecompiled.load(PrecompiledAddress.BFS_PRECOMPILED_ADDRESS, client, credential);
        this.currentVersion = client.getNodeInfo().getSupportedVersion();
    }

    public RetCode mkdir(String path) throws ContractException {
        return ReceiptParser.parseTransactionReceipt(bfsPrecompiled.mkdir(path));
    }

    public String list(String path) throws ContractException {
        try {
            return bfsPrecompiled.list(path);
        } catch (ContractException e) {
            throw ReceiptParser.parseExceptionCall(e);
        }
    }
}
