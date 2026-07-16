/*
 * Copyright 2014-2020  [fisco-dev]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package org.fisco.bcos.sdk.v3.test.codec;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.fisco.bcos.sdk.v3.codec.Utils;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray3;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.v3.codec.wrapper.ContractCodecJsonWrapper;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.hash.Keccak256;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.junit.Test;

/**
 * Unit-test coverage for the codec wrapper helper / DTO classes:
 *
 * <ul>
 *   <li>{@link ABIDefinition} getters/setters, {@code NamedType} (structIdentifier, nestedness,
 *       isDynamic, getTypeAsString for tuples, equals/hashCode/toString), {@code ConflictField},
 *       method-id / signature helpers and the createDefault* factory.
 *   <li>{@link Utils} type-name / method-sign helpers, {@code dynamicType}, {@code getClassType},
 *       {@code convert} and {@code typeMap}.
 *   <li>{@link ContractCodecJsonWrapper#tryDecodeInputData}.
 * </ul>
 */
public class CodecWrapperMoreUnitCoverageTest {

    private CryptoSuite cryptoSuite() {
        return new CryptoSuite(CryptoType.ECDSA_TYPE);
    }

    // -------------------------------------------------------------------------
    // ABIDefinition getters / setters / factory
    // -------------------------------------------------------------------------

    @Test
    public void testAbiDefinitionGettersSetters() {
        ABIDefinition def = new ABIDefinition();
        def.setName("transfer");
        def.setType("function");
        def.setConstant(false);
        def.setPayable(true);
        def.setAnonymous(false);
        def.setStateMutability("payable");

        assertEquals("transfer", def.getName());
        assertEquals("function", def.getType());
        assertTrue(def.isPayable());
        assertFalse(def.isAnonymous());
        assertEquals("payable", def.getStateMutability());

        List<ABIDefinition.NamedType> inputs = new ArrayList<>();
        inputs.add(new ABIDefinition.NamedType("to", "address"));
        def.setInputs(inputs);
        assertEquals(1, def.getInputs().size());

        List<ABIDefinition.NamedType> outputs = new ArrayList<>();
        outputs.add(new ABIDefinition.NamedType("ok", "bool"));
        def.setOutputs(outputs);
        assertTrue(def.hasOutputs());
        assertEquals(1, def.getOutputs().size());

        List<Long> selector = Arrays.asList(1L, 2L);
        def.setSelector(selector);
        assertEquals(selector, def.getSelector());

        assertNotNull(def.toString());
    }

    @Test
    public void testIsConstantFromStateMutability() {
        ABIDefinition def = new ABIDefinition();
        def.setStateMutability("view");
        assertTrue(def.isConstant());
        def.setStateMutability("pure");
        assertTrue(def.isConstant());
        def.setStateMutability("nonpayable");
        assertFalse(def.isConstant());
    }

    @Test
    public void testCreateDefaultConstructorABIDefinition() {
        ABIDefinition ctor = ABIDefinition.createDefaultConstructorABIDefinition();
        assertEquals("constructor", ctor.getType());
        assertTrue(ctor.getInputs().isEmpty());
    }

    @Test
    public void testMethodSignatureAndMethodId() {
        ABIDefinition def =
                ABIDefinition.createABIDefinition("transfer(address,uint256)");
        assertEquals("transfer(address,uint256)", def.getMethodSignatureAsString());

        byte[] idViaHash = def.getMethodId(new Keccak256());
        assertEquals(4, idViaHash.length);

        @SuppressWarnings("deprecation")
        byte[] idViaSuite = def.getMethodId(cryptoSuite());
        assertArrayEquals(idViaHash, idViaSuite);
    }

    @Test
    public void testCreateABIDefinitionInvalidSignatureThrows() {
        try {
            ABIDefinition.createABIDefinition("no parens here");
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // ok
        }
    }

    @Test
    public void testCreateABIDefinitionTupleThrows() {
        try {
            ABIDefinition.createABIDefinition("f(tuple)");
            fail("expected IllegalArgumentException for tuple params");
        } catch (IllegalArgumentException expected) {
            // ok
        }
    }

