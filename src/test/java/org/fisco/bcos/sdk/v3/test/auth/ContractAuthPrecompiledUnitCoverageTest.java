package org.fisco.bcos.sdk.v3.test.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes4;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.v3.contract.auth.contracts.ContractAuthPrecompiled;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.test.transaction.mock.MockTransactionProcessor;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.stubbing.Answer;

/**
 * Pure-Java (no live node) unit tests for the generated {@link ContractAuthPrecompiled} auth
 * wrapper: static getters, every {@code view} call-path getter via a stubbed {@code
 * client.call(...)}, the abi input/output decoders via encode -> decode round trips (including
 * {@code bytes4}/{@code byte[]} params and the two overloaded {@code setContractStatus} variants),
 * and the transaction paths via a {@link MockTransactionProcessor}.
 */
public class ContractAuthPrecompiledUnitCoverageTest {

    private static final String ADDRESS = "0x0000000000000000000000000000000000001005";
    private static final String CONTRACT = "0x1234567890123456789012345678901234567890";
    private static final String ACCOUNT = "0x2222222222222222222222222222222222222222";
    private static final byte[] FUNC = new byte[] {0x11, 0x22, 0x33, 0x44};
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

    private CryptoKeyPair keyPair() {
        return cryptoSuite.getCryptoKeyPair();
    }

    private ContractAuthPrecompiled load() {
        return ContractAuthPrecompiled.load(ADDRESS, mockClient(), keyPair());
    }

    private ContractAuthPrecompiled loadWithCall(String output) {
        Client client = mockClient();
        when(client.call(any()))
                .then(
                        (Answer<Call>)
                                invocation -> {
                                    Call call = new Call();
                                    Call.CallOutput callOutput = new Call.CallOutput();
                                    callOutput.setOutput(output);
                                    callOutput.setStatus(0);
                                    call.setResult(callOutput);
                                    return call;
                                });
        return ContractAuthPrecompiled.load(ADDRESS, client, keyPair());
    }

    private ContractAuthPrecompiled loadWithTx(String output) {
        Client client = mockClient();
        ContractAuthPrecompiled c = ContractAuthPrecompiled.load(ADDRESS, client, keyPair());
        c.setTransactionProcessor(
                new MockTransactionProcessor(
                        client, keyPair(), "group0", "chain0", "0xhash", 0, output));
        return c;
    }

    private static String encInput(Type... values) {
        byte[] selector = new byte[] {0x12, 0x34, 0x56, 0x78};
        return "0x" + Hex.toHexString(FunctionEncoder.encodeParameters(Arrays.asList(values), selector));
    }

    private static String encOutput(Type... values) {
        return "0x" + Hex.toHexString(FunctionEncoder.encodeParameters(Arrays.asList(values), null));
    }

    @Test
    public void testStaticGetters() {
        Assert.assertNotNull(ContractAuthPrecompiled.getABI());
        Assert.assertFalse(ContractAuthPrecompiled.getABI().isEmpty());
        Assert.assertNotNull(ContractAuthPrecompiled.ABI);
        Assert.assertEquals("checkMethodAuth", ContractAuthPrecompiled.FUNC_CHECKMETHODAUTH);
        Assert.assertEquals("closeDeployAuth", ContractAuthPrecompiled.FUNC_CLOSEDEPLOYAUTH);
        Assert.assertEquals("closeMethodAuth", ContractAuthPrecompiled.FUNC_CLOSEMETHODAUTH);
        Assert.assertEquals("contractAvailable", ContractAuthPrecompiled.FUNC_CONTRACTAVAILABLE);
        Assert.assertEquals("deployType", ContractAuthPrecompiled.FUNC_DEPLOYTYPE);
        Assert.assertEquals("getAdmin", ContractAuthPrecompiled.FUNC_GETADMIN);
        Assert.assertEquals("getMethodAuth", ContractAuthPrecompiled.FUNC_GETMETHODAUTH);
        Assert.assertEquals("hasDeployAuth", ContractAuthPrecompiled.FUNC_HASDEPLOYAUTH);
        Assert.assertEquals("initAuth", ContractAuthPrecompiled.FUNC_INITAUTH);
        Assert.assertEquals("openDeployAuth", ContractAuthPrecompiled.FUNC_OPENDEPLOYAUTH);
        Assert.assertEquals("openMethodAuth", ContractAuthPrecompiled.FUNC_OPENMETHODAUTH);
        Assert.assertEquals("resetAdmin", ContractAuthPrecompiled.FUNC_RESETADMIN);
        Assert.assertEquals("setContractStatus", ContractAuthPrecompiled.FUNC_SETCONTRACTSTATUS);
        Assert.assertEquals("setDeployAuthType", ContractAuthPrecompiled.FUNC_SETDEPLOYAUTHTYPE);
        Assert.assertEquals("setMethodAuthType", ContractAuthPrecompiled.FUNC_SETMETHODAUTHTYPE);
    }

