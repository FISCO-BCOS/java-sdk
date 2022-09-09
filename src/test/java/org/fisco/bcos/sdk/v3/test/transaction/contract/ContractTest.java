package org.fisco.bcos.sdk.v3.test.transaction.contract;

import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosTransactionReceipt;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.TransactionReceiptStatus;
import org.fisco.bcos.sdk.v3.test.transaction.mock.MockContract;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import java.math.BigInteger;
import java.util.Collections;

import static org.mockito.Mockito.*;

public class ContractTest {
    private Client mockClient;
    private final CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
    private MockContract mockContract;

    public ContractTest() {
        mockClient = mock(Client.class);
        when(mockClient.getChainId()).thenReturn("chain0");
        when(mockClient.getGroup()).thenReturn("group0");
        when(mockClient.getCryptoSuite()).thenReturn(cryptoSuite);
        when(mockClient.isWASM()).thenReturn(false);
        when(mockClient.getBlockLimit()).thenReturn(BigInteger.valueOf(500));
        mockContract = MockContract.load("", mockClient, mockClient.getCryptoSuite().getCryptoKeyPair());
    }

    public <R> R mockCallRequest(Function function, Class<R> returnType, String output, int status) throws ContractException {
        when(mockClient.call(any())).then((Answer<Call>) invocation -> {
            Call call = new Call();
            Call.CallOutput callOutput = new Call.CallOutput();
            callOutput.setOutput(output);
            callOutput.setStatus(status);
            call.setResult(callOutput);
            return call;
        });
        return mockContract.executeCallWithSingleValueReturn(function, returnType);
    }

    public TransactionReceipt mockSendTxRequest(Function function, String output, int status) {
        when(mockClient.sendTransaction(any(), anyBoolean())).then(
                invocation -> {
                    BcosTransactionReceipt transactionReceipt = new BcosTransactionReceipt();
                    TransactionReceipt mockReceipt = new TransactionReceipt();
                    mockReceipt.setOutput(output);
                    mockReceipt.setStatus(status);
                    transactionReceipt.setResult(mockReceipt);
                    return transactionReceipt;
                }
        );
        return mockContract.executeTransaction(function);
    }

    public MockContract mockDeploy(String output, int status, String address, boolean isWasm) throws ContractException {
        when(mockClient.sendTransaction(any(), anyBoolean())).then(
                invocation -> {
                    BcosTransactionReceipt transactionReceipt = new BcosTransactionReceipt();
                    TransactionReceipt mockReceipt = new TransactionReceipt();
                    mockReceipt.setOutput(output);
                    mockReceipt.setStatus(status);
                    mockReceipt.setContractAddress(address);
                    transactionReceipt.setResult(mockReceipt);
                    return transactionReceipt;
                }
        );
        when(mockClient.isWASM()).thenReturn(isWasm);
        return MockContract.deploy(mockClient, address);
    }

    public <R> R mockSuccessCallRequest(Function function, Class<R> returnType, String output) throws ContractException {
        return mockCallRequest(function, returnType, output, 0);
    }

    public TransactionReceipt mockSuccessSendTxRequest(Function function, String output) {
        return mockSendTxRequest(function, output, 0);
    }

    @Test
    public void executeCallTest() throws ContractException {
        final Function function =
                new Function(
                        "readlink",
                        Collections.emptyList(),
                        Collections.singletonList(new TypeReference<Address>() {
                        }));
        // empty output, throw
        Assert.assertThrows(ContractException.class, () -> mockSuccessCallRequest(function, String.class, ""));
        // get address success
        String address = mockSuccessCallRequest(function, String.class, "0x000000000000000000000000be5422d15f39373eb0a97ff8c10fbd0e40e29338");
        Assert.assertEquals("0xbe5422d15f39373eb0a97ff8c10fbd0e40e29338", address);

        final Function function2 =
                new Function(
                        "readlink",
                        Collections.emptyList(),
                        Collections.singletonList(new TypeReference<Utf8String>() {
                        }));
        // decode error
        Assert.assertThrows(ContractException.class, () -> mockCallRequest(function2, String.class, "0x000000000000000000000000be5422d15f39373eb0a97ff8c10fbd0e40e29338", 0));
        // status 15
        Assert.assertThrows(ContractException.class, () -> mockCallRequest(function2, String.class, "0x000000000000000000000000be5422d15f39373eb0a97ff8c10fbd0e40e29338", TransactionReceiptStatus.PrecompiledError.getCode()));
        try {
            mockCallRequest(function2, String.class, "0x000000000000000000000000be5422d15f39373eb0a97ff8c10fbd0e40e29338", TransactionReceiptStatus.PrecompiledError.getCode());
        } catch (ContractException exception) {
            Assert.assertEquals(TransactionReceiptStatus.PrecompiledError.getCode(), exception.getErrorCode());
            Assert.assertEquals(TransactionReceiptStatus.PrecompiledError.getMessage(), exception.getMessage());
        }
    }

    @Test
    public void sendTxTest() {
        final Function function =
                new Function(
                        "readlink",
                        Collections.emptyList(),
                        Collections.singletonList(new TypeReference<Address>() {
                        }));
        TransactionReceipt receipt = mockSuccessSendTxRequest(function, "");
        Assert.assertEquals(0, receipt.getStatus());
        Assert.assertEquals("", receipt.getOutput());

        TransactionReceipt receipt2 = mockSendTxRequest(function, "", 16);
        Assert.assertEquals(16, receipt2.getStatus());
        Assert.assertEquals("", receipt2.getOutput());
    }

    @Test
    public void deployTest() throws ContractException {
        MockContract mockContract = mockDeploy("", 0, "0x123", false);
        Assert.assertEquals("0x123", mockContract.getContractAddress());
        MockContract mockContract2 = mockDeploy("", 0, "/hello", true);
        Assert.assertEquals("/hello", mockContract2.getContractAddress());

        // receipt not contains contract
        Assert.assertThrows(ContractException.class, () -> mockDeploy("", 12, null, false));
        Assert.assertThrows(ContractException.class, () -> mockDeploy("", 12, "0x123", false));
        Assert.assertThrows(ContractException.class, () -> mockDeploy("", 12, "/hello", true));
    }
}
