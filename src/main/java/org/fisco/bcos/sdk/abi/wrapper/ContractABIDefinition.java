package org.fisco.bcos.sdk.abi.wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.utils.Numeric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContractABIDefinition {

    private static final Logger logger = LoggerFactory.getLogger(ContractABIDefinition.class);

    private ABIDefinition constructor = null;
    private Map<String, List<ABIDefinition>> functions = new HashMap<>();
    private Map<String, List<ABIDefinition>> events = new HashMap<>();
    // method id => function
    private Map<String, ABIDefinition> methodIDToFunctions = new HashMap<>();
    private CryptoInterface cryptoInterface;

    public ContractABIDefinition(CryptoInterface cryptoInterface) {
        this.cryptoInterface = cryptoInterface;
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

    public void addFunction(String name, ABIDefinition ABIDefinition) {

        List<ABIDefinition> ABIDefinitions = functions.get(name);
        if (ABIDefinitions == null) {
            functions.put(name, new ArrayList<>());
            ABIDefinitions = functions.get(name);
        } else {
            logger.info(" overload method ??? name: {}, ABIDefinition: {}", name, ABIDefinition);
        }
        ABIDefinitions.add(ABIDefinition);

        // calculate method id and add ABIDefinition to methodIdToFunctions
        String methodId = ABIDefinition.getMethodId(cryptoInterface);
        methodIDToFunctions.put(methodId, ABIDefinition);

        logger.info(
                " name: {}, methodId: {}, methodSignature: {}, abi: {}",
                name,
                methodId,
                ABIDefinition.getMethodSignatureAsString(),
                ABIDefinition);
    }

    public void addEvent(String name, ABIDefinition ABIDefinition) {
        events.putIfAbsent(name, new ArrayList<>());
        List<ABIDefinition> ABIDefinitions = events.get(name);
        ABIDefinitions.add(ABIDefinition);
        logger.info(" name: {}, abi: {}", name, ABIDefinition);
    }

    public ABIDefinition getABIDefinitionByMethodId(String methodId) {
        return methodIDToFunctions.get(Numeric.prependHexPrefix(methodId));
    }
}
