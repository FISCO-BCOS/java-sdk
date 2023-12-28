package org.fisco.bcos.sdk.v3.transaction.manager.transactionv2;

import java.util.Objects;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.dto.BasicDeployRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.dto.BasicRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.dto.DeployTransactionRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.dto.DeployTransactionRequestWithStringParams;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.dto.TransactionRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.dto.TransactionRequestWithStringParams;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.v3.utils.Hex;

public class AssembleEIP1559TransactionService extends AssembleTransactionService {

    AssembleEIP1559TransactionService(Client client) {
        super(client);
    }

    public TransactionResponse sendEIP1559Transaction(BasicRequest request)
            throws ContractCodecException, JniException {
        if (!request.isTransactionEssentialSatisfy()) {
            throw new ContractCodecException("Request is not satisfy, please check.");
        }
        byte[] encodeMethod = null;
        if (request instanceof TransactionRequest) {
            encodeMethod =
                    contractCodec.encodeMethod(
                            request.getAbi(),
                            request.getMethod(),
                            ((TransactionRequest) request).getParams());
        } else if (request instanceof TransactionRequestWithStringParams) {
            encodeMethod =
                    contractCodec.encodeMethodFromString(
                            request.getAbi(),
                            request.getMethod(),
                            ((TransactionRequestWithStringParams) request).getStringParams());
        } else {
            throw new ContractCodecException("Request type error, please check.");
        }

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

    public TransactionResponse deployContractEIP1559(BasicDeployRequest request)
            throws ContractCodecException, JniException {
        if (!request.isTransactionEssentialSatisfy()) {
            throw new ContractCodecException("DeployRequest is not satisfy, please check.");
        }

        byte[] encodeConstructor = null;
        if (request instanceof DeployTransactionRequest) {
            encodeConstructor =
                    contractCodec.encodeConstructor(
                            request.getAbi(),
                            request.getBin(),
                            ((DeployTransactionRequest) request).getParams());
        } else if (request instanceof DeployTransactionRequestWithStringParams) {
            encodeConstructor =
                    contractCodec.encodeConstructorFromString(
                            request.getAbi(),
                            request.getBin(),
                            ((DeployTransactionRequestWithStringParams) request).getStringParams());
        } else {
            throw new ContractCodecException("DeployRequest type error, please check.");
        }

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

    public String asyncSendEIP1559Transaction(BasicRequest request, TransactionCallback callback)
            throws ContractCodecException, JniException {
        if (!request.isTransactionEssentialSatisfy()) {
            throw new ContractCodecException("Request is not satisfy, please check.");
        }
        byte[] encodeMethod = null;
        if (request instanceof TransactionRequest) {
            encodeMethod =
                    contractCodec.encodeMethod(
                            request.getAbi(),
                            request.getMethod(),
                            ((TransactionRequest) request).getParams());
        } else if (request instanceof TransactionRequestWithStringParams) {
            encodeMethod =
                    contractCodec.encodeMethodFromString(
                            request.getAbi(),
                            request.getMethod(),
                            ((TransactionRequestWithStringParams) request).getStringParams());
        } else {
            throw new ContractCodecException("Request type error, please check.");
        }

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
            BasicDeployRequest request, TransactionCallback callback)
            throws ContractCodecException, JniException {
        if (!request.isTransactionEssentialSatisfy()) {
            throw new ContractCodecException("DeployRequest is not satisfy, please check.");
        }

        byte[] encodeConstructor = null;
        if (request instanceof DeployTransactionRequest) {
            encodeConstructor =
                    contractCodec.encodeConstructor(
                            request.getAbi(),
                            request.getBin(),
                            ((DeployTransactionRequest) request).getParams());
        } else if (request instanceof DeployTransactionRequestWithStringParams) {
            encodeConstructor =
                    contractCodec.encodeConstructorFromString(
                            request.getAbi(),
                            request.getBin(),
                            ((DeployTransactionRequestWithStringParams) request).getStringParams());
        } else {
            throw new ContractCodecException("DeployRequest type error, please check.");
        }

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
