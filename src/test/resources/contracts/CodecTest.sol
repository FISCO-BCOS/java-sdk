// SPDX-License-Identifier: Apache-2.0
pragma solidity >=0.6.10 <0.8.20;
pragma experimental ABIEncoderV2;

contract CodecTest {
    string s;
    uint8 u8;
    uint128 u128;
    uint256 u256;
    int8 i8;
    int128 i128;
    int256 i256;
    bool b;
    address addr;
    bytes1  b1;
    bytes16 b16;
    bytes32 b32;
    bytes bs;
    uint8[10] u8_array10;
    uint128[10] u128_array10;
    uint256[10] u256_array10;
    uint8[] u8_a;
    uint128[] u128_a;
    uint256[] u256_a;
    int8[10] i8_array10;
    int128[10] i128_array10;
    int256[10] i256_array10;
    int8[] i8_a;
    int128[] i128_a;
    int256[] i256_a;
    bool[10] b_array10;
    bool[] b_array;
    address[10] addr_array10;
    address[] addr_arr;
    bytes1[10] b1_array10;
    bytes16[10] b16_array10;
    bytes32[10] b32_array10;
    bytes1[] b1_array;
    bytes16[] b16_array;
    bytes32[] b32_array;

    constructor() public {
        s = "Hello, World!";
    }

    /////////////////////////string//////////////////////
    function get() public view returns (string memory) {
        return s;
    }

    function set(string memory n) public {
        s = n;
    }

    /////////////////////////uintN//////////////////////
    function setU8(uint8 u8_para) public {
        u8 = u8_para;
    }

    function getU8() view public returns(uint8){
        return u8;
    }

    function setU128(uint128 u128_para) public {
        u128 = u128_para;
    }

    function getU128() view public returns(uint128){
        return u128;
    }

    function setU256(uint256 u256_para) public {
        u256 = u256_para;
    }

    function getU256() view public returns(uint256){
        return u256;
    }

    /////////////////////////intN//////////////////////
    function setI8(int8 i8_para) public {
        i8 = i8_para;
    }

    function getI8()view public returns(int8){
        return i8;
    }

    function setI128(int128 i128_para) public {
        i128 = i128_para;
    }

    function getI128() view public returns(int128){
        return i128;
    }

    function setI256(int256 i256_para) public{
        i256 = i256_para;
    }

    function getI256() view public returns(int256){
        return i256;
    }

    /////////////////////////bool//////////////////////
    function setBool(bool b_para) public {
        b = b_para;
    }

    function getBool() view public returns(bool){
        return b;
    }

    /////////////////////////address//////////////////////
    function setAddress(address addr_para) public {
        addr = addr_para;
    }

    function getAddress() public view returns(address){
        return addr;
    }

    /////////////////////////bytesN 不支持//////////////////////
    function setBytes1(bytes1 b1_param) public {
        b1 = b1_param;
    }

    function getBytes1() public view returns(bytes1){
        return b1;
    }

    function setBytes16(bytes16 b16_param) public {
        b16 = b16_param;
    }

    function getBytes16() public view returns(bytes16){
        return b16;
    }

    function setBytes32(bytes32 b32_param) public {
        b32 = b32_param;
    }

    function getBytes32() public view returns(bytes32){
        return b32;
    }

    /////////////////////////bytes//////////////////////
    function setBytes(bytes memory bs_param) public {
        bs = bs_param;
    }

    function getBytes() public view returns(bytes memory){
        return bs;
    }

    /////////////////////////uintN[10]//////////////////////
    function setU8Arr(uint8[10] memory u8a_para) public {
        u8_array10 = u8a_para;
    }

    function getU8Arr() view public returns(uint8[10] memory){
        return u8_array10;
    }

    function setU128Arr(uint128[10] memory u128a_para) public {
        u128_array10 = u128a_para;
    }

    function getU128Arr()view public returns(uint128[10] memory){
        return u128_array10;
    }

    function setU256Arr(uint256[10] memory u256a_para) public {
        u256_array10 = u256a_para;
    }

    function getU256Arr() view public returns(uint256[10] memory){
        return u256_array10;
    }

    /////////////////////////uintN[]//////////////////////
    function setU8ArrDyn(uint8[] memory u8a_para) public {
        u8_a = u8a_para;
    }

    function getU8ArrDyn() view public returns(uint8[] memory){
        return u8_a;
    }

    function setU128ArrDyn(uint128[] memory u128a_para) public {
        u128_a = u128a_para;
    }

    function getU128ArrDyn()view public returns(uint128[] memory){
        return u128_a;
    }

    function setU256ArrDyn(uint256[] memory u256a_para) public {
        u256_a = u256a_para;
    }

    function getU256ArrDyn() view public returns(uint256[] memory){
        return u256_a;
    }

    /////////////////////////intN[10]//////////////////////
    function setI8Arr(int8[10] memory i8a_para) public {
        i8_array10 = i8a_para;
    }

    function getI8Arr() view public returns(int8[10] memory){
        return i8_array10;
    }

    function setI128Arr(int128[10] memory i128a_para) public {
        i128_array10 = i128a_para;
    }

    function getI128Arr()view public returns(int128[10] memory){
        return i128_array10;
    }

    function setI256Arr(int256[10] memory i256a_para) public {
        i256_array10 = i256a_para;
    }

    function getI256Arr() view public returns(int256[10] memory){
        return i256_array10;
    }

    /////////////////////////intN[]//////////////////////
    function setI8ArrDyn(int8[] memory i8a_para) public {
        i8_a = i8a_para;
    }

    function getI8ArrDyn() view public returns(int8[] memory){
        return i8_a;
    }

    function setI128ArrDyn(int128[] memory i128a_para) public {
        i128_a = i128a_para;
    }

    function getI128ArrDyn()view public returns(int128[] memory){
        return i128_a;
    }

    function setI256ArrDyn(int256[] memory i256a_para) public {
        i256_a = i256a_para;
    }

    function getI256ArrDyn() view public returns(int256[] memory){
        return i256_a;
    }

    /////////////////////////bool[10]//////////////////////
    function setBoolArr(bool[10] memory b_array10_param) public {
        b_array10 = b_array10_param;
    }

    function getBoolArr() view public returns(bool[10] memory){
        return b_array10;
    }

    /////////////////////////bool[]//////////////////////
    function setBoolArrDyn(bool[] memory b_array10_param) public {
        b_array = b_array10_param;
    }

    function getBoolArrDyn() view public returns(bool[] memory){
        return b_array;
    }

    /////////////////////////address[10]//////////////////////
    function setAddrArr(address[10] memory addr_array10_param) public {
        addr_array10 = addr_array10_param;
    }

    function getAddrArr() view public returns(address[10] memory){
        return addr_array10;
    }

    /////////////////////////address[]//////////////////////
    function setAddrArrDyn(address[] memory addr_array) public {
        addr_arr = addr_array;
    }

    function getAddrArrDyn() view public returns(address[] memory){
        return addr_arr;
    }

    function setBytes1Arr10(bytes1[10] memory b1_array10_param) public {
        b1_array10 = b1_array10_param;
    }

    function setBytes16Arr10(bytes16[10] memory b16_array10_param) public {
        b16_array10 = b16_array10_param;
    }

    function setBytes32Arr10(bytes32[10] memory b32_array10_param) public {
        b32_array10 = b32_array10_param;
    }

    function getBytes1Arr10()view public returns(bytes1[10] memory){
        return b1_array10;
    }

    function getBytes16Arr10()view public returns(bytes16[10] memory){
        return b16_array10;
    }

    function getBytes32Arr10()view public returns(bytes32[10] memory){
        return b32_array10;
    }

    function setBytes1Arr(bytes1[] memory b1_array_param) public {
        b1_array = b1_array_param;
    }

    function setBytes16Arr(bytes16[] memory b16_array_param) public {
        b16_array = b16_array_param;
    }

    function setBytes32Arr(bytes32[] memory b32_array_param) public {
        b32_array = b32_array_param;
    }

    function getBytes1Arr()view public returns(bytes1[] memory){
        return b1_array;
    }

    function getBytes16Arr()view public returns(bytes16[] memory){
        return b16_array;
    }

    function getBytes32Arr()view public returns(bytes32[] memory){
        return b32_array;
    }

    /////////////////////////复杂组合场景//////////////////////
    function setMix0(int256 i256_para,uint8 u8_para,address addr_para,bool b_para,bytes memory bs_para,string memory s_para,bytes32 b32_para) public {
        i256 = i256_para;
        u8 = u8_para;
        addr = addr_para;
        b = b_para;
        bs = bs_para;
        s = s_para;
        b32 = b32_para;
    }

    function getMix0() view public returns(int256,uint8,address,bool,bytes memory,string memory,bytes32){
        return (i256,u8,addr,b,bs,s,b32);
    }

    function setMix1(uint[] memory ua_para,int[] memory ia_para,address[] memory addr_para,string memory s_para,bytes memory bs_para,bool[] memory ba) public {
        u256_a = ua_para;
        i256_a = ia_para;
        addr_arr = addr_para;
        s  = s_para;
        bs = bs_para;
        b_array = ba;
    }

    function getMix1() view public returns(uint[] memory,int[] memory,address[] memory,string memory,bytes memory,bool[] memory){
        return (u256_a,i256_a,addr_arr,s,bs,b_array);
    }

    function setMix2(uint[10] memory ua_para,int[10] memory ia_para,address[10] memory addr_para,string memory s_para,bytes memory bs_para,bool[10] memory ba) public {
        u256_array10 = ua_para;
        i256_array10 = ia_para;
        addr_array10 = addr_para;
        s  = s_para;
        bs = bs_para;
        b_array10 = ba;
    }

    function getMix2() view public returns(uint[10] memory,int[10] memory,address[10] memory,string memory,bytes memory,bool[10] memory){
        return (u256_array10,i256_array10,addr_array10,s,bs,b_array10);
    }
}