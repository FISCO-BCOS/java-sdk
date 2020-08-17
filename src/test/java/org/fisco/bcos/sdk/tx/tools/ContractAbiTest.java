package org.fisco.bcos.sdk.tx.tools;

import java.util.List;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.Address;
import org.fisco.bcos.sdk.abi.datatypes.DynamicArray;
import org.fisco.bcos.sdk.abi.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.abi.datatypes.Utf8String;
import org.fisco.bcos.sdk.abi.datatypes.generated.Bytes32;
import org.fisco.bcos.sdk.abi.datatypes.generated.StaticArray2;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.abi.tools.ContractAbiUtil;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionBaseException;
import org.fisco.bcos.sdk.transaction.tools.JsonUtils;
import org.junit.Assert;
import org.junit.Test;

public class ContractAbiTest {

    @Test
    public void paramInputTest() throws Exception {
        ABIDefinition.NamedType uint256 = new ABIDefinition.NamedType();
        uint256.setType("uint256");
        TypeReference<?> typeReference = ContractAbiUtil.paramInput(uint256);
        Assert.assertEquals(Uint256.class, typeReference.getClassType());

        ABIDefinition.NamedType string = new ABIDefinition.NamedType();
        string.setType("string");
        typeReference = ContractAbiUtil.paramInput(string);
        Assert.assertEquals(Utf8String.class, typeReference.getClassType());

        ABIDefinition.NamedType address = new ABIDefinition.NamedType();
        address.setType("address");
        typeReference = ContractAbiUtil.paramInput(address);
        Assert.assertEquals(Address.class, typeReference.getClassType());

        ABIDefinition.NamedType bytes = new ABIDefinition.NamedType();
        bytes.setType("bytes");
        typeReference = ContractAbiUtil.paramInput(bytes);
        Assert.assertEquals(DynamicBytes.class, typeReference.getClassType());

        ABIDefinition.NamedType bytes32 = new ABIDefinition.NamedType();
        bytes32.setType("bytes32");
        typeReference = ContractAbiUtil.paramInput(bytes32);
        Assert.assertEquals(Bytes32.class, typeReference.getClassType());
    }

    @Test
    public void paramInputTestForDynamicArray() throws Exception {
        ABIDefinition.NamedType uint256 = new ABIDefinition.NamedType();
        uint256.setType("uint256[]");
        TypeReference<?> typeReference = ContractAbiUtil.paramInput(uint256);
        Assert.assertEquals(DynamicArray.class, typeReference.getClassType());
        Assert.assertEquals(Uint256.class, ContractAbiUtil.resolveArrayBasicType(typeReference));

        ABIDefinition.NamedType string = new ABIDefinition.NamedType();
        string.setType("string[]");
        typeReference = ContractAbiUtil.paramInput(string);
        Assert.assertEquals(DynamicArray.class, typeReference.getClassType());
        Assert.assertEquals(Utf8String.class, ContractAbiUtil.resolveArrayBasicType(typeReference));

        ABIDefinition.NamedType address = new ABIDefinition.NamedType();
        address.setType("address[]");
        typeReference = ContractAbiUtil.paramInput(address);
        Assert.assertEquals(DynamicArray.class, typeReference.getClassType());
        Assert.assertEquals(Address.class, ContractAbiUtil.resolveArrayBasicType(typeReference));

        ABIDefinition.NamedType bytes = new ABIDefinition.NamedType();
        bytes.setType("bytes[]");
        typeReference = ContractAbiUtil.paramInput(bytes);
        Assert.assertEquals(DynamicArray.class, typeReference.getClassType());
        Assert.assertEquals(
                DynamicBytes.class, ContractAbiUtil.resolveArrayBasicType(typeReference));

        ABIDefinition.NamedType bytes32 = new ABIDefinition.NamedType();
        bytes32.setType("bytes32[]");
        typeReference = ContractAbiUtil.paramInput(bytes32);
        Assert.assertEquals(DynamicArray.class, typeReference.getClassType());
        Assert.assertEquals(Bytes32.class, ContractAbiUtil.resolveArrayBasicType(typeReference));
    }

