package org.fisco.bcos.sdk.codec.wrapper;

import org.fisco.bcos.sdk.codec.TestUtils;
import org.fisco.bcos.sdk.codec.datatypes.*;
import org.fisco.bcos.sdk.codec.datatypes.generated.*;
import org.fisco.bcos.sdk.utils.Hex;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;

public class ABIObjectTest {

  @Test
  public void testMixedTypeEncode0() throws IOException, ClassNotFoundException {
    // int a, Info[] memory b, string memory c
    /*
     	 * {
         "0": "int256: a 100",
         "1": "tuple(string,int256,tuple(int256,int256,int256)[])[]: b Hello world!,100,1,2,3,Hello world2!,200,5,6,7",
         "2": "string: c Hello world!"
    }
     	 */

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

    ABIObject listParams = new ABIObject(ABIObject.ListType.DYNAMIC);

    ABIObject item1 = new ABIObject(ABIObject.ObjectType.STRUCT);
    item1.getStructFields().add(new ABIObject(new Utf8String("Hello world!")));
    item1.getStructFields().add(new ABIObject(new Uint256(100)));
    item1.getStructFields().add(new ABIObject(ABIObject.ListType.DYNAMIC));

    item1.getStructFields().get(2).getListValues().add(new ABIObject(ABIObject.ObjectType.STRUCT));

    item1
        .getStructFields()
        .get(2)
        .getListValues()
        .get(0)
        .getStructFields()
        .add(new ABIObject(new Uint256(1)));
    item1
        .getStructFields()
        .get(2)
        .getListValues()
        .get(0)
        .getStructFields()
        .add(new ABIObject(new Uint256(2)));
    item1
        .getStructFields()
        .get(2)
        .getListValues()
        .get(0)
        .getStructFields()
        .add(new ABIObject(new Uint256(3)));

    listParams.getListValues().add(item1);

    ABIObject item2 = new ABIObject(ABIObject.ObjectType.STRUCT);
    item2.getStructFields().add(new ABIObject(new Utf8String("Hello world2")));
    item2.getStructFields().add(new ABIObject(new Uint256(200)));
    item2.getStructFields().add(new ABIObject(ABIObject.ListType.DYNAMIC));

    item2.getStructFields().get(2).getListValues().add(new ABIObject(ABIObject.ObjectType.STRUCT));

    item2
        .getStructFields()
        .get(2)
        .getListValues()
        .get(0)
        .getStructFields()
        .add(new ABIObject(new Uint256(5)));
    item2
        .getStructFields()
        .get(2)
        .getListValues()
        .get(0)
        .getStructFields()
        .add(new ABIObject(new Uint256(6)));
    item2
        .getStructFields()
        .get(2)
        .getListValues()
        .get(0)
        .getStructFields()
        .add(new ABIObject(new Uint256(7)));

    listParams.getListValues().add(item2);

    ABIObject abiObject = new ABIObject(ABIObject.ObjectType.STRUCT);

    abiObject.getStructFields().add(new ABIObject(new Uint256(100)));
    abiObject.getStructFields().add(listParams);
    abiObject.getStructFields().add(new ABIObject(new Utf8String("Hello world!")));

    byte[] encodeBytes = abiObject.encode(false);
    String encodeHex = Hex.toHexString(encodeBytes);

    Assert.assertEquals(
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
            + "48656c6c6f20776f726c64210000000000000000000000000000000000000000",
        encodeHex);

    ABIDefinition abiDefinition =
        TestUtils.getContractABIDefinition(abiDesc).getFunctions().get("test").get(0);
    ABIObject inputObject = ABIObjectFactory.createInputObject(abiDefinition);
    ABIObject decodeObject = inputObject.decode(encodeBytes,false);

    Assert.assertEquals(encodeHex, Hex.toHexString(decodeObject.encode(false)));

    ABIObject newObjectWithoutValue = inputObject.newObjectWithoutValue();
    Assert.assertEquals(
        encodeHex, Hex.toHexString(newObjectWithoutValue.decode(encodeBytes,false).encode(false)));
  }

