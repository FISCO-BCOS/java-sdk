package org.fisco.bcos.sdk.codec.abi;

import org.fisco.bcos.sdk.codec.abi.wrapper.*;
import org.fisco.bcos.sdk.utils.Hex;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

class Item {
    private final BigInteger a;
    private final BigInteger b;
    private final BigInteger c;

    public Item(BigInteger a, BigInteger b, BigInteger c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }
}

class Info {
    private final String name;
    private final BigInteger count;
    private final Item[] items;

    public Info(String name, BigInteger count, Item[] items) {
        this.name = name;
        this.count = count;
        this.items = items;
    }
}

public class ABICodecTest {
    private final String abiDesc =
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
    private final String encoded =
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

    private final String encodedWithMethodId = "00a3c75d" + this.encoded;

    @Test
    public void testEncodeFromString() {
        List<String> args = new ArrayList<String>();
        args.add("100");
        // [{"name": "Hello world!", "count": 100, "items": [{"a": 1, "b": 2, "c": 3}]}, {"name":
        // "Hello world2", "count": 200, "items": [{"a": 1, "b": 2, "c": 3}]}]
        args.add(
                "[{\"name\": \"Hello world!\", \"count\": 100, \"items\": [{\"a\": 1, \"b\": 2, \"c\": 3}]}, {\"name\": \"Hello world2\", \"count\": 200, \"items\": [{\"a\": 5, \"b\": 6, \"c\": 7}]}]");
        args.add("Hello world!");

        List<Object> argsObjects = new ArrayList<Object>();
        argsObjects.add(new BigInteger("100"));
        List<Info> listParams = new ArrayList<Info>();
        Item item1 = new Item(new BigInteger("1"), new BigInteger("2"), new BigInteger("3"));
        Item[] listItem1 = {item1};
        Info info1 = new Info("Hello world!", new BigInteger("100"), listItem1);
        listParams.add(info1);
        Item item2 = new Item(new BigInteger("5"), new BigInteger("6"), new BigInteger("7"));
        Item[] listItem2 = {item2};
        Info info2 = new Info("Hello world2", new BigInteger("200"), listItem2);
        listParams.add(info2);
        argsObjects.add(listParams);
        argsObjects.add("Hello world!");

        ABICodec abiCodec = new ABICodec(TestUtils.getCryptoSuite());
        try {
            // Method
            // encode
            Assert.assertEquals(
                    this.encodedWithMethodId,
                    Hex.toHexString(abiCodec.encodeMethodFromString(this.abiDesc, "test", args)));
            Assert.assertEquals(
                    this.encodedWithMethodId,
                    Hex.toHexString(abiCodec.encodeMethod(this.abiDesc, "test", argsObjects)));
            // decode
            ContractABIDefinition contractABIDefinition = TestUtils.getContractABIDefinition(this.abiDesc);
            ABIObject inputObject =
                    ABIObjectFactory.createInputObject(
                            contractABIDefinition.getFunctions().get("test").get(0));
            ABICodecObject abiCodecObject = new ABICodecObject();
            List<Object> abiObjects = abiCodecObject.decodeJavaObject(inputObject, this.encoded);
            Assert.assertEquals(
                    this.encodedWithMethodId,
                    Hex.toHexString(abiCodec.encodeMethod(this.abiDesc, "test", abiObjects)));
            // MethodById String & JavaObject
            ABIDefinition test = contractABIDefinition.getFunctions().get("test").get(0);
            Assert.assertEquals(
                    this.encodedWithMethodId,
                    Hex.toHexString(
                            abiCodec.encodeMethodByIdFromString(
                                    this.abiDesc, test.getMethodId(TestUtils.getCryptoSuite()), args)));
            Assert.assertEquals(
                    this.encodedWithMethodId,
                    Hex.toHexString(
                            abiCodec.encodeMethodById(
                                    this.abiDesc, test.getMethodId(TestUtils.getCryptoSuite()), abiObjects)));
        } catch (ABICodecException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testEncodeFromStringWithInvalidParams() {
        List<String> args = new ArrayList<String>();
        ABICodec abiCodec = new ABICodec(TestUtils.getCryptoSuite());
        try {
            abiCodec.encodeMethodFromString(this.abiDesc, "test", args);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ABICodecException);
        }
    }

    @Test
    public void testEncodeConsctructor() {
        List<String> args = new ArrayList<String>();
        ABICodec abiCodec = new ABICodec(TestUtils.getCryptoSuite());
        try {
            abiCodec.encodeConstructorFromString(this.abiDesc, "0xaaaaaaaa", args);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testEncodeConsctructorWithInvalidParams() {
        List<String> args = new ArrayList<String>();
        args.add("invalid");
        ABICodec abiCodec = new ABICodec(TestUtils.getCryptoSuite());
        try {
            abiCodec.encodeConstructorFromString(this.abiDesc, "BIN", args);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ABICodecException);
        }
    }

    @Test
    public void testEncodeByInterface() {
        ABICodec abiCodec = new ABICodec(TestUtils.getCryptoSuite());
        List<Object> argsObjects = new ArrayList<Object>();
        List<BigInteger> b1 = new ArrayList<BigInteger>();
        b1.add(new BigInteger("100"));
        b1.add(new BigInteger("200"));
        argsObjects.add(b1);
        List<BigInteger> b2 = new ArrayList<BigInteger>();
        b2.add(new BigInteger("100"));
        b2.add(new BigInteger("200"));
        b2.add(new BigInteger("300"));
        argsObjects.add(b2);
        byte[] b = "1234".getBytes();
        argsObjects.add(b);
        String a = "0x5678";
        argsObjects.add(a);
        try {
            byte[] s1 =
                    abiCodec.encodeMethodByInterface("call(uint256[2],uint256[],bytes,address)", argsObjects);
            String abi =
                    "[{\"constant\":false,\"inputs\":[{\"name\":\"u1\",\"type\":\"uint256[2]\"},{\"name\":\"u2\",\"type\":\"uint256[]\"},{\"name\":\"b\",\"type\":\"bytes\"},{\"name\":\"a\",\"type\":\"address\"}],\"name\":\"call\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"get\",\"outputs\":[{\"name\":\"u\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"a\",\"type\":\"uint256\"},{\"name\":\"s\",\"type\":\"string\"}],\"name\":\"add\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"u\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"a\",\"type\":\"uint256\"}],\"name\":\"LogAdd1\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"u\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"a\",\"type\":\"uint256\"}],\"name\":\"LogAdd2\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"u\",\"type\":\"uint256\"},{\"indexed\":true,\"name\":\"a\",\"type\":\"uint256\"},{\"indexed\":true,\"name\":\"s\",\"type\":\"string\"}],\"name\":\"LogAdd3\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"a\",\"type\":\"uint256\"}],\"name\":\"LogAdd4\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"a\",\"type\":\"uint256\"}],\"name\":\"LogAdd5\",\"type\":\"event\"}]";
            byte[] s2 = abiCodec.encodeMethod(abi, "call", argsObjects);
            Assert.assertEquals(Hex.toHexString(s1), Hex.toHexString(s2));
        } catch (ABICodecException e) {
            Assert.fail(e.getMessage());
        }
    }
}
