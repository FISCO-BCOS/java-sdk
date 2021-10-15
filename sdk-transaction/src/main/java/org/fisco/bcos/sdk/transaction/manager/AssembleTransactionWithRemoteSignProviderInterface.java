package org.fisco.bcos.sdk.transaction.manager;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.fisco.bcos.sdk.client.protocol.model.tars.TransactionData;
import org.fisco.bcos.sdk.codec.abi.ABICodecException;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.exception.NoSuchTransactionFileException;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionBaseException;
import org.fisco.bcos.sdk.transaction.signer.RemoteSignCallbackInterface;

public interface AssembleTransactionWithRemoteSignProviderInterface {

    /**
     * deploy contract to fisco bcos node asynchronously.
     *
     * @param rawTransaction raw transaction
     * @param remoteSignCallbackInterface after signed, callback function hook
     */
    void deployAsync(
            TransactionData rawTransaction, RemoteSignCallbackInterface remoteSignCallbackInterface)
            throws ABICodecException;

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
            throws ABICodecException;

    /**
     * deploy contract to fisco bcos node by contract name asynchronously. The contract loader will
     * load the transaction abi information.
     *
     * @param contractName contract function name.
     * @param params contract function parameters
     * @param remoteSignCallbackInterface after signed, callback function hook
     * @return
     */
    void deployByContractLoaderAsync(
            String contractName,
            List<Object> params,
            RemoteSignCallbackInterface remoteSignCallbackInterface)
            throws ABICodecException, NoSuchTransactionFileException;

    /**
     * deploy contract to fisco bcos node by contract name asynchronously.
     *
     * @param contractName target contract name.
     * @param to target contract address.
     * @param functionName contract function name.
     * @param params contract function parameters
     * @param remoteSignCallbackInterface after signed, callback function hook
     * @return
     */
    void sendTransactionAndGetReceiptByContractLoaderAsync(
            String contractName,
            String to,
            String functionName,
            List<Object> params,
            RemoteSignCallbackInterface remoteSignCallbackInterface)
            throws ABICodecException, TransactionBaseException;

    /**
     * send transaction to fisco bcos node by contract name asynchronously.
     *
     * @param to the target contract address.
     * @param abi contract abi, which could be obtained by compiling solidity contract.
     * @param functionName contract function name.
     * @param params contract function parameters
     * @param remoteSignCallbackInterface after signed, callback function hook
     * @return
     */
    public CompletableFuture<TransactionReceipt> sendTransactionAsync(
            String to,
            String abi,
            String functionName,
            List<Object> params,
            RemoteSignCallbackInterface remoteSignCallbackInterface)
            throws ABICodecException;

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
            throws ABICodecException;

    /**
     * sign based on raw transaction and send to fisco bcos node.
     *
     * @param rawTransaction raw transaction
     * @param signatureStr signature string.
     * @return TransactionReceipt
     */
    TransactionReceipt encodeAndPush(
            TransactionData rawTransaction, byte[] rawTxHash, String signatureStr);

    /**
     * sign based on raw transaction and send to fisco bcos node.
     *
     * @param rawTransaction raw transaction
     * @param rawTxHash signature byte array.
     * @return TransactionReceipt
     */
    CompletableFuture<TransactionReceipt> signAndPush(
            TransactionData rawTransaction, byte[] rawTxHash);
}
