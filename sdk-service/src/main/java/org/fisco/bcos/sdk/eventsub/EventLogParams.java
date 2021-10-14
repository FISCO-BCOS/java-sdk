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
import java.util.List;
import org.fisco.bcos.sdk.codec.abi.tools.TopicTools;
import org.fisco.bcos.sdk.utils.AddressUtils;
import org.fisco.bcos.sdk.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventLogParams {
    private static Logger logger = LoggerFactory.getLogger(EventLogParams.class);
    private String fromBlock;
    private String toBlock;
    private List<String> addresses;
    private List<Object> topics;

    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger logger) {
        EventLogParams.logger = logger;
    }

    public String getFromBlock() {
        return fromBlock;
    }

    public void setFromBlock(String fromBlock) {
        this.fromBlock = fromBlock;
    }

    public String getToBlock() {
        return toBlock;
    }

    public void setToBlock(String toBlock) {
        this.toBlock = toBlock;
    }

    public List<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }

    public List<Object> getTopics() {
        return topics;
    }

    public void setTopics(List<Object> topics) {
        this.topics = topics;
    }

    @Override
    public String toString() {
        return "EventLogFilterParams [fromBlock="
                + fromBlock
                + ", toBlock="
                + toBlock
                + ", addresses="
                + addresses
                + ", topics="
                + topics
                + "]";
    }

    public boolean checkAddresses() {
        if (getAddresses() == null) {
            return false;
        }
        for (String address : getAddresses()) {
            // check if address valid
            if (!AddressUtils.isValidAddress(address)) {
                return false;
            }
        }
        return true;
    }

    /**
     * check if topics valid
     *
     * @return
     */
    private boolean checkTopics() {

        // topics
        if ((getTopics() == null) || (getTopics().size() > TopicTools.MAX_NUM_TOPIC_EVENT_LOG)) {
            return false;
        }

        for (Object topic : getTopics()) {
            if (topic == null) {
                continue;
            }
            if (topic instanceof String) {
                // if valid topic
                if (((String) topic).isEmpty()) {
                    return false;
                }
            } else if (topic instanceof List) {
                for (Object o : (List<String>) topic) {
                    // if valid topic
                    if (((String) o).isEmpty()) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }

        return true;
    }

    private boolean checkToBlock(BigInteger blockNumber) {

        // fromBlock="latest" but toBlock is not
        // range in (latest,10), blockNumber 1 ok, 6 ok, 11 fail
        BigInteger toBlock = new BigInteger(getToBlock());
        return (blockNumber.compareTo(BigInteger.ONE) <= 0)
                || (blockNumber.compareTo(BigInteger.ONE) > 0
                        && (toBlock.compareTo(blockNumber)) > 0);
    }

    private boolean checkFromBlock(BigInteger blockNumber) {

        // toBlock="latest" but fromBlock is not
        // range in (10,latest), blockNumber 6 ok, 11 fail
        BigInteger fromBlock = new BigInteger(getFromBlock());
        // fromBlock is bigger than block number of the blockchain
        if (fromBlock.compareTo(BigInteger.ZERO) <= 0) {
            return false;
        }

        if (blockNumber.compareTo(BigInteger.ONE) > 0 && (fromBlock.compareTo(blockNumber) > 0)) {
            logger.info(
                    " future block range request, from: {}, to: {}", getFromBlock(), getToBlock());
        }

        return true;
    }

    private boolean checkFromToBlock(BigInteger blockNumber) throws NumberFormatException {

        // fromBlock and toBlock none is "latest"
        // range in (10,20), blockNumber 6 ok, 11 ok, 25 ok
        BigInteger fromBlock = new BigInteger(getFromBlock());
        BigInteger toBlock = new BigInteger(getToBlock());

        if ((fromBlock.compareTo(BigInteger.ZERO) <= 0) || (fromBlock.compareTo(toBlock) > 0)) {
            return false;
        } else {
            if (blockNumber.compareTo(BigInteger.ONE) > 0
                    && (fromBlock.compareTo(blockNumber) > 0)) {
                logger.info(
                        " future block range request, from: {}, to: {}",
                        getFromBlock(),
                        getToBlock());
            }
            return true;
        }
    }

    /**
     * check if valid fromBlock and toBlock
     *
     * @param blockNumber
     * @return
     */
    private boolean checkBlockRange(BigInteger blockNumber) {

        if (StringUtils.isEmpty(getFromBlock()) || StringUtils.isEmpty(getToBlock())) {
            return false;
        }

        boolean isValidBlockRange = true;

        try {
            if (getFromBlock().equals(TopicTools.LATEST)
                    && !getToBlock().equals(TopicTools.LATEST)) {
                // fromBlock="latest" but toBlock is not
                isValidBlockRange = checkToBlock(blockNumber);
            } else if (!getFromBlock().equals(TopicTools.LATEST)
                    && getToBlock().equals(TopicTools.LATEST)) {
                // toBlock="latest" but fromBlock is not
                isValidBlockRange = checkFromBlock(blockNumber);
            } else if (!getFromBlock().equals(TopicTools.LATEST)
                    && !getToBlock().equals(TopicTools.LATEST)) {
                isValidBlockRange = checkFromToBlock(blockNumber);
            }
        } catch (Exception e) {
            // invalid blockNumber format string
            isValidBlockRange = false;
        }

        return isValidBlockRange;
    }

    /**
     * @param blockNumber block number of blockchain
     * @return check 3 params
     */
    @SuppressWarnings("unchecked")
    public boolean checkParams(BigInteger blockNumber) {
        return checkBlockRange(blockNumber) && checkAddresses() && checkTopics();
    }
}
