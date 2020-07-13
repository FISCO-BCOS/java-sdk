package org.fisco.bcos.sdk.contract;

import java.util.List;
import org.fisco.bcos.sdk.eventsub.EventCallback;

public abstract class Contract {
    // Relay on transaction module and abi module

    /**
     * Subscribe event
     *
     * @param abi
     * @param bin
     * @param topic0
     * @param fromBlock
     * @param toBlock
     * @param otherTopics
     * @param callback
     */
    public void subscribeEvent(
            String abi,
            String bin,
            String topic0,
            String fromBlock,
            String toBlock,
            List<String> otherTopics,
            EventCallback callback) {}
}
