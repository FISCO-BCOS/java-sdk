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

import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.transaction.model.bo.ResultEntity;

public class EventResultEntity extends ResultEntity {
    private boolean indexed;

    public EventResultEntity() {}

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
