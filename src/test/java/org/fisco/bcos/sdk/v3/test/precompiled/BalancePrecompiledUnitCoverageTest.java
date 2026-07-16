package org.fisco.bcos.sdk.v3.test.precompiled;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.v3.contract.precompiled.balance.BalancePrecompiled;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.stubbing.Answer;

/**
 * Pure-Java unit tests for {@link BalancePrecompiled}. The {@link Client} is fully mocked. Input
 * decoders are round-tripped against ABI-encoded values from the wrapper's own {@code
 * functionEncoder}; the {@code view} call methods ({@code getBalance}, {@code listCaller}) are
 * exercised by stubbing {@code client.call(...)}. No live node is contacted.
 */
public class BalancePrecompiledUnitCoverageTest {

    private static final String ADDRESS = "0x0000000000000000000000000000000000001011";
    private static final String ACCOUNT_A = "0x0000000000000000000000000000000000000abc";
    private static final String ACCOUNT_B = "0x0000000000000000000000000000000000000def";
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

    private void stubCall(Client client, String output) {
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

    private CryptoKeyPair keyPair() {
        return cryptoSuite.getCryptoKeyPair();
    }

    private BalancePrecompiled load(Client client) {
        return BalancePrecompiled.load(ADDRESS, client, keyPair());
    }

    private String hex(byte[] data) {
        return Hex.toHexStringWithPrefix(data);
    }

    @Test
    public void testStaticGettersAndLoad() {
        Assert.assertNotNull(BalancePrecompiled.getABI());
        Assert.assertFalse(BalancePrecompiled.getABI().isEmpty());
        Assert.assertNotNull(BalancePrecompiled.getBinary(cryptoSuite));
        Assert.assertNotNull(BalancePrecompiled.getBinary(new CryptoSuite(CryptoType.SM_TYPE)));
        Assert.assertNotNull(load(mockClient()));
    }

    @Test
    public void testGetBalanceCall() throws ContractException {
        Client client = mockClient();
        // uint256 = 100
        stubCall(client, "0x0000000000000000000000000000000000000000000000000000000000000064");
        BalancePrecompiled c = load(client);
        Assert.assertEquals(BigInteger.valueOf(100), c.getBalance(ACCOUNT_A));
    }

    @Test
    public void testListCallerEmpty() throws ContractException {
        Client client = mockClient();
        // empty dynamic address array: offset 0x20, length 0
        stubCall(
                client,
                "0x0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000000");
        BalancePrecompiled c = load(client);
        List callers = c.listCaller();
        Assert.assertNotNull(callers);
        Assert.assertTrue(callers.isEmpty());
    }

    @Test
    public void testAddBalanceInputRoundTrip() {
        BalancePrecompiled c = load(mockClient());
        Function function =
                new Function(
                        BalancePrecompiled.FUNC_ADDBALANCE,
                        Arrays.<Type>asList(
                                new Address(ACCOUNT_A), new Uint256(BigInteger.valueOf(50))),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setInput(hex(c.functionEncoder.encode(function)));
        Tuple2<String, BigInteger> in = c.getAddBalanceInput(receipt);
        Assert.assertEquals(ACCOUNT_A, in.getValue1());
        Assert.assertEquals(BigInteger.valueOf(50), in.getValue2());
    }

    @Test
    public void testSubBalanceInputRoundTrip() {
        BalancePrecompiled c = load(mockClient());
        Function function =
                new Function(
                        BalancePrecompiled.FUNC_SUBBALANCE,
                        Arrays.<Type>asList(
                                new Address(ACCOUNT_A), new Uint256(BigInteger.valueOf(11))),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setInput(hex(c.functionEncoder.encode(function)));
        Tuple2<String, BigInteger> in = c.getSubBalanceInput(receipt);
        Assert.assertEquals(ACCOUNT_A, in.getValue1());
        Assert.assertEquals(BigInteger.valueOf(11), in.getValue2());
    }

    @Test
    public void testRegisterCallerInputRoundTrip() {
        BalancePrecompiled c = load(mockClient());
        Function function =
                new Function(
                        BalancePrecompiled.FUNC_REGISTERCALLER,
                        Arrays.<Type>asList(new Address(ACCOUNT_A)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setInput(hex(c.functionEncoder.encode(function)));
        Tuple1<String> in = c.getRegisterCallerInput(receipt);
        Assert.assertEquals(ACCOUNT_A, in.getValue1());
    }

    @Test
    public void testUnregisterCallerInputRoundTrip() {
        BalancePrecompiled c = load(mockClient());
        Function function =
                new Function(
                        BalancePrecompiled.FUNC_UNREGISTERCALLER,
                        Arrays.<Type>asList(new Address(ACCOUNT_B)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setInput(hex(c.functionEncoder.encode(function)));
        Tuple1<String> in = c.getUnregisterCallerInput(receipt);
        Assert.assertEquals(ACCOUNT_B, in.getValue1());
    }

    @Test
    public void testTransferInputRoundTrip() {
        BalancePrecompiled c = load(mockClient());
        Function function =
                new Function(
                        BalancePrecompiled.FUNC_TRANSFER,
                        Arrays.<Type>asList(
                                new Address(ACCOUNT_A),
                                new Address(ACCOUNT_B),
                                new Uint256(BigInteger.valueOf(77))),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setInput(hex(c.functionEncoder.encode(function)));
        Tuple3<String, String, BigInteger> in = c.getTransferInput(receipt);
        Assert.assertEquals(ACCOUNT_A, in.getValue1());
        Assert.assertEquals(ACCOUNT_B, in.getValue2());
        Assert.assertEquals(BigInteger.valueOf(77), in.getValue3());
    }
}
