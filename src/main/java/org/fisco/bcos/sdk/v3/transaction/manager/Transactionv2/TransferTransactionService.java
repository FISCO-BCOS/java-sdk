package org.fisco.bcos.sdk.v3.transaction.manager.Transactionv2;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.tools.Convert;

public class TransferTransactionService {
    // This is the cost to send Ether between parties
    public static final BigInteger GAS_LIMIT = BigInteger.valueOf(21000);

    private ProxySignTransactionManager transactionManager;

    public TransferTransactionService(ProxySignTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    TransferTransactionService(Client client) {
        this.transactionManager = new ProxySignTransactionManager(client);
    }

    public TransactionReceipt sendFunds(String to, BigDecimal value, Convert.Unit unit) {
        BigDecimal weiValue = Convert.toWei(value, unit);
        return transactionManager.sendTransaction(to, "", weiValue.toBigIntegerExact());
    }

    public TransactionReceipt sendFunds(
            CryptoSuite cryptoSuite, String to, BigDecimal value, Convert.Unit unit) {
        BigDecimal weiValue = Convert.toWei(value, unit);
        return transactionManager.sendTransaction(
                cryptoSuite, to, "", weiValue.toBigIntegerExact(), "", false);
    }
}
