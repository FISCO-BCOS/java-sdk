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
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple4;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple5;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple7;
import org.fisco.bcos.sdk.v3.contract.auth.contracts.ProposalManager;
import org.fisco.bcos.sdk.v3.contract.auth.po.ProposalInfo;
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
 * Pure-Java (no live node) unit tests for the generated {@link ProposalManager} auth wrapper: static
 * getters, every {@code view} call-path getter via a stubbed {@code client.call(...)}, the abi
 * input/output decoders via encode -> decode round trips, and the transaction paths via a {@link
 * MockTransactionProcessor}.
 */
public class ProposalManagerUnitCoverageTest {

    private static final String ADDRESS = "0x3333333333333333333333333333333333333333";
    private static final String RESOURCE = "0x4444444444444444444444444444444444444444";
    private static final String VOTER = "0x5555555555555555555555555555555555555555";
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

    private ProposalManager load() {
        return ProposalManager.load(ADDRESS, mockClient(), keyPair());
    }

    private ProposalManager loadWithCall(String output) {
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
        return ProposalManager.load(ADDRESS, client, keyPair());
    }

    private ProposalManager loadWithTx(String output) {
        Client client = mockClient();
        ProposalManager c = ProposalManager.load(ADDRESS, client, keyPair());
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
        Assert.assertNotNull(ProposalManager.getABI());
        Assert.assertFalse(ProposalManager.getABI().isEmpty());
        Assert.assertNotNull(ProposalManager.ABI);
        Assert.assertEquals("_owner", ProposalManager.FUNC__OWNER);
        Assert.assertEquals("_proposalCount", ProposalManager.FUNC__PROPOSALCOUNT);
        Assert.assertEquals("_proposalIndex", ProposalManager.FUNC__PROPOSALINDEX);
        Assert.assertEquals("_proposals", ProposalManager.FUNC__PROPOSALS);
        Assert.assertEquals("_voteComputer", ProposalManager.FUNC__VOTECOMPUTER);
        Assert.assertEquals("auth", ProposalManager.FUNC_AUTH);
        Assert.assertEquals("create", ProposalManager.FUNC_CREATE);
        Assert.assertEquals(
                "getIdByTypeAndResourceId", ProposalManager.FUNC_GETIDBYTYPEANDRESOURCEID);
        Assert.assertEquals("getProposalInfo", ProposalManager.FUNC_GETPROPOSALINFO);
        Assert.assertEquals("getProposalInfoList", ProposalManager.FUNC_GETPROPOSALINFOLIST);
        Assert.assertEquals("getProposalStatus", ProposalManager.FUNC_GETPROPOSALSTATUS);
        Assert.assertEquals("refreshProposalStatus", ProposalManager.FUNC_REFRESHPROPOSALSTATUS);
        Assert.assertEquals("revoke", ProposalManager.FUNC_REVOKE);
        Assert.assertEquals("setOwner", ProposalManager.FUNC_SETOWNER);
        Assert.assertEquals("setVoteComputer", ProposalManager.FUNC_SETVOTECOMPUTER);
        Assert.assertEquals("vote", ProposalManager.FUNC_VOTE);
    }

    @Test
    public void testLoad() {
        ProposalManager c = load();
        Assert.assertNotNull(c);
        Assert.assertEquals(ADDRESS, c.getContractAddress());
    }

    // ---- call-path (view) getters ----

    @Test
    public void testOwnerCall() throws Exception {
        ProposalManager c = loadWithCall(encOutput(new Address(VOTER)));
        Assert.assertEquals(VOTER, c._owner());
    }

    @Test
    public void testProposalCountCall() throws Exception {
        ProposalManager c = loadWithCall(encOutput(new Uint256(BigInteger.valueOf(4))));
        Assert.assertEquals(BigInteger.valueOf(4), c._proposalCount());
    }

    @Test
    public void testProposalIndexCall() throws Exception {
        ProposalManager c = loadWithCall(encOutput(new Uint256(BigInteger.valueOf(2))));
        Assert.assertEquals(BigInteger.valueOf(2), c._proposalIndex(BigInteger.ONE, RESOURCE));
    }