  @Test
  public void testSingleValue() throws IOException {
    ABIObject abiObject = new ABIObject(new Utf8String("Greetings!"));
    ABIObject structObject = new ABIObject(ABIObject.ObjectType.STRUCT);
    structObject.getStructFields().add(abiObject);
    Assert.assertEquals(
        Hex.toHexString(structObject.encode(false)),
        "0000000000000000000000000000000000000000000000000000000000000020"
            + "000000000000000000000000000000000000000000000000000000000000000a"
            + "4772656574696e67732100000000000000000000000000000000000000000000");
  }

  @Test
  public void testEncode1() throws IOException {

    ABIObject abiObject = new ABIObject(ABIObject.ObjectType.STRUCT);
    abiObject.getStructFields().add(new ABIObject(new Uint256(69)));
    abiObject.getStructFields().add(new ABIObject(new Bool(true)));

    Assert.assertEquals(
        Hex.toHexString(abiObject.encode(false)),
        "0000000000000000000000000000000000000000000000000000000000000045"
            + "0000000000000000000000000000000000000000000000000000000000000001");

    abiObject.getStructFields().clear();
    abiObject.getStructFields().add(new ABIObject(new Bytes(3, "abc".getBytes())));
    abiObject.getStructFields().add(new ABIObject(new Bytes(3, "def".getBytes())));

    Assert.assertEquals(
        Hex.toHexString(abiObject.encode(false)),
        "6162630000000000000000000000000000000000000000000000000000000000"
            + "6465660000000000000000000000000000000000000000000000000000000000");

    abiObject.getStructFields().clear();
    abiObject.getStructFields().add(new ABIObject(new DynamicBytes("dave".getBytes())));
    abiObject.getStructFields().add(new ABIObject(new Bool(true)));

    ABIObject listObject = new ABIObject(ABIObject.ListType.DYNAMIC);
    listObject.getListValues().add(new ABIObject(new Uint256(1)));
    listObject.getListValues().add(new ABIObject(new Uint256(2)));
    listObject.getListValues().add(new ABIObject(new Uint256(3)));

    abiObject.getStructFields().add(listObject);

    Assert.assertEquals(
        Hex.toHexString(abiObject.encode(false)),
        "0000000000000000000000000000000000000000000000000000000000000060"
            + "0000000000000000000000000000000000000000000000000000000000000001"
            + "00000000000000000000000000000000000000000000000000000000000000a0"
            + "0000000000000000000000000000000000000000000000000000000000000004"
            + "6461766500000000000000000000000000000000000000000000000000000000"
            + "0000000000000000000000000000000000000000000000000000000000000003"
            + "0000000000000000000000000000000000000000000000000000000000000001"
            + "0000000000000000000000000000000000000000000000000000000000000002"
            + "0000000000000000000000000000000000000000000000000000000000000003");

    abiObject.getStructFields().clear();
    abiObject.getStructFields().add(new ABIObject(new Uint256(0x123)));

    ABIObject listObject0 = new ABIObject(ABIObject.ListType.DYNAMIC);
    listObject0.getListValues().add(new ABIObject(new Uint256(0x456)));
    listObject0.getListValues().add(new ABIObject(new Uint256(0x789)));
    abiObject.getStructFields().add(listObject0);

    abiObject.getStructFields().add(new ABIObject(new Bytes10("1234567890".getBytes())));
    abiObject.getStructFields().add(new ABIObject(new DynamicBytes("Hello, world!".getBytes())));

    Assert.assertEquals(
        Hex.toHexString(abiObject.encode(false)),
        "0000000000000000000000000000000000000000000000000000000000000123"
            + "0000000000000000000000000000000000000000000000000000000000000080"
            + "3132333435363738393000000000000000000000000000000000000000000000"
            + "00000000000000000000000000000000000000000000000000000000000000e0"
            + "0000000000000000000000000000000000000000000000000000000000000002"
            + "0000000000000000000000000000000000000000000000000000000000000456"
            + "0000000000000000000000000000000000000000000000000000000000000789"
            + "000000000000000000000000000000000000000000000000000000000000000d"
            + "48656c6c6f2c20776f726c642100000000000000000000000000000000000000");

    abiObject.getStructFields().clear();

    /*
    struct A1 {
      u256[][] l1;
      string[] l2;
    };
     */
    ABIObject listObject1 = new ABIObject(ABIObject.ListType.DYNAMIC);
    ABIObject listObject1_0 = new ABIObject(ABIObject.ListType.DYNAMIC);
    ABIObject listObject1_1 = new ABIObject(ABIObject.ListType.DYNAMIC);

    listObject1_0.getListValues().add(new ABIObject(new Uint256(1)));
    listObject1_0.getListValues().add(new ABIObject(new Uint256(2)));

    listObject1_1.getListValues().add(new ABIObject(new Uint256(3)));

    listObject1.getListValues().add(listObject1_0);
    listObject1.getListValues().add(listObject1_1);

    ABIObject listObject2 = new ABIObject(ABIObject.ListType.DYNAMIC);
    listObject2.getListValues().add(new ABIObject(new Utf8String("one")));
    listObject2.getListValues().add(new ABIObject(new Utf8String("two")));
    listObject2.getListValues().add(new ABIObject(new Utf8String("three")));

    abiObject.getStructFields().add(listObject1);
    abiObject.getStructFields().add(listObject2);

    Assert.assertEquals(
        Hex.toHexString(abiObject.encode(false)),
        "0000000000000000000000000000000000000000000000000000000000000040"
            + "0000000000000000000000000000000000000000000000000000000000000140"
            + "0000000000000000000000000000000000000000000000000000000000000002"
            + "0000000000000000000000000000000000000000000000000000000000000040"
            + "00000000000000000000000000000000000000000000000000000000000000a0"
            + "0000000000000000000000000000000000000000000000000000000000000002"
            + "0000000000000000000000000000000000000000000000000000000000000001"
            + "0000000000000000000000000000000000000000000000000000000000000002"
            + "0000000000000000000000000000000000000000000000000000000000000001"
            + "0000000000000000000000000000000000000000000000000000000000000003"
            + "0000000000000000000000000000000000000000000000000000000000000003"
            + "0000000000000000000000000000000000000000000000000000000000000060"
            + "00000000000000000000000000000000000000000000000000000000000000a0"
            + "00000000000000000000000000000000000000000000000000000000000000e0"
            + "0000000000000000000000000000000000000000000000000000000000000003"
            + "6f6e650000000000000000000000000000000000000000000000000000000000"
            + "0000000000000000000000000000000000000000000000000000000000000003"
            + "74776f0000000000000000000000000000000000000000000000000000000000"
            + "0000000000000000000000000000000000000000000000000000000000000005"
            + "7468726565000000000000000000000000000000000000000000000000000000");
  }

