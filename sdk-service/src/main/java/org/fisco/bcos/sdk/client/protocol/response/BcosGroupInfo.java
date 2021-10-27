package org.fisco.bcos.sdk.client.protocol.response;

import java.util.List;
import org.fisco.bcos.sdk.model.JsonRpcResponse;

public class BcosGroupInfo extends JsonRpcResponse<BcosGroupInfo.GroupInfo> {

    @Override
    public GroupInfo getResult() {
        return super.getResult();
    }

    @Override
    public void setResult(GroupInfo result) {
        super.setResult(result);
    }

    public static class GroupInfo {

        private String chainID;
        private String groupID;
        private String genesisConfig;
        private String iniConfig;

        private List<BcosGroupNodeInfo.GroupNodeInfo> nodeList;

        public String getChainID() {
            return chainID;
        }

        public void setChainID(String chainID) {
            this.chainID = chainID;
        }

        public String getGroupID() {
            return groupID;
        }

        public void setGroupID(String groupID) {
            this.groupID = groupID;
        }

        public String getGenesisConfig() {
            return genesisConfig;
        }

        public void setGenesisConfig(String genesisConfig) {
            this.genesisConfig = genesisConfig;
        }

        public String getIniConfig() {
            return iniConfig;
        }

        public void setIniConfig(String iniConfig) {
            this.iniConfig = iniConfig;
        }

        public List<BcosGroupNodeInfo.GroupNodeInfo> getNodeList() {
            return nodeList;
        }

        public void setNodeList(List<BcosGroupNodeInfo.GroupNodeInfo> nodeList) {
            this.nodeList = nodeList;
        }

        @Override
        public String toString() {
            return "GroupInfo{"
                    + "chainID='"
                    + chainID
                    + '\''
                    + ", groupID='"
                    + groupID
                    + '\''
                    + ", genesisConfig='"
                    + genesisConfig
                    + '\''
                    + ", iniConfig='"
                    + iniConfig
                    + '\''
                    + ", nodeList="
                    + nodeList
                    + '}';
        }
    }
}
