package org.fisco.bcos.sdk.contract.precompiled.gaschargemgr;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.Address;
import org.fisco.bcos.sdk.abi.datatypes.DynamicArray;
import org.fisco.bcos.sdk.abi.datatypes.Function;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.abi.datatypes.generated.Int256;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.Contract;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class GasChargeManagePrecompiled extends Contract {
    public static final String[] BINARY_ARRAY = {
        "608060405234801561001057600080fd5b50610320806100206000396000f300608060405260043610610078576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff168063470553211461007d578063867bde5d146100e5578063a3ffa9cd1461013c578063a9f2b9a8146101a4578063caf39c5114610210578063f001f6a314610267575b600080fd5b34801561008957600080fd5b506100c8600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001909291905050506102c5565b604051808381526020018281526020019250505060405180910390f35b3480156100f157600080fd5b50610126600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506102cf565b6040518082815260200191505060405180910390f35b34801561014857600080fd5b50610187600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001909291905050506102d6565b604051808381526020018281526020019250505060405180910390f35b3480156101b057600080fd5b506101b96102e0565b6040518080602001828103825283818151815260200191508051906020019060200280838360005b838110156101fc5780820151818401526020810190506101e1565b505050509050019250505060405180910390f35b34801561021c57600080fd5b50610251600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506102e5565b6040518082815260200191505060405180910390f35b34801561027357600080fd5b506102a8600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506102ec565b604051808381526020018281526020019250505060405180910390f35b6000809250929050565b6000919050565b6000809250929050565b606090565b6000919050565b6000809150915600a165627a7a7230582010d6854cc064344288619f8cb0d38e560caa996b227275c2d8503dca13980c1a0029"
    };

    public static final String BINARY = String.join("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {
        "608060405234801561001057600080fd5b5061031f806100206000396000f300608060405260043610610077576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff168062ba8f9b1461007c5780633009a33c146100da578063790162d714610131578063a06cc6ae14610199578063c74b68d914610205578063ec5975031461026d575b600080fd5b34801561008857600080fd5b506100bd600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506102c4565b604051808381526020018281526020019250505060405180910390f35b3480156100e657600080fd5b5061011b600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506102cc565b6040518082815260200191505060405180910390f35b34801561013d57600080fd5b5061017c600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001909291905050506102d3565b604051808381526020018281526020019250505060405180910390f35b3480156101a557600080fd5b506101ae6102dd565b6040518080602001828103825283818151815260200191508051906020019060200280838360005b838110156101f15780820151818401526020810190506101d6565b505050509050019250505060405180910390f35b34801561021157600080fd5b50610250600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001909291905050506102e2565b604051808381526020018281526020019250505060405180910390f35b34801561027957600080fd5b506102ae600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506102ec565b6040518082815260200191505060405180910390f35b600080915091565b6000919050565b6000809250929050565b606090565b6000809250929050565b60009190505600a165627a7a72305820a1c13d54d68b4887f1ec0b4898582edd56e0bb5d692faf271de07d4fbc21c1730029"
    };

    public static final String SM_BINARY = String.join("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"constant\":false,\"inputs\":[{\"name\":\"userAccount\",\"type\":\"address\"},{\"name\":\"gasValue\",\"type\":\"uint256\"}],\"name\":\"deduct\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"},{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"chargerAccount\",\"type\":\"address\"}],\"name\":\"revokeCharger\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"userAccount\",\"type\":\"address\"},{\"name\":\"gasValue\",\"type\":\"uint256\"}],\"name\":\"charge\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"},{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"listChargers\",\"outputs\":[{\"name\":\"\",\"type\":\"address[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"chargerAccount\",\"type\":\"address\"}],\"name\":\"grantCharger\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"userAccount\",\"type\":\"address\"}],\"name\":\"queryRemainGas\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"},{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"}]"
    };

    public static final String ABI = String.join("", ABI_ARRAY);

    public static final String FUNC_DEDUCT = "deduct";

    public static final String FUNC_REVOKECHARGER = "revokeCharger";

    public static final String FUNC_CHARGE = "charge";

    public static final String FUNC_LISTCHARGERS = "listChargers";

    public static final String FUNC_GRANTCHARGER = "grantCharger";

    public static final String FUNC_QUERYREMAINGAS = "queryRemainGas";

    protected GasChargeManagePrecompiled(
            String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public TransactionReceipt deduct(String userAccount, BigInteger gasValue) {
        final Function function =
                new Function(
                        FUNC_DEDUCT,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Address(userAccount),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(gasValue)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void deduct(String userAccount, BigInteger gasValue, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_DEDUCT,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Address(userAccount),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(gasValue)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForDeduct(String userAccount, BigInteger gasValue) {
        final Function function =
                new Function(
                        FUNC_DEDUCT,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Address(userAccount),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(gasValue)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<String, BigInteger> getDeductInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_DEDUCT,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, BigInteger>(
                (String) results.get(0).getValue(), (BigInteger) results.get(1).getValue());
    }

    public Tuple2<BigInteger, BigInteger> getDeductOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_DEDUCT,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Int256>() {}, new TypeReference<Uint256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<BigInteger, BigInteger>(
                (BigInteger) results.get(0).getValue(), (BigInteger) results.get(1).getValue());
    }

    public TransactionReceipt revokeCharger(String chargerAccount) {
        final Function function =
                new Function(
                        FUNC_REVOKECHARGER,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Address(chargerAccount)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void revokeCharger(String chargerAccount, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_REVOKECHARGER,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Address(chargerAccount)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForRevokeCharger(String chargerAccount) {
        final Function function =
                new Function(
                        FUNC_REVOKECHARGER,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Address(chargerAccount)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<String> getRevokeChargerInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_REVOKECHARGER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getRevokeChargerOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_REVOKECHARGER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt charge(String userAccount, BigInteger gasValue) {
        final Function function =
                new Function(
                        FUNC_CHARGE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Address(userAccount),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(gasValue)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void charge(String userAccount, BigInteger gasValue, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_CHARGE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Address(userAccount),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(gasValue)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForCharge(String userAccount, BigInteger gasValue) {
        final Function function =
                new Function(
                        FUNC_CHARGE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Address(userAccount),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(gasValue)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<String, BigInteger> getChargeInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_CHARGE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, BigInteger>(
                (String) results.get(0).getValue(), (BigInteger) results.get(1).getValue());
    }

    public Tuple2<BigInteger, BigInteger> getChargeOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_CHARGE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Int256>() {}, new TypeReference<Uint256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<BigInteger, BigInteger>(
                (BigInteger) results.get(0).getValue(), (BigInteger) results.get(1).getValue());
    }

    public List listChargers() throws ContractException {
        final Function function =
                new Function(
                        FUNC_LISTCHARGERS,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<DynamicArray<Address>>() {}));
        List<Type> result = (List<Type>) executeCallWithSingleValueReturn(function, List.class);
        return convertToNative(result);
    }

    public TransactionReceipt grantCharger(String chargerAccount) {
        final Function function =
                new Function(
                        FUNC_GRANTCHARGER,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Address(chargerAccount)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void grantCharger(String chargerAccount, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_GRANTCHARGER,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Address(chargerAccount)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForGrantCharger(String chargerAccount) {
        final Function function =
                new Function(
                        FUNC_GRANTCHARGER,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Address(chargerAccount)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<String> getGrantChargerInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_GRANTCHARGER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getGrantChargerOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_GRANTCHARGER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public Tuple2<BigInteger, BigInteger> queryRemainGas(String userAccount)
            throws ContractException {
        final Function function =
                new Function(
                        FUNC_QUERYREMAINGAS,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Address(userAccount)),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Int256>() {}, new TypeReference<Uint256>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple2<BigInteger, BigInteger>(
                (BigInteger) results.get(0).getValue(), (BigInteger) results.get(1).getValue());
    }

    public static GasChargeManagePrecompiled load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new GasChargeManagePrecompiled(contractAddress, client, credential);
    }

    public static GasChargeManagePrecompiled deploy(Client client, CryptoKeyPair credential)
            throws ContractException {
        return deploy(
                GasChargeManagePrecompiled.class,
                client,
                credential,
                getBinary(client.getCryptoSuite()),
                "");
    }
}
