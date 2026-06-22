package org.fisco.bcos.sdk.v3.test.utils;

import org.fisco.bcos.sdk.v3.utils.ThreadPoolService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolServiceTest {

    private ThreadPoolService threadPoolService;

    @Before
    public void setUp() {
        threadPoolService = null;
    }

    @After
    public void tearDown() {
        if (threadPoolService != null) {
            threadPoolService.stop();
        }
    }

    @Test
    public void testConstructorWithThreadNameAndMaxBlockingQueue() {
        // Test constructor with thread name and max blocking queue size
        threadPoolService = new ThreadPoolService("test-thread", 100);
        
        Assert.assertNotNull(threadPoolService);
        Assert.assertNotNull(threadPoolService.getThreadPool());
        Assert.assertFalse(threadPoolService.getThreadPool().isShutdown());
    }

    @Test
    public void testConstructorWithAllParameters() {
        // Test constructor with all parameters
        threadPoolService = new ThreadPoolService("test-thread-full", 4, 8, 30, 50);
        
        Assert.assertNotNull(threadPoolService);
        Assert.assertNotNull(threadPoolService.getThreadPool());
        Assert.assertFalse(threadPoolService.getThreadPool().isShutdown());
    }

    @Test
    public void testConstructorWithCorePoolSize() {
        // Test constructor with thread name, core pool size, and max blocking queue
        threadPoolService = new ThreadPoolService("test-thread-core", 2, 100);
        
        Assert.assertNotNull(threadPoolService);
        Assert.assertNotNull(threadPoolService.getThreadPool());
    }

    @Test
    public void testGetThreadPool() {
        threadPoolService = new ThreadPoolService("test-get-pool", 10);
        
        ExecutorService pool = threadPoolService.getThreadPool();
        
        Assert.assertNotNull(pool);
        Assert.assertFalse(pool.isShutdown());
        Assert.assertFalse(pool.isTerminated());
    }

    @Test
    public void testExecuteTask() throws InterruptedException {
        threadPoolService = new ThreadPoolService("test-execute", 10);
        
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger counter = new AtomicInteger(0);
        
        threadPoolService.getThreadPool().execute(() -> {
            counter.incrementAndGet();
            latch.countDown();
        });
        
        boolean completed = latch.await(5, TimeUnit.SECONDS);
        Assert.assertTrue("Task should complete", completed);
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void testExecuteMultipleTasks() throws InterruptedException {
        threadPoolService = new ThreadPoolService("test-multiple", 4, 50);
        
        final CountDownLatch latch = new CountDownLatch(10);
        final AtomicInteger counter = new AtomicInteger(0);
        
        for (int i = 0; i < 10; i++) {
            threadPoolService.getThreadPool().execute(() -> {
                counter.incrementAndGet();
                latch.countDown();
            });
        }
        
        boolean completed = latch.await(5, TimeUnit.SECONDS);
        Assert.assertTrue("All tasks should complete", completed);
        Assert.assertEquals(10, counter.get());
    }

    @Test
    public void testStop() {
        threadPoolService = new ThreadPoolService("test-stop", 10);
        
        ExecutorService pool = threadPoolService.getThreadPool();
        Assert.assertFalse(pool.isShutdown());
        
        threadPoolService.stop();
        
        Assert.assertTrue(pool.isShutdown());
        Assert.assertTrue(pool.isTerminated());
    }

    @Test
    public void testStopThreadPool() {
        ThreadPoolService tempService = new ThreadPoolService("test-static-stop", 10);
        ExecutorService pool = tempService.getThreadPool();
        
        Assert.assertFalse(pool.isShutdown());
        
        ThreadPoolService.stopThreadPool(pool);
        
        Assert.assertTrue(pool.isShutdown());
        Assert.assertTrue(pool.isTerminated());
    }

    @Test
    public void testStopWithRunningTasks() throws InterruptedException {
        threadPoolService = new ThreadPoolService("test-stop-running", 10);
        
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch taskLatch = new CountDownLatch(1);
        
        threadPoolService.getThreadPool().execute(() -> {
            try {
                startLatch.countDown();
                taskLatch.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // Wait for task to start
        startLatch.await(1, TimeUnit.SECONDS);
        
        // Release task and stop
        taskLatch.countDown();
        threadPoolService.stop();
        
        Assert.assertTrue(threadPoolService.getThreadPool().isShutdown());
    }

    @Test
    public void testThreadPoolRejectionPolicy() throws InterruptedException {
        // Create a thread pool with small queue to test rejection policy
        threadPoolService = new ThreadPoolService("test-rejection", 1, 1, 60, 1);
        
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch blockLatch = new CountDownLatch(1);
        final AtomicInteger executedTasks = new AtomicInteger(0);
        
        // Submit blocking task to fill the pool
        threadPoolService.getThreadPool().execute(() -> {
            try {
                executedTasks.incrementAndGet();
                startLatch.countDown();
                blockLatch.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // Wait for the first task to start
        startLatch.await(1, TimeUnit.SECONDS);
        
        // Try to submit more tasks
        // With CallerRunsPolicy, rejected tasks should run in the caller's thread
        threadPoolService.getThreadPool().execute(() -> {
            executedTasks.incrementAndGet();
        });
        
        blockLatch.countDown();
        
        // Give some time for tasks to complete
        Thread.sleep(100);
        
        Assert.assertTrue("At least some tasks should execute", executedTasks.get() >= 1);
    }

    @Test
    public void testDefaultKeepAliveTime() {
        // Test that default keep alive time is set correctly
        Assert.assertEquals(Integer.valueOf(60), ThreadPoolService.DEFAULT_KEEP_ALIVE_TIME);
    }

    @Test
    public void testConcurrentExecution() throws InterruptedException {
        threadPoolService = new ThreadPoolService("test-concurrent", 4, 100);
        
        final int taskCount = 20;
        final CountDownLatch latch = new CountDownLatch(taskCount);
        final AtomicInteger counter = new AtomicInteger(0);
        
        for (int i = 0; i < taskCount; i++) {
            threadPoolService.getThreadPool().execute(() -> {
                try {
                    // Simulate some work
                    Thread.sleep(10);
                    counter.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        boolean completed = latch.await(10, TimeUnit.SECONDS);
        Assert.assertTrue("All tasks should complete", completed);
        Assert.assertEquals(taskCount, counter.get());
    }

    @Test(expected = NullPointerException.class)
    public void testStopThreadPoolWithNull() {
        ThreadPoolService.stopThreadPool(null);
    }

    @Test
    public void testThreadNaming() {
        threadPoolService = new ThreadPoolService("custom-thread-name", 10);
        
        final String[] threadName = new String[1];
        final CountDownLatch latch = new CountDownLatch(1);
        
        threadPoolService.getThreadPool().execute(() -> {
            threadName[0] = Thread.currentThread().getName();
            latch.countDown();
        });
        
        try {
            latch.await(5, TimeUnit.SECONDS);
            Assert.assertNotNull(threadName[0]);
            Assert.assertTrue("Thread name should contain 'custom-thread-name'", 
                threadName[0].contains("custom-thread-name"));
        } catch (InterruptedException e) {
            Assert.fail("Test interrupted");
        }
    }
}
