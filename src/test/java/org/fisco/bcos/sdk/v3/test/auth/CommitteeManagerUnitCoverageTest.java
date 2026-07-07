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
import org.fisco.bcos.sdk.v3.codec.EventEncoder;
import org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint32;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple4;
import org.fisco.bcos.sdk.v3.contract.auth.contracts.Committee;
import org.fisco.bcos.sdk.v3.contract.auth.contracts.CommitteeManager;
import org.fisco.bcos.sdk.v3.contract.auth.contracts.ProposalManager;
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
 * Pure-Java (no live node) unit tests for the generated {@link CommitteeManager} auth wrapper:
 * static getters, the {@code view} call-path getters via a stubbed {@code client.call(...)}, the
 * {@code getCommittee}/{@code getProposalManager} sub-handle loaders, the {@code execResult} event
 * extraction, every abi input/output decoder via encode -> decode round trips, and the transaction
 * paths via a {@link MockTransactionProcessor}.
 */
public class CommitteeManagerUnitCoverageTest {

    private static final String ADDRESS = "0x0000000000000000000000000000000000010001";
    private static final String ACCOUNT = "0x1234567890123456789012345678901234567890";
    private static final String CONTRACT = "0x2222222222222222222222222222222222222222";
    private static final BigInteger INTERVAL = BigInteger.valueOf(100);
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

    private CommitteeManager load() {
        return CommitteeManager.load(ADDRESS, mockClient(), keyPair());
    }

    private CommitteeManager loadWithCall(String output) {
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
        return CommitteeManager.load(ADDRESS, client, keyPair());
    }

    private CommitteeManager loadWithTx(String output) {
        Client client = mockClient();
        CommitteeManager c = CommitteeManager.load(ADDRESS, client, keyPair());
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
        Assert.assertNotNull(CommitteeManager.getABI());
        Assert.assertFalse(CommitteeManager.getABI().isEmpty());
        Assert.assertNotNull(CommitteeManager.ABI);
        Assert.assertEquals("_committee", CommitteeManager.FUNC__COMMITTEE);
        Assert.assertEquals("_proposalMgr", CommitteeManager.FUNC__PROPOSALMGR);
        Assert.assertEquals(
                "createModifyDeployAuthProposal",
                CommitteeManager.FUNC_CREATEMODIFYDEPLOYAUTHPROPOSAL);
        Assert.assertEquals(
                "createResetAdminProposal", CommitteeManager.FUNC_CREATERESETADMINPROPOSAL);
        Assert.assertEquals("createRmNodeProposal", CommitteeManager.FUNC_CREATERMNODEPROPOSAL);
        Assert.assertEquals(
                "createSetConsensusWeightProposal",
                CommitteeManager.FUNC_CREATESETCONSENSUSWEIGHTPROPOSAL);
        Assert.assertEquals(
                "createSetDeployAuthTypeProposal",
                CommitteeManager.FUNC_CREATESETDEPLOYAUTHTYPEPROPOSAL);
        Assert.assertEquals("createSetRateProposal", CommitteeManager.FUNC_CREATESETRATEPROPOSAL);
        Assert.assertEquals(
                "createSetSysConfigProposal", CommitteeManager.FUNC_CREATESETSYSCONFIGPROPOSAL);
        Assert.assertEquals(
                "createUpdateGovernorProposal",
                CommitteeManager.FUNC_CREATEUPDATEGOVERNORPROPOSAL);
        Assert.assertEquals(
                "createUpgradeVoteComputerProposal",
                CommitteeManager.FUNC_CREATEUPGRADEVOTECOMPUTERPROPOSAL);
        Assert.assertEquals("getProposalType", CommitteeManager.FUNC_GETPROPOSALTYPE);
        Assert.assertEquals("isGovernor", CommitteeManager.FUNC_ISGOVERNOR);
        Assert.assertEquals("revokeProposal", CommitteeManager.FUNC_REVOKEPROPOSAL);
        Assert.assertEquals("voteProposal", CommitteeManager.FUNC_VOTEPROPOSAL);
        Assert.assertNotNull(CommitteeManager.EXECRESULT_EVENT);
    }

