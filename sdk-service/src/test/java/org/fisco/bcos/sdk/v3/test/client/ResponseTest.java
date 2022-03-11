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
package org.fisco.bcos.sdk.v3.test.client;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fisco.bcos.sdk.v3.client.protocol.model.GroupStatus;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosBlock;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosBlockHeader;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosTransaction;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosTransactionReceipt;
import org.fisco.bcos.sdk.v3.client.protocol.response.BlockHash;
import org.fisco.bcos.sdk.v3.client.protocol.response.BlockNumber;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.client.protocol.response.ObserverList;
import org.fisco.bcos.sdk.v3.client.protocol.response.PbftView;
import org.fisco.bcos.sdk.v3.client.protocol.response.Peers;
import org.fisco.bcos.sdk.v3.client.protocol.response.PendingTxSize;
import org.fisco.bcos.sdk.v3.client.protocol.response.SealerList;
import org.fisco.bcos.sdk.v3.client.protocol.response.SyncStatus;
import org.fisco.bcos.sdk.v3.client.protocol.response.SystemConfig;
import org.fisco.bcos.sdk.v3.client.protocol.response.TotalTransactionCount;
import org.fisco.bcos.sdk.v3.client.protocol.response.Code;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.NodeVersion;
import org.fisco.bcos.sdk.v3.utils.ObjectMapperFactory;
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
                        + "    \"extraData\": \"\",\n"
                        + "    \"gasLimit\": \"0x100\",\n"
                        + "    \"gasUsed\": \"0x200\",\n"
                        + "    \"hash\": \"0xc558dd020df46dd3c2753dc8e1f85b79bf7849005dd4b84e3c8b5c1f6f642a82\",\n"
                        + "    \"logsBloom\": \"0x0000abc123\",\n"
                        + "    \"number\": 1,\n"
                        + "    \"receiptRoot\": \"0x69a04fa6073e4fc0947bac7ee6990e788d1e2c5ec0fe6c2436d0892e7f3c09d2\",\n"
                        + "    \"sealer\": \"3\",\n"
                        + "    \"sealerList\": [\n"
                        + "      \"11e1be251ca08bb44f36fdeedfaeca40894ff80dfd80084607a75509edeaf2a9c6fee914f1e9efda571611cf4575a1577957edfd2baa9386bd63eb034868625f\",\n"
                        + "      \"b8acb51b9fe84f88d670646be36f31c52e67544ce56faf3dc8ea4cf1b0ebff0864c6b218fdcd9cf9891ebd414a995847911bd26a770f429300085f37e1131f36\"\n"
                        + "    ],\n"
                        + "    \"signatureList\": [\n"
                        + "      {\n"
                        + "        \"index\": \"0\",\n"
                        + "        \"signature\": \"0x2e491c54f75c3b501745e1b2c92898f9434751326a17e4bcd37b93d5930405e14a461cff9ea7857da33fbf8b5ae6450ff0e281953553193aefb298b66d45b38401\"\n"
                        + "      },\n"
                        + "      {\n"
                        + "        \"index\": \"3\",\n"
                        + "        \"signature\": \"0x5cf59da9e2f580f0b62f8a43f0debe85a209a1c0e3bf66da58913841cb0daf50439baf9807c63a2cebcbac72a5ba679447a9101e39f7c08f1634ca5c99da970c01\"\n"
                        + "      }\n"
                        + "    ],\n"
                        + "    \"stateRoot\": \"0x000000000000000000000000000000000000000000000000000c000000000000\",\n"
                        + "    \"timestamp\": \"0x1736f190efb\",\n"
                        + "    \"txsRootCoped\": \"0x9eec1be2effb2d7934928d4ccab1bd2886b920b1cf29f8744e3be1d253102cd7\",\n"
                        + "    \"txsRoot\": \"0x9eec1be2effb2d7934928d4ccab1bd2886b920b1cf29f8744e3be1d253102cd7\"\n"
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
                    "0x69a04fa6073e4fc0947bac7ee6990e788d1e2c5ec0fe6c2436d0892e7f3c09d2",
                    blockHeader.getBlockHeader().getReceiptsRoot());
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
    public void testTransaction() throws IOException {
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
                "{\n" +
                        "  \"id\": 5,\n" +
                        "  \"jsonrpc\": \"2.0\",\n" +
                        "  \"result\": {\n" +
                        "    \"consensusWeights\": [\n" +
                        "      1\n" +
                        "    ],\n" +
                        "    \"extraData\": \"0x\",\n" +
                        "    \"gasUsed\": \"36488\",\n" +
                        "    \"hash\": \"0xaa3fb2b657db63ca437f9b862bab1a5e06bb0be6281cd78bf51373beafc97f5b\",\n" +
                        "    \"number\": 1,\n" +
                        "    \"parentInfo\": [\n" +
                        "      {\n" +
                        "        \"blockHash\": \"0x3e05e34a36cad0836483101667a9ed1822a7810f848979ce2a38444a222e029c\",\n" +
                        "        \"blockNumber\": 0\n" +
                        "      }\n" +
                        "    ],\n" +
                        "    \"receiptsRoot\": \"0x0000000000000000000000000000000000000000000000000000000000000000\",\n" +
                        "    \"sealer\": 0,\n" +
                        "    \"sealerList\": [\n" +
                        "      \"0x751bbcec9ab8fc8a8ecc9da7827e40a7f1b710801b3090cf06f846198b1ad0a0baff615a2624c00d2bf1f48bd29fbe509982130eb8c22debca2cacc8125b551e\"\n" +
                        "    ],\n" +
                        "    \"signatureList\": [\n" +
                        "      {\n" +
                        "        \"sealerIndex\": 0,\n" +
                        "        \"signature\": \"0x8427bf9a5f3081dc4ede863d85ccccb22b870d56be63fbac110e20b9473e5f822486238fe39faf732f902bdad895c437ddf95037e46e37dbf06f0aee8bb14ad100\"\n" +
                        "      }\n" +
                        "    ],\n" +
                        "    \"stateRoot\": \"0x6a061fd2b32d4384df1d2e1897286f6dbbc139a3383569cbd940d0d990f66604\",\n" +
                        "    \"timestamp\": 1637579843630,\n" +
                        "    \"transactions\": [\n" +
                        "      {\n" +
                        "        \"blockLimit\": 500,\n" +
                        "        \"chainID\": \"chain\",\n" +
                        "        \"from\": \"0x2d6300a8f067872ebc87252d711b83a0c9325d35\",\n" +
                        "        \"groupID\": \"group\",\n" +
                        "        \"hash\": \"0x24e190d013390901562265e4e3158dbb392c83e50e7cfa394d56d4afac4536a7\",\n" +
                        "        \"importTime\": 0,\n" +
                        "        \"input\": \"0x608060405234801561001057600080fd5b506040518060400160405280600d81526020017f48656c6c6f2c20576f726c6421000000000000000000000000000000000000008152506000908051906020019061005c929190610062565b50610107565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106100a357805160ff19168380011785556100d1565b828001600101855582156100d1579182015b828111156100d05782518255916020019190600101906100b5565b5b5090506100de91906100e2565b5090565b61010491905b808211156101005760008160009055506001016100e8565b5090565b90565b610310806101166000396000f3fe608060405234801561001057600080fd5b50600436106100365760003560e01c80634ed3885e1461003b5780636d4ce63c146100f6575b600080fd5b6100f46004803603602081101561005157600080fd5b810190808035906020019064010000000081111561006e57600080fd5b82018360208201111561008057600080fd5b803590602001918460018302840111640100000000831117156100a257600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f820116905080830192505050505050509192919290505050610179565b005b6100fe610193565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561013e578082015181840152602081019050610123565b50505050905090810190601f16801561016b5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b806000908051906020019061018f929190610235565b5050565b606060008054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561022b5780601f106102005761010080835404028352916020019161022b565b820191906000526020600020905b81548152906001019060200180831161020e57829003601f168201915b5050505050905090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061027657805160ff19168380011785556102a4565b828001600101855582156102a4579182015b828111156102a3578251825591602001919060010190610288565b5b5090506102b191906102b5565b5090565b6102d791905b808211156102d35760008160009055506001016102bb565b5090565b9056fea26469706673582212200a71759272326fb76572af846152efb3ab0eecfce0a176c6bc5805cf18e343bb64736f6c634300060a0033\",\n" +
                        "        \"nonce\": \"855475221066568941671465558267495097831584488208619915012326931364330103302\",\n" +
                        "        \"signature\": \"0xd8ab504b932b2a338685ea1a3c378a3a83683c055fe13651957d01a72ffdb2c53963367108ae496c53785dd3e6827a6f9bb72d55fcc7cb6ed846d302f384873200\",\n" +
                        "        \"to\": \"0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744\",\n" +
                        "        \"version\": 0\n" +
                        "      }\n" +
                        "    ],\n" +
                        "    \"txsRoot\": \"0x0000000000000000000000000000000000000000000000000000000000000000\",\n" +
                        "    \"version\": 0\n" +
                        "  }\n" +
                        "}";
        // encode the string into object
        BcosBlock bcosBlock = objectMapper.readValue(blockString.getBytes(), BcosBlock.class);
        this.checkBlockHeader(bcosBlock);
        // check the transaction
        this.checkTransactionsForBlock(bcosBlock);
        this.checkEncodeDecode(bcosBlock);
    }

    @Test
    public void testBcosBlockWithoutTransaction() throws IOException {
        String blockString =
                "{\n" +
                        "  \"id\": 5,\n" +
                        "  \"jsonrpc\": \"2.0\",\n" +
                        "  \"result\": {\n" +
                        "    \"consensusWeights\": [\n" +
                        "      1\n" +
                        "    ],\n" +
                        "    \"extraData\": \"0x\",\n" +
                        "    \"gasUsed\": \"36488\",\n" +
                        "    \"hash\": \"0xaa3fb2b657db63ca437f9b862bab1a5e06bb0be6281cd78bf51373beafc97f5b\",\n" +
                        "    \"number\": 1,\n" +
                        "    \"parentInfo\": [\n" +
                        "      {\n" +
                        "        \"blockHash\": \"0x3e05e34a36cad0836483101667a9ed1822a7810f848979ce2a38444a222e029c\",\n" +
                        "        \"blockNumber\": 0\n" +
                        "      }\n" +
                        "    ],\n" +
                        "    \"receiptsRoot\": \"0x0000000000000000000000000000000000000000000000000000000000000000\",\n" +
                        "    \"sealer\": 0,\n" +
                        "    \"sealerList\": [\n" +
                        "      \"0x751bbcec9ab8fc8a8ecc9da7827e40a7f1b710801b3090cf06f846198b1ad0a0baff615a2624c00d2bf1f48bd29fbe509982130eb8c22debca2cacc8125b551e\"\n" +
                        "    ],\n" +
                        "    \"signatureList\": [\n" +
                        "      {\n" +
                        "        \"sealerIndex\": 0,\n" +
                        "        \"signature\": \"0x79a8fd54dc9371d9fd8dc51b1d42aee959d9f1a75f9a348a48a7fc2de42bf7c9134dd0988a2e4c5cfea63ff1b0f8acac0295925662b0ebb7a0e336fdf4175ce501\"\n" +
                        "      }\n" +
                        "    ],\n" +
                        "    \"stateRoot\": \"0x6a061fd2b32d4384df1d2e1897286f6dbbc139a3383569cbd940d0d990f66604\",\n" +
                        "    \"timestamp\": 1637579843630,\n" +
                        "    \"transactions\": [\n" +
                        "      \"0x8e72f7411887bdd218487437d3af29ce6fb5f3e624bf25b5bd4e593ba574ba28\"\n" +
                        "    ],\n" +
                        "    \"txsRoot\": \"0x0000000000000000000000000000000000000000000000000000000000000000\",\n" +
                        "    \"version\": 0\n" +
                        "  }\n" +
                        "}";
        BcosBlock bcosBlock = objectMapper.readValue(blockString.getBytes(), BcosBlock.class);
        this.checkBlockHeader(bcosBlock);
        // check transaction
        this.checkEncodeDecode(bcosBlock);
        this.checkEncodeDecodeString(bcosBlock);
    }

    private void checkEncodeDecodeString(BcosBlock bcosBlock) throws IOException {
        // encode the block
        String encodedData = objectMapper.writeValueAsString(bcosBlock);
        // decode the block
        BcosBlock decodedBlock = objectMapper.readValue(encodedData, BcosBlock.class);
        // check the block
        Assert.assertEquals(bcosBlock.getBlock(), decodedBlock.getBlock());
        Assert.assertEquals(bcosBlock.getBlock().hashCode(), decodedBlock.getBlock().hashCode());
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
        Assert.assertEquals("2.0", bcosBlock.getJsonrpc());
        Assert.assertEquals(5, bcosBlock.getId());
        Assert.assertEquals(1L, bcosBlock.getBlock().getNumber());
        Assert.assertEquals(
                "0xaa3fb2b657db63ca437f9b862bab1a5e06bb0be6281cd78bf51373beafc97f5b",
                bcosBlock.getBlock().getHash());
        Assert.assertEquals(
                "0x3e05e34a36cad0836483101667a9ed1822a7810f848979ce2a38444a222e029c",
                bcosBlock.getBlock().getParentInfo().get(0).getBlockHash());
        Assert.assertEquals(
                "0x0000000000000000000000000000000000000000000000000000000000000000",
                bcosBlock.getBlock().getReceiptsRoot());
        Assert.assertEquals(1, bcosBlock.getBlock().getSealerList().size());
        Assert.assertEquals(
                "0x751bbcec9ab8fc8a8ecc9da7827e40a7f1b710801b3090cf06f846198b1ad0a0baff615a2624c00d2bf1f48bd29fbe509982130eb8c22debca2cacc8125b551e",
                bcosBlock.getBlock().getSealerList().get(0));
        Assert.assertEquals(0, bcosBlock.getBlock().getSealer());
        Assert.assertEquals(1637579843630L, bcosBlock.getBlock().getTimestamp());
        Assert.assertEquals(2, bcosBlock.getBlock().getExtraData().length());
    }

    private void checkTransactionsForBlock(BcosBlock bcosBlock) {
        Assert.assertEquals(1, bcosBlock.getBlock().getTransactions().size());
        BcosBlock.TransactionObject transaction =
                ((BcosBlock.TransactionObject) bcosBlock.getBlock().getTransactions().get(0));
        Assert.assertEquals("0x2d6300a8f067872ebc87252d711b83a0c9325d35", transaction.getFrom());
        Assert.assertEquals(
                "0x24e190d013390901562265e4e3158dbb392c83e50e7cfa394d56d4afac4536a7",
                transaction.getHash());
        Assert.assertEquals(
                "0x608060405234801561001057600080fd5b506040518060400160405280600d81526020017f48656c6c6f2c20576f726c6421000000000000000000000000000000000000008152506000908051906020019061005c929190610062565b50610107565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106100a357805160ff19168380011785556100d1565b828001600101855582156100d1579182015b828111156100d05782518255916020019190600101906100b5565b5b5090506100de91906100e2565b5090565b61010491905b808211156101005760008160009055506001016100e8565b5090565b90565b610310806101166000396000f3fe608060405234801561001057600080fd5b50600436106100365760003560e01c80634ed3885e1461003b5780636d4ce63c146100f6575b600080fd5b6100f46004803603602081101561005157600080fd5b810190808035906020019064010000000081111561006e57600080fd5b82018360208201111561008057600080fd5b803590602001918460018302840111640100000000831117156100a257600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f820116905080830192505050505050509192919290505050610179565b005b6100fe610193565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561013e578082015181840152602081019050610123565b50505050905090810190601f16801561016b5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b806000908051906020019061018f929190610235565b5050565b606060008054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561022b5780601f106102005761010080835404028352916020019161022b565b820191906000526020600020905b81548152906001019060200180831161020e57829003601f168201915b5050505050905090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061027657805160ff19168380011785556102a4565b828001600101855582156102a4579182015b828111156102a3578251825591602001919060010190610288565b5b5090506102b191906102b5565b5090565b6102d791905b808211156102d35760008160009055506001016102bb565b5090565b9056fea26469706673582212200a71759272326fb76572af846152efb3ab0eecfce0a176c6bc5805cf18e343bb64736f6c634300060a0033",
                transaction.getInput());
        Assert.assertEquals(
                "855475221066568941671465558267495097831584488208619915012326931364330103302",
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
                        + "        \"blockNumber\": \"11\",\n"
                        + "        \"output\": \"0x\",\n"
                        + "        \"status\": \"0\"\n"
                        + "    }\n"
                        + "}";
        Call callResult = objectMapper.readValue(callString.getBytes(), Call.class);
        Assert.assertEquals("3.0", callResult.getJsonrpc());
        Assert.assertEquals(102, callResult.getId());
        // check callResult
        Call.CallOutput callOutput = callResult.getCallResult();
        Assert.assertEquals(11L, callOutput.getBlockNumber());
        Assert.assertEquals("0x", callOutput.getOutput());
        Assert.assertEquals(0, callOutput.getStatus());

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
                "{\n" +
                        "  \"id\": 12,\n" +
                        "  \"jsonrpc\": \"2.0\",\n" +
                        "  \"result\": {\n" +
                        "    \"endPoint\": \"0.0.0.0:30300\",\n" +
                        "    \"groupNodeIDInfo\": [\n" +
                        "      {\n" +
                        "        \"group\": \"group\",\n" +
                        "        \"nodeIDList\": [\n" +
                        "          \"204fb43da3190191bea1aea99396a8397789830914d2173b80627b025baf609ada69e61676af3371b5ec4d33af5e724dca71b6a95b187026a4a279cd095355bf\"\n" +
                        "        ]\n" +
                        "      }\n" +
                        "    ],\n" +
                        "    \"p2pNodeID\": \"3082010a0282010100b42c5e3f2c371074e7f783e1b38d1626f4d8f5e12f9301de02e5f0b88e64eb26fefa93500acdd40924d1b6883c1fa2aad8e69ee436ff1289ce39fe6ff8f5ed106bf49788a00a312699bf01a880bec5fe369960eb9d05b9bc29e83b2e597f47cdd8fde52a3abbf42f97f215708be13d441a8d9392cb880c542dededdccd94d07361e078d3e8b7f1f7a075764b52bd3a5c2f6138820f749412bddcb642cf45379ab0dab4075c733b0bde81848d1578d38f379c09af3f24e25d178c0da94b3a75584b51f434a322eb610d84ce96dd7c86e3eb2983b95c6d2b810eeacecd55dc22f710f408295b2336d873e7f7891277835797e81d5b535489af6d89ce8de4a0c68d0203010001\",\n" +
                        "    \"peers\": [\n" +
                        "      {\n" +
                        "        \"endPoint\": \"127.0.0.1:60889\",\n" +
                        "        \"groupNodeIDInfo\": [\n" +
                        "          {\n" +
                        "            \"group\": \"group\",\n" +
                        "            \"nodeIDList\": [\n" +
                        "              \"06d1c259e116fbfa9c1a3554098dfec3d8bb8a7cf9e74c1f411c4af3a84300a8b727ec7b4a4b429ef8482337845301ea89a9166c77ca29ee4aa8b7bee970727c\"\n" +
                        "            ]\n" +
                        "          }\n" +
                        "        ],\n" +
                        "        \"p2pNodeID\": \"3082010a0282010100c096bb1c65c83eef4d2e76096164284b48e80cc3ff14c6aeea4df01650c680605ef0e58451f9d15b3afc1295c9539360c00a1a8df60d3ee530a5742eaa0d1e10cba21ee35e6be294dcbfee328f1d19472a2559e3f1dc6975e8238bcd599942e512d4c737953e8c0f8e89874dd0c85367e548772217b2e152681b59c54e3004f1678bd3394b7a76940ed013e79c981131696a1bee16519ab430153223d4d98122e99c96b0d74d8fc321b311575c04c2cdff3b83b19daca1eb5f3cdf7a8bf42b1db015bbb00692dbe70d45acba0b4f6599cd7229113140507fd2348fcd1a23597cdc7a3b43678f21c1606ca691032c761caeb33aff93cf9f8eeb565c84e1df39850203010001\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"endPoint\": \"127.0.0.1:60897\",\n" +
                        "        \"groupNodeIDInfo\": [\n" +
                        "          {\n" +
                        "            \"group\": \"group\",\n" +
                        "            \"nodeIDList\": [\n" +
                        "              \"ed31b944b490a9989a563d6c74f6548f4efdde384e9ad94c5ac7a77349e175f990a365add0c31bac1eb8f129beb871fa70f1ea1ab80f6c8bb3f885a19de8371c\"\n" +
                        "            ]\n" +
                        "          }\n" +
                        "        ],\n" +
                        "        \"p2pNodeID\": \"3082010a0282010100ce1329b647cf199997fa1d2f170143d96d127ef405d48baa64c225a8da6db4da9c480e1a19e313e2382de1ee908e5c8e75879eb61136e3eaca687d4e3f8443ce791587a01f8c9fceb0965ba4581e226fa66398d9476f339e1b26bee94eece1810ec8f8de0fdd8423efe23e367c4943632c2fb8f946edc9ac9539aecd4e1c49ae93dbc419844b71fa702a4b620156dc73072721ae5be967cb77a632616321b107778ab28c1a3ecfb46701943dbf61ac9732a21ec6ff3ed4442fdc82a92128048069ba0bf9cc14173f640fe5680287d3f26bf231ab1b63940be1d1b8d2e74f815f1886d089faa87aacd33a019ed7e8a112624c5d1977e3a11d3c4be799e86a14d90203010001\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"endPoint\": \"127.0.0.1:60909\",\n" +
                        "        \"groupNodeIDInfo\": [\n" +
                        "          {\n" +
                        "            \"group\": \"group\",\n" +
                        "            \"nodeIDList\": [\n" +
                        "              \"9a52f228155a94e6ee7a63245b5d0d7b5a1d55c355e67cfd22b650dfd3a5d40c84d0f078a8c163bd3b9b1d0a88cea3443ee5f36f11404df3c4e1946ffc441e9f\"\n" +
                        "            ]\n" +
                        "          }\n" +
                        "        ],\n" +
                        "        \"p2pNodeID\": \"3082010a0282010100c123a8bc6c024d5abcd0e365c48e5d7e7192bc069ddca158e673445a50ba17f242b2fc7ae5c46054fce4fb9f79c5693b761177e1a4c68ef2abafa5d7c5b1ebca179716a78e002986db62536e4b615baf73c9817913cc7ef649857d6e1f2396b3f73057df5e01de408d303da4883662744b7052a39179e746e99554aa60d557c884f064da348993b7f42eba4300dcbe6faa7cbb11be65f9f89fa634eebf656f27d32294e7f8af95e2074cb95b3baa6c31eeafebbb9eb2b918f39806123375dfcd1d8bbe03a76bf5c9b5a6fb74c309ae24d6bda556000505059affcff2d22ae570a9848122b8b1362786c6270d4cc5711225ff673996281e9bec78905d50dda9010203010001\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  }\n" +
                        "}";
        Peers peers = objectMapper.readValue(peerStr.getBytes(), Peers.class);
        Assert.assertEquals(3, peers.getPeers().getPeers().size());
        Assert.assertEquals("0.0.0.0:30300", peers.getPeers().getEndPoint());
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
                        + "        {\n"
                        + "           \"nodeID\": \"037c255c06161711b6234b8c0960a6979ef039374ccc8b723afea2107cba3432dbbc837a714b7da20111f74d5a24e91925c773a72158fa066f586055379a1772\",\n"
                        + "           \"weight\": \"1\"\n"
                        + "        }\n,"
                        + "        {\n"
                        + "           \"nodeID\": \"0c0bbd25152d40969d3d3cee3431fa28287e07cff2330df3258782d3008b876d146ddab97eab42796495bfbb281591febc2a0069dcc7dfe88c8831801c5b5801\",\n"
                        + "           \"weight\": \"1\"\n"
                        + "        }\n,"
                        + "        {\n"
                        + "           \"nodeID\": \"622af37b2bd29c60ae8f15d467b67c0a7fe5eb3e5c63fdc27a0ee8066707a25afa3aa0eb5a3b802d3a8e5e26de9d5af33806664554241a3de9385d3b448bcd73\",\n"
                        + "           \"weight\": \"1\"\n"
                        + "        }\n"
                        + "    ]\n"
                        + "}";
        SealerList sealerList = objectMapper.readValue(sealerListStr, SealerList.class);
        Assert.assertEquals(3, sealerList.getSealerList().size());
        Assert.assertEquals(
                "0c0bbd25152d40969d3d3cee3431fa28287e07cff2330df3258782d3008b876d146ddab97eab42796495bfbb281591febc2a0069dcc7dfe88c8831801c5b5801",
                sealerList.getSealerList().get(1).getNodeID());
        Assert.assertEquals(
                "037c255c06161711b6234b8c0960a6979ef039374ccc8b723afea2107cba3432dbbc837a714b7da20111f74d5a24e91925c773a72158fa066f586055379a1772",
                sealerList.getSealerList().get(0).getNodeID());
        Assert.assertEquals(
                "622af37b2bd29c60ae8f15d467b67c0a7fe5eb3e5c63fdc27a0ee8066707a25afa3aa0eb5a3b802d3a8e5e26de9d5af33806664554241a3de9385d3b448bcd73",
                sealerList.getSealerList().get(2).getNodeID());
    }

    @Test
    public void testSyncStatus() throws JsonProcessingException,IOException {
        // FIXME: this unit test is wired
//        String syncStatusStr =
//                "{\n" +
//                        "  \"id\": 14,\n" +
//                        "  \"jsonrpc\": \"2.0\",\n" +
//                        "  \"result\": {\n" +
//                        "    \"blockNumber\": 0,\n" +
//                        "    \"genesisHash\": \"ee6d901cf8127bc16df2a97a7e6e036d986ab5a8e007d73d0cd1e801af334b4c\",\n" +
//                        "    \"isSyncing\": false,\n" +
//                        "    \"knownHighestNumber\": 0,\n" +
//                        "    \"knownLatestHash\": \"ee6d901cf8127bc16df2a97a7e6e036d986ab5a8e007d73d0cd1e801af334b4c\",\n" +
//                        "    \"latestHash\": \"ee6d901cf8127bc16df2a97a7e6e036d986ab5a8e007d73d0cd1e801af334b4c\",\n" +
//                        "    \"nodeID\": \"204fb43da3190191bea1aea99396a8397789830914d2173b80627b025baf609ada69e61676af3371b5ec4d33af5e724dca71b6a95b187026a4a279cd095355bf\",\n" +
//                        "    \"peers\": [\n" +
//                        "      {\n" +
//                        "        \"blockNumber\": 0,\n" +
//                        "        \"genesisHash\": \"ee6d901cf8127bc16df2a97a7e6e036d986ab5a8e007d73d0cd1e801af334b4c\",\n" +
//                        "        \"latestHash\": \"ee6d901cf8127bc16df2a97a7e6e036d986ab5a8e007d73d0cd1e801af334b4c\",\n" +
//                        "        \"nodeID\": \"06d1c259e116fbfa9c1a3554098dfec3d8bb8a7cf9e74c1f411c4af3a84300a8b727ec7b4a4b429ef8482337845301ea89a9166c77ca29ee4aa8b7bee970727c\"\n" +
//                        "      },\n" +
//                        "      {\n" +
//                        "        \"blockNumber\": 0,\n" +
//                        "        \"genesisHash\": \"ee6d901cf8127bc16df2a97a7e6e036d986ab5a8e007d73d0cd1e801af334b4c\",\n" +
//                        "        \"latestHash\": \"ee6d901cf8127bc16df2a97a7e6e036d986ab5a8e007d73d0cd1e801af334b4c\",\n" +
//                        "        \"nodeID\": \"9a52f228155a94e6ee7a63245b5d0d7b5a1d55c355e67cfd22b650dfd3a5d40c84d0f078a8c163bd3b9b1d0a88cea3443ee5f36f11404df3c4e1946ffc441e9f\"\n" +
//                        "      },\n" +
//                        "      {\n" +
//                        "        \"blockNumber\": 0,\n" +
//                        "        \"genesisHash\": \"ee6d901cf8127bc16df2a97a7e6e036d986ab5a8e007d73d0cd1e801af334b4c\",\n" +
//                        "        \"latestHash\": \"ee6d901cf8127bc16df2a97a7e6e036d986ab5a8e007d73d0cd1e801af334b4c\",\n" +
//                        "        \"nodeID\": \"ed31b944b490a9989a563d6c74f6548f4efdde384e9ad94c5ac7a77349e175f990a365add0c31bac1eb8f129beb871fa70f1ea1ab80f6c8bb3f885a19de8371c\"\n" +
//                        "      }\n" +
//                        "    ]\n" +
//                        "  }\n" +
//                        "}";
//        SyncStatus syncStatus = objectMapper.readValue(syncStatusStr.getBytes(), SyncStatus.class);
//        Assert.assertEquals(0L, syncStatus.getSyncStatus().getBlockNumber());
//        Assert.assertEquals(
//                "c02f53b63a055b921a83c3d74ef2cc7a76038ff50fe8e93e312b8746a5ef38ad",
//                syncStatus.getSyncStatus().getGenesisHash());
//        Assert.assertEquals(false, syncStatus.getSyncStatus().getIsSyncing());
//        Assert.assertEquals(0, syncStatus.getSyncStatus().getKnownHighestNumber());
//        Assert.assertEquals(
//                "c02f53b63a055b921a83c3d74ef2cc7a76038ff50fe8e93e312b8746a5ef38ad",
//                syncStatus.getSyncStatus().getKnownLatestHash());
//        Assert.assertEquals(
//                "c02f53b63a055b921a83c3d74ef2cc7a76038ff50fe8e93e312b8746a5ef38ad",
//                syncStatus.getSyncStatus().getLatestHash());
//        Assert.assertEquals(
//                "8d1751c88d7074bf80afa3c4c2cbd8e6e72e45928877957203eaff98bb11a6c82a6def474c5ce8b6e3e0cbcbcae69593e64c68e7d2dd8c067370cecfae77b221",
//                syncStatus.getSyncStatus().getNodeId());
//        // check peers
//        Assert.assertEquals(3, syncStatus.getSyncStatus().getPeers().size());
//        Assert.assertEquals(0L, syncStatus.getSyncStatus().getPeers().get(2).getBlockNumber());
//        Assert.assertEquals(
//                "c02f53b63a055b921a83c3d74ef2cc7a76038ff50fe8e93e312b8746a5ef38ad",
//                syncStatus.getSyncStatus().getPeers().get(2).getGenesisHash());
//        Assert.assertEquals(
//                "c02f53b63a055b921a83c3d74ef2cc7a76038ff50fe8e93e312b8746a5ef38ad",
//                syncStatus.getSyncStatus().getPeers().get(2).getLatestHash());
//        Assert.assertEquals(
//                "eb64cca1736744fafbd4678aecd7c7dcb4226cf36f76690ff0725d24b3e42203fa7142714c1c49c894f9c31e6d5b856d35e27c2efc7ba7f8159a9606d79e60b8",
//                syncStatus.getSyncStatus().getPeers().get(2).getNodeId());
    }

    @Test
    public void testSystemConfig() throws IOException {
        String systemConfigStr =
                "{\n"
                        + "  \"id\": 1,\n"
                        + "  \"jsonrpc\": \"2.0\",\n"
                        + "  \"result\": {\n"
                        + "    blockNumber: \"1\","
                        + "    value: \"1000\"\n"
                        + "  }\n"
                        + "}";
        SystemConfig systemConfig =
                objectMapper.readValue(systemConfigStr.getBytes(), SystemConfig.class);
        Assert.assertEquals("1000", systemConfig.getSystemConfig().getValue());
    }

    @Test
    public void testTotalTransactionCount() throws JsonProcessingException {
        String totalTxCountStr =
                "{\n"
                        + "    \"id\": 1,\n"
                        + "    \"jsonrpc\": \"2.0\",\n"
                        + "    \"result\": {\n"
                        + "      \"blockNumber\": \"0x1\",\n"
                        + "      \"failedTransactionCount\": \"0x0\",\n"
                        + "      \"transactionCount\": \"0x20\"\n"
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
                        + "        \"contractAddress\": \"0000000000000000000000000000000000000000\",\n"
                        + "        \"from\": \"0xcdcce60801c0a2e6bb534322c32ae528b9dec8d2\",\n"
                        + "        \"gasUsed\": \"0x1fb8d\",\n"
                        + "        \"input\": \"0xb602109a000000000000000000000000000000000000000000000000000000000000008000000000000000000000000000000000000000000000000000000000000000c00000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000014000000000000000000000000000000000000000000000000000000000000000203078313030303030303030303030303030303030303030303030303030303030000000000000000000000000000000000000000000000000000000000000000832303139303733300000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002616100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000026262000000000000000000000000000000000000000000000000000000000000\",\n"
                        + "        \"logEntries\": [ ],\n"
                        + "        \"logsBloom\": \"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n"
                        + "        \"output\": \"0x0000000000000000000000000000000000000000000000000000000000000000\",\n"
                        + "        \"root\":\"0x38723a2e5e8a17aa7950dc008209944e898f69a7bd10a23c839d341e935fd5ca\",\n"
                        + "        \"status\": \"12\",\n"
                        + "        \"to\": \"15538acd403ac1b2ff09083c70d04856b8c0bdfd\",\n"
                        + "        \"transactionHash\": \"0x708b5781b62166bd86e543217be6cd954fd815fd192b9a124ee9327580df8f3f\",\n"
                        + "        \"transactionIndex\": \"0x10\"\n"
                        + "    }\n"
                        + "}";
        BcosTransactionReceipt transactionReceipt =
                objectMapper.readValue(receiptStr, BcosTransactionReceipt.class);
        Assert.assertEquals(
                "0x0000000000000000000000000000000000000000",
                transactionReceipt.getTransactionReceipt().getContractAddress());
        Assert.assertEquals(
                "0xcdcce60801c0a2e6bb534322c32ae528b9dec8d2",
                transactionReceipt.getTransactionReceipt().getFrom());
        Assert.assertEquals(
                "0x1fb8d", transactionReceipt.getTransactionReceipt().getGasUsed());
        Assert.assertEquals(
                "0xb602109a000000000000000000000000000000000000000000000000000000000000008000000000000000000000000000000000000000000000000000000000000000c00000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000014000000000000000000000000000000000000000000000000000000000000000203078313030303030303030303030303030303030303030303030303030303030000000000000000000000000000000000000000000000000000000000000000832303139303733300000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002616100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000026262000000000000000000000000000000000000000000000000000000000000",
                transactionReceipt.getTransactionReceipt().getInput());
        Assert.assertEquals(0, transactionReceipt.getTransactionReceipt().getLogEntries().size());
        Assert.assertEquals(
                "0x0000000000000000000000000000000000000000000000000000000000000000",
                transactionReceipt.getTransactionReceipt().getOutput());
        Assert.assertEquals(12, transactionReceipt.getTransactionReceipt().getStatus());
        Assert.assertEquals(
                "0x15538acd403ac1b2ff09083c70d04856b8c0bdfd",
                transactionReceipt.getTransactionReceipt().getTo());
        Assert.assertEquals(
                "0x708b5781b62166bd86e543217be6cd954fd815fd192b9a124ee9327580df8f3f",
                transactionReceipt.getTransactionReceipt().getTransactionHash());
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
                        + "    \"blockHash\": \"0xcd31b05e466bce99460b1ed70d6069fdfbb15e6eef84e9b9e4534358edb3899a\",\n"
                        + "    \"blockNumber\": \"0x5\",\n"
                        + "    \"contractAddress\": \"0000000000000000000000000000000000000000\",\n"
                        + "    \"from\": \"0x148947262ec5e21739fe3a931c29e8b84ee34a0f\",\n"
                        + "    \"gasUsed\": \"0x21dc1b\",\n"
                        + "    \"input\": \"0x8a42ebe90000000000000000000000000000000000000000000000000000000000000040000000000000000000000000000000000000000000000000000000003b9aca00000000000000000000000000000000000000000000000000000000000000000a3564646636663863653800000000000000000000000000000000000000000000\",\n"
                        + "    \"logs\": [],\n"
                        + "    \"logsBloom\": \"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n"
                        + "    \"output\": \"0x\",\n"
                        + "    \"root\": \"0xc3b4185963c78a4ca8eb90240e5cd95371d7217a9ce2bfa1149d53f79c73afbb\",\n"
                        + "    \"status\": \"0\",\n"
                        + "    \"to\": \"0xd6c8a04b8826b0a37c6d4aa0eaa8644d8e35b79f\",\n"
                        + "    \"transactionHash\": \"0xd2c12e211315ef09dbad53407bc820d062780232841534954f9c23ab11d8ab4c\",\n"
                        + "    \"transactionIndex\": \"0x32\"\n"
                        + "  }\n"
                        + "}";
        BcosTransactionReceipt receiptWithProof =
                objectMapper.readValue(receiptWithProofStr, BcosTransactionReceipt.class);
        Assert.assertEquals(
                3, receiptWithProof.getTransactionReceipt().getReceiptProof().size());
        Assert.assertEquals(
                2,
                receiptWithProof.getTransactionReceipt().getReceiptProof()
                        .get(0)
                        .getLeft()
                        .size());
        Assert.assertEquals(
                13,
                receiptWithProof
                        .getTransactionReceipt().getReceiptProof()
                        .get(0)
                        .getRight()
                        .size());
        Assert.assertEquals(
                3,
                receiptWithProof
                        .getTransactionReceipt().getReceiptProof()
                        .get(1)
                        .getLeft()
                        .size());
        Assert.assertEquals(
                2,
                receiptWithProof
                        .getTransactionReceipt().getReceiptProof()
                        .get(1)
                        .getRight()
                        .size());
        Assert.assertEquals(
                0,
                receiptWithProof
                        .getTransactionReceipt().getReceiptProof()
                        .get(2)
                        .getLeft()
                        .size());
        Assert.assertEquals(
                0,
                receiptWithProof
                        .getTransactionReceipt().getReceiptProof()
                        .get(2)
                        .getRight()
                        .size());
        Assert.assertEquals(
                "cd46118c0e99be585ffcf50423630348dbc486e54e9d9293a6a8754020a68a92",
                receiptWithProof
                        .getTransactionReceipt().getReceiptProof()
                        .get(1)
                        .getLeft()
                        .get(0));
        Assert.assertEquals(
                "6a6cefef8b48e455287a8c8694b06f4f7cb7950017ab048d6e6bdd8029f9f8c9",
                receiptWithProof
                        .getTransactionReceipt().getReceiptProof()
                        .get(1)
                        .getRight()
                        .get(0));
        Assert.assertEquals(
                "0x5",
                receiptWithProof.getTransactionReceipt().getBlockNumber());
        Assert.assertEquals(
                "0x0000000000000000000000000000000000000000",
                receiptWithProof
                        .getTransactionReceipt()
                        .getContractAddress());
        Assert.assertEquals(
                "0x148947262ec5e21739fe3a931c29e8b84ee34a0f",
                receiptWithProof.getTransactionReceipt().getFrom());
        Assert.assertEquals(
                "0x21dc1b",
                receiptWithProof.getTransactionReceipt().getGasUsed());
        Assert.assertEquals(
                0, receiptWithProof.getTransactionReceipt().getStatus());
        Assert.assertEquals(
                "0xd6c8a04b8826b0a37c6d4aa0eaa8644d8e35b79f",
                receiptWithProof.getTransactionReceipt().getTo());
        Assert.assertEquals(
                null, receiptWithProof.getTransactionReceipt().getTransactionProof());
    }

    @Test
    public void testSMGetTransactionAndCalculateHash() throws IOException {
        String transactionString = "{\n" +
                "  \"id\": 1,\n" +
                "  \"jsonrpc\": \"2.0\",\n" +
                "  \"result\": {\n" +
                "    \"blockHash\": \"0x030f5cda798836d6747aaa94fd53f5eeb96ccd9af74567ab1c186c18ff20a811\",\n" +
                "    \"blockLimit\": \"244\",\n" +
                "    \"blockNumber\": \"0x1\",\n" +
                "    \"chainID\": \"0x1\",\n" +
                "    \"extraData\": \"0x\",\n" +
                "    \"from\": \"0xdba1e9f40baa1b956b509b04eb738eccfa8d784c\",\n" +
                "    \"gas\": \"0x419ce0\",\n" +
                "    \"gasPrice\": \"0x51f4d5c00\",\n" +
                "    \"groupID\": \"0x1\",\n" +
                "    \"hash\": \"0xb15bdf21b95d407cc3e5bcdaf22ed6330e8e852bd4f85875c55099a982fa19bc\",\n" +
                "    \"input\": \"0x608060405234801561001057600080fd5b506040805190810160405280600d81526020017f48656c6c6f2c20576f726c6421000000000000000000000000000000000000008152506000908051906020019061005c929190610062565b50610107565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106100a357805160ff19168380011785556100d1565b828001600101855582156100d1579182015b828111156100d05782518255916020019190600101906100b5565b5b5090506100de91906100e2565b5090565b61010491905b808211156101005760008160009055506001016100e8565b5090565b90565b6102d7806101166000396000f30060806040526004361061004c576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff168063299f7f9d146100515780633590b49f146100e1575b600080fd5b34801561005d57600080fd5b5061006661014a565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156100a657808201518184015260208101905061008b565b50505050905090810190601f1680156100d35780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156100ed57600080fd5b50610148600480360381019080803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091929192905050506101ec565b005b606060008054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156101e25780601f106101b7576101008083540402835291602001916101e2565b820191906000526020600020905b8154815290600101906020018083116101c557829003601f168201915b5050505050905090565b8060009080519060200190610202929190610206565b5050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061024757805160ff1916838001178555610275565b82800160010185558215610275579182015b82811115610274578251825591602001919060010190610259565b5b5090506102829190610286565b5090565b6102a891905b808211156102a457600081600090555060010161028c565b5090565b905600a165627a7a72305820c94ea3bc35f522d8a1c8c998b61d072d792ee8f822425f7e4b54e727f3ce105b0029\",\n" +
                "    \"nonce\": \"0x191da6814c29612a12bf24af7e9565b628f55e4eee2c340df144e86738f0d93\",\n" +
                "    \"signature\": \"0xce7b4b60bc723819e374547c3bdf5867743a88c401bfbe9cf89bc05dd95d01bc7bc71f92263204486f666e074d54ac0ec36bb12da45d9b8bba90356eb447e37a837195bb75136ce67f54ae466941c5b6092137db4586faf5bfff2269820aefd487fb09c03a5e84799c8c7c2263da6f710b48e6e24bfb5b3096613591499a2714\",\n" +
                "    \"to\": \"0x0000000000000000000000000000000000000000\",\n" +
                "    \"transactionIndex\": \"0x0\",\n" +
                "    \"value\": \"0x0\"\n" +
                "  }\n" +
                "}";

        BcosTransaction bcosTransaction = objectMapper.readValue(transactionString.getBytes(), BcosTransaction.class);
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
//        Assert.assertEquals(
//                bcosTransaction.getTransaction().get().calculateHash(cryptoSuite),
//                bcosTransaction.getTransaction().get().getHash());

        transactionString = "{\n" +
                "  \"id\": 1,\n" +
                "  \"jsonrpc\": \"2.0\",\n" +
                "  \"result\": {\n" +
                "    \"blockHash\": \"0x03d593af4d2bc7c4d07373bee5b5fc556c898b37328c2a04df7ea0f021941c3c\",\n" +
                "    \"blockLimit\": \"244\",\n" +
                "    \"blockNumber\": \"0x2\",\n" +
                "    \"chainID\": \"0x1\",\n" +
                "    \"extraData\": \"0x\",\n" +
                "    \"from\": \"0xdba1e9f40baa1b956b509b04eb738eccfa8d784c\",\n" +
                "    \"gas\": \"0x419ce0\",\n" +
                "    \"gasPrice\": \"0x51f4d5c00\",\n" +
                "    \"groupID\": \"0x1\",\n" +
                "    \"hash\": \"0xfcaa0498324848d038965f1f7496f5bb90c9d630f7f6620a6f738fda3d7ee879\",\n" +
                "    \"input\": \"0x3590b49f0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000568656c6c6f000000000000000000000000000000000000000000000000000000\",\n" +
                "    \"nonce\": \"0x50a15577227bf5790b2fc79b6c6c30d8413a7b4f7ae489d89e926fd6b72631\",\n" +
                "    \"signature\": \"0x8130abdda331c9c7c8654c63da1fc34041148b990c76c3ff6238cfcd2386f56e1d864e9f338c380b6502180ebde81685273f5f7b0945dde1c254f9c58152d64f837195bb75136ce67f54ae466941c5b6092137db4586faf5bfff2269820aefd487fb09c03a5e84799c8c7c2263da6f710b48e6e24bfb5b3096613591499a2714\",\n" +
                "    \"to\": \"0xeb1164d2b50f07a5fdd719e2b924a81905ec0d53\",\n" +
                "    \"transactionIndex\": \"0x0\",\n" +
                "    \"value\": \"0x0\"\n" +
                "  }\n" +
                "}";
        bcosTransaction = objectMapper.readValue(transactionString.getBytes(), BcosTransaction.class);
        cryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
//        Assert.assertEquals(
//                bcosTransaction.getTransaction().get().calculateHash(cryptoSuite),
//                bcosTransaction.getTransaction().get().getHash());
    }

    @Test
    public void testSMGetBlockAndCalculateHash() throws IOException {
        String blockHeaderStr = "{\n" +
                "  \"id\": 12,\n" +
                "  \"jsonrpc\": \"2.0\",\n" +
                "  \"result\": {\n" +
                "    \"consensusWeights\": [\n" +
                "      1,\n" +
                "      1\n" +
                "    ],\n" +
                "    \"extraData\": \"0x\",\n" +
                "    \"gasUsed\": \"40488\",\n" +
                "    \"hash\": \"0xc5a884ef6d4025bdd674574f82747de132fb615266f4ac01c726e1048350a591\",\n" +
                "    \"number\": 2,\n" +
                "    \"parentInfo\": [\n" +
                "      {\n" +
                "        \"blockHash\": \"0x571d5b233405eab3fae832b73bd268a6253d9f622a7cdf9819c952ee09977395\",\n" +
                "        \"blockNumber\": 1\n" +
                "      }\n" +
                "    ],\n" +
                "    \"receiptsRoot\": \"0xc2de3ab29139de8708238e851a58c68233ebfa41134d5309d2ac0439f70ff508\",\n" +
                "    \"sealer\": 1,\n" +
                "    \"sealerList\": [\n" +
                "      \"0x6c5911d6ba3080fd22f0b58680c4cb122e33ca95b1c1dd8cf18e79d03853a2d7392f58f7e6b4dd7b99a83d9ab490d2d32fd98ed77a7ebc75c37adc8b6de65a1e\",\n" +
                "      \"0x6cc479308738951ea2f32e4b2d7f6a4e916b849e6559441bfd366ac44bdc277ba428375fa4862c4fed28fb5c30c79587e627d2a342c9ac86083fcc76d5cf36ee\"\n" +
                "    ],\n" +
                "    \"signatureList\": [\n" +
                "      {\n" +
                "        \"sealerIndex\": 0,\n" +
                "        \"signature\": \"0x19330ad0f7307b9efac50f297922af6e850be9800e1d2c3526d28b9448ce5193de686d128aba0a090ef2454c1917832c856c33bb656e7187626191223a4805e1\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"sealerIndex\": 1,\n" +
                "        \"signature\": \"0x854635f29c3bb2af14ac16626229de6607f760cb8a5eae18beedd09e3fade535088bca1cf2b9d9750c4eda401f4a9ae674fd6468897511c73f84f17a80a89dc0\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"stateRoot\": \"0xe6fa667ea86614a0aee150942ffc2030fb52bcea5953315e7317b7dadd75d611\",\n" +
                "    \"timestamp\": 1642577548192,\n" +
                "    \"txsRoot\": \"0xfc06d70729538508110611c10b219e4e17a949411ec80865ed277421ed67ddf4\",\n" +
                "    \"version\": 0\n" +
                "  }\n" +
                "}";

        BcosBlockHeader bcosBlockHeader = objectMapper.readValue(blockHeaderStr.getBytes(), BcosBlockHeader.class);
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
        // Assert.assertEquals(
        //        bcosBlockHeader.getBlockHeader().calculateHash(cryptoSuite),
        //        bcosBlockHeader.getBlockHeader().getHash());

    }

    @Test
    public void testECDSAGetTransactionAndCalculateHash() throws IOException {
        String transactionStr = "{\n" +
                "  \"id\": 12,\n" +
                "  \"jsonrpc\": \"2.0\",\n" +
                "  \"result\": {\n" +
                "    \"blockLimit\": 543,\n" +
                "    \"chainID\": \"chain\",\n" +
                "    \"from\": \"0x9036450ed747ef3b0423734f36ed6472d35cac6f\",\n" +
                "    \"groupID\": \"group0\",\n" +
                "    \"hash\": \"0xbd5121a964a0f14414e4f7ef99e91943baa830bdbb2e345b7eae56c94b8e8386\",\n" +
                "    \"importTime\": 1642493461036,\n" +
                "    \"input\": \"0x4ed3885e000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000033132330000000000000000000000000000000000000000000000000000000000\",\n" +
                "    \"nonce\": \"815106147678017284033451788138572352403714561031581276615138411883384218839\",\n" +
                "    \"signature\": \"0x5fb225c4d87c5db55e2416412550b6fa6b874f421f7bd345566bba08bd443d3970dd0ce526d06ed6628990cdb607800776922e19fcdeee7365582919f21887e900\",\n" +
                "    \"to\": \"dCDECd228F59A234287FECe68aD8fB94f016B124\",\n" +
                "    \"version\": 0\n" +
                "  }\n" +
                "}";
        BcosTransaction bcosTransaction = objectMapper.readValue(transactionStr.getBytes(), BcosTransaction.class);
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        //TODO: fix
        // Assert.assertEquals(
        //         bcosTransaction.getTransaction().get().calculateHash(cryptoSuite),
        //        bcosTransaction.getTransaction().get().getHash());
    }

    @Test
    public void testECDSAGetBlockAndCalculateHash() throws IOException {
        String blockHeaderStr = "{\n" +
                "  \"id\": 10,\n" +
                "  \"jsonrpc\": \"2.0\",\n" +
                "  \"result\": {\n" +
                "    \"consensusWeights\": [\n" +
                "      1,\n" +
                "      1\n" +
                "    ],\n" +
                "    \"extraData\": \"0x\",\n" +
                "    \"gasUsed\": \"10241\",\n" +
                "    \"hash\": \"0xffaff61daa37328340f8f37406a9477602c9013bc72dee4020b0c759abed1f56\",\n" +
                "    \"number\": 44,\n" +
                "    \"parentInfo\": [\n" +
                "      {\n" +
                "        \"blockHash\": \"0xc160d5b049f93e5d5dce4549d88d4c2c592e15ffad07cbc5655d11236e570f26\",\n" +
                "        \"blockNumber\": 43\n" +
                "      }\n" +
                "    ],\n" +
                "    \"receiptsRoot\": \"0xb1bf3b5fedf6a68aed297ee8f00711e592338e47790acc8767c0457773f10a9a\",\n" +
                "    \"sealer\": 1,\n" +
                "    \"sealerList\": [\n" +
                "      \"0x6da2e77c2181de646e8dd977fb6614f8eb0ecef647537895a03ea4ac532cb3e5a958eab4cf15d1355c3e09039611b80ea0a07e456ac92cf5a98b5eb42e0312c3\",\n" +
                "      \"0xc99634f99b4fd5c570e01ec022c3fcaa81cd4797acac4b86c81521e08b225385e3cbd5f15e70b6360b9dbaf070bed7111b2f42bb1d5a33c735145be966a8ccd4\"\n" +
                "    ],\n" +
                "    \"signatureList\": [\n" +
                "      {\n" +
                "        \"sealerIndex\": 0,\n" +
                "        \"signature\": \"0xcc04d759a0e286be367ee9339f0332d799752917ba6c8c2c5df4f5ae77dc469b285674bae7bef138c98c84c73c5384a5341616edb0d7d4c60351d6e05066bf6b01\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"sealerIndex\": 1,\n" +
                "        \"signature\": \"0x678eef1c50c3fcd876d3f2a79012ca4eaa1765d8e78cb08407b5fcbc6cdef3df3a71ce2359d946d2d81ce86d8ee270d74db6460eb00f3deb1dbeb0e3c9eabe0b01\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"stateRoot\": \"0xbcf31ce042e80ab45720e751d544c1df2bddc9aacb7d66ec2d14b05638b1718d\",\n" +
                "    \"timestamp\": 1642493461037,\n" +
                "    \"transactions\": [\n" +
                "      {\n" +
                "        \"blockLimit\": 543,\n" +
                "        \"chainID\": \"chain\",\n" +
                "        \"from\": \"0x9036450ed747ef3b0423734f36ed6472d35cac6f\",\n" +
                "        \"groupID\": \"group0\",\n" +
                "        \"hash\": \"0xbd5121a964a0f14414e4f7ef99e91943baa830bdbb2e345b7eae56c94b8e8386\",\n" +
                "        \"importTime\": 1642493461036,\n" +
                "        \"input\": \"0x4ed3885e000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000033132330000000000000000000000000000000000000000000000000000000000\",\n" +
                "        \"nonce\": \"815106147678017284033451788138572352403714561031581276615138411883384218839\",\n" +
                "        \"signature\": \"0x5fb225c4d87c5db55e2416412550b6fa6b874f421f7bd345566bba08bd443d3970dd0ce526d06ed6628990cdb607800776922e19fcdeee7365582919f21887e900\",\n" +
                "        \"to\": \"dCDECd228F59A234287FECe68aD8fB94f016B124\",\n" +
                "        \"version\": 0\n" +
                "      }\n" +
                "    ],\n" +
                "    \"txsRoot\": \"0x6ed09587e47ff677552830b014835e55a189ba80f0dd8a9de3c3e7938d752286\",\n" +
                "    \"version\": 0\n" +
                "  }\n" +
                "}";
        // BcosBlockHeader bcosBlockHeader = objectMapper.readValue(blockHeaderStr.getBytes(), BcosBlockHeader.class);
        // CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        // Assert.assertEquals(
        //        bcosBlockHeader.getBlockHeader().calculateHash(cryptoSuite),
        //        bcosBlockHeader.getBlockHeader().getHash());
    }

}
