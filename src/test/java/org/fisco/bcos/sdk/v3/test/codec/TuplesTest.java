package org.fisco.bcos.sdk.v3.test.codec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple4;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple5;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple6;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple7;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple8;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple9;
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
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple20;
import org.junit.Test;

/**
 * Exhaustive unit tests for the generated Tuple1..Tuple20 value classes: constructor, getters,
 * getSize, equals (self / null / different-class / equal / each-field-differs), hashCode and
 * toString. Generated test mirroring the generated production classes.
 */
public class TuplesTest {

    @Test
    public void testTuple1() {
        Tuple1<String> a = new Tuple1<>("v1_1");
        assertEquals("v1_1", a.getValue1());
        assertEquals(1, a.getSize());
        Tuple1<String> b = new Tuple1<>("v1_1");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(a.equals(a));
        assertNotEquals(null, a);
        assertNotEquals(a, "not-a-tuple");
        assertNotEquals(a, new Tuple1<>("X1"));
        assertTrue(a.toString().startsWith("Tuple1{"));
        assertTrue(a.toString().contains("value1="));
    }

    @Test
    public void testTuple2() {
        Tuple2<String, String> a = new Tuple2<>("v2_1", "v2_2");
        assertEquals("v2_1", a.getValue1());
        assertEquals("v2_2", a.getValue2());
        assertEquals(2, a.getSize());
        Tuple2<String, String> b = new Tuple2<>("v2_1", "v2_2");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(a.equals(a));
        assertNotEquals(null, a);
        assertNotEquals(a, "not-a-tuple");
        assertNotEquals(a, new Tuple2<>("X1", "v2_2"));
        assertNotEquals(a, new Tuple2<>("v2_1", "X2"));
        assertTrue(a.toString().startsWith("Tuple2{"));
        assertTrue(a.toString().contains("value1="));
    }

    @Test
    public void testTuple3() {
        Tuple3<String, String, String> a = new Tuple3<>("v3_1", "v3_2", "v3_3");
        assertEquals("v3_1", a.getValue1());
        assertEquals("v3_2", a.getValue2());
        assertEquals("v3_3", a.getValue3());
        assertEquals(3, a.getSize());
        Tuple3<String, String, String> b = new Tuple3<>("v3_1", "v3_2", "v3_3");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(a.equals(a));
        assertNotEquals(null, a);
        assertNotEquals(a, "not-a-tuple");
        assertNotEquals(a, new Tuple3<>("X1", "v3_2", "v3_3"));
        assertNotEquals(a, new Tuple3<>("v3_1", "X2", "v3_3"));
        assertNotEquals(a, new Tuple3<>("v3_1", "v3_2", "X3"));
        assertTrue(a.toString().startsWith("Tuple3{"));
        assertTrue(a.toString().contains("value1="));
    }

    @Test
    public void testTuple4() {
        Tuple4<String, String, String, String> a = new Tuple4<>("v4_1", "v4_2", "v4_3", "v4_4");
        assertEquals("v4_1", a.getValue1());
        assertEquals("v4_2", a.getValue2());
        assertEquals("v4_3", a.getValue3());
        assertEquals("v4_4", a.getValue4());
        assertEquals(4, a.getSize());
        Tuple4<String, String, String, String> b = new Tuple4<>("v4_1", "v4_2", "v4_3", "v4_4");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(a.equals(a));
        assertNotEquals(null, a);
        assertNotEquals(a, "not-a-tuple");
        assertNotEquals(a, new Tuple4<>("X1", "v4_2", "v4_3", "v4_4"));
        assertNotEquals(a, new Tuple4<>("v4_1", "X2", "v4_3", "v4_4"));
        assertNotEquals(a, new Tuple4<>("v4_1", "v4_2", "X3", "v4_4"));
        assertNotEquals(a, new Tuple4<>("v4_1", "v4_2", "v4_3", "X4"));
        assertTrue(a.toString().startsWith("Tuple4{"));
        assertTrue(a.toString().contains("value1="));
    }

    @Test
    public void testTuple5() {
        Tuple5<String, String, String, String, String> a = new Tuple5<>("v5_1", "v5_2", "v5_3", "v5_4", "v5_5");
        assertEquals("v5_1", a.getValue1());
        assertEquals("v5_2", a.getValue2());
        assertEquals("v5_3", a.getValue3());
        assertEquals("v5_4", a.getValue4());
        assertEquals("v5_5", a.getValue5());
        assertEquals(5, a.getSize());
        Tuple5<String, String, String, String, String> b = new Tuple5<>("v5_1", "v5_2", "v5_3", "v5_4", "v5_5");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(a.equals(a));
        assertNotEquals(null, a);
        assertNotEquals(a, "not-a-tuple");
        assertNotEquals(a, new Tuple5<>("X1", "v5_2", "v5_3", "v5_4", "v5_5"));
        assertNotEquals(a, new Tuple5<>("v5_1", "X2", "v5_3", "v5_4", "v5_5"));
        assertNotEquals(a, new Tuple5<>("v5_1", "v5_2", "X3", "v5_4", "v5_5"));
        assertNotEquals(a, new Tuple5<>("v5_1", "v5_2", "v5_3", "X4", "v5_5"));
        assertNotEquals(a, new Tuple5<>("v5_1", "v5_2", "v5_3", "v5_4", "X5"));
        assertTrue(a.toString().startsWith("Tuple5{"));
        assertTrue(a.toString().contains("value1="));
    }

    @Test
    public void testTuple6() {
        Tuple6<String, String, String, String, String, String> a = new Tuple6<>("v6_1", "v6_2", "v6_3", "v6_4", "v6_5", "v6_6");
        assertEquals("v6_1", a.getValue1());
        assertEquals("v6_2", a.getValue2());
        assertEquals("v6_3", a.getValue3());
        assertEquals("v6_4", a.getValue4());
        assertEquals("v6_5", a.getValue5());
        assertEquals("v6_6", a.getValue6());
        assertEquals(6, a.getSize());
        Tuple6<String, String, String, String, String, String> b = new Tuple6<>("v6_1", "v6_2", "v6_3", "v6_4", "v6_5", "v6_6");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(a.equals(a));
        assertNotEquals(null, a);
        assertNotEquals(a, "not-a-tuple");
        assertNotEquals(a, new Tuple6<>("X1", "v6_2", "v6_3", "v6_4", "v6_5", "v6_6"));
        assertNotEquals(a, new Tuple6<>("v6_1", "X2", "v6_3", "v6_4", "v6_5", "v6_6"));
        assertNotEquals(a, new Tuple6<>("v6_1", "v6_2", "X3", "v6_4", "v6_5", "v6_6"));
        assertNotEquals(a, new Tuple6<>("v6_1", "v6_2", "v6_3", "X4", "v6_5", "v6_6"));
        assertNotEquals(a, new Tuple6<>("v6_1", "v6_2", "v6_3", "v6_4", "X5", "v6_6"));
        assertNotEquals(a, new Tuple6<>("v6_1", "v6_2", "v6_3", "v6_4", "v6_5", "X6"));
        assertTrue(a.toString().startsWith("Tuple6{"));
        assertTrue(a.toString().contains("value1="));
    }

    @Test
    public void testTuple7() {
        Tuple7<String, String, String, String, String, String, String> a = new Tuple7<>("v7_1", "v7_2", "v7_3", "v7_4", "v7_5", "v7_6", "v7_7");
        assertEquals("v7_1", a.getValue1());
        assertEquals("v7_2", a.getValue2());
        assertEquals("v7_3", a.getValue3());
        assertEquals("v7_4", a.getValue4());
        assertEquals("v7_5", a.getValue5());
        assertEquals("v7_6", a.getValue6());
        assertEquals("v7_7", a.getValue7());
        assertEquals(7, a.getSize());
        Tuple7<String, String, String, String, String, String, String> b = new Tuple7<>("v7_1", "v7_2", "v7_3", "v7_4", "v7_5", "v7_6", "v7_7");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(a.equals(a));
        assertNotEquals(null, a);
        assertNotEquals(a, "not-a-tuple");
        assertNotEquals(a, new Tuple7<>("X1", "v7_2", "v7_3", "v7_4", "v7_5", "v7_6", "v7_7"));
        assertNotEquals(a, new Tuple7<>("v7_1", "X2", "v7_3", "v7_4", "v7_5", "v7_6", "v7_7"));
        assertNotEquals(a, new Tuple7<>("v7_1", "v7_2", "X3", "v7_4", "v7_5", "v7_6", "v7_7"));
        assertNotEquals(a, new Tuple7<>("v7_1", "v7_2", "v7_3", "X4", "v7_5", "v7_6", "v7_7"));
        assertNotEquals(a, new Tuple7<>("v7_1", "v7_2", "v7_3", "v7_4", "X5", "v7_6", "v7_7"));
        assertNotEquals(a, new Tuple7<>("v7_1", "v7_2", "v7_3", "v7_4", "v7_5", "X6", "v7_7"));
        assertNotEquals(a, new Tuple7<>("v7_1", "v7_2", "v7_3", "v7_4", "v7_5", "v7_6", "X7"));
        assertTrue(a.toString().startsWith("Tuple7{"));
        assertTrue(a.toString().contains("value1="));
    }

