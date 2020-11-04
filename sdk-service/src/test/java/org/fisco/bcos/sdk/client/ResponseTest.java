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

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import org.fisco.bcos.sdk.client.protocol.model.GroupStatus;
import org.fisco.bcos.sdk.client.protocol.response.BcosBlock;
import org.fisco.bcos.sdk.client.protocol.response.BcosBlockHeader;
import org.fisco.bcos.sdk.client.protocol.response.BcosTransaction;
import org.fisco.bcos.sdk.client.protocol.response.BcosTransactionReceipt;
import org.fisco.bcos.sdk.client.protocol.response.BcosTransactionReceiptsDecoder;
import org.fisco.bcos.sdk.client.protocol.response.BcosTransactionReceiptsInfo;
import org.fisco.bcos.sdk.client.protocol.response.BlockHash;
import org.fisco.bcos.sdk.client.protocol.response.BlockNumber;
import org.fisco.bcos.sdk.client.protocol.response.Call;
import org.fisco.bcos.sdk.client.protocol.response.Code;
import org.fisco.bcos.sdk.client.protocol.response.ConsensusStatus;
import org.fisco.bcos.sdk.client.protocol.response.GenerateGroup;
import org.fisco.bcos.sdk.client.protocol.response.GroupList;
import org.fisco.bcos.sdk.client.protocol.response.GroupPeers;
import org.fisco.bcos.sdk.client.protocol.response.NodeIDList;
import org.fisco.bcos.sdk.client.protocol.response.ObserverList;
import org.fisco.bcos.sdk.client.protocol.response.PbftView;
import org.fisco.bcos.sdk.client.protocol.response.Peers;
import org.fisco.bcos.sdk.client.protocol.response.PendingTransactions;
import org.fisco.bcos.sdk.client.protocol.response.PendingTxSize;
import org.fisco.bcos.sdk.client.protocol.response.RecoverGroup;
import org.fisco.bcos.sdk.client.protocol.response.SealerList;
import org.fisco.bcos.sdk.client.protocol.response.SendTransaction;
import org.fisco.bcos.sdk.client.protocol.response.StartGroup;
import org.fisco.bcos.sdk.client.protocol.response.StopGroup;
import org.fisco.bcos.sdk.client.protocol.response.SyncStatus;
import org.fisco.bcos.sdk.client.protocol.response.SystemConfig;
import org.fisco.bcos.sdk.client.protocol.response.TotalTransactionCount;
import org.fisco.bcos.sdk.client.protocol.response.TransactionReceiptWithProof;
import org.fisco.bcos.sdk.client.protocol.response.TransactionWithProof;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.NodeVersion;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.junit.Assert;
import org.junit.Test;

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
                        + "    \"receiptsRoot\": \"0x69a04fa6073e4fc0947bac7ee6990e788d1e2c5ec0fe6c2436d0892e7f3c09d2\",\n"
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
                    blockHeader.getBlockHeader().getReceiptsRoot());
            Assert.assertEquals("0x100", blockHeader.getBlockHeader().getGasLimit());
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
                "0xc558dd020df46dd3c2753dc8e1f85b79bf7849005dd4b84e3c8b5c1f6f642a82",
                transaction.getTransaction().get().getBlockHash());
        Assert.assertEquals(
                BigInteger.valueOf(0x1001), transaction.getTransaction().get().getBlockNumber());
        Assert.assertEquals(
                "0x2d6300a8f067872ebc87252d711b83a0c9325d35",
                transaction.getTransaction().get().getFrom());
        Assert.assertEquals("0x2faf080", transaction.getTransaction().get().getGas());
        Assert.assertEquals("0xa", transaction.getTransaction().get().getGasPrice());
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
        Assert.assertEquals("0x199", transaction.getTransaction().get().getTransactionIndex());
        Assert.assertEquals("0x010", transaction.getTransaction().get().getValue());

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
                        + "    \"receiptsRoot\": \"0x69a04fa6073e4fc0947bac7ee6990e788d1e2c5ec0fe6c2436d0892e7f3c09d2\",\n"
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
        checkBlockHeader(bcosBlock);
        // check the transaction
        checkTransactionsForBlock(bcosBlock);
        checkEncodeDecode(bcosBlock);
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
                        + "    \"receiptsRoot\": \"0x69a04fa6073e4fc0947bac7ee6990e788d1e2c5ec0fe6c2436d0892e7f3c09d2\",\n"
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
        checkBlockHeader(bcosBlock);
        // check transaction
        BcosBlock.TransactionHash transactionHash =
                ((BcosBlock.TransactionHash) bcosBlock.getBlock().getTransactions().get(0));
        // check the transactionHash
        Assert.assertEquals(
                "0x19e5f919888038fcb16c7d75bb86945e1bf6349c591d33e3b5fdcda973577875",
                transactionHash);
        checkEncodeDecode(bcosBlock);
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
                bcosBlock.getBlock().getReceiptsRoot());
        Assert.assertEquals(2, bcosBlock.getBlock().getSealerList().size());
        Assert.assertEquals(
                "11e1be251ca08bb44f36fdeedfaeca40894ff80dfd80084607a75509edeaf2a9c6fee914f1e9efda571611cf4575a1577957edfd2baa9386bd63eb034868625f",
                bcosBlock.getBlock().getSealerList().get(0));
        Assert.assertEquals("0x4", bcosBlock.getBlock().getSealer());
        Assert.assertEquals("0x1736f190efb", bcosBlock.getBlock().getTimestamp());
        Assert.assertEquals(0, bcosBlock.getBlock().getExtraData().size());
    }

    private void checkTransactionsForBlock(BcosBlock bcosBlock) {
        Assert.assertEquals(1, bcosBlock.getBlock().getTransactions().size());
        BcosBlock.TransactionObject transaction =
                ((BcosBlock.TransactionObject) bcosBlock.getBlock().getTransactions().get(0));
        Assert.assertEquals(
                "0xc558dd020df46dd3c2753dc8e1f85b79bf7849005dd4b84e3c8b5c1f6f642a82",
                transaction.getBlockHash());
        Assert.assertEquals(BigInteger.valueOf(0x100), transaction.getBlockNumber());
        Assert.assertEquals("0x2d6300a8f067872ebc87252d711b83a0c9325d35", transaction.getFrom());
        Assert.assertEquals("0x2faf080", transaction.getGas());
        Assert.assertEquals("0xa", transaction.getGasPrice());
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
        Assert.assertEquals("0x0", transaction.getTransactionIndex());
        Assert.assertEquals("0x0", transaction.getValue());
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
        Assert.assertEquals(BigInteger.valueOf(0xb), callOutput.getCurrentBlockNumber());
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

    @Test
    public void testPBFTConsensusStatus() throws IOException {
        String pbftConsensusStatusString =
                "{\n"
                        + "  \"id\": 1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": [\n"
                        + "    {\n"
                        + "      \"accountType\": 1,\n"
                        + "      \"allowFutureBlocks\": true,\n"
                        + "      \"cfgErr\": false,\n"
                        + "      \"connectedNodes\": 3,\n"
                        + "      \"consensusedBlockNumber\": 38207,\n"
                        + "      \"currentView\": 54477,\n"
                        + "      \"groupId\": 1,\n"
                        + "      \"highestblockHash\": \"0x19a16e8833e671aa11431de589c866a6442ca6c8548ba40a44f50889cd785069\",\n"
                        + "      \"highestblockNumber\": 38206,\n"
                        + "      \"leaderFailed\": false,\n"
                        + "      \"max_faulty_leader\": 1,\n"
                        + "      \"nodeId\": \"f72648fe165da17a889bece08ca0e57862cb979c4e3661d6a77bcc2de85cb766af5d299fec8a4337eedd142dca026abc2def632f6e456f80230902f93e2bea13\",\n"
                        + "      \"nodeNum\": 4,\n"
                        + "      \"node_index\": 3,\n"
                        + "      \"omitEmptyBlock\": true,\n"
                        + "      \"protocolId\": 65544,\n"
                        + "      \"sealer.0\": \"6a99f357ecf8a001e03b68aba66f68398ee08f3ce0f0147e777ec77995369aac470b8c9f0f85f91ebb58a98475764b7ca1be8e37637dd6cb80b3355749636a3d\",\n"
                        + "      \"sealer.1\": \"8a453f1328c80b908b2d02ba25adca6341b16b16846d84f903c4f4912728c6aae1050ce4f24cd9c13e010ce922d3393b846f6f5c42f6af59c65a814de733afe4\",\n"
                        + "      \"sealer.2\": \"ed483837e73ee1b56073b178f5ac0896fa328fc0ed418ae3e268d9e9109721421ec48d68f28d6525642868b40dd26555c9148dbb8f4334ca071115925132889c\",\n"
                        + "      \"sealer.3\": \"f72648fe165da17a889bece08ca0e57862cb979c4e3661d6a77bcc2de85cb766af5d299fec8a4337eedd142dca026abc2def632f6e456f80230902f93e2bea13\",\n"
                        + "      \"toView\": 54477\n"
                        + "    },\n"
                        + "    [\n"
                        + "      {\n"
                        + "        \"nodeId\": \"6a99f357ecf8a001e03b68aba66f68398ee08f3ce0f0147e777ec77995369aac470b8c9f0f85f91ebb58a98475764b7ca1be8e37637dd6cb80b3355749636a3d\",\n"
                        + "        \"view\": 54474\n"
                        + "      },\n"
                        + "      {\n"
                        + "        \"nodeId\": \"8a453f1328c80b908b2d02ba25adca6341b16b16846d84f903c4f4912728c6aae1050ce4f24cd9c13e010ce922d3393b846f6f5c42f6af59c65a814de733afe4\",\n"
                        + "        \"view\": 54475\n"
                        + "      },\n"
                        + "      {\n"
                        + "        \"nodeId\": \"ed483837e73ee1b56073b178f5ac0896fa328fc0ed418ae3e268d9e9109721421ec48d68f28d6525642868b40dd26555c9148dbb8f4334ca071115925132889c\",\n"
                        + "        \"view\": 54476\n"
                        + "      },\n"
                        + "      {\n"
                        + "        \"nodeId\": \"f72648fe165da17a889bece08ca0e57862cb979c4e3661d6a77bcc2de85cb766af5d299fec8a4337eedd142dca026abc2def632f6e456f80230902f93e2bea13\",\n"
                        + "        \"view\": 54477\n"
                        + "      }\n"
                        + "    ]\n"
                        + "  ]\n"
                        + "}";
        ConsensusStatus status =
                objectMapper.readValue(pbftConsensusStatusString.getBytes(), ConsensusStatus.class);
        Assert.assertEquals("2.0", status.getJsonrpc());
        Assert.assertEquals(1, status.getId());
        ConsensusStatus.BasicConsensusInfo basicConsensusInfo =
                status.getConsensusStatus().getBaseConsensusInfo();
        Assert.assertEquals(
                "6a99f357ecf8a001e03b68aba66f68398ee08f3ce0f0147e777ec77995369aac470b8c9f0f85f91ebb58a98475764b7ca1be8e37637dd6cb80b3355749636a3d",
                basicConsensusInfo.getSealerList().get(0).toString());
        Assert.assertEquals("1", basicConsensusInfo.getAccountType());
        Assert.assertEquals("4", basicConsensusInfo.getNodeNum());
        Assert.assertEquals(4, basicConsensusInfo.getSealerList().size());
        Assert.assertEquals(
                "f72648fe165da17a889bece08ca0e57862cb979c4e3661d6a77bcc2de85cb766af5d299fec8a4337eedd142dca026abc2def632f6e456f80230902f93e2bea13",
                basicConsensusInfo.getNodeId());
        Assert.assertEquals("54477", basicConsensusInfo.getCurrentView());
        Assert.assertEquals("1", basicConsensusInfo.getGroupId());
        Assert.assertEquals("38206", basicConsensusInfo.getHighestblockNumber());
        Assert.assertEquals(
                "0x19a16e8833e671aa11431de589c866a6442ca6c8548ba40a44f50889cd785069",
                basicConsensusInfo.getHighestblockHash());
        Assert.assertEquals("38206", basicConsensusInfo.getHighestblockNumber());
        Assert.assertEquals("false", basicConsensusInfo.getLeaderFailed());
        Assert.assertEquals("1", basicConsensusInfo.getMaxFaultyNodeNum());
        Assert.assertEquals("3", basicConsensusInfo.getNodeIndex());
        Assert.assertEquals("true", basicConsensusInfo.getOmitEmptyBlock());
        Assert.assertEquals("65544", basicConsensusInfo.getProtocolId());
        Assert.assertEquals("54477", basicConsensusInfo.getToView());

        // check ViewInfo
        List<ConsensusStatus.ViewInfo> viewInfoList = status.getConsensusStatus().getViewInfos();
        Assert.assertEquals(4, viewInfoList.size());
        Assert.assertEquals(
                "6a99f357ecf8a001e03b68aba66f68398ee08f3ce0f0147e777ec77995369aac470b8c9f0f85f91ebb58a98475764b7ca1be8e37637dd6cb80b3355749636a3d",
                viewInfoList.get(0).getNodeId());
        Assert.assertEquals("54474", viewInfoList.get(0).getView());
        Assert.assertEquals("54475", viewInfoList.get(1).getView());
        Assert.assertEquals("54476", viewInfoList.get(2).getView());
        Assert.assertEquals("54477", viewInfoList.get(3).getView());

        ConsensusStatus status2 =
                objectMapper.readValue(pbftConsensusStatusString.getBytes(), ConsensusStatus.class);
        Assert.assertEquals(status.getConsensusStatus(), status2.getConsensusStatus());
        Assert.assertEquals(
                status.getConsensusStatus().hashCode(), status2.getConsensusStatus().hashCode());
    }

    @Test
    public void testRaftConsensusStatus() throws IOException {
        String raftConsenusStatus =
                "{\n"
                        + "  \"id\": 1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": [\n"
                        + "    {\n"
                        + "      \"accountType\": 1,\n"
                        + "      \"allowFutureBlocks\": true,\n"
                        + "      \"cfgErr\": false,\n"
                        + "      \"consensusedBlockNumber\": 1,\n"
                        + "      \"groupId\": 1,\n"
                        + "      \"highestblockHash\": \"0x4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2\",\n"
                        + "      \"highestblockNumber\": 0,\n"
                        + "      \"leaderId\": \"d5b3a9782c6aca271c9642aea391415d8b258e3a6d92082e59cc5b813ca123745440792ae0b29f4962df568f8ad58b75fc7cea495684988e26803c9c5198f3f8\",\n"
                        + "      \"leaderIdx\": 3,\n"
                        + "      \"max_faulty_leader\": 1,\n"
                        + "      \"sealer.0\": \"29c34347a190c1ec0c4507c6eed6a5bcd4d7a8f9f54ef26da616e81185c0af11a8cea4eacb74cf6f61820292b24bc5d9e426af24beda06fbd71c217960c0dff0\",\n"
                        + "      \"sealer.1\": \"41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba\",\n"
                        + "      \"sealer.2\": \"87774114e4a496c68f2482b30d221fa2f7b5278876da72f3d0a75695b81e2591c1939fc0d3fadb15cc359c997bafc9ea6fc37345346acaf40b6042b5831c97e1\",\n"
                        + "      \"sealer.3\": \"d5b3a9782c6aca271c9642aea391415d8b258e3a6d92082e59cc5b813ca123745440792ae0b29f4962df568f8ad58b75fc7cea495684988e26803c9c5198f3f8\",\n"
                        + "      \"node index\": 1,\n"
                        + "      \"nodeId\": \"41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba\",\n"
                        + "      \"nodeNum\": 4,\n"
                        + "      \"omitEmptyBlock\": true,\n"
                        + "      \"protocolId\": 267\n"
                        + "    }\n"
                        + "  ]\n"
                        + "}";
        ConsensusStatus status =
                objectMapper.readValue(raftConsenusStatus.getBytes(), ConsensusStatus.class);
        ConsensusStatus.BasicConsensusInfo basicConsensusInfo =
                status.getConsensusStatus().getBaseConsensusInfo();
        Assert.assertEquals("1", basicConsensusInfo.getAccountType());
        Assert.assertEquals("true", basicConsensusInfo.getAllowFutureBlocks());
        Assert.assertEquals(
                "d5b3a9782c6aca271c9642aea391415d8b258e3a6d92082e59cc5b813ca123745440792ae0b29f4962df568f8ad58b75fc7cea495684988e26803c9c5198f3f8",
                basicConsensusInfo.getLeaderId());
        Assert.assertEquals("3", basicConsensusInfo.getLeaderIdx());
        Assert.assertEquals("267", basicConsensusInfo.getProtocolId());
        Assert.assertEquals(4, basicConsensusInfo.getSealerList().size());
        Assert.assertEquals("1", basicConsensusInfo.getMaxFaultyNodeNum());
        Assert.assertEquals("1", basicConsensusInfo.getRaftNodeIndex());
        Assert.assertEquals(null, status.getConsensusStatus().getViewInfos());
    }

    @Test
    public void testGroupStatus() throws IOException {
        String groupStatusStr =
                "{\n"
                        + "  \"id\": 1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": {\n"
                        + "    \"code\": \"0x0\",\n"
                        + "    \"message\": \"\",\n"
                        + "    \"status\": \"STOPPED\"\n"
                        + "  }\n"
                        + "}";
        // test generateGropu
        GroupStatus groupStatus =
                objectMapper
                        .readValue(groupStatusStr.getBytes(), GenerateGroup.class)
                        .getGroupStatus();
        checkGroupStatus(groupStatus, "0x0", "", "STOPPED");

        groupStatus =
                objectMapper
                        .readValue(groupStatusStr.getBytes(), StartGroup.class)
                        .getGroupStatus();
        checkGroupStatus(groupStatus, "0x0", "", "STOPPED");

        groupStatus =
                objectMapper.readValue(groupStatusStr.getBytes(), StopGroup.class).getGroupStatus();
        checkGroupStatus(groupStatus, "0x0", "", "STOPPED");

        groupStatus =
                objectMapper
                        .readValue(groupStatusStr.getBytes(), RecoverGroup.class)
                        .getGroupStatus();
        checkGroupStatus(groupStatus, "0x0", "", "STOPPED");
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
    public void testGroupList() throws IOException {
        String groupListStr =
                "{\n"
                        + "    \"id\": 1,\n"
                        + "    \"jsonrpc\": \"2.0\",\n"
                        + "    \"result\": [1,2,3]\n"
                        + "}";
        GroupList groupList = objectMapper.readValue(groupListStr.getBytes(), GroupList.class);
        Assert.assertEquals(3, groupList.getGroupList().size());
        List<String> parsedGroupList = Arrays.asList("1", "2", "3");
        Assert.assertTrue(groupList.getGroupList().equals(parsedGroupList));

        // encode
        byte[] encodedBytes = objectMapper.writeValueAsBytes(groupList.getGroupList());
        List<String> decodedGroupList =
                objectMapper.readValue(encodedBytes, new TypeReference<List<String>>() {});
        encodedBytes = objectMapper.writeValueAsBytes(decodedGroupList);

        Assert.assertEquals(groupList.getGroupList(), decodedGroupList);
        Assert.assertEquals(groupList.getGroupList().hashCode(), decodedGroupList.hashCode());
    }

    @Test
    public void testGroupPeers() throws IOException {
        String groupPeersStr =
                "{\n"
                        + "    \"id\": 1,\n"
                        + "    \"jsonrpc\": \"2.0\",\n"
                        + "    \"result\": [\n"
                        + "        \"0c0bbd25152d40969d3d3cee3431fa28287e07cff2330df3258782d3008b876d146ddab97eab42796495bfbb281591febc2a0069dcc7dfe88c8831801c5b5801\",\n"
                        + "        \"037c255c06161711b6234b8c0960a6979ef039374ccc8b723afea2107cba3432dbbc837a714b7da20111f74d5a24e91925c773a72158fa066f586055379a1772\",\n"
                        + "        \"622af37b2bd29c60ae8f15d467b67c0a7fe5eb3e5c63fdc27a0ee8066707a25afa3aa0eb5a3b802d3a8e5e26de9d5af33806664554241a3de9385d3b448bcd73\",\n"
                        + "        \"10b3a2d4b775ec7f3c2c9e8dc97fa52beb8caab9c34d026db9b95a72ac1d1c1ad551c67c2b7fdc34177857eada75836e69016d1f356c676a6e8b15c45fc9bc34\"\n"
                        + "    ]\n"
                        + "}";
        GroupPeers groupPeers = objectMapper.readValue(groupPeersStr.getBytes(), GroupPeers.class);
        Assert.assertEquals(4, groupPeers.getGroupPeers().size());
        Assert.assertEquals(
                "0c0bbd25152d40969d3d3cee3431fa28287e07cff2330df3258782d3008b876d146ddab97eab42796495bfbb281591febc2a0069dcc7dfe88c8831801c5b5801",
                groupPeers.getGroupPeers().get(0));
        Assert.assertEquals(
                "10b3a2d4b775ec7f3c2c9e8dc97fa52beb8caab9c34d026db9b95a72ac1d1c1ad551c67c2b7fdc34177857eada75836e69016d1f356c676a6e8b15c45fc9bc34",
                groupPeers.getGroupPeers().get(3));
    }

    @Test
    public void testNodeIDList() throws IOException {
        String nodeIdListStr =
                "{\n"
                        + "    \"id\": 1,\n"
                        + "    \"jsonrpc\": \"2.0\",\n"
                        + "    \"result\": [\n"
                        + "        \"0c0bbd25152d40969d3d3cee3431fa28287e07cff2330df3258782d3008b876d146ddab97eab42796495bfbb281591febc2a0069dcc7dfe88c8831801c5b5801\",\n"
                        + "        \"037c255c06161711b6234b8c0960a6979ef039374ccc8b723afea2107cba3432dbbc837a714b7da20111f74d5a24e91925c773a72158fa066f586055379a1772\",\n"
                        + "        \"622af37b2bd29c60ae8f15d467b67c0a7fe5eb3e5c63fdc27a0ee8066707a25afa3aa0eb5a3b802d3a8e5e26de9d5af33806664554241a3de9385d3b448bcd73\",\n"
                        + "        \"10b3a2d4b775ec7f3c2c9e8dc97fa52beb8caab9c34d026db9b95a72ac1d1c1ad551c67c2b7fdc34177857eada75836e69016d1f356c676a6e8b15c45fc9bc34\"\n"
                        + "    ]\n"
                        + "}";
        NodeIDList nodeIDList = objectMapper.readValue(nodeIdListStr.getBytes(), NodeIDList.class);
        Assert.assertEquals(4, nodeIDList.getNodeIDList().size());
        Assert.assertEquals(
                "0c0bbd25152d40969d3d3cee3431fa28287e07cff2330df3258782d3008b876d146ddab97eab42796495bfbb281591febc2a0069dcc7dfe88c8831801c5b5801",
                nodeIDList.getNodeIDList().get(0));
        Assert.assertEquals(
                "622af37b2bd29c60ae8f15d467b67c0a7fe5eb3e5c63fdc27a0ee8066707a25afa3aa0eb5a3b802d3a8e5e26de9d5af33806664554241a3de9385d3b448bcd73",
                nodeIDList.getNodeIDList().get(2));
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
        Assert.assertEquals(3, peers.getPeers().size());
        Assert.assertEquals("127.0.0.1:51869", peers.getPeers().get(0).getIpAndPort());
        Assert.assertEquals(
                "95b7ff064f91de76598f90bc059bec1834f0d9eeb0d05e1086d49af1f9c2f321062d011ee8b0df7644bd54c4f9ca3d8515a3129bbb9d0df8287c9fa69552887e",
                peers.getPeers().get(1).getNodeID());
        Assert.assertTrue(peers.getPeers().get(0).getTopic().isEmpty());
        Assert.assertEquals("node1", peers.getPeers().get(2).getNode());
        Assert.assertEquals("agency", peers.getPeers().get(2).getAgency());
    }

    @Test
    public void testPendingTransactions() throws IOException {
        String pendingListStr =
                "{\n"
                        + "    \"id\": 1,\n"
                        + "    \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": [\n"
                        + "            {\n"
                        + "                \"from\": \"0x6bc952a2e4db9c0c86a368d83e9df0c6ab481102\",\n"
                        + "                \"gas\": \"0x9184e729fff\",\n"
                        + "                \"gasPrice\": \"0x174876e7ff\",\n"
                        + "                \"hash\": \"0x7536cf1286b5ce6c110cd4fea5c891467884240c9af366d678eb4191e1c31c6f\",\n"
                        + "                \"input\": \"0x48f85bce000000000000000000000000000000000000000000000000000000000000001bf5bd8a9e7ba8b936ea704292ff4aaa5797bf671fdc8526dcd159f23c1f5a05f44e9fa862834dc7cb4541558f2b4961dc39eaaf0af7f7395028658d0e01b86a37\",\n"
                        + "                \"nonce\": \"0x65f0d06e39dc3c08e32ac10a5070858962bc6c0f5760baca823f2d5582d03f\",\n"
                        + "                \"to\": \"0xd6f1a71052366dbae2f7ab2d5d5845e77965cf0d\",\n"
                        + "                \"value\": \"0x0\"\n"
                        + "            }\n"
                        + "        ]\n"
                        + "}";
        PendingTransactions pendingTransactions =
                objectMapper.readValue(pendingListStr.getBytes(), PendingTransactions.class);
        Assert.assertEquals(1, pendingTransactions.getPendingTransactions().size());
        Assert.assertEquals(
                "0x7536cf1286b5ce6c110cd4fea5c891467884240c9af366d678eb4191e1c31c6f",
                pendingTransactions.getPendingTransactions().get(0).getHash());
        Assert.assertEquals(
                "0x48f85bce000000000000000000000000000000000000000000000000000000000000001bf5bd8a9e7ba8b936ea704292ff4aaa5797bf671fdc8526dcd159f23c1f5a05f44e9fa862834dc7cb4541558f2b4961dc39eaaf0af7f7395028658d0e01b86a37",
                pendingTransactions.getPendingTransactions().get(0).getInput());
        Assert.assertEquals(
                "0xd6f1a71052366dbae2f7ab2d5d5845e77965cf0d",
                pendingTransactions.getPendingTransactions().get(0).getTo());
        Assert.assertEquals(
                "0x6bc952a2e4db9c0c86a368d83e9df0c6ab481102",
                pendingTransactions.getPendingTransactions().get(0).getFrom());
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
    public void testSendTransaction() throws JsonProcessingException {
        String sendRawTransactionStr =
                "{\n"
                        + "    \"id\": 1,\n"
                        + "    \"jsonrpc\": \"2.0\",\n"
                        + "    \"result\": \"0x0accad4228274b0d78939f48149767883a6e99c95941baa950156e926f1c96ba\"\n"
                        + "}";
        SendTransaction sendTransaction =
                objectMapper.readValue(sendRawTransactionStr, SendTransaction.class);
        Assert.assertEquals(
                "0x0accad4228274b0d78939f48149767883a6e99c95941baa950156e926f1c96ba",
                sendTransaction.getTransactionHash());
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
        Assert.assertEquals("0x0", txCount.getTotalTransactionCount().getFailedTxSum());
        Assert.assertEquals("0x20", txCount.getTotalTransactionCount().getTxSum());
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
                "0x977efec48c248ea4be87016446b40d7785d7b71b7d4e3aa0b103b9cf0f5fe19e",
                transactionReceipt.getTransactionReceipt().get().getBlockHash());
        Assert.assertEquals(
                "0xa", transactionReceipt.getTransactionReceipt().get().getBlockNumber());
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
        Assert.assertEquals(
                "0x38723a2e5e8a17aa7950dc008209944e898f69a7bd10a23c839d341e935fd5ca",
                transactionReceipt.getTransactionReceipt().get().getRoot());
        Assert.assertEquals("0xc", transactionReceipt.getTransactionReceipt().get().getStatus());
        Assert.assertEquals(
                "0x15538acd403ac1b2ff09083c70d04856b8c0bdfd",
                transactionReceipt.getTransactionReceipt().get().getTo());
        Assert.assertEquals(
                "0x708b5781b62166bd86e543217be6cd954fd815fd192b9a124ee9327580df8f3f",
                transactionReceipt.getTransactionReceipt().get().getTransactionHash());
        Assert.assertEquals(
                "0x10", transactionReceipt.getTransactionReceipt().get().getTransactionIndex());
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
        TransactionReceiptWithProof receiptWithProof =
                objectMapper.readValue(receiptWithProofStr, TransactionReceiptWithProof.class);
        Assert.assertEquals(
                3, receiptWithProof.getTransactionReceiptWithProof().getReceiptProof().size());
        Assert.assertEquals(
                2,
                receiptWithProof
                        .getTransactionReceiptWithProof()
                        .getReceiptProof()
                        .get(0)
                        .getLeft()
                        .size());
        Assert.assertEquals(
                13,
                receiptWithProof
                        .getTransactionReceiptWithProof()
                        .getReceiptProof()
                        .get(0)
                        .getRight()
                        .size());
        Assert.assertEquals(
                3,
                receiptWithProof
                        .getTransactionReceiptWithProof()
                        .getReceiptProof()
                        .get(1)
                        .getLeft()
                        .size());
        Assert.assertEquals(
                2,
                receiptWithProof
                        .getTransactionReceiptWithProof()
                        .getReceiptProof()
                        .get(1)
                        .getRight()
                        .size());
        Assert.assertEquals(
                0,
                receiptWithProof
                        .getTransactionReceiptWithProof()
                        .getReceiptProof()
                        .get(2)
                        .getLeft()
                        .size());
        Assert.assertEquals(
                0,
                receiptWithProof
                        .getTransactionReceiptWithProof()
                        .getReceiptProof()
                        .get(2)
                        .getRight()
                        .size());
        Assert.assertEquals(
                "cd46118c0e99be585ffcf50423630348dbc486e54e9d9293a6a8754020a68a92",
                receiptWithProof
                        .getTransactionReceiptWithProof()
                        .getReceiptProof()
                        .get(1)
                        .getLeft()
                        .get(0));
        Assert.assertEquals(
                "6a6cefef8b48e455287a8c8694b06f4f7cb7950017ab048d6e6bdd8029f9f8c9",
                receiptWithProof
                        .getTransactionReceiptWithProof()
                        .getReceiptProof()
                        .get(1)
                        .getRight()
                        .get(0));
        // check receipt
        Assert.assertEquals(
                "0xcd31b05e466bce99460b1ed70d6069fdfbb15e6eef84e9b9e4534358edb3899a",
                receiptWithProof.getTransactionReceiptWithProof().getReceipt().getBlockHash());
        Assert.assertEquals(
                "0x5",
                receiptWithProof.getTransactionReceiptWithProof().getReceipt().getBlockNumber());
        Assert.assertEquals(
                "0x0000000000000000000000000000000000000000",
                receiptWithProof
                        .getTransactionReceiptWithProof()
                        .getReceipt()
                        .getContractAddress());
        Assert.assertEquals(
                "0x148947262ec5e21739fe3a931c29e8b84ee34a0f",
                receiptWithProof.getTransactionReceiptWithProof().getReceipt().getFrom());
        Assert.assertEquals(
                "0x21dc1b",
                receiptWithProof.getTransactionReceiptWithProof().getReceipt().getGasUsed());
        Assert.assertEquals(
                "0xc3b4185963c78a4ca8eb90240e5cd95371d7217a9ce2bfa1149d53f79c73afbb",
                receiptWithProof.getTransactionReceiptWithProof().getReceipt().getRoot());
        Assert.assertEquals(
                "0x0", receiptWithProof.getTransactionReceiptWithProof().getReceipt().getStatus());
        Assert.assertEquals(
                "0xd6c8a04b8826b0a37c6d4aa0eaa8644d8e35b79f",
                receiptWithProof.getTransactionReceiptWithProof().getReceipt().getTo());
        Assert.assertEquals(
                null, receiptWithProof.getTransactionReceiptWithProof().getReceipt().getTxProof());
        Assert.assertEquals(
                null,
                receiptWithProof.getTransactionReceiptWithProof().getReceipt().getReceiptProof());
    }

    @Test
    public void testTransactionWithProof() throws IOException {
        String transactionWithProofStr =
                "{\n"
                        + "  \"id\": 1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": {\n"
                        + "    \"transaction\": {\n"
                        + "      \"blockHash\": \"0xcd31b05e466bce99460b1ed70d6069fdfbb15e6eef84e9b9e4534358edb3899a\",\n"
                        + "      \"blockNumber\": \"0x5\",\n"
                        + "      \"from\": \"0x148947262ec5e21739fe3a931c29e8b84ee34a0f\",\n"
                        + "      \"gas\": \"0x1c9c380\",\n"
                        + "      \"gasPrice\": \"0x1c9c380\",\n"
                        + "      \"hash\": \"0xd2c12e211315ef09dbad53407bc820d062780232841534954f9c23ab11d8ab4c\",\n"
                        + "      \"input\": \"0x8a42ebe90000000000000000000000000000000000000000000000000000000000000040000000000000000000000000000000000000000000000000000000003b9aca00000000000000000000000000000000000000000000000000000000000000000a3564646636663863653800000000000000000000000000000000000000000000\",\n"
                        + "      \"nonce\": \"0x208f6fd78d48aad370df51c6fdf866f8ab022de765c2959841ff2e81bfd9af9\",\n"
                        + "      \"to\": \"0xd6c8a04b8826b0a37c6d4aa0eaa8644d8e35b79f\",\n"
                        + "      \"transactionIndex\": \"0x32\",\n"
                        + "      \"value\": \"0x0\"\n"
                        + "    },\n"
                        + "    \"txProof\": [\n"
                        + "      {\n"
                        + "        \"left\": [\n"
                        + "          \"30f0abfcf4ca152815548620e33d21fd0feaa7c78867791c751e57cb5aa38248c2\",\n"
                        + "          \"31a864156ca9841da8176738bb981d5da9102d9703746039b3e5407fa987e5183e\"\n"
                        + "        ],\n"
                        + "        \"right\": [\n"
                        + "          \"33d8078d7e71df3544f8845a9db35aa35b2638e8468a321423152e64b9004367b4\",\n"
                        + "          \"34343a4bce325ec8f6cf48517588830cd15f69b60a05598b78b03c3656d1fbf2f5\",\n"
                        + "          \"35ac231554047ce77c0b31cd1c469f1f39ebe23404fa8ff6cc7819ad83e2c029e7\",\n"
                        + "          \"361f6c588e650323e03afe6460dd89a9c061583e0d62c117ba64729d2c9d79317c\",\n"
                        + "          \"377606f79f3e08b1ba3759eceada7fde3584f01822467855aa6356652f2499c738\",\n"
                        + "          \"386722fe270659232c5572ba54ce23b474c85d8b709e7c08e85230afb1c155fe18\",\n"
                        + "          \"39a9441d668e5e09a5619c365577c8c31365f44a984bde04300d4dbba190330c0b\",\n"
                        + "          \"3a78a8c288120cbe612c24a33cce2731dd3a8fe6927d9ee25cb2350dba08a541f5\",\n"
                        + "          \"3bd9b67256e201b5736f6081f39f83bcb917261144384570bdbb8766586c3bb417\",\n"
                        + "          \"3c3158e5a82a1ac1ed41c4fd78d5be06bf79327f60b094895b886e7aae57cff375\",\n"
                        + "          \"3de9a4d98c5ae658ffe764fbfa81edfdd4774e01b35ccb42beacb67064a5457863\",\n"
                        + "          \"3e525e60c0f7eb935125f1156a692eb455ab4038c6b16390ce30937b0d1b314298\",\n"
                        + "          \"3f1600afe67dec2d21582b8c7b76a15e569371d736d7bfc7a96c0327d280b91dfc\"\n"
                        + "        ]\n"
                        + "      },\n"
                        + "      {\n"
                        + "        \"left\": [\n"
                        + "          \"3577673b86ad4d594d86941d731f17d1515f4669483aed091d49f279d677cb19\",\n"
                        + "          \"75603bfea5b44df4c41fbb99268364641896334f006af3a3f67edaa4b26477ca\",\n"
                        + "          \"1339d43c526f0f34d8a0f4fb3bb47b716fdfde8d35697be5992e0888e4d794c9\"\n"
                        + "        ],\n"
                        + "        \"right\": [\n"
                        + "          \"63c8e574fb2ef52e995427a8acaa72c27073dd8e37736add8dbf36be4f609ecb\",\n"
                        + "          \"e65353d911d6cc8ead3fad53ab24cab69a1e31df8397517b124f578ba908558d\"\n"
                        + "        ]\n"
                        + "      },\n"
                        + "      {\n"
                        + "        \"left\": [],\n"
                        + "        \"right\": []\n"
                        + "      }\n"
                        + "    ]\n"
                        + "  }\n"
                        + "}";
        TransactionWithProof transactionWithProof =
                objectMapper.readValue(
                        transactionWithProofStr.getBytes(), TransactionWithProof.class);
        Assert.assertEquals(
                3, transactionWithProof.getTransactionWithProof().getTransactionProof().size());
        Assert.assertEquals(
                2,
                transactionWithProof
                        .getTransactionWithProof()
                        .getTransactionProof()
                        .get(0)
                        .getLeft()
                        .size());
        Assert.assertEquals(
                13,
                transactionWithProof
                        .getTransactionWithProof()
                        .getTransactionProof()
                        .get(0)
                        .getRight()
                        .size());

        Assert.assertEquals(
                3,
                transactionWithProof
                        .getTransactionWithProof()
                        .getTransactionProof()
                        .get(1)
                        .getLeft()
                        .size());
        Assert.assertEquals(
                2,
                transactionWithProof
                        .getTransactionWithProof()
                        .getTransactionProof()
                        .get(1)
                        .getRight()
                        .size());

        Assert.assertEquals(
                0,
                transactionWithProof
                        .getTransactionWithProof()
                        .getTransactionProof()
                        .get(2)
                        .getLeft()
                        .size());
        Assert.assertEquals(
                0,
                transactionWithProof
                        .getTransactionWithProof()
                        .getTransactionProof()
                        .get(2)
                        .getRight()
                        .size());

        Assert.assertEquals(
                "3577673b86ad4d594d86941d731f17d1515f4669483aed091d49f279d677cb19",
                transactionWithProof
                        .getTransactionWithProof()
                        .getTransactionProof()
                        .get(1)
                        .getLeft()
                        .get(0));
        Assert.assertEquals(
                "63c8e574fb2ef52e995427a8acaa72c27073dd8e37736add8dbf36be4f609ecb",
                transactionWithProof
                        .getTransactionWithProof()
                        .getTransactionProof()
                        .get(1)
                        .getRight()
                        .get(0));

        // check transaction
        Assert.assertEquals(
                "0xcd31b05e466bce99460b1ed70d6069fdfbb15e6eef84e9b9e4534358edb3899a",
                transactionWithProof.getTransactionWithProof().getTransaction().getBlockHash());
        Assert.assertEquals(
                BigInteger.valueOf(0x5),
                transactionWithProof.getTransactionWithProof().getTransaction().getBlockNumber());
        Assert.assertEquals(
                "0x148947262ec5e21739fe3a931c29e8b84ee34a0f",
                transactionWithProof.getTransactionWithProof().getTransaction().getFrom());
        Assert.assertEquals(
                "0x1c9c380",
                transactionWithProof.getTransactionWithProof().getTransaction().getGas());
        Assert.assertEquals(
                "0x1c9c380",
                transactionWithProof.getTransactionWithProof().getTransaction().getGasPrice());
        Assert.assertEquals(
                "0xd2c12e211315ef09dbad53407bc820d062780232841534954f9c23ab11d8ab4c",
                transactionWithProof.getTransactionWithProof().getTransaction().getHash());
        Assert.assertEquals(
                "0x8a42ebe90000000000000000000000000000000000000000000000000000000000000040000000000000000000000000000000000000000000000000000000003b9aca00000000000000000000000000000000000000000000000000000000000000000a3564646636663863653800000000000000000000000000000000000000000000",
                transactionWithProof.getTransactionWithProof().getTransaction().getInput());
        Assert.assertEquals(
                "0x208f6fd78d48aad370df51c6fdf866f8ab022de765c2959841ff2e81bfd9af9",
                transactionWithProof.getTransactionWithProof().getTransaction().getNonce());
        Assert.assertEquals(
                "0xd6c8a04b8826b0a37c6d4aa0eaa8644d8e35b79f",
                transactionWithProof.getTransactionWithProof().getTransaction().getTo());
        Assert.assertEquals(
                "0x32",
                transactionWithProof
                        .getTransactionWithProof()
                        .getTransaction()
                        .getTransactionIndex());
        Assert.assertEquals(
                "0x0", transactionWithProof.getTransactionWithProof().getTransaction().getValue());

    }

    @Test
    public void testSMGetTransactionAndCalculateHash() throws IOException {
        String transactionString = "{\n" +
                "  \"id\": 1,\n" +
                "  \"jsonrpc\": \"2.0\",\n" +
                "  \"result\": {\n" +
                "    \"blockHash\": \"0x2ff860cf49b95f721398b78a128617bf62ab03e09002895c4926f7be990615f1\",\n" +
                "    \"blockLimit\": \"0x38e\",\n" +
                "    \"blockNumber\": \"0x19b\",\n" +
                "    \"chainId\": \"0x1\",\n" +
                "    \"extraData\": \"0x\",\n" +
                "    \"from\": \"0x37e6cd2081a11c345fac93eaff0ca9ef66f27451\",\n" +
                "    \"gas\": \"0x419ce0\",\n" +
                "    \"gasPrice\": \"0x51f4d5c00\",\n" +
                "    \"groupId\": \"0x1\",\n" +
                "    \"hash\": \"0x880ee49599e731086d44d268239bce2e36a1b1032329bcd3f194b2e86297caf4\",\n" +
                "    \"input\": \"0x3590b49f0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000c48656c6c6f2c20464953434f0000000000000000000000000000000000000000\",\n" +
                "    \"nonce\": \"0x22cbceaa80e80cf1aa6c719b659601f2ae6ed68d549c537be57b44bc7668405\",\n" +
                "    \"signature\": {\n" +
                "      \"r\": \"0xcc108436f41e5ee91491f5e91bd72f1bdc43f6169d2b72bf96c7cf6f32702540\",\n" +
                "      \"s\": \"0x94eec76fe9d7902a9d328ec169329820914b7720d675c657356fd68f4758108f\",\n" +
                "      \"signature\": \"0xcc108436f41e5ee91491f5e91bd72f1bdc43f6169d2b72bf96c7cf6f3270254094eec76fe9d7902a9d328ec169329820914b7720d675c657356fd68f4758108f6feaf705e8b16de494b4fec3ec3176e38b1eaa416605f4bb5c141c2a22434580f03b8257b29213bdc059c9b3673a7c3868df55eb1b85c2abc22aae64e4d9cac6\",\n" +
                "      \"v\": \"0x6feaf705e8b16de494b4fec3ec3176e38b1eaa416605f4bb5c141c2a22434580f03b8257b29213bdc059c9b3673a7c3868df55eb1b85c2abc22aae64e4d9cac6\"\n" +
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
                "    \"receiptsRoot\": \"0xd748b478e6b8f90e049f7a4a9d2b9acf76624baed8c2abe0e868b33cd5e989e5\",\n" +
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
                "    \"receiptsRoot\": \"0x69a04fa6073e4fc0947bac7ee6990e788d1e2c5ec0fe6c2436d0892e7f3c09d2\",\n" +
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

    @Test
    public void testCompressedBatchReceipts() throws IOException {
        String receiptListStr = "{\n" +
                "  \"id\": 1,\n" +
                "  \"jsonrpc\": \"2.0\",\n" +
                "  \"result\": {\n" +
                "    \"blockInfo\": {\n" +
                "      \"blockHash\": \"0x9a3c01559f63f17739db7159ebe945da0105f15144c97cc0a18e5389f07a9fdb\",\n" +
                "      \"blockNumber\": \"0x1\",\n" +
                "      \"receiptRoot\": \"0x1e9ee115d0a0ed6a248f5a711e5e8a2f93c82b8d70922254efcec2b4bbd9d174\",\n" +
                "      \"receiptsCount\": \"0x1\"\n" +
                "    },\n" +
                "    \"transactionReceipts\": [\n" +
                "      {\n" +
                "        \"contractAddress\": \"0x7c6dc94e4e146cb13eb03dc98d2b96ac79ef5e67\",\n" +
                "        \"from\": \"0x6fad87071f790c3234108f41b76bb99874a6d813\",\n" +
                "        \"gasUsed\": \"0x44ab3\",\n" +
                "        \"logs\": [],\n" +
                "        \"output\": \"0x\",\n" +
                "        \"status\": \"0x0\",\n" +
                "        \"to\": \"0x0000000000000000000000000000000000000000\",\n" +
                "        \"transactionHash\": \"0x066dce3c06b9d74881e0996172717cfcba4330206243a9f964eb3097f3696e27\",\n" +
                "        \"transactionIndex\": \"0x0\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        BcosTransactionReceiptsInfo bcosTransactionReceiptsInfo = objectMapper.readValue(receiptListStr.getBytes(), BcosTransactionReceiptsInfo.class);
        checkReceipts(bcosTransactionReceiptsInfo.getTransactionReceiptsInfo());

        String compressedReceiptStr = "{\n" +
                "  \"id\": 1,\n" +
                "  \"jsonrpc\": \"2.0\",\n" +
                "  \"result\": \"eJyNUT1PZDEM7O9npN4iTpw43u5EA80VSFedKOLE4RDwgt6HhLTa/35h3wpEd67iiWc81pyMvPTyfDe1bo6nvbnNy19zNPadsy8WQuAWfQMiz1UIAqsoY6jZgg0NAiAWplJshqTBJ26WMrcq5rAL/tpeReeLJAxs1qJPb+t97+uOKasChGqz1Rqzw9RCJgANmrJr7EtykipZds4F1Fa0OEGRyhUIvySXm75NV1FzPph1ztOSy/rUp/vrhDn+OZnSp/FV1p+1zrosFwKVWAujogLGIuBVrB9Iqk445kKsLWiksa3N/fXCiS3XRJagEdvinUewqSEIRRHmRJhjTeAH5zEvvxetFxpilg/spT9++Hk4mL6tb9tufODLmtdtd2VHu/b9+Z9lvt39GaaN4z4dgUbhSpgSqGWOQI6ASiuS0XvrbHToR3ocUcVbpuYjR3X0XfZuqvp+dXh+OP/4B2fCqOE=\"\n" +
                "}";
        BcosTransactionReceiptsDecoder bcosTransactionReceiptsDecoder= objectMapper.readValue(compressedReceiptStr.getBytes(), BcosTransactionReceiptsDecoder.class);
        checkReceipts(bcosTransactionReceiptsDecoder.decodeTransactionReceiptsInfo());
    }

    private void checkReceipts(BcosTransactionReceiptsInfo.TransactionReceiptsInfo receiptsInfo)
    {
        BcosTransactionReceiptsInfo.BlockInfo blockInfo = receiptsInfo.getBlockInfo();
        // check block info
        Assert.assertEquals("0x9a3c01559f63f17739db7159ebe945da0105f15144c97cc0a18e5389f07a9fdb", blockInfo.getBlockHash()
        );
        Assert.assertEquals("0x1", blockInfo.getBlockNumber());
        Assert.assertEquals("0x1e9ee115d0a0ed6a248f5a711e5e8a2f93c82b8d70922254efcec2b4bbd9d174", blockInfo.getReceiptRoot());
        Assert.assertEquals("0x1", blockInfo.getReceiptsCount());

        // check receipts
        List<TransactionReceipt> receipts = receiptsInfo.getTransactionReceipts();
        Assert.assertTrue(receipts.size() == 1);
        Assert.assertEquals("0x7c6dc94e4e146cb13eb03dc98d2b96ac79ef5e67", receipts.get(0).getContractAddress());
        Assert.assertEquals("0x6fad87071f790c3234108f41b76bb99874a6d813", receipts.get(0).getFrom());
        Assert.assertEquals("0x44ab3", receipts.get(0).getGasUsed());
        Assert.assertEquals("0x", receipts.get(0).getOutput());
        Assert.assertEquals("0x0", receipts.get(0).getStatus());
        Assert.assertEquals("0x0000000000000000000000000000000000000000", receipts.get(0).getTo());
        Assert.assertEquals("0x066dce3c06b9d74881e0996172717cfcba4330206243a9f964eb3097f3696e27", receipts.get(0).getTransactionHash());
        Assert.assertEquals("0x0", receipts.get(0).getTransactionIndex());
        Assert.assertTrue( receipts.get(0).getLogs().size() == 0);
    }
}
