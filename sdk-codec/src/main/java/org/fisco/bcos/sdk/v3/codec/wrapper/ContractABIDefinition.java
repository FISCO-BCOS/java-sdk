package org.fisco.bcos.sdk.v3.codec.wrapper;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContractABIDefinition {

    private static final Logger logger = LoggerFactory.getLogger(ContractABIDefinition.class);

    private ABIDefinition constructor = null;
    private Map<String, List<ABIDefinition>> functions = new HashMap<>();
    private Map<String, List<ABIDefinition>> events = new HashMap<>();
    // method id => function
    private Map<ByteBuffer, ABIDefinition> methodIDToFunctions = new HashMap<>();
    // event topic => topic
    private Map<ByteBuffer, ABIDefinition> eventTopicToEvents = new HashMap<>();
    private final CryptoSuite cryptoSuite;

    public ContractABIDefinition(CryptoSuite cryptoSuite) {
        this.cryptoSuite = cryptoSuite;
    }

    public ABIDefinition getConstructor() {
        return this.constructor;
    }

    public void setConstructor(ABIDefinition constructor) {
        this.constructor = constructor;
    }

    public Map<String, List<ABIDefinition>> getFunctions() {
        return this.functions;
    }

    public void setFunctions(Map<String, List<ABIDefinition>> functions) {
        this.functions = functions;
    }

    public Map<String, List<ABIDefinition>> getEvents() {
        return this.events;
    }

    public void setEvents(Map<String, List<ABIDefinition>> events) {
        this.events = events;
    }

    public Map<ByteBuffer, ABIDefinition> getMethodIDToFunctions() {
        return this.methodIDToFunctions;
    }

    public void setMethodIDToFunctions(Map<ByteBuffer, ABIDefinition> methodIDToFunctions) {
        this.methodIDToFunctions = methodIDToFunctions;
    }

    public Map<ByteBuffer, ABIDefinition> getEventTopicToEvents() {
        return this.eventTopicToEvents;
    }

    public void setEventTopicToEvents(Map<ByteBuffer, ABIDefinition> eventTopicToEvents) {
        this.eventTopicToEvents = eventTopicToEvents;
    }

    public void addFunction(String name, ABIDefinition abiDefinition) {

        List<ABIDefinition> abiDefinitions = this.functions.get(name);
        if (abiDefinitions == null) {
            this.functions.put(name, new ArrayList<>());
            abiDefinitions = this.functions.get(name);
        } else {
            logger.info(" overload method ??? name: {}, abiDefinition: {}", name, abiDefinition);
        }
        abiDefinitions.add(abiDefinition);

        // calculate method id and add abiDefinition to methodIdToFunctions
        byte[] methodId = abiDefinition.getMethodId(this.cryptoSuite);
        this.methodIDToFunctions.put(ByteBuffer.wrap(methodId), abiDefinition);

        logger.debug(
                " name: {}, methodId: {}, methodSignature: {}, abi: {}",
                name,
                methodId,
                abiDefinition.getMethodSignatureAsString(),
                abiDefinition);
    }

    public void addEvent(String name, ABIDefinition abiDefinition) {
        this.events.putIfAbsent(name, new ArrayList<>());
        List<ABIDefinition> abiDefinitions = this.events.get(name);
        abiDefinitions.add(abiDefinition);
        logger.debug(" name: {}, abi: {}", name, abiDefinition);

        // calculate method id and add abiDefinition to eventTopicToEvents
        byte[] methodId = abiDefinition.getMethodId(this.cryptoSuite);
        this.eventTopicToEvents.put(ByteBuffer.wrap(methodId), abiDefinition);
    }

    public ABIDefinition getABIDefinitionByMethodId(byte[] methodId) {
        return this.methodIDToFunctions.get(ByteBuffer.wrap(methodId));
    }

    public ABIDefinition getABIDefinitionByEventTopic(String topic) {
        // FIXME: check topic string is hex
        return this.eventTopicToEvents.get(Hex.decode(topic));
    }
}
