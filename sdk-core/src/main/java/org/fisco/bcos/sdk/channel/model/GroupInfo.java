package org.fisco.bcos.sdk.channel.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

// FIXME: complete this class
public class GroupInfo {
    @JsonProperty("chainID")
    private String chainId;

    @JsonProperty("groupID")
    private String groupId;

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
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
        if (!(o instanceof GroupInfo)) return false;
        GroupInfo groupInfo = (GroupInfo) o;
        return Objects.equals(chainId, groupInfo.chainId)
                && Objects.equals(groupId, groupInfo.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chainId, groupId);
    }

    @Override
    public String toString() {
        return "GroupInfo{" + "chainId='" + chainId + '\'' + ", groupId='" + groupId + '\'' + '}';
    }
}
