package org.fisco.bcos.sdk.v3.contract.precompiled.bfs;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicStruct;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int32;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple4;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class BFSPrecompiled extends Contract {

    public static final String[] ABI_ARRAY = {
        "[{\"inputs\":[{\"internalType\":\"string\",\"name\":\"absolutePath\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_address\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_abi\",\"type\":\"string\"}],\"name\":\"link\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[2381387951,2713674467],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"name\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"version\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_address\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_abi\",\"type\":\"string\"}],\"name\":\"link\",\"outputs\":[{\"internalType\":\"int32\",\"name\":\"\",\"type\":\"int32\"}],\"selector\":[3785109455,1224568665],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"absolutePath\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"offset\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"limit\",\"type\":\"uint256\"}],\"name\":\"list\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"},{\"components\":[{\"internalType\":\"string\",\"name\":\"file_name\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"file_type\",\"type\":\"string\"},{\"internalType\":\"string[]\",\"name\":\"ext\",\"type\":\"string[]\"}],\"internalType\":\"struct BfsInfo[]\",\"name\":\"\",\"type\":\"tuple[]\"}],\"selector\":[2435788949,2035845870],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"absolutePath\",\"type\":\"string\"}],\"name\":\"list\",\"outputs\":[{\"internalType\":\"int32\",\"name\":\"\",\"type\":\"int32\"},{\"components\":[{\"internalType\":\"string\",\"name\":\"file_name\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"file_type\",\"type\":\"string\"},{\"internalType\":\"string[]\",\"name\":\"ext\",\"type\":\"string[]\"}],\"internalType\":\"struct BfsInfo[]\",\"name\":\"\",\"type\":\"tuple[]\"}],\"selector\":[4265787162,1825731743],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"absolutePath\",\"type\":\"string\"}],\"name\":\"mkdir\",\"outputs\":[{\"internalType\":\"int32\",\"name\":\"\",\"type\":\"int32\"}],\"selector\":[2271940274,494916525],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"absolutePath\",\"type\":\"string\"}],\"name\":\"readlink\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"selector\":[486910006,3786941869],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"rebuildBfs\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[2684871284,3887714310],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"absolutePath\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"fileType\",\"type\":\"string\"}],\"name\":\"touch\",\"outputs\":[{\"internalType\":\"int32\",\"name\":\"\",\"type\":\"int32\"}],\"selector\":[320863453,2170328757],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_LINK = "link";

    public static final String FUNC_LIST = "list";

    public static final String FUNC_MKDIR = "mkdir";

    public static final String FUNC_READLINK = "readlink";

    protected BFSPrecompiled(String contractAddress, Client client, CryptoKeyPair credential) {
        super("", contractAddress, client, credential);
    }

    public static String getABI() {
        return ABI;
    }

    public TransactionReceipt link(String absolutePath, String _address, String _abi) {
        final Function function =
                new Function(
                        FUNC_LINK,
                        Arrays.<Type>asList(
                                new Utf8String(absolutePath),
                                new Utf8String(_address),
                                new Utf8String(_abi)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String link(
            String absolutePath, String _address, String _abi, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_LINK,
                        Arrays.<Type>asList(
                                new Utf8String(absolutePath),
                                new Utf8String(_address),
                                new Utf8String(_abi)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForLink(String absolutePath, String _address, String _abi) {
        final Function function =
                new Function(
                        FUNC_LINK,
                        Arrays.<Type>asList(
                                new Utf8String(absolutePath),
                                new Utf8String(_address),
                                new Utf8String(_abi)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple3<String, String, String> getLinkInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_LINK,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<String, String, String>(
                (String) results.get(0).getValue(),
                (String) results.get(1).getValue(),
                (String) results.get(2).getValue());
    }

    public Tuple1<BigInteger> getLinkOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_LINK,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt link(String name, String version, String address, String abi) {
        final Function function =
                new Function(
                        FUNC_LINK,
                        Arrays.<Type>asList(
                                new Utf8String(name),
                                new Utf8String(version),
                                new Utf8String(address),
                                new Utf8String(abi)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String link(
            String name, String version, String address, String abi, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_LINK,
                        Arrays.<Type>asList(
                                new Utf8String(name),
                                new Utf8String(version),
                                new Utf8String(address),
                                new Utf8String(abi)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForLink(
            String name, String version, String address, String abi) {
        final Function function =
                new Function(
                        FUNC_LINK,
                        Arrays.<Type>asList(
                                new Utf8String(name),
                                new Utf8String(version),
                                new Utf8String(address),
                                new Utf8String(abi)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple4<String, String, String, String> getLinkStringStringStringStringInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_LINK,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple4<String, String, String, String>(
                (String) results.get(0).getValue(),
                (String) results.get(1).getValue(),
                (String) results.get(2).getValue(),
                (String) results.get(3).getValue());
    }

    public Tuple1<BigInteger> getLinkWithVersionOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_LINK,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int32>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public Tuple2<BigInteger, DynamicArray<BfsInfo>> list(
            String absolutePath, BigInteger offset, BigInteger limit) throws ContractException {
        final Function function =
                new Function(
                        FUNC_LIST,
                        Arrays.<Type>asList(
                                new Utf8String(absolutePath),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(offset),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(limit)),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Int256>() {},
                                new TypeReference<DynamicArray<BfsInfo>>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple2<>(
                (BigInteger) results.get(0).getValue(),
                new DynamicArray<>(BfsInfo.class, (List<BfsInfo>) results.get(1).getValue()));
    }

    public Tuple2<BigInteger, DynamicArray<BfsInfo>> list(String absolutePath)
            throws ContractException {
        final Function function =
                new Function(
                        FUNC_LIST,
                        Arrays.<Type>asList(new Utf8String(absolutePath)),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Int32>() {},
                                new TypeReference<DynamicArray<BfsInfo>>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple2<>(
                (BigInteger) results.get(0).getValue(),
                new DynamicArray<>(BfsInfo.class, (List<BfsInfo>) results.get(1).getValue()));
    }

    public TransactionReceipt mkdir(String absolutePath) {
        final Function function =
                new Function(
                        FUNC_MKDIR,
                        Arrays.<Type>asList(new Utf8String(absolutePath)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String mkdir(String absolutePath, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_MKDIR,
                        Arrays.<Type>asList(new Utf8String(absolutePath)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForMkdir(String absolutePath) {
        final Function function =
                new Function(
                        FUNC_MKDIR,
                        Arrays.<Type>asList(new Utf8String(absolutePath)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<String> getMkdirInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_MKDIR,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getMkdirOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_MKDIR,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int32>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public String readlink(String absolutePath) throws ContractException {
        final Function function =
                client.isWASM()
                        ? new Function(
                                FUNC_READLINK,
                                Arrays.<Type>asList(new Utf8String(absolutePath)),
                                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}))
                        : new Function(
                                FUNC_READLINK,
                                Arrays.<Type>asList(new Utf8String(absolutePath)),
                                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public static BFSPrecompiled load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new BFSPrecompiled(contractAddress, client, credential);
    }

    public static class BfsInfo extends DynamicStruct {
        public String fileName;

        public String fileType;

        public List<String> ext;

        public BfsInfo(Utf8String fileName, Utf8String fileType, DynamicArray<Utf8String> ext) {
            super(fileName, fileType, ext);
            this.fileName = fileName.getValue();
            this.fileType = fileType.getValue();
            this.ext =
                    ext.getValue().stream().map(Utf8String::getValue).collect(Collectors.toList());
        }

        public BfsInfo(String fileName, String fileType, List<String> ext) {
            super(
                    new Utf8String(fileName),
                    new Utf8String(fileType),
                    new DynamicArray<>(
                            Utf8String.class,
                            ext.stream().map(Utf8String::new).collect(Collectors.toList())));
            this.fileName = fileName;
            this.fileType = fileType;
            this.ext = ext;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public List<String> getExt() {
            return ext;
        }

        public void setExt(List<String> ext) {
            this.ext = ext;
        }
    }
}
