package org.fisco.bcos.sdk.v3.test;

import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.model.ConstantConfig;

public class DestroyElegantTest {
    private static final String configFile =
            BcosSDKTest.class
                    .getClassLoader()
                    .getResource(ConstantConfig.CONFIG_FILE_NAME)
                    .getPath();

    public static void main(String[] args) {
        BcosSDK sdk = BcosSDK.build(configFile);
        Client client = sdk.getClient("group0");
        client.start();
        client.stop();
        client.destroy();
    }
}
