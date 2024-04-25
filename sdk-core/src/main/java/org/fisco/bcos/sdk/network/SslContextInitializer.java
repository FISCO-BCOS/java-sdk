package org.fisco.bcos.sdk.network;

import static org.fisco.bcos.sdk.model.CryptoProviderType.HSM;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.Security;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SslContextInitializer {

    private static Logger logger = LoggerFactory.getLogger(SslContextInitializer.class);

    private static boolean enableNettyOpenSSLProvider = false;

    static {
        String property = System.getProperty("fisco.netty.enable.openssl.provider");
        if (property != null) {
            enableNettyOpenSSLProvider = Boolean.valueOf(property);
            logger.info("load `fisco.netty.enable.openssl.provider` value: {}", property);
        }
    }

    public SslContext initSslContext(ConfigOption configOption) throws NetworkException {
        try {
            Security.setProperty("jdk.disabled.namedCurves", "");
            System.setProperty("jdk.sunec.disableNative", "false");

            // Get file, file existence is already checked when check config file.
            // Init SslContext
            logger.info(" build ECDSA ssl context with configured certificates ");

            SslProvider sslProvider = SslProvider.JDK;
            if (enableNettyOpenSSLProvider) {
                sslProvider = SslProvider.OPENSSL;
            }

            logger.info("sslProvider: {}", sslProvider);

            SslContext sslCtx =
                    SslContextBuilder.forClient()
                            .trustManager(configOption.getCryptoMaterialConfig().getCaInputStream())
                            .keyManager(
                                    configOption.getCryptoMaterialConfig().getSdkCertInputStream(),
                                    configOption
                                            .getCryptoMaterialConfig()
                                            .getSdkPrivateKeyInputStream())
                            // .sslProvider(SslProvider.OPENSSL)
                            .sslProvider(sslProvider)
                            .build();
            return sslCtx;
        } catch (IOException e) {
            logger.error(
                    "initSslContext failed, caCert: {}, sslCert: {}, sslKey: {}, error: {}, e: {}",
                    configOption.getCryptoMaterialConfig().getCaCertPath(),
                    configOption.getCryptoMaterialConfig().getSdkCertPath(),
                    configOption.getCryptoMaterialConfig().getSdkPrivateKeyPath(),
                    e.getMessage(),
                    e);
            throw new NetworkException(
                    "SSL context init failed, please make sure your cert and key files are properly configured. error info: "
                            + e.getMessage(),
                    NetworkException.INIT_CONTEXT_FAILED);
        } catch (IllegalArgumentException e) {
            logger.error("initSslContext failed, error: {}, e: {}", e.getMessage(), e);
            throw new NetworkException(
                    "SSL context init failed, error info: " + e.getMessage(),
                    NetworkException.INIT_CONTEXT_FAILED);
        }
    }

    public SslContext initSMSslContext(ConfigOption configOption) throws NetworkException {
        try {
            // Get file, file existence is already checked when check config file.
            InputStream caInputStream = configOption.getCryptoMaterialConfig().getCaInputStream();
            InputStream enSSLCertInputStream =
                    configOption.getCryptoMaterialConfig().getEnSSLCertInputStream();
            InputStream enSSLPrivateKeyInputStream =
                    configOption.getCryptoMaterialConfig().getEnSSLPrivateKeyInputStream();
            InputStream sdkCertInputStream =
                    configOption.getCryptoMaterialConfig().getSdkCertInputStream();
            InputStream sdkPrivateKeyInputStream =
                    configOption.getCryptoMaterialConfig().getSdkPrivateKeyInputStream();

            String smContextFactoryClassName = "io.netty.handler.ssl.SMSslClientContextFactory";

            Class<?> smContextFactoryClass = Class.forName(smContextFactoryClassName);
            logger.info("加载类`{}`成功", smContextFactoryClassName);
            Method buildMethod =
                    smContextFactoryClass.getMethod(
                            "build",
                            InputStream.class,
                            InputStream.class,
                            InputStream.class,
                            InputStream.class,
                            InputStream.class);
            SslContext sslContext =
                    (SslContext)
                            buildMethod.invoke(
                                    null,
                                    caInputStream,
                                    enSSLCertInputStream,
                                    enSSLPrivateKeyInputStream,
                                    sdkCertInputStream,
                                    sdkPrivateKeyInputStream);

            return sslContext;
        } catch (Exception e) {
            if (configOption.getCryptoMaterialConfig().getCryptoProvider().equalsIgnoreCase(HSM)) {
                logger.error(
                        "initSMSslContext failed, caCert:{}, sslCert: {}, sslKeyIndex: {}, enCert: {}, enSslKeyIndex: {}, error: {}, e: {}",
                        configOption.getCryptoMaterialConfig().getCaCertPath(),
                        configOption.getCryptoMaterialConfig().getSdkCertPath(),
                        configOption.getCryptoMaterialConfig().getSslKeyIndex(),
                        configOption.getCryptoMaterialConfig().getEnSSLCertPath(),
                        configOption.getCryptoMaterialConfig().getEnSslKeyIndex(),
                        e.getMessage(),
                        e);
            } else {
                logger.error(
                        "initSMSslContext failed, caCert:{}, sslCert: {}, sslKey: {}, enCert: {}, enSslKey: {}, error: {}, e: {}",
                        configOption.getCryptoMaterialConfig().getCaCertPath(),
                        configOption.getCryptoMaterialConfig().getSdkCertPath(),
                        configOption.getCryptoMaterialConfig().getSdkPrivateKeyPath(),
                        configOption.getCryptoMaterialConfig().getEnSSLCertPath(),
                        configOption.getCryptoMaterialConfig().getEnSSLPrivateKeyPath(),
                        e.getMessage(),
                        e);
            }
            throw new NetworkException(
                    "SSL context init failed, please make sure your cert and key files are properly configured. error info: "
                            + e.getMessage(),
                    e);
        }
    }
}
