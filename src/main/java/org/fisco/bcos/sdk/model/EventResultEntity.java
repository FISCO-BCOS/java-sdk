package org.fisco.bcos.sdk.model;

import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.transaction.model.bo.ResultEntity;

public class EventResultEntity extends ResultEntity {
    private boolean indexed;

    public boolean isIndexed() {
        return indexed;
    }

    public void setIndexed(boolean indexed) {
        this.indexed = indexed;
    }

    @SuppressWarnings("rawtypes")
    public EventResultEntity(String name, String type, boolean indexed, Type data) {
        super(name, type, data);
        this.setIndexed(indexed);
    }

    @Override
    public String toString() {
        return "EventResultEntity [name="
                + getName()
                + ", type="
                + getType()
                + ", data="
                + getData()
                + ", indexed="
                + indexed
                + "]";
    }
}
