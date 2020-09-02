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

package org.fisco.bcos.sdk.config.model;

import java.util.List;

public class AmopConfig {
    // AMOP topic related config
    private List<AmopTopic> amopTopicConfig;

    public AmopConfig(ConfigProperty configProperty) {
        this.amopTopicConfig = configProperty.getAmop();
    }

    public List<AmopTopic> getAmopTopicConfig() {
        return amopTopicConfig;
    }

    public void setAmopTopicConfig(List<AmopTopic> amopTopicConfig) {
        this.amopTopicConfig = amopTopicConfig;
    }
}