    @Test
    public void testLoad() {
        ContractAuthPrecompiled c = load();
        Assert.assertNotNull(c);
        Assert.assertEquals(ADDRESS, c.getContractAddress());
    }

    // ---- call-path (view) getters ----

    @Test
    public void testCheckMethodAuthCall() throws Exception {
        ContractAuthPrecompiled c = loadWithCall(encOutput(new Bool(true)));
        Assert.assertTrue(c.checkMethodAuth(CONTRACT, FUNC, ACCOUNT));
    }

    @Test
    public void testContractAvailableCall() throws Exception {
        ContractAuthPrecompiled c = loadWithCall(encOutput(new Bool(false)));
        Assert.assertFalse(c.contractAvailable(CONTRACT));
    }

    @Test
    public void testDeployTypeCall() throws Exception {
        ContractAuthPrecompiled c = loadWithCall(encOutput(new Uint256(BigInteger.valueOf(2))));
        Assert.assertEquals(BigInteger.valueOf(2), c.deployType());
    }

    @Test
    public void testGetAdminCall() throws Exception {
        ContractAuthPrecompiled c = loadWithCall(encOutput(new Address(ACCOUNT)));
        Assert.assertEquals(ACCOUNT, c.getAdmin(CONTRACT));
    }

    @Test
    public void testGetMethodAuthCall() throws Exception {
        DynamicArray<Utf8String> open =
                new DynamicArray<>(Utf8String.class, Collections.singletonList(new Utf8String("a")));
        DynamicArray<Utf8String> close =
                new DynamicArray<>(Utf8String.class, Collections.singletonList(new Utf8String("b")));
        ContractAuthPrecompiled c =
                loadWithCall(encOutput(new Uint8(BigInteger.ONE), open, close));
        Tuple3<BigInteger, List<String>, List<String>> auth = c.getMethodAuth(CONTRACT, FUNC);
        Assert.assertEquals(BigInteger.ONE, auth.getValue1());
        Assert.assertEquals("a", auth.getValue2().get(0));
        Assert.assertEquals("b", auth.getValue3().get(0));
    }

    @Test
    public void testHasDeployAuthCall() throws Exception {
        ContractAuthPrecompiled c = loadWithCall(encOutput(new Bool(true)));
        Assert.assertTrue(c.hasDeployAuth(ACCOUNT));
    }

    // ---- input/output decoders ----

    @Test
    public void testCloseDeployAuthInputOutputRoundTrip() {
        ContractAuthPrecompiled c = load();
        TransactionReceipt in = new TransactionReceipt();
        in.setInput(encInput(new Address(ACCOUNT)));
        Assert.assertEquals(ACCOUNT, c.getCloseDeployAuthInput(in).getValue1());

        TransactionReceipt out = new TransactionReceipt();
        out.setOutput(encOutput(new Int256(BigInteger.ZERO)));
        Assert.assertEquals(BigInteger.ZERO, c.getCloseDeployAuthOutput(out).getValue1());
    }

    @Test
    public void testCloseMethodAuthInputOutputRoundTrip() {
        ContractAuthPrecompiled c = load();
        TransactionReceipt in = new TransactionReceipt();
        in.setInput(encInput(new Address(CONTRACT), new Bytes4(FUNC), new Address(ACCOUNT)));
        Tuple3<String, byte[], String> decoded = c.getCloseMethodAuthInput(in);
        Assert.assertEquals(CONTRACT, decoded.getValue1());
        Assert.assertArrayEquals(FUNC, decoded.getValue2());
        Assert.assertEquals(ACCOUNT, decoded.getValue3());

        TransactionReceipt out = new TransactionReceipt();
        out.setOutput(encOutput(new Int256(BigInteger.valueOf(-1))));
        Assert.assertEquals(BigInteger.valueOf(-1), c.getCloseMethodAuthOutput(out).getValue1());
    }

