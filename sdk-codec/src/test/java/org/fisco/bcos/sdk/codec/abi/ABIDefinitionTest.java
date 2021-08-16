package org.fisco.bcos.sdk.codec.abi;

import org.fisco.bcos.sdk.codec.abi.wrapper.ABIDefinition;
import org.junit.Assert;
import org.junit.Test;

public class ABIDefinitionTest {
    @Test
    public void typeIntTest() {
        ABIDefinition.Type type = new ABIDefinition.Type("int");
        Assert.assertTrue(!type.isList());
        Assert.assertTrue(!type.isFixedList());
        Assert.assertTrue(!type.isDynamicList());
        Assert.assertTrue(type.getRawType().equals("int"));
        Assert.assertTrue(type.getType().equals("int"));
        Assert.assertTrue(type.getDimensions().isEmpty());
        Assert.assertTrue(type.getLastDimension() == 0);
    }

    @Test
    public void typeStringTest() {
        ABIDefinition.Type type = new ABIDefinition.Type("string");
        Assert.assertTrue(!type.isList());
        Assert.assertTrue(!type.isFixedList());
        Assert.assertTrue(!type.isDynamicList());
        Assert.assertTrue(type.getRawType().equals("string"));
        Assert.assertTrue(type.getType().equals("string"));
        Assert.assertTrue(type.getDimensions().isEmpty());
        Assert.assertTrue(type.getLastDimension() == 0);

        ABIDefinition.Type type1 = type.reduceDimensionAndGetType();
        Assert.assertTrue(!type1.isList());
        Assert.assertTrue(!type1.isFixedList());
        Assert.assertTrue(!type1.isDynamicList());
        Assert.assertTrue(type1.getRawType().equals("string"));
        Assert.assertTrue(type1.getType().equals("string"));
        Assert.assertTrue(type1.getDimensions().size() == 0);
        Assert.assertTrue(type1.getLastDimension() == 0);
    }

    @Test
    public void typeStringFixedTest() {
        ABIDefinition.Type type = new ABIDefinition.Type("string[5]");
        Assert.assertTrue(type.isList());
        Assert.assertTrue(type.isFixedList());
        Assert.assertTrue(!type.isDynamicList());
        Assert.assertTrue(type.getRawType().equals("string"));
        Assert.assertTrue(type.getType().equals("string[5]"));
        Assert.assertTrue(type.getDimensions().size() == 1);
        Assert.assertTrue(type.getLastDimension() == 5);

        ABIDefinition.Type type1 = type.reduceDimensionAndGetType();
        Assert.assertTrue(!type1.isList());
        Assert.assertTrue(!type1.isFixedList());
        Assert.assertTrue(!type1.isDynamicList());
        Assert.assertTrue(type1.getRawType().equals("string"));
        Assert.assertTrue(type1.getType().equals("string"));
        Assert.assertTrue(type1.getDimensions().size() == 0);
        Assert.assertTrue(type1.getLastDimension() == 0);
    }

    @Test
    public void typeStringFixedDynamicTest() {
        ABIDefinition.Type type = new ABIDefinition.Type("string[5][]");
        Assert.assertTrue(type.isList());
        Assert.assertTrue(!type.isFixedList());
        Assert.assertTrue(type.isDynamicList());
        Assert.assertTrue(type.getRawType().equals("string"));
        Assert.assertTrue(type.getType().equals("string[5][]"));
        Assert.assertTrue(type.getDimensions().size() == 2);
        Assert.assertTrue(type.getLastDimension() == 0);

        ABIDefinition.Type type1 = type.reduceDimensionAndGetType();
        Assert.assertTrue(type1.isList());
        Assert.assertTrue(type1.isFixedList());
        Assert.assertTrue(!type1.isDynamicList());
        Assert.assertTrue(type1.getRawType().equals("string"));
        Assert.assertTrue(type1.getType().equals("string[5]"));
        Assert.assertTrue(type1.getDimensions().size() == 1);
        Assert.assertTrue(type1.getLastDimension() == 5);
    }

