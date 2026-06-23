package org.fisco.bcos.sdk.v3.test.model;

import org.fisco.bcos.sdk.v3.model.EnumNodeVersion;
import org.junit.Assert;
import org.junit.Test;

public class EnumNodeVersionTest {

    @Test
    public void testEnumValues() {
        EnumNodeVersion[] values = EnumNodeVersion.values();
        Assert.assertNotNull(values);
        Assert.assertTrue(values.length > 0);  // At least some versions exist
    }

    @Test
    public void testUnknownVersion() {
        Assert.assertEquals(-1, EnumNodeVersion.UNKNOWN.getVersion().intValue());
    }

    @Test
    public void testBcos300RC4() {
        Assert.assertEquals(4, EnumNodeVersion.BCOS_3_0_0_RC4.getVersion().intValue());
    }

    @Test
    public void testBcos300() {
        Assert.assertEquals(0x03000000, EnumNodeVersion.BCOS_3_0_0.getVersion().intValue());
    }

    @Test
    public void testBcos310() {
        Assert.assertEquals(0x03010000, EnumNodeVersion.BCOS_3_1_0.getVersion().intValue());
    }

    @Test
    public void testValueOfKnownVersion() {
        Assert.assertEquals(EnumNodeVersion.BCOS_3_0_0, EnumNodeVersion.valueOf(0x03000000));
        Assert.assertEquals(EnumNodeVersion.BCOS_3_1_0, EnumNodeVersion.valueOf(0x03010000));
        Assert.assertEquals(EnumNodeVersion.BCOS_3_2_0, EnumNodeVersion.valueOf(0x03020000));
    }

    @Test
    public void testValueOfUnknownVersion() {
        Assert.assertEquals(EnumNodeVersion.UNKNOWN, EnumNodeVersion.valueOf(999999));
    }

    @Test
    public void testGetVersionString() {
        Assert.assertEquals("3.0.0-rc4", EnumNodeVersion.BCOS_3_0_0_RC4.getVersionString());
        Assert.assertEquals("3.0.0", EnumNodeVersion.BCOS_3_0_0.getVersionString());
        Assert.assertEquals("3.1.0", EnumNodeVersion.BCOS_3_1_0.getVersionString());
        Assert.assertEquals("3.2.0", EnumNodeVersion.BCOS_3_2_0.getVersionString());
        Assert.assertEquals("0.0.0", EnumNodeVersion.UNKNOWN.getVersionString());
    }

    @Test
    public void testCompareToVersion() {
        EnumNodeVersion v1 = EnumNodeVersion.BCOS_3_0_0;
        EnumNodeVersion v2 = EnumNodeVersion.BCOS_3_1_0;
        EnumNodeVersion v3 = EnumNodeVersion.BCOS_3_0_0;

        Assert.assertTrue(v2.compareToVersion(v1) > 0);
        Assert.assertTrue(v1.compareToVersion(v2) < 0);
        Assert.assertEquals(0, v1.compareToVersion(v3));
    }

    @Test
    public void testCompareTo() {
        EnumNodeVersion v1 = EnumNodeVersion.BCOS_3_0_0;
        EnumNodeVersion v2 = EnumNodeVersion.BCOS_3_1_0;

        Assert.assertTrue(EnumNodeVersion.compareTo(v2, v1) > 0);
        Assert.assertTrue(EnumNodeVersion.compareTo(v1, v2) < 0);
        Assert.assertEquals(0, EnumNodeVersion.compareTo(v1, v1));
    }

    @Test
    public void testToVersionObj() {
        EnumNodeVersion.Version version = EnumNodeVersion.BCOS_3_1_0.toVersionObj();
        Assert.assertNotNull(version);
        Assert.assertEquals(3, version.getMajor());
        Assert.assertEquals(1, version.getMinor());
        Assert.assertEquals(0, version.getPatch());
    }

    @Test
    public void testConvertToVersion() {
        EnumNodeVersion.Version version = EnumNodeVersion.convertToVersion(0x03010000);
        Assert.assertNotNull(version);
        Assert.assertEquals(3, version.getMajor());
        Assert.assertEquals(1, version.getMinor());
    }

    @Test
    public void testGetClassVersion() {
        EnumNodeVersion.Version version = EnumNodeVersion.getClassVersion("3.1.0");
        Assert.assertEquals(3, version.getMajor());
        Assert.assertEquals(1, version.getMinor());
        Assert.assertEquals(0, version.getPatch());
        Assert.assertEquals("", version.getExt());
    }

