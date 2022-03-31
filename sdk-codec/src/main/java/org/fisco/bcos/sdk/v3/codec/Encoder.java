package org.fisco.bcos.sdk.v3.codec;

import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;

public class Encoder {
    private CryptoSuite cryptoSuite;

    public Encoder(CryptoSuite cryptoSuite) {
        this.cryptoSuite = cryptoSuite;
    }

    /** @return the cryptoSuite */
    public CryptoSuite getCryptoSuite() {
        return this.cryptoSuite;
    }

    /** @param cryptoSuite the cryptoSuite to set */
    public void setCryptoSuite(CryptoSuite cryptoSuite) {
        this.cryptoSuite = cryptoSuite;
    }
}
