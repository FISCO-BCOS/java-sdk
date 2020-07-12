package org.fisco.bcos.sdk.client.response;

import org.fisco.bcos.sdk.client.RPCResponse;

public class QueryGroupStatus extends RPCResponse<QueryGroupStatus.Status> {

    public Status getStatus() {
        return getResult();
    }

    public static class Status {
        private String code;
        private String status;
        private String message;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "Status{"
                    + "code='"
                    + code
                    + '\''
                    + ", status='"
                    + status
                    + '\''
                    + ", message='"
                    + message
                    + '\''
                    + '}';
        }
    }
}
