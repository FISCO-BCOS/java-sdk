package org.fisco.bcos.sdk.v3.transaction.manager.Transactionv2;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.tools.Convert;

public class TransferTransactionService {
    // This is the cost to send Ether between parties
    public static final BigInteger GAS_LIMIT = BigInteger.valueOf(21000);

    private ProxySignTransactionManager transactionManager;

    private Client client;

    public TransferTransactionService(ProxySignTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    TransferTransactionService(Client client) {

        this.client = client;
        this.transactionManager = new ProxySignTransactionManager(client);
    }

    public TransactionReceipt sendFunds(String to, BigDecimal value, Convert.Unit unit)
            throws JniException {
        BigDecimal weiValue = Convert.toWei(value, unit);
        return transactionManager.sendTransaction(to, "", weiValue.toBigIntegerExact());
    }

    public TransactionReceipt sendFunds(
            CryptoSuite cryptoSuite, String to, BigDecimal value, Convert.Unit unit)
            throws JniException {
        BigDecimal weiValue = Convert.toWei(value, unit);
        ProxySignTransactionManager proxySignTransactionManager =
                new ProxySignTransactionManager(
                        client,
                        (hash, transactionSignCallback) -> {
                            SignatureResult sign =
                                    cryptoSuite.sign(hash, cryptoSuite.getCryptoKeyPair());
                            transactionSignCallback.handleSignedTransaction(sign);
                        });
        return proxySignTransactionManager.sendTransaction(to, "", weiValue.toBigIntegerExact());
    }
}
