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
package org.fisco.bcos.sdk.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class BlockNumberNotification {
    private String blockNumber;

    @JsonProperty("groupID")
    private String groupId;

    public BlockNumberNotification() {}

    public BlockNumberNotification(String groupId, String blockNumber) {
        this.groupId = groupId;
        this.blockNumber = blockNumber;
    }

    public String getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockNumberNotification that = (BlockNumberNotification) o;
        return Objects.equals(blockNumber, that.blockNumber)
                && Objects.equals(groupId, that.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockNumber, groupId);
    }

    @Override
    public String toString() {
        return "BlockNumberNotification{"
                + "blockNumber='"
                + blockNumber
                + '\''
                + ", groupId='"
                + groupId
                + '\''
                + '}';
    }
}
