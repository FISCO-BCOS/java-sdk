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
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.TransactionReceiptStatus;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReceiptParser {
    private static final Logger logger = LoggerFactory.getLogger(ReceiptParser.class);
    private static final String PRECOMPILED_PREFIX = "00000000000000000000000000000000000";
    private static final String PRECOMPILED_NAME_PREFIX = "/sys/";

    private ReceiptParser() {}

    /**
     * parse transaction receipt and get return code.
     *
     * @param receipt @See TransactionReceipt
     * @return return code @see RetCode
     */
    public static RetCode parseTransactionReceipt(TransactionReceipt receipt)
            throws ContractException {
        RetCode retCode;
        try {
            int status = receipt.getStatus();
            if (status != 0) {
                retCode = TransactionReceiptStatus.getStatusMessage(status, receipt.getMessage());
                Tuple2<Boolean, String> errorOutput =
                        RevertMessageParser.tryResolveRevertMessage(receipt);
                if (errorOutput.getValue1()) {
                    throw new ContractException(
                            errorOutput.getValue2(), retCode.getCode(), receipt);
                }
                throw new ContractException(retCode.getMessage(), retCode.getCode(), receipt);
            } else {
                if (Hex.trimPrefix(receipt.getTo()).startsWith(ReceiptParser.PRECOMPILED_PREFIX)
                        || receipt.getTo().startsWith(ReceiptParser.PRECOMPILED_NAME_PREFIX)) {
                    String output = receipt.getOutput();
                    RetCode precompiledRetCode = PrecompiledRetCode.CODE_SUCCESS;
                    if (output.equals("0x")) {
                        return precompiledRetCode;
                    }
                    precompiledRetCode = getPrecompiledRetCode(output, receipt.getMessage());
                    if (receipt.getMessage() == null || receipt.getMessage().isEmpty()) {
                        receipt.setMessage(PrecompiledRetCode.CODE_SUCCESS.getMessage());
                    }
                    precompiledRetCode.setTransactionReceipt(receipt);
                    return precompiledRetCode;
                }
                return PrecompiledRetCode.CODE_SUCCESS;
            }
        } catch (NumberFormatException e) {
            throw new ContractException(
                    "NumberFormatException when parse receipt, receipt info: "
                            + receipt
                            + ", error info: "
                            + e.getMessage());
        }
    }

    private static RetCode getPrecompiledRetCode(String output, String message) {
        RetCode retCode;
        try {
            // output with prefix
            int statusValue = new BigInteger(output.substring(2), 16).intValue();
            retCode = PrecompiledRetCode.getPrecompiledResponse(statusValue, message);
            return retCode;
        } catch (Exception e) {
            logger.debug(
                    "try to parse the output failed, output: {}, exception: {}",
                    output,
                    e.getMessage());
            retCode = PrecompiledRetCode.CODE_SUCCESS;
            return retCode;
        }
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
            return new ContractException(exception.getMessage(), exception);
        }
        RetCode retCode =
                parseCallOutput(
                        callResult,
                        exception.getMessage(),
                        exception.getReceipt() == null ? "" : exception.getReceipt().getTo());
        return new ContractException(retCode.getMessage(), retCode.getCode());
    }

    /**
     * parse output of call.
     *
     * @param callResult rpc response of call
     * @param message revert message if exists
     * @return return code @see RetCode
     */
    public static RetCode parseCallOutput(Call.CallOutput callResult, String message, String to) {
        if (callResult.getStatus() != 0) {
            Tuple2<Boolean, String> errorOutput =
                    RevertMessageParser.tryResolveRevertMessage(
                            callResult.getStatus(), callResult.getOutput());
            if (errorOutput.getValue1()) {
                return new RetCode(callResult.getStatus(), errorOutput.getValue2());
            }
            return TransactionReceiptStatus.getStatusMessage(callResult.getStatus(), message);
        }
        try {
            if (to == null || to.isEmpty()) {
                return PrecompiledRetCode.CODE_UNKNOWN_FAILED;
            }
            logger.info("parseCallOutput: to:{}", to);
            if (Hex.trimPrefix(to).startsWith(ReceiptParser.PRECOMPILED_PREFIX)
                    || to.startsWith(ReceiptParser.PRECOMPILED_NAME_PREFIX)) {
                return getPrecompiledRetCode(callResult.getOutput(), message);
            }
            return PrecompiledRetCode.CODE_SUCCESS;
        } catch (Exception e) {
            return PrecompiledRetCode.CODE_SUCCESS;
        }
    }
}
