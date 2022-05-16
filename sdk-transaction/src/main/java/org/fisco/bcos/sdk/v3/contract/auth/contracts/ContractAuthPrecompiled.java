package org.fisco.bcos.sdk.v3.contract.auth.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes4;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.utils.StringUtils;

@SuppressWarnings("unchecked")
public class ContractAuthPrecompiled extends Contract {
    public static final String[] BINARY_ARRAY = {
        "608060405234801561001057600080fd5b5061048a806100206000396000f3fe608060405234801561001057600080fd5b50600436106100ea5760003560e01c806364efb22b1161008c578063bb0aa40c11610066578063bb0aa40c146101c9578063c53057b4146101d7578063cb7c5c1114610121578063d8662aa4146101e557600080fd5b806364efb22b1461017f57806381c81cdc146101a55780639cc3ca0f146101bb57600080fd5b80632c8c4a4f116100c85780632c8c4a4f1461014d57806356bd7084146101715780636154809914610171578063630577e51461014d57600080fd5b80630578519a146100ef5780630c82b73d146101215780631749bea914610146575b600080fd5b6101096100fd366004610227565b60006060809250925092565b604051610118939291906102eb565b60405180910390f35b61013861012f366004610323565b60009392505050565b604051908152602001610118565b6000610138565b61016161015b366004610366565b50600090565b6040519015158152602001610118565b61013861015b366004610366565b61018d61015b366004610366565b6040516001600160a01b039091168152602001610118565b6101386101b3366004610388565b600092915050565b61013861012f3660046103d5565b61013861015b36600461040f565b6101386101b336600461042a565b61016161012f366004610323565b80356001600160a01b038116811461020a57600080fd5b919050565b80356001600160e01b03198116811461020a57600080fd5b6000806040838503121561023a57600080fd5b610243836101f3565b91506102516020840161020f565b90509250929050565b600082825180855260208086019550808260051b8401018186016000805b858110156102dd57601f1980888603018b5283518051808752845b818110156102ae578281018901518882018a01528801610293565b818111156102be578589838a0101525b509b87019b601f01909116949094018501935091840191600101610278565b509198975050505050505050565b60ff84168152606060208201526000610307606083018561025a565b8281036040840152610319818561025a565b9695505050505050565b60008060006060848603121561033857600080fd5b610341846101f3565b925061034f6020850161020f565b915061035d604085016101f3565b90509250925092565b60006020828403121561037857600080fd5b610381826101f3565b9392505050565b6000806040838503121561039b57600080fd5b6103a4836101f3565b9150602083013580151581146103b957600080fd5b809150509250929050565b803560ff8116811461020a57600080fd5b6000806000606084860312156103ea57600080fd5b6103f3846101f3565b92506104016020850161020f565b915061035d604085016103c4565b60006020828403121561042157600080fd5b610381826103c4565b6000806040838503121561043d57600080fd5b610446836101f3565b9150610251602084016101f356fea2646970667358221220dd99f6799cd185865a37def05b080cf2f08e55644305404e7e0b45eb21224ea464736f6c634300080b0033"
    };

    public static final String BINARY = StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {
        "608060405234801561001057600080fd5b50610482806100206000396000f3fe608060405234801561001057600080fd5b50600436106100e95760003560e01c80639148dde11161008c578063c59065fb11610066578063c59065fb1461018b578063cea65584146101b7578063d3f8058c146101e0578063f73f575c1461016f57600080fd5b80639148dde11461017d57806391ca7f841461018b578063b0ca889b146101a957600080fd5b80631a205251116100c85780631a2052511461011f5780633d2521db14610151578063598ab59614610168578063851404e71461016f57600080fd5b80622e8d78146100ee578063026a22fd1461011f57806302b3703b1461013b575b600080fd5b6101026100fc36600461020a565b50600090565b6040516001600160a01b0390911681526020015b60405180910390f35b61012d6100fc36600461020a565b604051908152602001610116565b61012d61014936600461022c565b600092915050565b61012d61015f366004610288565b60009392505050565b600061012d565b61012d61015f3660046102cb565b61012d610149366004610305565b6101996100fc36600461020a565b6040519015158152602001610116565b61012d6100fc366004610341565b6101d16101c536600461035c565b60006060809250925092565b60405161011693929190610414565b61019961015f3660046102cb565b80356001600160a01b038116811461020557600080fd5b919050565b60006020828403121561021c57600080fd5b610225826101ee565b9392505050565b6000806040838503121561023f57600080fd5b610248836101ee565b9150610256602084016101ee565b90509250929050565b80356001600160e01b03198116811461020557600080fd5b803560ff8116811461020557600080fd5b60008060006060848603121561029d57600080fd5b6102a6846101ee565b92506102b46020850161025f565b91506102c260408501610277565b90509250925092565b6000806000606084860312156102e057600080fd5b6102e9846101ee565b92506102f76020850161025f565b91506102c2604085016101ee565b6000806040838503121561031857600080fd5b610321836101ee565b91506020830135801515811461033657600080fd5b809150509250929050565b60006020828403121561035357600080fd5b61022582610277565b6000806040838503121561036f57600080fd5b610378836101ee565b91506102566020840161025f565b600081518084526020808501808196508360051b810191508286016000805b86811015610406578385038a5282518051808752835b818110156103d6578281018901518882018a015288016103bb565b818111156103e6578489838a0101525b509a87019a601f01601f19169590950186019450918501916001016103a5565b509298975050505050505050565b60ff841681526060602082015260006104306060830185610386565b82810360408401526104428185610386565b969550505050505056fea2646970667358221220297bd5c6903984b4a9e3a70d2efb7e6ad6a36cd36b102321c389b38a1b136afc64736f6c634300080b0033"
    };

