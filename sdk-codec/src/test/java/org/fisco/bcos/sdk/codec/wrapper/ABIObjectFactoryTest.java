package org.fisco.bcos.sdk.codec.wrapper;

import org.fisco.bcos.sdk.codec.abi.TestUtils;
import org.fisco.bcos.sdk.codec.wrapper.ABIObject;
import org.fisco.bcos.sdk.codec.wrapper.ABIObjectFactory;
import org.fisco.bcos.sdk.codec.wrapper.ContractABIDefinition;
import org.fisco.bcos.sdk.codec.*;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.utils.Hex;
import org.junit.Assert;
import org.junit.Test;

import java.util.Objects;

import static junit.framework.TestCase.*;

public class ABIObjectFactoryTest {

    @Test
    public void buildRawTypeObjectTest() {
        Assert.assertTrue(
                ABIObjectFactory.buildRawTypeObject("uint").getValueType() == ABIObject.ValueType.UINT);
        Assert.assertTrue(
                ABIObjectFactory.buildRawTypeObject("uint256").getValueType() == ABIObject.ValueType.UINT);
        Assert.assertTrue(
                ABIObjectFactory.buildRawTypeObject("int").getValueType() == ABIObject.ValueType.INT);
        Assert.assertTrue(
                ABIObjectFactory.buildRawTypeObject("int256").getValueType() == ABIObject.ValueType.INT);
        Assert.assertTrue(
                ABIObjectFactory.buildRawTypeObject("bool").getValueType() == ABIObject.ValueType.BOOL);
        Assert.assertTrue(
                ABIObjectFactory.buildRawTypeObject("address").getValueType()
                        == ABIObject.ValueType.ADDRESS);
        Assert.assertTrue(
                ABIObjectFactory.buildRawTypeObject("bytes").getValueType() == ABIObject.ValueType.DBYTES);
        Assert.assertTrue(
                ABIObjectFactory.buildRawTypeObject("bytes1").getValueType() == ABIObject.ValueType.BYTES);
        Assert.assertTrue(
                ABIObjectFactory.buildRawTypeObject("bytes32").getValueType() == ABIObject.ValueType.BYTES);
        Assert.assertTrue(
                ABIObjectFactory.buildRawTypeObject("string").getValueType() == ABIObject.ValueType.STRING);
    }

    @Test
    public void testContractABIDefinitionBuildMethodSignature() throws Exception {

    /*

    pragma solidity>=0.4.19 <0.7.0;
    pragma experimental ABIEncoderV2;
    contract Test{
       struct E { string s;}
       struct S { uint a; uint[] b; T[] c;T t;E e;}
       struct T { uint x; uint y;}

       function a(E memory e) public {}
       function b( T memory t)public {}
       function c(T memory t,E memory e) public {}
       function d(uint ) public {}
       function e(uint, string[]memory, bool) public {}
       function f(S memory,T memory,E memory,uint)public {}
       function g(S[] memory, T[] memory,E[] memory,uint256 [] memory)public {}
       function h(S[4] memory, T[4] memory, E[4] memory, uint256[4] memory) public {}
    }

    */

    /*
    {
        "d92a9e33": "a((string))",
        "5282e79c": "b((uint256,uint256))",
        "f332a566": "c((uint256,uint256),(string))",
        "7f6b590c": "d(uint256)",
        "b45c9d9f": "e(uint256,string[],bool)",
        "edb896f9": "f((uint256,uint256[],(uint256,uint256)[],(uint256,uint256),(string)),(uint256,uint256),(string),uint256)",
        "adc86690": "g((uint256,uint256[],(uint256,uint256)[],(uint256,uint256),(string))[],(uint256,uint256)[],(string)[],uint256[])",
        "7a3093eb": "h((uint256,uint256[],(uint256,uint256)[],(uint256,uint256),(string))[4],(uint256,uint256)[4],(string)[4],uint256[4])"
    }
    */

        String abi =
                "[{\"constant\":false,\"inputs\":[{\"components\":[{\"internalType\":\"string\",\"name\":\"s\",\"type\":\"string\"}],\"internalType\":\"struct Test.E\",\"name\":\"e\",\"type\":\"tuple\"}],\"name\":\"a\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"components\":[{\"internalType\":\"uint256\",\"name\":\"x\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"y\",\"type\":\"uint256\"}],\"internalType\":\"struct Test.T\",\"name\":\"t\",\"type\":\"tuple\"}],\"name\":\"b\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"components\":[{\"internalType\":\"uint256\",\"name\":\"x\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"y\",\"type\":\"uint256\"}],\"internalType\":\"struct Test.T\",\"name\":\"t\",\"type\":\"tuple\"},{\"components\":[{\"internalType\":\"string\",\"name\":\"s\",\"type\":\"string\"}],\"internalType\":\"struct Test.E\",\"name\":\"e\",\"type\":\"tuple\"}],\"name\":\"c\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"d\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"},{\"internalType\":\"string[]\",\"name\":\"\",\"type\":\"string[]\"},{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"name\":\"e\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"components\":[{\"internalType\":\"uint256\",\"name\":\"a\",\"type\":\"uint256\"},{\"internalType\":\"uint256[]\",\"name\":\"b\",\"type\":\"uint256[]\"},{\"components\":[{\"internalType\":\"uint256\",\"name\":\"x\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"y\",\"type\":\"uint256\"}],\"internalType\":\"struct Test.T[]\",\"name\":\"c\",\"type\":\"tuple[]\"},{\"components\":[{\"internalType\":\"uint256\",\"name\":\"x\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"y\",\"type\":\"uint256\"}],\"internalType\":\"struct Test.T\",\"name\":\"t\",\"type\":\"tuple\"},{\"components\":[{\"internalType\":\"string\",\"name\":\"s\",\"type\":\"string\"}],\"internalType\":\"struct Test.E\",\"name\":\"e\",\"type\":\"tuple\"}],\"internalType\":\"struct Test.S\",\"name\":\"\",\"type\":\"tuple\"},{\"components\":[{\"internalType\":\"uint256\",\"name\":\"x\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"y\",\"type\":\"uint256\"}],\"internalType\":\"struct Test.T\",\"name\":\"\",\"type\":\"tuple\"},{\"components\":[{\"internalType\":\"string\",\"name\":\"s\",\"type\":\"string\"}],\"internalType\":\"struct Test.E\",\"name\":\"\",\"type\":\"tuple\"},{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"f\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"pure\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"components\":[{\"internalType\":\"uint256\",\"name\":\"a\",\"type\":\"uint256\"},{\"internalType\":\"uint256[]\",\"name\":\"b\",\"type\":\"uint256[]\"},{\"components\":[{\"internalType\":\"uint256\",\"name\":\"x\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"y\",\"type\":\"uint256\"}],\"internalType\":\"struct Test.T[]\",\"name\":\"c\",\"type\":\"tuple[]\"},{\"components\":[{\"internalType\":\"uint256\",\"name\":\"x\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"y\",\"type\":\"uint256\"}],\"internalType\":\"struct Test.T\",\"name\":\"t\",\"type\":\"tuple\"},{\"components\":[{\"internalType\":\"string\",\"name\":\"s\",\"type\":\"string\"}],\"internalType\":\"struct Test.E\",\"name\":\"e\",\"type\":\"tuple\"}],\"internalType\":\"struct Test.S[]\",\"name\":\"\",\"type\":\"tuple[]\"},{\"components\":[{\"internalType\":\"uint256\",\"name\":\"x\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"y\",\"type\":\"uint256\"}],\"internalType\":\"struct Test.T[]\",\"name\":\"\",\"type\":\"tuple[]\"},{\"components\":[{\"internalType\":\"string\",\"name\":\"s\",\"type\":\"string\"}],\"internalType\":\"struct Test.E[]\",\"name\":\"\",\"type\":\"tuple[]\"},{\"internalType\":\"uint256[]\",\"name\":\"\",\"type\":\"uint256[]\"}],\"name\":\"g\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"pure\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"components\":[{\"internalType\":\"uint256\",\"name\":\"a\",\"type\":\"uint256\"},{\"internalType\":\"uint256[]\",\"name\":\"b\",\"type\":\"uint256[]\"},{\"components\":[{\"internalType\":\"uint256\",\"name\":\"x\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"y\",\"type\":\"uint256\"}],\"internalType\":\"struct Test.T[]\",\"name\":\"c\",\"type\":\"tuple[]\"},{\"components\":[{\"internalType\":\"uint256\",\"name\":\"x\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"y\",\"type\":\"uint256\"}],\"internalType\":\"struct Test.T\",\"name\":\"t\",\"type\":\"tuple\"},{\"components\":[{\"internalType\":\"string\",\"name\":\"s\",\"type\":\"string\"}],\"internalType\":\"struct Test.E\",\"name\":\"e\",\"type\":\"tuple\"}],\"internalType\":\"struct Test.S[4]\",\"name\":\"\",\"type\":\"tuple[4]\"},{\"components\":[{\"internalType\":\"uint256\",\"name\":\"x\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"y\",\"type\":\"uint256\"}],\"internalType\":\"struct Test.T[4]\",\"name\":\"\",\"type\":\"tuple[4]\"},{\"components\":[{\"internalType\":\"string\",\"name\":\"s\",\"type\":\"string\"}],\"internalType\":\"struct Test.E[4]\",\"name\":\"\",\"type\":\"tuple[4]\"},{\"internalType\":\"uint256[4]\",\"name\":\"\",\"type\":\"uint256[4]\"}],\"name\":\"h\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"pure\",\"type\":\"function\"}]";

        ContractABIDefinition contractABIDefinition = TestUtils.getContractABIDefinition(abi);
        CryptoSuite cryptoSuite = TestUtils.getCryptoSuite();
        FunctionEncoderInterface[] functionEncoders = new FunctionEncoderInterface[]{
                new org.fisco.bcos.sdk.codec.abi.FunctionEncoder(cryptoSuite),
                new org.fisco.bcos.sdk.codec.scale.FunctionEncoder(cryptoSuite),
        };

        for (FunctionEncoderInterface functionEncoder : functionEncoders) {
            assertEquals(
                    contractABIDefinition.getFunctions().get("a").get(0).getMethodSignatureAsString(),
                    "a((string))");
            assertEquals(
                    Hex.toHexString(
                            functionEncoder.buildMethodId(
                                    contractABIDefinition.getFunctions().get("a").get(0).getMethodSignatureAsString())),
                    "d92a9e33");
            assertTrue(
                    Objects.nonNull(
                            contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("0xd92a9e33"))));

            assertEquals(
                    contractABIDefinition.getFunctions().get("b").get(0).getMethodSignatureAsString(),
                    "b((uint256,uint256))");
            assertEquals(
                    Hex.toHexString(
                            functionEncoder.buildMethodId(
                                    contractABIDefinition.getFunctions().get("b").get(0).getMethodSignatureAsString())),
                    "5282e79c");
            assertTrue(
                    Objects.nonNull(
                            contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("0x5282e79c"))));

