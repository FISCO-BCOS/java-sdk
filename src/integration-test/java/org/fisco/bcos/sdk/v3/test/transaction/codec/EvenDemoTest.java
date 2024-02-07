package org.fisco.bcos.sdk.v3.test.transaction.codec;

import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.model.ConstantConfig;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.test.contract.solidity.EventSubDemo;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

public class EvenDemoTest {
    private static final String CONFIG_FILE =
            "src/integration-test/resources/" + ConstantConfig.CONFIG_FILE_NAME;
    private final Client client;

    public EvenDemoTest() {
        // init the sdk, and set the config options.
        BcosSDK sdk = BcosSDK.build(CONFIG_FILE);
        client = sdk.getClient("group0");
    }

    @Override
    protected void finalize() {
        try {
            super.finalize();
            client.stop();
            client.destroy();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testEvent() throws Exception {

        EventSubDemo eventSubDemo = EventSubDemo.deploy(client, client.getCryptoSuite().getCryptoKeyPair());
        Assert.assertEquals(eventSubDemo.getDeployReceipt().getStatus(), 0);
        Assert.assertTrue(StringUtils.isNotBlank(eventSubDemo.getContractAddress()));

        // transfer
        {
            TransactionReceipt transfer = eventSubDemo.transfer("test1", "test2", BigInteger.valueOf(100));
            Assert.assertEquals(transfer.getStatus(), 0);
            Assert.assertEquals(transfer.getLogEntries().size(), 4);
            List<EventSubDemo.TransferDataEventResponse> transferDataEvents = eventSubDemo.getTransferDataEvents(transfer);
            EventSubDemo.TransferDataEventResponse transferDataEventResponse = transferDataEvents.get(0);
            EventSubDemo.TransactionData transactionData = transferDataEventResponse.transaction_data.getValue().get(0);
            Assert.assertEquals(transactionData.from_account, "test1");
            Assert.assertEquals(transactionData.to_account, "test2");
            Assert.assertEquals(transactionData.amount, BigInteger.valueOf(100));
        }

        // echo
        {
            TransactionReceipt echo = eventSubDemo.echo(BigInteger.valueOf(100), BigInteger.valueOf(-100), "test");
            Assert.assertEquals(echo.getStatus(), 0);
            Assert.assertEquals(echo.getLogEntries().size(), 4);
            EventSubDemo.EchoUint256EventResponse echoUint256EventResponse = eventSubDemo.getEchoUint256Events(echo).get(0);
            Assert.assertEquals(echoUint256EventResponse.u, BigInteger.valueOf(100));
            EventSubDemo.EchoInt256EventResponse echoInt256EventResponse = eventSubDemo.getEchoInt256Events(echo).get(0);
            Assert.assertEquals(echoInt256EventResponse.i, BigInteger.valueOf(-100));
            EventSubDemo.EchoStringEventResponse echoStringEventResponse = eventSubDemo.getEchoStringEvents(echo).get(0);
            Assert.assertEquals(Hex.toHexString(echoStringEventResponse.s), client.getCryptoSuite().hash("test"));

            EventSubDemo.EchoUint256Int256StringEventResponse echoUint256Int256StringEventResponse = eventSubDemo.getEchoUint256Int256StringEvents(echo).get(0);
            Assert.assertEquals(echoUint256Int256StringEventResponse.u, BigInteger.valueOf(100));
            Assert.assertEquals(echoUint256Int256StringEventResponse.i, BigInteger.valueOf(-100));
            Assert.assertEquals(Hex.toHexString(echoUint256Int256StringEventResponse.s), client.getCryptoSuite().hash("test"));
        }
    }
}