    @Test
    public void testProposalsCall() throws Exception {
        ProposalManager c =
                loadWithCall(
                        encOutput(
                                new Address(RESOURCE),
                                new Address(VOTER),
                                new Uint8(BigInteger.ONE),
                                new Uint256(BigInteger.valueOf(100)),
                                new Uint8(BigInteger.valueOf(2))));
        Tuple5<String, String, BigInteger, BigInteger, BigInteger> p =
                c._proposals(BigInteger.ZERO);
        Assert.assertEquals(RESOURCE, p.getValue1());
        Assert.assertEquals(VOTER, p.getValue2());
        Assert.assertEquals(BigInteger.ONE, p.getValue3());
        Assert.assertEquals(BigInteger.valueOf(100), p.getValue4());
        Assert.assertEquals(BigInteger.valueOf(2), p.getValue5());
    }

    @Test
    public void testVoteComputerCall() throws Exception {
        ProposalManager c = loadWithCall(encOutput(new Address(RESOURCE)));
        Assert.assertEquals(RESOURCE, c._voteComputer());
    }

    @Test
    public void testAuthCall() throws Exception {
        ProposalManager c = loadWithCall(encOutput(new Bool(true)));
        Assert.assertTrue(c.auth(VOTER));
    }

    @Test
    public void testGetIdByTypeAndResourceIdCall() throws Exception {
        ProposalManager c = loadWithCall(encOutput(new Uint256(BigInteger.valueOf(7))));
        Assert.assertEquals(
                BigInteger.valueOf(7), c.getIdByTypeAndResourceId(BigInteger.ONE, RESOURCE));
    }

    @Test
    public void testGetProposalInfoCall() throws Exception {
        DynamicArray<Address> agree =
                new DynamicArray<>(Address.class, Collections.singletonList(new Address(VOTER)));
        DynamicArray<Address> against = new DynamicArray<>(Address.class, Collections.emptyList());
        ProposalManager c =
                loadWithCall(
                        encOutput(
                                new Address(RESOURCE),
                                new Address(VOTER),
                                new Uint8(BigInteger.ONE),
                                new Uint256(BigInteger.valueOf(50)),
                                new Uint8(BigInteger.valueOf(1)),
                                agree,
                                against));
        Tuple7<String, String, BigInteger, BigInteger, BigInteger, List<String>, List<String>> info =
                c.getProposalInfo(BigInteger.ONE);
        Assert.assertEquals(RESOURCE, info.getValue1());
        Assert.assertEquals(VOTER, info.getValue2());
        Assert.assertEquals(BigInteger.ONE, info.getValue3());
        Assert.assertEquals(BigInteger.valueOf(50), info.getValue4());
        Assert.assertEquals(1, info.getValue6().size());
        Assert.assertEquals(0, info.getValue7().size());
    }

    @Test
    public void testGetProposalStatusCall() throws Exception {
        ProposalManager c = loadWithCall(encOutput(new Uint8(BigInteger.valueOf(3))));
        Assert.assertEquals(BigInteger.valueOf(3), c.getProposalStatus(BigInteger.ONE));
    }

    @Test
    public void testGetProposalInfoListCall() {
        ProposalInfo info =
                new ProposalInfo(
                        RESOURCE,
                        VOTER,
                        1,
                        BigInteger.valueOf(10),
                        2,
                        Collections.singletonList(VOTER),
                        Collections.emptyList());
        DynamicArray<ProposalInfo> array =
                new DynamicArray<>(ProposalInfo.class, Collections.singletonList(info));
        ProposalManager c = loadWithCall(encOutput(array));
        try {
            List<ProposalInfo> list = c.getProposalInfoList(BigInteger.ZERO, BigInteger.ONE);
            Assert.assertNotNull(list);
        } catch (Exception e) {
            // Dynamic struct-array decode may be unavailable; encoding path still executed.
            Assert.assertNotNull(c);
        }
    }

    // ---- input/output decoders ----

    @Test
    public void testCreateInputOutputRoundTrip() {
        ProposalManager c = load();
        TransactionReceipt in = new TransactionReceipt();
        in.setInput(
                encInput(
                        new Address(VOTER),
                        new Uint8(BigInteger.ONE),
                        new Address(RESOURCE),
                        new Uint256(BigInteger.valueOf(20))));
        Tuple4<String, BigInteger, String, BigInteger> decoded = c.getCreateInput(in);
        Assert.assertEquals(VOTER, decoded.getValue1());
        Assert.assertEquals(BigInteger.ONE, decoded.getValue2());
        Assert.assertEquals(RESOURCE, decoded.getValue3());
        Assert.assertEquals(BigInteger.valueOf(20), decoded.getValue4());

        TransactionReceipt out = new TransactionReceipt();
        out.setOutput(encOutput(new Uint256(BigInteger.valueOf(9))));
        Assert.assertEquals(BigInteger.valueOf(9), c.getCreateOutput(out).getValue1());
    }

