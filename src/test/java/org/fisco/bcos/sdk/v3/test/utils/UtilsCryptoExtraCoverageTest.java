/**
 * Copyright 2014-2020 [fisco-dev]
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fisco.bcos.sdk.v3.test.utils;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.fisco.bcos.sdk.v3.client.protocol.model.GroupNodeIniConfig;
import org.fisco.bcos.sdk.v3.client.protocol.model.GroupNodeIniInfo;
import org.fisco.bcos.sdk.v3.client.protocol.response.Abi;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupNodeInfo;
import org.fisco.bcos.sdk.v3.client.protocol.response.Log;
import org.fisco.bcos.sdk.v3.client.protocol.response.LogFilterResponse;
import org.fisco.bcos.sdk.v3.client.protocol.response.LogWrapper;
import org.fisco.bcos.sdk.v3.client.protocol.response.UninstallLogFilter;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.exceptions.HashException;
import org.fisco.bcos.sdk.v3.crypto.exceptions.KeyPairException;
import org.fisco.bcos.sdk.v3.crypto.exceptions.LoadKeyStoreException;
import org.fisco.bcos.sdk.v3.crypto.exceptions.SaveKeyStoreException;
import org.fisco.bcos.sdk.v3.crypto.exceptions.SignatureException;
import org.fisco.bcos.sdk.v3.crypto.exceptions.UnsupportedCryptoTypeException;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.crypto.signature.ECDSASignatureResult;
import org.fisco.bcos.sdk.v3.crypto.signature.SM2SignatureResult;
import org.fisco.bcos.sdk.v3.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.fisco.bcos.sdk.v3.utils.Numeric;
import org.fisco.bcos.sdk.v3.utils.ObjectMapperFactory;
import org.fisco.bcos.sdk.v3.utils.StringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Extra coverage-oriented tests focusing on pure-logic branches in utils (Numeric padded
 * conversions, StringUtils helpers, Hex stream encode/decode), crypto (signature result POJOs,
 * round-trip sign/verify, exceptions) and protocol response/model POJOs that are not exercised by
 * the existing test suite. No network / node is involved.
 */
public class UtilsCryptoExtraCoverageTest {

    // ==================================================================================
    // Numeric : padded / key conversions and remaining edge branches
    // ==================================================================================

    @Test
    public void testNumericToBytesPadded() {
        byte[] result = Numeric.toBytesPadded(BigInteger.valueOf(255), 4);
        Assert.assertEquals(4, result.length);
        Assert.assertEquals(0, result[0]);
        Assert.assertEquals((byte) 0xFF, result[3]);

        // value whose two's-complement byte array has a leading zero byte
        byte[] result2 = Numeric.toBytesPadded(new BigInteger("128"), 2);
        Assert.assertEquals(2, result2.length);
        Assert.assertEquals((byte) 0x80, result2[1]);

        // value with no leading zero byte (high bit not set)
        byte[] result3 = Numeric.toBytesPadded(new BigInteger("256"), 4);
        Assert.assertEquals(4, result3.length);
        Assert.assertEquals(1, result3[2]);
        Assert.assertEquals(0, result3[3]);
    }

    @Test(expected = RuntimeException.class)
    public void testNumericToBytesPaddedTooLarge() {
        Numeric.toBytesPadded(new BigInteger("65536"), 1);
    }

