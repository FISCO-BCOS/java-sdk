package org.fisco.bcos.sdk.demo.perf.contract;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.Event;
import org.fisco.bcos.sdk.abi.datatypes.Function;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.Contract;
import org.fisco.bcos.sdk.contract.exceptions.ContractException;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.eventsub.EventCallback;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.callback.TransactionCallback;

@SuppressWarnings("unchecked")
public class Ok extends Contract {
    public static final String[] BINARY_ARRAY = {
        "608060405234801561001057600080fd5b5060016000800160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506402540be40060006001018190555060028060000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060006002600101819055506103bf806100c26000396000f30060806040526004361061004c576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806366c99139146100515780636d4ce63c1461007e575b600080fd5b34801561005d57600080fd5b5061007c600480360381019080803590602001909291905050506100a9565b005b34801561008a57600080fd5b506100936102e1565b6040518082815260200191505060405180910390f35b8060006001015410806100c757506002600101548160026001015401105b156100d1576102de565b8060006001015403600060010181905550806002600101600082825401925050819055507fc77b710b83d1dc3f3fafeccd08a6c469beb873b2f0975b50d1698e46b3ee5b4c816040518082815260200191505060405180910390a160046080604051908101604052806040805190810160405280600881526020017f323031373034313300000000000000000000000000000000000000000000000081525081526020016000800160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001600260000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001838152509080600181540180825580915050906001820390600052602060002090600402016000909192909190915060008201518160000190805190602001906102419291906102ee565b5060208201518160010160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060408201518160020160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550606082015181600301555050505b50565b6000600260010154905090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061032f57805160ff191683800117855561035d565b8280016001018555821561035d579182015b8281111561035c578251825591602001919060010190610341565b5b50905061036a919061036e565b5090565b61039091905b8082111561038c576000816000905550600101610374565b5090565b905600a165627a7a72305820c39f433fed93fc308e8f2f3b629e9fbabc41d5490ca4858176bd0c2ac2e9a5030029"
    };

    public static final String BINARY = String.join("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {
        "608060405234801561001057600080fd5b5060016000800160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506402540be40060006001018190555060028060000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060006002600101819055506103bf806100c26000396000f30060806040526004361061004c576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff168063299f7f9d146100515780638fff0fc41461007c575b600080fd5b34801561005d57600080fd5b506100666100a9565b6040518082815260200191505060405180910390f35b34801561008857600080fd5b506100a7600480360381019080803590602001909291905050506100b6565b005b6000600260010154905090565b8060006001015410806100d457506002600101548160026001015401105b156100de576102eb565b8060006001015403600060010181905550806002600101600082825401925050819055507fc345610041c3c141ff9e0fbc73b34bf13842fd02e0c3cfe6541eedd6adef4b2c816040518082815260200191505060405180910390a160046080604051908101604052806040805190810160405280600881526020017f323031373034313300000000000000000000000000000000000000000000000081525081526020016000800160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001600260000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018381525090806001815401808255809150509060018203906000526020600020906004020160009091929091909150600082015181600001908051906020019061024e9291906102ee565b5060208201518160010160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060408201518160020160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550606082015181600301555050505b50565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061032f57805160ff191683800117855561035d565b8280016001018555821561035d579182015b8281111561035c578251825591602001919060010190610341565b5b50905061036a919061036e565b5090565b61039091905b8082111561038c576000816000905550600101610374565b5090565b905600a165627a7a723058200567f5298660ed3a5d54fe0a632a025fa2bb68f50fce74a6337855177c36f50e0029"
    };

    public static final String SM_BINARY = String.join("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"constant\":false,\"inputs\":[{\"name\":\"num\",\"type\":\"uint256\"}],\"name\":\"trans\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"get\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"num\",\"type\":\"uint256\"}],\"name\":\"TransEvent\",\"type\":\"event\"}]"
    };

    public static final String ABI = String.join("", ABI_ARRAY);

    public static final String FUNC_TRANS = "trans";

    public static final String FUNC_GET = "get";

    public static final Event TRANSEVENT_EVENT =
            new Event(
                    "TransEvent",
                    Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));;

    protected Ok(String contractAddress, Client client, CryptoInterface credential) {
        super(getBinary(credential), contractAddress, client, credential);
    }

    public static String getBinary(CryptoInterface credential) {
        return (credential.getCryptoTypeConfig() == CryptoInterface.ECDSA_TYPE
                ? BINARY
                : SM_BINARY);
    }

    public TransactionReceipt trans(BigInteger num) {
        final Function function =
                new Function(
                        FUNC_TRANS,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(num)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void trans(BigInteger num, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_TRANS,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(num)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForTrans(BigInteger num) {
        final Function function =
                new Function(
                        FUNC_TRANS,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(num)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<BigInteger> getTransInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_TRANS,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public BigInteger get() throws ContractException {
        final Function function =
                new Function(
                        FUNC_GET,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public List<TransEventEventResponse> getTransEventEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList =
                extractEventParametersWithLog(TRANSEVENT_EVENT, transactionReceipt);
        ArrayList<TransEventEventResponse> responses =
                new ArrayList<TransEventEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransEventEventResponse typedResponse = new TransEventEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.num = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeTransEventEvent(
            String fromBlock, String toBlock, List<String> otherTopics, EventCallback callback) {
        String topic0 = eventEncoder.encode(TRANSEVENT_EVENT);
        subscribeEvent(ABI, BINARY, topic0, fromBlock, toBlock, otherTopics, callback);
    }

    public void subscribeTransEventEvent(EventCallback callback) {
        String topic0 = eventEncoder.encode(TRANSEVENT_EVENT);
        subscribeEvent(ABI, BINARY, topic0, callback);
    }

    public static Ok load(String contractAddress, Client client, CryptoInterface credential) {
        return new Ok(contractAddress, client, credential);
    }

    public static Ok deploy(Client client, CryptoInterface credential) throws ContractException {
        return deploy(Ok.class, client, credential, getBinary(credential), "");
    }

    public static class TransEventEventResponse {
        public TransactionReceipt.Logs log;

        public BigInteger num;
    }
}
