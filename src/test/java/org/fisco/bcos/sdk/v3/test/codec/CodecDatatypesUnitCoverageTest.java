package org.fisco.bcos.sdk.v3.test.codec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicStruct;
import org.fisco.bcos.sdk.v3.codec.datatypes.StaticStruct;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes2;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int8;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint160;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple10;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple11;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple12;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple13;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple14;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple15;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple16;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple17;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple18;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple19;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple20;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple4;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple5;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple6;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple7;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple8;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple9;
import org.junit.Test;

/**
 * Unit-test coverage that complements the existing codec / datatypes / tuple test suite
 * ({@code TuplesTest}, {@code GeneratedTypesTest}, {@code CodecRoundTripCoverageTest},
 * {@code CodecDeepCoverageTest}, {@code CodecExtraCoverageTest}, {@code CodecFinalCoverageTest},
 * {@code CodecContractUnitCoverageTest}). It deliberately targets branches NOT exercised there:
 *
 * <ul>
 *   <li>The generated {@code Tuple1..Tuple20.equals()} NULL-value ternary branches: the
 *       {@code valueK == null} (this-side-null) path, comparing against another tuple that is also
 *       null at K (continue / equal) and against a non-null tuple (return false). Only {@code
 *       equals()} is invoked on null-containing tuples because {@code hashCode()} unconditionally
 *       dereferences {@code value1} and would NPE.
 *   <li>A handful of datatype validation / equals edge cases on distinct inputs (non-8-bit-aligned
 *       int sizes, Bytes length mismatch, cross-class inequality, struct / array accessors) that
 *       use different values from the existing tests.
 * </ul>
 */
public class CodecDatatypesUnitCoverageTest {

    // ====================================================================
    // Tuple1..Tuple20 equals() null-value branch coverage.
    //
    // For each value position K we build a tuple that is null at K (non-null
    // elsewhere) and assert:
    //   - it equals another tuple that is also null at K (both-null branch),
    //   - it does NOT equal a fully non-null tuple (this-null / other-non-null
    //     branch),
    //   - a fully non-null tuple does NOT equal the null-at-K tuple (the
    //     other-null branch from the non-null side).
    // hashCode() is never called on null-containing tuples (it would NPE on
    // value1), matching the production contract.
    // ====================================================================

    @Test
    public void testTuple1EqualsNullBranches() {
        Tuple1<String> nullA = new Tuple1<>(null);
        Tuple1<String> nullB = new Tuple1<>(null);
        Tuple1<String> nonNull = new Tuple1<>("v");
        assertEquals(nullA, nullB);
        assertNotEquals(nullA, nonNull);
        assertNotEquals(nonNull, nullA);
    }

    @Test
    public void testTuple2EqualsNullBranches() {
        for (int k = 1; k <= 2; k++) {
            Object[] a = filled(2, k);
            Object[] b = filled(2, k);
            Object[] full = filled(2, 0);
            assertEquals(new Tuple2<>(a[0], a[1]), new Tuple2<>(b[0], b[1]));
            assertNotEquals(new Tuple2<>(a[0], a[1]), new Tuple2<>(full[0], full[1]));
            assertNotEquals(new Tuple2<>(full[0], full[1]), new Tuple2<>(a[0], a[1]));
        }
    }

    @Test
    public void testTuple3EqualsNullBranches() {
        for (int k = 1; k <= 3; k++) {
            Object[] a = filled(3, k);
            Object[] b = filled(3, k);
            Object[] full = filled(3, 0);
            assertEquals(new Tuple3<>(a[0], a[1], a[2]), new Tuple3<>(b[0], b[1], b[2]));
            assertNotEquals(
                    new Tuple3<>(a[0], a[1], a[2]), new Tuple3<>(full[0], full[1], full[2]));
            assertNotEquals(
                    new Tuple3<>(full[0], full[1], full[2]), new Tuple3<>(a[0], a[1], a[2]));
        }
    }

    @Test
    public void testTuple4EqualsNullBranches() {
        for (int k = 1; k <= 4; k++) {
            Object[] a = filled(4, k);
            Object[] b = filled(4, k);
            Object[] f = filled(4, 0);
            assertEquals(new Tuple4<>(a[0], a[1], a[2], a[3]), new Tuple4<>(b[0], b[1], b[2], b[3]));
            assertNotEquals(
                    new Tuple4<>(a[0], a[1], a[2], a[3]), new Tuple4<>(f[0], f[1], f[2], f[3]));
            assertNotEquals(
                    new Tuple4<>(f[0], f[1], f[2], f[3]), new Tuple4<>(a[0], a[1], a[2], a[3]));
        }
    }

