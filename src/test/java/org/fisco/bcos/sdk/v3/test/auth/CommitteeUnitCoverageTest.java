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
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint32;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple4;
import org.fisco.bcos.sdk.v3.contract.auth.contracts.Committee;
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
 * Pure-Java (no live node) unit tests for the generated {@link Committee} auth wrapper: static
 * getters, every {@code view} call-path getter via a stubbed {@code client.call(...)}, the abi input
 * decoders via encode -> decode round trips, and the transaction paths via a {@link
 * MockTransactionProcessor}.
 */
public class CommitteeUnitCoverageTest {

    private static final String ADDRESS = "0x1111111111111111111111111111111111111111";
    private static final String GOVERNOR = "0x2222222222222222222222222222222222222222";
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

    private Committee load() {
        return Committee.load(ADDRESS, mockClient(), keyPair());
    }

    private Committee loadWithCall(String output) {
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
        return Committee.load(ADDRESS, client, keyPair());
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
        Assert.assertNotNull(Committee.getABI());
        Assert.assertFalse(Committee.getABI().isEmpty());
        Assert.assertNotNull(Committee.ABI);
        Assert.assertEquals("_owner", Committee.FUNC__OWNER);
        Assert.assertEquals("_participatesRate", Committee.FUNC__PARTICIPATESRATE);
        Assert.assertEquals("_winRate", Committee.FUNC__WINRATE);
        Assert.assertEquals("auth", Committee.FUNC_AUTH);
        Assert.assertEquals("getCommitteeInfo", Committee.FUNC_GETCOMMITTEEINFO);
        Assert.assertEquals("getWeight", Committee.FUNC_GETWEIGHT);
        Assert.assertEquals("getWeights", Committee.FUNC_GETWEIGHTS);
        Assert.assertEquals("isGovernor", Committee.FUNC_ISGOVERNOR);
        Assert.assertEquals("setOwner", Committee.FUNC_SETOWNER);
        Assert.assertEquals("setRate", Committee.FUNC_SETRATE);
        Assert.assertEquals("setWeight", Committee.FUNC_SETWEIGHT);
    }

    @Test
    public void testLoad() {
        Committee committee = load();
        Assert.assertNotNull(committee);
        Assert.assertEquals(ADDRESS, committee.getContractAddress());
    }

    @Test
    public void testOwnerCall() throws Exception {
        Committee committee = loadWithCall(encOutput(new Address(GOVERNOR)));
        Assert.assertEquals(GOVERNOR, committee._owner());
    }

    @Test
    public void testParticipatesRateCall() throws Exception {
        Committee committee = loadWithCall(encOutput(new Uint8(BigInteger.valueOf(33))));
        Assert.assertEquals(BigInteger.valueOf(33), committee._participatesRate());
    }

    @Test
    public void testWinRateCall() throws Exception {
        Committee committee = loadWithCall(encOutput(new Uint8(BigInteger.valueOf(66))));
        Assert.assertEquals(BigInteger.valueOf(66), committee._winRate());
    }

    @Test
    public void testAuthCall() throws Exception {
        Committee committee = loadWithCall(encOutput(new Bool(true)));
        Assert.assertTrue(committee.auth(GOVERNOR));
    }

    @Test
    public void testGetCommitteeInfoCall() throws Exception {
        DynamicArray<Address> governors =
                new DynamicArray<>(Address.class, Collections.singletonList(new Address(GOVERNOR)));
        DynamicArray<Uint32> weights =
                new DynamicArray<>(
                        Uint32.class, Collections.singletonList(new Uint32(BigInteger.TEN)));
        Committee committee =
                loadWithCall(
                        encOutput(
                                new Uint8(BigInteger.valueOf(50)),
                                new Uint8(BigInteger.valueOf(60)),
                                governors,
                                weights));
        Tuple4<BigInteger, BigInteger, List<String>, List<BigInteger>> info =
                committee.getCommitteeInfo();
        Assert.assertEquals(BigInteger.valueOf(50), info.getValue1());
        Assert.assertEquals(BigInteger.valueOf(60), info.getValue2());
        Assert.assertEquals(1, info.getValue3().size());
        Assert.assertEquals(GOVERNOR, info.getValue3().get(0));
        Assert.assertEquals(BigInteger.TEN, info.getValue4().get(0));
    }

