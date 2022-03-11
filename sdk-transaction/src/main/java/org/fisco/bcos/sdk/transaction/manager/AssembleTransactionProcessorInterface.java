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

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.fisco.bcos.sdk.codec.ContractCodecException;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.utilities.tx.TxPair;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.dto.CallRequest;
import org.fisco.bcos.sdk.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.model.exception.NoSuchTransactionFileException;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionBaseException;

public interface AssembleTransactionProcessorInterface {

    /**
     * deploy contract to fisco bcos node and get transaction receipt.
     *
     * @param data encoded transaction data
     * @param abi contract abi string
     * @param path the BFS path, which the contract be deployed in exactly path, this param only
     *     enable in wasm
     * @return transaction receipt
     */
    public TransactionReceipt deployAndGetReceipt(byte[] data, String abi, String path)
            throws JniException;

    /**
     * deploy contract to fisco bcos node and get transaction receipt.
     *
     * @param data encoded transaction data
     * @return transaction receipt
     */
    public TransactionReceipt deployAndGetReceipt(byte[] data) throws JniException;

    /**
     * deploy contract in exact BFS path to fisco bcos node only without receive any response.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary, which could be obtained by compiling solidity contract.
     * @param params contract construct parameters
     * @param path this param only enable in wasm, the BFS path, which the contract be deployed in
     *     exactly path
     * @return deploy transaction hash
     */
    public String deployOnly(String abi, String bin, List<Object> params, String path)
            throws ContractCodecException;

    /**
     * deploy contract to fisco bcos node only without receive any response.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary, which could be obtained by compiling solidity contract.
     * @param params contract construct parameters
     * @return deploy transaction hash
     */
    public String deployOnly(String abi, String bin, List<Object> params)
            throws ContractCodecException;

    /**
     * deploy contract to fisco bcos node and get response.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param signedData signed & encoded constructor data
     * @return transaction response @See TransactionResponse
     */
    public TransactionResponse deployAndGetResponse(String abi, String signedData);

    /**
     * deploy contract to fisco bcos node and get response.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary, which could be obtained by compiling solidity contract.
     * @param params contract construct parameters
     * @return transaction response @See TransactionResponse
     */
    public TransactionResponse deployAndGetResponse(String abi, String bin, List<Object> params)
            throws ContractCodecException;

    /**
     * deploy contract to fisco bcos node and get response.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary, which could be obtained by compiling solidity contract.
     * @param params contract construct parameters
     * @param path this param only enable in wasm, the BFS path, which the contract be deployed in
     *     exactly path
     * @return transaction response @See TransactionResponse
     */
    public TransactionResponse deployAndGetResponse(
            String abi, String bin, List<Object> params, String path) throws ContractCodecException;

    /**
     * deploy contract to fisco bcos node and get response.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary, which could be obtained by compiling solidity contract.
     * @param params contract construct string parameters
     * @param path the BFS path, which the contract be deployed in exactly path, this param only
     *     enable in wasm
     */
    public TransactionResponse deployAndGetResponseWithStringParams(
            String abi, String bin, List<String> params, String path) throws ContractCodecException;

    /**
     * deploy contract to fisco bcos node asynchronously.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary, which could be obtained by compiling solidity contract.
     * @param params contract construct parameters
     * @param callback transaction with callback function
     * @return return deploy tx hash
     */
    public String deployAsync(
            String abi, String bin, List<Object> params, TransactionCallback callback)
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
     */
    public String deployAsync(
            String abi, String bin, List<Object> params, String path, TransactionCallback callback)
            throws ContractCodecException;

    /**
     * deploy contract to fisco bcos node asynchronously.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary, which could be obtained by compiling solidity contract.
     * @param params contract construct parameters
     * @return CompletableFuture wrapper transaction receipt
     */
    public CompletableFuture<TransactionReceipt> deployAsync(
            String abi, String bin, List<Object> params)
            throws ContractCodecException, JniException;

    /**
     * deploy contract to fisco bcos node asynchronously.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary, which could be obtained by compiling solidity contract.
     * @param params contract construct parameters
     * @param path the BFS path, which the contract be deployed in exactly path, this param only
     *     enable in wasm
     * @return CompletableFuture wrapper transaction receipt
     */
    public CompletableFuture<TransactionReceipt> deployAsync(
            String abi, String bin, List<Object> params, String path)
            throws ContractCodecException, JniException;

    /**
     * deploy contract to fisco bcos node and get response by contract name. The contract loader
     * will load the transaction abi & bin information.
     *
     * @param contractName contract name.
     * @param params contract construct parameters
     * @return transaction response
     */
    public TransactionResponse deployByContractLoader(String contractName, List<Object> params)
            throws ContractCodecException, TransactionBaseException;

    /**
     * deploy contract to fisco bcos node and get response by contract name asynchronously. The
     * contract loader will load the transaction abi & bin information.
     *
     * @param contractName contract name.
     * @param params contract construct parameters
     * @param callback transaction with callback function
     */
    public void deployByContractLoaderAsync(
            String contractName, List<Object> params, TransactionCallback callback)
            throws ContractCodecException, NoSuchTransactionFileException;

