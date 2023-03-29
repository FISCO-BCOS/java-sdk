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
package org.fisco.bcos.sdk.v3.transaction.manager;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.utilities.tx.TxPair;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.RespCallback;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.model.dto.CallRequest;
import org.fisco.bcos.sdk.v3.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.v3.transaction.model.exception.NoSuchTransactionFileException;
import org.fisco.bcos.sdk.v3.transaction.model.exception.TransactionBaseException;

public interface AssembleTransactionProcessorInterface {

    /**
     * deploy contract to fisco bcos node and get transaction receipt.
     *
     * @param data encoded transaction data
     * @param abi contract abi string
     * @param path the BFS path, which the contract be deployed in exactly path, this param only
     *     enable in wasm
     * @return transaction receipt
     * @throws JniException throw when encode transaction error
     */
    TransactionReceipt deployAndGetReceipt(byte[] data, String abi, String path)
            throws JniException;

    /**
     * deploy contract to fisco bcos node and get transaction receipt.
     *
     * @param data encoded transaction data
     * @return transaction receipt
     * @throws JniException throw when encode transaction error
     */
    TransactionReceipt deployAndGetReceipt(byte[] data) throws JniException;

    /**
     * deploy contract in exact BFS path to fisco bcos node only without receive any response.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary, which could be obtained by compiling solidity contract.
     * @param params contract construct parameters
     * @param path this param only enable in wasm, the BFS path, which the contract be deployed in
     *     exactly path
     * @return deploy transaction hash
     * @throws ContractCodecException throw when encode deploy error
     */
    String deployOnly(String abi, String bin, List<Object> params, String path)
            throws ContractCodecException;

    String deployOnly(
            String abi, String bin, List<Object> params, String path, CryptoKeyPair cryptoKeyPair)
            throws ContractCodecException;

    /**
     * deploy contract to fisco bcos node only without receive any response.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary, which could be obtained by compiling solidity contract.
     * @param params contract construct parameters
     * @return deploy transaction hash
     * @throws ContractCodecException throw when encode deploy error
     */
    String deployOnly(String abi, String bin, List<Object> params) throws ContractCodecException;

    /**
     * deploy contract to fisco bcos node and get response.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param signedData signed and encoded constructor data
     * @return transaction response
     */
    TransactionResponse deployAndGetResponse(String abi, String signedData);

    TransactionResponse deployAndGetResponse(
            String abi, String bin, List<Object> params, String path, CryptoKeyPair cryptoKeyPair)
            throws ContractCodecException;

    /**
     * deploy contract to fisco bcos node and get response.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary, which could be obtained by compiling solidity contract.
     * @param params contract construct parameters
     * @return transaction response @See TransactionResponse
     * @throws ContractCodecException throw when encode deploy error
     */
    TransactionResponse deployAndGetResponse(String abi, String bin, List<Object> params)
            throws ContractCodecException;

    /**
     * deploy contract to fisco bcos node and get response.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary, which could be obtained by compiling solidity contract.
     * @param params contract construct parameters
     * @param path this param only enable in wasm, the BFS path, which the contract be deployed in
     *     exactly path
     * @return transaction response
     * @throws ContractCodecException throw when encode deploy error
     */
    TransactionResponse deployAndGetResponse(
            String abi, String bin, List<Object> params, String path) throws ContractCodecException;

    /**
     * deploy contract to fisco bcos node and get response.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary, which could be obtained by compiling solidity contract.
     * @param params contract construct string parameters
     * @param path the BFS path, which the contract be deployed in exactly path, this param only
     *     enable in wasm
     * @return transaction response
     * @throws ContractCodecException throw when encode deploy error
     */
    TransactionResponse deployAndGetResponseWithStringParams(
            String abi, String bin, List<String> params, String path) throws ContractCodecException;

    TransactionResponse deployAndGetResponseWithStringParams(
            String abi, String bin, List<String> params, String path, CryptoKeyPair cryptoKeyPair)
            throws ContractCodecException;

    /**
     * deploy contract to fisco bcos node asynchronously.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary, which could be obtained by compiling solidity contract.
     * @param params contract construct parameters
     * @param callback transaction with callback function
     * @return return deploy tx hash
     * @throws ContractCodecException throw when encode deploy error
     */
    String deployAsync(String abi, String bin, List<Object> params, TransactionCallback callback)
            throws ContractCodecException;

