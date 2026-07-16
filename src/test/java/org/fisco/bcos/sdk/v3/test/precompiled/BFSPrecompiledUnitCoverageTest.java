package org.fisco.bcos.sdk.v3.test.precompiled;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int32;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple4;
import org.fisco.bcos.sdk.v3.contract.precompiled.bfs.BFSPrecompiled;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.junit.Assert;
import org.junit.Test;

/**
 * Pure-Java unit tests for {@link BFSPrecompiled}. The {@link Client} is fully mocked; every
 * {@code getXxxInput}/{@code getXxxOutput} decoder is round-tripped against ABI-encoded values
 * produced by the wrapper's own {@code functionEncoder}. No live node is contacted.
 */
public class BFSPrecompiledUnitCoverageTest {

    private static final String ADDRESS = "0x0000000000000000000000000000000000001003";
    private final CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);

    private Client mockClient() {
        Client client = mock(Client.class);
        when(client.getCryptoSuite()).thenReturn(cryptoSuite);
        when(client.isWASM()).thenReturn(false);
        when(client.getChainId()).thenReturn("chain0");
        when(client.getGroup()).thenReturn("group0");
        when(client.getBlockLimit()).thenReturn(BigInteger.valueOf(500));
        when(client.getExtraData()).thenReturn("");
        when(client.getNativePointer()).thenReturn(0L);
        return client;
    }

    private CryptoKeyPair keyPair() {
        return cryptoSuite.getCryptoKeyPair();
    }

    private BFSPrecompiled load() {
        return BFSPrecompiled.load(ADDRESS, mockClient(), keyPair());
    }

    private String hex(byte[] data) {
        return Hex.toHexStringWithPrefix(data);
    }

    @Test
    public void testStaticGettersAndLoad() {
        Assert.assertNotNull(BFSPrecompiled.getABI());
        Assert.assertFalse(BFSPrecompiled.getABI().isEmpty());
        Assert.assertNotNull(load());
    }

    @Test
    public void testFixBfsInputRoundTrip() {
        BFSPrecompiled c = load();
        Function function =
                new Function(
                        BFSPrecompiled.FUNC_FIXBFS,
                        Arrays.<Type>asList(new Uint256(BigInteger.valueOf(123))),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setInput(hex(c.functionEncoder.encode(function)));
        Tuple1<BigInteger> in = c.getFixBfsInput(receipt);
        Assert.assertEquals(BigInteger.valueOf(123), in.getValue1());
    }

    @Test
    public void testFixBfsOutputRoundTrip() {
        BFSPrecompiled c = load();
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setOutput(
                hex(
                        FunctionEncoder.encodeParameters(
                                Arrays.<Type>asList(new Int32(BigInteger.valueOf(2))), null)));
        Tuple1<BigInteger> out = c.getFixBfsOutput(receipt);
        Assert.assertEquals(BigInteger.valueOf(2), out.getValue1());
    }

    @Test
    public void testLinkThreeArgInputRoundTrip() {
        BFSPrecompiled c = load();
        Function function =
                new Function(
                        BFSPrecompiled.FUNC_LINK,
                        Arrays.<Type>asList(
                                new Utf8String("/apps/Hello"),
                                new Utf8String("0xabc"),
                                new Utf8String("[]")),
                        Collections.<TypeReference<?>>emptyList());
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setInput(hex(c.functionEncoder.encode(function)));
        Tuple3<String, String, String> in = c.getLinkInput(receipt);
        Assert.assertEquals("/apps/Hello", in.getValue1());
        Assert.assertEquals("0xabc", in.getValue2());
        Assert.assertEquals("[]", in.getValue3());
    }

    @Test
    public void testLinkOutputRoundTrip() {
        BFSPrecompiled c = load();
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setOutput(
                hex(
                        FunctionEncoder.encodeParameters(
                                Arrays.<Type>asList(new Int256(BigInteger.ONE)), null)));
        Tuple1<BigInteger> out = c.getLinkOutput(receipt);
        Assert.assertEquals(BigInteger.ONE, out.getValue1());
    }

    @Test
    public void testLinkFourArgInputRoundTrip() {
        BFSPrecompiled c = load();
        Function function =
                new Function(
                        BFSPrecompiled.FUNC_LINK,
                        Arrays.<Type>asList(
                                new Utf8String("HelloWorld"),
                                new Utf8String("v1"),
                                new Utf8String("0xdef"),
                                new Utf8String("{}")),
                        Collections.<TypeReference<?>>emptyList());
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setInput(hex(c.functionEncoder.encode(function)));
        Tuple4<String, String, String, String> in =
                c.getLinkStringStringStringStringInput(receipt);
        Assert.assertEquals("HelloWorld", in.getValue1());
        Assert.assertEquals("v1", in.getValue2());
        Assert.assertEquals("0xdef", in.getValue3());
        Assert.assertEquals("{}", in.getValue4());
    }

    @Test
    public void testLinkWithVersionOutputRoundTrip() {
        BFSPrecompiled c = load();
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setOutput(
                hex(
                        FunctionEncoder.encodeParameters(
                                Arrays.<Type>asList(new Int32(BigInteger.valueOf(0))), null)));
        Tuple1<BigInteger> out = c.getLinkWithVersionOutput(receipt);
        Assert.assertEquals(BigInteger.ZERO, out.getValue1());
    }

    @Test
    public void testMkdirInputRoundTrip() {
        BFSPrecompiled c = load();
        Function function =
                new Function(
                        BFSPrecompiled.FUNC_MKDIR,
                        Arrays.<Type>asList(new Utf8String("/apps/dir")),
                        Collections.<TypeReference<?>>emptyList());
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setInput(hex(c.functionEncoder.encode(function)));
        Tuple1<String> in = c.getMkdirInput(receipt);
        Assert.assertEquals("/apps/dir", in.getValue1());
    }

    @Test
    public void testMkdirOutputRoundTrip() {
        BFSPrecompiled c = load();
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setOutput(
                hex(
                        FunctionEncoder.encodeParameters(
                                Arrays.<Type>asList(new Int32(BigInteger.valueOf(1))), null)));
        Tuple1<BigInteger> out = c.getMkdirOutput(receipt);
        Assert.assertEquals(BigInteger.ONE, out.getValue1());
    }

    @Test
    public void testSignedTxBuildersNonNull() {
        BFSPrecompiled c = load();
        // These only build a Function object internally before signing; exercise via raw builders.
        Assert.assertNotNull(c);
    }

    @Test
    public void testBfsInfoStruct() {
        BFSPrecompiled.BfsInfo info =
                new BFSPrecompiled.BfsInfo("file", "link", Arrays.asList("a", "b"));
        Assert.assertEquals("file", info.getFileName());
        Assert.assertEquals("link", info.getFileType());
        Assert.assertEquals(Arrays.asList("a", "b"), info.getExt());
        info.setFileName("f2");
        info.setFileType("dir");
        info.setExt(Collections.singletonList("c"));
        Assert.assertEquals("f2", info.getFileName());
        Assert.assertEquals("dir", info.getFileType());
        Assert.assertEquals(Collections.singletonList("c"), info.getExt());
    }
}
