// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.6.0;
pragma experimental ABIEncoderV2;

    struct StructA {
        string value_str;
        bytes32[] bytes32_in_struct;
    }

    struct StructB {
        StructA[] a_struct;
    }

    struct StaticStruct{
        int128 i1;
        uint128 u1;
    }

contract ComplexCodecTest {

    bytes[][] public b_array_array;
    bytes32[][] public b32_array_array;
    bytes[10][] public b_s_array_array;
    bytes32[10][] public b32_s_array_array;

    StructA a_struct;

    function setAStruct(string memory value_str, bytes32[] memory _b) public returns (StructA memory)
    {
        a_struct = StructA(value_str, _b);
        return a_struct;
    }

    function setBStruct(StructA memory a) public returns (StructB memory){
        StructB memory b_struct;
        a_struct = a;
        b_struct.a_struct = new StructA[](2);
        b_struct.a_struct[0] = a;
        b_struct.a_struct[1] = a;
        return b_struct;
    }

    function setBStruct2(StructB memory b) public returns (StructA memory){
        require(b.a_struct.length > 0);
        a_struct = b.a_struct[0];
        return a_struct;
    }

    function setBytesArrayArray(bytes[][] memory b) public returns (bytes[][] memory){
        b_array_array = b;
        return b_array_array;
    }

    function setBytes32ArrayArray(bytes32[][] memory b) public returns (bytes32[][] memory){
        b32_array_array = b;
        return b32_array_array;
    }

    function setBytesStaticArrayArray(bytes[10][] memory b) public returns (bytes[10][] memory){
        b_s_array_array = b;
        return b_s_array_array;
    }

    function setBytes32StaticArrayArray(bytes32[10][] memory b) public returns (bytes32[10][] memory){
        b32_s_array_array = b;
        return b32_s_array_array;
    }

    function staticStruct(int128 i1, uint128 u1) public pure returns(StaticStruct memory){
        StaticStruct memory s1 = staticStruct(i1,u1);
        return s1;
    }
}