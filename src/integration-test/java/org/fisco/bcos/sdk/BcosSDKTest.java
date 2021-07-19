/*
 * Copyright 2014-2020  [fisco-dev]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package org.fisco.bcos.sdk;

import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.exceptions.ClientException;
import org.fisco.bcos.sdk.client.protocol.response.*;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.model.ConstantConfig;
import org.junit.Test;

import java.math.BigInteger;

public class BcosSDKTest {
  private static final String configFile =
      BcosSDKTest.class.getClassLoader().getResource(ConstantConfig.CONFIG_FILE_NAME).getPath();

  @Test
  public void testClient() throws ConfigException {
    BcosSDK sdk = BcosSDK.build(configFile);
    // get the client
    Client client = sdk.getClient(Integer.valueOf(1));

    // get NodeVersion
    NodeInfo.NodeInformation nodeVersion = client.getNodeInfo();
    System.out.println(nodeVersion);

    // test getBlockNumber
    BlockNumber blockNumber = client.getBlockNumber();

    // test getBlockByNumber
    BcosBlock block = client.getBlockByNumber(BigInteger.ZERO, false);

    // get SealerList
    SealerList sealerList = client.getSealerList();

    // get observerList
    client.getObserverList();

    // get pbftView
    client.getPbftView();

  }
}
