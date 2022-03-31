package org.fisco.bcos.sdk.v3.codec.abi;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.fisco.bcos.sdk.v3.codec.TestUtils;
import org.fisco.bcos.sdk.v3.codec.wrapper.ContractCodecJsonWrapper;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIObject;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIObjectFactory;
import org.fisco.bcos.sdk.v3.codec.wrapper.ContractABIDefinition;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.junit.Assert;
import org.junit.Test;

public class ContractTypeTest {
    String abiDesc =
            "[\n"
                    + " {\n"
                    + "  \"constant\": false,\n"
                    + "  \"inputs\": [\n"
                    + "   {\n"
                    + "    \"name\": \"_u\",\n"
                    + "    \"type\": \"uint256[]\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_b\",\n"
                    + "    \"type\": \"bool[]\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_addr\",\n"
                    + "    \"type\": \"address[]\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_bs32\",\n"
                    + "    \"type\": \"bytes32[]\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_s\",\n"
                    + "    \"type\": \"string[]\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_bs\",\n"
                    + "    \"type\": \"bytes[]\"\n"
                    + "   }\n"
                    + "  ],\n"
                    + "  \"name\": \"setDynamicValue\",\n"
                    + "  \"outputs\": [],\n"
                    + "  \"payable\": false,\n"
                    + "  \"stateMutability\": \"nonpayable\",\n"
                    + "  \"type\": \"function\"\n"
                    + " },\n"
                    + " {\n"
                    + "  \"constant\": false,\n"
                    + "  \"inputs\": [\n"
                    + "   {\n"
                    + "    \"name\": \"_u\",\n"
                    + "    \"type\": \"uint256[3]\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_b\",\n"
                    + "    \"type\": \"bool[3]\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_addr\",\n"
                    + "    \"type\": \"address[3]\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_bs32\",\n"
                    + "    \"type\": \"bytes32[3]\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_s\",\n"
                    + "    \"type\": \"string[3]\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_bs\",\n"
                    + "    \"type\": \"bytes[3]\"\n"
                    + "   }\n"
                    + "  ],\n"
                    + "  \"name\": \"setFixedValue\",\n"
                    + "  \"outputs\": [],\n"
                    + "  \"payable\": false,\n"
                    + "  \"stateMutability\": \"nonpayable\",\n"
                    + "  \"type\": \"function\"\n"
                    + " },\n"
                    + " {\n"
                    + "  \"constant\": false,\n"
                    + "  \"inputs\": [\n"
                    + "   {\n"
                    + "    \"name\": \"_u\",\n"
                    + "    \"type\": \"uint256\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_b\",\n"
                    + "    \"type\": \"bool\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_addr\",\n"
                    + "    \"type\": \"address\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_bs32\",\n"
                    + "    \"type\": \"bytes32\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_s\",\n"
                    + "    \"type\": \"string\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_bs\",\n"
                    + "    \"type\": \"bytes\"\n"
                    + "   }\n"
                    + "  ],\n"
                    + "  \"name\": \"setValue\",\n"
                    + "  \"outputs\": [],\n"
                    + "  \"payable\": false,\n"
                    + "  \"stateMutability\": \"nonpayable\",\n"
                    + "  \"type\": \"function\"\n"
                    + " },\n"
                    + " {\n"
                    + "  \"constant\": true,\n"
                    + "  \"inputs\": [],\n"
                    + "  \"name\": \"getDynamicValue\",\n"
                    + "  \"outputs\": [\n"
                    + "   {\n"
                    + "    \"name\": \"_u\",\n"
                    + "    \"type\": \"uint256[]\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_b\",\n"
                    + "    \"type\": \"bool[]\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_addr\",\n"
                    + "    \"type\": \"address[]\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_bs32\",\n"
                    + "    \"type\": \"bytes32[]\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_s\",\n"
                    + "    \"type\": \"string[]\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_bs\",\n"
                    + "    \"type\": \"bytes[]\"\n"
                    + "   }\n"
                    + "  ],\n"
                    + "  \"payable\": false,\n"
                    + "  \"stateMutability\": \"view\",\n"
                    + "  \"type\": \"function\"\n"
                    + " },\n"
                    + " {\n"
                    + "  \"constant\": true,\n"
                    + "  \"inputs\": [],\n"
                    + "  \"name\": \"getFixedValue\",\n"
                    + "  \"outputs\": [\n"
                    + "   {\n"
                    + "    \"name\": \"_u\",\n"
                    + "    \"type\": \"uint256[3]\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_b\",\n"
                    + "    \"type\": \"bool[3]\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_addr\",\n"
                    + "    \"type\": \"address[3]\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_bs32\",\n"
                    + "    \"type\": \"bytes32[3]\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_s\",\n"
                    + "    \"type\": \"string[3]\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_bs\",\n"
                    + "    \"type\": \"bytes[3]\"\n"
                    + "   }\n"
                    + "  ],\n"
                    + "  \"payable\": false,\n"
                    + "  \"stateMutability\": \"view\",\n"
                    + "  \"type\": \"function\"\n"
                    + " },\n"
                    + " {\n"
                    + "  \"constant\": true,\n"
                    + "  \"inputs\": [],\n"
                    + "  \"name\": \"getValue\",\n"
                    + "  \"outputs\": [\n"
                    + "   {\n"
                    + "    \"name\": \"_u\",\n"
                    + "    \"type\": \"uint256\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_b\",\n"
                    + "    \"type\": \"bool\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_addr\",\n"
                    + "    \"type\": \"address\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_bs32\",\n"
                    + "    \"type\": \"bytes32\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_s\",\n"
                    + "    \"type\": \"string\"\n"
                    + "   },\n"
                    + "   {\n"
                    + "    \"name\": \"_bs\",\n"
                    + "    \"type\": \"bytes\"\n"
                    + "   }\n"
                    + "  ],\n"
                    + "  \"payable\": false,\n"
                    + "  \"stateMutability\": \"view\",\n"
                    + "  \"type\": \"function\"\n"
                    + " }\n"
                    + "]";

