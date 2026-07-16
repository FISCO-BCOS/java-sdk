package org.fisco.bcos.sdk.v3.test.precompiled;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.KVTableService;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.TableManagerPrecompiled;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.PrecompiledConstant;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.stubbing.Answer;

/**
 * Pure-Java unit tests for {@link KVTableService}. Read paths (openTable + get + desc) flow through
 * a mocked {@code client.call(...)} fed with real ABI-encoded outputs. No live chain.
 */
public class KVTableServiceUnitCoverageTest {

    private static final String TABLE_ADDR = "0x000000000000000000000000000000000000100a";
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
        return client;
    }

    private static String encOutput(Type... values) {
        byte[] encoded = FunctionEncoder.encodeParameters(Arrays.asList(values), null);
        return "0x" + Hex.toHexString(encoded);
    }

    private static Call callOf(String output) {
        Call call = new Call();
        Call.CallOutput callOutput = new Call.CallOutput();
        callOutput.setOutput(output);
        callOutput.setStatus(0);
        call.setResult(callOutput);
        return call;
    }

    /** TableInfo struct output for desc(): keyColumn + valueColumns[]. */
    private static String tableInfoOutput(String keyColumn, String... valueColumns) {
        DynamicArray<Utf8String> cols =
                new DynamicArray<>(
                        Utf8String.class,
                        Arrays.stream(valueColumns)
                                .map(Utf8String::new)
                                .collect(java.util.stream.Collectors.toList()));
        return encOutput(new TableManagerPrecompiled.TableInfo(new Utf8String(keyColumn), cols));
    }

    private static String tableInfoV320Output(
            int keyOrder, String keyColumn, String... valueColumns) {
        DynamicArray<Utf8String> cols =
                new DynamicArray<>(
                        Utf8String.class,
                        Arrays.stream(valueColumns)
                                .map(Utf8String::new)
                                .collect(java.util.stream.Collectors.toList()));
        return encOutput(
                new TableManagerPrecompiled.TableInfoV320(
                        new Uint8(keyOrder), new Utf8String(keyColumn), cols));
    }

    @Test
    public void testConstruct() {
        Client client = baseMockClient();
        KVTableService service = new KVTableService(client, keyPair());
        Assert.assertNotNull(service);
    }

    @Test
    public void testCheckKeyOk() throws ContractException {
        Client client = baseMockClient();
        KVTableService service = new KVTableService(client, keyPair());
        service.checkKey("short");
    }

    @Test
    public void testCheckKeyTooLongThrows() {
        Client client = baseMockClient();
        KVTableService service = new KVTableService(client, keyPair());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            sb.append('x');
        }
        Assert.assertThrows(ContractException.class, () -> service.checkKey(sb.toString()));
    }

    @Test
    public void testCreateTableRejectsLongKeyField() {
        Client client = baseMockClient();
        KVTableService service = new KVTableService(client, keyPair());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            sb.append('k');
        }
        // checkKey runs before any JNI / network access
        Assert.assertThrows(
                ContractException.class,
                () -> service.createTable("t_test", sb.toString(), "value"));
    }

    @Test
    public void testDesc() throws ContractException {
        Client client = baseMockClient();
        // desc() issues a single openTable-less call: tableManagerPrecompiled.desc -> TableInfo
        when(client.call(any())).thenReturn(callOf(tableInfoOutput("id", "name")));
        KVTableService service = new KVTableService(client, keyPair());
        Map<String, String> desc = service.desc("t_test");
        Assert.assertEquals("id", desc.get(PrecompiledConstant.KEY_FIELD_NAME));
        Assert.assertEquals("name", desc.get(PrecompiledConstant.VALUE_FIELD_NAME));
    }

    @Test
    public void testDescWithKeyOrder() throws ContractException {
        Client client = baseMockClient();
        when(client.call(any())).thenReturn(callOf(tableInfoV320Output(0, "id", "name")));
        KVTableService service = new KVTableService(client, keyPair());
        Map<String, String> desc = service.descWithKeyOrder("t_test");
        Assert.assertEquals("id", desc.get(PrecompiledConstant.KEY_FIELD_NAME));
        Assert.assertEquals("name", desc.get(PrecompiledConstant.VALUE_FIELD_NAME));
    }

    @Test
    public void testGetFound() throws ContractException {
        Client client = baseMockClient();
        // first call: openTable -> Address; second call: get -> (bool true, string value)
        when(client.call(any()))
                .then(
                        (Answer<Call>)
                                new SequentialCallAnswer(
                                        callOf(encOutput(new Address(TABLE_ADDR))),
                                        callOf(
                                                encOutput(
                                                        new Bool(true),
                                                        new Utf8String("hello")))));
        KVTableService service = new KVTableService(client, keyPair());
        String value = service.get("t_test", "k1");
        Assert.assertEquals("hello", value);
    }

    @Test
    public void testGetNotFoundThrows() {
        Client client = baseMockClient();
        when(client.call(any()))
                .then(
                        (Answer<Call>)
                                new SequentialCallAnswer(
                                        callOf(encOutput(new Address(TABLE_ADDR))),
                                        callOf(
                                                encOutput(
                                                        new Bool(false),
                                                        new Utf8String("")))));
        KVTableService service = new KVTableService(client, keyPair());
        Assert.assertThrows(ContractException.class, () -> service.get("t_test", "missing"));
    }

    @Test
    public void testGetOpenTableMalformedOutputThrows() {
        Client client = baseMockClient();
        // openTable returns an undecodable output -> ContractException in loadKVTablePrecompiled
        when(client.call(any())).thenReturn(callOf("0x"));
        KVTableService service = new KVTableService(client, keyPair());
        Assert.assertThrows(ContractException.class, () -> service.get("t_test", "k1"));
    }

    /** Returns each supplied Call result in turn, repeating the last once exhausted. */
    private static final class SequentialCallAnswer implements Answer<Call> {
        private final Call[] results;
        private int index = 0;

        SequentialCallAnswer(Call... results) {
            this.results = results;
        }

        @Override
        public Call answer(org.mockito.invocation.InvocationOnMock invocation) {
            Call result = results[Math.min(index, results.length - 1)];
            index++;
            return result;
        }
    }
}
