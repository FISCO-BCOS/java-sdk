package org.fisco.bcos.sdk.demo.contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.Function;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.abi.datatypes.Utf8String;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.Contract;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class ParallelOk extends Contract {
    public static final String[] BINARY_ARRAY = {
        "60806040526110066000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555034801561005257600080fd5b50610aeb806100626000396000f30060806040526004361061008e576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806334a18dda1461009357806335ee5f871461010657806379fa913f146101835780638a42ebe9146101ec5780639b80b0501461025f578063bca926af14610318578063d39f70bc1461032f578063fad42f8714610346575b600080fd5b34801561009f57600080fd5b50610104600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001909291905050506103ff565b005b34801561011257600080fd5b5061016d600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050610570565b6040518082815260200191505060405180910390f35b34801561018f57600080fd5b506101ea600480360381019080803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091929192905050506105e5565b005b3480156101f857600080fd5b5061025d600480360381019080803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091929192908035906020019092919050505061074d565b005b34801561026b57600080fd5b50610316600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001909291905050506107c1565b005b34801561032457600080fd5b5061032d6108b8565b005b34801561033b57600080fd5b5061034461093a565b005b34801561035257600080fd5b506103fd600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001909291905050506109b8565b005b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16630553904e3084846040518463ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200180602001838152602001828103825284818151815260200191508051906020019080838360005b838110156104e25780820151818401526020810190506104c7565b50505050905090810190601f16801561050f5780820380516001836020036101000a031916815260200191505b50945050505050602060405180830381600087803b15801561053057600080fd5b505af1158015610544573d6000803e3d6000fd5b505050506040513d602081101561055a57600080fd5b8101908080519060200190929190505050505050565b60006001826040518082805190602001908083835b6020831015156105aa5780518252602082019150602081019050602083039250610585565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020549050919050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166311e3f2af30836040518363ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200180602001828103825283818151815260200191508051906020019080838360005b838110156106c15780820151818401526020810190506106a6565b50505050905090810190601f1680156106ee5780820380516001836020036101000a031916815260200191505b509350505050602060405180830381600087803b15801561070e57600080fd5b505af1158015610722573d6000803e3d6000fd5b505050506040513d602081101561073857600080fd5b81019080805190602001909291905050505050565b806001836040518082805190602001908083835b6020831015156107865780518252602082019150602081019050602083039250610761565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020819055505050565b806001846040518082805190602001908083835b6020831015156107fa57805182526020820191506020810190506020830392506107d5565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060008282540392505081905550806001836040518082805190602001908083835b602083101515610873578051825260208201915060208101905060208303925061084e565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060008282540192505081905550505050565b6108f86040805190810160405280601f81526020017f7472616e7366657228737472696e672c737472696e672c75696e74323536290081525060026103ff565b6109386040805190810160405280601381526020017f73657428737472696e672c75696e74323536290000000000000000000000000081525060016103ff565b565b6109786040805190810160405280601f81526020017f7472616e7366657228737472696e672c737472696e672c75696e7432353629008152506105e5565b6109b66040805190810160405280601381526020017f73657428737472696e672c75696e7432353629000000000000000000000000008152506105e5565b565b806001846040518082805190602001908083835b6020831015156109f157805182526020820191506020810190506020830392506109cc565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060008282540392505081905550806001836040518082805190602001908083835b602083101515610a6a5780518252602082019150602081019050602083039250610a45565b6001836020036101000a03801982511681845116808217855250505050505090500191505090815260200160405180910390206000828254019250508190555060648111151515610aba57600080fd5b5050505600a165627a7a72305820d721c70a8f692fdebc8cd485607c47cdce4652290111b4152b32f5e54812f0410029"
    };

    public static final String BINARY = String.join("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {
        "60806040526110066000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555034801561005257600080fd5b50610aea806100626000396000f30060806040526004361061008d576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff168062f0e13314610092578063612d2bff146100fb578063748e7a1b146101b457806394618e4c146101cb578063ab71bf09146101e2578063b4c653e01461029b578063cd93c25d1461030e578063f2f4ee6d1461038b575b600080fd5b34801561009e57600080fd5b506100f9600480360381019080803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091929192905050506103fe565b005b34801561010757600080fd5b506101b2600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f016020809104026020016040519081016040528093929190818152602001838380828437820191505050505050919291929080359060200190929190505050610566565b005b3480156101c057600080fd5b506101c961065d565b005b3480156101d757600080fd5b506101e06106db565b005b3480156101ee57600080fd5b50610299600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091929192908035906020019092919050505061075d565b005b3480156102a757600080fd5b5061030c600480360381019080803590602001908201803590602001908080601f016020809104026020016040519081016040528093929190818152602001838380828437820191505050505050919291929080359060200190929190505050610864565b005b34801561031a57600080fd5b50610375600480360381019080803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091929192905050506109d5565b6040518082815260200191505060405180910390f35b34801561039757600080fd5b506103fc600480360381019080803590602001908201803590602001908080601f016020809104026020016040519081016040528093929190818152602001838380828437820191505050505050919291929080359060200190929190505050610a4a565b005b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663714c65bd30836040518363ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200180602001828103825283818151815260200191508051906020019080838360005b838110156104da5780820151818401526020810190506104bf565b50505050905090810190601f1680156105075780820380516001836020036101000a031916815260200191505b509350505050602060405180830381600087803b15801561052757600080fd5b505af115801561053b573d6000803e3d6000fd5b505050506040513d602081101561055157600080fd5b81019080805190602001909291905050505050565b806001846040518082805190602001908083835b60208310151561059f578051825260208201915060208101905060208303925061057a565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060008282540392505081905550806001836040518082805190602001908083835b60208310151561061857805182526020820191506020810190506020830392506105f3565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060008282540192505081905550505050565b61069b6040805190810160405280601f81526020017f7472616e7366657228737472696e672c737472696e672c75696e7432353629008152506103fe565b6106d96040805190810160405280601381526020017f73657428737472696e672c75696e7432353629000000000000000000000000008152506103fe565b565b61071b6040805190810160405280601f81526020017f7472616e7366657228737472696e672c737472696e672c75696e7432353629008152506002610864565b61075b6040805190810160405280601381526020017f73657428737472696e672c75696e7432353629000000000000000000000000008152506001610864565b565b806001846040518082805190602001908083835b6020831015156107965780518252602082019150602081019050602083039250610771565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060008282540392505081905550806001836040518082805190602001908083835b60208310151561080f57805182526020820191506020810190506020830392506107ea565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020600082825401925050819055506064811115151561085f57600080fd5b505050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663dc536a623084846040518463ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200180602001838152602001828103825284818151815260200191508051906020019080838360005b8381101561094757808201518184015260208101905061092c565b50505050905090810190601f1680156109745780820380516001836020036101000a031916815260200191505b50945050505050602060405180830381600087803b15801561099557600080fd5b505af11580156109a9573d6000803e3d6000fd5b505050506040513d60208110156109bf57600080fd5b8101908080519060200190929190505050505050565b60006001826040518082805190602001908083835b602083101515610a0f57805182526020820191506020810190506020830392506109ea565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020549050919050565b806001836040518082805190602001908083835b602083101515610a835780518252602082019150602081019050602083039250610a5e565b6001836020036101000a03801982511681845116808217855250505050505090500191505090815260200160405180910390208190555050505600a165627a7a723058201c320eb489845fe9ef20bfa702e4699ad5264d960fde53a298b8dccc7161345b0029"
    };

    public static final String SM_BINARY = String.join("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"constant\":false,\"inputs\":[{\"name\":\"functionName\",\"type\":\"string\"},{\"name\":\"criticalSize\",\"type\":\"uint256\"}],\"name\":\"registerParallelFunction\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"name\",\"type\":\"string\"}],\"name\":\"balanceOf\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"functionName\",\"type\":\"string\"}],\"name\":\"unregisterParallelFunction\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"num\",\"type\":\"uint256\"}],\"name\":\"set\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"from\",\"type\":\"string\"},{\"name\":\"to\",\"type\":\"string\"},{\"name\":\"num\",\"type\":\"uint256\"}],\"name\":\"transfer\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"enableParallel\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"disableParallel\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"from\",\"type\":\"string\"},{\"name\":\"to\",\"type\":\"string\"},{\"name\":\"num\",\"type\":\"uint256\"}],\"name\":\"transferWithRevert\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = String.join("", ABI_ARRAY);

    public static final String FUNC_REGISTERPARALLELFUNCTION = "registerParallelFunction";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_UNREGISTERPARALLELFUNCTION = "unregisterParallelFunction";

    public static final String FUNC_SET = "set";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_ENABLEPARALLEL = "enableParallel";

    public static final String FUNC_DISABLEPARALLEL = "disableParallel";

    public static final String FUNC_TRANSFERWITHREVERT = "transferWithRevert";

    protected ParallelOk(String contractAddress, Client client, CryptoInterface credential) {
        super(getBinary(credential), contractAddress, client, credential);
    }

    public static String getBinary(CryptoInterface credential) {
        return (credential.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public TransactionReceipt registerParallelFunction(
            String functionName, BigInteger criticalSize) {
        final Function function =
                new Function(
                        FUNC_REGISTERPARALLELFUNCTION,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(functionName),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(
                                        criticalSize)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void registerParallelFunction(
            String functionName, BigInteger criticalSize, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_REGISTERPARALLELFUNCTION,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(functionName),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(
                                        criticalSize)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForRegisterParallelFunction(
            String functionName, BigInteger criticalSize) {
        final Function function =
                new Function(
                        FUNC_REGISTERPARALLELFUNCTION,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(functionName),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(
                                        criticalSize)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<String, BigInteger> getRegisterParallelFunctionInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_REGISTERPARALLELFUNCTION,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Uint256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, BigInteger>(
                (String) results.get(0).getValue(), (BigInteger) results.get(1).getValue());
    }

    public BigInteger balanceOf(String name) throws ContractException {
        final Function function =
                new Function(
                        FUNC_BALANCEOF,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Utf8String(name)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public TransactionReceipt unregisterParallelFunction(String functionName) {
        final Function function =
                new Function(
                        FUNC_UNREGISTERPARALLELFUNCTION,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(functionName)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void unregisterParallelFunction(String functionName, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_UNREGISTERPARALLELFUNCTION,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(functionName)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForUnregisterParallelFunction(String functionName) {
        final Function function =
                new Function(
                        FUNC_UNREGISTERPARALLELFUNCTION,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(functionName)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<String> getUnregisterParallelFunctionInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_UNREGISTERPARALLELFUNCTION,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public TransactionReceipt set(String name, BigInteger num) {
        final Function function =
                new Function(
                        FUNC_SET,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(name),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(num)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void set(String name, BigInteger num, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_SET,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(name),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(num)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForSet(String name, BigInteger num) {
        final Function function =
                new Function(
                        FUNC_SET,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(name),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(num)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<String, BigInteger> getSetInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_SET,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Uint256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, BigInteger>(
                (String) results.get(0).getValue(), (BigInteger) results.get(1).getValue());
    }

    public TransactionReceipt transfer(String from, String to, BigInteger num) {
        final Function function =
                new Function(
                        FUNC_TRANSFER,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(from),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(to),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(num)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void transfer(String from, String to, BigInteger num, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_TRANSFER,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(from),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(to),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(num)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForTransfer(String from, String to, BigInteger num) {
        final Function function =
                new Function(
                        FUNC_TRANSFER,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(from),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(to),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(num)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
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
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<String, String, BigInteger>(
                (String) results.get(0).getValue(),
                (String) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue());
    }

    public TransactionReceipt enableParallel() {
        final Function function =
                new Function(
                        FUNC_ENABLEPARALLEL,
                        Arrays.<Type>asList(),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void enableParallel(TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_ENABLEPARALLEL,
                        Arrays.<Type>asList(),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForEnableParallel() {
        final Function function =
                new Function(
                        FUNC_ENABLEPARALLEL,
                        Arrays.<Type>asList(),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public TransactionReceipt disableParallel() {
        final Function function =
                new Function(
                        FUNC_DISABLEPARALLEL,
                        Arrays.<Type>asList(),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void disableParallel(TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_DISABLEPARALLEL,
                        Arrays.<Type>asList(),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForDisableParallel() {
        final Function function =
                new Function(
                        FUNC_DISABLEPARALLEL,
                        Arrays.<Type>asList(),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public TransactionReceipt transferWithRevert(String from, String to, BigInteger num) {
        final Function function =
                new Function(
                        FUNC_TRANSFERWITHREVERT,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(from),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(to),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(num)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void transferWithRevert(
            String from, String to, BigInteger num, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_TRANSFERWITHREVERT,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(from),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(to),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(num)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForTransferWithRevert(
            String from, String to, BigInteger num) {
        final Function function =
                new Function(
                        FUNC_TRANSFERWITHREVERT,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(from),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(to),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(num)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple3<String, String, BigInteger> getTransferWithRevertInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_TRANSFERWITHREVERT,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Uint256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<String, String, BigInteger>(
                (String) results.get(0).getValue(),
                (String) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue());
    }

    public static ParallelOk load(
            String contractAddress, Client client, CryptoInterface credential) {
        return new ParallelOk(contractAddress, client, credential);
    }

    public static ParallelOk deploy(Client client, CryptoInterface credential)
            throws ContractException {
        return deploy(ParallelOk.class, client, credential, getBinary(credential), "");
    }
}
