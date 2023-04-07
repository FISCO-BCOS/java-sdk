package org.fisco.bcos.sdk.v3.contract.precompiled.bfs;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.PrecompiledVersionCheck;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.EnumNodeVersion;
import org.fisco.bcos.sdk.v3.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

public class BFSService {
    private final BFSPrecompiled bfsPrecompiled;
    private EnumNodeVersion currentVersion;
    private final Client client;

    public BFSService(Client client, CryptoKeyPair credential) {
        this.bfsPrecompiled =
                BFSPrecompiled.load(
                        client.isWASM()
                                ? PrecompiledAddress.BFS_PRECOMPILED_NAME
                                : PrecompiledAddress.BFS_PRECOMPILED_ADDRESS,
                        client,
                        credential);
        this.currentVersion = client.getChainVersion();
        this.client = client;
    }

    public EnumNodeVersion getCurrentVersion() {
        return currentVersion;
    }

    public BFSPrecompiled getBfsPrecompiled() {
        return bfsPrecompiled;
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
        String child = FilenameUtils.getBaseName(absolutePath);
        if (BFSUtils.BFS_SYSTEM_PATH.contains(absolutePath)) {
            return new BFSInfo(child, BFSUtils.BFS_TYPE_DIR);
        }
        int offset = 0;
        int limit = 500;
        Tuple2<BigInteger, DynamicArray<BFSPrecompiled.BfsInfo>> list =
                bfsPrecompiled.list(
                        absolutePath, BigInteger.valueOf(offset), BigInteger.valueOf(limit));
        if (list.getValue1().equals(BigInteger.ZERO)) {
            if (list.getValue2().getValue().size() != 1) {
                // directory
                return new BFSInfo(child, BFSUtils.BFS_TYPE_DIR);
            } else {
                // if return size is 1
                BFSPrecompiled.BfsInfo bfsInfo = list.getValue2().getValue().get(0);
                if (child.equals(bfsInfo.fileName)) {
                    // if name == child
                    //      if type == dir, return dir
                    //      if type != dir, try to ls absolute+child again
                    //          if childLs is empty, it means child is regular
                    //          else it means child is dir
                    if (BFSUtils.BFS_TYPE_DIR.equals(bfsInfo.fileType)) {
                        return new BFSInfo(child, BFSUtils.BFS_TYPE_DIR);
                    } else {
                        Tuple2<BigInteger, DynamicArray<BFSPrecompiled.BfsInfo>> childList =
                                bfsPrecompiled.list(absolutePath + "/" + child);
                        if (childList.getValue2().getValue().isEmpty()) {
                            // not exist, it is regular file
                            return BFSInfo.fromPrecompiledBfs(bfsInfo);
                        } else {
                            return new BFSInfo(child, BFSUtils.BFS_TYPE_DIR);
                        }
                    }
                } else {
                    return new BFSInfo(child, BFSUtils.BFS_TYPE_DIR);
                }
            }
        } else if (list.getValue1().compareTo(BigInteger.ZERO) > 0) {
            return new BFSInfo(child, BFSUtils.BFS_TYPE_DIR);
        } else {
            return null;
        }
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

    public RetCode fixBfs(EnumNodeVersion version) throws ContractException {
        PrecompiledVersionCheck.V330_FIX_BFS_VERSION.checkVersion(currentVersion);
        TransactionReceipt transactionReceipt =
                bfsPrecompiled.fixBfs(BigInteger.valueOf(version.getVersion()));
        return ReceiptParser.parseTransactionReceipt(
                transactionReceipt, tr -> bfsPrecompiled.getFixBfsOutput(tr).getValue1());
    }

    public RetCode fixBfs() throws ContractException {
        this.currentVersion = client.getChainVersion();
        return fixBfs(currentVersion);
    }
}
