package org.fisco.bcos.sdk.v3.test.wasm.transaction.decoder;

import java.math.BigInteger;
import java.util.Random;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.abi.Constant;
import org.fisco.bcos.sdk.v3.model.ConstantConfig;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.test.wasm.liquid.CodecTest;
import org.junit.Assert;
import org.junit.Test;

public class ScaleCodecTest {
    private static final String configFile =
            "src/integration-wasm-test/resources/" + ConstantConfig.CONFIG_FILE_NAME;

    @Test
    public void testNumericType() throws Exception {
        BcosSDK sdk = BcosSDK.build(configFile);
        Client client = sdk.getClient("group0");
        CodecTest codecTest =
                CodecTest.deploy(
                        client,
                        client.getCryptoSuite().getCryptoKeyPair(),
                        "codecTest" + new Random().nextInt(10000));
        // string
        TransactionReceipt receipt = codecTest.set_str("Test test");
        Assert.assertEquals(0, receipt.getStatus());
        String s = codecTest.get_str();
        Assert.assertEquals("Test test", s);
        // u8
        codecTest.set_u8(BigInteger.TEN);
        BigInteger u8 = codecTest.get_u8();
        Assert.assertEquals(BigInteger.TEN, u8);

        // u128
        // 2^63-1 * 2^63-1
        codecTest.set_u128(Constant.MAX_INT128);
        BigInteger u128 = codecTest.get_u128();
        System.out.println(u128);
        Assert.assertEquals(0, Constant.MAX_INT128.compareTo(u128));
        // u256
        codecTest.set_u256(Constant.MAX_INT256);
        BigInteger u256 = codecTest.get_u256();
        Assert.assertEquals(Constant.MAX_INT256, u256);
        // i8
        codecTest.set_i8(BigInteger.valueOf(-1));
        BigInteger i8 = codecTest.get_i8();
        Assert.assertEquals(BigInteger.valueOf(-1), i8);
        // i128
        codecTest.set_i128(Constant.MIN_INT128);
        BigInteger i128 = codecTest.get_i128();
        Assert.assertEquals(0, Constant.MIN_INT128.compareTo(i128));

        codecTest.set_i128(Constant.MAX_INT128);
        BigInteger maxI128 = codecTest.get_i128();
        Assert.assertEquals(0, Constant.MAX_INT128.compareTo(maxI128));
        // i256
        // FIXME: use MIN_INT256 and MAX_INT256 will cause error
        codecTest.set_i256(BigInteger.valueOf(-123456789));
        BigInteger i256 = codecTest.get_i256();
        Assert.assertEquals(0, i256.compareTo(BigInteger.valueOf(-123456789)));

        client.stop();
        client.destroy();
    }
}
