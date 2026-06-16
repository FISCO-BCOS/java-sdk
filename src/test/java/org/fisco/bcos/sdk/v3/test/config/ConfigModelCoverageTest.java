/*
 * Copyright 2014-2020  [fisco-dev]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package org.fisco.bcos.sdk.v3.test.config;

import java.util.Arrays;
import java.util.Collections;
import org.fisco.bcos.sdk.v3.config.Config;
import org.fisco.bcos.sdk.v3.config.ConfigOption;
import org.fisco.bcos.sdk.v3.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.v3.config.model.AccountConfig;
import org.fisco.bcos.sdk.v3.config.model.AmopConfig;
import org.fisco.bcos.sdk.v3.config.model.AmopTopic;
import org.fisco.bcos.sdk.v3.config.model.ConfigProperty;
import org.fisco.bcos.sdk.v3.config.model.CryptoMaterialConfig;
import org.fisco.bcos.sdk.v3.config.model.NetworkConfig;
import org.fisco.bcos.sdk.v3.config.model.ThreadPoolConfig;
import org.junit.Assert;
import org.junit.Test;

public class ConfigModelCoverageTest {

    private static final String CONFIG_PATH = "src/test/resources/config/config-example.toml";
    private static final String GM_CONFIG_PATH =
            "src/test/resources/config/config-example-gm.toml";

    // ======================= ConfigOption loaded from TOML =======================

    @Test
    public void testLoadConfigOption() throws ConfigException {
        ConfigOption configOption = Config.load(CONFIG_PATH);
        Assert.assertNotNull(configOption.getCryptoMaterialConfig());
        Assert.assertNotNull(configOption.getAccountConfig());
        Assert.assertNotNull(configOption.getAmopConfig());
        Assert.assertNotNull(configOption.getNetworkConfig());
        Assert.assertNotNull(configOption.getThreadPoolConfig());
        Assert.assertNotNull(configOption.getConfigProperty());
        Assert.assertNotNull(configOption.getJniConfig());
        Assert.assertFalse(configOption.getCryptoMaterialConfig().getUseSmCrypto());
        Assert.assertEquals("group0", configOption.getNetworkConfig().getDefaultGroup());
        Assert.assertNotNull(configOption.toString());
    }

    @Test
    public void testConfigOptionReload() throws ConfigException {
        ConfigOption configOption = Config.load(CONFIG_PATH);
        configOption.reloadConfig();
        Assert.assertFalse(configOption.getCryptoMaterialConfig().getUseSmCrypto());
    }

    @Test
    public void testConfigOptionGenerateJniConfig() throws ConfigException {
        ConfigOption configOption = Config.load(CONFIG_PATH);
        Assert.assertNotNull(configOption.generateJniConfig());
    }

    @Test
    public void testConfigOptionSetters() throws ConfigException {
        ConfigOption configOption = new ConfigOption();
        CryptoMaterialConfig crypto = new CryptoMaterialConfig();
        AccountConfig account = new AccountConfig();
        AmopConfig amop = new AmopConfig();
        NetworkConfig network = new NetworkConfig();
        ThreadPoolConfig threadPool = new ThreadPoolConfig();
        ConfigProperty property = new ConfigProperty();

        configOption.setCryptoMaterialConfig(crypto);
        configOption.setAccountConfig(account);
        configOption.setAmopConfig(amop);
        configOption.setNetworkConfig(network);
        configOption.setThreadPoolConfig(threadPool);
        configOption.setConfigProperty(property);

        Assert.assertSame(crypto, configOption.getCryptoMaterialConfig());
        Assert.assertSame(account, configOption.getAccountConfig());
        Assert.assertSame(amop, configOption.getAmopConfig());
        Assert.assertSame(network, configOption.getNetworkConfig());
        Assert.assertSame(threadPool, configOption.getThreadPoolConfig());
        Assert.assertSame(property, configOption.getConfigProperty());
    }

    @Test(expected = ConfigException.class)
    public void testConfigLoadInvalidPath() throws ConfigException {
        Config.load("src/test/resources/config/does-not-exist.toml");
    }

    @Test
    public void testLoadGmConfig() throws ConfigException {
        ConfigOption configOption = Config.load(GM_CONFIG_PATH);
        Assert.assertTrue(configOption.getCryptoMaterialConfig().getUseSmCrypto());
        Assert.assertEquals("group0", configOption.getNetworkConfig().getDefaultGroup());
    }

    // ======================= AccountConfig =======================

    @Test
    public void testAccountConfigFromToml() throws ConfigException {
        AccountConfig accountConfig = Config.load(CONFIG_PATH).getAccountConfig();
        Assert.assertEquals("pem", accountConfig.getAccountFileFormat());
        Assert.assertNotNull(accountConfig.getKeyStoreDir());
    }

    @Test
    public void testAccountConfigPojo() {
        AccountConfig config = new AccountConfig();
        config.setKeyStoreDir("dir");
        config.setAccountAddress("0xabc");
        config.setAccountFileFormat("p12");
        config.setAccountPassword("pwd");
        config.setAccountFilePath("path");

        Assert.assertEquals("dir", config.getKeyStoreDir());
        Assert.assertEquals("0xabc", config.getAccountAddress());
        Assert.assertEquals("p12", config.getAccountFileFormat());
        Assert.assertEquals("pwd", config.getAccountPassword());
        Assert.assertEquals("path", config.getAccountFilePath());
        Assert.assertTrue(config.isAccountConfigured());
        Assert.assertNotNull(config.toString());
    }

    @Test
    public void testAccountConfigClearAndConfigured() {
        AccountConfig config = new AccountConfig();
        Assert.assertFalse(config.isAccountConfigured());

        config.setAccountAddress("0xabc");
        Assert.assertTrue(config.isAccountConfigured());

        config.clearAccount();
        Assert.assertEquals("", config.getAccountAddress());
        Assert.assertEquals("", config.getAccountFilePath());
        Assert.assertEquals("", config.getAccountPassword());
        Assert.assertFalse(config.isAccountConfigured());

        config.setAccountFilePath("file");
        Assert.assertTrue(config.isAccountConfigured());
    }

    @Test
    public void testAccountConfigEqualsHashCode() {
        AccountConfig a = new AccountConfig();
        a.setKeyStoreDir("d");
        a.setAccountAddress("0x1");
        a.setAccountFileFormat("pem");
        a.setAccountPassword("p");

        AccountConfig b = new AccountConfig();
        b.setKeyStoreDir("d");
        b.setAccountAddress("0x1");
        b.setAccountFileFormat("pem");
        b.setAccountPassword("p");

        Assert.assertEquals(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());
        Assert.assertEquals(a, a);
        Assert.assertNotEquals(a, null);
        Assert.assertNotEquals(a, "string");

        b.setAccountAddress("0x2");
        Assert.assertNotEquals(a, b);
    }

    // ======================= NetworkConfig =======================

    @Test
    public void testNetworkConfigFromToml() throws ConfigException {
        NetworkConfig networkConfig = Config.load(CONFIG_PATH).getNetworkConfig();
        Assert.assertEquals("group0", networkConfig.getDefaultGroup());
        Assert.assertEquals(1, networkConfig.getPeers().size());
        Assert.assertEquals("127.0.0.1:20201", networkConfig.getPeers().get(0));
        Assert.assertEquals(10000, networkConfig.getTimeout());
        Assert.assertTrue(networkConfig.isSendRpcRequestToHighestBlockNode());
    }

    @Test
    public void testNetworkConfigPojo() {
        NetworkConfig config = new NetworkConfig();
        config.setPeers(Arrays.asList("a:1", "b:2"));
        config.setTarsPeers(Collections.singletonList("t:3"));
        config.setDefaultGroup("g1");
        config.setTimeout(5000);
        config.setSendRpcRequestToHighestBlockNode(false);

        Assert.assertEquals(2, config.getPeers().size());
        Assert.assertEquals(1, config.getTarsPeers().size());
        Assert.assertEquals("g1", config.getDefaultGroup());
        Assert.assertEquals(5000, config.getTimeout());
        Assert.assertFalse(config.isSendRpcRequestToHighestBlockNode());
        Assert.assertNotNull(config.toString());
    }

    @Test
    public void testNetworkConfigFromProperty() {
        ConfigProperty property = new ConfigProperty();
        java.util.Map<String, Object> network = new java.util.HashMap<>();
        network.put("peers", Arrays.asList("p1:1", "p2:2"));
        network.put("tarsPeers", Collections.singletonList("t:1"));
        network.put("defaultGroup", "gx");
        network.put("messageTimeout", "8000");
        network.put("sendRpcRequestToHighestBlockNode", "false");
        property.setNetwork(network);

        NetworkConfig config = new NetworkConfig(property);
        Assert.assertEquals("gx", config.getDefaultGroup());
        Assert.assertEquals(8000, config.getTimeout());
        Assert.assertEquals(2, config.getPeers().size());
        Assert.assertFalse(config.isSendRpcRequestToHighestBlockNode());
    }

    @Test
    public void testNetworkConfigFromNullProperty() {
        ConfigProperty property = new ConfigProperty();
        NetworkConfig config = new NetworkConfig(property);
        // defaults retained when network property is null
        Assert.assertEquals(10000, config.getTimeout());
        Assert.assertTrue(config.isSendRpcRequestToHighestBlockNode());
        Assert.assertNull(config.getPeers());
    }

    // ======================= ThreadPoolConfig =======================

    @Test
    public void testThreadPoolConfigFromToml() throws ConfigException {
        ThreadPoolConfig config = Config.load(CONFIG_PATH).getThreadPoolConfig();
        Assert.assertTrue(config.getThreadPoolSize() > 0);
        Assert.assertNotNull(config.toString());
    }

    @Test
    public void testThreadPoolConfigPojo() {
        ThreadPoolConfig config = new ThreadPoolConfig();
        config.setThreadPoolSize(16);
        Assert.assertEquals(16, config.getThreadPoolSize());
        Assert.assertTrue(config.toString().contains("16"));
    }

    @Test
    public void testThreadPoolConfigFromProperty() {
        ConfigProperty property = new ConfigProperty();
        java.util.Map<String, Object> threadPool = new java.util.HashMap<>();
        threadPool.put("threadPoolSize", "8");
        property.setThreadPool(threadPool);

        ThreadPoolConfig config = new ThreadPoolConfig(property);
        Assert.assertEquals(8, config.getThreadPoolSize());
    }

    @Test
    public void testThreadPoolConfigFromPropertyDefault() {
        ConfigProperty property = new ConfigProperty();
        ThreadPoolConfig config = new ThreadPoolConfig(property);
        // defaults to available processors
        Assert.assertTrue(config.getThreadPoolSize() > 0);
    }

    // ======================= CryptoMaterialConfig =======================

    @Test
    public void testCryptoMaterialConfigFromToml() throws ConfigException {
        CryptoMaterialConfig config = Config.load(CONFIG_PATH).getCryptoMaterialConfig();
        Assert.assertFalse(config.getUseSmCrypto());
        Assert.assertFalse(config.isUseSmCrypto());
        Assert.assertNotNull(config.getCertPath());
        Assert.assertNotNull(config.getCaCert());
        Assert.assertNotNull(config.getSdkCert());
        Assert.assertNotNull(config.getSdkPrivateKey());
        Assert.assertNotNull(config.toString());
    }

    @Test
    public void testCryptoMaterialConfigPojo() {
        CryptoMaterialConfig config = new CryptoMaterialConfig();
        config.setUseSmCrypto(true);
        config.setDisableSsl(true);
        config.setEnableHsm(true);
        config.setCertPath("certs");
        config.setCaCert("ca");
        config.setSdkCert("sdk");
        config.setSdkPrivateKey("key");
        config.setEnSdkCert("encert");
        config.setEnSdkPrivateKey("enkey");
        config.setCaCertPath("ca.path");
        config.setSdkCertPath("sdk.path");
        config.setSdkPrivateKeyPath("key.path");
        config.setEnSdkCertPath("encert.path");
        config.setEnSdkPrivateKeyPath("enkey.path");
        config.setHsmLibPath("lib");
        config.setHsmKeyIndex("idx");
        config.setHsmPassword("hsmpwd");

        Assert.assertTrue(config.getUseSmCrypto());
        Assert.assertTrue(config.isUseSmCrypto());
        Assert.assertTrue(config.getDisableSsl());
        Assert.assertTrue(config.getEnableHsm());
        Assert.assertEquals("certs", config.getCertPath());
        Assert.assertEquals("ca", config.getCaCert());
        Assert.assertEquals("sdk", config.getSdkCert());
        Assert.assertEquals("key", config.getSdkPrivateKey());
        Assert.assertEquals("encert", config.getEnSdkCert());
        Assert.assertEquals("enkey", config.getEnSdkPrivateKey());
        Assert.assertEquals("ca.path", config.getCaCertPath());
        Assert.assertEquals("sdk.path", config.getSdkCertPath());
        Assert.assertEquals("key.path", config.getSdkPrivateKeyPath());
        Assert.assertEquals("encert.path", config.getEnSdkCertPath());
        Assert.assertEquals("enkey.path", config.getEnSdkPrivateKeyPath());
        Assert.assertEquals("lib", config.getHsmLibPath());
        Assert.assertEquals("idx", config.getHsmKeyIndex());
        Assert.assertEquals("hsmpwd", config.getHsmPassword());
        Assert.assertNotNull(config.toString());
    }

    @Test
    public void testCryptoMaterialConfigSslCryptoType() {
        CryptoMaterialConfig config = new CryptoMaterialConfig();
        config.setUseSmCrypto(false);
        // CryptoType.ECDSA_TYPE == 0
        Assert.assertEquals(0, config.getSslCryptoType());
        config.setUseSmCrypto(true);
        // CryptoType.SM_TYPE == 1
        Assert.assertEquals(1, config.getSslCryptoType());
    }

    @Test
    public void testGetDefaultCaCertPathEcdsa() throws ConfigException {
        CryptoMaterialConfig config = new CryptoMaterialConfig();
        // CryptoType.ECDSA_TYPE == 0
        CryptoMaterialConfig defaults = config.getDefaultCaCertPath(0, "conf");
        Assert.assertEquals("conf/ca.crt", defaults.getCaCertPath());
        Assert.assertEquals("conf/sdk.crt", defaults.getSdkCertPath());
        Assert.assertEquals("conf/sdk.key", defaults.getSdkPrivateKeyPath());
    }

    @Test
    public void testGetDefaultCaCertPathSm() throws ConfigException {
        CryptoMaterialConfig config = new CryptoMaterialConfig();
        // CryptoType.SM_TYPE == 1
        CryptoMaterialConfig defaults = config.getDefaultCaCertPath(1, "conf");
        Assert.assertEquals("conf/sm_ca.crt", defaults.getCaCertPath());
        Assert.assertEquals("conf/sm_sdk.crt", defaults.getSdkCertPath());
        Assert.assertEquals("conf/sm_sdk.key", defaults.getSdkPrivateKeyPath());
        Assert.assertEquals("conf/sm_ensdk.crt", defaults.getEnSdkCertPath());
        Assert.assertEquals("conf/sm_ensdk.key", defaults.getEnSdkPrivateKeyPath());
    }

    @Test(expected = ConfigException.class)
    public void testGetDefaultCaCertPathInvalidType() throws ConfigException {
        new CryptoMaterialConfig().getDefaultCaCertPath(99, "conf");
    }

    // ======================= AmopConfig =======================

    @Test
    public void testAmopConfigFromTomlNullTopics() throws ConfigException {
        AmopConfig config = Config.load(CONFIG_PATH).getAmopConfig();
        // amop section is commented out in the example config
        Assert.assertNull(config.getAmopTopicConfig());
        Assert.assertNotNull(config.toString());
    }

    @Test
    public void testAmopConfigPojo() {
        AmopConfig config = new AmopConfig();
        AmopTopic topic = new AmopTopic();
        topic.setTopicName("t1");
        config.setAmopTopicConfig(Collections.singletonList(topic));
        Assert.assertEquals(1, config.getAmopTopicConfig().size());
        Assert.assertNotNull(config.toString());
    }

    @Test
    public void testAmopConfigFromProperty() throws ConfigException {
        ConfigProperty property = new ConfigProperty();
        AmopConfig config = new AmopConfig(property);
        Assert.assertNull(config.getAmopTopicConfig());
    }

    // ======================= AmopTopic =======================

    @Test
    public void testAmopTopicPojo() {
        AmopTopic topic = new AmopTopic();
        topic.setTopicName("topic");
        topic.setPublicKeys(Arrays.asList("pk1", "pk2"));
        topic.setPrivateKey("priv");
        topic.setPassword("pwd");

        Assert.assertEquals("topic", topic.getTopicName());
        Assert.assertEquals(2, topic.getPublicKeys().size());
        Assert.assertEquals("priv", topic.getPrivateKey());
        Assert.assertEquals("pwd", topic.getPassword());
        Assert.assertNotNull(topic.toString());
    }

    // ======================= ConfigProperty =======================

    @Test
    public void testConfigPropertyPojo() {
        ConfigProperty property = new ConfigProperty();
        java.util.Map<String, Object> crypto = new java.util.HashMap<>();
        crypto.put("useSMCrypto", "false");
        property.setCryptoMaterial(crypto);
        java.util.Map<String, Object> network = new java.util.HashMap<>();
        property.setNetwork(network);
        java.util.Map<String, Object> account = new java.util.HashMap<>();
        property.setAccount(account);
        java.util.Map<String, Object> threadPool = new java.util.HashMap<>();
        property.setThreadPool(threadPool);
        property.setAmop(Collections.singletonList(new AmopTopic()));

        Assert.assertSame(crypto, property.getCryptoMaterial());
        Assert.assertSame(network, property.getNetwork());
        Assert.assertSame(account, property.getAccount());
        Assert.assertSame(threadPool, property.getThreadPool());
        Assert.assertEquals(1, property.getAmop().size());
        Assert.assertNotNull(property.toString());
    }

    @Test
    public void testConfigPropertyGetValue() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("key", "value");
        Assert.assertEquals("value", ConfigProperty.getValue(map, "key", "default"));
        Assert.assertEquals("default", ConfigProperty.getValue(map, "absent", "default"));
        Assert.assertEquals("default", ConfigProperty.getValue(null, "key", "default"));
    }

    @Test
    public void testConfigPropertyGetConfigFilePath() throws ConfigException {
        Assert.assertNull(ConfigProperty.getConfigFilePath(null));
        // a non-existent relative path returns itself unchanged
        String unknown = "this/path/does/not/exist.txt";
        Assert.assertEquals(unknown, ConfigProperty.getConfigFilePath(unknown));
    }

    @Test
    public void testConfigPropertyGetConfigFilePathExisting() throws ConfigException {
        String existing = CONFIG_PATH;
        Assert.assertEquals(existing, ConfigProperty.getConfigFilePath(existing));
    }

    @Test(expected = ConfigException.class)
    public void testConfigPropertyGetConfigFileContentMissing() throws ConfigException {
        ConfigProperty.getConfigFileContent("definitely/missing/file.crt");
    }
}
