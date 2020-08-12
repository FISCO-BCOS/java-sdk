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

import java.util.List;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.model.SolidityConstructor;
import org.fisco.bcos.sdk.model.SolidityFunction;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.builder.FunctionBuilderInterface;
import org.fisco.bcos.sdk.transaction.builder.FunctionBuilderService;
import org.fisco.bcos.sdk.transaction.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.dto.CallRequest;
import org.fisco.bcos.sdk.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.transaction.model.dto.ResultCodeEnum;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionBaseException;
import org.fisco.bcos.sdk.transaction.tools.ContractLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ContractlessTransactionManager @Description: ContractlessTransactionManager
 *
 * @author maojiayu
 * @data Aug 11, 2020 8:04:46 PM
 */
public class ContractlessTransactionManager extends TransactionManager
        implements ContractlessTransactionManagerInterface {
    protected static Logger log = LoggerFactory.getLogger(TransactionManager.class);
    protected final FunctionBuilderInterface functionBuilder;

    /**
     * In file mode, use abi and bin to send transactions.
     *
     * @param client
     * @param cryptoInterface
     * @param groupId
     * @param chainId
     * @param contractLoader
     */
    public ContractlessTransactionManager(
            Client client,
            CryptoInterface cryptoInterface,
            Integer groupId,
            String chainId,
            ContractLoader contractLoader) {
        super(client, cryptoInterface, groupId, chainId);
        this.functionBuilder = new FunctionBuilderService(contractLoader);
    }

    /**
     * Deploy by bin & abi files. Should init with contractLoader.
     *
     * @param contractName
     * @param args
     * @return
     * @throws TransactionBaseException
     */
    @Override
    public TransactionResponse deployByContractLoader(String contractName, List<Object> args)
            throws TransactionBaseException {
        SolidityConstructor constructor = functionBuilder.buildConstructor(contractName, args);
        String signedData = createSignedTransaction(null, constructor.getData());
        return deployAndGetResponse(constructor.getAbi(), signedData);
    }

    @Override
    public void deployByContractLoaderAsync(
            String contractName, List<Object> args, TransactionCallback callback)
            throws TransactionBaseException {
        SolidityConstructor constructor = functionBuilder.buildConstructor(contractName, args);
        String signedData = createSignedTransaction(null, constructor.getData());
        sendTransactionAsync(signedData, callback);
    }

    @Override
    public TransactionReceipt sendTransactionAndGetReceiptByContractLoader(
            String contractName, String contractAddress, String functionName, List<Object> args)
            throws TransactionBaseException {
        SolidityFunction solidityFunction =
                functionBuilder.buildFunction(contractName, functionName, args);
        if (solidityFunction.getFunctionAbi().isConstant()) {
            throw new TransactionBaseException(
                    ResultCodeEnum.PARAMETER_ERROR.getCode(),
                    "Wrong transaction type, actually it's a call");
        }
        String data = functionEncoder.encode(solidityFunction.getFunction());
        return sendTransactionAndGetReceipt(contractAddress, data);
    }

    @Override
    public void sendTransactionAndGetReceiptByContractLoaderAsync(
            String contractName,
            String contractAddress,
            String functionName,
            List<Object> args,
            TransactionCallback callback)
            throws TransactionBaseException {
        SolidityFunction solidityFunction =
                functionBuilder.buildFunction(contractName, functionName, args);
        if (solidityFunction.getFunctionAbi().isConstant()) {
            throw new TransactionBaseException(
                    ResultCodeEnum.PARAMETER_ERROR.getCode(),
                    "Wrong transaction type, actually it's a call");
        }
        String data = functionEncoder.encode(solidityFunction.getFunction());
        sendTransactionAsync(contractAddress, data, callback);
    }

    @Override
    public CallResponse sendCallByContractLoader(
            String contractName, String contractAddress, String functionName, List<Object> args)
            throws TransactionBaseException {
        SolidityFunction solidityFunction =
                functionBuilder.buildFunction(contractName, functionName, args);
        if (!solidityFunction.getFunctionAbi().isConstant()) {
            throw new TransactionBaseException(
                    ResultCodeEnum.PARAMETER_ERROR.getCode(),
                    "Wrong transaction type, actually it's a transaction");
        }
        String data = functionEncoder.encode(solidityFunction.getFunction());
        CallRequest callRequest =
                new CallRequest(getCurrentExternalAccountAddress(), contractAddress, data);
        callRequest.setAbi(solidityFunction.getFunctionAbi());
        return sendCall(callRequest);
    }
}
