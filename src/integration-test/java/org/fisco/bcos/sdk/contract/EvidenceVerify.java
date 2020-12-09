package org.fisco.bcos.sdk.contract;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.Address;
import org.fisco.bcos.sdk.abi.datatypes.Event;
import org.fisco.bcos.sdk.abi.datatypes.Function;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.abi.datatypes.Utf8String;
import org.fisco.bcos.sdk.abi.datatypes.generated.Bytes32;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint8;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple8;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.eventsub.EventCallback;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class EvidenceVerify extends Contract {
    public static final String[] BINARY_ARRAY = {
        "608060405234801561001057600080fd5b50610d6b806100206000396000f300608060405260043610610041576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680635a53475114610046575b600080fd5b34801561005257600080fd5b5061006d60048036036100689190810190610244565b610083565b60405161007a91906103a1565b60405180910390f35b6000808989896100916101a2565b61009d93929190610401565b604051809103906000f0801580156100b9573d6000803e3d6000fd5b5090507f8b94c7f6b3fadc764673ea85b4bfef3e17ce928d13e51b818ddfa891ad0f1fcc816040516100eb91906103a1565b60405180910390a18673ffffffffffffffffffffffffffffffffffffffff1661011687878787610145565b73ffffffffffffffffffffffffffffffffffffffff1614151561013857600080fd5b5098975050505050505050565b60006001858585856040516000815260200160405260405161016a94939291906103bc565b60206040516020810390808403906000865af115801561018e573d6000803e3d6000fd5b505050602060405103519050949350505050565b6040516107bf8061057383390190565b60006101be82356104e8565b905092915050565b60006101d28235610508565b905092915050565b600082601f83011215156101ed57600080fd5b81356102006101fb8261047a565b61044d565b9150808252602083016020830185838301111561021c57600080fd5b61022783828461051f565b50505092915050565b600061023c8235610512565b905092915050565b600080600080600080600080610100898b03121561026157600080fd5b600089013567ffffffffffffffff81111561027b57600080fd5b6102878b828c016101da565b985050602089013567ffffffffffffffff8111156102a457600080fd5b6102b08b828c016101da565b975050604089013567ffffffffffffffff8111156102cd57600080fd5b6102d98b828c016101da565b96505060606102ea8b828c016101b2565b95505060806102fb8b828c016101c6565b94505060a061030c8b828c01610230565b93505060c061031d8b828c016101c6565b92505060e061032e8b828c016101c6565b9150509295985092959890939650565b610347816104b1565b82525050565b610356816104d1565b82525050565b6000610367826104a6565b80845261037b81602086016020860161052e565b61038481610561565b602085010191505092915050565b61039b816104db565b82525050565b60006020820190506103b6600083018461033e565b92915050565b60006080820190506103d1600083018761034d565b6103de6020830186610392565b6103eb604083018561034d565b6103f8606083018461034d565b95945050505050565b6000606082019050818103600083015261041b818661035c565b9050818103602083015261042f818561035c565b90508181036040830152610443818461035c565b9050949350505050565b6000604051905081810181811067ffffffffffffffff8211171561047057600080fd5b8060405250919050565b600067ffffffffffffffff82111561049157600080fd5b601f19601f8301169050602081019050919050565b600081519050919050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000819050919050565b600060ff82169050919050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000819050919050565b600060ff82169050919050565b82818337600083830152505050565b60005b8381101561054c578082015181840152602081019050610531565b8381111561055b576000848401525b50505050565b6000601f19601f83011690509190505600608060405234801561001057600080fd5b506040516107bf3803806107bf833981018060405281019080805182019291906020018051820192919060200180518201929190505050826000908051906020019061005d92919061020a565b50816001908051906020019061007492919061020a565b50806002908051906020019061008b92919061020a565b507faf1f4f8d84431b65de566a0a4f73763572c14edb25a1360312ff3c7b5386191183838360405180806020018060200180602001848103845287818151815260200191508051906020019080838360005b838110156100f85780820151818401526020810190506100dd565b50505050905090810190601f1680156101255780820380516001836020036101000a031916815260200191505b50848103835286818151815260200191508051906020019080838360005b8381101561015e578082015181840152602081019050610143565b50505050905090810190601f16801561018b5780820380516001836020036101000a031916815260200191505b50848103825285818151815260200191508051906020019080838360005b838110156101c45780820151818401526020810190506101a9565b50505050905090810190601f1680156101f15780820380516001836020036101000a031916815260200191505b50965050505050505060405180910390a15050506102af565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061024b57805160ff1916838001178555610279565b82800160010185558215610279579182015b8281111561027857825182559160200191906001019061025d565b5b509050610286919061028a565b5090565b6102ac91905b808211156102a8576000816000905550600101610290565b5090565b90565b610501806102be6000396000f30060806040526004361061004c576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff168063596f21f814610051578063c7eaf9b4146101b9575b600080fd5b34801561005d57600080fd5b50610066610249565b60405180806020018060200180602001848103845287818151815260200191508051906020019080838360005b838110156100ae578082015181840152602081019050610093565b50505050905090810190601f1680156100db5780820380516001836020036101000a031916815260200191505b50848103835286818151815260200191508051906020019080838360005b838110156101145780820151818401526020810190506100f9565b50505050905090810190601f1680156101415780820380516001836020036101000a031916815260200191505b50848103825285818151815260200191508051906020019080838360005b8381101561017a57808201518184015260208101905061015f565b50505050905090810190601f1680156101a75780820380516001836020036101000a031916815260200191505b50965050505050505060405180910390f35b3480156101c557600080fd5b506101ce610433565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561020e5780820151818401526020810190506101f3565b50505050905090810190601f16801561023b5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b6060806060600060016002828054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156102e95780601f106102be576101008083540402835291602001916102e9565b820191906000526020600020905b8154815290600101906020018083116102cc57829003601f168201915b50505050509250818054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156103855780601f1061035a57610100808354040283529160200191610385565b820191906000526020600020905b81548152906001019060200180831161036857829003601f168201915b50505050509150808054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156104215780601f106103f657610100808354040283529160200191610421565b820191906000526020600020905b81548152906001019060200180831161040457829003601f168201915b50505050509050925092509250909192565b606060018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156104cb5780601f106104a0576101008083540402835291602001916104cb565b820191906000526020600020905b8154815290600101906020018083116104ae57829003601f168201915b50505050509050905600a165627a7a723058201cf1bc8a57a38e21d1e85b85ca4be8778f698dd709336cd895434870112827360029a265627a7a72305820abb1942242b6690bc45f10f2d4c88ad4e0e9e57e2193a6164d871c7bf5f64cc06c6578706572696d656e74616cf50037"
    };

    public static final String BINARY = String.join("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {
        "608060405234801561001057600080fd5b50610d6b806100206000396000f300608060405260043610610041576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680634547fd6414610046575b600080fd5b34801561005257600080fd5b5061006d60048036036100689190810190610244565b610083565b60405161007a91906103a1565b60405180910390f35b6000808989896100916101a2565b61009d93929190610401565b604051809103906000f0801580156100b9573d6000803e3d6000fd5b5090507ffce723060091dd1452a91ae12d05541e3141b37fa27968bc557add04601f74d0816040516100eb91906103a1565b60405180910390a18673ffffffffffffffffffffffffffffffffffffffff1661011687878787610145565b73ffffffffffffffffffffffffffffffffffffffff1614151561013857600080fd5b5098975050505050505050565b60006001858585856040516000815260200160405260405161016a94939291906103bc565b60206040516020810390808403906000865af115801561018e573d6000803e3d6000fd5b505050602060405103519050949350505050565b6040516107bf8061057383390190565b60006101be82356104e8565b905092915050565b60006101d28235610508565b905092915050565b600082601f83011215156101ed57600080fd5b81356102006101fb8261047a565b61044d565b9150808252602083016020830185838301111561021c57600080fd5b61022783828461051f565b50505092915050565b600061023c8235610512565b905092915050565b600080600080600080600080610100898b03121561026157600080fd5b600089013567ffffffffffffffff81111561027b57600080fd5b6102878b828c016101da565b985050602089013567ffffffffffffffff8111156102a457600080fd5b6102b08b828c016101da565b975050604089013567ffffffffffffffff8111156102cd57600080fd5b6102d98b828c016101da565b96505060606102ea8b828c016101b2565b95505060806102fb8b828c016101c6565b94505060a061030c8b828c01610230565b93505060c061031d8b828c016101c6565b92505060e061032e8b828c016101c6565b9150509295985092959890939650565b610347816104b1565b82525050565b610356816104d1565b82525050565b6000610367826104a6565b80845261037b81602086016020860161052e565b61038481610561565b602085010191505092915050565b61039b816104db565b82525050565b60006020820190506103b6600083018461033e565b92915050565b60006080820190506103d1600083018761034d565b6103de6020830186610392565b6103eb604083018561034d565b6103f8606083018461034d565b95945050505050565b6000606082019050818103600083015261041b818661035c565b9050818103602083015261042f818561035c565b90508181036040830152610443818461035c565b9050949350505050565b6000604051905081810181811067ffffffffffffffff8211171561047057600080fd5b8060405250919050565b600067ffffffffffffffff82111561049157600080fd5b601f19601f8301169050602081019050919050565b600081519050919050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000819050919050565b600060ff82169050919050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000819050919050565b600060ff82169050919050565b82818337600083830152505050565b60005b8381101561054c578082015181840152602081019050610531565b8381111561055b576000848401525b50505050565b6000601f19601f83011690509190505600608060405234801561001057600080fd5b506040516107bf3803806107bf833981018060405281019080805182019291906020018051820192919060200180518201929190505050826000908051906020019061005d92919061020a565b50816001908051906020019061007492919061020a565b50806002908051906020019061008b92919061020a565b507f862dc616a336d85acf6c0a863ee6e67b03a19d22753b3455d915fddcdc414ea483838360405180806020018060200180602001848103845287818151815260200191508051906020019080838360005b838110156100f85780820151818401526020810190506100dd565b50505050905090810190601f1680156101255780820380516001836020036101000a031916815260200191505b50848103835286818151815260200191508051906020019080838360005b8381101561015e578082015181840152602081019050610143565b50505050905090810190601f16801561018b5780820380516001836020036101000a031916815260200191505b50848103825285818151815260200191508051906020019080838360005b838110156101c45780820151818401526020810190506101a9565b50505050905090810190601f1680156101f15780820380516001836020036101000a031916815260200191505b50965050505050505060405180910390a15050506102af565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061024b57805160ff1916838001178555610279565b82800160010185558215610279579182015b8281111561027857825182559160200191906001019061025d565b5b509050610286919061028a565b5090565b6102ac91905b808211156102a8576000816000905550600101610290565b5090565b90565b610501806102be6000396000f30060806040526004361061004c576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680634ae70cef14610051578063995297f3146101b9575b600080fd5b34801561005d57600080fd5b50610066610249565b60405180806020018060200180602001848103845287818151815260200191508051906020019080838360005b838110156100ae578082015181840152602081019050610093565b50505050905090810190601f1680156100db5780820380516001836020036101000a031916815260200191505b50848103835286818151815260200191508051906020019080838360005b838110156101145780820151818401526020810190506100f9565b50505050905090810190601f1680156101415780820380516001836020036101000a031916815260200191505b50848103825285818151815260200191508051906020019080838360005b8381101561017a57808201518184015260208101905061015f565b50505050905090810190601f1680156101a75780820380516001836020036101000a031916815260200191505b50965050505050505060405180910390f35b3480156101c557600080fd5b506101ce610433565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561020e5780820151818401526020810190506101f3565b50505050905090810190601f16801561023b5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b6060806060600060016002828054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156102e95780601f106102be576101008083540402835291602001916102e9565b820191906000526020600020905b8154815290600101906020018083116102cc57829003601f168201915b50505050509250818054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156103855780601f1061035a57610100808354040283529160200191610385565b820191906000526020600020905b81548152906001019060200180831161036857829003601f168201915b50505050509150808054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156104215780601f106103f657610100808354040283529160200191610421565b820191906000526020600020905b81548152906001019060200180831161040457829003601f168201915b50505050509050925092509250909192565b606060018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156104cb5780601f106104a0576101008083540402835291602001916104cb565b820191906000526020600020905b8154815290600101906020018083116104ae57829003601f168201915b50505050509050905600a165627a7a72305820470e4045e0d16c5c0a96da91ea8dcc016c322903c8ea98854decf9673a4037f70029a265627a7a723058204f3440a3bed0f0d6a1a03a6851cf595aee6c231ec1e0c55bcef81b11a61735e36c6578706572696d656e74616cf50037"
    };

    public static final String SM_BINARY = String.join("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"constant\":false,\"inputs\":[{\"name\":\"evi\",\"type\":\"string\"},{\"name\":\"info\",\"type\":\"string\"},{\"name\":\"id\",\"type\":\"string\"},{\"name\":\"signAddr\",\"type\":\"address\"},{\"name\":\"message\",\"type\":\"bytes32\"},{\"name\":\"v\",\"type\":\"uint8\"},{\"name\":\"r\",\"type\":\"bytes32\"},{\"name\":\"s\",\"type\":\"bytes32\"}],\"name\":\"insertEvidence\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"addr\",\"type\":\"address\"}],\"name\":\"newEvidenceEvent\",\"type\":\"event\"}]"
    };

    public static final String ABI = String.join("", ABI_ARRAY);

    public static final String FUNC_INSERTEVIDENCE = "insertEvidence";

    public static final Event NEWEVIDENCEEVENT_EVENT =
            new Event(
                    "newEvidenceEvent",
                    Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));;

    protected EvidenceVerify(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public TransactionReceipt insertEvidence(
            String evi,
            String info,
            String id,
            String signAddr,
            byte[] message,
            BigInteger v,
            byte[] r,
            byte[] s) {
        final Function function =
                new Function(
                        FUNC_INSERTEVIDENCE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(evi),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(info),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(id),
                                new org.fisco.bcos.sdk.abi.datatypes.Address(signAddr),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Bytes32(message),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint8(v),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Bytes32(r),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Bytes32(s)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void insertEvidence(
            String evi,
            String info,
            String id,
            String signAddr,
            byte[] message,
            BigInteger v,
            byte[] r,
            byte[] s,
            TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_INSERTEVIDENCE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(evi),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(info),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(id),
                                new org.fisco.bcos.sdk.abi.datatypes.Address(signAddr),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Bytes32(message),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint8(v),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Bytes32(r),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Bytes32(s)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForInsertEvidence(
            String evi,
            String info,
            String id,
            String signAddr,
            byte[] message,
            BigInteger v,
            byte[] r,
            byte[] s) {
        final Function function =
                new Function(
                        FUNC_INSERTEVIDENCE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(evi),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(info),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(id),
                                new org.fisco.bcos.sdk.abi.datatypes.Address(signAddr),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Bytes32(message),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint8(v),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Bytes32(r),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Bytes32(s)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple8<String, String, String, String, byte[], BigInteger, byte[], byte[]>
            getInsertEvidenceInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_INSERTEVIDENCE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Address>() {},
                                new TypeReference<Bytes32>() {},
                                new TypeReference<Uint8>() {},
                                new TypeReference<Bytes32>() {},
                                new TypeReference<Bytes32>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple8<String, String, String, String, byte[], BigInteger, byte[], byte[]>(
                (String) results.get(0).getValue(),
                (String) results.get(1).getValue(),
                (String) results.get(2).getValue(),
                (String) results.get(3).getValue(),
                (byte[]) results.get(4).getValue(),
                (BigInteger) results.get(5).getValue(),
                (byte[]) results.get(6).getValue(),
                (byte[]) results.get(7).getValue());
    }

    public Tuple1<String> getInsertEvidenceOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_INSERTEVIDENCE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public List<NewEvidenceEventEventResponse> getNewEvidenceEventEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList =
                extractEventParametersWithLog(NEWEVIDENCEEVENT_EVENT, transactionReceipt);
        ArrayList<NewEvidenceEventEventResponse> responses =
                new ArrayList<NewEvidenceEventEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            NewEvidenceEventEventResponse typedResponse = new NewEvidenceEventEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.addr = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeNewEvidenceEventEvent(
            String fromBlock, String toBlock, List<String> otherTopics, EventCallback callback) {
        String topic0 = eventEncoder.encode(NEWEVIDENCEEVENT_EVENT);
        subscribeEvent(ABI, BINARY, topic0, fromBlock, toBlock, otherTopics, callback);
    }

    public void subscribeNewEvidenceEventEvent(EventCallback callback) {
        String topic0 = eventEncoder.encode(NEWEVIDENCEEVENT_EVENT);
        subscribeEvent(ABI, BINARY, topic0, callback);
    }

    public static EvidenceVerify load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new EvidenceVerify(contractAddress, client, credential);
    }

    public static EvidenceVerify deploy(Client client, CryptoKeyPair credential)
            throws ContractException {
        return deploy(
                EvidenceVerify.class, client, credential, getBinary(client.getCryptoSuite()), "");
    }

    public static class NewEvidenceEventEventResponse {
        public TransactionReceipt.Logs log;

        public String addr;
    }
}
