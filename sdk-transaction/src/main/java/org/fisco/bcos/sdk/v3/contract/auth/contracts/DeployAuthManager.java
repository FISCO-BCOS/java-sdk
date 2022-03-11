package org.fisco.bcos.sdk.v3.contract.auth.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.utils.StringUtils;

@SuppressWarnings("unchecked")
public class DeployAuthManager extends Contract {
    public static final String[] BINARY_ARRAY = {
        "608060405234801561001057600080fd5b50604051610bc8380380610bc88339818101604052602081101561003357600080fd5b8101908080519060200190929190505050336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550610093816100b460201b60201c565b60008060146101000a81548160ff021916908360ff1602179055505061021f565b6100c33361017860201b60201c565b610135576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600b8152602001807f4f6e6c79206f776e65722100000000000000000000000000000000000000000081525060200191505060405180910390fd5b806000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b60003073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1614156101b7576001905061021a565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff161415610215576001905061021a565b600090505b919050565b61099a8061022e6000396000f3fe608060405234801561001057600080fd5b50600436106100885760003560e01c8063b2bdfa7b1161005b578063b2bdfa7b146101b5578063bb0aa40c146101ff578063cd2ba05514610230578063cd5d21181461025457610088565b806313af40351461008d57806356bd7084146100d15780636154809914610115578063630577e514610159575b600080fd5b6100cf600480360360208110156100a357600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506102b0565b005b610113600480360360208110156100e757600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061036e565b005b6101576004803603602081101561012b57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506104d9565b005b61019b6004803603602081101561016f57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610643565b604051808215151515815260200191505060405180910390f35b6101bd610765565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b61022e6004803603602081101561021557600080fd5b81019080803560ff16906020019092919050505061078a565b005b6102386108aa565b604051808260ff1660ff16815260200191505060405180910390f35b6102966004803603602081101561026a57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506108bd565b604051808215151515815260200191505060405180910390f35b6102b9336108bd565b61032b576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600b8152602001807f4f6e6c79206f776e65722100000000000000000000000000000000000000000081525060200191505060405180910390fd5b806000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b610377336108bd565b6103e9576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600b8152602001807f4f6e6c79206f776e65722100000000000000000000000000000000000000000081525060200191505060405180910390fd5b6001600060149054906101000a900460ff1660ff161415610461576000600160008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055506104d6565b6002600060149054906101000a900460ff1660ff1614156104d5576001600260008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055505b5b50565b6104e2336108bd565b610554576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600b8152602001807f4f6e6c79206f776e65722100000000000000000000000000000000000000000081525060200191505060405180910390fd5b6001600060149054906101000a900460ff1660ff1614156105cb5760018060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff021916908315150217905550610640565b6002600060149054906101000a900460ff1660ff16141561063f576000600260008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055505b5b50565b600080600060149054906101000a900460ff1660ff1614156106685760019050610760565b6001600060149054906101000a900460ff1660ff161480156106d35750600160008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff165b156106e15760019050610760565b6002600060149054906101000a900460ff1660ff1614801561074d5750600260008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16155b1561075b5760019050610760565b600090505b919050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b610793336108bd565b610805576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600b8152602001807f4f6e6c79206f776e65722100000000000000000000000000000000000000000081525060200191505060405180910390fd5b60018160ff16148061081a575060028160ff16145b61088c576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260208152602001807f6465706c6f7920617574682074797065206d7573742062652031206f7220322e81525060200191505060405180910390fd5b80600060146101000a81548160ff021916908360ff16021790555050565b600060149054906101000a900460ff1681565b60003073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1614156108fc576001905061095f565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff16141561095a576001905061095f565b600090505b91905056fea2646970667358221220a4b87c591d7f6b772a088ec8fe5f8710b81ff32107aa270daf51a5660515bc2e64736f6c634300060a0033"
    };