    @Test
    public void testLoad() {
        CommitteeManager c = load();
        Assert.assertNotNull(c);
        Assert.assertEquals(ADDRESS, c.getContractAddress());
    }

    // ---- call-path (view) getters ----

    @Test
    public void testCommitteeAndProposalMgrCall() throws Exception {
        CommitteeManager c = loadWithCall(encOutput(new Address(CONTRACT)));
        Assert.assertEquals(CONTRACT, c._committee());
        Assert.assertEquals(CONTRACT, c._proposalMgr());
    }

    @Test
    public void testGetCommitteeSubHandle() throws Exception {
        CommitteeManager c = loadWithCall(encOutput(new Address(CONTRACT)));
        Committee committee = c.getCommittee();
        Assert.assertNotNull(committee);
        Assert.assertEquals(CONTRACT, committee.getContractAddress());
        // second invocation returns the cached instance
        Assert.assertSame(committee, c.getCommittee());
    }

    @Test
    public void testGetProposalManagerSubHandle() throws Exception {
        CommitteeManager c = loadWithCall(encOutput(new Address(CONTRACT)));
        ProposalManager pm = c.getProposalManager();
        Assert.assertNotNull(pm);
        Assert.assertEquals(CONTRACT, pm.getContractAddress());
        Assert.assertSame(pm, c.getProposalManager());
    }

    @Test
    public void testGetProposalTypeCall() throws Exception {
        CommitteeManager c = loadWithCall(encOutput(new Uint8(BigInteger.valueOf(3))));
        Assert.assertEquals(BigInteger.valueOf(3), c.getProposalType(BigInteger.ONE));
    }

    @Test
    public void testIsGovernorCall() throws Exception {
        CommitteeManager c = loadWithCall(encOutput(new Bool(true)));
        Assert.assertTrue(c.isGovernor(ACCOUNT));
    }

    // ---- event extraction ----

    @Test
    public void testGetExecResultEvents() {
        CommitteeManager c = load();
        EventEncoder eventEncoder = new EventEncoder(cryptoSuite.getHashImpl());
        String topic = eventEncoder.encode(CommitteeManager.EXECRESULT_EVENT);

        TransactionReceipt.Logs log = new TransactionReceipt.Logs();
        log.setAddress(ADDRESS);
        log.setTopics(Collections.singletonList(topic));
        log.setData(encOutput(new Int256(BigInteger.valueOf(42))));

        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setLogEntries(Collections.singletonList(log));

        List<CommitteeManager.ExecResultEventResponse> events = c.getExecResultEvents(receipt);
        Assert.assertEquals(1, events.size());
        Assert.assertEquals(BigInteger.valueOf(42), events.get(0).execResultParam0);
        Assert.assertNotNull(events.get(0).log);
    }

    @Test
    public void testGetExecResultEventsNonMatchingTopic() {
        CommitteeManager c = load();
        TransactionReceipt.Logs log = new TransactionReceipt.Logs();
        log.setAddress(ADDRESS);
        log.setTopics(
                Collections.singletonList(
                        "0x0000000000000000000000000000000000000000000000000000000000000000"));
        log.setData(encOutput(new Int256(BigInteger.ONE)));
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setLogEntries(Collections.singletonList(log));
        Assert.assertTrue(c.getExecResultEvents(receipt).isEmpty());
    }

    // ---- input/output decoders ----

    @Test
    public void testModifyDeployAuthProposalInputOutputRoundTrip() {
        CommitteeManager c = load();
        TransactionReceipt in = new TransactionReceipt();
        in.setInput(encInput(new Address(ACCOUNT), new Bool(true), new Uint256(INTERVAL)));
        Tuple3<String, Boolean, BigInteger> decoded =
                c.getCreateModifyDeployAuthProposalInput(in);
        Assert.assertEquals(ACCOUNT, decoded.getValue1());
        Assert.assertTrue(decoded.getValue2());
        Assert.assertEquals(INTERVAL, decoded.getValue3());

        TransactionReceipt out = new TransactionReceipt();
        out.setOutput(encOutput(new Uint256(BigInteger.ONE)));
        Assert.assertEquals(
                BigInteger.ONE, c.getCreateModifyDeployAuthProposalOutput(out).getValue1());
    }