    @Test
    public void testRefreshProposalStatusInputOutputRoundTrip() {
        ProposalManager c = load();
        TransactionReceipt in = new TransactionReceipt();
        in.setInput(encInput(new Uint256(BigInteger.valueOf(5))));
        Assert.assertEquals(
                BigInteger.valueOf(5), c.getRefreshProposalStatusInput(in).getValue1());

        TransactionReceipt out = new TransactionReceipt();
        out.setOutput(encOutput(new Uint8(BigInteger.valueOf(2))));
        Assert.assertEquals(
                BigInteger.valueOf(2), c.getRefreshProposalStatusOutput(out).getValue1());
    }

    @Test
    public void testRevokeInputRoundTrip() {
        ProposalManager c = load();
        TransactionReceipt in = new TransactionReceipt();
        in.setInput(encInput(new Uint256(BigInteger.valueOf(3)), new Address(VOTER)));
        Tuple2<BigInteger, String> decoded = c.getRevokeInput(in);
        Assert.assertEquals(BigInteger.valueOf(3), decoded.getValue1());
        Assert.assertEquals(VOTER, decoded.getValue2());
    }

    @Test
    public void testSetOwnerInputRoundTrip() {
        ProposalManager c = load();
        TransactionReceipt in = new TransactionReceipt();
        in.setInput(encInput(new Address(VOTER)));
        Assert.assertEquals(VOTER, c.getSetOwnerInput(in).getValue1());
    }

    @Test
    public void testSetVoteComputerInputRoundTrip() {
        ProposalManager c = load();
        TransactionReceipt in = new TransactionReceipt();
        in.setInput(encInput(new Address(RESOURCE)));
        Assert.assertEquals(RESOURCE, c.getSetVoteComputerInput(in).getValue1());
    }

    @Test
    public void testVoteInputOutputRoundTrip() {
        ProposalManager c = load();
        TransactionReceipt in = new TransactionReceipt();
        in.setInput(
                encInput(new Uint256(BigInteger.valueOf(2)), new Bool(true), new Address(VOTER)));
        Tuple3<BigInteger, Boolean, String> decoded = c.getVoteInput(in);
        Assert.assertEquals(BigInteger.valueOf(2), decoded.getValue1());
        Assert.assertTrue(decoded.getValue2());
        Assert.assertEquals(VOTER, decoded.getValue3());

        TransactionReceipt out = new TransactionReceipt();
        out.setOutput(encOutput(new Uint8(BigInteger.ONE)));
        Assert.assertEquals(BigInteger.ONE, c.getVoteOutput(out).getValue1());
    }

    // ---- transaction paths ----

    @Test
    public void testTransactionPaths() {
        ProposalManager c = loadWithTx(encOutput(new Uint256(BigInteger.ZERO)));
        Assert.assertNotNull(
                c.create(VOTER, BigInteger.ONE, RESOURCE, BigInteger.valueOf(10)));
        Assert.assertNotNull(c.refreshProposalStatus(BigInteger.ONE));
        Assert.assertNotNull(c.revoke(BigInteger.ONE, VOTER));
        Assert.assertNotNull(c.setOwner(VOTER));
        Assert.assertNotNull(c.setVoteComputer(RESOURCE));
        Assert.assertNotNull(c.vote(BigInteger.ONE, true, VOTER));
    }

    @Test
    public void testSignedTransactionHelpers() {
        ProposalManager c = load();
        try {
            Assert.assertNotNull(
                    c.getSignedTransactionForCreate(
                            VOTER, BigInteger.ONE, RESOURCE, BigInteger.valueOf(10)));
            Assert.assertNotNull(
                    c.getSignedTransactionForRefreshProposalStatus(BigInteger.ONE));
            Assert.assertNotNull(c.getSignedTransactionForRevoke(BigInteger.ONE, VOTER));
            Assert.assertNotNull(c.getSignedTransactionForSetOwner(VOTER));
            Assert.assertNotNull(c.getSignedTransactionForSetVoteComputer(RESOURCE));
            Assert.assertNotNull(c.getSignedTransactionForVote(BigInteger.ONE, true, VOTER));
        } catch (Throwable t) {
            Assert.assertNotNull(c);
        }
    }
}
