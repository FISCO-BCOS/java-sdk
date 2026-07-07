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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.fisco.bcos.sdk.v3.codec.datatypes.AbiTypes;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes16;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int128;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int8;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint128;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint16;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8;
import org.junit.Test;

/**
 * Unit-test coverage for the {@link AbiTypes#getType(String)} factory and {@link
 * AbiTypes#getTypeAString(Class)} reverse mapping across the solidity type space (uint*, int*,
 * bytes*, bool, address, string, bytes) plus the default branches.
 */
public class AbiTypesUnitCoverageTest {

    @Test
    public void testBaseTypes() {
        assertEquals(Address.class, AbiTypes.getType("address"));
        assertEquals(Bool.class, AbiTypes.getType("bool"));
        assertEquals(Bool.class, AbiTypes.getType("boolean"));
        assertEquals(Utf8String.class, AbiTypes.getType("string"));
        assertEquals(DynamicBytes.class, AbiTypes.getType("bytes"));
    }

    @Test
    public void testUintTypes() {
        assertEquals(Uint8.class, AbiTypes.getType("uint8"));
        assertEquals(Uint16.class, AbiTypes.getType("uint16"));
        assertEquals(Uint128.class, AbiTypes.getType("uint128"));
        assertEquals(Uint256.class, AbiTypes.getType("uint256"));
    }

    @Test
    public void testIntTypes() {
        assertEquals(Int8.class, AbiTypes.getType("int8"));
        assertEquals(Int128.class, AbiTypes.getType("int128"));
        assertEquals(Int256.class, AbiTypes.getType("int256"));
    }

    @Test
    public void testAllUintIntWidths() {
        for (int bits = 8; bits <= 256; bits += 8) {
            Class<? extends Type> uintType = AbiTypes.getType("uint" + bits);
            assertEquals("Uint" + bits, uintType.getSimpleName());
            Class<? extends Type> intType = AbiTypes.getType("int" + bits);
            assertEquals("Int" + bits, intType.getSimpleName());
        }
    }

    @Test
    public void testBytesNTypes() {
        assertEquals(Bytes1.class, AbiTypes.getType("bytes1"));
        assertEquals(Bytes16.class, AbiTypes.getType("bytes16"));
        assertEquals(Bytes32.class, AbiTypes.getType("bytes32"));
    }

    @Test
    public void testAllBytesNWidths() {
        for (int n = 1; n <= 32; n++) {
            Class<? extends Type> type = AbiTypes.getType("bytes" + n);
            assertEquals("Bytes" + n, type.getSimpleName());
        }
    }

    @Test
    public void testDefaultBranchResolvesFullyQualifiedClass() {
        // The default branch falls back to Class.forName for unknown short names.
        Class<? extends Type> resolved =
                AbiTypes.getType("org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint64");
        assertEquals("Uint64", resolved.getSimpleName());
    }

    @Test
    public void testDefaultBranchUnknownTypeThrows() {
        try {
            AbiTypes.getType("not_a_real_type");
            fail("expected UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            // ok
        }
    }

    @Test
    public void testGetTypeAString() {
        assertEquals("string", AbiTypes.getTypeAString(Utf8String.class));
        assertEquals("bytes", AbiTypes.getTypeAString(DynamicBytes.class));
        assertEquals("address", AbiTypes.getTypeAString(Address.class));
        assertEquals("bool", AbiTypes.getTypeAString(Bool.class));
        assertEquals("uint256", AbiTypes.getTypeAString(Uint256.class));
        assertEquals("int8", AbiTypes.getTypeAString(Int8.class));
        assertEquals("bytes32", AbiTypes.getTypeAString(Bytes32.class));
    }

    @Test
    public void testGetTypeRoundTripWithGetTypeAString() {
        String[] names = {"uint256", "int128", "bytes16", "bool", "address", "string", "bytes"};
        for (String name : names) {
            Class<? extends Type> type = AbiTypes.getType(name);
            assertEquals(name, AbiTypes.getTypeAString(type));
        }
    }
}
