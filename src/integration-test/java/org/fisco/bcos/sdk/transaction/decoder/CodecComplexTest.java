package org.fisco.bcos.sdk.transaction.decoder;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.util.encoders.Hex;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.contract.solidity.ComplexCodecTest;
import org.fisco.bcos.sdk.model.ConstantConfig;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.junit.Assert;

public class CodecComplexTest {
    private static final String configFile =
            "src/integration-test/resources/" + ConstantConfig.CONFIG_FILE_NAME;

    // @Test
    public void testStructType() throws Exception {
        BcosSDK sdk = BcosSDK.build(configFile);
        Client client = sdk.getClient("group0");
        ComplexCodecTest complexCodecTest =
                ComplexCodecTest.deploy(client, client.getCryptoSuite().getCryptoKeyPair());

        String bytes32Str = "ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff";
        List<byte[]> bytes = new ArrayList<>();
        bytes.add(Hex.decode(bytes32Str));

        // setAStruct
        TransactionReceipt transactionReceipt = complexCodecTest.setAStruct(bytes32Str, bytes);
        Tuple1<ComplexCodecTest.StructA> setAStructOutput =
                complexCodecTest.getSetAStructOutput(transactionReceipt);
        ComplexCodecTest.StructA structA = setAStructOutput.getValue1();
        Assert.assertEquals(bytes32Str, structA.value_str);
        Assert.assertEquals(bytes32Str, Hex.toHexString(structA.bytes32_in_struct.get(0)));

        // setBStruct
        TransactionReceipt transactionReceipt1 = complexCodecTest.setBStruct(structA);
        ComplexCodecTest.StructB structB =
                complexCodecTest.getSetBStructOutput(transactionReceipt1).getValue1();
        Assert.assertEquals(bytes32Str, structB.a_struct.getValue().get(0).value_str);
        Assert.assertEquals(
                bytes32Str,
                Hex.toHexString(structB.a_struct.getValue().get(0).bytes32_in_struct.get(0)));

        // setBStruct2
        TransactionReceipt transactionReceipt2 = complexCodecTest.setBStruct2(structB);
        ComplexCodecTest.StructA structA1 =
                complexCodecTest.getSetBStruct2Output(transactionReceipt2).getValue1();
        Assert.assertEquals(bytes32Str, structA1.value_str);
        Assert.assertEquals(bytes32Str, Hex.toHexString(structA1.bytes32_in_struct.get(0)));

        // staticStruct

    }
}
