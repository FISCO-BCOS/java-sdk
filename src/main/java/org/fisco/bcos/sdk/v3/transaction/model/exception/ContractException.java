/**
 * Copyright 2014-2020 [fisco-dev]
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fisco.bcos.sdk.v3.transaction.model.exception;

import java.util.Objects;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;

public class ContractException extends Exception {
    private Call.CallOutput responseOutput = null;
    private int errorCode;
    private TransactionReceipt receipt;

    public ContractException(String errorMessage, int errorCode) {
        super(errorMessage);
        this.errorCode = errorCode;
    }

    public ContractException(String errorMessage, int errorCode, TransactionReceipt receipt) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.receipt = receipt;
    }

    public ContractException(String message) {
        super(message);
    }

    public ContractException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContractException(String message, Throwable cause, Call.CallOutput responseOutput) {
        super(message, cause);
        this.responseOutput = responseOutput;
    }

    public ContractException(String message, Call.CallOutput responseOutput) {
        super(message);
        this.responseOutput = responseOutput;
    }

    public Call.CallOutput getResponseOutput() {
        return this.responseOutput;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public void setResponseOutput(Call.CallOutput responseOutput) {
        this.responseOutput = responseOutput;
    }

    public TransactionReceipt getReceipt() {
        return receipt;
    }

    public void setReceipt(TransactionReceipt receipt) {
        this.receipt = receipt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContractException that = (ContractException) o;
        return errorCode == that.errorCode && Objects.equals(responseOutput, that.responseOutput);
    }

    @Override
    public int hashCode() {
        return Objects.hash(responseOutput, errorCode);
    }

    @Override
    public String toString() {
        return "ContractException{"
                + "responseOutput="
                + (responseOutput == null ? "null" : responseOutput.toString())
                + ", errorCode="
                + errorCode
                + '}';
    }
}
