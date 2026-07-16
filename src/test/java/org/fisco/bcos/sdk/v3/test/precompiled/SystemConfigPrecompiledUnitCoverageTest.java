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
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int32;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.contract.precompiled.sysconfig.SystemConfigPrecompiled;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.junit.Assert;
import org.junit.Test;

/**
 * Pure-Java unit tests for {@link SystemConfigPrecompiled}. The {@link Client} is fully mocked; the
 * {@code getSetValueByKeyInput}/{@code getSetValueByKeyOutput} decoders are round-tripped against
 * ABI-encoded values produced by the wrapper's own {@code functionEncoder}. No live node is
 * contacted.
 */
public class SystemConfigPrecompiledUnitCoverageTest {

    private static final String ADDRESS = "0x0000000000000000000000000000000000001000";
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

    private SystemConfigPrecompiled load() {
        return SystemConfigPrecompiled.load(ADDRESS, mockClient(), keyPair());
    }

    private String hex(byte[] data) {
        return Hex.toHexStringWithPrefix(data);
    }

    @Test
    public void testStaticGettersAndLoad() {
        Assert.assertNotNull(SystemConfigPrecompiled.ABI);
        Assert.assertFalse(SystemConfigPrecompiled.ABI.isEmpty());
        Assert.assertNotNull(SystemConfigPrecompiled.getBinary(cryptoSuite));
        Assert.assertNotNull(
                SystemConfigPrecompiled.getBinary(new CryptoSuite(CryptoType.SM_TYPE)));
        Assert.assertNotNull(load());
    }

    @Test
    public void testSetValueByKeyInputRoundTrip() {
        SystemConfigPrecompiled c = load();
        Function function =
                new Function(
                        SystemConfigPrecompiled.FUNC_SETVALUEBYKEY,
                        Arrays.<Type>asList(
                                new Utf8String("tx_count_limit"), new Utf8String("1000")),
                        Collections.<TypeReference<?>>emptyList());
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setInput(hex(c.functionEncoder.encode(function)));
        Tuple2<String, String> in = c.getSetValueByKeyInput(receipt);
        Assert.assertEquals("tx_count_limit", in.getValue1());
        Assert.assertEquals("1000", in.getValue2());
    }

    @Test
    public void testSetValueByKeyOutputRoundTrip() {
        SystemConfigPrecompiled c = load();
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setOutput(
                hex(
                        FunctionEncoder.encodeParameters(
                                Arrays.<Type>asList(new Int32(BigInteger.valueOf(0))), null)));
        Tuple1<BigInteger> out = c.getSetValueByKeyOutput(receipt);
        Assert.assertEquals(BigInteger.ZERO, out.getValue1());
    }

    @Test
    public void testSetValueByKeyOutputNegative() {
        SystemConfigPrecompiled c = load();
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setOutput(
                hex(
                        FunctionEncoder.encodeParameters(
                                Arrays.<Type>asList(new Int32(BigInteger.valueOf(-1))), null)));
        Tuple1<BigInteger> out = c.getSetValueByKeyOutput(receipt);
        Assert.assertEquals(BigInteger.valueOf(-1), out.getValue1());
    }
}