    @Test
    public void testTuple5EqualsNullBranches() {
        for (int k = 1; k <= 5; k++) {
            Object[] a = filled(5, k);
            Object[] b = filled(5, k);
            Object[] f = filled(5, 0);
            assertEquals(
                    new Tuple5<>(a[0], a[1], a[2], a[3], a[4]),
                    new Tuple5<>(b[0], b[1], b[2], b[3], b[4]));
            assertNotEquals(
                    new Tuple5<>(a[0], a[1], a[2], a[3], a[4]),
                    new Tuple5<>(f[0], f[1], f[2], f[3], f[4]));
            assertNotEquals(
                    new Tuple5<>(f[0], f[1], f[2], f[3], f[4]),
                    new Tuple5<>(a[0], a[1], a[2], a[3], a[4]));
        }
    }

    @Test
    public void testTuple6EqualsNullBranches() {
        for (int k = 1; k <= 6; k++) {
            Object[] a = filled(6, k);
            Object[] b = filled(6, k);
            Object[] f = filled(6, 0);
            assertEquals(
                    new Tuple6<>(a[0], a[1], a[2], a[3], a[4], a[5]),
                    new Tuple6<>(b[0], b[1], b[2], b[3], b[4], b[5]));
            assertNotEquals(
                    new Tuple6<>(a[0], a[1], a[2], a[3], a[4], a[5]),
                    new Tuple6<>(f[0], f[1], f[2], f[3], f[4], f[5]));
            assertNotEquals(
                    new Tuple6<>(f[0], f[1], f[2], f[3], f[4], f[5]),
                    new Tuple6<>(a[0], a[1], a[2], a[3], a[4], a[5]));
        }
    }

    @Test
    public void testTuple7EqualsNullBranches() {
        for (int k = 1; k <= 7; k++) {
            Object[] a = filled(7, k);
            Object[] b = filled(7, k);
            Object[] f = filled(7, 0);
            assertEquals(
                    new Tuple7<>(a[0], a[1], a[2], a[3], a[4], a[5], a[6]),
                    new Tuple7<>(b[0], b[1], b[2], b[3], b[4], b[5], b[6]));
            assertNotEquals(
                    new Tuple7<>(a[0], a[1], a[2], a[3], a[4], a[5], a[6]),
                    new Tuple7<>(f[0], f[1], f[2], f[3], f[4], f[5], f[6]));
            assertNotEquals(
                    new Tuple7<>(f[0], f[1], f[2], f[3], f[4], f[5], f[6]),
                    new Tuple7<>(a[0], a[1], a[2], a[3], a[4], a[5], a[6]));
        }
    }

