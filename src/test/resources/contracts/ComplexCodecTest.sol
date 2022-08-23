// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.6.0;
pragma experimental ABIEncoderV2;

    struct StructA {
        string[] value_str;
        bytes32[] bytes32_in_struct;
    }

    struct StructB {
        string[][] d_str;
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

    function buildStructA(string memory value_str, bytes32[] memory _b) public returns (StructA memory)
    {
        string[] memory s = new string[](1);
        s[0] = value_str;
        a_struct = StructA(s, _b);
        return a_struct;
    }

    function buildStructB(StructA memory a) public returns (StructB memory){
        StructB memory b_struct;
        a_struct = a;
        b_struct.a_struct = new StructA[](2);
        b_struct.a_struct[0] = a;
        b_struct.a_struct[1] = a;
        return b_struct;
    }

    function getStructAInStructB(StructB memory b) public returns (StructA memory){
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

    function buildStaticStruct(int128 i1, uint128 u1) public pure returns(StaticStruct memory){
        StaticStruct memory s1 = StaticStruct(i1,u1);
        return s1;
    }

    function buildStaticStruct(StaticStruct memory b) public pure returns(StaticStruct memory){
        StaticStruct memory s1 = StaticStruct(b.i1+1,b.u1+1);
        return s1;
    }
}