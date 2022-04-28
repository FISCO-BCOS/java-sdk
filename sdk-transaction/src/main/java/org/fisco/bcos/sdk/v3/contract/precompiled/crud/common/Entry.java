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
    private LinkedHashMap<String, String> fieldNameToValue = new LinkedHashMap<>();

    public Entry(String key, LinkedHashMap<String, String> fieldNameToValue) {
        this.key = key;
        this.fieldNameToValue = fieldNameToValue;
    }

    public Entry(
            TablePrecompiled.TableInfo tableInfo,
            String key,
            Map<String, String> fieldNameToValue) {
        this.key = key;
        for (String column : tableInfo.valueColumns) {
            this.fieldNameToValue.put(column, fieldNameToValue.get(column));
        }
    }

    public Entry(List<String> valueColumns, String key, Map<String, String> fieldNameToValue) {
        this.key = key;
        for (String column : valueColumns) {
            this.fieldNameToValue.put(column, fieldNameToValue.get(column));
        }
    }

    public Entry(TablePrecompiled.TableInfo tableInfo, TablePrecompiled.Entry entry) {
        key = entry.key;
        for (int i = 0; i < tableInfo.valueColumns.size(); i++) {
            fieldNameToValue.put(tableInfo.valueColumns.get(i), entry.fields.get(i));
        }
    }

    public Entry(TablePrecompiled.Entry entry) {
        key = entry.key;
        for (String field : entry.fields) {
            fieldNameToValue.put("", field);
        }
    }

    public TablePrecompiled.Entry covertToEntry(String key) {
        return new TablePrecompiled.Entry(key, new ArrayList<>(fieldNameToValue.values()));
    }

    public TablePrecompiled.Entry covertToEntry() {
        return new TablePrecompiled.Entry(key, new ArrayList<>(fieldNameToValue.values()));
    }

    public TablePrecompiled.Entry covertToUpdateField(String key) {
        return new TablePrecompiled.Entry(key, new ArrayList<>(fieldNameToValue.values()));
    }

    public void setFieldNameToValue(LinkedHashMap<String, String> fieldNameToValue) {
        this.fieldNameToValue = fieldNameToValue;
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
}
