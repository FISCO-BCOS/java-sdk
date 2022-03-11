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

package org.fisco.bcos.sdk.v3.client.protocol.response;

import java.util.Objects;
import org.fisco.bcos.sdk.v3.model.JsonRpcResponse;

/**
 * RPC response of ledger call
 *
 * @author Maggie
 */
public class Call extends JsonRpcResponse<Call.CallOutput> {
    public static class CallOutput {
        private long blockNumber;
        private String output;
        private int status;

        public long getBlockNumber() {
            return this.blockNumber;
        }

        public void setBlockNumber(long blockNumber) {
            this.blockNumber = blockNumber;
        }

        public int getStatus() {
            return this.status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getOutput() {
            return this.output;
        }

        public void setOutput(String output) {
            this.output = output;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            CallOutput that = (CallOutput) o;
            return Objects.equals(this.blockNumber, that.blockNumber)
                    && Objects.equals(this.status, that.status)
                    && Objects.equals(this.output, that.output);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.blockNumber, this.status, this.output);
        }

        @Override
        public String toString() {
            return "CallOutput{"
                    + "blockNumber='"
                    + this.blockNumber
                    + '\''
                    + ", status='"
                    + this.status
                    + '\''
                    + ", output='"
                    + this.output
                    + '\''
                    + '}';
        }
    }

    @Override
    public void setResult(CallOutput result) {
        super.setResult(result);
    }

    public CallOutput getCallResult() {
        return this.getResult();
    }
}
