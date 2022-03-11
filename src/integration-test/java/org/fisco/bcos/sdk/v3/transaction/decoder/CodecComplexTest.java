package org.fisco.bcos.sdk.v3.transaction.decoder;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.util.encoders.Hex;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.contract.solidity.ComplexCodecTest;
import org.fisco.bcos.sdk.v3.model.ConstantConfig;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.junit.Assert;
import org.junit.Test;

public class CodecComplexTest {
    private static final String configFile =
            "src/integration-test/resources/" + ConstantConfig.CONFIG_FILE_NAME;

    @Test
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
        Assert.assertEquals(bytes32Str, structA.valueStr);
        Assert.assertEquals(bytes32Str, Hex.toHexString(structA.bytes32InStruct.get(0)));

        // setBStruct
        TransactionReceipt transactionReceipt1 = complexCodecTest.setBStruct(structA);
        ComplexCodecTest.StructB structB =
                complexCodecTest.getSetBStructOutput(transactionReceipt1).getValue1();
        Assert.assertEquals(bytes32Str, structB.a_struct.getValue().get(0).valueStr);
        Assert.assertEquals(
                bytes32Str,
                Hex.toHexString(structB.a_struct.getValue().get(0).bytes32InStruct.get(0)));

        // setBStruct2
        TransactionReceipt transactionReceipt2 = complexCodecTest.setBStruct2(structB);
        ComplexCodecTest.StructA structA1 =
                complexCodecTest.getSetBStruct2Output(transactionReceipt2).getValue1();
        Assert.assertEquals(bytes32Str, structA1.valueStr);
        Assert.assertEquals(bytes32Str, Hex.toHexString(structA1.bytes32InStruct.get(0)));

        // staticStruct

    }
}
