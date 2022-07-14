package org.fisco.bcos.sdk.v3.client.protocol.response;

import java.util.List;
import org.fisco.bcos.sdk.v3.model.JsonRpcResponse;

public class BcosGroupList extends JsonRpcResponse<BcosGroupList.GroupList> {

    @Override
    public GroupList getResult() {
        return super.getResult();
    }

    @Override
    public void setResult(GroupList result) {
        super.setResult(result);
    }

    public static class GroupList {
        private int code;
        private String msg;
        private List<String> groupList;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public List<String> getGroupList() {
            return groupList;
        }

        public void setGroupList(List<String> groupList) {
            this.groupList = groupList;
        }

        @Override
        public String toString() {
            return "GroupList{"
                    + "code="
                    + code
                    + ", msg='"
                    + msg
                    + '\''
                    + ", groupList="
                    + groupList
                    + '}';
        }
    };
}
