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
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigInteger;
import org.fisco.bcos.sdk.client.protocol.response.BcosBlockHeader;
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
}
