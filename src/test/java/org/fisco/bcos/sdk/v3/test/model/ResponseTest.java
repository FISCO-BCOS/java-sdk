package org.fisco.bcos.sdk.v3.test.model;

import org.fisco.bcos.sdk.v3.model.Response;
import org.junit.Assert;
import org.junit.Test;

public class ResponseTest {

    @Test
    public void testDefaultConstructor() {
        Response response = new Response();
        Assert.assertNotNull(response);
    }

    @Test
    public void testConstructorWithParameters() {
        Response response = new Response(200, "Success");

        Assert.assertEquals(Integer.valueOf(200), response.getErrorCode());
        Assert.assertEquals("Success", response.getErrorMessage());
    }

    @Test
    public void testGettersAndSetters() {
        Response response = new Response();

        response.setErrorCode(404);
        Assert.assertEquals(Integer.valueOf(404), response.getErrorCode());

        response.setErrorMessage("Not Found");
        Assert.assertEquals("Not Found", response.getErrorMessage());

        byte[] content = "test content".getBytes();
        response.setContent(content);
        Assert.assertArrayEquals(content, response.getContent());
        Assert.assertEquals("test content", response.getContentString());
    }

    @Test
    public void testGetContentString() {
        Response response = new Response();
        byte[] content = "Hello World".getBytes();
        response.setContent(content);

        Assert.assertEquals("Hello World", response.getContentString());
    }

    @Test
    public void testToString() {
        Response response = new Response(200, "OK");
        response.setContent("response data".getBytes());

        String result = response.toString();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("200"));
        Assert.assertTrue(result.contains("OK"));
        Assert.assertTrue(result.contains("response data"));
    }

    @Test
    public void testWithEmptyContent() {
        Response response = new Response(500, "Internal Error");
        response.setContent(new byte[0]);

        Assert.assertEquals(0, response.getContent().length);
        Assert.assertEquals("", response.getContentString());
    }

    @Test
    public void testWithNullErrorCode() {
        Response response = new Response();
        response.setErrorCode(null);
        response.setErrorMessage("Error");

        Assert.assertNull(response.getErrorCode());
    }
}

