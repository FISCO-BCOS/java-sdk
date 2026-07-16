package org.fisco.bcos.sdk.v3.test.precompiled;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.contract.precompiled.bfs.BFSInfo;
import org.fisco.bcos.sdk.v3.contract.precompiled.bfs.BFSPrecompiled;
import org.fisco.bcos.sdk.v3.contract.precompiled.bfs.BFSService;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.EnumNodeVersion;
import org.fisco.bcos.sdk.v3.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.test.transaction.mock.MockTransactionProcessor;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.stubbing.Answer;

/**
 * Pure-Java unit tests for {@link BFSService} that drive the read paths (via a mocked {@code
 * client.call(...)}) and the send-transaction paths (via an injected {@link
 * MockTransactionProcessor}). No live chain, no network.
 */
public class BFSServiceUnitCoverageTest {

    private final CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);

    private CryptoKeyPair keyPair() {
        return cryptoSuite.getCryptoKeyPair();
    }

    private Client baseMockClient() {
        Client client = mock(Client.class);
        when(client.getCryptoSuite()).thenReturn(cryptoSuite);
        when(client.isWASM()).thenReturn(false);
        when(client.getChainId()).thenReturn("chain0");
        when(client.getGroup()).thenReturn("group0");
        when(client.getBlockLimit()).thenReturn(BigInteger.valueOf(500));
        when(client.getExtraData()).thenReturn("");
        when(client.getNativePointer()).thenReturn(0L);
        when(client.getChainCompatibilityVersion())
                .thenReturn(EnumNodeVersion.BCOS_3_2_0.toVersionObj());
        return client;
    }

    private void stubCall(Client client, String output, int status) {
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

    private MockTransactionProcessor injectProcessor(
            Client client, BFSService service, int status, String output) {
        MockTransactionProcessor processor =
                new MockTransactionProcessor(
                        client, keyPair(), "group0", "chain0", "", status, output);
        service.getBfsPrecompiled().setTransactionProcessor(processor);
        return processor;
    }

    private static final String EMPTY_LIST_OUTPUT =
            // int256 code = 0, then offset to empty dynamic array of structs, then length 0
            "0x0000000000000000000000000000000000000000000000000000000000000000"
                    + "0000000000000000000000000000000000000000000000000000000000000040"
                    + "0000000000000000000000000000000000000000000000000000000000000000";

    @Test
    public void testReadlink() throws ContractException {
        Client client = baseMockClient();
        stubCall(
                client,
                "0x000000000000000000000000be5422d15f39373eb0a97ff8c10fbd0e40e29338",
                0);
        BFSService service = new BFSService(client, keyPair());
        String addr = service.readlink("/apps/Hello");
        Assert.assertEquals("0xbe5422d15f39373eb0a97ff8c10fbd0e40e29338", addr);
    }

    @Test
    public void testListWithPaging() throws ContractException {
        Client client = baseMockClient();
        stubCall(client, EMPTY_LIST_OUTPUT, 0);
        BFSService service = new BFSService(client, keyPair());
        Tuple2<BigInteger, List<BFSPrecompiled.BfsInfo>> out =
                service.list("/apps", BigInteger.ZERO, BigInteger.TEN);
        Assert.assertEquals(BigInteger.ZERO, out.getValue1());
        Assert.assertTrue(out.getValue2().isEmpty());
    }

    @Test
    public void testListBFSInfoWithPaging() throws ContractException {
        Client client = baseMockClient();
        stubCall(client, EMPTY_LIST_OUTPUT, 0);
        BFSService service = new BFSService(client, keyPair());
        Tuple2<BigInteger, List<BFSInfo>> out =
                service.listBFSInfo("/apps", BigInteger.ZERO, BigInteger.TEN);
        Assert.assertEquals(BigInteger.ZERO, out.getValue1());
        Assert.assertTrue(out.getValue2().isEmpty());
    }

    @Test
    public void testListVersionGuardThrows() {
        Client client = baseMockClient();
        when(client.getChainCompatibilityVersion())
                .thenReturn(EnumNodeVersion.BCOS_3_0_0.toVersionObj());
        BFSService service = new BFSService(client, keyPair());
        Assert.assertThrows(
                ContractException.class,
                () -> service.list("/apps", BigInteger.ZERO, BigInteger.TEN));
    }

    @Test
    public void testListDeprecatedSuccess() throws ContractException {
        Client client = baseMockClient();
        stubCall(client, EMPTY_LIST_OUTPUT, 0);
        BFSService service = new BFSService(client, keyPair());
        List<BFSPrecompiled.BfsInfo> list = service.list("/apps");
        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void testListBFSInfoDeprecatedSuccess() throws ContractException {
        Client client = baseMockClient();
        stubCall(client, EMPTY_LIST_OUTPUT, 0);
        BFSService service = new BFSService(client, keyPair());
        List<BFSInfo> list = service.listBFSInfo("/apps");
        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void testListDeprecatedNonZeroCodeThrows() {
        Client client = baseMockClient();
        // int256 code != 0 -> ContractException
        stubCall(
                client,
                "0xffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff30f7"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000000",
                0);
        BFSService service = new BFSService(client, keyPair());
        Assert.assertThrows(ContractException.class, () -> service.list("/"));
    }

    @Test
    public void testIsExistSystemPath() throws ContractException {
        Client client = baseMockClient();
        BFSService service = new BFSService(client, keyPair());
        BFSInfo info = service.isExist("/apps");
        Assert.assertNotNull(info);
        Assert.assertEquals("directory", info.getFileType());
    }

    @Test
    public void testIsExistEmptyListIsDirectory() throws ContractException {
        Client client = baseMockClient();
        // empty list (size != 1) -> treated as directory
        stubCall(client, EMPTY_LIST_OUTPUT, 0);
        BFSService service = new BFSService(client, keyPair());
        BFSInfo info = service.isExist("/data/foo");
        Assert.assertNotNull(info);
        Assert.assertEquals("directory", info.getFileType());
        Assert.assertEquals("foo", info.getFileName());
    }

    @Test
    public void testIsExistNotFoundReturnsNull() throws ContractException {
        Client client = baseMockClient();
        // negative code path -> returns null
        stubCall(
                client,
                "0xffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff30f7"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000000",
                0);
        BFSService service = new BFSService(client, keyPair());
        BFSInfo info = service.isExist("/data/missing");
        Assert.assertNull(info);
    }

    @Test
    public void testIsExistVersionGuardThrows() {
        Client client = baseMockClient();
        when(client.getChainCompatibilityVersion())
                .thenReturn(EnumNodeVersion.BCOS_3_0_0.toVersionObj());
        BFSService service = new BFSService(client, keyPair());
        Assert.assertThrows(ContractException.class, () -> service.isExist("/data/foo"));
    }

    @Test
    public void testMkdirSuccessViaProcessor() throws ContractException {
        Client client = baseMockClient();
        BFSService service = new BFSService(client, keyPair());
        // Int32 output = 0 (success)
        injectProcessor(
                client,
                service,
                0,
                "0x0000000000000000000000000000000000000000000000000000000000000000");
        RetCode ret = service.mkdir("/apps/newdir");
        Assert.assertEquals(PrecompiledRetCode.CODE_SUCCESS.code, ret.getCode());
    }

    @Test
    public void testLinkWithVersionSuccessViaProcessor() throws ContractException {
        Client client = baseMockClient();
        BFSService service = new BFSService(client, keyPair());
        injectProcessor(
                client,
                service,
                0,
                "0x0000000000000000000000000000000000000000000000000000000000000000");
        RetCode ret =
                service.link(
                        "name",
                        "ver",
                        "0x1234567890123456789012345678901234567890",
                        "abi");
        Assert.assertEquals(PrecompiledRetCode.CODE_SUCCESS.code, ret.getCode());
    }

    @Test
    public void testLinkSimpleSuccessViaProcessor() throws ContractException {
        Client client = baseMockClient();
        BFSService service = new BFSService(client, keyPair());
        injectProcessor(
                client,
                service,
                0,
                "0x0000000000000000000000000000000000000000000000000000000000000000");
        RetCode ret =
                service.link(
                        "/apps/Hello",
                        "0x1234567890123456789012345678901234567890",
                        "abi");
        Assert.assertEquals(PrecompiledRetCode.CODE_SUCCESS.code, ret.getCode());
    }

    @Test
    public void testFixBfsVersionGuardThrows() {
        Client client = baseMockClient();
        // current version 3.2.0 < V330_FIX_BFS_VERSION(3.3.0) -> guard throws
        BFSService service = new BFSService(client, keyPair());
        Assert.assertThrows(
                ContractException.class,
                () -> service.fixBfs(EnumNodeVersion.BCOS_3_3_0.toVersionObj()));
    }

    @Test
    public void testConstructorAndAccessors() {
        Client client = baseMockClient();
        BFSService service = new BFSService(client, keyPair());
        Assert.assertNotNull(service.getBfsPrecompiled());
        Assert.assertEquals(
                EnumNodeVersion.BCOS_3_2_0.toVersionObj(), service.getCurrentVersion());
        Assert.assertNotNull(service.getBfsPrecompiled().getContractAddress());
    }
}
