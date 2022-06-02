package org.fisco.bcos.sdk.v3.test.transaction.codec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosTransactionReceipt;
import org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
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

import java.util.Collections;

public class TransactionDecodeTest {
    private final CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
    private final TransactionDecoderService transactionDecoderService = new TransactionDecoderService(cryptoSuite, false);
    private static final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
    private static TransactionReceipt errorReceipt;
    private static TransactionReceipt normalReceipt;
    private static TransactionReceipt sysReceipt;
    private static TransactionReceipt sysErrorReceipt;

    public TransactionDecodeTest() throws JsonProcessingException {
        String receiptStr =
                "{\n"
                        + "    \"id\": 1,\n"
                        + "    \"jsonrpc\": \"2.0\",\n"
                        + "    \"result\": {\n"
                        + "        \"blockHash\": \"0x977efec48c248ea4be87016446b40d7785d7b71b7d4e3aa0b103b9cf0f5fe19e\",\n"
                        + "        \"blockNumber\": \"0xa\",\n"
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
        RetCode retCode = ReceiptParser.parseTransactionReceipt(normalReceipt);
        Assert.assertEquals(retCode.getCode(), PrecompiledRetCode.CODE_SUCCESS.code);

        Assert.assertThrows(ContractException.class,
                () -> ReceiptParser.parseTransactionReceipt(errorReceipt));

        try {
            ReceiptParser.parseTransactionReceipt(errorReceipt);
        } catch (ContractException exception) {
            Assert.assertEquals("test string", exception.getMessage());
            Assert.assertEquals(12, exception.getErrorCode());
        }

        Assert.assertThrows(ContractException.class,
                () -> ReceiptParser.parseTransactionReceipt(sysErrorReceipt));
        try {
            ReceiptParser.parseTransactionReceipt(sysErrorReceipt);
        } catch (ContractException exception) {
            Assert.assertEquals("test string", exception.getMessage());
            Assert.assertEquals(15, exception.getErrorCode());
        }

        RetCode retCode1 = ReceiptParser.parseTransactionReceipt(sysReceipt);
        Assert.assertEquals(123, retCode1.getCode());
    }
}
