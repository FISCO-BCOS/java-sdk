/*
 * Copyright 2014-2020  [fisco-dev]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package org.fisco.bcos.sdk.transaction.builder;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.model.tars.TransactionData;
import org.fisco.bcos.sdk.codec.ABICodec;
import org.fisco.bcos.sdk.codec.ABICodecException;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.transaction.codec.encode.TransactionEncoderService;
import org.fisco.bcos.sdk.utils.Hex;

public class TransactionBuilderService implements TransactionBuilderInterface {
    private Client client;

    /**
     * create TransactionBuilderService
     *
     * @param client the client object
     */
    public TransactionBuilderService(Client client) {
        super();
        this.client = client;
    }

    /**
     * Create fisco bcos transaction
     *
     * @param cryptoSuite @See CryptoSuite
     * @param groupId the group that need create transaction
     * @param chainId default 1
     * @param blockLimit cached limited block number
     * @param abi compiled contract abi
     * @param to target address
     * @param functionName function name
     * @param params object list of function parameter
     * @param isWasm whether the invoked contract is a Wasm contract
     * @return TransactionData the signed transaction hexed string
     */
    public static String createSignedTransaction(
            CryptoSuite cryptoSuite,
            String groupId,
            String chainId,
            BigInteger blockLimit,
            String abi,
            String to,
            String functionName,
            List<Object> params,
            boolean isWasm)
            throws ABICodecException {
        ABICodec abiCodec = new ABICodec(cryptoSuite, isWasm);
        byte[] data = abiCodec.encodeMethod(abi, functionName, params);
        Random r = ThreadLocalRandom.current();
        BigInteger randomId = new BigInteger(250, r);
        TransactionData rawTransaction =
                new TransactionData(
                        0, chainId, groupId, blockLimit.intValue(), randomId.toString(), to, data);

        TransactionEncoderService transactionEncoder = new TransactionEncoderService(cryptoSuite);
        return transactionEncoder.encodeAndSign(rawTransaction, cryptoSuite.getCryptoKeyPair());
    }

    @Override
    public TransactionData createTransaction(
            String to, byte[] data, String chainId, String groupId) {

        return this.createTransaction(this.client.getBlockLimit(), to, data, chainId, groupId);
    }

    @Override
    public TransactionData createTransaction(
            BigInteger blockLimit, String to, byte[] data, String chainId, String groupId) {
        Random r = ThreadLocalRandom.current();
        BigInteger randomId = new BigInteger(250, r);
        return new TransactionData(
                0,
                chainId,
                groupId,
                blockLimit.intValue(),
                randomId.toString(),
                Hex.trimPrefix(to),
                data);
    }

    /** @return the client */
    public Client getClient() {
        return this.client;
    }

    /** @param client the client to set */
    public void setClient(Client client) {
        this.client = client;
    }
}