    /**
     * deploy contract to fisco bcos node asynchronously.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary, which could be obtained by compiling solidity contract.
     * @param params contract construct parameters
     * @param path the BFS path, which the contract be deployed in exactly path, this param only
     *     enable in wasm
     * @param callback transaction with callback function
     * @return return deploy tx hash
     * @throws ContractCodecException throw when encode deploy error
     */
    String deployAsync(
            String abi, String bin, List<Object> params, String path, TransactionCallback callback)
            throws ContractCodecException;

    /**
     * deploy contract to fisco bcos node asynchronously.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary, which could be obtained by compiling solidity contract.
     * @param params contract construct parameters
     * @return CompletableFuture wrapper transaction receipt
     * @throws ContractCodecException throw when encode deploy error
     * @throws JniException throw when encode transaction error
     */
    CompletableFuture<TransactionReceipt> deployAsync(String abi, String bin, List<Object> params)
            throws ContractCodecException, JniException;

    String deployAsync(
            String abi,
            String bin,
            List<Object> params,
            String path,
            CryptoKeyPair cryptoKeyPair,
            TransactionCallback callback)
            throws ContractCodecException;

    /**
     * deploy contract to fisco bcos node asynchronously.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary, which could be obtained by compiling solidity contract.
     * @param params contract construct parameters
     * @param path the BFS path, which the contract be deployed in exactly path, this param only
     *     enable in wasm
     * @return CompletableFuture wrapper transaction receipt
     * @throws ContractCodecException throw when encode deploy error
     * @throws JniException throw when encode transaction error
     */
    CompletableFuture<TransactionReceipt> deployAsync(
            String abi, String bin, List<Object> params, String path)
            throws ContractCodecException, JniException;

    CompletableFuture<TransactionReceipt> deployAsync(
            String abi, String bin, List<Object> params, String path, CryptoKeyPair cryptoKeyPair)
            throws ContractCodecException;

    /**
     * deploy contract to fisco bcos node and get response by contract name. The contract loader
     * will load the transaction abi and bin information.
     *
     * @param contractName contract name.
     * @param params contract construct parameters
     * @return transaction response
     * @throws ContractCodecException throw when encode deploy error
     * @throws TransactionBaseException throw when loader get contract error
     */
    TransactionResponse deployByContractLoader(String contractName, List<Object> params)
            throws ContractCodecException, TransactionBaseException;

    /**
     * deploy contract to fisco bcos node and get response by contract name. The contract loader
     * will load the transaction abi and bin information.
     *
     * @param contractName contract name.
     * @param params contract construct parameters
     * @param path BFS path only for wasm
     * @return transaction response
     * @throws ContractCodecException throw when encode deploy error
     * @throws TransactionBaseException throw when loader get contract error
     */
    TransactionResponse deployByContractLoader(
            String contractName, List<Object> params, String path)
            throws ContractCodecException, TransactionBaseException;

    /**
     * deploy contract to fisco bcos node and get response by contract name asynchronously. The
     * contract loader will load the transaction abi and bin information.
     *
     * @param contractName contract name.
     * @param params contract construct parameters
     * @param callback transaction with callback function
     * @throws ContractCodecException throw when encode deploy error
     * @throws NoSuchTransactionFileException throw when loader get contract error
     */
    void deployByContractLoaderAsync(
            String contractName, List<Object> params, TransactionCallback callback)
            throws ContractCodecException, NoSuchTransactionFileException;

    /**
     * deploy contract to fisco bcos node and get response by contract name asynchronously. The
     * contract loader will load the transaction abi and bin information.
     *
     * @param contractName contract name.
     * @param params contract construct parameters
     * @param path BFS path only for wasm
     * @param callback transaction with callback function
     * @throws ContractCodecException throw when encode deploy error
     * @throws NoSuchTransactionFileException throw when loader get contract error
     */
    void deployByContractLoaderAsync(
            String contractName, List<Object> params, String path, TransactionCallback callback)
            throws ContractCodecException, NoSuchTransactionFileException;

    /**
     * send transaction only.
     *
     * @param signedData signed and encoded transaction data
     */
    void sendTransactionOnly(String signedData);

    /**
     * send transaction to fisco bcos node and get transaction receipt by contract name. The
     * contract loader will load the transaction abi and bin information.
     *
     * @param contractName contract name.
     * @param contractAddress contract address
     * @param functionName contract function name
     * @param params contract construct parameters
     * @return transaction receipt
     * @throws ContractCodecException throw when encode deploy error
     * @throws TransactionBaseException throw when loader get contract error
     * @throws JniException throw when jni encode transaction error
     */
    TransactionReceipt sendTransactionAndGetReceiptByContractLoader(
            String contractName, String contractAddress, String functionName, List<Object> params)
            throws ContractCodecException, TransactionBaseException, JniException;

