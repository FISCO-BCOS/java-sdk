package org.fisco.bcos.sdk.v3.test.contract.solidity;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.Event;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.eventsub.EventSubCallback;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.ProxySignTransactionManager;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.TransactionManager;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class HelloWorldPayable extends Contract {
    public static final String[] BINARY_ARRAY = {
        "608060405234801561001057600080fd5b5060408051808201909152600d8082526c48656c6c6f2c20576f726c642160981b60209092019182526100459160009161004b565b5061011f565b828054610057906100e4565b90600052602060002090601f01602090048101928261007957600085556100bf565b82601f1061009257805160ff19168380011785556100bf565b828001600101855582156100bf579182015b828111156100bf5782518255916020019190600101906100a4565b506100cb9291506100cf565b5090565b5b808211156100cb57600081556001016100d0565b600181811c908216806100f857607f821691505b6020821081141561011957634e487b7160e01b600052602260045260246000fd5b50919050565b61033a8061012e6000396000f3fe6080604052600436106100295760003560e01c80634ed3885e1461002e5780636d4ce63c14610057575b600080fd5b61004161003c3660046101c9565b61006c565b60405161004e919061027a565b60405180910390f35b34801561006357600080fd5b50610041610088565b805160609061008290600090602085019061011a565b50919050565b606060008054610097906102cf565b80601f01602080910402602001604051908101604052809291908181526020018280546100c3906102cf565b80156101105780601f106100e557610100808354040283529160200191610110565b820191906000526020600020905b8154815290600101906020018083116100f357829003601f168201915b5050505050905090565b828054610126906102cf565b90600052602060002090601f016020900481019282610148576000855561018e565b82601f1061016157805160ff191683800117855561018e565b8280016001018555821561018e579182015b8281111561018e578251825591602001919060010190610173565b5061019a92915061019e565b5090565b5b8082111561019a576000815560010161019f565b634e487b7160e01b600052604160045260246000fd5b6000602082840312156101db57600080fd5b813567ffffffffffffffff808211156101f357600080fd5b818401915084601f83011261020757600080fd5b813581811115610219576102196101b3565b604051601f8201601f19908116603f01168101908382118183101715610241576102416101b3565b8160405282815287602084870101111561025a57600080fd5b826020860160208301376000928101602001929092525095945050505050565b600060208083528351808285015260005b818110156102a75785810183015185820160400152820161028b565b818111156102b9576000604083870101525b50601f01601f1916929092016040019392505050565b600181811c908216806102e357607f821691505b6020821081141561008257634e487b7160e01b600052602260045260246000fdfea26469706673582212204e290b021c36a93b084c22da8430091a363fd52dbb28dce015c1d2b4eafd2c5164736f6c634300080b0033"
    };

    public static final String BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {
        "608060405234801561001057600080fd5b5060408051808201909152600d8082526c48656c6c6f2c20576f726c642160981b60209092019182526100459160009161004b565b5061011f565b828054610057906100e4565b90600052602060002090601f01602090048101928261007957600085556100bf565b82601f1061009257805160ff19168380011785556100bf565b828001600101855582156100bf579182015b828111156100bf5782518255916020019190600101906100a4565b506100cb9291506100cf565b5090565b5b808211156100cb57600081556001016100d0565b600181811c908216806100f857607f821691505b602082108114156101195763b95aa35560e01b600052602260045260246000fd5b50919050565b61033a8061012e6000396000f3fe6080604052600436106100295760003560e01c8063299f7f9d1461002e5780633590b49f14610059575b600080fd5b34801561003a57600080fd5b5061004361006c565b60405161005091906101b3565b60405180910390f35b61004361006736600461021e565b6100fe565b60606000805461007b906102cf565b80601f01602080910402602001604051908101604052809291908181526020018280546100a7906102cf565b80156100f45780601f106100c9576101008083540402835291602001916100f4565b820191906000526020600020905b8154815290600101906020018083116100d757829003601f168201915b5050505050905090565b805160609061011490600090602085019061011a565b50919050565b828054610126906102cf565b90600052602060002090601f016020900481019282610148576000855561018e565b82601f1061016157805160ff191683800117855561018e565b8280016001018555821561018e579182015b8281111561018e578251825591602001919060010190610173565b5061019a92915061019e565b5090565b5b8082111561019a576000815560010161019f565b600060208083528351808285015260005b818110156101e0578581018301518582016040015282016101c4565b818111156101f2576000604083870101525b50601f01601f1916929092016040019392505050565b63b95aa35560e01b600052604160045260246000fd5b60006020828403121561023057600080fd5b813567ffffffffffffffff8082111561024857600080fd5b818401915084601f83011261025c57600080fd5b81358181111561026e5761026e610208565b604051601f8201601f19908116603f0116810190838211818310171561029657610296610208565b816040528281528760208487010111156102af57600080fd5b826020860160208301376000928101602001929092525095945050505050565b600181811c908216806102e357607f821691505b602082108114156101145763b95aa35560e01b600052602260045260246000fdfea264697066735822122034d8e1ccdfe70284c826efb1bdd0c87f1c7183f04756946c181ae76e57827b6c64736f6c634300080b0033"
    };

    public static final String SM_BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"inputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"string[]\",\"name\":\"\",\"type\":\"string[]\"}],\"name\":\"log\",\"type\":\"event\"},{\"inputs\":[],\"name\":\"get\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"n\",\"type\":\"string\"}],\"name\":\"set\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"payable\",\"type\":\"function\"}]"
    };

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_GET = "get";

    public static final String FUNC_SET = "set";

    public static final Event LOG_EVENT =
            new Event(
                    "log",
                    Arrays.<TypeReference<?>>asList(
                            new TypeReference<DynamicArray<Utf8String>>() {}));

    protected HelloWorldPayable(
            String contractAddress, Client client, TransactionManager transactionManager) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, transactionManager);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public List<LogEventResponse> getLogEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList =
                extractEventParametersWithLog(LOG_EVENT, transactionReceipt);
        ArrayList<LogEventResponse> responses = new ArrayList<LogEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogEventResponse typedResponse = new LogEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.logParam0 =
                    (List<String>) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeLogEvent(
            BigInteger fromBlock,
            BigInteger toBlock,
            List<String> otherTopics,
            EventSubCallback callback) {
        String topic0 = eventEncoder.encode(LOG_EVENT);
        subscribeEvent(topic0, otherTopics, fromBlock, toBlock, callback);
    }

    public void subscribeLogEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(LOG_EVENT);
        subscribeEvent(topic0, callback);
    }

    public String get() throws ContractException {
        final Function function =
                new Function(
                        FUNC_GET,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public Function getMethodGetRawFunction() throws ContractException {
        final Function function =
                new Function(
                        FUNC_GET,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return function;
    }

    public TransactionReceipt set(String n) {
        final Function function =
                new Function(
                        FUNC_SET,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(n)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public Function getMethodSetRawFunction(String n) throws ContractException {
        final Function function =
                new Function(
                        FUNC_SET,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(n)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return function;
    }

    public String getSignedTransactionForSet(String n) {
        final Function function =
                new Function(
                        FUNC_SET,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(n)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
    }

    public String set(String n, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_SET,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(n)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple1<String> getSetInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_SET,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public Tuple1<String> getSetOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_SET,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public static HelloWorldPayable load(
            String contractAddress, Client client, TransactionManager transactionManager) {
        return new HelloWorldPayable(contractAddress, client, transactionManager);
    }

    public static HelloWorldPayable load(String contractAddress, Client client) {
        return new HelloWorldPayable(
                contractAddress, client, new ProxySignTransactionManager(client));
    }

    public static HelloWorldPayable deploy(Client client, CryptoKeyPair credential)
            throws ContractException {
        HelloWorldPayable contract =
                deploy(
                        HelloWorldPayable.class,
                        client,
                        credential,
                        getBinary(client.getCryptoSuite()),
                        getABI(),
                        null,
                        null);
        contract.setTransactionManager(new ProxySignTransactionManager(client));
        return contract;
    }

    public static class LogEventResponse {
        public TransactionReceipt.Logs log;

        public List<String> logParam0;
    }
}
