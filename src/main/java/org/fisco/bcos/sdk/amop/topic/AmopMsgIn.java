package org.fisco.bcos.sdk.amop.topic;

import io.netty.channel.ChannelHandlerContext;
import org.fisco.bcos.sdk.model.AmopMsg;
import org.fisco.bcos.sdk.model.MsgType;

public class AmopMsgIn {
    private String messageID;
    private byte[] content;
    private String topic;
    private Integer result;
    private ChannelHandlerContext ctx;

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public void sendResponse(byte[] content) {
        AmopMsg msg = new AmopMsg();
        msg.setTopic(topic);
        msg.setSeq(messageID);
        msg.setResult(0);
        msg.setType((short) MsgType.AMOP_RESPONSE.getType());
        msg.setData(content);
        System.out.println("Send response:" + messageID + " topic:" + topic);
        ctx.writeAndFlush(msg.getMessage());
    }
}
