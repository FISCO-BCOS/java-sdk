package org.fisco.bcos.sdk.client.protocol.response;

import org.fisco.bcos.sdk.model.JsonRpcResponse;

public class ErasePeers extends JsonRpcResponse<ErasePeers.ErasePeersStatus> {

    public static class ErasePeersStatus {
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
    }

    public ErasePeersStatus getErasePeersStatus() {
        return getResult();
    }
}
