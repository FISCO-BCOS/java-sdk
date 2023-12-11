package org.fisco.bcos.sdk.v3.transaction.manager.transactionv2;

import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.tuple.Pair;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.codec.ContractCodec;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIObject;
import org.fisco.bcos.sdk.v3.model.Response;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.RespCallback;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.TransactionDecoderInterface;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.TransactionDecoderService;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.dto.DeployTransactionRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.dto.DeployTransactionRequestWithStringParams;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.dto.TransactionRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.dto.TransactionRequestWithStringParams;
import org.fisco.bcos.sdk.v3.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.v3.utils.Hex;

/**
 * AssembleTransactionService
 *
 * <p>codec(abi, method, params) -> inputData sendTx(to, inputData) -> receipt decode(abi, method,
 * receipt.output, ) -> result
 */
public class AssembleTransactionService {
    protected TransactionManager transactionManager;
    protected final TransactionDecoderInterface transactionDecoder;
    protected final ContractCodec contractCodec;
    protected final Client client;

    public AssembleTransactionService(Client client) {
        this.client = client;
        this.contractCodec =
                new ContractCodec(client.getCryptoSuite().getHashImpl(), client.isWASM());
        this.transactionManager = new DefaultTransactionManager(client);
        this.transactionDecoder =
                new TransactionDecoderService(
                        client.getCryptoSuite().getHashImpl(), client.isWASM());
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public TransactionResponse sendTransaction(TransactionRequest request)
            throws ContractCodecException, JniException {
        byte[] encodeMethod =
                contractCodec.encodeMethod(
                        request.getAbi(), request.getMethod(), request.getParams());
        TransactionReceipt receipt =
                transactionManager.sendTransaction(
                        request.getTo(),
                        Hex.toHexString(encodeMethod),
                        request.getValue(),
                        request.getGasPrice(),
                        request.getGasLimit(),
                        request.getAbi(),
                        false);
        if (Objects.nonNull(receipt)
                && (Objects.isNull(receipt.getInput()) || receipt.getInput().isEmpty())) {
            receipt.setInput(Hex.toHexStringWithPrefix(encodeMethod));
        }
        return this.transactionDecoder.decodeReceiptWithValues(
                request.getAbi(), request.getMethod(), receipt);
    }

    public TransactionResponse sendTransactionWithStringParams(
            TransactionRequestWithStringParams request)
            throws ContractCodecException, JniException {
        byte[] transactionData =
                contractCodec.encodeMethodFromString(
                        request.getAbi(), request.getMethod(), request.getStringParams());
        TransactionReceipt receipt =
                transactionManager.sendTransaction(
                        request.getTo(),
                        Hex.toHexString(transactionData),
                        request.getValue(),
                        request.getGasPrice(),
                        request.getGasLimit(),
                        request.getAbi(),
                        false);
        if (Objects.nonNull(receipt)
                && (Objects.isNull(receipt.getInput()) || receipt.getInput().isEmpty())) {
            receipt.setInput(Hex.toHexStringWithPrefix(transactionData));
        }
        return this.transactionDecoder.decodeReceiptWithValues(
                request.getAbi(), request.getMethod(), receipt);
    }

    public TransactionResponse deployContract(DeployTransactionRequest request)
            throws ContractCodecException, JniException {
        byte[] encodeConstructor =
                contractCodec.encodeConstructor(
                        request.getAbi(), request.getBin(), request.getParams());
        TransactionReceipt receipt =
                transactionManager.sendTransaction(
                        request.getTo(),
                        Hex.toHexString(encodeConstructor),
                        request.getValue(),
                        request.getGasPrice(),
                        request.getGasLimit(),
                        request.getAbi(),
                        true);
        if (Objects.nonNull(receipt)
                && (Objects.isNull(receipt.getInput()) || receipt.getInput().isEmpty())) {
            receipt.setInput(Hex.toHexStringWithPrefix(encodeConstructor));
        }
        return this.transactionDecoder.decodeReceiptWithoutValues(request.getAbi(), receipt);
    }

    public TransactionResponse deployContractWithStringParams(
            DeployTransactionRequestWithStringParams request)
            throws ContractCodecException, JniException {
        byte[] encodedConstructor =
                contractCodec.encodeConstructorFromString(
                        request.getAbi(), request.getBin(), request.getStringParams());
        TransactionReceipt receipt =
                transactionManager.sendTransaction(
                        request.getTo(),
                        Hex.toHexString(encodedConstructor),
                        request.getValue(),
                        request.getGasPrice(),
                        request.getGasLimit(),
                        request.getAbi(),
                        true);
        if (Objects.nonNull(receipt)
                && (Objects.isNull(receipt.getInput()) || receipt.getInput().isEmpty())) {
            receipt.setInput(Hex.toHexStringWithPrefix(encodedConstructor));
        }
        return this.transactionDecoder.decodeReceiptWithoutValues(request.getAbi(), receipt);
    }

    public String asyncSendTransaction(TransactionRequest request, TransactionCallback callback)
            throws ContractCodecException, JniException {
        byte[] encodeMethod =
                contractCodec.encodeMethod(
                        request.getAbi(), request.getMethod(), request.getParams());
        return transactionManager.asyncSendTransaction(
                request.getTo(),
                Hex.toHexString(encodeMethod),
                request.getValue(),
                request.getGasPrice(),
                request.getGasLimit(),
                request.getAbi(),
                false,
                callback);
    }

    public String asyncSendTransactionWithStringParams(
            TransactionRequestWithStringParams request, TransactionCallback callback)
            throws ContractCodecException, JniException {
        byte[] encodeMethodFromString =
                contractCodec.encodeMethodFromString(
                        request.getAbi(), request.getMethod(), request.getStringParams());
        return transactionManager.asyncSendTransaction(
                request.getTo(),
                Hex.toHexString(encodeMethodFromString),
                request.getValue(),
                request.getGasPrice(),
                request.getGasLimit(),
                request.getAbi(),
                false,
                callback);
    }

    public String asyncDeployContract(
            DeployTransactionRequest request, TransactionCallback callback)
            throws ContractCodecException, JniException {
        byte[] encodeConstructor =
                contractCodec.encodeConstructor(
                        request.getAbi(), request.getBin(), request.getParams());
        return transactionManager.asyncSendTransaction(
                request.getTo(),
                Hex.toHexString(encodeConstructor),
                request.getValue(),
                request.getGasPrice(),
                request.getGasLimit(),
                request.getAbi(),
                true,
                callback);
    }

    public String asyncDeployContractWithStringParams(
            DeployTransactionRequestWithStringParams request, TransactionCallback callback)
            throws JniException, ContractCodecException {
        byte[] encodeConstructorFromString =
                contractCodec.encodeConstructorFromString(
                        request.getAbi(), request.getBin(), request.getStringParams());
        return transactionManager.asyncSendTransaction(
                request.getTo(),
                Hex.toHexString(encodeConstructorFromString),
                request.getValue(),
                request.getGasPrice(),
                request.getGasLimit(),
                request.getAbi(),
                true,
                callback);
    }

    public CallResponse sendCall(TransactionRequest request)
            throws ContractCodecException, JniException {
        byte[] encodeMethod =
                contractCodec.encodeMethod(
                        request.getAbi(), request.getMethod(), request.getParams());
        Call call = transactionManager.sendCall(request.getTo(), Hex.toHexString(encodeMethod));
        return parseCallResponse(request, call);
    }

    public CallResponse sendCallWithStringParams(TransactionRequestWithStringParams request)
            throws ContractCodecException, JniException {
        byte[] encodeMethodFromString =
                contractCodec.encodeMethodFromString(
                        request.getAbi(), request.getMethod(), request.getStringParams());
        Call call =
                transactionManager.sendCall(
                        request.getTo(), Hex.toHexString(encodeMethodFromString));
        return parseCallResponse(request, call);
    }

    public void asyncSendCall(TransactionRequest request, RespCallback<CallResponse> callback)
            throws ContractCodecException, JniException {
        byte[] encodeMethod =
                contractCodec.encodeMethod(
                        request.getAbi(), request.getMethod(), request.getParams());
        transactionManager.asyncSendCall(
                request.getTo(),
                Hex.toHexString(encodeMethod),
                new RespCallback<Call>() {
                    @Override
                    public void onResponse(Call call) {
                        try {
                            callback.onResponse(parseCallResponse(request, call));
                        } catch (ContractCodecException e) {
                            Response response = new Response();
                            response.setErrorCode(-1);
                            response.setErrorMessage(e.getMessage());
                            callback.onError(response);
                        }
                    }

                    @Override
                    public void onError(Response errorResponse) {
                        callback.onError(errorResponse);
                    }
                });
    }

    public void asyncSendCallWithStringParams(
            TransactionRequestWithStringParams request, RespCallback<CallResponse> callback)
            throws ContractCodecException, JniException {
        byte[] encodeMethodFromString =
                contractCodec.encodeMethodFromString(
                        request.getAbi(), request.getMethod(), request.getStringParams());
        transactionManager.asyncSendCall(
                request.getTo(),
                Hex.toHexString(encodeMethodFromString),
                new RespCallback<Call>() {
                    @Override
                    public void onResponse(Call call) {
                        try {
                            callback.onResponse(parseCallResponse(request, call));
                        } catch (ContractCodecException e) {
                            Response response = new Response();
                            response.setErrorCode(-1);
                            response.setErrorMessage(e.getMessage());
                            callback.onError(response);
                        }
                    }

                    @Override
                    public void onError(Response errorResponse) {
                        callback.onError(errorResponse);
                    }
                });
    }

    private CallResponse parseCallResponse(TransactionRequest request, Call call)
            throws ContractCodecException {
        CallResponse callResponse = new CallResponse();
        RetCode retCode = ReceiptParser.parseCallOutput(call.getCallResult(), "");
        callResponse.setReturnCode(call.getCallResult().getStatus());
        callResponse.setReturnMessage(retCode.getMessage());
        Pair<List<Object>, List<ABIObject>> methodOutputAndGetObject =
                contractCodec.decodeMethodOutputAndGetObject(
                        request.getAbi(), request.getMethod(), call.getCallResult().getOutput());
        callResponse.setReturnObject(methodOutputAndGetObject.getLeft());
        callResponse.setReturnABIObject(methodOutputAndGetObject.getRight());
        return callResponse;
    }
}
