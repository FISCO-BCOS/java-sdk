package org.fisco.bcos.sdk.v3.test.precompiled;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.contract.precompiled.balance.BalanceService;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.EnumNodeVersion;
import org.fisco.bcos.sdk.v3.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.test.transaction.mock.MockTransactionProcessor;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.transaction.tools.Convert;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.stubbing.Answer;

/**
 * Pure-Java unit tests for {@link BalanceService}. Read paths flow through a mocked {@code
 * client.call(...)}; send-transaction paths flow through an injected {@link
 * MockTransactionProcessor}. No live chain.
 */
public class BalanceServiceUnitCoverageTest {

    private final CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);

    private static final String ADDR = "0x0000000000000000000000000000000000000001";

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
                .thenReturn(EnumNodeVersion.BCOS_3_6_0.toVersionObj());
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

    private void injectProcessor(
            Client client, BalanceService service, int status, String output) {
        MockTransactionProcessor processor =
                new MockTransactionProcessor(
                        client, keyPair(), "group0", "chain0", "", status, output);
        service.getBalancePrecompiled().setTransactionProcessor(processor);
    }

    @Test
    public void testConstructorAndVersion() {
        Client client = baseMockClient();
        BalanceService service = new BalanceService(client, keyPair());
        Assert.assertNotNull(service.getBalancePrecompiled());
        Assert.assertEquals(
                EnumNodeVersion.BCOS_3_6_0.toVersionObj(), service.getCurrentVersion());
    }

    @Test
    public void testGetBalance() throws ContractException {
        Client client = baseMockClient();
        // uint256 = 0x3039 = 12345
        stubCall(
                client,
                "0x0000000000000000000000000000000000000000000000000000000000003039",
                0);
        BalanceService service = new BalanceService(client, keyPair());
        Assert.assertEquals(BigInteger.valueOf(12345), service.getBalance(ADDR));
    }

    @Test
    public void testGetBalanceNonZeroStatusThrows() {
        Client client = baseMockClient();
        stubCall(client, "0x", 15);
        BalanceService service = new BalanceService(client, keyPair());
        Assert.assertThrows(ContractException.class, () -> service.getBalance(ADDR));
    }

    @Test
    public void testListCallerEmpty() throws ContractException {
        Client client = baseMockClient();
        // dynamic address[]: offset 0x20, length 0
        stubCall(
                client,
                "0x0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000000",
                0);
        BalanceService service = new BalanceService(client, keyPair());
        List<String> callers = service.listCaller();
        Assert.assertNotNull(callers);
        Assert.assertTrue(callers.isEmpty());
    }

    @Test
    public void testListCallerOneEntry() throws ContractException {
        Client client = baseMockClient();
        // address[] with one element
        stubCall(
                client,
                "0x0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "000000000000000000000000be5422d15f39373eb0a97ff8c10fbd0e40e29338",
                0);
        BalanceService service = new BalanceService(client, keyPair());
        List<String> callers = service.listCaller();
        Assert.assertEquals(1, callers.size());
        Assert.assertEquals("0xbe5422d15f39373eb0a97ff8c10fbd0e40e29338", callers.get(0));
    }

    @Test
    public void testAddBalanceSuccess() throws ContractException {
        Client client = baseMockClient();
        BalanceService service = new BalanceService(client, keyPair());
        injectProcessor(
                client,
                service,
                0,
                "0x0000000000000000000000000000000000000000000000000000000000000000");
        RetCode ret = service.addBalance(ADDR, "1", Convert.Unit.WEI);
        Assert.assertEquals(PrecompiledRetCode.CODE_SUCCESS.getCode(), ret.getCode());
    }

    @Test
    public void testSubBalanceSuccess() throws ContractException {
        Client client = baseMockClient();
        BalanceService service = new BalanceService(client, keyPair());
        injectProcessor(
                client,
                service,
                0,
                "0x0000000000000000000000000000000000000000000000000000000000000000");
        RetCode ret = service.subBalance(ADDR, "1", Convert.Unit.WEI);
        Assert.assertEquals(PrecompiledRetCode.CODE_SUCCESS.getCode(), ret.getCode());
    }

    @Test
    public void testTransferSuccess() throws ContractException {
        Client client = baseMockClient();
        BalanceService service = new BalanceService(client, keyPair());
        injectProcessor(
                client,
                service,
                0,
                "0x0000000000000000000000000000000000000000000000000000000000000000");
        RetCode ret =
                service.transfer(
                        ADDR, "0x0000000000000000000000000000000000000002", "1", Convert.Unit.WEI);
        Assert.assertEquals(PrecompiledRetCode.CODE_SUCCESS.getCode(), ret.getCode());
    }

    @Test
    public void testRegisterCallerSuccess() throws ContractException {
        Client client = baseMockClient();
        BalanceService service = new BalanceService(client, keyPair());
        injectProcessor(
                client,
                service,
                0,
                "0x0000000000000000000000000000000000000000000000000000000000000000");
        RetCode ret = service.registerCaller(ADDR);
        Assert.assertEquals(PrecompiledRetCode.CODE_SUCCESS.getCode(), ret.getCode());
    }

    @Test
    public void testUnregisterCallerSuccess() throws ContractException {
        Client client = baseMockClient();
        BalanceService service = new BalanceService(client, keyPair());
        injectProcessor(
                client,
                service,
                0,
                "0x0000000000000000000000000000000000000000000000000000000000000000");
        RetCode ret = service.unregisterCaller(ADDR);
        Assert.assertEquals(PrecompiledRetCode.CODE_SUCCESS.getCode(), ret.getCode());
    }

    @Test
    public void testRegisterCallerAsyncDeliversRetCode() throws ContractException {
        Client client = baseMockClient();
        BalanceService service = new BalanceService(client, keyPair());
        injectProcessor(
                client,
                service,
                0,
                "0x0000000000000000000000000000000000000000000000000000000000000000");
        AtomicReference<RetCode> captured = new AtomicReference<>();
        service.registerCallerAsync(ADDR, captured::set);
        Assert.assertNotNull(captured.get());
        Assert.assertEquals(PrecompiledRetCode.CODE_SUCCESS.getCode(), captured.get().getCode());
    }

    @Test
    public void testAddBalanceAsyncDeliversRetCode() throws ContractException {
        Client client = baseMockClient();
        BalanceService service = new BalanceService(client, keyPair());
        injectProcessor(
                client,
                service,
                0,
                "0x0000000000000000000000000000000000000000000000000000000000000000");
        AtomicReference<RetCode> captured = new AtomicReference<>();
        service.addBalanceAsync(ADDR, "1", Convert.Unit.WEI, captured::set);
        Assert.assertNotNull(captured.get());
        Assert.assertEquals(PrecompiledRetCode.CODE_SUCCESS.getCode(), captured.get().getCode());
    }
}
