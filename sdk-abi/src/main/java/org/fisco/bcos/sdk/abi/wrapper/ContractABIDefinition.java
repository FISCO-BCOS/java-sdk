package org.fisco.bcos.sdk.abi.wrapper;

import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.utils.Numeric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContractABIDefinition {

    private static final Logger logger = LoggerFactory.getLogger(ContractABIDefinition.class);

    private ABIDefinition constructor = null;
    private Map<String, List<ABIDefinition>> functions = new HashMap<>();
    private Map<String, List<ABIDefinition>> events = new HashMap<>();
    // method id => function
    private Map<String, ABIDefinition> methodIDToFunctions = new HashMap<>();
    private ABIDefinition fallbackFunction;
    private ABIDefinition receiveFunction;
    // event topic => event
    private Map<String, ABIDefinition> eventTopicToEvents = new HashMap<>();
    private CryptoSuite cryptoSuite;

    public ContractABIDefinition(CryptoSuite cryptoSuite) {
        this.cryptoSuite = cryptoSuite;
    }

    public ABIDefinition getConstructor() {
        return constructor;
    }

    public void setConstructor(ABIDefinition constructor) {
        this.constructor = constructor;
    }

    public Map<String, List<ABIDefinition>> getFunctions() {
        return functions;
    }

    public void setFunctions(Map<String, List<ABIDefinition>> functions) {
        this.functions = functions;
    }

    public Map<String, List<ABIDefinition>> getEvents() {
        return events;
    }

    public void setEvents(Map<String, List<ABIDefinition>> events) {
        this.events = events;
    }

    public Map<String, ABIDefinition> getMethodIDToFunctions() {
        return methodIDToFunctions;
    }

    public void setMethodIDToFunctions(Map<String, ABIDefinition> methodIDToFunctions) {
        this.methodIDToFunctions = methodIDToFunctions;
    }

    public Map<String, ABIDefinition> getEventTopicToEvents() {
        return eventTopicToEvents;
    }

    public void setEventTopicToEvents(Map<String, ABIDefinition> eventTopicToEvents) {
        this.eventTopicToEvents = eventTopicToEvents;
    }

    public void addFunction(String name, ABIDefinition abiDefinition) {

        List<ABIDefinition> abiDefinitions = functions.get(name);
        if (abiDefinitions == null) {
            functions.put(name, new ArrayList<>());
            abiDefinitions = functions.get(name);
        } else {
            logger.info(" overload method ??? name: {}, abiDefinition: {}", name, abiDefinition);
        }
        abiDefinitions.add(abiDefinition);

        // calculate method id and add abiDefinition to methodIdToFunctions
        String methodId = abiDefinition.getMethodId(cryptoSuite);
        methodIDToFunctions.put(methodId, abiDefinition);

        logger.info(
                " name: {}, methodId: {}, methodSignature: {}, abi: {}",
                name,
                methodId,
                abiDefinition.getMethodSignatureAsString(),
                abiDefinition);
    }

    public void addEvent(String name, ABIDefinition abiDefinition) {
        events.putIfAbsent(name, new ArrayList<>());
        List<ABIDefinition> abiDefinitions = events.get(name);
        abiDefinitions.add(abiDefinition);
        logger.info(" name: {}, abi: {}", name, abiDefinition);

        // calculate event topic and add abiDefinition to eventTopicToEvents
        String eventTopic = abiDefinition.getEventTopic(cryptoSuite);
        eventTopicToEvents.put(eventTopic, abiDefinition);
    }

    public ABIDefinition getABIDefinitionByMethodId(String methodId) {
        return methodIDToFunctions.get(Numeric.prependHexPrefix(methodId));
    }

    public ABIDefinition getABIDefinitionByEventTopic(String eventTopic) {
        return eventTopicToEvents.get(Numeric.prependHexPrefix(eventTopic));
    }

    public ABIDefinition getFallbackFunction() {
        return fallbackFunction;
    }

    public void setFallbackFunction(ABIDefinition fallbackFunction) {
        this.fallbackFunction = fallbackFunction;
    }

    public boolean hasFallbackFunction() {
        return this.fallbackFunction != null;
    }

    public boolean hasReceiveFunction() {
        return this.receiveFunction != null;
    }

    public ABIDefinition getReceiveFunction() {
        return receiveFunction;
    }

    public void setReceiveFunction(ABIDefinition receiveFunction) {
        this.receiveFunction = receiveFunction;
    }
}
