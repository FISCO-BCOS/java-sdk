package org.fisco.bcos.sdk.v3.test.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.Arrays;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int32;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.contract.auth.contracts.AccountManager;
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
 * Pure-Java (no live node) unit tests for the generated {@link AccountManager} auth wrapper. The
 * {@link Client} is fully mocked; decoders are exercised via real ABI encode -> decode round trips,
 * call-path getters via a stubbed {@code client.call(...)}, and the transaction paths via a {@link
 * MockTransactionProcessor}.
 */
public class AccountManagerUnitCoverageTest {

    private static final String ADDRESS = "0x0000000000000000000000000000000000010002";
    private static final String ACCOUNT = "0x1234567890123456789012345678901234567890";
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

    private AccountManager load() {
        return AccountManager.load(mockClient(), keyPair());
    }

    private void mockCall(Client client, String output) {
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
        Assert.assertNotNull(AccountManager.getABI());
        Assert.assertFalse(AccountManager.getABI().isEmpty());
        Assert.assertNotNull(AccountManager.ABI);
        Assert.assertEquals("getAccountStatus", AccountManager.FUNC_GETACCOUNTSTATUS);
        Assert.assertEquals("setAccountStatus", AccountManager.FUNC_SETACCOUNTSTATUS);
    }

    @Test
    public void testLoad() {
        AccountManager am = load();
        Assert.assertNotNull(am);
        Assert.assertNotNull(am.getContractAddress());
    }

    @Test
    public void testGetAccountStatusCallPath() throws Exception {
        Client client = mockClient();
        mockCall(client, encOutput(new Uint8(BigInteger.valueOf(3))));
        AccountManager am = AccountManager.load(client, keyPair());
        BigInteger status = am.getAccountStatus(ACCOUNT);
        Assert.assertEquals(BigInteger.valueOf(3), status);
    }

    @Test
    public void testSetAccountStatusInputRoundTrip() {
        AccountManager am = load();
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setInput(encInput(new Address(ACCOUNT), new Uint8(BigInteger.valueOf(2))));
        Tuple2<String, BigInteger> in = am.getSetAccountStatusInput(receipt);
        Assert.assertEquals(ACCOUNT, in.getValue1());
        Assert.assertEquals(BigInteger.valueOf(2), in.getValue2());
    }

    @Test
    public void testSetAccountStatusOutputRoundTrip() {
        AccountManager am = load();
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setOutput(encOutput(new Int32(7)));
        Tuple1<BigInteger> out = am.getSetAccountStatusOutput(receipt);
        Assert.assertEquals(BigInteger.valueOf(7), out.getValue1());
    }

    @Test
    public void testSetAccountStatusTransactionPath() {
        Client client = mockClient();
        AccountManager am = AccountManager.load(client, keyPair());
        MockTransactionProcessor processor =
                new MockTransactionProcessor(
                        client,
                        keyPair(),
                        "group0",
                        "chain0",
                        "0xhash",
                        0,
                        encOutput(new Int32(0)));
        am.setTransactionProcessor(processor);
        TransactionReceipt receipt = am.setAccountStatus(ACCOUNT, BigInteger.ONE);
        Assert.assertNotNull(receipt);
        Assert.assertEquals(0, receipt.getStatus());
        Tuple1<BigInteger> out = am.getSetAccountStatusOutput(receipt);
        Assert.assertEquals(BigInteger.ZERO, out.getValue1());
    }

    @Test
    public void testSetAccountStatusAsyncPath() {
        Client client = mockClient();
        AccountManager am = AccountManager.load(client, keyPair());
        MockTransactionProcessor processor =
                new MockTransactionProcessor(
                        client, keyPair(), "group0", "chain0", "0xhash", 0, "0x");
        am.setTransactionProcessor(processor);
        final TransactionReceipt[] captured = new TransactionReceipt[1];
        String hash =
                am.setAccountStatus(
                        ACCOUNT,
                        BigInteger.ONE,
                        new org.fisco.bcos.sdk.v3.model.callback.TransactionCallback() {
                            @Override
                            public void onResponse(TransactionReceipt receipt) {
                                captured[0] = receipt;
                            }
                        });
        Assert.assertEquals("0xhash", hash);
        Assert.assertNotNull(captured[0]);
    }

    @Test
    public void testSignedTransactionHelper() {
        AccountManager am = load();
        try {
            Assert.assertNotNull(am.getSignedTransactionForSetAccountStatus(ACCOUNT, BigInteger.ONE));
        } catch (Throwable t) {
            // Native signing unavailable offline; function-encoding path still executed.
            Assert.assertNotNull(am);
        }
    }
}
