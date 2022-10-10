package org.fisco.bcos.sdk.v3.contract.auth.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class AccountManager extends Contract {

    public static final String[] ABI_ARRAY = {
        "[{\"inputs\":[{\"internalType\":\"address\",\"name\":\"addr\",\"type\":\"address\"}],\"name\":\"getAccountStatus\",\"outputs\":[{\"internalType\":\"enum AccessStatus\",\"name\":\"\",\"type\":\"uint8\"}],\"selector\":[4249854042,2753454540],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"addr\",\"type\":\"address\"},{\"internalType\":\"enum AccessStatus\",\"name\":\"status\",\"type\":\"uint8\"}],\"name\":\"setAccountStatus\",\"outputs\":[{\"internalType\":\"int32\",\"name\":\"\",\"type\":\"int32\"}],\"selector\":[181579937,3980545228],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_GETACCOUNTSTATUS = "getAccountStatus";

    public static final String FUNC_SETACCOUNTSTATUS = "setAccountStatus";

    protected AccountManager(String contractAddress, Client client, CryptoKeyPair credential) {
        super("", contractAddress, client, credential);
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

    public static AccountManager load(Client client, CryptoKeyPair credential) {
        return new AccountManager(
                client.isWASM()
                        ? PrecompiledAddress.ACCOUNT_MANAGER_PRECOMPILED_NAME
                        : PrecompiledAddress.ACCOUNT_MANAGER_ADDRESS,
                client,
                credential);
    }
}
