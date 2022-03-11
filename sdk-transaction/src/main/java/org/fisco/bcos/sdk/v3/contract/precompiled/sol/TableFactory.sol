pragma solidity ^0.4.2;
import "./Table.sol";

contract TableFactory {
        function openTable(string) public view returns (Table); //open table
        function createTable(string, string, string) public returns (int256); //create table
}