package org.fisco.bcos.sdk.client.protocol.response;

import org.fisco.bcos.sdk.model.JsonRpcResponse;

public class AddPeers extends JsonRpcResponse<AddPeers.AddPeersStatus> {

    public static class AddPeersStatus {
        private String code;
        private String message;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "AddPeersStatus{"
                    + "code='"
                    + code
                    + '\''
                    + ", message='"
                    + message
                    + '\''
                    + '}';
        }
    }

    public AddPeersStatus getAddPeersStatus() {
        return getResult();
    }
}
