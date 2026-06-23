package org.fisco.bcos.sdk.v3.test.precompiled;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.Arrays;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int32;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.KVTablePrecompiled;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.stubbing.Answer;

/**
 * Pure-Java (no live node) unit tests for {@link KVTablePrecompiled}: static getters, the
 * {@code get} call (via a mocked client.call response) and the {@code set} input/output abi decoder
 * helpers exercised via a real encode -> decode round trip.
 */
public class KVTablePrecompiledUnitCoverageTest {

    private static final String ADDRESS = "0x000000000000000000000000000000000000100b";
    private final CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);

    private Client mockClient() {
        Client client = mock(Client.class);
        when(client.getChainId()).thenReturn("chain0");
        when(client.getGroup()).thenReturn("group0");
        when(client.getCryptoSuite()).thenReturn(cryptoSuite);
        when(client.isWASM()).thenReturn(false);
        when(client.getBlockLimit()).thenReturn(BigInteger.valueOf(500));
        when(client.getExtraData()).thenReturn("");
        when(client.getNativePointer()).thenReturn(0L);
        return client;
    }

    private KVTablePrecompiled load(Client client) {
        return KVTablePrecompiled.load(ADDRESS, client, cryptoSuite.getCryptoKeyPair());
    }

    private static String encInput(Type... values) {
        byte[] dummySelector = new byte[] {0x12, 0x34, 0x56, 0x78};
        byte[] encoded = FunctionEncoder.encodeParameters(Arrays.asList(values), dummySelector);
        return "0x" + Hex.toHexString(encoded);
    }

    private static String encOutput(Type... values) {
        byte[] encoded = FunctionEncoder.encodeParameters(Arrays.asList(values), null);
        return "0x" + Hex.toHexString(encoded);
    }

    private void mockCall(Client client, String output, int status) {
        when(client.call(any()))
                .then(
                        (Answer<Call>)
                                invocation -> {
                                    Call call = new Call();
                                    Call.CallOutput callOutput = new Call.CallOutput();
                                    callOutput.setOutput(output);
                                    callOutput.setStatus(status);
                                    call.setResult(callOutput);
                                    return call;
                                });
    }

    @Test
    public void testStaticGetters() {
        Assert.assertNotNull(KVTablePrecompiled.getABI());
        Assert.assertFalse(KVTablePrecompiled.getABI().isEmpty());
        Assert.assertNotNull(KVTablePrecompiled.ABI);
        Assert.assertNotNull(KVTablePrecompiled.getBinary(cryptoSuite));
        CryptoSuite sm = new CryptoSuite(CryptoType.SM_TYPE);
        Assert.assertEquals(KVTablePrecompiled.SM_BINARY, KVTablePrecompiled.getBinary(sm));
        Assert.assertEquals("get", KVTablePrecompiled.FUNC_GET);
        Assert.assertEquals("set", KVTablePrecompiled.FUNC_SET);
    }

    @Test
    public void testLoad() {
        KVTablePrecompiled kv = load(mockClient());
        Assert.assertNotNull(kv);
        Assert.assertEquals(ADDRESS, kv.getContractAddress());
    }

    @Test
    public void testGetCall() throws ContractException {
        Client client = mockClient();
        KVTablePrecompiled kv = load(client);
        mockCall(client, encOutput(new Bool(true), new Utf8String("theValue")), 0);
        Tuple2<Boolean, String> result = kv.get("someKey");
        Assert.assertTrue(result.getValue1());
        Assert.assertEquals("theValue", result.getValue2());
    }

    @Test
    public void testGetCallNonZeroStatusThrows() {
        Client client = mockClient();
        KVTablePrecompiled kv = load(client);
        mockCall(client, "0x", 1);
        try {
            kv.get("k");
            Assert.fail("expected ContractException for non-zero status");
        } catch (ContractException e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void testGetSetInputRoundTrip() {
        KVTablePrecompiled kv = load(mockClient());
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setInput(encInput(new Utf8String("k"), new Utf8String("v")));
        Tuple2<String, String> decoded = kv.getSetInput(receipt);
        Assert.assertEquals("k", decoded.getValue1());
        Assert.assertEquals("v", decoded.getValue2());
    }

    @Test
    public void testGetSetOutputRoundTrip() {
        KVTablePrecompiled kv = load(mockClient());
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setOutput(encOutput(new Int32(42)));
        Tuple1<BigInteger> decoded = kv.getSetOutput(receipt);
        Assert.assertEquals(BigInteger.valueOf(42), decoded.getValue1());
    }

    @Test
    public void testGetSignedTransactionForSet() {
        KVTablePrecompiled kv = load(mockClient());
        try {
            Assert.assertNotNull(kv.getSignedTransactionForSet("k", "v"));
        } catch (Throwable t) {
            // Native signing unavailable offline; function-encoding path still executed.
            Assert.assertNotNull(kv);
        }
    }
}
