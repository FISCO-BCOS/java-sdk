package org.fisco.bcos.sdk.v3.test.contract.solidity;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicStruct;
import org.fisco.bcos.sdk.v3.codec.datatypes.Event;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class EventSubDemo extends Contract {
    public static final String[] BINARY_ARRAY = {
        "608060405234801561001057600080fd5b50610766806100206000396000f3fe608060405234801561001057600080fd5b50600436106100415760003560e01c8063274b9f101461004657806375a71ca0146100705780639b80b05014610092575b600080fd5b610059610054366004610453565b6100a7565b60405161006792919061050a565b60405180910390f35b61008361007e366004610552565b61015e565b604051610067939291906105a2565b6100a56100a03660046105ca565b610247565b005b60006060837f347ac5ea9ec0d19b3b6ad2651257cc684ecca0e41558fc1c578516e90bcf45f260405160405180910390a2826040516100e69190610637565b604051908190038120907f5f886b86d4364df6c5d7d9a65705aac01b180e2a136442a9921860ca0fdf49db90600090a2826040516101249190610637565b6040519081900381209085907f3752a357a949d58944fee07631779eb98f8c1ba788096770cc9cbc014e2375b890600090a3509192909150565b6000806060857f34d6d9becd7a327109612b0e636ca3bea6263a273c0256df42fbdf3d92e467f960405160405180910390a260405185907f335e9c894374ff443b4a42deadc7dce6dba3b921062aef988a13ed1fba42034390600090a2836040516101c99190610637565b604051908190038120907fdb84d7c006c4de68f9c0bd50b8b81ed31f29ebeec325c872d36445c6565d757c90600090a2836040516102079190610637565b60405190819003812090869088907f6a69e35d4db25f48425c10a32d6e2553fdc3af0e5096cb309b0049c2235e197990600090a450939492935090919050565b604080516001808252818301909252600091816020015b61028260405180606001604052806060815260200160608152602001600081525090565b81526020019060019003908161025e579050509050604051806060016040528085815260200184815260200183815250816000815181106102c5576102c5610653565b60200260200101819052507f6e8b295299c2e7d6f65fcccc3b54ca076974ed2db26b305fe48716ec468a4ee6816040516102ff9190610669565b60405180910390a17f5358be4df107be4d9b023fc323f41d7109610225c6ef211b9d375b9fbd7ccc4f84848460405161033a939291906106fa565b60405180910390a1826040516103509190610637565b6040518091039020846040516103669190610637565b604051908190038120907f8c21d0fee2cb98bb839d8a17a9fe8e11839e85be0675622169aca05cda58260890600090a360405182907f8f922f97953a95596e8c51012daa271d3dd61455382767329dec1c997ed1fbf190600090a250505050565b634e487b7160e01b600052604160045260246000fd5b600067ffffffffffffffff808411156103f8576103f86103c7565b604051601f8501601f19908116603f01168101908282118183101715610420576104206103c7565b8160405280935085815286868601111561043957600080fd5b858560208301376000602087830101525050509392505050565b6000806040838503121561046657600080fd5b82359150602083013567ffffffffffffffff81111561048457600080fd5b8301601f8101851361049557600080fd5b6104a4858235602084016103dd565b9150509250929050565b60005b838110156104c95781810151838201526020016104b1565b838111156104d8576000848401525b50505050565b600081518084526104f68160208601602086016104ae565b601f01601f19169290920160200192915050565b82815260406020820152600061052360408301846104de565b949350505050565b600082601f83011261053c57600080fd5b61054b838335602085016103dd565b9392505050565b60008060006060848603121561056757600080fd5b8335925060208401359150604084013567ffffffffffffffff81111561058c57600080fd5b6105988682870161052b565b9150509250925092565b8381528260208201526060604082015260006105c160608301846104de565b95945050505050565b6000806000606084860312156105df57600080fd5b833567ffffffffffffffff808211156105f757600080fd5b6106038783880161052b565b9450602086013591508082111561061957600080fd5b506106268682870161052b565b925050604084013590509250925092565b600082516106498184602087016104ae565b9190910192915050565b634e487b7160e01b600052603260045260246000fd5b60006020808301818452808551808352604092508286019150828160051b87010184880160005b838110156106ec57603f198984030185528151606081518186526106b6828701826104de565b915050888201518582038a8701526106ce82826104de565b92890151958901959095525094870194925090860190600101610690565b509098975050505050505050565b60608152600061070d60608301866104de565b828103602084015261071f81866104de565b91505082604083015294935050505056fea264697066735822122089d55dc51975d823956db3c79734aa364503e977f601f3788cbd34b23bb53d6364736f6c634300080b0033"
    };

    public static final String BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {
        "608060405234801561001057600080fd5b50610766806100206000396000f3fe608060405234801561001057600080fd5b50600436106100415760003560e01c8063612d2bff14610046578063d2ae49dd1461005b578063e1ddfaf714610085575b600080fd5b61005961005436600461047a565b6100a7565b005b61006e6100693660046104e7565b610227565b60405161007c92919061059e565b60405180910390f35b6100986100933660046105bf565b6102de565b60405161007c9392919061060f565b604080516001808252818301909252600091816020015b6100e260405180606001604052806060815260200160608152602001600081525090565b8152602001906001900390816100be5790505090506040518060600160405280858152602001848152602001838152508160008151811061012557610125610637565b60200260200101819052507f3ae910f84d81af606d026c30f74703483eb1967fe2ce0739958df871c45354dc8160405161015f919061064d565b60405180910390a17fb8ab06736b3dfec12a172981931c9e25724e33b9d0902dcc3d776d1957fed49584848460405161019a939291906106de565b60405180910390a1826040516101b09190610714565b6040518091039020846040516101c69190610714565b604051908190038120907f7d5d4b35565e8ef6f3e083591478c95dcb35dedf5d0ac6355dde5d9f9e0e5bd390600090a360405182907fe083dc30f45290272bf5496ad6e0c7a5051ed7c6aa2042d26d38a69aa579042190600090a250505050565b60006060837fa088533085dfc0ff7c4d3bbdb59aaff77bbd3c33e7d9ab03a5925f992f9b82a660405160405180910390a2826040516102669190610714565b604051908190038120907fd93dc2cb92c6566fa1747478cf11424fea23528fdb5cbfb18b3d9a617e88531a90600090a2826040516102a49190610714565b6040519081900381209085907f2f05c1c5ee846538bc6a5c8740a4385209bd1569ec55feb14b9a9af7d018730d90600090a3509192909150565b6000806060857f5cbb1b4a7272b6d2f504f0a84614866e3b1c10358e1aff1650b1261492fc134760405160405180910390a260405185907fd8b50b6b74c748d297159108a50fc320d5c4c12ba2ec65a752f8bc35e0422cd190600090a2836040516103499190610714565b604051908190038120907fcb4a0749276a06a00950a4a6d16baf41ed5148d70c2ee5a255bb6c02ee6ad95f90600090a2836040516103879190610714565b60405190819003812090869088907ff16a378efaa9313a2841e3199e34474dcc5449d20b6cc8d5e9f97ef97429b1c390600090a450939492935090919050565b63b95aa35560e01b600052604160045260246000fd5b600067ffffffffffffffff808411156103f8576103f86103c7565b604051601f8501601f19908116603f01168101908282118183101715610420576104206103c7565b8160405280935085815286868601111561043957600080fd5b858560208301376000602087830101525050509392505050565b600082601f83011261046457600080fd5b610473838335602085016103dd565b9392505050565b60008060006060848603121561048f57600080fd5b833567ffffffffffffffff808211156104a757600080fd5b6104b387838801610453565b945060208601359150808211156104c957600080fd5b506104d686828701610453565b925050604084013590509250925092565b600080604083850312156104fa57600080fd5b82359150602083013567ffffffffffffffff81111561051857600080fd5b8301601f8101851361052957600080fd5b610538858235602084016103dd565b9150509250929050565b60005b8381101561055d578181015183820152602001610545565b8381111561056c576000848401525b50505050565b6000815180845261058a816020860160208601610542565b601f01601f19169290920160200192915050565b8281526040602082015260006105b76040830184610572565b949350505050565b6000806000606084860312156105d457600080fd5b8335925060208401359150604084013567ffffffffffffffff8111156105f957600080fd5b61060586828701610453565b9150509250925092565b83815282602082015260606040820152600061062e6060830184610572565b95945050505050565b63b95aa35560e01b600052603260045260246000fd5b60006020808301818452808551808352604092508286019150828160051b87010184880160005b838110156106d057603f1989840301855281516060815181865261069a82870182610572565b915050888201518582038a8701526106b28282610572565b92890151958901959095525094870194925090860190600101610674565b509098975050505050505050565b6060815260006106f16060830186610572565b82810360208401526107038186610572565b915050826040830152949350505050565b60008251610726818460208701610542565b919091019291505056fea2646970667358221220d061c428ac904f1378671d62012d32a5eb0e8c70131d6b5db8364510214eb88b64736f6c634300080b0033"
    };

    public static final String SM_BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"uint256\",\"name\":\"u\",\"type\":\"uint256\"}],\"name\":\"Echo\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"int256\",\"name\":\"i\",\"type\":\"int256\"}],\"name\":\"Echo\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"string\",\"name\":\"s\",\"type\":\"string\"}],\"name\":\"Echo\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"uint256\",\"name\":\"u\",\"type\":\"uint256\"},{\"indexed\":true,\"internalType\":\"int256\",\"name\":\"i\",\"type\":\"int256\"},{\"indexed\":true,\"internalType\":\"string\",\"name\":\"s\",\"type\":\"string\"}],\"name\":\"Echo\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"bytes32\",\"name\":\"bsn\",\"type\":\"bytes32\"}],\"name\":\"Echo\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"bytes\",\"name\":\"bs\",\"type\":\"bytes\"}],\"name\":\"Echo\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"bytes32\",\"name\":\"bsn\",\"type\":\"bytes32\"},{\"indexed\":true,\"internalType\":\"bytes\",\"name\":\"bs\",\"type\":\"bytes\"}],\"name\":\"Echo\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"string\",\"name\":\"from_account\",\"type\":\"string\"},{\"indexed\":false,\"internalType\":\"string\",\"name\":\"to_account\",\"type\":\"string\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"Transfer\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"string\",\"name\":\"from_account\",\"type\":\"string\"},{\"indexed\":true,\"internalType\":\"string\",\"name\":\"to_account\",\"type\":\"string\"}],\"name\":\"TransferAccount\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"TransferAmount\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"components\":[{\"internalType\":\"string\",\"name\":\"from_account\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"to_account\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"indexed\":false,\"internalType\":\"struct EventSubDemo.TransactionData[]\",\"name\":\"transaction_data\",\"type\":\"tuple[]\"}],\"name\":\"TransferData\",\"type\":\"event\"},{\"inputs\":[{\"internalType\":\"bytes32\",\"name\":\"bsn\",\"type\":\"bytes32\"},{\"internalType\":\"bytes\",\"name\":\"bs\",\"type\":\"bytes\"}],\"name\":\"echo\",\"outputs\":[{\"internalType\":\"bytes32\",\"name\":\"\",\"type\":\"bytes32\"},{\"internalType\":\"bytes\",\"name\":\"\",\"type\":\"bytes\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"u\",\"type\":\"uint256\"},{\"internalType\":\"int256\",\"name\":\"i\",\"type\":\"int256\"},{\"internalType\":\"string\",\"name\":\"s\",\"type\":\"string\"}],\"name\":\"echo\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"},{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"},{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"from_account\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"to_account\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"transfer\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_ECHO = "echo";

    public static final String FUNC_TRANSFER = "transfer";

    public static final Event ECHOUINT256_EVENT =
            new Event(
                    "Echo", Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}));;

    public static final Event ECHOINT256_EVENT =
            new Event("Echo", Arrays.<TypeReference<?>>asList(new TypeReference<Int256>(true) {}));;

    public static final Event ECHOSTRING_EVENT =
            new Event(
                    "Echo",
                    Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>(true) {}));;

    public static final Event ECHOUINT256INT256STRING_EVENT =
            new Event(
                    "Echo",
                    Arrays.<TypeReference<?>>asList(
                            new TypeReference<Uint256>(true) {},
                            new TypeReference<Int256>(true) {},
                            new TypeReference<Utf8String>(true) {}));;

    public static final Event ECHOBYTES32_EVENT =
            new Event(
                    "Echo", Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}));;

    public static final Event ECHOBYTES_EVENT =
            new Event(
                    "Echo",
                    Arrays.<TypeReference<?>>asList(new TypeReference<DynamicBytes>(true) {}));;

    public static final Event ECHOBYTES32BYTES_EVENT =
            new Event(
                    "Echo",
                    Arrays.<TypeReference<?>>asList(
                            new TypeReference<Bytes32>(true) {},
                            new TypeReference<DynamicBytes>(true) {}));;

    public static final Event TRANSFER_EVENT =
            new Event(
                    "Transfer",
                    Arrays.<TypeReference<?>>asList(
                            new TypeReference<Utf8String>() {},
                            new TypeReference<Utf8String>() {},
                            new TypeReference<Uint256>() {}));;

    public static final Event TRANSFERACCOUNT_EVENT =
            new Event(
                    "TransferAccount",
                    Arrays.<TypeReference<?>>asList(
                            new TypeReference<Utf8String>(true) {},
                            new TypeReference<Utf8String>(true) {}));;

    public static final Event TRANSFERAMOUNT_EVENT =
            new Event(
                    "TransferAmount",
                    Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}));;

    public static final Event TRANSFERDATA_EVENT =
            new Event(
                    "TransferData",
                    Arrays.<TypeReference<?>>asList(
                            new TypeReference<DynamicArray<TransactionData>>() {}));;

    protected EventSubDemo(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public List<EchoUint256EventResponse> getEchoUint256Events(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList =
                extractEventParametersWithLog(ECHOUINT256_EVENT, transactionReceipt);
        ArrayList<EchoUint256EventResponse> responses =
                new ArrayList<EchoUint256EventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            EchoUint256EventResponse typedResponse = new EchoUint256EventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.u = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public List<EchoInt256EventResponse> getEchoInt256Events(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList =
                extractEventParametersWithLog(ECHOINT256_EVENT, transactionReceipt);
        ArrayList<EchoInt256EventResponse> responses =
                new ArrayList<EchoInt256EventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            EchoInt256EventResponse typedResponse = new EchoInt256EventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.i = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public List<EchoStringEventResponse> getEchoStringEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList =
                extractEventParametersWithLog(ECHOSTRING_EVENT, transactionReceipt);
        ArrayList<EchoStringEventResponse> responses =
                new ArrayList<EchoStringEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            EchoStringEventResponse typedResponse = new EchoStringEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.s = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public List<EchoUint256Int256StringEventResponse> getEchoUint256Int256StringEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList =
                extractEventParametersWithLog(ECHOUINT256INT256STRING_EVENT, transactionReceipt);
        ArrayList<EchoUint256Int256StringEventResponse> responses =
                new ArrayList<EchoUint256Int256StringEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            EchoUint256Int256StringEventResponse typedResponse =
                    new EchoUint256Int256StringEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.u = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.i = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.s = (byte[]) eventValues.getIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public List<EchoBytes32EventResponse> getEchoBytes32Events(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList =
                extractEventParametersWithLog(ECHOBYTES32_EVENT, transactionReceipt);
        ArrayList<EchoBytes32EventResponse> responses =
                new ArrayList<EchoBytes32EventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            EchoBytes32EventResponse typedResponse = new EchoBytes32EventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.bsn = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public List<EchoBytesEventResponse> getEchoBytesEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList =
                extractEventParametersWithLog(ECHOBYTES_EVENT, transactionReceipt);
        ArrayList<EchoBytesEventResponse> responses =
                new ArrayList<EchoBytesEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            EchoBytesEventResponse typedResponse = new EchoBytesEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.bs = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public List<EchoBytes32BytesEventResponse> getEchoBytes32BytesEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList =
                extractEventParametersWithLog(ECHOBYTES32BYTES_EVENT, transactionReceipt);
        ArrayList<EchoBytes32BytesEventResponse> responses =
                new ArrayList<EchoBytes32BytesEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            EchoBytes32BytesEventResponse typedResponse = new EchoBytes32BytesEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.bsn = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.bs = (byte[]) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList =
                extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses =
                new ArrayList<TransferEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from_account =
                    (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.to_account = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public List<TransferAccountEventResponse> getTransferAccountEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList =
                extractEventParametersWithLog(TRANSFERACCOUNT_EVENT, transactionReceipt);
        ArrayList<TransferAccountEventResponse> responses =
                new ArrayList<TransferAccountEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            TransferAccountEventResponse typedResponse = new TransferAccountEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from_account = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to_account = (byte[]) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public List<TransferAmountEventResponse> getTransferAmountEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList =
                extractEventParametersWithLog(TRANSFERAMOUNT_EVENT, transactionReceipt);
        ArrayList<TransferAmountEventResponse> responses =
                new ArrayList<TransferAmountEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            TransferAmountEventResponse typedResponse = new TransferAmountEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.amount = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public List<TransferDataEventResponse> getTransferDataEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList =
                extractEventParametersWithLog(TRANSFERDATA_EVENT, transactionReceipt);
        ArrayList<TransferDataEventResponse> responses =
                new ArrayList<TransferDataEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            TransferDataEventResponse typedResponse = new TransferDataEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.transaction_data =
                    (DynamicArray<TransactionData>)
                            eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public TransactionReceipt echo(byte[] bsn, byte[] bs) {
        final Function function =
                new Function(
                        FUNC_ECHO,
                        Arrays.<Type>asList(new Bytes32(bsn), new DynamicBytes(bs)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String getSignedTransactionForEcho(byte[] bsn, byte[] bs) {
        final Function function =
                new Function(
                        FUNC_ECHO,
                        Arrays.<Type>asList(new Bytes32(bsn), new DynamicBytes(bs)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
    }

    public String echo(byte[] bsn, byte[] bs, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_ECHO,
                        Arrays.<Type>asList(new Bytes32(bsn), new DynamicBytes(bs)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple2<byte[], byte[]> getEchoBytes32BytesInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_ECHO,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Bytes32>() {},
                                new TypeReference<DynamicBytes>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<byte[], byte[]>(
                (byte[]) results.get(0).getValue(), (byte[]) results.get(1).getValue());
    }

    public Tuple2<byte[], byte[]> getEchoBytes32BytesOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_ECHO,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Bytes32>() {},
                                new TypeReference<DynamicBytes>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<byte[], byte[]>(
                (byte[]) results.get(0).getValue(), (byte[]) results.get(1).getValue());
    }

    public TransactionReceipt echo(BigInteger u, BigInteger i, String s) {
        final Function function =
                new Function(
                        FUNC_ECHO,
                        Arrays.<Type>asList(new Uint256(u), new Int256(i), new Utf8String(s)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String getSignedTransactionForEcho(BigInteger u, BigInteger i, String s) {
        final Function function =
                new Function(
                        FUNC_ECHO,
                        Arrays.<Type>asList(new Uint256(u), new Int256(i), new Utf8String(s)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
    }

    public String echo(BigInteger u, BigInteger i, String s, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_ECHO,
                        Arrays.<Type>asList(new Uint256(u), new Int256(i), new Utf8String(s)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple3<BigInteger, BigInteger, String> getEchoUint256Int256StringInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_ECHO,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Uint256>() {},
                                new TypeReference<Int256>() {},
                                new TypeReference<Utf8String>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<BigInteger, BigInteger, String>(
                (BigInteger) results.get(0).getValue(),
                (BigInteger) results.get(1).getValue(),
                (String) results.get(2).getValue());
    }

    public Tuple3<BigInteger, BigInteger, String> getEchoUint256Int256StringOutput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_ECHO,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Uint256>() {},
                                new TypeReference<Int256>() {},
                                new TypeReference<Utf8String>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<BigInteger, BigInteger, String>(
                (BigInteger) results.get(0).getValue(),
                (BigInteger) results.get(1).getValue(),
                (String) results.get(2).getValue());
    }

    public TransactionReceipt transfer(String from_account, String to_account, BigInteger amount) {
        final Function function =
                new Function(
                        FUNC_TRANSFER,
                        Arrays.<Type>asList(
                                new Utf8String(from_account),
                                new Utf8String(to_account),
                                new Uint256(amount)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String getSignedTransactionForTransfer(
            String from_account, String to_account, BigInteger amount) {
        final Function function =
                new Function(
                        FUNC_TRANSFER,
                        Arrays.<Type>asList(
                                new Utf8String(from_account),
                                new Utf8String(to_account),
                                new Uint256(amount)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
    }

    public String transfer(
            String from_account,
            String to_account,
            BigInteger amount,
            TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_TRANSFER,
                        Arrays.<Type>asList(
                                new Utf8String(from_account),
                                new Utf8String(to_account),
                                new Uint256(amount)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple3<String, String, BigInteger> getTransferInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_TRANSFER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<String, String, BigInteger>(
                (String) results.get(0).getValue(),
                (String) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue());
    }

    public static EventSubDemo load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new EventSubDemo(contractAddress, client, credential);
    }

    public static EventSubDemo deploy(Client client, CryptoKeyPair credential)
            throws ContractException {
        return deploy(
                EventSubDemo.class,
                client,
                credential,
                getBinary(client.getCryptoSuite()),
                getABI(),
                null,
                null);
    }

    public static class TransactionData extends DynamicStruct {
        public String from_account;

        public String to_account;

        public BigInteger amount;

        public TransactionData(Utf8String from_account, Utf8String to_account, Uint256 amount) {
            super(from_account, to_account, amount);
            this.from_account = from_account.getValue();
            this.to_account = to_account.getValue();
            this.amount = amount.getValue();
        }

        public TransactionData(String from_account, String to_account, BigInteger amount) {
            super(new Utf8String(from_account), new Utf8String(to_account), new Uint256(amount));
            this.from_account = from_account;
            this.to_account = to_account;
            this.amount = amount;
        }
    }

    public static class EchoUint256EventResponse {
        public TransactionReceipt.Logs log;

        public BigInteger u;
    }

    public static class EchoInt256EventResponse {
        public TransactionReceipt.Logs log;

        public BigInteger i;
    }

    public static class EchoStringEventResponse {
        public TransactionReceipt.Logs log;

        public byte[] s;
    }

    public static class EchoUint256Int256StringEventResponse {
        public TransactionReceipt.Logs log;

        public BigInteger u;

        public BigInteger i;

        public byte[] s;
    }

    public static class EchoBytes32EventResponse {
        public TransactionReceipt.Logs log;

        public byte[] bsn;
    }

    public static class EchoBytesEventResponse {
        public TransactionReceipt.Logs log;

        public byte[] bs;
    }

    public static class EchoBytes32BytesEventResponse {
        public TransactionReceipt.Logs log;

        public byte[] bsn;

        public byte[] bs;
    }

    public static class TransferEventResponse {
        public TransactionReceipt.Logs log;

        public String from_account;

        public String to_account;

        public BigInteger amount;
    }

    public static class TransferAccountEventResponse {
        public TransactionReceipt.Logs log;

        public byte[] from_account;

        public byte[] to_account;
    }

    public static class TransferAmountEventResponse {
        public TransactionReceipt.Logs log;

        public BigInteger amount;
    }

    public static class TransferDataEventResponse {
        public TransactionReceipt.Logs log;

        public DynamicArray<TransactionData> transaction_data;
    }
}
