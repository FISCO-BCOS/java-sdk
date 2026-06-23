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
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.contract.precompiled.consensus.ConsensusPrecompiled;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.junit.Assert;
import org.junit.Test;

/**
 * Pure-Java unit tests for {@link ConsensusPrecompiled}. No live node is contacted: the {@link
 * Client} is fully mocked and every {@code getXxxInput}/{@code getXxxOutput} decoder is exercised by
 * ABI-encoding known values (via the wrapper's own {@code functionEncoder}) and asserting the values
 * round-trip back through the decoder.
 */
public class ConsensusPrecompiledUnitCoverageTest {

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

    private ConsensusPrecompiled load() {
        return ConsensusPrecompiled.load(ADDRESS, mockClient(), keyPair());
    }

    /** Encodes a single-string-arg input (selector + utf8 string) and returns it as a 0x hex. */
    private String encodeStringInput(ConsensusPrecompiled c, String funcName, String value) {
        Function function =
                new Function(
                        funcName,
                        Arrays.<Type>asList(new Utf8String(value)),
                        Collections.<TypeReference<?>>emptyList(),
                        4);
        return Hex.toHexStringWithPrefix(c.functionEncoder.encode(function));
    }

    /** Encodes a (string, uint256) input and returns it as a 0x hex. */
    private String encodeStringUintInput(
            ConsensusPrecompiled c, String funcName, String value, BigInteger weight) {
        Function function =
                new Function(
                        funcName,
                        Arrays.<Type>asList(new Utf8String(value), new Uint256(weight)),
                        Collections.<TypeReference<?>>emptyList(),
                        4);
        return Hex.toHexStringWithPrefix(c.functionEncoder.encode(function));
    }

    /** Encodes a single Int256 return value (no selector) as a 0x hex. */
    private String encodeInt256Output(BigInteger value) {
        byte[] data =
                FunctionEncoder.encodeParameters(
                        Arrays.<Type>asList(new Int256(value)), null);
        return Hex.toHexStringWithPrefix(data);
    }

    @Test
    public void testStaticGetters() {
        Assert.assertNotNull(ConsensusPrecompiled.getABI());
        Assert.assertFalse(ConsensusPrecompiled.getABI().isEmpty());
        Assert.assertNotNull(ConsensusPrecompiled.getBinary(cryptoSuite));
        Assert.assertNotNull(
                ConsensusPrecompiled.getBinary(new CryptoSuite(CryptoType.SM_TYPE)));
    }

    @Test
    public void testLoadNonNull() {
        Assert.assertNotNull(load());
    }

    @Test
    public void testAddObserverInputRoundTrip() {
        ConsensusPrecompiled c = load();
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setInput(encodeStringInput(c, ConsensusPrecompiled.FUNC_ADDOBSERVER, "node-obs"));
        Tuple1<String> in = c.getAddObserverInput(receipt);
        Assert.assertEquals("node-obs", in.getValue1());
    }

    @Test
    public void testAddObserverOutputRoundTrip() {
        ConsensusPrecompiled c = load();
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setOutput(encodeInt256Output(BigInteger.ONE));
        Tuple1<BigInteger> out = c.getAddObserverOutput(receipt);
        Assert.assertEquals(BigInteger.ONE, out.getValue1());
    }

    @Test
    public void testAddSealerInputRoundTrip() {
        ConsensusPrecompiled c = load();
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setInput(
                encodeStringUintInput(
                        c, ConsensusPrecompiled.FUNC_ADDSEALER, "sealer-1", BigInteger.valueOf(7)));
        Tuple2<String, BigInteger> in = c.getAddSealerInput(receipt);
        Assert.assertEquals("sealer-1", in.getValue1());
        Assert.assertEquals(BigInteger.valueOf(7), in.getValue2());
    }

    @Test
    public void testAddSealerOutputRoundTrip() {
        ConsensusPrecompiled c = load();
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setOutput(encodeInt256Output(BigInteger.valueOf(-1)));
        Tuple1<BigInteger> out = c.getAddSealerOutput(receipt);
        Assert.assertEquals(BigInteger.valueOf(-1), out.getValue1());
    }

    @Test
    public void testRemoveInputRoundTrip() {
        ConsensusPrecompiled c = load();
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setInput(encodeStringInput(c, ConsensusPrecompiled.FUNC_REMOVE, "node-rm"));
        Tuple1<String> in = c.getRemoveInput(receipt);
        Assert.assertEquals("node-rm", in.getValue1());
    }

    @Test
    public void testRemoveOutputRoundTrip() {
        ConsensusPrecompiled c = load();
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setOutput(encodeInt256Output(BigInteger.ZERO));
        Tuple1<BigInteger> out = c.getRemoveOutput(receipt);
        Assert.assertEquals(BigInteger.ZERO, out.getValue1());
    }

    @Test
    public void testSetTermWeightInputRoundTrip() {
        ConsensusPrecompiled c = load();
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setInput(
                encodeStringUintInput(
                        c,
                        ConsensusPrecompiled.FUNC_SETTERMWEIGHT,
                        "term-node",
                        BigInteger.valueOf(42)));
        Tuple2<String, BigInteger> in = c.getSetTermWeightInput(receipt);
        Assert.assertEquals("term-node", in.getValue1());
        Assert.assertEquals(BigInteger.valueOf(42), in.getValue2());
    }

    @Test
    public void testSetTermWeightOutputRoundTrip() {
        ConsensusPrecompiled c = load();
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setOutput(encodeInt256Output(BigInteger.valueOf(3)));
        Tuple1<BigInteger> out = c.getSetTermWeightOutput(receipt);
        Assert.assertEquals(BigInteger.valueOf(3), out.getValue1());
    }

    @Test
    public void testSetWeightInputRoundTrip() {
        ConsensusPrecompiled c = load();
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setInput(
                encodeStringUintInput(
                        c, ConsensusPrecompiled.FUNC_SETWEIGHT, "w-node", BigInteger.valueOf(5)));
        Tuple2<String, BigInteger> in = c.getSetWeightInput(receipt);
        Assert.assertEquals("w-node", in.getValue1());
        Assert.assertEquals(BigInteger.valueOf(5), in.getValue2());
    }

    @Test
    public void testSetWeightOutputRoundTrip() {
        ConsensusPrecompiled c = load();
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setOutput(encodeInt256Output(BigInteger.valueOf(9)));
        Tuple1<BigInteger> out = c.getSetWeightOutput(receipt);
        Assert.assertEquals(BigInteger.valueOf(9), out.getValue1());
    }

    @Test
    public void testRawFunctionAndSignedTxBuildersNonNull() throws Exception {
        ConsensusPrecompiled c = load();
        Assert.assertNotNull(c.getMethodAddObserverRawFunction("n"));
        Assert.assertNotNull(c.getMethodAddSealerRawFunction("n", BigInteger.ONE));
        Assert.assertNotNull(c.getMethodRemoveRawFunction("n"));
        Assert.assertNotNull(c.getMethodSetTermWeightRawFunction("n", BigInteger.ONE));
        Assert.assertNotNull(c.getMethodSetWeightRawFunction("n", BigInteger.ONE));
    }
}
