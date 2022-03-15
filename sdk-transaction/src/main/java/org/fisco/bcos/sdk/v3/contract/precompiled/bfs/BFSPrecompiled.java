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
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple4;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class BFSPrecompiled extends Contract {
    public static final String[] BINARY_ARRAY = {
        "608060405234801561001057600080fd5b506103d1806100206000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c80631d05a83614610051578063876b0eb214610082578063e19c2fcf1461009e578063fe42bf1a146100b6575b600080fd5b61006561005f36600461017e565b50600090565b6040516001600160a01b0390911681526020015b60405180910390f35b61009061005f36600461017e565b604051908152602001610079565b6100906100ac3660046101bb565b6000949350505050565b6100cd6100c436600461017e565b60006060915091565b6040516100799291906102b5565b634e487b7160e01b600052604160045260246000fd5b600082601f83011261010257600080fd5b813567ffffffffffffffff8082111561011d5761011d6100db565b604051601f8301601f19908116603f01168101908282118183101715610145576101456100db565b8160405283815286602085880101111561015e57600080fd5b836020870160208301376000602085830101528094505050505092915050565b60006020828403121561019057600080fd5b813567ffffffffffffffff8111156101a757600080fd5b6101b3848285016100f1565b949350505050565b600080600080608085870312156101d157600080fd5b843567ffffffffffffffff808211156101e957600080fd5b6101f5888389016100f1565b9550602087013591508082111561020b57600080fd5b610217888389016100f1565b9450604087013591508082111561022d57600080fd5b610239888389016100f1565b9350606087013591508082111561024f57600080fd5b5061025c878288016100f1565b91505092959194509250565b6000815180845260005b8181101561028e57602081850181015186830182015201610272565b818111156102a0576000602083870101525b50601f01601f19169290920160200192915050565b600060408083018584526020828186015281865180845260609350838701915060058482821b890101848a016000805b85811015610389578b8403605f19018752825180518a86526103098b870182610268565b9050898201518682038b8801526103208282610268565b928d0151878403888f01528051808552908c019392508b8301915080891b83018c01865b8281101561037257601f19858303018452610360828751610268565b958e0195938e01939150600101610344565b509a8c019a975050509389019350506001016102e5565b50919c9b50505050505050505050505056fea2646970667358221220b6b634d3e0fb0db6588dc02b816f30e29f986ac3f95841fee4209fc5a55986ac64736f6c634300080b0033"
    };

    public static final String BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {
        "608060405234801561001057600080fd5b506103d1806100206000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c80631d7fd3ad1461005157806348fd6f59146100785780636cd2749f14610090578063e1b825ad146100b5575b600080fd5b61006561005f36600461017e565b50600090565b6040519081526020015b60405180910390f35b6100656100863660046101bb565b6000949350505050565b6100a761009e36600461017e565b60006060915091565b60405161006f9291906102b5565b6100c361005f36600461017e565b6040516001600160a01b03909116815260200161006f565b63b95aa35560e01b600052604160045260246000fd5b600082601f83011261010257600080fd5b813567ffffffffffffffff8082111561011d5761011d6100db565b604051601f8301601f19908116603f01168101908282118183101715610145576101456100db565b8160405283815286602085880101111561015e57600080fd5b836020870160208301376000602085830101528094505050505092915050565b60006020828403121561019057600080fd5b813567ffffffffffffffff8111156101a757600080fd5b6101b3848285016100f1565b949350505050565b600080600080608085870312156101d157600080fd5b843567ffffffffffffffff808211156101e957600080fd5b6101f5888389016100f1565b9550602087013591508082111561020b57600080fd5b610217888389016100f1565b9450604087013591508082111561022d57600080fd5b610239888389016100f1565b9350606087013591508082111561024f57600080fd5b5061025c878288016100f1565b91505092959194509250565b6000815180845260005b8181101561028e57602081850181015186830182015201610272565b818111156102a0576000602083870101525b50601f01601f19169290920160200192915050565b600060408083018584526020828186015281865180845260609350838701915060058482821b890101848a016000805b85811015610389578b8403605f19018752825180518a86526103098b870182610268565b9050898201518682038b8801526103208282610268565b928d0151878403888f01528051808552908c019392508b8301915080891b83018c01865b8281101561037257601f19858303018452610360828751610268565b958e0195938e01939150600101610344565b509a8c019a975050509389019350506001016102e5565b50919c9b50505050505050505050505056fea26469706673582212206f27cef46f1af78179967a301c7ed379936db8b0739cfae81029a18abd4effbb64736f6c634300080b0033"
    };

    public static final String SM_BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"name\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"version\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_address\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_abi\",\"type\":\"string\"}],\"name\":\"link\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[3785109455,1224568665],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"absolutePath\",\"type\":\"string\"}],\"name\":\"list\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"},{\"components\":[{\"internalType\":\"string\",\"name\":\"file_name\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"file_type\",\"type\":\"string\"},{\"internalType\":\"string[]\",\"name\":\"ext\",\"type\":\"string[]\"}],\"internalType\":\"struct BfsInfo[]\",\"name\":\"\",\"type\":\"tuple[]\"}],\"selector\":[4265787162,1825731743],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"absolutePath\",\"type\":\"string\"}],\"name\":\"mkdir\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[2271940274,494916525],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"absolutePath\",\"type\":\"string\"}],\"name\":\"readlink\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"selector\":[486910006,3786941869],\"stateMutability\":\"view\",\"type\":\"function\"}]"
    };

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_LINK = "link";

    public static final String FUNC_LIST = "list";

    public static final String FUNC_MKDIR = "mkdir";

    public static final String FUNC_READLINK = "readlink";

    protected BFSPrecompiled(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
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

    public void link(
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
        asyncExecuteTransaction(function, callback);
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

    public Tuple4<String, String, String, String> getLinkInput(
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

    public Tuple2<BigInteger, DynamicArray<BfsInfo>> list(String absolutePath)
            throws ContractException {
        final Function function =
                new Function(
                        FUNC_LIST,
                        Arrays.<Type>asList(new Utf8String(absolutePath)),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Int256>() {},
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

    public void mkdir(String absolutePath, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_MKDIR,
                        Arrays.<Type>asList(new Utf8String(absolutePath)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
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
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public String readlink(String absolutePath) throws ContractException {
        final Function function =
                new Function(
                        FUNC_READLINK,
                        Arrays.<Type>asList(new Utf8String(absolutePath)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public static BFSPrecompiled load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new BFSPrecompiled(contractAddress, client, credential);
    }

    public static BFSPrecompiled deploy(Client client, CryptoKeyPair credential)
            throws ContractException {
        return deploy(
                BFSPrecompiled.class,
                client,
                credential,
                getBinary(client.getCryptoSuite()),
                null,
                null,
                null);
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
