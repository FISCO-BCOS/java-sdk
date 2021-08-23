pragma solidity ^0.4.24;

contract FileSystemPrecompiled {
    function mkdir(string path) public returns (int256);
    function list(string path) public view returns (string);
}