    @Test
    public void testNumericToHexStringWithPrefixZeroPadded() {
        String result = Numeric.toHexStringWithPrefixZeroPadded(BigInteger.valueOf(255), 4);
        Assert.assertEquals("0x00ff", result);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNumericZeroPaddedTooLarge() {
        // value longer than requested size -> UnsupportedOperationException
        Numeric.toHexStringNoPrefixZeroPadded(new BigInteger("65535"), 2);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNumericZeroPaddedNegative() {
        Numeric.toHexStringNoPrefixZeroPadded(new BigInteger("-1"), 8);
    }

    @Test
    public void testNumericToHexStringWithPrefixSafe() {
        // small value -> result length < 2 so a zero is prepended
        String small = Numeric.toHexStringWithPrefixSafe(BigInteger.ONE);
        Assert.assertEquals("0x01", small);

        String larger = Numeric.toHexStringWithPrefixSafe(BigInteger.valueOf(255));
        Assert.assertEquals("0xff", larger);
    }

    @Test
    public void testNumericToHexStringNoPrefixVariants() {
        Assert.assertEquals("ff", Numeric.toHexStringNoPrefix(BigInteger.valueOf(255)));
        byte[] bytes = new byte[] {0x0a, 0x0b};
        Assert.assertEquals("0a0b", Numeric.toHexStringNoPrefix(bytes));
    }

    @Test
    public void testNumericToHexStringWithPrefixFromBigInteger() {
        Assert.assertEquals("0xff", Numeric.toHexStringWithPrefix(BigInteger.valueOf(255)));
    }

    @Test
    public void testNumericToHexStringNullAndZero() {
        Assert.assertEquals("0x0", Numeric.toHexString((BigInteger) null));
        Assert.assertEquals("0x0", Numeric.toHexString(BigInteger.ZERO));
        Assert.assertEquals("0xff", Numeric.toHexString(BigInteger.valueOf(255)));
    }

    @Test
    public void testNumericToHexStringFromBytes() {
        byte[] bytes = new byte[] {0x01, 0x02, 0x03};
        Assert.assertEquals("0x010203", Numeric.toHexString(bytes));
    }

    @Test
    public void testNumericToHexStringFromBytesOffsetWithAndWithoutPrefix() {
        byte[] bytes = new byte[] {0x00, 0x01, 0x02, 0x03};
        Assert.assertEquals("0x0102", Numeric.toHexString(bytes, 1, 2, true));
        Assert.assertEquals("0102", Numeric.toHexString(bytes, 1, 2, false));
    }

    @Test
    public void testNumericHexStringToByteArrayEvenAndOdd() {
        // empty
        Assert.assertEquals(0, Numeric.hexStringToByteArray("0x").length);
        Assert.assertEquals(0, Numeric.hexStringToByteArray("").length);

        // even length with prefix
        byte[] even = Numeric.hexStringToByteArray("0x0102");
        Assert.assertArrayEquals(new byte[] {0x01, 0x02}, even);

        // odd length -> first nibble handled separately
        byte[] odd = Numeric.hexStringToByteArray("123");
        Assert.assertEquals(2, odd.length);
        Assert.assertEquals(0x01, odd[0]);
        Assert.assertEquals(0x23, odd[1]);
    }

    @Test
    public void testNumericAsByte() {
        Assert.assertEquals((byte) 0x12, Numeric.asByte(0x1, 0x2));
        Assert.assertEquals((byte) 0xFF, Numeric.asByte(0xF, 0xF));
        Assert.assertEquals((byte) 0x00, Numeric.asByte(0x0, 0x0));
    }

    @Test
    public void testNumericDecodeQuantityDecimalAndNull() {
        Assert.assertEquals(BigInteger.valueOf(100), Numeric.decodeQuantity("100"));
        Assert.assertEquals(BigInteger.ZERO, Numeric.decodeQuantity(null));
        // short string (< 3 chars) is treated as decimal
        Assert.assertEquals(BigInteger.valueOf(5), Numeric.decodeQuantity("5"));
    }

    @Test(expected = Exception.class)
    public void testNumericDecodeQuantityInvalidDecimal() {
        Numeric.decodeQuantity("not-a-number");
    }

    @Test
    public void testNumericGetKeyNoPrefix() {
        // strips uncompressed flag prefix "04" when length matches and left-pads
        String pubNoFlag = "ab"; // shorter than length -> left padded with zeros
        String result = Numeric.getKeyNoPrefix("04", pubNoFlag, 8);
        Assert.assertEquals(8, result.length());
        Assert.assertTrue(result.endsWith("ab"));

        // with prefix and exact length: prefix should be removed
        String exact = "04" + "11223344";
        String stripped = Numeric.getKeyNoPrefix("04", exact, 8);
        Assert.assertEquals("11223344", stripped);
    }

    @Test
    public void testNumericGetHexKeyWithPrefix() {
        // left pad then ensure prefix present
        String result = Numeric.getHexKeyWithPrefix("ab", "04", 8);
        Assert.assertTrue(result.startsWith("04"));
        // already long enough and already prefixed -> unchanged
        String already = "04" + StringUtils.zeros(6);
        String result2 = Numeric.getHexKeyWithPrefix(already, "04", 8);
        Assert.assertEquals(already, result2);
    }

    @Test
    public void testNumericToBigIntWithOffsetRoundTrip() {
        byte[] bytes = new byte[] {0x00, 0x00, 0x01, 0x00};
        Assert.assertEquals(BigInteger.valueOf(256), Numeric.toBigInt(bytes, 2, 2));
    }

    // ==================================================================================
    // StringUtils : ascii case conversion, byte array helpers, split, joinAll
    // ==================================================================================

    @Test
    public void testStringUtilsToUpperLower() {
        Assert.assertEquals("HELLO", StringUtils.toUpperCase("hello"));
        Assert.assertEquals("HELLO", StringUtils.toUpperCase("HELLO")); // unchanged path
        Assert.assertEquals("hello", StringUtils.toLowerCase("HELLO"));
        Assert.assertEquals("hello", StringUtils.toLowerCase("hello")); // unchanged path
    }

    @Test
    public void testStringUtilsByteArrayRoundTrip() {
        byte[] bytes = StringUtils.toByteArray("ABC");
        Assert.assertEquals(3, bytes.length);
        Assert.assertEquals((byte) 'A', bytes[0]);
        Assert.assertEquals("ABC", StringUtils.fromByteArray(bytes));

        char[] chars = StringUtils.asCharArray(bytes);
        Assert.assertEquals('A', chars[0]);

        byte[] fromChars = StringUtils.toByteArray(new char[] {'X', 'Y'});
        Assert.assertEquals((byte) 'X', fromChars[0]);
    }

    @Test
    public void testStringUtilsToByteArrayWithBuffer() {
        byte[] buf = new byte[5];
        int count = StringUtils.toByteArray("AB", buf, 1);
        Assert.assertEquals(2, count);
        Assert.assertEquals((byte) 'A', buf[1]);
        Assert.assertEquals((byte) 'B', buf[2]);
    }

    @Test
    public void testStringUtilsUtf8RoundTrip() {
        byte[] utf8 = StringUtils.toUTF8ByteArray("hi".toCharArray());
        Assert.assertEquals("hi", StringUtils.fromUTF8ByteArray(utf8));
    }

    @Test
    public void testStringUtilsSplit() {
        String[] parts = StringUtils.split("a,b,c", ',');
        Assert.assertEquals(3, parts.length);
        Assert.assertEquals("a", parts[0]);
        Assert.assertEquals("c", parts[2]);

        // no delimiter -> single element
        String[] single = StringUtils.split("abc", ',');
        Assert.assertEquals(1, single.length);
        Assert.assertEquals("abc", single[0]);
    }

    @Test
    public void testStringUtilsJoinAll() {
        Assert.assertEquals("a-b-c", StringUtils.joinAll("-", new String[] {"a", "b", "c"}));
        Assert.assertEquals("a,b", StringUtils.joinAll(",", Arrays.asList("a", "b")));
        Assert.assertNull(StringUtils.joinAll(",", (String[]) null));
        Assert.assertNull(StringUtils.joinAll(",", (java.util.List<String>) null));
    }

    // ==================================================================================
    // Hex : stream encode/decode paths
    // ==================================================================================

    @Test
    public void testHexEncodeToStream() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int produced = Hex.encode(new byte[] {0x12, 0x34}, out);
        Assert.assertEquals(4, produced);
        Assert.assertEquals("1234", new String(out.toByteArray()));
    }

    @Test
    public void testHexEncodeWithOffsetToStream() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int produced = Hex.encode(new byte[] {0x00, 0x12, 0x34, 0x56}, 1, 2, out);
        Assert.assertEquals(4, produced);
        Assert.assertEquals("1234", new String(out.toByteArray()));
    }

