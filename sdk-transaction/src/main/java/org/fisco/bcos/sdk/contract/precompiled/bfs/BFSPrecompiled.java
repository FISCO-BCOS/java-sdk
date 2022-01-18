package org.fisco.bcos.sdk.contract.precompiled.bfs;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.codec.datatypes.DynamicStruct;
import org.fisco.bcos.sdk.codec.datatypes.Function;
import org.fisco.bcos.sdk.codec.datatypes.Type;
import org.fisco.bcos.sdk.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.codec.datatypes.generated.Int256;
import org.fisco.bcos.sdk.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.codec.datatypes.generated.tuples.generated.Tuple4;
import org.fisco.bcos.sdk.contract.Contract;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class BFSPrecompiled extends Contract {
    public static final String[] BINARY_ARRAY = {
        "608060405234801561001057600080fd5b506105c6806100206000396000f3fe608060405234801561001057600080fd5b50600436106100415760003560e01c8063876b0eb214610046578063e19c2fcf14610076578063fe42bf1a146100a6575b600080fd5b610060600480360381019061005b9190610145565b6100d7565b60405161006d9190610401565b60405180910390f35b610090600480360381019061008b9190610186565b6100de565b60405161009d9190610401565b60405180910390f35b6100c060048036038101906100bb9190610145565b6100e8565b6040516100ce92919061041c565b60405180910390f35b6000919050565b6000949350505050565b60006060915091565b600082601f83011261010257600080fd5b813561011561011082610479565b61044c565b9150808252602083016020830185838301111561013157600080fd5b61013c83828461053d565b50505092915050565b60006020828403121561015757600080fd5b600082013567ffffffffffffffff81111561017157600080fd5b61017d848285016100f1565b91505092915050565b6000806000806080858703121561019c57600080fd5b600085013567ffffffffffffffff8111156101b657600080fd5b6101c2878288016100f1565b945050602085013567ffffffffffffffff8111156101df57600080fd5b6101eb878288016100f1565b935050604085013567ffffffffffffffff81111561020857600080fd5b610214878288016100f1565b925050606085013567ffffffffffffffff81111561023157600080fd5b61023d878288016100f1565b91505092959194509250565b6000610255838361036a565b905092915050565b600061026983836103a3565b905092915050565b600061027c826104c5565b6102868185610500565b935083602082028501610298856104a5565b8060005b858110156102d457848403895281516102b58582610249565b94506102c0836104e6565b925060208a0199505060018101905061029c565b50829750879550505050505092915050565b60006102f1826104d0565b6102fb8185610511565b93508360208202850161030d856104b5565b8060005b85811015610349578484038952815161032a858261025d565b9450610335836104f3565b925060208a01995050600181019050610311565b50829750879550505050505092915050565b61036481610533565b82525050565b6000610375826104db565b61037f8185610522565b935061038f81856020860161054c565b6103988161057f565b840191505092915050565b600060608301600083015184820360008601526103c0828261036a565b915050602083015184820360208601526103da828261036a565b915050604083015184820360408601526103f48282610271565b9150508091505092915050565b6000602082019050610416600083018461035b565b92915050565b6000604082019050610431600083018561035b565b818103602083015261044381846102e6565b90509392505050565b6000604051905081810181811067ffffffffffffffff8211171561046f57600080fd5b8060405250919050565b600067ffffffffffffffff82111561049057600080fd5b601f19601f8301169050602081019050919050565b6000819050602082019050919050565b6000819050602082019050919050565b600081519050919050565b600081519050919050565b600081519050919050565b6000602082019050919050565b6000602082019050919050565b600082825260208201905092915050565b600082825260208201905092915050565b600082825260208201905092915050565b6000819050919050565b82818337600083830152505050565b60005b8381101561056a57808201518184015260208101905061054f565b83811115610579576000848401525b50505050565b6000601f19601f830116905091905056fea2646970667358221220b46156ecf727183a7addff1d19be32b54b8c33bb9960362fb4b982cc7282bdf264736f6c634300060a0033"
    };

    public static final String BINARY =
            org.fisco.bcos.sdk.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {
        "608060405234801561001057600080fd5b506105c6806100206000396000f3fe608060405234801561001057600080fd5b50600436106100415760003560e01c80631d7fd3ad1461004657806348fd6f59146100765780636cd2749f146100a6575b600080fd5b610060600480360381019061005b9190610145565b6100d7565b60405161006d9190610401565b60405180910390f35b610090600480360381019061008b9190610186565b6100de565b60405161009d9190610401565b60405180910390f35b6100c060048036038101906100bb9190610145565b6100e8565b6040516100ce92919061041c565b60405180910390f35b6000919050565b6000949350505050565b60006060915091565b600082601f83011261010257600080fd5b813561011561011082610479565b61044c565b9150808252602083016020830185838301111561013157600080fd5b61013c83828461053d565b50505092915050565b60006020828403121561015757600080fd5b600082013567ffffffffffffffff81111561017157600080fd5b61017d848285016100f1565b91505092915050565b6000806000806080858703121561019c57600080fd5b600085013567ffffffffffffffff8111156101b657600080fd5b6101c2878288016100f1565b945050602085013567ffffffffffffffff8111156101df57600080fd5b6101eb878288016100f1565b935050604085013567ffffffffffffffff81111561020857600080fd5b610214878288016100f1565b925050606085013567ffffffffffffffff81111561023157600080fd5b61023d878288016100f1565b91505092959194509250565b6000610255838361036a565b905092915050565b600061026983836103a3565b905092915050565b600061027c826104c5565b6102868185610500565b935083602082028501610298856104a5565b8060005b858110156102d457848403895281516102b58582610249565b94506102c0836104e6565b925060208a0199505060018101905061029c565b50829750879550505050505092915050565b60006102f1826104d0565b6102fb8185610511565b93508360208202850161030d856104b5565b8060005b85811015610349578484038952815161032a858261025d565b9450610335836104f3565b925060208a01995050600181019050610311565b50829750879550505050505092915050565b61036481610533565b82525050565b6000610375826104db565b61037f8185610522565b935061038f81856020860161054c565b6103988161057f565b840191505092915050565b600060608301600083015184820360008601526103c0828261036a565b915050602083015184820360208601526103da828261036a565b915050604083015184820360408601526103f48282610271565b9150508091505092915050565b6000602082019050610416600083018461035b565b92915050565b6000604082019050610431600083018561035b565b818103602083015261044381846102e6565b90509392505050565b6000604051905081810181811067ffffffffffffffff8211171561046f57600080fd5b8060405250919050565b600067ffffffffffffffff82111561049057600080fd5b601f19601f8301169050602081019050919050565b6000819050602082019050919050565b6000819050602082019050919050565b600081519050919050565b600081519050919050565b600081519050919050565b6000602082019050919050565b6000602082019050919050565b600082825260208201905092915050565b600082825260208201905092915050565b600082825260208201905092915050565b6000819050919050565b82818337600083830152505050565b60005b8381101561056a57808201518184015260208101905061054f565b83811115610579576000848401525b50505050565b6000601f19601f830116905091905056fea2646970667358221220cf878b47a0a6294058f6bf3480888fa0f8370bea0207c51a94b122b9e7857fc864736f6c634300060a0033"
    };

    public static final String SM_BINARY =
            org.fisco.bcos.sdk.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"inputs\":[{\"internalType\":\"string\",\"name\":\"name\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"version\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_address\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_abi\",\"type\":\"string\"}],\"name\":\"link\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"absolutPath\",\"type\":\"string\"}],\"name\":\"list\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"},{\"components\":[{\"internalType\":\"string\",\"name\":\"file_name\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"file_type\",\"type\":\"string\"},{\"internalType\":\"string[]\",\"name\":\"ext\",\"type\":\"string[]\"}],\"internalType\":\"struct BfsInfo[]\",\"name\":\"\",\"type\":\"tuple[]\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"absolutPath\",\"type\":\"string\"}],\"name\":\"mkdir\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_LINK = "link";

    public static final String FUNC_LIST = "list";

    public static final String FUNC_MKDIR = "mkdir";

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

    public Tuple2<BigInteger, DynamicArray<BfsInfo>> list(String absolutPath)
            throws ContractException {
        final Function function =
                new Function(
                        FUNC_LIST,
                        Arrays.<Type>asList(new Utf8String(absolutPath)),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Int256>() {},
                                new TypeReference<DynamicArray<BfsInfo>>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple2<>(
                (BigInteger) results.get(0).getValue(),
                new DynamicArray<>(BfsInfo.class, (List<BfsInfo>) results.get(1).getValue()));
    }

    public TransactionReceipt mkdir(String absolutPath) {
        final Function function =
                new Function(
                        FUNC_MKDIR,
                        Arrays.<Type>asList(new Utf8String(absolutPath)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void mkdir(String absolutPath, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_MKDIR,
                        Arrays.<Type>asList(new Utf8String(absolutPath)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForMkdir(String absolutPath) {
        final Function function =
                new Function(
                        FUNC_MKDIR,
                        Arrays.<Type>asList(new Utf8String(absolutPath)),
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
