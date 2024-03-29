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
package org.fisco.bcos.sdk.v3.transaction.codec.decode;

import java.math.BigInteger;
import java.util.function.Function;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.TransactionReceiptStatus;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

public class ReceiptParser {

    private ReceiptParser() {}

    /**
     * parse transaction receipt and get return code.
     *
     * @param receipt transaction receipt
     * @param resultCaller actual decode receipt out function, from receipt to big int
     * @return return code @see RetCode
     * @throws ContractException throw when receipt status not equal to 0
     */
    public static RetCode parseTransactionReceipt(
            TransactionReceipt receipt, Function<TransactionReceipt, BigInteger> resultCaller)
            throws ContractException {
        int status = receipt.getStatus();
        if (status != 0) {
            getErrorStatus(receipt);
        } else {
            RetCode precompiledRetCode = PrecompiledRetCode.CODE_SUCCESS;
            if (receipt.getOutput().equals("0x") || resultCaller == null) {
                return precompiledRetCode;
            }
            BigInteger apply = resultCaller.apply(receipt);
            precompiledRetCode =
                    PrecompiledRetCode.getPrecompiledResponse(
                            apply.intValue(), receipt.getMessage());
            precompiledRetCode.setTransactionReceipt(receipt);
            return precompiledRetCode;
        }
        return PrecompiledRetCode.CODE_SUCCESS;
    }

    /**
     * get revert message when receipt status is not success
     *
     * @param receipt receipt which status not equal to 0
     * @throws ContractException always throw exception with specific error message
     */
    public static void getErrorStatus(TransactionReceipt receipt) throws ContractException {
        RetCode retCode =
                TransactionReceiptStatus.getStatusMessage(
                        receipt.getStatus(), receipt.getMessage());
        Tuple2<Boolean, String> errorOutput = RevertMessageParser.tryResolveRevertMessage(receipt);
        if (errorOutput.getValue1()) {
            throw new ContractException(errorOutput.getValue2(), retCode.getCode(), receipt);
        }
        throw new ContractException(retCode.getMessage(), retCode.getCode(), receipt);
    }

    /**
     * parse call which throws ContractException.
     *
     * @param exception call exception
     * @return ContractException
     */
    public static ContractException parseExceptionCall(ContractException exception) {
        Call.CallOutput callResult = exception.getResponseOutput();
        if (callResult == null) {
            return exception;
        }
        RetCode retCode = parseCallOutput(callResult, exception.getMessage());
        return new ContractException(retCode.getMessage(), retCode.getCode());
    }

    /**
     * parse output of call.
     *
     * @param callResult rpc response of call
     * @param message revert message if exists
     * @return return code @see RetCode
     */
    public static RetCode parseCallOutput(Call.CallOutput callResult, String message) {
        if (callResult.getStatus() != 0) {
            Tuple2<Boolean, String> errorOutput =
                    RevertMessageParser.tryResolveRevertMessage(
                            callResult.getStatus(), callResult.getOutput());
            if (errorOutput.getValue1()) {
                return new RetCode(callResult.getStatus(), errorOutput.getValue2());
            }
            return TransactionReceiptStatus.getStatusMessage(callResult.getStatus(), message);
        }
        return PrecompiledRetCode.CODE_SUCCESS;
    }
}
