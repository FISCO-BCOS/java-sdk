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
package org.fisco.bcos.sdk.demo.event;

import java.math.BigInteger;
import java.net.URL;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.demo.contract.Ok;
import org.fisco.bcos.sdk.model.ConstantConfig;

public class SendOk {

    private static void Usage() {
        System.out.println(" Usage:");
        System.out.println(
                " \t java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.event.SendOk [groupId] [count].");
    }

    public static void main(String[] args) {
        try {
            String configFileName = ConstantConfig.CONFIG_FILE_NAME;
            URL configUrl = SendOk.class.getClassLoader().getResource(configFileName);

            if (configUrl == null) {
                System.out.println("The configFile " + configFileName + " doesn't exist!");
                return;
            }
            if (args.length < 2) {
                Usage();
                return;
            }
            int groupId = Integer.valueOf(args[0]).intValue();
            Integer count = Integer.valueOf(args[1]);

            String configFile = configUrl.getPath();
            BcosSDK sdk = new BcosSDK(configFile);

            // build the client
            Client client = sdk.getClient(groupId);

            // deploy the HelloWorld
            System.out.println("====== Deploy Ok ====== ");
            Ok ok = Ok.deploy(client, client.getCryptoInterface());
            System.out.println(
                    "====== Deploy Ok successfully, address: "
                            + ok.getContractAddress()
                            + " ====== ");

            System.out.println("====== Send Ok trans begin ======");
            for (int i = 0; i < count; i++) {
                ok.trans(new BigInteger("4"));
                System.out.println("transaction idx " + i + " sent");
            }
            System.out.println("====== Send Ok trans end ======");

            System.exit(0);
        } catch (Exception e) {
            System.out.println("====== Send Ok failed, error message: " + e.getMessage());
            System.exit(0);
        }
    }
}
