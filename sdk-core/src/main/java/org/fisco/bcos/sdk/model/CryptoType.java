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

package org.fisco.bcos.sdk.model;

public class CryptoType {
    // signature related crypto type(0-999)
    public static final int ECDSA_TYPE = 0;
    public static final int SM_TYPE = 1;

    // vrf related crypto type(1000-1999)
    public static final int ED25519_VRF_TYPE = 1000;
}