    @Test
    public void testTuple8() {
        Tuple8<String, String, String, String, String, String, String, String> a = new Tuple8<>("v8_1", "v8_2", "v8_3", "v8_4", "v8_5", "v8_6", "v8_7", "v8_8");
        assertEquals("v8_1", a.getValue1());
        assertEquals("v8_2", a.getValue2());
        assertEquals("v8_3", a.getValue3());
        assertEquals("v8_4", a.getValue4());
        assertEquals("v8_5", a.getValue5());
        assertEquals("v8_6", a.getValue6());
        assertEquals("v8_7", a.getValue7());
        assertEquals("v8_8", a.getValue8());
        assertEquals(8, a.getSize());
        Tuple8<String, String, String, String, String, String, String, String> b = new Tuple8<>("v8_1", "v8_2", "v8_3", "v8_4", "v8_5", "v8_6", "v8_7", "v8_8");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(a.equals(a));
        assertNotEquals(null, a);
        assertNotEquals(a, "not-a-tuple");
        assertNotEquals(a, new Tuple8<>("X1", "v8_2", "v8_3", "v8_4", "v8_5", "v8_6", "v8_7", "v8_8"));
        assertNotEquals(a, new Tuple8<>("v8_1", "X2", "v8_3", "v8_4", "v8_5", "v8_6", "v8_7", "v8_8"));
        assertNotEquals(a, new Tuple8<>("v8_1", "v8_2", "X3", "v8_4", "v8_5", "v8_6", "v8_7", "v8_8"));
        assertNotEquals(a, new Tuple8<>("v8_1", "v8_2", "v8_3", "X4", "v8_5", "v8_6", "v8_7", "v8_8"));
        assertNotEquals(a, new Tuple8<>("v8_1", "v8_2", "v8_3", "v8_4", "X5", "v8_6", "v8_7", "v8_8"));
        assertNotEquals(a, new Tuple8<>("v8_1", "v8_2", "v8_3", "v8_4", "v8_5", "X6", "v8_7", "v8_8"));
        assertNotEquals(a, new Tuple8<>("v8_1", "v8_2", "v8_3", "v8_4", "v8_5", "v8_6", "X7", "v8_8"));
        assertNotEquals(a, new Tuple8<>("v8_1", "v8_2", "v8_3", "v8_4", "v8_5", "v8_6", "v8_7", "X8"));
        assertTrue(a.toString().startsWith("Tuple8{"));
        assertTrue(a.toString().contains("value1="));
    }

    @Test
    public void testTuple9() {
        Tuple9<String, String, String, String, String, String, String, String, String> a = new Tuple9<>("v9_1", "v9_2", "v9_3", "v9_4", "v9_5", "v9_6", "v9_7", "v9_8", "v9_9");
        assertEquals("v9_1", a.getValue1());
        assertEquals("v9_2", a.getValue2());
        assertEquals("v9_3", a.getValue3());
        assertEquals("v9_4", a.getValue4());
        assertEquals("v9_5", a.getValue5());
        assertEquals("v9_6", a.getValue6());
        assertEquals("v9_7", a.getValue7());
        assertEquals("v9_8", a.getValue8());
        assertEquals("v9_9", a.getValue9());
        assertEquals(9, a.getSize());
        Tuple9<String, String, String, String, String, String, String, String, String> b = new Tuple9<>("v9_1", "v9_2", "v9_3", "v9_4", "v9_5", "v9_6", "v9_7", "v9_8", "v9_9");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(a.equals(a));
        assertNotEquals(null, a);
        assertNotEquals(a, "not-a-tuple");
        assertNotEquals(a, new Tuple9<>("X1", "v9_2", "v9_3", "v9_4", "v9_5", "v9_6", "v9_7", "v9_8", "v9_9"));
        assertNotEquals(a, new Tuple9<>("v9_1", "X2", "v9_3", "v9_4", "v9_5", "v9_6", "v9_7", "v9_8", "v9_9"));
        assertNotEquals(a, new Tuple9<>("v9_1", "v9_2", "X3", "v9_4", "v9_5", "v9_6", "v9_7", "v9_8", "v9_9"));
        assertNotEquals(a, new Tuple9<>("v9_1", "v9_2", "v9_3", "X4", "v9_5", "v9_6", "v9_7", "v9_8", "v9_9"));
        assertNotEquals(a, new Tuple9<>("v9_1", "v9_2", "v9_3", "v9_4", "X5", "v9_6", "v9_7", "v9_8", "v9_9"));
        assertNotEquals(a, new Tuple9<>("v9_1", "v9_2", "v9_3", "v9_4", "v9_5", "X6", "v9_7", "v9_8", "v9_9"));
        assertNotEquals(a, new Tuple9<>("v9_1", "v9_2", "v9_3", "v9_4", "v9_5", "v9_6", "X7", "v9_8", "v9_9"));
        assertNotEquals(a, new Tuple9<>("v9_1", "v9_2", "v9_3", "v9_4", "v9_5", "v9_6", "v9_7", "X8", "v9_9"));
        assertNotEquals(a, new Tuple9<>("v9_1", "v9_2", "v9_3", "v9_4", "v9_5", "v9_6", "v9_7", "v9_8", "X9"));
        assertTrue(a.toString().startsWith("Tuple9{"));
        assertTrue(a.toString().contains("value1="));
    }

    @Test
    public void testTuple10() {
        Tuple10<String, String, String, String, String, String, String, String, String, String> a = new Tuple10<>("v10_1", "v10_2", "v10_3", "v10_4", "v10_5", "v10_6", "v10_7", "v10_8", "v10_9", "v10_10");
        assertEquals("v10_1", a.getValue1());
        assertEquals("v10_2", a.getValue2());
        assertEquals("v10_3", a.getValue3());
        assertEquals("v10_4", a.getValue4());
        assertEquals("v10_5", a.getValue5());
        assertEquals("v10_6", a.getValue6());
        assertEquals("v10_7", a.getValue7());
        assertEquals("v10_8", a.getValue8());
        assertEquals("v10_9", a.getValue9());
        assertEquals("v10_10", a.getValue10());
        assertEquals(10, a.getSize());
        Tuple10<String, String, String, String, String, String, String, String, String, String> b = new Tuple10<>("v10_1", "v10_2", "v10_3", "v10_4", "v10_5", "v10_6", "v10_7", "v10_8", "v10_9", "v10_10");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(a.equals(a));
        assertNotEquals(null, a);
        assertNotEquals(a, "not-a-tuple");
        assertNotEquals(a, new Tuple10<>("X1", "v10_2", "v10_3", "v10_4", "v10_5", "v10_6", "v10_7", "v10_8", "v10_9", "v10_10"));
        assertNotEquals(a, new Tuple10<>("v10_1", "X2", "v10_3", "v10_4", "v10_5", "v10_6", "v10_7", "v10_8", "v10_9", "v10_10"));
        assertNotEquals(a, new Tuple10<>("v10_1", "v10_2", "X3", "v10_4", "v10_5", "v10_6", "v10_7", "v10_8", "v10_9", "v10_10"));
        assertNotEquals(a, new Tuple10<>("v10_1", "v10_2", "v10_3", "X4", "v10_5", "v10_6", "v10_7", "v10_8", "v10_9", "v10_10"));
        assertNotEquals(a, new Tuple10<>("v10_1", "v10_2", "v10_3", "v10_4", "X5", "v10_6", "v10_7", "v10_8", "v10_9", "v10_10"));
        assertNotEquals(a, new Tuple10<>("v10_1", "v10_2", "v10_3", "v10_4", "v10_5", "X6", "v10_7", "v10_8", "v10_9", "v10_10"));
        assertNotEquals(a, new Tuple10<>("v10_1", "v10_2", "v10_3", "v10_4", "v10_5", "v10_6", "X7", "v10_8", "v10_9", "v10_10"));
        assertNotEquals(a, new Tuple10<>("v10_1", "v10_2", "v10_3", "v10_4", "v10_5", "v10_6", "v10_7", "X8", "v10_9", "v10_10"));
        assertNotEquals(a, new Tuple10<>("v10_1", "v10_2", "v10_3", "v10_4", "v10_5", "v10_6", "v10_7", "v10_8", "X9", "v10_10"));
        assertNotEquals(a, new Tuple10<>("v10_1", "v10_2", "v10_3", "v10_4", "v10_5", "v10_6", "v10_7", "v10_8", "v10_9", "X10"));
        assertTrue(a.toString().startsWith("Tuple10{"));
        assertTrue(a.toString().contains("value1="));
    }

    @Test
    public void testTuple11() {
        Tuple11<String, String, String, String, String, String, String, String, String, String, String> a = new Tuple11<>("v11_1", "v11_2", "v11_3", "v11_4", "v11_5", "v11_6", "v11_7", "v11_8", "v11_9", "v11_10", "v11_11");
        assertEquals("v11_1", a.getValue1());
        assertEquals("v11_2", a.getValue2());
        assertEquals("v11_3", a.getValue3());
        assertEquals("v11_4", a.getValue4());
        assertEquals("v11_5", a.getValue5());
        assertEquals("v11_6", a.getValue6());
        assertEquals("v11_7", a.getValue7());
        assertEquals("v11_8", a.getValue8());
        assertEquals("v11_9", a.getValue9());
        assertEquals("v11_10", a.getValue10());
        assertEquals("v11_11", a.getValue11());
        assertEquals(11, a.getSize());
        Tuple11<String, String, String, String, String, String, String, String, String, String, String> b = new Tuple11<>("v11_1", "v11_2", "v11_3", "v11_4", "v11_5", "v11_6", "v11_7", "v11_8", "v11_9", "v11_10", "v11_11");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(a.equals(a));
        assertNotEquals(null, a);
        assertNotEquals(a, "not-a-tuple");
        assertNotEquals(a, new Tuple11<>("X1", "v11_2", "v11_3", "v11_4", "v11_5", "v11_6", "v11_7", "v11_8", "v11_9", "v11_10", "v11_11"));
        assertNotEquals(a, new Tuple11<>("v11_1", "X2", "v11_3", "v11_4", "v11_5", "v11_6", "v11_7", "v11_8", "v11_9", "v11_10", "v11_11"));
        assertNotEquals(a, new Tuple11<>("v11_1", "v11_2", "X3", "v11_4", "v11_5", "v11_6", "v11_7", "v11_8", "v11_9", "v11_10", "v11_11"));
        assertNotEquals(a, new Tuple11<>("v11_1", "v11_2", "v11_3", "X4", "v11_5", "v11_6", "v11_7", "v11_8", "v11_9", "v11_10", "v11_11"));
        assertNotEquals(a, new Tuple11<>("v11_1", "v11_2", "v11_3", "v11_4", "X5", "v11_6", "v11_7", "v11_8", "v11_9", "v11_10", "v11_11"));
        assertNotEquals(a, new Tuple11<>("v11_1", "v11_2", "v11_3", "v11_4", "v11_5", "X6", "v11_7", "v11_8", "v11_9", "v11_10", "v11_11"));
        assertNotEquals(a, new Tuple11<>("v11_1", "v11_2", "v11_3", "v11_4", "v11_5", "v11_6", "X7", "v11_8", "v11_9", "v11_10", "v11_11"));
        assertNotEquals(a, new Tuple11<>("v11_1", "v11_2", "v11_3", "v11_4", "v11_5", "v11_6", "v11_7", "X8", "v11_9", "v11_10", "v11_11"));
        assertNotEquals(a, new Tuple11<>("v11_1", "v11_2", "v11_3", "v11_4", "v11_5", "v11_6", "v11_7", "v11_8", "X9", "v11_10", "v11_11"));
        assertNotEquals(a, new Tuple11<>("v11_1", "v11_2", "v11_3", "v11_4", "v11_5", "v11_6", "v11_7", "v11_8", "v11_9", "X10", "v11_11"));
        assertNotEquals(a, new Tuple11<>("v11_1", "v11_2", "v11_3", "v11_4", "v11_5", "v11_6", "v11_7", "v11_8", "v11_9", "v11_10", "X11"));
        assertTrue(a.toString().startsWith("Tuple11{"));
        assertTrue(a.toString().contains("value1="));
    }

