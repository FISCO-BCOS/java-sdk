package org.fisco.bcos.sdk.v3.contract.auth.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int32;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class AccountManager extends Contract {

    public static final String[] ABI_ARRAY = {
        "[{\"inputs\":[{\"internalType\":\"address\",\"name\":\"addr\",\"type\":\"address\"}],\"name\":\"getAccountStatus\",\"outputs\":[{\"internalType\":\"enum AccountStatus\",\"name\":\"\",\"type\":\"uint8\"}],\"selector\":[4249854042,2753454540],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"addr\",\"type\":\"address\"},{\"internalType\":\"enum AccountStatus\",\"name\":\"status\",\"type\":\"uint8\"}],\"name\":\"setAccountStatus\",\"outputs\":[{\"internalType\":\"int32\",\"name\":\"\",\"type\":\"int32\"}],\"selector\":[181579937,3980545228],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_GETACCOUNTSTATUS = "getAccountStatus";

    public static final String FUNC_SETACCOUNTSTATUS = "setAccountStatus";

    protected AccountManager(Client client, CryptoKeyPair credential) {
        super("", PrecompiledAddress.ACCOUNT_MANAGER_ADDRESS, client, credential);
    }

    public static String getABI() {
        return ABI;
    }

    public BigInteger getAccountStatus(String addr) throws ContractException {
        final Function function =
                new Function(
                        FUNC_GETACCOUNTSTATUS,
                        Arrays.<Type>asList(new Address(addr)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public TransactionReceipt setAccountStatus(String addr, BigInteger status) {
        final Function function =
                new Function(
                        FUNC_SETACCOUNTSTATUS,
                        Arrays.<Type>asList(new Address(addr), new Uint8(status)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String setAccountStatus(String addr, BigInteger status, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_SETACCOUNTSTATUS,
                        Arrays.<Type>asList(new Address(addr), new Uint8(status)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForSetAccountStatus(String addr, BigInteger status) {
        final Function function =
                new Function(
                        FUNC_SETACCOUNTSTATUS,
                        Arrays.<Type>asList(new Address(addr), new Uint8(status)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
    }

    public Tuple2<String, BigInteger> getSetAccountStatusInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_SETACCOUNTSTATUS,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {}, new TypeReference<Uint8>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, BigInteger>(
                (String) results.get(0).getValue(), (BigInteger) results.get(1).getValue());
    }

    public Tuple1<BigInteger> getSetAccountStatusOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_SETACCOUNTSTATUS,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int32>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public static AccountManager load(Client client, CryptoKeyPair credential) {
        return new AccountManager(client, credential);
    }
}