    /*
    {
        "20965255": "getValue()",
            "ed4d0e39": "getDynamicValue()",
            "c1cee39a": "getFixedValue()",
            "dfed87e3": "setDynamicValue(uint256[],bool[],address[],bytes32[],string[],bytes[])",
            "63e5584b": "setFixedValue(uint256[3],bool[3],address[3],bytes32[3],string[3],bytes[3])",
            "11cfbe17": "setValue(uint256,bool,address,bytes32,string,bytes)"
    }*/

    private ContractABIDefinition contractABIDefinition = TestUtils.getContractABIDefinition(abiDesc);

    @Test
    public void ContractFixedTypeCodecTest() throws IOException, ClassNotFoundException {
        ABIObject inputObject =
                ABIObjectFactory.createInputObject(
                        contractABIDefinition.getFunctions().get("setFixedValue").get(0));

        ABIObject outObject =
                ABIObjectFactory.createOutputObject(
                        contractABIDefinition.getFunctions().get("getFixedValue").get(0));

        String bytes1 = "HelloWorld 11111";
        String bytes1Hex = Hex.toHexString(bytes1.getBytes());
        String bytes2 = "hex://" + Hex.toHexString("HelloWorld 22222".getBytes());
        String bytes3 = "hex://" + Hex.toHexString("HelloWorld 33333".getBytes());
        String bytes32ValueHex = "hex://0x6162636465666768736466336577657277657272657772657765727765726565";
        String bytes32ValuePlain = "abcdefghsdf3ewerwerrewrewerweree";
        String bytes32PrettyString = "6162636465666768736466336577657277657272657772657765727765726565";
        List<String> params =
                Arrays.asList(
                        "[1,2,3]",
                        "[true,false,true]",
                        "[\"0xa\",\"0xb\",\"0xc\"]",
                        "[\"" + bytes32ValuePlain + "\",\"" + bytes32ValueHex + "\",\"" + bytes32ValueHex + "\"]",
                        "[\"a\",\"b\",\"c\"]",
                        "[\"" + bytes1 + "\",\"" + bytes2 + "\",\"" + bytes3 + "\"]");

        ContractCodecJsonWrapper abiCodecJsonWrapper = new ContractCodecJsonWrapper();
        ABIObject encodeObject = abiCodecJsonWrapper.encode(inputObject, params);
        byte[] encode = encodeObject.encode(false);
        String s = Hex.toHexString(encode);
        List<String> decodeResult = abiCodecJsonWrapper.decode(outObject, encode,false);

        Assert.assertEquals(decodeResult.get(0), "[ 1, 2, 3 ]");
        Assert.assertEquals(decodeResult.get(1), "[ true, false, true ]");
        Assert.assertEquals(
                decodeResult.get(2),
                "[ \"0x000000000000000000000000000000000000000a\", \"0x000000000000000000000000000000000000000b\", \"0x000000000000000000000000000000000000000c\" ]");
        Assert.assertEquals(
                decodeResult.get(3),
                "[ \"" + bytes32PrettyString + "\", \"" + bytes32PrettyString + "\", \"" + bytes32PrettyString + "\" ]");

        Assert.assertEquals(decodeResult.get(4), "[ \"a\", \"b\", \"c\" ]");
        Assert.assertEquals(
                decodeResult.get(5),
                "[ \"" + bytes1Hex + "\", \"" + bytes2.substring("hex://".length()) + "\", \"" + bytes3.substring("hex://".length()) + "\" ]");
    }