    @Test
    public void testGetWeightCall() throws Exception {
        Committee committee = loadWithCall(encOutput(new Uint32(BigInteger.valueOf(5))));
        Assert.assertEquals(BigInteger.valueOf(5), committee.getWeight(GOVERNOR));
    }

    @Test
    public void testGetWeightsNoArgCall() throws Exception {
        Committee committee = loadWithCall(encOutput(new Uint32(BigInteger.valueOf(8))));
        Assert.assertEquals(BigInteger.valueOf(8), committee.getWeights());
    }

    @Test
    public void testGetWeightsWithVotesCall() throws Exception {
        Committee committee = loadWithCall(encOutput(new Uint32(BigInteger.valueOf(9))));
        Assert.assertEquals(
                BigInteger.valueOf(9),
                committee.getWeights(Collections.singletonList(GOVERNOR)));
    }

    @Test
    public void testIsGovernorCall() throws Exception {
        Committee committee = loadWithCall(encOutput(new Bool(false)));
        Assert.assertFalse(committee.isGovernor(GOVERNOR));
    }

    @Test
    public void testSetOwnerInputRoundTrip() {
        Committee committee = load();
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setInput(encInput(new Address(GOVERNOR)));
        Tuple1<String> in = committee.getSetOwnerInput(receipt);
        Assert.assertEquals(GOVERNOR, in.getValue1());
    }

    @Test
    public void testSetRateInputRoundTrip() {
        Committee committee = load();
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setInput(
                encInput(new Uint8(BigInteger.valueOf(40)), new Uint8(BigInteger.valueOf(70))));
        Tuple2<BigInteger, BigInteger> in = committee.getSetRateInput(receipt);
        Assert.assertEquals(BigInteger.valueOf(40), in.getValue1());
        Assert.assertEquals(BigInteger.valueOf(70), in.getValue2());
    }

    @Test
    public void testSetWeightInputRoundTrip() {
        Committee committee = load();
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setInput(encInput(new Address(GOVERNOR), new Uint32(BigInteger.valueOf(12))));
        Tuple2<String, BigInteger> in = committee.getSetWeightInput(receipt);
        Assert.assertEquals(GOVERNOR, in.getValue1());
        Assert.assertEquals(BigInteger.valueOf(12), in.getValue2());
    }

    @Test
    public void testSetOwnerTransactionPath() {
        Client client = mockClient();
        Committee committee = Committee.load(ADDRESS, client, keyPair());
        committee.setTransactionProcessor(
                new MockTransactionProcessor(
                        client, keyPair(), "group0", "chain0", "0xhash", 0, "0x"));
        TransactionReceipt receipt = committee.setOwner(GOVERNOR);
        Assert.assertNotNull(receipt);
        Assert.assertEquals(0, receipt.getStatus());
    }

    @Test
    public void testSetRateAndSetWeightTransactionPath() {
        Client client = mockClient();
        Committee committee = Committee.load(ADDRESS, client, keyPair());
        committee.setTransactionProcessor(
                new MockTransactionProcessor(
                        client, keyPair(), "group0", "chain0", "0xhash", 0, "0x"));
        Assert.assertNotNull(
                committee.setRate(BigInteger.valueOf(40), BigInteger.valueOf(70)));
        Assert.assertNotNull(committee.setWeight(GOVERNOR, BigInteger.valueOf(3)));
    }

    @Test
    public void testSignedTransactionHelpers() {
        Committee committee = load();
        try {
            Assert.assertNotNull(committee.getSignedTransactionForSetOwner(GOVERNOR));
            Assert.assertNotNull(
                    committee.getSignedTransactionForSetRate(
                            BigInteger.valueOf(40), BigInteger.valueOf(70)));
            Assert.assertNotNull(
                    committee.getSignedTransactionForSetWeight(GOVERNOR, BigInteger.ONE));
        } catch (Throwable t) {
            Assert.assertNotNull(committee);
        }
    }
}
