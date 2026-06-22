package org.fisco.bcos.sdk.v3.test.model;

import org.fisco.bcos.sdk.v3.model.NodeVersion;
import org.junit.Assert;
import org.junit.Test;

public class NodeVersionTest {

    @Test
    public void testGetNodeVersion() {
        NodeVersion nodeVersion = new NodeVersion();
        NodeVersion.ClientVersion clientVersion = new NodeVersion.ClientVersion();
        
        nodeVersion.setResult(clientVersion);
        Assert.assertEquals(clientVersion, nodeVersion.getNodeVersion());
    }

    @Test
    public void testClientVersionGettersAndSetters() {
        NodeVersion.ClientVersion cv = new NodeVersion.ClientVersion();
        
        cv.setVersion("3.0.0");
        Assert.assertEquals("3.0.0", cv.getVersion());
        
        cv.setSupportedVersion("3.0.0");
        Assert.assertEquals("3.0.0", cv.getSupportedVersion());
        
        cv.setChainId("chain0");
        Assert.assertEquals("chain0", cv.getChainId());
        
        cv.setBuildTime("2023-01-01");
        Assert.assertEquals("2023-01-01", cv.getBuildTime());
        
        cv.setBuildType("Release");
        Assert.assertEquals("Release", cv.getBuildType());
        
        cv.setGitBranch("master");
        Assert.assertEquals("master", cv.getGitBranch());
        
        cv.setGitCommitHash("abc123");
        Assert.assertEquals("abc123", cv.getGitCommitHash());
    }

    @Test
    public void testClientVersionEquals() {
        NodeVersion.ClientVersion cv1 = new NodeVersion.ClientVersion();
        cv1.setVersion("3.0.0");
        cv1.setSupportedVersion("3.0.0");
        cv1.setChainId("chain0");
        cv1.setBuildTime("2023-01-01");
        cv1.setBuildType("Release");
        cv1.setGitBranch("master");
        cv1.setGitCommitHash("abc123");

        NodeVersion.ClientVersion cv2 = new NodeVersion.ClientVersion();
        cv2.setVersion("3.0.0");
        cv2.setSupportedVersion("3.0.0");
        cv2.setChainId("chain0");
        cv2.setBuildTime("2023-01-01");
        cv2.setBuildType("Release");
        cv2.setGitBranch("master");
        cv2.setGitCommitHash("abc123");

        NodeVersion.ClientVersion cv3 = new NodeVersion.ClientVersion();
        cv3.setVersion("3.1.0");

        // Test equality
        Assert.assertEquals(cv1, cv2);
        
        // Test same object
        Assert.assertEquals(cv1, cv1);
        
        // Test different version
        Assert.assertNotEquals(cv1, cv3);
        
        // Test null
        Assert.assertNotEquals(cv1, null);
        
        // Test different class
        Assert.assertNotEquals(cv1, "string");
    }

    @Test
    public void testClientVersionHashCode() {
        NodeVersion.ClientVersion cv1 = new NodeVersion.ClientVersion();
        cv1.setVersion("3.0.0");
        cv1.setSupportedVersion("3.0.0");

        NodeVersion.ClientVersion cv2 = new NodeVersion.ClientVersion();
        cv2.setVersion("3.0.0");
        cv2.setSupportedVersion("3.0.0");

        Assert.assertEquals(cv1.hashCode(), cv2.hashCode());
    }

    @Test
    public void testClientVersionToString() {
        NodeVersion.ClientVersion cv = new NodeVersion.ClientVersion();
        cv.setVersion("3.0.0");
        cv.setChainId("chain0");
        
        String result = cv.toString();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("3.0.0"));
        Assert.assertTrue(result.contains("chain0"));
        Assert.assertTrue(result.contains("ClientVersion"));
    }

    @Test
    public void testClientVersionAllFieldsNull() {
        NodeVersion.ClientVersion cv1 = new NodeVersion.ClientVersion();
        NodeVersion.ClientVersion cv2 = new NodeVersion.ClientVersion();
        
        Assert.assertEquals(cv1, cv2);
        Assert.assertEquals(cv1.hashCode(), cv2.hashCode());
    }

    @Test
    public void testClientVersionPartialFields() {
        NodeVersion.ClientVersion cv = new NodeVersion.ClientVersion();
        cv.setVersion("3.0.0");
        cv.setChainId("chain0");
        
        Assert.assertNotNull(cv.getVersion());
        Assert.assertNotNull(cv.getChainId());
        Assert.assertNull(cv.getBuildTime());
        Assert.assertNull(cv.getGitBranch());
    }
}
