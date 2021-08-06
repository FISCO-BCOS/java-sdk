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
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.fisco.bcos.sdk.client.protocol.model.TransactionData;
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
     * @return transaction receipt
     */
    public TransactionReceipt deployAndGetReceipt(byte[] data);

    /**
     * deploy contract to fisco bcos node only without receive any response.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary, which could be obtained by compiling solidity contract.
     * @param params contract construct parameters
     */
    public void deployOnly(String abi, String bin, List<Object> params) throws ABICodecException;

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
            throws ABICodecException;

    /**
     * deploy contract to fisco bcos node and get response.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary, which could be obtained by compiling solidity contract.
     * @param params contract construct string parameters
     */
    public TransactionResponse deployAndGetResponseWithStringParams(
            String abi, String bin, List<String> params) throws ABICodecException;

    /**
     * deploy contract to fisco bcos node asynchronously.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary, which could be obtained by compiling solidity contract.
     * @param params contract construct parameters
     * @param callback transaction with callback function
     */
    public void deployAsync(
            String abi, String bin, List<Object> params, TransactionCallback callback)
            throws ABICodecException;

    /**
     * deploy contract to fisco bcos node asynchronously.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary, which could be obtained by compiling solidity contract.
     * @param params contract construct parameters
     * @return CompletableFuture wrapper transaction receipt
     */
    public CompletableFuture<TransactionReceipt> deployAsync(
            String abi, String bin, List<Object> params) throws ABICodecException;

    /**
     * deploy contract to fisco bcos node and get response by contract name. The contract loader
     * will load the transaction abi & bin information.
     *
     * @param contractName contract name.
     * @param params contract construct parameters
     * @return transaction response
     */
    public TransactionResponse deployByContractLoader(String contractName, List<Object> params)
            throws ABICodecException, TransactionBaseException;

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
            throws ABICodecException, NoSuchTransactionFileException;

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
            throws ABICodecException, TransactionBaseException;

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
            throws TransactionBaseException, ABICodecException;

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
            throws ABICodecException, TransactionBaseException;

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
            throws ABICodecException, TransactionBaseException;

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
            throws TransactionBaseException, ABICodecException;

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
            throws ABICodecException, TransactionBaseException;

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
            throws ABICodecException, TransactionBaseException;

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
            throws TransactionBaseException, ABICodecException;

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
            throws TransactionBaseException, ABICodecException;

    /**
     * send call to fisco bcos node and get call response
     *
     * @param callRequest call request information
     * @return call response
     */
    public CallResponse sendCall(CallRequest callRequest)
            throws ABICodecException, TransactionBaseException;

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
            throws TransactionBaseException, ABICodecException;

    /**
     * create signed constructor.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary, which could be obtained by compiling solidity contract.
     * @param params contract construct parameters
     * @return signed constructor string
     */
    public String createSignedConstructor(String abi, String bin, List<Object> params)
            throws ABICodecException;

    /**
     * encode function with abi and parameters.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param functionName contract function name
     * @param params contract function parameters
     * @return encoded function string
     */
    public byte[] encodeFunction(String abi, String functionName, List<Object> params)
            throws ABICodecException;

    /**
     * get constructor raw transaction.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary
     * @param params contract function parameters
     * @return raw transaction
     */
    TransactionData getRawTransactionForConstructor(String abi, String bin, List<Object> params)
            throws ABICodecException;

    /**
     * get constructor raw transaction.
     *
     * @param blockLimit block limit
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary
     * @param params contract function parameters
     * @return raw transaction
     */
    TransactionData getRawTransactionForConstructor(
            BigInteger blockLimit, String abi, String bin, List<Object> params)
            throws ABICodecException;

    /**
     * get raw transaction exclude constructor.
     *
     * @param to target address
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param functionName function name
     * @param params contract function parameters
     * @return raw transaction
     */
    TransactionData getRawTransaction(
            String to, String abi, String functionName, List<Object> params)
            throws ABICodecException;

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
    TransactionData getRawTransaction(
            BigInteger blockLimit, String to, String abi, String functionName, List<Object> params)
            throws ABICodecException;
}
