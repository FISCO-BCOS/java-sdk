package org.fisco.bcos.sdk.demo.amop.perf;

import org.fisco.bcos.sdk.amop.AmopCallback;
import org.fisco.bcos.sdk.amop.topic.AmopMsgIn;

public class AmopMsgCallback extends AmopCallback {
    private Long startTime = System.currentTimeMillis();

    private AmopMsgCollector collector = new AmopMsgCollector();

    public AmopMsgCollector getCollector() {
        return collector;
    }

    @Override
    public void receiveAmopMsg(AmopMsgIn msg) {
        Long cost = System.currentTimeMillis() - startTime;
        collector.onSubscribedTopicMsg(msg, cost);
    }
}
