package org.fisco.bcos.sdk.contract.auth.contracts;

import java.util.Arrays;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.codec.datatypes.Bool;
import org.fisco.bcos.sdk.codec.datatypes.Function;
import org.fisco.bcos.sdk.codec.datatypes.Type;
import org.fisco.bcos.sdk.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.contract.Contract;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class ContractInterceptor extends Contract {
    public static final String[] BINARY_ARRAY = {
        "608060405234801561001057600080fd5b5061045a806100206000396000f3fe608060405234801561001057600080fd5b50600436106100415760003560e01c806352d48562146100465780636bedbe88146100eb5780639ed9331814610190575b600080fd5b6100d16004803603606081101561005c57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080357bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19169060200190929190803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506101ec565b604051808215151515815260200191505060405180910390f35b6101766004803603606081101561010157600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080357bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610202565b604051808215151515815260200191505060405180910390f35b6101d2600480360360208110156101a657600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610218565b604051808215151515815260200191505060405180910390f35b60006101f98484846102e0565b90509392505050565b600061020f8484846102e0565b90509392505050565b60008061100590508073ffffffffffffffffffffffffffffffffffffffff1663630577e5846040518263ffffffff1660e01b8152600401808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060206040518083038186803b15801561029d57600080fd5b505afa1580156102b1573d6000803e3d6000fd5b505050506040513d60208110156102c757600080fd5b8101908080519060200190929190505050915050919050565b60008061100590508073ffffffffffffffffffffffffffffffffffffffff1663d8662aa48686866040518463ffffffff1660e01b8152600401808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001837bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff191681526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001935050505060206040518083038186803b1580156103df57600080fd5b505afa1580156103f3573d6000803e3d6000fd5b505050506040513d602081101561040957600080fd5b8101908080519060200190929190505050915050939250505056fea26469706673582212204bc4858487aef75f434b59f43af97ee8d6cc6c870deeaf8f0720e69be9c79a1064736f6c634300060a0033"
    };

    public static final String BINARY =
            org.fisco.bcos.sdk.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {
        "608060405234801561001057600080fd5b5061045a806100206000396000f3fe608060405234801561001057600080fd5b50600436106100415760003560e01c806324f8d036146100465780634ba0a39d146100eb5780636ebc0c5014610190575b600080fd5b6100d16004803603606081101561005c57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080357bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19169060200190929190803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506101ec565b604051808215151515815260200191505060405180910390f35b6101766004803603606081101561010157600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080357bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610202565b604051808215151515815260200191505060405180910390f35b6101d2600480360360208110156101a657600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610218565b604051808215151515815260200191505060405180910390f35b60006101f98484846102e0565b90509392505050565b600061020f8484846102e0565b90509392505050565b60008061100590508073ffffffffffffffffffffffffffffffffffffffff1663c59065fb846040518263ffffffff1660e01b8152600401808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060206040518083038186803b15801561029d57600080fd5b505afa1580156102b1573d6000803e3d6000fd5b505050506040513d60208110156102c757600080fd5b8101908080519060200190929190505050915050919050565b60008061100590508073ffffffffffffffffffffffffffffffffffffffff1663d3f8058c8686866040518463ffffffff1660e01b8152600401808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001837bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff191681526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001935050505060206040518083038186803b1580156103df57600080fd5b505afa1580156103f3573d6000803e3d6000fd5b505050506040513d602081101561040957600080fd5b8101908080519060200190929190505050915050939250505056fea26469706673582212204ce95a88c9b830bdb3c6f99bd4fb1a848749c1423864a57412074580c8edad5064736f6c634300060a0033"
    };

    public static final String SM_BINARY =
            org.fisco.bcos.sdk.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"inputs\":[{\"internalType\":\"address\",\"name\":\"contractAddr\",\"type\":\"address\"},{\"internalType\":\"bytes4\",\"name\":\"methodId\",\"type\":\"bytes4\"},{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"call\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"create\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"contractAddr\",\"type\":\"address\"},{\"internalType\":\"bytes4\",\"name\":\"methodId\",\"type\":\"bytes4\"},{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"sendTransaction\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"stateMutability\":\"view\",\"type\":\"function\"}]"
    };

    public static final String ABI = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_CALL = "call";

    public static final String FUNC_CREATE = "create";

    public static final String FUNC_SENDTRANSACTION = "sendTransaction";

    protected ContractInterceptor(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public Boolean call(String contractAddr, byte[] methodId, String account)
            throws ContractException {
        final Function function =
                new Function(
                        FUNC_CALL,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.codec.datatypes.Address(contractAddr),
                                new org.fisco.bcos.sdk.codec.datatypes.generated.Bytes4(methodId),
                                new org.fisco.bcos.sdk.codec.datatypes.Address(account)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Boolean create(String account) throws ContractException {
        final Function function =
                new Function(
                        FUNC_CREATE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.codec.datatypes.Address(account)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public Boolean sendTransaction(String contractAddr, byte[] methodId, String account)
            throws ContractException {
        final Function function =
                new Function(
                        FUNC_SENDTRANSACTION,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.codec.datatypes.Address(contractAddr),
                                new org.fisco.bcos.sdk.codec.datatypes.generated.Bytes4(methodId),
                                new org.fisco.bcos.sdk.codec.datatypes.Address(account)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public static ContractInterceptor load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new ContractInterceptor(contractAddress, client, credential);
    }

    public static ContractInterceptor deploy(Client client, CryptoKeyPair credential)
            throws ContractException {
        return deploy(
                ContractInterceptor.class,
                client,
                credential,
                getBinary(client.getCryptoSuite()),
                null,
                null,
                null);
    }
}
