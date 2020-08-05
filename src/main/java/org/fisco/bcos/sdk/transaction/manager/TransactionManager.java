/*
 * Copyright 2014-2020  [fisco-dev]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package org.fisco.bcos.sdk.transaction.manager;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.response.Call;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.model.SolidityConstructor;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.builder.FunctionBuilderInterface;
import org.fisco.bcos.sdk.transaction.builder.TransactionBuilderInterface;
import org.fisco.bcos.sdk.transaction.builder.TransactionBuilderService;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderInterface;
import org.fisco.bcos.sdk.transaction.codec.encode.TransactionEncoderService;
import org.fisco.bcos.sdk.transaction.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.callback.TransactionSucCallback;
import org.fisco.bcos.sdk.transaction.model.dto.CallRequest;
import org.fisco.bcos.sdk.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.transaction.model.dto.ResultCodeEnum;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionRequest;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionBaseException;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionException;
import org.fisco.bcos.sdk.transaction.model.gas.DefaultGasProvider;
import org.fisco.bcos.sdk.transaction.model.po.RawTransaction;
import org.fisco.bcos.sdk.transaction.pusher.TransactionPusherInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TransactionManager @Description: TransactionManager
 *
 * @author maojiayu
 * @data Jul 17, 2020 3:23:19 PM
 */
public class TransactionManager implements TransactionManagerInterface {
    protected static Logger log = LoggerFactory.getLogger(TransactionManager.class);
    private final Client client;
    private final Integer groupId;
    private final String chainId;
    private final CryptoInterface cryptoInterface;
    private final TransactionBuilderInterface transactionBuilder;
    private FunctionBuilderInterface functionBuilder;
    private TransactionEncoderService transactionEncoder;
    private TransactionPusherInterface transactionPusher;
    private TransactionDecoderInterface transactionDecoder;

    public TransactionManager(
            Client client, CryptoInterface cryptoInterface, Integer groupId, String chainId) {
        this.client = client;
        this.groupId = groupId;
        this.chainId = chainId;
        this.transactionBuilder = new TransactionBuilderService(client);
        this.cryptoInterface = cryptoInterface;
        this.transactionEncoder = new TransactionEncoderService(cryptoInterface);
        // TODO: init functionBuilder, transactionPusher and transactionDecoder
    }

    public TransactionResponse deploy(
            String abi, String bin, String contractName, List<Object> args) {
        SolidityConstructor constructor =
                functionBuilder.buildConstructor(abi, bin, contractName, args);
        RawTransaction rawTransaction =
                transactionBuilder.createTransaction(
                        null, constructor.getData(), BigInteger.valueOf(groupId));
        String signedData = transactionEncoder.encodeAndSign(rawTransaction);
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setSignedData(signedData);
        return deploy(transactionRequest);
    }

    @Override
    public TransactionResponse deploy(TransactionRequest transactionRequest) {
        String contract = transactionRequest.getContractName();
        TransactionReceipt receipt = transactionPusher.push(transactionRequest.getSignedData());
        try {
            TransactionResponse response =
                    transactionDecoder.decodeTransactionReceipt(contract, receipt);
            return response;
        } catch (TransactionBaseException | TransactionException | IOException e) {
            log.error("deploy exception: {}", e.getMessage());
            return new TransactionResponse(
                    ResultCodeEnum.EXCEPTION_OCCUR.getCode(), e.getMessage());
        }
    }

    @Override
    public void sendTransaction(TransactionRequest transactionRequest) {
        this.transactionPusher.pushOnly(transactionRequest.getSignedData());
    }

    @Override
    public TransactionReceipt sendTransaction(String to, String data) {
        DefaultGasProvider defaultGasProvider = new DefaultGasProvider();
        RawTransaction rawTransaction =
                transactionBuilder.createTransaction(
                        defaultGasProvider.getGasPrice(),
                        defaultGasProvider.getGasLimit(),
                        to,
                        data,
                        BigInteger.ZERO,
                        new BigInteger(this.chainId),
                        BigInteger.valueOf(this.groupId),
                        "");
        String signedData = transactionEncoder.encodeAndSign(rawTransaction);
        return this.client.sendRawTransactionAndGetReceipt(signedData);
    }

    @Override
    public TransactionResponse sendTransactionAndGetResponse(
            TransactionRequest transactionRequest) {
        String contract = transactionRequest.getContractName();
        TransactionReceipt receipt =
                this.transactionPusher.push(transactionRequest.getSignedData());
        try {
            return transactionDecoder.decodeTransactionReceipt(contract, receipt);
        } catch (TransactionBaseException | TransactionException | IOException e) {
            log.error("sendTransaction exception: {}", e.getMessage());
            return new TransactionResponse(
                    ResultCodeEnum.EXCEPTION_OCCUR.getCode(), e.getMessage());
        }
    }

    @Override
    public void sendTransactionAsync(String signedTransaction, TransactionCallback callback) {
        this.transactionPusher.pushAsync(signedTransaction, callback);
    }

    @Override
    public void sendTransactionAsync(String to, String data, TransactionSucCallback callback) {
        DefaultGasProvider defaultGasProvider = new DefaultGasProvider();
        RawTransaction rawTransaction =
                transactionBuilder.createTransaction(
                        defaultGasProvider.getGasPrice(),
                        defaultGasProvider.getGasLimit(),
                        to,
                        data,
                        BigInteger.ZERO,
                        new BigInteger(this.chainId),
                        BigInteger.valueOf(this.groupId),
                        "");
        String signedData = transactionEncoder.encodeAndSign(rawTransaction);
        this.client.asyncSendRawTransaction(signedData, callback);
    }

    @Override
    public CompletableFuture<TransactionReceipt> sendTransactionAsync(
            TransactionRequest transactionRequest) {
        return this.transactionPusher.pushAsync(transactionRequest.getSignedData());
    }

    @Override
    public CallResponse sendCall(CallRequest callRequest) {
        // TODO
        return null;
    }

    @Override
    public String getCurrentExternalAccountAddress() {
        return this.cryptoInterface.getCryptoKeyPair().getAddress();
    }

    @Override
    public Call executeCall(CallRequest callRequest) {
        return null;
    }

    @Override
    public String createSignedTransaction(String to, String data) {
        return null;
    }
}