    public static final String BINARY = StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {
        "608060405234801561001057600080fd5b50604051610bc8380380610bc88339818101604052602081101561003357600080fd5b8101908080519060200190929190505050336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550610093816100b460201b60201c565b60008060146101000a81548160ff021916908360ff1602179055505061021f565b6100c33361017860201b60201c565b610135576040517fc703cb1200000000000000000000000000000000000000000000000000000000815260040180806020018281038252600b8152602001807f4f6e6c79206f776e65722100000000000000000000000000000000000000000081525060200191505060405180910390fd5b806000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b60003073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1614156101b7576001905061021a565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff161415610215576001905061021a565b600090505b919050565b61099a8061022e6000396000f3fe608060405234801561001057600080fd5b50600436106100885760003560e01c8063291fc90d1161005b578063291fc90d146101a35780636e0376d4146101c7578063b0ca889b14610223578063c59065fb1461025457610088565b8063026a22fd1461008d57806305282c70146100d15780631a2052511461011557806328e9148914610159575b600080fd5b6100cf600480360360208110156100a357600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506102b0565b005b610113600480360360208110156100e757600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061041a565b005b6101576004803603602081101561012b57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506104d8565b005b610161610643565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b6101ab610668565b604051808260ff1660ff16815260200191505060405180910390f35b610209600480360360208110156101dd57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061067b565b604051808215151515815260200191505060405180910390f35b6102526004803603602081101561023957600080fd5b81019080803560ff169060200190929190505050610722565b005b6102966004803603602081101561026a57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610842565b604051808215151515815260200191505060405180910390f35b6102b93361067b565b61032b576040517fc703cb1200000000000000000000000000000000000000000000000000000000815260040180806020018281038252600b8152602001807f4f6e6c79206f776e65722100000000000000000000000000000000000000000081525060200191505060405180910390fd5b6001600060149054906101000a900460ff1660ff1614156103a25760018060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff021916908315150217905550610417565b6002600060149054906101000a900460ff1660ff161415610416576000600260008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055505b5b50565b6104233361067b565b610495576040517fc703cb1200000000000000000000000000000000000000000000000000000000815260040180806020018281038252600b8152602001807f4f6e6c79206f776e65722100000000000000000000000000000000000000000081525060200191505060405180910390fd5b806000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b6104e13361067b565b610553576040517fc703cb1200000000000000000000000000000000000000000000000000000000815260040180806020018281038252600b8152602001807f4f6e6c79206f776e65722100000000000000000000000000000000000000000081525060200191505060405180910390fd5b6001600060149054906101000a900460ff1660ff1614156105cb576000600160008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff021916908315150217905550610640565b6002600060149054906101000a900460ff1660ff16141561063f576001600260008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055505b5b50565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600060149054906101000a900460ff1681565b60003073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1614156106ba576001905061071d565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff161415610718576001905061071d565b600090505b919050565b61072b3361067b565b61079d576040517fc703cb1200000000000000000000000000000000000000000000000000000000815260040180806020018281038252600b8152602001807f4f6e6c79206f776e65722100000000000000000000000000000000000000000081525060200191505060405180910390fd5b60018160ff1614806107b2575060028160ff16145b610824576040517fc703cb120000000000000000000000000000000000000000000000000000000081526004018080602001828103825260208152602001807f6465706c6f7920617574682074797065206d7573742062652031206f7220322e81525060200191505060405180910390fd5b80600060146101000a81548160ff021916908360ff16021790555050565b600080600060149054906101000a900460ff1660ff161415610867576001905061095f565b6001600060149054906101000a900460ff1660ff161480156108d25750600160008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff165b156108e0576001905061095f565b6002600060149054906101000a900460ff1660ff1614801561094c5750600260008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16155b1561095a576001905061095f565b600090505b91905056fea2646970667358221220b165933a543bba7f85a68ee9e622f2f9f2a9bd22c7c21da9702e88200e6b5d5c64736f6c634300060a0033"
    };

    public static final String SM_BINARY = StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"inputs\":[{\"internalType\":\"address\",\"name\":\"owner\",\"type\":\"address\"}],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"inputs\":[],\"name\":\"_deployAuthType\",\"outputs\":[{\"internalType\":\"uint8\",\"name\":\"\",\"type\":\"uint8\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"_owner\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"src\",\"type\":\"address\"}],\"name\":\"auth\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"closeDeployAuth\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"hasDeployAuth\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"openDeployAuth\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"uint8\",\"name\":\"deployAuthType\",\"type\":\"uint8\"}],\"name\":\"setDeployAuthType\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"owner\",\"type\":\"address\"}],\"name\":\"setOwner\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC__DEPLOYAUTHTYPE = "_deployAuthType";