    /**
     * send transaction to fisco bcos node and get response.
     *
     * @param to the target contract address.
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param functionName contract function name.
     * @param data abi encoded transaction data
     * @return transaction response
     */
    TransactionResponse sendTransactionAndGetResponse(
            String to, String abi, String functionName, byte[] data);

    TransactionResponse sendTransactionAndGetResponse(
            String to, String abi, String functionName, byte[] data, CryptoKeyPair cryptoKeyPair);

    /**
     * send transaction to fisco bcos node and get response.
     *
     * @param to the target contract address.
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param functionName contract function name.
     * @param params contract construct parameters
     * @return transaction response
     * @throws ContractCodecException throw when encode deploy error
     * @throws TransactionBaseException throw when loader get contract error
     */
    TransactionResponse sendTransactionAndGetResponse(
            String to, String abi, String functionName, List<Object> params)
            throws ContractCodecException, TransactionBaseException;

    /**
     * send transaction to fisco bcos node and get response.
     *
     * @param to the target contract address.
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param functionName contract function name.
     * @param params contract function string parameters
     * @return transaction response
     * @throws ContractCodecException throw when encode deploy error
     * @throws TransactionBaseException throw when loader get contract error
     */
    TransactionResponse sendTransactionWithStringParamsAndGetResponse(
            String to, String abi, String functionName, List<String> params)
            throws ContractCodecException, TransactionBaseException;

    /**
     * send transaction to fisco bcos node asynchronously.
     *
     * @param signedTransaction signed and encoded transaction data
     * @param callback transaction with callback function
     */
    void sendTransactionAsync(String signedTransaction, TransactionCallback callback);

    /**
     * send transaction to fisco bcos node by contract name asynchronously. The contract loader will
     * load the transaction abi information.
     *
     * @param to the target contract address.
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param functionName contract function name.
     * @param params contract function parameters
     * @param callback transaction with callback function
     * @throws ContractCodecException throw when encode deploy error
     * @throws TransactionBaseException throw when loader get contract error
     */
    void sendTransactionAsync(
            String to,
            String abi,
            String functionName,
            List<Object> params,
            TransactionCallback callback)
            throws TransactionBaseException, ContractCodecException;

    /**
     * send transaction to fisco bcos node asynchronously.
     *
     * @param signedData signed and encoded transaction data
     * @return CompletableFuture wrapper transaction receipt
     */
    CompletableFuture<TransactionReceipt> sendTransactionAsync(String signedData);

    /**
     * send transaction to fisco bcos node by contract name asynchronously. The contract loader will
     * load the transaction abi information.
     *
     * @param contractName contract name.
     * @param to the target contract address.
     * @param functionName contract function name.
     * @param params contract function parameters
     * @param callback transaction with callback function
     * @throws ContractCodecException throw when encode deploy error
     * @throws TransactionBaseException throw when loader get contract error
     */
    void sendTransactionAndGetReceiptByContractLoaderAsync(
            String contractName,
            String to,
            String functionName,
            List<Object> params,
            TransactionCallback callback)
            throws ContractCodecException, TransactionBaseException;

    /**
     * send transaction to fisco bcos node and get transaction response by contract name. The
     * contract loader will load the transaction abi information.
     *
     * @param contractName contract name.
     * @param to the target contract address.
     * @param functionName contract function name.
     * @param funcParams contract function parameters
     * @return transaction response
     * @throws ContractCodecException throw when encode deploy error
     * @throws TransactionBaseException throw when loader get contract error
     */
    TransactionResponse sendTransactionAndGetResponseByContractLoader(
            String contractName, String to, String functionName, List<Object> funcParams)
            throws ContractCodecException, TransactionBaseException;

    /**
     * send call to fisco bcos node and get call response by contract name. The contract loader will
     * load the transaction abi information.
     *
     * @param contractName contract name.
     * @param to the target contract address.
     * @param functionName contract function name.
     * @param params contract call parameters
     * @return call response
     * @throws ContractCodecException throw when encode deploy error
     * @throws TransactionBaseException throw when loader get contract error
     */
    CallResponse sendCallByContractLoader(
            String contractName, String to, String functionName, List<Object> params)
            throws TransactionBaseException, ContractCodecException;

