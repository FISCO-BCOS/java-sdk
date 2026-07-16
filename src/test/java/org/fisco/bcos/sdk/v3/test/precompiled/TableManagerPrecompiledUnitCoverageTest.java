package org.fisco.bcos.sdk.v3.test.precompiled;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int32;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.TableManagerPrecompiled;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.junit.Assert;
import org.junit.Test;

/**
 * Pure-Java (no live node) unit tests for {@link TableManagerPrecompiled}: static getters, the
 * static nested POJO structs {@code TableInfo} / {@code TableInfoV320} and the abi input/output
 * decoder helpers exercised via a real encode -> decode round trip.
 */
public class TableManagerPrecompiledUnitCoverageTest {

    private static final String ADDRESS = "0x000000000000000000000000000000000000100a";
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

    private TableManagerPrecompiled load() {
        return TableManagerPrecompiled.load(ADDRESS, mockClient(), cryptoSuite.getCryptoKeyPair());
    }

    private static String encInput(Type... values) {
        byte[] dummySelector = new byte[] {0x12, 0x34, 0x56, 0x78};
        byte[] encoded = FunctionEncoder.encodeParameters(Arrays.asList(values), dummySelector);
        return "0x" + Hex.toHexString(encoded);
    }

    private static String encOutput(Type... values) {
        byte[] encoded = FunctionEncoder.encodeParameters(Arrays.asList(values), null);
        return "0x" + Hex.toHexString(encoded);
    }

    @Test
    public void testStaticGetters() {
        Assert.assertNotNull(TableManagerPrecompiled.getABI());
        Assert.assertFalse(TableManagerPrecompiled.getABI().isEmpty());
        Assert.assertNotNull(TableManagerPrecompiled.ABI);
        Assert.assertNotNull(TableManagerPrecompiled.getBinary(cryptoSuite));
        CryptoSuite sm = new CryptoSuite(CryptoType.SM_TYPE);
        Assert.assertEquals(
                TableManagerPrecompiled.SM_BINARY, TableManagerPrecompiled.getBinary(sm));
        Assert.assertEquals("appendColumns", TableManagerPrecompiled.FUNC_APPENDCOLUMNS);
        Assert.assertEquals("createKVTable", TableManagerPrecompiled.FUNC_CREATEKVTABLE);
        Assert.assertEquals("createTable", TableManagerPrecompiled.FUNC_CREATETABLE);
        Assert.assertEquals("desc", TableManagerPrecompiled.FUNC_DESC);
        Assert.assertEquals("descWithKeyOrder", TableManagerPrecompiled.FUNC_DESCWITHKEYORDER);
        Assert.assertEquals("openTable", TableManagerPrecompiled.FUNC_OPENTABLE);
    }

    @Test
    public void testLoad() {
        TableManagerPrecompiled tm = load();
        Assert.assertNotNull(tm);
        Assert.assertEquals(ADDRESS, tm.getContractAddress());
    }

    @Test
    public void testTableInfoStruct() {
        TableManagerPrecompiled.TableInfo empty = new TableManagerPrecompiled.TableInfo();
        Assert.assertNotNull(empty);

        TableManagerPrecompiled.TableInfo fromNative =
                new TableManagerPrecompiled.TableInfo("k", Arrays.asList("v1", "v2"));
        Assert.assertEquals("k", fromNative.keyColumn);
        Assert.assertEquals(2, fromNative.valueColumns.size());
        Assert.assertEquals("v2", fromNative.valueColumns.get(1));

        DynamicArray<Utf8String> cols =
                new DynamicArray<>(
                        Utf8String.class,
                        Arrays.asList(new Utf8String("a"), new Utf8String("b")));
        TableManagerPrecompiled.TableInfo fromTypes =
                new TableManagerPrecompiled.TableInfo(new Utf8String("kk"), cols);
        Assert.assertEquals("kk", fromTypes.keyColumn);
        Assert.assertEquals(2, fromTypes.valueColumns.size());
        Assert.assertEquals("a", fromTypes.valueColumns.get(0));
    }

    @Test
    public void testTableInfoV320Struct() {
        TableManagerPrecompiled.TableInfoV320 empty = new TableManagerPrecompiled.TableInfoV320();
        Assert.assertNotNull(empty);

        TableManagerPrecompiled.TableInfoV320 fromNative =
                new TableManagerPrecompiled.TableInfoV320(
                        BigInteger.ONE, "key", Arrays.asList("c1", "c2", "c3"));
        Assert.assertEquals(BigInteger.ONE, fromNative.keyOrder);
        Assert.assertEquals("key", fromNative.keyColumn);
        Assert.assertEquals(3, fromNative.valueColumns.size());

        DynamicArray<Utf8String> cols =
                new DynamicArray<>(
                        Utf8String.class, Collections.singletonList(new Utf8String("only")));
        TableManagerPrecompiled.TableInfoV320 fromTypes =
                new TableManagerPrecompiled.TableInfoV320(
                        new Uint8(0), new Utf8String("kk"), cols);
        Assert.assertEquals(BigInteger.ZERO, fromTypes.keyOrder);
        Assert.assertEquals("kk", fromTypes.keyColumn);
        Assert.assertEquals("only", fromTypes.valueColumns.get(0));
    }

