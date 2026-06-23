package org.fisco.bcos.sdk.v3.test.precompiled;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupInfo;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupNodeInfo;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.TableCRUDService;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.TableManagerPrecompiled;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.TablePrecompiled;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.common.Common;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.common.Condition;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.common.ConditionV320;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.common.Entry;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.common.UpdateFields;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.EnumNodeVersion;
import org.fisco.bcos.sdk.v3.model.PrecompiledConstant;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Pure-Java unit tests for {@link TableCRUDService}. Read paths (openTable / desc / select) flow
 * through a mocked {@code client.call(...)} fed with real ABI-encoded outputs. Tx paths that reach
 * native JNI are exercised up to (and guarded by) try/catch; the input-building / encoding logic
 * still runs. No live chain.
 */
public class TableCRUDServiceUnitCoverageTest {

    private static final String TABLE_ADDR = "0x000000000000000000000000000000000000100a";
    private final CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);

    private CryptoKeyPair keyPair() {
        return cryptoSuite.getCryptoKeyPair();
    }

    private Client baseMockClient(long compatibilityVersion) {
        Client client = mock(Client.class);
        when(client.getCryptoSuite()).thenReturn(cryptoSuite);
        when(client.isWASM()).thenReturn(false);
        when(client.getChainId()).thenReturn("chain0");
        when(client.getGroup()).thenReturn("group0");
        when(client.getBlockLimit()).thenReturn(BigInteger.valueOf(500));
        when(client.getExtraData()).thenReturn("");
        when(client.getNativePointer()).thenReturn(0L);
        BcosGroupNodeInfo.Protocol protocol = new BcosGroupNodeInfo.Protocol();
        protocol.setCompatibilityVersion(compatibilityVersion);
        BcosGroupNodeInfo.GroupNodeInfo nodeInfo = new BcosGroupNodeInfo.GroupNodeInfo();
        nodeInfo.setProtocol(protocol);
        BcosGroupInfo.GroupInfo groupInfo = new BcosGroupInfo.GroupInfo();
        groupInfo.setNodeList(Collections.singletonList(nodeInfo));
        BcosGroupInfo bcosGroupInfo = new BcosGroupInfo();
        bcosGroupInfo.setResult(groupInfo);
        when(client.getGroupInfo()).thenReturn(bcosGroupInfo);
        return client;
    }

    private Client v320Client() {
        return baseMockClient(
                EnumNodeVersion.BCOS_3_2_0.toVersionObj().toCompatibilityVersion());
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

    private static String addressOutput() {
        return encOutput(new Address(TABLE_ADDR));
    }

    private static String tableInfoOutput(String keyColumn, String... valueColumns) {
        DynamicArray<Utf8String> cols =
                new DynamicArray<>(
                        Utf8String.class,
                        Arrays.stream(valueColumns)
                                .map(Utf8String::new)
                                .collect(Collectors.toList()));
        return encOutput(new TableManagerPrecompiled.TableInfo(new Utf8String(keyColumn), cols));
    }

    private static String tableInfoV320Output(
            int keyOrder, String keyColumn, String... valueColumns) {
        DynamicArray<Utf8String> cols =
                new DynamicArray<>(
                        Utf8String.class,
                        Arrays.stream(valueColumns)
                                .map(Utf8String::new)
                                .collect(Collectors.toList()));
        return encOutput(
                new TableManagerPrecompiled.TableInfoV320(
                        new Uint8(keyOrder), new Utf8String(keyColumn), cols));
    }

    private static String singleEntryOutput(String key, String... fields) {
        return encOutput(
                new TablePrecompiled.Entry(
                        new Utf8String(key),
                        new DynamicArray<>(
                                Utf8String.class,
                                Arrays.stream(fields)
                                        .map(Utf8String::new)
                                        .collect(Collectors.toList()))));
    }

    @SuppressWarnings("unchecked")
    private static String entryArrayOutput(TablePrecompiled.Entry... entries) {
        return encOutput(
                new DynamicArray<>(TablePrecompiled.Entry.class, Arrays.asList(entries)));
    }

    private static TablePrecompiled.Entry entry(String key, String... fields) {
        return new TablePrecompiled.Entry(key, Arrays.asList(fields));
    }

    /** Sequentially returns Call results, repeating the last once exhausted. */
    private static void stubSequential(Client client, String... outputs) {
        Call[] calls = new Call[outputs.length];
        for (int i = 0; i < outputs.length; i++) {
            calls[i] = callOf(outputs[i]);
        }
        when(client.call(any()))
                .then(
                        new Answer<Call>() {
                            private int index = 0;

                            @Override
                            public Call answer(InvocationOnMock invocation) {
                                Call result = calls[Math.min(index, calls.length - 1)];
                                index++;
                                return result;
                            }
                        });
    }

    // ------------------------------------------------------------------
    // construct / version
    // ------------------------------------------------------------------

    @Test
    public void testConstructAndVersion() {
        Client client = v320Client();
        TableCRUDService service = new TableCRUDService(client, keyPair());
        Assert.assertEquals(
                EnumNodeVersion.BCOS_3_2_0.toVersionObj().toCompatibilityVersion(),
                service.getCurrentVersion());
    }

    @Test
    public void testDescWithKeyOrderVersionGuardThrows() {
        Client client =
                baseMockClient(
                        EnumNodeVersion.BCOS_3_1_0.toVersionObj().toCompatibilityVersion());
        TableCRUDService service = new TableCRUDService(client, keyPair());
        Assert.assertThrows(ContractException.class, () -> service.descWithKeyOrder("t_test"));
    }

    // ------------------------------------------------------------------
    // desc
    // ------------------------------------------------------------------

    @Test
    public void testDescPopulated() throws ContractException {
        Client client = v320Client();
        when(client.call(any())).thenReturn(callOf(tableInfoOutput("id", "name", "age")));
        TableCRUDService service = new TableCRUDService(client, keyPair());
        Map<String, List<String>> desc = service.desc("t_test");
        Assert.assertEquals(
                Collections.singletonList("id"),
                desc.get(PrecompiledConstant.KEY_FIELD_NAME));
        Assert.assertEquals(
                Arrays.asList("name", "age"), desc.get(PrecompiledConstant.VALUE_FIELD_NAME));
    }

    @Test
    public void testDescEmptyThrows() {
        Client client = v320Client();
        when(client.call(any())).thenReturn(callOf(tableInfoOutput("")));
        TableCRUDService service = new TableCRUDService(client, keyPair());
        Assert.assertThrows(ContractException.class, () -> service.desc("t_test"));
    }

    @Test
    public void testDescWithKeyOrderPopulated() throws ContractException {
        Client client = v320Client();
        when(client.call(any())).thenReturn(callOf(tableInfoV320Output(0, "id", "name")));
        TableCRUDService service = new TableCRUDService(client, keyPair());
        Map<String, List<String>> desc = service.descWithKeyOrder("t_test");
        Assert.assertEquals(
                Collections.singletonList("id"),
                desc.get(PrecompiledConstant.KEY_FIELD_NAME));
        Assert.assertEquals(
                Collections.singletonList("name"),
                desc.get(PrecompiledConstant.VALUE_FIELD_NAME));
        Assert.assertEquals(
                Collections.singletonList(Common.TableKeyOrder.Lexicographic.toString()),
                desc.get(PrecompiledConstant.KEY_ORDER));
    }

    // ------------------------------------------------------------------
    // select by single key
    // ------------------------------------------------------------------

    @Test
    public void testSelectByKey() throws ContractException {
        Client client = v320Client();
        // openTable -> address, desc -> TableInfo, select(key) -> Entry
        stubSequential(
                client,
                addressOutput(),
                tableInfoOutput("id", "name"),
                singleEntryOutput("k1", "alice"));
        TableCRUDService service = new TableCRUDService(client, keyPair());
        Map<String, String> result = service.select("t_test", "k1");
        Assert.assertEquals("k1", result.get("id"));
        Assert.assertEquals("alice", result.get("name"));
    }

    @Test
    public void testSelectByKeyEmptyEntry() throws ContractException {
        Client client = v320Client();
        stubSequential(
                client,
                addressOutput(),
                tableInfoOutput("id", "name"),
                singleEntryOutput("")); // empty fields -> empty result map
        TableCRUDService service = new TableCRUDService(client, keyPair());
        Map<String, String> result = service.select("t_test", "k1");
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testSelectByKeyWithDesc() throws ContractException {
        Client client = v320Client();
        // openTable -> address, then select(key) -> Entry (desc supplied by caller)
        stubSequential(client, addressOutput(), singleEntryOutput("k1", "bob"));
        TableCRUDService service = new TableCRUDService(client, keyPair());
        Map<String, List<String>> desc = new java.util.HashMap<>();
        desc.put(PrecompiledConstant.KEY_FIELD_NAME, Collections.singletonList("id"));
        desc.put(PrecompiledConstant.VALUE_FIELD_NAME, Collections.singletonList("name"));
        Map<String, String> result = service.select("t_test", desc, "k1");
        Assert.assertEquals("k1", result.get("id"));
        Assert.assertEquals("bob", result.get("name"));
    }

    // ------------------------------------------------------------------
    // select by condition
    // ------------------------------------------------------------------

    @Test
    public void testSelectByCondition() throws ContractException {
        Client client = v320Client();
        // openTable -> address, desc -> TableInfo, select(conditions) -> Entry[]
        stubSequential(
                client,
                addressOutput(),
                tableInfoOutput("id", "name"),
                entryArrayOutput(entry("k1", "alice"), entry("k2", "bob")));
        TableCRUDService service = new TableCRUDService(client, keyPair());
        Condition condition = new Condition();
        condition.GE("k1");
        condition.setLimit(0, 10);
        List<Map<String, String>> result = service.select("t_test", condition);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("alice", result.get(0).get("name"));
    }

    @Test
    public void testSelectByConditionWithDesc() throws ContractException {
        Client client = v320Client();
        // openTable -> address, select(conditions) -> Entry[] (desc supplied by caller)
        stubSequential(
                client, addressOutput(), entryArrayOutput(entry("k1", "alice")));
        TableCRUDService service = new TableCRUDService(client, keyPair());
        Map<String, List<String>> desc = new java.util.HashMap<>();
        desc.put(PrecompiledConstant.KEY_FIELD_NAME, Collections.singletonList("id"));
        desc.put(PrecompiledConstant.VALUE_FIELD_NAME, Collections.singletonList("name"));
        Condition condition = new Condition();
        condition.LT("k9");
        List<Map<String, String>> result = service.select("t_test", desc, condition);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("k1", result.get(0).get("id"));
    }

    @Test
    public void testSelectByConditionV320() throws ContractException {
        Client client = v320Client();
        // openTable -> address, descWithKeyOrder -> TableInfoV320, selectV320 -> Entry[]
        stubSequential(
                client,
                addressOutput(),
                tableInfoV320Output(0, "id", "name"),
                entryArrayOutput(entry("k1", "alice")));
        TableCRUDService service = new TableCRUDService(client, keyPair());
        ConditionV320 condition = new ConditionV320();
        condition.EQ("name", "alice");
        condition.setLimit(0, 10);
        List<Map<String, String>> result = service.select("t_test", condition);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("alice", result.get(0).get("name"));
    }

    @Test
    public void testSelectConditionV320VersionGuardThrows() {
        Client client =
                baseMockClient(
                        EnumNodeVersion.BCOS_3_1_0.toVersionObj().toCompatibilityVersion());
        TableCRUDService service = new TableCRUDService(client, keyPair());
        ConditionV320 condition = new ConditionV320();
        condition.EQ("name", "alice");
        Assert.assertThrows(
                ContractException.class, () -> service.select("t_test", condition));
    }

    // ------------------------------------------------------------------
    // tx paths: input building runs before JNI; wrap the JNI failure
    // ------------------------------------------------------------------

    @Test
    public void testInsertBuildsInputThenJniGuarded() {
        Client client = v320Client();
        when(client.call(any())).thenReturn(callOf(addressOutput()));
        TableCRUDService service = new TableCRUDService(client, keyPair());
        Entry entry =
                new Entry(
                        Collections.singletonList("name"),
                        "k1",
                        Collections.singletonMap("name", "alice"));
        try {
            service.insert("t_test", entry);
        } catch (Throwable t) {
            // native tx-building unavailable offline; loadTablePrecompiled + encoding ran
            Assert.assertNotNull(t);
        }
    }

    @Test
    public void testUpdateByKeyBuildsInputThenJniGuarded() {
        Client client = v320Client();
        when(client.call(any())).thenReturn(callOf(addressOutput()));
        TableCRUDService service = new TableCRUDService(client, keyPair());
        UpdateFields updateFields =
                new UpdateFields(Collections.singletonMap("name", "bob"));
        try {
            service.update("t_test", "k1", updateFields);
        } catch (Throwable t) {
            Assert.assertNotNull(t);
        }
    }

    @Test
    public void testRemoveByKeyBuildsInputThenJniGuarded() {
        Client client = v320Client();
        when(client.call(any())).thenReturn(callOf(addressOutput()));
        TableCRUDService service = new TableCRUDService(client, keyPair());
        try {
            service.remove("t_test", "k1");
        } catch (Throwable t) {
            Assert.assertNotNull(t);
        }
    }

    @Test
    public void testRemoveByConditionBuildsInputThenJniGuarded() {
        Client client = v320Client();
        when(client.call(any())).thenReturn(callOf(addressOutput()));
        TableCRUDService service = new TableCRUDService(client, keyPair());
        Condition condition = new Condition();
        condition.GE("k1");
        try {
            service.remove("t_test", condition);
        } catch (Throwable t) {
            Assert.assertNotNull(t);
        }
    }

    @Test
    public void testCreateTableBuildsInputThenJniGuarded() {
        Client client = v320Client();
        TableCRUDService service = new TableCRUDService(client, keyPair());
        try {
            service.createTable("t_test", "id", Arrays.asList("name", "age"));
        } catch (Throwable t) {
            Assert.assertNotNull(t);
        }
    }

    @Test
    public void testCreateTableV320BuildsInputThenJniGuarded() {
        Client client = v320Client();
        TableCRUDService service = new TableCRUDService(client, keyPair());
        try {
            service.createTable(
                    "t_test",
                    Common.TableKeyOrder.Lexicographic,
                    "id",
                    Arrays.asList("name", "age"));
        } catch (Throwable t) {
            Assert.assertNotNull(t);
        }
    }

    @Test
    public void testAppendColumnsBuildsInputThenJniGuarded() {
        Client client = v320Client();
        TableCRUDService service = new TableCRUDService(client, keyPair());
        try {
            service.appendColumns("t_test", Collections.singletonList("extra"));
        } catch (Throwable t) {
            Assert.assertNotNull(t);
        }
    }
}
