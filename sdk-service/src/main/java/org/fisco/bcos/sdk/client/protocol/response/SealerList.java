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

import java.util.List;
import org.fisco.bcos.sdk.model.JsonRpcResponse;

public class SealerList extends JsonRpcResponse<List<SealerList.Sealer>> {

    public static class Sealer {
        private String nodeID;
        private int weight;

        @Override
        public String toString() {
            return "Sealer{" + "nodeID='" + nodeID + '\'' + ", weight=" + weight + '}';
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
    };

    public List<Sealer> getSealerList() {
        return getResult();
    }
}