  @Test
  public void testBoolTypeEncode() throws IOException {
    Assert.assertEquals(
        Hex.toHexString(new ABIObject(new Bool(false)).encode(false)),
        "0000000000000000000000000000000000000000000000000000000000000000");
    Assert.assertEquals(
        Hex.toHexString(new ABIObject(new Bool(true)).encode(false)),
        "0000000000000000000000000000000000000000000000000000000000000001");
  }

  @Test
  public void testIntTypeEncode() throws IOException {
    Assert.assertEquals(
        Hex.toHexString(new ABIObject(new Uint256(BigInteger.ZERO)).encode(false)),
        "0000000000000000000000000000000000000000000000000000000000000000");

    Assert.assertEquals(
        Hex.toHexString(new ABIObject(new Int256(BigInteger.ZERO)).encode(false)),
        "0000000000000000000000000000000000000000000000000000000000000000");

    Assert.assertEquals(
        Hex.toHexString(new ABIObject(new Uint256(Long.MAX_VALUE)).encode(false)),
        "0000000000000000000000000000000000000000000000007fffffffffffffff");

    Assert.assertEquals(
        Hex.toHexString(new ABIObject(new Int256(Long.MAX_VALUE)).encode(false)),
        "0000000000000000000000000000000000000000000000007fffffffffffffff");

    Assert.assertEquals(
        Hex.toHexString(
            new ABIObject(
                    new Uint256(
                        new BigInteger(
                            "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe",
                            16)))
                .encode(false)),
        "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe");
  }

  @Test
  public void testEmptyListEncode() throws IOException {

    Assert.assertEquals(
        Hex.toHexString(new ABIObject(ABIObject.ListType.DYNAMIC).encode(false)),
        "0000000000000000000000000000000000000000000000000000000000000000");
  }

