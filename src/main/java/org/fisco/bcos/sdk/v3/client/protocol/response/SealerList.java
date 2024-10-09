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

import java.util.List;
import java.util.Objects;
import org.fisco.bcos.sdk.v3.model.JsonRpcResponse;

public class SealerList extends JsonRpcResponse<List<SealerList.Sealer>> {

    public static class Sealer {
        private String nodeID;
        private int weight;
        private int termWeight;

        @Override
        public String toString() {
            return "Sealer{"
                    + "nodeID='"
                    + nodeID
                    + '\''
                    + ", weight="
                    + weight
                    + "'"
                    + "termWeight='"
                    + termWeight
                    + "'"
                    + '}';
        }

        public String getNodeID() {
            return nodeID;
        }

        public void setNodeID(String nodeID) {
            this.nodeID = nodeID;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public int getTermWeight() {
            return termWeight;
        }

        public void setTermWeight(int termWeight) {
            this.termWeight = termWeight;
        }

        @Override
        public int hashCode() {
            return this.getNodeID().hashCode() + this.getWeight();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Sealer) {
                Sealer sealer = (Sealer) obj;
                return Objects.equals(this.nodeID, sealer.getNodeID());
            }
            return super.equals(obj);
        }
    }

    public List<Sealer> getSealerList() {
        return getResult();
    }
}
