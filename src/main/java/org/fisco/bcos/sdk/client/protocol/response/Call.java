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

/**
 * RPC response of ledger call
 *
 * @author Maggie
 */
public class Call extends JsonRpcResponse<Call.CallOutput> {
    public static class CallOutput {
        private String currentBlockNumber;
        private String output;
        private String status;

        public String getCurrentBlockNumber() {
            return currentBlockNumber;
        }

        public void setCurrentBlockNumber(String number) {
            this.currentBlockNumber = number;
        }

        public String getOutput() {
            return output;
        }

        public void setOutput(String output) {
            this.output = output;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public CallOutput getValue() {
        return getResult();
    }

    public void setResult(CallOutput result) {
        super.setResult(result);
    }
}
