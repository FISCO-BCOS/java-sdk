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
package org.fisco.bcos.sdk.model;

import java.math.BigInteger;
import org.fisco.bcos.sdk.client.protocol.response.Call;
import org.fisco.bcos.sdk.contract.exceptions.ContractException;
import org.fisco.bcos.sdk.contract.precompiled.model.PrecompiledRetCode;

public class ReceiptParser {
    private ReceiptParser() {}

    public static RetCode parsePrecompiledReceipt(TransactionReceipt receipt)
            throws ContractException {
        try {
            String status = receipt.getStatus();
            if (!"0x0".equals(status)) {
                RetCode retCode =
                        TransactionReceiptStatus.getStatusMessage(status, receipt.getMessage());
                throw new ContractException(retCode.getMessage());
            } else {
                String output = receipt.getOutput();
                int statusValue =
                        new BigInteger(output.substring(2, output.length()), 16).intValue();
                return PrecompiledRetCode.getPrecompiledResponse(statusValue, receipt.getMessage());
            }
        } catch (NumberFormatException e) {
            throw new ContractException(
                    "NumberFormatException when parse receipt, receipt info: "
                            + receipt.toString()
                            + ", error info: "
                            + e.getMessage());
        }
    }

    public static ContractException parseExceptionCall(ContractException exception) {
        Call.CallOutput callResult = exception.getResponseOutput();
        if (callResult == null) {
            return new ContractException(exception.getMessage(), exception);
        }
        try {
            if (!callResult.getStatus().equals("0x0")) {
                RetCode retCode =
                        TransactionReceiptStatus.getStatusMessage(
                                callResult.getStatus(), exception.getMessage());
                return new ContractException(retCode.getMessage(), retCode.getCode());
            }
            int statusValue =
                    new BigInteger(
                                    callResult
                                            .getOutput()
                                            .substring(2, callResult.getOutput().length()),
                                    16)
                            .intValue();
            RetCode ret = PrecompiledRetCode.getPrecompiledResponse(statusValue, "");
            return new ContractException(ret.getMessage(), ret.getCode());
        } catch (Exception e) {
            return new ContractException(exception.getMessage(), e);
        }
    }
}
