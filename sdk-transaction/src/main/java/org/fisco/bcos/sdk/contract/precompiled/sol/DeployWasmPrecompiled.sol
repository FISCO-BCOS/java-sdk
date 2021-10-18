pragma solidity ^0.4.24;

contract DeployWasmPrecompiled {
    function deployWasm(bytes code, bytes params, string path, string jsonAbi) returns (int256);
}
