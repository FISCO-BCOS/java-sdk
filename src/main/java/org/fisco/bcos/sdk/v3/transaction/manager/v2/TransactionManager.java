package org.fisco.bcos.sdk.v3.transaction.manager.v2;

import java.math.BigInteger;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.RespCallback;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;

public abstract class TransactionManager {

    public class EIP1559Struct {
        BigInteger maxFeePerGas;
        BigInteger maxPriorityFeePerGas;
        BigInteger gasLimit;

        public EIP1559Struct(
                BigInteger maxFeePerGas, BigInteger maxPriorityFeePerGas, BigInteger gasLimit) {
            this.maxFeePerGas = maxFeePerGas;
            this.maxPriorityFeePerGas = maxPriorityFeePerGas;
            this.gasLimit = gasLimit;
        }
    }

    protected Client client;

    public Client getClient() {
        return client;
    }

    protected TransactionManager(Client client) {
        this.client = client;
    }

    /**
     * Simple send tx
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @return receipt
     */
    protected TransactionReceipt sendTransaction(String to, String data, BigInteger value) {
        return sendTransaction(to, data, value, "", false);
    }

    /**
     * Send tx with abi field
     *
     * @param to to address
     * @param data input data
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     *     contract
     * @param value transfer value
     * @param constructor if you deploy contract, should set to be true
     * @return receipt
     */
    protected abstract TransactionReceipt sendTransaction(
            String to, String data, BigInteger value, String abi, boolean constructor);

    /**
     * Send tx with gasPrice and gasLimit fields
     *
     * @param to to address
     * @param data input data
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     *     contract
     * @param value transfer value
     * @param gasPrice price of gas
     * @param gasLimit use limit of gas
     * @param constructor if you deploy contract, should set to be true
     * @return receipt
     */
    protected abstract TransactionReceipt sendTransaction(
            String to,
            String data,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            String abi,
            boolean constructor);

    /**
     * Send tx with blockLimit and nonce fields
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param blockLimit block limit
     * @param nonce tx nonce, for avoiding tx replay attack
     * @return receipt
     */
    protected TransactionReceipt sendTransaction(
            String to, String data, BigInteger value, BigInteger blockLimit, BigInteger nonce) {
        return sendTransaction(to, data, value, blockLimit, nonce, "", false);
    }

    /**
     * Send tx with gasPrice and gasLimit fields
     *
     * @param to to address
     * @param data input data
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     *     contract
     * @param value transfer value
     * @param gasPrice price of gas
     * @param gasLimit use limit of gas
     * @param blockLimit block limit
     * @param nonce tx nonce, for avoiding tx replay attack
     * @param constructor if you deploy contract, should set to be true
     * @return receipt
     */
    protected abstract TransactionReceipt sendTransaction(
            String to,
            String data,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            BigInteger blockLimit,
            BigInteger nonce,
            String abi,
            boolean constructor);

    /**
     * Simple send tx asynchronously
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @return receipt
     */
    protected String asyncSendTransaction(
            String to, String data, BigInteger value, TransactionCallback callback) {
        return asyncSendTransaction(to, data, value, "", false, callback);
    }

    /**
     * Send tx with abi field asynchronously
     *
     * @param to to address
     * @param data input data
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     *     contract
     * @param value transfer value
     * @param constructor if you deploy contract, should set to be true
     * @return receipt
     */
    protected abstract String asyncSendTransaction(
            String to,
            String data,
            BigInteger value,
            String abi,
            boolean constructor,
            TransactionCallback callback);

    /**
     * Send tx with gasPrice and gasLimit fields asynchronously
     *
     * @param to to address
     * @param data input data
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     *     contract
     * @param value transfer value
     * @param gasPrice price of gas
     * @param gasLimit use limit of gas
     * @param constructor if you deploy contract, should set to be true
     * @return receipt
     */
    protected abstract String asyncSendTransaction(
            String to,
            String data,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            String abi,
            boolean constructor,
            TransactionCallback callback);

    /**
     * Send tx with blockLimit and nonce fields asynchronously
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param blockLimit block limit
     * @param nonce tx nonce, for avoiding tx replay attack
     * @param callback callback function
     * @return receipt
     */
    protected abstract String asyncSendTransaction(
            String to,
            String data,
            BigInteger value,
            BigInteger blockLimit,
            BigInteger nonce,
            TransactionCallback callback);

    /**
     * Send tx with gasPrice and gasLimit fields asynchronously
     *
     * @param to to address
     * @param data input data
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     *     contract
     * @param value transfer value
     * @param gasPrice price of gas
     * @param gasLimit use limit of gas
     * @param blockLimit block limit
     * @param nonce tx nonce, for avoiding tx replay attack
     * @param constructor if you deploy contract, should set to be true
     * @param callback callback function
     * @return receipt
     */
    protected abstract String asyncSendTransaction(
            String to,
            String data,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            BigInteger blockLimit,
            BigInteger nonce,
            String abi,
            boolean constructor,
            TransactionCallback callback);

