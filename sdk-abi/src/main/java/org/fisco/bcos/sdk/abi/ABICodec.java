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

package org.fisco.bcos.sdk.abi;

import java.io.IOException;
import java.util.List;
import org.fisco.bcos.sdk.abi.wrapper.ABICodecJsonWrapper;
import org.fisco.bcos.sdk.abi.wrapper.ABICodecObject;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinitionFactory;
import org.fisco.bcos.sdk.abi.wrapper.ABIObject;
import org.fisco.bcos.sdk.abi.wrapper.ABIObjectFactory;
import org.fisco.bcos.sdk.abi.wrapper.ContractABIDefinition;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ABICodec {

    private static final Logger logger = LoggerFactory.getLogger(ABICodec.class);

    private final CryptoSuite cryptoSuite;
    public static final String TYPE_CONSTRUCTOR = "constructor";
    private ABIDefinitionFactory abiDefinitionFactory;
    private final ABIObjectFactory abiObjectFactory = new ABIObjectFactory();
    private final ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();

    public ABICodec(CryptoSuite cryptoSuite) {
        super();
        this.cryptoSuite = cryptoSuite;
        abiDefinitionFactory = new ABIDefinitionFactory(cryptoSuite);
    }

    public CryptoSuite getCryptoSuite() {
        return cryptoSuite;
    }

    public String encodeConstructor(String ABI, String BIN, List<Object> params)
            throws ABICodecException {

        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        ABIDefinition abiDefinition = contractABIDefinition.getConstructor();
        @SuppressWarnings("static-access")
        ABIObject inputABIObject = abiObjectFactory.createInputObject(abiDefinition);
        ABICodecObject abiCodecObject = new ABICodecObject();
        try {
            return BIN + abiCodecObject.encodeValue(inputABIObject, params).encode();
        } catch (Exception e) {
            logger.error(" exception in encodeMethodFromObject : {}", e.getMessage());
        }
        String errorMsg = " cannot encode in encodeMethodFromObject with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public String encodeConstructorFromString(String ABI, String BIN, List<String> params)
            throws ABICodecException {
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        ABIDefinition abiDefinition = contractABIDefinition.getConstructor();
        @SuppressWarnings("static-access")
        ABIObject inputABIObject = abiObjectFactory.createInputObject(abiDefinition);

        try {
            return BIN + abiCodecJsonWrapper.encode(inputABIObject, params).encode();
        } catch (Exception e) {
            logger.error(" exception in encodeMethodFromObject : {}", e.getMessage());
        }
        String errorMsg = " cannot encode in encodeMethodFromObject with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public String encodeMethod(String ABI, String methodName, List<Object> params)
            throws ABICodecException {
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        List<ABIDefinition> methods = contractABIDefinition.getFunctions().get(methodName);
        for (ABIDefinition abiDefinition : methods) {
            if (abiDefinition.getInputs().size() == params.size()) {
                @SuppressWarnings("static-access")
                ABIObject inputABIObject = abiObjectFactory.createInputObject(abiDefinition);
                ABICodecObject abiCodecObject = new ABICodecObject();
                try {
                    String methodId = abiDefinition.getMethodId(cryptoSuite);
                    return methodId + abiCodecObject.encodeValue(inputABIObject, params).encode();
                } catch (Exception e) {
                    logger.error(" exception in encodeMethodFromObject : {}", e.getMessage());
                }
            }
        }

        String errorMsg = " cannot encode in encodeMethodFromObject with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public String encodeMethodById(String ABI, String methodId, List<Object> params)
            throws ABICodecException {
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        ABIDefinition abiDefinition = contractABIDefinition.getABIDefinitionByMethodId(methodId);
        if (abiDefinition == null) {
            String errorMsg = " methodId " + methodId + " is invalid";
            logger.error(errorMsg);
            throw new ABICodecException(errorMsg);
        }
        ABIObject inputABIObject = abiObjectFactory.createInputObject(abiDefinition);
        ABICodecObject abiCodecObject = new ABICodecObject();
        try {
            return methodId + abiCodecObject.encodeValue(inputABIObject, params).encode();
        } catch (Exception e) {
            logger.error(" exception in encodeMethodByIdFromObject : {}", e.getMessage());
        }

        String errorMsg =
                " cannot encode in encodeMethodByIdFromObject with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public String encodeMethodByInterface(String ABI, String methodInterface, List<Object> params)
            throws ABICodecException {
        FunctionEncoder functionEncoder = new FunctionEncoder(cryptoSuite);
        String methodId = functionEncoder.buildMethodId(methodInterface);
        return encodeMethodById(ABI, methodId, params);
    }

    public String encodeMethodFromString(String ABI, String methodName, List<String> params)
            throws ABICodecException {
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        List<ABIDefinition> methods = contractABIDefinition.getFunctions().get(methodName);
        if (methods == null) {
            logger.debug(
                    "Invalid methodName: {}, all the functions are: {}",
                    methodName,
                    contractABIDefinition.getFunctions());
            throw new ABICodecException(
                    "Invalid method "
                            + methodName
                            + " , supported functions are: "
                            + contractABIDefinition.getFunctions().keySet());
        }
        for (ABIDefinition abiDefinition : methods) {
            if (abiDefinition.getInputs().size() == params.size()) {
                ABIObject inputABIObject = abiObjectFactory.createInputObject(abiDefinition);
                ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
                try {
                    String methodId = abiDefinition.getMethodId(cryptoSuite);
                    return methodId + abiCodecJsonWrapper.encode(inputABIObject, params).encode();
                } catch (IOException e) {
                    logger.error(" exception in encodeMethodFromString : {}", e.getMessage());
                }
            }
        }

        String errorMsg = " cannot encode in encodeMethodFromString with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public String encodeMethodByIdFromString(String ABI, String methodId, List<String> params)
            throws ABICodecException {
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        ABIDefinition abiDefinition = contractABIDefinition.getABIDefinitionByMethodId(methodId);
        if (abiDefinition == null) {
            String errorMsg = " methodId " + methodId + " is invalid";
            logger.error(errorMsg);
            throw new ABICodecException(errorMsg);
        }
        ABIObject inputABIObject = abiObjectFactory.createInputObject(abiDefinition);
        ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
        try {
            return methodId + abiCodecJsonWrapper.encode(inputABIObject, params).encode();
        } catch (IOException e) {
            logger.error(" exception in encodeMethodByIdFromString : {}", e.getMessage());
        }

        String errorMsg =
                " cannot encode in encodeMethodByIdFromString with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public String encodeMethodByInterfaceFromString(
            String ABI, String methodInterface, List<String> params) throws ABICodecException {
        FunctionEncoder functionEncoder = new FunctionEncoder(cryptoSuite);
        String methodId = functionEncoder.buildMethodId(methodInterface);
        return encodeMethodByIdFromString(ABI, methodId, params);
    }

    public List<Object> decodeMethod(ABIDefinition abiDefinition, String output)
            throws ABICodecException {
        ABIObject outputABIObject = abiObjectFactory.createOutputObject(abiDefinition);
        ABICodecObject abiCodecObject = new ABICodecObject();
        try {
            return abiCodecObject.decodeJavaObject(outputABIObject, output);
        } catch (Exception e) {
            logger.error(" exception in decodeMethodToObject : {}", e.getMessage());
        }
        String errorMsg = " cannot decode in decodeMethodToObject with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public List<Object> decodeMethod(String ABI, String methodName, String output)
            throws ABICodecException {
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        List<ABIDefinition> methods = contractABIDefinition.getFunctions().get(methodName);
        for (ABIDefinition abiDefinition : methods) {
            ABIObject outputABIObject = abiObjectFactory.createOutputObject(abiDefinition);
            ABICodecObject abiCodecObject = new ABICodecObject();
            try {
                return abiCodecObject.decodeJavaObject(outputABIObject, output);
            } catch (Exception e) {
                logger.error(" exception in decodeMethodToObject : {}", e.getMessage());
            }
        }

        String errorMsg = " cannot decode in decodeMethodToObject with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public List<Object> decodeMethodById(String ABI, String methodId, String output)
            throws ABICodecException {
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        ABIDefinition abiDefinition = contractABIDefinition.getABIDefinitionByMethodId(methodId);
        if (abiDefinition == null) {
            String errorMsg = " methodId " + methodId + " is invalid";
            logger.error(errorMsg);
            throw new ABICodecException(errorMsg);
        }
        ABIObject outputABIObject = abiObjectFactory.createOutputObject(abiDefinition);
        ABICodecObject abiCodecObject = new ABICodecObject();
        try {
            return abiCodecObject.decodeJavaObject(outputABIObject, output);
        } catch (Exception e) {
            logger.error(" exception in decodeMethodByIdToObject : {}", e.getMessage());
        }

        String errorMsg = " cannot decode in decodeMethodToObject with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public List<Object> decodeMethodByInterface(String ABI, String methodInterface, String output)
            throws ABICodecException {
        FunctionEncoder functionEncoder = new FunctionEncoder(cryptoSuite);
        String methodId = functionEncoder.buildMethodId(methodInterface);
        return decodeMethodById(ABI, methodId, output);
    }

    public List<String> decodeMethodToString(String ABI, String methodName, String output)
            throws ABICodecException {
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        List<ABIDefinition> methods = contractABIDefinition.getFunctions().get(methodName);
        if (methods == null) {
            throw new ABICodecException(
                    "Invalid method "
                            + methodName
                            + ", supported methods are: "
                            + contractABIDefinition.getFunctions().keySet());
        }
        for (ABIDefinition abiDefinition : methods) {
            ABIObject outputABIObject = abiObjectFactory.createOutputObject(abiDefinition);
            ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
            try {
                return abiCodecJsonWrapper.decode(outputABIObject, output);
            } catch (Exception e) {
                logger.error(" exception in decodeMethodToString : {}", e.getMessage());
            }
        }

        String errorMsg = " cannot decode in decodeMethodToString with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public List<String> decodeMethodByIdToString(String ABI, String methodId, String output)
            throws ABICodecException {
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        ABIDefinition abiDefinition = contractABIDefinition.getABIDefinitionByMethodId(methodId);
        if (abiDefinition == null) {
            String errorMsg = " methodId " + methodId + " is invalid";
            logger.error(errorMsg);
            throw new ABICodecException(errorMsg);
        }
        ABIObject outputABIObject = abiObjectFactory.createOutputObject(abiDefinition);
        ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
        try {
            return abiCodecJsonWrapper.decode(outputABIObject, output);
        } catch (UnsupportedOperationException e) {
            logger.error(" exception in decodeMethodByIdToString : {}", e.getMessage());
        }

        String errorMsg =
                " cannot decode in decodeMethodByIdToString with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public List<String> decodeMethodByInterfaceToString(
            String ABI, String methodInterface, String output) throws ABICodecException {
        FunctionEncoder functionEncoder = new FunctionEncoder(cryptoSuite);
        String methodId = functionEncoder.buildMethodId(methodInterface);
        return decodeMethodByIdToString(ABI, methodId, output);
    }

    public List<Object> decodeEvent(String ABI, String eventName, String output)
            throws ABICodecException {
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        List<ABIDefinition> events = contractABIDefinition.getEvents().get(eventName);
        if (events == null) {
            throw new ABICodecException(
                    "Invalid event "
                            + eventName
                            + ", supported events are: "
                            + contractABIDefinition.getEvents().keySet());
        }
        for (ABIDefinition abiDefinition : events) {
            ABIObject outputObject = abiObjectFactory.createInputObject(abiDefinition);
            ABICodecObject abiCodecObject = new ABICodecObject();
            try {
                return abiCodecObject.decodeJavaObject(outputObject, output);
            } catch (Exception e) {
                logger.error(" exception in decodeEventToObject : {}", e.getMessage());
            }
        }

        String errorMsg = " cannot decode in decodeEventToObject with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public List<Object> decodeEventByTopic(String ABI, String eventTopic, String output)
            throws ABICodecException {
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        ABIDefinition abiDefinition =
                contractABIDefinition.getABIDefinitionByEventTopic(eventTopic);
        ABIObject outputObject = abiObjectFactory.createOutputObject(abiDefinition);
        ABICodecObject abiCodecObject = new ABICodecObject();
        try {
            return abiCodecObject.decodeJavaObject(outputObject, output);
        } catch (Exception e) {
            logger.error(" exception in decodeEventByTopicToObject : {}", e.getMessage());
        }

        String errorMsg =
                " cannot decode in decodeEventByTopicToObject with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public List<Object> decodeEventByInterface(String ABI, String eventSignature, String output)
            throws ABICodecException {
        FunctionEncoder functionEncoder = new FunctionEncoder(cryptoSuite);
        String methodId = functionEncoder.buildMethodId(eventSignature);
        return decodeEventByTopic(ABI, methodId, output);
    }

    public List<String> decodeEventToString(String ABI, String eventName, String output)
            throws ABICodecException {
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        List<ABIDefinition> events = contractABIDefinition.getEvents().get(eventName);
        if (events == null) {
            throw new ABICodecException(
                    "Invalid event "
                            + eventName
                            + ", current supported events are: "
                            + contractABIDefinition.getEvents().keySet());
        }
        for (ABIDefinition abiDefinition : events) {
            ABIObject outputObject = abiObjectFactory.createOutputObject(abiDefinition);
            ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
            try {
                return abiCodecJsonWrapper.decode(outputObject, output);
            } catch (Exception e) {
                logger.error(" exception in decodeEventToString : {}", e.getMessage());
            }
        }

        String errorMsg = " cannot decode in decodeEventToString with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public List<String> decodeEventByTopicToString(String ABI, String eventTopic, String output)
            throws ABICodecException {
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        ABIDefinition abiDefinition =
                contractABIDefinition.getABIDefinitionByEventTopic(eventTopic);
        ABIObject outputObject = abiObjectFactory.createOutputObject(abiDefinition);
        ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
        try {
            return abiCodecJsonWrapper.decode(outputObject, output);
        } catch (Exception e) {
            logger.error(" exception in decodeEventByTopicToString : {}", e.getMessage());
        }

        String errorMsg =
                " cannot decode in decodeEventByTopicToString with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public List<String> decodeEventByInterfaceToString(
            String ABI, String eventSignature, String output) throws ABICodecException {
        FunctionEncoder functionEncoder = new FunctionEncoder(cryptoSuite);
        String methodId = functionEncoder.buildMethodId(eventSignature);
        return decodeEventByTopicToString(ABI, methodId, output);
    }
}