    @Test
    public void testTuple12() {
        Tuple12<String, String, String, String, String, String, String, String, String, String, String, String> a = new Tuple12<>("v12_1", "v12_2", "v12_3", "v12_4", "v12_5", "v12_6", "v12_7", "v12_8", "v12_9", "v12_10", "v12_11", "v12_12");
        assertEquals("v12_1", a.getValue1());
        assertEquals("v12_2", a.getValue2());
        assertEquals("v12_3", a.getValue3());
        assertEquals("v12_4", a.getValue4());
        assertEquals("v12_5", a.getValue5());
        assertEquals("v12_6", a.getValue6());
        assertEquals("v12_7", a.getValue7());
        assertEquals("v12_8", a.getValue8());
        assertEquals("v12_9", a.getValue9());
        assertEquals("v12_10", a.getValue10());
        assertEquals("v12_11", a.getValue11());
        assertEquals("v12_12", a.getValue12());
        assertEquals(12, a.getSize());
        Tuple12<String, String, String, String, String, String, String, String, String, String, String, String> b = new Tuple12<>("v12_1", "v12_2", "v12_3", "v12_4", "v12_5", "v12_6", "v12_7", "v12_8", "v12_9", "v12_10", "v12_11", "v12_12");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(a.equals(a));
        assertNotEquals(null, a);
        assertNotEquals(a, "not-a-tuple");
        assertNotEquals(a, new Tuple12<>("X1", "v12_2", "v12_3", "v12_4", "v12_5", "v12_6", "v12_7", "v12_8", "v12_9", "v12_10", "v12_11", "v12_12"));
        assertNotEquals(a, new Tuple12<>("v12_1", "X2", "v12_3", "v12_4", "v12_5", "v12_6", "v12_7", "v12_8", "v12_9", "v12_10", "v12_11", "v12_12"));
        assertNotEquals(a, new Tuple12<>("v12_1", "v12_2", "X3", "v12_4", "v12_5", "v12_6", "v12_7", "v12_8", "v12_9", "v12_10", "v12_11", "v12_12"));
        assertNotEquals(a, new Tuple12<>("v12_1", "v12_2", "v12_3", "X4", "v12_5", "v12_6", "v12_7", "v12_8", "v12_9", "v12_10", "v12_11", "v12_12"));
        assertNotEquals(a, new Tuple12<>("v12_1", "v12_2", "v12_3", "v12_4", "X5", "v12_6", "v12_7", "v12_8", "v12_9", "v12_10", "v12_11", "v12_12"));
        assertNotEquals(a, new Tuple12<>("v12_1", "v12_2", "v12_3", "v12_4", "v12_5", "X6", "v12_7", "v12_8", "v12_9", "v12_10", "v12_11", "v12_12"));
        assertNotEquals(a, new Tuple12<>("v12_1", "v12_2", "v12_3", "v12_4", "v12_5", "v12_6", "X7", "v12_8", "v12_9", "v12_10", "v12_11", "v12_12"));
        assertNotEquals(a, new Tuple12<>("v12_1", "v12_2", "v12_3", "v12_4", "v12_5", "v12_6", "v12_7", "X8", "v12_9", "v12_10", "v12_11", "v12_12"));
        assertNotEquals(a, new Tuple12<>("v12_1", "v12_2", "v12_3", "v12_4", "v12_5", "v12_6", "v12_7", "v12_8", "X9", "v12_10", "v12_11", "v12_12"));
        assertNotEquals(a, new Tuple12<>("v12_1", "v12_2", "v12_3", "v12_4", "v12_5", "v12_6", "v12_7", "v12_8", "v12_9", "X10", "v12_11", "v12_12"));
        assertNotEquals(a, new Tuple12<>("v12_1", "v12_2", "v12_3", "v12_4", "v12_5", "v12_6", "v12_7", "v12_8", "v12_9", "v12_10", "X11", "v12_12"));
        assertNotEquals(a, new Tuple12<>("v12_1", "v12_2", "v12_3", "v12_4", "v12_5", "v12_6", "v12_7", "v12_8", "v12_9", "v12_10", "v12_11", "X12"));
        assertTrue(a.toString().startsWith("Tuple12{"));
        assertTrue(a.toString().contains("value1="));
    }

    @Test
    public void testTuple13() {
        Tuple13<String, String, String, String, String, String, String, String, String, String, String, String, String> a = new Tuple13<>("v13_1", "v13_2", "v13_3", "v13_4", "v13_5", "v13_6", "v13_7", "v13_8", "v13_9", "v13_10", "v13_11", "v13_12", "v13_13");
        assertEquals("v13_1", a.getValue1());
        assertEquals("v13_2", a.getValue2());
        assertEquals("v13_3", a.getValue3());
        assertEquals("v13_4", a.getValue4());
        assertEquals("v13_5", a.getValue5());
        assertEquals("v13_6", a.getValue6());
        assertEquals("v13_7", a.getValue7());
        assertEquals("v13_8", a.getValue8());
        assertEquals("v13_9", a.getValue9());
        assertEquals("v13_10", a.getValue10());
        assertEquals("v13_11", a.getValue11());
        assertEquals("v13_12", a.getValue12());
        assertEquals("v13_13", a.getValue13());
        assertEquals(13, a.getSize());
        Tuple13<String, String, String, String, String, String, String, String, String, String, String, String, String> b = new Tuple13<>("v13_1", "v13_2", "v13_3", "v13_4", "v13_5", "v13_6", "v13_7", "v13_8", "v13_9", "v13_10", "v13_11", "v13_12", "v13_13");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(a.equals(a));
        assertNotEquals(null, a);
        assertNotEquals(a, "not-a-tuple");
        assertNotEquals(a, new Tuple13<>("X1", "v13_2", "v13_3", "v13_4", "v13_5", "v13_6", "v13_7", "v13_8", "v13_9", "v13_10", "v13_11", "v13_12", "v13_13"));
        assertNotEquals(a, new Tuple13<>("v13_1", "X2", "v13_3", "v13_4", "v13_5", "v13_6", "v13_7", "v13_8", "v13_9", "v13_10", "v13_11", "v13_12", "v13_13"));
        assertNotEquals(a, new Tuple13<>("v13_1", "v13_2", "X3", "v13_4", "v13_5", "v13_6", "v13_7", "v13_8", "v13_9", "v13_10", "v13_11", "v13_12", "v13_13"));
        assertNotEquals(a, new Tuple13<>("v13_1", "v13_2", "v13_3", "X4", "v13_5", "v13_6", "v13_7", "v13_8", "v13_9", "v13_10", "v13_11", "v13_12", "v13_13"));
        assertNotEquals(a, new Tuple13<>("v13_1", "v13_2", "v13_3", "v13_4", "X5", "v13_6", "v13_7", "v13_8", "v13_9", "v13_10", "v13_11", "v13_12", "v13_13"));
        assertNotEquals(a, new Tuple13<>("v13_1", "v13_2", "v13_3", "v13_4", "v13_5", "X6", "v13_7", "v13_8", "v13_9", "v13_10", "v13_11", "v13_12", "v13_13"));
        assertNotEquals(a, new Tuple13<>("v13_1", "v13_2", "v13_3", "v13_4", "v13_5", "v13_6", "X7", "v13_8", "v13_9", "v13_10", "v13_11", "v13_12", "v13_13"));
        assertNotEquals(a, new Tuple13<>("v13_1", "v13_2", "v13_3", "v13_4", "v13_5", "v13_6", "v13_7", "X8", "v13_9", "v13_10", "v13_11", "v13_12", "v13_13"));
        assertNotEquals(a, new Tuple13<>("v13_1", "v13_2", "v13_3", "v13_4", "v13_5", "v13_6", "v13_7", "v13_8", "X9", "v13_10", "v13_11", "v13_12", "v13_13"));
        assertNotEquals(a, new Tuple13<>("v13_1", "v13_2", "v13_3", "v13_4", "v13_5", "v13_6", "v13_7", "v13_8", "v13_9", "X10", "v13_11", "v13_12", "v13_13"));
        assertNotEquals(a, new Tuple13<>("v13_1", "v13_2", "v13_3", "v13_4", "v13_5", "v13_6", "v13_7", "v13_8", "v13_9", "v13_10", "X11", "v13_12", "v13_13"));
        assertNotEquals(a, new Tuple13<>("v13_1", "v13_2", "v13_3", "v13_4", "v13_5", "v13_6", "v13_7", "v13_8", "v13_9", "v13_10", "v13_11", "X12", "v13_13"));
        assertNotEquals(a, new Tuple13<>("v13_1", "v13_2", "v13_3", "v13_4", "v13_5", "v13_6", "v13_7", "v13_8", "v13_9", "v13_10", "v13_11", "v13_12", "X13"));
        assertTrue(a.toString().startsWith("Tuple13{"));
        assertTrue(a.toString().contains("value1="));
    }