    @Test
    public void testHexDecodeStringToStream() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int produced = Hex.decode("1234", out);
        Assert.assertEquals(2, produced);
        byte[] decoded = out.toByteArray();
        Assert.assertEquals(0x12, decoded[0]);
        Assert.assertEquals(0x34, decoded[1]);
    }

    @Test
    public void testHexEncodeWithOffset() {
        byte[] encoded = Hex.encode(new byte[] {0x00, 0x0a, 0x0b}, 1, 2);
        Assert.assertEquals("0a0b", new String(encoded));
    }

    @Test
    public void testHexDecodeWithUpperPrefix() {
        byte[] decoded = Hex.decode("0X1234");
        Assert.assertEquals(2, decoded.length);
        Assert.assertEquals(0x12, decoded[0]);
    }

    // ==================================================================================
    // ObjectMapperFactory : singletons
    // ==================================================================================

    @Test
    public void testObjectMapperFactory() {
        Assert.assertNotNull(ObjectMapperFactory.getObjectMapper());
        Assert.assertNotNull(ObjectMapperFactory.getObjectReader());
        // same shared instance
        Assert.assertSame(
                ObjectMapperFactory.getObjectMapper(), ObjectMapperFactory.getObjectMapper());
    }

    // ==================================================================================
    // Crypto : SignatureResult POJOs (construct from r/s/v/pub and from hex string)
    // ==================================================================================

    @Test
    public void testEcdsaSignatureResultFromComponents() {
        byte[] r = new byte[32];
        byte[] s = new byte[32];
        Arrays.fill(r, (byte) 0x11);
        Arrays.fill(s, (byte) 0x22);
        byte v = (byte) 1;
        ECDSASignatureResult result = new ECDSASignatureResult(v, r, s);
        Assert.assertEquals(v, result.getV());
        Assert.assertArrayEquals(r, result.getR());
        Assert.assertArrayEquals(s, result.getS());

        // convertToString produces a 65-byte (130 hex chars) signature
        String hexSig = result.convertToString();
        Assert.assertEquals(130, hexSig.length());
        Assert.assertEquals(hexSig, result.toString());

        // encode produces 65 bytes (r + s + v)
        byte[] encoded = result.encode();
        Assert.assertEquals(65, encoded.length);
        Assert.assertEquals(v, encoded[64]);

        // reconstruct from the hex string and compare components
        ECDSASignatureResult roundTrip = new ECDSASignatureResult(hexSig);
        Assert.assertArrayEquals(r, roundTrip.getR());
        Assert.assertArrayEquals(s, roundTrip.getS());
        Assert.assertEquals(v, roundTrip.getV());
        Assert.assertNotNull(roundTrip.getSignatureBytes());

        result.setV((byte) 2);
        Assert.assertEquals((byte) 2, result.getV());
    }

    @Test(expected = SignatureException.class)
    public void testEcdsaSignatureResultInvalidLength() {
        // 64 bytes -> base class ok, but ECDSA requires 65
        new ECDSASignatureResult(StringUtils.zeros(128));
    }

    @Test(expected = SignatureException.class)
    public void testSignatureResultTooShort() {
        // fewer than 64 bytes triggers the abstract base constructor check
        new ECDSASignatureResult(StringUtils.zeros(60));
    }

    @Test
    public void testSm2SignatureResultFromComponents() {
        byte[] r = new byte[32];
        byte[] s = new byte[32];
        byte[] pub = new byte[64];
        Arrays.fill(r, (byte) 0x33);
        Arrays.fill(s, (byte) 0x44);
        Arrays.fill(pub, (byte) 0x55);
        SM2SignatureResult result = new SM2SignatureResult(pub, r, s);
        Assert.assertArrayEquals(r, result.getR());
        Assert.assertArrayEquals(s, result.getS());
        Assert.assertArrayEquals(pub, result.getPub());

        // convertToString -> 64-byte [r,s] (128 hex chars)
        Assert.assertEquals(128, result.convertToString().length());
        Assert.assertEquals(result.convertToString(), result.toString());

        // encode -> r + s + pub
        byte[] encoded = result.encode();
        Assert.assertEquals(128, encoded.length);

        byte[] newPub = new byte[64];
        Arrays.fill(newPub, (byte) 0x66);
        result.setPub(newPub);
        Assert.assertArrayEquals(newPub, result.getPub());

        result.setR(new byte[32]);
        result.setS(new byte[32]);
        Assert.assertEquals(32, result.getR().length);
    }

    @Test
    public void testBaseSignatureResultSetters() {
        ECDSASignatureResult result =
                new ECDSASignatureResult((byte) 0, new byte[32], new byte[32]);
        byte[] sigBytes = new byte[65];
        result.setSignatureBytes(sigBytes);
        Assert.assertArrayEquals(sigBytes, result.getSignatureBytes());
        // base getPub default returns empty for ECDSA result
        SignatureResult asBase = result;
        Assert.assertEquals(0, asBase.getPub().length);
    }

    // ==================================================================================
    // Crypto : CryptoSuite construction + hash + sign + verify round trip
    // ==================================================================================

    @Test
    public void testCryptoSuiteEcdsaRoundTrip() {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        Assert.assertEquals(CryptoType.ECDSA_TYPE, cryptoSuite.getCryptoTypeConfig());
        CryptoKeyPair keyPair = cryptoSuite.getCryptoKeyPair();
        Assert.assertNotNull(keyPair.getAddress());

        String message = cryptoSuite.hash("round-trip-ecdsa");
        SignatureResult signResult = cryptoSuite.sign(message, keyPair);
        Assert.assertTrue(
                cryptoSuite.verify(
                        keyPair.getHexPublicKey(), message, signResult.convertToString()));
        // recover address matches
        Assert.assertEquals(keyPair.getAddress(), cryptoSuite.recoverAddress(message, signResult));
    }

    @Test
    public void testCryptoSuiteSmRoundTrip() {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
        Assert.assertEquals(CryptoType.SM_TYPE, cryptoSuite.getCryptoTypeConfig());
        CryptoKeyPair keyPair = cryptoSuite.getCryptoKeyPair();

        String message = cryptoSuite.hash("round-trip-sm");
        SignatureResult signResult = cryptoSuite.sign(message, keyPair);
        Assert.assertTrue(
                cryptoSuite.verify(
                        keyPair.getHexPublicKey(), message, signResult.convertToString()));
    }

    @Test(expected = UnsupportedCryptoTypeException.class)
    public void testCryptoSuiteUnsupportedType() {
        new CryptoSuite(42);
    }

    // ==================================================================================
    // Crypto : exceptions
    // ==================================================================================

    @Test
    public void testCryptoExceptions() {
        Throwable cause = new RuntimeException("cause");

        HashException he = new HashException("h", cause);
        Assert.assertEquals("h", he.getMessage());
        Assert.assertSame(cause, he.getCause());
        Assert.assertEquals("h2", new HashException("h2").getMessage());

        KeyPairException kpe = new KeyPairException("k", cause);
        Assert.assertSame(cause, kpe.getCause());
        Assert.assertEquals("k2", new KeyPairException("k2").getMessage());

        LoadKeyStoreException lke = new LoadKeyStoreException("l", cause);
        Assert.assertSame(cause, lke.getCause());
        Assert.assertEquals("l2", new LoadKeyStoreException("l2").getMessage());

        SaveKeyStoreException ske = new SaveKeyStoreException("s", cause);
        Assert.assertSame(cause, ske.getCause());
        Assert.assertEquals("s2", new SaveKeyStoreException("s2").getMessage());

        SignatureException se = new SignatureException("sig", cause);
        Assert.assertSame(cause, se.getCause());
        Assert.assertEquals("sig2", new SignatureException("sig2").getMessage());

        UnsupportedCryptoTypeException ucte =
                new UnsupportedCryptoTypeException("u", cause);
        Assert.assertSame(cause, ucte.getCause());
        Assert.assertEquals("u2", new UnsupportedCryptoTypeException("u2").getMessage());
    }

    // ==================================================================================
    // Protocol responses : Abi / UninstallLogFilter / LogFilterResponse
    // ==================================================================================

    @Test
    public void testAbiResponse() {
        Abi abi = new Abi();
        abi.setResult("[{\"type\":\"function\"}]");
        Assert.assertEquals("[{\"type\":\"function\"}]", abi.getABI());
    }

    @Test
    public void testUninstallLogFilter() {
        UninstallLogFilter filter = new UninstallLogFilter();
        filter.setResult(Boolean.TRUE);
        Assert.assertTrue(filter.isUninstalled());
        filter.setResult(Boolean.FALSE);
        Assert.assertFalse(filter.isUninstalled());
    }

    @Test
    public void testLogFilterResponse() {
        LogFilterResponse response = new LogFilterResponse();
        response.setResult("0x1f");
        Assert.assertEquals(BigInteger.valueOf(31), response.getFilterId());
    }

    // ==================================================================================
    // Protocol responses : Log POJO + equals/hashCode/toString
    // ==================================================================================

    @Test
    public void testLogPojoDefaultAndSetters() {
        Log log = new Log();
        log.setRemoved(true);
        log.setLogIndex("0x1");
        log.setTransactionIndex("0x2");
        log.setTransactionHash("0xtxhash");
        log.setBlockHash("0xblockhash");
        log.setBlockNumber("0x10");
        log.setAddress("0xaddr");
        log.setData("0xdata");
        log.setType("mined");
        log.setTopics(Collections.singletonList("0xtopic"));

        Assert.assertTrue(log.isRemoved());
        Assert.assertEquals(BigInteger.ONE, log.getLogIndex());
        Assert.assertEquals("0x1", log.getLogIndexRaw());
        Assert.assertEquals(BigInteger.valueOf(2), log.getTransactionIndex());
        Assert.assertEquals("0x2", log.getTransactionIndexRaw());
        Assert.assertEquals("0xtxhash", log.getTransactionHash());
        Assert.assertEquals("0xblockhash", log.getBlockHash());
        Assert.assertEquals(BigInteger.valueOf(16), log.getBlockNumber());
        Assert.assertEquals("0x10", log.getBlockNumberRaw());
        Assert.assertEquals("0xaddr", log.getAddress());
        Assert.assertEquals("0xdata", log.getData());
        Assert.assertEquals("mined", log.getType());
        Assert.assertEquals(1, log.getTopics().size());
        Assert.assertNotNull(log.toString());
    }

    @Test
    public void testLogConvertNullRawFields() {
        Log log = new Log();
        // raw fields not set -> convert returns null instead of NPE
        Assert.assertNull(log.getLogIndex());
        Assert.assertNull(log.getTransactionIndex());
        Assert.assertNull(log.getBlockNumber());
    }

    @Test
    public void testLogEqualsHashCode() {
        Log a =
                new Log(
                        false,
                        "0x1",
                        "0x2",
                        "0xtx",
                        "0xbh",
                        "0x3",
                        "0xaddr",
                        "0xdata",
                        "t",
                        Collections.singletonList("0xtopic"));
        Log b =
                new Log(
                        false,
                        "0x1",
                        "0x2",
                        "0xtx",
                        "0xbh",
                        "0x3",
                        "0xaddr",
                        "0xdata",
                        "t",
                        Collections.singletonList("0xtopic"));
        Assert.assertEquals(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());
        Assert.assertEquals(a, a);
        Assert.assertNotEquals(a, new Log());
        Assert.assertNotEquals(a, "not-a-log");
    }

    // ==================================================================================
    // Protocol responses : LogWrapper.Hash and LogWrapper.LogObject
    // ==================================================================================

    @Test
    public void testLogWrapperHash() {
        LogWrapper.Hash hash = new LogWrapper.Hash("0xabc");
        Assert.assertEquals("0xabc", hash.get());
        LogWrapper.Hash same = new LogWrapper.Hash();
        same.setValue("0xabc");
        Assert.assertEquals(hash, same);
        Assert.assertEquals(hash.hashCode(), same.hashCode());
        Assert.assertEquals(hash, hash);
        Assert.assertNotEquals(hash, new LogWrapper.Hash("0xdef"));
        Assert.assertNotEquals(hash, "not-a-hash");

        LogWrapper.Hash empty = new LogWrapper.Hash();
        Assert.assertNull(empty.get());
        Assert.assertEquals(0, empty.hashCode());
    }

    @Test
    public void testLogWrapperLogObject() {
        LogWrapper.LogObject logObject =
                new LogWrapper.LogObject(
                        false,
                        "0x1",
                        "0x2",
                        "0xtx",
                        "0xbh",
                        "0x3",
                        "0xaddr",
                        "0xdata",
                        "t",
                        Collections.singletonList("0xtopic"));
        Assert.assertSame(logObject, logObject.get());
        Assert.assertEquals("0xaddr", logObject.get().getAddress());

        LogWrapper.LogObject defaultObj = new LogWrapper.LogObject();
        defaultObj.setAddress("0xother");
        Assert.assertEquals("0xother", defaultObj.getAddress());

        LogWrapper wrapper = new LogWrapper();
        wrapper.setResult(Collections.singletonList(logObject));
        Assert.assertEquals(1, wrapper.getLogs().size());
        Assert.assertSame(logObject, wrapper.getLogs().get(0));
    }

    // ==================================================================================
    // Protocol responses : BcosGroupNodeInfo.Protocol and GroupNodeInfo
    // ==================================================================================

    @Test
    public void testBcosGroupNodeInfoProtocol() {
        BcosGroupNodeInfo.Protocol protocol = new BcosGroupNodeInfo.Protocol();
        protocol.setCompatibilityVersion(5L);
        protocol.setMinSupportedVersion(1L);
        protocol.setMaxSupportedVersion(9L);
        Assert.assertEquals(5L, protocol.getCompatibilityVersion());
        Assert.assertEquals(1L, protocol.getMinSupportedVersion());
        Assert.assertEquals(9L, protocol.getMaxSupportedVersion());
        Assert.assertNotNull(protocol.toString());
    }

    @Test
    public void testBcosGroupNodeInfoGroupNodeInfo() {
        BcosGroupNodeInfo.Protocol protocol = new BcosGroupNodeInfo.Protocol();
        protocol.setCompatibilityVersion(3L);

        GroupNodeIniInfo iniInfo = new GroupNodeIniInfo();
        iniInfo.setChainID("chain0");

        BcosGroupNodeInfo.GroupNodeInfo nodeInfo = new BcosGroupNodeInfo.GroupNodeInfo();
        nodeInfo.setType(1);
        nodeInfo.setName("node0");
        nodeInfo.setProtocol(protocol);
        nodeInfo.setIniConfig(iniInfo);
        nodeInfo.setServiceInfoList(Collections.emptyList());
        nodeInfo.setFeatureKeys(Arrays.asList("f0", "f1"));
        nodeInfo.setSupportConfigs(Collections.singletonList("c0"));

        Assert.assertEquals(1, nodeInfo.getType());
        Assert.assertEquals("node0", nodeInfo.getName());
        Assert.assertSame(protocol, nodeInfo.getProtocol());
        Assert.assertSame(iniInfo, nodeInfo.getIniConfig());
        Assert.assertEquals(0, nodeInfo.getServiceInfoList().size());
        Assert.assertEquals(2, nodeInfo.getFeatureKeys().size());
        Assert.assertEquals(1, nodeInfo.getSupportConfigs().size());
        Assert.assertNotNull(nodeInfo.toString());

        BcosGroupNodeInfo response = new BcosGroupNodeInfo();
        response.setResult(nodeInfo);
        Assert.assertSame(nodeInfo, response.getResult());
    }

    // ==================================================================================
    // Protocol model : GroupNodeIniInfo (+ BinaryInfo) and GroupNodeIniConfig (+ Chain/Executor)
    // ==================================================================================

    @Test
    public void testGroupNodeIniInfo() {
        GroupNodeIniInfo.BinaryInfo binaryInfo = new GroupNodeIniInfo.BinaryInfo();
        binaryInfo.setVersion("3.0.0");
        binaryInfo.setGitCommitHash("abc123");
        binaryInfo.setPlatform("Linux");
        binaryInfo.setBuildTime("20220607");
        Assert.assertEquals("3.0.0", binaryInfo.getVersion());
        Assert.assertEquals("abc123", binaryInfo.getGitCommitHash());
        Assert.assertEquals("Linux", binaryInfo.getPlatform());
        Assert.assertEquals("20220607", binaryInfo.getBuildTime());
        Assert.assertNotNull(binaryInfo.toString());

        GroupNodeIniInfo iniInfo = new GroupNodeIniInfo();
        iniInfo.setBinaryInfo(binaryInfo);
        iniInfo.setChainID("chain0");
        iniInfo.setGroupID("group0");
        iniInfo.setSmCryptoType(Boolean.TRUE);
        iniInfo.setWasm(Boolean.FALSE);
        iniInfo.setAuthCheck(Boolean.TRUE);
        iniInfo.setSerialExecute(Boolean.FALSE);
        iniInfo.setNodeID("0xnode");
        iniInfo.setNodeName("nodeName");
        iniInfo.setRpcServiceName("rpc");
        iniInfo.setGatewayServiceName("gateway");

        Assert.assertSame(binaryInfo, iniInfo.getBinaryInfo());
        Assert.assertEquals("chain0", iniInfo.getChainID());
        Assert.assertEquals("group0", iniInfo.getGroupID());
        Assert.assertEquals(Boolean.TRUE, iniInfo.getSmCryptoType());
        Assert.assertEquals(Boolean.FALSE, iniInfo.getWasm());
        Assert.assertEquals(Boolean.TRUE, iniInfo.getAuthCheck());
        Assert.assertEquals(Boolean.FALSE, iniInfo.getIsSerialExecute());
        Assert.assertEquals("0xnode", iniInfo.getNodeID());
        Assert.assertEquals("nodeName", iniInfo.getNodeName());
        Assert.assertEquals("rpc", iniInfo.getRpcServiceName());
        Assert.assertEquals("gateway", iniInfo.getGatewayServiceName());
        Assert.assertNotNull(iniInfo.toString());
    }

    @Test
    public void testGroupNodeIniConfigDirect() {
        GroupNodeIniConfig.Chain chain = new GroupNodeIniConfig.Chain();
        chain.setSmCrypto(true);
        chain.setGroupID("group0");
        chain.setChainID("chain0");
        Assert.assertTrue(chain.isSmCrypto());
        Assert.assertEquals("group0", chain.getGroupID());
        Assert.assertEquals("chain0", chain.getChainID());
        Assert.assertNotNull(chain.toString());

        GroupNodeIniConfig.Executor executor = new GroupNodeIniConfig.Executor();
        executor.setWasm(true);
        executor.setAuthCheck(false);
        executor.setSerialExecute(true);
        Assert.assertTrue(executor.isWasm());
        Assert.assertFalse(executor.isAuthCheck());
        Assert.assertTrue(executor.isSerialExecute());
        Assert.assertNotNull(executor.toString());

        GroupNodeIniConfig config = new GroupNodeIniConfig();
        config.setChain(chain);
        config.setExecutor(executor);
        Assert.assertSame(chain, config.getChain());
        Assert.assertSame(executor, config.getExecutor());
        Assert.assertNotNull(config.toString());
    }

    @Test
    public void testGroupNodeIniConfigNewIniConfig() {
        GroupNodeIniInfo iniInfo = new GroupNodeIniInfo();
        iniInfo.setChainID("chain0");
        iniInfo.setGroupID("group0");
        iniInfo.setSmCryptoType(Boolean.FALSE);
        iniInfo.setWasm(Boolean.FALSE);
        iniInfo.setAuthCheck(Boolean.TRUE);
        iniInfo.setSerialExecute(Boolean.FALSE);

        GroupNodeIniConfig config = GroupNodeIniConfig.newIniConfig(iniInfo);
        Assert.assertNotNull(config.getChain());
        Assert.assertEquals("chain0", config.getChain().getChainID());
        Assert.assertEquals("group0", config.getChain().getGroupID());
        Assert.assertFalse(config.getChain().isSmCrypto());
        Assert.assertNotNull(config.getExecutor());
        Assert.assertFalse(config.getExecutor().isWasm());
        Assert.assertTrue(config.getExecutor().isAuthCheck());
        Assert.assertFalse(config.getExecutor().isSerialExecute());
    }
}