    /**
     * send transaction only.
     *
     * @param signedData signed & encoded transaction data
     */
    public void sendTransactionOnly(String signedData);

    /**
     * send transaction to fisco bcos node and get transaction receipt by contract name. The
     * contract loader will load the transaction abi & bin information.
     *
     * @param contractName contract name.
     * @param contractAddress contract address
     * @param functionName contract function name
     * @param params contract construct parameters
     * @return transaction receipt
     */
    public TransactionReceipt sendTransactionAndGetReceiptByContractLoader(
            String contractName, String contractAddress, String functionName, List<Object> params)
            throws ContractCodecException, TransactionBaseException, JniException;

    /**
     * send transaction to fisco bcos node and get response.
     *
     * @param to the target contract address.
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param functionName contract function name.
     * @param data abi encoded transaction data
     * @return transaction response @See TransactionResponse
     */
    public TransactionResponse sendTransactionAndGetResponse(
            String to, String abi, String functionName, byte[] data)
            throws TransactionBaseException, ContractCodecException;

    /**
     * send transaction to fisco bcos node and get response.
     *
     * @param to the target contract address.
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param functionName contract function name.
     * @param params contract construct parameters
     * @return transaction response @See TransactionResponse
     */
    public TransactionResponse sendTransactionAndGetResponse(
            String to, String abi, String functionName, List<Object> params)
            throws ContractCodecException, TransactionBaseException;

    /**
     * send transaction to fisco bcos node and get response.
     *
     * @param to the target contract address.
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param functionName contract function name.
     * @param params contract function string parameters
     * @return transaction response @See TransactionResponse
     */
    public TransactionResponse sendTransactionWithStringParamsAndGetResponse(
            String to, String abi, String functionName, List<String> params)
            throws ContractCodecException, TransactionBaseException;

    /**
     * send transaction to fisco bcos node asynchronously.
     *
     * @param signedTransaction signed & encoded transaction data
     * @param callback transaction with callback function
     */
    public void sendTransactionAsync(String signedTransaction, TransactionCallback callback);

    /**
     * send transaction to fisco bcos node by contract name asynchronously. The contract loader will
     * load the transaction abi information.
     *
     * @param to the target contract address.
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param functionName contract function name.
     * @param params contract function parameters
     * @param callback transaction with callback function
     */
    public void sendTransactionAsync(
            String to,
            String abi,
            String functionName,
            List<Object> params,
            TransactionCallback callback)
            throws TransactionBaseException, ContractCodecException;

    /**
     * send transaction to fisco bcos node asynchronously.
     *
     * @param signedData signed & encoded transaction data
     * @return CompletableFuture wrapper transaction receipt
     */
    public CompletableFuture<TransactionReceipt> sendTransactionAsync(String signedData);

    /**
     * send transaction to fisco bcos node by contract name asynchronously. The contract loader will
     * load the transaction abi information.
     *
     * @param contractName contract name.
     * @param to the target contract address.
     * @param functionName contract function name.
     * @param params contract function parameters
     * @param callback transaction with callback function
     */
    public void sendTransactionAndGetReceiptByContractLoaderAsync(
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
     */
    public TransactionResponse sendTransactionAndGetResponseByContractLoader(
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
     */
    public CallResponse sendCallByContractLoader(
            String contractName, String to, String functionName, List<Object> params)
            throws TransactionBaseException, ContractCodecException;

    /**
     * send call to fisco bcos node and get call response
     *
     * @param from sender address
     * @param to the target contract address.
     * @param functionName contract function name.
     * @param params contract call parameters
     * @return call response
     */
    public CallResponse sendCall(
            String from, String to, String abi, String functionName, List<Object> params)
            throws TransactionBaseException, ContractCodecException;

    /**
     * send call to fisco bcos node and get call response
     *
     * @param callRequest call request information
     * @return call response
     */
    public CallResponse sendCall(CallRequest callRequest)
            throws ContractCodecException, TransactionBaseException;

    /**
     * send call to fisco bcos node and get call response
     *
     * @param from sender address
     * @param to the target contract address.
     * @param functionName contract function name.
     * @param params contract call parameters
     * @return call response
     */
    public CallResponse sendCallWithStringParams(
            String from, String to, String abi, String functionName, List<String> params)
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
     */
    public TxPair createSignedConstructor(String abi, String bin, List<Object> params, String path)
            throws ContractCodecException;

    /**
     * encode function with abi and parameters.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param functionName contract function name
     * @param params contract function parameters
     * @return encoded function string
     */
    public byte[] encodeFunction(String abi, String functionName, List<Object> params)
            throws ContractCodecException;

    /**
     * get constructor raw transaction.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary
     * @param params contract function parameters
     * @return raw transaction
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
     */
    long getRawTransaction(
            BigInteger blockLimit, String to, String abi, String functionName, List<Object> params)
            throws ContractCodecException, JniException;
}
