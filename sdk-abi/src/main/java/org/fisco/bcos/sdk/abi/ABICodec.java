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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.fisco.bcos.sdk.abi.wrapper.*;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.model.EventLog;
import org.fisco.bcos.sdk.utils.Hex;
import org.fisco.bcos.sdk.utils.Numeric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** ABI encode and decode tool */
public class ABICodec {

    private static final Logger logger = LoggerFactory.getLogger(ABICodec.class);

    private final CryptoSuite cryptoSuite;
    public static final String TYPE_CONSTRUCTOR = "constructor";
    private final ABIDefinitionFactory abiDefinitionFactory;
    private final ABIObjectFactory abiObjectFactory = new ABIObjectFactory();
    private final ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();

    public ABICodec(CryptoSuite cryptoSuite) {
        super();
        this.cryptoSuite = cryptoSuite;
        this.abiDefinitionFactory = new ABIDefinitionFactory(cryptoSuite);
    }

    public CryptoSuite getCryptoSuite() {
        return this.cryptoSuite;
    }

    public byte[] encodeConstructor(String ABI, String BIN, List<Object> params)
            throws ABICodecException {

        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(ABI);
        ABIDefinition abiDefinition = contractABIDefinition.getConstructor();
        ABIObject inputABIObject = ABIObjectFactory.createInputObject(abiDefinition);
        ABICodecObject abiCodecObject = new ABICodecObject();
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(Hex.decode(BIN));
            outputStream.write(abiCodecObject.encodeValue(inputABIObject, params).encode());
            return outputStream.toByteArray();
        } catch (Exception e) {
            logger.error(" exception in encodeMethodFromObject : {}", e.getMessage());
        }
        String errorMsg = " cannot encode in encodeMethodFromObject with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public byte[] encodeConstructorFromString(String ABI, String BIN, List<String> params)
            throws ABICodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(ABI);
        ABIDefinition abiDefinition = contractABIDefinition.getConstructor();
        ABIObject inputABIObject = ABIObjectFactory.createInputObject(abiDefinition);
        Throwable cause = null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(Hex.decode(BIN));
            outputStream.write(this.abiCodecJsonWrapper.encode(inputABIObject, params).encode());
            return outputStream.toByteArray();
        } catch (Exception e) {
            cause = e;
            logger.error(" exception in encodeMethodFromObject : {}", e.getMessage());
        }
        String errorMsg =
                " cannot encode in encodeMethodFromObject with appropriate interface ABI, cause:"
                        + cause.getMessage();
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public byte[] encodeMethod(String ABI, String methodName, List<Object> params)
            throws ABICodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(ABI);
        List<ABIDefinition> methods = contractABIDefinition.getFunctions().get(methodName);
        if (methods == null || methods.size() == 0) {
            throw new ABICodecException(Constant.NO_APPROPRIATE_ABI_METHOD);
        }
        for (ABIDefinition abiDefinition : methods) {
            if (abiDefinition.getInputs().size() == params.size()) {
                ABIObject inputABIObject = ABIObjectFactory.createInputObject(abiDefinition);
                ABICodecObject abiCodecObject = new ABICodecObject();
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    outputStream.write(abiDefinition.getMethodId(this.cryptoSuite));
                    outputStream.write(abiCodecObject.encodeValue(inputABIObject, params).encode());
                    return outputStream.toByteArray();
                } catch (Exception e) {
                    logger.error(" exception in encodeMethodFromObject : {}", e.getMessage());
                }
            }
        }
        logger.error(Constant.NO_APPROPRIATE_ABI_METHOD);
        throw new ABICodecException(Constant.NO_APPROPRIATE_ABI_METHOD);
    }

    public byte[] encodeMethodById(String ABI, byte[] methodId, List<Object> params)
            throws ABICodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(ABI);
        ABIDefinition abiDefinition = contractABIDefinition.getABIDefinitionByMethodId(methodId);
        if (abiDefinition == null) {
            String errorMsg = " methodId " + methodId + " is invalid";
            logger.error(errorMsg);
            throw new ABICodecException(errorMsg);
        }
        ABIObject inputABIObject = ABIObjectFactory.createInputObject(abiDefinition);
        ABICodecObject abiCodecObject = new ABICodecObject();
        Exception cause = null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(methodId);
            outputStream.write(abiCodecObject.encodeValue(inputABIObject, params).encode());
            return outputStream.toByteArray();
        } catch (Exception e) {
            cause = e;
            logger.error(" exception in encodeMethodByIdFromObject : {}", e.getMessage());
        }

        String errorMsg =
                " cannot encode in encodeMethodByIdFromObject with appropriate interface ABI, cause:"
                        + cause.getMessage();
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    private ABIDefinition getABIDefinition(String methodInterface) throws ABICodecException {
        int start = methodInterface.indexOf("(");
        int end = methodInterface.lastIndexOf(")");
        if (start == -1 || end == -1 || start >= end) {
            String errorMsg = " error format";
            logger.error(errorMsg);
            throw new ABICodecException(errorMsg);
        }
        String name = methodInterface.substring(0, start);
        String type = methodInterface.substring(start + 1, end);
        if (type.indexOf("tuple") != -1) {
            String errorMsg = " cannot support tuple type";
            logger.error(errorMsg);
            throw new ABICodecException(errorMsg);
        }
        String[] types = type.split(",");
        List<ABIDefinition.NamedType> inputs = new ArrayList<ABIDefinition.NamedType>();
        for (String s : types) {
            ABIDefinition.NamedType input = new ABIDefinition.NamedType("name", s);
            inputs.add(input);
        }

        return new ABIDefinition(false, inputs, name, null, "function", false, "nonpayable");
    }

    public byte[] encodeMethodByInterface(String methodInterface, List<Object> params)
            throws ABICodecException {
        ABIDefinition abiDefinition = this.getABIDefinition(methodInterface);
        if (abiDefinition.getInputs().size() == params.size()) {
            ABIObject inputABIObject = ABIObjectFactory.createInputObject(abiDefinition);
            ABICodecObject abiCodecObject = new ABICodecObject();
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                outputStream.write(abiDefinition.getMethodId(this.cryptoSuite));
                outputStream.write(abiCodecObject.encodeValue(inputABIObject, params).encode());
                return outputStream.toByteArray();
            } catch (Exception e) {
                logger.error(
                        " exception in encodeMethodByInterfaceFromObject : {}", e.getMessage());
            }
        }

        String errorMsg = " cannot encode in encodeMethodByInterfaceFromObject";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public byte[] encodeMethodFromString(String ABI, String methodName, List<String> params)
            throws ABICodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(ABI);
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
                ABIObject inputABIObject = ABIObjectFactory.createInputObject(abiDefinition);
                ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    outputStream.write(abiDefinition.getMethodId(this.cryptoSuite));
                    outputStream.write(abiCodecJsonWrapper.encode(inputABIObject, params).encode());
                    return outputStream.toByteArray();
                } catch (Exception e) {
                    logger.error(" exception in encodeMethodFromString : {}", e.getMessage());
                }
            }
        }

        String errorMsg =
                " cannot encode in encodeMethodFromString with appropriate interface ABI, make sure params match";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public byte[] encodeMethodByIdFromString(String ABI, byte[] methodId, List<String> params)
            throws ABICodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(ABI);
        ABIDefinition abiDefinition = contractABIDefinition.getABIDefinitionByMethodId(methodId);
        if (abiDefinition == null) {
            String errorMsg = " methodId " + methodId + " is invalid";
            logger.error(errorMsg);
            throw new ABICodecException(errorMsg);
        }
        ABIObject inputABIObject = ABIObjectFactory.createInputObject(abiDefinition);
        ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(methodId);
            outputStream.write(abiCodecJsonWrapper.encode(inputABIObject, params).encode());
            return outputStream.toByteArray();
        } catch (IOException e) {
            logger.error(" exception in encodeMethodByIdFromString : {}", e.getMessage());
        }

        String errorMsg =
                " cannot encode in encodeMethodByIdFromString with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public byte[] encodeMethodByInterfaceFromString(String methodInterface, List<String> params)
            throws ABICodecException {
        ABIDefinition abiDefinition = this.getABIDefinition(methodInterface);
        if (abiDefinition.getInputs().size() == params.size()) {
            ABIObject inputABIObject = ABIObjectFactory.createInputObject(abiDefinition);
            ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                outputStream.write(abiDefinition.getMethodId(this.cryptoSuite));
                outputStream.write(abiCodecJsonWrapper.encode(inputABIObject, params).encode());
                return outputStream.toByteArray();
            } catch (IOException e) {
                logger.error(
                        " exception in encodeMethodByInterfaceFromString : {}", e.getMessage());
            }
        }

        String errorMsg = " cannot encode in encodeMethodByInterfaceFromString";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public Pair<List<Object>, List<ABIObject>> decodeMethodAndGetOutputObject(
            ABIDefinition abiDefinition, String output) throws ABICodecException {
        ABIObject outputABIObject = ABIObjectFactory.createOutputObject(abiDefinition);
        ABICodecObject abiCodecObject = new ABICodecObject();
        try {
            return abiCodecObject.decodeJavaObjectAndOutputObject(outputABIObject, output);
        } catch (Exception e) {
            logger.error(" exception in decodeMethodToObject : {}", e.getMessage());
        }
        String errorMsg = " cannot decode in decodeMethodToObject with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public Pair<List<Object>, List<ABIObject>> decodeMethodAndGetOutputObject(
            String ABI, String methodName, String output) throws ABICodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(ABI);
        List<ABIDefinition> methods = contractABIDefinition.getFunctions().get(methodName);
        for (ABIDefinition abiDefinition : methods) {
            ABIObject outputABIObject = ABIObjectFactory.createOutputObject(abiDefinition);
            ABICodecObject abiCodecObject = new ABICodecObject();
            try {
                return abiCodecObject.decodeJavaObjectAndOutputObject(outputABIObject, output);
            } catch (Exception e) {
                logger.error(" exception in decodeMethodToObject : {}", e.getMessage());
            }
        }

        String errorMsg = " cannot decode in decodeMethodToObject with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public List<Object> decodeMethod(ABIDefinition abiDefinition, String output)
            throws ABICodecException {
        return this.decodeMethodAndGetOutputObject(abiDefinition, output).getLeft();
    }

    public List<Object> decodeMethod(String ABI, String methodName, String output)
            throws ABICodecException {
        return this.decodeMethodAndGetOutputObject(ABI, methodName, output).getLeft();
    }

    public List<Object> decodeMethodById(String ABI, byte[] methodId, byte[] output)
            throws ABICodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(ABI);
        ABIDefinition abiDefinition = contractABIDefinition.getABIDefinitionByMethodId(methodId);
        if (abiDefinition == null) {
            String errorMsg = " methodId " + methodId + " is invalid";
            logger.error(errorMsg);
            throw new ABICodecException(errorMsg);
        }
        ABIObject outputABIObject = ABIObjectFactory.createOutputObject(abiDefinition);
        ABICodecObject abiCodecObject = new ABICodecObject();
        try {
            return abiCodecObject.decodeJavaObject(outputABIObject, Hex.toHexString(output));
        } catch (Exception e) {
            logger.error(" exception in decodeMethodByIdToObject : {}", e.getMessage());
        }

        String errorMsg = " cannot decode in decodeMethodToObject with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public List<Object> decodeMethodByInterface(String ABI, String methodInterface, byte[] output)
            throws ABICodecException {
        FunctionEncoder functionEncoder = new FunctionEncoder(this.cryptoSuite);
        byte[] methodId = functionEncoder.buildMethodId(methodInterface);
        return this.decodeMethodById(ABI, methodId, output);
    }

    public List<String> decodeMethodToString(String ABI, String methodName, byte[] output)
            throws ABICodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(ABI);
        List<ABIDefinition> methods = contractABIDefinition.getFunctions().get(methodName);
        if (methods == null) {
            throw new ABICodecException(
                    "Invalid method "
                            + methodName
                            + ", supported methods are: "
                            + contractABIDefinition.getFunctions().keySet());
        }
        for (ABIDefinition abiDefinition : methods) {
            ABIObject outputABIObject = ABIObjectFactory.createOutputObject(abiDefinition);
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

    public List<String> decodeMethodByIdToString(String ABI, byte[] methodId, byte[] output)
            throws ABICodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(ABI);
        ABIDefinition abiDefinition = contractABIDefinition.getABIDefinitionByMethodId(methodId);
        if (abiDefinition == null) {
            String errorMsg = " methodId " + methodId + " is invalid";
            logger.error(errorMsg);
            throw new ABICodecException(errorMsg);
        }
        ABIObject outputABIObject = ABIObjectFactory.createOutputObject(abiDefinition);
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
            String ABI, String methodInterface, byte[] output) throws ABICodecException {
        FunctionEncoder functionEncoder = new FunctionEncoder(this.cryptoSuite);
        byte[] methodId = functionEncoder.buildMethodId(methodInterface);
        return this.decodeMethodByIdToString(ABI, methodId, output);
    }

    public List<Object> decodeEvent(String ABI, String eventName, EventLog log)
            throws ABICodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(ABI);
        List<ABIDefinition> events = contractABIDefinition.getEvents().get(eventName);
        if (events == null) {
            throw new ABICodecException(
                    "Invalid event "
                            + eventName
                            + ", supported events are: "
                            + contractABIDefinition.getEvents().keySet());
        }
        for (ABIDefinition abiDefinition : events) {
            ABIObject inputObject = ABIObjectFactory.createEventInputObject(abiDefinition);
            ABICodecObject abiCodecObject = new ABICodecObject();
            try {
                List<Object> params = new ArrayList<>();
                if (!log.getData().equals("0x")) {
                    params = abiCodecObject.decodeJavaObject(inputObject, log.getData());
                }
                List<String> topics = log.getTopics();
                return this.mergeEventParamsAndTopics(abiDefinition, params, topics);
            } catch (Exception e) {
                logger.error(" exception in decodeEventToObject : {}", e.getMessage());
            }
        }

        String errorMsg = " cannot decode in decodeEventToObject with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public List<Object> decodeEventByTopic(String ABI, String eventTopic, EventLog log)
            throws ABICodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(ABI);
        ABIDefinition abiDefinition =
                contractABIDefinition.getABIDefinitionByEventTopic(eventTopic);
        ABIObject inputObject = ABIObjectFactory.createEventInputObject(abiDefinition);
        ABICodecObject abiCodecObject = new ABICodecObject();
        try {
            List<Object> params = new ArrayList<>();
            if (!log.getData().equals("0x")) {
                params = abiCodecObject.decodeJavaObject(inputObject, log.getData());
            }
            List<String> topics = log.getTopics();
            return this.mergeEventParamsAndTopics(abiDefinition, params, topics);
        } catch (Exception e) {
            logger.error(" exception in decodeEventByTopicToObject : {}", e.getMessage());
        }

        String errorMsg =
                " cannot decode in decodeEventByTopicToObject with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public List<Object> decodeEventByInterface(String ABI, String eventSignature, EventLog log)
            throws ABICodecException {
        FunctionEncoder functionEncoder = new FunctionEncoder(this.cryptoSuite);
        byte[] methodId = functionEncoder.buildMethodId(eventSignature);
        return this.decodeEventByTopic(ABI, Numeric.toHexString(methodId), log);
    }

    public List<String> decodeEventToString(String ABI, String eventName, EventLog log)
            throws ABICodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(ABI);
        List<ABIDefinition> events = contractABIDefinition.getEvents().get(eventName);
        if (events == null) {
            throw new ABICodecException(
                    "Invalid event "
                            + eventName
                            + ", current supported events are: "
                            + contractABIDefinition.getEvents().keySet());
        }
        for (ABIDefinition abiDefinition : events) {
            ABIObject inputObject = ABIObjectFactory.createEventInputObject(abiDefinition);
            ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
            try {
                List<String> params = new ArrayList<>();
                if (!log.getData().equals("0x")) {
                    params = abiCodecJsonWrapper.decode(inputObject, Hex.decode(log.getData()));
                }
                List<String> topics = log.getTopics();
                return this.mergeEventParamsAndTopicsToString(abiDefinition, params, topics);
            } catch (Exception e) {
                logger.error(" exception in decodeEventToString : {}", e.getMessage());
            }
        }

        String errorMsg = " cannot decode in decodeEventToString with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public List<String> decodeEventByTopicToString(String ABI, String eventTopic, EventLog log)
            throws ABICodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(ABI);
        ABIDefinition abiDefinition =
                contractABIDefinition.getABIDefinitionByEventTopic(eventTopic);
        ABIObject inputObject = ABIObjectFactory.createEventInputObject(abiDefinition);
        ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();
        try {
            List<String> params = new ArrayList<>();
            if (!log.getData().equals("0x")) {
                params = abiCodecJsonWrapper.decode(inputObject, Hex.decode(log.getData()));
            }
            List<String> topics = log.getTopics();
            return this.mergeEventParamsAndTopicsToString(abiDefinition, params, topics);
        } catch (Exception e) {
            logger.error(" exception in decodeEventByTopicToString : {}", e.getMessage());
        }

        String errorMsg =
                " cannot decode in decodeEventByTopicToString with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public List<String> decodeEventByInterfaceToString(
            String ABI, String eventSignature, EventLog log) throws ABICodecException {
        FunctionEncoder functionEncoder = new FunctionEncoder(this.cryptoSuite);
        byte[] methodId = functionEncoder.buildMethodId(eventSignature);
        return this.decodeEventByTopicToString(ABI, Numeric.toHexString(methodId), log);
    }

    private List<Object> mergeEventParamsAndTopics(
            ABIDefinition abiDefinition, List<Object> params, List<String> topics) {
        List<Object> ret = new ArrayList<>();
        int paramIdx = 0;
        int topicIdx = 1;
        for (ABIDefinition.NamedType namedType : abiDefinition.getInputs()) {
            if (namedType.isIndexed()) {
                ret.add(topics.get(topicIdx));
                topicIdx++;
            } else {
                ret.add(params.get(paramIdx));
                paramIdx++;
            }
        }
        return ret;
    }

    private List<String> mergeEventParamsAndTopicsToString(
            ABIDefinition abiDefinition, List<String> params, List<String> topics) {
        List<String> ret = new ArrayList<>();
        int paramIdx = 0;
        int topicIdx = 1;
        for (ABIDefinition.NamedType namedType : abiDefinition.getInputs()) {
            if (namedType.isIndexed()) {
                ret.add(topics.get(topicIdx));
                topicIdx++;
            } else {
                ret.add(params.get(paramIdx));
                paramIdx++;
            }
        }
        return ret;
    }
}
