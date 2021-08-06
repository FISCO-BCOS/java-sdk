/**
 * Copyright 2014-2020 [fisco-dev]
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fisco.bcos.sdk.codegen;

import com.squareup.javapoet.*;
import org.fisco.bcos.sdk.abi.FunctionEncoder;
import org.fisco.bcos.sdk.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.*;
import org.fisco.bcos.sdk.abi.datatypes.generated.AbiTypes;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.codegen.exceptions.CodeGenException;
import org.fisco.bcos.sdk.contract.Contract;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.utils.Collection;
import org.fisco.bcos.sdk.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Generate Java Classes based on generated Solidity bin and abi files.
 */
public class SolidityContractWrapper {

    private static final Logger logger = LoggerFactory.getLogger(SolidityContractWrapper.class);

    private static final int maxSolidityBinSize = 0x40000;
    private static final int maxField = 8 * 1024;

    private static String BINARY_ARRAY_NAME = "BINARY_ARRAY";
    private static String SM_BINARY_ARRAY_NAME = "SM_BINARY_ARRAY";
    private static final String BINARY_NAME = "BINARY";
    private static final String SM_BINARY_NAME = "SM_BINARY";
    private static final String ABI_ARRAY_NAME = "ABI_ARRAY";
    private static final String ABI_NAME = "ABI";

    private static final String GET_BINARY_FUNC = "getBinary";
    private static final String CLIENT = "client";
    private static final String CREDENTIAL = "credential";
    private static final String CRYPTOSUITE = "cryptoSuite";
    private static final String CONTRACT_ADDRESS = "contractAddress";
    private static final String FROM_BLOCK = "fromBlock";
    private static final String TO_BLOCK = "toBlock";
    private static final String CALLBACK_VALUE = "callback";
    private static final String OTHER_TOPICS = "otherTopics";
    private static final String FUNC_NAME_PREFIX = "FUNC_";
    private static final String EVENT_ENCODER = "eventEncoder";

    private static final String regex = "(\\w+)(?:\\[(.*?)\\])(?:\\[(.*?)\\])?";
    private static final Pattern pattern = Pattern.compile(regex);

    public void generateJavaFiles(
            String contractName,
            String bin,
            String smBin,
            String abi,
            String destinationDir,
            String basePackageName)
            throws IOException, ClassNotFoundException, UnsupportedOperationException,
            CodeGenException {
        String className = StringUtils.capitaliseFirstLetter(contractName);

        logger.info("bin: {}", bin);
        logger.info("smBin: {}", smBin);

        if (bin.length() > maxSolidityBinSize) {
            throw new UnsupportedOperationException(
                    " contract binary too long, max support is 256k, now is "
                            + Integer.valueOf(bin.length()));
        }

        List<ABIDefinition> abiDefinitions = CodeGenUtils.loadContractAbiDefinition(abi);
        TypeSpec.Builder classBuilder = this.createClassBuilder(className, bin, smBin, abi);

        classBuilder.addMethod(
                buildGetBinaryMethod(CryptoSuite.class, CryptoType.class, CRYPTOSUITE));
        classBuilder.addMethod(buildConstructor(CryptoKeyPair.class, CREDENTIAL));

        classBuilder.addFields(this.buildFuncNameConstants(abiDefinitions));
        classBuilder.addMethods(this.buildFunctionDefinitions(classBuilder, abiDefinitions));
        classBuilder.addMethod(buildLoad(className, CryptoKeyPair.class, CREDENTIAL));
        classBuilder.addMethods(this.buildDeployMethods(className, abiDefinitions));

        this.write(basePackageName, classBuilder.build(), destinationDir);
    }

    protected void write(String packageName, TypeSpec typeSpec, String destinationDir)
            throws IOException {
        JavaFile javaFile =
                JavaFile.builder(packageName, typeSpec)
                        .indent("    ")
                        .skipJavaLangImports(true)
                        .build();

        javaFile.writeTo(new File(destinationDir));
    }

    private TypeSpec.Builder createClassBuilder(
            String className, String binary, String smBinary, String abi) {
        TypeSpec.Builder builder =
                TypeSpec.classBuilder(className)
                        .addModifiers(Modifier.PUBLIC)
                        .superclass(Contract.class)
                        .addAnnotation(
                                AnnotationSpec.builder(SuppressWarnings.class)
                                        .addMember("value", "$S", "unchecked")
                                        .build())
                        // binary fields
                        .addField(this.createArrayDefinition(BINARY_ARRAY_NAME, binary))
                        .addField(this.createDefinition(BINARY_NAME, BINARY_ARRAY_NAME))
                        .addField(this.createArrayDefinition(SM_BINARY_ARRAY_NAME, smBinary))
                        .addField(this.createDefinition(SM_BINARY_NAME, SM_BINARY_ARRAY_NAME))
                        // abi fields
                        .addField(this.createArrayDefinition(ABI_ARRAY_NAME, abi))
                        .addField(this.createDefinition(ABI_NAME, ABI_ARRAY_NAME));

        return builder;
    }

    public List<String> stringToArrayString(String binary) {

        List<String> binaryArray = new ArrayList<String>();

        for (int offset = 0; offset < binary.length(); ) {

            int length = binary.length() - offset;
            if (length > maxField) {
                length = maxField;
            }

            String item = binary.substring(offset, offset + length);

            binaryArray.add(item);
            offset += item.length();
        }

        return binaryArray;
    }

    private FieldSpec createArrayDefinition(String type, String binary) {
        List<String> binaryArray = this.stringToArrayString(binary);
        List<String> formatArray =
                new ArrayList<String>(Collections.nCopies(binaryArray.size(), "$S"));

        return FieldSpec.builder(String[].class, type)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                .initializer(
                        "{" + org.fisco.bcos.sdk.utils.StringUtils.joinAll(",", formatArray) + "}",
                        binaryArray.toArray())
                .build();
    }

    private FieldSpec createDefinition(String type, String binayArrayName) {
        return FieldSpec.builder(String.class, type)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                .initializer(
                        "org.fisco.bcos.sdk.utils.StringUtils.joinAll(\"\", "
                                + binayArrayName
                                + ")")
                .build();
    }