    public static final String FUNC__OWNER = "_owner";

    public static final String FUNC_AUTH = "auth";

    public static final String FUNC_CLOSEDEPLOYAUTH = "closeDeployAuth";

    public static final String FUNC_HASDEPLOYAUTH = "hasDeployAuth";

    public static final String FUNC_OPENDEPLOYAUTH = "openDeployAuth";

    public static final String FUNC_SETDEPLOYAUTHTYPE = "setDeployAuthType";

    public static final String FUNC_SETOWNER = "setOwner";

    protected DeployAuthManager(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public BigInteger _deployAuthType() throws ContractException {
        final Function function =
                new Function(
                        FUNC__DEPLOYAUTHTYPE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public String _owner() throws ContractException {
        final Function function =
                new Function(
                        FUNC__OWNER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public Boolean auth(String src) throws ContractException {
        final Function function =
                new Function(
                        FUNC_AUTH,
                        Arrays.<Type>asList(new Address(src)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public TransactionReceipt closeDeployAuth(String account) {
        final Function function =
                new Function(
                        FUNC_CLOSEDEPLOYAUTH,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void closeDeployAuth(String account, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_CLOSEDEPLOYAUTH,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForCloseDeployAuth(String account) {
        final Function function =
                new Function(
                        FUNC_CLOSEDEPLOYAUTH,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<String> getCloseDeployAuthInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_CLOSEDEPLOYAUTH,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public Boolean hasDeployAuth(String account) throws ContractException {
        final Function function =
                new Function(
                        FUNC_HASDEPLOYAUTH,
                        Arrays.<Type>asList(new Address(account)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public TransactionReceipt openDeployAuth(String account) {
        final Function function =
                new Function(
                        FUNC_OPENDEPLOYAUTH,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void openDeployAuth(String account, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_OPENDEPLOYAUTH,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForOpenDeployAuth(String account) {
        final Function function =
                new Function(
                        FUNC_OPENDEPLOYAUTH,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<String> getOpenDeployAuthInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_OPENDEPLOYAUTH,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public TransactionReceipt setDeployAuthType(BigInteger deployAuthType) {
        final Function function =
                new Function(
                        FUNC_SETDEPLOYAUTHTYPE,
                        Arrays.<Type>asList(new Uint8(deployAuthType)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void setDeployAuthType(BigInteger deployAuthType, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_SETDEPLOYAUTHTYPE,
                        Arrays.<Type>asList(new Uint8(deployAuthType)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForSetDeployAuthType(BigInteger deployAuthType) {
        final Function function =
                new Function(
                        FUNC_SETDEPLOYAUTHTYPE,
                        Arrays.<Type>asList(new Uint8(deployAuthType)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<BigInteger> getSetDeployAuthTypeInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_SETDEPLOYAUTHTYPE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt setOwner(String owner) {
        final Function function =
                new Function(
                        FUNC_SETOWNER,
                        Arrays.<Type>asList(new Address(owner)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void setOwner(String owner, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_SETOWNER,
                        Arrays.<Type>asList(new Address(owner)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForSetOwner(String owner) {
        final Function function =
                new Function(
                        FUNC_SETOWNER,
                        Arrays.<Type>asList(new Address(owner)),
                        Collections.<TypeReference<?>>emptyList());
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

    public static DeployAuthManager load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new DeployAuthManager(contractAddress, client, credential);
    }

    public static DeployAuthManager deploy(Client client, CryptoKeyPair credential, String owner)
            throws ContractException {
        byte[] encodedConstructor =
                FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(owner)));
        return deploy(
                DeployAuthManager.class,
                client,
                credential,
                getBinary(client.getCryptoSuite()),
                null,
                encodedConstructor,
                null);
    }
}
