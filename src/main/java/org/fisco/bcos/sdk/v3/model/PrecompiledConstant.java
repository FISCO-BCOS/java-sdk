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
package org.fisco.bcos.sdk.v3.model;

public class PrecompiledConstant {
    // constant value
    public static final int TABLE_KEY_MAX_LENGTH = 255;
    public static final int TABLE_FIELD_NAME_MAX_LENGTH = 64;
    public static final int USER_TABLE_NAME_MAX_LENGTH = 48;
    public static final int TABLE_VALUE_FIELD_MAX_LENGTH = 1024;
    public static final int TABLE_KEY_VALUE_MAX_LENGTH = 255;
    public static final int USER_TABLE_FIELD_VALUE_MAX_LENGTH = 16 * 1024 * 1024 - 1;
    public static final int SYNC_KEEP_UP_THRESHOLD = 10;

    public static final String KEY_ORDER = "key_order";
    public static final String KEY_FIELD_NAME = "key_field";
    public static final String VALUE_FIELD_NAME = "value_field";
}
