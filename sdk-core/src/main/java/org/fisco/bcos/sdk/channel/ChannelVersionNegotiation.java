package org.fisco.bcos.sdk.channel;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.AttributeKey;
import java.net.InetSocketAddress;
import org.fisco.bcos.sdk.channel.model.ChannelProtocol;
import org.fisco.bcos.sdk.channel.model.EnumChannelProtocolVersion;
import org.fisco.bcos.sdk.channel.model.EnumSocketChannelAttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelVersionNegotiation {

    private static Logger logger = LoggerFactory.getLogger(ChannelVersionNegotiation.class);

    public static void setProtocolVersion(
            ChannelHandlerContext ctx, EnumChannelProtocolVersion version, String nodeVersion) {
        ChannelProtocol channelProtocol = new ChannelProtocol();
        channelProtocol.setProtocol(version.getVersionNumber());
        channelProtocol.setNodeVersion(nodeVersion);
        channelProtocol.setEnumProtocol(version);
        ctx.channel()
                .attr(
                        AttributeKey.valueOf(
                                EnumSocketChannelAttributeKey.CHANNEL_PROTOCOL_KEY.getKey()))
                .set(channelProtocol);
    }

    public static void setCtxAttibuteValue(ChannelHandlerContext ctx, String key, String value) {

        AttributeKey<String> attributeKey = AttributeKey.valueOf(key);
        ctx.channel().attr(attributeKey).set(value);
    }

    public static EnumChannelProtocolVersion getProtocolVersion(ChannelHandlerContext ctx) {

        String host = getPeerHost(ctx);
        AttributeKey<ChannelProtocol> attributeKey =
                AttributeKey.valueOf(EnumSocketChannelAttributeKey.CHANNEL_PROTOCOL_KEY.getKey());
        if (ctx.channel().hasAttr(attributeKey)) {
            ChannelProtocol channelProtocol = ctx.channel().attr(attributeKey).get();
            if (null != channelProtocol) {
                return channelProtocol.getEnumProtocol();
            } else {
                logger.warn(" channel has attr but get null, host: {}", host);
            }
        } else {
            logger.warn(" channel has not attr, host: {}", host);
        }

        return null;
    }

    public static String getPeerHost(ChannelHandlerContext ctx) {

        InetSocketAddress inetSocketAddress = ((SocketChannel) ctx.channel()).remoteAddress();
        String hostAddress = inetSocketAddress.getAddress().getHostName();
        Integer port = inetSocketAddress.getPort();
        return hostAddress + ":" + port;
    }

    public static boolean isChannelAvailable(ChannelHandlerContext ctx) {

        return (null != ctx) && ctx.channel().isActive() && (null != getProtocolVersion(ctx));
    }
}
