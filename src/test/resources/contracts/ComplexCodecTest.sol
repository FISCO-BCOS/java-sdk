// SPDX-License-Identifier: Apache-2.0
pragma solidity >=0.6.10 <0.8.20;
pragma experimental ABIEncoderV2;

    struct StructA {
        string[] value_str;
        bytes32[] bytes32_in_struct;
    }

    struct StructB {
        string[] d_str;
        StructA[] a_struct;
    }

    struct StaticStruct{
        int128 i1;
        uint128 u1;
        int32[1] b1;
    }

contract ComplexCodecTest {

    bytes[][] public b_array_array;
    bytes32[][] public b32_array_array;
    bytes[2][] public b_s_array_array;
    bytes32[2][] public b32_s_array_array;

    StructA a_struct;
    StaticStruct static_struct;

    // constructor() public{
    //     bytes32[] memory b = new bytes32[](1);
    //     string[] memory s = new string[](1);
    //     s[0] = "test";
    //     a_struct = StructA(s,b);
    // }

    constructor(StructA memory a) public{
        a_struct = a;
    }

    function getStructA(StructA memory a) public view returns (StructA[] memory){
        StructA[] memory s = new StructA[](2);
        s[0] = a_struct;
        s[1] = a;
        return s;
    }

    function getStructA() public view returns (StructA[] memory){
        StructA[] memory s = new StructA[](2);
        s[0] = a_struct;
        s[1] = a_struct;
        return s;
    }

    function buildStructA(string memory value_str, bytes32[] memory _b) public returns (StructA memory)
    {
        string[] memory s = new string[](1);
        s[0] = value_str;
        a_struct = StructA(s, _b);
        return a_struct;
    }

    function buildStructA(string memory value_str, bytes32[] memory _b, uint8 size) public returns (StructA[] memory)
    {
        string[] memory s = new string[](2);
        s[0] = value_str;
        a_struct = StructA(s, _b);
        StructA[] memory ss = new StructA[](size);
        ss[0] = a_struct;
        return ss;
    }

    function buildStructB(StructA memory a) public returns (StructB memory, StructA[] memory){
        StructB memory b_struct;
        a_struct = a;
        b_struct.a_struct = new StructA[](2);
        b_struct.a_struct[0] = a;
        b_struct.a_struct[1] = a;
        return (b_struct,b_struct.a_struct);
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

    function setBytesStaticArrayArray(bytes[2][] memory b) public returns (bytes[2][] memory){
        b_s_array_array = b;
        return b_s_array_array;
    }

    function setBytes32StaticArrayArray(bytes32[2][] memory b) public returns (bytes32[2][] memory){
        b32_s_array_array = b;
        return b32_s_array_array;
    }

    function buildStaticStruct(int128 i1, uint128 u1) public returns(StaticStruct[] memory){
        int32[1] memory b = [int32(1)];
        b[0] = int32(1);
        static_struct = StaticStruct(i1,u1,b);
        StaticStruct memory s2 = StaticStruct(i1,u1,b);
        StaticStruct[] memory ss = new StaticStruct[](2);
        ss[0] = static_struct;
        ss[1] = s2;
        return ss;
    }

    function buildStaticStruct(StaticStruct memory b) public returns(StaticStruct memory){
        static_struct = StaticStruct(b.i1+1,b.u1+1,b.b1);
        return static_struct;
    }
}