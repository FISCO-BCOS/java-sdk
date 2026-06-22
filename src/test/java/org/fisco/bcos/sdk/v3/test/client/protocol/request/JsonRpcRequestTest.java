package org.fisco.bcos.sdk.v3.test.client.protocol.request;

import org.fisco.bcos.sdk.v3.client.protocol.request.JsonRpcRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class JsonRpcRequestTest {

    private JsonRpcRequest<String> request;
    private List<String> testParams;

    @Before
    public void setUp() {
        // Reset the ID getter to ensure consistent test behavior
        JsonRpcRequest.setNextIdGetter(new AtomicLong(0));
        
        testParams = Arrays.asList("param1", "param2");
        request = new JsonRpcRequest<>("testMethod", testParams);
    }

    @Test
    public void testConstructor() {
        JsonRpcRequest<String> newRequest = new JsonRpcRequest<>("method1", testParams);
        
        Assert.assertNotNull(newRequest);
        Assert.assertEquals("method1", newRequest.getMethod());
        Assert.assertEquals(testParams, newRequest.getParams());
        Assert.assertEquals("2.0", newRequest.getJsonrpc());
    }

    @Test
    public void testConstructorWithNullParams() {
        JsonRpcRequest<String> newRequest = new JsonRpcRequest<>("method1", null);
        
        Assert.assertNotNull(newRequest);
        Assert.assertEquals("method1", newRequest.getMethod());
        Assert.assertNull(newRequest.getParams());
    }

    @Test
    public void testConstructorWithEmptyParams() {
        JsonRpcRequest<String> newRequest = new JsonRpcRequest<>("method1", Collections.emptyList());
        
        Assert.assertNotNull(newRequest);
        Assert.assertEquals("method1", newRequest.getMethod());
        Assert.assertEquals(0, newRequest.getParams().size());
    }

    @Test
    public void testIdIncrement() {
        // Reset the ID getter
        JsonRpcRequest.setNextIdGetter(new AtomicLong(100));
        
        JsonRpcRequest<String> request1 = new JsonRpcRequest<>("method1", testParams);
        JsonRpcRequest<String> request2 = new JsonRpcRequest<>("method2", testParams);
        JsonRpcRequest<String> request3 = new JsonRpcRequest<>("method3", testParams);
        
        Assert.assertEquals(100L, request1.getId());
        Assert.assertEquals(101L, request2.getId());
        Assert.assertEquals(102L, request3.getId());
    }

    @Test
    public void testGetJsonrpc() {
        Assert.assertEquals("2.0", request.getJsonrpc());
    }

    @Test
    public void testSetJsonrpc() {
        request.setJsonrpc("3.0");
        Assert.assertEquals("3.0", request.getJsonrpc());
    }

    @Test
    public void testGetMethod() {
        Assert.assertEquals("testMethod", request.getMethod());
    }

    @Test
    public void testSetMethod() {
        request.setMethod("newMethod");
        Assert.assertEquals("newMethod", request.getMethod());
    }

    @Test
    public void testGetParams() {
        Assert.assertEquals(testParams, request.getParams());
        Assert.assertEquals(2, request.getParams().size());
        Assert.assertEquals("param1", request.getParams().get(0));
        Assert.assertEquals("param2", request.getParams().get(1));
    }

    @Test
    public void testSetParams() {
        List<String> newParams = Arrays.asList("new1", "new2", "new3");
        request.setParams(newParams);
        
        Assert.assertEquals(newParams, request.getParams());
        Assert.assertEquals(3, request.getParams().size());
    }

    @Test
    public void testGetId() {
        long id = request.getId();
        Assert.assertTrue(id >= 0);
    }

    @Test
    public void testSetId() {
        request.setId(999L);
        Assert.assertEquals(999L, request.getId());
    }

    @Test
    public void testGetNextIdGetter() {
        AtomicLong getter = JsonRpcRequest.getNextIdGetter();
        Assert.assertNotNull(getter);
    }

    @Test
    public void testSetNextIdGetter() {
        AtomicLong newGetter = new AtomicLong(500);
        JsonRpcRequest.setNextIdGetter(newGetter);
        
        JsonRpcRequest<String> newRequest = new JsonRpcRequest<>("method", testParams);
        Assert.assertEquals(500L, newRequest.getId());
        
        JsonRpcRequest<String> anotherRequest = new JsonRpcRequest<>("method", testParams);
        Assert.assertEquals(501L, anotherRequest.getId());
    }

    @Test
    public void testEquals() {
        // Reset ID getter for consistent IDs
        JsonRpcRequest.setNextIdGetter(new AtomicLong(0));
        
        JsonRpcRequest<String> request1 = new JsonRpcRequest<>("method1", testParams);
        JsonRpcRequest<String> request2 = new JsonRpcRequest<>("method1", testParams);
        
        // Set same ID for comparison
        request2.setId(request1.getId());
        
        Assert.assertEquals(request1, request2);
    }

    @Test
    public void testEqualsWithSameObject() {
        Assert.assertEquals(request, request);
    }

    @Test
    public void testEqualsWithNull() {
        Assert.assertNotEquals(request, null);
    }

    @Test
    public void testEqualsWithDifferentClass() {
        Assert.assertNotEquals(request, "string");
    }

    @Test
    public void testEqualsWithDifferentMethod() {
        JsonRpcRequest.setNextIdGetter(new AtomicLong(0));
        
        JsonRpcRequest<String> request1 = new JsonRpcRequest<>("method1", testParams);
        JsonRpcRequest<String> request2 = new JsonRpcRequest<>("method2", testParams);
        
        request2.setId(request1.getId());
        
        Assert.assertNotEquals(request1, request2);
    }

    @Test
    public void testEqualsWithDifferentParams() {
        JsonRpcRequest.setNextIdGetter(new AtomicLong(0));
        
        JsonRpcRequest<String> request1 = new JsonRpcRequest<>("method1", testParams);
        JsonRpcRequest<String> request2 = new JsonRpcRequest<>("method1", Arrays.asList("different"));
        
        request2.setId(request1.getId());
        
        Assert.assertNotEquals(request1, request2);
    }

    @Test
    public void testEqualsWithDifferentId() {
        JsonRpcRequest.setNextIdGetter(new AtomicLong(0));
        
        JsonRpcRequest<String> request1 = new JsonRpcRequest<>("method1", testParams);
        JsonRpcRequest<String> request2 = new JsonRpcRequest<>("method1", testParams);
        
        Assert.assertNotEquals(request1, request2);
    }

    @Test
    public void testEqualsWithDifferentJsonrpc() {
        JsonRpcRequest.setNextIdGetter(new AtomicLong(0));
        
        JsonRpcRequest<String> request1 = new JsonRpcRequest<>("method1", testParams);
        JsonRpcRequest<String> request2 = new JsonRpcRequest<>("method1", testParams);
        
        request2.setId(request1.getId());
        request2.setJsonrpc("3.0");
        
        Assert.assertNotEquals(request1, request2);
    }

    @Test
    public void testHashCode() {
        JsonRpcRequest.setNextIdGetter(new AtomicLong(0));
        
        JsonRpcRequest<String> request1 = new JsonRpcRequest<>("method1", testParams);
        JsonRpcRequest<String> request2 = new JsonRpcRequest<>("method1", testParams);
        
        request2.setId(request1.getId());
        
        Assert.assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    public void testHashCodeConsistency() {
        int hashCode1 = request.hashCode();
        int hashCode2 = request.hashCode();
        
        Assert.assertEquals(hashCode1, hashCode2);
    }

    @Test
    public void testToString() {
        String str = request.toString();
        
        Assert.assertNotNull(str);
        Assert.assertTrue(str.contains("JsonRpcRequest"));
        Assert.assertTrue(str.contains("jsonrpc"));
        Assert.assertTrue(str.contains("method"));
        Assert.assertTrue(str.contains("params"));
        Assert.assertTrue(str.contains("id"));
        Assert.assertTrue(str.contains("testMethod"));
        Assert.assertTrue(str.contains("2.0"));
    }

    @Test
    public void testToStringWithNullParams() {
        JsonRpcRequest<String> nullParamsRequest = new JsonRpcRequest<>("method", null);
        String str = nullParamsRequest.toString();
        
        Assert.assertNotNull(str);
        Assert.assertTrue(str.contains("null"));
    }

    @Test
    public void testGenericTypeWithInteger() {
        List<Integer> intParams = Arrays.asList(1, 2, 3);
        JsonRpcRequest<Integer> intRequest = new JsonRpcRequest<>("intMethod", intParams);
        
        Assert.assertEquals("intMethod", intRequest.getMethod());
        Assert.assertEquals(intParams, intRequest.getParams());
        Assert.assertEquals(Integer.valueOf(1), intRequest.getParams().get(0));
    }

    @Test
    public void testGenericTypeWithObject() {
        List<Object> objParams = Arrays.asList("string", 123, true);
        JsonRpcRequest<Object> objRequest = new JsonRpcRequest<>("objMethod", objParams);
        
        Assert.assertEquals("objMethod", objRequest.getMethod());
        Assert.assertEquals(objParams, objRequest.getParams());
        Assert.assertEquals(3, objRequest.getParams().size());
    }

    @Test
    public void testConcurrentIdGeneration() throws InterruptedException {
        JsonRpcRequest.setNextIdGetter(new AtomicLong(0));
        
        int threadCount = 10;
        int requestsPerThread = 10;
        Thread[] threads = new Thread[threadCount];
        
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < requestsPerThread; j++) {
                    new JsonRpcRequest<>("method", testParams);
                }
            });
            threads[i].start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        // After all threads complete, the next ID should be threadCount * requestsPerThread
        JsonRpcRequest<String> finalRequest = new JsonRpcRequest<>("method", testParams);
        Assert.assertEquals(threadCount * requestsPerThread, finalRequest.getId());
    }

    @Test
    public void testDefaultJsonrpcVersion() {
        JsonRpcRequest<String> newRequest = new JsonRpcRequest<>("method", testParams);
        Assert.assertEquals("2.0", newRequest.getJsonrpc());
    }

    @Test
    public void testMultipleRequestsWithDifferentIds() {
        JsonRpcRequest.setNextIdGetter(new AtomicLong(0));
        
        JsonRpcRequest<String> req1 = new JsonRpcRequest<>("method1", testParams);
        JsonRpcRequest<String> req2 = new JsonRpcRequest<>("method2", testParams);
        JsonRpcRequest<String> req3 = new JsonRpcRequest<>("method3", testParams);
        
        Assert.assertNotEquals(req1.getId(), req2.getId());
        Assert.assertNotEquals(req2.getId(), req3.getId());
        Assert.assertNotEquals(req1.getId(), req3.getId());
    }
}