    @Test
    public void testTuple14() {
        Tuple14<String, String, String, String, String, String, String, String, String, String, String, String, String, String> a = new Tuple14<>("v14_1", "v14_2", "v14_3", "v14_4", "v14_5", "v14_6", "v14_7", "v14_8", "v14_9", "v14_10", "v14_11", "v14_12", "v14_13", "v14_14");
        assertEquals("v14_1", a.getValue1());
        assertEquals("v14_2", a.getValue2());
        assertEquals("v14_3", a.getValue3());
        assertEquals("v14_4", a.getValue4());
        assertEquals("v14_5", a.getValue5());
        assertEquals("v14_6", a.getValue6());
        assertEquals("v14_7", a.getValue7());
        assertEquals("v14_8", a.getValue8());
        assertEquals("v14_9", a.getValue9());
        assertEquals("v14_10", a.getValue10());
        assertEquals("v14_11", a.getValue11());
        assertEquals("v14_12", a.getValue12());
        assertEquals("v14_13", a.getValue13());
        assertEquals("v14_14", a.getValue14());
        assertEquals(14, a.getSize());
        Tuple14<String, String, String, String, String, String, String, String, String, String, String, String, String, String> b = new Tuple14<>("v14_1", "v14_2", "v14_3", "v14_4", "v14_5", "v14_6", "v14_7", "v14_8", "v14_9", "v14_10", "v14_11", "v14_12", "v14_13", "v14_14");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(a.equals(a));
        assertNotEquals(null, a);
        assertNotEquals(a, "not-a-tuple");
        assertNotEquals(a, new Tuple14<>("X1", "v14_2", "v14_3", "v14_4", "v14_5", "v14_6", "v14_7", "v14_8", "v14_9", "v14_10", "v14_11", "v14_12", "v14_13", "v14_14"));
        assertNotEquals(a, new Tuple14<>("v14_1", "X2", "v14_3", "v14_4", "v14_5", "v14_6", "v14_7", "v14_8", "v14_9", "v14_10", "v14_11", "v14_12", "v14_13", "v14_14"));
        assertNotEquals(a, new Tuple14<>("v14_1", "v14_2", "X3", "v14_4", "v14_5", "v14_6", "v14_7", "v14_8", "v14_9", "v14_10", "v14_11", "v14_12", "v14_13", "v14_14"));
        assertNotEquals(a, new Tuple14<>("v14_1", "v14_2", "v14_3", "X4", "v14_5", "v14_6", "v14_7", "v14_8", "v14_9", "v14_10", "v14_11", "v14_12", "v14_13", "v14_14"));
        assertNotEquals(a, new Tuple14<>("v14_1", "v14_2", "v14_3", "v14_4", "X5", "v14_6", "v14_7", "v14_8", "v14_9", "v14_10", "v14_11", "v14_12", "v14_13", "v14_14"));
        assertNotEquals(a, new Tuple14<>("v14_1", "v14_2", "v14_3", "v14_4", "v14_5", "X6", "v14_7", "v14_8", "v14_9", "v14_10", "v14_11", "v14_12", "v14_13", "v14_14"));
        assertNotEquals(a, new Tuple14<>("v14_1", "v14_2", "v14_3", "v14_4", "v14_5", "v14_6", "X7", "v14_8", "v14_9", "v14_10", "v14_11", "v14_12", "v14_13", "v14_14"));
        assertNotEquals(a, new Tuple14<>("v14_1", "v14_2", "v14_3", "v14_4", "v14_5", "v14_6", "v14_7", "X8", "v14_9", "v14_10", "v14_11", "v14_12", "v14_13", "v14_14"));
        assertNotEquals(a, new Tuple14<>("v14_1", "v14_2", "v14_3", "v14_4", "v14_5", "v14_6", "v14_7", "v14_8", "X9", "v14_10", "v14_11", "v14_12", "v14_13", "v14_14"));
        assertNotEquals(a, new Tuple14<>("v14_1", "v14_2", "v14_3", "v14_4", "v14_5", "v14_6", "v14_7", "v14_8", "v14_9", "X10", "v14_11", "v14_12", "v14_13", "v14_14"));
        assertNotEquals(a, new Tuple14<>("v14_1", "v14_2", "v14_3", "v14_4", "v14_5", "v14_6", "v14_7", "v14_8", "v14_9", "v14_10", "X11", "v14_12", "v14_13", "v14_14"));
        assertNotEquals(a, new Tuple14<>("v14_1", "v14_2", "v14_3", "v14_4", "v14_5", "v14_6", "v14_7", "v14_8", "v14_9", "v14_10", "v14_11", "X12", "v14_13", "v14_14"));
        assertNotEquals(a, new Tuple14<>("v14_1", "v14_2", "v14_3", "v14_4", "v14_5", "v14_6", "v14_7", "v14_8", "v14_9", "v14_10", "v14_11", "v14_12", "X13", "v14_14"));
        assertNotEquals(a, new Tuple14<>("v14_1", "v14_2", "v14_3", "v14_4", "v14_5", "v14_6", "v14_7", "v14_8", "v14_9", "v14_10", "v14_11", "v14_12", "v14_13", "X14"));
        assertTrue(a.toString().startsWith("Tuple14{"));
        assertTrue(a.toString().contains("value1="));
    }

    @Test
    public void testTuple15() {
        Tuple15<String, String, String, String, String, String, String, String, String, String, String, String, String, String, String> a = new Tuple15<>("v15_1", "v15_2", "v15_3", "v15_4", "v15_5", "v15_6", "v15_7", "v15_8", "v15_9", "v15_10", "v15_11", "v15_12", "v15_13", "v15_14", "v15_15");
        assertEquals("v15_1", a.getValue1());
        assertEquals("v15_2", a.getValue2());
        assertEquals("v15_3", a.getValue3());
        assertEquals("v15_4", a.getValue4());
        assertEquals("v15_5", a.getValue5());
        assertEquals("v15_6", a.getValue6());
        assertEquals("v15_7", a.getValue7());
        assertEquals("v15_8", a.getValue8());
        assertEquals("v15_9", a.getValue9());
        assertEquals("v15_10", a.getValue10());
        assertEquals("v15_11", a.getValue11());
        assertEquals("v15_12", a.getValue12());
        assertEquals("v15_13", a.getValue13());
        assertEquals("v15_14", a.getValue14());
        assertEquals("v15_15", a.getValue15());
        assertEquals(15, a.getSize());
        Tuple15<String, String, String, String, String, String, String, String, String, String, String, String, String, String, String> b = new Tuple15<>("v15_1", "v15_2", "v15_3", "v15_4", "v15_5", "v15_6", "v15_7", "v15_8", "v15_9", "v15_10", "v15_11", "v15_12", "v15_13", "v15_14", "v15_15");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(a.equals(a));
        assertNotEquals(null, a);
        assertNotEquals(a, "not-a-tuple");
        assertNotEquals(a, new Tuple15<>("X1", "v15_2", "v15_3", "v15_4", "v15_5", "v15_6", "v15_7", "v15_8", "v15_9", "v15_10", "v15_11", "v15_12", "v15_13", "v15_14", "v15_15"));
        assertNotEquals(a, new Tuple15<>("v15_1", "X2", "v15_3", "v15_4", "v15_5", "v15_6", "v15_7", "v15_8", "v15_9", "v15_10", "v15_11", "v15_12", "v15_13", "v15_14", "v15_15"));
        assertNotEquals(a, new Tuple15<>("v15_1", "v15_2", "X3", "v15_4", "v15_5", "v15_6", "v15_7", "v15_8", "v15_9", "v15_10", "v15_11", "v15_12", "v15_13", "v15_14", "v15_15"));
        assertNotEquals(a, new Tuple15<>("v15_1", "v15_2", "v15_3", "X4", "v15_5", "v15_6", "v15_7", "v15_8", "v15_9", "v15_10", "v15_11", "v15_12", "v15_13", "v15_14", "v15_15"));
        assertNotEquals(a, new Tuple15<>("v15_1", "v15_2", "v15_3", "v15_4", "X5", "v15_6", "v15_7", "v15_8", "v15_9", "v15_10", "v15_11", "v15_12", "v15_13", "v15_14", "v15_15"));
        assertNotEquals(a, new Tuple15<>("v15_1", "v15_2", "v15_3", "v15_4", "v15_5", "X6", "v15_7", "v15_8", "v15_9", "v15_10", "v15_11", "v15_12", "v15_13", "v15_14", "v15_15"));
        assertNotEquals(a, new Tuple15<>("v15_1", "v15_2", "v15_3", "v15_4", "v15_5", "v15_6", "X7", "v15_8", "v15_9", "v15_10", "v15_11", "v15_12", "v15_13", "v15_14", "v15_15"));
        assertNotEquals(a, new Tuple15<>("v15_1", "v15_2", "v15_3", "v15_4", "v15_5", "v15_6", "v15_7", "X8", "v15_9", "v15_10", "v15_11", "v15_12", "v15_13", "v15_14", "v15_15"));
        assertNotEquals(a, new Tuple15<>("v15_1", "v15_2", "v15_3", "v15_4", "v15_5", "v15_6", "v15_7", "v15_8", "X9", "v15_10", "v15_11", "v15_12", "v15_13", "v15_14", "v15_15"));
        assertNotEquals(a, new Tuple15<>("v15_1", "v15_2", "v15_3", "v15_4", "v15_5", "v15_6", "v15_7", "v15_8", "v15_9", "X10", "v15_11", "v15_12", "v15_13", "v15_14", "v15_15"));
        assertNotEquals(a, new Tuple15<>("v15_1", "v15_2", "v15_3", "v15_4", "v15_5", "v15_6", "v15_7", "v15_8", "v15_9", "v15_10", "X11", "v15_12", "v15_13", "v15_14", "v15_15"));
        assertNotEquals(a, new Tuple15<>("v15_1", "v15_2", "v15_3", "v15_4", "v15_5", "v15_6", "v15_7", "v15_8", "v15_9", "v15_10", "v15_11", "X12", "v15_13", "v15_14", "v15_15"));
        assertNotEquals(a, new Tuple15<>("v15_1", "v15_2", "v15_3", "v15_4", "v15_5", "v15_6", "v15_7", "v15_8", "v15_9", "v15_10", "v15_11", "v15_12", "X13", "v15_14", "v15_15"));
        assertNotEquals(a, new Tuple15<>("v15_1", "v15_2", "v15_3", "v15_4", "v15_5", "v15_6", "v15_7", "v15_8", "v15_9", "v15_10", "v15_11", "v15_12", "v15_13", "X14", "v15_15"));
        assertNotEquals(a, new Tuple15<>("v15_1", "v15_2", "v15_3", "v15_4", "v15_5", "v15_6", "v15_7", "v15_8", "v15_9", "v15_10", "v15_11", "v15_12", "v15_13", "v15_14", "X15"));
        assertTrue(a.toString().startsWith("Tuple15{"));
        assertTrue(a.toString().contains("value1="));
    }