    private FieldSpec createEventDefinition(String name, List<NamedTypeName> parameters) {

        CodeBlock initializer = buildVariableLengthEventInitializer(name, parameters);

        return FieldSpec.builder(Event.class, this.buildEventDefinitionName(name))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer(initializer)
                .build();
    }

    private String buildEventDefinitionName(String eventName) {
        return eventName.toUpperCase() + "_EVENT";
    }

    private static boolean isOverLoadFunction(
            String name, List<ABIDefinition> functionDefinitions) {
        int count = 0;
        for (ABIDefinition functionDefinition : functionDefinitions) {
            if (!functionDefinition.getType().equals("function")) {
                continue;
            }

            if (functionDefinition.getName().equals(name)) {
                count += 1;
            }
        }
        return count > 1;
    }

    private List<MethodSpec> buildFunctionDefinitions(
            TypeSpec.Builder classBuilder, List<ABIDefinition> functionDefinitions)
            throws ClassNotFoundException {

        List<MethodSpec> methodSpecs = new ArrayList<>();
        for (ABIDefinition functionDefinition : functionDefinitions) {
            if (functionDefinition.getType().equals("function")) {
                MethodSpec ms = this.buildFunction(functionDefinition);
                methodSpecs.add(ms);

                if (!functionDefinition.isConstant()) {
                    MethodSpec msCallback = this.buildFunctionWithCallback(functionDefinition);
                    methodSpecs.add(msCallback);

                    MethodSpec msSeq = this.buildFunctionSignedTransaction(functionDefinition);
                    methodSpecs.add(msSeq);

                    boolean isOverLoad =
                            isOverLoadFunction(functionDefinition.getName(), functionDefinitions);
                    if (!functionDefinition.getInputs().isEmpty()) {
                        MethodSpec inputDecoder =
                                this.buildFunctionWithInputDecoder(functionDefinition, isOverLoad);
                        methodSpecs.add(inputDecoder);
                    }

                    if (!functionDefinition.getOutputs().isEmpty()) {
                        MethodSpec outputDecoder =
                                this.buildFunctionWithOutputDecoder(functionDefinition, isOverLoad);
                        methodSpecs.add(outputDecoder);
                    }
                }
            } else if (functionDefinition.getType().equals("event")) {
                methodSpecs.addAll(this.buildEventFunctions(functionDefinition, classBuilder));
            }
        }

        return methodSpecs;
    }

    private List<MethodSpec> buildDeployMethods(
            String className, List<ABIDefinition> functionDefinitions) {
        boolean constructor = false;
        List<MethodSpec> methodSpecs = new ArrayList<>();
        for (ABIDefinition functionDefinition : functionDefinitions) {
            if (functionDefinition.getType().equals("constructor")) {
                constructor = true;
                methodSpecs.add(
                        this.buildDeploy(
                                className, functionDefinition, CryptoKeyPair.class, CREDENTIAL));
            }
        }
        // constructor will not be specified in ABI file if its empty
        if (!constructor) {
            MethodSpec.Builder credentialsMethodBuilder =
                    getDeployMethodSpec(className, CryptoKeyPair.class, CREDENTIAL);
            methodSpecs.add(buildDeployNoParams(credentialsMethodBuilder, className, CREDENTIAL));
        }
        return methodSpecs;
    }

    private Iterable<FieldSpec> buildFuncNameConstants(List<ABIDefinition> functionDefinitions) {
        List<FieldSpec> fields = new ArrayList<>();
        Set<String> fieldNames = new HashSet<>();
        fieldNames.add(Contract.FUNC_DEPLOY);

        for (ABIDefinition functionDefinition : functionDefinitions) {
            if (functionDefinition.getType().equals("function")) {
                String funcName = functionDefinition.getName();

                if (!fieldNames.contains(funcName)) {
                    FieldSpec field =
                            FieldSpec.builder(
                                            String.class,
                                            funcNameToConst(funcName),
                                            Modifier.PUBLIC,
                                            Modifier.STATIC,
                                            Modifier.FINAL)
                                    .initializer("$S", funcName)
                                    .build();
                    fields.add(field);
                    fieldNames.add(funcName);
                }
            }
        }
        return fields;
    }

    private static MethodSpec buildGetBinaryMethod(
            Class authType, Class cryptoType, String authName) {
        MethodSpec.Builder toReturn =
                MethodSpec.methodBuilder(GET_BINARY_FUNC)
                        .addParameter(authType, authName)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(String.class);

        toReturn.addStatement(
                "return ($N.getCryptoTypeConfig() == $T.ECDSA_TYPE ? $N : $N)",
                authName,
                cryptoType,
                BINARY_NAME,
                SM_BINARY_NAME);
        return toReturn.build();
    }

