/**
 * Copyright 2014-2020 [fisco-dev]
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fisco.bcos.sdk.v3.test.precompiled;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.contract.precompiled.bfs.BFSInfo;
import org.fisco.bcos.sdk.v3.contract.precompiled.bfs.BFSPrecompiled;
import org.fisco.bcos.sdk.v3.contract.precompiled.bfs.BFSUtils;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.TablePrecompiled;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.common.Common;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.common.Condition;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.common.ConditionOperator;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.common.ConditionV320;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.common.Entry;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.common.UpdateFields;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.PrecompiledVersionCheck;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.Version;
import org.fisco.bcos.sdk.v3.contract.precompiled.sysconfig.SystemConfigFeature;
import org.fisco.bcos.sdk.v3.model.EnumNodeVersion;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.junit.Assert;
import org.junit.Test;

public class PrecompiledPureLogicCoverageTest {

    // ---------------------------------------------------------------
    // Condition
    // ---------------------------------------------------------------

    @Test
    public void testConditionComparators() {
        Condition condition = new Condition();
        condition.GT("1");
        condition.GE("2");
        condition.LT("10");
        condition.LE("9");
        condition.EQ("5");

        Map<ConditionOperator, String> conditions = condition.getConditions();
        Assert.assertEquals("1", conditions.get(ConditionOperator.GT));
        Assert.assertEquals("2", conditions.get(ConditionOperator.GE));
        Assert.assertEquals("10", conditions.get(ConditionOperator.LT));
        Assert.assertEquals("9", conditions.get(ConditionOperator.LE));
        Assert.assertEquals("5", condition.getEqValue());
    }

    @Test
    public void testConditionDefaultEqValueIsEmpty() {
        Condition condition = new Condition();
        Assert.assertEquals("", condition.getEqValue());
        Assert.assertTrue(condition.getConditions().isEmpty());
    }

    @Test
    public void testConditionGetTableConditions() {
        Condition condition = new Condition();
        condition.GT("100");
        condition.LE("200");

        List<TablePrecompiled.Condition> tableConditions = condition.getTableConditions();
        Assert.assertEquals(2, tableConditions.size());
    }

    @Test
    public void testConditionSetLimitInt() {
        Condition condition = new Condition();
        condition.setLimit(3, 50);
        TablePrecompiled.Limit limit = condition.getLimit();
        Assert.assertNotNull(limit);
        Assert.assertTrue(limit.toString().contains("offset=3"));
        Assert.assertTrue(limit.toString().contains("count=50"));
    }

    @Test
    public void testConditionSetLimitBigInteger() {
        Condition condition = new Condition();
        condition.setLimit(BigInteger.valueOf(7), BigInteger.valueOf(13));
        TablePrecompiled.Limit limit = condition.getLimit();
        Assert.assertNotNull(limit);
        Assert.assertTrue(limit.toString().contains("offset=7"));
        Assert.assertTrue(limit.toString().contains("count=13"));
    }

    @Test
    public void testConditionDefaultLimitNotNull() {
        Condition condition = new Condition();
        Assert.assertNotNull(condition.getLimit());
    }

    @Test
    public void testConditionToString() {
        Condition condition = new Condition();
        condition.GT("1");
        condition.EQ("abc");
        String str = condition.toString();
        Assert.assertTrue(str.startsWith("Condition{"));
        Assert.assertTrue(str.contains("abc"));
    }

    // ---------------------------------------------------------------
    // ConditionV320
    // ---------------------------------------------------------------

    @Test
    public void testConditionV320AllOperators() {
        ConditionV320 condition = new ConditionV320();
        condition.EQ("f1", "v1");
        condition.NE("f2", "v2");
        condition.GT("f3", "v3");
        condition.GE("f4", "v4");
        condition.LT("f5", "v5");
        condition.LE("f6", "v6");
        condition.STARTS_WITH("f7", "v7");
        condition.ENDS_WITH("f8", "v8");
        condition.CONTAINS("f9", "v9");

        Map conditions = condition.getConditions();
        Assert.assertEquals(9, conditions.size());
    }

    @Test
    public void testConditionV320MultipleOperatorsSameField() {
        ConditionV320 condition = new ConditionV320();
        condition.GT("age", "10");
        condition.LT("age", "20");

        @SuppressWarnings("unchecked")
        Map<String, Map<ConditionOperator, String>> conditions = condition.getConditions();
        Assert.assertEquals(1, conditions.size());
        Map<ConditionOperator, String> opMap = conditions.get("age");
        Assert.assertEquals("10", opMap.get(ConditionOperator.GT));
        Assert.assertEquals("20", opMap.get(ConditionOperator.LT));
    }

    @Test
    public void testConditionV320GetTableConditions() {
        ConditionV320 condition = new ConditionV320();
        condition.EQ("name", "alice");
        condition.CONTAINS("desc", "abc");

        List tableConditions = condition.getTableConditions();
        Assert.assertEquals(2, tableConditions.size());
        Assert.assertTrue(tableConditions.get(0) instanceof TablePrecompiled.ConditionV320);
    }

    @Test
    public void testConditionV320ToString() {
        ConditionV320 condition = new ConditionV320();
        condition.EQ("k", "v");
        String str = condition.toString();
        Assert.assertTrue(str.startsWith("ConditionV320{"));
    }

    @Test
    public void testConditionV320InheritsLimit() {
        ConditionV320 condition = new ConditionV320();
        Assert.assertNotNull(condition.getLimit());
        condition.setLimit(1, 100);
        Assert.assertTrue(condition.getLimit().toString().contains("offset=1"));
    }

    // ---------------------------------------------------------------
    // ConditionOperator
    // ---------------------------------------------------------------

    @Test
    public void testConditionOperatorValues() {
        ConditionOperator[] values = ConditionOperator.values();
        Assert.assertEquals(9, values.length);
    }

    @Test
    public void testConditionOperatorGetValue() {
        Assert.assertEquals(0, ConditionOperator.GT.getValue());
        Assert.assertEquals(1, ConditionOperator.GE.getValue());
        Assert.assertEquals(2, ConditionOperator.LT.getValue());
        Assert.assertEquals(3, ConditionOperator.LE.getValue());
        Assert.assertEquals(4, ConditionOperator.EQ.getValue());
        Assert.assertEquals(5, ConditionOperator.NE.getValue());
        Assert.assertEquals(6, ConditionOperator.STARTS_WITH.getValue());
        Assert.assertEquals(7, ConditionOperator.ENDS_WITH.getValue());
        Assert.assertEquals(8, ConditionOperator.CONTAINS.getValue());
    }

    @Test
    public void testConditionOperatorGetBigIntValue() {
        Assert.assertEquals(BigInteger.valueOf(0), ConditionOperator.GT.getBigIntValue());
        Assert.assertEquals(BigInteger.valueOf(8), ConditionOperator.CONTAINS.getBigIntValue());
    }

    @Test
    public void testConditionOperatorToString() {
        Assert.assertEquals("GT", ConditionOperator.GT.toString());
        Assert.assertEquals("GE", ConditionOperator.GE.toString());
        Assert.assertEquals("LT", ConditionOperator.LT.toString());
        Assert.assertEquals("LE", ConditionOperator.LE.toString());
        Assert.assertEquals("EQ", ConditionOperator.EQ.toString());
        Assert.assertEquals("NE", ConditionOperator.NE.toString());
        Assert.assertEquals("STARTS_WITH", ConditionOperator.STARTS_WITH.toString());
        Assert.assertEquals("ENDS_WITH", ConditionOperator.ENDS_WITH.toString());
        Assert.assertEquals("CONTAINS", ConditionOperator.CONTAINS.toString());
    }

    @Test
    public void testConditionOperatorValueOf() {
        Assert.assertEquals(ConditionOperator.EQ, ConditionOperator.valueOf("EQ"));
        Assert.assertEquals(ConditionOperator.CONTAINS, ConditionOperator.valueOf("CONTAINS"));
    }

    // ---------------------------------------------------------------
    // Entry
    // ---------------------------------------------------------------

    @Test
    public void testEntryFromValueColumnsAndMap() {
        List<String> valueColumns = Arrays.asList("name", "age");
        Map<String, String> fieldNameToValue = new HashMap<>();
        fieldNameToValue.put("name", "alice");
        fieldNameToValue.put("age", "30");
        fieldNameToValue.put("ignored", "x");

        Entry entry = new Entry(valueColumns, "key1", fieldNameToValue);
        Assert.assertEquals("key1", entry.getKey());
        Map<String, String> mapped = entry.getFieldNameToValue();
        Assert.assertEquals("alice", mapped.get("name"));
        Assert.assertEquals("30", mapped.get("age"));
        Assert.assertFalse(mapped.containsKey("ignored"));
    }

    @Test
    public void testEntryFromTablePrecompiledEntry() {
        List<String> valueColumns = Arrays.asList("c1", "c2");
        TablePrecompiled.Entry tableEntry =
                new TablePrecompiled.Entry("k2", Arrays.asList("v1", "v2"));
        Entry entry = new Entry(valueColumns, tableEntry);
        Assert.assertEquals("k2", entry.getKey());
        Assert.assertEquals("v1", entry.getFieldNameToValue().get("c1"));
        Assert.assertEquals("v2", entry.getFieldNameToValue().get("c2"));
    }

    @Test
    public void testEntryConvertToEntry() {
        List<String> valueColumns = Arrays.asList("a", "b");
        Map<String, String> fieldNameToValue = new HashMap<>();
        fieldNameToValue.put("a", "1");
        fieldNameToValue.put("b", "2");
        Entry entry = new Entry(valueColumns, "key", fieldNameToValue);
        TablePrecompiled.Entry converted = entry.covertToEntry();
        Assert.assertEquals("key", converted.key);
        Assert.assertEquals(2, converted.fields.size());
    }

    @Test
    public void testEntrySettersAndPut() {
        List<String> valueColumns = new ArrayList<>();
        Entry entry = new Entry(valueColumns, "k", new HashMap<>());
        entry.setKey("newKey");
        Assert.assertEquals("newKey", entry.getKey());

        entry.putFieldNameToValue("foo", "bar");
        Assert.assertEquals("bar", entry.getFieldNameToValue().get("foo"));

        Map<String, String> replacement = new HashMap<>();
        replacement.put("x", "y");
        entry.setFieldNameToValue(replacement);
        Assert.assertEquals("y", entry.getFieldNameToValue().get("x"));
        Assert.assertFalse(entry.getFieldNameToValue().containsKey("foo"));
    }

    @Test
    public void testEntryToString() {
        List<String> valueColumns = new ArrayList<>();
        Entry entry = new Entry(valueColumns, "kk", new HashMap<>());
        String str = entry.toString();
        Assert.assertTrue(str.startsWith("Entry{"));
        Assert.assertTrue(str.contains("kk"));
    }

    // ---------------------------------------------------------------
    // UpdateFields
    // ---------------------------------------------------------------

    @Test
    public void testUpdateFieldsDefaultConstructor() {
        UpdateFields updateFields = new UpdateFields();
        Assert.assertTrue(updateFields.getFieldNameToValue().isEmpty());
    }

    @Test
    public void testUpdateFieldsMapConstructor() {
        Map<String, String> map = new HashMap<>();
        map.put("col1", "val1");
        map.put("col2", "val2");
        UpdateFields updateFields = new UpdateFields(map);
        Assert.assertEquals(2, updateFields.getFieldNameToValue().size());
        Assert.assertEquals("val1", updateFields.getFieldNameToValue().get("col1"));
    }

    @Test
    public void testUpdateFieldsConvertToUpdateFields() {
        Map<String, String> map = new HashMap<>();
        map.put("a", "1");
        map.put("b", "2");
        UpdateFields updateFields = new UpdateFields(map);
        List<TablePrecompiled.UpdateField> list = updateFields.convertToUpdateFields();
        Assert.assertEquals(2, list.size());
    }

    @Test
    public void testUpdateFieldsToString() {
        UpdateFields updateFields = new UpdateFields();
        Assert.assertTrue(updateFields.toString().startsWith("UpdateFields{"));
    }

    // ---------------------------------------------------------------
    // Common
    // ---------------------------------------------------------------

    @Test
    public void testCommonTablePrefix() {
        Assert.assertEquals("/tables/", Common.TABLE_PREFIX);
    }

    @Test
    public void testCommonTableKeyOrderValues() {
        Common.TableKeyOrder[] values = Common.TableKeyOrder.values();
        Assert.assertEquals(3, values.length);
    }

    @Test
    public void testCommonTableKeyOrderGetBigValue() {
        Assert.assertEquals(BigInteger.valueOf(-1), Common.TableKeyOrder.Unknown.getBigValue());
        Assert.assertEquals(BigInteger.valueOf(0), Common.TableKeyOrder.Lexicographic.getBigValue());
        Assert.assertEquals(BigInteger.valueOf(1), Common.TableKeyOrder.Numerical.getBigValue());
    }

    @Test
    public void testCommonTableKeyOrderValueOfInt() {
        Assert.assertEquals(Common.TableKeyOrder.Lexicographic, Common.TableKeyOrder.valueOf(0));
        Assert.assertEquals(Common.TableKeyOrder.Numerical, Common.TableKeyOrder.valueOf(1));
        Assert.assertEquals(Common.TableKeyOrder.Unknown, Common.TableKeyOrder.valueOf(-1));
        Assert.assertEquals(Common.TableKeyOrder.Unknown, Common.TableKeyOrder.valueOf(99));
    }

    @Test
    public void testCommonTableKeyOrderToString() {
        Assert.assertEquals("Lexicographic", Common.TableKeyOrder.Lexicographic.toString());
        Assert.assertEquals("Numerical", Common.TableKeyOrder.Numerical.toString());
        Assert.assertEquals("Unknown", Common.TableKeyOrder.Unknown.toString());
    }

    @Test
    public void testCommonTableKeyOrderValueOfString() {
        Assert.assertEquals(
                Common.TableKeyOrder.Numerical, Common.TableKeyOrder.valueOf("Numerical"));
    }

    // ---------------------------------------------------------------
    // BFSUtils
    // ---------------------------------------------------------------

    @Test
    public void testBfsUtilsConstants() {
        Assert.assertEquals("directory", BFSUtils.BFS_TYPE_DIR);
        Assert.assertEquals("contract", BFSUtils.BFS_TYPE_CON);
        Assert.assertEquals("link", BFSUtils.BFS_TYPE_LNK);
        Assert.assertEquals("/", BFSUtils.BFS_ROOT);
        Assert.assertEquals("/apps", BFSUtils.BFS_APPS);
        Assert.assertEquals("/sys", BFSUtils.BFS_SYS);
        Assert.assertEquals("/tables", BFSUtils.BFS_TABLES);
        Assert.assertEquals("/usr", BFSUtils.BFS_USER);
    }

    @Test
    public void testBfsSystemPathSet() {
        Assert.assertTrue(BFSUtils.BFS_SYSTEM_PATH.contains("/"));
        Assert.assertTrue(BFSUtils.BFS_SYSTEM_PATH.contains("/apps"));
        Assert.assertTrue(BFSUtils.BFS_SYSTEM_PATH.contains("/sys"));
        Assert.assertEquals(5, BFSUtils.BFS_SYSTEM_PATH.size());
    }

    @Test
    public void testPath2LevelRoot() {
        List<String> levels = BFSUtils.path2Level("/");
        Assert.assertTrue(levels.isEmpty());
    }

    @Test
    public void testPath2LevelSingle() {
        List<String> levels = BFSUtils.path2Level("/apps");
        Assert.assertEquals(1, levels.size());
        Assert.assertEquals("apps", levels.get(0));
    }

    @Test
    public void testPath2LevelMulti() {
        List<String> levels = BFSUtils.path2Level("/apps/foo/bar");
        Assert.assertEquals(Arrays.asList("apps", "foo", "bar"), levels);
    }

    @Test
    public void testPath2LevelRelativeAndDots() {
        // "." segments are skipped; ".." pops the previous segment
        List<String> levels = BFSUtils.path2Level("apps/./foo/../bar");
        Assert.assertEquals(Arrays.asList("apps", "bar"), levels);
    }

    @Test
    public void testPath2LevelTrailingSlash() {
        List<String> levels = BFSUtils.path2Level("/apps/foo/");
        Assert.assertEquals(Arrays.asList("apps", "foo"), levels);
    }

    @Test
    public void testPath2LevelDotDotUnderflow() {
        // leading ".." with empty stack must not throw
        List<String> levels = BFSUtils.path2Level("/../foo");
        Assert.assertEquals(Arrays.asList("foo"), levels);
    }

    @Test
    public void testGetParentPathAndBaseNameRoot() {
        Tuple2<String, String> result = BFSUtils.getParentPathAndBaseName("/");
        Assert.assertEquals("/", result.getValue1());
        Assert.assertEquals("/", result.getValue2());
    }

    @Test
    public void testGetParentPathAndBaseNameSingleLevel() {
        Tuple2<String, String> result = BFSUtils.getParentPathAndBaseName("/apps");
        Assert.assertEquals("/", result.getValue1());
        Assert.assertEquals("apps", result.getValue2());
    }

    @Test
    public void testGetParentPathAndBaseNameMultiLevel() {
        Tuple2<String, String> result = BFSUtils.getParentPathAndBaseName("/apps/foo/bar");
        Assert.assertEquals("/apps/foo", result.getValue1());
        Assert.assertEquals("bar", result.getValue2());
    }

    // ---------------------------------------------------------------
    // BFSInfo
    // ---------------------------------------------------------------

    @Test
    public void testBfsInfoGettersSetters() {
        BFSInfo info = new BFSInfo("file.sol", "contract");
        Assert.assertEquals("file.sol", info.getFileName());
        Assert.assertEquals("contract", info.getFileType());

        info.setFileName("other.sol");
        info.setFileType("link");
        info.setAddress("0xabc");
        info.setAbi("[]");
        Assert.assertEquals("other.sol", info.getFileName());
        Assert.assertEquals("link", info.getFileType());
        Assert.assertEquals("0xabc", info.getAddress());
        Assert.assertEquals("[]", info.getAbi());
    }

    @Test
    public void testBfsInfoEqualsAndHashCode() {
        BFSInfo a = new BFSInfo("f", "contract");
        BFSInfo b = new BFSInfo("f", "contract");
        Assert.assertEquals(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());
        Assert.assertEquals(a, a);

        BFSInfo c = new BFSInfo("g", "contract");
        Assert.assertNotEquals(a, c);
        Assert.assertNotEquals(a, "not a bfsinfo");
        Assert.assertNotEquals(a, null);
    }

    @Test
    public void testBfsInfoToString() {
        BFSInfo info = new BFSInfo("f", "directory");
        String str = info.toString();
        Assert.assertTrue(str.startsWith("BFSInfo{"));
        Assert.assertTrue(str.contains("directory"));
    }

    @Test
    public void testBfsInfoFromPrecompiledBfsNull() {
        Assert.assertNull(BFSInfo.fromPrecompiledBfs(null));
    }

    @Test
    public void testBfsInfoFromPrecompiledBfsEmptyName() {
        BFSPrecompiled.BfsInfo bfsInfo =
                new BFSPrecompiled.BfsInfo("", "contract", new ArrayList<>());
        Assert.assertNull(BFSInfo.fromPrecompiledBfs(bfsInfo));
    }

    @Test
    public void testBfsInfoFromPrecompiledBfsContract() {
        BFSPrecompiled.BfsInfo bfsInfo =
                new BFSPrecompiled.BfsInfo("c.sol", "contract", new ArrayList<>());
        BFSInfo info = BFSInfo.fromPrecompiledBfs(bfsInfo);
        Assert.assertNotNull(info);
        Assert.assertEquals("c.sol", info.getFileName());
        Assert.assertEquals("contract", info.getFileType());
        Assert.assertNull(info.getAddress());
    }

    @Test
    public void testBfsInfoFromPrecompiledBfsLink() {
        BFSPrecompiled.BfsInfo bfsInfo =
                new BFSPrecompiled.BfsInfo("l", "link", Arrays.asList("0xaddr", "abiStr"));
        BFSInfo info = BFSInfo.fromPrecompiledBfs(bfsInfo);
        Assert.assertNotNull(info);
        Assert.assertEquals("link", info.getFileType());
        Assert.assertEquals("0xaddr", info.getAddress());
        Assert.assertEquals("abiStr", info.getAbi());
    }

    // ---------------------------------------------------------------
    // SystemConfigFeature
    // ---------------------------------------------------------------

    @Test
    public void testSystemConfigFeatureToStringIsFeatureName() {
        Assert.assertEquals(
                "bugfix_revert", SystemConfigFeature.Features.BUGFIX_REVERT.toString());
        Assert.assertEquals(
                "feature_sharding", SystemConfigFeature.Features.FEATURE_SHARDING.toString());
        Assert.assertEquals(
                "feature_balance", SystemConfigFeature.Features.FEATURE_BALANCE.toString());
    }

    @Test
    public void testSystemConfigFeatureEnableVersion() {
        Assert.assertEquals(
                EnumNodeVersion.BCOS_3_2_3.getVersion().intValue(),
                SystemConfigFeature.Features.BUGFIX_REVERT.enableVersion());
        Assert.assertEquals(
                EnumNodeVersion.BCOS_3_6_0.getVersion().intValue(),
                SystemConfigFeature.Features.FEATURE_BALANCE.enableVersion());
    }

    @Test
    public void testSystemConfigFeatureValuesNotEmpty() {
        Assert.assertTrue(SystemConfigFeature.Features.values().length > 0);
    }

    @Test
    public void testSystemConfigFeatureFromStringKnown() {
        Assert.assertEquals(
                SystemConfigFeature.Features.BUGFIX_REVERT,
                SystemConfigFeature.fromString("bugfix_revert"));
        Assert.assertEquals(
                SystemConfigFeature.Features.FEATURE_SHARDING,
                SystemConfigFeature.fromString("feature_sharding"));
        Assert.assertEquals(
                SystemConfigFeature.Features.BUGFIX_EIP55_ADDR,
                SystemConfigFeature.fromString("bugfix_eip55_addr"));
        Assert.assertEquals(
                SystemConfigFeature.Features.FEATURE_BALANCE_POLICY1,
                SystemConfigFeature.fromString("feature_balance_policy1"));
    }

    @Test
    public void testSystemConfigFeatureFromStringUnknown() {
        Assert.assertNull(SystemConfigFeature.fromString("not_a_feature"));
    }

    @Test
    public void testSystemConfigFeatureFromStringRoundTrip() {
        for (SystemConfigFeature.Features f : SystemConfigFeature.Features.values()) {
            Assert.assertEquals(f, SystemConfigFeature.fromString(f.toString()));
        }
    }

    // ---------------------------------------------------------------
    // PrecompiledAddress
    // ---------------------------------------------------------------

    @Test
    public void testPrecompiledAddressConstants() {
        Assert.assertEquals(
                "0000000000000000000000000000000000001000",
                PrecompiledAddress.SYS_CONFIG_PRECOMPILED_ADDRESS);
        Assert.assertEquals(
                "0000000000000000000000000000000000001002",
                PrecompiledAddress.TABLE_MANAGER_PRECOMPILED_ADDRESS);
        Assert.assertEquals(
                "0000000000000000000000000000000000001003",
                PrecompiledAddress.CONSENSUS_PRECOMPILED_ADDRESS);
        Assert.assertEquals(
                "000000000000000000000000000000000000100e",
                PrecompiledAddress.BFS_PRECOMPILED_ADDRESS);
        Assert.assertEquals(
                "0000000000000000000000000000000000001011",
                PrecompiledAddress.BALANCE_PRECOMPILED_ADDRESS);
    }

    @Test
    public void testPrecompiledAddressNameConstants() {
        Assert.assertEquals("/sys/status", PrecompiledAddress.SYS_CONFIG_PRECOMPILED_NAME);
        Assert.assertEquals("/sys/consensus", PrecompiledAddress.CONSENSUS_PRECOMPILED_NAME);
        Assert.assertEquals("/sys/bfs", PrecompiledAddress.BFS_PRECOMPILED_NAME);
        Assert.assertEquals("/sys/sharding", PrecompiledAddress.SHARDING_PRECOMPILED_NAME);
        Assert.assertEquals("/sys/balance", PrecompiledAddress.BALANCE_PRECOMPILED_NAME);
    }

    // ---------------------------------------------------------------
    // Version
    // ---------------------------------------------------------------

    @Test
    public void testVersionTwoArgConstructorGetters() {
        Version version = new Version("myIface", "3.2.0");
        Assert.assertEquals("myIface", version.getInterfaceName());
        Assert.assertEquals("3.2.0", version.getMinVersion());
        Assert.assertNull(version.getMaxVersion());
    }

    @Test
    public void testVersionThreeArgConstructorGetters() {
        Version version = new Version("myIface", "3.2.0", "3.6.0");
        Assert.assertEquals("3.2.0", version.getMinVersion());
        Assert.assertEquals("3.6.0", version.getMaxVersion());
    }

    @Test
    public void testVersionSetters() {
        Version version = new Version("a", "3.0.0");
        version.setInterfaceName("b");
        version.setMaxVersion("3.9.0");
        Assert.assertEquals("b", version.getInterfaceName());
        Assert.assertEquals("3.9.0", version.getMaxVersion());
    }

    @Test
    public void testVersionCheckPasses() throws ContractException {
        Version version = new Version("iface", "3.2.0");
        // current version 3.5.0 >= 3.2.0 -> no exception
        version.checkVersion(EnumNodeVersion.getClassVersion("3.5.0"));
    }

    @Test(expected = ContractException.class)
    public void testVersionCheckFailsBelowMin() throws ContractException {
        Version version = new Version("iface", "3.6.0");
        // current version 3.2.0 < 3.6.0 -> exception
        version.checkVersion(EnumNodeVersion.getClassVersion("3.2.0"));
    }

    @Test
    public void testVersionCheckWithinMaxRange() throws ContractException {
        Version version = new Version("iface", "3.2.0", "3.6.0");
        version.checkVersion(EnumNodeVersion.getClassVersion("3.4.0"));
    }

    @Test(expected = ContractException.class)
    public void testVersionCheckAboveMax() throws ContractException {
        Version version = new Version("iface", "3.2.0", "3.6.0");
        // current version 3.8.0 > 3.6.0 -> exception
        version.checkVersion(EnumNodeVersion.getClassVersion("3.8.0"));
    }

    @Test
    public void testVersionCheckEmptyMaxIsUnbounded() throws ContractException {
        Version version = new Version("iface", "3.2.0", "");
        version.checkVersion(EnumNodeVersion.getClassVersion("9.9.0"));
    }

    @Test
    public void testVersionCheckLongOverload() throws ContractException {
        // 0x03050000 = 3.5.0 in compatibility-version encoding
        Version version = new Version("iface", "3.2.0");
        version.checkVersion((long) 0x03050000);
    }

    // ---------------------------------------------------------------
    // PrecompiledVersionCheck
    // ---------------------------------------------------------------

    @Test
    public void testPrecompiledVersionCheckConstants() {
        Assert.assertEquals("list", PrecompiledVersionCheck.LS_PAGE_VERSION.getInterfaceName());
        Assert.assertEquals("3.1.0", PrecompiledVersionCheck.LS_PAGE_VERSION.getMinVersion());
        Assert.assertEquals("3.1.0", PrecompiledVersionCheck.LINK_SIMPLE_VERSION.getMinVersion());
        Assert.assertEquals(
                "3.2.0", PrecompiledVersionCheck.SET_CONTRACT_STATUS_VERSION.getMinVersion());
        Assert.assertEquals("3.2.0", PrecompiledVersionCheck.V320_CRUD_VERSION.getMinVersion());
        Assert.assertEquals("3.3.0", PrecompiledVersionCheck.INIT_AUTH_VERSION.getMinVersion());
        Assert.assertEquals(
                "3.3.0", PrecompiledVersionCheck.SHARDING_MIN_SUPPORT_VERSION.getMinVersion());
        Assert.assertEquals("3.3.0", PrecompiledVersionCheck.V330_FIX_BFS_VERSION.getMinVersion());
        Assert.assertEquals(
                "3.6.0", PrecompiledVersionCheck.BALANCE_PRECOMPILED_VERSION.getMinVersion());
        Assert.assertEquals(
                "3.12.0", PrecompiledVersionCheck.TERM_WEIGHT_MIN_SUPPORT_VERSION.getMinVersion());
    }

    @Test
    public void testPrecompiledVersionCheckBalancePasses() throws ContractException {
        // balance requires >= 3.6.0; 3.6.0 should pass
        PrecompiledVersionCheck.BALANCE_PRECOMPILED_VERSION.checkVersion(
                EnumNodeVersion.getClassVersion("3.6.0"));
    }

    @Test(expected = ContractException.class)
    public void testPrecompiledVersionCheckBalanceFailsBelow() throws ContractException {
        // balance requires >= 3.6.0; 3.5.0 should fail
        PrecompiledVersionCheck.BALANCE_PRECOMPILED_VERSION.checkVersion(
                EnumNodeVersion.getClassVersion("3.5.0"));
    }
}
