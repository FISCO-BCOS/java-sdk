package org.fisco.bcos.sdk.test.abi;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import org.fisco.bcos.sdk.abi.wrapper.ABICodecJsonWrapper;
import org.fisco.bcos.sdk.abi.wrapper.ABIObject;
import org.fisco.bcos.sdk.abi.wrapper.ABIObjectFactory;
import org.fisco.bcos.sdk.abi.wrapper.ContractABIDefinition;
import org.junit.Assert;
import org.junit.Test;

public class ContractTypeTest {
    String abiDesc =
            "[\n"
                    + "\t{\n"
                    + "\t\t\"constant\": false,\n"
                    + "\t\t\"inputs\": [\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_u\",\n"
                    + "\t\t\t\t\"type\": \"uint256[]\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_b\",\n"
                    + "\t\t\t\t\"type\": \"bool[]\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_addr\",\n"
                    + "\t\t\t\t\"type\": \"address[]\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_bs32\",\n"
                    + "\t\t\t\t\"type\": \"bytes32[]\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_s\",\n"
                    + "\t\t\t\t\"type\": \"string[]\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_bs\",\n"
                    + "\t\t\t\t\"type\": \"bytes[]\"\n"
                    + "\t\t\t}\n"
                    + "\t\t],\n"
                    + "\t\t\"name\": \"setDynamicValue\",\n"
                    + "\t\t\"outputs\": [],\n"
                    + "\t\t\"payable\": false,\n"
                    + "\t\t\"stateMutability\": \"nonpayable\",\n"
                    + "\t\t\"type\": \"function\"\n"
                    + "\t},\n"
                    + "\t{\n"
                    + "\t\t\"constant\": false,\n"
                    + "\t\t\"inputs\": [\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_u\",\n"
                    + "\t\t\t\t\"type\": \"uint256[3]\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_b\",\n"
                    + "\t\t\t\t\"type\": \"bool[3]\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_addr\",\n"
                    + "\t\t\t\t\"type\": \"address[3]\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_bs32\",\n"
                    + "\t\t\t\t\"type\": \"bytes32[3]\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_s\",\n"
                    + "\t\t\t\t\"type\": \"string[3]\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_bs\",\n"
                    + "\t\t\t\t\"type\": \"bytes[3]\"\n"
                    + "\t\t\t}\n"
                    + "\t\t],\n"
                    + "\t\t\"name\": \"setFixedValue\",\n"
                    + "\t\t\"outputs\": [],\n"
                    + "\t\t\"payable\": false,\n"
                    + "\t\t\"stateMutability\": \"nonpayable\",\n"
                    + "\t\t\"type\": \"function\"\n"
                    + "\t},\n"
                    + "\t{\n"
                    + "\t\t\"constant\": false,\n"
                    + "\t\t\"inputs\": [\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_u\",\n"
                    + "\t\t\t\t\"type\": \"uint256\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_b\",\n"
                    + "\t\t\t\t\"type\": \"bool\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_addr\",\n"
                    + "\t\t\t\t\"type\": \"address\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_bs32\",\n"
                    + "\t\t\t\t\"type\": \"bytes32\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_s\",\n"
                    + "\t\t\t\t\"type\": \"string\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_bs\",\n"
                    + "\t\t\t\t\"type\": \"bytes\"\n"
                    + "\t\t\t}\n"
                    + "\t\t],\n"
                    + "\t\t\"name\": \"setValue\",\n"
                    + "\t\t\"outputs\": [],\n"
                    + "\t\t\"payable\": false,\n"
                    + "\t\t\"stateMutability\": \"nonpayable\",\n"
                    + "\t\t\"type\": \"function\"\n"
                    + "\t},\n"
                    + "\t{\n"
                    + "\t\t\"constant\": true,\n"
                    + "\t\t\"inputs\": [],\n"
                    + "\t\t\"name\": \"getDynamicValue\",\n"
                    + "\t\t\"outputs\": [\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_u\",\n"
                    + "\t\t\t\t\"type\": \"uint256[]\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_b\",\n"
                    + "\t\t\t\t\"type\": \"bool[]\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_addr\",\n"
                    + "\t\t\t\t\"type\": \"address[]\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_bs32\",\n"
                    + "\t\t\t\t\"type\": \"bytes32[]\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_s\",\n"
                    + "\t\t\t\t\"type\": \"string[]\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_bs\",\n"
                    + "\t\t\t\t\"type\": \"bytes[]\"\n"
                    + "\t\t\t}\n"
                    + "\t\t],\n"
                    + "\t\t\"payable\": false,\n"
                    + "\t\t\"stateMutability\": \"view\",\n"
                    + "\t\t\"type\": \"function\"\n"
                    + "\t},\n"
                    + "\t{\n"
                    + "\t\t\"constant\": true,\n"
                    + "\t\t\"inputs\": [],\n"
                    + "\t\t\"name\": \"getFixedValue\",\n"
                    + "\t\t\"outputs\": [\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_u\",\n"
                    + "\t\t\t\t\"type\": \"uint256[3]\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_b\",\n"
                    + "\t\t\t\t\"type\": \"bool[3]\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_addr\",\n"
                    + "\t\t\t\t\"type\": \"address[3]\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_bs32\",\n"
                    + "\t\t\t\t\"type\": \"bytes32[3]\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_s\",\n"
                    + "\t\t\t\t\"type\": \"string[3]\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_bs\",\n"
                    + "\t\t\t\t\"type\": \"bytes[3]\"\n"
                    + "\t\t\t}\n"
                    + "\t\t],\n"
                    + "\t\t\"payable\": false,\n"
                    + "\t\t\"stateMutability\": \"view\",\n"
                    + "\t\t\"type\": \"function\"\n"
                    + "\t},\n"
                    + "\t{\n"
                    + "\t\t\"constant\": true,\n"
                    + "\t\t\"inputs\": [],\n"
                    + "\t\t\"name\": \"getValue\",\n"
                    + "\t\t\"outputs\": [\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_u\",\n"
                    + "\t\t\t\t\"type\": \"uint256\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_b\",\n"
                    + "\t\t\t\t\"type\": \"bool\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_addr\",\n"
                    + "\t\t\t\t\"type\": \"address\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_bs32\",\n"
                    + "\t\t\t\t\"type\": \"bytes32\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_s\",\n"
                    + "\t\t\t\t\"type\": \"string\"\n"
                    + "\t\t\t},\n"
                    + "\t\t\t{\n"
                    + "\t\t\t\t\"name\": \"_bs\",\n"
                    + "\t\t\t\t\"type\": \"bytes\"\n"
                    + "\t\t\t}\n"
                    + "\t\t],\n"
                    + "\t\t\"payable\": false,\n"
                    + "\t\t\"stateMutability\": \"view\",\n"
                    + "\t\t\"type\": \"function\"\n"
                    + "\t}\n"
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

