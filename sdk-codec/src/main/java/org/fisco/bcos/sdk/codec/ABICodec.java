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

package org.fisco.bcos.sdk.codec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.fisco.bcos.sdk.codec.abi.Constant;
import org.fisco.bcos.sdk.codec.datatypes.*;
import org.fisco.bcos.sdk.codec.wrapper.*;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.model.EventLog;
import org.fisco.bcos.sdk.utils.Hex;
import org.fisco.bcos.sdk.utils.Numeric;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** ABI encode and decode tool */
public class ABICodec {

    private static final Logger logger = LoggerFactory.getLogger(ABICodec.class);

    private final CryptoSuite cryptoSuite;
    private final boolean isWasm;
    private final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
    private org.fisco.bcos.sdk.codec.scale.FunctionEncoder scaleFunctionEncoder = null;
    private org.fisco.bcos.sdk.codec.abi.FunctionEncoder abiFunctionEncoder = null;
    private FunctionReturnDecoderInterface functionReturnDecoder = null;
    private final ABIDefinitionFactory abiDefinitionFactory;
    private final ABICodecJsonWrapper abiCodecJsonWrapper = new ABICodecJsonWrapper();

    public ABICodec(CryptoSuite cryptoSuite, boolean isWasm) {
        super();
        this.cryptoSuite = cryptoSuite;
        this.isWasm = isWasm;
        if (isWasm) {
            this.scaleFunctionEncoder =
                    new org.fisco.bcos.sdk.codec.scale.FunctionEncoder(cryptoSuite);
            this.functionReturnDecoder = new org.fisco.bcos.sdk.codec.scale.FunctionReturnDecoder();
        } else {
            this.abiFunctionEncoder = new org.fisco.bcos.sdk.codec.abi.FunctionEncoder(cryptoSuite);
            this.functionReturnDecoder = new org.fisco.bcos.sdk.codec.abi.FunctionReturnDecoder();
        }
        this.abiDefinitionFactory = new ABIDefinitionFactory(cryptoSuite);
    }

    public CryptoSuite getCryptoSuite() {
        return this.cryptoSuite;
    }

