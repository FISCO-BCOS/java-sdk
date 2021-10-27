package org.fisco.bcos.sdk.client.protocol.response;

import java.util.List;
import org.fisco.bcos.sdk.model.JsonRpcResponse;

public class BcosGroupNodeInfo extends JsonRpcResponse<BcosGroupNodeInfo.GroupNodeInfo> {

    @Override
    public GroupNodeInfo getResult() {
        return super.getResult();
    }

    @Override
    public void setResult(GroupNodeInfo result) {
        super.setResult(result);
    }

    public static class GroupNodeInfo {
        private int type;
        private String iniConfig;
        private String name;
        private List<ServiceInfo> serviceInfoList;

        static class ServiceInfo {
            private String serviceName;
            private int type;

            public String getServiceName() {
                return serviceName;
            }

            public void setServiceName(String serviceName) {
                this.serviceName = serviceName;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            @Override
            public String toString() {
                return "ServiceInfo{"
                        + "serviceName='"
                        + serviceName
                        + '\''
                        + ", type="
                        + type
                        + '}';
            }
        }

        @Override
        public String toString() {
            return "GroupNodeInfo{"
                    + "type="
                    + type
                    + ", iniConfig='"
                    + iniConfig
                    + '\''
                    + ", name='"
                    + name
                    + '\''
                    + ", serviceInfoList="
                    + serviceInfoList
                    + '}';
        }
    }
}