    @Test
    public void testTuple16() {
        Tuple16<String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String> a = new Tuple16<>("v16_1", "v16_2", "v16_3", "v16_4", "v16_5", "v16_6", "v16_7", "v16_8", "v16_9", "v16_10", "v16_11", "v16_12", "v16_13", "v16_14", "v16_15", "v16_16");
        assertEquals("v16_1", a.getValue1());
        assertEquals("v16_2", a.getValue2());
        assertEquals("v16_3", a.getValue3());
        assertEquals("v16_4", a.getValue4());
        assertEquals("v16_5", a.getValue5());
        assertEquals("v16_6", a.getValue6());
        assertEquals("v16_7", a.getValue7());
        assertEquals("v16_8", a.getValue8());
        assertEquals("v16_9", a.getValue9());
        assertEquals("v16_10", a.getValue10());
        assertEquals("v16_11", a.getValue11());
        assertEquals("v16_12", a.getValue12());
        assertEquals("v16_13", a.getValue13());
        assertEquals("v16_14", a.getValue14());
        assertEquals("v16_15", a.getValue15());
        assertEquals("v16_16", a.getValue16());
        assertEquals(16, a.getSize());
        Tuple16<String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String> b = new Tuple16<>("v16_1", "v16_2", "v16_3", "v16_4", "v16_5", "v16_6", "v16_7", "v16_8", "v16_9", "v16_10", "v16_11", "v16_12", "v16_13", "v16_14", "v16_15", "v16_16");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(a.equals(a));
        assertNotEquals(null, a);
        assertNotEquals(a, "not-a-tuple");
        assertNotEquals(a, new Tuple16<>("X1", "v16_2", "v16_3", "v16_4", "v16_5", "v16_6", "v16_7", "v16_8", "v16_9", "v16_10", "v16_11", "v16_12", "v16_13", "v16_14", "v16_15", "v16_16"));
        assertNotEquals(a, new Tuple16<>("v16_1", "X2", "v16_3", "v16_4", "v16_5", "v16_6", "v16_7", "v16_8", "v16_9", "v16_10", "v16_11", "v16_12", "v16_13", "v16_14", "v16_15", "v16_16"));
        assertNotEquals(a, new Tuple16<>("v16_1", "v16_2", "X3", "v16_4", "v16_5", "v16_6", "v16_7", "v16_8", "v16_9", "v16_10", "v16_11", "v16_12", "v16_13", "v16_14", "v16_15", "v16_16"));
        assertNotEquals(a, new Tuple16<>("v16_1", "v16_2", "v16_3", "X4", "v16_5", "v16_6", "v16_7", "v16_8", "v16_9", "v16_10", "v16_11", "v16_12", "v16_13", "v16_14", "v16_15", "v16_16"));
        assertNotEquals(a, new Tuple16<>("v16_1", "v16_2", "v16_3", "v16_4", "X5", "v16_6", "v16_7", "v16_8", "v16_9", "v16_10", "v16_11", "v16_12", "v16_13", "v16_14", "v16_15", "v16_16"));
        assertNotEquals(a, new Tuple16<>("v16_1", "v16_2", "v16_3", "v16_4", "v16_5", "X6", "v16_7", "v16_8", "v16_9", "v16_10", "v16_11", "v16_12", "v16_13", "v16_14", "v16_15", "v16_16"));
        assertNotEquals(a, new Tuple16<>("v16_1", "v16_2", "v16_3", "v16_4", "v16_5", "v16_6", "X7", "v16_8", "v16_9", "v16_10", "v16_11", "v16_12", "v16_13", "v16_14", "v16_15", "v16_16"));
        assertNotEquals(a, new Tuple16<>("v16_1", "v16_2", "v16_3", "v16_4", "v16_5", "v16_6", "v16_7", "X8", "v16_9", "v16_10", "v16_11", "v16_12", "v16_13", "v16_14", "v16_15", "v16_16"));
        assertNotEquals(a, new Tuple16<>("v16_1", "v16_2", "v16_3", "v16_4", "v16_5", "v16_6", "v16_7", "v16_8", "X9", "v16_10", "v16_11", "v16_12", "v16_13", "v16_14", "v16_15", "v16_16"));
        assertNotEquals(a, new Tuple16<>("v16_1", "v16_2", "v16_3", "v16_4", "v16_5", "v16_6", "v16_7", "v16_8", "v16_9", "X10", "v16_11", "v16_12", "v16_13", "v16_14", "v16_15", "v16_16"));
        assertNotEquals(a, new Tuple16<>("v16_1", "v16_2", "v16_3", "v16_4", "v16_5", "v16_6", "v16_7", "v16_8", "v16_9", "v16_10", "X11", "v16_12", "v16_13", "v16_14", "v16_15", "v16_16"));
        assertNotEquals(a, new Tuple16<>("v16_1", "v16_2", "v16_3", "v16_4", "v16_5", "v16_6", "v16_7", "v16_8", "v16_9", "v16_10", "v16_11", "X12", "v16_13", "v16_14", "v16_15", "v16_16"));
        assertNotEquals(a, new Tuple16<>("v16_1", "v16_2", "v16_3", "v16_4", "v16_5", "v16_6", "v16_7", "v16_8", "v16_9", "v16_10", "v16_11", "v16_12", "X13", "v16_14", "v16_15", "v16_16"));
        assertNotEquals(a, new Tuple16<>("v16_1", "v16_2", "v16_3", "v16_4", "v16_5", "v16_6", "v16_7", "v16_8", "v16_9", "v16_10", "v16_11", "v16_12", "v16_13", "X14", "v16_15", "v16_16"));
        assertNotEquals(a, new Tuple16<>("v16_1", "v16_2", "v16_3", "v16_4", "v16_5", "v16_6", "v16_7", "v16_8", "v16_9", "v16_10", "v16_11", "v16_12", "v16_13", "v16_14", "X15", "v16_16"));
        assertNotEquals(a, new Tuple16<>("v16_1", "v16_2", "v16_3", "v16_4", "v16_5", "v16_6", "v16_7", "v16_8", "v16_9", "v16_10", "v16_11", "v16_12", "v16_13", "v16_14", "v16_15", "X16"));
        assertTrue(a.toString().startsWith("Tuple16{"));
        assertTrue(a.toString().contains("value1="));
    }

