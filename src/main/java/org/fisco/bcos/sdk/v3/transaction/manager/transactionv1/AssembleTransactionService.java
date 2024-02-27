package org.fisco.bcos.sdk.v3.transaction.manager.transactionv1;

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
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.AbiEncodedRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.BasicDeployRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.BasicRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.DeployTransactionRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.DeployTransactionRequestWithStringParams;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.TransactionRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.TransactionRequestWithStringParams;
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

    /**
     * Sets the TransactionManager for this service. DefaultTransactionManager is used by default.
     *
     * <p>ProxySignTransactionManager can be used to sign transactions with a proxy account, you can
     * change account easily.
     *
     * @param transactionManager the TransactionManager to be set
     */
    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * This method is used to send a transaction.
     *
     * <p>If the request is an instance of TransactionRequest, it encodes the method with the
     * parameters from the request.
     *
     * <p>If the request is an instance of TransactionRequestWithStringParams, it encodes the method
     * with the string parameters from the request.
     *
     * <p>If the request is not an instance of either, it throws a ContractCodecException.
     *
     * @param request the request containing the necessary information to send the transaction
     * @return TransactionResponse the response of the transaction
     * @throws ContractCodecException if there is an error with the contract codec or the request is
     *     not an instance of TransactionRequest or TransactionRequestWithStringParams
     * @throws JniException if there is an error with the JNI
     */
    public TransactionResponse sendTransaction(BasicRequest request)
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
        AbiEncodedRequest abiEncodedRequest = new AbiEncodedRequest(request);
        abiEncodedRequest.setEncodedData(encodeMethod);

        TransactionReceipt receipt = transactionManager.sendTransaction(abiEncodedRequest);
        if (Objects.nonNull(receipt)
                && (Objects.isNull(receipt.getInput()) || receipt.getInput().isEmpty())) {
            receipt.setInput(Hex.toHexStringWithPrefix(encodeMethod));
        }
        return this.transactionDecoder.decodeReceiptWithValues(
                request.getAbi(), request.getMethod(), receipt);
    }

    /**
     * This method is used to deploy a contract.
     *
     * <p>If the request is an instance of DeployTransactionRequest, it encodes the constructor with
     * the parameters from the request.
     *
     * <p>If the request is an instance of DeployTransactionRequestWithStringParams, it encodes the
     * constructor with the string parameters from the request.
     *
     * <p>If the request is not an instance of either, it throws a ContractCodecException.
     *
     * @param request the request containing the necessary information to deploy the contract
     * @return TransactionResponse the response of the transaction
     * @throws ContractCodecException if there is an error with the contract codec or the request is
     *     not an instance of DeployTransactionRequest or DeployTransactionRequestWithStringParams
     * @throws JniException if there is an error with the JNI
     */
    public TransactionResponse deployContract(BasicDeployRequest request)
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
        AbiEncodedRequest abiEncodedRequest = new AbiEncodedRequest(request);
        abiEncodedRequest.setEncodedData(encodeConstructor);
        abiEncodedRequest.setCreate(true);
        TransactionReceipt receipt = transactionManager.sendTransaction(abiEncodedRequest);
        if (Objects.nonNull(receipt)
                && (Objects.isNull(receipt.getInput()) || receipt.getInput().isEmpty())) {
            receipt.setInput(Hex.toHexStringWithPrefix(encodeConstructor));
        }
        return this.transactionDecoder.decodeReceiptWithoutValues(request.getAbi(), receipt);
    }

    /**
     * This method is used to send a transaction asynchronously.
     *
     * <p>If the request is an instance of TransactionRequest, it encodes the method with the
     * parameters from the request.
     *
     * <p>If the request is an instance of TransactionRequestWithStringParams, it encodes the method
     * with the string parameters from the request.
     *
     * <p>If the request is not an instance of either, it throws a ContractCodecException.
     *
     * @param request the request containing the necessary information to send the transaction
     * @param callback the callback to be called when the transaction is sent
     * @return String the transaction hash
     * @throws ContractCodecException if there is an error with the contract codec or the request is
     *     not an instance of TransactionRequest or TransactionRequestWithStringParams
     * @throws JniException if there is an error with the JNI
     */
    public String asyncSendTransaction(BasicRequest request, TransactionCallback callback)
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
        AbiEncodedRequest abiEncodedRequest = new AbiEncodedRequest(request);
        abiEncodedRequest.setEncodedData(encodeMethod);
        return transactionManager.asyncSendTransaction(abiEncodedRequest, callback);
    }

    /**
     * This method is used to deploy a contract asynchronously.
     *
     * <p>If the request is an instance of DeployTransactionRequest, it encodes the constructor with
     * the parameters from the request.
     *
     * <p>If the request is an instance of DeployTransactionRequestWithStringParams, it encodes the
     * constructor with the string parameters from the request.
     *
     * <p>If the request is not an instance of either, it throws a ContractCodecException.
     *
     * @param request the request containing the necessary information to deploy the contract
     * @param callback the callback to be called when the transaction is sent
     * @return String the transaction hash
     * @throws ContractCodecException if there is an error with the contract codec or the request is
     *     not an instance of DeployTransactionRequest or DeployTransactionRequestWithStringParams
     * @throws JniException if there is an error with the JNI
     */
    public String asyncDeployContract(BasicDeployRequest request, TransactionCallback callback)
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
        AbiEncodedRequest abiEncodedRequest = new AbiEncodedRequest(request);
        abiEncodedRequest.setEncodedData(encodeConstructor);
        abiEncodedRequest.setCreate(true);
        return transactionManager.asyncSendTransaction(abiEncodedRequest, callback);
    }

    /**
     * This method is used to send a call.
     *
     * <p>If the request is an instance of TransactionRequest, it encodes the method with the
     * parameters from the request.
     *
     * <p>If the request is an instance of TransactionRequestWithStringParams, it encodes the method
     * with the string parameters from the request.
     *
     * <p>If the request is not an instance of either, it throws a ContractCodecException.
     *
     * @param request the request containing the necessary information to send the call
     * @return CallResponse the response of the call
     * @throws ContractCodecException if there is an error with the contract codec or the request is
     *     not an instance of TransactionRequest or TransactionRequestWithStringParams
     * @throws JniException if there is an error with the JNI
     */
    public CallResponse sendCall(BasicRequest request) throws ContractCodecException, JniException {
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
        Call call = transactionManager.sendCall(request.getTo(), encodeMethod);
        return parseCallResponse(request, call);
    }

    /**
     * This method is used to send a call asynchronously.
     *
     * <p>If the request is an instance of TransactionRequest, it encodes the method with the
     * parameters from the request.
     *
     * <p>If the request is an instance of TransactionRequestWithStringParams, it encodes the method
     * with the string parameters from the request.
     *
     * <p>If the request is not an instance of either, it throws a ContractCodecException.
     *
     * @param request the request containing the necessary information to send the call
     * @param callback the callback to be called when the call is sent
     * @throws ContractCodecException if there is an error with the contract codec or the request is
     *     not an instance of TransactionRequest or TransactionRequestWithStringParams
     */
    public void asyncSendCall(BasicRequest request, RespCallback<CallResponse> callback)
            throws ContractCodecException {
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

        transactionManager.asyncSendCall(
                request.getTo(),
                encodeMethod,
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

    private CallResponse parseCallResponse(BasicRequest request, Call call)
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