    /**
     * Send tx with EIP1559
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param eip1559Struct EIP1559 transaction payload
     * @return receipt
     */
    protected TransactionReceipt sendTransactionEIP1559(
            String to, String data, BigInteger value, EIP1559Struct eip1559Struct) {
        return sendTransactionEIP1559(to, data, value, eip1559Struct, "", false);
    }

    /**
     * Send tx with EIP1559
     *
     * @param to to address
     * @param data input data
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     *     contract
     * @param value transfer value
     * @param eip1559Struct EIP1559 transaction payload
     * @param constructor if you deploy contract, should set to be true
     * @return receipt
     */
    protected abstract TransactionReceipt sendTransactionEIP1559(
            String to,
            String data,
            BigInteger value,
            EIP1559Struct eip1559Struct,
            String abi,
            boolean constructor);

    /**
     * Send tx with EIP1559
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param eip1559Struct EIP1559 transaction payload
     * @param blockLimit block limit
     * @param nonce tx nonce, for avoiding tx replay attack
     * @return receipt
     */
    protected TransactionReceipt sendTransactionEIP1559(
            String to,
            String data,
            BigInteger value,
            EIP1559Struct eip1559Struct,
            BigInteger blockLimit,
            BigInteger nonce) {
        return sendTransactionEIP1559(to, data, value, eip1559Struct, blockLimit, nonce, "", false);
    }

    /**
     * Send tx with EIP1559
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param eip1559Struct EIP1559 transaction payload
     * @param blockLimit block limit
     * @param nonce tx nonce, for avoiding tx replay attack
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     * @param constructor if you deploy contract, should set to be true
     * @return receipt
     */
    protected abstract TransactionReceipt sendTransactionEIP1559(
            String to,
            String data,
            BigInteger value,
            EIP1559Struct eip1559Struct,
            BigInteger blockLimit,
            BigInteger nonce,
            String abi,
            boolean constructor);

    /**
     * Send tx with EIP1559 asynchronously
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param eip1559Struct EIP1559 transaction payload
     * @param callback callback function
     * @return receipt
     */
    protected String asyncSendTransactionEIP1559(
            String to,
            String data,
            BigInteger value,
            EIP1559Struct eip1559Struct,
            TransactionCallback callback) {
        return asyncSendTransactionEIP1559(to, data, value, eip1559Struct, "", false, callback);
    }

    /**
     * Send tx with EIP1559 asynchronously
     *
     * @param to to address
     * @param data input data
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     *     contract
     * @param value transfer value
     * @param eip1559Struct EIP1559 transaction payload
     * @param constructor if you deploy contract, should set to be true
     * @param callback callback function
     * @return receipt
     */
    protected abstract String asyncSendTransactionEIP1559(
            String to,
            String data,
            BigInteger value,
            EIP1559Struct eip1559Struct,
            String abi,
            boolean constructor,
            TransactionCallback callback);

    /**
     * Send tx with EIP1559 asynchronously
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param eip1559Struct EIP1559 transaction payload
     * @param blockLimit block limit
     * @param nonce tx nonce, for avoiding tx replay attack
     * @param callback callback function
     * @return receipt
     */
    protected abstract String asyncSendTransactionEIP1559(
            String to,
            String data,
            BigInteger value,
            EIP1559Struct eip1559Struct,
            BigInteger blockLimit,
            BigInteger nonce,
            TransactionCallback callback);

    /**
     * Send tx with EIP1559 asynchronously
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param eip1559Struct EIP1559 transaction payload
     * @param blockLimit block limit
     * @param nonce tx nonce, for avoiding tx replay attack
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     * @param constructor if you deploy contract, should set to be true
     * @param callback callback function
     * @return receipt
     */
    protected abstract String asyncSendTransactionEIP1559(
            String to,
            String data,
            BigInteger value,
            EIP1559Struct eip1559Struct,
            BigInteger blockLimit,
            BigInteger nonce,
            String abi,
            boolean constructor,
            TransactionCallback callback);

    /**
     * Send call
     *
     * @param to to address
     * @param data input data
     * @return call result
     */
    protected abstract Call sendCall(String to, String data);

    /**
     * Send call with signature of call data
     *
     * @param to to address
     * @param data input data
     * @param signature signature of call data
     */
    protected abstract Call sendCall(String to, String data, String signature);

    /**
     * Send call asynchronously
     *
     * @param to to address
     * @param data input data
     * @param callback callback function
     */
    protected abstract void asyncSendCall(String to, String data, RespCallback<Call> callback);

    /**
     * Send call asynchronously with signature of call data
     *
     * @param to to address
     * @param data input data
     * @param signature signature of call data
     * @param callback callback function
     */
    protected abstract void asyncSendCall(
            String to, String data, String signature, RespCallback<Call> callback);
}
