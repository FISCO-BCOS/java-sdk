package org.fisco.bcos.sdk.transaction.manager;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.signer.RemoteSignCallbackInterface;

public interface AssembleTransactionWithRemoteSignProviderInterface {

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
     * send transaction to fisco bcos node by contract name asynchronously. The contract loader will
     * load the transaction abi information.
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
}
