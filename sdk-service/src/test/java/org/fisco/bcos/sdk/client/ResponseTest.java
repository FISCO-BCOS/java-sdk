/**
 * Copyright 2014-2020 [fisco-dev]
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fisco.bcos.sdk.test.client;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fisco.bcos.sdk.client.protocol.model.GroupStatus;
import org.fisco.bcos.sdk.client.protocol.response.*;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.NodeVersion;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;

public class ResponseTest {
    private static ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();

    @Test
    public void testBlockHeaderResponse() {
        String blockHeaderString =
                "{\n"
                        + "  \"id\": 1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": {\n"
                        + "    \"dbHash\": \"0x0000000000000000000000000000000000000000000000000000000000000000\",\n"
                        + "    \"extraData\": [],\n"
                        + "    \"gasLimit\": \"0x100\",\n"
                        + "    \"gasUsed\": \"0x200\",\n"
                        + "    \"hash\": \"0xc558dd020df46dd3c2753dc8e1f85b79bf7849005dd4b84e3c8b5c1f6f642a82\",\n"
                        + "    \"logsBloom\": \"0x0000abc123\",\n"
                        + "    \"number\": 1,\n"
                        + "    \"parentHash\": \"0x3d161a0302bb05d97d68e129c552a83f171e673d0b6b866c1f687c3da98d9a08\",\n"
                        + "    \"receiptRoot\": \"0x69a04fa6073e4fc0947bac7ee6990e788d1e2c5ec0fe6c2436d0892e7f3c09d2\",\n"
                        + "    \"sealer\": \"0x3\",\n"
                        + "    \"sealerList\": [\n"
                        + "      \"11e1be251ca08bb44f36fdeedfaeca40894ff80dfd80084607a75509edeaf2a9c6fee914f1e9efda571611cf4575a1577957edfd2baa9386bd63eb034868625f\",\n"
                        + "      \"b8acb51b9fe84f88d670646be36f31c52e67544ce56faf3dc8ea4cf1b0ebff0864c6b218fdcd9cf9891ebd414a995847911bd26a770f429300085f37e1131f36\"\n"
                        + "    ],\n"
                        + "    \"signatureList\": [\n"
                        + "      {\n"
                        + "        \"index\": \"0x0\",\n"
                        + "        \"signature\": \"0x2e491c54f75c3b501745e1b2c92898f9434751326a17e4bcd37b93d5930405e14a461cff9ea7857da33fbf8b5ae6450ff0e281953553193aefb298b66d45b38401\"\n"
                        + "      },\n"
                        + "      {\n"
                        + "        \"index\": \"0x3\",\n"
                        + "        \"signature\": \"0x5cf59da9e2f580f0b62f8a43f0debe85a209a1c0e3bf66da58913841cb0daf50439baf9807c63a2cebcbac72a5ba679447a9101e39f7c08f1634ca5c99da970c01\"\n"
                        + "      }\n"
                        + "    ],\n"
                        + "    \"stateRoot\": \"0x000000000000000000000000000000000000000000000000000c000000000000\",\n"
                        + "    \"timestamp\": \"0x1736f190efb\",\n"
                        + "    \"transactionsRootCopyed\": \"0x9eec1be2effb2d7934928d4ccab1bd2886b920b1cf29f8744e3be1d253102cd7\",\n"
                        + "    \"transactionsRoot\": \"0x9eec1be2effb2d7934928d4ccab1bd2886b920b1cf29f8744e3be1d253102cd7\"\n"
                        + "  }\n"
                        + "}";
        try {
            // decode the block header
            BcosBlockHeader blockHeader =
                    objectMapper.readValue(blockHeaderString.getBytes(), BcosBlockHeader.class);
            // check the value field of the blockHeader
            Assert.assertEquals("2.0", blockHeader.getJsonrpc());
            Assert.assertEquals(1, blockHeader.getId());
            Assert.assertEquals(BigInteger.valueOf(1), blockHeader.getBlockHeader().getNumber());
            Assert.assertEquals(
                    "0xc558dd020df46dd3c2753dc8e1f85b79bf7849005dd4b84e3c8b5c1f6f642a82",
                    blockHeader.getBlockHeader().getHash());
            Assert.assertEquals("0x0000abc123", blockHeader.getBlockHeader().getLogsBloom());
            Assert.assertEquals(
                    "0x9eec1be2effb2d7934928d4ccab1bd2886b920b1cf29f8744e3be1d253102cd7",
                    blockHeader.getBlockHeader().getTransactionsRoot());
            Assert.assertEquals("0x1736f190efb", blockHeader.getBlockHeader().getTimestamp());
            Assert.assertEquals(
                    "11e1be251ca08bb44f36fdeedfaeca40894ff80dfd80084607a75509edeaf2a9c6fee914f1e9efda571611cf4575a1577957edfd2baa9386bd63eb034868625f",
                    blockHeader.getBlockHeader().getSealerList().get(0));
            Assert.assertEquals(
                    "0x2e491c54f75c3b501745e1b2c92898f9434751326a17e4bcd37b93d5930405e14a461cff9ea7857da33fbf8b5ae6450ff0e281953553193aefb298b66d45b38401",
                    blockHeader.getBlockHeader().getSignatureList().get(0).getSignature());
            Assert.assertEquals(
                    "0x3", blockHeader.getBlockHeader().getSignatureList().get(1).getIndex());
            Assert.assertEquals("0x3", blockHeader.getBlockHeader().getSealer());
            Assert.assertEquals(
                    "0x3d161a0302bb05d97d68e129c552a83f171e673d0b6b866c1f687c3da98d9a08",
                    blockHeader.getBlockHeader().getParentHash());
            Assert.assertEquals(
                    "0x69a04fa6073e4fc0947bac7ee6990e788d1e2c5ec0fe6c2436d0892e7f3c09d2",
                    blockHeader.getBlockHeader().getReceiptRoot());
            Assert.assertEquals("0x200", blockHeader.getBlockHeader().getGasUsed());
            Assert.assertEquals(
                    "0x000000000000000000000000000000000000000000000000000c000000000000",
                    blockHeader.getBlockHeader().getStateRoot());
            Assert.assertEquals(2, blockHeader.getBlockHeader().getSignatureList().size());

            // encode the block header
            byte[] encodedData = objectMapper.writeValueAsBytes(blockHeader);
            // decode the encoded block header
            BcosBlockHeader decodedBlockHeader =
                    objectMapper.readValue(encodedData, BcosBlockHeader.class);

            // check decodedBlockHeader and blockHeader
            Assert.assertEquals(blockHeader.getBlockHeader(), decodedBlockHeader.getBlockHeader());
            Assert.assertEquals(
                    blockHeader.getBlockHeader().hashCode(),
                    decodedBlockHeader.getBlockHeader().hashCode());
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTransacation() throws IOException {
        String transactionString =
                "{\n"
                        + "  \"id\": 100,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": {\n"
                        + "    \"blockHash\": \"0xc558dd020df46dd3c2753dc8e1f85b79bf7849005dd4b84e3c8b5c1f6f642a82\",\n"
                        + "    \"blockNumber\": \"0x1001\",\n"
                        + "    \"from\": \"0x2d6300a8f067872ebc87252d711b83a0c9325d35\",\n"
                        + "    \"gas\": \"0x2faf080\",\n"
                        + "    \"gasPrice\": \"0xa\",\n"
                        + "    \"hash\": \"0x83ae369e15e1aafb18df7da2ff30de009bf53a1ff72ced3d1c342182409c4f87\",\n"
                        + "    \"input\": \"0x4ed3885e0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000a464953434f2042434f5300000000000000000000000000000000000000000000\",\n"
                        + "    \"nonce\": \"0x3eb675ec791c2d19858c91d0046821c27d815e2e9c151595296779000016038\",\n"
                        + "    \"to\": \"0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744\",\n"
                        + "    \"transactionIndex\": \"0x199\",\n"
                        + "    \"value\": \"0x010\"\n"
                        + "  }\n"
                        + "}";
        // decode the BcosTransaction object from the given string
        BcosTransaction transaction =
                objectMapper.readValue(transactionString.getBytes(), BcosTransaction.class);
        Assert.assertEquals("2.0", transaction.getJsonrpc());
        Assert.assertEquals(100, transaction.getId());
        Assert.assertEquals(null, transaction.getError());
        Assert.assertEquals(
                "0x2d6300a8f067872ebc87252d711b83a0c9325d35",
                transaction.getTransaction().get().getFrom());
        Assert.assertEquals(
                "0x83ae369e15e1aafb18df7da2ff30de009bf53a1ff72ced3d1c342182409c4f87",
                transaction.getTransaction().get().getHash());
        Assert.assertEquals(
                "0x4ed3885e0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000a464953434f2042434f5300000000000000000000000000000000000000000000",
                transaction.getTransaction().get().getInput());
        Assert.assertEquals(
                "0x3eb675ec791c2d19858c91d0046821c27d815e2e9c151595296779000016038",
                transaction.getTransaction().get().getNonce());
        Assert.assertEquals(
                "0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744",
                transaction.getTransaction().get().getTo());

        // encode the transaction
        byte[] encodedData = objectMapper.writeValueAsBytes(transaction);
        // decode the transaction
        BcosTransaction decodedTransaction =
                objectMapper.readValue(encodedData, BcosTransaction.class);

        // check `hashCode` and `equals`
        Assert.assertEquals(
                transaction.getTransaction().get(), decodedTransaction.getTransaction().get());
        Assert.assertEquals(
                transaction.getTransaction().get().hashCode(),
                decodedTransaction.getTransaction().get().hashCode());
    }

    @Test
    public void testBcosBlockWithTransaction() throws IOException {
        String blockString =
                "{\n"
                        + "  \"id\": 10001,\n"
                        + "  \"jsonrpc\": \"3.0\",\n"
                        + "  \"result\": {\n"
                        + "    \"dbHash\": \"0x0000000000000000000000000000000000000000000000000000000000000000\",\n"
                        + "    \"extraData\": [],\n"
                        + "    \"gasLimit\": \"0x0\",\n"
                        + "    \"gasUsed\": \"0x0\",\n"
                        + "    \"hash\": \"0xc558dd020df46dd3c2753dc8e1f85b79bf7849005dd4b84e3c8b5c1f6f642a82\",\n"
                        + "    \"logsBloom\": \"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n"
                        + "    \"number\": \"0x100\",\n"
                        + "    \"parentHash\": \"0x3d161a0302bb05d97d68e129c552a83f171e673d0b6b866c1f687c3da98d9a08\",\n"
                        + "    \"receiptRoot\": \"0x69a04fa6073e4fc0947bac7ee6990e788d1e2c5ec0fe6c2436d0892e7f3c09d2\",\n"
                        + "    \"sealer\": \"0x4\",\n"
                        + "    \"sealerList\": [\n"
                        + "      \"11e1be251ca08bb44f36fdeedfaeca40894ff80dfd80084607a75509edeaf2a9c6fee914f1e9efda571611cf4575a1577957edfd2baa9386bd63eb034868625f\",\n"
                        + "      \"b8acb51b9fe84f88d670646be36f31c52e67544ce56faf3dc8ea4cf1b0ebff0864c6b218fdcd9cf9891ebd414a995847911bd26a770f429300085f37e1131f36\"\n"
                        + "    ],\n"
                        + "    \"stateRoot\": \"0x0000000000000000000000000000000000000000000000000000000000000000\",\n"
                        + "    \"timestamp\": \"0x1736f190efb\",\n"
                        + "    \"transactions\": [\n"
                        + "      {\n"
                        + "        \"blockHash\": \"0xc558dd020df46dd3c2753dc8e1f85b79bf7849005dd4b84e3c8b5c1f6f642a82\",\n"
                        + "        \"blockNumber\": \"0x100\",\n"
                        + "        \"from\": \"0x2d6300a8f067872ebc87252d711b83a0c9325d35\",\n"
                        + "        \"gas\": \"0x2faf080\",\n"
                        + "        \"gasPrice\": \"0xa\",\n"
                        + "        \"hash\": \"0x83ae369e15e1aafb18df7da2ff30de009bf53a1ff72ced3d1c342182409c4f87\",\n"
                        + "        \"input\": \"0x4ed3885e0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000a464953434f2042434f5300000000000000000000000000000000000000000000\",\n"
                        + "        \"nonce\": \"0x3eb675ec791c2d19858c91d0046821c27d815e2e9c151595296779000016038\",\n"
                        + "        \"to\": \"0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744\",\n"
                        + "        \"transactionIndex\": \"0x0\",\n"
                        + "        \"value\": \"0x0\"\n"
                        + "      }\n"
                        + "    ],\n"
                        + "    \"transactionsRoot\": \"0x9eec1be2effb2d7934928d4ccab1bd2886b920b1cf29f8744e3be1d253102cd7\"\n"
                        + "    }\n"
                        + "  }";
        // encode the string into object
        BcosBlock bcosBlock = objectMapper.readValue(blockString.getBytes(), BcosBlock.class);
        this.checkBlockHeader(bcosBlock);
        // check the transaction
        this.checkTransactionsForBlock(bcosBlock);
        this.checkEncodeDecode(bcosBlock);
    }

    public void testBcosBlockWithoutTransaction() throws IOException {
        String blockString =
                "{\n"
                        + "  \"id\": 10001,\n"
                        + "  \"jsonrpc\": \"3.0\",\n"
                        + "  \"result\": {\n"
                        + "    \"dbHash\": \"0x0000000000000000000000000000000000000000000000000000000000000000\",\n"
                        + "    \"extraData\": [],\n"
                        + "    \"gasLimit\": \"0x0\",\n"
                        + "    \"gasUsed\": \"0x0\",\n"
                        + "    \"hash\": \"0xc558dd020df46dd3c2753dc8e1f85b79bf7849005dd4b84e3c8b5c1f6f642a82\",\n"
                        + "    \"logsBloom\": \"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n"
                        + "    \"number\": \"0x100\",\n"
                        + "    \"parentHash\": \"0x3d161a0302bb05d97d68e129c552a83f171e673d0b6b866c1f687c3da98d9a08\",\n"
                        + "    \"receiptRoot\": \"0x69a04fa6073e4fc0947bac7ee6990e788d1e2c5ec0fe6c2436d0892e7f3c09d2\",\n"
                        + "    \"sealer\": \"0x4\",\n"
                        + "    \"sealerList\": [\n"
                        + "      \"11e1be251ca08bb44f36fdeedfaeca40894ff80dfd80084607a75509edeaf2a9c6fee914f1e9efda571611cf4575a1577957edfd2baa9386bd63eb034868625f\",\n"
                        + "      \"b8acb51b9fe84f88d670646be36f31c52e67544ce56faf3dc8ea4cf1b0ebff0864c6b218fdcd9cf9891ebd414a995847911bd26a770f429300085f37e1131f36\"\n"
                        + "    ],\n"
                        + "    \"stateRoot\": \"0x0000000000000000000000000000000000000000000000000000000000000000\",\n"
                        + "    \"timestamp\": \"0x1736f190efb\",\n"
                        + " \"transactions\": [ \n"
                        + "             \"0x19e5f919888038fcb16c7d75bb86945e1bf6349c591d33e3b5fdcda973577875\" \n"
                        + "    ],\n"
                        + "    \"transactionsRoot\": \"0x9eec1be2effb2d7934928d4ccab1bd2886b920b1cf29f8744e3be1d253102cd7\"\n"
                        + "    }\n"
                        + "  }";
        BcosBlock bcosBlock = objectMapper.readValue(blockString.getBytes(), BcosBlock.class);
        this.checkBlockHeader(bcosBlock);
        // check transaction
        this.checkEncodeDecode(bcosBlock);
    }

    private void checkEncodeDecode(BcosBlock bcosBlock) throws IOException {
        // encode the block
        byte[] encodedData = objectMapper.writeValueAsBytes(bcosBlock);
        // decode the block
        BcosBlock decodedBlock = objectMapper.readValue(encodedData, BcosBlock.class);
        // check the block
        Assert.assertEquals(bcosBlock.getBlock(), decodedBlock.getBlock());
        Assert.assertEquals(bcosBlock.getBlock().hashCode(), decodedBlock.getBlock().hashCode());
    }

    private void checkBlockHeader(BcosBlock bcosBlock) {
        // check the BcosBlock object
        Assert.assertEquals("3.0", bcosBlock.getJsonrpc());
        Assert.assertEquals(10001, bcosBlock.getId());
        Assert.assertEquals(BigInteger.valueOf(0x100), bcosBlock.getBlock().getNumber());
        Assert.assertEquals(
                "0xc558dd020df46dd3c2753dc8e1f85b79bf7849005dd4b84e3c8b5c1f6f642a82",
                bcosBlock.getBlock().getHash());
        Assert.assertEquals(
                "0x3d161a0302bb05d97d68e129c552a83f171e673d0b6b866c1f687c3da98d9a08",
                bcosBlock.getBlock().getParentHash());
        Assert.assertEquals(
                "0x69a04fa6073e4fc0947bac7ee6990e788d1e2c5ec0fe6c2436d0892e7f3c09d2",
                bcosBlock.getBlock().getReceiptRoot());
        Assert.assertEquals(2, bcosBlock.getBlock().getSealerList().size());
        Assert.assertEquals(
                "11e1be251ca08bb44f36fdeedfaeca40894ff80dfd80084607a75509edeaf2a9c6fee914f1e9efda571611cf4575a1577957edfd2baa9386bd63eb034868625f",
                bcosBlock.getBlock().getSealerList().get(0));
        Assert.assertEquals("0x4", bcosBlock.getBlock().getSealer());
        Assert.assertEquals("0x1736f190efb", bcosBlock.getBlock().getTimestamp());
        Assert.assertEquals(0, bcosBlock.getBlock().getExtraData().length());
    }

    private void checkTransactionsForBlock(BcosBlock bcosBlock) {
        Assert.assertEquals(1, bcosBlock.getBlock().getTransactions().size());
        BcosBlock.TransactionObject transaction =
                ((BcosBlock.TransactionObject) bcosBlock.getBlock().getTransactions().get(0));
        Assert.assertEquals("0x2d6300a8f067872ebc87252d711b83a0c9325d35", transaction.getFrom());
        Assert.assertEquals(
                "0x83ae369e15e1aafb18df7da2ff30de009bf53a1ff72ced3d1c342182409c4f87",
                transaction.getHash());
        Assert.assertEquals(
                "0x4ed3885e0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000a464953434f2042434f5300000000000000000000000000000000000000000000",
                transaction.getInput());
        Assert.assertEquals(
                "0x3eb675ec791c2d19858c91d0046821c27d815e2e9c151595296779000016038",
                transaction.getNonce());
        Assert.assertEquals("0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744", transaction.getTo());
    }

    @Test
    public void testBlockHash() throws IOException {
        // test BlockHash
        String blockHashString =
                "{\n"
                        + "  \"id\": 1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": \"0xc558dd020df46dd3c2753dc8e1f85b79bf7849005dd4b84e3c8b5c1f6f642a82\"\n"
                        + "}";
        BlockHash blockHash = objectMapper.readValue(blockHashString.getBytes(), BlockHash.class);
        // check the blockHash
        Assert.assertEquals(
                "0xc558dd020df46dd3c2753dc8e1f85b79bf7849005dd4b84e3c8b5c1f6f642a82",
                blockHash.getBlockHashByNumber());

        // encode the blockHash
        byte[] encodedData = objectMapper.writeValueAsBytes(blockHash);
        BlockHash decodedBlockHash = objectMapper.readValue(encodedData, BlockHash.class);
        Assert.assertEquals(
                blockHash.getBlockHashByNumber(), decodedBlockHash.getBlockHashByNumber());
        Assert.assertEquals(
                blockHash.getBlockHashByNumber().hashCode(),
                decodedBlockHash.getBlockHashByNumber().hashCode());
    }

    @Test
    public void testBlockNumber() throws IOException {
        // test BlockNumber
        String blockNumberString =
                "{\n"
                        + "  \"id\": 11,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": \"0x200\"\n"
                        + "}";
        BlockNumber blockNumber =
                objectMapper.readValue(blockNumberString.getBytes(), BlockNumber.class);
        Assert.assertEquals(BigInteger.valueOf(0x200), blockNumber.getBlockNumber());
        Assert.assertEquals("2.0", blockNumber.getJsonrpc());
        Assert.assertEquals(11, blockNumber.getId());

        // encode the block number
        byte[] encodedData = objectMapper.writeValueAsBytes(blockNumber);
        BlockNumber decodedBlockNumber = objectMapper.readValue(encodedData, BlockNumber.class);
        Assert.assertEquals(blockNumber.getBlockNumber(), decodedBlockNumber.getBlockNumber());
        Assert.assertEquals(
                blockNumber.getBlockNumber().hashCode(),
                decodedBlockNumber.getBlockNumber().hashCode());
    }

    @Test
    public void testCall() throws IOException {
        String callString =
                "{\n"
                        + "    \"id\": 102,\n"
                        + "    \"jsonrpc\": \"3.0\",\n"
                        + "    \"result\": {\n"
                        + "        \"currentBlockNumber\": \"0xb\",\n"
                        + "        \"output\": \"0x\",\n"
                        + "        \"status\": \"0x0\"\n"
                        + "    }\n"
                        + "}";
        Call callResult = objectMapper.readValue(callString.getBytes(), Call.class);
        Assert.assertEquals("3.0", callResult.getJsonrpc());
        Assert.assertEquals(102, callResult.getId());
        // check callResult
        Call.CallOutput callOutput = callResult.getCallResult();
        Assert.assertEquals(BigInteger.valueOf(0xb), callOutput.getBlockNumber());
        Assert.assertEquals("0x", callOutput.getOutput());
        Assert.assertEquals("0x0", callOutput.getStatus());

        // encode the callResult
        byte[] encodedData = objectMapper.writeValueAsBytes(callResult);
        Call decodedCallResult = objectMapper.readValue(encodedData, Call.class);
        Assert.assertEquals(callResult.getCallResult(), decodedCallResult.getCallResult());
        Assert.assertEquals(
                callResult.getCallResult().hashCode(),
                decodedCallResult.getCallResult().hashCode());
    }

    @Test
    public void testGetCode() throws IOException {
        String codeStr =
                "{\n"
                        + "    \"id\": 1,\n"
                        + "    \"jsonrpc\": \"2.0\",\n"
                        + "    \"result\": \"0x60606040523415600b57fe5b5b60928061001a6000396000f30060606040526000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680636d4ce63c14603a575bfe5b3415604157fe5b6047605d565b6040518082815260200191505060405180910390f35b60004290505b905600a165627a7a723058203d9c292921247163d180a161baa8db840c9da6764cab1d23f1e11a5cff13c7910029\"\n"
                        + "}";
        Code code = objectMapper.readValue(codeStr.getBytes(), Code.class);
        Assert.assertEquals("2.0", code.getJsonrpc());
        Assert.assertEquals(1, code.getId());
        // check result
        Assert.assertEquals(
                "0x60606040523415600b57fe5b5b60928061001a6000396000f30060606040526000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680636d4ce63c14603a575bfe5b3415604157fe5b6047605d565b6040518082815260200191505060405180910390f35b60004290505b905600a165627a7a723058203d9c292921247163d180a161baa8db840c9da6764cab1d23f1e11a5cff13c7910029",
                code.getCode());
        // encode the code
        byte[] encodedData = objectMapper.writeValueAsBytes(code);
        Code decodedCode = objectMapper.readValue(encodedData, Code.class);
        Assert.assertEquals(code.getCode(), decodedCode.getCode());
        Assert.assertEquals(code.getCode().hashCode(), decodedCode.getCode().hashCode());
    }

    private void checkGroupStatus(
            GroupStatus status, String expectedCode, String expectedMsg, String expectedStatus)
            throws IOException {
        Assert.assertEquals(expectedCode, status.getCode());
        Assert.assertEquals(expectedMsg, status.getMessage());
        Assert.assertEquals(expectedStatus, status.getStatus());

        // check encode/decode
        byte[] encodedData = objectMapper.writeValueAsBytes(status);
        GroupStatus decodedStatus = objectMapper.readValue(encodedData, GroupStatus.class);
        Assert.assertEquals(status, decodedStatus);
        Assert.assertEquals(status.hashCode(), decodedStatus.hashCode());
    }

    @Test
    public void testNodeVersion() throws IOException {
        String nodeVersionStr =
                "{\n"
                        + "  \"id\": 83,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": {\n"
                        + "    \"Build Time\": \"20190106 20:49:10\",\n"
                        + "    \"Build Type\": \"Linux/g++/RelWithDebInfo\",\n"
                        + "    \"FISCO-BCOS Version\": \"2.0.0\",\n"
                        + "    \"Git Branch\": \"master\",\n"
                        + "    \"Git Commit Hash\": \"693a709ddab39965d9c39da0104836cfb4a72054\"\n"
                        + "  }\n"
                        + "}\n";
        NodeVersion nodeVersion =
                objectMapper.readValue(nodeVersionStr.getBytes(), NodeVersion.class);
        Assert.assertEquals("20190106 20:49:10", nodeVersion.getNodeVersion().getBuildTime());
        Assert.assertEquals(
                "Linux/g++/RelWithDebInfo", nodeVersion.getNodeVersion().getBuildType());
        Assert.assertEquals("2.0.0", nodeVersion.getNodeVersion().getVersion());
        Assert.assertEquals("master", nodeVersion.getNodeVersion().getGitBranch());
        Assert.assertEquals(
                "693a709ddab39965d9c39da0104836cfb4a72054",
                nodeVersion.getNodeVersion().getGitCommitHash());
    }

    @Test
    public void testObserverList() throws IOException {
        String observerListStr =
                "{\n"
                        + "    \"id\": 1,\n"
                        + "    \"jsonrpc\": \"2.0\",\n"
                        + "    \"result\": [\n"
                        + "        \"10b3a2d4b775ec7f3c2c9e8dc97fa52beb8caab9c34d026db9b95a72ac1d1c1ad551c67c2b7fdc34177857eada75836e69016d1f356c676a6e8b15c45fc9bc34\"\n"
                        + "    ]\n"
                        + "}";

        ObserverList observerList =
                objectMapper.readValue(observerListStr.getBytes(), ObserverList.class);
        Assert.assertEquals(1, observerList.getObserverList().size());
        Assert.assertEquals(
                "10b3a2d4b775ec7f3c2c9e8dc97fa52beb8caab9c34d026db9b95a72ac1d1c1ad551c67c2b7fdc34177857eada75836e69016d1f356c676a6e8b15c45fc9bc34",
                observerList.getObserverList().get(0));
    }

    @Test
    public void testPbftView() throws IOException {
        String pbftViewStr =
                "{\n"
                        + "    \"id\": 1,\n"
                        + "    \"jsonrpc\": \"2.0\",\n"
                        + "    \"result\": \"0x1a0\"\n"
                        + "}";
        PbftView pbftView = objectMapper.readValue(pbftViewStr.getBytes(), PbftView.class);
        Assert.assertEquals(BigInteger.valueOf(0x1a0), pbftView.getPbftView());
    }

    @Test
    public void testPeers() throws IOException {
        String peerStr =
                "{\n"
                        + "  \"id\": 1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": [\n"
                        + "    {\n"
                        + "      \"Agency\": \"agency\",\n"
                        + "      \"IPAndPort\": \"127.0.0.1:51869\",\n"
                        + "      \"Node\": \"node2\",\n"
                        + "      \"NodeID\": \"78a313b426c3de3267d72b53c044fa9fe70c2a27a00af7fea4a549a7d65210ed90512fc92b6194c14766366d434235c794289d66deff0796f15228e0e14a9191\",\n"
                        + "      \"Topic\": []\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"Agency\": \"agency\",\n"
                        + "      \"IPAndPort\": \"127.0.0.1:30303\",\n"
                        + "      \"Node\": \"node3\",\n"
                        + "      \"NodeID\": \"95b7ff064f91de76598f90bc059bec1834f0d9eeb0d05e1086d49af1f9c2f321062d011ee8b0df7644bd54c4f9ca3d8515a3129bbb9d0df8287c9fa69552887e\",\n"
                        + "      \"Topic\": []\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"Agency\": \"agency\",\n"
                        + "      \"IPAndPort\": \"127.0.0.1:30301\",\n"
                        + "      \"Node\": \"node1\",\n"
                        + "      \"NodeID\": \"11e1be251ca08bb44f36fdeedfaeca40894ff80dfd80084607a75509edeaf2a9c6fee914f1e9efda571611cf4575a1577957edfd2baa9386bd63eb034868625f\",\n"
                        + "      \"Topic\": []\n"
                        + "    }\n"
                        + "  ]\n"
                        + "}";
        Peers peers = objectMapper.readValue(peerStr.getBytes(), Peers.class);
    }

    @Test
    public void testPendingTxSize() throws IOException {
        String pendingTxSizeStr =
                "{\n"
                        + "  \"id\": 1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": \"0x100\"\n"
                        + "}";
        PendingTxSize pendingTxSize =
                objectMapper.readValue(pendingTxSizeStr.getBytes(), PendingTxSize.class);
        Assert.assertEquals(BigInteger.valueOf(0x100), pendingTxSize.getPendingTxSize());
    }

    @Test
    public void testSealerList() throws JsonProcessingException {
        String sealerListStr =
                "{\n"
                        + "    \"id\": 1,\n"
                        + "    \"jsonrpc\": \"2.0\",\n"
                        + "    \"result\": [\n"
                        + "        \"037c255c06161711b6234b8c0960a6979ef039374ccc8b723afea2107cba3432dbbc837a714b7da20111f74d5a24e91925c773a72158fa066f586055379a1772\",\n"
                        + "        \"0c0bbd25152d40969d3d3cee3431fa28287e07cff2330df3258782d3008b876d146ddab97eab42796495bfbb281591febc2a0069dcc7dfe88c8831801c5b5801\",\n"
                        + "        \"622af37b2bd29c60ae8f15d467b67c0a7fe5eb3e5c63fdc27a0ee8066707a25afa3aa0eb5a3b802d3a8e5e26de9d5af33806664554241a3de9385d3b448bcd73\"\n"
                        + "    ]\n"
                        + "}";
        SealerList sealerList = objectMapper.readValue(sealerListStr, SealerList.class);
        Assert.assertEquals(3, sealerList.getSealerList().size());
        Assert.assertEquals(
                "0c0bbd25152d40969d3d3cee3431fa28287e07cff2330df3258782d3008b876d146ddab97eab42796495bfbb281591febc2a0069dcc7dfe88c8831801c5b5801",
                sealerList.getSealerList().get(1));
        Assert.assertEquals(
                "037c255c06161711b6234b8c0960a6979ef039374ccc8b723afea2107cba3432dbbc837a714b7da20111f74d5a24e91925c773a72158fa066f586055379a1772",
                sealerList.getSealerList().get(0));
        Assert.assertEquals(
                "622af37b2bd29c60ae8f15d467b67c0a7fe5eb3e5c63fdc27a0ee8066707a25afa3aa0eb5a3b802d3a8e5e26de9d5af33806664554241a3de9385d3b448bcd73",
                sealerList.getSealerList().get(2));
    }

    @Test
    public void testSyncStatus() throws JsonProcessingException {
        String syncStatusStr =
                "{\n"
                        + "  \"id\": 1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": {\n"
                        + "    \"blockNumber\": 100,\n"
                        + "    \"genesisHash\": \"0x4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2\",\n"
                        + "    \"isSyncing\": false,\n"
                        + "    \"knownHighestNumber\":0,\n"
                        + "    \"knownLatestHash\":\"0x4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2\",\n"
                        + "    \"latestHash\": \"0x4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2\",\n"
                        + "    \"nodeId\": \"41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba\",\n"
                        + "    \"peers\": [\n"
                        + "      {\n"
                        + "        \"blockNumber\": 0,\n"
                        + "        \"genesisHash\": \"0x4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2\",\n"
                        + "        \"latestHash\": \"0x4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2\",\n"
                        + "        \"nodeId\": \"29c34347a190c1ec0c4507c6eed6a5bcd4d7a8f9f54ef26da616e81185c0af11a8cea4eacb74cf6f61820292b24bc5d9e426af24beda06fbd71c217960c0dff0\"\n"
                        + "      },\n"
                        + "      {\n"
                        + "        \"blockNumber\": 0,\n"
                        + "        \"genesisHash\": \"0x4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2\",\n"
                        + "        \"latestHash\": \"0x4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2\",\n"
                        + "        \"nodeId\": \"87774114e4a496c68f2482b30d221fa2f7b5278876da72f3d0a75695b81e2591c1939fc0d3fadb15cc359c997bafc9ea6fc37345346acaf40b6042b5831c97e1\"\n"
                        + "      },\n"
                        + "      {\n"
                        + "        \"blockNumber\": 0,\n"
                        + "        \"genesisHash\": \"0x4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2\",\n"
                        + "        \"latestHash\": \"0x4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2\",\n"
                        + "        \"nodeId\": \"d5b3a9782c6aca271c9642aea391415d8b258e3a6d92082e59cc5b813ca123745440792ae0b29f4962df568f8ad58b75fc7cea495684988e26803c9c5198f3f8\"\n"
                        + "      }\n"
                        + "    ],\n"
                        + "    \"protocolId\": 265,\n"
                        + "    \"txPoolSize\": \"0\"\n"
                        + "  }\n"
                        + "}";
        SyncStatus syncStatus = objectMapper.readValue(syncStatusStr, SyncStatus.class);
        Assert.assertEquals("100", syncStatus.getSyncStatus().getBlockNumber());
        Assert.assertEquals(
                "0x4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2",
                syncStatus.getSyncStatus().getGenesisHash());
        Assert.assertEquals("false", syncStatus.getSyncStatus().getIsSyncing());
        Assert.assertEquals("0", syncStatus.getSyncStatus().getKnownHighestNumber());
        Assert.assertEquals(
                "0x4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2",
                syncStatus.getSyncStatus().getKnownLatestHash());
        Assert.assertEquals(
                "0x4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2",
                syncStatus.getSyncStatus().getLatestHash());
        Assert.assertEquals(
                "41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba",
                syncStatus.getSyncStatus().getNodeId());
        Assert.assertEquals("265", syncStatus.getSyncStatus().getProtocolId());
        Assert.assertEquals("0", syncStatus.getSyncStatus().getTxPoolSize());
        // check peers
        Assert.assertEquals(3, syncStatus.getSyncStatus().getPeers().size());
        Assert.assertEquals("0", syncStatus.getSyncStatus().getPeers().get(2).getBlockNumber());
        Assert.assertEquals(
                "0x4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2",
                syncStatus.getSyncStatus().getPeers().get(2).getGenesisHash());
        Assert.assertEquals(
                "0x4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2",
                syncStatus.getSyncStatus().getPeers().get(2).getLatestHash());
        Assert.assertEquals(
                "d5b3a9782c6aca271c9642aea391415d8b258e3a6d92082e59cc5b813ca123745440792ae0b29f4962df568f8ad58b75fc7cea495684988e26803c9c5198f3f8",
                syncStatus.getSyncStatus().getPeers().get(2).getNodeId());
    }

    @Test
    public void testSystemConfig() throws IOException {
        String systemConfigStr =
                "{\n"
                        + "  \"id\": 1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": \"1000\"\n"
                        + "}";
        SystemConfig systemConfig =
                objectMapper.readValue(systemConfigStr.getBytes(), SystemConfig.class);
        Assert.assertEquals("1000", systemConfig.getSystemConfig().toString());
    }

    @Test
    public void testTotalTransactionCount() throws JsonProcessingException {
        String totalTxCountStr =
                "{\n"
                        + "    \"id\": 1,\n"
                        + "    \"jsonrpc\": \"2.0\",\n"
                        + "    \"result\": {\n"
                        + "      \"blockNumber\": \"0x1\",\n"
                        + "      \"failedTxSum\": \"0x0\",\n"
                        + "      \"txSum\": \"0x20\"\n"
                        + "    }\n"
                        + "}";
        TotalTransactionCount txCount =
                objectMapper.readValue(totalTxCountStr, TotalTransactionCount.class);
        Assert.assertEquals("0x1", txCount.getTotalTransactionCount().getBlockNumber());
        Assert.assertEquals("0x0", txCount.getTotalTransactionCount().getFailedTransactionCount());
        Assert.assertEquals("0x20", txCount.getTotalTransactionCount().getTransactionCount());
    }

    @Test
    public void testTransactionReceipt() throws JsonProcessingException {
        String receiptStr =
                "{\n"
                        + "    \"id\": 1,\n"
                        + "    \"jsonrpc\": \"2.0\",\n"
                        + "    \"result\": {\n"
                        + "        \"blockHash\": \"0x977efec48c248ea4be87016446b40d7785d7b71b7d4e3aa0b103b9cf0f5fe19e\",\n"
                        + "        \"blockNumber\": \"0xa\",\n"
                        + "        \"contractAddress\": \"0x0000000000000000000000000000000000000000\",\n"
                        + "        \"from\": \"0xcdcce60801c0a2e6bb534322c32ae528b9dec8d2\",\n"
                        + "        \"gasUsed\": \"0x1fb8d\",\n"
                        + "        \"input\": \"0xb602109a000000000000000000000000000000000000000000000000000000000000008000000000000000000000000000000000000000000000000000000000000000c00000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000014000000000000000000000000000000000000000000000000000000000000000203078313030303030303030303030303030303030303030303030303030303030000000000000000000000000000000000000000000000000000000000000000832303139303733300000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002616100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000026262000000000000000000000000000000000000000000000000000000000000\",\n"
                        + "        \"logs\": [ ],\n"
                        + "        \"logsBloom\": \"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n"
                        + "        \"output\": \"0x0000000000000000000000000000000000000000000000000000000000000000\",\n"
                        + "        \"root\":\"0x38723a2e5e8a17aa7950dc008209944e898f69a7bd10a23c839d341e935fd5ca\",\n"
                        + "        \"status\": \"0xc\",\n"
                        + "        \"to\": \"0x15538acd403ac1b2ff09083c70d04856b8c0bdfd\",\n"
                        + "        \"transactionHash\": \"0x708b5781b62166bd86e543217be6cd954fd815fd192b9a124ee9327580df8f3f\",\n"
                        + "        \"transactionIndex\": \"0x10\"\n"
                        + "    }\n"
                        + "}";
        BcosTransactionReceipt transactionReceipt =
                objectMapper.readValue(receiptStr, BcosTransactionReceipt.class);
        Assert.assertEquals(
                "0x0000000000000000000000000000000000000000",
                transactionReceipt.getTransactionReceipt().get().getContractAddress());
        Assert.assertEquals(
                "0xcdcce60801c0a2e6bb534322c32ae528b9dec8d2",
                transactionReceipt.getTransactionReceipt().get().getFrom());
        Assert.assertEquals(
                "0x1fb8d", transactionReceipt.getTransactionReceipt().get().getGasUsed());
        Assert.assertEquals(
                "0xb602109a000000000000000000000000000000000000000000000000000000000000008000000000000000000000000000000000000000000000000000000000000000c00000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000014000000000000000000000000000000000000000000000000000000000000000203078313030303030303030303030303030303030303030303030303030303030000000000000000000000000000000000000000000000000000000000000000832303139303733300000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002616100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000026262000000000000000000000000000000000000000000000000000000000000",
                transactionReceipt.getTransactionReceipt().get().getInput());
        Assert.assertEquals(0, transactionReceipt.getTransactionReceipt().get().getLogs().size());
        Assert.assertEquals(
                "0x0000000000000000000000000000000000000000000000000000000000000000",
                transactionReceipt.getTransactionReceipt().get().getOutput());
        Assert.assertEquals("0xc", transactionReceipt.getTransactionReceipt().get().getStatus());
        Assert.assertEquals(
                "0x15538acd403ac1b2ff09083c70d04856b8c0bdfd",
                transactionReceipt.getTransactionReceipt().get().getTo());
        Assert.assertEquals(
                "0x708b5781b62166bd86e543217be6cd954fd815fd192b9a124ee9327580df8f3f",
                transactionReceipt.getTransactionReceipt().get().getTransactionHash());
    }

    @Test
    public void testTransactionReceiptWithProof() throws JsonProcessingException {
        String receiptWithProofStr =
                "{\n"
                        + "  \"id\": 1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": {\n"
                        + "    \"receiptProof\": [\n"
                        + "      {\n"
                        + "        \"left\": [\n"
                        + "          \"3088b5c8f9d92a3411a911f35ff0119a02e8f8f04852cf2fdfaa659843eac6a3ad\",\n"
                        + "          \"31170ac8fd555dc50e59050841da0d96e4c4bc7e6266e1c6865c08c3b2391801dd\"\n"
                        + "        ],\n"
                        + "        \"right\": [\n"
                        + "          \"33c572c8f961e0c56689d641fcf274916857819769a74e6424c58659bf530e90e3\",\n"
                        + "          \"341233933ea3d357b4fdd6b3d1ed732dcff15cfd54e527c93c15a4e0238585ed11\",\n"
                        + "          \"351e7ba09965cce1cfb820aced1d37204b06d96a21c5c2cf36850ffc62cf1fc84c\",\n"
                        + "          \"361f65633d9ae843d4d3679b255fd448546a7b531c0056e8161ea0adbf1af12c0f\",\n"
                        + "          \"37744f6e0d320314536b230d28b2fd6ac90b0111fb1e3bf4a750689abc282d8589\",\n"
                        + "          \"386e60d9daa0be9825019fcf3d08cdaf51a90dc62a22a6e11371f94a8e516679cc\",\n"
                        + "          \"391ef2f2cee81f3561a9900d5333af18f59aa3cd14e70241b5e86305ba697bf5f2\",\n"
                        + "          \"3ac9999d4f36d76c95c61761879eb9ec60b964a489527f5af844398ffaa8617f0d\",\n"
                        + "          \"3b0039ce903e275170640f3a464ce2e1adc2a7caee41267c195469365074032401\",\n"
                        + "          \"3ca53017502028a0cb5bbf6c47c4779f365138da6910ffcfebf9591b45b89abd48\",\n"
                        + "          \"3de04fc8766a344bb73d3fe6360c61d036e2eeedfd9ecdb86a0498d7849ed591f0\",\n"
                        + "          \"3e2fc73ee22c4986111423dd20e8db317a313c9df29fa5aa3090f27097ecc4e1a9\",\n"
                        + "          \"3fa7d31ad5c6e7bba3f99f9efc03ed8dd97cb1504003c34ad6bde5a662481f00a0\"\n"
                        + "        ]\n"
                        + "      },\n"
                        + "      {\n"
                        + "        \"left\": [\n"
                        + "          \"cd46118c0e99be585ffcf50423630348dbc486e54e9d9293a6a8754020a68a92\",\n"
                        + "          \"3be78209b3e3c83af3668ec3192b5bf232531323ef66b66de80a11f386270132\",\n"
                        + "          \"bd3a11d74a3fd79b1e1ea17e45b76eda4d25f6a5ec7fc5f067ea0d086b1ce70f\"\n"
                        + "        ],\n"
                        + "        \"right\": [\n"
                        + "          \"6a6cefef8b48e455287a8c8694b06f4f7cb7950017ab048d6e6bdd8029f9f8c9\",\n"
                        + "          \"0a27c5ee02e618d919d228e6a754dc201d299c91c9e4420a48783bb6fcd09be5\"\n"
                        + "        ]\n"
                        + "      },\n"
                        + "      {\n"
                        + "        \"left\": [],\n"
                        + "        \"right\": []\n"
                        + "      }\n"
                        + "    ],\n"
                        + "    \"transactionReceipt\": {\n"
                        + "      \"blockHash\": \"0xcd31b05e466bce99460b1ed70d6069fdfbb15e6eef84e9b9e4534358edb3899a\",\n"
                        + "      \"blockNumber\": \"0x5\",\n"
                        + "      \"contractAddress\": \"0x0000000000000000000000000000000000000000\",\n"
                        + "      \"from\": \"0x148947262ec5e21739fe3a931c29e8b84ee34a0f\",\n"
                        + "      \"gasUsed\": \"0x21dc1b\",\n"
                        + "      \"input\": \"0x8a42ebe90000000000000000000000000000000000000000000000000000000000000040000000000000000000000000000000000000000000000000000000003b9aca00000000000000000000000000000000000000000000000000000000000000000a3564646636663863653800000000000000000000000000000000000000000000\",\n"
                        + "      \"logs\": [],\n"
                        + "      \"logsBloom\": \"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n"
                        + "      \"output\": \"0x\",\n"
                        + "      \"root\": \"0xc3b4185963c78a4ca8eb90240e5cd95371d7217a9ce2bfa1149d53f79c73afbb\",\n"
                        + "      \"status\": \"0x0\",\n"
                        + "      \"to\": \"0xd6c8a04b8826b0a37c6d4aa0eaa8644d8e35b79f\",\n"
                        + "      \"transactionHash\": \"0xd2c12e211315ef09dbad53407bc820d062780232841534954f9c23ab11d8ab4c\",\n"
                        + "      \"transactionIndex\": \"0x32\"\n"
                        + "    }\n"
                        + "  }\n"
                        + "}";
        BcosTransactionReceipt receiptWithProof =
                objectMapper.readValue(receiptWithProofStr, BcosTransactionReceipt.class);
        Assert.assertEquals(
                3, receiptWithProof.getTransactionReceipt().get().getReceiptProof().size());
        Assert.assertEquals(
                2,
                receiptWithProof.getTransactionReceipt().get().getReceiptProof()
                        .get(0)
                        .getLeft()
                        .size());
        Assert.assertEquals(
                13,
                receiptWithProof
                        .getTransactionReceipt().get().getReceiptProof()
                        .get(0)
                        .getRight()
                        .size());
        Assert.assertEquals(
                3,
                receiptWithProof
                        .getTransactionReceipt().get().getReceiptProof()
                        .get(1)
                        .getLeft()
                        .size());
        Assert.assertEquals(
                2,
                receiptWithProof
                        .getTransactionReceipt().get().getReceiptProof()
                        .get(1)
                        .getRight()
                        .size());
        Assert.assertEquals(
                0,
                receiptWithProof
                        .getTransactionReceipt().get().getReceiptProof()
                        .get(2)
                        .getLeft()
                        .size());
        Assert.assertEquals(
                0,
                receiptWithProof
                        .getTransactionReceipt().get().getReceiptProof()
                        .get(2)
                        .getRight()
                        .size());
        Assert.assertEquals(
                "cd46118c0e99be585ffcf50423630348dbc486e54e9d9293a6a8754020a68a92",
                receiptWithProof
                        .getTransactionReceipt().get().getReceiptProof()
                        .get(1)
                        .getLeft()
                        .get(0));
        Assert.assertEquals(
                "6a6cefef8b48e455287a8c8694b06f4f7cb7950017ab048d6e6bdd8029f9f8c9",
                receiptWithProof
                        .getTransactionReceipt().get().getReceiptProof()
                        .get(1)
                        .getRight()
                        .get(0));
        Assert.assertEquals(
                "0x5",
                receiptWithProof.getTransactionReceipt().get().getBlockNumber());
        Assert.assertEquals(
                "0x0000000000000000000000000000000000000000",
                receiptWithProof
                        .getTransactionReceipt()
                        .get().getContractAddress());
        Assert.assertEquals(
                "0x148947262ec5e21739fe3a931c29e8b84ee34a0f",
                receiptWithProof.getTransactionReceipt().get().getFrom());
        Assert.assertEquals(
                "0x21dc1b",
                receiptWithProof.getTransactionReceipt().get().getGasUsed());
        Assert.assertEquals(
                "0x0", receiptWithProof.getTransactionReceipt().get().getStatus());
        Assert.assertEquals(
                "0xd6c8a04b8826b0a37c6d4aa0eaa8644d8e35b79f",
                receiptWithProof.getTransactionReceipt().get().getTo());
        Assert.assertEquals(
                null, receiptWithProof.getTransactionReceipt().get().getTransactionProof());
        Assert.assertEquals(
                null,
                receiptWithProof.getTransactionReceipt().get().getReceiptProof());
    }

    @Test
    public void testSMGetTransactionAndCalculateHash() throws IOException {
        String transactionString = "{\n" +
                "  \"id\": 1,\n" +
                "  \"jsonrpc\": \"2.0\",\n" +
                "  \"result\": {\n" +
                "    \"blockHash\": \"0x030f5cda798836d6747aaa94fd53f5eeb96ccd9af74567ab1c186c18ff20a811\",\n" +
                "    \"blockLimit\": \"0x1f4\",\n" +
                "    \"blockNumber\": \"0x1\",\n" +
                "    \"chainId\": \"0x1\",\n" +
                "    \"extraData\": \"0x\",\n" +
                "    \"from\": \"0xdba1e9f40baa1b956b509b04eb738eccfa8d784c\",\n" +
                "    \"gas\": \"0x419ce0\",\n" +
                "    \"gasPrice\": \"0x51f4d5c00\",\n" +
                "    \"groupId\": \"0x1\",\n" +
                "    \"hash\": \"0x9bd0f48eec8384231166321474203ed4d1398f1feb2c8bd35c13d3a1be5d0afd\",\n" +
                "    \"input\": \"0x608060405234801561001057600080fd5b506040805190810160405280600d81526020017f48656c6c6f2c20576f726c6421000000000000000000000000000000000000008152506000908051906020019061005c929190610062565b50610107565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106100a357805160ff19168380011785556100d1565b828001600101855582156100d1579182015b828111156100d05782518255916020019190600101906100b5565b5b5090506100de91906100e2565b5090565b61010491905b808211156101005760008160009055506001016100e8565b5090565b90565b6102d7806101166000396000f30060806040526004361061004c576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff168063299f7f9d146100515780633590b49f146100e1575b600080fd5b34801561005d57600080fd5b5061006661014a565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156100a657808201518184015260208101905061008b565b50505050905090810190601f1680156100d35780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156100ed57600080fd5b50610148600480360381019080803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091929192905050506101ec565b005b606060008054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156101e25780601f106101b7576101008083540402835291602001916101e2565b820191906000526020600020905b8154815290600101906020018083116101c557829003601f168201915b5050505050905090565b8060009080519060200190610202929190610206565b5050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061024757805160ff1916838001178555610275565b82800160010185558215610275579182015b82811115610274578251825591602001919060010190610259565b5b5090506102829190610286565b5090565b6102a891905b808211156102a457600081600090555060010161028c565b5090565b905600a165627a7a72305820c94ea3bc35f522d8a1c8c998b61d072d792ee8f822425f7e4b54e727f3ce105b0029\",\n" +
                "    \"nonce\": \"0x191da6814c29612a12bf24af7e9565b628f55e4eee2c340df144e86738f0d93\",\n" +
                "    \"signature\": {\n" +
                "      \"r\": \"0xce7b4b60bc723819e374547c3bdf5867743a88c401bfbe9cf89bc05dd95d01bc\",\n" +
                "      \"s\": \"0x7bc71f92263204486f666e074d54ac0ec36bb12da45d9b8bba90356eb447e37a\",\n" +
                "      \"signature\": \"0xce7b4b60bc723819e374547c3bdf5867743a88c401bfbe9cf89bc05dd95d01bc7bc71f92263204486f666e074d54ac0ec36bb12da45d9b8bba90356eb447e37a837195bb75136ce67f54ae466941c5b6092137db4586faf5bfff2269820aefd487fb09c03a5e84799c8c7c2263da6f710b48e6e24bfb5b3096613591499a2714\",\n" +
                "      \"v\": \"0x837195bb75136ce67f54ae466941c5b6092137db4586faf5bfff2269820aefd487fb09c03a5e84799c8c7c2263da6f710b48e6e24bfb5b3096613591499a2714\"\n" +
                "    },\n" +
                "    \"to\": \"0x0000000000000000000000000000000000000000\",\n" +
                "    \"transactionIndex\": \"0x0\",\n" +
                "    \"value\": \"0x0\"\n" +
                "  }\n" +
                "}";

        BcosTransaction bcosTransaction = objectMapper.readValue(transactionString.getBytes(), BcosTransaction.class);
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
        Assert.assertEquals(
                bcosTransaction.getTransaction().get().calculateHash(cryptoSuite),
                bcosTransaction.getTransaction().get().getHash());

        transactionString = "{\n" +
                "  \"id\": 1,\n" +
                "  \"jsonrpc\": \"2.0\",\n" +
                "  \"result\": {\n" +
                "    \"blockHash\": \"0x03d593af4d2bc7c4d07373bee5b5fc556c898b37328c2a04df7ea0f021941c3c\",\n" +
                "    \"blockLimit\": \"0x1f5\",\n" +
                "    \"blockNumber\": \"0x2\",\n" +
                "    \"chainId\": \"0x1\",\n" +
                "    \"extraData\": \"0x\",\n" +
                "    \"from\": \"0xdba1e9f40baa1b956b509b04eb738eccfa8d784c\",\n" +
                "    \"gas\": \"0x419ce0\",\n" +
                "    \"gasPrice\": \"0x51f4d5c00\",\n" +
                "    \"groupId\": \"0x1\",\n" +
                "    \"hash\": \"0x5badccc5577b3851cd1133c1a054491f2a3432171ecf07728ffbb32c1c7b8050\",\n" +
                "    \"input\": \"0x3590b49f0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000568656c6c6f000000000000000000000000000000000000000000000000000000\",\n" +
                "    \"nonce\": \"0x50a15577227bf5790b2fc79b6c6c30d8413a7b4f7ae489d89e926fd6b72631\",\n" +
                "    \"signature\": {\n" +
                "      \"r\": \"0x8130abdda331c9c7c8654c63da1fc34041148b990c76c3ff6238cfcd2386f56e\",\n" +
                "      \"s\": \"0x1d864e9f338c380b6502180ebde81685273f5f7b0945dde1c254f9c58152d64f\",\n" +
                "      \"signature\": \"0x8130abdda331c9c7c8654c63da1fc34041148b990c76c3ff6238cfcd2386f56e1d864e9f338c380b6502180ebde81685273f5f7b0945dde1c254f9c58152d64f837195bb75136ce67f54ae466941c5b6092137db4586faf5bfff2269820aefd487fb09c03a5e84799c8c7c2263da6f710b48e6e24bfb5b3096613591499a2714\",\n" +
                "      \"v\": \"0x837195bb75136ce67f54ae466941c5b6092137db4586faf5bfff2269820aefd487fb09c03a5e84799c8c7c2263da6f710b48e6e24bfb5b3096613591499a2714\"\n" +
                "    },\n" +
                "    \"to\": \"0xeb1164d2b50f07a5fdd719e2b924a81905ec0d53\",\n" +
                "    \"transactionIndex\": \"0x0\",\n" +
                "    \"value\": \"0x0\"\n" +
                "  }\n" +
                "}";
        bcosTransaction = objectMapper.readValue(transactionString.getBytes(), BcosTransaction.class);
        cryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
        Assert.assertEquals(
                bcosTransaction.getTransaction().get().calculateHash(cryptoSuite),
                bcosTransaction.getTransaction().get().getHash());
    }

    @Test
    public void testSMGetBlockAndCalculateHash() throws IOException {
        String blockHeaderStr = "{\n" +
                "  \"id\": 1,\n" +
                "  \"jsonrpc\": \"2.0\",\n" +
                "  \"result\": {\n" +
                "    \"dbHash\": \"0x68a77b2364be2f3197bce9ca265a5405ed77904237d8e31dbacfe9e1d3119f77\",\n" +
                "    \"extraData\": [],\n" +
                "    \"gasLimit\": \"0x0\",\n" +
                "    \"gasUsed\": \"0x0\",\n" +
                "    \"hash\": \"0xc5360efd06024b02340eb2afa283fe022f57791e888f22366b77d6218a247a13\",\n" +
                "    \"logsBloom\": \"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n" +
                "    \"number\": 1,\n" +
                "    \"parentHash\": \"0x7e1b0fc3efa8026f282bfa994d3a79305542d5ad3ea65b84a8d72b152f15dfb1\",\n" +
                "    \"receiptRoot\": \"0xd748b478e6b8f90e049f7a4a9d2b9acf76624baed8c2abe0e868b33cd5e989e5\",\n" +
                "    \"sealer\": \"0x3\",\n" +
                "    \"sealerList\": [\n" +
                "      \"1daca8140ba483b560d1b3b8905ca07f447b305875a4f9c6cb2a826c9315ef10bc87a7e135d0a34f605f3a95ff5d9a8c83f2ac5f070c6fe740400910813110a2\",\n" +
                "      \"2e6ddeb52fcdb0f0287c8b6bbe407f4a3a52bc1b04ea5b978ab698ac1802eb5db482ec1681b1d1d8d1a5e99143a7cde2b85fe29bbe6538066507a91fc8e5ecc6\",\n" +
                "      \"4905b78b643c19c03e7b8e6779fca2a3e917baa317e8d2abde6daec543d375ac5052aeda22fda7e174c780e04afd215f965237a809e814369a05bb90b965a6ed\",\n" +
                "      \"86f731c15ca2f44925fea7f379ca32a55245fb988228305c7625d4a174a186fc4472d4668053c7fe4c8608562cf2fb8fea1ab5ea4c96b9be01949b565ec36c9e\"\n" +
                "    ],\n" +
                "    \"signatureList\": [\n" +
                "      {\n" +
                "        \"index\": \"0x0\",\n" +
                "        \"signature\": \"0xa99fcb5298a5dd39644af81b2c3ebd9839ffa9f2cb65c6c8b9f2b84b8804c93cf836cb45059cdefe8767ea922e0141318da7ffcc6d0d9db2b0cfa23638bc86591daca8140ba483b560d1b3b8905ca07f447b305875a4f9c6cb2a826c9315ef10bc87a7e135d0a34f605f3a95ff5d9a8c83f2ac5f070c6fe740400910813110a2\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"index\": \"0x3\",\n" +
                "        \"signature\": \"0x1addc8032fbca41e31afb429dd2f749653492684fcc7845acb4558d5b09095a311a2d965c2a59133b497cb2553c23f29c6613ca0d312acb4f9fd93df602936f686f731c15ca2f44925fea7f379ca32a55245fb988228305c7625d4a174a186fc4472d4668053c7fe4c8608562cf2fb8fea1ab5ea4c96b9be01949b565ec36c9e\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"index\": \"0x1\",\n" +
                "        \"signature\": \"0xd2831bc1b60ce6a0bf71bd89d312b57ef9ad211b4efea3f8bfd38387998d547f5db59ce870a65d16d98a344c85ce0f2e22d371ce455eef0a8c9566f5fa7f71ec2e6ddeb52fcdb0f0287c8b6bbe407f4a3a52bc1b04ea5b978ab698ac1802eb5db482ec1681b1d1d8d1a5e99143a7cde2b85fe29bbe6538066507a91fc8e5ecc6\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"stateRoot\": \"0x68a77b2364be2f3197bce9ca265a5405ed77904237d8e31dbacfe9e1d3119f77\",\n" +
                "    \"timestamp\": \"0x174ce4a8931\",\n" +
                "    \"transactionsRoot\": \"0x60368d2fde59f678e096418d521b53fce8355fb8bca1448d4bb6f5209376e7fc\"\n" +
                "  }\n" +
                "}";

        BcosBlockHeader bcosBlockHeader = objectMapper.readValue(blockHeaderStr.getBytes(), BcosBlockHeader.class);
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
        Assert.assertEquals(
                bcosBlockHeader.getBlockHeader().calculateHash(cryptoSuite),
                bcosBlockHeader.getBlockHeader().getHash());
    }

    @Test
    public void testECDSAGetTransactionAndCalculateHash() throws IOException {
        String transactionStr = "{\n" +
                "  \"id\": 1,\n" +
                "  \"jsonrpc\": \"2.0\",\n" +
                "  \"result\": {\n" +
                "    \"blockHash\": \"0xed79502afaf87734f5bc75c2b50d340adc83128afed9dc626a4f5a3cfed837a7\",\n" +
                "    \"blockLimit\": \"0x100\",\n" +
                "    \"blockNumber\": \"0x1\",\n" +
                "    \"chainId\": \"0x1\",\n" +
                "    \"extraData\": \"0x\",\n" +
                "    \"from\": \"0xfb257558db8f24ee1c2799df7cc68051fc8d27f7\",\n" +
                "    \"gas\": \"0x2faf080\",\n" +
                "    \"gasPrice\": \"0xa\",\n" +
                "    \"groupId\": \"0x1\",\n" +
                "    \"hash\": \"0xd8a34a32b86e049fb5e1c0ce89a2a96c34f0c54e622e10abf20d0a0f15bb98cf\",\n" +
                "    \"input\": \"0x4ed3885e0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000a464953434f2042434f5300000000000000000000000000000000000000000000\",\n" +
                "    \"nonce\": \"0x3eb675ec791c2d19858c91d0046821c27d815e2e9c151601203592000016309\",\n" +
                "    \"signature\": {\n" +
                "      \"r\": \"0x9edf7c0cb63645442aff11323916d51ec5440de979950747c0189f338afdcefd\",\n" +
                "      \"s\": \"0x2f3473184513c6a3516e066ea98b7cfb55a79481c9db98e658dd016c37f03dcf\",\n" +
                "      \"signature\": \"0x9edf7c0cb63645442aff11323916d51ec5440de979950747c0189f338afdcefd2f3473184513c6a3516e066ea98b7cfb55a79481c9db98e658dd016c37f03dcf00\",\n" +
                "      \"v\": \"0x0\"\n" +
                "    },\n" +
                "    \"to\": \"0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744\",\n" +
                "    \"transactionIndex\": \"0x0\",\n" +
                "    \"value\": \"0x0\"\n" +
                "  }\n" +
                "}";
        BcosTransaction bcosTransaction = objectMapper.readValue(transactionStr.getBytes(), BcosTransaction.class);
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        Assert.assertEquals(
                bcosTransaction.getTransaction().get().calculateHash(cryptoSuite),
                "0xd8a34a32b86e049fb5e1c0ce89a2a96c34f0c54e622e10abf20d0a0f15bb98cf");
    }

    @Test
    public void testECDSAGetBlockAndCalculateHash() throws IOException {
        String blockHeaderStr = "{\n" +
                "  \"id\": 1,\n" +
                "  \"jsonrpc\": \"2.0\",\n" +
                "  \"result\": {\n" +
                "    \"dbHash\": \"0x0000000000000000000000000000000000000000000000000000000000000000\",\n" +
                "    \"extraData\": [],\n" +
                "    \"gasLimit\": \"0x0\",\n" +
                "    \"gasUsed\": \"0x0\",\n" +
                "    \"hash\": \"0xed79502afaf87734f5bc75c2b50d340adc83128afed9dc626a4f5a3cfed837a7\",\n" +
                "    \"logsBloom\": \"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n" +
                "    \"number\": 1,\n" +
                "    \"parentHash\": \"0x4f6394763c33c1709e5a72b202ad4d7a3b8152de3dc698cef6f675ecdaf20a3b\",\n" +
                "    \"receiptRoot\": \"0x69a04fa6073e4fc0947bac7ee6990e788d1e2c5ec0fe6c2436d0892e7f3c09d2\",\n" +
                "    \"sealer\": \"0x3\",\n" +
                "    \"sealerList\": [\n" +
                "      \"11e1be251ca08bb44f36fdeedfaeca40894ff80dfd80084607a75509edeaf2a9c6fee914f1e9efda571611cf4575a1577957edfd2baa9386bd63eb034868625f\",\n" +
                "      \"78a313b426c3de3267d72b53c044fa9fe70c2a27a00af7fea4a549a7d65210ed90512fc92b6194c14766366d434235c794289d66deff0796f15228e0e14a9191\",\n" +
                "      \"95b7ff064f91de76598f90bc059bec1834f0d9eeb0d05e1086d49af1f9c2f321062d011ee8b0df7644bd54c4f9ca3d8515a3129bbb9d0df8287c9fa69552887e\",\n" +
                "      \"b8acb51b9fe84f88d670646be36f31c52e67544ce56faf3dc8ea4cf1b0ebff0864c6b218fdcd9cf9891ebd414a995847911bd26a770f429300085f37e1131f36\"\n" +
                "    ],\n" +
                "    \"signatureList\": [\n" +
                "      {\n" +
                "        \"index\": \"0x0\",\n" +
                "        \"signature\": \"0x8c274e08c1b86b363634266a9c474a261313ec19ad28bd029465143e9708ef4e74844b6d3e4b1192e290548efe27639398917dfc42195fc81509aa995179895501\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"index\": \"0x1\",\n" +
                "        \"signature\": \"0x2f25b3cae930b15963745b75bcd12f25837bca336e63f9039e531a505dd85f212b74da6a6530c87052bc8a54d49ee1baae480d32b8b2283cc0b5474f8dd1835400\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"index\": \"0x3\",\n" +
                "        \"signature\": \"0x97bc872a3beb48d0c373a6a3368ce23086c1c070f29137978f5ac3803b5ef5dc7f9d0d2a377be5995b89a37bc0ccb6cced8a1fcf29b808d7073c2afe819b3be101\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"stateRoot\": \"0x0000000000000000000000000000000000000000000000000000000000000000\",\n" +
                "    \"timestamp\": \"0x174cf2bdede\",\n" +
                "    \"transactionsRoot\": \"0xab7114f4e2930d02852e1578c0a845328e8b69fa8413000d8570483d272937a8\"\n" +
                "  }\n" +
                "}";
        BcosBlockHeader bcosBlockHeader = objectMapper.readValue(blockHeaderStr.getBytes(), BcosBlockHeader.class);
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        Assert.assertEquals(
                bcosBlockHeader.getBlockHeader().calculateHash(cryptoSuite),
                "0xed79502afaf87734f5bc75c2b50d340adc83128afed9dc626a4f5a3cfed837a7");
    }

}
