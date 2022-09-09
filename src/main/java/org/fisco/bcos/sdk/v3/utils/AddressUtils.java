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

package org.fisco.bcos.sdk.v3.utils;

import java.util.Objects;

public class AddressUtils {
    private AddressUtils() {}

    public static final String ADDRESS_PATTERN = "^[0-9A-Fa-f]{40}$";

    public static boolean isValidAddress(String address) {
        String addressNoPrefix = Numeric.cleanHexPrefix(address);
        return addressNoPrefix.matches(ADDRESS_PATTERN);
    }

    public static String addHexPrefixToAddress(String address) {
        if (!Objects.isNull(address)
                && !(address.startsWith("0x") || address.startsWith("0X"))
                && isValidAddress(address)) {
            return Hex.addPrefix(address);
        }

        return address;
    }
}
