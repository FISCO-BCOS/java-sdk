package org.fisco.bcos.sdk.v3.test.transaction.codec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.utilities.tx.Transaction;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionDataV2;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionStructBuilderJniObj;
import org.fisco.bcos.sdk.v3.client.protocol.model.JsonTransactionResponse;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosTransactionReceipt;
import org.fisco.bcos.sdk.v3.codec.FunctionReturnDecoderInterface;
import org.fisco.bcos.sdk.v3.codec.Utils;
import org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int32;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.TransactionDecoderService;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.fisco.bcos.sdk.v3.utils.ObjectMapperFactory;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

public class TransactionDecodeTest {
    private final CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
    private final TransactionDecoderService transactionDecoderService = new TransactionDecoderService(cryptoSuite, false);
    private static final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
    private static TransactionReceipt errorReceipt;
    private static TransactionReceipt normalReceipt;
    private static TransactionReceipt sysReceipt;
    private static TransactionReceipt sysErrorReceipt;
    FunctionReturnDecoderInterface abiDecoder = new org.fisco.bcos.sdk.v3.codec.abi.FunctionReturnDecoder();
    FunctionReturnDecoderInterface scaleDecoder = new org.fisco.bcos.sdk.v3.codec.scale.FunctionReturnDecoder();

    public TransactionDecodeTest() throws JsonProcessingException {
        String receiptStr =
                "{\n"
                        + "    \"id\": 1,\n"
                        + "    \"jsonrpc\": \"2.0\",\n"
                        + "    \"result\": {\n"
                        + "        \"blockHash\": \"0x977efec48c248ea4be87016446b40d7785d7b71b7d4e3aa0b103b9cf0f5fe19e\",\n"
                        + "        \"blockNumber\": \"11\",\n"
                        + "        \"contractAddress\": \"0000000000000000000000000000000000000000\",\n"
                        + "        \"from\": \"0xcdcce60801c0a2e6bb534322c32ae528b9dec8d2\",\n"
                        + "        \"gasUsed\": \"0x1fb8d\",\n"
                        + "        \"input\": \"0xb602109a000000000000000000000000000000000000000000000000000000000000008000000000000000000000000000000000000000000000000000000000000000c00000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000014000000000000000000000000000000000000000000000000000000000000000203078313030303030303030303030303030303030303030303030303030303030000000000000000000000000000000000000000000000000000000000000000832303139303733300000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002616100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000026262000000000000000000000000000000000000000000000000000000000000\",\n"
                        + "        \"logEntries\": [ ],\n"
                        + "        \"logsBloom\": \"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n"
                        + "        \"output\": \"0x08c379a00000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000b7465737420737472696e67000000000000000000000000000000000000000000\",\n"
                        + "        \"root\":\"0x38723a2e5e8a17aa7950dc008209944e898f69a7bd10a23c839d341e935fd5ca\",\n"
                        + "        \"status\": \"12\",\n"
                        + "        \"to\": \"15538acd403ac1b2ff09083c70d04856b8c0bdfd\",\n"
                        + "        \"transactionHash\": \"0x708b5781b62166bd86e543217be6cd954fd815fd192b9a124ee9327580df8f3f\",\n"
                        + "        \"transactionIndex\": \"0x10\"\n"
                        + "    }\n"
                        + "}";
        errorReceipt = objectMapper.readValue(receiptStr, BcosTransactionReceipt.class).getTransactionReceipt();
        normalReceipt = objectMapper.readValue(receiptStr, BcosTransactionReceipt.class).getTransactionReceipt();
        normalReceipt.setStatus(0);
        sysErrorReceipt = objectMapper.readValue(receiptStr, BcosTransactionReceipt.class).getTransactionReceipt();
        sysErrorReceipt.setTo("0000000000000000000000000000000000001001");
        sysErrorReceipt.setStatus(15);

        sysReceipt = objectMapper.readValue(receiptStr, BcosTransactionReceipt.class).getTransactionReceipt();
        sysReceipt.setTo("0000000000000000000000000000000000001001");
        sysReceipt.setStatus(0);
        sysReceipt.setOutput("0x000000000000000000000000000000000000000000000000000000000000007b");
    }

