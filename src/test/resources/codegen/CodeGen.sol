pragma solidity >0.4.24 <0.6.11;
pragma experimental ABIEncoderV2;

contract CodeGen{
    
    struct Person {
        string name;
        uint age;
        Account[] accounts;
        string[] personTag;
        int[] phoneNum;
        
    }
    
    struct Account {
        address accountAddr;
        int points;
        string accountName;
        string[] accountTag;
        bytes32 label;
    }

    string test;
    //构造
    constructor() public {
        test="Test Contract ";
    }

    //test string
    function setString(string memory str) public view returns ( string memory){
        string memory result="hello new";
        return result;
    }
    
    function getString(string memory str) public view returns ( string memory){
        return str;
    }
    
    //test int、string、bytes、address
    function setMix(address  addr_a,int  int_b,string memory str_c,bytes32  byte_e) public view returns ( Account memory){
        string[] memory str_d=new string[](1);
        str_d[0]=str_c;
        Account  memory account=Account(addr_a,int_b,str_c,str_d,byte_e);
        return account;
    }
    function getMix(address addr_a,int int_b,string memory str_c,bytes32 byte_e) public view returns ( Account memory){
        string[] memory str_d=new string[](1);
        Account  memory account=Account(addr_a,int_b,str_c,str_d,byte_e);
        return account;
    }
    //struct
    function setStructTest(Account memory account) public view returns ( Account memory){
        account.accountName="set_new";
        return account;
    }
    function getStructTest(Account memory account) public view returns ( Account memory){
        return account;
    }
    //test int[]、string[]、bytes[]、address[]
    function setArray(address[] memory addr_arr,int[] memory int_arr,string[] memory  str_arr,bytes32[] memory byte_arr) public view returns ( Account memory){
        Account memory account=Account(addr_arr[0],int_arr[0],"set array",str_arr,byte_arr[0]);
        return account;
    }
    function getArray(address[] memory addr_arr,int[] memory int_arr,string[] memory  str_arr,bytes32[] memory byte_arr) public view returns ( Account memory){
        Account memory account=Account(addr_arr[0],int_arr[0],str_arr[0],str_arr,byte_arr[0]);
        return account;
    }
    //int、string、struct
     function set(int int_b,string memory str_c,Account memory account) public view returns ( Account memory){
       account.accountName=str_c;
       account.points=int_b;
        return account;
    }
    function get(int int_b,string memory str_c,Account memory account) public view returns ( Account memory){
        return account;
    }
    //struct array
    function setPerson(Person[] memory person) public view returns ( Person[] memory){
        return person;
    }
    function getPerson(Person[] memory person) public view returns ( Person[] memory){
        return person;
    }
    
    
    

}