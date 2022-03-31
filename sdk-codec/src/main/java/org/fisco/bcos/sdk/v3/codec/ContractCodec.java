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

package org.fisco.bcos.sdk.v3.codec;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.fisco.bcos.sdk.v3.codec.abi.Constant;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicStruct;
import org.fisco.bcos.sdk.v3.codec.datatypes.StaticArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.StaticStruct;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8;
import org.fisco.bcos.sdk.v3.codec.scale.FunctionEncoder;
import org.fisco.bcos.sdk.v3.codec.scale.FunctionReturnDecoder;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIDefinitionFactory;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIObject;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIObjectFactory;
import org.fisco.bcos.sdk.v3.codec.wrapper.ContractABIDefinition;
import org.fisco.bcos.sdk.v3.codec.wrapper.ContractCodecJsonWrapper;
import org.fisco.bcos.sdk.v3.codec.wrapper.ContractCodecTools;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.model.EventLog;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.fisco.bcos.sdk.v3.utils.Numeric;
import org.fisco.bcos.sdk.v3.utils.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** ABI encode and decode tool */
public class ContractCodec {

    private static final Logger logger = LoggerFactory.getLogger(ContractCodec.class);

    private final CryptoSuite cryptoSuite;
    private final boolean isWasm;
    private final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
    private FunctionEncoderInterface functionEncoder = null;
    private FunctionReturnDecoderInterface functionReturnDecoder = null;
    private final ABIDefinitionFactory abiDefinitionFactory;
    private final ContractCodecJsonWrapper contractCodecJsonWrapper =
            new ContractCodecJsonWrapper();

    public ContractCodec(CryptoSuite cryptoSuite, boolean isWasm) {
        this.cryptoSuite = cryptoSuite;
        this.isWasm = isWasm;
        if (isWasm) {
            this.functionEncoder = new FunctionEncoder(cryptoSuite);
            this.functionReturnDecoder = new FunctionReturnDecoder();
        } else {
            this.functionEncoder = new org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder(cryptoSuite);
            this.functionReturnDecoder =
                    new org.fisco.bcos.sdk.v3.codec.abi.FunctionReturnDecoder();
        }
        this.abiDefinitionFactory = new ABIDefinitionFactory(cryptoSuite);
    }

    public boolean isWasm() {
        return isWasm;
    }

    public CryptoSuite getCryptoSuite() {
        return this.cryptoSuite;
    }

    public byte[] encodeConstructor(String abi, String bin, List<Object> params)
            throws ContractCodecException {

        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(abi);
        ABIDefinition abiDefinition = contractABIDefinition.getConstructor();
        ABIObject inputABIObject = ABIObjectFactory.createInputObject(abiDefinition);
        try {
            byte[] encodeParams =
                    ContractCodecTools.encode(
                            ContractCodecTools.decodeABIObjectValue(inputABIObject, params),
                            isWasm);
            return encodeConstructorFromBytes(bin, encodeParams, abi);
        } catch (Exception e) {
            logger.error(" exception in encodeConstructor : {}", e.getMessage());
        }
        String errorMsg = " cannot encode in encodeConstructor with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ContractCodecException(errorMsg);
    }