    public byte[] encodeConstructor(String abi, String bin, List<Object> params)
            throws ABICodecException {

        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(abi);
        ABIDefinition abiDefinition = contractABIDefinition.getConstructor();
        ABIObject inputABIObject = ABIObjectFactory.createInputObject(abiDefinition);
        ABICodecObject abiCodecObject = new ABICodecObject();
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(Hex.decode(bin));
            outputStream.write(abiCodecObject.encodeValue(inputABIObject, params).encode());
            return outputStream.toByteArray();
        } catch (Exception e) {
            logger.error(" exception in encodeMethodFromObject : {}", e.getMessage());
        }
        String errorMsg = " cannot encode in encodeMethodFromObject with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    private Type buildType(ABIDefinition.NamedType namedType, String param)
            throws ABICodecException, JsonProcessingException {
        String typeStr = namedType.getType();
        ABIDefinition.Type paramType = new ABIDefinition.Type(typeStr);
        Type type = null;
        if (paramType.isList()) {
            List elements = new ArrayList();
            JsonNode jsonNode = this.objectMapper.readTree(param);
            assert jsonNode.isArray();

            ABIDefinition.NamedType subType = new ABIDefinition.NamedType();
            subType.setType(paramType.reduceDimensionAndGetType().getType());
            subType.setComponents(namedType.getComponents());

            for (JsonNode subNode : jsonNode) {
                String subNodeStr =
                        subNode.isTextual()
                                ? subNode.asText()
                                : this.objectMapper.writeValueAsString(subNode);
                Type element = buildType(subType, subNodeStr);
                elements.add(element);
            }
            type = paramType.isFixedList() ? new StaticArray(elements) : new DynamicArray(elements);
            return type;
        } else if (typeStr.equals("tuple")) {
            List<Type> components = new ArrayList<>();
            JsonNode jsonNode = this.objectMapper.readTree(param);
            assert jsonNode.isObject();
            for (ABIDefinition.NamedType component : namedType.getComponents()) {
                JsonNode subNode = jsonNode.get(component.getName());
                String subNodeStr =
                        subNode.isTextual()
                                ? subNode.asText()
                                : this.objectMapper.writeValueAsString(subNode);
                components.add(buildType(component, subNodeStr));
            }
            type =
                    namedType.isDynamic()
                            ? new DynamicStruct(components)
                            : new StaticStruct(components);
            return type;
        } else {
            if (typeStr.startsWith("uint")) {
                int bitSize = 256;
                if (!typeStr.equals("uint")) {
                    String bitSizeStr = typeStr.substring("uint".length());
                    try {
                        bitSize = Integer.parseInt(bitSizeStr);
                    } catch (NumberFormatException e) {
                        String errorMsg = " unrecognized uint type: " + typeStr;
                        logger.error(errorMsg);
                        throw new ABICodecException(errorMsg);
                    }
                }

                try {
                    Class<?> uintClass =
                            Class.forName(
                                    "org.fisco.bcos.sdk.codec.datatypes.generated.Uint" + bitSize);
                    type =
                            (Type)
                                    uintClass
                                            .getDeclaredConstructor(BigInteger.class)
                                            .newInstance(new BigInteger(param));
                } catch (ClassNotFoundException
                        | NoSuchMethodException
                        | InstantiationException
                        | IllegalAccessException
                        | InvocationTargetException e) {
                    String errorMsg = "unrecognized uint type: " + typeStr;
                    logger.error(errorMsg);
                    throw new ABICodecException(errorMsg);
                }

                return type;
            }

            if (typeStr.startsWith("int")) {
                int bitSize = 256;
                if (!typeStr.equals("int")) {
                    String bitSizeStr = typeStr.substring("int".length());
                    try {
                        bitSize = Integer.parseInt(bitSizeStr);
                    } catch (NumberFormatException e) {
                        String errorMsg = "unrecognized int type: " + typeStr;
                        logger.error(errorMsg);
                        throw new ABICodecException(errorMsg);
                    }
                }

                try {
                    Class<?> uintClass =
                            Class.forName(
                                    "org.fisco.bcos.sdk.codec.datatypes.generated.Int" + bitSize);
                    type =
                            (Type)
                                    uintClass
                                            .getDeclaredConstructor(BigInteger.class)
                                            .newInstance(new BigInteger(param));
                } catch (ClassNotFoundException
                        | NoSuchMethodException
                        | InstantiationException
                        | IllegalAccessException
                        | InvocationTargetException e) {
                    String errorMsg = "unrecognized uint type: " + typeStr;
                    logger.error(errorMsg);
                    throw new ABICodecException(errorMsg);
                }

                return type;
            }

            if (typeStr.equals("bool")) {
                type = new Bool(Boolean.parseBoolean(param));
                return type;
            }

            if (typeStr.equals("string")) {
                type = new Utf8String(param);
                return type;
            }

            if (typeStr.equals("bytes")) {
                JsonNode jsonNode = this.objectMapper.readTree(param);
                assert jsonNode.isArray();
                byte[] bytes = new byte[jsonNode.size()];
                for (int i = 0; i < jsonNode.size(); ++i) {
                    bytes[i] = ((byte) jsonNode.get(i).asInt());
                }
                type = new DynamicBytes(bytes);
                return type;
            }

            if (typeStr.startsWith("bytes")) {
                String lengthStr = typeStr.substring("bytes".length());
                int length;
                try {
                    length = Integer.parseInt(lengthStr);
                } catch (NumberFormatException e) {
                    String errorMsg = "unrecognized static byte array type: " + typeStr;
                    logger.error(errorMsg);
                    throw new ABICodecException(errorMsg);
                }

                if (length > 32) {
                    String errorMsg = "the length of static byte array exceeds 32: " + typeStr;
                    logger.error(errorMsg);
                    throw new ABICodecException(errorMsg);
                }

                JsonNode jsonNode = this.objectMapper.readTree(param);
                assert jsonNode.isArray();
                if (jsonNode.size() != length) {
                    String errorMsg =
                            String.format(
                                    "expected byte array at length %d but length of provided in data is %d",
                                    length, jsonNode.size());
                    logger.error(errorMsg);
                    throw new ABICodecException(errorMsg);
                }

                byte[] bytes = new byte[jsonNode.size()];
                for (int i = 0; i < jsonNode.size(); ++i) {
                    bytes[i] = ((byte) jsonNode.get(i).asInt());
                }
                try {
                    Class<?> bytesClass =
                            Class.forName(
                                    "org.fisco.bcos.sdk.codec.datatypes.generated.Bytes" + length);
                    type =
                            (Type)
                                    bytesClass
                                            .getDeclaredConstructor(byte[].class)
                                            .newInstance(bytes);
                } catch (ClassNotFoundException
                        | NoSuchMethodException
                        | InstantiationException
                        | IllegalAccessException
                        | InvocationTargetException e) {
                    e.printStackTrace();
                }
                return type;
            }
        }
        String errorMsg = "unrecognized type: " + typeStr;
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public byte[] encodeConstructorFromString(
            String abi, String bin, List<String> params, String path) throws ABICodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(abi);
        ABIDefinition abiDefinition = contractABIDefinition.getConstructor();
        List<ABIDefinition.NamedType> inputTypes = abiDefinition.getInputs();
        if (inputTypes.size() != params.size()) {
            String errorMsg =
                    String.format(
                            " expected %d parameters but provided %d parameters",
                            inputTypes.size(), params.size());
            logger.error(errorMsg);
            throw new ABICodecException(errorMsg);
        }

        Throwable cause;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            List<Type> types = new ArrayList<>();
            for (int i = 0; i < inputTypes.size(); ++i) {
                types.add(buildType(inputTypes.get(i), params.get(i)));
            }

            if (!this.isWasm) {
                outputStream.write(Hex.decode(bin));
                outputStream.write(
                        org.fisco.bcos.sdk.codec.abi.FunctionEncoder.encodeConstructor(types));
            } else {
                assert path != null;
                List<Type> deployParams = new ArrayList<>();
                deployParams.add(new DynamicBytes(Hex.decode(bin)));
                deployParams.add(
                        new DynamicBytes(
                                org.fisco.bcos.sdk.codec.scale.FunctionEncoder.encodeConstructor(
                                        types)));
                deployParams.add(new Utf8String(path));
                deployParams.add(new Utf8String(abi));
                byte[] input = "deployWasm(bytes,bytes,string,string)".getBytes();
                byte[] hash = this.cryptoSuite.hash(input);
                byte[] methodID = Arrays.copyOfRange(hash, 0, 4);
                outputStream.write(
                        org.fisco.bcos.sdk.codec.scale.FunctionEncoder.encodeParameters(
                                deployParams, methodID));
            }
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

    public byte[] encodeMethodFromString(String abi, String methodName, List<String> params)
            throws ABICodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(abi);
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
                List<ABIDefinition.NamedType> inputs = abiDefinition.getInputs();
                List<Type> inputTypes = new ArrayList<>();
                try {
                    for (int i = 0; i < inputs.size(); ++i) {
                        inputTypes.add(buildType(inputs.get(i), params.get(i)));
                    }
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    String signature =
                            FunctionEncoderInterface.buildMethodSignature(
                                    abiDefinition.getName(), inputTypes);
                    if (this.isWasm) {
                        byte[] methodID = this.scaleFunctionEncoder.buildMethodId(signature);
                        outputStream.write(
                                org.fisco.bcos.sdk.codec.scale.FunctionEncoder.encodeParameters(
                                        inputTypes, methodID));
                    } else {
                        byte[] methodID = this.abiFunctionEncoder.buildMethodId(signature);
                        outputStream.write(
                                org.fisco.bcos.sdk.codec.abi.FunctionEncoder.encodeParameters(
                                        inputTypes, methodID));
                    }
                    return outputStream.toByteArray();
                } catch (IOException e) {
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

    public List<Type> decodeMethodAndGetOutputObject(String abi, String methodName, String output)
            throws ABICodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(abi);
        List<ABIDefinition> methods = contractABIDefinition.getFunctions().get(methodName);
        for (ABIDefinition abiDefinition : methods) {
            List<ABIDefinition.NamedType> outputs = abiDefinition.getOutputs();
            List<TypeReference<Type>> outputTypes = new ArrayList<>();
            try {
                for (ABIDefinition.NamedType namedType : outputs) {
                    outputTypes.add(TypeReference.makeTypeReference(namedType.getType(), false));
                }
                List<Type> decodedOutputs = this.functionReturnDecoder.decode(output, outputTypes);
                return decodedOutputs;
            } catch (Exception e) {
                logger.error("exception in decodeMethodToObject: {}", e.getMessage());
            }
        }

        String errorMsg =
                String.format(
                        "cannot decode in decodeMethodToObject with appropriate interface ABI: methodName = %s",
                        methodName);
        logger.error(errorMsg);
        throw new ABICodecException(errorMsg);
    }

    public List<Object> decodeMethod(ABIDefinition abiDefinition, String output)
            throws ABICodecException {
        return this.decodeMethodAndGetOutputObject(abiDefinition, output).getLeft();
    }

    public List<Type> decodeMethod(String ABI, String methodName, String output)
            throws ABICodecException {
        return this.decodeMethodAndGetOutputObject(ABI, methodName, output);
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
        org.fisco.bcos.sdk.codec.abi.FunctionEncoder functionEncoder =
                new org.fisco.bcos.sdk.codec.abi.FunctionEncoder(this.cryptoSuite);
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
        org.fisco.bcos.sdk.codec.abi.FunctionEncoder functionEncoder =
                new org.fisco.bcos.sdk.codec.abi.FunctionEncoder(this.cryptoSuite);
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
        org.fisco.bcos.sdk.codec.abi.FunctionEncoder functionEncoder =
                new org.fisco.bcos.sdk.codec.abi.FunctionEncoder(this.cryptoSuite);
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
        org.fisco.bcos.sdk.codec.abi.FunctionEncoder functionEncoder =
                new org.fisco.bcos.sdk.codec.abi.FunctionEncoder(this.cryptoSuite);
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