    @Test
    public void decodeRevertMessageTest() {
        org.fisco.bcos.sdk.v3.codec.FunctionEncoderInterface functionEncoderInterface = new FunctionEncoder(cryptoSuite);
        Function function =
                new Function(
                        "Error",
                        Collections.singletonList(new Utf8String("test string")),
                        Collections.emptyList()
                );

        byte[] encode = functionEncoderInterface.encode(function);
        String encodeHex = Hex.toHexStringWithPrefix(encode);
        String testString = "0x08c379a00000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000b7465737420737472696e67000000000000000000000000000000000000000000";

        Assert.assertEquals(testString, encodeHex);

        String revertMessage = transactionDecoderService.decodeRevertMessage(testString);
        Assert.assertEquals("test string", revertMessage);
    }

    @Test
    public void parseTransactionReceiptTest() throws ContractException {
        RetCode retCode = ReceiptParser.parseTransactionReceipt(normalReceipt, null);
        Assert.assertEquals(retCode.getCode(), PrecompiledRetCode.CODE_SUCCESS.code);

        Assert.assertThrows(ContractException.class,
                () -> ReceiptParser.parseTransactionReceipt(errorReceipt, null));

        try {
            ReceiptParser.parseTransactionReceipt(errorReceipt, null);
        } catch (ContractException exception) {
            Assert.assertEquals("test string", exception.getMessage());
            Assert.assertEquals(12, exception.getErrorCode());
        }

        Assert.assertThrows(ContractException.class,
                () -> ReceiptParser.parseTransactionReceipt(sysErrorReceipt, null));
        try {
            ReceiptParser.parseTransactionReceipt(sysErrorReceipt, null);
        } catch (ContractException exception) {
            Assert.assertEquals("test string", exception.getMessage());
            Assert.assertEquals(15, exception.getErrorCode());
        }

        RetCode retCode1 = ReceiptParser.parseTransactionReceipt(sysReceipt, transactionReceipt -> {
            List<Type> result = abiDecoder.decode(transactionReceipt.getOutput(),
                    Utils.convert(Collections.singletonList(new TypeReference<Int32>() {
            })));
            return (BigInteger)result.get(0).getValue();
        });
        Assert.assertEquals(123, retCode1.getCode());

        sysReceipt.setOutput("01ffffff");
        RetCode retCode2 = ReceiptParser.parseTransactionReceipt(sysReceipt, transactionReceipt -> {
            List<Type> result = scaleDecoder.decode(transactionReceipt.getOutput(),
                    Utils.convert(Collections.singletonList(new TypeReference<Int32>() {
                    })));
            return (BigInteger)result.get(0).getValue();
        });
        Assert.assertEquals(-255, retCode2.getCode());
    }

    @Test
    public void testDeocde () throws JniException {
        String txV0 = "0x1a1c2606636861696e30360667726f7570304101fd562831343230303535313832363235373935303133363132373836353832393033393938343232343237662a3078303130326538623666633863646639363236666464633163336561386331653739623366636539347d0000644ed3885e0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000474657374000000000000000000000000000000000000000000000000000000000b2d00002082b0283ac2793fb68be2f2f79056251664093500a57313e3adad74d852cb32e53d000041cb1d9cc1e3b87ec4afa9ccf680ce11cad980056b63a58641dff78c84a6e7c6a41f3480b0cfa1a9a5107f4a0bf2ccffabeb1a550d66a1a754e9aba6b266cde7da00";
        String txV1 = "0x1a10012606636861696e30360667726f7570304101fb56203731646430333435623135373437353738643531376661366164623330393461662a3078333165643532333362383163373964356164646465656639393166353331613962626332616430317d0000644ed3885e000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000047465730a000000000000000000000000000000000000000000000000000000009603307830a6033078300b2d0000205bb1fa7f6b420cbc5d99f075edf4e2f340d003f76c35cc95d83ce3d6f790a4823d0000418f72ab9782925d323e759aa6d1bfc492160d84176030712395530dc42bdf973b0c4dcb64e20fcbd922e417f04010ed4cbd3e86c073da00292e49b301a4e2554c015001";

        {
            JsonTransactionResponse transactionV0 = JsonTransactionResponse.decodeTransaction(txV0);
            JsonTransactionResponse transactionV1 =
                    JsonTransactionResponse.decodeTransactionV1(txV1);

            System.out.println(transactionV0);
            System.out.println(transactionV1);
        }

        {
//            TransactionDataV2 transactionDataV2 = new TransactionDataV2();
//            transactionDataV2.setInput(new byte[1]);
//            String s = TransactionStructBuilderJniObj.encodeTransactionDataStruct(transactionDataV2);
//            System.out.println(s);
        }
    }
}
