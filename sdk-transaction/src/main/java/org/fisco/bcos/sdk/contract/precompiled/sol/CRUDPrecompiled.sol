pragma solidity ^0.4.24;

contract CRUDPrecompiled {
    function insert(string tableName, string entry, string) public returns (int256);
    function remove(string tableName, string condition, string) public returns (int256);
    function select(string tableName, string condition, string) public view returns (string);
    function update(string tableName, string entry, string condition, string) public returns (int256);
    function desc(string tableName) public view returns (string, string);
}
