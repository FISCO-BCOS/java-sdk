package org.fisco.bcos.sdk.v3.transaction.manager.Transactionv2;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionBuilderV2JniObj;
import org.fisco.bcos.sdk.jni.utilities.tx.TxPair;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.model.TransactionAttribute;
import org.fisco.bcos.sdk.v3.client.protocol.request.Transaction;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosTransactionReceipt;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.RespCallback;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.ContractGasProvider;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.DefaultGasProvider;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.EIP1559Struct;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default transaction manager:
 *
 * <p>use default jni sign method use default client key pair to sign tx
 */
public class DefaultTransactionManager extends TransactionManager {

    private ContractGasProvider defaultGasProvider = new DefaultGasProvider();
    private static Logger logger = LoggerFactory.getLogger(DefaultTransactionManager.class);

    protected DefaultTransactionManager(Client client) {
        super(client);
    }

    @Override
    protected ContractGasProvider getGasProvider() {
        return defaultGasProvider;
    }

    @Override
    protected void steGasProvider(ContractGasProvider gasProvider) {
        defaultGasProvider = gasProvider;
    }

    /**
     * Send tx with abi field
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     *     contract
     * @param constructor if you deploy contract, should set to be true
     * @return receipt
     */
    @Override
    protected TransactionReceipt sendTransaction(
            String to, String data, BigInteger value, String abi, boolean constructor)
            throws JniException {
        String strippedData = Hex.trimPrefix(data);
        String methodSignature = strippedData.length() < 8 ? "" : strippedData.substring(0, 8);
        return sendTransaction(
                to,
                data,
                value,
                getGasProvider().getGasPrice(methodSignature),
                getGasProvider().getGasPrice(methodSignature),
                client.getBlockLimit(),
                abi,
                constructor);
    }

    /**
     * Send tx with gasPrice and gasLimit fields
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param gasPrice price of gas
     * @param gasLimit use limit of gas
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     *     contract
     * @param constructor if you deploy contract, should set to be true
     * @return receipt
     */
    @Override
    protected TransactionReceipt sendTransaction(
            String to,
            String data,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            String abi,
            boolean constructor)
            throws JniException {
        return sendTransaction(
                to, data, value, gasPrice, gasLimit, client.getBlockLimit(), abi, constructor);
    }

    /**
     * Send tx with gasPrice and gasLimit fields
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param gasPrice price of gas
     * @param gasLimit use limit of gas
     * @param blockLimit block limit
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     *     contract
     * @param constructor if you deploy contract, should set to be true
     * @return receipt
     */
    @Override
    protected TransactionReceipt sendTransaction(
            String to,
            String data,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            BigInteger blockLimit,
            String abi,
            boolean constructor)
            throws JniException {
        int transactionAttribute;
        if (client.isWASM()) {
            transactionAttribute = TransactionAttribute.LIQUID_SCALE_CODEC;
            if (constructor) {
                transactionAttribute |= TransactionAttribute.LIQUID_CREATE;
            }
        } else {
            transactionAttribute = TransactionAttribute.EVM_ABI_CODEC;
        }
        TxPair txPair =
                TransactionBuilderV2JniObj.createSignedTransactionWithFullFields(
                        client.getCryptoSuite().getCryptoKeyPair().getJniKeyPair(),
                        client.getGroup(),
                        client.getChainId(),
                        to,
                        data,
                        abi,
                        blockLimit.longValue(),
                        value.toString(16),
                        gasPrice.toString(16),
                        gasLimit.longValue(),
                        transactionAttribute,
                        client.getExtraData());

        BcosTransactionReceipt bcosTransactionReceipt =
                client.sendTransaction(txPair.getSignedTx(), false);
        return bcosTransactionReceipt.getTransactionReceipt();
    }

    /**
     * Send tx with abi field asynchronously
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     *     contract
     * @param constructor if you deploy contract, should set to be true
     * @param callback callback function
     * @return receipt
     */
    @Override
    protected String asyncSendTransaction(
            String to,
            String data,
            BigInteger value,
            String abi,
            boolean constructor,
            TransactionCallback callback)
            throws JniException {
        String strippedData = Hex.trimPrefix(data);
        String methodSignature = strippedData.length() < 8 ? "" : strippedData.substring(0, 8);
        return asyncSendTransaction(
                to,
                data,
                value,
                getGasProvider().getGasPrice(methodSignature),
                getGasProvider().getGasPrice(methodSignature),
                client.getBlockLimit(),
                abi,
                constructor,
                callback);
    }

