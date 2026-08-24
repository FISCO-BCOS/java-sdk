package org.fisco.bcos.sdk.v3.test.transaction.manager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.AssembleEIP1559TransactionService;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.AssembleTransactionService;
import org.junit.Assert;
import org.junit.Test;

public class AssembleEIP1559TransactionServiceTest {
    @Test
    public void shouldInstantiateOutsideTransactionV1Package() {
        Client client = mock(Client.class);
        when(client.getCryptoSuite()).thenReturn(new CryptoSuite(CryptoType.ECDSA_TYPE));
        when(client.isWASM()).thenReturn(false);
        when(client.isSupportTransactionV1()).thenReturn(true);

        AssembleEIP1559TransactionService service = new AssembleEIP1559TransactionService(client);

        Assert.assertNotNull(service);
        Assert.assertTrue(service instanceof AssembleTransactionService);
    }
}
