package org.fisco.bcos.sdk.amop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmopResponse {
    private static final Logger logger = LoggerFactory.getLogger(AmopResponse.class);
    private Integer errorCode;
    private String errorMessage;
    private String messageID;

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }
}