    /**
     * Send tx with gasPrice and gasLimit fields asynchronously
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param gasPrice price of gas
     * @param gasLimit use limit of gas
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     *     contract
     * @param constructor if you deploy contract, should set to be true
     * @param callback callback function
     * @return receipt
     */
    @Override
    protected String asyncSendTransaction(
            String to,
            String data,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            String abi,
            boolean constructor,
            TransactionCallback callback)
            throws JniException {
        return asyncSendTransaction(
                to,
                data,
                value,
                gasPrice,
                gasLimit,
                client.getBlockLimit(),
                abi,
                constructor,
                callback);
    }

    /**
     * Send tx with gasPrice and gasLimit fields asynchronously
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param gasPrice price of gas
     * @param gasLimit use limit of gas
     * @param blockLimit block limit
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     *     contract
     * @param constructor if you deploy contract, should set to be true
     * @param callback callback function
     * @return receipt
     */
    @Override
    protected String asyncSendTransaction(
            String to,
            String data,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            BigInteger blockLimit,
            String abi,
            boolean constructor,
            TransactionCallback callback)
            throws JniException {
        int transactionAttribute;
        if (client.isWASM()) {
            transactionAttribute = TransactionAttribute.LIQUID_SCALE_CODEC;
            if (constructor) {
                transactionAttribute |= TransactionAttribute.LIQUID_CREATE;
            }
        } else {
            transactionAttribute = TransactionAttribute.EVM_ABI_CODEC;
        }
        TxPair txPair =
                TransactionBuilderV2JniObj.createSignedTransactionWithFullFields(
                        client.getCryptoSuite().getCryptoKeyPair().getJniKeyPair(),
                        client.getGroup(),
                        client.getChainId(),
                        to,
                        data,
                        abi,
                        blockLimit.longValue(),
                        value.toString(16),
                        gasPrice.toString(16),
                        gasLimit.longValue(),
                        transactionAttribute,
                        client.getExtraData());
        client.sendTransactionAsync(txPair.getSignedTx(), false, callback);
        return txPair.getTxHash();
    }

    /**
     * Send tx with EIP1559
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param eip1559Struct EIP1559 transaction payload
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     *     contract
     * @param constructor if you deploy contract, should set to be true
     * @return receipt
     */
    @Override
    protected TransactionReceipt sendTransactionEIP1559(
            String to,
            String data,
            BigInteger value,
            EIP1559Struct eip1559Struct,
            String abi,
            boolean constructor)
            throws JniException {
        return sendTransactionEIP1559(
                to, data, value, eip1559Struct, client.getBlockLimit(), abi, constructor);
    }

    /**
     * Send tx with EIP1559
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param eip1559Struct EIP1559 transaction payload
     * @param blockLimit block limit
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     * @param constructor if you deploy contract, should set to be true
     * @return receipt
     */
    @Override
    protected TransactionReceipt sendTransactionEIP1559(
            String to,
            String data,
            BigInteger value,
            EIP1559Struct eip1559Struct,
            BigInteger blockLimit,
            String abi,
            boolean constructor)
            throws JniException {
        int transactionAttribute;
        if (client.isWASM()) {
            transactionAttribute = TransactionAttribute.LIQUID_SCALE_CODEC;
            if (constructor) {
                transactionAttribute |= TransactionAttribute.LIQUID_CREATE;
            }
        } else {
            transactionAttribute = TransactionAttribute.EVM_ABI_CODEC;
        }
        TxPair txPair =
                TransactionBuilderV2JniObj.createSignedEIP1559TransactionWithFullFields(
                        client.getCryptoSuite().getCryptoKeyPair().getJniKeyPair(),
                        client.getGroup(),
                        client.getChainId(),
                        to,
                        data,
                        abi,
                        blockLimit.longValue(),
                        value.toString(16),
                        eip1559Struct.getMaxFeePerGas().toString(16),
                        eip1559Struct.getMaxPriorityFeePerGas().toString(16),
                        eip1559Struct.getGasLimit().longValue(),
                        transactionAttribute,
                        client.getExtraData());
        return client.sendTransaction(txPair.getSignedTx(), false).getTransactionReceipt();
    }