    public static final String SM_BINARY = StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"contractAddr\",\"type\":\"address\"},{\"internalType\":\"bytes4\",\"name\":\"func\",\"type\":\"bytes4\"},{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"checkMethodAuth\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[3630574244,3556246924],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"closeDeployAuth\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[1455255684,438325841],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"contractAddr\",\"type\":\"address\"},{\"internalType\":\"bytes4\",\"name\":\"func\",\"type\":\"bytes4\"},{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"closeMethodAuth\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[3413924881,2232681703],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"_address\",\"type\":\"address\"}],\"name\":\"contractAvailable\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[747391567,2445967236],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[],\"name\":\"deployType\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"selector\":[390708905,1502262678],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"contractAddr\",\"type\":\"address\"}],\"name\":\"getAdmin\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"selector\":[1693430315,3050872],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"path\",\"type\":\"address\"},{\"internalType\":\"bytes4\",\"name\":\"func\",\"type\":\"bytes4\"}],\"name\":\"getMethodAuth\",\"outputs\":[{\"internalType\":\"uint8\",\"name\":\"\",\"type\":\"uint8\"},{\"internalType\":\"string[]\",\"name\":\"\",\"type\":\"string[]\"},{\"internalType\":\"string[]\",\"name\":\"\",\"type\":\"string[]\"}],\"selector\":[91771290,3467007364],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"hasDeployAuth\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[1661302757,3314574843],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"openDeployAuth\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[1632927897,40510205],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"contractAddr\",\"type\":\"address\"},{\"internalType\":\"bytes4\",\"name\":\"func\",\"type\":\"bytes4\"},{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"openMethodAuth\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[209893181,4148123484],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"contractAddr\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"admin\",\"type\":\"address\"}],\"name\":\"resetAdmin\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[3308279732,45314107],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"_address\",\"type\":\"address\"},{\"internalType\":\"bool\",\"name\":\"isFreeze\",\"type\":\"bool\"}],\"name\":\"setContractStatus\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[2177375452,2437471713],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"uint8\",\"name\":\"_type\",\"type\":\"uint8\"}],\"name\":\"setDeployAuthType\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[3138036748,2966063259],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"contractAddr\",\"type\":\"address\"},{\"internalType\":\"bytes4\",\"name\":\"func\",\"type\":\"bytes4\"},{\"internalType\":\"uint8\",\"name\":\"authType\",\"type\":\"uint8\"}],\"name\":\"setMethodAuthType\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[2630076943,1025843675],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_CHECKMETHODAUTH = "checkMethodAuth";

    public static final String FUNC_CLOSEDEPLOYAUTH = "closeDeployAuth";

    public static final String FUNC_CLOSEMETHODAUTH = "closeMethodAuth";

    public static final String FUNC_CONTRACTAVAILABLE = "contractAvailable";

    public static final String FUNC_DEPLOYTYPE = "deployType";

    public static final String FUNC_GETADMIN = "getAdmin";

    public static final String FUNC_GETMETHODAUTH = "getMethodAuth";

    public static final String FUNC_HASDEPLOYAUTH = "hasDeployAuth";

    public static final String FUNC_OPENDEPLOYAUTH = "openDeployAuth";

    public static final String FUNC_OPENMETHODAUTH = "openMethodAuth";

    public static final String FUNC_RESETADMIN = "resetAdmin";

    public static final String FUNC_SETCONTRACTSTATUS = "setContractStatus";

    public static final String FUNC_SETDEPLOYAUTHTYPE = "setDeployAuthType";

    public static final String FUNC_SETMETHODAUTHTYPE = "setMethodAuthType";

