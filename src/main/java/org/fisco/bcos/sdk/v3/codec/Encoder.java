package org.fisco.bcos.sdk.v3.codec;

import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.hash.Hash;
import org.fisco.bcos.sdk.v3.crypto.hash.Keccak256;
import org.fisco.bcos.sdk.v3.crypto.hash.SM3Hash;
import org.fisco.bcos.sdk.v3.model.CryptoType;

public class Encoder {
    private CryptoSuite cryptoSuite;

    private Hash hashImpl;

    @Deprecated
    public Encoder(CryptoSuite cryptoSuite) {
        this.cryptoSuite = cryptoSuite;
        this.hashImpl = cryptoSuite.getHashImpl();
    }

    public Encoder(Hash hash) {
        // for compatibility
        if (hashImpl instanceof SM3Hash) {
            this.cryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
        }
        if (hashImpl instanceof Keccak256) {
            this.cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        }
        this.hashImpl = hash;
    }

    /** @return the cryptoSuite */
    @Deprecated
    public CryptoSuite getCryptoSuite() {
        return this.cryptoSuite;
    }

    /** @param cryptoSuite the cryptoSuite to set */
    @Deprecated
    public void setCryptoSuite(CryptoSuite cryptoSuite) {
        this.cryptoSuite = cryptoSuite;
        this.hashImpl = cryptoSuite.getHashImpl();
    }

    public Hash getHashImpl() {
        return hashImpl;
    }

    public void setHashImpl(Hash hashImpl) {
        this.hashImpl = hashImpl;
    }
}
