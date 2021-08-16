package org.fisco.bcos.sdk.codec.abi;

import org.fisco.bcos.sdk.codec.abi.wrapper.*;
import org.fisco.bcos.sdk.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.utils.Hex;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TableTest {
  /*
  pragmasolidity^0.4.24;
  pragmaexperimentalABIEncoderV2;
  contractTableTest
  {
      functionselect(stringmemoryname)publicviewreturns(string[]memory,int256[]memory,string[]memory){}
      functioninsert(stringmemoryname,int256item_id,stringmemoryitem_name)publicreturns(int256){}
      functionupdate(stringmemoryname,int256item_id,stringmemoryitem_name)publicreturns(int256){}
      functionremove(stringmemoryname,int256item_id)publicreturns(int256){}
  }
   */

  private static final String abiDesc =
      "[\n"
          + "  {\n"
          + "    \"constant\": false,\n"
          + "    \"inputs\": [\n"
          + "      {\n"
          + "        \"name\": \"name\",\n"
          + "        \"type\": \"string\"\n"
          + "      },\n"
          + "      {\n"
          + "        \"name\": \"item_id\",\n"
          + "        \"type\": \"int256\"\n"
          + "      },\n"
          + "      {\n"
          + "        \"name\": \"item_name\",\n"
          + "        \"type\": \"string\"\n"
          + "      }\n"
          + "    ],\n"
          + "    \"name\": \"update\",\n"
          + "    \"outputs\": [\n"
          + "      {\n"
          + "        \"name\": \"\",\n"
          + "        \"type\": \"int256\"\n"
          + "      }\n"
          + "    ],\n"
          + "    \"payable\": false,\n"
          + "    \"stateMutability\": \"nonpayable\",\n"
          + "    \"type\": \"function\"\n"
          + "  },\n"
          + "  {\n"
          + "    \"constant\": false,\n"
          + "    \"inputs\": [\n"
          + "      {\n"
          + "        \"name\": \"name\",\n"
          + "        \"type\": \"string\"\n"
          + "      },\n"
          + "      {\n"
          + "        \"name\": \"item_id\",\n"
          + "        \"type\": \"int256\"\n"
          + "      }\n"
          + "    ],\n"
          + "    \"name\": \"remove\",\n"
          + "    \"outputs\": [\n"
          + "      {\n"
          + "        \"name\": \"\",\n"
          + "        \"type\": \"int256\"\n"
          + "      }\n"
          + "    ],\n"
          + "    \"payable\": false,\n"
          + "    \"stateMutability\": \"nonpayable\",\n"
          + "    \"type\": \"function\"\n"
          + "  },\n"
          + "  {\n"
          + "    \"constant\": false,\n"
          + "    \"inputs\": [\n"
          + "      {\n"
          + "        \"name\": \"name\",\n"
          + "        \"type\": \"string\"\n"
          + "      },\n"
          + "      {\n"
          + "        \"name\": \"item_id\",\n"
          + "        \"type\": \"int256\"\n"
          + "      },\n"
          + "      {\n"
          + "        \"name\": \"item_name\",\n"
          + "        \"type\": \"string\"\n"
          + "      }\n"
          + "    ],\n"
          + "    \"name\": \"insert\",\n"
          + "    \"outputs\": [\n"
          + "      {\n"
          + "        \"name\": \"\",\n"
          + "        \"type\": \"int256\"\n"
          + "      }\n"
          + "    ],\n"
          + "    \"payable\": false,\n"
          + "    \"stateMutability\": \"nonpayable\",\n"
          + "    \"type\": \"function\"\n"
          + "  },\n"
          + "  {\n"
          + "    \"constant\": true,\n"
          + "    \"inputs\": [\n"
          + "      {\n"
          + "        \"name\": \"name\",\n"
          + "        \"type\": \"string\"\n"
          + "      }\n"
          + "    ],\n"
          + "    \"name\": \"select\",\n"
          + "    \"outputs\": [\n"
          + "      {\n"
          + "        \"name\": \"\",\n"
          + "        \"type\": \"string[]\"\n"
          + "      },\n"
          + "      {\n"
          + "        \"name\": \"\",\n"
          + "        \"type\": \"int256[]\"\n"
          + "      },\n"
          + "      {\n"
          + "        \"name\": \"\",\n"
          + "        \"type\": \"string[]\"\n"
          + "      }\n"
          + "    ],\n"
          + "    \"payable\": false,\n"
          + "    \"stateMutability\": \"view\",\n"
          + "    \"type\": \"function\"\n"
          + "  }\n"
          + "]";

  /*
  {
      "ebf3b24f": "insert(string,int256,string)",
          "c4f41ab3": "remove(string,int256)",
          "fcd7e3c1": "select(string)",
          "487a5a10": "update(string,int256,string)"
  }
  */

  private static final ContractABIDefinition contractABIDefinition =
      TestUtils.getContractABIDefinition(abiDesc);
  private static final ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();

  @Test
  public void ABILoadTest() {
    ContractABIDefinition contractABIDefinition = TestUtils.getContractABIDefinition(abiDesc);
    ABIDefinition insert = contractABIDefinition.getFunctions().get("insert").get(0);
    ABIDefinition update = contractABIDefinition.getFunctions().get("update").get(0);
    ABIDefinition remove = contractABIDefinition.getFunctions().get("remove").get(0);
    ABIDefinition select = contractABIDefinition.getFunctions().get("select").get(0);

    Assert.assertEquals(Hex.toHexString(insert.getMethodId(TestUtils.getCryptoSuite())), "ebf3b24f");
    Assert.assertEquals(Hex.toHexString(remove.getMethodId(TestUtils.getCryptoSuite())), "c4f41ab3");
    Assert.assertEquals(Hex.toHexString(update.getMethodId(TestUtils.getCryptoSuite())), "487a5a10");
    Assert.assertEquals(Hex.toHexString(select.getMethodId(TestUtils.getCryptoSuite())), "fcd7e3c1");

    Assert.assertEquals(insert.getMethodSignatureAsString(), "insert(string,int256,string)");
    Assert.assertEquals(remove.getMethodSignatureAsString(), "remove(string,int256)");
    Assert.assertEquals(update.getMethodSignatureAsString(), "update(string,int256,string)");
    Assert.assertEquals(select.getMethodSignatureAsString(), "select(string)");
  }

  @Test
  public void ABIObjectTest() {
    ContractABIDefinition contractABIDefinition = TestUtils.getContractABIDefinition(abiDesc);
    ABIObject insertInputObject =
        ABIObjectFactory.createInputObject(
            contractABIDefinition.getFunctions().get("insert").get(0));
    ABIObject insertOutputObject =
        ABIObjectFactory.createOutputObject(
            contractABIDefinition.getFunctions().get("insert").get(0));

    Assert.assertEquals(insertInputObject.getStructFields().size(), 3);
    Assert.assertEquals(
        insertInputObject.getStructFields().get(0).getValueType(), ABIObject.ValueType.STRING);
    Assert.assertEquals(
        insertInputObject.getStructFields().get(1).getValueType(), ABIObject.ValueType.INT);
    Assert.assertEquals(
        insertInputObject.getStructFields().get(2).getValueType(), ABIObject.ValueType.STRING);

    Assert.assertTrue(insertInputObject.getStructFields().get(0).isDynamic());
    Assert.assertTrue(!insertInputObject.getStructFields().get(1).isDynamic());
    Assert.assertTrue(insertInputObject.getStructFields().get(2).isDynamic());

    Assert.assertEquals(insertOutputObject.getStructFields().size(), 1);
    Assert.assertEquals(
        insertOutputObject.getStructFields().get(0).getValueType(), ABIObject.ValueType.INT);

    ABIObject updateInputObject =
        ABIObjectFactory.createInputObject(
            contractABIDefinition.getFunctions().get("update").get(0));
    ABIObject updateOutputObject =
        ABIObjectFactory.createOutputObject(
            contractABIDefinition.getFunctions().get("update").get(0));

    Assert.assertEquals(updateInputObject.getStructFields().size(), 3);
    Assert.assertEquals(
        updateInputObject.getStructFields().get(0).getValueType(), ABIObject.ValueType.STRING);
    Assert.assertEquals(
        updateInputObject.getStructFields().get(1).getValueType(), ABIObject.ValueType.INT);
    Assert.assertEquals(
        updateInputObject.getStructFields().get(2).getValueType(), ABIObject.ValueType.STRING);

    Assert.assertTrue(updateInputObject.getStructFields().get(0).isDynamic());
    Assert.assertTrue(!updateInputObject.getStructFields().get(1).isDynamic());
    Assert.assertTrue(updateInputObject.getStructFields().get(2).isDynamic());

    Assert.assertEquals(updateOutputObject.getStructFields().size(), 1);
    Assert.assertEquals(
        updateOutputObject.getStructFields().get(0).getValueType(), ABIObject.ValueType.INT);

    ABIObject removeInputObject =
        ABIObjectFactory.createInputObject(
            contractABIDefinition.getFunctions().get("remove").get(0));
    ABIObject removeOutputObject =
        ABIObjectFactory.createOutputObject(
            contractABIDefinition.getFunctions().get("remove").get(0));

    Assert.assertEquals(removeInputObject.getStructFields().size(), 2);
    Assert.assertEquals(
        removeInputObject.getStructFields().get(0).getValueType(), ABIObject.ValueType.STRING);
    Assert.assertEquals(
        removeInputObject.getStructFields().get(1).getValueType(), ABIObject.ValueType.INT);

    Assert.assertTrue(removeInputObject.getStructFields().get(0).isDynamic());
    Assert.assertTrue(!removeInputObject.getStructFields().get(1).isDynamic());

    Assert.assertEquals(removeOutputObject.getStructFields().size(), 1);
    Assert.assertEquals(
        removeOutputObject.getStructFields().get(0).getValueType(), ABIObject.ValueType.INT);

    ABIObject selectInputObject =
        ABIObjectFactory.createInputObject(
            contractABIDefinition.getFunctions().get("select").get(0));
    ABIObject selectOutputObject =
        ABIObjectFactory.createOutputObject(
            contractABIDefinition.getFunctions().get("select").get(0));

    Assert.assertEquals(selectInputObject.getStructFields().size(), 1);
    Assert.assertEquals(
        selectInputObject.getStructFields().get(0).getValueType(), ABIObject.ValueType.STRING);

    Assert.assertTrue(selectInputObject.getStructFields().get(0).isDynamic());

    Assert.assertEquals(selectOutputObject.getStructFields().size(), 3);
    Assert.assertEquals(
        selectOutputObject.getStructFields().get(0).getListType(), ABIObject.ListType.DYNAMIC);
    Assert.assertEquals(
        selectOutputObject.getStructFields().get(1).getListType(), ABIObject.ListType.DYNAMIC);
    Assert.assertEquals(
        selectOutputObject.getStructFields().get(2).getListType(), ABIObject.ListType.DYNAMIC);
  }

  @Test
  public void ABIObjectCodecTest() throws IOException {
    ContractABIDefinition contractABIDefinition = TestUtils.getContractABIDefinition(abiDesc);
    ABIObject insertInputObject =
        ABIObjectFactory.createInputObject(
            contractABIDefinition.getFunctions().get("insert").get(0));
    ABIObject insertOutputObject =
        ABIObjectFactory.createOutputObject(
            contractABIDefinition.getFunctions().get("insert").get(0));

    insertInputObject.getStructFields().set(0, new ABIObject(new Utf8String("hello")));
    insertInputObject.getStructFields().set(1, new ABIObject(new Uint256(100)));
    insertInputObject.getStructFields().set(2, new ABIObject(new Utf8String("car")));
    byte[] insertInputEncode = insertInputObject.encode();
    String insertEncode =
        "0000000000000000000000000000000000000000000000000000000000000060000000000000000000000000000000000000000000000000000000000000006400000000000000000000000000000000000000000000000000000000000000a0000000000000000000000000000000000000000000000000000000000000000568656c6c6f00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000036361720000000000000000000000000000000000000000000000000000000000";
    Assert.assertEquals(Hex.toHexString(insertInputEncode), insertEncode);
    Assert.assertEquals(
        Hex.toHexString(insertInputObject.decode(insertInputEncode).encode()),
        Hex.toHexString(insertInputObject.encode()));

    insertOutputObject.getStructFields().set(0, new ABIObject(new Uint256(1111)));
    byte[] insertOutputEncode = insertOutputObject.encode();
    Assert.assertEquals(
        Hex.toHexString(insertOutputObject.decode(insertOutputEncode).encode()),
        Hex.toHexString(insertOutputEncode));

    //        ABIObject updateInputObject =
    // ABIObjectFactory.createInputObject(contractABIDefinition.getFunctions().get("update").get(0));
    //        ABIObject updateOutputObject =
    // ABIObjectFactory.createOutputObject(contractABIDefinition.getFunctions().get("update").get(0));
    //
    //        ABIObject removeInputObject =
    // ABIObjectFactory.createInputObject(contractABIDefinition.getFunctions().get("remove").get(0));
    //        ABIObject removeOutputObject =
    // ABIObjectFactory.createOutputObject(contractABIDefinition.getFunctions().get("remove").get(0));

    //        ABIObject selectInputObject =
    // ABIObjectFactory.createInputObject(contractABIDefinition.getFunctions().get("select").get(0));
    //        ABIObject selectOutputObject =
    // ABIObjectFactory.createOutputObject(contractABIDefinition.getFunctions().get("select").get(0));
  }

  @Test
  public void ABIObjectCodecJsonWrapperTest() throws IOException {
    ContractABIDefinition contractABIDefinition = TestUtils.getContractABIDefinition(abiDesc);

    ABIObject insertInputObject =
        ABIObjectFactory.createInputObject(
            contractABIDefinition.getFunctions().get("insert").get(0));
    ABIObject insertOutputObject =
        ABIObjectFactory.createOutputObject(
            contractABIDefinition.getFunctions().get("insert").get(0));

    byte[] encode_i =
        abiCodecJsonWrapper
            .encode(insertInputObject, Arrays.asList("hello", "100", "car"))
            .encode();
    String encoded_i =
        "0000000000000000000000000000000000000000000000000000000000000060000000000000000000000000000000000000000000000000000000000000006400000000000000000000000000000000000000000000000000000000000000a0000000000000000000000000000000000000000000000000000000000000000568656c6c6f00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000036361720000000000000000000000000000000000000000000000000000000000";
    Assert.assertEquals(Hex.toHexString(encode_i), encoded_i);
    List<String> json_decode_i = abiCodecJsonWrapper.decode(insertInputObject, encode_i);
    Assert.assertEquals(json_decode_i.get(0), "hello");
    Assert.assertEquals(json_decode_i.get(1), "100");
    Assert.assertEquals(json_decode_i.get(2), "car");

    byte[] encode_o = abiCodecJsonWrapper.encode(insertOutputObject, Arrays.asList("100")).encode();
    String encoded_o = "0000000000000000000000000000000000000000000000000000000000000064";
    Assert.assertEquals(Hex.toHexString(encode_o), encoded_o);
    List<String> json_decode_o = abiCodecJsonWrapper.decode(insertOutputObject, encode_o);
    Assert.assertEquals(json_decode_o.get(0), "100");

    ABIObject updateInputObject =
        ABIObjectFactory.createInputObject(
            contractABIDefinition.getFunctions().get("update").get(0));
    ABIObject updateOutputObject =
        ABIObjectFactory.createOutputObject(
            contractABIDefinition.getFunctions().get("update").get(0));

    byte[] encode_i_u =
        abiCodecJsonWrapper
            .encode(updateInputObject, Arrays.asList("hello", "100", "car"))
            .encode();
    String encoded_i_u =
        "0000000000000000000000000000000000000000000000000000000000000060000000000000000000000000000000000000000000000000000000000000006400000000000000000000000000000000000000000000000000000000000000a0000000000000000000000000000000000000000000000000000000000000000568656c6c6f00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000036361720000000000000000000000000000000000000000000000000000000000";
    Assert.assertEquals(Hex.toHexString(encode_i_u), encoded_i_u);

    byte[] encode_o_u =
        abiCodecJsonWrapper.encode(updateOutputObject, Arrays.asList("100")).encode();
    String encoded_o_u = "0000000000000000000000000000000000000000000000000000000000000064";
    Assert.assertEquals(Hex.toHexString(encode_o_u), encoded_o_u);

    List<String> json_decode_i_o = abiCodecJsonWrapper.decode(updateInputObject, encode_i_u);
    Assert.assertEquals(json_decode_i_o.get(0), "hello");
    Assert.assertEquals(json_decode_i_o.get(1), "100");
    Assert.assertEquals(json_decode_i_o.get(2), "car");

    List<String> json_decode_o_o = abiCodecJsonWrapper.decode(updateOutputObject, encode_o_u);
    Assert.assertEquals(json_decode_o_o.get(0), "100");

    ABIObject removeInputObject =
        ABIObjectFactory.createInputObject(
            contractABIDefinition.getFunctions().get("remove").get(0));
    ABIObject removeOutputObject =
        ABIObjectFactory.createOutputObject(
            contractABIDefinition.getFunctions().get("remove").get(0));

    byte[] encode_i_r =
        abiCodecJsonWrapper.encode(removeInputObject, Arrays.asList("hello", "100")).encode();
    String encoded_i_r =
        "00000000000000000000000000000000000000000000000000000000000000400000000000000000000000000000000000000000000000000000000000000064000000000000000000000000000000000000000000000000000000000000000568656c6c6f000000000000000000000000000000000000000000000000000000";
    Assert.assertEquals(Hex.toHexString(encode_i_r), encoded_i_r);

    byte[] encode_o_r =
        abiCodecJsonWrapper.encode(removeOutputObject, Arrays.asList("100")).encode();
    String encoded_o_r = "0000000000000000000000000000000000000000000000000000000000000064";
    Assert.assertEquals(Hex.toHexString(encode_o_r), encoded_o_r);

    List<String> json_decode_r_i = abiCodecJsonWrapper.decode(removeInputObject, encode_i_r);
    Assert.assertEquals(json_decode_r_i.get(0), "hello");
    List<String> json_decode_r_o = abiCodecJsonWrapper.decode(updateOutputObject, encode_o_r);
    Assert.assertEquals(json_decode_r_o.get(0), "100");

    ABIObject selectInputObject =
        ABIObjectFactory.createInputObject(
            contractABIDefinition.getFunctions().get("select").get(0));
    ABIObject selectOutputObject =
        ABIObjectFactory.createOutputObject(
            contractABIDefinition.getFunctions().get("select").get(0));

    byte[] encode_i_s =
        abiCodecJsonWrapper.encode(selectInputObject, Arrays.asList("hello")).encode();
    String encoded_i_s =
        "0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000568656c6c6f000000000000000000000000000000000000000000000000000000";
    Assert.assertEquals(Hex.toHexString(encode_i_s), encoded_i_s);

    byte[] encode_o_s =
        abiCodecJsonWrapper
            .encode(
                selectOutputObject,
                Arrays.asList(
                    "[\"hello\",\"hello\",\"hello\"]",
                    "[100,100,100]",
                    "[\"car\",\"car\",\"car\"]"))
            .encode();
    String encoded_o_s =
        "000000000000000000000000000000000000000000000000000000000000006000000000000000000000000000000000000000000000000000000000000001a000000000000000000000000000000000000000000000000000000000000002200000000000000000000000000000000000000000000000000000000000000003000000000000000000000000000000000000000000000000000000000000006000000000000000000000000000000000000000000000000000000000000000a000000000000000000000000000000000000000000000000000000000000000e0000000000000000000000000000000000000000000000000000000000000000568656c6c6f000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000568656c6c6f000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000568656c6c6f00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000030000000000000000000000000000000000000000000000000000000000000064000000000000000000000000000000000000000000000000000000000000006400000000000000000000000000000000000000000000000000000000000000640000000000000000000000000000000000000000000000000000000000000003000000000000000000000000000000000000000000000000000000000000006000000000000000000000000000000000000000000000000000000000000000a000000000000000000000000000000000000000000000000000000000000000e0000000000000000000000000000000000000000000000000000000000000000363617200000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000003636172000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000036361720000000000000000000000000000000000000000000000000000000000";
    Assert.assertEquals(Hex.toHexString(encode_o_s), encoded_o_s);

    List<String> json_decode_s_i = abiCodecJsonWrapper.decode(selectInputObject, encode_i_s);
    Assert.assertEquals(json_decode_s_i.get(0), "hello");

    List<String> json_decode_s_o = abiCodecJsonWrapper.decode(selectOutputObject, encode_o_s);

    Assert.assertEquals(json_decode_s_o.get(0), "[ \"hello\", \"hello\", \"hello\" ]");
    Assert.assertEquals(json_decode_s_o.get(1), "[ 100, 100, 100 ]");
    Assert.assertEquals(json_decode_s_o.get(2), "[ \"car\", \"car\", \"car\" ]");
  }
}