    private ContractABIDefinition contractABIDefinition = Utils.getContractABIDefinition(abiDesc);

    @Test
    public void ContractFixedTypeCodecTest() throws IOException {
        ABIObject inputObject =
                ABIObjectFactory.createInputObject(
                        contractABIDefinition.getFunctions().get("setFixedValue").get(0));

        ABIObject outObject =
                ABIObjectFactory.createOutputObject(
                        contractABIDefinition.getFunctions().get("getFixedValue").get(0));

        String bytes1 = Base64.getEncoder().encodeToString("HelloWorld 11111".getBytes());
        String bytes2 = Base64.getEncoder().encodeToString("HelloWorld 22222".getBytes());
        String bytes3 = Base64.getEncoder().encodeToString("HelloWorld 33333".getBytes());

        List<String> params =
                Arrays.asList(
                        "[1,2,3]",
                        "[true,false,true]",
                        "[\"0xa\",\"0xb\",\"0xc\"]",
                        "[\"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=\",\"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=\",\"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=\"]",
                        "[\"a\",\"b\",\"c\"]",
                        "[\"" + bytes1 + "\",\"" + bytes2 + "\",\"" + bytes3 + "\"]");

        ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
        ABIObject encodeObject = abiCodecJsonWrapper.encode(inputObject, params);

        List<String> decodeResult = abiCodecJsonWrapper.decode(outObject, encodeObject.encode());

        Assert.assertEquals(decodeResult.get(0), "[ 1, 2, 3 ]");
        Assert.assertEquals(decodeResult.get(1), "[ true, false, true ]");
        Assert.assertEquals(
                decodeResult.get(2),
                "[ \"0x000000000000000000000000000000000000000a\", \"0x000000000000000000000000000000000000000b\", \"0x000000000000000000000000000000000000000c\" ]");
        Assert.assertEquals(
                decodeResult.get(3),
                "[ \"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=\", \"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=\", \"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=\" ]");

        Assert.assertEquals(decodeResult.get(4), "[ \"a\", \"b\", \"c\" ]");
        Assert.assertEquals(
                decodeResult.get(5),
                "[ \"" + bytes1 + "\", \"" + bytes2 + "\", \"" + bytes3 + "\" ]");
    }

