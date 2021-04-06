package org.fisco.bcos.sdk.config.model;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptoProviderConfig {
    private static Logger logger = LoggerFactory.getLogger(CryptoProviderConfig.class);
    private String type;

    protected CryptoProviderConfig() {}

    public CryptoProviderConfig(ConfigProperty configProperty) {
        Map<String, Object> cryptoProvider = configProperty.getCryptoProvider();
        if (cryptoProvider != null) {
            this.type = ConfigProperty.getValue(cryptoProvider, "type", "ssm");
        } else {
            type = "ssm";
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
