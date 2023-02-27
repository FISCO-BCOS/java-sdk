package org.fisco.bcos.sdk.v3.contract.precompiled.bfs;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.PrecompiledVersionCheck;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

public class BFSService {
    private final BFSPrecompiled bfsPrecompiled;
    private final long currentVersion;

    public BFSService(Client client, CryptoKeyPair credential) {
        this.bfsPrecompiled =
                BFSPrecompiled.load(
                        client.isWASM()
                                ? PrecompiledAddress.BFS_PRECOMPILED_NAME
                                : PrecompiledAddress.BFS_PRECOMPILED_ADDRESS,
                        client,
                        credential);
        this.currentVersion =
                client.getGroupInfo()
                        .getResult()
                        .getNodeList()
                        .get(0)
                        .getProtocol()
                        .getCompatibilityVersion();
    }

    public long getCurrentVersion() {
        return currentVersion;
    }

    public RetCode mkdir(String path) throws ContractException {
        TransactionReceipt transactionReceipt = bfsPrecompiled.mkdir(path);
        return ReceiptParser.parseTransactionReceipt(
                transactionReceipt, tr -> bfsPrecompiled.getMkdirOutput(tr).getValue1());
    }

    // deprecated, use list(String absolutePath, BigInteger offset, BigInteger limit)

    /**
     * @param path absolute path
     * @return If path is a directory, then return sub files; if path is a link, then return
     *     fileName and link-address in ext.
     * @deprecated use {@link #list(String, BigInteger, BigInteger)} instead.
     */
    @Deprecated
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
                            + ", error msg: "
                            + precompiledResponse.getMessage(),
                    precompiledResponse.getCode());
        }
        return listOutput.getValue2().getValue();
    }

    /**
     * @param path absolute path
     * @return If path is a directory, then return sub files; if path is a link, then return
     *     fileName and link-address in ext.
     * @deprecated use {@link #list(String, BigInteger, BigInteger)} instead.
     */
    @Deprecated
    public List<BFSInfo> listBFSInfo(String path) throws ContractException {
        Tuple2<BigInteger, DynamicArray<BFSPrecompiled.BfsInfo>> listOutput =
                bfsPrecompiled.list(path);
        if (!listOutput.getValue1().equals(BigInteger.ZERO)) {
            RetCode precompiledResponse =
                    PrecompiledRetCode.getPrecompiledResponse(
                            listOutput.getValue1().intValue(), "");
            throw new ContractException(
                    "BfsService: list return error code: "
                            + listOutput.getValue1()
                            + ", error msg: "
                            + precompiledResponse.getMessage(),
                    precompiledResponse.getCode());
        }
        return listOutput.getValue2().getValue().stream()
                .map(BFSInfo::fromPrecompiledBfs)
                .collect(Collectors.toList());
    }

    public Tuple2<BigInteger, List<BFSPrecompiled.BfsInfo>> list(
            String absolutePath, BigInteger offset, BigInteger limit) throws ContractException {
        PrecompiledVersionCheck.LS_PAGE_VERSION.checkVersion(currentVersion);
        Tuple2<BigInteger, DynamicArray<BFSPrecompiled.BfsInfo>> listOutput =
                bfsPrecompiled.list(absolutePath, offset, limit);
        return new Tuple2<>(listOutput.getValue1(), listOutput.getValue2().getValue());
    }

    public Tuple2<BigInteger, List<BFSInfo>> listBFSInfo(
            String absolutePath, BigInteger offset, BigInteger limit) throws ContractException {
        PrecompiledVersionCheck.LS_PAGE_VERSION.checkVersion(currentVersion);
        Tuple2<BigInteger, DynamicArray<BFSPrecompiled.BfsInfo>> listOutput =
                bfsPrecompiled.list(absolutePath, offset, limit);
        return new Tuple2<>(
                listOutput.getValue1(),
                listOutput.getValue2().getValue().stream()
                        .map(BFSInfo::fromPrecompiledBfs)
                        .collect(Collectors.toList()));
    }

    /**
     * check the path file is exist in BFS
     *
     * @param absolutePath absolute path in BFS
     * @return if file exist, then return a BFSInfo; if not exist, then return null
     */
    public BFSInfo isExist(String absolutePath) throws ContractException {
        PrecompiledVersionCheck.LS_PAGE_VERSION.checkVersion(currentVersion);
        Tuple2<String, String> parentPathAndBaseName =
                BFSUtils.getParentPathAndBaseName(absolutePath);
        String parent = parentPathAndBaseName.getValue1();
        String child = parentPathAndBaseName.getValue2();
        if (BFSUtils.BFS_SYSTEM_PATH.contains(absolutePath)) {
            return new BFSInfo(child, BFSUtils.BFS_TYPE_DIR);
        }
        int offset = 0;
        int limit = 500;
        while (true) {
            Tuple2<BigInteger, DynamicArray<BFSPrecompiled.BfsInfo>> listOutput =
                    bfsPrecompiled.list(
                            parent, BigInteger.valueOf(offset), BigInteger.valueOf(limit));
            if (!listOutput.getValue1().equals(BigInteger.ZERO)) {
                break;
            }
            for (BFSPrecompiled.BfsInfo bfsInfo : listOutput.getValue2().getValue()) {
                if (bfsInfo.fileName.equals(child)) {
                    return BFSInfo.fromPrecompiledBfs(bfsInfo);
                }
            }
            offset += limit + 1;
            limit = listOutput.getValue1().intValue();
        }
        return null;
    }

    public RetCode link(String name, String version, String contractAddress, String abi)
            throws ContractException {
        TransactionReceipt transactionReceipt =
                bfsPrecompiled.link(name, version, contractAddress, abi);
        return ReceiptParser.parseTransactionReceipt(
                transactionReceipt, tr -> bfsPrecompiled.getLinkWithVersionOutput(tr).getValue1());
    }

    public RetCode link(String absolutePath, String contractAddress, String abi)
            throws ContractException {
        PrecompiledVersionCheck.LINK_SIMPLE_VERSION.checkVersion(currentVersion);
        TransactionReceipt transactionReceipt =
                bfsPrecompiled.link(absolutePath, contractAddress, abi);
        return ReceiptParser.parseTransactionReceipt(
                transactionReceipt, tr -> bfsPrecompiled.getLinkOutput(tr).getValue1());
    }

    public String readlink(String absolutePath) throws ContractException {
        return bfsPrecompiled.readlink(absolutePath);
    }
}
