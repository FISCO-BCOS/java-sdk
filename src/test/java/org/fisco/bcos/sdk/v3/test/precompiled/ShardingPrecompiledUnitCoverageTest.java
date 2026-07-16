package org.fisco.bcos.sdk.v3.test.precompiled;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.contract.precompiled.sharding.ShardingPrecompiled;
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
 * Pure-Java unit tests for {@link ShardingPrecompiled}. The {@link Client} is fully mocked. Input
 * and output decoders are round-tripped against ABI-encoded values from the wrapper's own {@code
 * functionEncoder}; {@code getContractShard} (a view call) is exercised by stubbing {@code
 * client.call(...)}. No live node is contacted.
 */
public class ShardingPrecompiledUnitCoverageTest {

    private static final String ADDRESS = "0x0000000000000000000000000000000000001005";
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

    private ShardingPrecompiled load(Client client) {
        return ShardingPrecompiled.load(ADDRESS, client, keyPair());
    }

    private String hex(byte[] data) {
        return Hex.toHexStringWithPrefix(data);
    }

    @Test
    public void testStaticGettersAndLoad() {
        Assert.assertNotNull(ShardingPrecompiled.getABI());
        Assert.assertFalse(ShardingPrecompiled.getABI().isEmpty());
        Assert.assertNotNull(ShardingPrecompiled.getBinary(cryptoSuite));
        Assert.assertNotNull(ShardingPrecompiled.getBinary(new CryptoSuite(CryptoType.SM_TYPE)));
        Assert.assertNotNull(load(mockClient()));
    }

    @Test
    public void testGetContractShardCall() throws ContractException {
        Client client = mockClient();
        // tuple (int256 code = 0, string "shardX") with dynamic string at offset 0x40
        stubCall(
                client,
                "0x0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000006"
                        + "7368617264580000000000000000000000000000000000000000000000000000");
        ShardingPrecompiled c = load(client);
        Tuple2<BigInteger, String> result = c.getContractShard("/apps/Hello");
        Assert.assertEquals(BigInteger.ZERO, result.getValue1());
        Assert.assertEquals("shardX", result.getValue2());
    }

    @Test
    public void testLinkShardInputRoundTrip() {
        ShardingPrecompiled c = load(mockClient());
        Function function =
                new Function(
                        ShardingPrecompiled.FUNC_LINKSHARD,
                        Arrays.<Type>asList(new Utf8String("shardA"), new Utf8String("0xabc")),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setInput(hex(c.functionEncoder.encode(function)));
        Tuple2<String, String> in = c.getLinkShardInput(receipt);
        Assert.assertEquals("shardA", in.getValue1());
        Assert.assertEquals("0xabc", in.getValue2());
    }

    @Test
    public void testLinkShardOutputRoundTrip() {
        ShardingPrecompiled c = load(mockClient());
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setOutput(
                hex(
                        FunctionEncoder.encodeParameters(
                                Arrays.<Type>asList(new Int256(BigInteger.ONE)), null)));
        Tuple1<BigInteger> out = c.getLinkShardOutput(receipt);
        Assert.assertEquals(BigInteger.ONE, out.getValue1());
    }

    @Test
    public void testMakeShardInputRoundTrip() {
        ShardingPrecompiled c = load(mockClient());
        Function function =
                new Function(
                        ShardingPrecompiled.FUNC_MAKESHARD,
                        Arrays.<Type>asList(new Utf8String("shardB")),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setInput(hex(c.functionEncoder.encode(function)));
        Tuple1<String> in = c.getMakeShardInput(receipt);
        Assert.assertEquals("shardB", in.getValue1());
    }

    @Test
    public void testMakeShardOutputRoundTrip() {
        ShardingPrecompiled c = load(mockClient());
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setOutput(
                hex(
                        FunctionEncoder.encodeParameters(
                                Arrays.<Type>asList(new Int256(BigInteger.valueOf(-1))), null)));
        Tuple1<BigInteger> out = c.getMakeShardOutput(receipt);
        Assert.assertEquals(BigInteger.valueOf(-1), out.getValue1());
    }
}
