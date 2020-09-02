package org.fisco.bcos.sdk.amop.topic;

import io.netty.channel.ChannelHandlerContext;
import org.fisco.bcos.sdk.model.AmopMsg;
import org.fisco.bcos.sdk.model.MsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmopMsgIn {
    private static Logger logger = LoggerFactory.getLogger(AmopMsgIn.class);
    private String messageID;
    private byte[] content;
    private String topic;
    private Integer result;
    protected Short type = 0;
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

    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public void sendResponse(byte[] content) {
        if (type == (short) MsgType.AMOP_MULBROADCAST.getType()) {
            // If received a broadcast msg, do not response.
            return;
        }
        AmopMsg msg = new AmopMsg();
        msg.setTopic(topic);
        msg.setSeq(messageID);
        msg.setResult(0);
        msg.setType((short) MsgType.AMOP_RESPONSE.getType());
        msg.setData(content);
        logger.trace(
                "Send response, seq:{} topic:{} content:{}", messageID, topic, new String(content));
        ctx.writeAndFlush(msg.getMessage());
    }
}