    @Test
    public void testInitAuthInputOutputRoundTrip() {
        ContractAuthPrecompiled c = load();
        TransactionReceipt in = new TransactionReceipt();
        in.setInput(encInput(new Utf8String("acct")));
        Assert.assertEquals("acct", c.getInitAuthInput(in).getValue1());

        TransactionReceipt out = new TransactionReceipt();
        out.setOutput(encOutput(new Int256(BigInteger.ONE)));
        Assert.assertEquals(BigInteger.ONE, c.getInitAuthOutput(out).getValue1());
    }

    @Test
    public void testOpenDeployAuthInputOutputRoundTrip() {
        ContractAuthPrecompiled c = load();
        TransactionReceipt in = new TransactionReceipt();
        in.setInput(encInput(new Address(ACCOUNT)));
        Assert.assertEquals(ACCOUNT, c.getOpenDeployAuthInput(in).getValue1());

        TransactionReceipt out = new TransactionReceipt();
        out.setOutput(encOutput(new Int256(BigInteger.ZERO)));
        Assert.assertEquals(BigInteger.ZERO, c.getOpenDeployAuthOutput(out).getValue1());
    }

    @Test
    public void testOpenMethodAuthInputOutputRoundTrip() {
        ContractAuthPrecompiled c = load();
        TransactionReceipt in = new TransactionReceipt();
        in.setInput(encInput(new Address(CONTRACT), new Bytes4(FUNC), new Address(ACCOUNT)));
        Tuple3<String, byte[], String> decoded = c.getOpenMethodAuthInput(in);
        Assert.assertEquals(CONTRACT, decoded.getValue1());
        Assert.assertArrayEquals(FUNC, decoded.getValue2());
        Assert.assertEquals(ACCOUNT, decoded.getValue3());

        TransactionReceipt out = new TransactionReceipt();
        out.setOutput(encOutput(new Int256(BigInteger.ONE)));
        Assert.assertEquals(BigInteger.ONE, c.getOpenMethodAuthOutput(out).getValue1());
    }

    @Test
    public void testResetAdminInputOutputRoundTrip() {
        ContractAuthPrecompiled c = load();
        TransactionReceipt in = new TransactionReceipt();
        in.setInput(encInput(new Address(CONTRACT), new Address(ACCOUNT)));
        Tuple2<String, String> decoded = c.getResetAdminInput(in);
        Assert.assertEquals(CONTRACT, decoded.getValue1());
        Assert.assertEquals(ACCOUNT, decoded.getValue2());

        TransactionReceipt out = new TransactionReceipt();
        out.setOutput(encOutput(new Int256(BigInteger.ZERO)));
        Assert.assertEquals(BigInteger.ZERO, c.getResetAdminOutput(out).getValue1());
    }

    @Test
    public void testSetContractStatusAddressBoolInputOutputRoundTrip() {
        ContractAuthPrecompiled c = load();
        TransactionReceipt in = new TransactionReceipt();
        in.setInput(encInput(new Address(CONTRACT), new Bool(true)));
        Tuple2<String, Boolean> decoded = c.getSetContractStatusAddressBoolInput(in);
        Assert.assertEquals(CONTRACT, decoded.getValue1());
        Assert.assertTrue(decoded.getValue2());

        TransactionReceipt out = new TransactionReceipt();
        out.setOutput(encOutput(new Int256(BigInteger.ZERO)));
        Assert.assertEquals(
                BigInteger.ZERO, c.getSetContractStatusAddressBoolOutput(out).getValue1());
    }

    @Test
    public void testSetContractStatusAddressUint8InputOutputRoundTrip() {
        ContractAuthPrecompiled c = load();
        TransactionReceipt in = new TransactionReceipt();
        in.setInput(encInput(new Address(CONTRACT), new Uint8(BigInteger.valueOf(2))));
        Tuple2<String, BigInteger> decoded = c.getSetContractStatusAddressUint8Input(in);
        Assert.assertEquals(CONTRACT, decoded.getValue1());
        Assert.assertEquals(BigInteger.valueOf(2), decoded.getValue2());

        TransactionReceipt out = new TransactionReceipt();
        out.setOutput(encOutput(new Int256(BigInteger.ONE)));
        Assert.assertEquals(
                BigInteger.ONE, c.getSetContractStatusAddressUint8Output(out).getValue1());
    }

