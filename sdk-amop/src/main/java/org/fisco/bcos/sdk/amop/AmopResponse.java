package org.fisco.bcos.sdk.amop;

import io.netty.channel.ChannelHandlerContext;
import org.fisco.bcos.sdk.amop.topic.AmopMsgHandler;
import org.fisco.bcos.sdk.amop.topic.AmopMsgIn;
import org.fisco.bcos.sdk.amop.topic.TopicType;
import org.fisco.bcos.sdk.model.AmopMsg;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.MsgType;
import org.fisco.bcos.sdk.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmopResponse {
    private static Logger logger = LoggerFactory.getLogger(AmopResponse.class);
    private Integer errorCode;
    private String errorMessage;
    private String messageID;
    private ChannelHandlerContext ctx;
    private AmopMsgIn amopMsgIn;

    public AmopResponse(Response response) {
        this.setCtx(response.getCtx());
        this.setErrorCode(response.getErrorCode());
        this.setErrorMessage(response.getErrorMessage());
        this.setMessageID(response.getMessageID());
        if (response.getErrorCode().intValue() == 0) {
            amopMsgIn = new AmopMsgIn();
            Message msg = new Message();
            msg.setSeq(response.getMessageID());
            msg.setResult(response.getErrorCode());
            msg.setType((short) MsgType.AMOP_RESPONSE.getType());
            msg.setData(response.getContentBytes());
            AmopMsg amopMsg = new AmopMsg(msg);
            try {
                amopMsg.decodeAmopBody(response.getContentBytes());
            } catch (Exception e) {
                logger.error("Receive an invalid amop response, seq:{}", response.getMessageID());
                return;
            }
            amopMsgIn.setTopic(amopMsg.getTopic());
            if (AmopMsgHandler.isPrivateTopic(amopMsg.getTopic())) {
                amopMsgIn.setTopicType(TopicType.PRIVATE_TOPIC);
                amopMsgIn.setTopic(AmopMsgHandler.removePrivateTopicPrefix(amopMsg.getTopic()));
            }
            amopMsgIn.setContent(amopMsg.getData());
            amopMsgIn.setMessageID(response.getMessageID());
            amopMsgIn.setCtx(response.getCtx());
            amopMsgIn.setType((short) MsgType.AMOP_RESPONSE.getType());
            amopMsgIn.setResult(response.getErrorCode());
        }
    }

    public AmopMsgIn getAmopMsgIn() {
        return amopMsgIn;
    }

    public void setAmopMsgIn(AmopMsgIn amopMsgIn) {
        this.amopMsgIn = amopMsgIn;
    }

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

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }
}