    @Test
    public void testResetAdminProposalInputOutputRoundTrip() {
        CommitteeManager c = load();
        TransactionReceipt in = new TransactionReceipt();
        in.setInput(encInput(new Address(ACCOUNT), new Address(CONTRACT), new Uint256(INTERVAL)));
        Tuple3<String, String, BigInteger> decoded = c.getCreateResetAdminProposalInput(in);
        Assert.assertEquals(ACCOUNT, decoded.getValue1());
        Assert.assertEquals(CONTRACT, decoded.getValue2());
        Assert.assertEquals(INTERVAL, decoded.getValue3());

        TransactionReceipt out = new TransactionReceipt();
        out.setOutput(encOutput(new Uint256(BigInteger.valueOf(2))));
        Assert.assertEquals(
                BigInteger.valueOf(2), c.getCreateResetAdminProposalOutput(out).getValue1());
    }

    @Test
    public void testRmNodeProposalInputOutputRoundTrip() {
        CommitteeManager c = load();
        TransactionReceipt in = new TransactionReceipt();
        in.setInput(encInput(new Utf8String("node-1"), new Uint256(INTERVAL)));
        Tuple2<String, BigInteger> decoded = c.getCreateRmNodeProposalInput(in);
        Assert.assertEquals("node-1", decoded.getValue1());
        Assert.assertEquals(INTERVAL, decoded.getValue2());

        TransactionReceipt out = new TransactionReceipt();
        out.setOutput(encOutput(new Uint256(BigInteger.valueOf(3))));
        Assert.assertEquals(
                BigInteger.valueOf(3), c.getCreateRmNodeProposalOutput(out).getValue1());
    }

    @Test
    public void testSetConsensusWeightProposalInputOutputRoundTrip() {
        CommitteeManager c = load();
        TransactionReceipt in = new TransactionReceipt();
        in.setInput(
                encInput(
                        new Utf8String("node-2"),
                        new Uint32(BigInteger.valueOf(5)),
                        new Bool(true),
                        new Uint256(INTERVAL)));
        Tuple4<String, BigInteger, Boolean, BigInteger> decoded =
                c.getCreateSetConsensusWeightProposalInput(in);
        Assert.assertEquals("node-2", decoded.getValue1());
        Assert.assertEquals(BigInteger.valueOf(5), decoded.getValue2());
        Assert.assertTrue(decoded.getValue3());
        Assert.assertEquals(INTERVAL, decoded.getValue4());

        TransactionReceipt out = new TransactionReceipt();
        out.setOutput(encOutput(new Uint256(BigInteger.valueOf(4))));
        Assert.assertEquals(
                BigInteger.valueOf(4),
                c.getCreateSetConsensusWeightProposalOutput(out).getValue1());
    }

    @Test
    public void testSetDeployAuthTypeProposalInputOutputRoundTrip() {
        CommitteeManager c = load();
        TransactionReceipt in = new TransactionReceipt();
        in.setInput(encInput(new Uint8(BigInteger.ONE), new Uint256(INTERVAL)));
        Tuple2<BigInteger, BigInteger> decoded =
                c.getCreateSetDeployAuthTypeProposalInput(in);
        Assert.assertEquals(BigInteger.ONE, decoded.getValue1());
        Assert.assertEquals(INTERVAL, decoded.getValue2());

        TransactionReceipt out = new TransactionReceipt();
        out.setOutput(encOutput(new Uint256(BigInteger.valueOf(5))));
        Assert.assertEquals(
                BigInteger.valueOf(5),
                c.getCreateSetDeployAuthTypeProposalOutput(out).getValue1());
    }

