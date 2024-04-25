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
package org.fisco.bcos.sdk.transaction.codec.encode;

import java.util.ArrayList;
import java.util.List;
import org.fisco.bcos.sdk.channel.model.EnumNodeVersion;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.signature.Signature;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.rlp.RlpEncoder;
import org.fisco.bcos.sdk.rlp.RlpList;
import org.fisco.bcos.sdk.rlp.RlpString;
import org.fisco.bcos.sdk.rlp.RlpType;
import org.fisco.bcos.sdk.transaction.model.po.RawTransaction;
import org.fisco.bcos.sdk.transaction.signer.RemoteSignProviderInterface;
import org.fisco.bcos.sdk.transaction.signer.TransactionSignerFactory;
import org.fisco.bcos.sdk.transaction.signer.TransactionSignerInterface;
import org.fisco.bcos.sdk.utils.Numeric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionEncoderService implements TransactionEncoderInterface {
    protected static Logger logger = LoggerFactory.getLogger(TransactionEncoderService.class);
    private final Signature signature;
    private final TransactionSignerInterface transactionSignerService;
    private final CryptoSuite cryptoSuite;

    public TransactionEncoderService(CryptoSuite cryptoSuite) {
        super();
        this.cryptoSuite = cryptoSuite;
        this.signature = cryptoSuite.getSignatureImpl();
        this.transactionSignerService = TransactionSignerFactory.createTransactionSigner(signature);
    }

    public TransactionEncoderService(
            CryptoSuite cryptoSuite, RemoteSignProviderInterface transactionSignProvider) {
        super();
        this.cryptoSuite = cryptoSuite;
        this.signature = cryptoSuite.getSignatureImpl();
        this.transactionSignerService =
                TransactionSignerFactory.createTransactionSigner(
                        transactionSignProvider, cryptoSuite.getCryptoTypeConfig());
    }

    @Override
    public String encodeAndSign(RawTransaction rawTransaction, CryptoKeyPair cryptoKeyPair) {
        return Numeric.toHexString(encodeAndSignBytes(rawTransaction, cryptoKeyPair));
    }

    @Override
    public byte[] encodeAndHashBytes(RawTransaction rawTransaction, CryptoKeyPair cryptoKeyPair) {
        return cryptoSuite.hash(encode(rawTransaction, null));
    }

    @Override
    public byte[] encodeAndSignBytes(RawTransaction rawTransaction, CryptoKeyPair cryptoKeyPair) {
        byte[] hash = encodeAndHashBytes(rawTransaction, cryptoKeyPair);
        SignatureResult result = transactionSignerService.sign(hash, cryptoKeyPair);
        return encode(rawTransaction, result);
    }

    @Override
    public byte[] encode(RawTransaction rawTransaction, SignatureResult signature) {
        List<RlpType> values = asRlpValues(rawTransaction, signature);
        RlpList rlpList = new RlpList(values);
        return RlpEncoder.encode(rlpList);
    }

    @Override
    public byte[] encodeTransactionReceipt(
            TransactionReceipt transactionReceipt, EnumNodeVersion.Version version) {
        List<RlpType> values = asRlpValues(transactionReceipt, version);
        RlpList rlpList = new RlpList(values);
        return RlpEncoder.encode(rlpList);
    }

    @Override
    public String encodeTransactionReceiptHexString(
            TransactionReceipt transactionReceipt, EnumNodeVersion.Version version) {
        byte[] rlpBytes = encodeTransactionReceipt(transactionReceipt, version);
        return Numeric.toHexString(rlpBytes);
    }

    /**
     * Rlp encode and sign based on RawTransaction
     *
     * @param rawTransaction data to be encoded
     * @param signatureResult signature result
     * @return encoded & signed transaction RLP values
     */
    public static List<RlpType> asRlpValues(
            RawTransaction rawTransaction, SignatureResult signatureResult) {
        List<RlpType> result = new ArrayList<>();
        result.add(RlpString.create(rawTransaction.getRandomid()));
        result.add(RlpString.create(rawTransaction.getGasPrice()));
        result.add(RlpString.create(rawTransaction.getGasLimit()));
        result.add(RlpString.create(rawTransaction.getBlockLimit()));
        // an empty to address (contract creation) should not be encoded as a numeric 0 value
        String to = rawTransaction.getTo();
        if (to != null && to.length() > 0) {
            // addresses that start with zeros should be encoded with the zeros included, not
            // as numeric values
            result.add(RlpString.create(Numeric.hexStringToByteArray(to)));
        } else {
            result.add(RlpString.create(""));
        }

        result.add(RlpString.create(rawTransaction.getValue()));

        // value field will already be hex encoded, so we need to convert into binary first
        byte[] data = Numeric.hexStringToByteArray(rawTransaction.getData());
        result.add(RlpString.create(data));

        // add extra data!!!
        result.add(RlpString.create(rawTransaction.getFiscoChainId()));
        result.add(RlpString.create(rawTransaction.getGroupId()));
        if (rawTransaction.getExtraData() == null || "".equals(rawTransaction.getExtraData())) {
            result.add(RlpString.create(""));
        } else {
            result.add(
                    RlpString.create(Numeric.hexStringToByteArray(rawTransaction.getExtraData())));
        }
        if (signatureResult != null) {
            result.addAll(signatureResult.encode());
        }
        return result;
    }

    /**
     * Rlp encode and sign based on TransactionReceipt
     *
     * @param transactionReceipt data to be encoded
     * @param version version
     * @return encoded & signed transactionReceipt RLP values
     */
    private static List<RlpType> asRlpValues(
            TransactionReceipt transactionReceipt, EnumNodeVersion.Version version) {
        List<RlpType> result = new ArrayList<>();
        // bytes
        result.add(RlpString.create(Numeric.hexStringToByteArray(transactionReceipt.getRoot())));

        // BigInteger
        result.add(RlpString.create(Numeric.toBigInt(transactionReceipt.getGasUsed())));

        result.add(
                RlpString.create(
                        Numeric.hexStringToByteArray(transactionReceipt.getContractAddress())));

        result.add(
                RlpString.create(Numeric.hexStringToByteArray(transactionReceipt.getLogsBloom())));

        result.add(RlpString.create(Numeric.toBigInt(transactionReceipt.getStatus())));

        result.add(RlpString.create(Numeric.hexStringToByteArray(transactionReceipt.getOutput())));
        // gas used
        if (version != null && version.getMinor() >= 9) {
            result.add(RlpString.create(Numeric.toBigInt(transactionReceipt.getRemainGas())));
        }
        // List
        List<TransactionReceipt.Logs> logs = transactionReceipt.getLogs();
        List<RlpType> logList = new ArrayList<>();
        for (TransactionReceipt.Logs log : logs) {
            List<RlpType> logUnit = new ArrayList<>();
            logUnit.add(RlpString.create(Numeric.hexStringToByteArray(log.getAddress())));

            List<String> topics = log.getTopics();
            List<RlpType> topicList = new ArrayList<>();
            for (String topic : topics) {
                topicList.add(RlpString.create(Numeric.hexStringToByteArray(topic)));
            }
            RlpList topicRlpList = new RlpList(topicList);
            logUnit.add(topicRlpList);
            logUnit.add(RlpString.create(Numeric.hexStringToByteArray(log.getData())));
            logList.add(new RlpList(logUnit));
        }
        RlpList logRlpList = new RlpList(logList);
        result.add(logRlpList);
        return result;
    }

    /** @return the signature */
    public Signature getSignature() {
        return signature;
    }
}
