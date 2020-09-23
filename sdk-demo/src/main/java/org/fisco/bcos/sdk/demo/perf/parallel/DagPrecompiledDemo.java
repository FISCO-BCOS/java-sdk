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
package org.fisco.bcos.sdk.demo.perf.parallel;

import com.google.common.util.concurrent.RateLimiter;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.demo.contract.DagTransfer;
import org.fisco.bcos.sdk.demo.perf.callback.ParallelOkCallback;
import org.fisco.bcos.sdk.demo.perf.collector.PerformanceCollector;
import org.fisco.bcos.sdk.demo.perf.model.DagTransferUser;
import org.fisco.bcos.sdk.demo.perf.model.DagUserInfo;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.utils.ThreadPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DagPrecompiledDemo {
    private static final Logger logger = LoggerFactory.getLogger(DagPrecompiledDemo.class);
    private final DagTransfer dagTransfer;
    private final DagUserInfo dagUserInfo;
    private final PerformanceCollector collector;
    private final ThreadPoolService threadPoolService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String DAG_TRANSFER_ADDR = "0x0000000000000000000000000000000000005002";

    public DagPrecompiledDemo(
            Client client, DagUserInfo dagUserInfo, ThreadPoolService threadPoolService) {
        this.threadPoolService = threadPoolService;
        this.dagTransfer =
                DagTransfer.load(
                        DAG_TRANSFER_ADDR, client, client.getCryptoInterface().getCryptoKeyPair());
        this.dagUserInfo = dagUserInfo;
        this.collector = new PerformanceCollector();
    }

    public void userAdd(BigInteger userCount, BigInteger qps)
            throws InterruptedException, IOException {
        System.out.println("Start userAdd test...");
        System.out.println("===================================================================");
        RateLimiter limiter = RateLimiter.create(qps.intValue());
        Integer area = userCount.intValue() / 10;

        long seconds = System.currentTimeMillis() / 1000L;

        this.collector.setStartTimestamp(System.currentTimeMillis());
        AtomicInteger sended = new AtomicInteger(0);
        AtomicInteger sendFailed = new AtomicInteger(9);
        collector.setTotal(userCount.intValue());

        for (Integer i = 0; i < userCount.intValue(); i++) {
            final Integer index = i;
            limiter.acquire();
            threadPoolService
                    .getThreadPool()
                    .execute(
                            new Runnable() {
                                @Override
                                public void run() {
                                    String user =
                                            Long.toHexString(seconds) + Integer.toHexString(index);
                                    BigInteger amount = new BigInteger("1000000000");
                                    DagTransferUser dtu = new DagTransferUser();
                                    dtu.setUser(user);
                                    dtu.setAmount(amount);
                                    ParallelOkCallback callback =
                                            new ParallelOkCallback(
                                                    collector,
                                                    dagUserInfo,
                                                    ParallelOkCallback.ADD_USER_CALLBACK);
                                    callback.setUser(dtu);
                                    try {
                                        callback.recordStartTime();
                                        callback.setTimeout(0);
                                        dagTransfer.userAdd(user, amount, callback);
                                        int current = sended.incrementAndGet();
                                        if (current >= area && ((current % area) == 0)) {
                                            System.out.println(
                                                    "Already sended: "
                                                            + current
                                                            + "/"
                                                            + userCount
                                                            + " transactions");
                                        }
                                    } catch (Exception e) {
                                        TransactionReceipt receipt = new TransactionReceipt();
                                        receipt.setStatus("-1");
                                        callback.onResponse(receipt);
                                        logger.error(
                                                "dagTransfer add failed, error info: "
                                                        + e.getMessage());
                                        sendFailed.incrementAndGet();
                                        logger.info(e.getMessage());
                                    }
                                }
                            });
        }
        while (collector.getReceived().intValue() != collector.getTotal().intValue()) {
            logger.info(
                    " received: {}, total: {}, sendFailed: {}, sended: {}",
                    collector.getReceived().intValue(),
                    collector.getTotal(),
                    sendFailed.get(),
                    sended.get());
            Thread.sleep(2000);
        }
        // save the user info
        dagUserInfo.writeDagTransferUser();
        System.exit(0);
    }

    public void queryAccountInfo(BigInteger qps) throws InterruptedException {
        System.out.println("Start queryAccountInfo...");
        // get the user
        List<DagTransferUser> allUser = dagUserInfo.getUserList();
        RateLimiter rateLimiter = RateLimiter.create(qps.intValue());
        AtomicInteger getted = new AtomicInteger(0);
        for (Integer i = 0; i < allUser.size(); i++) {
            final Integer index = i;
            rateLimiter.acquire();
            threadPoolService
                    .getThreadPool()
                    .execute(
                            new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Tuple2<BigInteger, BigInteger> result =
                                                dagTransfer.userBalance(
                                                        allUser.get(index).getUser());

                                        if (result.getValue1().compareTo(new BigInteger("0"))
                                                == 0) {
                                            allUser.get(index).setAmount(result.getValue2());
                                        } else {
                                            System.out.println(
                                                    " Query failed, user is "
                                                            + allUser.get(index).getUser());
                                            System.exit(0);
                                        }
                                        int all = getted.incrementAndGet();
                                        if (all >= allUser.size()) {
                                            System.out.println(
                                                    dateFormat.format(new Date())
                                                            + " Query account finished");
                                        }
                                    } catch (Exception e) {
                                        System.out.println(
                                                " Query failed, user is "
                                                        + allUser.get(index).getUser());
                                        System.exit(0);
                                    }
                                }
                            });
        }
        while (getted.get() < allUser.size()) {
            Thread.sleep(50);
        }
    }

    public void userTransfer(BigInteger count, BigInteger qps) throws InterruptedException {
        System.out.println("Start userTransfer test...");
        System.out.println("===================================================================");
        queryAccountInfo(qps);
        long startTime = System.currentTimeMillis();
        AtomicInteger sended = new AtomicInteger(0);
        AtomicInteger sendFailed = new AtomicInteger(0);
        collector.setTotal(count.intValue());
        collector.setStartTimestamp(startTime);
        Integer area = count.intValue() / 10;
        RateLimiter rateLimiter = RateLimiter.create(qps.intValue());
        // transfer balance
        for (Integer i = 0; i < count.intValue(); i++) {
            final Integer index = i;
            rateLimiter.acquire();
            threadPoolService
                    .getThreadPool()
                    .execute(
                            new Runnable() {
                                @Override
                                public void run() {
                                    ParallelOkCallback callback =
                                            new ParallelOkCallback(
                                                    collector,
                                                    dagUserInfo,
                                                    ParallelOkCallback.TRANS_CALLBACK);
                                    try {
                                        DagTransferUser from = dagUserInfo.getFrom(index);
                                        DagTransferUser to = dagUserInfo.getTo(index);
                                        Random random = new Random();
                                        int r = random.nextInt(100) + 1;
                                        BigInteger amount = BigInteger.valueOf(r);
                                        callback.setFromUser(from);
                                        callback.setToUser(to);
                                        callback.setAmount(amount);
                                        callback.setTimeout(0);
                                        callback.recordStartTime();
                                        dagTransfer.userTransfer(
                                                from.getUser(), to.getUser(), amount, callback);
                                        long elapsed = System.currentTimeMillis() - startTime;
                                        sended.incrementAndGet();
                                        double sendSpeed = sended.get() / ((double) elapsed / 1000);
                                        if (sended.get() >= area && ((sended.get() % area) == 0)) {
                                            System.out.println(
                                                    "Already sent: "
                                                            + sended.get()
                                                            + "/"
                                                            + count
                                                            + " transactions");
                                        }
                                    } catch (Exception e) {
                                        logger.warn(
                                                "userTransfer failed, error info: {}",
                                                e.getMessage());
                                        TransactionReceipt receipt = new TransactionReceipt();
                                        receipt.setStatus("-1");
                                        receipt.setMessage(
                                                "userTransfer failed, error info: "
                                                        + e.getMessage());
                                        callback.onResponse(receipt);
                                        sendFailed.incrementAndGet();
                                    }
                                }
                            });
        }
        while (collector.getReceived().intValue() != count.intValue()) {
            Thread.sleep(2000);
            logger.info(
                    " received: {}, total: {}, sended: {}, sendFailed: {}",
                    collector.getReceived().intValue(),
                    collector.getTotal(),
                    sended.get(),
                    sendFailed.get());
        }
        veryTransferData(qps);
        System.exit(0);
    }

    public void veryTransferData(BigInteger qps) throws InterruptedException {
        System.out.println("Start veryTransferData...");
        RateLimiter rateLimiter = RateLimiter.create(qps.intValue());
        AtomicInteger verify_success = new AtomicInteger(0);
        AtomicInteger verify_failed = new AtomicInteger(0);

        List<DagTransferUser> allUser = dagUserInfo.getUserList();
        for (Integer i = 0; i < allUser.size(); i++) {
            final Integer index = i;
            rateLimiter.acquire();
            threadPoolService
                    .getThreadPool()
                    .execute(
                            new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Tuple2<BigInteger, BigInteger> result =
                                                dagTransfer.userBalance(
                                                        allUser.get(index).getUser());

                                        String user = allUser.get(index).getUser();
                                        BigInteger local = allUser.get(index).getAmount();
                                        BigInteger remote = result.getValue2();

                                        if (result.getValue1().compareTo(new BigInteger("0"))
                                                != 0) {
                                            logger.error(
                                                    " query failed, user "
                                                            + user
                                                            + " ret code "
                                                            + result.getValue1());
                                            verify_failed.incrementAndGet();
                                            return;
                                        }
                                        if (local.compareTo(remote) != 0) {
                                            verify_failed.incrementAndGet();
                                            logger.error(
                                                    " local amount is not same as remote, user "
                                                            + user
                                                            + " local "
                                                            + local
                                                            + " remote "
                                                            + remote);
                                        } else {
                                            verify_success.incrementAndGet();
                                        }
                                    } catch (Exception e) {
                                        logger.error(
                                                "get amount failed, error info: {}",
                                                e.getMessage());
                                        verify_failed.incrementAndGet();
                                    }
                                }
                            });
        }
        while (verify_success.get() + verify_failed.get() < allUser.size()) {
            Thread.sleep(40);
        }
        System.out.println("validation:");
        System.out.println(" \tuser count is " + allUser.size());
        System.out.println(" \tverify_success count is " + verify_success);
        System.out.println(" \tverify_failed count is " + verify_failed);
    }
}
