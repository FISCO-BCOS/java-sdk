package org.fisco.bcos.sdk.v3.test.transaction.model.dto;

import org.junit.Assert;
import org.junit.Test;

public class CallRequestTest {

    @Test
    public void testConstructorWithThreeParameters() {
        String from = "0x1234";
        String to = "0x5678";
        byte[] encodedFunction = {0x01, 0x02, 0x03};
        
        CallRequest request = new CallRequest(from, to, encodedFunction);
        
        Assert.assertEquals(from, request.getFrom());
        Assert.assertEquals(to, request.getTo());
        Assert.assertArrayEquals(encodedFunction, request.getEncodedFunction());
        Assert.assertNull(request.getAbi());
    }

    @Test
    public void testConstructorWithAbi() {
        String from = "0x1234";
        String to = "0x5678";
        byte[] encodedFunction = {0x01, 0x02, 0x03};
        
        CallRequest request = new CallRequest(from, to, encodedFunction, null);
        
        Assert.assertEquals(from, request.getFrom());
        Assert.assertEquals(to, request.getTo());
        Assert.assertArrayEquals(encodedFunction, request.getEncodedFunction());
        Assert.assertNull(request.getAbi());
    }

    @Test
    public void testSetFrom() {
        CallRequest request = new CallRequest("0x1234", "0x5678", new byte[]{0x01});
        String newFrom = "0xabcd";
        request.setFrom(newFrom);
        
        Assert.assertEquals(newFrom, request.getFrom());
    }

    @Test
    public void testSetTo() {
        CallRequest request = new CallRequest("0x1234", "0x5678", new byte[]{0x01});
        String newTo = "0xef01";
        request.setTo(newTo);
        
        Assert.assertEquals(newTo, request.getTo());
    }

    @Test
    public void testSetEncodedFunction() {
        CallRequest request = new CallRequest("0x1234", "0x5678", new byte[]{0x01});
        byte[] newEncoded = {0x04, 0x05, 0x06};
        request.setEncodedFunction(newEncoded);
        
        Assert.assertArrayEquals(newEncoded, request.getEncodedFunction());
    }

    @Test
    public void testSetAbi() {
        CallRequest request = new CallRequest("0x1234", "0x5678", new byte[]{0x01});
        request.setAbi(null);
        
        Assert.assertNull(request.getAbi());
    }

    @Test
    public void testSetSign() {
        CallRequest request = new CallRequest("0x1234", "0x5678", new byte[]{0x01});
        String sign = "signature";
        request.setSign(sign);
        
        Assert.assertEquals(sign, request.getSign());
    }

    @Test
    public void testGetSignInitiallyNull() {
        CallRequest request = new CallRequest("0x1234", "0x5678", new byte[]{0x01});
        
        Assert.assertNull(request.getSign());
    }

    @Test
    public void testWithEmptyEncodedFunction() {
        CallRequest request = new CallRequest("0x1234", "0x5678", new byte[]{});
        
        Assert.assertEquals(0, request.getEncodedFunction().length);
    }
}