            assertEquals(
                    contractABIDefinition.getFunctions().get("c").get(0).getMethodSignatureAsString(),
                    "c((uint256,uint256),(string))");
            assertEquals(
                    Hex.toHexString(
                            functionEncoder.buildMethodId(
                                    contractABIDefinition.getFunctions().get("c").get(0).getMethodSignatureAsString())),
                    "f332a566");

            assertTrue(
                    Objects.nonNull(
                            contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("0xf332a566"))));

            assertEquals(
                    contractABIDefinition.getFunctions().get("d").get(0).getMethodSignatureAsString(),
                    "d(uint256)");
            assertEquals(
                    Hex.toHexString(
                            functionEncoder.buildMethodId(
                                    contractABIDefinition.getFunctions().get("d").get(0).getMethodSignatureAsString())),
                    "7f6b590c");
            assertTrue(
                    Objects.nonNull(
                            contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("0x7f6b590c"))));

            assertEquals(
                    contractABIDefinition.getFunctions().get("e").get(0).getMethodSignatureAsString(),
                    "e(uint256,string[],bool)");
            assertEquals(
                    Hex.toHexString(
                            functionEncoder.buildMethodId(
                                    contractABIDefinition.getFunctions().get("e").get(0).getMethodSignatureAsString())),
                    "b45c9d9f");
            assertTrue(
                    Objects.nonNull(
                            contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("0xb45c9d9f"))));

            assertEquals(
                    contractABIDefinition.getFunctions().get("f").get(0).getMethodSignatureAsString(),
                    "f((uint256,uint256[],(uint256,uint256)[],(uint256,uint256),(string)),(uint256,uint256),(string),uint256)");
            assertEquals(
                    Hex.toHexString(
                            functionEncoder.buildMethodId(
                                    contractABIDefinition.getFunctions().get("f").get(0).getMethodSignatureAsString())),
                    "edb896f9");

            assertTrue(
                    Objects.nonNull(
                            contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("0xedb896f9"))));

            assertEquals(
                    contractABIDefinition.getFunctions().get("g").get(0).getMethodSignatureAsString(),
                    "g((uint256,uint256[],(uint256,uint256)[],(uint256,uint256),(string))[],(uint256,uint256)[],(string)[],uint256[])");
            assertEquals(
                    Hex.toHexString(
                            contractABIDefinition
                                    .getFunctions()
                                    .get("g")
                                    .get(0)
                                    .getMethodId(TestUtils.getCryptoSuite())),
                    "adc86690");

            assertTrue(
                    Objects.nonNull(
                            contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("0xadc86690"))));

            assertEquals(
                    contractABIDefinition.getFunctions().get("h").get(0).getMethodSignatureAsString(),
                    "h((uint256,uint256[],(uint256,uint256)[],(uint256,uint256),(string))[4],(uint256,uint256)[4],(string)[4],uint256[4])");
            assertEquals(
                    Hex.toHexString(
                            contractABIDefinition
                                    .getFunctions()
                                    .get("h")
                                    .get(0)
                                    .getMethodId(TestUtils.getCryptoSuite())),
                    "7a3093eb");
            assertTrue(
                    Objects.nonNull(
                            contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("0xadc86690"))));
        }

    }

    @Test
    public void testContractABIDefinitionDynamic() throws Exception {

    /*

    pragma solidity>=0.4.19 <0.7.0;
    pragma experimental ABIEncoderV2;
    contract Test{
       struct E { string s;}
       struct S { uint a; uint[] b; T[] c;T t;E e;}
       struct T { uint x; uint y;}

       function a(E memory e) public {}
       function b( T memory t)public {}
       function c(T memory t,E memory e) public {}
       function d(uint ) public {}
       function e(uint, string[]memory, bool) public {}
       function f(S memory,T memory,E memory,uint)public {}
       function g(S[] memory, T[] memory,E[] memory,uint256 [] memory)public {}
       function h(S[4] memory, T[4] memory, E[4] memory, uint256[4] memory) public {}
    }

    */

    /*
    {
        "d92a9e33": "a((string))",
        "5282e79c": "b((uint256,uint256))",
        "f332a566": "c((uint256,uint256),(string))",
        "7f6b590c": "d(uint256)",
        "b45c9d9f": "e(uint256,string[],bool)",
        "edb896f9": "f((uint256,uint256[],(uint256,uint256)[],(uint256,uint256),(string)),(uint256,uint256),(string),uint256)",
        "adc86690": "g((uint256,uint256[],(uint256,uint256)[],(uint256,uint256),(string))[],(uint256,uint256)[],(string)[],uint256[])",
        "7a3093eb": "h((uint256,uint256[],(uint256,uint256)[],(uint256,uint256),(string))[4],(uint256,uint256)[4],(string)[4],uint256[4])"
    }
    */

        String abi =
                "[{\"constant\":false,\"inputs\":[{\"components\":[{\"internalType\":\"string\",\"name\":\"s\",\"type\":\"string\"}],\"internalType\":\"struct Test.E\",\"name\":\"e\",\"type\":\"tuple\"}],\"name\":\"a\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"components\":[{\"internalType\":\"uint256\",\"name\":\"x\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"y\",\"type\":\"uint256\"}],\"internalType\":\"struct Test.T\",\"name\":\"t\",\"type\":\"tuple\"}],\"name\":\"b\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"components\":[{\"internalType\":\"uint256\",\"name\":\"x\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"y\",\"type\":\"uint256\"}],\"internalType\":\"struct Test.T\",\"name\":\"t\",\"type\":\"tuple\"},{\"components\":[{\"internalType\":\"string\",\"name\":\"s\",\"type\":\"string\"}],\"internalType\":\"struct Test.E\",\"name\":\"e\",\"type\":\"tuple\"}],\"name\":\"c\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"d\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"},{\"internalType\":\"string[]\",\"name\":\"\",\"type\":\"string[]\"},{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"name\":\"e\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"components\":[{\"internalType\":\"uint256\",\"name\":\"a\",\"type\":\"uint256\"},{\"internalType\":\"uint256[]\",\"name\":\"b\",\"type\":\"uint256[]\"},{\"components\":[{\"internalType\":\"uint256\",\"name\":\"x\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"y\",\"type\":\"uint256\"}],\"internalType\":\"struct Test.T[]\",\"name\":\"c\",\"type\":\"tuple[]\"},{\"components\":[{\"internalType\":\"uint256\",\"name\":\"x\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"y\",\"type\":\"uint256\"}],\"internalType\":\"struct Test.T\",\"name\":\"t\",\"type\":\"tuple\"},{\"components\":[{\"internalType\":\"string\",\"name\":\"s\",\"type\":\"string\"}],\"internalType\":\"struct Test.E\",\"name\":\"e\",\"type\":\"tuple\"}],\"internalType\":\"struct Test.S\",\"name\":\"\",\"type\":\"tuple\"},{\"components\":[{\"internalType\":\"uint256\",\"name\":\"x\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"y\",\"type\":\"uint256\"}],\"internalType\":\"struct Test.T\",\"name\":\"\",\"type\":\"tuple\"},{\"components\":[{\"internalType\":\"string\",\"name\":\"s\",\"type\":\"string\"}],\"internalType\":\"struct Test.E\",\"name\":\"\",\"type\":\"tuple\"},{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"f\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"pure\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"components\":[{\"internalType\":\"uint256\",\"name\":\"a\",\"type\":\"uint256\"},{\"internalType\":\"uint256[]\",\"name\":\"b\",\"type\":\"uint256[]\"},{\"components\":[{\"internalType\":\"uint256\",\"name\":\"x\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"y\",\"type\":\"uint256\"}],\"internalType\":\"struct Test.T[]\",\"name\":\"c\",\"type\":\"tuple[]\"},{\"components\":[{\"internalType\":\"uint256\",\"name\":\"x\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"y\",\"type\":\"uint256\"}],\"internalType\":\"struct Test.T\",\"name\":\"t\",\"type\":\"tuple\"},{\"components\":[{\"internalType\":\"string\",\"name\":\"s\",\"type\":\"string\"}],\"internalType\":\"struct Test.E\",\"name\":\"e\",\"type\":\"tuple\"}],\"internalType\":\"struct Test.S[]\",\"name\":\"\",\"type\":\"tuple[]\"},{\"components\":[{\"internalType\":\"uint256\",\"name\":\"x\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"y\",\"type\":\"uint256\"}],\"internalType\":\"struct Test.T[]\",\"name\":\"\",\"type\":\"tuple[]\"},{\"components\":[{\"internalType\":\"string\",\"name\":\"s\",\"type\":\"string\"}],\"internalType\":\"struct Test.E[]\",\"name\":\"\",\"type\":\"tuple[]\"},{\"internalType\":\"uint256[]\",\"name\":\"\",\"type\":\"uint256[]\"}],\"name\":\"g\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"pure\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"components\":[{\"internalType\":\"uint256\",\"name\":\"a\",\"type\":\"uint256\"},{\"internalType\":\"uint256[]\",\"name\":\"b\",\"type\":\"uint256[]\"},{\"components\":[{\"internalType\":\"uint256\",\"name\":\"x\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"y\",\"type\":\"uint256\"}],\"internalType\":\"struct Test.T[]\",\"name\":\"c\",\"type\":\"tuple[]\"},{\"components\":[{\"internalType\":\"uint256\",\"name\":\"x\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"y\",\"type\":\"uint256\"}],\"internalType\":\"struct Test.T\",\"name\":\"t\",\"type\":\"tuple\"},{\"components\":[{\"internalType\":\"string\",\"name\":\"s\",\"type\":\"string\"}],\"internalType\":\"struct Test.E\",\"name\":\"e\",\"type\":\"tuple\"}],\"internalType\":\"struct Test.S[4]\",\"name\":\"\",\"type\":\"tuple[4]\"},{\"components\":[{\"internalType\":\"uint256\",\"name\":\"x\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"y\",\"type\":\"uint256\"}],\"internalType\":\"struct Test.T[4]\",\"name\":\"\",\"type\":\"tuple[4]\"},{\"components\":[{\"internalType\":\"string\",\"name\":\"s\",\"type\":\"string\"}],\"internalType\":\"struct Test.E[4]\",\"name\":\"\",\"type\":\"tuple[4]\"},{\"internalType\":\"uint256[4]\",\"name\":\"\",\"type\":\"uint256[4]\"}],\"name\":\"h\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"pure\",\"type\":\"function\"}]";

        ContractABIDefinition contractABIDefinition = TestUtils.getContractABIDefinition(abi);
        ABIObject inputObject0 =
                ABIObjectFactory.createInputObject(contractABIDefinition.getFunctions().get("a").get(0));
        assertTrue(inputObject0.getStructFields().get(0).isDynamic());

        ABIObject inputObject1 =
                ABIObjectFactory.createInputObject(contractABIDefinition.getFunctions().get("b").get(0));
        assertTrue(!inputObject1.getStructFields().get(0).isDynamic());

        ABIObject inputObject2 =
                ABIObjectFactory.createInputObject(contractABIDefinition.getFunctions().get("c").get(0));
        assertTrue(!inputObject2.getStructFields().get(0).isDynamic());
        assertTrue(inputObject2.getStructFields().get(1).isDynamic());

        ABIObject inputObject3 =
                ABIObjectFactory.createInputObject(contractABIDefinition.getFunctions().get("d").get(0));
        assertTrue(!inputObject3.getStructFields().get(0).isDynamic());

        ABIObject inputObject4 =
                ABIObjectFactory.createInputObject(contractABIDefinition.getFunctions().get("e").get(0));
        assertTrue(!inputObject4.getStructFields().get(0).isDynamic());
        assertTrue(inputObject4.getStructFields().get(1).isDynamic());
        assertTrue(!inputObject4.getStructFields().get(2).isDynamic());

        ABIObject inputObject5 =
                ABIObjectFactory.createInputObject(contractABIDefinition.getFunctions().get("f").get(0));
        assertTrue(inputObject5.getStructFields().get(0).isDynamic());
        assertTrue(!inputObject5.getStructFields().get(1).isDynamic());
        assertTrue(inputObject5.getStructFields().get(2).isDynamic());
        assertTrue(!inputObject5.getStructFields().get(3).isDynamic());

        ABIObject inputObject6 =
                ABIObjectFactory.createInputObject(contractABIDefinition.getFunctions().get("g").get(0));
        assertTrue(inputObject6.getStructFields().get(0).isDynamic());
        assertTrue(inputObject6.getStructFields().get(1).isDynamic());
        assertTrue(inputObject6.getStructFields().get(2).isDynamic());
        assertTrue(inputObject6.getStructFields().get(3).isDynamic());

        ABIObject inputObject7 =
                ABIObjectFactory.createInputObject(contractABIDefinition.getFunctions().get("h").get(0));
        assertTrue(inputObject7.getStructFields().get(0).isDynamic());
        assertTrue(!inputObject7.getStructFields().get(1).isDynamic());
        assertTrue(inputObject7.getStructFields().get(2).isDynamic());
        assertTrue(!inputObject7.getStructFields().get(3).isDynamic());
    }

    @Test
    public void testContractABIDefinitionBuildMethodSignature0() throws Exception {
    /*
    
    pragma solidity >=0.5.0 <0.6.0;
    pragma experimental ABIEncoderV2;

    contract Proxy {
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

        // // test(int256,(string,int256,(int256,int256,int256)[])[],string)
        function test(int a, Info[] memory b, string memory c) public returns(int) {
            // emit output1(a, b, c);
        }

        function test_empty() public returns(int a, Info[][] memory b, string memory c) {

        }
    }
    */

    /*
    "00a3c75d": "test(int256,(string,int256,(int256,int256,int256)[])[],string)",
    "6057db30": "test1()"
     */

        String abi =
                "[{\"constant\":false,\"inputs\":[{\"name\":\"a\",\"type\":\"int256\"},{\"components\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"count\",\"type\":\"int256\"},{\"components\":[{\"name\":\"a\",\"type\":\"int256\"},{\"name\":\"b\",\"type\":\"int256\"},{\"name\":\"c\",\"type\":\"int256\"}],\"name\":\"items\",\"type\":\"tuple[]\"}],\"name\":\"b\",\"type\":\"tuple[]\"},{\"name\":\"c\",\"type\":\"string\"}],\"name\":\"test\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"test1\",\"outputs\":[{\"name\":\"a\",\"type\":\"int256\"},{\"components\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"count\",\"type\":\"int256\"},{\"components\":[{\"name\":\"a\",\"type\":\"int256\"},{\"name\":\"b\",\"type\":\"int256\"},{\"name\":\"c\",\"type\":\"int256\"}],\"name\":\"items\",\"type\":\"tuple[]\"}],\"name\":\"b\",\"type\":\"tuple[][]\"},{\"name\":\"c\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"a\",\"type\":\"int256\"},{\"components\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"count\",\"type\":\"int256\"},{\"components\":[{\"name\":\"a\",\"type\":\"int256\"},{\"name\":\"b\",\"type\":\"int256\"},{\"name\":\"c\",\"type\":\"int256\"}],\"name\":\"items\",\"type\":\"tuple[]\"}],\"indexed\":false,\"name\":\"b\",\"type\":\"tuple[]\"},{\"indexed\":false,\"name\":\"c\",\"type\":\"string\"}],\"name\":\"output1\",\"type\":\"event\"}]";
        ContractABIDefinition contractABIDefinition = TestUtils.getContractABIDefinition(abi);

        assertEquals(
                contractABIDefinition.getFunctions().get("test").get(0).getMethodSignatureAsString(),
                "test(int256,(string,int256,(int256,int256,int256)[])[],string)");

        assertEquals(
                Hex.toHexString(
                        contractABIDefinition
                                .getFunctions()
                                .get("test")
                                .get(0)
                                .getMethodId(TestUtils.getCryptoSuite())),
                "00a3c75d");
        assertTrue(
                Objects.nonNull(contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("00a3c75d"))));

        assertEquals(
                contractABIDefinition.getFunctions().get("test1").get(0).getMethodSignatureAsString(),
                "test1()");

        Assert.assertArrayEquals(
                contractABIDefinition
                        .getFunctions()
                        .get("test1")
                        .get(0)
                        .getMethodId(TestUtils.getCryptoSuite()),
                Hex.decode("6b59084d"));
        assertTrue(
                Objects.nonNull(contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("6b59084d"))));
    }

    @Test
    public void testContractABIDefinitionDynamic0() throws Exception {
    /*
    
    pragma solidity >=0.5.0 <0.6.0;
    pragma experimental ABIEncoderV2;

    contract Proxy {
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

        // // test(int256,(string,int256,(int256,int256,int256)[])[],string)
        function test(int a, Info[] memory b, string memory c) public returns(int) {
            // emit output1(a, b, c);
        }

        function test_empty() public returns(int a, Info[][] memory b, string memory c) {

        }
    }
    */

    /*
    "00a3c75d": "test(int256,(string,int256,(int256,int256,int256)[])[],string)",
    "6057db30": "test1()"
     */

        String abi =
                "[{\"constant\":false,\"inputs\":[{\"name\":\"a\",\"type\":\"int256\"},{\"components\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"count\",\"type\":\"int256\"},{\"components\":[{\"name\":\"a\",\"type\":\"int256\"},{\"name\":\"b\",\"type\":\"int256\"},{\"name\":\"c\",\"type\":\"int256\"}],\"name\":\"items\",\"type\":\"tuple[]\"}],\"name\":\"b\",\"type\":\"tuple[]\"},{\"name\":\"c\",\"type\":\"string\"}],\"name\":\"test\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"test1\",\"outputs\":[{\"name\":\"a\",\"type\":\"int256\"},{\"components\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"count\",\"type\":\"int256\"},{\"components\":[{\"name\":\"a\",\"type\":\"int256\"},{\"name\":\"b\",\"type\":\"int256\"},{\"name\":\"c\",\"type\":\"int256\"}],\"name\":\"items\",\"type\":\"tuple[]\"}],\"name\":\"b\",\"type\":\"tuple[][]\"},{\"name\":\"c\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"a\",\"type\":\"int256\"},{\"components\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"count\",\"type\":\"int256\"},{\"components\":[{\"name\":\"a\",\"type\":\"int256\"},{\"name\":\"b\",\"type\":\"int256\"},{\"name\":\"c\",\"type\":\"int256\"}],\"name\":\"items\",\"type\":\"tuple[]\"}],\"indexed\":false,\"name\":\"b\",\"type\":\"tuple[]\"},{\"indexed\":false,\"name\":\"c\",\"type\":\"string\"}],\"name\":\"output1\",\"type\":\"event\"}]";
        ContractABIDefinition contractABIDefinition = TestUtils.getContractABIDefinition(abi);

        ABIObject inputObject =
                ABIObjectFactory.createInputObject(contractABIDefinition.getFunctions().get("test").get(0));
        assertTrue(!inputObject.getStructFields().get(0).isDynamic());
        assertTrue(inputObject.getStructFields().get(1).isDynamic());
        assertTrue(inputObject.getStructFields().get(2).isDynamic());
    }

    @Test
    public void testContractABIDefinitionBuildMethodSignature2() throws Exception {
    /*

    pragma solidity >=0.5.0 <0.6.0;
    pragma experimental ABIEncoderV2;

    contract WeCrossProxy {

        string constant version = "v1.0.0-rc4";

        // per step of transaction
        struct TransactionStep {
            string path;
            uint256 timestamp;
            address contractAddress;
            string func;
            bytes args;
        }

        // information of transaction
        struct TransactionInfo {
            string[] allPaths;  // all paths related to this transaction
            string[] paths;     // paths related to current chain
            address[] contractAddresses; // locked addressed in current chain
            uint8 status;    // 0-Start 1-Commit 2-Rollback
            uint256 startTimestamp;
            uint256 commitTimestamp;
            uint256 rollbackTimestamp;
            uint256[] seqs;   // sequence of each step
            uint256 stepNum;  // step number
        }

        struct ContractInfo {
            bool locked;     // isolation control, read-committed
            string path;
            string transactionID;
        }

        constructor() public {

        }

        function getVersion(string[] memory _args) public pure
        returns(string[] memory)
        {

        }

        function getMaxStep(string[] memory _args) public view
        returns(string[] memory)
        {

        }

        function setMaxStep(string[] memory _args) public
        {

        }

        function addPath(string[] memory _args) public
        {

        }

        function getPaths(string[] memory _args) public view
        returns (string[] memory)
        {

        }

        function deletePathList(string[] memory _args) public
        {

        }

        // constant call
        function constantCall(string memory _transactionID, string memory _path, string memory _func, bytes memory _args) public
        returns(bytes memory)
        {

        }

        // non-constant call
        function sendTransaction(string memory _transactionID, uint256 _seq, string memory _path, string memory _func, bytes memory _args) public
        returns(bytes memory)
        {

        }

        function startTransaction(string[] memory _args) public
        returns(string[] memory)
        {

        }


        function commitTransaction(string[] memory _args) public
        returns(string[] memory)
        {

        }


        function rollbackTransaction(string[] memory _args) public
        returns(string[] memory)
        {

        }


        function getTransactionInfo(string[] memory _args) public view
        returns(string[] memory)
        {

        }

        // called by router to check transaction status
        function getLatestTransactionInfo() public view
        returns(string[] memory)
        {

        }

        function rollbackAndDeleteTransaction(string[] memory _args) public
        returns (string[] memory)
        {

        }

        function getLatestTransaction() public view
        returns (string memory)
        {

        }

        function addTransaction(string memory _transactionID) internal
        {

        }

        function deleteTransaction(string memory _transactionID) internal
        returns (string[] memory)
        {

        }

        function callContract(address _contractAddress, string memory _sig, bytes memory _args) internal
        returns(bytes memory result)
        {

        }

        function getAddressByPath(string memory _path) internal view
        returns (address)
        {

        }


        function getNameByPath(string memory _path) internal pure
        returns (string memory)
        {

        }

        // "transactionSteps": [{"seq": 0, "contract": "0x12","path": "a.b.c","timestamp": "123","func": "test1(string)","args": "aaa"},{"seq": 1, "contract": "0x12","path": "a.b.c","timestamp": "123","func": "test2(string)","args": "bbb"}]
        function transactionStepArrayToJson(string memory _transactionID, uint256[] memory _seqs, uint256 _len) internal view
        returns(string memory result)
        {

        }

        // {"seq": 0, "contract": "0x12","path": "a.b.c","timestamp": "123","func": "test2(string)","args": "bbb"}
        function transactionStepToJson(TransactionStep memory _step, uint256 _seq) internal pure
        returns(string memory)
        {

        }


        function stringToUint256(string memory _str) public pure
        returns (uint256)
        {

        }
    }
    */

    /*
    "e1207bee": "addPath(string[])",
            "063ff7ef": "commitTransaction(string[])",
            "b54138b0": "constantCall(string,string,string,bytes)",
            "f4fa9d03": "deletePathList(string[])",
            "6ccc29dc": "getLatestTransaction()",
            "9edd3441": "getLatestTransactionInfo()",
            "cb797d2f": "getMaxStep(string[])",
            "4efcaed0": "getPaths(string[])",
            "d55c01f7": "getTransactionInfo(string[])",
            "8bc4827c": "getVersion(string[])",
            "8c31f9ad": "rollbackAndDeleteTransaction(string[])",
            "51cd3824": "rollbackTransaction(string[])",
            "772d0b53": "sendTransaction(string,uint256,string,string,bytes)",
            "18a56b67": "setMaxStep(string[])",
            "e25a0866": "startTransaction(string[])",
            "ac5d3723": "stringToUint256(string)"
    */

        String abi =
                "[{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"string[]\",\"name\":\"_args\",\"type\":\"string[]\"}],\"name\":\"addPath\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"string[]\",\"name\":\"_args\",\"type\":\"string[]\"}],\"name\":\"commitTransaction\",\"outputs\":[{\"internalType\":\"string[]\",\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"string\",\"name\":\"_transactionID\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_path\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_func\",\"type\":\"string\"},{\"internalType\":\"bytes\",\"name\":\"_args\",\"type\":\"bytes\"}],\"name\":\"constantCall\",\"outputs\":[{\"internalType\":\"bytes\",\"name\":\"\",\"type\":\"bytes\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"string[]\",\"name\":\"_args\",\"type\":\"string[]\"}],\"name\":\"deletePathList\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"getLatestTransaction\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"getLatestTransactionInfo\",\"outputs\":[{\"internalType\":\"string[]\",\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"internalType\":\"string[]\",\"name\":\"_args\",\"type\":\"string[]\"}],\"name\":\"getMaxStep\",\"outputs\":[{\"internalType\":\"string[]\",\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"internalType\":\"string[]\",\"name\":\"_args\",\"type\":\"string[]\"}],\"name\":\"getPaths\",\"outputs\":[{\"internalType\":\"string[]\",\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"internalType\":\"string[]\",\"name\":\"_args\",\"type\":\"string[]\"}],\"name\":\"getTransactionInfo\",\"outputs\":[{\"internalType\":\"string[]\",\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"internalType\":\"string[]\",\"name\":\"_args\",\"type\":\"string[]\"}],\"name\":\"getVersion\",\"outputs\":[{\"internalType\":\"string[]\",\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"pure\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"string[]\",\"name\":\"_args\",\"type\":\"string[]\"}],\"name\":\"rollbackAndDeleteTransaction\",\"outputs\":[{\"internalType\":\"string[]\",\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"string[]\",\"name\":\"_args\",\"type\":\"string[]\"}],\"name\":\"rollbackTransaction\",\"outputs\":[{\"internalType\":\"string[]\",\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"string\",\"name\":\"_transactionID\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"_seq\",\"type\":\"uint256\"},{\"internalType\":\"string\",\"name\":\"_path\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_func\",\"type\":\"string\"},{\"internalType\":\"bytes\",\"name\":\"_args\",\"type\":\"bytes\"}],\"name\":\"sendTransaction\",\"outputs\":[{\"internalType\":\"bytes\",\"name\":\"\",\"type\":\"bytes\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"string[]\",\"name\":\"_args\",\"type\":\"string[]\"}],\"name\":\"setMaxStep\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"string[]\",\"name\":\"_args\",\"type\":\"string[]\"}],\"name\":\"startTransaction\",\"outputs\":[{\"internalType\":\"string[]\",\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"internalType\":\"string\",\"name\":\"_str\",\"type\":\"string\"}],\"name\":\"stringToUint256\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"pure\",\"type\":\"function\"}]";
        ContractABIDefinition contractABIDefinition = TestUtils.getContractABIDefinition(abi);

        assertEquals(
                contractABIDefinition.getFunctions().get("addPath").get(0).getMethodSignatureAsString(),
                "addPath(string[])");

        assertEquals(
                Hex.toHexString(
                        contractABIDefinition
                                .getFunctions()
                                .get("addPath")
                                .get(0)
                                .getMethodId(TestUtils.getCryptoSuite())),
                "e1207bee");
        assertTrue(
                Objects.nonNull(
                        contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("0xe1207bee"))));

        assertEquals(
                contractABIDefinition
                        .getFunctions()
                        .get("commitTransaction")
                        .get(0)
                        .getMethodSignatureAsString(),
                "commitTransaction(string[])");

        assertEquals(
                Hex.toHexString(
                        contractABIDefinition
                                .getFunctions()
                                .get("commitTransaction")
                                .get(0)
                                .getMethodId(TestUtils.getCryptoSuite())),
                "063ff7ef");
        assertTrue(
                Objects.nonNull(
                        contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("0x063ff7ef"))));

        assertEquals(
                contractABIDefinition
                        .getFunctions()
                        .get("constantCall")
                        .get(0)
                        .getMethodSignatureAsString(),
                "constantCall(string,string,string,bytes)");

        assertEquals(
                Hex.toHexString(
                        contractABIDefinition
                                .getFunctions()
                                .get("constantCall")
                                .get(0)
                                .getMethodId(TestUtils.getCryptoSuite())),
                "b54138b0");
        assertTrue(
                Objects.nonNull(
                        contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("0xb54138b0"))));

        assertEquals(
                contractABIDefinition
                        .getFunctions()
                        .get("deletePathList")
                        .get(0)
                        .getMethodSignatureAsString(),
                "deletePathList(string[])");

        assertEquals(
                Hex.toHexString(
                        contractABIDefinition
                                .getFunctions()
                                .get("deletePathList")
                                .get(0)
                                .getMethodId(TestUtils.getCryptoSuite())),
                "f4fa9d03");
        assertTrue(
                Objects.nonNull(
                        contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("0xf4fa9d03"))));

        assertEquals(
                contractABIDefinition
                        .getFunctions()
                        .get("getLatestTransaction")
                        .get(0)
                        .getMethodSignatureAsString(),
                "getLatestTransaction()");

        assertEquals(
                Hex.toHexString(
                        contractABIDefinition
                                .getFunctions()
                                .get("getLatestTransaction")
                                .get(0)
                                .getMethodId(TestUtils.getCryptoSuite())),
                "6ccc29dc");
        assertTrue(
                Objects.nonNull(
                        contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("0x6ccc29dc"))));

        assertEquals(
                contractABIDefinition
                        .getFunctions()
                        .get("getLatestTransactionInfo")
                        .get(0)
                        .getMethodSignatureAsString(),
                "getLatestTransactionInfo()");

        assertEquals(
                Hex.toHexString(
                        contractABIDefinition
                                .getFunctions()
                                .get("getLatestTransactionInfo")
                                .get(0)
                                .getMethodId(TestUtils.getCryptoSuite())),
                "9edd3441");
        assertTrue(
                Objects.nonNull(
                        contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("0x9edd3441"))));

        assertEquals(
                contractABIDefinition.getFunctions().get("getMaxStep").get(0).getMethodSignatureAsString(),
                "getMaxStep(string[])");

        assertEquals(
                Hex.toHexString(
                        contractABIDefinition
                                .getFunctions()
                                .get("getMaxStep")
                                .get(0)
                                .getMethodId(TestUtils.getCryptoSuite())),
                "cb797d2f");
        assertTrue(
                Objects.nonNull(
                        contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("0xcb797d2f"))));

        assertEquals(
                contractABIDefinition.getFunctions().get("getPaths").get(0).getMethodSignatureAsString(),
                "getPaths(string[])");

        assertEquals(
                Hex.toHexString(
                        contractABIDefinition
                                .getFunctions()
                                .get("getPaths")
                                .get(0)
                                .getMethodId(TestUtils.getCryptoSuite())),
                "4efcaed0");
        assertTrue(
                Objects.nonNull(
                        contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("0x4efcaed0"))));

        assertEquals(
                contractABIDefinition
                        .getFunctions()
                        .get("getTransactionInfo")
                        .get(0)
                        .getMethodSignatureAsString(),
                "getTransactionInfo(string[])");

        assertEquals(
                Hex.toHexString(
                        contractABIDefinition
                                .getFunctions()
                                .get("getTransactionInfo")
                                .get(0)
                                .getMethodId(TestUtils.getCryptoSuite())),
                "d55c01f7");
        assertTrue(
                Objects.nonNull(
                        contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("0xd55c01f7"))));

        assertEquals(
                contractABIDefinition.getFunctions().get("getVersion").get(0).getMethodSignatureAsString(),
                "getVersion(string[])");

        assertEquals(
                Hex.toHexString(
                        contractABIDefinition
                                .getFunctions()
                                .get("getVersion")
                                .get(0)
                                .getMethodId(TestUtils.getCryptoSuite())),
                "8bc4827c");
        assertTrue(
                Objects.nonNull(
                        contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("0x8bc4827c"))));

        assertEquals(
                contractABIDefinition
                        .getFunctions()
                        .get("rollbackAndDeleteTransaction")
                        .get(0)
                        .getMethodSignatureAsString(),
                "rollbackAndDeleteTransaction(string[])");

        assertEquals(
                Hex.toHexString(
                        contractABIDefinition
                                .getFunctions()
                                .get("rollbackAndDeleteTransaction")
                                .get(0)
                                .getMethodId(TestUtils.getCryptoSuite())),
                "8c31f9ad");
        assertTrue(
                Objects.nonNull(
                        contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("0x8c31f9ad"))));

        assertEquals(
                contractABIDefinition
                        .getFunctions()
                        .get("sendTransaction")
                        .get(0)
                        .getMethodSignatureAsString(),
                "sendTransaction(string,uint256,string,string,bytes)");

        assertEquals(
                Hex.toHexString(
                        contractABIDefinition
                                .getFunctions()
                                .get("sendTransaction")
                                .get(0)
                                .getMethodId(TestUtils.getCryptoSuite())),
                "772d0b53");
        assertTrue(
                Objects.nonNull(
                        contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("0x772d0b53"))));

        assertEquals(
                contractABIDefinition
                        .getFunctions()
                        .get("rollbackTransaction")
                        .get(0)
                        .getMethodSignatureAsString(),
                "rollbackTransaction(string[])");

        assertEquals(
                Hex.toHexString(
                        contractABIDefinition
                                .getFunctions()
                                .get("rollbackTransaction")
                                .get(0)
                                .getMethodId(TestUtils.getCryptoSuite())),
                "51cd3824");
        assertTrue(
                Objects.nonNull(
                        contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("0x51cd3824"))));

        assertEquals(
                contractABIDefinition.getFunctions().get("setMaxStep").get(0).getMethodSignatureAsString(),
                "setMaxStep(string[])");

        assertEquals(
                Hex.toHexString(
                        contractABIDefinition
                                .getFunctions()
                                .get("setMaxStep")
                                .get(0)
                                .getMethodId(TestUtils.getCryptoSuite())),
                "18a56b67");
        assertTrue(
                Objects.nonNull(
                        contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("0x18a56b67"))));

        assertEquals(
                contractABIDefinition
                        .getFunctions()
                        .get("startTransaction")
                        .get(0)
                        .getMethodSignatureAsString(),
                "startTransaction(string[])");

        assertEquals(
                Hex.toHexString(
                        contractABIDefinition
                                .getFunctions()
                                .get("startTransaction")
                                .get(0)
                                .getMethodId(TestUtils.getCryptoSuite())),
                "e25a0866");
        assertTrue(
                Objects.nonNull(
                        contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("0xe25a0866"))));

        assertEquals(
                contractABIDefinition
                        .getFunctions()
                        .get("stringToUint256")
                        .get(0)
                        .getMethodSignatureAsString(),
                "stringToUint256(string)");

        assertEquals(
                Hex.toHexString(
                        contractABIDefinition
                                .getFunctions()
                                .get("stringToUint256")
                                .get(0)
                                .getMethodId(TestUtils.getCryptoSuite())),
                "ac5d3723");
        assertTrue(
                Objects.nonNull(
                        contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("0xac5d3723"))));
    }

    @Test
    public void testContractABIDefinitionDynamic1() throws Exception {
    /*

    pragma solidity >=0.5.0 <0.6.0;
    pragma experimental ABIEncoderV2;

    contract WeCrossProxy {

        string constant version = "v1.0.0-rc4";

        // per step of transaction
        struct TransactionStep {
            string path;
            uint256 timestamp;
            address contractAddress;
            string func;
            bytes args;
        }

        // information of transaction
        struct TransactionInfo {
            string[] allPaths;  // all paths related to this transaction
            string[] paths;     // paths related to current chain
            address[] contractAddresses; // locked addressed in current chain
            uint8 status;    // 0-Start 1-Commit 2-Rollback
            uint256 startTimestamp;
            uint256 commitTimestamp;
            uint256 rollbackTimestamp;
            uint256[] seqs;   // sequence of each step
            uint256 stepNum;  // step number
        }

        struct ContractInfo {
            bool locked;     // isolation control, read-committed
            string path;
            string transactionID;
        }

        constructor() public {

        }

        function getVersion(string[] memory _args) public pure
        returns(string[] memory)
        {

        }

        function getMaxStep(string[] memory _args) public view
        returns(string[] memory)
        {

        }

        function setMaxStep(string[] memory _args) public
        {

        }

        function addPath(string[] memory _args) public
        {

        }

        function getPaths(string[] memory _args) public view
        returns (string[] memory)
        {

        }

        function deletePathList(string[] memory _args) public
        {

        }

        // constant call
        function constantCall(string memory _transactionID, string memory _path, string memory _func, bytes memory _args) public
        returns(bytes memory)
        {

        }

        // non-constant call
        function sendTransaction(string memory _transactionID, uint256 _seq, string memory _path, string memory _func, bytes memory _args) public
        returns(bytes memory)
        {

        }

        function startTransaction(string[] memory _args) public
        returns(string[] memory)
        {

        }


        function commitTransaction(string[] memory _args) public
        returns(string[] memory)
        {

        }


        function rollbackTransaction(string[] memory _args) public
        returns(string[] memory)
        {

        }


        function getTransactionInfo(string[] memory _args) public view
        returns(string[] memory)
        {

        }

        // called by router to check transaction status
        function getLatestTransactionInfo() public view
        returns(string[] memory)
        {

        }

        function rollbackAndDeleteTransaction(string[] memory _args) public
        returns (string[] memory)
        {

        }

        function getLatestTransaction() public view
        returns (string memory)
        {

        }

        function addTransaction(string memory _transactionID) internal
        {

        }

        function deleteTransaction(string memory _transactionID) internal
        returns (string[] memory)
        {

        }

        function callContract(address _contractAddress, string memory _sig, bytes memory _args) internal
        returns(bytes memory result)
        {

        }

        function getAddressByPath(string memory _path) internal view
        returns (address)
        {

        }


        function getNameByPath(string memory _path) internal pure
        returns (string memory)
        {

        }

        // "transactionSteps": [{"seq": 0, "contract": "0x12","path": "a.b.c","timestamp": "123","func": "test1(string)","args": "aaa"},{"seq": 1, "contract": "0x12","path": "a.b.c","timestamp": "123","func": "test2(string)","args": "bbb"}]
        function transactionStepArrayToJson(string memory _transactionID, uint256[] memory _seqs, uint256 _len) internal view
        returns(string memory result)
        {

        }

        // {"seq": 0, "contract": "0x12","path": "a.b.c","timestamp": "123","func": "test2(string)","args": "bbb"}
        function transactionStepToJson(TransactionStep memory _step, uint256 _seq) internal pure
        returns(string memory)
        {

        }


        function stringToUint256(string memory _str) public pure
        returns (uint256)
        {

        }
    }
    */

    /*
    "e1207bee": "addPath(string[])",
            "063ff7ef": "commitTransaction(string[])",
            "b54138b0": "constantCall(string,string,string,bytes)",
            "f4fa9d03": "deletePathList(string[])",
            "6ccc29dc": "getLatestTransaction()",
            "9edd3441": "getLatestTransactionInfo()",
            "cb797d2f": "getMaxStep(string[])",
            "4efcaed0": "getPaths(string[])",
            "d55c01f7": "getTransactionInfo(string[])",
            "8bc4827c": "getVersion(string[])",
            "8c31f9ad": "rollbackAndDeleteTransaction(string[])",
            "51cd3824": "rollbackTransaction(string[])",
            "772d0b53": "sendTransaction(string,uint256,string,string,bytes)",
            "18a56b67": "setMaxStep(string[])",
            "e25a0866": "startTransaction(string[])",
            "ac5d3723": "stringToUint256(string)"
    */

        String abi =
                "[{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"string[]\",\"name\":\"_args\",\"type\":\"string[]\"}],\"name\":\"addPath\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"string[]\",\"name\":\"_args\",\"type\":\"string[]\"}],\"name\":\"commitTransaction\",\"outputs\":[{\"internalType\":\"string[]\",\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"string\",\"name\":\"_transactionID\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_path\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_func\",\"type\":\"string\"},{\"internalType\":\"bytes\",\"name\":\"_args\",\"type\":\"bytes\"}],\"name\":\"constantCall\",\"outputs\":[{\"internalType\":\"bytes\",\"name\":\"\",\"type\":\"bytes\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"string[]\",\"name\":\"_args\",\"type\":\"string[]\"}],\"name\":\"deletePathList\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"getLatestTransaction\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"getLatestTransactionInfo\",\"outputs\":[{\"internalType\":\"string[]\",\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"internalType\":\"string[]\",\"name\":\"_args\",\"type\":\"string[]\"}],\"name\":\"getMaxStep\",\"outputs\":[{\"internalType\":\"string[]\",\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"internalType\":\"string[]\",\"name\":\"_args\",\"type\":\"string[]\"}],\"name\":\"getPaths\",\"outputs\":[{\"internalType\":\"string[]\",\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"internalType\":\"string[]\",\"name\":\"_args\",\"type\":\"string[]\"}],\"name\":\"getTransactionInfo\",\"outputs\":[{\"internalType\":\"string[]\",\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"internalType\":\"string[]\",\"name\":\"_args\",\"type\":\"string[]\"}],\"name\":\"getVersion\",\"outputs\":[{\"internalType\":\"string[]\",\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"pure\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"string[]\",\"name\":\"_args\",\"type\":\"string[]\"}],\"name\":\"rollbackAndDeleteTransaction\",\"outputs\":[{\"internalType\":\"string[]\",\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"string[]\",\"name\":\"_args\",\"type\":\"string[]\"}],\"name\":\"rollbackTransaction\",\"outputs\":[{\"internalType\":\"string[]\",\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"string\",\"name\":\"_transactionID\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"_seq\",\"type\":\"uint256\"},{\"internalType\":\"string\",\"name\":\"_path\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_func\",\"type\":\"string\"},{\"internalType\":\"bytes\",\"name\":\"_args\",\"type\":\"bytes\"}],\"name\":\"sendTransaction\",\"outputs\":[{\"internalType\":\"bytes\",\"name\":\"\",\"type\":\"bytes\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"string[]\",\"name\":\"_args\",\"type\":\"string[]\"}],\"name\":\"setMaxStep\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"string[]\",\"name\":\"_args\",\"type\":\"string[]\"}],\"name\":\"startTransaction\",\"outputs\":[{\"internalType\":\"string[]\",\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"internalType\":\"string\",\"name\":\"_str\",\"type\":\"string\"}],\"name\":\"stringToUint256\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"pure\",\"type\":\"function\"}]";

        ContractABIDefinition contractABIDefinition = TestUtils.getContractABIDefinition(abi);
        ABIObject inputObject =
                ABIObjectFactory.createInputObject(
                        contractABIDefinition.getFunctions().get("commitTransaction").get(0));
        assertTrue(inputObject.getStructFields().get(0).isDynamic());

        ABIObject inputObject0 =
                ABIObjectFactory.createInputObject(
                        contractABIDefinition.getFunctions().get("addPath").get(0));
        assertTrue(inputObject0.getStructFields().get(0).isDynamic());

        ABIObject inputObject1 =
                ABIObjectFactory.createInputObject(
                        contractABIDefinition.getFunctions().get("constantCall").get(0));
        assertTrue(inputObject1.getStructFields().get(0).isDynamic());
        assertTrue(inputObject1.getStructFields().get(1).isDynamic());
        assertTrue(inputObject1.getStructFields().get(2).isDynamic());
        assertTrue(inputObject1.getStructFields().get(3).isDynamic());

        ABIObject inputObject2 =
                ABIObjectFactory.createInputObject(
                        contractABIDefinition.getFunctions().get("deletePathList").get(0));
        assertTrue(inputObject2.getStructFields().get(0).isDynamic());

        ABIObject inputObject3 =
                ABIObjectFactory.createInputObject(
                        contractABIDefinition.getFunctions().get("sendTransaction").get(0));
        assertTrue(inputObject3.getStructFields().get(0).isDynamic());
        assertTrue(!inputObject3.getStructFields().get(1).isDynamic());
        assertTrue(inputObject3.getStructFields().get(2).isDynamic());
        assertTrue(inputObject3.getStructFields().get(3).isDynamic());
        assertTrue(inputObject3.getStructFields().get(4).isDynamic());
    }

    @Test
    public void testContractABIDefinitionBuildMethodSignature1() throws Exception {
    /*

        pragma solidity ^0.4.24;
        pragma experimental ABIEncoderV2;
        contract TestContract
        {
            event TestEventSimpleParams(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs);
            event TestEventDArrayParams(uint256[] _u,int256[] _i,bool[] _b,address[] _addr,bytes32[] _bs32, string[] _s,bytes[] _bs);
            event TestEventSArrayParams(uint256[4] _u,int256[4] _i,bool[4] _b,address[4] _addr,bytes32[4] _bs32, string[4] _s,bytes[4] _bs);

            function test0(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs) public constant returns (uint256,int256,bool,address,bytes32,string,bytes) {

            }

            function test1(uint256[] _u,int256[] _i,bool[] _b,address[] _addr,bytes32[] _bs32,string[] _s,bytes[] _bs) public constant returns (uint256[],int256[],bool[],address[],bytes32[],string[],bytes[]) {

            }

            function test2(uint256[4] _u,int256[4] _i,bool[4] _b,address[4] _addr,bytes32[4] _bs32,string[4] _s,bytes[4] _bs) public constant returns (uint256[2],int256[2],bool[2],address[2],bytes32[2],string[2],bytes[2]) {

            }
        }
    */

    /*
       "f92a5e47": "test0(uint256,int256,bool,address,bytes32,string,bytes)",
       "70be28d9": "test1(uint256[4],int256[4],bool[4],address[4],bytes32[4],string[4],bytes[4])",
       "10c7e4ab": "test2(uint256[],int256[],bool[],address[],bytes32[],string[],bytes[])"
    */

        String abi =
                "[{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"uint256[]\",\"name\":\"_u\",\"type\":\"uint256[]\"},{\"indexed\":false,\"internalType\":\"int256[]\",\"name\":\"_i\",\"type\":\"int256[]\"},{\"indexed\":false,\"internalType\":\"bool[]\",\"name\":\"_b\",\"type\":\"bool[]\"},{\"indexed\":false,\"internalType\":\"address[]\",\"name\":\"_addr\",\"type\":\"address[]\"},{\"indexed\":false,\"internalType\":\"bytes32[]\",\"name\":\"_bs32\",\"type\":\"bytes32[]\"},{\"indexed\":false,\"internalType\":\"string[]\",\"name\":\"_s\",\"type\":\"string[]\"},{\"indexed\":false,\"internalType\":\"bytes[]\",\"name\":\"_bs\",\"type\":\"bytes[]\"}],\"name\":\"TestEventDArrayParams\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"uint256[4]\",\"name\":\"_u\",\"type\":\"uint256[4]\"},{\"indexed\":false,\"internalType\":\"int256[4]\",\"name\":\"_i\",\"type\":\"int256[4]\"},{\"indexed\":false,\"internalType\":\"bool[4]\",\"name\":\"_b\",\"type\":\"bool[4]\"},{\"indexed\":false,\"internalType\":\"address[4]\",\"name\":\"_addr\",\"type\":\"address[4]\"},{\"indexed\":false,\"internalType\":\"bytes32[4]\",\"name\":\"_bs32\",\"type\":\"bytes32[4]\"},{\"indexed\":false,\"internalType\":\"string[4]\",\"name\":\"_s\",\"type\":\"string[4]\"},{\"indexed\":false,\"internalType\":\"bytes[4]\",\"name\":\"_bs\",\"type\":\"bytes[4]\"}],\"name\":\"TestEventSArrayParams\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"_u\",\"type\":\"uint256\"},{\"indexed\":false,\"internalType\":\"int256\",\"name\":\"_i\",\"type\":\"int256\"},{\"indexed\":false,\"internalType\":\"bool\",\"name\":\"_b\",\"type\":\"bool\"},{\"indexed\":false,\"internalType\":\"address\",\"name\":\"_addr\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"bytes32\",\"name\":\"_bs32\",\"type\":\"bytes32\"},{\"indexed\":false,\"internalType\":\"string\",\"name\":\"_s\",\"type\":\"string\"},{\"indexed\":false,\"internalType\":\"bytes\",\"name\":\"_bs\",\"type\":\"bytes\"}],\"name\":\"TestEventSimpleParams\",\"type\":\"event\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"_u\",\"type\":\"uint256\"},{\"internalType\":\"int256\",\"name\":\"_i\",\"type\":\"int256\"},{\"internalType\":\"bool\",\"name\":\"_b\",\"type\":\"bool\"},{\"internalType\":\"address\",\"name\":\"_addr\",\"type\":\"address\"},{\"internalType\":\"bytes32\",\"name\":\"_bs32\",\"type\":\"bytes32\"},{\"internalType\":\"string\",\"name\":\"_s\",\"type\":\"string\"},{\"internalType\":\"bytes\",\"name\":\"_bs\",\"type\":\"bytes\"}],\"name\":\"test0\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"},{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"},{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"},{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"},{\"internalType\":\"bytes32\",\"name\":\"\",\"type\":\"bytes32\"},{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"},{\"internalType\":\"bytes\",\"name\":\"\",\"type\":\"bytes\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"uint256[]\",\"name\":\"_u\",\"type\":\"uint256[]\"},{\"internalType\":\"int256[]\",\"name\":\"_i\",\"type\":\"int256[]\"},{\"internalType\":\"bool[]\",\"name\":\"_b\",\"type\":\"bool[]\"},{\"internalType\":\"address[]\",\"name\":\"_addr\",\"type\":\"address[]\"},{\"internalType\":\"bytes32[]\",\"name\":\"_bs32\",\"type\":\"bytes32[]\"},{\"internalType\":\"string[]\",\"name\":\"_s\",\"type\":\"string[]\"},{\"internalType\":\"bytes[]\",\"name\":\"_bs\",\"type\":\"bytes[]\"}],\"name\":\"test1\",\"outputs\":[{\"internalType\":\"uint256[]\",\"name\":\"\",\"type\":\"uint256[]\"},{\"internalType\":\"int256[]\",\"name\":\"\",\"type\":\"int256[]\"},{\"internalType\":\"bool[]\",\"name\":\"\",\"type\":\"bool[]\"},{\"internalType\":\"address[]\",\"name\":\"\",\"type\":\"address[]\"},{\"internalType\":\"bytes32[]\",\"name\":\"\",\"type\":\"bytes32[]\"},{\"internalType\":\"string[]\",\"name\":\"\",\"type\":\"string[]\"},{\"internalType\":\"bytes[]\",\"name\":\"\",\"type\":\"bytes[]\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"uint256[4]\",\"name\":\"_u\",\"type\":\"uint256[4]\"},{\"internalType\":\"int256[4]\",\"name\":\"_i\",\"type\":\"int256[4]\"},{\"internalType\":\"bool[4]\",\"name\":\"_b\",\"type\":\"bool[4]\"},{\"internalType\":\"address[4]\",\"name\":\"_addr\",\"type\":\"address[4]\"},{\"internalType\":\"bytes32[4]\",\"name\":\"_bs32\",\"type\":\"bytes32[4]\"},{\"internalType\":\"string[4]\",\"name\":\"_s\",\"type\":\"string[4]\"},{\"internalType\":\"bytes[4]\",\"name\":\"_bs\",\"type\":\"bytes[4]\"}],\"name\":\"test2\",\"outputs\":[{\"internalType\":\"uint256[2]\",\"name\":\"\",\"type\":\"uint256[2]\"},{\"internalType\":\"int256[2]\",\"name\":\"\",\"type\":\"int256[2]\"},{\"internalType\":\"bool[2]\",\"name\":\"\",\"type\":\"bool[2]\"},{\"internalType\":\"address[2]\",\"name\":\"\",\"type\":\"address[2]\"},{\"internalType\":\"bytes32[2]\",\"name\":\"\",\"type\":\"bytes32[2]\"},{\"internalType\":\"string[2]\",\"name\":\"\",\"type\":\"string[2]\"},{\"internalType\":\"bytes[2]\",\"name\":\"\",\"type\":\"bytes[2]\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";

        ContractABIDefinition contractABIDefinition = TestUtils.getContractABIDefinition(abi);

        assertEquals(
                contractABIDefinition.getFunctions().get("test0").get(0).getMethodSignatureAsString(),
                "test0(uint256,int256,bool,address,bytes32,string,bytes)");

        assertEquals(
                Hex.toHexString(
                        contractABIDefinition
                                .getFunctions()
                                .get("test0")
                                .get(0)
                                .getMethodId(TestUtils.getCryptoSuite())),
                "f92a5e47");
        assertTrue(
                Objects.nonNull(
                        contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("0xf92a5e47"))));

        assertEquals(
                contractABIDefinition.getFunctions().get("test2").get(0).getMethodSignatureAsString(),
                "test2(uint256[4],int256[4],bool[4],address[4],bytes32[4],string[4],bytes[4])");

        assertEquals(
                Hex.toHexString(
                        contractABIDefinition
                                .getFunctions()
                                .get("test2")
                                .get(0)
                                .getMethodId(TestUtils.getCryptoSuite())),
                "10c7e4ab");
        assertTrue(
                Objects.nonNull(
                        contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("0x10c7e4ab"))));

        assertEquals(
                contractABIDefinition.getFunctions().get("test1").get(0).getMethodSignatureAsString(),
                "test1(uint256[],int256[],bool[],address[],bytes32[],string[],bytes[])");

        assertEquals(
                Hex.toHexString(
                        contractABIDefinition
                                .getFunctions()
                                .get("test1")
                                .get(0)
                                .getMethodId(TestUtils.getCryptoSuite())),
                "70be28d9");
        assertTrue(
                Objects.nonNull(
                        contractABIDefinition.getABIDefinitionByMethodId(Hex.decode("0x70be28d9"))));
    }

    @Test
    public void testContractABIDefinitionDynamic2() throws Exception {
    /*

        pragma solidity ^0.4.24;
        pragma experimental ABIEncoderV2;
        contract TestContract
        {
            event TestEventSimpleParams(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs);
            event TestEventDArrayParams(uint256[] _u,int256[] _i,bool[] _b,address[] _addr,bytes32[] _bs32, string[] _s,bytes[] _bs);
            event TestEventSArrayParams(uint256[4] _u,int256[4] _i,bool[4] _b,address[4] _addr,bytes32[4] _bs32, string[4] _s,bytes[4] _bs);

            function test0(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs) public constant returns (uint256,int256,bool,address,bytes32,string,bytes) {

            }

            function test1(uint256[] _u,int256[] _i,bool[] _b,address[] _addr,bytes32[] _bs32,string[] _s,bytes[] _bs) public constant returns (uint256[],int256[],bool[],address[],bytes32[],string[],bytes[]) {

            }

            function test2(uint256[4] _u,int256[4] _i,bool[4] _b,address[4] _addr,bytes32[4] _bs32,string[4] _s,bytes[4] _bs) public constant returns (uint256[2],int256[2],bool[2],address[2],bytes32[2],string[2],bytes[2]) {

            }
        }
    */

    /*
       "f92a5e47": "test0(uint256,int256,bool,address,bytes32,string,bytes)",
       "70be28d9": "test2(uint256[4],int256[4],bool[4],address[4],bytes32[4],string[4],bytes[4])",
       "10c7e4ab": "test1(uint256[],int256[],bool[],address[],bytes32[],string[],bytes[])"
    */

        String abi =
                "[{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"uint256[]\",\"name\":\"_u\",\"type\":\"uint256[]\"},{\"indexed\":false,\"internalType\":\"int256[]\",\"name\":\"_i\",\"type\":\"int256[]\"},{\"indexed\":false,\"internalType\":\"bool[]\",\"name\":\"_b\",\"type\":\"bool[]\"},{\"indexed\":false,\"internalType\":\"address[]\",\"name\":\"_addr\",\"type\":\"address[]\"},{\"indexed\":false,\"internalType\":\"bytes32[]\",\"name\":\"_bs32\",\"type\":\"bytes32[]\"},{\"indexed\":false,\"internalType\":\"string[]\",\"name\":\"_s\",\"type\":\"string[]\"},{\"indexed\":false,\"internalType\":\"bytes[]\",\"name\":\"_bs\",\"type\":\"bytes[]\"}],\"name\":\"TestEventDArrayParams\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"uint256[4]\",\"name\":\"_u\",\"type\":\"uint256[4]\"},{\"indexed\":false,\"internalType\":\"int256[4]\",\"name\":\"_i\",\"type\":\"int256[4]\"},{\"indexed\":false,\"internalType\":\"bool[4]\",\"name\":\"_b\",\"type\":\"bool[4]\"},{\"indexed\":false,\"internalType\":\"address[4]\",\"name\":\"_addr\",\"type\":\"address[4]\"},{\"indexed\":false,\"internalType\":\"bytes32[4]\",\"name\":\"_bs32\",\"type\":\"bytes32[4]\"},{\"indexed\":false,\"internalType\":\"string[4]\",\"name\":\"_s\",\"type\":\"string[4]\"},{\"indexed\":false,\"internalType\":\"bytes[4]\",\"name\":\"_bs\",\"type\":\"bytes[4]\"}],\"name\":\"TestEventSArrayParams\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"_u\",\"type\":\"uint256\"},{\"indexed\":false,\"internalType\":\"int256\",\"name\":\"_i\",\"type\":\"int256\"},{\"indexed\":false,\"internalType\":\"bool\",\"name\":\"_b\",\"type\":\"bool\"},{\"indexed\":false,\"internalType\":\"address\",\"name\":\"_addr\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"bytes32\",\"name\":\"_bs32\",\"type\":\"bytes32\"},{\"indexed\":false,\"internalType\":\"string\",\"name\":\"_s\",\"type\":\"string\"},{\"indexed\":false,\"internalType\":\"bytes\",\"name\":\"_bs\",\"type\":\"bytes\"}],\"name\":\"TestEventSimpleParams\",\"type\":\"event\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"_u\",\"type\":\"uint256\"},{\"internalType\":\"int256\",\"name\":\"_i\",\"type\":\"int256\"},{\"internalType\":\"bool\",\"name\":\"_b\",\"type\":\"bool\"},{\"internalType\":\"address\",\"name\":\"_addr\",\"type\":\"address\"},{\"internalType\":\"bytes32\",\"name\":\"_bs32\",\"type\":\"bytes32\"},{\"internalType\":\"string\",\"name\":\"_s\",\"type\":\"string\"},{\"internalType\":\"bytes\",\"name\":\"_bs\",\"type\":\"bytes\"}],\"name\":\"test0\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"},{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"},{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"},{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"},{\"internalType\":\"bytes32\",\"name\":\"\",\"type\":\"bytes32\"},{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"},{\"internalType\":\"bytes\",\"name\":\"\",\"type\":\"bytes\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"uint256[]\",\"name\":\"_u\",\"type\":\"uint256[]\"},{\"internalType\":\"int256[]\",\"name\":\"_i\",\"type\":\"int256[]\"},{\"internalType\":\"bool[]\",\"name\":\"_b\",\"type\":\"bool[]\"},{\"internalType\":\"address[]\",\"name\":\"_addr\",\"type\":\"address[]\"},{\"internalType\":\"bytes32[]\",\"name\":\"_bs32\",\"type\":\"bytes32[]\"},{\"internalType\":\"string[]\",\"name\":\"_s\",\"type\":\"string[]\"},{\"internalType\":\"bytes[]\",\"name\":\"_bs\",\"type\":\"bytes[]\"}],\"name\":\"test1\",\"outputs\":[{\"internalType\":\"uint256[]\",\"name\":\"\",\"type\":\"uint256[]\"},{\"internalType\":\"int256[]\",\"name\":\"\",\"type\":\"int256[]\"},{\"internalType\":\"bool[]\",\"name\":\"\",\"type\":\"bool[]\"},{\"internalType\":\"address[]\",\"name\":\"\",\"type\":\"address[]\"},{\"internalType\":\"bytes32[]\",\"name\":\"\",\"type\":\"bytes32[]\"},{\"internalType\":\"string[]\",\"name\":\"\",\"type\":\"string[]\"},{\"internalType\":\"bytes[]\",\"name\":\"\",\"type\":\"bytes[]\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"uint256[4]\",\"name\":\"_u\",\"type\":\"uint256[4]\"},{\"internalType\":\"int256[4]\",\"name\":\"_i\",\"type\":\"int256[4]\"},{\"internalType\":\"bool[4]\",\"name\":\"_b\",\"type\":\"bool[4]\"},{\"internalType\":\"address[4]\",\"name\":\"_addr\",\"type\":\"address[4]\"},{\"internalType\":\"bytes32[4]\",\"name\":\"_bs32\",\"type\":\"bytes32[4]\"},{\"internalType\":\"string[4]\",\"name\":\"_s\",\"type\":\"string[4]\"},{\"internalType\":\"bytes[4]\",\"name\":\"_bs\",\"type\":\"bytes[4]\"}],\"name\":\"test2\",\"outputs\":[{\"internalType\":\"uint256[2]\",\"name\":\"\",\"type\":\"uint256[2]\"},{\"internalType\":\"int256[2]\",\"name\":\"\",\"type\":\"int256[2]\"},{\"internalType\":\"bool[2]\",\"name\":\"\",\"type\":\"bool[2]\"},{\"internalType\":\"address[2]\",\"name\":\"\",\"type\":\"address[2]\"},{\"internalType\":\"bytes32[2]\",\"name\":\"\",\"type\":\"bytes32[2]\"},{\"internalType\":\"string[2]\",\"name\":\"\",\"type\":\"string[2]\"},{\"internalType\":\"bytes[2]\",\"name\":\"\",\"type\":\"bytes[2]\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";

        ContractABIDefinition contractABIDefinition = TestUtils.getContractABIDefinition(abi);
        ABIObject inputObject1 =
                ABIObjectFactory.createInputObject(
                        contractABIDefinition.getFunctions().get("test0").get(0));
        assertFalse(inputObject1.getStructFields().get(0).isDynamic());
        assertFalse(inputObject1.getStructFields().get(1).isDynamic());
        assertFalse(inputObject1.getStructFields().get(2).isDynamic());
        assertFalse(inputObject1.getStructFields().get(3).isDynamic());
        assertFalse(inputObject1.getStructFields().get(4).isDynamic());
        assertTrue(inputObject1.getStructFields().get(5).isDynamic());
        assertTrue(inputObject1.getStructFields().get(6).isDynamic());

        ABIObject inputObject2 =
                ABIObjectFactory.createInputObject(
                        contractABIDefinition.getFunctions().get("test2").get(0));
        assertFalse(inputObject2.getStructFields().get(0).isDynamic());
        assertFalse(inputObject2.getStructFields().get(1).isDynamic());
        assertFalse(inputObject2.getStructFields().get(2).isDynamic());
        assertFalse(inputObject2.getStructFields().get(3).isDynamic());
        assertFalse(inputObject2.getStructFields().get(4).isDynamic());
        assertTrue(inputObject2.getStructFields().get(5).isDynamic());
        assertTrue(inputObject2.getStructFields().get(6).isDynamic());

        ABIObject inputObject3 =
                ABIObjectFactory.createInputObject(
                        contractABIDefinition.getFunctions().get("test1").get(0));
        assertTrue(inputObject3.getStructFields().get(0).isDynamic());
        assertTrue(inputObject3.getStructFields().get(1).isDynamic());
        assertTrue(inputObject3.getStructFields().get(2).isDynamic());
        assertTrue(inputObject3.getStructFields().get(3).isDynamic());
        assertTrue(inputObject3.getStructFields().get(4).isDynamic());
        assertTrue(inputObject3.getStructFields().get(5).isDynamic());
        assertTrue(inputObject3.getStructFields().get(6).isDynamic());
    }
}