    @Test
    public void ContractDynamicTypeCodecTest() throws IOException, ClassNotFoundException {
        ABIObject inputObject =
                ABIObjectFactory.createInputObject(
                        contractABIDefinition.getFunctions().get("setDynamicValue").get(0));

        ABIObject outObject =
                ABIObjectFactory.createOutputObject(
                        contractABIDefinition.getFunctions().get("getDynamicValue").get(0));

        String bytes1 = "HelloWorld 11111";
        String bytes1Hex = Hex.toHexString(bytes1.getBytes());
        String bytes2 = "hex://" + Hex.toHexString("HelloWorld 22222".getBytes());
        String bytes3 = "hex://" + Hex.toHexString("HelloWorld 33333".getBytes());

        String bytes32ValueHex = "hex://0x6162636465666768736466336577657277657272657772657765727765726565";
        String bytes32ValuePlain = "abcdefghsdf3ewerwerrewrewerweree";
        String bytes32PrettyString = "6162636465666768736466336577657277657272657772657765727765726565";
        List<String> params =
                Arrays.asList(
                        "[1,2,3]",
                        "[true,false,true]",
                        "[\"0xa\",\"0xb\",\"0xc\"]",
                        "[\"" + bytes32ValueHex + "\",\"" + bytes32ValuePlain + "\",\"" + bytes32ValueHex + "\"]",
                        "[\"a\",\"b\",\"c\"]",
                        "[\"" + bytes1 + "\",\"" + bytes2 + "\",\"" + bytes3 + "\"]");

        ContractCodecJsonWrapper abiCodecJsonWrapper = new ContractCodecJsonWrapper();
        ABIObject encodeObject = abiCodecJsonWrapper.encode(inputObject, params);

        List<String> decodeResult = abiCodecJsonWrapper.decode(outObject, encodeObject.encode(false),false);

        Assert.assertEquals(decodeResult.get(0), "[ 1, 2, 3 ]");
        Assert.assertEquals(decodeResult.get(1), "[ true, false, true ]");
        Assert.assertEquals(
                decodeResult.get(2),
                "[ \"0x000000000000000000000000000000000000000a\", \"0x000000000000000000000000000000000000000b\", \"0x000000000000000000000000000000000000000c\" ]");
        Assert.assertEquals(
                decodeResult.get(3),
                "[ \"" + bytes32PrettyString + "\", \"" + bytes32PrettyString + "\", \"" + bytes32PrettyString + "\" ]");

        Assert.assertEquals(decodeResult.get(4), "[ \"a\", \"b\", \"c\" ]");
        Assert.assertEquals(
                decodeResult.get(5),
                "[ \"" + bytes1Hex + "\", \"" + bytes2.substring("hex://".length()) + "\", \"" + bytes3.substring("hex://".length()) + "\" ]");
    }

    @Test
    public void ContractDynamicTypeEmptyParamsCodecTest() throws IOException, ClassNotFoundException {
        ABIObject inputObject =
                ABIObjectFactory.createInputObject(
                        contractABIDefinition.getFunctions().get("setDynamicValue").get(0));

        ABIObject outObject =
                ABIObjectFactory.createOutputObject(
                        contractABIDefinition.getFunctions().get("getDynamicValue").get(0));

        List<String> params = Arrays.asList("[]", "[]", "[]", "[]", "[]", "[]");

        ContractCodecJsonWrapper abiCodecJsonWrapper = new ContractCodecJsonWrapper();
        ABIObject encodeObject = abiCodecJsonWrapper.encode(inputObject, params);

        List<String> decodeResult = abiCodecJsonWrapper.decode(outObject, encodeObject.encode(false),false);

        Assert.assertEquals(decodeResult.get(0), "[ ]");
        Assert.assertEquals(decodeResult.get(1), "[ ]");
        Assert.assertEquals(decodeResult.get(2), "[ ]");
        Assert.assertEquals(decodeResult.get(3), "[ ]");

        Assert.assertEquals(decodeResult.get(4), "[ ]");
        Assert.assertEquals(decodeResult.get(5), "[ ]");
    }
}
