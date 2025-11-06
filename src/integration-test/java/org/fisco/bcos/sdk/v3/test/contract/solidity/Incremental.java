package org.fisco.bcos.sdk.v3.test.contract.solidity;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.Event;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.contract.FunctionWrapper;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.eventsub.EventSubCallback;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.ProxySignTransactionManager;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.TransactionManager;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class Incremental extends Contract {
    public static final String[] BINARY_ARRAY = {
        "60806040526000805534801561001457600080fd5b50610235806100246000396000f3fe608060405234801561001057600080fd5b50600436106100365760003560e01c80633fa4f2451461003b578063bdcbbf5414610056575b600080fd5b61004460005481565b60405190815260200160405180910390f35b6100446100643660046100d3565b600060016000808282546100789190610184565b90915550506040517f7ed53cbadbbc7d8800605823ac88c67b7d9c9fa9d54d45a68d221760e0a86552906100ad9084906101aa565b60405180910390a1505060005490565b634e487b7160e01b600052604160045260246000fd5b6000602082840312156100e557600080fd5b813567ffffffffffffffff808211156100fd57600080fd5b818401915084601f83011261011157600080fd5b813581811115610123576101236100bd565b604051601f8201601f19908116603f0116810190838211818310171561014b5761014b6100bd565b8160405282815287602084870101111561016457600080fd5b826020860160208301376000928101602001929092525095945050505050565b600082198211156101a557634e487b7160e01b600052601160045260246000fd5b500190565b600060208083528351808285015260005b818110156101d7578581018301518582016040015282016101bb565b818111156101e9576000604083870101525b50601f01601f191692909201604001939250505056fea26469706673582212204f4929b54be1544a8e77f16102dc301071ee72f711fad9923c31e0e272bc48d164736f6c634300080b0033"
    };

    public static final String BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {
        "60806040526000805534801561001457600080fd5b50610235806100246000396000f3fe608060405234801561001057600080fd5b50600436106100365760003560e01c80632d92c59d1461003b57806386ff705814610056575b600080fd5b61004460005481565b60405190815260200160405180910390f35b6100446100643660046100d3565b600060016000808282546100789190610184565b90915550506040517f7d280234d3c48f94f1914a272412c3e0030dd7a5841f7933ab1e45658bfaabbe906100ad9084906101aa565b60405180910390a1505060005490565b63b95aa35560e01b600052604160045260246000fd5b6000602082840312156100e557600080fd5b813567ffffffffffffffff808211156100fd57600080fd5b818401915084601f83011261011157600080fd5b813581811115610123576101236100bd565b604051601f8201601f19908116603f0116810190838211818310171561014b5761014b6100bd565b8160405282815287602084870101111561016457600080fd5b826020860160208301376000928101602001929092525095945050505050565b600082198211156101a55763b95aa35560e01b600052601160045260246000fd5b500190565b600060208083528351808285015260005b818110156101d7578581018301518582016040015282016101bb565b818111156101e9576000604083870101525b50601f01601f191692909201604001939250505056fea26469706673582212203b3bc5791505a1493d2d8c6acd87f26438a740b84bb247353830397fa78e254264736f6c634300080b0033"
    };

    public static final String SM_BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"string\",\"name\":\"msg\",\"type\":\"string\"}],\"name\":\"incEvent\",\"type\":\"event\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"msg\",\"type\":\"string\"}],\"name\":\"inc\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"value\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"}]"
    };

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_INC = "inc";

    public static final String FUNC_VALUE = "value";

    public static final Event INCEVENT_EVENT =
            new Event(
                    "incEvent",
                    Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));;

    protected Incremental(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
        this.transactionManager = new ProxySignTransactionManager(client);
    }

    protected Incremental(
            String contractAddress, Client client, TransactionManager transactionManager) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, transactionManager);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public List<IncEventEventResponse> getIncEventEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList =
                extractEventParametersWithLog(INCEVENT_EVENT, transactionReceipt);
        ArrayList<IncEventEventResponse> responses =
                new ArrayList<IncEventEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            IncEventEventResponse typedResponse = new IncEventEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.msg = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeIncEventEvent(
            BigInteger fromBlock,
            BigInteger toBlock,
            List<String> otherTopics,
            EventSubCallback callback) {
        String topic0 = eventEncoder.encode(INCEVENT_EVENT);
        subscribeEvent(topic0, otherTopics, fromBlock, toBlock, callback);
    }

    public void subscribeIncEventEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(INCEVENT_EVENT);
        subscribeEvent(topic0, callback);
    }

    public TransactionReceipt inc(String msg) {
        final Function function =
                new Function(
                        FUNC_INC,
                        Arrays.<Type>asList(new Utf8String(msg)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public Function getMethodIncRawFunction(String msg) throws ContractException {
        final Function function =
                new Function(
                        FUNC_INC,
                        Arrays.<Type>asList(new Utf8String(msg)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return function;
    }

    public FunctionWrapper buildMethodInc(String msg) throws ContractException {
        final Function function =
                new Function(
                        FUNC_INC,
                        Arrays.<Type>asList(new Utf8String(msg)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return new FunctionWrapper(this, function);
    }

    public String getSignedTransactionForInc(String msg) {
        final Function function =
                new Function(
                        FUNC_INC,
                        Arrays.<Type>asList(new Utf8String(msg)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
    }

    public String inc(String msg, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_INC,
                        Arrays.<Type>asList(new Utf8String(msg)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple1<String> getIncInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_INC,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getIncOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_INC,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public BigInteger value() throws ContractException {
        final Function function =
                new Function(
                        FUNC_VALUE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Function getMethodValueRawFunction() throws ContractException {
        final Function function =
                new Function(
                        FUNC_VALUE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return function;
    }

    public static Incremental load(
            String contractAddress, Client client, TransactionManager transactionManager) {
        return new Incremental(contractAddress, client, transactionManager);
    }

    public static Incremental load(String contractAddress, Client client) {
        return new Incremental(contractAddress, client, new ProxySignTransactionManager(client));
    }

    public static Incremental deploy(Client client, CryptoKeyPair credential)
            throws ContractException {
        Incremental contract =
                deploy(
                        Incremental.class,
                        client,
                        credential,
                        getBinary(client.getCryptoSuite()),
                        getABI(),
                        null,
                        null);
        contract.setTransactionManager(new ProxySignTransactionManager(client));
        return contract;
    }

    public static class IncEventEventResponse {
        public TransactionReceipt.Logs log;

        public String msg;
    }
}
