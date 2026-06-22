package org.fisco.bcos.sdk.v3.test.model;

import org.fisco.bcos.sdk.v3.model.JsonRpcResponse;
import org.junit.Assert;
import org.junit.Test;

public class JsonRpcResponseTest {

    @Test
    public void testJsonRpcResponseGettersAndSetters() {
        JsonRpcResponse<String> response = new JsonRpcResponse<>();

        response.setId(123L);
        Assert.assertEquals(123L, response.getId());

        response.setJsonrpc("2.0");
        Assert.assertEquals("2.0", response.getJsonrpc());

        response.setResult("test result");
        Assert.assertEquals("test result", response.getResult());

        response.setRawResponse("{\"id\":123}");
        Assert.assertEquals("{\"id\":123}", response.getRawResponse());
    }

    @Test
    public void testJsonRpcResponseWithError() {
        JsonRpcResponse<String> response = new JsonRpcResponse<>();

        JsonRpcResponse.Error error = new JsonRpcResponse.Error(404, "Not Found");
        response.setError(error);

        Assert.assertNotNull(response.getError());
        Assert.assertTrue(response.hasError());
        Assert.assertEquals(404, response.getError().getCode());
        Assert.assertEquals("Not Found", response.getError().getMessage());
    }

    @Test
    public void testJsonRpcResponseWithoutError() {
        JsonRpcResponse<String> response = new JsonRpcResponse<>();

        Assert.assertNull(response.getError());
        Assert.assertFalse(response.hasError());
    }

    @Test
    public void testErrorDefaultConstructor() {
        JsonRpcResponse.Error error = new JsonRpcResponse.Error();
        Assert.assertNotNull(error);
    }

    @Test
    public void testErrorWithParameters() {
        JsonRpcResponse.Error error = new JsonRpcResponse.Error(500, "Internal Server Error");

        Assert.assertEquals(500, error.getCode());
        Assert.assertEquals("Internal Server Error", error.getMessage());
    }

    @Test
    public void testErrorGettersAndSetters() {
        JsonRpcResponse.Error error = new JsonRpcResponse.Error();

        error.setCode(400);
        Assert.assertEquals(400, error.getCode());

        error.setMessage("Bad Request");
        Assert.assertEquals("Bad Request", error.getMessage());

        error.setData("additional data");
        Assert.assertEquals("additional data", error.getData());
    }

    @Test
    public void testJsonRpcResponseWithDifferentResultTypes() {
        // Test with Integer result
        JsonRpcResponse<Integer> intResponse = new JsonRpcResponse<>();
        intResponse.setResult(42);
        Assert.assertEquals(Integer.valueOf(42), intResponse.getResult());

        // Test with Boolean result
        JsonRpcResponse<Boolean> boolResponse = new JsonRpcResponse<>();
        boolResponse.setResult(true);
        Assert.assertEquals(Boolean.TRUE, boolResponse.getResult());
    }
}

