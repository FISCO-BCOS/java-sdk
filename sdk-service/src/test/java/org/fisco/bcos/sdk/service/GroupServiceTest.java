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
package org.fisco.bcos.sdk.test.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.fisco.bcos.sdk.service.GroupService;
import org.fisco.bcos.sdk.service.GroupServiceImpl;
import org.junit.Assert;
import org.junit.Test;

public class GroupServiceTest {
    @Test
    public void testUpdateGroupNodeList() throws InterruptedException {
        GroupService groupService = new GroupServiceImpl(1);
        ExecutorService threadPool = Executors.newFixedThreadPool(50);
        List<String> nodeList = (new ArrayList<>());
        for (int i = 1; i <= 100; i++) {
            nodeList.add("127.0.0.1:" + (20200 + i));
        }
        // access the groupService concurrently
        for (int i = 0; i < 100; i++) {
            final Integer nodeIndex = i;
            threadPool.execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String insertedNode = nodeList.get(nodeIndex);
                                groupService.insertNode(insertedNode);
                                Assert.assertEquals(
                                        true,
                                        groupService.getGroupNodesInfo().contains(insertedNode));
                            } catch (Exception e) {
                                System.out.println("run exception, error info:" + e.getMessage());
                            }
                        }
                    });
        }
        awaitAfterShutdown(threadPool);
        // check the groupService
        Assert.assertEquals(100, groupService.getGroupNodesInfo().size());
        ExecutorService threadPool2 = Executors.newCachedThreadPool();
        for (int i = 0; i < 100; i++) {
            final Integer nodeIndex = i;
            threadPool2.execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String removedNode = nodeList.get(nodeIndex);
                                groupService.removeNode(removedNode);
                                Assert.assertEquals(
                                        false,
                                        groupService.getGroupNodesInfo().contains(removedNode));
                            } catch (Exception e) {
                                System.out.println(
                                        "run remove exception, error info:" + e.getMessage());
                            }
                        }
                    });
        }
        awaitAfterShutdown(threadPool2);
        Assert.assertEquals(0, groupService.getGroupNodesInfo().size());
    }

    @Test
    public void testConcurrency() {
        GroupService groupService = new GroupServiceImpl(1);
        List<String> nodeList = (new ArrayList<>());
        for (int i = 1; i <= 100; i++) {
            nodeList.add("127.0.0.1:" + (20200 + i));
        }
        // two thread pool, one insert and the another remove
        ExecutorService threadPool1 = Executors.newCachedThreadPool();
        ExecutorService threadPool2 = Executors.newCachedThreadPool();
        for (int i = 0; i < 100; i++) {
            final Integer nodeIndex = i;
            threadPool1.execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String insertedNode = nodeList.get(nodeIndex);
                                groupService.insertNode(insertedNode);
                            } catch (Exception e) {
                                System.out.println(
                                        "run insert exception, error info:" + e.getMessage());
                            }
                        }
                    });
        }
        for (int i = 0; i < 100; i++) {
            final Integer nodeIndex = i;
            threadPool2.execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String removedNode = nodeList.get(nodeIndex);
                                groupService.removeNode(removedNode);
                            } catch (Exception e) {
                                System.out.println(
                                        "run remove exception, error info:" + e.getMessage());
                            }
                        }
                    });
        }
        awaitAfterShutdown(threadPool1);
        awaitAfterShutdown(threadPool2);
    }

    public static void awaitAfterShutdown(ExecutorService threadPool) {
        threadPool.shutdown();
        try {
            while (!threadPool.isTerminated()) {
                threadPool.awaitTermination(10, TimeUnit.MILLISECONDS);
            }
            threadPool.shutdownNow();
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
