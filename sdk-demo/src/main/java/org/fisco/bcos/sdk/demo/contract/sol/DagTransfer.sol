pragma solidity ^0.4.25;
contract DagTransfer{
    function userAdd(string user, uint256 balance) public returns(uint256);
    function userSave(string user, uint256 balance) public returns(uint256);
    function userDraw(string user, uint256 balance) public returns(uint256);
    function userBalance(string user) public constant returns(uint256,uint256);
    function userTransfer(string user_a, string user_b, uint256 amount) public returns(uint256);
}