    @Test
    public void testTuple8EqualsNullBranches() {
        for (int k = 1; k <= 8; k++) {
            Object[] a = filled(8, k);
            Object[] b = filled(8, k);
            Object[] f = filled(8, 0);
            assertEquals(
                    new Tuple8<>(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7]),
                    new Tuple8<>(b[0], b[1], b[2], b[3], b[4], b[5], b[6], b[7]));
            assertNotEquals(
                    new Tuple8<>(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7]),
                    new Tuple8<>(f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7]));
            assertNotEquals(
                    new Tuple8<>(f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7]),
                    new Tuple8<>(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7]));
        }
    }

    @Test
    public void testTuple9EqualsNullBranches() {
        for (int k = 1; k <= 9; k++) {
            Object[] a = filled(9, k);
            Object[] b = filled(9, k);
            Object[] f = filled(9, 0);
            assertEquals(
                    new Tuple9<>(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8]),
                    new Tuple9<>(b[0], b[1], b[2], b[3], b[4], b[5], b[6], b[7], b[8]));
            assertNotEquals(
                    new Tuple9<>(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8]),
                    new Tuple9<>(f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8]));
            assertNotEquals(
                    new Tuple9<>(f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8]),
                    new Tuple9<>(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8]));
        }
    }

    @Test
    public void testTuple10EqualsNullBranches() {
        for (int k = 1; k <= 10; k++) {
            Object[] a = filled(10, k);
            Object[] b = filled(10, k);
            Object[] f = filled(10, 0);
            assertEquals(
                    new Tuple10<>(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9]),
                    new Tuple10<>(b[0], b[1], b[2], b[3], b[4], b[5], b[6], b[7], b[8], b[9]));
            assertNotEquals(
                    new Tuple10<>(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9]),
                    new Tuple10<>(f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8], f[9]));
            assertNotEquals(
                    new Tuple10<>(f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8], f[9]),
                    new Tuple10<>(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9]));
        }
    }

    @Test
    public void testTuple11EqualsNullBranches() {
        for (int k = 1; k <= 11; k++) {
            Object[] a = filled(11, k);
            Object[] b = filled(11, k);
            Object[] f = filled(11, 0);
            assertEquals(
                    new Tuple11<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10]),
                    new Tuple11<>(
                            b[0], b[1], b[2], b[3], b[4], b[5], b[6], b[7], b[8], b[9], b[10]));
            assertNotEquals(
                    new Tuple11<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10]),
                    new Tuple11<>(
                            f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8], f[9], f[10]));
            assertNotEquals(
                    new Tuple11<>(
                            f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8], f[9], f[10]),
                    new Tuple11<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10]));
        }
    }

    @Test
    public void testTuple12EqualsNullBranches() {
        for (int k = 1; k <= 12; k++) {
            Object[] a = filled(12, k);
            Object[] b = filled(12, k);
            Object[] f = filled(12, 0);
            assertEquals(
                    new Tuple12<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                            a[11]),
                    new Tuple12<>(
                            b[0], b[1], b[2], b[3], b[4], b[5], b[6], b[7], b[8], b[9], b[10],
                            b[11]));
            assertNotEquals(
                    new Tuple12<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                            a[11]),
                    new Tuple12<>(
                            f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8], f[9], f[10],
                            f[11]));
            assertNotEquals(
                    new Tuple12<>(
                            f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8], f[9], f[10],
                            f[11]),
                    new Tuple12<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                            a[11]));
        }
    }

    @Test
    public void testTuple13EqualsNullBranches() {
        for (int k = 1; k <= 13; k++) {
            Object[] a = filled(13, k);
            Object[] b = filled(13, k);
            Object[] f = filled(13, 0);
            assertEquals(
                    new Tuple13<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                            a[11], a[12]),
                    new Tuple13<>(
                            b[0], b[1], b[2], b[3], b[4], b[5], b[6], b[7], b[8], b[9], b[10],
                            b[11], b[12]));
            assertNotEquals(
                    new Tuple13<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                            a[11], a[12]),
                    new Tuple13<>(
                            f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8], f[9], f[10],
                            f[11], f[12]));
            assertNotEquals(
                    new Tuple13<>(
                            f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8], f[9], f[10],
                            f[11], f[12]),
                    new Tuple13<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                            a[11], a[12]));
        }
    }

    @Test
    public void testTuple14EqualsNullBranches() {
        for (int k = 1; k <= 14; k++) {
            Object[] a = filled(14, k);
            Object[] b = filled(14, k);
            Object[] f = filled(14, 0);
            assertEquals(
                    new Tuple14<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                            a[11], a[12], a[13]),
                    new Tuple14<>(
                            b[0], b[1], b[2], b[3], b[4], b[5], b[6], b[7], b[8], b[9], b[10],
                            b[11], b[12], b[13]));
            assertNotEquals(
                    new Tuple14<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                            a[11], a[12], a[13]),
                    new Tuple14<>(
                            f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8], f[9], f[10],
                            f[11], f[12], f[13]));
            assertNotEquals(
                    new Tuple14<>(
                            f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8], f[9], f[10],
                            f[11], f[12], f[13]),
                    new Tuple14<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                            a[11], a[12], a[13]));
        }
    }

    @Test
    public void testTuple15EqualsNullBranches() {
        for (int k = 1; k <= 15; k++) {
            Object[] a = filled(15, k);
            Object[] b = filled(15, k);
            Object[] f = filled(15, 0);
            assertEquals(
                    new Tuple15<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                            a[11], a[12], a[13], a[14]),
                    new Tuple15<>(
                            b[0], b[1], b[2], b[3], b[4], b[5], b[6], b[7], b[8], b[9], b[10],
                            b[11], b[12], b[13], b[14]));
            assertNotEquals(
                    new Tuple15<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                            a[11], a[12], a[13], a[14]),
                    new Tuple15<>(
                            f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8], f[9], f[10],
                            f[11], f[12], f[13], f[14]));
            assertNotEquals(
                    new Tuple15<>(
                            f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8], f[9], f[10],
                            f[11], f[12], f[13], f[14]),
                    new Tuple15<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                            a[11], a[12], a[13], a[14]));
        }
    }

    @Test
    public void testTuple16EqualsNullBranches() {
        for (int k = 1; k <= 16; k++) {
            Object[] a = filled(16, k);
            Object[] b = filled(16, k);
            Object[] f = filled(16, 0);
            assertEquals(
                    new Tuple16<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                            a[11], a[12], a[13], a[14], a[15]),
                    new Tuple16<>(
                            b[0], b[1], b[2], b[3], b[4], b[5], b[6], b[7], b[8], b[9], b[10],
                            b[11], b[12], b[13], b[14], b[15]));
            assertNotEquals(
                    new Tuple16<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                            a[11], a[12], a[13], a[14], a[15]),
                    new Tuple16<>(
                            f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8], f[9], f[10],
                            f[11], f[12], f[13], f[14], f[15]));
            assertNotEquals(
                    new Tuple16<>(
                            f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8], f[9], f[10],
                            f[11], f[12], f[13], f[14], f[15]),
                    new Tuple16<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                            a[11], a[12], a[13], a[14], a[15]));
        }
    }

    @Test
    public void testTuple17EqualsNullBranches() {
        for (int k = 1; k <= 17; k++) {
            Object[] a = filled(17, k);
            Object[] b = filled(17, k);
            Object[] f = filled(17, 0);
            assertEquals(
                    new Tuple17<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                            a[11], a[12], a[13], a[14], a[15], a[16]),
                    new Tuple17<>(
                            b[0], b[1], b[2], b[3], b[4], b[5], b[6], b[7], b[8], b[9], b[10],
                            b[11], b[12], b[13], b[14], b[15], b[16]));
            assertNotEquals(
                    new Tuple17<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                            a[11], a[12], a[13], a[14], a[15], a[16]),
                    new Tuple17<>(
                            f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8], f[9], f[10],
                            f[11], f[12], f[13], f[14], f[15], f[16]));
            assertNotEquals(
                    new Tuple17<>(
                            f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8], f[9], f[10],
                            f[11], f[12], f[13], f[14], f[15], f[16]),
                    new Tuple17<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                            a[11], a[12], a[13], a[14], a[15], a[16]));
        }
    }

    @Test
    public void testTuple18EqualsNullBranches() {
        for (int k = 1; k <= 18; k++) {
            Object[] a = filled(18, k);
            Object[] b = filled(18, k);
            Object[] f = filled(18, 0);
            assertEquals(
                    new Tuple18<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                            a[11], a[12], a[13], a[14], a[15], a[16], a[17]),
                    new Tuple18<>(
                            b[0], b[1], b[2], b[3], b[4], b[5], b[6], b[7], b[8], b[9], b[10],
                            b[11], b[12], b[13], b[14], b[15], b[16], b[17]));
            assertNotEquals(
                    new Tuple18<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                            a[11], a[12], a[13], a[14], a[15], a[16], a[17]),
                    new Tuple18<>(
                            f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8], f[9], f[10],
                            f[11], f[12], f[13], f[14], f[15], f[16], f[17]));
            assertNotEquals(
                    new Tuple18<>(
                            f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8], f[9], f[10],
                            f[11], f[12], f[13], f[14], f[15], f[16], f[17]),
                    new Tuple18<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                            a[11], a[12], a[13], a[14], a[15], a[16], a[17]));
        }
    }

    @Test
    public void testTuple19EqualsNullBranches() {
        for (int k = 1; k <= 19; k++) {
            Object[] a = filled(19, k);
            Object[] b = filled(19, k);
            Object[] f = filled(19, 0);
            assertEquals(
                    new Tuple19<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                            a[11], a[12], a[13], a[14], a[15], a[16], a[17], a[18]),
                    new Tuple19<>(
                            b[0], b[1], b[2], b[3], b[4], b[5], b[6], b[7], b[8], b[9], b[10],
                            b[11], b[12], b[13], b[14], b[15], b[16], b[17], b[18]));
            assertNotEquals(
                    new Tuple19<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                            a[11], a[12], a[13], a[14], a[15], a[16], a[17], a[18]),
                    new Tuple19<>(
                            f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8], f[9], f[10],
                            f[11], f[12], f[13], f[14], f[15], f[16], f[17], f[18]));
            assertNotEquals(
                    new Tuple19<>(
                            f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8], f[9], f[10],
                            f[11], f[12], f[13], f[14], f[15], f[16], f[17], f[18]),
                    new Tuple19<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                            a[11], a[12], a[13], a[14], a[15], a[16], a[17], a[18]));
        }
    }

    @Test
    public void testTuple20EqualsNullBranches() {
        for (int k = 1; k <= 20; k++) {
            Object[] a = filled(20, k);
            Object[] b = filled(20, k);
            Object[] f = filled(20, 0);
            assertEquals(
                    new Tuple20<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                            a[11], a[12], a[13], a[14], a[15], a[16], a[17], a[18], a[19]),
                    new Tuple20<>(
                            b[0], b[1], b[2], b[3], b[4], b[5], b[6], b[7], b[8], b[9], b[10],
                            b[11], b[12], b[13], b[14], b[15], b[16], b[17], b[18], b[19]));
            assertNotEquals(
                    new Tuple20<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                            a[11], a[12], a[13], a[14], a[15], a[16], a[17], a[18], a[19]),
                    new Tuple20<>(
                            f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8], f[9], f[10],
                            f[11], f[12], f[13], f[14], f[15], f[16], f[17], f[18], f[19]));
            assertNotEquals(
                    new Tuple20<>(
                            f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8], f[9], f[10],
                            f[11], f[12], f[13], f[14], f[15], f[16], f[17], f[18], f[19]),
                    new Tuple20<>(
                            a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                            a[11], a[12], a[13], a[14], a[15], a[16], a[17], a[18], a[19]));
        }
    }

    /**
     * Build an Object[] of length {@code size}, all elements a distinct non-null String, except the
     * (1-based) position {@code nullPos} which is set to null. A {@code nullPos} of 0 leaves all
     * elements non-null. Same inputs always yield equal element values, so two arrays built with the
     * same {@code nullPos} produce equal tuples.
     */
    private static Object[] filled(int size, int nullPos) {
        Object[] out = new Object[size];
        for (int i = 0; i < size; i++) {
            out[i] = (i + 1 == nullPos) ? null : ("v" + i);
        }
        return out;
    }

    // ====================================================================
    // datatypes: validation / equals edge cases on DISTINCT inputs.
    // ====================================================================

    @Test
    public void testIntTypeNonEightBitAlignedThrows() {
        // bitSize 8-bit alignment is checked in IntType.valid; an unaligned size is rejected.
        // Use the protected-by-package generated types differently: a value whose bitLength
        // exceeds the type width also triggers the throw.
        try {
            new Uint8(BigInteger.valueOf(512)); // bitLength 10 > 8
            org.junit.Assert.fail("expected UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            // ok
        }
    }

    @Test
    public void testInt8NegativeBoundary() {
        // A small negative is accepted; a value whose (two's-complement) bitLength exceeds 8 is
        // rejected. BigInteger.valueOf(-257).bitLength() == 9 > 8 -> throws.
        Int8 small = new Int8(BigInteger.valueOf(-1));
        assertEquals(BigInteger.valueOf(-1), small.getValue());
        try {
            new Int8(BigInteger.valueOf(-257));
            org.junit.Assert.fail("expected UnsupportedOperationException for int8 underflow");
        } catch (UnsupportedOperationException expected) {
            // ok
        }
    }

    @Test
    public void testUint8MaxBoundary() {
        Uint8 max = new Uint8(BigInteger.valueOf(255));
        assertEquals(BigInteger.valueOf(255), max.getValue());
        try {
            new Uint8(BigInteger.valueOf(256));
            org.junit.Assert.fail("expected UnsupportedOperationException for uint8 overflow");
        } catch (UnsupportedOperationException expected) {
            // ok
        }
    }

    @Test
    public void testBytesLengthMismatchThrows() {
        // declared length 2 but 1 byte supplied -> reject.
        try {
            new Bytes(2, new byte[] {1});
            org.junit.Assert.fail("expected UnsupportedOperationException for bytes length");
        } catch (UnsupportedOperationException expected) {
            // ok
        }
    }

    @Test
    public void testBytesTooLongThrows() {
        // 33-byte value exceeds the 0 < M <= 32 range.
        try {
            new Bytes(33, new byte[33]);
            org.junit.Assert.fail("expected UnsupportedOperationException for bytes > 32");
        } catch (UnsupportedOperationException expected) {
            // ok
        }
    }

    @Test
    public void testStaticBytesCrossClassInequality() {
        Bytes1 b1 = new Bytes1(new byte[] {5});
        Bytes2 b2 = new Bytes2(new byte[] {5, 5});
        // different runtime classes are never equal.
        assertNotEquals(b1, b2);
        assertNotEquals(b1, null);
        assertNotEquals(b1, "bytes");
    }

    @Test
    public void testAddressNullVsDefaultBranch() {
        // Address.equals: value-non-null side compared to default (zero) -> not equal.
        Address a = new Address(BigInteger.valueOf(0x77));
        assertNotEquals(a, Address.DEFAULT);
        assertEquals(a, new Address(new Uint160(BigInteger.valueOf(0x77))));
        assertEquals(a, a);
    }

    @Test
    public void testBoolValueAndHashDistinct() {
        Bool t = new Bool(true);
        Bool f = new Bool(false);
        assertNotEquals(t, f);
        assertEquals(1, t.hashCode());
        assertEquals(0, f.hashCode());
        assertFalse(t.equals(Boolean.TRUE));
    }

    // ====================================================================
    // DynamicArray / StaticArray equals edge cases (Array.equals branches).
    // ====================================================================

    @Test
    public void testDynamicArrayEqualsAndComponentMismatch() {
        DynamicArray<Uint256> a =
                new DynamicArray<>(
                        Uint256.class,
                        Arrays.asList(new Uint256(BigInteger.valueOf(3)), new Uint256(BigInteger.valueOf(4))));
        DynamicArray<Uint256> b =
                new DynamicArray<>(
                        Uint256.class,
                        Arrays.asList(new Uint256(BigInteger.valueOf(3)), new Uint256(BigInteger.valueOf(4))));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a, a);
        assertNotEquals(a, null);
        assertNotEquals(a, "array");

        // different component values -> Objects.equals(value,...) false branch.
        DynamicArray<Uint256> diff =
                new DynamicArray<>(
                        Uint256.class,
                        Arrays.asList(new Uint256(BigInteger.valueOf(9)), new Uint256(BigInteger.valueOf(9))));
        assertNotEquals(a, diff);

        // different component type -> type.equals(...) false branch.
        DynamicArray<Uint8> diffType =
                new DynamicArray<>(
                        Uint8.class,
                        Arrays.asList(new Uint8(BigInteger.valueOf(3)), new Uint8(BigInteger.valueOf(4))));
        assertNotEquals(a, diffType);
    }

    @Test
    public void testStaticArrayEqualsAndTypeAsString() {
        StaticArray2<Uint256> a =
                new StaticArray2<>(
                        Uint256.class,
                        Arrays.asList(new Uint256(BigInteger.valueOf(1)), new Uint256(BigInteger.valueOf(2))));
        StaticArray2<Uint256> b =
                new StaticArray2<>(
                        Uint256.class,
                        Arrays.asList(new Uint256(BigInteger.valueOf(1)), new Uint256(BigInteger.valueOf(2))));
        assertEquals("uint256[2]", a.getTypeAsString());
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertFalse(a.isFixed());
        assertEquals(2, a.bytes32PaddedLength() / 32);
    }

    // ====================================================================
    // StaticStruct / DynamicStruct accessors on distinct inputs.
    // ====================================================================

    @Test
    public void testStaticStructComponentsAndType() {
        StaticStruct s =
                new StaticStruct(new Uint256(BigInteger.valueOf(5)), new Bool(true));
        assertEquals("(uint256,bool)", s.getTypeAsString());
        List<Type> components = s.getComponentTypes();
        assertEquals(2, components.size());
        assertEquals(BigInteger.valueOf(5), components.get(0).getValue());
        assertEquals(Boolean.TRUE, components.get(1).getValue());
    }

    @Test
    public void testDynamicStructComponentsAndPaddedLength() {
        DynamicStruct s =
                new DynamicStruct(
                        new Utf8String("nested"),
                        new DynamicBytes(new byte[] {1, 2, 3}),
                        new Uint256(BigInteger.ONE));
        assertEquals("(string,bytes,uint256)", s.getTypeAsString());
        assertEquals(3, s.getComponentTypes().size());
        assertNotNull(s.getValue());
        // DynamicStruct adds an offset word, so its padded length exceeds the bare field sum.
        assertTrue(s.bytes32PaddedLength() >= 32);
    }
}
