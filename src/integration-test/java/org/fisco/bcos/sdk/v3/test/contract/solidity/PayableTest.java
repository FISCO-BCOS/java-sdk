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
public class PayableTest extends Contract {
    public static final String[] BINARY_ARRAY = {"608060405260008055610260806100176000396000f3fe6080604052600436106100345760003560e01c80633fa4f245146100395780634a0ac20f14610061578063bdcbbf5414610074575b600080fd5b34801561004557600080fd5b5061004f60005481565b60405190815260200160405180910390f35b61004f61006f3660046100fe565b61008f565b34801561008057600080fd5b5061004f61006f3660046100fe565b600060016000808282546100a391906101af565b90915550506040517f7ed53cbadbbc7d8800605823ac88c67b7d9c9fa9d54d45a68d221760e0a86552906100d89084906101d5565b60405180910390a1505060005490565b634e487b7160e01b600052604160045260246000fd5b60006020828403121561011057600080fd5b813567ffffffffffffffff8082111561012857600080fd5b818401915084601f83011261013c57600080fd5b81358181111561014e5761014e6100e8565b604051601f8201601f19908116603f01168101908382118183101715610176576101766100e8565b8160405282815287602084870101111561018f57600080fd5b826020860160208301376000928101602001929092525095945050505050565b600082198211156101d057634e487b7160e01b600052601160045260246000fd5b500190565b600060208083528351808285015260005b81811015610202578581018301518582016040015282016101e6565b81811115610214576000604083870101525b50601f01601f191692909201604001939250505056fea2646970667358221220fa8eb1a6900123689f8fc8f453a833ef65799ff1621c365eb755b27affb2a22864736f6c634300080b0033"};

    public static final String BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {"608060405260008055610260806100176000396000f3fe6080604052600436106100345760003560e01c80632d92c59d1461003957806386ff705814610061578063d14dc71014610081575b600080fd5b34801561004557600080fd5b5061004f60005481565b60405190815260200160405180910390f35b34801561006d57600080fd5b5061004f61007c3660046100fe565b61008f565b61004f61007c3660046100fe565b600060016000808282546100a391906101af565b90915550506040517f7d280234d3c48f94f1914a272412c3e0030dd7a5841f7933ab1e45658bfaabbe906100d89084906101d5565b60405180910390a1505060005490565b63b95aa35560e01b600052604160045260246000fd5b60006020828403121561011057600080fd5b813567ffffffffffffffff8082111561012857600080fd5b818401915084601f83011261013c57600080fd5b81358181111561014e5761014e6100e8565b604051601f8201601f19908116603f01168101908382118183101715610176576101766100e8565b8160405282815287602084870101111561018f57600080fd5b826020860160208301376000928101602001929092525095945050505050565b600082198211156101d05763b95aa35560e01b600052601160045260246000fd5b500190565b600060208083528351808285015260005b81811015610202578581018301518582016040015282016101e6565b81811115610214576000604083870101525b50601f01601f191692909201604001939250505056fea2646970667358221220e323cce0a161c2d557143e940bdbe0b3d5f519b9f82685f275c6b0d2b1786d9e64736f6c634300080b0033"};