    @Test
    public void paramInputTestForStaticArray() throws Exception {
        ABIDefinition.NamedType uint256 = new ABIDefinition.NamedType();
        uint256.setType("uint256[2]");
        TypeReference<?> typeReference = ContractAbiUtil.paramInput(uint256);
        Assert.assertEquals(StaticArray2.class, typeReference.getClassType());
        Assert.assertEquals(Uint256.class, ContractAbiUtil.resolveArrayBasicType(typeReference));

        ABIDefinition.NamedType string = new ABIDefinition.NamedType();
        string.setType("string[2]");
        typeReference = ContractAbiUtil.paramInput(string);
        Assert.assertEquals(StaticArray2.class, typeReference.getClassType());
        Assert.assertEquals(Utf8String.class, ContractAbiUtil.resolveArrayBasicType(typeReference));

        ABIDefinition.NamedType address = new ABIDefinition.NamedType();
        address.setType("address[2]");
        typeReference = ContractAbiUtil.paramInput(address);
        Assert.assertEquals(StaticArray2.class, typeReference.getClassType());
        Assert.assertEquals(Address.class, ContractAbiUtil.resolveArrayBasicType(typeReference));

        ABIDefinition.NamedType bytes = new ABIDefinition.NamedType();
        bytes.setType("bytes[2]");
        typeReference = ContractAbiUtil.paramInput(bytes);
        Assert.assertEquals(StaticArray2.class, typeReference.getClassType());
        Assert.assertEquals(
                DynamicBytes.class, ContractAbiUtil.resolveArrayBasicType(typeReference));

        ABIDefinition.NamedType bytes32 = new ABIDefinition.NamedType();
        bytes32.setType("bytes32[2]");
        typeReference = ContractAbiUtil.paramInput(bytes32);
        Assert.assertEquals(StaticArray2.class, typeReference.getClassType());
        Assert.assertEquals(Bytes32.class, ContractAbiUtil.resolveArrayBasicType(typeReference));
    }

    @Test
    public void testAbi() throws TransactionBaseException {
        String json =
                "{\n"
                        + "    \"constant\": true,\n"
                        + "    \"inputs\": [],\n"
                        + "    \"name\": \"name\",\n"
                        + "    \"outputs\": [\n"
                        + "      {\n"
                        + "        \"name\": \"\",\n"
                        + "        \"type\": \"string\"\n"
                        + "      }\n"
                        + "    ],\n"
                        + "    \"payable\": false,\n"
                        + "    \"stateMutability\": \"view\",\n"
                        + "    \"type\": \"function\"\n"
                        + "  }";
        ABIDefinition ad = JsonUtils.fromJson(json, ABIDefinition.class);
        Assert.assertEquals("name", ad.getName());
        List<TypeReference<?>> list = ContractAbiUtil.paramFormat(ad.getOutputs());
        Assert.assertEquals(
                "[{\"type\":\"org.fisco.bcos.sdk.abi.datatypes.Utf8String\",\"indexed\":false,\"classType\":\"org.fisco.bcos.sdk.abi.datatypes.Utf8String\"}]",
                JsonUtils.toJson(list));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFull() {
        String json =
                "[{\"constant\":true,\"inputs\":[],\"name\":\"name\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"n\",\"type\":\"string\"}],\"name\":\"set\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"}]";
        List<ABIDefinition> ad = JsonUtils.fromJson(json, List.class, ABIDefinition.class);
        Assert.assertEquals("name", ad.get(0).getName());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCompare() {
        String json =
                "{\n"
                        + "    \"constant\": true,\n"
                        + "    \"inputs\": [],\n"
                        + "    \"name\": \"name\",\n"
                        + "    \"outputs\": [\n"
                        + "      {\n"
                        + "        \"name\": \"\",\n"
                        + "        \"type\": \"string\"\n"
                        + "      }\n"
                        + "    ],\n"
                        + "    \"payable\": false,\n"
                        + "    \"stateMutability\": \"view\",\n"
                        + "    \"type\": \"function\"\n"
                        + "  }";
        ABIDefinition ad = JsonUtils.fromJson(json, ABIDefinition.class);
        String fullJson =
                "[{\"constant\":true,\"inputs\":[],\"name\":\"name\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"n\",\"type\":\"string\"}],\"name\":\"set\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"}]";
        List<ABIDefinition> fullAd = JsonUtils.fromJson(fullJson, List.class, ABIDefinition.class);
        Assert.assertTrue(fullAd.get(0).equals(ad));
    }
}
