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
import org.fisco.bcos.sdk.v3.client.protocol.model.GroupNodeIniInfo;
import org.fisco.bcos.sdk.v3.model.JsonRpcResponse;
import org.fisco.bcos.sdk.v3.utils.ObjectMapperFactory;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BcosGroupNodeInfo extends JsonRpcResponse<BcosGroupNodeInfo.GroupNodeInfo> {

    @Override
    public GroupNodeInfo getResult() {
        return super.getResult();
    }

    @Override
    @JsonDeserialize(using = BcosGroupNodeInfo.GroupNodeInfoDeserializer.class)
    public void setResult(GroupNodeInfo result) {
        super.setResult(result);
    }

    public static class Protocol {

        private long compatibilityVersion;
        private long minSupportedVersion;
        private long maxSupportedVersion;

        public long getCompatibilityVersion() {
            return compatibilityVersion;
        }

        public void setCompatibilityVersion(long compatibilityVersion) {
            this.compatibilityVersion = compatibilityVersion;
        }

        public long getMinSupportedVersion() {
            return minSupportedVersion;
        }

        public void setMinSupportedVersion(long minSupportedVersion) {
            this.minSupportedVersion = minSupportedVersion;
        }

        public long getMaxSupportedVersion() {
            return maxSupportedVersion;
        }

        public void setMaxSupportedVersion(long maxSupportedVersion) {
            this.maxSupportedVersion = maxSupportedVersion;
        }

        @Override
        public String toString() {
            return "Protocol{"
                    + "compatibilityVersion="
                    + compatibilityVersion
                    + ", minSupportedVersion="
                    + minSupportedVersion
                    + ", maxSupportedVersion="
                    + maxSupportedVersion
                    + '}';
        }
    }

    public static class GroupNodeInfo {
        private int type;
        private GroupNodeIniInfo iniConfig;
        private String name;

        @Override
        public String toString() {
            return "GroupNodeInfo{"
                    + "type="
                    + type
                    + ", iniConfig="
                    + iniConfig
                    + ", name='"
                    + name
                    + '\''
                    + ", serviceInfoList="
                    + serviceInfoList
                    + ", protocol="
                    + protocol
                    + '}';
        }

        public Protocol getProtocol() {
            return protocol;
        }

        public void setProtocol(Protocol protocol) {
            this.protocol = protocol;
        }

        private List<ServiceInfo> serviceInfoList;
        private Protocol protocol;

        public GroupNodeIniInfo getIniConfig() {
            return iniConfig;
        }

        @JsonDeserialize(converter = BcosGroupNodeInfo.GroupNodeIniInfoConvert.class)
        public void setIniConfig(GroupNodeIniInfo iniConfig) {
            this.iniConfig = iniConfig;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<ServiceInfo> getServiceInfoList() {
            return serviceInfoList;
        }

        public void setServiceInfoList(List<ServiceInfo> serviceInfoList) {
            this.serviceInfoList = serviceInfoList;
        }

        static class ServiceInfo {
            private String serviceName;
            private int type;

            public String getServiceName() {
                return serviceName;
            }

            public void setServiceName(String serviceName) {
                this.serviceName = serviceName;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            @Override
            public String toString() {
                return "ServiceInfo{"
                        + "serviceName='"
                        + serviceName
                        + '\''
                        + ", type="
                        + type
                        + '}';
            }
        }
    }

    public static class GroupNodeInfoDeserializer
            extends JsonDeserializer<BcosGroupNodeInfo.GroupNodeInfo> {
        private final ObjectReader objectReader = ObjectMapperFactory.getObjectReader();

        @Override
        public BcosGroupNodeInfo.GroupNodeInfo deserialize(
                JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            if (jsonParser.getCurrentToken() != JsonToken.VALUE_NULL) {
                return this.objectReader.readValue(
                        jsonParser, BcosGroupNodeInfo.GroupNodeInfo.class);
            } else {
                return null; // null is wrapped by Optional in above getter
            }
        }
    }

    public static class GroupNodeIniInfoConvert implements Converter<String, GroupNodeIniInfo> {
        @Override
        public GroupNodeIniInfo convert(String value) {
            try {
                return new ObjectMapper().readValue(value, GroupNodeIniInfo.class);
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
            return typeFactory.constructSimpleType(GroupNodeIniInfo.class, null);
        }
    }
}