    @Test
    public void testTuple17() {
        Tuple17<String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String> a = new Tuple17<>("v17_1", "v17_2", "v17_3", "v17_4", "v17_5", "v17_6", "v17_7", "v17_8", "v17_9", "v17_10", "v17_11", "v17_12", "v17_13", "v17_14", "v17_15", "v17_16", "v17_17");
        assertEquals("v17_1", a.getValue1());
        assertEquals("v17_2", a.getValue2());
        assertEquals("v17_3", a.getValue3());
        assertEquals("v17_4", a.getValue4());
        assertEquals("v17_5", a.getValue5());
        assertEquals("v17_6", a.getValue6());
        assertEquals("v17_7", a.getValue7());
        assertEquals("v17_8", a.getValue8());
        assertEquals("v17_9", a.getValue9());
        assertEquals("v17_10", a.getValue10());
        assertEquals("v17_11", a.getValue11());
        assertEquals("v17_12", a.getValue12());
        assertEquals("v17_13", a.getValue13());
        assertEquals("v17_14", a.getValue14());
        assertEquals("v17_15", a.getValue15());
        assertEquals("v17_16", a.getValue16());
        assertEquals("v17_17", a.getValue17());
        assertEquals(17, a.getSize());
        Tuple17<String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String> b = new Tuple17<>("v17_1", "v17_2", "v17_3", "v17_4", "v17_5", "v17_6", "v17_7", "v17_8", "v17_9", "v17_10", "v17_11", "v17_12", "v17_13", "v17_14", "v17_15", "v17_16", "v17_17");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(a.equals(a));
        assertNotEquals(null, a);
        assertNotEquals(a, "not-a-tuple");
        assertNotEquals(a, new Tuple17<>("X1", "v17_2", "v17_3", "v17_4", "v17_5", "v17_6", "v17_7", "v17_8", "v17_9", "v17_10", "v17_11", "v17_12", "v17_13", "v17_14", "v17_15", "v17_16", "v17_17"));
        assertNotEquals(a, new Tuple17<>("v17_1", "X2", "v17_3", "v17_4", "v17_5", "v17_6", "v17_7", "v17_8", "v17_9", "v17_10", "v17_11", "v17_12", "v17_13", "v17_14", "v17_15", "v17_16", "v17_17"));
        assertNotEquals(a, new Tuple17<>("v17_1", "v17_2", "X3", "v17_4", "v17_5", "v17_6", "v17_7", "v17_8", "v17_9", "v17_10", "v17_11", "v17_12", "v17_13", "v17_14", "v17_15", "v17_16", "v17_17"));
        assertNotEquals(a, new Tuple17<>("v17_1", "v17_2", "v17_3", "X4", "v17_5", "v17_6", "v17_7", "v17_8", "v17_9", "v17_10", "v17_11", "v17_12", "v17_13", "v17_14", "v17_15", "v17_16", "v17_17"));
        assertNotEquals(a, new Tuple17<>("v17_1", "v17_2", "v17_3", "v17_4", "X5", "v17_6", "v17_7", "v17_8", "v17_9", "v17_10", "v17_11", "v17_12", "v17_13", "v17_14", "v17_15", "v17_16", "v17_17"));
        assertNotEquals(a, new Tuple17<>("v17_1", "v17_2", "v17_3", "v17_4", "v17_5", "X6", "v17_7", "v17_8", "v17_9", "v17_10", "v17_11", "v17_12", "v17_13", "v17_14", "v17_15", "v17_16", "v17_17"));
        assertNotEquals(a, new Tuple17<>("v17_1", "v17_2", "v17_3", "v17_4", "v17_5", "v17_6", "X7", "v17_8", "v17_9", "v17_10", "v17_11", "v17_12", "v17_13", "v17_14", "v17_15", "v17_16", "v17_17"));
        assertNotEquals(a, new Tuple17<>("v17_1", "v17_2", "v17_3", "v17_4", "v17_5", "v17_6", "v17_7", "X8", "v17_9", "v17_10", "v17_11", "v17_12", "v17_13", "v17_14", "v17_15", "v17_16", "v17_17"));
        assertNotEquals(a, new Tuple17<>("v17_1", "v17_2", "v17_3", "v17_4", "v17_5", "v17_6", "v17_7", "v17_8", "X9", "v17_10", "v17_11", "v17_12", "v17_13", "v17_14", "v17_15", "v17_16", "v17_17"));
        assertNotEquals(a, new Tuple17<>("v17_1", "v17_2", "v17_3", "v17_4", "v17_5", "v17_6", "v17_7", "v17_8", "v17_9", "X10", "v17_11", "v17_12", "v17_13", "v17_14", "v17_15", "v17_16", "v17_17"));
        assertNotEquals(a, new Tuple17<>("v17_1", "v17_2", "v17_3", "v17_4", "v17_5", "v17_6", "v17_7", "v17_8", "v17_9", "v17_10", "X11", "v17_12", "v17_13", "v17_14", "v17_15", "v17_16", "v17_17"));
        assertNotEquals(a, new Tuple17<>("v17_1", "v17_2", "v17_3", "v17_4", "v17_5", "v17_6", "v17_7", "v17_8", "v17_9", "v17_10", "v17_11", "X12", "v17_13", "v17_14", "v17_15", "v17_16", "v17_17"));
        assertNotEquals(a, new Tuple17<>("v17_1", "v17_2", "v17_3", "v17_4", "v17_5", "v17_6", "v17_7", "v17_8", "v17_9", "v17_10", "v17_11", "v17_12", "X13", "v17_14", "v17_15", "v17_16", "v17_17"));
        assertNotEquals(a, new Tuple17<>("v17_1", "v17_2", "v17_3", "v17_4", "v17_5", "v17_6", "v17_7", "v17_8", "v17_9", "v17_10", "v17_11", "v17_12", "v17_13", "X14", "v17_15", "v17_16", "v17_17"));
        assertNotEquals(a, new Tuple17<>("v17_1", "v17_2", "v17_3", "v17_4", "v17_5", "v17_6", "v17_7", "v17_8", "v17_9", "v17_10", "v17_11", "v17_12", "v17_13", "v17_14", "X15", "v17_16", "v17_17"));
        assertNotEquals(a, new Tuple17<>("v17_1", "v17_2", "v17_3", "v17_4", "v17_5", "v17_6", "v17_7", "v17_8", "v17_9", "v17_10", "v17_11", "v17_12", "v17_13", "v17_14", "v17_15", "X16", "v17_17"));
        assertNotEquals(a, new Tuple17<>("v17_1", "v17_2", "v17_3", "v17_4", "v17_5", "v17_6", "v17_7", "v17_8", "v17_9", "v17_10", "v17_11", "v17_12", "v17_13", "v17_14", "v17_15", "v17_16", "X17"));
        assertTrue(a.toString().startsWith("Tuple17{"));
        assertTrue(a.toString().contains("value1="));
    }

    @Test
    public void testTuple18() {
        Tuple18<String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String> a = new Tuple18<>("v18_1", "v18_2", "v18_3", "v18_4", "v18_5", "v18_6", "v18_7", "v18_8", "v18_9", "v18_10", "v18_11", "v18_12", "v18_13", "v18_14", "v18_15", "v18_16", "v18_17", "v18_18");
        assertEquals("v18_1", a.getValue1());
        assertEquals("v18_2", a.getValue2());
        assertEquals("v18_3", a.getValue3());
        assertEquals("v18_4", a.getValue4());
        assertEquals("v18_5", a.getValue5());
        assertEquals("v18_6", a.getValue6());
        assertEquals("v18_7", a.getValue7());
        assertEquals("v18_8", a.getValue8());
        assertEquals("v18_9", a.getValue9());
        assertEquals("v18_10", a.getValue10());
        assertEquals("v18_11", a.getValue11());
        assertEquals("v18_12", a.getValue12());
        assertEquals("v18_13", a.getValue13());
        assertEquals("v18_14", a.getValue14());
        assertEquals("v18_15", a.getValue15());
        assertEquals("v18_16", a.getValue16());
        assertEquals("v18_17", a.getValue17());
        assertEquals("v18_18", a.getValue18());
        assertEquals(18, a.getSize());
        Tuple18<String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String> b = new Tuple18<>("v18_1", "v18_2", "v18_3", "v18_4", "v18_5", "v18_6", "v18_7", "v18_8", "v18_9", "v18_10", "v18_11", "v18_12", "v18_13", "v18_14", "v18_15", "v18_16", "v18_17", "v18_18");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(a.equals(a));
        assertNotEquals(null, a);
        assertNotEquals(a, "not-a-tuple");
        assertNotEquals(a, new Tuple18<>("X1", "v18_2", "v18_3", "v18_4", "v18_5", "v18_6", "v18_7", "v18_8", "v18_9", "v18_10", "v18_11", "v18_12", "v18_13", "v18_14", "v18_15", "v18_16", "v18_17", "v18_18"));
        assertNotEquals(a, new Tuple18<>("v18_1", "X2", "v18_3", "v18_4", "v18_5", "v18_6", "v18_7", "v18_8", "v18_9", "v18_10", "v18_11", "v18_12", "v18_13", "v18_14", "v18_15", "v18_16", "v18_17", "v18_18"));
        assertNotEquals(a, new Tuple18<>("v18_1", "v18_2", "X3", "v18_4", "v18_5", "v18_6", "v18_7", "v18_8", "v18_9", "v18_10", "v18_11", "v18_12", "v18_13", "v18_14", "v18_15", "v18_16", "v18_17", "v18_18"));
        assertNotEquals(a, new Tuple18<>("v18_1", "v18_2", "v18_3", "X4", "v18_5", "v18_6", "v18_7", "v18_8", "v18_9", "v18_10", "v18_11", "v18_12", "v18_13", "v18_14", "v18_15", "v18_16", "v18_17", "v18_18"));
        assertNotEquals(a, new Tuple18<>("v18_1", "v18_2", "v18_3", "v18_4", "X5", "v18_6", "v18_7", "v18_8", "v18_9", "v18_10", "v18_11", "v18_12", "v18_13", "v18_14", "v18_15", "v18_16", "v18_17", "v18_18"));
        assertNotEquals(a, new Tuple18<>("v18_1", "v18_2", "v18_3", "v18_4", "v18_5", "X6", "v18_7", "v18_8", "v18_9", "v18_10", "v18_11", "v18_12", "v18_13", "v18_14", "v18_15", "v18_16", "v18_17", "v18_18"));
        assertNotEquals(a, new Tuple18<>("v18_1", "v18_2", "v18_3", "v18_4", "v18_5", "v18_6", "X7", "v18_8", "v18_9", "v18_10", "v18_11", "v18_12", "v18_13", "v18_14", "v18_15", "v18_16", "v18_17", "v18_18"));
        assertNotEquals(a, new Tuple18<>("v18_1", "v18_2", "v18_3", "v18_4", "v18_5", "v18_6", "v18_7", "X8", "v18_9", "v18_10", "v18_11", "v18_12", "v18_13", "v18_14", "v18_15", "v18_16", "v18_17", "v18_18"));
        assertNotEquals(a, new Tuple18<>("v18_1", "v18_2", "v18_3", "v18_4", "v18_5", "v18_6", "v18_7", "v18_8", "X9", "v18_10", "v18_11", "v18_12", "v18_13", "v18_14", "v18_15", "v18_16", "v18_17", "v18_18"));
        assertNotEquals(a, new Tuple18<>("v18_1", "v18_2", "v18_3", "v18_4", "v18_5", "v18_6", "v18_7", "v18_8", "v18_9", "X10", "v18_11", "v18_12", "v18_13", "v18_14", "v18_15", "v18_16", "v18_17", "v18_18"));
        assertNotEquals(a, new Tuple18<>("v18_1", "v18_2", "v18_3", "v18_4", "v18_5", "v18_6", "v18_7", "v18_8", "v18_9", "v18_10", "X11", "v18_12", "v18_13", "v18_14", "v18_15", "v18_16", "v18_17", "v18_18"));
        assertNotEquals(a, new Tuple18<>("v18_1", "v18_2", "v18_3", "v18_4", "v18_5", "v18_6", "v18_7", "v18_8", "v18_9", "v18_10", "v18_11", "X12", "v18_13", "v18_14", "v18_15", "v18_16", "v18_17", "v18_18"));
        assertNotEquals(a, new Tuple18<>("v18_1", "v18_2", "v18_3", "v18_4", "v18_5", "v18_6", "v18_7", "v18_8", "v18_9", "v18_10", "v18_11", "v18_12", "X13", "v18_14", "v18_15", "v18_16", "v18_17", "v18_18"));
        assertNotEquals(a, new Tuple18<>("v18_1", "v18_2", "v18_3", "v18_4", "v18_5", "v18_6", "v18_7", "v18_8", "v18_9", "v18_10", "v18_11", "v18_12", "v18_13", "X14", "v18_15", "v18_16", "v18_17", "v18_18"));
        assertNotEquals(a, new Tuple18<>("v18_1", "v18_2", "v18_3", "v18_4", "v18_5", "v18_6", "v18_7", "v18_8", "v18_9", "v18_10", "v18_11", "v18_12", "v18_13", "v18_14", "X15", "v18_16", "v18_17", "v18_18"));
        assertNotEquals(a, new Tuple18<>("v18_1", "v18_2", "v18_3", "v18_4", "v18_5", "v18_6", "v18_7", "v18_8", "v18_9", "v18_10", "v18_11", "v18_12", "v18_13", "v18_14", "v18_15", "X16", "v18_17", "v18_18"));
        assertNotEquals(a, new Tuple18<>("v18_1", "v18_2", "v18_3", "v18_4", "v18_5", "v18_6", "v18_7", "v18_8", "v18_9", "v18_10", "v18_11", "v18_12", "v18_13", "v18_14", "v18_15", "v18_16", "X17", "v18_18"));
        assertNotEquals(a, new Tuple18<>("v18_1", "v18_2", "v18_3", "v18_4", "v18_5", "v18_6", "v18_7", "v18_8", "v18_9", "v18_10", "v18_11", "v18_12", "v18_13", "v18_14", "v18_15", "v18_16", "v18_17", "X18"));
        assertTrue(a.toString().startsWith("Tuple18{"));
        assertTrue(a.toString().contains("value1="));
    }

