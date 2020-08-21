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

package org.fisco.bcos.sdk.amop;

import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.BcosSDKTest;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class blockNotifyTest {
    private static final String configFile = BcosSDKTest.class.getClassLoader().getResource("config-example.yaml").getPath();

    @Test
    public void testBlockNotify() throws ConfigException, InterruptedException {
        BcosSDK sdk = new BcosSDK(configFile);
        Assert.assertTrue(sdk.getChannel().getAvailablePeer().size() >= 1);
        Amop amop = Amop.build(sdk.getGroupManagerService(),null);
    }
}

