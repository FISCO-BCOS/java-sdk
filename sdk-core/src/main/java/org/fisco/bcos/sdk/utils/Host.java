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

package org.fisco.bcos.sdk.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Verify host and port, and extract host or port from string. */
public class Host {
    /**
     * @param IP
     * @return true if IP valid IP string otherwise false
     */
    public static boolean validIP(String IP) {
        String regex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(IP);
        return matcher.matches();
    }

    /**
     * @param port
     * @return true if port valid IP port otherwise false
     */
    public static boolean validPort(String port) {
        try {
            Integer p = Integer.parseInt(port);
            return p > 0 && p <= 65535;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get ip from IPAndPort string
     *
     * @param IPAndPort
     * @return String of IP address
     */
    public static String getIpFromString(String IPAndPort) {
        int index = IPAndPort.lastIndexOf(':');
        String IP = IPAndPort.substring(0, index);
        return IP;
    }

    /**
     * Get port from IPAndPort string
     *
     * @param IPAndPort
     * @return String of port.
     */
    public static String getPortFromString(String IPAndPort) {
        int index = IPAndPort.lastIndexOf(':');
        String port = IPAndPort.substring(index + 1);
        return port;
    }
}