    @Test
    public void testGetClassVersionWithExt() {
        EnumNodeVersion.Version version = EnumNodeVersion.getClassVersion("3.0.0-rc4");
        Assert.assertEquals(3, version.getMajor());
        Assert.assertEquals(0, version.getMinor());
        Assert.assertEquals(0, version.getPatch());
        Assert.assertEquals("rc4", version.getExt());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetClassVersionInvalidFormat() {
        EnumNodeVersion.getClassVersion("invalid");
    }

    @Test
    public void testVersionToVersionString() {
        EnumNodeVersion.Version version = new EnumNodeVersion.Version();
        version.setMajor(3);
        version.setMinor(1);
        version.setPatch(0);
        Assert.assertEquals("3.1.0", version.toVersionString());
    }

    @Test
    public void testVersionToVersionStringWithExt() {
        EnumNodeVersion.Version version = new EnumNodeVersion.Version();
        version.setMajor(3);
        version.setMinor(0);
        version.setPatch(0);
        version.setExt("rc4");
        Assert.assertEquals("3.0.0-rc4", version.toVersionString());
    }

    @Test
    public void testVersionToCompatibilityVersion() {
        EnumNodeVersion.Version version = new EnumNodeVersion.Version();
        version.setMajor(3);
        version.setMinor(1);
        version.setPatch(0);
        Assert.assertEquals(0x03010000, version.toCompatibilityVersion());
    }

    @Test
    public void testVersionCompareTo() {
        EnumNodeVersion.Version v1 = new EnumNodeVersion.Version();
        v1.setMajor(3);
        v1.setMinor(0);
        v1.setPatch(0);

        EnumNodeVersion.Version v2 = new EnumNodeVersion.Version();
        v2.setMajor(3);
        v2.setMinor(1);
        v2.setPatch(0);

        Assert.assertTrue(v2.compareTo(v1) > 0);
        Assert.assertTrue(v1.compareTo(v2) < 0);
        Assert.assertEquals(0, v1.compareTo(v1));
    }

    @Test
    public void testVersionEquals() {
        EnumNodeVersion.Version v1 = new EnumNodeVersion.Version();
        v1.setMajor(3);
        v1.setMinor(1);
        v1.setPatch(0);

        EnumNodeVersion.Version v2 = new EnumNodeVersion.Version();
        v2.setMajor(3);
        v2.setMinor(1);
        v2.setPatch(0);

        EnumNodeVersion.Version v3 = new EnumNodeVersion.Version();
        v3.setMajor(3);
        v3.setMinor(2);
        v3.setPatch(0);

        Assert.assertEquals(v1, v2);
        Assert.assertNotEquals(v1, v3);
        Assert.assertNotEquals(v1, null);
        Assert.assertNotEquals(v1, "string");
    }

    @Test
    public void testVersionToString() {
        EnumNodeVersion.Version version = new EnumNodeVersion.Version();
        version.setMajor(3);
        version.setMinor(1);
        version.setPatch(0);
        version.setExt("test");
        
        String result = version.toString();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("3"));
        Assert.assertTrue(result.contains("1"));
        Assert.assertTrue(result.contains("0"));
        Assert.assertTrue(result.contains("test"));
    }

    @Test
    public void testValueFromCompatibilityVersion() {
        EnumNodeVersion.Version version = EnumNodeVersion.valueFromCompatibilityVersion(0x03010000);
        Assert.assertEquals(3, version.getMajor());
        Assert.assertEquals(1, version.getMinor());
        Assert.assertEquals(0, version.getPatch());
    }

    @Test
    public void testValueFromCompatibilityVersionRC4() {
        EnumNodeVersion.Version version = EnumNodeVersion.valueFromCompatibilityVersion(4);
        Assert.assertEquals(3, version.getMajor());
        Assert.assertEquals(0, version.getMinor());
        Assert.assertEquals(0, version.getPatch());
        Assert.assertEquals("rc4", version.getExt());
    }

    @Test
    public void testVersionGettersAndSetters() {
        EnumNodeVersion.Version version = new EnumNodeVersion.Version();
        
        version.setMajor(3);
        Assert.assertEquals(3, version.getMajor());
        
        version.setMinor(2);
        Assert.assertEquals(2, version.getMinor());
        
        version.setPatch(5);
        Assert.assertEquals(5, version.getPatch());
        
        version.setExt("beta");
        Assert.assertEquals("beta", version.getExt());
    }
}