    public static final String SM_BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"inputs\":[],\"stateMutability\":\"payable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"string\",\"name\":\"msg\",\"type\":\"string\"}],\"name\":\"incEvent\",\"type\":\"event\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"mesg\",\"type\":\"string\"}],\"name\":\"inc\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"mesg\",\"type\":\"string\"}],\"name\":\"incWithPayable\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"payable\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"value\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"}]"};

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_INC = "inc";

    public static final String FUNC_INCWITHPAYABLE = "incWithPayable";

    public static final String FUNC_VALUE = "value";

    public static final Event INCEVENT_EVENT = new Event("incEvent", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
    ;

    protected PayableTest(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
        this.transactionManager = new ProxySignTransactionManager(client);
    }

    protected PayableTest(String contractAddress, Client client,
            TransactionManager transactionManager) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, transactionManager);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public List<IncEventEventResponse> getIncEventEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(INCEVENT_EVENT, transactionReceipt);
        ArrayList<IncEventEventResponse> responses = new ArrayList<IncEventEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            IncEventEventResponse typedResponse = new IncEventEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.msg = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeIncEventEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(INCEVENT_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribeIncEventEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(INCEVENT_EVENT);
        subscribeEvent(topic0,callback);
    }

    public TransactionReceipt inc(String mesg) {
        final Function function = new Function(
                FUNC_INC, 
                Arrays.<Type>asList(new Utf8String(mesg)),
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public Function getMethodIncRawFunction(String mesg) throws ContractException {
        final Function function = new Function(FUNC_INC, 
                Arrays.<Type>asList(new Utf8String(mesg)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return function;
    }

    public String getSignedTransactionForInc(String mesg) {
        final Function function = new Function(
                FUNC_INC, 
                Arrays.<Type>asList(new Utf8String(mesg)),
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String inc(String mesg, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_INC, 
                Arrays.<Type>asList(new Utf8String(mesg)),
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple1<String> getIncInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_INC, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>(

                (String) results.get(0).getValue()
                );
    }

    public Tuple1<BigInteger> getIncOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function = new Function(FUNC_INC, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>(

                (BigInteger) results.get(0).getValue()
                );
    }

    public TransactionReceipt incWithPayable(String mesg, BigInteger value) {
        final Function function = new Function(
                FUNC_INCWITHPAYABLE, 
                Arrays.<Type>asList(new Utf8String(mesg)),
                Collections.<TypeReference<?>>emptyList(), 0, value);
        return executeTransaction(function);
    }

    public Function getMethodIncWithPayableRawFunction(String mesg) throws ContractException {
        final Function function = new Function(FUNC_INCWITHPAYABLE, 
                Arrays.<Type>asList(new Utf8String(mesg)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return function;
    }

    public String getSignedTransactionForIncWithPayable(String mesg, BigInteger value) {
        final Function function = new Function(
                FUNC_INCWITHPAYABLE, 
                Arrays.<Type>asList(new Utf8String(mesg)),
                Collections.<TypeReference<?>>emptyList(), 0, value);
        return createSignedTransaction(function);
    }

    public String incWithPayable(String mesg, BigInteger value, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_INCWITHPAYABLE, 
                Arrays.<Type>asList(new Utf8String(mesg)),
                Collections.<TypeReference<?>>emptyList(), 0, value);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple1<String> getIncWithPayableInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_INCWITHPAYABLE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>(

                (String) results.get(0).getValue()
                );
    }

    public Tuple1<BigInteger> getIncWithPayableOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function = new Function(FUNC_INCWITHPAYABLE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>(

                (BigInteger) results.get(0).getValue()
                );
    }

    public BigInteger value() throws ContractException {
        final Function function = new Function(FUNC_VALUE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Function getMethodValueRawFunction() throws ContractException {
        final Function function = new Function(FUNC_VALUE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return function;
    }

    public static PayableTest load(String contractAddress, Client client,
            TransactionManager transactionManager) {
        return new PayableTest(contractAddress, client, transactionManager);
    }

    public static PayableTest load(String contractAddress, Client client) {
        return new PayableTest(contractAddress, client, new ProxySignTransactionManager(client));
    }

    public static PayableTest deploy(Client client, CryptoKeyPair credential, BigInteger value)
            throws ContractException {
        PayableTest contract = deploy(PayableTest.class, client, credential, getBinary(client.getCryptoSuite()), getABI(), null, null, value);
        contract.setTransactionManager(new ProxySignTransactionManager(client));
        return contract;
    }

    public static class IncEventEventResponse {
        public TransactionReceipt.Logs log;

        public String msg;
    }
}