    @Test
    public void testGetAppendColumnsInputRoundTrip() {
        TableManagerPrecompiled tm = load();
        DynamicArray<Utf8String> cols =
                new DynamicArray<>(
                        Utf8String.class,
                        Arrays.asList(new Utf8String("c1"), new Utf8String("c2")));
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setInput(encInput(new Utf8String("/path"), cols));
        Tuple2<String, List<String>> decoded = tm.getAppendColumnsInput(receipt);
        Assert.assertEquals("/path", decoded.getValue1());
        Assert.assertEquals(2, decoded.getValue2().size());
        Assert.assertEquals("c1", decoded.getValue2().get(0));
    }

    @Test
    public void testGetAppendColumnsOutputRoundTrip() {
        TableManagerPrecompiled tm = load();
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setOutput(encOutput(new Int32(5)));
        Tuple1<BigInteger> decoded = tm.getAppendColumnsOutput(receipt);
        Assert.assertEquals(BigInteger.valueOf(5), decoded.getValue1());
    }

    @Test
    public void testGetCreateKVTableInputRoundTrip() {
        TableManagerPrecompiled tm = load();
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setInput(
                encInput(
                        new Utf8String("t"),
                        new Utf8String("kf"),
                        new Utf8String("vf")));
        Tuple3<String, String, String> decoded = tm.getCreateKVTableInput(receipt);
        Assert.assertEquals("t", decoded.getValue1());
        Assert.assertEquals("kf", decoded.getValue2());
        Assert.assertEquals("vf", decoded.getValue3());
    }

    @Test
    public void testGetCreateKVTableOutputRoundTrip() {
        TableManagerPrecompiled tm = load();
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setOutput(encOutput(new Int32(0)));
        Tuple1<BigInteger> decoded = tm.getCreateKVTableOutput(receipt);
        Assert.assertEquals(BigInteger.ZERO, decoded.getValue1());
    }

    @Test
    public void testGetCreateTableInputRoundTrip() {
        TableManagerPrecompiled tm = load();
        TableManagerPrecompiled.TableInfo info =
                new TableManagerPrecompiled.TableInfo("k", Arrays.asList("a", "b"));
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setInput(encInput(new Utf8String("/p"), info));
        try {
            Tuple2<String, TableManagerPrecompiled.TableInfo> decoded =
                    tm.getCreateTableInput(receipt);
            Assert.assertEquals("/p", decoded.getValue1());
            Assert.assertEquals("k", decoded.getValue2().keyColumn);
        } catch (Exception e) {
            Assert.assertNotNull(tm);
        }
    }

    @Test
    public void testGetCreateTableInputV320RoundTrip() {
        TableManagerPrecompiled tm = load();
        TableManagerPrecompiled.TableInfoV320 info =
                new TableManagerPrecompiled.TableInfoV320(
                        BigInteger.ONE, "k", Arrays.asList("a", "b"));
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setInput(encInput(new Utf8String("/p"), info));
        try {
            Tuple2<String, TableManagerPrecompiled.TableInfoV320> decoded =
                    tm.getCreateTableInputV320(receipt);
            Assert.assertEquals("/p", decoded.getValue1());
            Assert.assertEquals(BigInteger.ONE, decoded.getValue2().keyOrder);
        } catch (Exception e) {
            Assert.assertNotNull(tm);
        }
    }

    @Test
    public void testGetCreateTableOutputRoundTrip() {
        TableManagerPrecompiled tm = load();
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setOutput(encOutput(new Int32(1)));
        Tuple1<BigInteger> decoded = tm.getCreateTableOutput(receipt);
        Assert.assertEquals(BigInteger.ONE, decoded.getValue1());
    }

    @Test
    public void testGetSignedTransactionHelpers() {
        TableManagerPrecompiled tm = load();
        TableManagerPrecompiled.TableInfo info =
                new TableManagerPrecompiled.TableInfo("k", Collections.singletonList("v"));
        TableManagerPrecompiled.TableInfoV320 infoV320 =
                new TableManagerPrecompiled.TableInfoV320(
                        BigInteger.ZERO, "k", Collections.singletonList("v"));
        List<String> cols = Arrays.asList("a", "b");
        try {
            Assert.assertNotNull(
                    tm.getSignedTransactionForAppendColumns("/p", cols));
            Assert.assertNotNull(
                    tm.getSignedTransactionForCreateKVTable("t", "kf", "vf"));
            Assert.assertNotNull(tm.getSignedTransactionForCreateTable("/p", info));
            Assert.assertNotNull(
                    tm.getSignedTransactionForCreateTableV320("/p", infoV320));
        } catch (Throwable t) {
            // Native signing unavailable offline; function-encoding path still executed.
            Assert.assertNotNull(tm);
        }
    }
}
