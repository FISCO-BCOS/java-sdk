package org.fisco.bcos.sdk.v3.contract.precompiled.crud.common;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.TablePrecompiled;

public class UpdateFields {
    private final Map<String, String> fieldNameToValue;

    public UpdateFields() {
        this.fieldNameToValue = new HashMap<>();
    }

    public UpdateFields(Map<String, String> fieldNameToValue) {
        this.fieldNameToValue = fieldNameToValue;
    }

    public Map<String, String> getFieldNameToValue() {
        return fieldNameToValue;
    }

    public List<TablePrecompiled.UpdateField> convertToUpdateFields(List<String> columns) {
        List<TablePrecompiled.UpdateField> updateFields = new ArrayList<>();
        fieldNameToValue.forEach(
                (fieldName, value) -> {
                    int index = columns.indexOf(fieldName);
                    updateFields.add(
                            new TablePrecompiled.UpdateField(BigInteger.valueOf(index), value));
                });
        return updateFields;
    }

    @Override
    public String toString() {
        return "UpdateFields{" + "fieldNameToValue=" + fieldNameToValue + '}';
    }
}
