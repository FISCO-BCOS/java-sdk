/**
 * Copyright 2014-2020 [fisco-dev]
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fisco.bcos.sdk.client.protocol.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class GenerateGroupParam {
    private String timestamp;
    private List<String> sealers;

    @JsonProperty("enable_free_storage")
    private boolean enableFreeStorage;

    public GenerateGroupParam(String timestamp, boolean enableFreeStorage, List<String> sealers) {
        this.timestamp = timestamp;
        this.enableFreeStorage = enableFreeStorage;
        this.sealers = sealers;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getSealers() {
        return sealers;
    }

    public void setSealers(List<String> sealers) {
        this.sealers = sealers;
    }

    public boolean isEnableFreeStorage() {
        return enableFreeStorage;
    }

    public void setEnableFreeStorage(boolean enableFreeStorage) {
        this.enableFreeStorage = enableFreeStorage;
    }
}
