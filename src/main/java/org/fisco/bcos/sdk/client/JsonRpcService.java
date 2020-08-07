/**
 * Copyright 2014-2020 [fisco-dev]
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fisco.bcos.sdk.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fisco.bcos.sdk.channel.Channel;
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.channel.model.Options;
import org.fisco.bcos.sdk.client.exceptions.ClientException;
import org.fisco.bcos.sdk.client.protocol.request.JsonRpcRequest;
import org.fisco.bcos.sdk.client.protocol.response.JsonRpcResponse;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.MsgType;
import org.fisco.bcos.sdk.model.Response;
import org.fisco.bcos.sdk.service.GroupManagerService;
import org.fisco.bcos.sdk.transaction.model.callback.TransactionSucCallback;
import org.fisco.bcos.sdk.utils.ChannelUtils;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonRpcService {
    protected final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
    private static Logger logger = LoggerFactory.getLogger(JsonRpcService.class);
    private final GroupManagerService groupManagerService;
    public final Channel channel;
    private final Integer groupId;

    public JsonRpcService(
            GroupManagerService groupManagerService, Channel channel, Integer groupId) {
        this.groupManagerService = groupManagerService;
        this.channel = channel;
        this.groupId = groupId;
    }

    public Channel getChannel() {
        return this.channel;
    }

    public GroupManagerService getGroupManagerService() {
        return this.groupManagerService;
    }

    public <T extends JsonRpcResponse> T sendRequestToPeer(
            JsonRpcRequest request, String peerIpPort, Class<T> responseType) {
        return this.sendRequestToPeer(
                request, MsgType.CHANNEL_RPC_REQUEST, responseType, peerIpPort);
    }

    public <T extends JsonRpcResponse> T sendRequestToGroup(
            JsonRpcRequest request, Class<T> responseType) {
        return this.sendRequestToGroup(request, MsgType.CHANNEL_RPC_REQUEST, responseType);
    }

    public <T extends JsonRpcResponse> T sendRequestToPeer(
            JsonRpcRequest request, MsgType messageType, Class<T> responseType, String peerIpPort) {
        Message message =
                encodeRequestToMessage(request, Short.valueOf((short) messageType.getType()));
        Response response = channel.sendToPeer(message, peerIpPort);
        return this.parseResponseIntoJsonRpcResponse(request, response, responseType);
    }

    public <T extends JsonRpcResponse> T sendRequestToGroup(
            JsonRpcRequest request, MsgType messageType, Class<T> responseType) {
        Message message =
                encodeRequestToMessage(request, Short.valueOf((short) messageType.getType()));
        Response response = this.groupManagerService.sendMessageToGroup(this.groupId, message);
        return this.parseResponseIntoJsonRpcResponse(request, response, responseType);
    }

    public <T extends JsonRpcResponse> void asyncSendRequestToPeer(
            JsonRpcRequest request,
            String peerIpAndPort,
            Class<T> responseType,
            RespCallback<T> callback) {
        asyncSendRequestToPeer(
                request, MsgType.CHANNEL_RPC_REQUEST, peerIpAndPort, responseType, callback);
    }

    public <T extends JsonRpcResponse> void asyncSendRequestToPeer(
            JsonRpcRequest request,
            MsgType messageType,
            String peerIpAndPort,
            Class<T> responseType,
            RespCallback<T> callback) {
        Message message =
                encodeRequestToMessage(request, Short.valueOf((short) messageType.getType()));
        this.channel.asyncSendToPeer(
                message,
                peerIpAndPort,
                new ResponseCallback() {
                    @Override
                    public void onResponse(Response response) {
                        try {
                            // decode the transaction
                            T jsonRpcResponse =
                                    parseResponseIntoJsonRpcResponse(
                                            request, response, responseType);
                            callback.onResponse(jsonRpcResponse);
                        } catch (ClientException e) {
                            callback.onError(response);
                        }
                    }
                },
                new Options());
    }

    public <T extends JsonRpcResponse> void asyncSendRequestToGroup(
            JsonRpcRequest request, Class<T> responseType, RespCallback<T> callback) {
        asyncSendRequestToGroup(request, MsgType.CHANNEL_RPC_REQUEST, responseType, callback);
    }

    public <T extends JsonRpcResponse> void asyncSendRequestToGroup(
            JsonRpcRequest request,
            MsgType messageType,
            Class<T> responseType,
            RespCallback<T> callback) {
        Message message =
                encodeRequestToMessage(request, Short.valueOf((short) messageType.getType()));
        this.groupManagerService.asyncSendMessageToGroup(
                this.groupId,
                message,
                new ResponseCallback() {
                    @Override
                    public void onResponse(Response response) {
                        try {
                            // decode the transaction
                            T jsonRpcResponse =
                                    parseResponseIntoJsonRpcResponse(
                                            request, response, responseType);
                            callback.onResponse(jsonRpcResponse);
                        } catch (ClientException e) {
                            callback.onError(response);
                        }
                    }
                });
    }

    public <T extends JsonRpcResponse> void asyncSendTransactionToGroup(
            JsonRpcRequest request, TransactionSucCallback callback, Class<T> responseType) {
        Message message =
                encodeRequestToMessage(
                        request, Short.valueOf((short) MsgType.CHANNEL_RPC_REQUEST.getType()));
        this.groupManagerService.asyncSendTransaction(
                this.groupId,
                message,
                callback,
                new ResponseCallback() {
                    @Override
                    public void onResponse(Response response) {
                        try {
                            // decode the transaction
                            parseResponseIntoJsonRpcResponse(request, response, responseType);
                        } catch (ClientException e) {
                            groupManagerService.eraseTransactionSeq(response.getMessageID());
                            // fake the transactionReceipt
                            callback.onError(e.getErrorCode(), e.getErrorMessage());
                        }
                    }
                });
    }

    protected <T extends JsonRpcResponse> T parseResponseIntoJsonRpcResponse(
            JsonRpcRequest request, Response response, Class<T> responseType) {
        try {
            if (response.getErrorCode() == 0) {
                // parse the response into JsonRPCResponse
                T jsonRpcResponse = objectMapper.readValue(response.getContent(), responseType);
                if (jsonRpcResponse.getError() != null) {
                    logger.error(
                            "parseResponseIntoJsonRpcResponse failed for non-empty error message, method: {}, group: {}, seq: {}, messageType: {}, retErrorMessage: {}, retErrorCode: {}",
                            request.getMethod(),
                            this.groupId,
                            response.getMessageID(),
                            jsonRpcResponse.getError().getMessage(),
                            jsonRpcResponse.getError().getCode());
                    throw new ClientException(
                            jsonRpcResponse.getError().getCode(),
                            jsonRpcResponse.getError().getMessage(),
                            "parseResponseIntoJsonRpcResponse failed for non-empty error message, method: "
                                    + request.getMethod()
                                    + ", group: "
                                    + this.groupId
                                    + ", seq:"
                                    + response.getMessageID()
                                    + ",retErrorMessage: "
                                    + jsonRpcResponse.getError().getMessage());
                } else {
                    parseResponseOutput(jsonRpcResponse);
                }
                return jsonRpcResponse;
            } else {
                logger.error(
                        "parseResponseIntoJsonRpcResponse failed, method: {}, group: {}, seq: {}, retErrorMessage: {}, retErrorCode: {}",
                        request.getMethod(),
                        this.groupId,
                        response.getMessageID(),
                        response.getErrorMessage(),
                        response.getErrorCode());
                throw new ClientException(
                        "get response failed, errorCode:"
                                + response.getErrorCode()
                                + ", error message:"
                                + response.getErrorMessage());
            }

        } catch (JsonProcessingException e) {
            logger.error(
                    "parseResponseIntoJsonRpcResponse failed for decode the message exceptioned, errorMessge: {}, groupId: {}",
                    e.getMessage(),
                    this.groupId);
            throw new ClientException(
                    "parseResponseIntoJsonRpcResponse failed for decode the message exceptioned, error message:"
                            + e.getMessage(),
                    e);
        }
    }

    private <T extends JsonRpcResponse> void parseResponseOutput(T jsonRpcResponse) {
        // TODO: parse the transaction outpput(especially the revertMessage for the call interface)
    }

    /**
     * encode the request into message
     *
     * @return: the messaged encoded from the request
     */
    private Message encodeRequestToMessage(JsonRpcRequest request, Short messageType) {
        try {
            byte[] encodedData = objectMapper.writeValueAsBytes(request);
            Message message = new Message();
            message.setSeq(ChannelUtils.newSeq());
            message.setResult(0);
            message.setType(messageType);
            message.setData(encodedData);
            logger.trace(
                    "encodeRequestToMessage, seq: {}, method: {}, messageType: {}",
                    message.getSeq(),
                    request.getMethod(),
                    message.getType());
            return message;
        } catch (JsonProcessingException e) {
            logger.error(
                    "sendRequestToGroup failed for decode the message exceptioned, errorMessge: {}",
                    e.getMessage());
            throw new ClientException(
                    "sendRequestToGroup to "
                            + this.groupId
                            + "failed for decode the message exceptioned, error message:"
                            + e.getMessage(),
                    e);
        }
    }
}
