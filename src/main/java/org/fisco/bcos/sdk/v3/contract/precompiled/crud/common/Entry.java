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
package org.fisco.bcos.sdk.v3.contract.precompiled.crud.common;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.TablePrecompiled;

public class Entry {
    private String key;
    private Map<String, String> fieldNameToValue = new LinkedHashMap<>();

    public Entry(List<String> valueColumns, String key, Map<String, String> fieldNameToValue) {
        this.key = key;
        for (String column : valueColumns) {
            this.fieldNameToValue.put(column, fieldNameToValue.get(column));
        }
    }

    public Entry(List<String> valueColumns, TablePrecompiled.Entry entry) {
        key = entry.key;
        for (int i = 0; i < valueColumns.size(); i++) {
            fieldNameToValue.put(valueColumns.get(i), entry.fields.get(i));
        }
    }

    public TablePrecompiled.Entry covertToEntry() {
        List<String> values = new ArrayList<>(fieldNameToValue.values());
        return new TablePrecompiled.Entry(key, values);
    }

    public void setFieldNameToValue(Map<String, String> fieldNameToValue) {
        this.fieldNameToValue = fieldNameToValue;
    }

    public Map<String, String> getFieldNameToValue() {
        return fieldNameToValue;
    }

    public void putFieldNameToValue(String key, String value) {
        fieldNameToValue.put(key, value);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "Entry{" + "key='" + key + '\'' + ", fieldNameToValue=" + fieldNameToValue + '}';
    }
}
