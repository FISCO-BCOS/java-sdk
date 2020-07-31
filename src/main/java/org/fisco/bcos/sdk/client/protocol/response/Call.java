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

package org.fisco.bcos.sdk.client.protocol.response;

import java.math.BigInteger;
import java.util.Objects;
import org.fisco.bcos.sdk.utils.Numeric;

/**
 * RPC response of ledger call
 *
 * @author Maggie
 */
public class Call extends JsonRpcResponse<Call.CallOutput> {
    public static class CallOutput {
        private String currentBlockNumber;
        private String status;
        private String output;

        public BigInteger getCurrentBlockNumber() {
            return Numeric.decodeQuantity(currentBlockNumber);
        }

        public void setCurrentBlockNumber(String currentBlockNumber) {
            this.currentBlockNumber = currentBlockNumber;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getOutput() {
            return output;
        }

        public void setOutput(String output) {
            this.output = output;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CallOutput that = (CallOutput) o;
            return Objects.equals(
                            Numeric.decodeQuantity(currentBlockNumber),
                            Numeric.decodeQuantity(that.currentBlockNumber))
                    && Objects.equals(status, that.status)
                    && Objects.equals(output, that.output);
        }

        @Override
        public int hashCode() {
            return Objects.hash(Numeric.decodeQuantity(currentBlockNumber), status, output);
        }

        @Override
        public String toString() {
            return "CallOutput{"
                    + "currentBlockNumber='"
                    + currentBlockNumber
                    + '\''
                    + ", status='"
                    + status
                    + '\''
                    + ", output='"
                    + output
                    + '\''
                    + '}';
        }
    }

    public void setResult(CallOutput result) {
        super.setResult(result);
    }

    public CallOutput getCallResult() {
        return getResult();
    }
}