    @Test
    public void ContractDynamicTypeCodecTest() throws IOException {
        ABIObject inputObject =
                ABIObjectFactory.createInputObject(
                        contractABIDefinition.getFunctions().get("setDynamicValue").get(0));

        ABIObject outObject =
                ABIObjectFactory.createOutputObject(
                        contractABIDefinition.getFunctions().get("getDynamicValue").get(0));

        String bytes1 = Base64.getEncoder().encodeToString("HelloWorld 11111".getBytes());
        String bytes2 = Base64.getEncoder().encodeToString("HelloWorld 22222".getBytes());
        String bytes3 = Base64.getEncoder().encodeToString("HelloWorld 33333".getBytes());

        List<String> params =
                Arrays.asList(
                        "[1,2,3]",
                        "[true,false,true]",
                        "[\"0xa\",\"0xb\",\"0xc\"]",
                        "[\"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=\",\"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=\",\"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=\"]",
                        "[\"a\",\"b\",\"c\"]",
                        "[\"" + bytes1 + "\",\"" + bytes2 + "\",\"" + bytes3 + "\"]");

        ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
        ABIObject encodeObject = abiCodecJsonWrapper.encode(inputObject, params);

        List<String> decodeResult = abiCodecJsonWrapper.decode(outObject, encodeObject.encode());

        Assert.assertEquals(decodeResult.get(0), "[ 1, 2, 3 ]");
        Assert.assertEquals(decodeResult.get(1), "[ true, false, true ]");
        Assert.assertEquals(
                decodeResult.get(2),
                "[ \"0x000000000000000000000000000000000000000a\", \"0x000000000000000000000000000000000000000b\", \"0x000000000000000000000000000000000000000c\" ]");
        Assert.assertEquals(
                decodeResult.get(3),
                "[ \"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=\", \"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=\", \"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=\" ]");

        Assert.assertEquals(decodeResult.get(4), "[ \"a\", \"b\", \"c\" ]");
        Assert.assertEquals(
                decodeResult.get(5),
                "[ \"" + bytes1 + "\", \"" + bytes2 + "\", \"" + bytes3 + "\" ]");
    }

    @Test
    public void ContractDynamicTypeEmptyParamsCodecTest() throws IOException {
        ABIObject inputObject =
                ABIObjectFactory.createInputObject(
                        contractABIDefinition.getFunctions().get("setDynamicValue").get(0));

        ABIObject outObject =
                ABIObjectFactory.createOutputObject(
                        contractABIDefinition.getFunctions().get("getDynamicValue").get(0));

        List<String> params = Arrays.asList("[]", "[]", "[]", "[]", "[]", "[]");

        ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
        ABIObject encodeObject = abiCodecJsonWrapper.encode(inputObject, params);

        List<String> decodeResult = abiCodecJsonWrapper.decode(outObject, encodeObject.encode());

        Assert.assertEquals(decodeResult.get(0), "[ ]");
        Assert.assertEquals(decodeResult.get(1), "[ ]");
        Assert.assertEquals(decodeResult.get(2), "[ ]");
        Assert.assertEquals(decodeResult.get(3), "[ ]");

        Assert.assertEquals(decodeResult.get(4), "[ ]");
        Assert.assertEquals(decodeResult.get(5), "[ ]");
    }
}
