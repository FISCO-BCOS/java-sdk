package org.fisco.bcos.sdk.channel.model;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;

public class HeartBeatParser {

    public EnumChannelProtocolVersion version;

    public HeartBeatParser(EnumChannelProtocolVersion version) {
        this.version = version;
    }

    public EnumChannelProtocolVersion getVersion() {
        return version;
    }

    public void setVersion(EnumChannelProtocolVersion version) {
        this.version = version;
    }

    public byte[] encode(String value) throws JsonProcessingException {

        byte[] result = null;

        switch (getVersion()) {
            case VERSION_1:
                {
                    result = value.getBytes();
                }
                break;
            default:
                {
                    NodeHeartbeat nodeHeartbeat = new NodeHeartbeat();
                    nodeHeartbeat.setHeartBeat(Integer.parseInt(value));
                    result = ObjectMapperFactory.getObjectMapper().writeValueAsBytes(nodeHeartbeat);
                }
                break;
        }

        return result;
    }

    public NodeHeartbeat decode(String data)
            throws JsonParseException, JsonMappingException, IOException {
        NodeHeartbeat nodeHeartbeat = new NodeHeartbeat();

        switch (getVersion()) {
            case VERSION_1:
                {
                    nodeHeartbeat.setHeartBeat(Integer.parseInt(data));
                }
                break;
            default:
                {
                    nodeHeartbeat =
                            ObjectMapperFactory.getObjectMapper()
                                    .readValue(data, NodeHeartbeat.class);
                }
                break;
        }

        return nodeHeartbeat;
    }
}
