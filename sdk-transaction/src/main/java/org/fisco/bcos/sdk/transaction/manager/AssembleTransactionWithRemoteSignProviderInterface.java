package org.fisco.bcos.sdk.transaction.manager;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.fisco.bcos.sdk.codec.ContractCodecException;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.exception.NoSuchTransactionFileException;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionBaseException;
import org.fisco.bcos.sdk.transaction.signer.RemoteSignCallbackInterface;

public interface AssembleTransactionWithRemoteSignProviderInterface {

    /**
     * deploy contract to fisco bcos node asynchronously.
     *
     * @param transactionData raw transaction data
     * @param remoteSignCallbackInterface after signed, callback function hook
     */
    void deployAsync(long transactionData, RemoteSignCallbackInterface remoteSignCallbackInterface)
            throws ContractCodecException, JniException;

    /**
     * deploy contract to fisco bcos node asynchronously.
     *
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param bin contract binary, which could be obtained by compiling solidity contract.
     * @param params contract construct parameters
     * @param remoteSignCallbackInterface after signed, callback function hook
     */
    public void deployAsync(
            String abi,
            String bin,
            List<Object> params,
            RemoteSignCallbackInterface remoteSignCallbackInterface)
            throws ContractCodecException, JniException;

    /**
     * deploy contract to fisco bcos node by contract name asynchronously. The contract loader will
     * load the transaction abi information.
     *
     * @param contractName contract function name.
     * @param params contract function parameters
     * @param remoteSignCallbackInterface after signed, callback function hook
     */
    void deployByContractLoaderAsync(
            String contractName,
            List<Object> params,
            RemoteSignCallbackInterface remoteSignCallbackInterface)
            throws ContractCodecException, NoSuchTransactionFileException, JniException;

    /**
     * deploy contract to fisco bcos node by contract name asynchronously.
     *
     * @param contractName target contract name.
     * @param to target contract address.
     * @param functionName contract function name.
     * @param params contract function parameters
     * @param remoteSignCallbackInterface after signed, callback function hook
     */
    void sendTransactionAndGetReceiptByContractLoaderAsync(
            String contractName,
            String to,
            String functionName,
            List<Object> params,
            RemoteSignCallbackInterface remoteSignCallbackInterface)
            throws ContractCodecException, TransactionBaseException, JniException;

    /**
     * send transaction to fisco bcos node by contract name asynchronously.
     *
     * @param to the target contract address.
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param functionName contract function name.
     * @param params contract function parameters
     * @param remoteSignCallbackInterface after signed, callback function hook
     */
    public void sendTransactionAsync(
            String to,
            String abi,
            String functionName,
            List<Object> params,
            RemoteSignCallbackInterface remoteSignCallbackInterface)
            throws ContractCodecException, JniException;

    /**
     * send transaction to fisco bcos node by contract name asynchronously. The contract loader will
     * load the transaction abi information.
     *
     * @param to the target contract address.
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param functionName contract function name.
     * @param params contract function parameters
     * @return CompletableFuture of transaction receipt
     */
    public CompletableFuture<TransactionReceipt> sendTransactionAsync(
            String to, String abi, String functionName, List<Object> params)
            throws ContractCodecException, JniException;

    /**
     * sign based on raw transaction and send to fisco bcos node.
     *
     * @param transactionData raw transaction
     * @param signatureStr signature string.
     * @return TransactionReceipt
     */
    TransactionReceipt encodeAndPush(long transactionData, String signatureStr, int txAttribute)
            throws JniException;

    /**
     * sign based on raw transaction and send to fisco bcos node.
     *
     * @param transactionData raw transaction
     * @param rawTxHash signature byte array.
     * @return TransactionReceipt
     */
    CompletableFuture<TransactionReceipt> signAndPush(
            long transactionData, byte[] rawTxHash, int txAttribute);
}
