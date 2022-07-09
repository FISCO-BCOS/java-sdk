package org.fisco.bcos.sdk.v3.contract.precompiled.bfs;

import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

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
        TransactionReceipt transactionReceipt = bfsPrecompiled.mkdir(path);
        return ReceiptParser.parseTransactionReceipt(
                transactionReceipt, tr -> bfsPrecompiled.getMkdirOutput(tr).getValue1());
    }

    public List<BFSPrecompiled.BfsInfo> list(String path) throws ContractException {
        Tuple2<BigInteger, DynamicArray<BFSPrecompiled.BfsInfo>> listOutput =
                bfsPrecompiled.list(path);
        if (!listOutput.getValue1().equals(BigInteger.ZERO)) {
            RetCode precompiledResponse =
                    PrecompiledRetCode.getPrecompiledResponse(
                            listOutput.getValue1().intValue(), "");
            throw new ContractException(
                    "BfsService: list return error code: "
                            + listOutput.getValue1()
                            + ", check error msg in blockchain node.",
                    precompiledResponse.getCode());
        }
        return listOutput.getValue2().getValue();
    }

    public RetCode link(String name, String version, String contractAddress, String abi)
            throws ContractException {
        TransactionReceipt transactionReceipt =
                bfsPrecompiled.link(name, version, contractAddress, abi);
        return ReceiptParser.parseTransactionReceipt(
                transactionReceipt, tr -> bfsPrecompiled.getLinkOutput(tr).getValue1());
    }

    public String readlink(String absolutePath) throws ContractException {
        return bfsPrecompiled.readlink(absolutePath);
    }
}
