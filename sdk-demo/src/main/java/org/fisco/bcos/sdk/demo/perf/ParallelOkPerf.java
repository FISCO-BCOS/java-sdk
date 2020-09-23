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
package org.fisco.bcos.sdk.demo.perf;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.demo.contract.ParallelOk;
import org.fisco.bcos.sdk.demo.perf.model.DagUserInfo;
import org.fisco.bcos.sdk.demo.perf.parallel.DagPrecompiledDemo;
import org.fisco.bcos.sdk.demo.perf.parallel.ParallelOkDemo;
import org.fisco.bcos.sdk.model.ConstantConfig;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.utils.ThreadPoolService;

public class ParallelOkPerf {
    private static Client client;
    private static DagUserInfo dagUserInfo = new DagUserInfo();

    public static void Usage() {
        System.out.println(" Usage:");
        System.out.println("===== ParallelOk test===========");
        System.out.println(
                " \t java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.ParallelOkPerf [parallelok] [groupID] [add] [count] [tps] [file].");
        System.out.println(
                " \t java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.ParallelOkPerf [parallelok] [groupID] [transfer] [count] [tps] [file].");
        System.out.println("===== DagTransafer test===========");
        System.out.println(
                " \t java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.ParallelOkPerf [precompiled] [groupID] [add] [count] [tps] [file].");
        System.out.println(
                " \t java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.ParallelOkPerf [precompiled] [groupID] [transfer] [count] [tps] [file].");
    }

    public static void main(String[] args)
            throws ContractException, IOException, InterruptedException {
        try {
            String configFileName = ConstantConfig.CONFIG_FILE_NAME;
            URL configUrl = ParallelOkPerf.class.getClassLoader().getResource(configFileName);
            if (configUrl == null) {
                System.out.println("The configFile " + configFileName + " doesn't exist!");
                return;
            }
            if (args.length < 6) {
                Usage();
                return;
            }
            String perfType = args[0];
            Integer groupId = Integer.valueOf(args[1]);
            String command = args[2];
            Integer count = Integer.valueOf(args[3]);
            Integer qps = Integer.valueOf(args[4]);
            String userFile = args[5];

            String configFile = configUrl.getPath();
            BcosSDK sdk = BcosSDK.build(configFile);
            client = sdk.getClient(Integer.valueOf(groupId));
            dagUserInfo.setFile(userFile);
            ThreadPoolService threadPoolService =
                    new ThreadPoolService(
                            "ParallelOkPerf",
                            sdk.getConfig().getThreadPoolConfig().getMaxBlockingQueueSize());

            if (perfType.compareToIgnoreCase("parallelok") == 0) {
                parallelOkPerf(groupId, command, count, qps, threadPoolService);
            } else if (perfType.compareToIgnoreCase("precompiled") == 0) {
                dagTransferPerf(groupId, command, count, qps, threadPoolService);
            } else {
                System.out.println(
                        "invalid perf option: "
                                + perfType
                                + ", only support parallelok/precompiled now");
                Usage();
            }
        } catch (Exception e) {
            System.out.println("ParallelOkPerf test failed, error info: " + e.getMessage());
            System.exit(0);
        }
    }

    public static void parallelOkPerf(
            Integer groupId,
            String command,
            Integer count,
            Integer qps,
            ThreadPoolService threadPoolService)
            throws IOException, InterruptedException, ContractException {
        System.out.println(
                "====== ParallelOk trans, count: "
                        + count
                        + ", qps:"
                        + qps
                        + ", groupId: "
                        + groupId);
        ParallelOk parallelOk;
        ParallelOkDemo parallelOkDemo;
        switch (command) {
            case "add":
                // deploy ParallelOk
                parallelOk =
                        ParallelOk.deploy(client, client.getCryptoInterface().getCryptoKeyPair());
                // enable parallel
                parallelOk.enableParallel();
                System.out.println(
                        "====== ParallelOk userAdd, deploy success, address: "
                                + parallelOk.getContractAddress());
                parallelOkDemo = new ParallelOkDemo(parallelOk, dagUserInfo, threadPoolService);
                parallelOkDemo.userAdd(BigInteger.valueOf(count), BigInteger.valueOf(qps));
                break;
            case "transfer":
                dagUserInfo.loadDagTransferUser();
                parallelOk =
                        ParallelOk.load(
                                dagUserInfo.getContractAddr(),
                                client,
                                client.getCryptoInterface().getCryptoKeyPair());
                System.out.println(
                        "====== ParallelOk trans, load success, address: "
                                + parallelOk.getContractAddress());
                parallelOkDemo = new ParallelOkDemo(parallelOk, dagUserInfo, threadPoolService);
                parallelOkDemo.userTransfer(BigInteger.valueOf(count), BigInteger.valueOf(qps));
                break;

            default:
                System.out.println("invalid command: " + command);
                Usage();
                break;
        }
    }

    public static void dagTransferPerf(
            Integer groupId,
            String command,
            Integer count,
            Integer qps,
            ThreadPoolService threadPoolService)
            throws IOException, InterruptedException, ContractException {
        System.out.println(
                "====== DagTransfer trans, count: "
                        + count
                        + ", qps:"
                        + qps
                        + ", groupId: "
                        + groupId);

        DagPrecompiledDemo dagPrecompiledDemo;
        switch (command) {
            case "add":
                dagPrecompiledDemo = new DagPrecompiledDemo(client, dagUserInfo, threadPoolService);
                dagPrecompiledDemo.userAdd(BigInteger.valueOf(count), BigInteger.valueOf(qps));
                break;
            case "transfer":
                dagUserInfo.loadDagTransferUser();
                dagPrecompiledDemo = new DagPrecompiledDemo(client, dagUserInfo, threadPoolService);
                dagPrecompiledDemo.userTransfer(BigInteger.valueOf(count), BigInteger.valueOf(qps));
                break;
            default:
                System.out.println("invalid command: " + command);
                Usage();
                break;
        }
    }
};
