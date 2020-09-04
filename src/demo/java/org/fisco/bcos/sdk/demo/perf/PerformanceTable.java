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
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.BcosSDKException;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.exceptions.ContractException;
import org.fisco.bcos.sdk.demo.contract.TableTest;
import org.fisco.bcos.sdk.demo.perf.callback.PerformanceCallback;
import org.fisco.bcos.sdk.demo.perf.collector.PerformanceCollector;
import org.fisco.bcos.sdk.model.ConstantConfig;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.utils.ThreadPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerformanceTable {
    private static Logger logger = LoggerFactory.getLogger(PerformanceTable.class);
    private static AtomicInteger sendedTransactions = new AtomicInteger(0);
    private static AtomicLong uniqueID = new AtomicLong(0);

    private static void Usage() {
        System.out.println(" Usage:");
        System.out.println("===== PerformanceTable test===========");
        System.out.println(
                " \t java -cp conf/:lib/*:apps/* org.fisco.bcos.sdk.demo.perf.PerformanceTable [insert] [count] [tps] [groupId].");
        System.out.println(
                " \t java -cp conf/:lib/*:apps/* org.fisco.bcos.sdk.demo.perf.PerformanceTable [update] [count] [tps] [groupId].");
        System.out.println(
                " \t java -cp conf/:lib/*:apps/* org.fisco.bcos.sdk.demo.perf.PerformanceTable [remove] [count] [tps] [groupId].");
        System.out.println(
                " \t java -cp conf/:lib/*:apps/* org.fisco.bcos.sdk.demo.perf.PerformanceTable [query] [count] [tps] [groupId].");
    }

    public static void main(String[] args) {
        try {
            String configFileName = ConstantConfig.CONFIG_FILE_NAME;
            URL configUrl = PerformanceTable.class.getClassLoader().getResource(configFileName);
            if (configUrl == null) {
                System.out.println("The configFile " + configFileName + " doesn't exist!");
                return;
            }
            if (args.length < 4) {
                Usage();
                return;
            }
            String command = args[0];
            Integer count = Integer.valueOf(args[1]);
            Integer qps = Integer.valueOf(args[2]);
            Integer groupId = Integer.valueOf(args[3]);
            System.out.println(
                    "====== PerformanceTable "
                            + command
                            + ", count: "
                            + count
                            + ", qps:"
                            + qps
                            + ", groupId"
                            + groupId);

            String configFile = configUrl.getPath();
            BcosSDK sdk = new BcosSDK(configFile);

            // build the client
            Client client = sdk.getClient(groupId);

            // deploy the HelloWorld
            System.out.println("====== Deploy TableTest ====== ");
            TableTest tableTest = TableTest.deploy(client, client.getCryptoInterface());
            // create table
            tableTest.create();
            System.out.println(
                    "====== Deploy TableTest success, address: "
                            + tableTest.getContractAddress()
                            + " ====== ");

            PerformanceCollector collector = new PerformanceCollector();
            collector.setTotal(count);
            RateLimiter limiter = RateLimiter.create(qps);
            Integer area = count / 10;
            final Integer total = count;

            System.out.println("====== PerformanceTable " + command + " start ======");
            ThreadPoolService threadPoolService =
                    new ThreadPoolService(
                            "PerformanceTable",
                            sdk.getConfig().getThreadPoolConfig().getMaxBlockingQueueSize());
            for (Integer i = 0; i < count; ++i) {
                limiter.acquire();
                threadPoolService
                        .getThreadPool()
                        .execute(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        callTableOperation(command, tableTest, collector);
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
                    "====== PerformanceTable test failed, error message: " + e.getMessage());
            System.exit(0);
        }
    }

    private static void callTableOperation(
            String command, TableTest tableTest, PerformanceCollector collector) {
        if (command.compareToIgnoreCase("insert") == 0) {
            insert(tableTest, collector);
        }

        if (command.compareToIgnoreCase("update") == 0) {
            update(tableTest, collector);
        }
        if (command.compareToIgnoreCase("remove") == 0) {
            remove(tableTest, collector);
        }
        if (command.compareToIgnoreCase("query") == 0) {
            query(tableTest, collector);
        }
    }

    public static long getNextID() {
        return uniqueID.getAndIncrement();
    }

    private static String getId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "");
    }

    private static PerformanceCallback createCallback(PerformanceCollector collector) {
        PerformanceCallback callback = new PerformanceCallback();
        callback.setTimeout(0);
        callback.setCollector(collector);
        return callback;
    }

    private static void sendTransactionException(
            Exception e, String command, PerformanceCallback callback) {
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setStatus("-1");
        callback.onResponse(receipt);
        logger.info("call command {} failed, error info: {}", command, e.getMessage());
    }

    private static void insert(TableTest tableTest, PerformanceCollector collector) {
        PerformanceCallback callback = createCallback(collector);
        try {
            long _id = getNextID();
            tableTest.insert(
                    "fruit" + _id % 100, BigInteger.valueOf(_id), "apple" + getId(), callback);
        } catch (Exception e) {
            sendTransactionException(e, "insert", callback);
        }
    }

    private static void update(TableTest tableTest, PerformanceCollector collector) {
        PerformanceCallback callback = createCallback(collector);
        try {
            long _id = getNextID();
            Random r = new Random();
            long l1 = r.nextLong();
            tableTest.update(
                    "fruit" + l1 % 100, BigInteger.valueOf(_id), "apple" + getId(), callback);
        } catch (Exception e) {
            sendTransactionException(e, "update", callback);
        }
    }

    private static void remove(TableTest tableTest, PerformanceCollector collector) {
        PerformanceCallback callback = createCallback(collector);
        try {
            long _id = getNextID();
            Random r = new Random();
            long l1 = r.nextLong();
            tableTest.remove("fruit" + l1 % 100, BigInteger.valueOf(_id), callback);

        } catch (Exception e) {
            sendTransactionException(e, "remove", callback);
        }
    }

    private static void query(TableTest tableTest, PerformanceCollector collector) {
        try {
            Long time_before = System.currentTimeMillis();
            Random r = new Random();
            long l1 = r.nextLong();
            tableTest.select("fruit" + l1 % 100);
            Long time_after = System.currentTimeMillis();
            TransactionReceipt receipt = new TransactionReceipt();
            receipt.setStatus("0x0");
            collector.onMessage(receipt, time_after - time_before);
        } catch (Exception e) {
            TransactionReceipt receipt = new TransactionReceipt();
            receipt.setStatus("-1");
            collector.onMessage(receipt, (long) (0));
            logger.error("query error: {}", e);
        }
    }
}
