package org.fisco.bcos.sdk.v3.client.protocol.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import java.io.IOException;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.protocol.model.GroupNodeGenesisInfo;
import org.fisco.bcos.sdk.v3.model.JsonRpcResponse;
import org.fisco.bcos.sdk.v3.utils.ObjectMapperFactory;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BcosGroupInfo extends JsonRpcResponse<BcosGroupInfo.GroupInfo> {

    @Override
    public GroupInfo getResult() {
        return super.getResult();
    }

    @Override
    @JsonDeserialize(using = BcosGroupInfo.GroupInfoDeserializer.class)
    public void setResult(GroupInfo result) {
        super.setResult(result);
    }

    public static class GroupInfo {

        private String chainID;
        private String groupID;
        private GroupNodeGenesisInfo genesisConfig;

        private List<BcosGroupNodeInfo.GroupNodeInfo> nodeList;

        public String getChainID() {
            return chainID;
        }

        public void setChainID(String chainID) {
            this.chainID = chainID;
        }

        public String getGroupID() {
            return groupID;
        }

        public void setGroupID(String groupID) {
            this.groupID = groupID;
        }

        public List<BcosGroupNodeInfo.GroupNodeInfo> getNodeList() {
            return nodeList;
        }

        public void setNodeList(List<BcosGroupNodeInfo.GroupNodeInfo> nodeList) {
            this.nodeList = nodeList;
        }

        public GroupNodeGenesisInfo getGenesisConfig() {
            return genesisConfig;
        }

        @JsonDeserialize(converter = BcosGroupInfo.GroupNodeGenesisInfoConvert.class)
        public void setGenesisConfig(GroupNodeGenesisInfo genesisConfig) {
            this.genesisConfig = genesisConfig;
        }

        @Override
        public String toString() {
            return "GroupInfo{"
                    + "chainID='"
                    + chainID
                    + '\''
                    + ", groupID='"
                    + groupID
                    + '\''
                    + ", genesisConfig="
                    + genesisConfig
                    + ", nodeList="
                    + nodeList
                    + '}';
        }
    }

    public static class GroupInfoDeserializer extends JsonDeserializer<BcosGroupInfo.GroupInfo> {
        private final ObjectReader objectReader = ObjectMapperFactory.getObjectReader();

        @Override
        public BcosGroupInfo.GroupInfo deserialize(
                JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            if (jsonParser.getCurrentToken() != JsonToken.VALUE_NULL) {
                return this.objectReader.readValue(jsonParser, BcosGroupInfo.GroupInfo.class);
            } else {
                return null; // null is wrapped by Optional in above getter
            }
        }
    }

    public static class GroupNodeGenesisInfoConvert
            implements Converter<String, GroupNodeGenesisInfo> {
        @Override
        public GroupNodeGenesisInfo convert(String value) {
            try {
                return new ObjectMapper().readValue(value, GroupNodeGenesisInfo.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public JavaType getInputType(TypeFactory typeFactory) {
            return typeFactory.constructSimpleType(String.class, null);
        }

        @Override
        public JavaType getOutputType(TypeFactory typeFactory) {
            return typeFactory.constructSimpleType(GroupNodeGenesisInfo.class, null);
        }
    }
}
