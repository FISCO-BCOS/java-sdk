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
    public static final InformationProperty OS_NAME =
            new InformationProperty("OS Name", System.getProperty("os.name"));
    public static final InformationProperty OS_ARCH =
            new InformationProperty("OS Arch", System.getProperty("os.arch"));
    public static final InformationProperty OS_VERSION =
            new InformationProperty("OS Version", System.getProperty("os.version"));
    public static final InformationProperty VENDOR_NAME =
            new InformationProperty("Vendor Name", System.getProperty("java.vendor"));
    public static final InformationProperty VENDOR_URL =
            new InformationProperty("Vendor URL", System.getProperty("java.vendor.url"));
    public static final InformationProperty JVM_VERSION =
            new InformationProperty("JVM Version", System.getProperty("java.vm.version"));
    public static final InformationProperty JVM_NAME =
            new InformationProperty("JVM Name", System.getProperty("java.vm.name"));
    public static final InformationProperty JVM_VENDOR =
            new InformationProperty("JVM Vendor", System.getProperty("java.vm.vendor"));
    public static final InformationProperty JAVA_LIB_PATH =
            new InformationProperty("JAVA Library Path", System.getProperty("java.library.path"));
    public static final InformationProperty JDK_DISABLED_NAMED_CURVES =
            new InformationProperty(
                    "JDK Disabled NamedCurves", System.getProperty("jdk.disabled.namedCurves"));
    public static final InformationProperty JDK_DISABLE_NATIVE_OPTION =
            new InformationProperty(
                    "JDK DisableNative Option", System.getProperty("jdk.sunec.disableNative"));

    private static String systemInformation;
    public static List<String> EXPECTED_CURVES = Arrays.asList("secp256k1", "secp256r1");

    static {
        systemInformation = "[System Information]:\n";
        Field[] fields = SystemInformation.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().equals(InformationProperty.class)) {
                try {
                    InformationProperty property = (InformationProperty) field.get(null);
                    systemInformation +=
                            "[" + property.getKey() + "] : " + property.getValue() + "\n";
                } catch (IllegalAccessException e) {
                    continue;
                }
            }
        }
        String supportedCurves =
                Security.getProviders("AlgorithmParameters.EC")[0]
                        .getService("AlgorithmParameters", "EC")
                        .getAttribute("SupportedCurves");
        for (String curve : EXPECTED_CURVES) {
            if (supportedCurves.contains(curve)) {
                systemInformation += "[Support " + curve + "] : true\n";
            } else {
                systemInformation += "[Support " + curve + "] : false\n";
            }
        }
    }

    public static String getSystemInformation() {
        return systemInformation;
    }
}
