package org.fisco.bcos.sdk.contract;

import org.fisco.bcos.sdk.eventsub.EventCallback;

import java.util.List;

public abstract class Contract {
    // TODO
    // Relay on transaction module and abi module

    /**
     * Subscribe event
     * @param abi
     * @param bin
     * @param topic0
     * @param fromBlock
     * @param toBlock
     * @param otherTopics
     * @param callback
     */
    public void subscribeEvent(String abi,
                               String bin,
                               String topic0,
                               String fromBlock,
                               String toBlock,
                               List<String> otherTopics,
                               EventCallback callback){
        //TODO
    }
}
