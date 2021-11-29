package org.fisco.bcos.sdk.codec.scale;

import org.fisco.bcos.sdk.utils.Hex;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public class ScaleTest
{
    public void testFixedWidthInteger(BigInteger value, boolean signed, int valueByteSize, String encodeData) throws IOException {
        // test encode
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ScaleCodecWriter writer = new ScaleCodecWriter(outputStream);
        if(valueByteSize >= 0 && valueByteSize <= 16) {
            if (signed) {
                writer.writeInteger(value, valueByteSize);
            } else {
                writer.writeUnsignedInteger(value, valueByteSize);
            }
        }else
            {
                writer.writeBigInt256(signed, value);
            }
        String encodeHexData = Hex.toHexString(outputStream.toByteArray());
        System.out.println("* encodeHexData: " + encodeHexData + ", expected data:" + encodeData);
        Assert.assertEquals(encodeHexData, encodeData);

        // test decode
        ScaleCodecReader reader = new ScaleCodecReader(outputStream.toByteArray());
        BigInteger decodedValue;
        if(valueByteSize >= 0 && valueByteSize <= 16) {
             decodedValue = reader.decodeInteger(signed, valueByteSize);
        }else
            {
                decodedValue = reader.decodeInt256();
            }
        System.out.println("* decodedValue: " + decodedValue + ", expected value:" + value);
        Assert.assertEquals(decodedValue, value);
    }
    @Test
    public void testInteger() throws IOException {
        // test int8_t
        boolean signed = true;
        int valueByteSize = 1;
        testFixedWidthInteger(BigInteger.valueOf(0), signed, valueByteSize, "00");
        testFixedWidthInteger(BigInteger.valueOf(-1), signed, valueByteSize, "ff");
        testFixedWidthInteger(BigInteger.valueOf(-128), signed, valueByteSize, "80");
        testFixedWidthInteger(BigInteger.valueOf(-127), signed, valueByteSize, "81");
        testFixedWidthInteger(BigInteger.valueOf(123), signed, valueByteSize, "7b");
        testFixedWidthInteger(BigInteger.valueOf(-15), signed, valueByteSize, "f1");

        // test uint8_t
        signed = false;
        testFixedWidthInteger(BigInteger.valueOf(0), signed, valueByteSize, "00");
        testFixedWidthInteger(BigInteger.valueOf(234), signed, valueByteSize, "ea");
        testFixedWidthInteger(BigInteger.valueOf(255), signed, valueByteSize, "ff");
        testFixedWidthInteger(BigInteger.valueOf(129), signed, valueByteSize, "81");
        testFixedWidthInteger(BigInteger.valueOf(132), signed, valueByteSize, "84");
        testFixedWidthInteger(BigInteger.valueOf(128), signed, valueByteSize, "80");
        testFixedWidthInteger(BigInteger.valueOf(244), signed, valueByteSize, "f4");

        // test int16_t
        signed = true;
        valueByteSize = 2;
        testFixedWidthInteger(BigInteger.valueOf(0), signed, valueByteSize, "0000");
        testFixedWidthInteger(BigInteger.valueOf(128), signed, valueByteSize, "8000");
        testFixedWidthInteger(BigInteger.valueOf(-128), signed, valueByteSize, "80ff");
        testFixedWidthInteger(BigInteger.valueOf(-32767), signed, valueByteSize, "0180");
        testFixedWidthInteger(BigInteger.valueOf(-3), signed, valueByteSize, "fdff");
        testFixedWidthInteger(BigInteger.valueOf(3), signed, valueByteSize, "0300");
        testFixedWidthInteger(BigInteger.valueOf(-1), signed, valueByteSize, "ffff");
        testFixedWidthInteger(BigInteger.valueOf(32767), signed, valueByteSize, "ff7f");
        testFixedWidthInteger(BigInteger.valueOf(12345), signed, valueByteSize, "3930");
        testFixedWidthInteger(BigInteger.valueOf(-12345), signed, valueByteSize, "c7cf");
        testFixedWidthInteger(BigInteger.valueOf(255), signed, valueByteSize, "ff00");
        testFixedWidthInteger(BigInteger.valueOf(252), signed, valueByteSize, "fc00");
        testFixedWidthInteger(BigInteger.valueOf(244), signed, valueByteSize, "f400");

        // test uint16_t
        signed = false;
        testFixedWidthInteger(BigInteger.valueOf(32767), signed, valueByteSize, "ff7f");
        testFixedWidthInteger(BigInteger.valueOf(65535), signed, valueByteSize, "ffff");
        testFixedWidthInteger(BigInteger.valueOf(0), signed, valueByteSize, "0000");
        testFixedWidthInteger(BigInteger.valueOf(1), signed, valueByteSize, "0100");
        testFixedWidthInteger(BigInteger.valueOf(128), signed, valueByteSize, "8000");
        testFixedWidthInteger(BigInteger.valueOf(255), signed, valueByteSize, "ff00");
        testFixedWidthInteger(BigInteger.valueOf(256), signed, valueByteSize, "0001");
        testFixedWidthInteger(BigInteger.valueOf(12345), signed, valueByteSize, "3930");

        // test int32_t
        signed = true;
        valueByteSize = 4;
        testFixedWidthInteger(BigInteger.valueOf(2147483647), signed, valueByteSize, "ffffff7f");
        testFixedWidthInteger(BigInteger.valueOf(0), signed, valueByteSize, "00000000");
        testFixedWidthInteger(BigInteger.valueOf(-3), signed, valueByteSize, "fdffffff");
        testFixedWidthInteger(BigInteger.valueOf(3), signed, valueByteSize, "03000000");
        testFixedWidthInteger(BigInteger.valueOf(252), signed, valueByteSize, "fc000000");
        testFixedWidthInteger(BigInteger.valueOf(-252), signed, valueByteSize, "04ffffff");
        testFixedWidthInteger(BigInteger.valueOf(255), signed, valueByteSize, "ff000000");
        testFixedWidthInteger(BigInteger.valueOf(-255), signed, valueByteSize, "01ffffff");
        testFixedWidthInteger(BigInteger.valueOf(256), signed, valueByteSize, "00010000");
        testFixedWidthInteger(BigInteger.valueOf(-256), signed, valueByteSize, "00ffffff");
        testFixedWidthInteger(BigInteger.valueOf(257), signed, valueByteSize, "01010000");
        testFixedWidthInteger(BigInteger.valueOf(-257), signed, valueByteSize, "fffeffff");
        testFixedWidthInteger(BigInteger.valueOf(65535), signed, valueByteSize, "ffff0000");
        testFixedWidthInteger(BigInteger.valueOf(-65535), signed, valueByteSize, "0100ffff");
        testFixedWidthInteger(BigInteger.valueOf(-1), signed, valueByteSize, "ffffffff");
        testFixedWidthInteger(BigInteger.valueOf(1), signed, valueByteSize, "01000000");

        // tet uint32_t
        signed = false;
        testFixedWidthInteger(BigInteger.valueOf(16909060), signed, valueByteSize, "04030201");
        testFixedWidthInteger(BigInteger.valueOf(0), signed, valueByteSize, "00000000");
        testFixedWidthInteger(BigInteger.valueOf(1), signed, valueByteSize, "01000000");
        testFixedWidthInteger(BigInteger.valueOf(2), signed, valueByteSize, "02000000");
        testFixedWidthInteger(BigInteger.valueOf(127), signed, valueByteSize, "7f000000");
        testFixedWidthInteger(BigInteger.valueOf(128), signed, valueByteSize, "80000000");
        testFixedWidthInteger(BigInteger.valueOf(129), signed, valueByteSize, "81000000");
        testFixedWidthInteger(BigInteger.valueOf(255), signed, valueByteSize, "ff000000");
        testFixedWidthInteger(BigInteger.valueOf(256), signed, valueByteSize, "00010000");
        testFixedWidthInteger(BigInteger.valueOf(257), signed, valueByteSize, "01010000");
        testFixedWidthInteger(BigInteger.valueOf(65535), signed, valueByteSize, "ffff0000");
        testFixedWidthInteger(BigInteger.valueOf(65536), signed, valueByteSize, "00000100");
        testFixedWidthInteger(BigInteger.valueOf(65537), signed, valueByteSize, "01000100");
        testFixedWidthInteger(BigInteger.valueOf(2147483647), signed, valueByteSize, "ffffff7f");
        testFixedWidthInteger(BigInteger.valueOf(67305985), signed, valueByteSize, "01020304");

        // test int64_t
        signed = true;
        valueByteSize = 8;
        testFixedWidthInteger(new BigInteger("578437695752307201"), signed, valueByteSize, "0102030405060708");
        testFixedWidthInteger(BigInteger.valueOf(0), signed, valueByteSize, "0000000000000000");
        testFixedWidthInteger(BigInteger.valueOf(-1), signed, valueByteSize, "ffffffffffffffff");
        testFixedWidthInteger(BigInteger.valueOf(1), signed, valueByteSize, "0100000000000000");
        testFixedWidthInteger(BigInteger.valueOf(-127), signed, valueByteSize, "81ffffffffffffff");
        testFixedWidthInteger(BigInteger.valueOf(127), signed, valueByteSize, "7f00000000000000");
        testFixedWidthInteger(BigInteger.valueOf(-128), signed, valueByteSize, "80ffffffffffffff");
        testFixedWidthInteger(BigInteger.valueOf(128), signed, valueByteSize, "8000000000000000");
        testFixedWidthInteger(BigInteger.valueOf(-129), signed, valueByteSize, "7fffffffffffffff");
        testFixedWidthInteger(BigInteger.valueOf(129), signed, valueByteSize, "8100000000000000");
        testFixedWidthInteger(BigInteger.valueOf(-255), signed, valueByteSize, "01ffffffffffffff");
        testFixedWidthInteger(BigInteger.valueOf(255), signed, valueByteSize, "ff00000000000000");
        testFixedWidthInteger(BigInteger.valueOf(256), signed, valueByteSize, "0001000000000000");
        testFixedWidthInteger(BigInteger.valueOf(-256), signed, valueByteSize, "00ffffffffffffff");
        testFixedWidthInteger(BigInteger.valueOf(-257), signed, valueByteSize, "fffeffffffffffff");
        testFixedWidthInteger(BigInteger.valueOf(257), signed, valueByteSize, "0101000000000000");
        testFixedWidthInteger(BigInteger.valueOf(65535), signed, valueByteSize, "ffff000000000000");
        testFixedWidthInteger(BigInteger.valueOf(-65535), signed, valueByteSize, "0100ffffffffffff");
        testFixedWidthInteger(BigInteger.valueOf(65536), signed, valueByteSize, "0000010000000000");
        testFixedWidthInteger(BigInteger.valueOf(-65536), signed, valueByteSize, "0000ffffffffffff");
        testFixedWidthInteger(BigInteger.valueOf(65537), signed, valueByteSize, "0100010000000000");
        testFixedWidthInteger(BigInteger.valueOf(-65537), signed, valueByteSize, "fffffeffffffffff");
        testFixedWidthInteger(BigInteger.valueOf(-2147483647), signed, valueByteSize, "01000080ffffffff");
        testFixedWidthInteger(BigInteger.valueOf(2147483647), signed, valueByteSize, "ffffff7f00000000");
        testFixedWidthInteger(BigInteger.valueOf(-2147483648), signed, valueByteSize, "00000080ffffffff");
        testFixedWidthInteger(new BigInteger("2147483648"), signed, valueByteSize, "0000008000000000");
        testFixedWidthInteger(new BigInteger("-2147483649"), signed, valueByteSize, "ffffff7fffffffff");
        testFixedWidthInteger(new BigInteger("2147483649"), signed, valueByteSize, "0100008000000000");
        testFixedWidthInteger(BigInteger.valueOf(67305985), signed, valueByteSize, "0102030400000000");
        testFixedWidthInteger(BigInteger.valueOf(-67305985), signed, valueByteSize, "fffdfcfbffffffff");

        // test uint64
        signed = false;
        testFixedWidthInteger(BigInteger.valueOf(0), signed, valueByteSize, "0000000000000000");
        testFixedWidthInteger(BigInteger.valueOf(1), signed, valueByteSize, "0100000000000000");
        testFixedWidthInteger(BigInteger.valueOf(127), signed, valueByteSize, "7f00000000000000");
        testFixedWidthInteger(BigInteger.valueOf(128), signed, valueByteSize, "8000000000000000");
        testFixedWidthInteger(BigInteger.valueOf(129), signed, valueByteSize, "8100000000000000");
        testFixedWidthInteger(BigInteger.valueOf(255), signed, valueByteSize, "ff00000000000000");
        testFixedWidthInteger(BigInteger.valueOf(256), signed, valueByteSize, "0001000000000000");
        testFixedWidthInteger(BigInteger.valueOf(257), signed, valueByteSize, "0101000000000000");
        testFixedWidthInteger(BigInteger.valueOf(65535), signed, valueByteSize, "ffff000000000000");
        testFixedWidthInteger(BigInteger.valueOf(65536), signed, valueByteSize, "0000010000000000");
        testFixedWidthInteger(BigInteger.valueOf(65537), signed, valueByteSize, "0100010000000000");
        testFixedWidthInteger(BigInteger.valueOf(2147483647), signed, valueByteSize, "ffffff7f00000000");
        testFixedWidthInteger(new BigInteger("2147483648"), signed, valueByteSize, "0000008000000000");
        testFixedWidthInteger(new BigInteger("2147483649"), signed, valueByteSize, "0100008000000000");
        testFixedWidthInteger(new BigInteger("578437695752307201"), signed, valueByteSize, "0102030405060708");

        // test s128
        signed = true;
        valueByteSize = 16;
        testFixedWidthInteger(BigInteger.valueOf(0), signed, valueByteSize, "00000000000000000000000000000000");
        testFixedWidthInteger(BigInteger.valueOf(1), signed, valueByteSize, "01000000000000000000000000000000");
        testFixedWidthInteger(BigInteger.valueOf(-1), signed, valueByteSize, "ffffffffffffffffffffffffffffffff");
        testFixedWidthInteger(BigInteger.valueOf(127), signed, valueByteSize, "7f000000000000000000000000000000");
        testFixedWidthInteger(BigInteger.valueOf(-127), signed, valueByteSize, "81ffffffffffffffffffffffffffffff");
        testFixedWidthInteger(BigInteger.valueOf(128), signed, valueByteSize, "80000000000000000000000000000000");
        testFixedWidthInteger(BigInteger.valueOf(-128), signed, valueByteSize, "80ffffffffffffffffffffffffffffff");
        testFixedWidthInteger(BigInteger.valueOf(129), signed, valueByteSize, "81000000000000000000000000000000");
        testFixedWidthInteger(BigInteger.valueOf(-129), signed, valueByteSize, "7fffffffffffffffffffffffffffffff");
        testFixedWidthInteger(BigInteger.valueOf(255), signed, valueByteSize, "ff000000000000000000000000000000");
        testFixedWidthInteger(BigInteger.valueOf(-255), signed, valueByteSize, "01ffffffffffffffffffffffffffffff");
        testFixedWidthInteger(BigInteger.valueOf(256), signed, valueByteSize, "00010000000000000000000000000000");
        testFixedWidthInteger(BigInteger.valueOf(-256), signed, valueByteSize, "00ffffffffffffffffffffffffffffff");
        testFixedWidthInteger(BigInteger.valueOf(257), signed, valueByteSize, "01010000000000000000000000000000");
        testFixedWidthInteger(BigInteger.valueOf(-257), signed, valueByteSize, "fffeffffffffffffffffffffffffffff");
        testFixedWidthInteger(BigInteger.valueOf(65535), signed, valueByteSize, "ffff0000000000000000000000000000");
        testFixedWidthInteger(BigInteger.valueOf(-65535), signed, valueByteSize, "0100ffffffffffffffffffffffffffff");
        testFixedWidthInteger(BigInteger.valueOf(65536), signed, valueByteSize, "00000100000000000000000000000000");
        testFixedWidthInteger(BigInteger.valueOf(-65536), signed, valueByteSize, "0000ffffffffffffffffffffffffffff");
        testFixedWidthInteger(BigInteger.valueOf(65537), signed, valueByteSize, "01000100000000000000000000000000");
        testFixedWidthInteger(BigInteger.valueOf(-65537), signed, valueByteSize, "fffffeffffffffffffffffffffffffff");
        testFixedWidthInteger(BigInteger.valueOf(2147483647), signed, valueByteSize, "ffffff7f000000000000000000000000");
        testFixedWidthInteger(BigInteger.valueOf(-2147483647), signed, valueByteSize, "01000080ffffffffffffffffffffffff");
        testFixedWidthInteger(new BigInteger("2147483648"), signed, valueByteSize, "00000080000000000000000000000000");
        testFixedWidthInteger(new BigInteger("-2147483648"), signed, valueByteSize, "00000080ffffffffffffffffffffffff");
        testFixedWidthInteger(new BigInteger("2147483649"), signed, valueByteSize, "01000080000000000000000000000000");
        testFixedWidthInteger(new BigInteger("-2147483649"), signed, valueByteSize, "ffffff7fffffffffffffffffffffffff");
        testFixedWidthInteger(new BigInteger("578437695752307201"), signed, valueByteSize, "01020304050607080000000000000000");
        testFixedWidthInteger(new BigInteger("-578437695752307201"), signed, valueByteSize, "fffdfcfbfaf9f8f7ffffffffffffffff");
        testFixedWidthInteger(new BigInteger("12312434324578437695752307201"), signed, valueByteSize, "0102d3614f9f262a629bc82700000000");
        testFixedWidthInteger(new BigInteger("-12312434324578437695752307201"), signed, valueByteSize, "fffd2c9eb060d9d59d6437d8ffffffff");

        // test u128
        signed = false;
        testFixedWidthInteger(BigInteger.valueOf(0), signed, valueByteSize, "00000000000000000000000000000000");
        testFixedWidthInteger(BigInteger.valueOf(1), signed, valueByteSize, "01000000000000000000000000000000");
        testFixedWidthInteger(BigInteger.valueOf(127), signed, valueByteSize, "7f000000000000000000000000000000");
        testFixedWidthInteger(BigInteger.valueOf(128), signed, valueByteSize, "80000000000000000000000000000000");
        testFixedWidthInteger(BigInteger.valueOf(129), signed, valueByteSize, "81000000000000000000000000000000");
        testFixedWidthInteger(BigInteger.valueOf(255), signed, valueByteSize, "ff000000000000000000000000000000");
        testFixedWidthInteger(BigInteger.valueOf(256), signed, valueByteSize, "00010000000000000000000000000000");
        testFixedWidthInteger(BigInteger.valueOf(257), signed, valueByteSize, "01010000000000000000000000000000");
        testFixedWidthInteger(BigInteger.valueOf(65535), signed, valueByteSize, "ffff0000000000000000000000000000");
        testFixedWidthInteger(BigInteger.valueOf(65536), signed, valueByteSize, "00000100000000000000000000000000");
        testFixedWidthInteger(BigInteger.valueOf(65537), signed, valueByteSize, "01000100000000000000000000000000");
        testFixedWidthInteger(BigInteger.valueOf(2147483647), signed, valueByteSize, "ffffff7f000000000000000000000000");
        testFixedWidthInteger(new BigInteger("2147483648"), signed, valueByteSize, "00000080000000000000000000000000");
        testFixedWidthInteger(new BigInteger("2147483649"), signed, valueByteSize, "01000080000000000000000000000000");
        testFixedWidthInteger(new BigInteger("578437695752307201"), signed, valueByteSize, "01020304050607080000000000000000");
        testFixedWidthInteger(new BigInteger("12312434324578437695752307201"), signed, valueByteSize, "0102d3614f9f262a629bc82700000000");

        // test u256
        signed = false;
        valueByteSize = 32;
        testFixedWidthInteger(BigInteger.valueOf(0), signed, valueByteSize, "0000000000000000000000000000000000000000000000000000000000000000");
        testFixedWidthInteger(BigInteger.valueOf(1), signed, valueByteSize, "0000000000000000000000000000000000000000000000000000000000000001");
        testFixedWidthInteger(BigInteger.valueOf(127), signed, valueByteSize, "000000000000000000000000000000000000000000000000000000000000007f");
        testFixedWidthInteger(BigInteger.valueOf(128), signed, valueByteSize, "0000000000000000000000000000000000000000000000000000000000000080");
        testFixedWidthInteger(BigInteger.valueOf(129), signed, valueByteSize, "0000000000000000000000000000000000000000000000000000000000000081");
        testFixedWidthInteger(BigInteger.valueOf(255), signed, valueByteSize, "00000000000000000000000000000000000000000000000000000000000000ff");
        testFixedWidthInteger(BigInteger.valueOf(256), signed, valueByteSize, "0000000000000000000000000000000000000000000000000000000000000100");
        testFixedWidthInteger(BigInteger.valueOf(257), signed, valueByteSize, "0000000000000000000000000000000000000000000000000000000000000101");
        testFixedWidthInteger(BigInteger.valueOf(65535), signed, valueByteSize, "000000000000000000000000000000000000000000000000000000000000ffff");
        testFixedWidthInteger(BigInteger.valueOf(65536), signed, valueByteSize, "0000000000000000000000000000000000000000000000000000000000010000");
        testFixedWidthInteger(BigInteger.valueOf(65537), signed, valueByteSize, "0000000000000000000000000000000000000000000000000000000000010001");
        testFixedWidthInteger(BigInteger.valueOf(2147483647), signed, valueByteSize, "000000000000000000000000000000000000000000000000000000007fffffff");
        testFixedWidthInteger(new BigInteger("2147483648"), signed, valueByteSize, "0000000000000000000000000000000000000000000000000000000080000000");
        testFixedWidthInteger(new BigInteger("2147483649"), signed, valueByteSize, "0000000000000000000000000000000000000000000000000000000080000001");
        testFixedWidthInteger(new BigInteger("123123122147483649"), signed, valueByteSize, "00000000000000000000000000000000000000000000000001b56bd3c73ce001");

        // s256 test
        signed = true;
        valueByteSize = 32;
        testFixedWidthInteger(BigInteger.valueOf(-1), signed, valueByteSize, "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
        testFixedWidthInteger(BigInteger.valueOf(-127), signed, valueByteSize, "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff81");
        testFixedWidthInteger(BigInteger.valueOf(-128), signed, valueByteSize, "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff80");
        testFixedWidthInteger(BigInteger.valueOf(-129), signed, valueByteSize, "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff7f");
        testFixedWidthInteger(BigInteger.valueOf(-255), signed, valueByteSize, "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff01");
        testFixedWidthInteger(BigInteger.valueOf(-256), signed, valueByteSize, "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff00");
        testFixedWidthInteger(BigInteger.valueOf(-257), signed, valueByteSize, "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffeff");
        testFixedWidthInteger(BigInteger.valueOf(-65535), signed, valueByteSize, "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff0001");
        testFixedWidthInteger(BigInteger.valueOf(-65536), signed, valueByteSize, "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff0000");
        testFixedWidthInteger(BigInteger.valueOf(-65537), signed, valueByteSize, "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffeffff");
        testFixedWidthInteger(BigInteger.valueOf(-2147483647), signed, valueByteSize, "ffffffffffffffffffffffffffffffffffffffffffffffffffffffff80000001");
        testFixedWidthInteger(BigInteger.valueOf(-2147483648), signed, valueByteSize, "ffffffffffffffffffffffffffffffffffffffffffffffffffffffff80000000");
        testFixedWidthInteger(new BigInteger("-2147483649"), signed, valueByteSize, "ffffffffffffffffffffffffffffffffffffffffffffffffffffffff7fffffff");
        testFixedWidthInteger(new BigInteger("-123123122147483649"), signed, valueByteSize, "fffffffffffffffffffffffffffffffffffffffffffffffffe4a942c38c31fff");

        testFixedWidthInteger(BigInteger.valueOf(1), signed, valueByteSize, "0000000000000000000000000000000000000000000000000000000000000001");
        testFixedWidthInteger(BigInteger.valueOf(127), signed, valueByteSize, "000000000000000000000000000000000000000000000000000000000000007f");
        testFixedWidthInteger(BigInteger.valueOf(128), signed, valueByteSize, "0000000000000000000000000000000000000000000000000000000000000080");
        testFixedWidthInteger(BigInteger.valueOf(129), signed, valueByteSize, "0000000000000000000000000000000000000000000000000000000000000081");
        testFixedWidthInteger(BigInteger.valueOf(255), signed, valueByteSize, "00000000000000000000000000000000000000000000000000000000000000ff");
        testFixedWidthInteger(BigInteger.valueOf(256), signed, valueByteSize, "0000000000000000000000000000000000000000000000000000000000000100");
        testFixedWidthInteger(BigInteger.valueOf(257), signed, valueByteSize, "0000000000000000000000000000000000000000000000000000000000000101");
        testFixedWidthInteger(BigInteger.valueOf(65535), signed, valueByteSize, "000000000000000000000000000000000000000000000000000000000000ffff");
        testFixedWidthInteger(BigInteger.valueOf(65536), signed, valueByteSize, "0000000000000000000000000000000000000000000000000000000000010000");
        testFixedWidthInteger(BigInteger.valueOf(65537), signed, valueByteSize, "0000000000000000000000000000000000000000000000000000000000010001");
        testFixedWidthInteger(BigInteger.valueOf(2147483647), signed, valueByteSize, "000000000000000000000000000000000000000000000000000000007fffffff");
        testFixedWidthInteger(new BigInteger("2147483648"), signed, valueByteSize, "0000000000000000000000000000000000000000000000000000000080000000");
        testFixedWidthInteger(new BigInteger("2147483649"), signed, valueByteSize, "0000000000000000000000000000000000000000000000000000000080000001");
        testFixedWidthInteger(new BigInteger("123123122147483649"), signed, valueByteSize, "00000000000000000000000000000000000000000000000001b56bd3c73ce001");
    }

}