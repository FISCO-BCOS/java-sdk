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
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinitionFactory;
import org.fisco.bcos.sdk.abi.wrapper.ABIObject;
import org.fisco.bcos.sdk.abi.wrapper.ABIObjectFactory;
import org.fisco.bcos.sdk.abi.wrapper.ContractABIDefinition;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ABICodec {

    private static final Logger logger = LoggerFactory.getLogger(ABICodec.class);

    private CryptoInterface cryptoInterface;

    public void setCryptoInterface(CryptoInterface cryptoInterface) {
        this.cryptoInterface = cryptoInterface;
    }

    public CryptoInterface getCryptoInterface() {
        return cryptoInterface;
    }

    public String encodeMethod(String ABI, String methodName, List<Object> params)
            throws ABICodecException {
        ABIDefinitionFactory abiDefinitionFactory = new ABIDefinitionFactory(cryptoInterface);
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        List<ABIDefinition> methods = contractABIDefinition.getFunctions().get(methodName);
        for (ABIDefinition abiDefinition : methods) {
            if (abiDefinition.getInputs().size() == params.size()) {
                ABIObjectFactory abiObjectFactory = new ABIObjectFactory();
                ABIObject inputABIObject = abiObjectFactory.createInputObject(abiDefinition);
                ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
                try {
                    return abiCodecJsonWrapper.encodeJavaObject(inputABIObject, params).encode();
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
        ABIDefinitionFactory abiDefinitionFactory = new ABIDefinitionFactory(cryptoInterface);
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        ABIDefinition abiDefinition = contractABIDefinition.getABIDefinitionByMethodId(methodId);
        ABIObjectFactory abiObjectFactory = new ABIObjectFactory();
        ABIObject inputABIObject = abiObjectFactory.createInputObject(abiDefinition);
        ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
        try {
            return abiCodecJsonWrapper.encodeJavaObject(inputABIObject, params).encode();
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
        FunctionEncoder functionEncoder = new FunctionEncoder(cryptoInterface);
        String methodId = functionEncoder.buildMethodId(methodInterface);
        return encodeMethodById(ABI, methodId, params);
    }

    public String encodeMethodFromString(String ABI, String methodName, List<String> params)
            throws ABICodecException {
        ABIDefinitionFactory abiDefinitionFactory = new ABIDefinitionFactory(cryptoInterface);
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        List<ABIDefinition> methods = contractABIDefinition.getFunctions().get(methodName);

        for (ABIDefinition abiDefinition : methods) {
            if (abiDefinition.getInputs().size() == params.size()) {
                ABIObjectFactory abiObjectFactory = new ABIObjectFactory();
                ABIObject inputABIObject = abiObjectFactory.createInputObject(abiDefinition);
                ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
                try {
                    return abiCodecJsonWrapper.encode(inputABIObject, params).encode();
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
        ABIDefinitionFactory abiDefinitionFactory = new ABIDefinitionFactory(cryptoInterface);
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        ABIDefinition abiDefinition = contractABIDefinition.getABIDefinitionByMethodId(methodId);

        ABIObjectFactory abiObjectFactory = new ABIObjectFactory();
        ABIObject inputABIObject = abiObjectFactory.createInputObject(abiDefinition);
        ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
        try {
            return abiCodecJsonWrapper.encode(inputABIObject, params).encode();
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
        FunctionEncoder functionEncoder = new FunctionEncoder(cryptoInterface);
        String methodId = functionEncoder.buildMethodId(methodInterface);
        return encodeMethodByIdFromString(ABI, methodId, params);
    }

    public String encodeEvent(String ABI, String eventName, List<Object> params)
            throws ABICodecException {
        ABIDefinitionFactory abiDefinitionFactory = new ABIDefinitionFactory(cryptoInterface);
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        List<ABIDefinition> events = contractABIDefinition.getEvents().get(eventName);
        for (ABIDefinition abiDefinition : events) {
            if (abiDefinition.getInputs().size() == params.size()) {
                ABIObjectFactory abiObjectFactory = new ABIObjectFactory();
                ABIObject inputABIObject = abiObjectFactory.createInputObject(abiDefinition);
                ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
                try {
                    return abiCodecJsonWrapper.encodeJavaObject(inputABIObject, params).encode();
                } catch (Exception e) {
                    logger.error(" exception in encodeEventFromObject : {}", e.getMessage());
                }
            }
        }

        String errorMsg = " cannot encode in encodeEventFromObject with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public String encodeEventByTopic(String ABI, String eventTopic, List<Object> params)
            throws ABICodecException {
        ABIDefinitionFactory abiDefinitionFactory = new ABIDefinitionFactory(cryptoInterface);
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        ABIDefinition abiDefinition =
                contractABIDefinition.getABIDefinitionByEventTopic(eventTopic);
        ABIObjectFactory abiObjectFactory = new ABIObjectFactory();
        ABIObject inputABIObject = abiObjectFactory.createInputObject(abiDefinition);
        ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
        try {
            return abiCodecJsonWrapper.encodeJavaObject(inputABIObject, params).encode();
        } catch (Exception e) {
            logger.error(" exception in encodeEventByTopicFromObject : {}", e.getMessage());
        }

        String errorMsg =
                " cannot encode in encodeEventByTopicFromObject with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public String encodeEventByInterface(String eventSignature, List<Object> params)
            throws ABICodecException {
        return null;
    }

    public String encodeEventFromString(String ABI, String eventName, List<String> params)
            throws ABICodecException {
        ABIDefinitionFactory abiDefinitionFactory = new ABIDefinitionFactory(cryptoInterface);
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        List<ABIDefinition> methods = contractABIDefinition.getEvents().get(eventName);
        for (ABIDefinition abiDefinition : methods) {
            if (abiDefinition.getInputs().size() == params.size()) {
                ABIObjectFactory abiObjectFactory = new ABIObjectFactory();
                ABIObject inputABIObject = abiObjectFactory.createInputObject(abiDefinition);
                ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
                try {
                    return abiCodecJsonWrapper.encode(inputABIObject, params).encode();
                } catch (Exception e) {
                    logger.error(" exception in encodeEventFromString : {}", e.getMessage());
                }
            }
        }

        String errorMsg = " cannot encode in encodeEventFromString with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public String encodeEventByTopicFromString(String ABI, String eventTopic, List<String> params)
            throws ABICodecException {
        ABIDefinitionFactory abiDefinitionFactory = new ABIDefinitionFactory(cryptoInterface);
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        ABIDefinition abiDefinition =
                contractABIDefinition.getABIDefinitionByEventTopic(eventTopic);
        ABIObjectFactory abiObjectFactory = new ABIObjectFactory();
        ABIObject inputABIObject = abiObjectFactory.createInputObject(abiDefinition);
        ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
        try {
            return abiCodecJsonWrapper.encode(inputABIObject, params).encode();
        } catch (Exception e) {
            logger.error(" exception in encodeEventByTopicFromString : {}", e.getMessage());
        }

        String errorMsg =
                " cannot encode in encodeEventByTopicFromString with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public String encodeEventByInterfaceFromString(String eventSignature, List<String> params)
            throws ABICodecException {
        return null;
    }

    public List<Object> decodeMethod(String ABI, String methodName, String output)
            throws ABICodecException {
        ABIDefinitionFactory abiDefinitionFactory = new ABIDefinitionFactory(cryptoInterface);
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        List<ABIDefinition> methods = contractABIDefinition.getFunctions().get(methodName);
        for (ABIDefinition abiDefinition : methods) {
            ABIObjectFactory abiObjectFactory = new ABIObjectFactory();
            ABIObject outputABIObject = abiObjectFactory.createOutputObject(abiDefinition);
            ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
            try {
                return abiCodecJsonWrapper.decodeJavaObject(outputABIObject, output);
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
        ABIDefinitionFactory abiDefinitionFactory = new ABIDefinitionFactory(cryptoInterface);
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        ABIDefinition abiDefinition = contractABIDefinition.getABIDefinitionByMethodId(methodId);
        ABIObjectFactory abiObjectFactory = new ABIObjectFactory();
        ABIObject outputABIObject = abiObjectFactory.createOutputObject(abiDefinition);
        ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
        try {
            return abiCodecJsonWrapper.decodeJavaObject(outputABIObject, output);
        } catch (Exception e) {
            logger.error(" exception in decodeMethodByIdToObject : {}", e.getMessage());
        }

        String errorMsg = " cannot decode in decodeMethodToObject with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public List<Object> decodeMethodByInterface(String ABI, String methodInterface, String output)
            throws ABICodecException {
        FunctionEncoder functionEncoder = new FunctionEncoder(cryptoInterface);
        String methodId = functionEncoder.buildMethodId(methodInterface);
        return decodeMethodById(ABI, methodId, output);
    }

    public List<String> decodeMethodToString(String ABI, String methodName, String output)
            throws ABICodecException {
        ABIDefinitionFactory abiDefinitionFactory = new ABIDefinitionFactory(cryptoInterface);
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        List<ABIDefinition> methods = contractABIDefinition.getFunctions().get(methodName);
        for (ABIDefinition abiDefinition : methods) {
            ABIObjectFactory abiObjectFactory = new ABIObjectFactory();
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
        ABIDefinitionFactory abiDefinitionFactory = new ABIDefinitionFactory(cryptoInterface);
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        ABIDefinition abiDefinition = contractABIDefinition.getABIDefinitionByMethodId(methodId);

        ABIObjectFactory abiObjectFactory = new ABIObjectFactory();
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
        FunctionEncoder functionEncoder = new FunctionEncoder(cryptoInterface);
        String methodId = functionEncoder.buildMethodId(methodInterface);
        return decodeMethodByIdToString(ABI, methodId, output);
    }

    public List<Object> decodeEvent(String ABI, String eventName, String output)
            throws ABICodecException {
        ABIDefinitionFactory abiDefinitionFactory = new ABIDefinitionFactory(cryptoInterface);
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        List<ABIDefinition> events = contractABIDefinition.getEvents().get(eventName);

        for (ABIDefinition abiDefinition : events) {
            ABIObjectFactory abiObjectFactory = new ABIObjectFactory();
            ABIObject outputObject = abiObjectFactory.createOutputObject(abiDefinition);
            ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
            try {
                return abiCodecJsonWrapper.decodeJavaObject(outputObject, output);
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
        ABIDefinitionFactory abiDefinitionFactory = new ABIDefinitionFactory(cryptoInterface);
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        ABIDefinition abiDefinition =
                contractABIDefinition.getABIDefinitionByEventTopic(eventTopic);
        ABIObjectFactory abiObjectFactory = new ABIObjectFactory();
        ABIObject outputObject = abiObjectFactory.createOutputObject(abiDefinition);
        ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
        try {
            return abiCodecJsonWrapper.decodeJavaObject(outputObject, output);
        } catch (Exception e) {
            logger.error(" exception in decodeEventByTopicToObject : {}", e.getMessage());
        }

        String errorMsg =
                " cannot decode in decodeEventByTopicToObject with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public List<Object> decodeEventByInterface(String eventSignature, String output)
            throws ABICodecException {
        return null;
    }

    public List<String> decodeEventToString(String ABI, String eventName, String output)
            throws ABICodecException {
        ABIDefinitionFactory abiDefinitionFactory = new ABIDefinitionFactory(cryptoInterface);
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        List<ABIDefinition> events = contractABIDefinition.getEvents().get(eventName);

        for (ABIDefinition abiDefinition : events) {
            ABIObjectFactory abiObjectFactory = new ABIObjectFactory();
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
        ABIDefinitionFactory abiDefinitionFactory = new ABIDefinitionFactory(cryptoInterface);
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        ABIDefinition abiDefinition =
                contractABIDefinition.getABIDefinitionByEventTopic(eventTopic);
        ABIObjectFactory abiObjectFactory = new ABIObjectFactory();
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

    public List<String> decodeEventByInterfaceToString(String eventSignature, String output)
            throws ABICodecException {
        return null;
    }
}
