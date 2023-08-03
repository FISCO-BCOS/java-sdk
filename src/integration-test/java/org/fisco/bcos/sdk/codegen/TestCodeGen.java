// package org.fisco.bcos.sdk.codegen;
//
// import java.math.BigInteger;
// import java.util.ArrayList;
// import java.util.List;
// import org.fisco.bcos.sdk.BcosSDK;
// import org.fisco.bcos.sdk.BcosSDKTest;
// import org.fisco.bcos.sdk.client.Client;
// import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
// import org.fisco.bcos.sdk.model.ConstantConfig;
// import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
// import org.junit.Assert;
// import org.junit.Test;
//
// public class TestCodeGen {
//    private static final String configFile =
//            BcosSDKTest.class
//                    .getClassLoader()
//                    .getResource(ConstantConfig.CONFIG_FILE_NAME)
//                    .getPath();
//
//    @Test
//    public void TestCodeGen() throws ContractException {
//        BcosSDK sdk = BcosSDK.build(configFile);
//        // check groupList
//        Assert.assertTrue(sdk.getChannel().getAvailablePeer().size() >= 1);
//        // get the client
//        Client client = sdk.getClient(1);
//        CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
//        CodeGen codeGen = CodeGen.deploy(client, cryptoKeyPair);
//        System.out.println(codeGen.getContractAddress());
//        // contractAddress="0x1212e1dfb2261629e7ededab17d014e859d0ea03";
//        // codeGen = CodeGen.load(contractAddress, client, cryptoKeyPair);
//        String addr = "123";
//        BigInteger points = new BigInteger("123");
//        String accountName = "TestCode";
//        List<String> accountTag = new ArrayList<String>();
//        accountTag.add("test codegen");
//        byte[] label = "eccc72479ad511e99835b8ee6591991d".getBytes();
//        CodeGen.Struct0 account = new CodeGen.Struct0(addr, points, accountName, accountTag,
// label);
//        // test string
//        String getString = codeGen.getString("get string");
//        Assert.assertEquals(getString, "get string");
//        String setString = codeGen.setString("set string");
//        Assert.assertEquals(setString, "hello new");
//        // test int、string、bytes、address
//        CodeGen.Struct0 getMix = codeGen.getMix(addr, points, addr, label);
//        Assert.assertTrue(getMix.accountTag.get(0).isEmpty());
//        CodeGen.Struct0 setMix = codeGen.setMix(addr, points, "666", label);
//        Assert.assertTrue(setMix.accountAddr.contains(addr));
//        Assert.assertEquals(setMix.accountTag.get(0), "666");
//        // test struct
//        CodeGen.Struct0 setStructTest = codeGen.setStructTest(account);
//        Assert.assertEquals(setStructTest.accountName, "set_new");
//        CodeGen.Struct0 getStructTest = codeGen.getStructTest(account);
//        Assert.assertEquals(getStructTest.accountName, accountName);
//        // test int[]、string[]、bytes[]、address[]
//        List<String> addr_arr = new ArrayList<String>();
//        addr_arr.add("123");
//        addr_arr.add("234");
//        List<BigInteger> int_arr = new ArrayList<BigInteger>();
//        int_arr.add(new BigInteger("111"));
//        int_arr.add(new BigInteger("222"));
//        List<String> str_arr = new ArrayList<String>();
//        str_arr.add("addr1");
//        str_arr.add("addr2");
//        List<byte[]> byte_arr = new ArrayList<byte[]>();
//        byte_arr.add(label);
//        CodeGen.Struct0 setArray = codeGen.setArray(addr_arr, int_arr, str_arr, byte_arr);
//        Assert.assertTrue(setArray.accountAddr.contains(addr_arr.get(0)));
//        CodeGen.Struct0 getArray = codeGen.getArray(addr_arr, int_arr, str_arr, byte_arr);
//        Assert.assertTrue(getArray.accountAddr.contains(addr_arr.get(0)));
//        // int、string、struct
//        CodeGen.Struct0 set = codeGen.set(new BigInteger("1"), "Test Code", account);
//        Assert.assertEquals(set.points, new BigInteger("1"));
//        CodeGen.Struct0 get = codeGen.get(new BigInteger("2"), "Test Code", account);
//        Assert.assertNotEquals(get.points, new BigInteger("2"));
//    }
// }