  @Test
  public void testStringEncode() throws IOException {
    Assert.assertEquals(
        Hex.toHexString(new ABIObject(new Utf8String("Hello, world!")).encode(false)),
        "000000000000000000000000000000000000000000000000000000000000000d"
            + "48656c6c6f2c20776f726c642100000000000000000000000000000000000000");

    Assert.assertEquals(
        Hex.toHexString(new ABIObject(new Utf8String("")).encode(false)),
        "0000000000000000000000000000000000000000000000000000000000000000");
  }

  @Test
  public void testAddressEncode() throws IOException {
    Address address = new Address("0xbe5422d15f39373eb0a97ff8c10fbd0e40e29338");
    Assert.assertEquals(
        Hex.toHexString(new ABIObject(address).encode(false)),
        "000000000000000000000000be5422d15f39373eb0a97ff8c10fbd0e40e29338");
  }

  @Test
  public void testStaticBytesEncode() throws IOException {
    Bytes staticBytes = new Bytes6(new byte[] {0, 1, 2, 3, 4, 5});
    Assert.assertEquals(
        Hex.toHexString(new ABIObject(staticBytes).encode(false)),
        "0001020304050000000000000000000000000000000000000000000000000000");

    Bytes empty = new Bytes1(new byte[] {0});
    Assert.assertEquals(
        Hex.toHexString(new ABIObject(empty).encode(false)),
        "0000000000000000000000000000000000000000000000000000000000000000");

    Bytes dave = new Bytes4("dave".getBytes());
    Assert.assertEquals(
        Hex.toHexString(new ABIObject(dave).encode(false)),
        "6461766500000000000000000000000000000000000000000000000000000000");
  }

  @Test
  public void testDynamicBytesEncode() throws IOException {
    DynamicBytes dynamicBytes = new DynamicBytes(new byte[] {0, 1, 2, 3, 4, 5});
    Assert.assertEquals(
        Hex.toHexString(new ABIObject(dynamicBytes).encode(false)),
        "0000000000000000000000000000000000000000000000000000000000000006"
            + "0001020304050000000000000000000000000000000000000000000000000000");

    DynamicBytes empty = new DynamicBytes(new byte[] {0});
    Assert.assertEquals(
        Hex.toHexString(new ABIObject(empty).encode(false)),
        "0000000000000000000000000000000000000000000000000000000000000001"
            + "0000000000000000000000000000000000000000000000000000000000000000");

    DynamicBytes dave = new DynamicBytes("dave".getBytes());
    Assert.assertEquals(
        Hex.toHexString(new ABIObject(dave).encode(false)),
        "0000000000000000000000000000000000000000000000000000000000000004"
            + "6461766500000000000000000000000000000000000000000000000000000000");

    DynamicBytes loremIpsum =
        new DynamicBytes(
            ("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod "
                    + "tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim "
                    + "veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex "
                    + "ea commodo consequat. Duis aute irure dolor in reprehenderit in "
                    + "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur "
                    + "sint occaecat cupidatat non proident, sunt in culpa qui officia "
                    + "deserunt mollit anim id est laborum.")
                .getBytes());
    Assert.assertEquals(
        Hex.toHexString(new ABIObject(loremIpsum).encode(false)),
        "00000000000000000000000000000000000000000000000000000000000001bd"
            + "4c6f72656d20697073756d20646f6c6f722073697420616d65742c20636f6e73"
            + "656374657475722061646970697363696e6720656c69742c2073656420646f20"
            + "656975736d6f642074656d706f7220696e6369646964756e74207574206c6162"
            + "6f726520657420646f6c6f7265206d61676e6120616c697175612e2055742065"
            + "6e696d206164206d696e696d2076656e69616d2c2071756973206e6f73747275"
            + "6420657865726369746174696f6e20756c6c616d636f206c61626f726973206e"
            + "69736920757420616c697175697020657820656120636f6d6d6f646f20636f6e"
            + "7365717561742e2044756973206175746520697275726520646f6c6f7220696e"
            + "20726570726568656e646572697420696e20766f6c7570746174652076656c69"
            + "7420657373652063696c6c756d20646f6c6f726520657520667567696174206e"
            + "756c6c612070617269617475722e204578636570746575722073696e74206f63"
            + "63616563617420637570696461746174206e6f6e2070726f6964656e742c2073"
            + "756e7420696e2063756c706120717569206f666669636961206465736572756e"
            + "74206d6f6c6c697420616e696d20696420657374206c61626f72756d2e000000");
  }
}