    @Test
    public void typeStringDynamicDynamicDynamicTest() {
        ABIDefinition.Type type = new ABIDefinition.Type("string[][][]");
        Assert.assertTrue(type.isList());
        Assert.assertTrue(!type.isFixedList());
        Assert.assertTrue(type.isDynamicList());
        Assert.assertTrue(type.getRawType().equals("string"));
        Assert.assertTrue(type.getType().equals("string[][][]"));
        Assert.assertTrue(type.getDimensions().size() == 3);
        Assert.assertTrue(type.getLastDimension() == 0);

        ABIDefinition.Type type1 = type.reduceDimensionAndGetType();
        Assert.assertTrue(type1.isList());
        Assert.assertTrue(!type1.isFixedList());
        Assert.assertTrue(type1.isDynamicList());
        Assert.assertTrue(type1.getRawType().equals("string"));
        Assert.assertTrue(type1.getType().equals("string[][]"));
        Assert.assertTrue(type1.getDimensions().size() == 2);
        Assert.assertTrue(type1.getLastDimension() == 0);

        ABIDefinition.Type type2 = type1.reduceDimensionAndGetType();
        Assert.assertTrue(type2.isList());
        Assert.assertTrue(!type2.isFixedList());
        Assert.assertTrue(type2.isDynamicList());
        Assert.assertTrue(type2.getRawType().equals("string"));
        Assert.assertTrue(type2.getType().equals("string[]"));
        Assert.assertTrue(type2.getDimensions().size() == 1);
        Assert.assertTrue(type2.getLastDimension() == 0);

        ABIDefinition.Type type3 = type2.reduceDimensionAndGetType();
        Assert.assertTrue(!type3.isList());
        Assert.assertTrue(!type3.isFixedList());
        Assert.assertTrue(!type3.isDynamicList());
        Assert.assertTrue(type3.getRawType().equals("string"));
        Assert.assertTrue(type3.getType().equals("string"));
        Assert.assertTrue(type3.getDimensions().size() == 0);
        Assert.assertTrue(type3.getLastDimension() == 0);
    }

    @Test
    public void typeStringFixedFixedFixedTest() {
        ABIDefinition.Type type = new ABIDefinition.Type("string[8][9][10]");
        Assert.assertTrue(type.isList());
        Assert.assertTrue(type.isFixedList());
        Assert.assertTrue(!type.isDynamicList());
        Assert.assertTrue(type.getRawType().equals("string"));
        Assert.assertTrue(type.getType().equals("string[8][9][10]"));
        Assert.assertTrue(type.getDimensions().size() == 3);
        Assert.assertTrue(type.getLastDimension() == 10);

        ABIDefinition.Type type1 = type.reduceDimensionAndGetType();
        Assert.assertTrue(type1.isList());
        Assert.assertTrue(type1.isFixedList());
        Assert.assertTrue(!type1.isDynamicList());
        Assert.assertTrue(type1.getRawType().equals("string"));
        Assert.assertTrue(type1.getType().equals("string[8][9]"));
        Assert.assertTrue(type1.getDimensions().size() == 2);
        Assert.assertTrue(type1.getLastDimension() == 9);

        ABIDefinition.Type type2 = type1.reduceDimensionAndGetType();
        Assert.assertTrue(type2.isList());
        Assert.assertTrue(type2.isFixedList());
        Assert.assertTrue(!type2.isDynamicList());
        Assert.assertTrue(type2.getRawType().equals("string"));
        Assert.assertTrue(type2.getType().equals("string[8]"));
        Assert.assertTrue(type2.getDimensions().size() == 1);
        Assert.assertTrue(type2.getLastDimension() == 8);

        ABIDefinition.Type type3 = type2.reduceDimensionAndGetType();
        Assert.assertTrue(!type3.isList());
        Assert.assertTrue(!type3.isFixedList());
        Assert.assertTrue(!type3.isDynamicList());
        Assert.assertTrue(type3.getRawType().equals("string"));
        Assert.assertTrue(type3.getType().equals("string"));
        Assert.assertTrue(type3.getDimensions().size() == 0);
        Assert.assertTrue(type3.getLastDimension() == 0);
    }
}