    private static MethodSpec buildConstructor(Class authType, String authName) {
        MethodSpec.Builder toReturn =
                MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PROTECTED)
                        .addParameter(String.class, CONTRACT_ADDRESS)
                        .addParameter(Client.class, CLIENT)
                        .addParameter(authType, authName)
                        .addStatement(
                                "super($N, $N, $N, $N)",
                                getBinaryFuncDefinition(),
                                CONTRACT_ADDRESS,
                                CLIENT,
                                authName);
        return toReturn.build();
    }

    private MethodSpec buildDeploy(
            String className, ABIDefinition functionDefinition, Class authType, String authName) {
        MethodSpec.Builder methodBuilder = getDeployMethodSpec(className, authType, authName);
        String inputParams = this.addParameters(methodBuilder, functionDefinition.getInputs());

        if (!inputParams.isEmpty()) {
            return buildDeployWithParams(methodBuilder, className, inputParams, authName);
        } else {
            return buildDeployNoParams(methodBuilder, className, authName);
        }
    }

    private static MethodSpec buildDeployWithParams(
            MethodSpec.Builder methodBuilder,
            String className,
            String inputParams,
            String authName) {

        methodBuilder
                .addStatement(
                        "byte[] encodedConstructor = $T.encodeConstructor(" + "$T.<$T>asList($L)" + ")",
                        FunctionEncoder.class,
                        Arrays.class,
                        Type.class,
                        inputParams)
                .addStatement(
                        "return deploy(" + "$L.class, $L, $L, $L, encodedConstructor)",
                        className,
                        CLIENT,
                        authName,
                        getBinaryFuncDefinition());
        return methodBuilder.build();
    }

    private static MethodSpec buildDeployNoParams(
            MethodSpec.Builder methodBuilder, String className, String authName) {
        methodBuilder.addStatement(
                "return deploy($L.class, $L, $L, $L, null)",
                className,
                CLIENT,
                authName,
                getBinaryFuncDefinition());
        return methodBuilder.build();
    }

    private static MethodSpec.Builder getDeployMethodSpec(
            String className, Class authType, String authName) {
        return MethodSpec.methodBuilder("deploy")
                .addException(ContractException.class)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeVariableName.get(className, Type.class))
                .addParameter(Client.class, CLIENT)
                .addParameter(authType, authName);
    }

    private static MethodSpec buildLoad(String className, Class authType, String authName) {
        MethodSpec.Builder toReturn =
                MethodSpec.methodBuilder("load")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(TypeVariableName.get(className, Type.class))
                        .addParameter(String.class, CONTRACT_ADDRESS)
                        .addParameter(Client.class, CLIENT)
                        .addParameter(authType, authName)
                        .addStatement(
                                "return new $L($L, $L, $L)",
                                className,
                                CONTRACT_ADDRESS,
                                CLIENT,
                                authName);
        return toReturn.build();
    }

    private MethodSpec.Builder addParameter(
            MethodSpec.Builder methodBuilder, String type, String name) {

        ParameterSpec parameterSpec = this.buildParameterType(type, name);

        TypeName typeName = getNativeType(parameterSpec.type);

        ParameterSpec inputParameter = ParameterSpec.builder(typeName, parameterSpec.name).build();

        methodBuilder.addParameter(inputParameter);

        return methodBuilder;
    }

    private String addParameters(
            MethodSpec.Builder methodBuilder, List<ABIDefinition.NamedType> namedTypes) {
        List<ParameterSpec> inputParameterTypes = buildParameterTypes(namedTypes);
        List<ParameterSpec> nativeInputParameterTypes = new ArrayList<>(inputParameterTypes.size());
        for (ParameterSpec parameterSpec : inputParameterTypes) {
            TypeName typeName = getNativeType(parameterSpec.type);
            nativeInputParameterTypes.add(
                    ParameterSpec.builder(typeName, parameterSpec.name).build());
        }
        methodBuilder.addParameters(nativeInputParameterTypes);
        return Collection.join(
                namedTypes,
                ", \n",
                // this results in fully qualified names being generated
                this::createMappedParameterTypes);
    }

    private String addParameters(List<ABIDefinition.NamedType> namedTypes) {

        List<ParameterSpec> inputParameterTypes = buildParameterTypes(namedTypes);

        List<ParameterSpec> nativeInputParameterTypes = new ArrayList<>(inputParameterTypes.size());
        for (ParameterSpec parameterSpec : inputParameterTypes) {
            TypeName typeName = getNativeType(parameterSpec.type);
            nativeInputParameterTypes.add(
                    ParameterSpec.builder(typeName, parameterSpec.name).build());
        }

        return Collection.join(
                namedTypes,
                ", \n",
                // this results in fully qualified names being generated
                this::createMappedParameterTypes);
    }

    private String createMappedParameterTypes(ABIDefinition.NamedType namedType) {

        String name = namedType.getName();
        String type = namedType.getType();
        ABIDefinition.Type innerType = new ABIDefinition.Type(type);

        ParameterSpec parameterSpec = ParameterSpec.builder(buildTypeName(type), name).build();

        if (parameterSpec.type instanceof ParameterizedTypeName) {
            List<TypeName> typeNames = ((ParameterizedTypeName) parameterSpec.type).typeArguments;
            if (typeNames.size() != 1) {
                throw new UnsupportedOperationException(
                        "Only a single parameterized type is supported");
            } else {
                String parameterSpecType = parameterSpec.type.toString();
                TypeName typeName = typeNames.get(0);
                String typeMapInput = typeName + ".class";

                if (typeName instanceof ParameterizedTypeName) {
                    List<TypeName> typeArguments = ((ParameterizedTypeName) typeName).typeArguments;
                    if (typeArguments.size() != 1) {
                        throw new UnsupportedOperationException(
                                "Only a single parameterized type is supported");
                    }

                    TypeName innerTypeName = typeArguments.get(0);
                    parameterSpecType =
                            ((ParameterizedTypeName) parameterSpec.type).rawType.toString();

                    typeMapInput =
                            ((ParameterizedTypeName) typeName).rawType
                                    + ".class, "
                                    + innerTypeName
                                    + ".class";
                }

                if (innerType.isDynamicList()) { // dynamic array
                    return parameterSpec.name
                            + ".isEmpty()?org.fisco.bcos.sdk.abi.datatypes.DynamicArray.empty"
                            + "(\""
                            + type
                            + "\"):"
                            + "new "
                            + parameterSpecType
                            + "(\n"
                            + "        org.fisco.bcos.sdk.abi.Utils.typeMap("
                            + parameterSpec.name
                            + ", "
                            + typeMapInput
                            + "))";
                } else { // static array
                    return "new "
                            + parameterSpecType
                            + "(\n"
                            + "        org.fisco.bcos.sdk.abi.Utils.typeMap("
                            + parameterSpec.name
                            + ", "
                            + typeMapInput
                            + "))";
                }
            }
        } else {
            return "new " + parameterSpec.type + "(" + parameterSpec.name + ")";
        }
    }

    private TypeName getWrapperRawType(TypeName typeName) {
        if (typeName instanceof ParameterizedTypeName) {
            return ClassName.get(List.class);
        }
        return getNativeType(typeName);
    }

    protected static TypeName getNativeType(TypeName typeName) {

        if (typeName instanceof ParameterizedTypeName) {
            return getNativeType((ParameterizedTypeName) typeName);
        }

        String simpleName = ((ClassName) typeName).simpleName();

        if (simpleName.startsWith(Address.class.getSimpleName())) {
            return TypeName.get(String.class);
        } else if (simpleName.startsWith("Uint")) {
            return TypeName.get(BigInteger.class);
        } else if (simpleName.startsWith("Int")) {
            return TypeName.get(BigInteger.class);
        } else if (simpleName.startsWith(Utf8String.class.getSimpleName())) {
            return TypeName.get(String.class);
        } else if (simpleName.startsWith("Bytes")) {
            return TypeName.get(byte[].class);
        } else if (simpleName.startsWith(DynamicBytes.class.getSimpleName())) {
            return TypeName.get(byte[].class);
        } else if (simpleName.startsWith(Bool.class.getSimpleName())) {
            return TypeName.get(Boolean.class); // boolean cannot be a parameterized type
        } else {
            throw new UnsupportedOperationException(
                    "Unsupported type: " + typeName + ", no native type mapping exists.");
        }
    }

    protected static TypeName getNativeType(ParameterizedTypeName parameterizedTypeName) {
        List<TypeName> typeNames = parameterizedTypeName.typeArguments;
        List<TypeName> nativeTypeNames = new ArrayList<>(typeNames.size());
        for (TypeName enclosedTypeName : typeNames) {
            nativeTypeNames.add(getNativeType(enclosedTypeName));
        }
        return ParameterizedTypeName.get(
                ClassName.get(List.class),
                nativeTypeNames.toArray(new TypeName[nativeTypeNames.size()]));
    }

    protected static TypeName getEventNativeType(TypeName typeName) {
        if (typeName instanceof ParameterizedTypeName) {
            return TypeName.get(byte[].class);
        }

        String simpleName = ((ClassName) typeName).simpleName();
        if ("Utf8String".equals(simpleName)) {
            return TypeName.get(byte[].class);
        } else {
            return getNativeType(typeName);
        }
    }

    private ParameterSpec buildParameterType(String type, String name) {

        return ParameterSpec.builder(buildTypeName(type), name).build();
    }

    protected static List<ParameterSpec> buildParameterTypes(
            List<ABIDefinition.NamedType> namedTypes) {

        List<ParameterSpec> result = new ArrayList<>(namedTypes.size());
        for (int i = 0; i < namedTypes.size(); i++) {
            ABIDefinition.NamedType namedType = namedTypes.get(i);
            String name = createValidParamName(namedType.getName(), i);
            String type = namedTypes.get(i).getType();
            namedType.setName(name);
            result.add(ParameterSpec.builder(buildTypeName(type), name).build());
        }
        return result;
    }

    /**
     * Public Solidity arrays and maps require an unnamed input parameter - multiple if they require
     * a struct type.
     *
     * @param name parameter name
     * @param idx  parameter index
     * @return non-empty parameter name
     */
    protected static String createValidParamName(String name, int idx) {
        if (name.equals("")) {
            return "param" + idx;
        } else {
            return name;
        }
    }

    protected static List<TypeName> buildTypeNames(List<ABIDefinition.NamedType> namedTypes) {
        List<TypeName> result = new ArrayList<>(namedTypes.size());
        for (ABIDefinition.NamedType namedType : namedTypes) {
            result.add(buildTypeName(namedType.getType()));
        }
        return result;
    }

    private MethodSpec buildFunction(ABIDefinition functionDefinition)
            throws ClassNotFoundException {
        String functionName = functionDefinition.getName();

        if (!SourceVersion.isName(functionName)) {
            functionName = "_" + functionName;
        }

        MethodSpec.Builder methodBuilder =
                MethodSpec.methodBuilder(functionName).addModifiers(Modifier.PUBLIC);

        String inputParams = this.addParameters(methodBuilder, functionDefinition.getInputs());

        List<TypeName> outputParameterTypes = buildTypeNames(functionDefinition.getOutputs());
        if (functionDefinition.isConstant()) {
            this.buildConstantFunction(
                    functionDefinition, methodBuilder, outputParameterTypes, inputParams);
        } else {
            this.buildTransactionFunction(functionDefinition, methodBuilder, inputParams);
        }

        return methodBuilder.build();
    }

    private MethodSpec buildFunctionSignedTransaction(ABIDefinition functionDefinition)
            throws ClassNotFoundException {
        String functionName = "getSignedTransactionFor";
        functionName += StringUtils.capitaliseFirstLetter(functionDefinition.getName());

        if (!SourceVersion.isName(functionName)) {
            functionName = "_" + functionName;
        }

        MethodSpec.Builder methodBuilder =
                MethodSpec.methodBuilder(functionName).addModifiers(Modifier.PUBLIC);

        String inputParams = this.addParameters(methodBuilder, functionDefinition.getInputs());

        this.buildTransactionFunctionSeq(functionDefinition, methodBuilder, inputParams);

        return methodBuilder.build();
    }

    private MethodSpec buildFunctionWithCallback(ABIDefinition functionDefinition)
            throws ClassNotFoundException {
        String functionName = functionDefinition.getName();

        MethodSpec.Builder methodBuilder =
                MethodSpec.methodBuilder(functionName).addModifiers(Modifier.PUBLIC);

        List<TypeName> outputParameterTypes = buildTypeNames(functionDefinition.getOutputs());

        if (functionDefinition.isConstant()) {
            String inputParams = this.addParameters(methodBuilder, functionDefinition.getInputs());
            this.buildConstantFunction(
                    functionDefinition, methodBuilder, outputParameterTypes, inputParams);
        } else {
            String inputParams = this.addParameters(methodBuilder, functionDefinition.getInputs());
            methodBuilder.addParameter(TransactionCallback.class, "callback");
            this.buildTransactionFunctionWithCallback(functionDefinition, methodBuilder, inputParams);
        }

        return methodBuilder.build();
    }

    public static String getInputOutputFunctionName(
            ABIDefinition functionDefinition, boolean isOverLoad) {
        if (!isOverLoad) {
            return functionDefinition.getName();
        }

        List<ABIDefinition.NamedType> nameTypes = functionDefinition.getInputs();
        String name = functionDefinition.getName();
        for (int i = 0; i < nameTypes.size(); i++) {
            ABIDefinition.Type type = nameTypes.get(i).newType();
            name += StringUtils.capitaliseFirstLetter(type.getRawType());
            if (!type.isList()) {
                continue;
            }
            // parse the array or the struct
            List<Integer> depths = type.getDimensions();
            for (int j = 0; j < depths.size(); j++) {
                name += type.getRawType();
                if (0 != depths.get(j)) {
                    name += String.valueOf(depths.get(j));
                }
            }
        }
        logger.debug(" name: {}, nameTypes: {}", name, nameTypes);
        return name;
    }

    private MethodSpec buildFunctionWithInputDecoder(
            ABIDefinition functionDefinition, boolean isOverLoad) throws ClassNotFoundException {

        String functionName = getInputOutputFunctionName(functionDefinition, isOverLoad);

        MethodSpec.Builder methodBuilder =
                MethodSpec.methodBuilder(
                                "get" + StringUtils.capitaliseFirstLetter(functionName) + "Input")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(TransactionReceipt.class, "transactionReceipt");

        List<TypeName> returnTypes =
                this.buildReturnTypes(buildTypeNames(functionDefinition.getInputs()));

        ParameterizedTypeName parameterizedTupleType =
                ParameterizedTypeName.get(
                        ClassName.get(
                                "org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated",
                                "Tuple" + returnTypes.size()),
                        returnTypes.toArray(new TypeName[returnTypes.size()]));

        methodBuilder.returns(parameterizedTupleType);
        methodBuilder.addStatement("String data = transactionReceipt.getInput().substring(10)");

        buildVariableLengthReturnFunctionConstructor(
                methodBuilder,
                functionDefinition.getName(),
                "",
                buildTypeNames(functionDefinition.getInputs()));

        methodBuilder.addStatement(
                "$T<Type> results = $T.decode(data, function.getOutputParameters())",
                List.class,
                FunctionReturnDecoder.class);

        this.buildTupleResultContainer0(
                methodBuilder,
                parameterizedTupleType,
                buildTypeNames(functionDefinition.getInputs()));

        return methodBuilder.build();
    }

    private MethodSpec buildFunctionWithOutputDecoder(
            ABIDefinition functionDefinition, boolean isOverLoad) throws ClassNotFoundException {

        String functionName = getInputOutputFunctionName(functionDefinition, isOverLoad);

        MethodSpec.Builder methodBuilder =
                MethodSpec.methodBuilder(
                                "get" + StringUtils.capitaliseFirstLetter(functionName) + "Output")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(TransactionReceipt.class, "transactionReceipt");

        List<TypeName> returnTypes =
                this.buildReturnTypes(buildTypeNames(functionDefinition.getOutputs()));

        ParameterizedTypeName parameterizedTupleType =
                ParameterizedTypeName.get(
                        ClassName.get(
                                "org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated",
                                "Tuple" + returnTypes.size()),
                        returnTypes.toArray(new TypeName[returnTypes.size()]));

        methodBuilder.returns(parameterizedTupleType);
        methodBuilder.addStatement("String data = transactionReceipt.getOutput()");

        buildVariableLengthReturnFunctionConstructor(
                methodBuilder,
                functionDefinition.getName(),
                "",
                buildTypeNames(functionDefinition.getOutputs()));

        methodBuilder.addStatement(
                "$T<Type> results = $T.decode(data, function.getOutputParameters())",
                List.class,
                FunctionReturnDecoder.class);

        this.buildTupleResultContainer0(
                methodBuilder,
                parameterizedTupleType,
                buildTypeNames(functionDefinition.getOutputs()));

        return methodBuilder.build();
    }

    private void buildConstantFunction(
            ABIDefinition functionDefinition,
            MethodSpec.Builder methodBuilder,
            List<TypeName> outputParameterTypes,
            String inputParams)
            throws ClassNotFoundException {

        String functionName = functionDefinition.getName();
        methodBuilder.addException(ContractException.class);
        if (outputParameterTypes.isEmpty()) {
            methodBuilder.addStatement(
                    "throw new RuntimeException"
                            + "(\"cannot call constant function with void return type\")");
        } else if (outputParameterTypes.size() == 1) {

            TypeName typeName = outputParameterTypes.get(0);
            TypeName nativeReturnTypeName;
            nativeReturnTypeName = this.getWrapperRawType(typeName);

            methodBuilder.returns(nativeReturnTypeName);

            methodBuilder.addStatement(
                    "final $T function = "
                            + "new $T($N, \n$T.<$T>asList($L), "
                            + "\n$T.<$T<?>>asList(new $T<$T>() {}))",
                    Function.class,
                    Function.class,
                    funcNameToConst(functionName),
                    Arrays.class,
                    Type.class,
                    inputParams,
                    Arrays.class,
                    TypeReference.class,
                    TypeReference.class,
                    typeName);

            if (nativeReturnTypeName.equals(ClassName.get(List.class))) {
                // We return list. So all the list elements should
                // also be converted to native types
                TypeName listType = ParameterizedTypeName.get(List.class, Type.class);

                CodeBlock.Builder callCode = CodeBlock.builder();
                callCode.addStatement(
                        "$T result = "
                                + "($T) executeCallWithSingleValueReturn(function, $T.class)",
                        listType,
                        listType,
                        nativeReturnTypeName);
                callCode.addStatement("return convertToNative(result)");
                methodBuilder.returns(nativeReturnTypeName).addCode(callCode.build());
            } else {
                methodBuilder.addStatement(
                        "return executeCallWithSingleValueReturn(function, $T.class)",
                        nativeReturnTypeName);
            }
        } else {
            List<TypeName> returnTypes = this.buildReturnTypes(outputParameterTypes);

            ParameterizedTypeName parameterizedTupleType =
                    ParameterizedTypeName.get(
                            ClassName.get(
                                    "org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated",
                                    "Tuple" + returnTypes.size()),
                            returnTypes.toArray(new TypeName[returnTypes.size()]));

            methodBuilder.returns(parameterizedTupleType);

            buildVariableLengthReturnFunctionConstructor(
                    methodBuilder, functionName, inputParams, outputParameterTypes);

            this.buildTupleResultContainer(methodBuilder, parameterizedTupleType, outputParameterTypes);
        }
    }

    private void buildTransactionFunction(
            ABIDefinition functionDefinition, MethodSpec.Builder methodBuilder, String inputParams)
            throws ClassNotFoundException {

        String functionName = functionDefinition.getName();

        methodBuilder.returns(TypeName.get(TransactionReceipt.class));

        methodBuilder.addStatement(
                "final $T function = new $T(\n$N, \n$T.<$T>asList($L), \n$T"
                        + ".<$T<?>>emptyList())",
                Function.class,
                Function.class,
                funcNameToConst(functionName),
                Arrays.class,
                Type.class,
                inputParams,
                Collections.class,
                TypeReference.class);
        methodBuilder.addStatement("return executeTransaction(function)");
    }

    private void buildTransactionFunctionWithCallback(
            ABIDefinition functionDefinition, MethodSpec.Builder methodBuilder, String inputParams)
            throws ClassNotFoundException {
        String functionName = functionDefinition.getName();

        methodBuilder.returns(TypeName.VOID);

        methodBuilder.addStatement(
                "final $T function = new $T(\n$N, \n$T.<$T>asList($L), \n$T"
                        + ".<$T<?>>emptyList())",
                Function.class,
                Function.class,
                funcNameToConst(functionName),
                Arrays.class,
                Type.class,
                inputParams,
                Collections.class,
                TypeReference.class);
        methodBuilder.addStatement("asyncExecuteTransaction(function, callback)");
    }

    private void buildTransactionFunctionSeq(
            ABIDefinition functionDefinition, MethodSpec.Builder methodBuilder, String inputParams)
            throws ClassNotFoundException {
        String functionName = functionDefinition.getName();

        TypeName returnType = TypeName.get(String.class);
        methodBuilder.returns(returnType);

        methodBuilder.addStatement(
                "final $T function = new $T(\n$N, \n$T.<$T>asList($L), \n$T"
                        + ".<$T<?>>emptyList())",
                Function.class,
                Function.class,
                funcNameToConst(functionName),
                Arrays.class,
                Type.class,
                inputParams,
                Collections.class,
                TypeReference.class);

        methodBuilder.addStatement("return createSignedTransaction(function)");
    }

    private TypeSpec buildEventResponseObject(
            String className,
            List<NamedTypeName> indexedParameters,
            List<NamedTypeName> nonIndexedParameters) {

        TypeSpec.Builder builder =
                TypeSpec.classBuilder(className).addModifiers(Modifier.PUBLIC, Modifier.STATIC);

        builder.addField(TransactionReceipt.Logs.class, "log", Modifier.PUBLIC);
        for (NamedTypeName namedType : indexedParameters) {
            TypeName typeName = getEventNativeType(namedType.typeName);
            builder.addField(typeName, namedType.getName(), Modifier.PUBLIC);
        }

        for (NamedTypeName namedType : nonIndexedParameters) {
            TypeName typeName = getNativeType(namedType.typeName);
            builder.addField(typeName, namedType.getName(), Modifier.PUBLIC);
        }

        return builder.build();
    }

    private MethodSpec buildSubscribeEventFunction(String eventName) throws ClassNotFoundException {

        String generatedFunctionName =
                "subscribe" + StringUtils.capitaliseFirstLetter(eventName) + "Event";

        MethodSpec.Builder getEventMethodBuilder =
                MethodSpec.methodBuilder(generatedFunctionName)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(String.class, FROM_BLOCK)
                        .addParameter(String.class, TO_BLOCK);

        this.addParameter(getEventMethodBuilder, "string[]", OTHER_TOPICS);
        // FIXME: implement event sub
        //        getEventMethodBuilder.addParameter(EventCallback.class, CALLBACK_VALUE);
        getEventMethodBuilder.addStatement(
                "String topic0 = $N.encode(" + this.buildEventDefinitionName(eventName) + ")",
                EVENT_ENCODER);

        getEventMethodBuilder.addStatement(
                "subscribeEvent(ABI,BINARY"
                        + ","
                        + "topic0"
                        + ","
                        + FROM_BLOCK
                        + ","
                        + TO_BLOCK
                        + ","
                        + OTHER_TOPICS
                        + ","
                        + CALLBACK_VALUE
                        + ")");

        return getEventMethodBuilder.build();
    }

    private MethodSpec buildDefaultSubscribeEventLog(String eventName) {

        String generatedFunctionName =
                "subscribe" + StringUtils.capitaliseFirstLetter(eventName) + "Event";
        // FIXME: event
        //
        //    MethodSpec.Builder getEventMethodBuilder =
        //        MethodSpec.methodBuilder(generatedFunctionName)
        //            .addModifiers(Modifier.PUBLIC)
        //            .addParameter(EventCallback.class, CALLBACK_VALUE);

        //    getEventMethodBuilder.addStatement(
        //        "String topic0 = $N.encode(" + buildEventDefinitionName(eventName) + ")",
        // EVENT_ENCODER);
        //
        //    getEventMethodBuilder.addStatement(
        //        "subscribeEvent(ABI,BINARY" + ",topic0" + "," + CALLBACK_VALUE + ")");
        //
        //    return getEventMethodBuilder.build();
        return null;
    }

    private MethodSpec buildEventTransactionReceiptFunction(
            String responseClassName,
            String functionName,
            List<NamedTypeName> indexedParameters,
            List<NamedTypeName> nonIndexedParameters) {

        ParameterizedTypeName parameterizedTypeName =
                ParameterizedTypeName.get(
                        ClassName.get(List.class), ClassName.get("", responseClassName));

        String generatedFunctionName =
                "get" + StringUtils.capitaliseFirstLetter(functionName) + "Events";
        MethodSpec.Builder transactionMethodBuilder =
                MethodSpec.methodBuilder(generatedFunctionName)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(TransactionReceipt.class, "transactionReceipt")
                        .returns(parameterizedTypeName);

        transactionMethodBuilder
                .addStatement(
                        "$T valueList = extractEventParametersWithLog("
                                + this.buildEventDefinitionName(functionName)
                                + ", "
                                + "transactionReceipt)",
                        ParameterizedTypeName.get(List.class, Contract.EventValuesWithLog.class))
                .addStatement(
                        "$1T responses = new $1T(valueList.size())",
                        ParameterizedTypeName.get(
                                ClassName.get(ArrayList.class),
                                ClassName.get("", responseClassName)))
                .beginControlFlow(
                        "for ($T eventValues : valueList)", Contract.EventValuesWithLog.class)
                .addStatement("$1T typedResponse = new $1T()", ClassName.get("", responseClassName))
                .addCode(
                        this.buildTypedResponse(
                                "typedResponse", indexedParameters, nonIndexedParameters, false))
                .addStatement("responses.add(typedResponse)")
                .endControlFlow();

        transactionMethodBuilder.addStatement("return responses");
        return transactionMethodBuilder.build();
    }

    private List<MethodSpec> buildEventFunctions(
            ABIDefinition functionDefinition, TypeSpec.Builder classBuilder)
            throws ClassNotFoundException {
        String functionName = functionDefinition.getName();
        List<ABIDefinition.NamedType> inputs = functionDefinition.getInputs();
        String responseClassName =
                StringUtils.capitaliseFirstLetter(functionName) + "EventResponse";

        List<NamedTypeName> parameters = new ArrayList<>();
        List<NamedTypeName> indexedParameters = new ArrayList<>();
        List<NamedTypeName> nonIndexedParameters = new ArrayList<>();

        Integer index = 0;
        Set<String> eventParamNameFilter = new HashSet<>();
        for (ABIDefinition.NamedType namedType : inputs) {
            if (namedType.getName() != null && !namedType.getName().equals("")) {
                eventParamNameFilter.add(namedType.getName());
            }
        }
        for (ABIDefinition.NamedType namedType : inputs) {
            if (namedType.getName() == null || namedType.getName().equals("")) {
                String paramName = functionName + "Param" + index;
                while (eventParamNameFilter.contains(paramName)) {
                    index++;
                    paramName = functionName + "Param" + index;
                }
                eventParamNameFilter.add(paramName);
                namedType.setName(paramName);
            }
            NamedTypeName parameter =
                    new NamedTypeName(
                            namedType.getName(),
                            buildTypeName(namedType.getType()),
                            namedType.isIndexed());
            if (namedType.isIndexed()) {
                indexedParameters.add(parameter);
            } else {
                nonIndexedParameters.add(parameter);
            }
            parameters.add(parameter);
        }

        classBuilder.addField(this.createEventDefinition(functionName, parameters));

        classBuilder.addType(
                this.buildEventResponseObject(
                        responseClassName, indexedParameters, nonIndexedParameters));

        List<MethodSpec> methods = new ArrayList<>();
        methods.add(
                this.buildEventTransactionReceiptFunction(
                        responseClassName, functionName, indexedParameters, nonIndexedParameters));

        //    methods.add(buildSubscribeEventFunction(functionName));
        //    methods.add(buildDefaultSubscribeEventLog(functionName));

        return methods;
    }

    private CodeBlock buildTypedResponse(
            String objectName,
            List<NamedTypeName> indexedParameters,
            List<NamedTypeName> nonIndexedParameters,
            boolean flowable) {
        String nativeConversion;

        nativeConversion = ".getValue()";

        CodeBlock.Builder builder = CodeBlock.builder();
        if (flowable) {
            builder.addStatement("$L.log = log", objectName);
        } else {
            builder.addStatement("$L.log = eventValues.getLog()", objectName);
        }
        for (int i = 0; i < indexedParameters.size(); i++) {
            builder.addStatement(
                    "$L.$L = ($T) eventValues.getIndexedValues().get($L)" + nativeConversion,
                    objectName,
                    indexedParameters.get(i).getName(),
                    getEventNativeType(indexedParameters.get(i).getTypeName()),
                    i);
        }

        for (int i = 0; i < nonIndexedParameters.size(); i++) {
            builder.addStatement(
                    "$L.$L = ($T) eventValues.getNonIndexedValues().get($L)" + nativeConversion,
                    objectName,
                    nonIndexedParameters.get(i).getName(),
                    getNativeType(nonIndexedParameters.get(i).getTypeName()),
                    i);
        }
        return builder.build();
    }

    protected static TypeName buildTypeName(String typeDeclaration) {
        String type = trimStorageDeclaration(typeDeclaration);
        Matcher matcher = pattern.matcher(type);
        if (matcher.find()) {
            Class<?> baseType = AbiTypes.getType(matcher.group(1));
            String firstArrayDimension = matcher.group(2);
            String secondArrayDimension = matcher.group(3);

            TypeName typeName;

            if ("".equals(firstArrayDimension)) {
                typeName = ParameterizedTypeName.get(DynamicArray.class, baseType);
            } else {
                Class<?> rawType = getStaticArrayTypeReferenceClass(firstArrayDimension);
                typeName = ParameterizedTypeName.get(rawType, baseType);
            }

            if (secondArrayDimension != null) {
                if ("".equals(secondArrayDimension)) {
                    return ParameterizedTypeName.get(ClassName.get(DynamicArray.class), typeName);
                } else {
                    Class<?> rawType = getStaticArrayTypeReferenceClass(secondArrayDimension);
                    return ParameterizedTypeName.get(ClassName.get(rawType), typeName);
                }
            }
            return typeName;
        } else {
            Class<?> cls = AbiTypes.getType(type);
            return ClassName.get(cls);
        }
    }

    private static Class<?> getStaticArrayTypeReferenceClass(String type) {
        try {
            return Class.forName("org.fisco.bcos.sdk.abi.datatypes.generated.StaticArray" + type);
        } catch (ClassNotFoundException e) {
            // Unfortunately we can't encode it's length as a type if it's > 32.
            return StaticArray.class;
        }
    }

    private static String trimStorageDeclaration(String type) {
        if (type.endsWith(" storage") || type.endsWith(" memory")) {
            return type.split(" ")[0];
        } else {
            return type;
        }
    }

    private List<TypeName> buildReturnTypes(List<TypeName> outputParameterTypes) {
        List<TypeName> result = new ArrayList<>(outputParameterTypes.size());
        for (TypeName typeName : outputParameterTypes) {
            result.add(getNativeType(typeName));
        }
        return result;
    }

    private static void buildVariableLengthReturnFunctionConstructor(
            MethodSpec.Builder methodBuilder,
            String functionName,
            String inputParameters,
            List<TypeName> outputParameterTypes) {

        List<Object> objects = new ArrayList<>();
        objects.add(Function.class);
        objects.add(Function.class);
        objects.add(funcNameToConst(functionName));

        objects.add(Arrays.class);
        objects.add(Type.class);
        objects.add(inputParameters);

        objects.add(Arrays.class);
        objects.add(TypeReference.class);
        for (TypeName outputParameterType : outputParameterTypes) {
            objects.add(TypeReference.class);
            objects.add(outputParameterType);
        }

        String asListParams =
                Collection.join(outputParameterTypes, ", ", typeName -> "new $T<$T>() {}");

        methodBuilder.addStatement(
                "final $T function = new $T($N, \n$T.<$T>asList($L), \n$T"
                        + ".<$T<?>>asList("
                        + asListParams
                        + "))",
                objects.toArray());
    }

    private void buildTupleResultContainer(
            MethodSpec.Builder methodBuilder,
            ParameterizedTypeName tupleType,
            List<TypeName> outputParameterTypes) {

        List<TypeName> typeArguments = tupleType.typeArguments;

        CodeBlock.Builder tupleConstructor = CodeBlock.builder();
        tupleConstructor
                .addStatement(
                        "$T results = executeCallWithMultipleValueReturn(function)",
                        ParameterizedTypeName.get(List.class, Type.class))
                .add("return new $T(", tupleType)
                .add("$>$>");

        String resultStringSimple = "\n($T) results.get($L)";
        resultStringSimple += ".getValue()";

        String resultStringNativeList = "\nconvertToNative(($T) results.get($L).getValue())";

        int size = typeArguments.size();
        ClassName classList = ClassName.get(List.class);

        for (int i = 0; i < size; i++) {
            TypeName param = outputParameterTypes.get(i);
            TypeName convertTo = typeArguments.get(i);

            String resultString = resultStringSimple;

            // If we use native java types we need to convert
            // elements of arrays to native java types too
            if (param instanceof ParameterizedTypeName) {
                ParameterizedTypeName oldContainer = (ParameterizedTypeName) param;
                ParameterizedTypeName newContainer = (ParameterizedTypeName) convertTo;
                if (newContainer.rawType.compareTo(classList) == 0
                        && newContainer.typeArguments.size() == 1) {
                    convertTo =
                            ParameterizedTypeName.get(classList, oldContainer.typeArguments.get(0));
                    resultString = resultStringNativeList;
                }
            }

            tupleConstructor.add(resultString, convertTo, i);
            tupleConstructor.add(i < size - 1 ? ", " : ");\n");
        }
        tupleConstructor.add("$<$<");
        methodBuilder.returns(tupleType).addCode(tupleConstructor.build());
    }

    private void buildTupleResultContainer0(
            MethodSpec.Builder methodBuilder,
            ParameterizedTypeName tupleType,
            List<TypeName> outputParameterTypes) {

        List<TypeName> typeArguments = tupleType.typeArguments;

        CodeBlock.Builder codeBuilder = CodeBlock.builder();

        String resultStringSimple = "\n($T) results.get($L)";
        resultStringSimple += ".getValue()";

        String resultStringNativeList = "\nconvertToNative(($T) results.get($L).getValue())";

        int size = typeArguments.size();
        ClassName classList = ClassName.get(List.class);

        for (int i = 0; i < size; i++) {
            TypeName param = outputParameterTypes.get(i);
            TypeName convertTo = typeArguments.get(i);

            String resultString = resultStringSimple;

            // If we use native java types we need to convert
            // elements of arrays to native java types too
            if (param instanceof ParameterizedTypeName) {
                ParameterizedTypeName oldContainer = (ParameterizedTypeName) param;
                ParameterizedTypeName newContainer = (ParameterizedTypeName) convertTo;
                if (newContainer.rawType.compareTo(classList) == 0
                        && newContainer.typeArguments.size() == 1) {
                    convertTo =
                            ParameterizedTypeName.get(classList, oldContainer.typeArguments.get(0));
                    resultString = resultStringNativeList;
                }
            }

            codeBuilder.add(resultString, convertTo, i);
            codeBuilder.add(i < size - 1 ? ", " : "\n");
        }

        methodBuilder.addStatement("return new $T(\n$L)", tupleType, codeBuilder.build());
    }

    private static CodeBlock buildVariableLengthEventInitializer(
            String eventName, List<NamedTypeName> parameterTypes) {

        List<Object> objects = new ArrayList<>();
        objects.add(Event.class);
        objects.add(eventName);

        objects.add(Arrays.class);
        objects.add(TypeReference.class);
        for (NamedTypeName parameterType : parameterTypes) {
            objects.add(TypeReference.class);
            objects.add(parameterType.getTypeName());
        }

        String asListParams =
                parameterTypes.stream()
                        .map(
                                type -> {
                                    if (type.isIndexed()) {
                                        return "new $T<$T>(true) {}";
                                    } else {
                                        return "new $T<$T>() {}";
                                    }
                                })
                        .collect(Collectors.joining(", "));

        return CodeBlock.builder()
                .addStatement(
                        "new $T($S, \n" + "$T.<$T<?>>asList(" + asListParams + "))",
                        objects.toArray())
                .build();
    }

    private static String funcNameToConst(String funcName) {
        return FUNC_NAME_PREFIX + funcName.toUpperCase();
    }

    private static class NamedTypeName {
        private final TypeName typeName;
        private final String name;
        private final boolean indexed;

        NamedTypeName(String name, TypeName typeName, boolean indexed) {
            this.name = name;
            this.typeName = typeName;
            this.indexed = indexed;
        }

        public String getName() {
            return this.name;
        }

        public TypeName getTypeName() {
            return this.typeName;
        }

        public boolean isIndexed() {
            return this.indexed;
        }
    }

    private static String getBinaryFuncDefinition() {
        return GET_BINARY_FUNC + "(client.getCryptoSuite())";
    }
}