    private Type buildType(ABIDefinition.NamedType namedType, String param)
            throws ContractCodecException, IOException {
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
                        String errorMsg =
                                " unrecognized type: " + typeStr + ", error:" + e.getCause();
                        logger.error(errorMsg);
                        throw new ContractCodecException(errorMsg);
                    }
                }

                try {
                    Class<?> uintClass =
                            Class.forName(
                                    "org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint"
                                            + bitSize);
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
                    String errorMsg =
                            "buildType error, type: " + typeStr + ", error: " + e.getCause();
                    logger.error(errorMsg);
                    throw new ContractCodecException(errorMsg);
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
                        throw new ContractCodecException(errorMsg);
                    }
                }

                try {
                    Class<?> uintClass =
                            Class.forName(
                                    "org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int"
                                            + bitSize);
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
                    String errorMsg = "unrecognized type: " + typeStr + ", error:" + e.getCause();
                    logger.error(errorMsg);
                    throw new ContractCodecException(errorMsg);
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
                byte[] bytes = ContractCodecJsonWrapper.tryDecodeInputData(param);
                if (bytes == null) {
                    bytes = param.getBytes();
                }
                type = new DynamicBytes(bytes);
                return type;
            }

            if (typeStr.equals("address")) {
                type = new Address(param);
                return type;
            }

            // static bytesN
            if (typeStr.startsWith("bytes")) {
                String lengthStr = typeStr.substring("bytes".length());
                int length;
                try {
                    length = Integer.parseInt(lengthStr);
                } catch (NumberFormatException e) {
                    String errorMsg = "unrecognized static byte array type: " + typeStr;
                    logger.error(errorMsg);
                    throw new ContractCodecException(errorMsg);
                }

                if (length > 32) {
                    String errorMsg = "the length of static byte array exceeds 32: " + typeStr;
                    logger.error(errorMsg);
                    throw new ContractCodecException(errorMsg);
                }
                byte[] bytesN = ContractCodecJsonWrapper.tryDecodeInputData(param);
                if (bytesN == null) {
                    bytesN = param.getBytes();
                }
                if (bytesN.length != length) {
                    String errorMsg =
                            String.format(
                                    "expected byte array at length %d but length of provided in data is %d",
                                    length, bytesN.length);
                    logger.error(errorMsg);
                    throw new ContractCodecException(errorMsg);
                }

                try {
                    Class<?> bytesClass =
                            Class.forName(
                                    "org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes"
                                            + length);
                    type =
                            (Type)
                                    bytesClass
                                            .getDeclaredConstructor(byte[].class)
                                            .newInstance(bytesN);
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
        throw new ContractCodecException(errorMsg);
    }

    public byte[] encodeConstructorFromString(String abi, String bin, List<String> params)
            throws ContractCodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(abi);
        ABIDefinition abiDefinition = contractABIDefinition.getConstructor();
        List<ABIDefinition.NamedType> inputTypes = abiDefinition.getInputs();
        if (inputTypes.size() != params.size()) {
            String errorMsg =
                    String.format(
                            " expected %d parameters but provided %d parameters",
                            inputTypes.size(), params.size());
            logger.error(errorMsg);
            throw new ContractCodecException(errorMsg);
        }

        try {
            List<Type> types = new ArrayList<>();
            for (int i = 0; i < inputTypes.size(); ++i) {
                types.add(buildType(inputTypes.get(i), params.get(i)));
            }
            byte[] paramBytes;
            if (isWasm) {
                paramBytes = FunctionEncoder.encodeConstructor(types);
            } else {
                paramBytes =
                        org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder.encodeConstructor(types);
            }
            return encodeConstructorFromBytes(bin, paramBytes, abi);
        } catch (Exception e) {
            String errorMsg =
                    " cannot encode in encodeMethodFromObject with appropriate interface ABI, cause:"
                            + e.getMessage();
            logger.error(errorMsg);
            throw new ContractCodecException(errorMsg);
        }
    }

    public byte[] encodeConstructorFromBytes(String bin, byte[] params, String abi)
            throws ContractCodecException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            if (!this.isWasm) {
                outputStream.write(Hex.decode(bin));
                if (params != null) {
                    outputStream.write(params);
                }
            } else {
                List<Type> deployParams = new ArrayList<>();
                deployParams.add(new DynamicBytes(Hex.decode(bin)));
                if (params != null) {
                    deployParams.add(new DynamicBytes(params));
                } else {
                    deployParams.add(new Uint8(0));
                }
                // deployParams.add(new Utf8String(abi));
                outputStream.write(FunctionEncoder.encodeParameters(deployParams, null));
            }
            return outputStream.toByteArray();
        } catch (Exception e) {
            logger.error(" exception in encodeMethodFromObject : {}", e.getMessage());
            String errorMsg =
                    " cannot encode in encodeMethodFromObject with appropriate interface ABI, cause:"
                            + e.getMessage();
            logger.error(errorMsg);
            throw new ContractCodecException(errorMsg);
        }
    }

    public byte[] encodeMethod(String abi, String methodName, List<Object> params)
            throws ContractCodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(abi);
        List<ABIDefinition> methods = contractABIDefinition.getFunctions().get(methodName);
        if (methods == null || methods.isEmpty()) {
            throw new ContractCodecException(Constant.NO_APPROPRIATE_ABI_METHOD);
        }
        for (ABIDefinition abiDefinition : methods) {
            if (abiDefinition.getInputs().size() == params.size()) {
                ABIObject inputABIObject = ABIObjectFactory.createInputObject(abiDefinition);
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    outputStream.write(abiDefinition.getMethodId(this.cryptoSuite));
                    outputStream.write(
                            ContractCodecTools.encode(
                                    ContractCodecTools.decodeABIObjectValue(inputABIObject, params),
                                    isWasm));
                    return outputStream.toByteArray();
                } catch (Exception e) {
                    logger.error(" exception in encodeMethodFromObject : {}", e.getMessage());
                }
            }
        }
        logger.error(Constant.NO_APPROPRIATE_ABI_METHOD);
        throw new ContractCodecException(Constant.NO_APPROPRIATE_ABI_METHOD);
    }

    public byte[] encodeMethodById(String abi, byte[] methodId, List<Object> params)
            throws ContractCodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(abi);
        ABIDefinition abiDefinition = contractABIDefinition.getABIDefinitionByMethodId(methodId);
        if (abiDefinition == null) {
            throw new ContractCodecException(Constant.NO_APPROPRIATE_ABI_METHOD);
        }
        ABIObject inputABIObject = ABIObjectFactory.createInputObject(abiDefinition);
        Exception cause;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(methodId);
            outputStream.write(
                    ContractCodecTools.encode(
                            ContractCodecTools.decodeABIObjectValue(inputABIObject, params),
                            isWasm));
            return outputStream.toByteArray();
        } catch (Exception e) {
            cause = e;
            logger.error(" exception in encodeMethodByIdFromObject : {}", e.getMessage());
        }

        String errorMsg =
                " cannot encode in encodeMethodByIdFromObject with appropriate interface ABI, cause:"
                        + cause.getMessage();
        logger.error(errorMsg);
        throw new ContractCodecException(errorMsg);
    }

    private ABIDefinition getABIDefinition(String methodInterface) throws ContractCodecException {
        int start = methodInterface.indexOf("(");
        int end = methodInterface.lastIndexOf(")");
        if (start == -1 || end == -1 || start >= end) {
            String errorMsg = " error format";
            logger.error(errorMsg);
            throw new ContractCodecException(errorMsg);
        }
        String name = methodInterface.substring(0, start);
        String type = methodInterface.substring(start + 1, end);
        String[] types = type.split(",");
        List<ABIDefinition.NamedType> inputs = new ArrayList<ABIDefinition.NamedType>();
        for (String s : types) {
            ABIDefinition.NamedType input = new ABIDefinition.NamedType("name", s);
            inputs.add(input);
        }

        return new ABIDefinition(false, inputs, name, null, "function", false, "nonpayable");
    }

    public byte[] encodeMethodByInterface(String methodInterface, List<Object> params)
            throws ContractCodecException {
        ABIDefinition abiDefinition = this.getABIDefinition(methodInterface);
        if (abiDefinition.getInputs().size() == params.size()) {
            ABIObject inputABIObject = ABIObjectFactory.createInputObject(abiDefinition);
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                outputStream.write(abiDefinition.getMethodId(this.cryptoSuite));
                outputStream.write(
                        ContractCodecTools.encode(
                                ContractCodecTools.decodeABIObjectValue(inputABIObject, params),
                                isWasm));
                return outputStream.toByteArray();
            } catch (Exception e) {
                logger.error(
                        " exception in encodeMethodByInterfaceFromObject : {}", e.getMessage());
            }
        }

        String errorMsg = " cannot encode in encodeMethodByInterfaceFromObject";
        logger.error(errorMsg);
        throw new ContractCodecException(errorMsg);
    }

    public byte[] encodeMethodFromString(String abi, String methodName, List<String> params)
            throws ContractCodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(abi);
        List<ABIDefinition> methods = contractABIDefinition.getFunctions().get(methodName);
        if (methods == null) {
            logger.debug(
                    "Invalid methodName: {}, all the functions are: {}",
                    methodName,
                    contractABIDefinition.getFunctions());
            throw new ContractCodecException(
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
                    byte[] methodID = this.functionEncoder.buildMethodId(signature);
                    if (this.isWasm) {
                        outputStream.write(FunctionEncoder.encodeParameters(inputTypes, methodID));
                    } else {
                        outputStream.write(
                                org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder.encodeParameters(
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
        throw new ContractCodecException(errorMsg);
    }

    public byte[] encodeMethodByIdFromString(String abi, byte[] methodId, List<String> params)
            throws ContractCodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(abi);
        ABIDefinition abiDefinition = contractABIDefinition.getABIDefinitionByMethodId(methodId);
        if (abiDefinition == null) {
            logger.error(Constant.NO_APPROPRIATE_ABI_METHOD);
            throw new ContractCodecException(Constant.NO_APPROPRIATE_ABI_METHOD);
        }
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
            byte[] methodID = this.functionEncoder.buildMethodId(signature);
            if (this.isWasm) {
                outputStream.write(FunctionEncoder.encodeParameters(inputTypes, methodID));
            } else {
                outputStream.write(
                        org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder.encodeParameters(
                                inputTypes, methodID));
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            logger.error(" exception in encodeMethodByIdFromString : {}", e.getMessage());
        }

        String errorMsg =
                " cannot encode in encodeMethodByIdFromString with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ContractCodecException(errorMsg);
    }

    public byte[] encodeMethodByInterfaceFromString(String methodInterface, List<String> params)
            throws ContractCodecException {
        ABIDefinition abiDefinition = this.getABIDefinition(methodInterface);
        if (abiDefinition.getInputs().size() == params.size()) {
            ABIObject inputABIObject = ABIObjectFactory.createInputObject(abiDefinition);
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                outputStream.write(abiDefinition.getMethodId(this.cryptoSuite));
                outputStream.write(
                        ContractCodecTools.encode(
                                contractCodecJsonWrapper.encode(inputABIObject, params), isWasm));
                return outputStream.toByteArray();
            } catch (IOException e) {
                logger.error(
                        " exception in encodeMethodByInterfaceFromString : {}", e.getMessage());
            }
        }

        String errorMsg = " cannot encode in encodeMethodByInterfaceFromString";
        logger.error(errorMsg);
        throw new ContractCodecException(errorMsg);
    }

    public Pair<List<Object>, List<ABIObject>> decodeMethodAndGetOutputObject(
            ABIDefinition abiDefinition, String output) throws ContractCodecException {
        ABIObject outputABIObject = ABIObjectFactory.createOutputObject(abiDefinition);
        try {
            return ContractCodecTools.decodeJavaObjectAndOutputObject(
                    outputABIObject, output, isWasm);
        } catch (Exception e) {
            logger.error(" exception in decodeMethodToObject : ", e);
        }
        String errorMsg = " cannot decode in decodeMethodToObject with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ContractCodecException(errorMsg);
    }

    public List<Type> decodeMethodAndGetOutputObject(String abi, String methodName, String output)
            throws ContractCodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(abi);
        List<ABIDefinition> methods = contractABIDefinition.getFunctions().get(methodName);
        for (ABIDefinition abiDefinition : methods) {
            List<ABIDefinition.NamedType> outputs = abiDefinition.getOutputs();
            List<TypeReference<Type>> outputTypes = new ArrayList<>();
            try {
                for (ABIDefinition.NamedType namedType : outputs) {
                    outputTypes.add(TypeReference.makeTypeReference(namedType.getType(), false));
                }
                return this.functionReturnDecoder.decode(output, outputTypes);
            } catch (Exception e) {
                logger.error("exception in decodeMethodToObject: {}, e:", e.getMessage(), e);
            }
        }

        String errorMsg =
                String.format(
                        "cannot decode in decodeMethodToObject with appropriate interface ABI: methodName = %s",
                        methodName);
        logger.error(errorMsg);
        throw new ContractCodecException(errorMsg);
    }

    public List<Object> decodeMethod(ABIDefinition abiDefinition, String output)
            throws ContractCodecException {
        return this.decodeMethodAndGetOutputObject(abiDefinition, output).getLeft();
    }

    public List<Type> decodeMethod(String ABI, String methodName, String output)
            throws ContractCodecException {
        return this.decodeMethodAndGetOutputObject(ABI, methodName, output);
    }

    public List<Object> decodeMethodById(String abi, byte[] methodId, byte[] output)
            throws ContractCodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(abi);
        ABIDefinition abiDefinition = contractABIDefinition.getABIDefinitionByMethodId(methodId);
        if (abiDefinition == null) {
            String errorMsg = " methodId " + methodId + " is invalid";
            logger.error(errorMsg);
            throw new ContractCodecException(errorMsg);
        }
        ABIObject outputABIObject = ABIObjectFactory.createOutputObject(abiDefinition);
        try {
            return ContractCodecTools.decodeJavaObject(
                    outputABIObject, Hex.toHexString(output), isWasm);
        } catch (Exception e) {
            logger.error(" exception in decodeMethodByIdToObject : {}", e.getMessage());
        }

        String errorMsg = " cannot decode in decodeMethodToObject with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ContractCodecException(errorMsg);
    }

    public List<Object> decodeMethodByInterface(String abi, String methodInterface, byte[] output)
            throws ContractCodecException {
        byte[] methodId = functionEncoder.buildMethodId(methodInterface);
        return this.decodeMethodById(abi, methodId, output);
    }

    public List<String> decodeMethodToString(String abi, String methodName, byte[] output)
            throws ContractCodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(abi);
        List<ABIDefinition> methods = contractABIDefinition.getFunctions().get(methodName);
        if (methods == null) {
            throw new ContractCodecException(
                    "Invalid method "
                            + methodName
                            + ", supported methods are: "
                            + contractABIDefinition.getFunctions().keySet());
        }
        for (ABIDefinition abiDefinition : methods) {
            ABIObject outputABIObject = ABIObjectFactory.createOutputObject(abiDefinition);
            try {
                return contractCodecJsonWrapper.decode(outputABIObject, output, isWasm);
            } catch (Exception e) {
                logger.error(" exception in decodeMethodToString : {}", e.getMessage());
            }
        }

        String errorMsg = " cannot decode in decodeMethodToString with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ContractCodecException(errorMsg);
    }

    public List<String> decodeMethodByIdToString(String abi, byte[] methodId, byte[] output)
            throws ContractCodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(abi);
        ABIDefinition abiDefinition = contractABIDefinition.getABIDefinitionByMethodId(methodId);
        if (abiDefinition == null) {
            String errorMsg = " methodId " + methodId + " is invalid";
            logger.error(errorMsg);
            throw new ContractCodecException(errorMsg);
        }
        ABIObject outputABIObject = ABIObjectFactory.createOutputObject(abiDefinition);
        try {
            return contractCodecJsonWrapper.decode(outputABIObject, output, isWasm);
        } catch (UnsupportedOperationException | ClassNotFoundException e) {
            logger.error(" exception in decodeMethodByIdToString : {}", e.getMessage());
        }

        String errorMsg =
                " cannot decode in decodeMethodByIdToString with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ContractCodecException(errorMsg);
    }

    public List<String> decodeMethodByInterfaceToString(
            String abi, String methodInterface, byte[] output) throws ContractCodecException {
        byte[] methodId = functionEncoder.buildMethodId(methodInterface);
        return this.decodeMethodByIdToString(abi, methodId, output);
    }

    public List<Object> decodeEvent(String abi, String eventName, EventLog log)
            throws ContractCodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(abi);
        List<ABIDefinition> events = contractABIDefinition.getEvents().get(eventName);
        if (events == null) {
            throw new ContractCodecException(
                    "Invalid event "
                            + eventName
                            + ", supported events are: "
                            + contractABIDefinition.getEvents().keySet());
        }
        for (ABIDefinition abiDefinition : events) {
            ABIObject inputObject = ABIObjectFactory.createEventInputObject(abiDefinition);
            try {
                List<Object> params = new ArrayList<>();
                if (!log.getData().equals("0x")) {
                    params =
                            ContractCodecTools.decodeJavaObject(inputObject, log.getData(), isWasm);
                }
                List<String> topics = log.getTopics();
                return this.mergeEventParamsAndTopics(abiDefinition, params, topics);
            } catch (Exception e) {
                logger.error(" exception in decodeEventToObject : {}", e.getMessage());
            }
        }

        String errorMsg = " cannot decode in decodeEventToObject with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ContractCodecException(errorMsg);
    }

    public List<Object> decodeEventByTopic(String abi, String eventTopic, EventLog log)
            throws ContractCodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(abi);
        ABIDefinition abiDefinition =
                contractABIDefinition.getABIDefinitionByEventTopic(eventTopic);
        ABIObject inputObject = ABIObjectFactory.createEventInputObject(abiDefinition);
        try {
            List<Object> params = new ArrayList<>();
            if (!log.getData().equals("0x")) {
                params = ContractCodecTools.decodeJavaObject(inputObject, log.getData(), isWasm);
            }
            List<String> topics = log.getTopics();
            return this.mergeEventParamsAndTopics(abiDefinition, params, topics);
        } catch (Exception e) {
            logger.error(" exception in decodeEventByTopicToObject : {}", e.getMessage());
        }

        String errorMsg =
                " cannot decode in decodeEventByTopicToObject with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ContractCodecException(errorMsg);
    }

    public List<Object> decodeEventByInterface(String abi, String eventSignature, EventLog log)
            throws ContractCodecException {
        byte[] methodId = functionEncoder.buildMethodId(eventSignature);
        return this.decodeEventByTopic(abi, Numeric.toHexString(methodId), log);
    }

    public List<String> decodeEventToString(String abi, String eventName, EventLog log)
            throws ContractCodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(abi);
        List<ABIDefinition> events = contractABIDefinition.getEvents().get(eventName);
        if (events == null) {
            throw new ContractCodecException(
                    "Invalid event "
                            + eventName
                            + ", current supported events are: "
                            + contractABIDefinition.getEvents().keySet());
        }
        for (ABIDefinition abiDefinition : events) {
            ABIObject inputObject = ABIObjectFactory.createEventInputObject(abiDefinition);
            try {
                List<String> params = new ArrayList<>();
                if (!log.getData().equals("0x")) {
                    params =
                            contractCodecJsonWrapper.decode(
                                    inputObject, Hex.decode(log.getData()), isWasm);
                }
                List<String> topics = log.getTopics();
                return this.mergeEventParamsAndTopicsToString(abiDefinition, params, topics);
            } catch (Exception e) {
                logger.error(" exception in decodeEventToString : {}", e.getMessage());
            }
        }

        String errorMsg = " cannot decode in decodeEventToString with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ContractCodecException(errorMsg);
    }

    public List<String> decodeEventByTopicToString(String abi, String eventTopic, EventLog log)
            throws ContractCodecException {
        ContractABIDefinition contractABIDefinition = this.abiDefinitionFactory.loadABI(abi);
        ABIDefinition abiDefinition =
                contractABIDefinition.getABIDefinitionByEventTopic(eventTopic);
        ABIObject inputObject = ABIObjectFactory.createEventInputObject(abiDefinition);
        try {
            List<String> params = new ArrayList<>();
            if (!log.getData().equals("0x")) {
                params =
                        contractCodecJsonWrapper.decode(
                                inputObject, Hex.decode(log.getData()), isWasm);
            }
            List<String> topics = log.getTopics();
            return this.mergeEventParamsAndTopicsToString(abiDefinition, params, topics);
        } catch (Exception e) {
            logger.error(" exception in decodeEventByTopicToString : {}", e.getMessage());
        }

        String errorMsg =
                " cannot decode in decodeEventByTopicToString with appropriate interface ABI";
        logger.error(errorMsg);
        throw new ContractCodecException(errorMsg);
    }

    public List<String> decodeEventByInterfaceToString(
            String abi, String eventSignature, EventLog log) throws ContractCodecException {
        byte[] methodId = functionEncoder.buildMethodId(eventSignature);
        return this.decodeEventByTopicToString(abi, Numeric.toHexString(methodId), log);
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
