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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.utilities.receipt.ReceiptBuilderJniObj;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionBuilderJniObj;
import org.fisco.bcos.sdk.v3.client.protocol.model.GroupStatus;
import org.fisco.bcos.sdk.v3.client.protocol.model.JsonTransactionResponse;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosBlock;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosBlockHeader;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupInfo;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupInfoList;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupNodeInfo;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosTransaction;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosTransactionReceipt;
import org.fisco.bcos.sdk.v3.client.protocol.response.BlockHash;
import org.fisco.bcos.sdk.v3.client.protocol.response.BlockNumber;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.client.protocol.response.ConsensusStatus;
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
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.utils.MerkleProofUtility;
import org.fisco.bcos.sdk.v3.utils.ObjectMapperFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public class ResponseTest {
    private static final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();

    @Test
    public void testBlockHeaderResponse() throws IOException {
        String blockHeaderString =
                "{\n" +
                        "  \"id\": 3,\n" +
                        "  \"jsonrpc\": \"2.0\",\n" +
                        "  \"result\": {\n" +
                        "    \"consensusWeights\": [\n" +
                        "      1,\n" +
                        "      1\n" +
                        "    ],\n" +
                        "    \"extraData\": \"0x\",\n" +
                        "    \"gasUsed\": \"24363\",\n" +
                        "    \"hash\": \"0xc3c038f6fdb78602dc82ad7dc7d7dd90816c767362ef0bc069c16d04b91dbfe7\",\n" +
                        "    \"number\": 1,\n" +
                        "    \"parentInfo\": [\n" +
                        "      {\n" +
                        "        \"blockHash\": \"0x4d3695d385084e56c814d078a987427e743e92b4164b42a8aa01bc24c82ab223\",\n" +
                        "        \"blockNumber\": 0\n" +
                        "      }\n" +
                        "    ],\n" +
                        "    \"receiptsRoot\": \"0xfceec39f7894f52d3a35b4bf8cb24e6d34f6ca7847ca45a2b3d99deed63007a1\",\n" +
                        "    \"sealer\": 0,\n" +
                        "    \"sealerList\": [\n" +
                        "      \"0x63a2e45b2d84f83b32342f0741ffc51069c74fb7c82b8eb0247b12230d50169b86545ecf84420adeec86c57dbc48db1342f4afebc6a127b481eeaaa23722fff0\",\n" +
                        "      \"0x6471685bb764ddd625c8855809ae23ae803fcf2890977def7c3d4353e22633cdea92471ba0859fc0538679c31b89577e1dd13b292d6538acff42ac4c599d5ce8\"\n" +
                        "    ],\n" +
                        "    \"signatureList\": [\n" +
                        "      {\n" +
                        "        \"sealerIndex\": 0,\n" +
                        "        \"signature\": \"0xf04f80b74a4b0c945a716c11b01794ae7bf1c7aa67e1b226a1dabc0f15b47d6316b8b08f56f846d676da66496dee1c23deb62ac091be06f3b50e01c998f672b501\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"sealerIndex\": 1,\n" +
                        "        \"signature\": \"0x939a87e660057db39a691bc74c869011e74fdd148b885a59a774b4b85c207a3c1133b1a8605a7acc2cc43a2544086a16ab62ff490453da5cc09f1d7d7167254300\"\n" +
                        "      }\n" +
                        "    ],\n" +
                        "    \"stateRoot\": \"0x5b64771638bc22c555b1a0e7597e2cf2afb0bab977eb39cd473e96bcf5235dd1\",\n" +
                        "    \"timestamp\": 1654587389123,\n" +
                        "    \"txsRoot\": \"0x6608f7090b3d014cc37571d505b4067cbbf381e9afa8999ccb44a15d5bb54dd2\",\n" +
                        "    \"version\": 4\n" +
                        "  }\n" +
                        "}";
        // decode the block header
        BcosBlockHeader blockHeader =
                objectMapper.readValue(blockHeaderString.getBytes(), BcosBlockHeader.class);
        // check the value field of the blockHeader
        Assert.assertEquals("2.0", blockHeader.getJsonrpc());
        Assert.assertEquals(3, blockHeader.getId());
        Assert.assertEquals(1, blockHeader.getBlockHeader().getNumber());
        Assert.assertEquals(
                "0xc3c038f6fdb78602dc82ad7dc7d7dd90816c767362ef0bc069c16d04b91dbfe7",
                blockHeader.getBlockHeader().getHash());
        Assert.assertEquals(
                "0x6608f7090b3d014cc37571d505b4067cbbf381e9afa8999ccb44a15d5bb54dd2",
                blockHeader.getBlockHeader().getTransactionsRoot());
        Assert.assertEquals(1654587389123L, blockHeader.getBlockHeader().getTimestamp());
        Assert.assertEquals(
                "0x63a2e45b2d84f83b32342f0741ffc51069c74fb7c82b8eb0247b12230d50169b86545ecf84420adeec86c57dbc48db1342f4afebc6a127b481eeaaa23722fff0",
                blockHeader.getBlockHeader().getSealerList().get(0));
        Assert.assertEquals(
                "0xf04f80b74a4b0c945a716c11b01794ae7bf1c7aa67e1b226a1dabc0f15b47d6316b8b08f56f846d676da66496dee1c23deb62ac091be06f3b50e01c998f672b501",
                blockHeader.getBlockHeader().getSignatureList().get(0).getSignature());
        Assert.assertEquals(
                Integer.valueOf(1), blockHeader.getBlockHeader().getSignatureList().get(1).getIndex());
        Assert.assertEquals(0, blockHeader.getBlockHeader().getSealer());
        Assert.assertEquals(
                "0xfceec39f7894f52d3a35b4bf8cb24e6d34f6ca7847ca45a2b3d99deed63007a1",
                blockHeader.getBlockHeader().getReceiptsRoot());
        Assert.assertEquals("24363", blockHeader.getBlockHeader().getGasUsed());
        Assert.assertEquals(
                "0x5b64771638bc22c555b1a0e7597e2cf2afb0bab977eb39cd473e96bcf5235dd1",
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
    }

    @Test
    public void testTransaction() throws IOException {
        String transactionString =
                "{\n" +
                        "  \"id\": 4,\n" +
                        "  \"jsonrpc\": \"2.0\",\n" +
                        "  \"result\": {\n" +
                        "    \"abi\": \"[{\\\"inputs\\\":[],\\\"stateMutability\\\":\\\"nonpayable\\\",\\\"type\\\":\\\"constructor\\\"},{\\\"inputs\\\":[],\\\"name\\\":\\\"get\\\",\\\"outputs\\\":[{\\\"internalType\\\":\\\"string\\\",\\\"name\\\":\\\"\\\",\\\"type\\\":\\\"string\\\"}],\\\"stateMutability\\\":\\\"view\\\",\\\"type\\\":\\\"function\\\"},{\\\"inputs\\\":[{\\\"internalType\\\":\\\"string\\\",\\\"name\\\":\\\"n\\\",\\\"type\\\":\\\"string\\\"}],\\\"name\\\":\\\"set\\\",\\\"outputs\\\":[],\\\"stateMutability\\\":\\\"nonpayable\\\",\\\"type\\\":\\\"function\\\"}]\",\n" +
                        "    \"blockLimit\": 501,\n" +
                        "    \"chainID\": \"chain0\",\n" +
                        "    \"from\": \"0xebf98be58e190cab7ebed61295b0321d55bb8163\",\n" +
                        "    \"groupID\": \"group0\",\n" +
                        "    \"hash\": \"0x4da85a5350adf7676c8a81d7ba5cf26a5041af68324c6018ebbf1dcfaf2dfa8f\",\n" +
                        "    \"importTime\": 1654587909356,\n" +
                        "    \"input\": \"0x12312312312396ee40c32b317e1dede0be7f6c1a976a111e319d50f936d04721a28aeb2c181b8cafa15d0a6035c261aede2b3d8cb9ad1791da28c7ab700\",\n" +
                        "    \"nonce\": \"115329848189538643633069930310086039866817189149861952393622337769039905453838\",\n" +
                        "    \"signature\": \"0x36b38271936016813d596ee40c32b317e1dede0be7f6c1a976a111e319d50f936d04721a28aeb2c181b8cafa15d0a6035c261aede2b3d8cb9ad1791da28c7ab700\",\n" +
                        "    \"to\": \"0xebf98be58e190cab7ebed61295b0321d55bb8123\",\n" +
                        "    \"version\": 0\n" +
                        "  }\n" +
                        "}";
        // decode the BcosTransaction object from the given string
        BcosTransaction transaction =
                objectMapper.readValue(transactionString.getBytes(), BcosTransaction.class);
        Assert.assertEquals("2.0", transaction.getJsonrpc());
        Assert.assertEquals(4, transaction.getId());
        Assert.assertEquals(
                "0xebf98be58e190cab7ebed61295b0321d55bb8163",
                transaction.getTransaction().get().getFrom());
        Assert.assertEquals(
                "0x4da85a5350adf7676c8a81d7ba5cf26a5041af68324c6018ebbf1dcfaf2dfa8f",
                transaction.getTransaction().get().getHash());
        Assert.assertEquals(
                "0x12312312312396ee40c32b317e1dede0be7f6c1a976a111e319d50f936d04721a28aeb2c181b8cafa15d0a6035c261aede2b3d8cb9ad1791da28c7ab700",
                transaction.getTransaction().get().getInput());
        Assert.assertEquals(
                "115329848189538643633069930310086039866817189149861952393622337769039905453838",
                transaction.getTransaction().get().getNonce());
        Assert.assertEquals(
                "0xebf98be58e190cab7ebed61295b0321d55bb8123",
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
                        + "  \"result\": \"2\"\n"
                        + "}";
        BlockNumber blockNumber =
                objectMapper.readValue(blockNumberString.getBytes(), BlockNumber.class);
        Assert.assertEquals(BigInteger.valueOf(2), blockNumber.getBlockNumber());
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
                        + "    \"result\": \"3\"\n"
                        + "}";
        PbftView pbftView = objectMapper.readValue(pbftViewStr.getBytes(), PbftView.class);
        Assert.assertEquals(BigInteger.valueOf(3), pbftView.getPbftView());
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
                        + "  \"result\": \"100\"\n"
                        + "}";
        PendingTxSize pendingTxSize =
                objectMapper.readValue(pendingTxSizeStr.getBytes(), PendingTxSize.class);
        Assert.assertEquals(BigInteger.valueOf(100), pendingTxSize.getPendingTxSize());
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
    public void testSyncStatus() throws JsonProcessingException, IOException {
        String syncStatusStr =
                "{\n" +
                        "  \"id\": 37,\n" +
                        "  \"jsonrpc\": \"2.0\",\n" +
                        "  \"result\": \"{\\\"blockNumber\\\":3,\\\"genesisHash\\\":\\\"4d3695d385084e56c814d078a987427e743e92b4164b42a8aa01bc24c82ab223\\\",\\\"isSyncing\\\":false,\\\"knownHighestNumber\\\":3,\\\"knownLatestHash\\\":\\\"16b0b5b407cfb16a76c1d14d9231c1ae9422e4ec2032144edd12c4b59c33d7ea\\\",\\\"latestHash\\\":\\\"16b0b5b407cfb16a76c1d14d9231c1ae9422e4ec2032144edd12c4b59c33d7ea\\\",\\\"nodeID\\\":\\\"63a2e45b2d84f83b32342f0741ffc51069c74fb7c82b8eb0247b12230d50169b86545ecf84420adeec86c57dbc48db1342f4afebc6a127b481eeaaa23722fff0\\\",\\\"peers\\\":[{\\\"blockNumber\\\":3,\\\"genesisHash\\\":\\\"4d3695d385084e56c814d078a987427e743e92b4164b42a8aa01bc24c82ab223\\\",\\\"latestHash\\\":\\\"16b0b5b407cfb16a76c1d14d9231c1ae9422e4ec2032144edd12c4b59c33d7ea\\\",\\\"nodeID\\\":\\\"6471685bb764ddd625c8855809ae23ae803fcf2890977def7c3d4353e22633cdea92471ba0859fc0538679c31b89577e1dd13b292d6538acff42ac4c599d5ce8\\\"}]}\\n\"\n" +
                        "}";
        SyncStatus syncStatus = objectMapper.readValue(syncStatusStr.getBytes(), SyncStatus.class);
        Assert.assertEquals(3L, syncStatus.getSyncStatus().getBlockNumber());
        Assert.assertEquals(
                "4d3695d385084e56c814d078a987427e743e92b4164b42a8aa01bc24c82ab223",
                syncStatus.getSyncStatus().getGenesisHash());
        Assert.assertEquals(false, syncStatus.getSyncStatus().getIsSyncing());
        Assert.assertEquals(3, syncStatus.getSyncStatus().getKnownHighestNumber());
        Assert.assertEquals(
                "16b0b5b407cfb16a76c1d14d9231c1ae9422e4ec2032144edd12c4b59c33d7ea",
                syncStatus.getSyncStatus().getKnownLatestHash());
        Assert.assertEquals(
                "16b0b5b407cfb16a76c1d14d9231c1ae9422e4ec2032144edd12c4b59c33d7ea",
                syncStatus.getSyncStatus().getLatestHash());
        Assert.assertEquals(
                "63a2e45b2d84f83b32342f0741ffc51069c74fb7c82b8eb0247b12230d50169b86545ecf84420adeec86c57dbc48db1342f4afebc6a127b481eeaaa23722fff0",
                syncStatus.getSyncStatus().getNodeId());
        // check peers
        Assert.assertEquals(1, syncStatus.getSyncStatus().getPeers().size());
        Assert.assertEquals(3L, syncStatus.getSyncStatus().getPeers().get(0).getBlockNumber());
        Assert.assertEquals(
                "4d3695d385084e56c814d078a987427e743e92b4164b42a8aa01bc24c82ab223",
                syncStatus.getSyncStatus().getPeers().get(0).getGenesisHash());
        Assert.assertEquals(
                "16b0b5b407cfb16a76c1d14d9231c1ae9422e4ec2032144edd12c4b59c33d7ea",
                syncStatus.getSyncStatus().getPeers().get(0).getLatestHash());
        Assert.assertEquals(
                "6471685bb764ddd625c8855809ae23ae803fcf2890977def7c3d4353e22633cdea92471ba0859fc0538679c31b89577e1dd13b292d6538acff42ac4c599d5ce8",
                syncStatus.getSyncStatus().getPeers().get(0).getNodeId());
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
                "{\n" +
                        "  \"id\": 24,\n" +
                        "  \"jsonrpc\": \"2.0\",\n" +
                        "  \"result\": {\n" +
                        "    \"blockNumber\": 321453545645654,\n" +
                        "    \"failedTransactionCount\": 1231232131231,\n" +
                        "    \"transactionCount\": 65464768432423432456436\n" +
                        "  }\n" +
                        "}";
        TotalTransactionCount txCount =
                objectMapper.readValue(totalTxCountStr, TotalTransactionCount.class);
        Assert.assertEquals("321453545645654", txCount.getTotalTransactionCount().getBlockNumber());
        Assert.assertEquals("1231232131231", txCount.getTotalTransactionCount().getFailedTransactionCount());
        Assert.assertEquals("65464768432423432456436", txCount.getTotalTransactionCount().getTransactionCount());
    }

    @Test
    public void testTransactionReceipt() throws IOException, JniException {
        String receiptStr =
                "{\n" +
                        "        \"id\" : 8,\n" +
                        "        \"jsonrpc\" : \"2.0\",\n" +
                        "        \"result\" :\n" +
                        "        {\n" +
                        "                \"blockNumber\" : 2,\n" +
                        "                \"checksumContractAddress\" : \"\",\n" +
                        "                \"contractAddress\" : \"\",\n" +
                        "                \"from\" : \"0x3d20a4e26f41b57c2061e520c825fbfa5f321f22\",\n" +
                        "                \"gasUsed\" : \"19413\",\n" +
                        "                \"hash\" : \"0xb59cfe6ef607b72a6bab515042e0882213d179bd421afba353e2259b2a6396e4\",\n" +
                        "                \"input\" : \"0x2fe99bdc000000000000000000000000000000000000000000000000000000000000006000000000000000000000000000000000000000000000000000000000000000a000000000000000000000000000000000000000000000000000000000000000e0000000000000000000000000000000000000000000000000000000000000000574657374310000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000005746573743200000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000057465737433000000000000000000000000000000000000000000000000000000\",\n" +
                        "                \"logEntries\" :\n" +
                        "                [\n" +
                        "                        {\n" +
                        "                                \"address\" : \"6849f21d1e455e9f0712b1e99fa4fcd23758e8f1\",\n" +
                        "                                \"data\" : \"0x0000000000000000000000000000000000000000000000000000000000000001\",\n" +
                        "                                \"topics\" :\n" +
                        "                                [\n" +
                        "                                        \"0xc57b01fa77f41df77eaab79a0e2623fab2e7ae3e9530d9b1cab225ad65f2b7ce\"\n" +
                        "                                ]\n" +
                        "                        }\n" +
                        "                ],\n" +
                        "                \"message\" : \"\",\n" +
                        "                \"output\" : \"0x0000000000000000000000000000000000000000000000000000000000000001\",\n" +
                        "                \"status\" : 0,\n" +
                        "                \"to\" : \"0x6849f21d1e455e9f0712b1e99fa4fcd23758e8f1\",\n" +
                        "                \"transactionHash\" : \"0x0359a5588c5e9c9dcfd2f4ece850d6f4c41bc88e2c27cc051890f26ef0ef118f\",\n" +
                        "                \"transactionProof\" : null,\n" +
                        "                \"version\" : 0\n" +
                        "        }\n" +
                        "}";
        BcosTransactionReceipt transactionReceipt =
                objectMapper.readValue(receiptStr, BcosTransactionReceipt.class);
        Assert.assertEquals(
                "",
                transactionReceipt.getTransactionReceipt().getContractAddress());
        Assert.assertEquals(
                "0x3d20a4e26f41b57c2061e520c825fbfa5f321f22",
                transactionReceipt.getTransactionReceipt().getFrom());
        Assert.assertEquals(
                "19413", transactionReceipt.getTransactionReceipt().getGasUsed());
        Assert.assertEquals(
                "0x2fe99bdc000000000000000000000000000000000000000000000000000000000000006000000000000000000000000000000000000000000000000000000000000000a000000000000000000000000000000000000000000000000000000000000000e0000000000000000000000000000000000000000000000000000000000000000574657374310000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000005746573743200000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000057465737433000000000000000000000000000000000000000000000000000000",
                transactionReceipt.getTransactionReceipt().getInput());
        Assert.assertEquals(1, transactionReceipt.getTransactionReceipt().getLogEntries().size());
        Assert.assertEquals(
                "0x0000000000000000000000000000000000000000000000000000000000000001",
                transactionReceipt.getTransactionReceipt().getOutput());
        Assert.assertEquals(0, transactionReceipt.getTransactionReceipt().getStatus());
        Assert.assertEquals(
                "0x6849f21d1e455e9f0712b1e99fa4fcd23758e8f1",
                transactionReceipt.getTransactionReceipt().getTo());
        Assert.assertEquals(
                "0x0359a5588c5e9c9dcfd2f4ece850d6f4c41bc88e2c27cc051890f26ef0ef118f",
                transactionReceipt.getTransactionReceipt().getTransactionHash());
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        String calculateReceiptHash = transactionReceipt.getTransactionReceipt().calculateReceiptHash(cryptoSuite);
        Assert.assertEquals(calculateReceiptHash, transactionReceipt.getTransactionReceipt().getReceiptHash());

        TransactionReceipt transactionReceipt1 = transactionReceipt.getTransactionReceipt();
        String receiptJson = objectMapper.writeValueAsString(transactionReceipt1);
        long receiptDataWithJson = ReceiptBuilderJniObj.createReceiptDataWithJson(receiptJson);
        String encodeReceiptData = ReceiptBuilderJniObj.encodeReceiptData(receiptDataWithJson);
        String receiptDataToJsonObj = ReceiptBuilderJniObj.decodeReceiptDataToJsonObj(encodeReceiptData);
        TransactionReceipt receipt = objectMapper.readValue(receiptDataToJsonObj.getBytes(), TransactionReceipt.class);
        String receiptHash = receipt.calculateReceiptHash(cryptoSuite);
        Assert.assertEquals(receiptHash, transactionReceipt.getTransactionReceipt().getReceiptHash());

        String hexString = transactionReceipt1.writeToHexString();
        TransactionReceipt newReceipt = TransactionReceipt.readFromHexString(hexString);
        Assert.assertEquals(newReceipt.calculateReceiptHash(cryptoSuite), transactionReceipt1.getReceiptHash());

        ReceiptBuilderJniObj.destroyReceiptData(receiptDataWithJson);
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
                        + "    \"txProof\": [\n"
                        + "             \"6a6cefef8b48e455287a8c8694b06f4f7cb7950017ab048d6e6bdd8029f9f8c9\",\n"
                        + "             \"0a27c5ee02e618d919d228e6a754dc201d299c91c9e4420a48783bb6fcd09be5\"\n"
                        + "     ],\n"
                        + "    \"txReceiptProof\": [\n"
                        + "             \"6a6cefef8b48e455287a8c8694b06f4f7cb7950017ab048d6e6bdd8029f9f8c9\",\n"
                        + "             \"0a27c5ee02e618d919d228e6a754dc201d299c91c9e4420a48783bb6fcd09be5\"\n"
                        + "     ],\n"
                        + "    \"blockHash\": \"0xcd31b05e466bce99460b1ed70d6069fdfbb15e6eef84e9b9e4534358edb3899a\",\n"
                        + "    \"blockNumber\": \"5\",\n"
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
                5,
                receiptWithProof.getTransactionReceipt().getBlockNumber().intValue());
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
        Assert.assertNull(receiptWithProof.getTransactionReceipt().getTransactionProof());
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
    public void testECDSAGetTransactionAndCalculateHash() throws IOException, JniException {
        String transactionStr = "{\n" +
                "        \"id\" : 14,\n" +
                "        \"jsonrpc\" : \"2.0\",\n" +
                "        \"result\" :\n" +
                "        {\n" +
                "                \"abi\" : \"\",\n" +
                "                \"blockLimit\" : 521,\n" +
                "                \"chainID\" : \"chain0\",\n" +
                "                \"from\" : \"0x45ed2b4ee8546a2808f6ea30a41b4e04074b3f17\",\n" +
                "                \"groupID\" : \"group0\",\n" +
                "                \"hash\" : \"0x08bf58cb6974d6011b4c38dfba6042a5581d09bcdad608b35c01faab60e65f60\",\n" +
                "                \"importTime\" : 1670993547912,\n" +
                "                \"input\" : \"0xd91921ed000000000000000000000000000000000000000000000000000000000000016b\",\n" +
                "                \"nonce\" : \"93211541293974952730087763682517272771230763513029093659263835585118451786019\",\n" +
                "                \"signature\" : \"0xd718e60c0b5a4afd69de84ef1084e2753c729875b3c514189f986047d354b4050c20d151efeadd0cff4b4a90517bb3200247cbe2c7dd930cf02e90c9f5939bd500\",\n" +
                "                \"to\" : \"0xc8ead4b26b2c6ac14c9fd90d9684c9bc2cc40085\",\n" +
                "                \"txProof\" :\n" +
                "                [\n" +
                "                        \"0000000200000000000000000000000000000000000000000000000000000000\",\n" +
                "                        \"08bf58cb6974d6011b4c38dfba6042a5581d09bcdad608b35c01faab60e65f60\",\n" +
                "                        \"86778510cb760455127e398ed0b2eac8274b7e6525df7cb662157867c0e72750\",\n" +
                "                        \"0000000200000000000000000000000000000000000000000000000000000000\",\n" +
                "                        \"93a3e602ab5f7148e47e018d4cdc52c1fc2b4a03c7b8c38987220041283aa257\",\n" +
                "                        \"a3c098545fde6a64058504abf7275813d82119ca354774d2503801b3e88511ca\",\n" +
                "                        \"0000000200000000000000000000000000000000000000000000000000000000\",\n" +
                "                        \"104c1d168e573f877ef07af01ce783ca372622ffdac29d7c2f41aa14913c3ca6\",\n" +
                "                        \"bdb782bbae5f1cdbbd5fe59974a7ccfc07f40b3ded5d0d824913a19b70031872\",\n" +
                "                        \"0000000100000000000000000000000000000000000000000000000000000000\",\n" +
                "                        \"37690dafc3390d75fe6e129cdf82f5a54c0d996464ffab7cecef006a1fcaf301\",\n" +
                "                        \"0000000200000000000000000000000000000000000000000000000000000000\",\n" +
                "                        \"b7b5642f280766843b8a2d9bd1793c70490c84d9c6a6d2a1cd6df1d9777fbebb\",\n" +
                "                        \"a973f00b74eb91954ed01a2928c06e9deb7373f5dd9b4da4f7776915374e9f05\",\n" +
                "                        \"0000000200000000000000000000000000000000000000000000000000000000\",\n" +
                "                        \"ffd9e7ee47da53f53b3181dea758f164726d2fc0c7bce0ceda199b76099bf32c\",\n" +
                "                        \"a64c46ae5a8a882cc0a9e2ac5cdc8e6cace8ce3dade41130f92eb0d24c58af92\",\n" +
                "                        \"0000000200000000000000000000000000000000000000000000000000000000\",\n" +
                "                        \"d0153796c8cea76c08a8b1ab932e7bd8a1ae123d6a3b9bc442fe329fb1592aeb\",\n" +
                "                        \"0880b67fdb80a69dfaed8f92719ccff3258508db68cfdbd851d6096bdab69e01\",\n" +
                "                        \"0000000200000000000000000000000000000000000000000000000000000000\",\n" +
                "                        \"b5b23a170215d9f74d107b1458a78ca97a1a0414e2f2ccf0d7ebac7d0e205ba9\",\n" +
                "                        \"e0aa18057629fad83d26e0c16e9aaa771b2ac85cfbd3cdc21c95a293feec3acb\",\n" +
                "                        \"0000000200000000000000000000000000000000000000000000000000000000\",\n" +
                "                        \"4d8176441b4098451d72e0f8d7cb57fd547907fb6156b07960a777eeadcfb3d9\",\n" +
                "                        \"987b1e86ba418316cf6b68395b4036fcd3802fbb492ce19985451b9b5b052759\"\n" +
                "                ],\n" +
                "                \"version\" : 0\n" +
                "        }\n" +
                "}";
        BcosTransaction bcosTransaction = objectMapper.readValue(transactionStr.getBytes(), BcosTransaction.class);
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        Assert.assertEquals(
                bcosTransaction.getTransaction().get().calculateHash(cryptoSuite),
                bcosTransaction.getTransaction().get().getHash());

        JsonTransactionResponse jsonTransactionResponse = bcosTransaction.getTransaction().get();
        String txDataJson = objectMapper.writeValueAsString(jsonTransactionResponse);
        long transactionData = TransactionBuilderJniObj.createTransactionDataWithJson(txDataJson);
        String encodeTransactionData = TransactionBuilderJniObj.encodeTransactionData(transactionData);
        String jsonObj = TransactionBuilderJniObj.decodeTransactionDataToJsonObj(encodeTransactionData);
        System.out.println(jsonObj);
        JsonTransactionResponse bcosTransaction1 = objectMapper.readValue(jsonObj.getBytes(), JsonTransactionResponse.class);
        Assert.assertEquals(bcosTransaction1.calculateHash(cryptoSuite), bcosTransaction.getTransaction().get().getHash());

        String hexString = jsonTransactionResponse.writeToHexString();
        JsonTransactionResponse jsonTransactionResponse1 = JsonTransactionResponse.readFromHexString(hexString);
        Assert.assertEquals(jsonTransactionResponse1.calculateHash(cryptoSuite), jsonTransactionResponse.getHash());

        TransactionBuilderJniObj.destroyTransactionData(transactionData);

        List<String> txProof = bcosTransaction.getTransaction().get().getTxProof();
        String hash = bcosTransaction.getTransaction().get().getHash();
        String root = "ab67ef374352b65bd6f411b153c821a0c2607bf275ffe407e18bc1e6957f1bf2";
        boolean verifyMerkle = MerkleProofUtility.verifyMerkle(root, txProof, hash, cryptoSuite);
        Assert.assertTrue(verifyMerkle);

        boolean verifyFalse = MerkleProofUtility.verifyMerkle(root, txProof, root, cryptoSuite);
        Assert.assertFalse(verifyFalse);
    }

    @Test
    public void testBcosGroupInfo() throws IOException {
        String groupInfoStr = "{\n" +
                "    \"id\":1,\n" +
                "    \"jsonrpc\":\"2.0\",\n" +
                "    \"result\":{\n" +
                "        \"chainID\":\"chain\",\n" +
                "        \"genesisConfig\":\"{\\\"blockTxCountLimit\\\":1000,\\\"consensusLeaderPeriod\\\":1,\\\"consensusType\\\":\\\"pbft\\\",\\\"sealerList\\\":[{\\\"nodeID\\\":\\\"c18c224d3704d1d74228d26f4c37ffc9ec9c546f4ba4f005840b1c5d5b1d906cb6cc9229b03df089642e298b5f5be57e1402ea7bfb508530f9af3e500dd94886\\\",\\\"weight\\\":1},{\\\"nodeID\\\":\\\"1a58ede2927112ebbc0416fadb41bb537cb9a93afa76b59af4fd8bae9d3b642269316a8e6dcb75079f054184500da55361adf6fd98f7fafbe75c08bdaebf7e23\\\",\\\"weight\\\":1},{\\\"nodeID\\\":\\\"714765a10d9d067fc60f6f845a1d38f3f7269bb1f838c6136061b6adb2c578bce890c4d16a53af0ce9d8987605880b5f46d43cb11b6c1cbe8256a39a363fa0eb\\\",\\\"weight\\\":1},{\\\"nodeID\\\":\\\"6b084e82f58d009dbb17acbaf9655624b1415694b955a9644fffbbe734002c5ee66014cee2b16f507284102a35cbdde0d53bac23391b7b69615187c15b0dafee\\\",\\\"weight\\\":1}],\\\"txGasLimit\\\":300000000}\\n\",\n" +
                "        \"groupID\":\"group\",\n" +
                "        \"iniConfig\":\"\",\n" +
                "        \"nodeList\":[\n" +
                "            {\n" +
                "                \"iniConfig\":\"{\\\"binaryInfo\\\":{\\\"buildTime\\\":\\\"20220330 20:17:43\\\",\\\"gitCommitHash\\\":\\\"8c54a02f0159469ab39025f0dfe99b7c97565211\\\",\\\"platform\\\":\\\"Darwin/appleclang\\\",\\\"version\\\":\\\"3.0.0-rc4\\\"},\\\"chainID\\\":\\\"chain\\\",\\\"gatewayServiceName\\\":\\\"\\\",\\\"groupID\\\":\\\"group\\\",\\\"isAuthCheck\\\":false,\\\"isWasm\\\":false,\\\"nodeID\\\":\\\"1a58ede2927112ebbc0416fadb41bb537cb9a93afa76b59af4fd8bae9d3b642269316a8e6dcb75079f054184500da55361adf6fd98f7fafbe75c08bdaebf7e23\\\",\\\"nodeName\\\":\\\"1a58ede2927112ebbc0416fadb41bb537cb9a93afa76b59af4fd8bae9d3b642269316a8e6dcb75079f054184500da55361adf6fd98f7fafbe75c08bdaebf7e23\\\",\\\"rpcServiceName\\\":\\\"\\\",\\\"smCryptoType\\\":false}\\n\",\n" +
                "                \"name\":\"1a58ede2927112ebbc0416fadb41bb537cb9a93afa76b59af4fd8bae9d3b642269316a8e6dcb75079f054184500da55361adf6fd98f7fafbe75c08bdaebf7e23\",\n" +
                "                \"protocol\":{\n" +
                "                    \"maxVersion\":1,\n" +
                "                    \"minVersion\":1,\n" +
                "                    \"sysVersion\":3\n" +
                "                },\n" +
                "                \"serviceInfo\":[\n" +
                "                    {\n" +
                "                        \"serviceName\":\".\",\n" +
                "                        \"type\":2\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"serviceName\":\".\",\n" +
                "                        \"type\":3\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"serviceName\":\".\",\n" +
                "                        \"type\":4\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"serviceName\":\".\",\n" +
                "                        \"type\":5\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"serviceName\":\".\",\n" +
                "                        \"type\":8\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"type\":0\n" +
                "            },\n" +
                "            {\n" +
                "                \"iniConfig\":\"{\\\"binaryInfo\\\":{\\\"buildTime\\\":\\\"20220330 20:17:43\\\",\\\"gitCommitHash\\\":\\\"8c54a02f0159469ab39025f0dfe99b7c97565211\\\",\\\"platform\\\":\\\"Darwin/appleclang\\\",\\\"version\\\":\\\"3.0.0-rc4\\\"},\\\"chainID\\\":\\\"chain\\\",\\\"gatewayServiceName\\\":\\\"\\\",\\\"groupID\\\":\\\"group\\\",\\\"isAuthCheck\\\":false,\\\"isWasm\\\":false,\\\"nodeID\\\":\\\"c18c224d3704d1d74228d26f4c37ffc9ec9c546f4ba4f005840b1c5d5b1d906cb6cc9229b03df089642e298b5f5be57e1402ea7bfb508530f9af3e500dd94886\\\",\\\"nodeName\\\":\\\"c18c224d3704d1d74228d26f4c37ffc9ec9c546f4ba4f005840b1c5d5b1d906cb6cc9229b03df089642e298b5f5be57e1402ea7bfb508530f9af3e500dd94886\\\",\\\"rpcServiceName\\\":\\\"\\\",\\\"smCryptoType\\\":false}\\n\",\n" +
                "                \"name\":\"c18c224d3704d1d74228d26f4c37ffc9ec9c546f4ba4f005840b1c5d5b1d906cb6cc9229b03df089642e298b5f5be57e1402ea7bfb508530f9af3e500dd94886\",\n" +
                "                \"protocol\":{\n" +
                "                    \"maxVersion\":1,\n" +
                "                    \"minVersion\":1,\n" +
                "                    \"sysVersion\":3\n" +
                "                },\n" +
                "                \"serviceInfo\":[\n" +
                "                    {\n" +
                "                        \"serviceName\":\".\",\n" +
                "                        \"type\":2\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"serviceName\":\".\",\n" +
                "                        \"type\":3\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"serviceName\":\".\",\n" +
                "                        \"type\":4\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"serviceName\":\".\",\n" +
                "                        \"type\":5\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"serviceName\":\".\",\n" +
                "                        \"type\":8\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"type\":842087796\n" +
                "            }\n" +
                "        ],\n" +
                "        \"smCryptoType\":false,\n" +
                "        \"wasm\":false\n" +
                "    }\n" +
                "}";
        objectMapper.readValue(groupInfoStr.getBytes(), BcosGroupInfo.class);

        String groupListStr = "{\n" +
                "  \"id\": 33,\n" +
                "  \"jsonrpc\": \"2.0\",\n" +
                "  \"result\": [\n" +
                "    {\n" +
                "      \"chainID\": \"chain0\",\n" +
                "      \"genesisConfig\": \"{\\\"blockTxCountLimit\\\":1000,\\\"consensusLeaderPeriod\\\":1,\\\"consensusType\\\":\\\"pbft\\\",\\\"sealerList\\\":[{\\\"nodeID\\\":\\\"63a2e45b2d84f83b32342f0741ffc51069c74fb7c82b8eb0247b12230d50169b86545ecf84420adeec86c57dbc48db1342f4afebc6a127b481eeaaa23722fff0\\\",\\\"weight\\\":1},{\\\"nodeID\\\":\\\"6471685bb764ddd625c8855809ae23ae803fcf2890977def7c3d4353e22633cdea92471ba0859fc0538679c31b89577e1dd13b292d6538acff42ac4c599d5ce8\\\",\\\"weight\\\":1}],\\\"txGasLimit\\\":3000000000}\\n\",\n" +
                "      \"groupID\": \"group0\",\n" +
                "      \"iniConfig\": \"\",\n" +
                "      \"nodeList\": [\n" +
                "        {\n" +
                "          \"iniConfig\": \"{\\\"binaryInfo\\\":{\\\"buildTime\\\":\\\"20220607 15:32:55\\\",\\\"gitCommitHash\\\":\\\"fe4301244c50956db779d0965ed39064c7984f86\\\",\\\"platform\\\":\\\"Darwin/appleclang\\\",\\\"version\\\":\\\"3.0.0-rc4\\\"},\\\"chainID\\\":\\\"chain0\\\",\\\"gatewayServiceName\\\":\\\"\\\",\\\"groupID\\\":\\\"group0\\\",\\\"isAuthCheck\\\":false,\\\"isWasm\\\":false,\\\"nodeID\\\":\\\"6471685bb764ddd625c8855809ae23ae803fcf2890977def7c3d4353e22633cdea92471ba0859fc0538679c31b89577e1dd13b292d6538acff42ac4c599d5ce8\\\",\\\"nodeName\\\":\\\"6471685bb764ddd625c8855809ae23ae803fcf2890977def7c3d4353e22633cdea92471ba0859fc0538679c31b89577e1dd13b292d6538acff42ac4c599d5ce8\\\",\\\"rpcServiceName\\\":\\\"\\\",\\\"smCryptoType\\\":false}\\n\",\n" +
                "          \"microService\": false,\n" +
                "          \"name\": \"6471685bb764ddd625c8855809ae23ae803fcf2890977def7c3d4353e22633cdea92471ba0859fc0538679c31b89577e1dd13b292d6538acff42ac4c599d5ce8\",\n" +
                "          \"nodeID\": \"6471685bb764ddd625c8855809ae23ae803fcf2890977def7c3d4353e22633cdea92471ba0859fc0538679c31b89577e1dd13b292d6538acff42ac4c599d5ce8\",\n" +
                "          \"protocol\": {\n" +
                "            \"compatibilityVersion\": 4,\n" +
                "            \"maxSupportedVersion\": 1,\n" +
                "            \"minSupportedVersion\": 0\n" +
                "          },\n" +
                "          \"serviceInfo\": [\n" +
                "            {\n" +
                "              \"serviceName\": \"LedgerServiceObj\",\n" +
                "              \"type\": 2\n" +
                "            },\n" +
                "            {\n" +
                "              \"serviceName\": \"SchedulerServiceObj\",\n" +
                "              \"type\": 3\n" +
                "            },\n" +
                "            {\n" +
                "              \"serviceName\": \"FrontServiceObj\",\n" +
                "              \"type\": 4\n" +
                "            },\n" +
                "            {\n" +
                "              \"serviceName\": \"\",\n" +
                "              \"type\": 6\n" +
                "            },\n" +
                "            {\n" +
                "              \"serviceName\": \"\",\n" +
                "              \"type\": 7\n" +
                "            },\n" +
                "            {\n" +
                "              \"serviceName\": \"TxPoolServiceObj\",\n" +
                "              \"type\": 8\n" +
                "            }\n" +
                "          ],\n" +
                "          \"type\": 0\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        objectMapper.readValue(groupListStr.getBytes(), BcosGroupInfoList.class);
    }

    @Test
    public void testGroupNodeInfo() throws IOException {
        String groupNodeInfoStr = "{\n" +
                "    \"type\":0,\n" +
                "    \"iniConfig\":{\n" +
                "        \"binaryInfo\":{\n" +
                "            \"version\":\"3.0.0-rc4\",\n" +
                "            \"gitCommitHash\":\"fe4301244c50956db779d0965ed39064c7984f86\",\n" +
                "            \"platform\":\"Darwin/appleclang\",\n" +
                "            \"buildTime\":\"20220607 15:32:55\"\n" +
                "        },\n" +
                "        \"chainID\":\"chain0\",\n" +
                "        \"groupID\":\"group0\",\n" +
                "        \"smCryptoType\":false,\n" +
                "        \"nodeID\":\"6471685bb764ddd625c8855809ae23ae803fcf2890977def7c3d4353e22633cdea92471ba0859fc0538679c31b89577e1dd13b292d6538acff42ac4c599d5ce8\",\n" +
                "        \"nodeName\":\"6471685bb764ddd625c8855809ae23ae803fcf2890977def7c3d4353e22633cdea92471ba0859fc0538679c31b89577e1dd13b292d6538acff42ac4c599d5ce8\",\n" +
                "        \"rpcServiceName\":\"\",\n" +
                "        \"gatewayServiceName\":\"\",\n" +
                "        \"authCheck\":false,\n" +
                "        \"isWasm\":false,\n" +
                "        \"isAuthCheck\":false\n" +
                "    },\n" +
                "    \"name\":\"6471685bb764ddd625c8855809ae23ae803fcf2890977def7c3d4353e22633cdea92471ba0859fc0538679c31b89577e1dd13b292d6538acff42ac4c599d5ce8\",\n" +
                "    \"serviceInfoList\":null,\n" +
                "    \"protocol\":{\n" +
                "        \"compatibilityVersion\":4,\n" +
                "        \"minSupportedVersion\":0,\n" +
                "        \"maxSupportedVersion\":1\n" +
                "    }\n" +
                "}";
        objectMapper.readValue(groupNodeInfoStr.getBytes(), BcosGroupNodeInfo.class);
    }

    @Test
    public void testConsensusStatus() throws IOException {
        String consensusStr = "{\n" +
                "  \"id\": 35,\n" +
                "  \"jsonrpc\": \"2.0\",\n" +
                "  \"result\": \"{\\\"blockNumber\\\":3,\\\"changeCycle\\\":0,\\\"connectedNodeList\\\":2,\\\"consensusNodeList\\\":[{\\\"index\\\":0,\\\"nodeID\\\":\\\"63a2e45b2d84f83b32342f0741ffc51069c74fb7c82b8eb0247b12230d50169b86545ecf84420adeec86c57dbc48db1342f4afebc6a127b481eeaaa23722fff0\\\",\\\"weight\\\":1},{\\\"index\\\":1,\\\"nodeID\\\":\\\"6471685bb764ddd625c8855809ae23ae803fcf2890977def7c3d4353e22633cdea92471ba0859fc0538679c31b89577e1dd13b292d6538acff42ac4c599d5ce8\\\",\\\"weight\\\":1}],\\\"consensusNodesNum\\\":2,\\\"hash\\\":\\\"16b0b5b407cfb16a76c1d14d9231c1ae9422e4ec2032144edd12c4b59c33d7ea\\\",\\\"index\\\":1,\\\"isConsensusNode\\\":true,\\\"leaderIndex\\\":1,\\\"maxFaultyQuorum\\\":0,\\\"minRequiredQuorum\\\":2,\\\"nodeID\\\":\\\"6471685bb764ddd625c8855809ae23ae803fcf2890977def7c3d4353e22633cdea92471ba0859fc0538679c31b89577e1dd13b292d6538acff42ac4c599d5ce8\\\",\\\"timeout\\\":false,\\\"view\\\":3}\\n\"\n" +
                "}";
        objectMapper.readValue(consensusStr.getBytes(), ConsensusStatus.class);
    }
}
