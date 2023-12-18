package org.fisco.bcos.sdk.v3.transaction.manager.transactionv2;

import java.util.Objects;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.dto.DeployTransactionRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.dto.DeployTransactionRequestWithStringParams;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.dto.TransactionRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.dto.TransactionRequestWithStringParams;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.v3.utils.Hex;

/**
 * AssembleTransactionService
 *
 * <p>codec(abi, method, params) -> inputData sendTx(to, inputData) -> receipt decode(abi, method,
 * receipt.output, ) -> result
 */
public class AssembleEIP1559TransactionService extends AssembleTransactionService {

    AssembleEIP1559TransactionService(Client client) {
        super(client);
    }

    public TransactionResponse sendEIP1559Transaction(TransactionRequest request)
            throws ContractCodecException, JniException {
        byte[] encodeMethod =
                contractCodec.encodeMethod(
                        request.getAbi(), request.getMethod(), request.getParams());
        TransactionReceipt receipt =
                transactionManager.sendTransactionEIP1559(
                        request.getTo(),
                        Hex.toHexString(encodeMethod),
                        request.getValue(),
                        request.getEip1559Struct(),
                        request.getAbi(),
                        false);
        if (Objects.nonNull(receipt)
                && (Objects.isNull(receipt.getInput()) || receipt.getInput().isEmpty())) {
            receipt.setInput(Hex.toHexStringWithPrefix(encodeMethod));
        }
        return this.transactionDecoder.decodeReceiptWithValues(
                request.getAbi(), request.getMethod(), receipt);
    }

    public TransactionResponse sendEIP1559TransactionWithStringParams(
            TransactionRequestWithStringParams request)
            throws ContractCodecException, JniException {
        byte[] encodeMethod =
                contractCodec.encodeMethodFromString(
                        request.getAbi(), request.getMethod(), request.getStringParams());
        TransactionReceipt receipt =
                transactionManager.sendTransactionEIP1559(
                        request.getTo(),
                        Hex.toHexString(encodeMethod),
                        request.getValue(),
                        request.getEip1559Struct(),
                        request.getAbi(),
                        false);
        if (Objects.nonNull(receipt)
                && (Objects.isNull(receipt.getInput()) || receipt.getInput().isEmpty())) {
            receipt.setInput(Hex.toHexStringWithPrefix(encodeMethod));
        }
        return this.transactionDecoder.decodeReceiptWithValues(
                request.getAbi(), request.getMethod(), receipt);
    }

    public TransactionResponse deployContractEIP1559(DeployTransactionRequest request)
            throws ContractCodecException, JniException {
        byte[] encodeConstructor =
                contractCodec.encodeConstructor(
                        request.getAbi(), request.getBin(), request.getParams());
        TransactionReceipt receipt =
                transactionManager.sendTransactionEIP1559(
                        request.getTo(),
                        Hex.toHexString(encodeConstructor),
                        request.getValue(),
                        request.getEip1559Struct(),
                        request.getAbi(),
                        true);
        if (Objects.nonNull(receipt)
                && (Objects.isNull(receipt.getInput()) || receipt.getInput().isEmpty())) {
            receipt.setInput(Hex.toHexStringWithPrefix(encodeConstructor));
        }
        return this.transactionDecoder.decodeReceiptWithValues(request.getAbi(), "", receipt);
    }

    public TransactionResponse deployContractEIP1559WithStringParams(
            DeployTransactionRequestWithStringParams request)
            throws ContractCodecException, JniException {
        byte[] encodeConstructor =
                contractCodec.encodeConstructorFromString(
                        request.getAbi(), request.getBin(), request.getStringParams());
        TransactionReceipt receipt =
                transactionManager.sendTransactionEIP1559(
                        request.getTo(),
                        Hex.toHexString(encodeConstructor),
                        request.getValue(),
                        request.getEip1559Struct(),
                        request.getAbi(),
                        true);
        if (Objects.nonNull(receipt)
                && (Objects.isNull(receipt.getInput()) || receipt.getInput().isEmpty())) {
            receipt.setInput(Hex.toHexStringWithPrefix(encodeConstructor));
        }
        return this.transactionDecoder.decodeReceiptWithValues(request.getAbi(), "", receipt);
    }

    public String asyncSendEIP1559Transaction(
            TransactionRequest request, TransactionCallback callback)
            throws ContractCodecException, JniException {
        byte[] encodeMethod =
                contractCodec.encodeMethod(
                        request.getAbi(), request.getMethod(), request.getParams());
        return transactionManager.asyncSendTransactionEIP1559(
                request.getTo(),
                Hex.toHexString(encodeMethod),
                request.getValue(),
                request.getEip1559Struct(),
                request.getAbi(),
                false,
                callback);
    }

    public String asyncSendEIP1559TransactionWithStringParams(
            TransactionRequestWithStringParams request, TransactionCallback callback)
            throws ContractCodecException, JniException {
        byte[] encodeMethod =
                contractCodec.encodeMethodFromString(
                        request.getAbi(), request.getMethod(), request.getStringParams());
        return transactionManager.asyncSendTransactionEIP1559(
                request.getTo(),
                Hex.toHexString(encodeMethod),
                request.getValue(),
                request.getEip1559Struct(),
                request.getAbi(),
                false,
                callback);
    }

    public String asyncDeployContractEIP1559(
            DeployTransactionRequest request, TransactionCallback callback)
            throws ContractCodecException, JniException {
        byte[] encodeConstructor =
                contractCodec.encodeConstructor(
                        request.getAbi(), request.getBin(), request.getParams());
        return transactionManager.asyncSendTransactionEIP1559(
                request.getTo(),
                Hex.toHexString(encodeConstructor),
                request.getValue(),
                request.getEip1559Struct(),
                request.getAbi(),
                true,
                callback);
    }

    public String asyncDeployContractEIP1559WithStringParams(
            DeployTransactionRequestWithStringParams request, TransactionCallback callback)
            throws ContractCodecException, JniException {
        byte[] encodeConstructor =
                contractCodec.encodeConstructorFromString(
                        request.getAbi(), request.getBin(), request.getStringParams());
        return transactionManager.asyncSendTransactionEIP1559(
                request.getTo(),
                Hex.toHexString(encodeConstructor),
                request.getValue(),
                request.getEip1559Struct(),
                request.getAbi(),
                true,
                callback);
    }
}