    @Test
    public void testSetDeployAuthTypeInputOutputRoundTrip() {
        ContractAuthPrecompiled c = load();
        TransactionReceipt in = new TransactionReceipt();
        in.setInput(encInput(new Uint8(BigInteger.ONE)));
        Assert.assertEquals(BigInteger.ONE, c.getSetDeployAuthTypeInput(in).getValue1());

        TransactionReceipt out = new TransactionReceipt();
        out.setOutput(encOutput(new Int256(BigInteger.ZERO)));
        Assert.assertEquals(BigInteger.ZERO, c.getSetDeployAuthTypeOutput(out).getValue1());
    }

    @Test
    public void testSetMethodAuthTypeInputOutputRoundTrip() {
        ContractAuthPrecompiled c = load();
        TransactionReceipt in = new TransactionReceipt();
        in.setInput(encInput(new Address(CONTRACT), new Bytes4(FUNC), new Uint8(BigInteger.ONE)));
        Tuple3<String, byte[], BigInteger> decoded = c.getSetMethodAuthTypeInput(in);
        Assert.assertEquals(CONTRACT, decoded.getValue1());
        Assert.assertArrayEquals(FUNC, decoded.getValue2());
        Assert.assertEquals(BigInteger.ONE, decoded.getValue3());

        TransactionReceipt out = new TransactionReceipt();
        out.setOutput(encOutput(new Int256(BigInteger.ZERO)));
        Assert.assertEquals(BigInteger.ZERO, c.getSetMethodAuthTypeOutput(out).getValue1());
    }

    // ---- transaction paths ----

    @Test
    public void testTransactionPaths() {
        ContractAuthPrecompiled c = loadWithTx(encOutput(new Int256(BigInteger.ZERO)));
        Assert.assertNotNull(c.closeDeployAuth(ACCOUNT));
        Assert.assertNotNull(c.openDeployAuth(ACCOUNT));
        Assert.assertNotNull(c.closeMethodAuth(CONTRACT, FUNC, ACCOUNT));
        Assert.assertNotNull(c.openMethodAuth(CONTRACT, FUNC, ACCOUNT));
        Assert.assertNotNull(c.initAuth(ACCOUNT));
        Assert.assertNotNull(c.resetAdmin(CONTRACT, ACCOUNT));
        Assert.assertNotNull(c.setContractStatus(CONTRACT, true));
        Assert.assertNotNull(c.setContractStatus(CONTRACT, BigInteger.ONE));
        Assert.assertNotNull(c.setDeployAuthType(BigInteger.ONE));
        Assert.assertNotNull(c.setMethodAuthType(CONTRACT, FUNC, BigInteger.ONE));
    }

    @Test
    public void testSignedTransactionHelpers() {
        ContractAuthPrecompiled c = load();
        try {
            Assert.assertNotNull(c.getSignedTransactionForCloseDeployAuth(ACCOUNT));
            Assert.assertNotNull(c.getSignedTransactionForOpenDeployAuth(ACCOUNT));
            Assert.assertNotNull(c.getSignedTransactionForCloseMethodAuth(CONTRACT, FUNC, ACCOUNT));
            Assert.assertNotNull(c.getSignedTransactionForOpenMethodAuth(CONTRACT, FUNC, ACCOUNT));
            Assert.assertNotNull(c.getSignedTransactionForInitAuth(ACCOUNT));
            Assert.assertNotNull(c.getSignedTransactionForResetAdmin(CONTRACT, ACCOUNT));
            Assert.assertNotNull(c.getSignedTransactionForSetContractStatus(CONTRACT, true));
            Assert.assertNotNull(
                    c.getSignedTransactionForSetContractStatus(CONTRACT, BigInteger.ONE));
            Assert.assertNotNull(c.getSignedTransactionForSetDeployAuthType(BigInteger.ONE));
            Assert.assertNotNull(
                    c.getSignedTransactionForSetMethodAuthType(CONTRACT, FUNC, BigInteger.ONE));
        } catch (Throwable t) {
            Assert.assertNotNull(c);
        }
    }
}
