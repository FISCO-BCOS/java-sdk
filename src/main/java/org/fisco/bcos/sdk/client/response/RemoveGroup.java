package org.fisco.bcos.sdk.client.response;

import org.fisco.bcos.sdk.client.RPCResponse;

public class RemoveGroup extends RPCResponse<RemoveGroup.Status> {

    public Status getStatus() {
        return getResult();
    }

    public static class Status {
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
            return "Status{" + "code='" + code + '\'' + ", message='" + message + '\'' + '}';
        }
    }
}