    /**
     * send call to fisco bcos node and get call response
     *
     * @param from sender address
     * @param to the target contract address.
     * @param abi ABI json string
     * @param functionName contract function name.
     * @param params contract call parameters
     * @return call response
     * @throws ContractCodecException throw when encode deploy error
     * @throws TransactionBaseException throw when loader get contract error
     */
    CallResponse sendCall(
            String from, String to, String abi, String functionName, List<Object> params)
            throws TransactionBaseException, ContractCodecException;

    /**
     * send call to fisco bcos node and get call response
     *
     * @param callRequest call request information
     * @return call response
     * @throws ContractCodecException throw when encode deploy error
     * @throws TransactionBaseException throw when loader get contract error
     */
    CallResponse sendCall(CallRequest callRequest)
            throws ContractCodecException, TransactionBaseException;

    void sendCallAsync(
            String from,
            String to,
            String abi,
            String functionName,
            List<Object> params,
            RespCallback<CallResponse> callback)
            throws ContractCodecException;

    void sendCallAsync(CallRequest callRequest, RespCallback<CallResponse> callback);

    /**
     * send call to fisco bcos node and get call response
     *
     * @param from sender address
     * @param to the target contract address.
     * @param abi ABI json string
     * @param functionName contract function name.
     * @param params contract call parameters
     * @return call response
     * @throws ContractCodecException throw when encode deploy error
     * @throws TransactionBaseException throw when loader get contract error
     */
    CallResponse sendCallWithStringParams(
            String from, String to, String abi, String functionName, List<String> params)
            throws TransactionBaseException, ContractCodecException;

    void sendCallWithStringParamsAsync(
            String from,
            String to,
            String abi,
            String functionName,
            List<String> params,
            RespCallback<CallResponse> callback)
            throws TransactionBaseException, ContractCodecException;

    /**
     * create signed constructor.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary, which could be obtained by compiling solidity contract.
     * @param params contract construct parameters
     * @param path the BFS path, which the contract be deployed in exactly path, this param only
     *     enable in wasm
     * @return signed constructor string
     * @throws ContractCodecException throw when encode deploy error
     */
    TxPair createSignedConstructor(String abi, String bin, List<Object> params, String path)
            throws ContractCodecException;

    /**
     * create signed constructor, use specific keyPair
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary, which could be obtained by compiling solidity contract.
     * @param params contract construct parameters
     * @param path the BFS path, which the contract be deployed in exactly path, this param only
     *     enable in wasm
     * @param keyPair specific keyPair, not use default keyPair
     * @return signed constructor string
     * @throws ContractCodecException throw when encode deploy error
     */
    TxPair createSignedConstructor(
            String abi, String bin, List<Object> params, String path, CryptoKeyPair keyPair)
            throws ContractCodecException;

    /**
     * encode function with abi and parameters.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param functionName contract function name
     * @param params contract function parameters
     * @return encoded function string
     * @throws ContractCodecException throw when encode deploy error
     */
    byte[] encodeFunction(String abi, String functionName, List<Object> params)
            throws ContractCodecException;

    /**
     * get constructor raw transaction.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary
     * @param params contract function parameters
     * @return raw transaction
     * @throws ContractCodecException throw when encode deploy error
     * @throws JniException throw when jni encode transaction error
     */
    long getRawTransactionForConstructor(String abi, String bin, List<Object> params)
            throws ContractCodecException, JniException;

    /**
     * get constructor raw transaction.
     *
     * @param blockLimit block limit
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary
     * @param params contract function parameters
     * @return raw transaction
     * @throws ContractCodecException throw when encode deploy error
     * @throws JniException throw when jni encode transaction error
     */
    long getRawTransactionForConstructor(
            BigInteger blockLimit, String abi, String bin, List<Object> params)
            throws ContractCodecException, JniException;

    /**
     * get raw transaction exclude constructor.
     *
     * @param to target address
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param functionName function name
     * @param params contract function parameters
     * @return raw transaction
     * @throws ContractCodecException throw when encode deploy error
     * @throws JniException throw when jni encode transaction error
     */
    long getRawTransaction(String to, String abi, String functionName, List<Object> params)
            throws ContractCodecException, JniException;

    /**
     * get raw transaction exclude constructor.
     *
     * @param blockLimit block limit
     * @param to target address
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param functionName function name
     * @param params contract function parameters
     * @return raw transaction
     * @throws ContractCodecException throw when encode deploy error
     * @throws JniException throw when jni encode transaction error
     */
    long getRawTransaction(
            BigInteger blockLimit, String to, String abi, String functionName, List<Object> params)
            throws ContractCodecException, JniException;
}