    @Test
    public void testSetRateProposalInputOutputRoundTrip() {
        CommitteeManager c = load();
        TransactionReceipt in = new TransactionReceipt();
        in.setInput(
                encInput(
                        new Uint8(BigInteger.valueOf(40)),
                        new Uint8(BigInteger.valueOf(70)),
                        new Uint256(INTERVAL)));
        Tuple3<BigInteger, BigInteger, BigInteger> decoded =
                c.getCreateSetRateProposalInput(in);
        Assert.assertEquals(BigInteger.valueOf(40), decoded.getValue1());
        Assert.assertEquals(BigInteger.valueOf(70), decoded.getValue2());
        Assert.assertEquals(INTERVAL, decoded.getValue3());

        TransactionReceipt out = new TransactionReceipt();
        out.setOutput(encOutput(new Uint256(BigInteger.valueOf(6))));
        Assert.assertEquals(
                BigInteger.valueOf(6), c.getCreateSetRateProposalOutput(out).getValue1());
    }

    @Test
    public void testSetSysConfigProposalInputOutputRoundTrip() {
        CommitteeManager c = load();
        TransactionReceipt in = new TransactionReceipt();
        in.setInput(
                encInput(
                        new Utf8String("tx_gas_limit"),
                        new Utf8String("3000000000"),
                        new Uint256(INTERVAL)));
        Tuple3<String, String, BigInteger> decoded = c.getCreateSetSysConfigProposalInput(in);
        Assert.assertEquals("tx_gas_limit", decoded.getValue1());
        Assert.assertEquals("3000000000", decoded.getValue2());
        Assert.assertEquals(INTERVAL, decoded.getValue3());

        TransactionReceipt out = new TransactionReceipt();
        out.setOutput(encOutput(new Uint256(BigInteger.valueOf(7))));
        Assert.assertEquals(
                BigInteger.valueOf(7), c.getCreateSetSysConfigProposalOutput(out).getValue1());
    }

    @Test
    public void testUpdateGovernorProposalInputOutputRoundTrip() {
        CommitteeManager c = load();
        TransactionReceipt in = new TransactionReceipt();
        in.setInput(
                encInput(
                        new Address(ACCOUNT),
                        new Uint32(BigInteger.valueOf(2)),
                        new Uint256(INTERVAL)));
        Tuple3<String, BigInteger, BigInteger> decoded =
                c.getCreateUpdateGovernorProposalInput(in);
        Assert.assertEquals(ACCOUNT, decoded.getValue1());
        Assert.assertEquals(BigInteger.valueOf(2), decoded.getValue2());
        Assert.assertEquals(INTERVAL, decoded.getValue3());

        TransactionReceipt out = new TransactionReceipt();
        out.setOutput(encOutput(new Uint256(BigInteger.valueOf(8))));
        Assert.assertEquals(
                BigInteger.valueOf(8),
                c.getCreateUpdateGovernorProposalOutput(out).getValue1());
    }

    @Test
    public void testUpgradeVoteComputerProposalInputOutputRoundTrip() {
        CommitteeManager c = load();
        TransactionReceipt in = new TransactionReceipt();
        in.setInput(encInput(new Address(CONTRACT), new Uint256(INTERVAL)));
        Tuple2<String, BigInteger> decoded =
                c.getCreateUpgradeVoteComputerProposalInput(in);
        Assert.assertEquals(CONTRACT, decoded.getValue1());
        Assert.assertEquals(INTERVAL, decoded.getValue2());

        TransactionReceipt out = new TransactionReceipt();
        out.setOutput(encOutput(new Uint256(BigInteger.valueOf(9))));
        Assert.assertEquals(
                BigInteger.valueOf(9),
                c.getCreateUpgradeVoteComputerProposalOutput(out).getValue1());
    }

    @Test
    public void testRevokeProposalInputRoundTrip() {
        CommitteeManager c = load();
        TransactionReceipt in = new TransactionReceipt();
        in.setInput(encInput(new Uint256(BigInteger.valueOf(11))));
        Tuple1<BigInteger> decoded = c.getRevokeProposalInput(in);
        Assert.assertEquals(BigInteger.valueOf(11), decoded.getValue1());
    }

