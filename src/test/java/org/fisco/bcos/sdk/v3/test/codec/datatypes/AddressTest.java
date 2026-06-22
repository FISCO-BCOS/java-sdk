package org.fisco.bcos.sdk.v3.test.codec.datatypes;

import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint160;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

public class AddressTest {

    @Test
    public void testAddressFromBigInteger() {
        BigInteger value = new BigInteger("1234567890");
        Address address = new Address(value);

        Assert.assertNotNull(address);
        Assert.assertEquals("address", address.getTypeAsString());
    }

    @Test
    public void testAddressFromHexString() {
        String hexValue = "0x1234567890abcdef1234567890abcdef12345678";
        Address address = new Address(hexValue);

        Assert.assertNotNull(address);
        Assert.assertNotNull(address.getValue());
    }

    @Test
    public void testAddressFromUint160() {
        Uint160 uint160 = new Uint160(BigInteger.valueOf(100));
        Address address = new Address(uint160);

        Assert.assertNotNull(address);
        Assert.assertEquals(uint160, address.toUint160());
    }

    @Test
    public void testGetTypeAsString() {
        Address address = new Address(BigInteger.ZERO);
        Assert.assertEquals("address", address.getTypeAsString());
    }

    @Test
    public void testToString() {
        Address address = new Address(BigInteger.valueOf(255));
        String result = address.toString();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.startsWith("0x"));
        Assert.assertEquals(42, result.length()); // 0x + 40 hex chars
    }

    @Test
    public void testGetValue() {
        Address address = new Address(BigInteger.valueOf(100));
        String value = address.getValue();

        Assert.assertNotNull(value);
        Assert.assertTrue(value.startsWith("0x"));
    }

    @Test
    public void testEquals() {
        Address address1 = new Address(BigInteger.valueOf(100));
        Address address2 = new Address(BigInteger.valueOf(100));
        Address address3 = new Address(BigInteger.valueOf(200));

        Assert.assertEquals(address1, address2);
        Assert.assertNotEquals(address1, address3);
        Assert.assertEquals(address1, address1);
        Assert.assertNotEquals(address1, null);
        Assert.assertNotEquals(address1, "String");
    }

    @Test
    public void testHashCode() {
        Address address1 = new Address(BigInteger.valueOf(100));
        Address address2 = new Address(BigInteger.valueOf(100));

        Assert.assertEquals(address1.hashCode(), address2.hashCode());
    }

    @Test
    public void testDefaultAddress() {
        Assert.assertNotNull(Address.DEFAULT);
        Assert.assertEquals(BigInteger.ZERO, Address.DEFAULT.toUint160().getValue());
    }

    @Test
    public void testAddressConstants() {
        Assert.assertEquals("address", Address.TYPE_NAME);
        Assert.assertEquals(160, Address.LENGTH);
        Assert.assertEquals(40, Address.LENGTH_IN_HEX);
    }

    @Test
    public void testToUint160() {
        BigInteger value = BigInteger.valueOf(12345);
        Address address = new Address(value);
        Uint160 uint160 = address.toUint160();

        Assert.assertNotNull(uint160);
        Assert.assertEquals(value, uint160.getValue());
    }
}

