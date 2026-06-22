package org.fisco.bcos.sdk.v3.transaction.model.dto;

import org.junit.Assert;
import org.junit.Test;

public class CommonResponseTest {

    @Test
    public void testDefaultConstructor() {
        CommonResponse response = new CommonResponse();
        
        Assert.assertEquals(0, response.getReturnCode());
        Assert.assertEquals("", response.getReturnMessage());
    }

    @Test
    public void testConstructorWithParameters() {
        int returnCode = 200;
        String returnMessage = "Success";
        
        CommonResponse response = new CommonResponse(returnCode, returnMessage);
        
        Assert.assertEquals(returnCode, response.getReturnCode());
        Assert.assertEquals(returnMessage, response.getReturnMessage());
    }

    @Test
    public void testSetReturnCode() {
        CommonResponse response = new CommonResponse();
        response.setReturnCode(404);
        
        Assert.assertEquals(404, response.getReturnCode());
    }

    @Test
    public void testSetReturnMessage() {
        CommonResponse response = new CommonResponse();
        response.setReturnMessage("Error");
        
        Assert.assertEquals("Error", response.getReturnMessage());
    }

    @Test
    public void testGettersAndSetters() {
        CommonResponse response = new CommonResponse();
        
        response.setReturnCode(500);
        response.setReturnMessage("Internal Server Error");
        
        Assert.assertEquals(500, response.getReturnCode());
        Assert.assertEquals("Internal Server Error", response.getReturnMessage());
    }
}
