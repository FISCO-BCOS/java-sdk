package org.fisco.bcos.sdk.v3.test.transaction.model.dto;

import org.junit.Assert;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

public class CallResponseTest {

    @Test
    public void testGettersAndSetters() {
        CallResponse response = new CallResponse();
        
        response.setValues("test values");
        Assert.assertEquals("test values", response.getValues());
        
        List<Object> returnObject = new ArrayList<>();
        returnObject.add("object1");
        response.setReturnObject(returnObject);
        Assert.assertEquals(returnObject, response.getReturnObject());
        
        response.setReturnABIObject(new ArrayList<>());
        Assert.assertNotNull(response.getReturnABIObject());
    }

    @Test
    public void testExtendsCommonResponse() {
        CallResponse response = new CallResponse();
        
        response.setReturnCode(200);
        response.setReturnMessage("Success");
        
        Assert.assertEquals(200, response.getReturnCode());
        Assert.assertEquals("Success", response.getReturnMessage());
    }

    @Test
    public void testToString() {
        CallResponse response = new CallResponse();
        response.setValues("test");
        
        String result = response.toString();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("CallResponse"));
        Assert.assertTrue(result.contains("test"));
    }

    @Test
    public void testWithNullValues() {
        CallResponse response = new CallResponse();
        
        Assert.assertNull(response.getValues());
        Assert.assertNull(response.getReturnObject());
        Assert.assertNull(response.getReturnABIObject());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testDeprecatedResults() {
        CallResponse response = new CallResponse();
        
        response.setResults(new ArrayList<>());
        Assert.assertNotNull(response.getResults());
    }
}
