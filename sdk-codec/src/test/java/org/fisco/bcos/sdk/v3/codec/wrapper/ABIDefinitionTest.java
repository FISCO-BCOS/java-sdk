package org.fisco.bcos.sdk.v3.codec.wrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class ABIDefinitionTest {
    private String abiString = "[\n" +
            "  {\n" +
            "    \"inputs\": [\n" +
            "      {\n" +
            "        \"internalType\": \"address\",\n" +
            "        \"name\": \"_owner\",\n" +
            "        \"type\": \"address\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"internalType\": \"address\",\n" +
            "        \"name\": \"_spender\",\n" +
            "        \"type\": \"address\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"name\": \"allowance\",\n" +
            "    \"outputs\": [\n" +
            "      {\n" +
            "        \"internalType\": \"uint256\",\n" +
            "        \"name\": \"remaining\",\n" +
            "        \"type\": \"uint256\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"stateMutability\": \"view\",\n" +
            "    \"type\": \"function\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"conflictFields\": [\n" +
            "      {\n" +
            "        \"kind\": 3,\n" +
            "        \"slot\": \"0x2\",\n" +
            "        \"value\": [0]\n" +
            "      },\n" +
            "      {\n" +
            "        \"kind\": 3,\n" +
            "        \"slot\": \"0x2\",\n" +
            "        \"value\": [1]\n" +
            "      }\n" +
            "    ],\n" +
            "    \"inputs\": [\n" +
            "      {\n" +
            "        \"internalType\": \"address\",\n" +
            "        \"name\": \"\",\n" +
            "        \"type\": \"address\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"internalType\": \"address\",\n" +
            "        \"name\": \"\",\n" +
            "        \"type\": \"address\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"name\": \"allowed\",\n" +
            "    \"outputs\": [\n" +
            "      {\n" +
            "        \"internalType\": \"uint256\",\n" +
            "        \"name\": \"\",\n" +
            "        \"type\": \"uint256\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"stateMutability\": \"view\",\n" +
            "    \"type\": \"function\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"conflictFields\": [\n" +
            "      {\n" +
            "        \"kind\": 2,\n" +
            "        \"slot\": \"0x2\",\n" +
            "        \"value\": [0]\n" +
            "      }\n" +
            "    ],\n" +
            "    \"inputs\": [\n" +
            "      {\n" +
            "        \"internalType\": \"address\",\n" +
            "        \"name\": \"_spender\",\n" +
            "        \"type\": \"address\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"internalType\": \"uint256\",\n" +
            "        \"name\": \"_value\",\n" +
            "        \"type\": \"uint256\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"name\": \"approve\",\n" +
            "    \"outputs\": [\n" +
            "      {\n" +
            "        \"internalType\": \"bool\",\n" +
            "        \"name\": \"success\",\n" +
            "        \"type\": \"bool\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"stateMutability\": \"nonpayable\",\n" +
            "    \"type\": \"function\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"inputs\": [\n" +
            "      {\n" +
            "        \"internalType\": \"address\",\n" +
            "        \"name\": \"_owner\",\n" +
            "        \"type\": \"address\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"name\": \"balanceOf\",\n" +
            "    \"outputs\": [\n" +
            "      {\n" +
            "        \"internalType\": \"uint256\",\n" +
            "        \"name\": \"balance\",\n" +
            "        \"type\": \"uint256\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"stateMutability\": \"view\",\n" +
            "    \"type\": \"function\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"conflictFields\": [\n" +
            "      {\n" +
            "        \"kind\": 3,\n" +
            "        \"slot\": \"0x1\",\n" +
            "        \"value\": [0]\n" +
            "      }\n" +
            "    ],\n" +
            "    \"inputs\": [\n" +
            "      {\n" +
            "        \"internalType\": \"address\",\n" +
            "        \"name\": \"\",\n" +
            "        \"type\": \"address\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"name\": \"balances\",\n" +
            "    \"outputs\": [\n" +
            "      {\n" +
            "        \"internalType\": \"uint256\",\n" +
            "        \"name\": \"\",\n" +
            "        \"type\": \"uint256\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"stateMutability\": \"view\",\n" +
            "    \"type\": \"function\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"conflictFields\": [\n" +
            "      {\n" +
            "        \"kind\": 4,\n" +
            "        \"slot\": \"0x5\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"inputs\": [],\n" +
            "    \"name\": \"decimals\",\n" +
            "    \"outputs\": [\n" +
            "      {\n" +
            "        \"internalType\": \"uint8\",\n" +
            "        \"name\": \"\",\n" +
            "        \"type\": \"uint8\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"stateMutability\": \"view\",\n" +
            "    \"type\": \"function\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"conflictFields\": [\n" +
            "      {\n" +
            "        \"kind\": 3,\n" +
            "        \"slot\": \"0x3\",\n" +
            "        \"value\": [0]\n" +
            "      }\n" +
            "    ],\n" +
            "    \"inputs\": [\n" +
            "      {\n" +
            "        \"internalType\": \"uint256\",\n" +
            "        \"name\": \"\",\n" +
            "        \"type\": \"uint256\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"name\": \"map3\",\n" +
            "    \"outputs\": [\n" +
            "      {\n" +
            "        \"internalType\": \"uint256\",\n" +
            "        \"name\": \"\",\n" +
            "        \"type\": \"uint256\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"stateMutability\": \"view\",\n" +
            "    \"type\": \"function\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"conflictFields\": [\n" +
            "      {\n" +
            "        \"kind\": 4,\n" +
            "        \"slot\": \"0x4\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"kind\": 4,\n" +
            "        \"slot\": \"0x4\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"kind\": 4,\n" +
            "        \"slot\": \"0x4\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"inputs\": [],\n" +
            "    \"name\": \"name\",\n" +
            "    \"outputs\": [\n" +
            "      {\n" +
            "        \"internalType\": \"string\",\n" +
            "        \"name\": \"\",\n" +
            "        \"type\": \"string\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"stateMutability\": \"view\",\n" +
            "    \"type\": \"function\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"conflictFields\": [\n" +
            "      {\n" +
            "        \"kind\": 3,\n" +
            "        \"slot\": \"0x3\",\n" +
            "        \"value\": [1]\n" +
            "      }\n" +
            "    ],\n" +
            "    \"inputs\": [\n" +
            "      {\n" +
            "        \"components\": [\n" +
            "          {\n" +
            "            \"internalType\": \"uint256\",\n" +
            "            \"name\": \"a\",\n" +
            "            \"type\": \"uint256\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"internalType\": \"uint256\",\n" +
            "            \"name\": \"b\",\n" +
            "            \"type\": \"uint256\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"internalType\": \"struct S\",\n" +
            "        \"name\": \"s\",\n" +
            "        \"type\": \"tuple\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"name\": \"set3\",\n" +
            "    \"outputs\": [],\n" +
            "    \"stateMutability\": \"nonpayable\",\n" +
            "    \"type\": \"function\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"conflictFields\": [\n" +
            "      {\n" +
            "        \"kind\": 4,\n" +
            "        \"slot\": \"0x6\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"kind\": 4,\n" +
            "        \"slot\": \"0x6\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"kind\": 4,\n" +
            "        \"slot\": \"0x6\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"inputs\": [],\n" +
            "    \"name\": \"symbol\",\n" +
            "    \"outputs\": [\n" +
            "      {\n" +
            "        \"internalType\": \"string\",\n" +
            "        \"name\": \"\",\n" +
            "        \"type\": \"string\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"stateMutability\": \"view\",\n" +
            "    \"type\": \"function\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"conflictFields\": [\n" +
            "      {\n" +
            "        \"kind\": 4,\n" +
            "        \"slot\": \"0x0\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"inputs\": [],\n" +
            "    \"name\": \"totalSupply\",\n" +
            "    \"outputs\": [\n" +
            "      {\n" +
            "        \"internalType\": \"uint256\",\n" +
            "        \"name\": \"\",\n" +
            "        \"type\": \"uint256\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"stateMutability\": \"view\",\n" +
            "    \"type\": \"function\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"conflictFields\": [\n" +
            "      {\n" +
            "        \"kind\": 2,\n" +
            "        \"slot\": \"0x1\",\n" +
            "        \"value\": [0]\n" +
            "      },\n" +
            "      {\n" +
            "        \"kind\": 2,\n" +
            "        \"slot\": \"0x1\",\n" +
            "        \"value\": [0]\n" +
            "      }\n" +
            "    ],\n" +
            "    \"inputs\": [\n" +
            "      {\n" +
            "        \"internalType\": \"address\",\n" +
            "        \"name\": \"_to\",\n" +
            "        \"type\": \"address\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"internalType\": \"uint256\",\n" +
            "        \"name\": \"_value\",\n" +
            "        \"type\": \"uint256\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"name\": \"transfer\",\n" +
            "    \"outputs\": [\n" +
            "      {\n" +
            "        \"internalType\": \"bool\",\n" +
            "        \"name\": \"success\",\n" +
            "        \"type\": \"bool\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"stateMutability\": \"nonpayable\",\n" +
            "    \"type\": \"function\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"conflictFields\": [\n" +
            "      {\n" +
            "        \"kind\": 2,\n" +
            "        \"slot\": \"0x2\",\n" +
            "        \"value\": [0]\n" +
            "      },\n" +
            "      {\n" +
            "        \"kind\": 2,\n" +
            "        \"slot\": \"0x2\",\n" +
            "        \"value\": [0]\n" +
            "      }\n" +
            "    ],\n" +
            "    \"inputs\": [\n" +
            "      {\n" +
            "        \"internalType\": \"address\",\n" +
            "        \"name\": \"_from\",\n" +
            "        \"type\": \"address\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"internalType\": \"address\",\n" +
            "        \"name\": \"_to\",\n" +
            "        \"type\": \"address\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"internalType\": \"uint256\",\n" +
            "        \"name\": \"_value\",\n" +
            "        \"type\": \"uint256\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"name\": \"transferFrom\",\n" +
            "    \"outputs\": [\n" +
            "      {\n" +
            "        \"internalType\": \"bool\",\n" +
            "        \"name\": \"success\",\n" +
            "        \"type\": \"bool\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"stateMutability\": \"nonpayable\",\n" +
            "    \"type\": \"function\"\n" +
            "  }\n" +
            "]";

    @Test
    public void decodeConflictTest() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ABIDefinition[] abiDefinitions = objectMapper.readValue(abiString, ABIDefinition[].class);
        for(int i = 0; i< abiDefinitions.length; ++i)
        {
            if(abiDefinitions[i].getConflictFields().size() != 0)
            {
                List<ABIDefinition.ConflictField> fields = abiDefinitions[i].getConflictFields();
                for(int j =0; j< fields.size(); ++j)
                {
                    Assert.assertNotEquals(fields.get(j).getKind(), Integer.valueOf(-1));
                }
            }
        }
    }

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