    @Test
    public void testAbiDefinitionEqualsAndHashCode() {
        ABIDefinition a = ABIDefinition.createABIDefinition("foo(uint256)");
        ABIDefinition b = ABIDefinition.createABIDefinition("foo(uint256)");
        ABIDefinition c = ABIDefinition.createABIDefinition("bar(uint256)");

        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, "not-an-abi");
    }

    // -------------------------------------------------------------------------
    // ABIDefinition.NamedType
    // -------------------------------------------------------------------------

    @Test
    public void testNamedTypeBasics() {
        ABIDefinition.NamedType nt = new ABIDefinition.NamedType("val", "uint256", true);
        assertEquals("val", nt.getName());
        assertEquals("uint256", nt.getType());
        assertTrue(nt.isIndexed());
        assertEquals("uint256", nt.getTypeAsString());
        assertEquals(0, nt.nestedness());
        assertFalse(nt.isDynamic());

        nt.setInternalType("uint256");
        assertEquals("uint256", nt.getInternalType());
        nt.setIndexed(false);
        assertFalse(nt.isIndexed());

        // newType returns an ABIDefinition.Type wrapping the type string
        ABIDefinition.Type t = nt.newType();
        assertEquals("uint256", t.getType());

        assertNotNull(nt.toString());
    }

    @Test
    public void testNamedTypeDynamicDetection() {
        assertTrue(new ABIDefinition.NamedType("s", "string").isDynamic());
        assertTrue(new ABIDefinition.NamedType("b", "bytes").isDynamic());
        assertTrue(new ABIDefinition.NamedType("a", "uint256[]").isDynamic());
        assertFalse(new ABIDefinition.NamedType("u", "uint256").isDynamic());
    }

    @Test
    public void testNamedTypeTupleTypeAsStringAndNestedness() {
        // tuple {uint256, string}
        ABIDefinition.NamedType tuple = new ABIDefinition.NamedType();
        tuple.setName("s");
        tuple.setType("tuple");
        List<ABIDefinition.NamedType> components = new ArrayList<>();
        components.add(new ABIDefinition.NamedType("a", "uint256"));
        components.add(new ABIDefinition.NamedType("b", "string"));
        tuple.setComponents(components);

        assertEquals("(uint256,string)", tuple.getTypeAsString());
        assertEquals(2, tuple.getComponents().size());
        assertEquals(1, tuple.nestedness());
        // dynamic because a component (string) is dynamic
        assertTrue(tuple.isDynamic());

        // structIdentifier should be stable for identical structures
        ABIDefinition.NamedType tuple2 = new ABIDefinition.NamedType();
        tuple2.setType("tuple");
        tuple2.setComponents(
                Arrays.asList(
                        new ABIDefinition.NamedType("a", "uint256"),
                        new ABIDefinition.NamedType("b", "string")));
        assertEquals(tuple.structIdentifier(), tuple2.structIdentifier());
    }

    @Test
    public void testNamedTypeArrayTupleTypeAsString() {
        ABIDefinition.NamedType tupleArray = new ABIDefinition.NamedType();
        tupleArray.setType("tuple[]");
        tupleArray.setComponents(
                Arrays.asList(new ABIDefinition.NamedType("x", "uint256")));
        assertEquals("(uint256)[]", tupleArray.getTypeAsString());
    }

    @Test
    public void testNamedTypeEqualsAndHashCode() {
        ABIDefinition.NamedType a = new ABIDefinition.NamedType("n", "uint256", true);
        ABIDefinition.NamedType b = new ABIDefinition.NamedType("n", "uint256", true);
        ABIDefinition.NamedType c = new ABIDefinition.NamedType("n", "int256", true);

        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, "x");
    }

    @Test
    public void testNamedTypeStructIdentifierStripsArraySuffix() {
        ABIDefinition.NamedType nt = new ABIDefinition.NamedType();
        nt.setType("uint256[]");
        nt.setInternalType("struct Foo[]");
        ABIDefinition.NamedType same = new ABIDefinition.NamedType();
        same.setType("uint256[]");
        same.setInternalType("struct Foo[]");
        // identifier is derived after stripping the [] suffix and is stable for equal structures
        assertEquals(nt.structIdentifier(), same.structIdentifier());
    }

    // -------------------------------------------------------------------------
    // ABIDefinition.ConflictField
    // -------------------------------------------------------------------------

    @Test
    public void testConflictFieldGettersSetters() {
        ABIDefinition.ConflictField field = new ABIDefinition.ConflictField();
        field.setKind(3);
        field.setSlot("0x2");
        field.setValue(Arrays.asList(0, 1));

        assertEquals(Integer.valueOf(3), field.getKind());
        assertEquals("0x2", field.getSlot());
        assertEquals(Arrays.asList(0, 1), field.getValue());
        assertNotNull(field.toString());

        ABIDefinition def = new ABIDefinition();
        List<ABIDefinition.ConflictField> fields = new ArrayList<>();
        fields.add(field);
        def.setConflictFields(fields);
        assertEquals(1, def.getConflictFields().size());
    }

    // -------------------------------------------------------------------------
    // Utils
    // -------------------------------------------------------------------------

    @Test
    public void testUtilsSimpleTypeAndMethodName() {
        assertEquals("uint256", Utils.getSimpleTypeName(Uint256.class));
        assertEquals("string", Utils.getSimpleTypeName(Utf8String.class));
        assertEquals("bytes", Utils.getSimpleTypeName(DynamicBytes.class));

        assertEquals("uint256", Utils.getSimpleMethodSign(Uint256.class));
        assertEquals("string", Utils.getSimpleMethodSign(Utf8String.class));
        assertEquals("bytes", Utils.getSimpleMethodSign(DynamicBytes.class));
    }

    @Test
    public void testUtilsGetTypeNameAndMethodSignForReferences() {
        TypeReference<Uint256> uintRef = TypeReference.create(Uint256.class);
        assertEquals("uint256", Utils.getTypeName(uintRef));
        assertEquals("uint256", Utils.getMethodSign(uintRef));

        TypeReference<DynamicArray<Uint256>> dynArrayRef =
                new TypeReference<DynamicArray<Uint256>>() {};
        assertEquals("uint256[]", Utils.getTypeName(dynArrayRef));
        assertEquals("uint256[]", Utils.getMethodSign(dynArrayRef));

        TypeReference<StaticArray3<Uint256>> staticArrayRef =
                new TypeReference<StaticArray3<Uint256>>() {};
        assertEquals("uint256[3]", Utils.getTypeName(staticArrayRef));
        assertEquals("uint256[3]", Utils.getMethodSign(staticArrayRef));
    }

    @Test
    public void testUtilsDynamicType() throws Exception {
        assertTrue(Utils.dynamicType(Utf8String.class));
        assertTrue(Utils.dynamicType(DynamicBytes.class));
        assertFalse(Utils.dynamicType(Uint256.class));

        TypeReference<DynamicArray<Uint256>> dynArrayRef =
                new TypeReference<DynamicArray<Uint256>>() {};
        assertTrue(Utils.dynamicType(dynArrayRef.getType()));
    }

    @Test
    public void testUtilsGetClassType() throws Exception {
        assertEquals(Uint256.class, Utils.getClassType(Uint256.class));

        TypeReference<DynamicArray<Uint256>> dynArrayRef =
                new TypeReference<DynamicArray<Uint256>>() {};
        assertEquals(DynamicArray.class, Utils.getClassType(dynArrayRef.getType()));
        assertEquals(Uint256.class, Utils.getParameterizedTypeFromArray(dynArrayRef));
    }

    @Test
    public void testUtilsConvert() {
        List<TypeReference<?>> input = new ArrayList<>();
        input.add(TypeReference.create(Uint256.class));
        List<TypeReference<Type>> converted = Utils.convert(input);
        assertEquals(1, converted.size());
    }

    @Test
    public void testUtilsTypeMap() {
        List<BigInteger> values = Arrays.asList(BigInteger.ONE, BigInteger.TEN);
        List<Uint256> mapped = Utils.typeMap(values, Uint256.class);
        assertEquals(2, mapped.size());
        assertEquals(BigInteger.ONE, mapped.get(0).getValue());
        assertEquals(BigInteger.TEN, mapped.get(1).getValue());

        // empty input -> empty result
        assertTrue(Utils.typeMap(new ArrayList<BigInteger>(), Uint256.class).isEmpty());
    }

    @Test
    public void testUtilsGetLengthSimple() {
        List<Type> parameters = new ArrayList<>();
        parameters.add(new Uint256(BigInteger.ONE));
        parameters.add(new Utf8String("x"));
        assertEquals(2, Utils.getLength(parameters));
    }

    // -------------------------------------------------------------------------
    // ContractCodecJsonWrapper static helper
    // -------------------------------------------------------------------------

    @Test
    public void testTryDecodeInputDataPrefixed() {
        byte[] decoded =
                ContractCodecJsonWrapper.tryDecodeInputData(
                        ContractCodecJsonWrapper.HexEncodedDataPrefix + "0a0b0c");
        assertArrayEquals(new byte[] {0x0a, 0x0b, 0x0c}, decoded);
    }

    @Test
    public void testTryDecodeInputDataHex() {
        byte[] decoded = ContractCodecJsonWrapper.tryDecodeInputData("0xdeadbeef");
        assertArrayEquals(new byte[] {(byte) 0xde, (byte) 0xad, (byte) 0xbe, (byte) 0xef}, decoded);
    }

    @Test
    public void testTryDecodeInputDataNonHexReturnsNull() {
        // not hex-decodable plain text -> null
        assertNull(ContractCodecJsonWrapper.tryDecodeInputData("hello world!"));
    }

    @Test
    public void testTryDecodeInputDataEmptyReturnsNull() {
        assertNull(ContractCodecJsonWrapper.tryDecodeInputData(""));
    }
}
