package org.fisco.bcos.sdk.demo.event;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.eventsub.EventCallback;
import org.fisco.bcos.sdk.eventsub.EventLogParams;
import org.fisco.bcos.sdk.eventsub.EventSubscribe;
import org.fisco.bcos.sdk.model.ConstantConfig;
import org.fisco.bcos.sdk.model.EventLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Listen {

    private static final Logger logger = LoggerFactory.getLogger(Listen.class);

    private static void allEventLog() {
        String configFileName = ConstantConfig.CONFIG_FILE_NAME;
        URL configUrl = Listen.class.getClassLoader().getResource(configFileName);
        if (configUrl == null) {
            System.out.println("The configFile " + configFileName + " doesn't exist!");
            return;
        }
        String configFile = configUrl.getPath();
        BcosSDK sdk = BcosSDK.build(configFile);
        Client client = sdk.getClient(Integer.valueOf(1));
        EventSubscribe eventSubscribe = sdk.getEventSubscribe(client.getGroupId());
        eventSubscribe.start();

        EventLogParams eventLogParams = new EventLogParams();
        eventLogParams.setFromBlock("latest");
        eventLogParams.setToBlock("latest");
        eventLogParams.setAddresses(new ArrayList<>());
        eventLogParams.setTopics(new ArrayList<>());

        class SubscribeCallback implements EventCallback {
            public transient Semaphore semaphore = new Semaphore(1, true);

            SubscribeCallback() {
                try {
                    semaphore.acquire(1);
                } catch (InterruptedException e) {
                    logger.error("error :", e);
                    Thread.currentThread().interrupt();
                }
            }

            @Override
            public void onReceiveLog(int status, List<EventLog> logs) {
                String str = "status in onReceiveLog : " + status;
                logger.debug(str);
                semaphore.release();
                if (logs != null) {
                    for (EventLog log : logs) {
                        logger.debug(
                                " blockNumber:"
                                        + log.getBlockNumber()
                                        + ",txIndex:"
                                        + log.getTransactionIndex()
                                        + " data:"
                                        + log.getData());
                    }
                }
            }
        }

        SubscribeCallback subscribeEventCallback = new SubscribeCallback();
        String registerId = eventSubscribe.subscribeEvent(eventLogParams, subscribeEventCallback);
        System.out.print("subscribe event, registerId is " + registerId);

        while (true) {}
    }

    public static void main(String[] args) {
        allEventLog();
    }
}
