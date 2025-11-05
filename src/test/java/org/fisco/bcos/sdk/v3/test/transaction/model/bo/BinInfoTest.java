package org.fisco.bcos.sdk.v3.test.transaction.model.bo;

import org.fisco.bcos.sdk.v3.transaction.model.bo.BinInfo;
import org.junit.Assert;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;

public class BinInfoTest {

    @Test
    public void testConstructorAndGetBin() {
        Map<String, String> bins = new HashMap<>();
        bins.put("Contract1", "0x1234567890");
        bins.put("Contract2", "0xabcdef");
        
        BinInfo binInfo = new BinInfo(bins);
        
        Assert.assertEquals("0x1234567890", binInfo.getBin("Contract1"));
        Assert.assertEquals("0xabcdef", binInfo.getBin("Contract2"));
    }

    @Test
    public void testGetBinWithNonExistentContract() {
        Map<String, String> bins = new HashMap<>();
        bins.put("Contract1", "0x1234567890");
        
        BinInfo binInfo = new BinInfo(bins);
        
        Assert.assertNull(binInfo.getBin("NonExistent"));
    }

    @Test
    public void testWithEmptyMap() {
        Map<String, String> bins = new HashMap<>();
        
        BinInfo binInfo = new BinInfo(bins);
        
        Assert.assertNull(binInfo.getBin("AnyContract"));
    }

    @Test
    public void testWithMultipleContracts() {
        Map<String, String> bins = new HashMap<>();
        bins.put("HelloWorld", "0x11111111");
        bins.put("Token", "0x22222222");
        bins.put("Storage", "0x33333333");
        
        BinInfo binInfo = new BinInfo(bins);
        
        Assert.assertEquals("0x11111111", binInfo.getBin("HelloWorld"));
        Assert.assertEquals("0x22222222", binInfo.getBin("Token"));
        Assert.assertEquals("0x33333333", binInfo.getBin("Storage"));
    }
}
