package org.fisco.bcos.sdk.v3.test.utils;

import org.fisco.bcos.sdk.v3.utils.SystemInformation;
import org.fisco.bcos.sdk.v3.utils.SystemInformation.InformationProperty;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SystemInformationTest {

    private InformationProperty testProperty;

    @Before
    public void setUp() {
        testProperty = new InformationProperty("Test Key", "Test Value");
    }

    @Test
    public void testInformationPropertyConstructor() {
        InformationProperty property = new InformationProperty("key1", "value1");
        
        Assert.assertNotNull(property);
        Assert.assertEquals("key1", property.getKey());
        Assert.assertEquals("value1", property.getValue());
    }

    @Test
    public void testInformationPropertyGetKey() {
        Assert.assertEquals("Test Key", testProperty.getKey());
    }

    @Test
    public void testInformationPropertySetKey() {
        testProperty.setKey("New Key");
        Assert.assertEquals("New Key", testProperty.getKey());
    }

    @Test
    public void testInformationPropertyGetValue() {
        Assert.assertEquals("Test Value", testProperty.getValue());
    }

    @Test
    public void testInformationPropertySetValue() {
        testProperty.setValue("New Value");
        Assert.assertEquals("New Value", testProperty.getValue());
    }

    @Test
    public void testJavaVersionProperty() {
        Assert.assertNotNull(SystemInformation.JAVA_VERSION);
        Assert.assertEquals("Java Version", SystemInformation.JAVA_VERSION.getKey());
        Assert.assertNotNull(SystemInformation.JAVA_VERSION.getValue());
        Assert.assertTrue(SystemInformation.JAVA_VERSION.getValue().length() > 0);
    }

    @Test
    public void testOsNameProperty() {
        Assert.assertNotNull(SystemInformation.OS_NAME);
        Assert.assertEquals("OS Name", SystemInformation.OS_NAME.getKey());
        Assert.assertNotNull(SystemInformation.OS_NAME.getValue());
    }

    @Test
    public void testOsArchProperty() {
        Assert.assertNotNull(SystemInformation.OS_ARCH);
        Assert.assertEquals("OS Arch", SystemInformation.OS_ARCH.getKey());
        Assert.assertNotNull(SystemInformation.OS_ARCH.getValue());
    }

    @Test
    public void testOsVersionProperty() {
        Assert.assertNotNull(SystemInformation.OS_VERSION);
        Assert.assertEquals("OS Version", SystemInformation.OS_VERSION.getKey());
        Assert.assertNotNull(SystemInformation.OS_VERSION.getValue());
    }

    @Test
    public void testVendorNameProperty() {
        Assert.assertNotNull(SystemInformation.VENDOR_NAME);
        Assert.assertEquals("Vendor Name", SystemInformation.VENDOR_NAME.getKey());
        Assert.assertNotNull(SystemInformation.VENDOR_NAME.getValue());
    }

    @Test
    public void testVendorUrlProperty() {
        Assert.assertNotNull(SystemInformation.VENDOR_URL);
        Assert.assertEquals("Vendor URL", SystemInformation.VENDOR_URL.getKey());
    }

    @Test
    public void testJvmVersionProperty() {
        Assert.assertNotNull(SystemInformation.JVM_VERSION);
        Assert.assertEquals("JVM Version", SystemInformation.JVM_VERSION.getKey());
        Assert.assertNotNull(SystemInformation.JVM_VERSION.getValue());
    }

    @Test
    public void testJvmNameProperty() {
        Assert.assertNotNull(SystemInformation.JVM_NAME);
        Assert.assertEquals("JVM Name", SystemInformation.JVM_NAME.getKey());
        Assert.assertNotNull(SystemInformation.JVM_NAME.getValue());
    }

    @Test
    public void testJvmVendorProperty() {
        Assert.assertNotNull(SystemInformation.JVM_VENDOR);
        Assert.assertEquals("JVM Vendor", SystemInformation.JVM_VENDOR.getKey());
        Assert.assertNotNull(SystemInformation.JVM_VENDOR.getValue());
    }

    @Test
    public void testJavaLibPathProperty() {
        Assert.assertNotNull(SystemInformation.JAVA_LIB_PATH);
        Assert.assertEquals("JAVA Library Path", SystemInformation.JAVA_LIB_PATH.getKey());
    }

    @Test
    public void testJdkDisabledNamedCurvesProperty() {
        Assert.assertNotNull(SystemInformation.JDK_DISABLED_NAMED_CURVES);
        Assert.assertEquals("JDK Disabled NamedCurves", SystemInformation.JDK_DISABLED_NAMED_CURVES.getKey());
    }

    @Test
    public void testJdkDisableNativeOptionProperty() {
        Assert.assertNotNull(SystemInformation.JDK_DISABLE_NATIVE_OPTION);
        Assert.assertEquals("JDK DisableNative Option", SystemInformation.JDK_DISABLE_NATIVE_OPTION.getKey());
    }

    @Test
    public void testExpectedCurves() {
        Assert.assertNotNull(SystemInformation.EXPECTED_CURVES);
        Assert.assertEquals(2, SystemInformation.EXPECTED_CURVES.size());
        Assert.assertTrue(SystemInformation.EXPECTED_CURVES.contains("secp256k1"));
        Assert.assertTrue(SystemInformation.EXPECTED_CURVES.contains("secp256r1"));
    }

    @Test
    public void testGetSystemInformation() {
        String systemInfo = SystemInformation.getSystemInformation();
        
        Assert.assertNotNull(systemInfo);
        Assert.assertTrue(systemInfo.length() > 0);
        Assert.assertTrue(systemInfo.contains("[System Information]:"));
        Assert.assertTrue(systemInfo.contains("Java Version"));
        Assert.assertTrue(systemInfo.contains("OS Name"));
    }

    @Test
    public void testSystemInformationContainsJavaVersion() {
        String systemInfo = SystemInformation.getSystemInformation();
        Assert.assertTrue("System information should contain Java Version", 
            systemInfo.contains("Java Version"));
    }

    @Test
    public void testSystemInformationContainsOsInfo() {
        String systemInfo = SystemInformation.getSystemInformation();
        Assert.assertTrue("System information should contain OS Name", 
            systemInfo.contains("OS Name"));
        Assert.assertTrue("System information should contain OS Arch", 
            systemInfo.contains("OS Arch"));
        Assert.assertTrue("System information should contain OS Version", 
            systemInfo.contains("OS Version"));
    }

    @Test
    public void testSystemInformationContainsJvmInfo() {
        String systemInfo = SystemInformation.getSystemInformation();
        Assert.assertTrue("System information should contain JVM Version", 
            systemInfo.contains("JVM Version"));
        Assert.assertTrue("System information should contain JVM Name", 
            systemInfo.contains("JVM Name"));
        Assert.assertTrue("System information should contain JVM Vendor", 
            systemInfo.contains("JVM Vendor"));
    }

    @Test
    public void testSystemInformationContainsCurveSupport() {
        String systemInfo = SystemInformation.getSystemInformation();
        Assert.assertTrue("System information should contain secp256k1 support info", 
            systemInfo.contains("secp256k1"));
        Assert.assertTrue("System information should contain secp256r1 support info", 
            systemInfo.contains("secp256r1"));
    }

    @Test
    public void testSystemInformationIsConsistent() {
        // Multiple calls should return the same information
        String info1 = SystemInformation.getSystemInformation();
        String info2 = SystemInformation.getSystemInformation();
        
        Assert.assertEquals("System information should be consistent", info1, info2);
    }

    @Test
    public void testInformationPropertyWithNullValues() {
        InformationProperty nullProperty = new InformationProperty(null, null);
        
        Assert.assertNull(nullProperty.getKey());
        Assert.assertNull(nullProperty.getValue());
        
        nullProperty.setKey("key");
        nullProperty.setValue("value");
        
        Assert.assertEquals("key", nullProperty.getKey());
        Assert.assertEquals("value", nullProperty.getValue());
    }

    @Test
    public void testInformationPropertyWithEmptyValues() {
        InformationProperty emptyProperty = new InformationProperty("", "");
        
        Assert.assertEquals("", emptyProperty.getKey());
        Assert.assertEquals("", emptyProperty.getValue());
    }
}
