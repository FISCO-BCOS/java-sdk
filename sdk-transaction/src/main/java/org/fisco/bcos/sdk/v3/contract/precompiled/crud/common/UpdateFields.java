package org.fisco.bcos.sdk.v3.contract.precompiled.crud.common;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.TablePrecompiled;

public class UpdateFields {
    private Map<String, String> fieldNameToValue;

    public UpdateFields(Map<String, String> fieldNameToValue) {
        this.fieldNameToValue = fieldNameToValue;
    }

    public void putFieldValue(String fieldName, String value) {
        fieldNameToValue.put(fieldName, value);
    }

    public String getFieldValue(String fieldName) {
        return fieldNameToValue.get(fieldName);
    }

    public List<TablePrecompiled.UpdateField> getUpdateFields(List<String> columns) {
        List<TablePrecompiled.UpdateField> updateFields = new ArrayList<>();
        fieldNameToValue.forEach(
                (fieldName, value) -> {
                    int index = columns.indexOf(fieldName);
                    updateFields.add(
                            new TablePrecompiled.UpdateField(BigInteger.valueOf(index), value));
                });
        return updateFields;
    }
}
