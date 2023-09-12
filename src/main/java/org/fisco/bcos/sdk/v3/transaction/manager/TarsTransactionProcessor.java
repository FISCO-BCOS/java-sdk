package org.fisco.bcos.sdk.v3.transaction.manager;

import org.fisco.bcos.sdk.tars.KeyPairInterface;
import org.fisco.bcos.sdk.tars.SWIGTYPE_p_bcos__bytesConstRef;
import org.fisco.bcos.sdk.tars.SWIGTYPE_p_std__shared_ptrT_KeyInterface_t;
import org.fisco.bcos.sdk.tars.SWIGTYPE_p_std__unique_ptrT_bcos__crypto__KeyPairInterface_t;
import org.fisco.bcos.sdk.tars.SWIGTYPE_p_std__vectorT_unsigned_char_t;
import org.fisco.bcos.sdk.tars.Transaction;
import org.fisco.bcos.sdk.tars.bcos;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.TarsClient;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TarsTransactionProcessor extends TransactionProcessor {
    private TarsClient tarsClient;
    private static final Logger logger = LoggerFactory.getLogger(TarsTransactionProcessor.class);

    public TarsTransactionProcessor(
            Client client, CryptoKeyPair cryptoKeyPair, String groupId, String chainId) {
        super(client, cryptoKeyPair, groupId, chainId);
        tarsClient = (TarsClient) client;
    }

    @Override
    public String sendTransactionAsync(
            String to,
            byte[] data,
            CryptoKeyPair cryptoKeyPair,
            int txAttribute,
            TransactionCallback callback) {
        String extraData = client.getExtraData();

        String hexPrivateKey = cryptoKeyPair.getHexPrivateKey();
        SWIGTYPE_p_bcos__bytesConstRef hexPrivateKeyRef = bcos.toBytesConstRef(hexPrivateKey);
        SWIGTYPE_p_std__vectorT_unsigned_char_t privateKey = bcos.fromHex(hexPrivateKeyRef);
        SWIGTYPE_p_bcos__bytesConstRef privateKeyRef = bcos.toBytesConstRef(privateKey);
        SWIGTYPE_p_std__shared_ptrT_KeyInterface_t key =
                tarsClient
                        .getTransactionFactory()
                        .cryptoSuite()
                        .keyFactory()
                        .createKey(privateKeyRef);
        SWIGTYPE_p_std__unique_ptrT_bcos__crypto__KeyPairInterface_t sharedKeyPair =
                tarsClient.getTransactionFactory().cryptoSuite().signatureImpl().createKeyPair(key);
        KeyPairInterface keyPair = bcos.pointerToReference(sharedKeyPair);

        SWIGTYPE_p_std__vectorT_unsigned_char_t input = bcos.toBytes(data);

        Transaction transaction =
                tarsClient
                        .getTransactionFactory()
                        .createTransaction(
                                0,
                                to,
                                input,
                                tarsClient.getTarsRPCClient().generateNonce(),
                                500,
                                client.getChainId(),
                                client.getGroup(),
                                0,
                                keyPair,
                                "");
        transaction.setExtraData(extraData);
        transaction.setAttribute(txAttribute);

        tarsClient.sendTransactionAsync(transaction, callback);

        return bcos.toHex(transaction.hash());
    }
}