    /**
     * Send tx with EIP1559 asynchronously
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param eip1559Struct EIP1559 transaction payload
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     *     contract
     * @param constructor if you deploy contract, should set to be true
     * @param callback callback function
     * @return receipt
     */
    @Override
    protected String asyncSendTransactionEIP1559(
            String to,
            String data,
            BigInteger value,
            EIP1559Struct eip1559Struct,
            String abi,
            boolean constructor,
            TransactionCallback callback)
            throws JniException {
        return asyncSendTransactionEIP1559(
                to, data, value, eip1559Struct, client.getBlockLimit(), abi, constructor, callback);
    }

    /**
     * Send tx with EIP1559 asynchronously
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param eip1559Struct EIP1559 transaction payload
     * @param blockLimit block limit
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     * @param constructor if you deploy contract, should set to be true
     * @param callback callback function
     * @return receipt
     */
    @Override
    protected String asyncSendTransactionEIP1559(
            String to,
            String data,
            BigInteger value,
            EIP1559Struct eip1559Struct,
            BigInteger blockLimit,
            String abi,
            boolean constructor,
            TransactionCallback callback)
            throws JniException {
        int transactionAttribute;
        if (client.isWASM()) {
            transactionAttribute = TransactionAttribute.LIQUID_SCALE_CODEC;
            if (constructor) {
                transactionAttribute |= TransactionAttribute.LIQUID_CREATE;
            }
        } else {
            transactionAttribute = TransactionAttribute.EVM_ABI_CODEC;
        }
        TxPair txPair =
                TransactionBuilderV2JniObj.createSignedEIP1559TransactionWithFullFields(
                        client.getCryptoSuite().getCryptoKeyPair().getJniKeyPair(),
                        client.getGroup(),
                        client.getChainId(),
                        to,
                        data,
                        abi,
                        blockLimit.longValue(),
                        value.toString(16),
                        eip1559Struct.getMaxFeePerGas().toString(16),
                        eip1559Struct.getMaxPriorityFeePerGas().toString(16),
                        eip1559Struct.getGasLimit().longValue(),
                        transactionAttribute,
                        client.getExtraData());
        client.sendTransactionAsync(txPair.getSignedTx(), false, callback);
        return txPair.getTxHash();
    }

    /**
     * Send call
     *
     * @param to to address
     * @param data input data
     * @return call result
     */
    @Override
    protected Call sendCall(String to, String data) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write(Hex.trimPrefix(to).getBytes());
            outputStream.write(Hex.decode(data));
            byte[] hash = client.getCryptoSuite().hash(outputStream.toByteArray());
            SignatureResult signature =
                    client.getCryptoSuite().sign(hash, client.getCryptoSuite().getCryptoKeyPair());
            return client.call(
                    new Transaction("", to, Hex.decode(data)), Hex.toHexString(signature.encode()));
        } catch (Exception e) {
            logger.error("Send call failed, error message: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Send call with signature of call data
     *
     * @param to to address
     * @param data input data
     * @param signature signature of call data
     */
    @Override
    protected Call sendCall(String to, String data, String signature) {
        return client.call(new Transaction("", to, Hex.decode(data)), signature);
    }

    /**
     * Send call asynchronously
     *
     * @param to to address
     * @param data input data
     * @param callback callback function
     */
    @Override
    protected void asyncSendCall(String to, String data, RespCallback<Call> callback) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write(Hex.trimPrefix(to).getBytes());
            outputStream.write(Hex.decode(data));
            byte[] hash = client.getCryptoSuite().hash(outputStream.toByteArray());
            SignatureResult signature =
                    client.getCryptoSuite().sign(hash, client.getCryptoSuite().getCryptoKeyPair());
            client.callAsync(
                    new Transaction("", to, Hex.decode(data)),
                    Hex.toHexString(signature.encode()),
                    callback);
        } catch (Exception e) {
            logger.error("Send call failed, error message: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Send call asynchronously with signature of call data
     *
     * @param to to address
     * @param data input data
     * @param signature signature of call data
     * @param callback callback function
     */
    @Override
    protected void asyncSendCall(
            String to, String data, String signature, RespCallback<Call> callback) {
        client.callAsync(new Transaction("", to, Hex.decode(data)), signature, callback);
    }
}
