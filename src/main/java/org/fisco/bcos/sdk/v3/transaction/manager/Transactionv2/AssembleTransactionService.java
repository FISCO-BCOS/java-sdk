package org.fisco.bcos.sdk.v3.transaction.manager.Transactionv2;

import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.ContractCodec;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.EIP1559Struct;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;

/**
 * AssembleTransactionService
 *
 * <p>codec(abi, method, params) -> inputData sendTx(to, inputData) -> receipt decode(abi, method,
 * receipt.output, ) -> result
 */
public class AssembleTransactionService {
    private TransactionManager transactionManager;

    private final ContractCodec contractCodec;

    AssembleTransactionService(Client client) {
        this.contractCodec =
                new ContractCodec(client.getCryptoSuite().getHashImpl(), client.isWASM());
        transactionManager = new DefaultTransactionManager(client);
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public TransactionResponse sendTransaction(
            String abi, String method, List<Object> params, String to, BigInteger value) {
        return null;
    }

    public TransactionResponse sendTransaction(
            String abi,
            String method,
            List<Object> params,
            String to,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit) {
        return null;
    }

    public TransactionResponse sendTransactionWithStringParams(
            String abi,
            String method,
            List<String> params,
            String to,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit) {
        return null;
    }

    public TransactionResponse sendTransactionWithStringParams(
            String abi, String method, List<String> params, String to, BigInteger value) {
        return null;
    }

    public TransactionResponse deployContract(
            String abi, String bin, List<Object> params, BigInteger value) {
        return null;
    }

    public TransactionResponse deployContractWithStringParams(
            String abi, String bin, List<String> params, BigInteger value) {
        return null;
    }

    public TransactionResponse deployContract(
            String abi,
            String bin,
            List<Object> params,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit) {
        return null;
    }

    public TransactionResponse deployContractWithStringParams(
            String abi,
            String bin,
            List<String> params,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit) {
        return null;
    }

    public TransactionResponse sendEIP1559Transaction(
            String abi,
            String method,
            List<Object> params,
            String to,
            BigInteger value,
            EIP1559Struct eip1559Struct) {
        return null;
    }

    public TransactionResponse sendEIP1559TransactionWithStringParams(
            String abi,
            String method,
            List<String> params,
            String to,
            BigInteger value,
            EIP1559Struct eip1559Struct) {
        return null;
    }

    public TransactionResponse deployContractEIP1559(
            String abi,
            String method,
            List<Object> params,
            String to,
            BigInteger value,
            EIP1559Struct eip1559Struct) {
        return null;
    }

    public TransactionResponse deployContractEIP1559WithStringParams(
            String abi,
            String method,
            List<String> params,
            String to,
            BigInteger value,
            EIP1559Struct eip1559Struct) {
        return null;
    }

    public String asyncSendTransaction(
            String abi,
            String method,
            List<Object> params,
            String to,
            BigInteger value,
            TransactionCallback callback) {
        return null;
    }

    public String asyncSendTransactionWithStringParams(
            String abi,
            String method,
            List<String> params,
            String to,
            BigInteger value,
            TransactionCallback callback) {
        return null;
    }

    public String asyncSendTransaction(
            String abi,
            String method,
            List<Object> params,
            String to,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            TransactionCallback callback) {
        return null;
    }

    public String asyncSendTransactionWithStringParams(
            String abi,
            String method,
            List<String> params,
            String to,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            TransactionCallback callback) {
        return null;
    }

    public String asyncDeployContract(
            String abi,
            String bin,
            List<Object> params,
            BigInteger value,
            TransactionCallback callback) {
        return null;
    }

    public String asyncDeployContractWithStringParams(
            String abi,
            String bin,
            List<String> params,
            BigInteger value,
            TransactionCallback callback) {
        return null;
    }

    public String asyncDeployContract(
            String abi,
            String bin,
            List<Object> params,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            TransactionCallback callback) {
        return null;
    }

    public String asyncDeployContractWithStringParams(
            String abi,
            String bin,
            List<String> params,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            TransactionCallback callback) {
        return null;
    }

    public String asyncSendEIP1559Transaction(
            String abi,
            String method,
            List<Object> params,
            String to,
            BigInteger value,
            EIP1559Struct eip1559Struct,
            TransactionCallback callback) {
        return null;
    }

    public String asyncSendEIP1559TransactionWithStringParams(
            String abi,
            String method,
            List<String> params,
            String to,
            BigInteger value,
            EIP1559Struct eip1559Struct,
            TransactionCallback callback) {
        return null;
    }

    public String asyncDeployContractEIP1559(
            String abi,
            String bin,
            List<Object> params,
            String to,
            BigInteger value,
            EIP1559Struct eip1559Struct,
            TransactionCallback callback) {
        return null;
    }

    public String asyncDeployContractEIP1559WithStringParams(
            String abi,
            String bin,
            List<String> params,
            String to,
            BigInteger value,
            EIP1559Struct eip1559Struct,
            TransactionCallback callback) {
        return null;
    }
}
