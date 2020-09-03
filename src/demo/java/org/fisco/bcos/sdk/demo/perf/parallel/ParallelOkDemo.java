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
import org.fisco.bcos.sdk.contract.exceptions.ContractException;
import org.fisco.bcos.sdk.demo.contract.ParallelOk;
import org.fisco.bcos.sdk.demo.perf.callback.ParallelOkCallback;
import org.fisco.bcos.sdk.demo.perf.collector.PerformanceCollector;
import org.fisco.bcos.sdk.demo.perf.model.DagTransferUser;
import org.fisco.bcos.sdk.demo.perf.model.DagUserInfo;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.utils.ThreadPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParallelOkDemo {
    private static final Logger logger = LoggerFactory.getLogger(ParallelOkDemo.class);
    private static AtomicInteger sended = new AtomicInteger(0);
    private AtomicInteger getted = new AtomicInteger(0);

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final ParallelOk parallelOk;
    private final ThreadPoolService threadPoolService;
    private final PerformanceCollector collector;
    private final DagUserInfo dagUserInfo;

    public ParallelOkDemo(
            ParallelOk parallelOk, DagUserInfo dagUserInfo, ThreadPoolService threadPoolService) {
        this.threadPoolService = threadPoolService;
        this.parallelOk = parallelOk;
        this.dagUserInfo = dagUserInfo;
        this.collector = new PerformanceCollector();
    }

    public void veryTransferData(BigInteger qps) throws InterruptedException {
        RateLimiter rateLimiter = RateLimiter.create(qps.intValue());
        System.out.println("===================================================================");
        AtomicInteger verifyFailed = new AtomicInteger(0);
        AtomicInteger verifySuccess = new AtomicInteger(0);

        final List<DagTransferUser> userInfo = dagUserInfo.getUserList();
        int userSize = userInfo.size();
        for (int i = 0; i < userSize; i++) {
            rateLimiter.acquire();
            final int userIndex = i;
            threadPoolService
                    .getThreadPool()
                    .execute(
                            new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String user = userInfo.get(userIndex).getUser();
                                        BigInteger balance = parallelOk.balanceOf(user);
                                        BigInteger localAmount =
                                                userInfo.get(userIndex).getAmount();
                                        if (localAmount.compareTo(balance) != 0) {
                                            logger.error(
                                                    "local balance is not the same as the remote, user: {}, local balance: {}, remote balance: {}",
                                                    user,
                                                    localAmount,
                                                    balance);
                                            verifyFailed.incrementAndGet();
                                        } else {
                                            verifySuccess.incrementAndGet();
                                        }
                                    } catch (ContractException exception) {
                                        verifyFailed.incrementAndGet();
                                        logger.error(
                                                "get remote balance failed, error info: "
                                                        + exception.getMessage());
                                    }
                                }
                            });
        }
        while (verifySuccess.get() + verifyFailed.get() < userSize) {
            Thread.sleep(40);
        }

        System.out.println("validation:");
        System.out.println(" \tuser count is " + userSize);
        System.out.println(" \tverify_success count is " + verifySuccess);
        System.out.println(" \tverify_failed count is " + verifyFailed);
    }

    public void userAdd(BigInteger userCount, BigInteger qps)
            throws InterruptedException, IOException {
        System.out.println("===================================================================");
        System.out.println("Start UserAdd test, count " + userCount);
        RateLimiter limiter = RateLimiter.create(qps.intValue());

        long currentSeconds = System.currentTimeMillis() / 1000L;
        Integer area = userCount.intValue() / 10;
        long startTime = System.currentTimeMillis();
        collector.setTotal(userCount.intValue());
        collector.setStartTimestamp(startTime);
        AtomicInteger sendFailed = new AtomicInteger(0);
        for (Integer i = 0; i < userCount.intValue(); i++) {
            final Integer index = i;
            limiter.acquire();
            threadPoolService
                    .getThreadPool()
                    .execute(
                            new Runnable() {
                                @Override
                                public void run() {
                                    // generate the user according to currentSeconds
                                    String user =
                                            Long.toHexString(currentSeconds)
                                                    + Integer.toHexString(index);
                                    BigInteger amount = new BigInteger("1000000000");
                                    DagTransferUser dtu = new DagTransferUser();
                                    dtu.setUser(user);
                                    dtu.setAmount(amount);
                                    ParallelOkCallback callback =
                                            new ParallelOkCallback(
                                                    collector,
                                                    dagUserInfo,
                                                    ParallelOkCallback.ADD_USER_CALLBACK);
                                    callback.setTimeout(0);
                                    callback.setUser(dtu);
                                    try {
                                        callback.recordStartTime();
                                        parallelOk.set(user, amount, callback);
                                        int current = sended.incrementAndGet();

                                        if (current >= area && ((current % area) == 0)) {
                                            long elapsed = System.currentTimeMillis() - startTime;
                                            double sendSpeed = current / ((double) elapsed / 1000);
                                            System.out.println(
                                                    "Already sended: "
                                                            + current
                                                            + "/"
                                                            + userCount
                                                            + " transactions"
                                                            + ",QPS="
                                                            + sendSpeed);
                                        }

                                    } catch (Exception e) {
                                        logger.warn(
                                                "addUser failed, error info: {}", e.getMessage());
                                        sendFailed.incrementAndGet();
                                        TransactionReceipt receipt = new TransactionReceipt();
                                        receipt.setStatus("-1");
                                        receipt.setMessage(
                                                "userAdd failed, error info: " + e.getMessage());
                                        callback.onResponse(receipt);
                                    }
                                }
                            });
        }
        while (collector.getReceived() != userCount.intValue()) {
            logger.info(
                    " sendFailed: {}, received: {}, total: {}",
                    sendFailed.get(),
                    collector.getReceived().intValue(),
                    collector.getTotal());
            Thread.sleep(100);
        }
        dagUserInfo.setContractAddr(parallelOk.getContractAddress());
        dagUserInfo.writeDagTransferUser();
        System.exit(0);
    }

    public void queryAccount() throws InterruptedException {
        final List<DagTransferUser> allUsers = dagUserInfo.getUserList();
        for (Integer i = 0; i < allUsers.size(); i++) {
            try {
                BigInteger result = parallelOk.balanceOf(allUsers.get(i).getUser());
                allUsers.get(i).setAmount(result);
                int all = getted.incrementAndGet();
                if (all >= allUsers.size()) {
                    System.out.println(dateFormat.format(new Date()) + " Query account finished");
                }
            } catch (ContractException exception) {
                logger.warn(
                        "queryAccount for {} failed, error info: {}",
                        allUsers.get(i).getUser(),
                        exception.getMessage());
            }
        }
    }

    public void userTransfer(BigInteger count, BigInteger qps)
            throws InterruptedException, IOException {
        System.out.println("Querying account info...");
        queryAccount();
        System.out.println("Sending transfer transactions...");
        RateLimiter limiter = RateLimiter.create(qps.intValue());
        int division = count.intValue() / 10;
        long startTime = System.currentTimeMillis();
        collector.setStartTimestamp(startTime);
        collector.setTotal(count.intValue());
        AtomicInteger sendFailed = new AtomicInteger(0);
        for (Integer i = 0; i < count.intValue(); i++) {
            limiter.acquire();
            final int index = i;
            threadPoolService
                    .getThreadPool()
                    .execute(
                            new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Random random = new Random();
                                        int r = random.nextInt(100);
                                        BigInteger amount = BigInteger.valueOf(r);

                                        ParallelOkCallback callback =
                                                new ParallelOkCallback(
                                                        collector,
                                                        dagUserInfo,
                                                        ParallelOkCallback.TRANS_CALLBACK);
                                        callback.setTimeout(0);
                                        DagTransferUser from = dagUserInfo.getFrom(index);
                                        DagTransferUser to = dagUserInfo.getTo(index);

                                        callback.setFromUser(from);
                                        callback.setToUser(to);
                                        callback.setAmount(amount);
                                        callback.recordStartTime();
                                        parallelOk.transfer(
                                                from.getUser(), to.getUser(), amount, callback);
                                        int current = sended.incrementAndGet();
                                        if (current >= division && ((current % division) == 0)) {
                                            long elapsed = System.currentTimeMillis() - startTime;
                                            double sendSpeed = current / ((double) elapsed / 1000);
                                            System.out.println(
                                                    "Already sent: "
                                                            + current
                                                            + "/"
                                                            + count
                                                            + " transactions"
                                                            + ",QPS="
                                                            + sendSpeed);
                                        }
                                    } catch (Exception e) {
                                        logger.error(
                                                "call transfer failed, error info: {}",
                                                e.getMessage());
                                        TransactionReceipt receipt = new TransactionReceipt();
                                        receipt.setStatus("-1");
                                        receipt.setMessage(
                                                "call transfer failed, error info: "
                                                        + e.getMessage());
                                        collector.onMessage(receipt, Long.valueOf(0));
                                        sendFailed.incrementAndGet();
                                    }
                                }
                            });
        }

        while (collector.getReceived() != (count.intValue() - sendFailed.get())) {
            Thread.sleep(3000);
            logger.info(
                    "userTransfer: sendFailed: {}, received: {}, total: {}",
                    sendFailed.get(),
                    collector.getReceived().intValue(),
                    collector.getTotal());
        }
        veryTransferData(qps);
        System.exit(0);
    }
}
