package org.fisco.bcos.sdk.codec.wrapper;

import org.fisco.bcos.sdk.codec.abi.TestUtils;
import org.fisco.bcos.sdk.utils.Hex;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ABIObjectCodecTest {
  String abiDesc =
      "[\n"
          + "        {\n"
          + "                \"anonymous\": false,\n"
          + "                \"inputs\": [\n"
          + "                        {\n"
          + "                                \"indexed\": false,\n"
          + "                                \"internalType\": \"int256\",\n"
          + "                                \"name\": \"a\",\n"
          + "                                \"type\": \"int256\"\n"
          + "                        },\n"
          + "                        {\n"
          + "                                \"components\": [\n"
          + "                                        {\n"
          + "                                                \"internalType\": \"string\",\n"
          + "                                                \"name\": \"name\",\n"
          + "                                                \"type\": \"string\"\n"
          + "                                        },\n"
          + "                                        {\n"
          + "                                                \"internalType\": \"int256\",\n"
          + "                                                \"name\": \"count\",\n"
          + "                                                \"type\": \"int256\"\n"
          + "                                        },\n"
          + "                                        {\n"
          + "                                                \"components\": [\n"
          + "                                                        {\n"
          + "                                                                \"internalType\": \"int256\",\n"
          + "                                                                \"name\": \"a\",\n"
          + "                                                                \"type\": \"int256\"\n"
          + "                                                        },\n"
          + "                                                        {\n"
          + "                                                                \"internalType\": \"int256\",\n"
          + "                                                                \"name\": \"b\",\n"
          + "                                                                \"type\": \"int256\"\n"
          + "                                                        },\n"
          + "                                                        {\n"
          + "                                                                \"internalType\": \"int256\",\n"
          + "                                                                \"name\": \"c\",\n"
          + "                                                                \"type\": \"int256\"\n"
          + "                                                        }\n"
          + "                                                ],\n"
          + "                                                \"internalType\": \"struct Proxy.Item[]\",\n"
          + "                                                \"name\": \"items\",\n"
          + "                                                \"type\": \"tuple[]\"\n"
          + "                                        }\n"
          + "                                ],\n"
          + "                                \"indexed\": false,\n"
          + "                                \"internalType\": \"struct Proxy.Info[]\",\n"
          + "                                \"name\": \"b\",\n"
          + "                                \"type\": \"tuple[]\"\n"
          + "                        },\n"
          + "                        {\n"
          + "                                \"indexed\": false,\n"
          + "                                \"internalType\": \"string\",\n"
          + "                                \"name\": \"c\",\n"
          + "                                \"type\": \"string\"\n"
          + "                        }\n"
          + "                ],\n"
          + "                \"name\": \"output1\",\n"
          + "                \"type\": \"event\"\n"
          + "        },\n"
          + "        {\n"
          + "                \"constant\": false,\n"
          + "                \"inputs\": [\n"
          + "                        {\n"
          + "                                \"internalType\": \"int256\",\n"
          + "                                \"name\": \"a\",\n"
          + "                                \"type\": \"int256\"\n"
          + "                        },\n"
          + "                        {\n"
          + "                                \"components\": [\n"
          + "                                        {\n"
          + "                                                \"internalType\": \"string\",\n"
          + "                                                \"name\": \"name\",\n"
          + "                                                \"type\": \"string\"\n"
          + "                                        },\n"
          + "                                        {\n"
          + "                                                \"internalType\": \"int256\",\n"
          + "                                                \"name\": \"count\",\n"
          + "                                                \"type\": \"int256\"\n"
          + "                                        },\n"
          + "                                        {\n"
          + "                                                \"components\": [\n"
          + "                                                        {\n"
          + "                                                                \"internalType\": \"int256\",\n"
          + "                                                                \"name\": \"a\",\n"
          + "                                                                \"type\": \"int256\"\n"
          + "                                                        },\n"
          + "                                                        {\n"
          + "                                                                \"internalType\": \"int256\",\n"
          + "                                                                \"name\": \"b\",\n"
          + "                                                                \"type\": \"int256\"\n"
          + "                                                        },\n"
          + "                                                        {\n"
          + "                                                                \"internalType\": \"int256\",\n"
          + "                                                                \"name\": \"c\",\n"
          + "                                                                \"type\": \"int256\"\n"
          + "                                                        }\n"
          + "                                                ],\n"
          + "                                                \"internalType\": \"struct Proxy.Item[]\",\n"
          + "                                                \"name\": \"items\",\n"
          + "                                                \"type\": \"tuple[]\"\n"
          + "                                        }\n"
          + "                                ],\n"
          + "                                \"internalType\": \"struct Proxy.Info[]\",\n"
          + "                                \"name\": \"b\",\n"
          + "                                \"type\": \"tuple[]\"\n"
          + "                        },\n"
          + "                        {\n"
          + "                                \"internalType\": \"string\",\n"
          + "                                \"name\": \"c\",\n"
          + "                                \"type\": \"string\"\n"
          + "                        }\n"
          + "                ],\n"
          + "                \"name\": \"test\",\n"
          + "                \"outputs\": [\n"
          + "                        {\n"
          + "                                \"internalType\": \"int256\",\n"
          + "                                \"name\": \"\",\n"
          + "                                \"type\": \"int256\"\n"
          + "                        }\n"
          + "                ],\n"
          + "                \"payable\": false,\n"
          + "                \"stateMutability\": \"nonpayable\",\n"
          + "                \"type\": \"function\"\n"
          + "        },\n"
          + "        {\n"
          + "                \"constant\": false,\n"
          + "                \"inputs\": [],\n"
          + "                \"name\": \"test1\",\n"
          + "                \"outputs\": [\n"
          + "                        {\n"
          + "                                \"internalType\": \"int256\",\n"
          + "                                \"name\": \"a\",\n"
          + "                                \"type\": \"int256\"\n"
          + "                        },\n"
          + "                        {\n"
          + "                                \"components\": [\n"
          + "                                        {\n"
          + "                                                \"internalType\": \"string\",\n"
          + "                                                \"name\": \"name\",\n"
          + "                                                \"type\": \"string\"\n"
          + "                                        },\n"
          + "                                        {\n"
          + "                                                \"internalType\": \"int256\",\n"
          + "                                                \"name\": \"count\",\n"
          + "                                                \"type\": \"int256\"\n"
          + "                                        },\n"
          + "                                        {\n"
          + "                                                \"components\": [\n"
          + "                                                        {\n"
          + "                                                                \"internalType\": \"int256\",\n"
          + "                                                                \"name\": \"a\",\n"
          + "                                                                \"type\": \"int256\"\n"
          + "                                                        },\n"
          + "                                                        {\n"
          + "                                                                \"internalType\": \"int256\",\n"
          + "                                                                \"name\": \"b\",\n"
          + "                                                                \"type\": \"int256\"\n"
          + "                                                        },\n"
          + "                                                        {\n"
          + "                                                                \"internalType\": \"int256\",\n"
          + "                                                                \"name\": \"c\",\n"
          + "                                                                \"type\": \"int256\"\n"
          + "                                                        }\n"
          + "                                                ],\n"
          + "                                                \"internalType\": \"struct Proxy.Item[]\",\n"
          + "                                                \"name\": \"items\",\n"
          + "                                                \"type\": \"tuple[]\"\n"
          + "                                        }\n"
          + "                                ],\n"
          + "                                \"internalType\": \"struct Proxy.Info[]\",\n"
          + "                                \"name\": \"b\",\n"
          + "                                \"type\": \"tuple[]\"\n"
          + "                        },\n"
          + "                        {\n"
          + "                                \"internalType\": \"string\",\n"
          + "                                \"name\": \"c\",\n"
          + "                                \"type\": \"string\"\n"
          + "                        }\n"
          + "                ],\n"
          + "                \"payable\": false,\n"
          + "                \"stateMutability\": \"nonpayable\",\n"
          + "                \"type\": \"function\"\n"
          + "        },\n"
          + "        {\n"
          + "                \"payable\": false,\n"
          + "                \"stateMutability\": \"nonpayable\",\n"
          + "                \"type\": \"fallback\"\n"
          + "        }\n"
          + "]";

  // int a, Info[] memory b, string memory c
  /*
  	 * {
      "0": "int256: a 100",
      "1": "tuple(string,int256,tuple(int256,int256,int256)[])[]: b Hello world!,100,1,2,3,Hello world2!,200,5,6,7",
      "2": "string: c Hello world!"
  }

  struct Item {
      int a;
      int b;
      int c;
  }

  struct Info {
      string name;
      int count;
      Item[] items;
  }

  event output1(int a, Info[] b, string c);

  function() external {

  }

  function test(int a, Info[] memory b, string memory c) public returns(int) {
      // emit output1(a, b, c);
  }
  	 */
  String encoded =
      "0000000000000000000000000000000000000000000000000000000000000064"
          + "0000000000000000000000000000000000000000000000000000000000000060"
          + "0000000000000000000000000000000000000000000000000000000000000300"
          + "0000000000000000000000000000000000000000000000000000000000000002"
          + "0000000000000000000000000000000000000000000000000000000000000040"
          + "0000000000000000000000000000000000000000000000000000000000000160"
          + "0000000000000000000000000000000000000000000000000000000000000060"
          + "0000000000000000000000000000000000000000000000000000000000000064"
          + "00000000000000000000000000000000000000000000000000000000000000a0"
          + "000000000000000000000000000000000000000000000000000000000000000c"
          + "48656c6c6f20776f726c64210000000000000000000000000000000000000000"
          + "0000000000000000000000000000000000000000000000000000000000000001"
          + "0000000000000000000000000000000000000000000000000000000000000001"
          + "0000000000000000000000000000000000000000000000000000000000000002"
          + "0000000000000000000000000000000000000000000000000000000000000003"
          + "0000000000000000000000000000000000000000000000000000000000000060"
          + "00000000000000000000000000000000000000000000000000000000000000c8"
          + "00000000000000000000000000000000000000000000000000000000000000a0"
          + "000000000000000000000000000000000000000000000000000000000000000c"
          + "48656c6c6f20776f726c64320000000000000000000000000000000000000000"
          + "0000000000000000000000000000000000000000000000000000000000000001"
          + "0000000000000000000000000000000000000000000000000000000000000005"
          + "0000000000000000000000000000000000000000000000000000000000000006"
          + "0000000000000000000000000000000000000000000000000000000000000007"
          + "000000000000000000000000000000000000000000000000000000000000000c"
          + "48656c6c6f20776f726c64210000000000000000000000000000000000000000";

  @Test
  public void testLoadABIJSON() throws IOException {
    ContractABIDefinition contractABIDefinition = TestUtils.getContractABIDefinition(this.abiDesc);

    Assert.assertEquals(2, contractABIDefinition.getFunctions().size());
    Assert.assertEquals(1, contractABIDefinition.getEvents().size());

    List<ABIDefinition> functions = contractABIDefinition.getFunctions().get("test");
    ABIObjectFactory abiObjectFactory = new ABIObjectFactory();
    ABIObject inputABIObject = ABIObjectFactory.createInputObject(functions.get(0));
    ABIObject obj = inputABIObject.decode(Hex.decode(this.encoded));
    ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
    // List<String> decodeJson = abiCodecJsonWrapper.decode(obj, encoded);
    // Assert.assertEquals(Arrays.toString(decodeJson.toArray(new String[0])), "[ [ \"Hello
    // world!\", 100, [ [ 1, 2, 3 ] ] ], [ \"Hello world2\", 200, [ [ 5, 6, 7 ] ] ], [ \"Hello
    // world!\", 100, [ [ 1, 2, 3 ] ] ], [ \"Hello world2\", 200, [ [ 5, 6, 7 ] ] ] ]");
    String buffer = Hex.toHexString(obj.encode());

    Assert.assertEquals(this.encoded, buffer);
  }

  @Test
  public void testEncodeByJSON() throws Exception {

    ContractABIDefinition contractABIDefinition = TestUtils.getContractABIDefinition(this.abiDesc);

    List<ABIDefinition> functions = contractABIDefinition.getFunctions().get("test");
    ABIObjectFactory abiObjectFactory = new ABIObjectFactory();
    ABIObject inputABIObject = ABIObjectFactory.createInputObject(functions.get(0));

    List<String> args = new ArrayList<String>();
    args.add("100");

    // [{"name": "Hello world!", "count": 100, "items": [{"a": 1, "b": 2, "c": 3}]}, {"name":
    // "Hello world2", "count": 200, "items": [{"a": 1, "b": 2, "c": 3}]}]
    args.add(
        "[{\"name\": \"Hello world!\", \"count\": 100, \"items\": [{\"a\": 1, \"b\": 2, \"c\": 3}]}, {\"name\": \"Hello world2\", \"count\": 200, \"items\": [{\"a\": 5, \"b\": 6, \"c\": 7}]}]");
    args.add("Hello world!");

    ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
    ABIObject encodedObj = abiCodecJsonWrapper.encode(inputABIObject, args);

    ABIObject inputABIObject0 = ABIObjectFactory.createInputObject(functions.get(0));
    List<String> decodeArgs = abiCodecJsonWrapper.decode(inputABIObject0, Hex.decode(this.encoded));

    // Assert.assertArrayEquals(args.toArray(), decodeArgs.toArray());
    Assert.assertEquals(this.encoded, Hex.toHexString(encodedObj.encode()));
  }

  @Test
  public void testBytesEncode() throws Exception {
    String proxyDesc =
        "[{\n"
            + "	\"constant\": false,\n"
            + "	\"inputs\": [{\n"
            + "		\"name\": \"_args\",\n"
            + "		\"type\": \"string[]\"\n"
            + "	}],\n"
            + "	\"name\": \"commitTransaction\",\n"
            + "	\"outputs\": [{\n"
            + "		\"name\": \"\",\n"
            + "		\"type\": \"string[]\"\n"
            + "	}],\n"
            + "	\"payable\": false,\n"
            + "	\"stateMutability\": \"nonpayable\",\n"
            + "	\"type\": \"function\"\n"
            + "}, {\n"
            + "	\"constant\": false,\n"
            + "	\"inputs\": [{\n"
            + "		\"name\": \"_args\",\n"
            + "		\"type\": \"string[]\"\n"
            + "	}],\n"
            + "	\"name\": \"setMaxStep\",\n"
            + "	\"outputs\": [],\n"
            + "	\"payable\": false,\n"
            + "	\"stateMutability\": \"nonpayable\",\n"
            + "	\"type\": \"function\"\n"
            + "}, {\n"
            + "	\"constant\": true,\n"
            + "	\"inputs\": [{\n"
            + "		\"name\": \"_args\",\n"
            + "		\"type\": \"string[]\"\n"
            + "	}],\n"
            + "	\"name\": \"getPaths\",\n"
            + "	\"outputs\": [{\n"
            + "		\"name\": \"\",\n"
            + "		\"type\": \"string[]\"\n"
            + "	}],\n"
            + "	\"payable\": false,\n"
            + "	\"stateMutability\": \"view\",\n"
            + "	\"type\": \"function\"\n"
            + "}, {\n"
            + "	\"constant\": false,\n"
            + "	\"inputs\": [{\n"
            + "		\"name\": \"_args\",\n"
            + "		\"type\": \"string[]\"\n"
            + "	}],\n"
            + "	\"name\": \"rollbackTransaction\",\n"
            + "	\"outputs\": [{\n"
            + "		\"name\": \"\",\n"
            + "		\"type\": \"string[]\"\n"
            + "	}],\n"
            + "	\"payable\": false,\n"
            + "	\"stateMutability\": \"nonpayable\",\n"
            + "	\"type\": \"function\"\n"
            + "}, {\n"
            + "	\"constant\": true,\n"
            + "	\"inputs\": [],\n"
            + "	\"name\": \"getLatestTransaction\",\n"
            + "	\"outputs\": [{\n"
            + "		\"name\": \"\",\n"
            + "		\"type\": \"string\"\n"
            + "	}],\n"
            + "	\"payable\": false,\n"
            + "	\"stateMutability\": \"view\",\n"
            + "	\"type\": \"function\"\n"
            + "}, {\n"
            + "	\"constant\": false,\n"
            + "	\"inputs\": [{\n"
            + "		\"name\": \"_transactionID\",\n"
            + "		\"type\": \"string\"\n"
            + "	}, {\n"
            + "		\"name\": \"_seq\",\n"
            + "		\"type\": \"uint256\"\n"
            + "	}, {\n"
            + "		\"name\": \"_path\",\n"
            + "		\"type\": \"string\"\n"
            + "	}, {\n"
            + "		\"name\": \"_func\",\n"
            + "		\"type\": \"string\"\n"
            + "	}, {\n"
            + "		\"name\": \"_args\",\n"
            + "		\"type\": \"bytes\"\n"
            + "	}],\n"
            + "	\"name\": \"sendTransaction\",\n"
            + "	\"outputs\": [{\n"
            + "		\"name\": \"\",\n"
            + "		\"type\": \"bytes\"\n"
            + "	}],\n"
            + "	\"payable\": false,\n"
            + "	\"stateMutability\": \"nonpayable\",\n"
            + "	\"type\": \"function\"\n"
            + "}, {\n"
            + "	\"constant\": true,\n"
            + "	\"inputs\": [{\n"
            + "		\"name\": \"_args\",\n"
            + "		\"type\": \"string[]\"\n"
            + "	}],\n"
            + "	\"name\": \"getVersion\",\n"
            + "	\"outputs\": [{\n"
            + "		\"name\": \"\",\n"
            + "		\"type\": \"string[]\"\n"
            + "	}],\n"
            + "	\"payable\": false,\n"
            + "	\"stateMutability\": \"pure\",\n"
            + "	\"type\": \"function\"\n"
            + "}, {\n"
            + "	\"constant\": false,\n"
            + "	\"inputs\": [{\n"
            + "		\"name\": \"_args\",\n"
            + "		\"type\": \"string[]\"\n"
            + "	}],\n"
            + "	\"name\": \"rollbackAndDeleteTransaction\",\n"
            + "	\"outputs\": [{\n"
            + "		\"name\": \"\",\n"
            + "		\"type\": \"string[]\"\n"
            + "	}],\n"
            + "	\"payable\": false,\n"
            + "	\"stateMutability\": \"nonpayable\",\n"
            + "	\"type\": \"function\"\n"
            + "}, {\n"
            + "	\"constant\": true,\n"
            + "	\"inputs\": [],\n"
            + "	\"name\": \"getLatestTransactionInfo\",\n"
            + "	\"outputs\": [{\n"
            + "		\"name\": \"\",\n"
            + "		\"type\": \"string[]\"\n"
            + "	}],\n"
            + "	\"payable\": false,\n"
            + "	\"stateMutability\": \"view\",\n"
            + "	\"type\": \"function\"\n"
            + "}, {\n"
            + "	\"constant\": true,\n"
            + "	\"inputs\": [{\n"
            + "		\"name\": \"_str\",\n"
            + "		\"type\": \"string\"\n"
            + "	}],\n"
            + "	\"name\": \"stringToUint256\",\n"
            + "	\"outputs\": [{\n"
            + "		\"name\": \"\",\n"
            + "		\"type\": \"uint256\"\n"
            + "	}],\n"
            + "	\"payable\": false,\n"
            + "	\"stateMutability\": \"pure\",\n"
            + "	\"type\": \"function\"\n"
            + "}, {\n"
            + "	\"constant\": false,\n"
            + "	\"inputs\": [{\n"
            + "		\"name\": \"_transactionID\",\n"
            + "		\"type\": \"string\"\n"
            + "	}, {\n"
            + "		\"name\": \"_path\",\n"
            + "		\"type\": \"string\"\n"
            + "	}, {\n"
            + "		\"name\": \"_func\",\n"
            + "		\"type\": \"string\"\n"
            + "	}, {\n"
            + "		\"name\": \"_args\",\n"
            + "		\"type\": \"bytes\"\n"
            + "	}],\n"
            + "	\"name\": \"constantCall\",\n"
            + "	\"outputs\": [{\n"
            + "		\"name\": \"\",\n"
            + "		\"type\": \"bytes\"\n"
            + "	}],\n"
            + "	\"payable\": false,\n"
            + "	\"stateMutability\": \"nonpayable\",\n"
            + "	\"type\": \"function\"\n"
            + "}, {\n"
            + "	\"constant\": true,\n"
            + "	\"inputs\": [{\n"
            + "		\"name\": \"_args\",\n"
            + "		\"type\": \"string[]\"\n"
            + "	}],\n"
            + "	\"name\": \"getMaxStep\",\n"
            + "	\"outputs\": [{\n"
            + "		\"name\": \"\",\n"
            + "		\"type\": \"string[]\"\n"
            + "	}],\n"
            + "	\"payable\": false,\n"
            + "	\"stateMutability\": \"view\",\n"
            + "	\"type\": \"function\"\n"
            + "}, {\n"
            + "	\"constant\": true,\n"
            + "	\"inputs\": [{\n"
            + "		\"name\": \"_args\",\n"
            + "		\"type\": \"string[]\"\n"
            + "	}],\n"
            + "	\"name\": \"getTransactionInfo\",\n"
            + "	\"outputs\": [{\n"
            + "		\"name\": \"\",\n"
            + "		\"type\": \"string[]\"\n"
            + "	}],\n"
            + "	\"payable\": false,\n"
            + "	\"stateMutability\": \"view\",\n"
            + "	\"type\": \"function\"\n"
            + "}, {\n"
            + "	\"constant\": false,\n"
            + "	\"inputs\": [{\n"
            + "		\"name\": \"_args\",\n"
            + "		\"type\": \"string[]\"\n"
            + "	}],\n"
            + "	\"name\": \"addPath\",\n"
            + "	\"outputs\": [],\n"
            + "	\"payable\": false,\n"
            + "	\"stateMutability\": \"nonpayable\",\n"
            + "	\"type\": \"function\"\n"
            + "}, {\n"
            + "	\"constant\": false,\n"
            + "	\"inputs\": [{\n"
            + "		\"name\": \"_args\",\n"
            + "		\"type\": \"string[]\"\n"
            + "	}],\n"
            + "	\"name\": \"startTransaction\",\n"
            + "	\"outputs\": [{\n"
            + "		\"name\": \"\",\n"
            + "		\"type\": \"string[]\"\n"
            + "	}],\n"
            + "	\"payable\": false,\n"
            + "	\"stateMutability\": \"nonpayable\",\n"
            + "	\"type\": \"function\"\n"
            + "}, {\n"
            + "	\"constant\": false,\n"
            + "	\"inputs\": [{\n"
            + "		\"name\": \"_args\",\n"
            + "		\"type\": \"string[]\"\n"
            + "	}],\n"
            + "	\"name\": \"deletePathList\",\n"
            + "	\"outputs\": [],\n"
            + "	\"payable\": false,\n"
            + "	\"stateMutability\": \"nonpayable\",\n"
            + "	\"type\": \"function\"\n"
            + "}, {\n"
            + "	\"inputs\": [],\n"
            + "	\"payable\": false,\n"
            + "	\"stateMutability\": \"nonpayable\",\n"
            + "	\"type\": \"constructor\"\n"
            + "}]";

    // ABIObjectCodecJsonWrapper abiFactory = new ABIObjectCodecJsonWrapper();
    ContractABIDefinition contractABIDefinition = TestUtils.getContractABIDefinition(proxyDesc);

    List<ABIDefinition> functions = contractABIDefinition.getFunctions().get("constantCall");
    ABIObject inputABIObject = ABIObjectFactory.createInputObject(functions.get(0));

    List<String> args = new ArrayList<String>();
    args.add("arg112345678901234567890123456789012345678901234567890");
    args.add("arg212345678901234567890123456789012345678901234567890");
    args.add("arg312345678901234567890123456789012345678901234567890");

    String bytesValue = "0x123456789874321";
    byte[] encode = Hex.encode(bytesValue.getBytes());

    args.add(ABICodecJsonWrapper.HexEncodedDataPrefix + new String(encode));

    ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
    ABIObject encodedObj = abiCodecJsonWrapper.encode(inputABIObject, args);
    List<String> decodeArgs = abiCodecJsonWrapper.decode(inputABIObject, encodedObj.encode());
    Assert.assertArrayEquals(args.toArray(), decodeArgs.toArray());

    List<ABIDefinition> functions0 = contractABIDefinition.getFunctions().get("commitTransaction");
    ABIObject inputABIObject0 = ABIObjectFactory.createInputObject(functions0.get(0));

    List<String> args0 = new ArrayList<String>();
    args0.add(
        "[\"arg112345678901234567890123456789012345678901234567890\",\"arg112345678901234567890123456789012345678901234567890\",\"arg112345678901234567890123456789012345678901234567890\",\"arg112345678901234567890123456789012345678901234567890\"]");

    ABIObject encodedObj0 = abiCodecJsonWrapper.encode(inputABIObject0, args0);

    List<String> decodeArgs0 = abiCodecJsonWrapper.decode(inputABIObject0, encodedObj0.encode());

    // Assert.assertArrayEquals(decodeArgs.toArray(new String[0]), args0.get(0));

    for (int i = 0; i < args.size() - 1; i++) {
      Assert.assertEquals(args.get(i), decodeArgs.get(i));
    }

    byte[] decode =
            Hex.decode(decodeArgs.get(args.size() - 1).substring(ABICodecJsonWrapper.HexEncodedDataPrefix.length()));

    Assert.assertEquals(new String(decode), bytesValue);
  }
}
