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

import com.google.common.util.concurrent.RateLimiter;
import java.math.BigInteger;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.BcosSDKException;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.exceptions.ContractException;
import org.fisco.bcos.sdk.demo.contract.OkD;
import org.fisco.bcos.sdk.demo.perf.callback.PerformanceCallback;
import org.fisco.bcos.sdk.demo.perf.collector.PerformanceCollector;
import org.fisco.bcos.sdk.model.ConstantConfig;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.utils.ThreadPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerformanceOkD {
    private static Logger logger = LoggerFactory.getLogger(PerformanceOkD.class);
    private static AtomicInteger sendedTransactions = new AtomicInteger(0);

    private static void Usage() {
        System.out.println(" Usage:");
        System.out.println(
                " \t java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.PerformanceOkD [count] [tps] [groupId].");
    }

    public static void main(String[] args) {
        try {
            String configFileName = ConstantConfig.CONFIG_FILE_NAME;
            URL configUrl = PerformanceOkD.class.getClassLoader().getResource(configFileName);
            if (configUrl == null) {
                System.out.println("The configFile " + configFileName + " doesn't exist!");
                return;
            }
            if (args.length < 3) {
                Usage();
                return;
            }
            Integer count = Integer.valueOf(args[0]);
            Integer qps = Integer.valueOf(args[1]);
            Integer groupId = Integer.valueOf(args[2]);
            System.out.println(
                    "====== PerformanceOkD trans, count: "
                            + count
                            + ", qps:"
                            + qps
                            + ", groupId: "
                            + groupId);

            String configFile = configUrl.getPath();
            BcosSDK sdk = BcosSDK.build(configFile);

            // build the client
            Client client = sdk.getClient(groupId);

            // deploy the HelloWorld
            System.out.println("====== Deploy OkD ====== ");
            OkD okd = OkD.deploy(client, client.getCryptoInterface());
            System.out.println(
                    "====== Deploy OkD success, address: " + okd.getContractAddress() + " ====== ");

            PerformanceCollector collector = new PerformanceCollector();
            collector.setTotal(count);
            RateLimiter limiter = RateLimiter.create(qps);
            Integer area = count / 10;
            final Integer total = count;

            System.out.println("====== PerformanceOkD trans start ======");

            ThreadPoolService threadPoolService =
                    new ThreadPoolService(
                            "PerformanceOkD",
                            sdk.getConfig().getThreadPoolConfig().getMaxBlockingQueueSize());
            Random random = new Random(System.currentTimeMillis());
            for (Integer i = 0; i < count; ++i) {
                limiter.acquire();
                threadPoolService
                        .getThreadPool()
                        .execute(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        PerformanceCallback callback = new PerformanceCallback();
                                        callback.setTimeout(0);
                                        callback.setCollector(collector);
                                        try {
                                            okd.trans(
                                                    String.valueOf(random.nextLong()),
                                                    new BigInteger("1"),
                                                    callback);
                                        } catch (Exception e) {
                                            TransactionReceipt receipt = new TransactionReceipt();
                                            receipt.setStatus("-1");
                                            callback.onResponse(receipt);
                                            logger.info(e.getMessage());
                                        }
                                        int current = sendedTransactions.incrementAndGet();
                                        if (current >= area && ((current % area) == 0)) {
                                            System.out.println(
                                                    "Already sended: "
                                                            + current
                                                            + "/"
                                                            + total
                                                            + " transactions");
                                        }
                                    }
                                });
            }
            // wait to collect all the receipts
            while (!collector.getReceived().equals(count)) {
                Thread.sleep(1000);
            }
            threadPoolService.stop();
            System.exit(0);
        } catch (BcosSDKException | ContractException | InterruptedException e) {
            System.out.println(
                    "====== PerformanceOkD test failed, error message: " + e.getMessage());
            System.exit(0);
        }
    }
}