    @Test
    public void testTuple19() {
        Tuple19<String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String> a = new Tuple19<>("v19_1", "v19_2", "v19_3", "v19_4", "v19_5", "v19_6", "v19_7", "v19_8", "v19_9", "v19_10", "v19_11", "v19_12", "v19_13", "v19_14", "v19_15", "v19_16", "v19_17", "v19_18", "v19_19");
        assertEquals("v19_1", a.getValue1());
        assertEquals("v19_2", a.getValue2());
        assertEquals("v19_3", a.getValue3());
        assertEquals("v19_4", a.getValue4());
        assertEquals("v19_5", a.getValue5());
        assertEquals("v19_6", a.getValue6());
        assertEquals("v19_7", a.getValue7());
        assertEquals("v19_8", a.getValue8());
        assertEquals("v19_9", a.getValue9());
        assertEquals("v19_10", a.getValue10());
        assertEquals("v19_11", a.getValue11());
        assertEquals("v19_12", a.getValue12());
        assertEquals("v19_13", a.getValue13());
        assertEquals("v19_14", a.getValue14());
        assertEquals("v19_15", a.getValue15());
        assertEquals("v19_16", a.getValue16());
        assertEquals("v19_17", a.getValue17());
        assertEquals("v19_18", a.getValue18());
        assertEquals("v19_19", a.getValue19());
        assertEquals(19, a.getSize());
        Tuple19<String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String> b = new Tuple19<>("v19_1", "v19_2", "v19_3", "v19_4", "v19_5", "v19_6", "v19_7", "v19_8", "v19_9", "v19_10", "v19_11", "v19_12", "v19_13", "v19_14", "v19_15", "v19_16", "v19_17", "v19_18", "v19_19");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(a.equals(a));
        assertNotEquals(null, a);
        assertNotEquals(a, "not-a-tuple");
        assertNotEquals(a, new Tuple19<>("X1", "v19_2", "v19_3", "v19_4", "v19_5", "v19_6", "v19_7", "v19_8", "v19_9", "v19_10", "v19_11", "v19_12", "v19_13", "v19_14", "v19_15", "v19_16", "v19_17", "v19_18", "v19_19"));
        assertNotEquals(a, new Tuple19<>("v19_1", "X2", "v19_3", "v19_4", "v19_5", "v19_6", "v19_7", "v19_8", "v19_9", "v19_10", "v19_11", "v19_12", "v19_13", "v19_14", "v19_15", "v19_16", "v19_17", "v19_18", "v19_19"));
        assertNotEquals(a, new Tuple19<>("v19_1", "v19_2", "X3", "v19_4", "v19_5", "v19_6", "v19_7", "v19_8", "v19_9", "v19_10", "v19_11", "v19_12", "v19_13", "v19_14", "v19_15", "v19_16", "v19_17", "v19_18", "v19_19"));
        assertNotEquals(a, new Tuple19<>("v19_1", "v19_2", "v19_3", "X4", "v19_5", "v19_6", "v19_7", "v19_8", "v19_9", "v19_10", "v19_11", "v19_12", "v19_13", "v19_14", "v19_15", "v19_16", "v19_17", "v19_18", "v19_19"));
        assertNotEquals(a, new Tuple19<>("v19_1", "v19_2", "v19_3", "v19_4", "X5", "v19_6", "v19_7", "v19_8", "v19_9", "v19_10", "v19_11", "v19_12", "v19_13", "v19_14", "v19_15", "v19_16", "v19_17", "v19_18", "v19_19"));
        assertNotEquals(a, new Tuple19<>("v19_1", "v19_2", "v19_3", "v19_4", "v19_5", "X6", "v19_7", "v19_8", "v19_9", "v19_10", "v19_11", "v19_12", "v19_13", "v19_14", "v19_15", "v19_16", "v19_17", "v19_18", "v19_19"));
        assertNotEquals(a, new Tuple19<>("v19_1", "v19_2", "v19_3", "v19_4", "v19_5", "v19_6", "X7", "v19_8", "v19_9", "v19_10", "v19_11", "v19_12", "v19_13", "v19_14", "v19_15", "v19_16", "v19_17", "v19_18", "v19_19"));
        assertNotEquals(a, new Tuple19<>("v19_1", "v19_2", "v19_3", "v19_4", "v19_5", "v19_6", "v19_7", "X8", "v19_9", "v19_10", "v19_11", "v19_12", "v19_13", "v19_14", "v19_15", "v19_16", "v19_17", "v19_18", "v19_19"));
        assertNotEquals(a, new Tuple19<>("v19_1", "v19_2", "v19_3", "v19_4", "v19_5", "v19_6", "v19_7", "v19_8", "X9", "v19_10", "v19_11", "v19_12", "v19_13", "v19_14", "v19_15", "v19_16", "v19_17", "v19_18", "v19_19"));
        assertNotEquals(a, new Tuple19<>("v19_1", "v19_2", "v19_3", "v19_4", "v19_5", "v19_6", "v19_7", "v19_8", "v19_9", "X10", "v19_11", "v19_12", "v19_13", "v19_14", "v19_15", "v19_16", "v19_17", "v19_18", "v19_19"));
        assertNotEquals(a, new Tuple19<>("v19_1", "v19_2", "v19_3", "v19_4", "v19_5", "v19_6", "v19_7", "v19_8", "v19_9", "v19_10", "X11", "v19_12", "v19_13", "v19_14", "v19_15", "v19_16", "v19_17", "v19_18", "v19_19"));
        assertNotEquals(a, new Tuple19<>("v19_1", "v19_2", "v19_3", "v19_4", "v19_5", "v19_6", "v19_7", "v19_8", "v19_9", "v19_10", "v19_11", "X12", "v19_13", "v19_14", "v19_15", "v19_16", "v19_17", "v19_18", "v19_19"));
        assertNotEquals(a, new Tuple19<>("v19_1", "v19_2", "v19_3", "v19_4", "v19_5", "v19_6", "v19_7", "v19_8", "v19_9", "v19_10", "v19_11", "v19_12", "X13", "v19_14", "v19_15", "v19_16", "v19_17", "v19_18", "v19_19"));
        assertNotEquals(a, new Tuple19<>("v19_1", "v19_2", "v19_3", "v19_4", "v19_5", "v19_6", "v19_7", "v19_8", "v19_9", "v19_10", "v19_11", "v19_12", "v19_13", "X14", "v19_15", "v19_16", "v19_17", "v19_18", "v19_19"));
        assertNotEquals(a, new Tuple19<>("v19_1", "v19_2", "v19_3", "v19_4", "v19_5", "v19_6", "v19_7", "v19_8", "v19_9", "v19_10", "v19_11", "v19_12", "v19_13", "v19_14", "X15", "v19_16", "v19_17", "v19_18", "v19_19"));
        assertNotEquals(a, new Tuple19<>("v19_1", "v19_2", "v19_3", "v19_4", "v19_5", "v19_6", "v19_7", "v19_8", "v19_9", "v19_10", "v19_11", "v19_12", "v19_13", "v19_14", "v19_15", "X16", "v19_17", "v19_18", "v19_19"));
        assertNotEquals(a, new Tuple19<>("v19_1", "v19_2", "v19_3", "v19_4", "v19_5", "v19_6", "v19_7", "v19_8", "v19_9", "v19_10", "v19_11", "v19_12", "v19_13", "v19_14", "v19_15", "v19_16", "X17", "v19_18", "v19_19"));
        assertNotEquals(a, new Tuple19<>("v19_1", "v19_2", "v19_3", "v19_4", "v19_5", "v19_6", "v19_7", "v19_8", "v19_9", "v19_10", "v19_11", "v19_12", "v19_13", "v19_14", "v19_15", "v19_16", "v19_17", "X18", "v19_19"));
        assertNotEquals(a, new Tuple19<>("v19_1", "v19_2", "v19_3", "v19_4", "v19_5", "v19_6", "v19_7", "v19_8", "v19_9", "v19_10", "v19_11", "v19_12", "v19_13", "v19_14", "v19_15", "v19_16", "v19_17", "v19_18", "X19"));
        assertTrue(a.toString().startsWith("Tuple19{"));
        assertTrue(a.toString().contains("value1="));
    }

