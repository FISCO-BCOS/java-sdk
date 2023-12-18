// SPDX-License-Identifier: Apache-2.0
pragma solidity >=0.6.10 <0.8.20;

contract BalancePrecompiled {
    function getBalance(address account) public returns (uint256) {}

    function addBalance(address account, uint256 amount) public {}

    function subBalance(address account, uint256 amount) public {}

    function transfer(address from, address to, uint256 amount) public {}

    function registerCaller(address account) public {}

    function unregisterCaller(address account) public {}
}
