/*
 * Copyright 2014-2020  [fisco-dev]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package org.fisco.bcos.sdk.eventsub;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.fisco.bcos.sdk.codec.abi.tools.TopicTools;

public class EventSubParams {

    private BigInteger fromBlock = BigInteger.valueOf(-1);
    private BigInteger toBlock = BigInteger.valueOf(-1);
    private List<String> addresses = new ArrayList<>();
    private List<List<String>> topics =
            new ArrayList<List<String>>() {
                {
                    add(null);
                    add(null);
                    add(null);
                    add(null);
                }
            };

    public BigInteger getFromBlock() {
        return fromBlock;
    }

    public void setFromBlock(BigInteger fromBlock) {
        this.fromBlock = fromBlock;
    }

    public BigInteger getToBlock() {
        return toBlock;
    }

    public void setToBlock(BigInteger toBlock) {
        this.toBlock = toBlock;
    }

    public List<String> getAddresses() {
        return addresses;
    }

    public List<List<String>> getTopics() {
        return topics;
    }

    public boolean addAddress(String addr) {
        // TODO: check address valid
        return this.addresses.add(addr);
    }

    public boolean addTopic(int index, String topic) {
        if (!TopicTools.validTopic(topic)) {
            return false;
        }

        if (index >= TopicTools.MAX_NUM_TOPIC_EVENT_LOG) {
            return false;
        }

        List<String> strings = this.topics.get(index);
        if (strings == null) {
            strings = new ArrayList<>();
            this.topics.set(index, strings);
        }

        strings.add(topic);
        return true;
    }

    /** @return check params */
    @SuppressWarnings("unchecked")
    public boolean checkParams() {
        if (fromBlock.compareTo(BigInteger.ZERO) > 0 && toBlock.compareTo(BigInteger.ZERO) > 0) {
            return fromBlock.compareTo(toBlock) <= 0;
        }
        return true;
    }

    @Override
    public String toString() {
        return "EventLogParams{"
                + "fromBlock="
                + fromBlock
                + ", toBlock="
                + toBlock
                + ", addresses="
                + addresses
                + ", topics="
                + topics
                + '}';
    }
}