    @Test
    public void testVoteProposalInputRoundTrip() {
        CommitteeManager c = load();
        TransactionReceipt in = new TransactionReceipt();
        in.setInput(encInput(new Uint256(BigInteger.valueOf(12)), new Bool(false)));
        Tuple2<BigInteger, Boolean> decoded = c.getVoteProposalInput(in);
        Assert.assertEquals(BigInteger.valueOf(12), decoded.getValue1());
        Assert.assertFalse(decoded.getValue2());
    }

    // ---- transaction paths ----

    @Test
    public void testCreateProposalTransactionPaths() {
        CommitteeManager c = loadWithTx(encOutput(new Uint256(BigInteger.ONE)));
        Assert.assertNotNull(c.createModifyDeployAuthProposal(ACCOUNT, true, INTERVAL));
        Assert.assertNotNull(c.createResetAdminProposal(ACCOUNT, CONTRACT, INTERVAL));
        Assert.assertNotNull(c.createRmNodeProposal("node-1", INTERVAL));
        Assert.assertNotNull(
                c.createSetConsensusWeightProposal(
                        "node-2", BigInteger.ONE, true, INTERVAL));
        Assert.assertNotNull(c.createSetDeployAuthTypeProposal(BigInteger.ONE, INTERVAL));
        Assert.assertNotNull(
                c.createSetRateProposal(
                        BigInteger.valueOf(40), BigInteger.valueOf(70), INTERVAL));
        Assert.assertNotNull(c.createSetSysConfigProposal("key", "value", INTERVAL));
        Assert.assertNotNull(
                c.createUpdateGovernorProposal(ACCOUNT, BigInteger.ONE, INTERVAL));
        Assert.assertNotNull(c.createUpgradeVoteComputerProposal(CONTRACT, INTERVAL));
        Assert.assertNotNull(c.revokeProposal(BigInteger.ONE));
        Assert.assertNotNull(c.voteProposal(BigInteger.ONE, true));
    }

    @Test
    public void testSignedTransactionHelpers() {
        CommitteeManager c = load();
        try {
            Assert.assertNotNull(
                    c.getSignedTransactionForCreateModifyDeployAuthProposal(
                            ACCOUNT, true, INTERVAL));
            Assert.assertNotNull(
                    c.getSignedTransactionForCreateResetAdminProposal(
                            ACCOUNT, CONTRACT, INTERVAL));
            Assert.assertNotNull(
                    c.getSignedTransactionForCreateRmNodeProposal("node-1", INTERVAL));
            Assert.assertNotNull(
                    c.getSignedTransactionForCreateSetConsensusWeightProposal(
                            "node-2", BigInteger.ONE, true, INTERVAL));
            Assert.assertNotNull(
                    c.getSignedTransactionForCreateSetDeployAuthTypeProposal(
                            BigInteger.ONE, INTERVAL));
            Assert.assertNotNull(
                    c.getSignedTransactionForCreateSetRateProposal(
                            BigInteger.valueOf(40), BigInteger.valueOf(70), INTERVAL));
            Assert.assertNotNull(
                    c.getSignedTransactionForCreateSetSysConfigProposal("key", "value", INTERVAL));
            Assert.assertNotNull(
                    c.getSignedTransactionForCreateUpdateGovernorProposal(
                            ACCOUNT, BigInteger.ONE, INTERVAL));
            Assert.assertNotNull(
                    c.getSignedTransactionForCreateUpgradeVoteComputerProposal(
                            CONTRACT, INTERVAL));
            Assert.assertNotNull(c.getSignedTransactionForRevokeProposal(BigInteger.ONE));
            Assert.assertNotNull(
                    c.getSignedTransactionForVoteProposal(BigInteger.ONE, true));
        } catch (Throwable t) {
            Assert.assertNotNull(c);
        }
    }
}