    @Test
    public void testTuple20() {
        Tuple20<String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String> a = new Tuple20<>("v20_1", "v20_2", "v20_3", "v20_4", "v20_5", "v20_6", "v20_7", "v20_8", "v20_9", "v20_10", "v20_11", "v20_12", "v20_13", "v20_14", "v20_15", "v20_16", "v20_17", "v20_18", "v20_19", "v20_20");
        assertEquals("v20_1", a.getValue1());
        assertEquals("v20_2", a.getValue2());
        assertEquals("v20_3", a.getValue3());
        assertEquals("v20_4", a.getValue4());
        assertEquals("v20_5", a.getValue5());
        assertEquals("v20_6", a.getValue6());
        assertEquals("v20_7", a.getValue7());
        assertEquals("v20_8", a.getValue8());
        assertEquals("v20_9", a.getValue9());
        assertEquals("v20_10", a.getValue10());
        assertEquals("v20_11", a.getValue11());
        assertEquals("v20_12", a.getValue12());
        assertEquals("v20_13", a.getValue13());
        assertEquals("v20_14", a.getValue14());
        assertEquals("v20_15", a.getValue15());
        assertEquals("v20_16", a.getValue16());
        assertEquals("v20_17", a.getValue17());
        assertEquals("v20_18", a.getValue18());
        assertEquals("v20_19", a.getValue19());
        assertEquals("v20_20", a.getValue20());
        assertEquals(20, a.getSize());
        Tuple20<String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String> b = new Tuple20<>("v20_1", "v20_2", "v20_3", "v20_4", "v20_5", "v20_6", "v20_7", "v20_8", "v20_9", "v20_10", "v20_11", "v20_12", "v20_13", "v20_14", "v20_15", "v20_16", "v20_17", "v20_18", "v20_19", "v20_20");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(a.equals(a));
        assertNotEquals(null, a);
        assertNotEquals(a, "not-a-tuple");
        assertNotEquals(a, new Tuple20<>("X1", "v20_2", "v20_3", "v20_4", "v20_5", "v20_6", "v20_7", "v20_8", "v20_9", "v20_10", "v20_11", "v20_12", "v20_13", "v20_14", "v20_15", "v20_16", "v20_17", "v20_18", "v20_19", "v20_20"));
        assertNotEquals(a, new Tuple20<>("v20_1", "X2", "v20_3", "v20_4", "v20_5", "v20_6", "v20_7", "v20_8", "v20_9", "v20_10", "v20_11", "v20_12", "v20_13", "v20_14", "v20_15", "v20_16", "v20_17", "v20_18", "v20_19", "v20_20"));
        assertNotEquals(a, new Tuple20<>("v20_1", "v20_2", "X3", "v20_4", "v20_5", "v20_6", "v20_7", "v20_8", "v20_9", "v20_10", "v20_11", "v20_12", "v20_13", "v20_14", "v20_15", "v20_16", "v20_17", "v20_18", "v20_19", "v20_20"));
        assertNotEquals(a, new Tuple20<>("v20_1", "v20_2", "v20_3", "X4", "v20_5", "v20_6", "v20_7", "v20_8", "v20_9", "v20_10", "v20_11", "v20_12", "v20_13", "v20_14", "v20_15", "v20_16", "v20_17", "v20_18", "v20_19", "v20_20"));
        assertNotEquals(a, new Tuple20<>("v20_1", "v20_2", "v20_3", "v20_4", "X5", "v20_6", "v20_7", "v20_8", "v20_9", "v20_10", "v20_11", "v20_12", "v20_13", "v20_14", "v20_15", "v20_16", "v20_17", "v20_18", "v20_19", "v20_20"));
        assertNotEquals(a, new Tuple20<>("v20_1", "v20_2", "v20_3", "v20_4", "v20_5", "X6", "v20_7", "v20_8", "v20_9", "v20_10", "v20_11", "v20_12", "v20_13", "v20_14", "v20_15", "v20_16", "v20_17", "v20_18", "v20_19", "v20_20"));
        assertNotEquals(a, new Tuple20<>("v20_1", "v20_2", "v20_3", "v20_4", "v20_5", "v20_6", "X7", "v20_8", "v20_9", "v20_10", "v20_11", "v20_12", "v20_13", "v20_14", "v20_15", "v20_16", "v20_17", "v20_18", "v20_19", "v20_20"));
        assertNotEquals(a, new Tuple20<>("v20_1", "v20_2", "v20_3", "v20_4", "v20_5", "v20_6", "v20_7", "X8", "v20_9", "v20_10", "v20_11", "v20_12", "v20_13", "v20_14", "v20_15", "v20_16", "v20_17", "v20_18", "v20_19", "v20_20"));
        assertNotEquals(a, new Tuple20<>("v20_1", "v20_2", "v20_3", "v20_4", "v20_5", "v20_6", "v20_7", "v20_8", "X9", "v20_10", "v20_11", "v20_12", "v20_13", "v20_14", "v20_15", "v20_16", "v20_17", "v20_18", "v20_19", "v20_20"));
        assertNotEquals(a, new Tuple20<>("v20_1", "v20_2", "v20_3", "v20_4", "v20_5", "v20_6", "v20_7", "v20_8", "v20_9", "X10", "v20_11", "v20_12", "v20_13", "v20_14", "v20_15", "v20_16", "v20_17", "v20_18", "v20_19", "v20_20"));
        assertNotEquals(a, new Tuple20<>("v20_1", "v20_2", "v20_3", "v20_4", "v20_5", "v20_6", "v20_7", "v20_8", "v20_9", "v20_10", "X11", "v20_12", "v20_13", "v20_14", "v20_15", "v20_16", "v20_17", "v20_18", "v20_19", "v20_20"));
        assertNotEquals(a, new Tuple20<>("v20_1", "v20_2", "v20_3", "v20_4", "v20_5", "v20_6", "v20_7", "v20_8", "v20_9", "v20_10", "v20_11", "X12", "v20_13", "v20_14", "v20_15", "v20_16", "v20_17", "v20_18", "v20_19", "v20_20"));
        assertNotEquals(a, new Tuple20<>("v20_1", "v20_2", "v20_3", "v20_4", "v20_5", "v20_6", "v20_7", "v20_8", "v20_9", "v20_10", "v20_11", "v20_12", "X13", "v20_14", "v20_15", "v20_16", "v20_17", "v20_18", "v20_19", "v20_20"));
        assertNotEquals(a, new Tuple20<>("v20_1", "v20_2", "v20_3", "v20_4", "v20_5", "v20_6", "v20_7", "v20_8", "v20_9", "v20_10", "v20_11", "v20_12", "v20_13", "X14", "v20_15", "v20_16", "v20_17", "v20_18", "v20_19", "v20_20"));
        assertNotEquals(a, new Tuple20<>("v20_1", "v20_2", "v20_3", "v20_4", "v20_5", "v20_6", "v20_7", "v20_8", "v20_9", "v20_10", "v20_11", "v20_12", "v20_13", "v20_14", "X15", "v20_16", "v20_17", "v20_18", "v20_19", "v20_20"));
        assertNotEquals(a, new Tuple20<>("v20_1", "v20_2", "v20_3", "v20_4", "v20_5", "v20_6", "v20_7", "v20_8", "v20_9", "v20_10", "v20_11", "v20_12", "v20_13", "v20_14", "v20_15", "X16", "v20_17", "v20_18", "v20_19", "v20_20"));
        assertNotEquals(a, new Tuple20<>("v20_1", "v20_2", "v20_3", "v20_4", "v20_5", "v20_6", "v20_7", "v20_8", "v20_9", "v20_10", "v20_11", "v20_12", "v20_13", "v20_14", "v20_15", "v20_16", "X17", "v20_18", "v20_19", "v20_20"));
        assertNotEquals(a, new Tuple20<>("v20_1", "v20_2", "v20_3", "v20_4", "v20_5", "v20_6", "v20_7", "v20_8", "v20_9", "v20_10", "v20_11", "v20_12", "v20_13", "v20_14", "v20_15", "v20_16", "v20_17", "X18", "v20_19", "v20_20"));
        assertNotEquals(a, new Tuple20<>("v20_1", "v20_2", "v20_3", "v20_4", "v20_5", "v20_6", "v20_7", "v20_8", "v20_9", "v20_10", "v20_11", "v20_12", "v20_13", "v20_14", "v20_15", "v20_16", "v20_17", "v20_18", "X19", "v20_20"));
        assertNotEquals(a, new Tuple20<>("v20_1", "v20_2", "v20_3", "v20_4", "v20_5", "v20_6", "v20_7", "v20_8", "v20_9", "v20_10", "v20_11", "v20_12", "v20_13", "v20_14", "v20_15", "v20_16", "v20_17", "v20_18", "v20_19", "X20"));
        assertTrue(a.toString().startsWith("Tuple20{"));
        assertTrue(a.toString().contains("value1="));
    }

}
