pragma solidity ^0.4.24;

contract ConsensusPrecompiled {
    function addSealer(string, uint256) public returns (int256);
    function addObserver(string) public returns (int256);
    function remove(string) public returns (int256);
    function setWeight(string, uint256) returns (int256);
}
