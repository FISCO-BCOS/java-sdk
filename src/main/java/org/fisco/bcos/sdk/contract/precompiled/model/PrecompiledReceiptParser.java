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
package org.fisco.bcos.sdk.contract.precompiled.model;

import java.math.BigInteger;
import org.fisco.bcos.sdk.contract.precompiled.exceptions.PrecompiledException;
import org.fisco.bcos.sdk.model.RetCode;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.TransactionReceiptStatus;

public class PrecompiledReceiptParser {
    private PrecompiledReceiptParser() {}

    public static RetCode parsePrecompiledReceipt(TransactionReceipt receipt)
            throws PrecompiledException {
        try {
            String status = receipt.getStatus();
            if (!"0x0".equals(status)) {
                RetCode retCode =
                        TransactionReceiptStatus.getStatusMessage(status, receipt.getMessage());
                throw new PrecompiledException(retCode.getMessage());
            } else {
                String output = receipt.getOutput();
                int statusValue =
                        new BigInteger(output.substring(2, output.length()), 16).intValue();
                return PrecompiledRetCode.getPrecompiledResponse(statusValue);
            }
        } catch (NumberFormatException e) {
            throw new PrecompiledException(
                    "NumberFormatException when parse receipt, receipt info: "
                            + receipt.toString()
                            + ", error info: "
                            + e.getMessage());
        }
    }
}
