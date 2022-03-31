package org.fisco.bcos.sdk.v3.contract.auth.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
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
        "608060405234801561001057600080fd5b50610618806100206000396000f3fe608060405234801561001057600080fd5b50600436106100a95760003560e01c806364efb22b1161007157806364efb22b146102795780639cc3ca0f146102fd578063bb0aa40c1461038b578063c53057b4146103d0578063cb7c5c1114610448578063d8662aa4146104e9576100a9565b80630c82b73d146100ae5780631749bea91461014f57806356bd70841461016d57806361548099146101c5578063630577e51461021d575b600080fd5b610139600480360360608110156100c457600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080357bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19169060200190929190803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061058e565b6040518082815260200191505060405180910390f35b610157610597565b6040518082815260200191505060405180910390f35b6101af6004803603602081101561018357600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061059c565b6040518082815260200191505060405180910390f35b610207600480360360208110156101db57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506105a3565b6040518082815260200191505060405180910390f35b61025f6004803603602081101561023357600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506105aa565b604051808215151515815260200191505060405180910390f35b6102bb6004803603602081101561028f57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506105b1565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b6103756004803603606081101561031357600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080357bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19169060200190929190803560ff1690602001909291905050506105b8565b6040518082815260200191505060405180910390f35b6103ba600480360360208110156103a157600080fd5b81019080803560ff1690602001909291905050506105c1565b6040518082815260200191505060405180910390f35b610432600480360360408110156103e657600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506105c8565b6040518082815260200191505060405180910390f35b6104d36004803603606081101561045e57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080357bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19169060200190929190803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506105d0565b6040518082815260200191505060405180910390f35b610574600480360360608110156104ff57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080357bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19169060200190929190803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506105d9565b604051808215151515815260200191505060405180910390f35b60009392505050565b600090565b6000919050565b6000919050565b6000919050565b6000919050565b60009392505050565b6000919050565b600092915050565b60009392505050565b6000939250505056fea264697066735822122074017eccd67e3da3244f0d8e50cc48074342d2ea91d0b818e11ce2500f972b4764736f6c634300060a0033"
    };

    public static final String BINARY = StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {
        "608060405234801561001057600080fd5b50610617806100206000396000f3fe608060405234801561001057600080fd5b50600436106100a85760003560e01c8063598ab59611610071578063598ab596146102e7578063851404e714610305578063b0ca889b146103a6578063c59065fb146103eb578063d3f8058c14610447578063f73f575c146104ec576100a8565b80622e8d78146100ad578063026a22fd1461013157806302b3703b146101895780631a205251146102015780633d2521db14610259575b600080fd5b6100ef600480360360208110156100c357600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061058d565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b6101736004803603602081101561014757600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610594565b6040518082815260200191505060405180910390f35b6101eb6004803603604081101561019f57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061059b565b6040518082815260200191505060405180910390f35b6102436004803603602081101561021757600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506105a3565b6040518082815260200191505060405180910390f35b6102d16004803603606081101561026f57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080357bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19169060200190929190803560ff1690602001909291905050506105aa565b6040518082815260200191505060405180910390f35b6102ef6105b3565b6040518082815260200191505060405180910390f35b6103906004803603606081101561031b57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080357bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19169060200190929190803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506105b8565b6040518082815260200191505060405180910390f35b6103d5600480360360208110156103bc57600080fd5b81019080803560ff1690602001909291905050506105c1565b6040518082815260200191505060405180910390f35b61042d6004803603602081101561040157600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506105c8565b604051808215151515815260200191505060405180910390f35b6104d26004803603606081101561045d57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080357bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19169060200190929190803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506105cf565b604051808215151515815260200191505060405180910390f35b6105776004803603606081101561050257600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080357bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19169060200190929190803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506105d8565b6040518082815260200191505060405180910390f35b6000919050565b6000919050565b600092915050565b6000919050565b60009392505050565b600090565b60009392505050565b6000919050565b6000919050565b60009392505050565b6000939250505056fea2646970667358221220b05b26524c943e771b230cdbf0f33110bc85416cae88f305fdf4ba56a6d76cf464736f6c634300060a0033"
    };

    public static final String SM_BINARY = StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"inputs\":[{\"internalType\":\"address\",\"name\":\"contractAddr\",\"type\":\"address\"},{\"internalType\":\"bytes4\",\"name\":\"func\",\"type\":\"bytes4\"},{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"checkMethodAuth\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"closeDeployAuth\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"contractAddr\",\"type\":\"address\"},{\"internalType\":\"bytes4\",\"name\":\"func\",\"type\":\"bytes4\"},{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"closeMethodAuth\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"deployType\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"contractAddr\",\"type\":\"address\"}],\"name\":\"getAdmin\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"hasDeployAuth\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"openDeployAuth\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"contractAddr\",\"type\":\"address\"},{\"internalType\":\"bytes4\",\"name\":\"func\",\"type\":\"bytes4\"},{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"openMethodAuth\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"contractAddr\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"admin\",\"type\":\"address\"}],\"name\":\"resetAdmin\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"uint8\",\"name\":\"_type\",\"type\":\"uint8\"}],\"name\":\"setDeployAuthType\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"contractAddr\",\"type\":\"address\"},{\"internalType\":\"bytes4\",\"name\":\"func\",\"type\":\"bytes4\"},{\"internalType\":\"uint8\",\"name\":\"authType\",\"type\":\"uint8\"}],\"name\":\"setMethodAuthType\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_CHECKMETHODAUTH = "checkMethodAuth";

    public static final String FUNC_CLOSEDEPLOYAUTH = "closeDeployAuth";

    public static final String FUNC_CLOSEMETHODAUTH = "closeMethodAuth";

    public static final String FUNC_DEPLOYTYPE = "deployType";

    public static final String FUNC_GETADMIN = "getAdmin";

    public static final String FUNC_HASDEPLOYAUTH = "hasDeployAuth";

    public static final String FUNC_OPENDEPLOYAUTH = "openDeployAuth";

    public static final String FUNC_OPENMETHODAUTH = "openMethodAuth";

    public static final String FUNC_RESETADMIN = "resetAdmin";

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

    public void closeDeployAuth(String account, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_CLOSEDEPLOYAUTH,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
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

    public void closeMethodAuth(
            String contractAddr, byte[] func, String account, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_CLOSEMETHODAUTH,
                        Arrays.<Type>asList(
                                new Address(contractAddr), new Bytes4(func), new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
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

    public void openDeployAuth(String account, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_OPENDEPLOYAUTH,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
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

    public void openMethodAuth(
            String contractAddr, byte[] func, String account, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_OPENMETHODAUTH,
                        Arrays.<Type>asList(
                                new Address(contractAddr), new Bytes4(func), new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
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

    public void resetAdmin(String contractAddr, String admin, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_RESETADMIN,
                        Arrays.<Type>asList(new Address(contractAddr), new Address(admin)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
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

    public TransactionReceipt setDeployAuthType(BigInteger _type) {
        final Function function =
                new Function(
                        FUNC_SETDEPLOYAUTHTYPE,
                        Arrays.<Type>asList(new Uint8(_type)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void setDeployAuthType(BigInteger _type, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_SETDEPLOYAUTHTYPE,
                        Arrays.<Type>asList(new Uint8(_type)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
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

    public void setMethodAuthType(
            String contractAddr, byte[] func, BigInteger authType, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_SETMETHODAUTHTYPE,
                        Arrays.<Type>asList(
                                new Address(contractAddr), new Bytes4(func), new Uint8(authType)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
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
                null,
                null,
                null);
    }
}
