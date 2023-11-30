package org.fisco.bcos.sdk.v3.transaction.signer;

import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionBuilderJniObj;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.crypto.signature.ECDSASignatureResult;
import org.fisco.bcos.sdk.v3.crypto.signature.SM2SignatureResult;
import org.fisco.bcos.sdk.v3.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.utils.Hex;

public class TransactionJniSignerService
        implements TransactionSignerInterface, AsyncTransactionSignercInterface {
    private CryptoKeyPair cryptoKeyPair;

    public TransactionJniSignerService(CryptoKeyPair cryptoKeyPair) {
        this.cryptoKeyPair = cryptoKeyPair;
    }

    /**
     * sign raw transaction hash string and get raw signature result
     *
     * @param hash raw transaction hash byte array to be signed
     * @param cryptoKeyPair keypair
     * @return signature result, hex string
     */
    @Override
    public String signWithRawResult(String hash, CryptoKeyPair cryptoKeyPair) {
        try {
            return TransactionBuilderJniObj.signTransactionDataHash(
                    cryptoKeyPair.getJniKeyPair(), hash);
        } catch (JniException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * sign raw transaction hash byte array and get raw signature result
     *
     * @param hash raw transaction hash byte array to be signed
     * @param cryptoKeyPair keypair
     * @return signature result, hex string
     */
    @Override
    public String signWithRawResult(byte[] hash, CryptoKeyPair cryptoKeyPair) {
        return signWithRawResult(Hex.toHexString(hash), cryptoKeyPair);
    }

    /**
     * sign raw transaction hash string and get signature result
     *
     * @param hash raw transaction hash string to be signed
     * @param cryptoKeyPair keypair
     * @return signature result
     */
    @Override
    public SignatureResult sign(String hash, CryptoKeyPair cryptoKeyPair) {
        try {
            String signTransaction =
                    TransactionBuilderJniObj.signTransactionDataHash(
                            cryptoKeyPair.getJniKeyPair(), hash);
            if (cryptoKeyPair.getKeyType() == CryptoType.ECDSA_TYPE) {
                return new ECDSASignatureResult(signTransaction);
            }
            if (cryptoKeyPair.getKeyType() == CryptoType.SM_TYPE) {
                return new SM2SignatureResult(cryptoKeyPair.getHexPublicKey(), signTransaction);
            }
        } catch (JniException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * sign raw transaction hash byte array and get signature result
     *
     * @param hash raw transaction hash byte array to be signed
     * @param cryptoKeyPair keypair
     * @return signature result
     */
    @Override
    public SignatureResult sign(byte[] hash, CryptoKeyPair cryptoKeyPair) {
        return sign(Hex.toHexString(hash), cryptoKeyPair);
    }

    /**
     * sign raw transaction hash string and get signature result
     *
     * @param hash raw transaction hash string to be signed
     * @param transactionSignCallback after signed, callback hook
     */
    @Override
    public void signAsync(byte[] hash, RemoteSignCallbackInterface transactionSignCallback) {
        SignatureResult sign = sign(hash, cryptoKeyPair);
        transactionSignCallback.handleSignedTransaction(sign);
    }
}
