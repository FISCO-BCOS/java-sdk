package org.fisco.bcos.sdk.contract.precompiled.bfs;

import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.RetCode;
import org.fisco.bcos.sdk.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

public class BFSService {
    private final BFSPrecompiled bfsPrecompiled;

    public BFSService(Client client, CryptoKeyPair credential) {
        this.bfsPrecompiled =
                BFSPrecompiled.load(
                        client.isWASM()
                                ? PrecompiledAddress.BFS_PRECOMPILED_NAME
                                : PrecompiledAddress.BFS_PRECOMPILED_ADDRESS,
                        client,
                        credential);
    }

    public RetCode mkdir(String path) throws ContractException {
        return ReceiptParser.parseTransactionReceipt(bfsPrecompiled.mkdir(path));
    }

    public List<BFSPrecompiled.BfsInfo> list(String path) throws ContractException {
        try {
            Tuple2<BigInteger, DynamicArray<BFSPrecompiled.BfsInfo>> listOutput =
                    bfsPrecompiled.list(path);
            if (!listOutput.getValue1().equals(BigInteger.ZERO)) {
                throw new ContractException(
                        "BfsService: list return error code: "
                                + listOutput.getValue1()
                                + ", check error msg in blockchain node.");
            }
            return listOutput.getValue2().getValue();
        } catch (ContractException e) {
            throw ReceiptParser.parseExceptionCall(e);
        }
    }

    public RetCode link(String name, String version, String address, String abi)
            throws ContractException {
        return ReceiptParser.parseTransactionReceipt(
                bfsPrecompiled.link(name, version, address, abi));
    }
}
