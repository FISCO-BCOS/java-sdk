package org.fisco.bcos.sdk.v3.client.protocol.model;

import org.junit.Assert;
import org.junit.Test;

public class GroupStatusTest {

    @Test
    public void testGettersAndSetters() {
        GroupStatus groupStatus = new GroupStatus();
        
        groupStatus.setCode("200");
        groupStatus.setMessage("Success");
        groupStatus.setStatus("running");
        
        Assert.assertEquals("200", groupStatus.getCode());
        Assert.assertEquals("Success", groupStatus.getMessage());
        Assert.assertEquals("running", groupStatus.getStatus());
    }

    @Test
    public void testEquals() {
        GroupStatus status1 = new GroupStatus();
        status1.setCode("200");
        status1.setMessage("Success");
        status1.setStatus("running");
        
        GroupStatus status2 = new GroupStatus();
        status2.setCode("200");
        status2.setMessage("Success");
        status2.setStatus("running");
        
        GroupStatus status3 = new GroupStatus();
        status3.setCode("404");
        status3.setMessage("Not Found");
        status3.setStatus("stopped");
        
        Assert.assertEquals(status1, status2);
        Assert.assertNotEquals(status1, status3);
        Assert.assertEquals(status1, status1);
        Assert.assertNotEquals(status1, null);
        Assert.assertNotEquals(status1, "String");
    }

    @Test
    public void testHashCode() {
        GroupStatus status1 = new GroupStatus();
        status1.setCode("200");
        status1.setMessage("Success");
        status1.setStatus("running");
        
        GroupStatus status2 = new GroupStatus();
        status2.setCode("200");
        status2.setMessage("Success");
        status2.setStatus("running");
        
        Assert.assertEquals(status1.hashCode(), status2.hashCode());
    }

    @Test
    public void testToString() {
        GroupStatus groupStatus = new GroupStatus();
        groupStatus.setCode("200");
        groupStatus.setMessage("Success");
        groupStatus.setStatus("running");
        
        String result = groupStatus.toString();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("200"));
        Assert.assertTrue(result.contains("Success"));
        Assert.assertTrue(result.contains("running"));
    }

    @Test
    public void testWithNullValues() {
        GroupStatus groupStatus = new GroupStatus();
        
        Assert.assertNull(groupStatus.getCode());
        Assert.assertNull(groupStatus.getMessage());
        Assert.assertNull(groupStatus.getStatus());
    }
}
