/**
 * Copyright 2014-2020 [fisco-dev]
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fisco.bcos.sdk.utils;

import java.lang.reflect.Field;
import java.security.Security;
import java.util.Arrays;
import java.util.List;

public class SystemInformation {
    // Note: must update the version if publish new-version
    private static final String sdkVersion = "2.10.0";
    public static final String connectionFaqIssueUrl =
            "https://github.com/FISCO-BCOS/java-sdk/issues/536";
    public static final String connectionFaqDocUrl =
            "https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/faq/connect.html";
    public static final String nettyVersion = "4.1.53.Final";

    public static class InformationProperty {
        private String key;
        private String value;

        public InformationProperty(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static final InformationProperty JAVA_VERSION =
            new InformationProperty("Java Version", System.getProperty("java.version"));
    public static final InformationProperty JDK_DISABLED_NAMED_CURVES =
            new InformationProperty(
                    "JDK Disabled NamedCurves", System.getProperty("jdk.disabled.namedCurves"));
    public static final InformationProperty JDK_DISABLE_NATIVE_OPTION =
            new InformationProperty(
                    "JDK DisableNative Option", System.getProperty("jdk.sunec.disableNative"));
    public static final InformationProperty OS_NAME =
            new InformationProperty("OS Name", System.getProperty("os.name"));
    public static final InformationProperty OS_ARCH =
            new InformationProperty("OS Arch", System.getProperty("os.arch"));
    public static final InformationProperty OS_VERSION =
            new InformationProperty("OS Version", System.getProperty("os.version"));
    public static final InformationProperty JVM_VERSION =
            new InformationProperty("JVM Version", System.getProperty("java.vm.version"));
    public static final InformationProperty JAVA_VENDOR =
            new InformationProperty("JVM Vendor", System.getProperty("java.vendor"));
    public static final InformationProperty JAVA_VENDOR_URL =
            new InformationProperty("JVM Vendor URL", System.getProperty("java.vendor.url"));
    private static String systemInformation;
    public static List<String> EXPECTED_CURVES = Arrays.asList("secp256k1");
    public static boolean supportSecp256K1 = false;

    static {
        systemInformation += "--------- System Information --------- \n";
        systemInformation = "* FISCO BCOS Java SDK Version: " + sdkVersion + "\n";
        String supportedCurves =
                Security.getProviders("AlgorithmParameters.EC")[0]
                        .getService("AlgorithmParameters", "EC")
                        .getAttribute("SupportedCurves");
        if (supportedCurves.contains("secp256k1")) {
            supportSecp256K1 = true;
        }
        for (String curve : EXPECTED_CURVES) {
            if (supportedCurves.contains(curve)) {
                systemInformation += "* Support " + curve + " : true\n";
            } else {
                systemInformation += "* Support " + curve + " : false\n";
            }
        }
        Field[] fields = SystemInformation.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().equals(InformationProperty.class)) {
                try {
                    InformationProperty property = (InformationProperty) field.get(null);
                    systemInformation +=
                            "* " + property.getKey() + " : " + property.getValue() + "\n";
                } catch (IllegalAccessException e) {
                    continue;
                }
            }
        }
    }

    public static String getSystemInformation() {
        return systemInformation;
    }
}