    protected ContractAuthPrecompiled(
            String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public Boolean checkMethodAuth(String contractAddr, byte[] func, String account)
            throws ContractException {
        final Function function =
                new Function(
                        FUNC_CHECKMETHODAUTH,
                        Arrays.<Type>asList(
                                new Address(contractAddr), new Bytes4(func), new Address(account)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public TransactionReceipt closeDeployAuth(String account) {
        final Function function =
                new Function(
                        FUNC_CLOSEDEPLOYAUTH,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String closeDeployAuth(String account, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_CLOSEDEPLOYAUTH,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForCloseDeployAuth(String account) {
        final Function function =
                new Function(
                        FUNC_CLOSEDEPLOYAUTH,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<String> getCloseDeployAuthInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_CLOSEDEPLOYAUTH,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getCloseDeployAuthOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_CLOSEDEPLOYAUTH,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt closeMethodAuth(String contractAddr, byte[] func, String account) {
        final Function function =
                new Function(
                        FUNC_CLOSEMETHODAUTH,
                        Arrays.<Type>asList(
                                new Address(contractAddr), new Bytes4(func), new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String closeMethodAuth(
            String contractAddr, byte[] func, String account, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_CLOSEMETHODAUTH,
                        Arrays.<Type>asList(
                                new Address(contractAddr), new Bytes4(func), new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForCloseMethodAuth(
            String contractAddr, byte[] func, String account) {
        final Function function =
                new Function(
                        FUNC_CLOSEMETHODAUTH,
                        Arrays.<Type>asList(
                                new Address(contractAddr), new Bytes4(func), new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple3<String, byte[], String> getCloseMethodAuthInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_CLOSEMETHODAUTH,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {},
                                new TypeReference<Bytes4>() {},
                                new TypeReference<Address>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<String, byte[], String>(
                (String) results.get(0).getValue(),
                (byte[]) results.get(1).getValue(),
                (String) results.get(2).getValue());
    }

    public Tuple1<BigInteger> getCloseMethodAuthOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_CLOSEMETHODAUTH,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public Boolean contractAvailable(String address) throws ContractException {
        final Function function =
                new Function(
                        FUNC_CONTRACTAVAILABLE,
                        Arrays.<Type>asList(new Address(address)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public BigInteger deployType() throws ContractException {
        final Function function =
                new Function(
                        FUNC_DEPLOYTYPE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public String getAdmin(String contractAddr) throws ContractException {
        final Function function =
                new Function(
                        FUNC_GETADMIN,
                        Arrays.<Type>asList(new Address(contractAddr)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public Tuple3<BigInteger, List<String>, List<String>> getMethodAuth(String path, byte[] func)
            throws ContractException {
        final Function function =
                new Function(
                        FUNC_GETMETHODAUTH,
                        Arrays.<Type>asList(new Address(path), new Bytes4(func)),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Uint8>() {},
                                new TypeReference<DynamicArray<Utf8String>>() {},
                                new TypeReference<DynamicArray<Utf8String>>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple3<>(
                (BigInteger) results.get(0).getValue(),
                convertToNative((List<Utf8String>) results.get(1).getValue()),
                convertToNative((List<Utf8String>) results.get(2).getValue()));
    }

    public Boolean hasDeployAuth(String account) throws ContractException {
        final Function function =
                new Function(
                        FUNC_HASDEPLOYAUTH,
                        Arrays.<Type>asList(new Address(account)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public TransactionReceipt openDeployAuth(String account) {
        final Function function =
                new Function(
                        FUNC_OPENDEPLOYAUTH,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String openDeployAuth(String account, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_OPENDEPLOYAUTH,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForOpenDeployAuth(String account) {
        final Function function =
                new Function(
                        FUNC_OPENDEPLOYAUTH,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<String> getOpenDeployAuthInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_OPENDEPLOYAUTH,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getOpenDeployAuthOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_OPENDEPLOYAUTH,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt openMethodAuth(String contractAddr, byte[] func, String account) {
        final Function function =
                new Function(
                        FUNC_OPENMETHODAUTH,
                        Arrays.<Type>asList(
                                new Address(contractAddr), new Bytes4(func), new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String openMethodAuth(
            String contractAddr, byte[] func, String account, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_OPENMETHODAUTH,
                        Arrays.<Type>asList(
                                new Address(contractAddr), new Bytes4(func), new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForOpenMethodAuth(
            String contractAddr, byte[] func, String account) {
        final Function function =
                new Function(
                        FUNC_OPENMETHODAUTH,
                        Arrays.<Type>asList(
                                new Address(contractAddr), new Bytes4(func), new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple3<String, byte[], String> getOpenMethodAuthInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_OPENMETHODAUTH,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {},
                                new TypeReference<Bytes4>() {},
                                new TypeReference<Address>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<String, byte[], String>(
                (String) results.get(0).getValue(),
                (byte[]) results.get(1).getValue(),
                (String) results.get(2).getValue());
    }

    public Tuple1<BigInteger> getOpenMethodAuthOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_OPENMETHODAUTH,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt resetAdmin(String contractAddr, String admin) {
        final Function function =
                new Function(
                        FUNC_RESETADMIN,
                        Arrays.<Type>asList(new Address(contractAddr), new Address(admin)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String resetAdmin(String contractAddr, String admin, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_RESETADMIN,
                        Arrays.<Type>asList(new Address(contractAddr), new Address(admin)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForResetAdmin(String contractAddr, String admin) {
        final Function function =
                new Function(
                        FUNC_RESETADMIN,
                        Arrays.<Type>asList(new Address(contractAddr), new Address(admin)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<String, String> getResetAdminInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_RESETADMIN,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {}, new TypeReference<Address>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, String>(
                (String) results.get(0).getValue(), (String) results.get(1).getValue());
    }

    public Tuple1<BigInteger> getResetAdminOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_RESETADMIN,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt setContractStatus(String address, Boolean isFreeze) {
        final Function function =
                new Function(
                        FUNC_SETCONTRACTSTATUS,
                        Arrays.<Type>asList(new Address(address), new Bool(isFreeze)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String setContractStatus(
            String address, Boolean isFreeze, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_SETCONTRACTSTATUS,
                        Arrays.<Type>asList(new Address(address), new Bool(isFreeze)),
                        Collections.emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForSetContractStatus(String address, Boolean isFreeze) {
        final Function function =
                new Function(
                        FUNC_SETCONTRACTSTATUS,
                        Arrays.<Type>asList(new Address(address), new Bool(isFreeze)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<String, Boolean> getSetContractStatusInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_SETCONTRACTSTATUS,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {}, new TypeReference<Bool>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, Boolean>(
                (String) results.get(0).getValue(), (Boolean) results.get(1).getValue());
    }

    public Tuple1<BigInteger> getSetContractStatusOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_SETCONTRACTSTATUS,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt setDeployAuthType(BigInteger _type) {
        final Function function =
                new Function(
                        FUNC_SETDEPLOYAUTHTYPE,
                        Arrays.<Type>asList(new Uint8(_type)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String setDeployAuthType(BigInteger _type, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_SETDEPLOYAUTHTYPE,
                        Arrays.<Type>asList(new Uint8(_type)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForSetDeployAuthType(BigInteger _type) {
        final Function function =
                new Function(
                        FUNC_SETDEPLOYAUTHTYPE,
                        Arrays.<Type>asList(new Uint8(_type)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<BigInteger> getSetDeployAuthTypeInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_SETDEPLOYAUTHTYPE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getSetDeployAuthTypeOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_SETDEPLOYAUTHTYPE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt setMethodAuthType(
            String contractAddr, byte[] func, BigInteger authType) {
        final Function function =
                new Function(
                        FUNC_SETMETHODAUTHTYPE,
                        Arrays.<Type>asList(
                                new Address(contractAddr), new Bytes4(func), new Uint8(authType)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String setMethodAuthType(
            String contractAddr, byte[] func, BigInteger authType, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_SETMETHODAUTHTYPE,
                        Arrays.<Type>asList(
                                new Address(contractAddr), new Bytes4(func), new Uint8(authType)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForSetMethodAuthType(
            String contractAddr, byte[] func, BigInteger authType) {
        final Function function =
                new Function(
                        FUNC_SETMETHODAUTHTYPE,
                        Arrays.<Type>asList(
                                new Address(contractAddr), new Bytes4(func), new Uint8(authType)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple3<String, byte[], BigInteger> getSetMethodAuthTypeInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_SETMETHODAUTHTYPE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {},
                                new TypeReference<Bytes4>() {},
                                new TypeReference<Uint8>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<String, byte[], BigInteger>(
                (String) results.get(0).getValue(),
                (byte[]) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue());
    }

    public Tuple1<BigInteger> getSetMethodAuthTypeOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_SETMETHODAUTHTYPE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public static ContractAuthPrecompiled load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new ContractAuthPrecompiled(contractAddress, client, credential);
    }

    public static ContractAuthPrecompiled deploy(Client client, CryptoKeyPair credential)
            throws ContractException {
        return deploy(
                ContractAuthPrecompiled.class,
                client,
                credential,
                getBinary(client.getCryptoSuite()),
                getABI(),
                null,
                null);
    }
}
