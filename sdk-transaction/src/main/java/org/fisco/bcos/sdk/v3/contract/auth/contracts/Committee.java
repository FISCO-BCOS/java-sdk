package org.fisco.bcos.sdk.v3.contract.auth.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.Utils;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint32;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple4;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.utils.StringUtils;

@SuppressWarnings("unchecked")
public class Committee extends Contract {
    public static final String[] ABI_ARRAY = {
        "[{\"inputs\":[{\"internalType\":\"address[]\",\"name\":\"governorList\",\"type\":\"address[]\"},{\"internalType\":\"uint32[]\",\"name\":\"weightList\",\"type\":\"uint32[]\"},{\"internalType\":\"uint8\",\"name\":\"participatesRate\",\"type\":\"uint8\"},{\"internalType\":\"uint8\",\"name\":\"winRate\",\"type\":\"uint8\"}],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"conflictFields\":[{\"kind\":4,\"value\":[0]}],\"inputs\":[],\"name\":\"_owner\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"selector\":[2998794875,686363785],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[4]}],\"inputs\":[],\"name\":\"_participatesRate\",\"outputs\":[{\"internalType\":\"uint8\",\"name\":\"\",\"type\":\"uint8\"}],\"selector\":[1444243823,243764944],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[4]}],\"inputs\":[],\"name\":\"_winRate\",\"outputs\":[{\"internalType\":\"uint8\",\"name\":\"\",\"type\":\"uint8\"}],\"selector\":[3070070887,133450137],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[0]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"src\",\"type\":\"address\"}],\"name\":\"auth\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[3445432600,1845720788],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0},{\"kind\":4,\"value\":[2]},{\"kind\":4,\"value\":[4]}],\"inputs\":[],\"name\":\"getCommitteeInfo\",\"outputs\":[{\"internalType\":\"uint8\",\"name\":\"participatesRate\",\"type\":\"uint8\"},{\"internalType\":\"uint8\",\"name\":\"winRate\",\"type\":\"uint8\"},{\"internalType\":\"address[]\",\"name\":\"governors\",\"type\":\"address[]\"},{\"internalType\":\"uint32[]\",\"name\":\"weights\",\"type\":\"uint32[]\"}],\"selector\":[1584922144,1955758279],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"governor\",\"type\":\"address\"}],\"name\":\"getWeight\",\"outputs\":[{\"internalType\":\"uint32\",\"name\":\"\",\"type\":\"uint32\"}],\"selector\":[2892780113,3346437604],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0},{\"kind\":4,\"value\":[2]}],\"inputs\":[],\"name\":\"getWeights\",\"outputs\":[{\"internalType\":\"uint32\",\"name\":\"\",\"type\":\"uint32\"}],\"selector\":[581744743,937400674],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0}],\"inputs\":[{\"internalType\":\"address[]\",\"name\":\"votes\",\"type\":\"address[]\"}],\"name\":\"getWeights\",\"outputs\":[{\"internalType\":\"uint32\",\"name\":\"\",\"type\":\"uint32\"}],\"selector\":[2522587121,599633121],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"governor\",\"type\":\"address\"}],\"name\":\"isGovernor\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[3828711864,3052548661],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[0]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"owner\",\"type\":\"address\"}],\"name\":\"setOwner\",\"outputs\":[],\"selector\":[330252341,86518896],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[4]}],\"inputs\":[{\"internalType\":\"uint8\",\"name\":\"participatesRate\",\"type\":\"uint8\"},{\"internalType\":\"uint8\",\"name\":\"winRate\",\"type\":\"uint8\"}],\"name\":\"setRate\",\"outputs\":[],\"selector\":[2579274779,1490899929],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0},{\"kind\":4,\"value\":[2]}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"governor\",\"type\":\"address\"},{\"internalType\":\"uint32\",\"name\":\"weight\",\"type\":\"uint32\"}],\"name\":\"setWeight\",\"outputs\":[],\"selector\":[4097272154,2422690589],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC__OWNER = "_owner";

    public static final String FUNC__PARTICIPATESRATE = "_participatesRate";

    public static final String FUNC__WINRATE = "_winRate";

    public static final String FUNC_AUTH = "auth";

    public static final String FUNC_GETCOMMITTEEINFO = "getCommitteeInfo";

    public static final String FUNC_GETWEIGHT = "getWeight";

    public static final String FUNC_GETWEIGHTS = "getWeights";

    public static final String FUNC_ISGOVERNOR = "isGovernor";

    public static final String FUNC_SETOWNER = "setOwner";

    public static final String FUNC_SETRATE = "setRate";

    public static final String FUNC_SETWEIGHT = "setWeight";

    protected Committee(String contractAddress, Client client, CryptoKeyPair credential) {
        super("", contractAddress, client, credential);
    }

    public static String getABI() {
        return ABI;
    }

    public String _owner() throws ContractException {
        final Function function =
                new Function(
                        FUNC__OWNER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public BigInteger _participatesRate() throws ContractException {
        final Function function =
                new Function(
                        FUNC__PARTICIPATESRATE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public BigInteger _winRate() throws ContractException {
        final Function function =
                new Function(
                        FUNC__WINRATE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Boolean auth(String src) throws ContractException {
        final Function function =
                new Function(
                        FUNC_AUTH,
                        Arrays.<Type>asList(new Address(src)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Tuple4<BigInteger, BigInteger, List<String>, List<BigInteger>> getCommitteeInfo()
            throws ContractException {
        final Function function =
                new Function(
                        FUNC_GETCOMMITTEEINFO,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Uint8>() {},
                                new TypeReference<Uint8>() {},
                                new TypeReference<DynamicArray<Address>>() {},
                                new TypeReference<DynamicArray<Uint32>>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple4<BigInteger, BigInteger, List<String>, List<BigInteger>>(
                (BigInteger) results.get(0).getValue(),
                (BigInteger) results.get(1).getValue(),
                convertToNative((List<Address>) results.get(2).getValue()),
                convertToNative((List<Uint32>) results.get(3).getValue()));
    }

    public BigInteger getWeight(String governor) throws ContractException {
        final Function function =
                new Function(
                        FUNC_GETWEIGHT,
                        Arrays.<Type>asList(new Address(governor)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint32>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public BigInteger getWeights() throws ContractException {
        final Function function =
                new Function(
                        FUNC_GETWEIGHTS,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint32>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public BigInteger getWeights(List<String> votes) throws ContractException {
        final Function function =
                new Function(
                        FUNC_GETWEIGHTS,
                        Arrays.<Type>asList(
                                new DynamicArray<Address>(
                                        Address.class, Utils.typeMap(votes, Address.class))),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint32>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Boolean isGovernor(String governor) throws ContractException {
        final Function function =
                new Function(
                        FUNC_ISGOVERNOR,
                        Arrays.<Type>asList(new Address(governor)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public TransactionReceipt setOwner(String owner) {
        final Function function =
                new Function(
                        FUNC_SETOWNER,
                        Arrays.<Type>asList(new Address(owner)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String setOwner(String owner, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_SETOWNER,
                        Arrays.<Type>asList(new Address(owner)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForSetOwner(String owner) {
        final Function function =
                new Function(
                        FUNC_SETOWNER,
                        Arrays.<Type>asList(new Address(owner)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
    }

    public Tuple1<String> getSetOwnerInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_SETOWNER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public TransactionReceipt setRate(BigInteger participatesRate, BigInteger winRate) {
        final Function function =
                new Function(
                        FUNC_SETRATE,
                        Arrays.<Type>asList(new Uint8(participatesRate), new Uint8(winRate)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String setRate(
            BigInteger participatesRate, BigInteger winRate, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_SETRATE,
                        Arrays.<Type>asList(new Uint8(participatesRate), new Uint8(winRate)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForSetRate(BigInteger participatesRate, BigInteger winRate) {
        final Function function =
                new Function(
                        FUNC_SETRATE,
                        Arrays.<Type>asList(new Uint8(participatesRate), new Uint8(winRate)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<BigInteger, BigInteger> getSetRateInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_SETRATE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Uint8>() {}, new TypeReference<Uint8>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<BigInteger, BigInteger>(
                (BigInteger) results.get(0).getValue(), (BigInteger) results.get(1).getValue());
    }

    public TransactionReceipt setWeight(String governor, BigInteger weight) {
        final Function function =
                new Function(
                        FUNC_SETWEIGHT,
                        Arrays.<Type>asList(new Address(governor), new Uint32(weight)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String setWeight(String governor, BigInteger weight, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_SETWEIGHT,
                        Arrays.<Type>asList(new Address(governor), new Uint32(weight)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForSetWeight(String governor, BigInteger weight) {
        final Function function =
                new Function(
                        FUNC_SETWEIGHT,
                        Arrays.<Type>asList(new Address(governor), new Uint32(weight)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<String, BigInteger> getSetWeightInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_SETWEIGHT,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {}, new TypeReference<Uint32>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, BigInteger>(
                (String) results.get(0).getValue(), (BigInteger) results.get(1).getValue());
    }

    public static Committee load(String contractAddress, Client client, CryptoKeyPair credential) {
        return new Committee(contractAddress, client, credential);
    }
}
