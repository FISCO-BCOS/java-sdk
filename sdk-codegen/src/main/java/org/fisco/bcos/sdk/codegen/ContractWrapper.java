package org.fisco.bcos.sdk.codegen;

import com.squareup.javapoet.*;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.codec.datatypes.*;
import org.fisco.bcos.sdk.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.codec.wrapper.ABIDefinition;
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

/** Generate Java Classes based on generated Solidity bin and abi files. */
public class ContractWrapper {

    private static final Logger logger = LoggerFactory.getLogger(ContractWrapper.class);

    private static final int MAX_BIN_SIZE = 0x40000;
    private static final int MAX_FIELD = 8 * 1024;

    private static final String BINARY_ARRAY_NAME = "BINARY_ARRAY";
    private static final String SM_BINARY_ARRAY_NAME = "SM_BINARY_ARRAY";
    private static final String BINARY_NAME = "BINARY";
    private static final String SM_BINARY_NAME = "SM_BINARY";
    private static final String ABI_ARRAY_NAME = "ABI_ARRAY";
    private static final String ABI_NAME = "ABI";

    private static final String GET_BINARY_FUNC = "getBinary";
    private static final String GET_ABI_FUNC = "getABI";
    private static final String CLIENT = "client";
    private static final String PATH = "contractPath";
    private static final String CREDENTIAL = "credential";
    private static final String CRYPTO_SUITE = "cryptoSuite";
    private static final String CONTRACT_ADDRESS = "contractAddress";
    private static final String FROM_BLOCK = "fromBlock";
    private static final String TO_BLOCK = "toBlock";
    private static final String CALLBACK_VALUE = "callback";
    private static final String OTHER_TOPICS = "otherTopics";
    private static final String FUNC_NAME_PREFIX = "FUNC_";
    private static final String EVENT_ENCODER = "eventEncoder";

    private static final String TUPLE_REGEX = "tuple\\.Tuple(\\d+)";
    private static final Pattern TUPLE_PATTERN = Pattern.compile(TUPLE_REGEX);

    private static final String TUPLE_PACKAGE_NAME =
            "org.fisco.bcos.sdk.codec.datatypes.generated.tuples.generated";

    private final boolean isWasm;
    private static final HashMap<Integer, TypeName> structClassNameMap = new HashMap<>();
    private static final List<ABIDefinition.NamedType> structsNamedTypeList = new ArrayList<>();

    public ContractWrapper(boolean isWasm) {
        this.isWasm = isWasm;
    }

    public void generateJavaFiles(
            String contractName,
            String bin,
            String smBin,
            String abi,
            String destinationDir,
            String basePackageName)
            throws IOException, ClassNotFoundException, UnsupportedOperationException {
        String[] nameParts = contractName.split("_");
        for (int i = 0; i < nameParts.length; ++i) {
            nameParts[i] = StringUtils.capitaliseFirstLetter(nameParts[i]);
        }
        String className = String.join("", nameParts);

        logger.info("bin: {}", bin);
        logger.info("smBin: {}", smBin);

        if (bin.length() > MAX_BIN_SIZE) {
            throw new UnsupportedOperationException(
                    String.format(
                            "Contract binary too long, max support is %d bytes, now is %d",
                            MAX_BIN_SIZE, bin.length()));
        }

        List<ABIDefinition> abiDefinitions = CodeGenUtils.loadContractAbiDefinition(abi);
        TypeSpec.Builder classBuilder = this.createClassBuilder(className, bin, smBin, abi);

        classBuilder.addMethod(buildGetBinaryMethod());
        classBuilder.addMethod(buildGetABIMethod());
        classBuilder.addMethod(buildConstructor());

        classBuilder.addFields(this.buildFuncNameConstants(abiDefinitions));
        classBuilder.addTypes(this.buildStructTypes(abiDefinitions));
        structsNamedTypeList.addAll(
                abiDefinitions.stream()
                        .flatMap(
                                definition -> {
                                    List<ABIDefinition.NamedType> parameters = new ArrayList<>();
                                    parameters.addAll(definition.getInputs());
                                    parameters.addAll(definition.getOutputs());
                                    return parameters.stream()
                                            .filter(
                                                    namedType ->
                                                            namedType.getType().equals("tuple"));
                                })
                        .collect(Collectors.toList()));
        classBuilder.addMethods(this.buildFunctionDefinitions(classBuilder, abiDefinitions));
        classBuilder.addMethod(buildLoad(className));
        classBuilder.addMethods(this.buildDeployMethods(isWasm, className, abiDefinitions));

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
        return TypeSpec.classBuilder(className)
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
    }

    public List<String> stringToArrayString(String binary) {

        List<String> binaryArray = new ArrayList<>();

        for (int offset = 0; offset < binary.length(); ) {

            int length = binary.length() - offset;
            if (length > MAX_FIELD) {
                length = MAX_FIELD;
            }

            String item = binary.substring(offset, offset + length);

            binaryArray.add(item);
            offset += item.length();
        }

        return binaryArray;
    }

    private FieldSpec createArrayDefinition(String type, String binary) {
        List<String> binaryArray = this.stringToArrayString(binary);
        List<String> formatArray = new ArrayList<>(Collections.nCopies(binaryArray.size(), "$S"));

        return FieldSpec.builder(String[].class, type)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                .initializer(
                        "{" + org.fisco.bcos.sdk.utils.StringUtils.joinAll(",", formatArray) + "}",
                        binaryArray.toArray())
                .build();
    }

    private FieldSpec createDefinition(String type, String binaryArrayName) {
        return FieldSpec.builder(String.class, type)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                .initializer(
                        "org.fisco.bcos.sdk.utils.StringUtils.joinAll(\"\", "
                                + binaryArrayName
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

    private String buildStructConstructorParameterDefinition(
            final List<ABIDefinition.NamedType> components, final boolean useNativeJavaTypes)
            throws ClassNotFoundException {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < components.size(); i++) {
            final ABIDefinition.NamedType component = components.get(i);
            stringBuilder.append(i > 0 ? "," : "");
            if (useNativeJavaTypes) {
                stringBuilder.append(
                        !component.getType().startsWith("tuple")
                                ? "new "
                                        + buildTypeName(component.getType())
                                        + "("
                                        + component.getName()
                                        + ")"
                                : component.getName());
            } else {
                stringBuilder.append(component.getName());
            }
        }
        return stringBuilder.toString();
    }

    private List<TypeSpec> buildStructTypes(List<ABIDefinition> functionDefinitions)
            throws ClassNotFoundException {
        final List<ABIDefinition.NamedType> orderedKeys = extractStructs(functionDefinitions);
        int structCounter = 0;
        List<TypeSpec> structs = new ArrayList<>();
        for (final ABIDefinition.NamedType namedType : orderedKeys) {
            if (!isStructType(namedType)) {
                List<TypeName> elementTypes = new ArrayList<>();
                for (ABIDefinition.NamedType component : namedType.getComponents()) {
                    final TypeName typeName;
                    if (component.getType().equals("tuple")) {
                        typeName = structClassNameMap.get(component.structIdentifier());
                    } else if (component.getType().startsWith("tuple")
                            && component.getType().contains("[")) {
                        typeName = buildStructArrayTypeName(component);
                    } else {
                        typeName = getNativeType(buildTypeName(component.getType()));
                    }
                    elementTypes.add(typeName);

                    ParameterizedTypeName parameterizedTupleType =
                            ParameterizedTypeName.get(
                                    ClassName.get(
                                            TUPLE_PACKAGE_NAME, "Tuple" + elementTypes.size()),
                                    elementTypes.toArray(new TypeName[0]));

                    structClassNameMap.put(namedType.structIdentifier(), parameterizedTupleType);
                }
                continue;
            }

            final String internalType = namedType.getInternalType();
            final String structName;
            if (internalType == null || internalType.isEmpty()) {
                structName = "Struct" + structCounter;
            } else {
                structName = internalType.substring(internalType.lastIndexOf(".") + 1);
            }

            final TypeSpec.Builder builder =
                    TypeSpec.classBuilder(structName)
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

            final MethodSpec.Builder constructorBuilder =
                    MethodSpec.constructorBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement(
                                    "super("
                                            + buildStructConstructorParameterDefinition(
                                                    namedType.getComponents(), false)
                                            + ")");
            final MethodSpec.Builder nativeConstructorBuilder =
                    MethodSpec.constructorBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement(
                                    "super("
                                            + buildStructConstructorParameterDefinition(
                                                    namedType.getComponents(), true)
                                            + ")");

            for (ABIDefinition.NamedType component : namedType.getComponents()) {
                if (component.getType().equals("tuple")) {
                    final TypeName typeName = structClassNameMap.get(component.structIdentifier());
                    builder.addField(typeName, component.getName(), Modifier.PUBLIC);
                    constructorBuilder.addParameter(typeName, component.getName());
                    nativeConstructorBuilder.addParameter(typeName, component.getName());
                } else if (component.getType().startsWith("tuple")
                        && component.getType().contains("[")) {
                    final TypeName typeName = buildStructArrayTypeName(component);
                    builder.addField(typeName, component.getName(), Modifier.PUBLIC);
                    constructorBuilder.addParameter(typeName, component.getName());
                    nativeConstructorBuilder.addParameter(typeName, component.getName());
                } else {
                    final TypeName typeName = buildTypeName(component.getType());
                    final TypeName nativeTypeName = getNativeType(typeName);
                    builder.addField(nativeTypeName, component.getName(), Modifier.PUBLIC);
                    constructorBuilder.addParameter(typeName, component.getName());
                    nativeConstructorBuilder.addParameter(nativeTypeName, component.getName());
                }
                nativeConstructorBuilder.addStatement(
                        "this." + component.getName() + " = " + component.getName());
                constructorBuilder.addStatement(
                        "this."
                                + component.getName()
                                + " = "
                                + component.getName()
                                + (structClassNameMap.keySet().stream()
                                                .noneMatch(i -> i == component.structIdentifier())
                                        ? ".getValue()"
                                        : ""));
            }

            builder.superclass(namedType.isDynamic() ? DynamicStruct.class : StaticStruct.class);
            builder.addMethod(constructorBuilder.build());
            if (!namedType.getComponents().isEmpty()
                    && namedType.getComponents().stream()
                            .anyMatch(
                                    component ->
                                            structClassNameMap.keySet().stream()
                                                    .noneMatch(
                                                            i ->
                                                                    i
                                                                            == component
                                                                                    .structIdentifier()))) {
                builder.addMethod(nativeConstructorBuilder.build());
            }
            structClassNameMap.put(namedType.structIdentifier(), ClassName.get("", structName));
            structs.add(builder.build());
            structCounter++;
        }
        return structs;
    }

    private boolean isStructType(ABIDefinition.NamedType namedType) {
        if (isWasm) {
            String internalType = namedType.getInternalType();
            Matcher matcher = TUPLE_PATTERN.matcher(internalType);
            if (matcher.find()) {
                return false;
            }
        }
        return namedType.getType().equals("tuple");
    }

    private List<ABIDefinition.NamedType> extractStructs(
            final List<ABIDefinition> functionDefinitions) {
        final HashMap<Integer, ABIDefinition.NamedType> structMap = new LinkedHashMap<>();
        functionDefinitions.stream()
                .flatMap(
                        definition -> {
                            List<ABIDefinition.NamedType> parameters =
                                    new ArrayList<>(definition.getInputs());
                            List<ABIDefinition.NamedType> outputs = definition.getOutputs();
                            if (outputs != null) {
                                parameters.addAll(definition.getOutputs());
                            }
                            return parameters.stream()
                                    .map(this::normalizeNamedType)
                                    .filter(namedType -> namedType.getType().equals("tuple"));
                        })
                .forEach(
                        namedType -> {
                            int structIdentifier = namedType.structIdentifier();
                            if (!structMap.containsKey(structIdentifier)) {
                                structMap.put(structIdentifier, namedType);
                            }
                            extractNested(namedType).stream()
                                    .filter(this::isStructType)
                                    .forEach(
                                            nestedNamedType ->
                                                    structMap.put(
                                                            nestedNamedType.structIdentifier(),
                                                            nestedNamedType));
                        });

        return structMap.values().stream()
                .sorted(Comparator.comparingInt(ABIDefinition.NamedType::nestedness))
                .collect(Collectors.toList());
    }

    private java.util.Collection<? extends ABIDefinition.NamedType> extractNested(
            final ABIDefinition.NamedType namedType) {
        if (namedType.getComponents().size() == 0) {
            return new ArrayList<>();
        } else {
            List<ABIDefinition.NamedType> nestedStructs = new ArrayList<>();
            namedType
                    .getComponents()
                    .forEach(
                            nestedNamedStruct -> {
                                nestedStructs.add(nestedNamedStruct);
                                nestedStructs.addAll(extractNested(nestedNamedStruct));
                            });
            return nestedStructs;
        }
    }

    private ABIDefinition.NamedType normalizeNamedType(ABIDefinition.NamedType namedType) {
        if (namedType.getType().endsWith("[]") && namedType.getInternalType().endsWith("[]")) {
            return new ABIDefinition.NamedType(
                    namedType.getName(),
                    namedType.getType().substring(0, namedType.getType().length() - 2),
                    namedType
                            .getInternalType()
                            .substring(0, namedType.getInternalType().length() - 2),
                    namedType.isIndexed(),
                    namedType.getComponents());
        } else {
            return namedType;
        }
    }

    private List<MethodSpec> buildDeployMethods(
            boolean isWasm, String className, List<ABIDefinition> functionDefinitions)
            throws ClassNotFoundException {
        boolean constructor = false;
        List<MethodSpec> methodSpecs = new ArrayList<>();
        for (ABIDefinition functionDefinition : functionDefinitions) {
            if (functionDefinition.getType().equals("constructor")) {
                constructor = true;
                methodSpecs.add(this.buildDeploy(isWasm, className, functionDefinition));
            }
        }
        // constructor will not be specified in ABI file if its empty
        if (!constructor) {
            MethodSpec.Builder credentialsMethodBuilder = getDeployMethodSpec(isWasm, className);
            methodSpecs.add(buildDeployNoParams(isWasm, credentialsMethodBuilder, className));
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

    private static MethodSpec buildGetBinaryMethod() {
        MethodSpec.Builder toReturn =
                MethodSpec.methodBuilder(GET_BINARY_FUNC)
                        .addParameter(CryptoSuite.class, ContractWrapper.CRYPTO_SUITE)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(String.class);

        toReturn.addStatement(
                "return ($N.getCryptoTypeConfig() == $T.ECDSA_TYPE ? $N : $N)",
                ContractWrapper.CRYPTO_SUITE,
                CryptoType.class,
                BINARY_NAME,
                SM_BINARY_NAME);
        return toReturn.build();
    }

    private static MethodSpec buildGetABIMethod() {
        MethodSpec.Builder toReturn =
                MethodSpec.methodBuilder(GET_ABI_FUNC)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(String.class);

        toReturn.addStatement("return $N", ABI_NAME);
        return toReturn.build();
    }

    private static MethodSpec buildConstructor() {
        MethodSpec.Builder toReturn =
                MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PROTECTED)
                        .addParameter(String.class, CONTRACT_ADDRESS)
                        .addParameter(Client.class, CLIENT)
                        .addParameter(CryptoKeyPair.class, ContractWrapper.CREDENTIAL)
                        .addStatement(
                                "super($N, $N, $N, $N)",
                                getBinaryFuncDefinition(),
                                CONTRACT_ADDRESS,
                                CLIENT,
                                ContractWrapper.CREDENTIAL);
        return toReturn.build();
    }

    private MethodSpec buildDeploy(
            boolean isWasm, String className, ABIDefinition functionDefinition)
            throws ClassNotFoundException {
        MethodSpec.Builder methodBuilder = getDeployMethodSpec(isWasm, className);
        String inputParams = this.addParameters(methodBuilder, functionDefinition.getInputs());

        if (!inputParams.isEmpty()) {
            return buildDeployWithParams(isWasm, methodBuilder, className, inputParams);
        } else {
            return buildDeployNoParams(isWasm, methodBuilder, className);
        }
    }

    private static MethodSpec buildDeployWithParams(
            boolean isWasm,
            MethodSpec.Builder methodBuilder,
            String className,
            String inputParams) {
        methodBuilder
                .addStatement(
                        "byte[] encodedConstructor = $T.encodeConstructor("
                                + "$T.<$T>asList($L)"
                                + ")",
                        isWasm
                                ? org.fisco.bcos.sdk.codec.scale.FunctionEncoder.class
                                : org.fisco.bcos.sdk.codec.abi.FunctionEncoder.class,
                        Arrays.class,
                        Type.class,
                        inputParams)
                .addStatement(
                        "return deploy(" + "$L.class, $L, $L, $L, $L, encodedConstructor, $L)",
                        className,
                        CLIENT,
                        ContractWrapper.CREDENTIAL,
                        getBinaryFuncDefinition(),
                        isWasm ? getABIFuncDefinition() : "null",
                        isWasm ? PATH : "null");
        return methodBuilder.build();
    }

    private static MethodSpec buildDeployNoParams(
            boolean isWasm, MethodSpec.Builder methodBuilder, String className) {
        methodBuilder.addStatement(
                "return deploy($L.class, $L, $L, $L, $L, null, $L)",
                className,
                CLIENT,
                ContractWrapper.CREDENTIAL,
                getBinaryFuncDefinition(),
                isWasm ? getABIFuncDefinition() : "null",
                isWasm ? PATH : "null");
        return methodBuilder.build();
    }

    private static MethodSpec.Builder getDeployMethodSpec(boolean isWasm, String className) {
        MethodSpec.Builder methodSpec =
                MethodSpec.methodBuilder("deploy")
                        .addException(ContractException.class)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(TypeVariableName.get(className, Type.class))
                        .addParameter(Client.class, CLIENT)
                        .addParameter(CryptoKeyPair.class, ContractWrapper.CREDENTIAL);
        if (isWasm) {
            methodSpec.addParameter(String.class, PATH);
        }
        return methodSpec;
    }

    private static MethodSpec buildLoad(String className) {
        MethodSpec.Builder toReturn =
                MethodSpec.methodBuilder("load")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(TypeVariableName.get(className, Type.class))
                        .addParameter(String.class, CONTRACT_ADDRESS)
                        .addParameter(Client.class, CLIENT)
                        .addParameter(CryptoKeyPair.class, ContractWrapper.CREDENTIAL)
                        .addStatement(
                                "return new $L($L, $L, $L)",
                                className,
                                CONTRACT_ADDRESS,
                                CLIENT,
                                ContractWrapper.CREDENTIAL);
        return toReturn.build();
    }

    private MethodSpec.Builder addParameter(
            MethodSpec.Builder methodBuilder, String type, String name)
            throws ClassNotFoundException {

        ParameterSpec parameterSpec = this.buildParameterType(type, name);

        TypeName typeName = getNativeType(parameterSpec.type);

        ParameterSpec inputParameter = ParameterSpec.builder(typeName, parameterSpec.name).build();

        methodBuilder.addParameter(inputParameter);

        return methodBuilder;
    }

    private String addParameters(
            MethodSpec.Builder methodBuilder, List<ABIDefinition.NamedType> namedTypes)
            throws ClassNotFoundException {
        List<ParameterSpec> inputParameterTypes = buildParameterTypes(namedTypes);
        List<ParameterSpec> nativeInputParameterTypes = new ArrayList<>(inputParameterTypes.size());
        for (int i = 0; i < inputParameterTypes.size(); ++i) {
            final TypeName typeName;
            if (namedTypes.get(i).getType().equals("tuple")) {
                typeName = structClassNameMap.get(namedTypes.get(i).structIdentifier());
            } else if (namedTypes.get(i).getType().startsWith("tuple")
                    && namedTypes.get(i).getType().contains("[")) {
                typeName = buildStructArrayTypeName(namedTypes.get(i));
            } else {
                typeName = getNativeType(inputParameterTypes.get(i).type);
            }
            nativeInputParameterTypes.add(
                    ParameterSpec.builder(typeName, inputParameterTypes.get(i).name).build());
        }
        methodBuilder.addParameters(nativeInputParameterTypes);
        return Collection.join(inputParameterTypes, ", \n", this::createMappedParameterTypes);
    }

    /**
     * Verifies if the two structs are the same. Equal structs means: - They have the same field
     * names - They have the same field types The order of declaring the fields does not matter.
     *
     * @return True if they are the same fields
     */
    private static boolean isSameStruct(
            ABIDefinition.NamedType base, ABIDefinition.NamedType target) {
        for (ABIDefinition.NamedType baseField : base.getComponents()) {
            if (target.getComponents().stream()
                    .noneMatch(
                            targetField ->
                                    baseField.getType().equals(targetField.getType())
                                            && baseField.getName().equals(targetField.getName())))
                return false;
        }
        return true;
    }

    private static TypeName buildStructArrayTypeName(ABIDefinition.NamedType namedType) {
        String structName;
        if (namedType.getInternalType().isEmpty()) {
            structName =
                    structClassNameMap
                            .get(
                                    structsNamedTypeList.stream()
                                            .filter(struct -> isSameStruct(namedType, struct))
                                            .collect(Collectors.toList())
                                            .get(0)
                                            .structIdentifier())
                            .toString();
        } else {
            structName =
                    namedType
                            .getInternalType()
                            .substring(
                                    namedType.getInternalType().lastIndexOf(".") + 1,
                                    namedType.getInternalType().indexOf("["));
        }

        return ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get("", structName));
    }

    private String createMappedParameterTypes(ParameterSpec parameterSpec) {
        if (parameterSpec.type instanceof ParameterizedTypeName) {
            ClassName rawType = ((ParameterizedTypeName) parameterSpec.type).rawType;
            String packageName = rawType.packageName();
            if (packageName.equals(TUPLE_PACKAGE_NAME)) {
                return parameterSpec.name;
            }

            List<TypeName> typeNames = ((ParameterizedTypeName) parameterSpec.type).typeArguments;
            if (typeNames.size() != 1) {
                throw new UnsupportedOperationException(
                        "Only a single parameterized type is supported");
            } else {
                TypeName typeArgument =
                        (((ParameterizedTypeName) parameterSpec.type).typeArguments.get(0));

                if (structClassNameMap.values().stream()
                        .anyMatch(name -> name.equals(typeArgument))) {
                    TypeName structName =
                            structClassNameMap.values().stream()
                                    .filter(name -> name.equals(typeArgument))
                                    .collect(Collectors.toList())
                                    .get(0);
                    return "new "
                            + parameterSpec.type
                            + "("
                            + structName
                            + ".class, "
                            + parameterSpec.name
                            + ")";
                } else {
                    String parameterSpecType = parameterSpec.type.toString();
                    TypeName typeName = typeNames.get(0);
                    String typeMapInput = typeName + ".class";
                    String componentType = typeName.toString();

                    if (typeName instanceof ParameterizedTypeName) {
                        List<TypeName> typeArguments =
                                ((ParameterizedTypeName) typeName).typeArguments;
                        if (typeArguments.size() != 1) {
                            throw new UnsupportedOperationException(
                                    "Only a single parameterized type is supported");
                        }

                        TypeName innerTypeName = typeArguments.get(0);
                        componentType =
                                ((ParameterizedTypeName) parameterSpec.type).rawType.toString();
                        parameterSpecType =
                                ((ParameterizedTypeName) parameterSpec.type).rawType
                                        + "<"
                                        + componentType
                                        + ">";
                        typeMapInput = componentType + ".class,\n" + innerTypeName + ".class";
                    }

                    return "new "
                            + parameterSpecType
                            + "(\n"
                            + "        "
                            + componentType
                            + ".class,\n"
                            + "        org.fisco.bcos.sdk.codec.Utils.typeMap("
                            + parameterSpec.name
                            + ", "
                            + typeMapInput
                            + "))";
                }
            }
        } else if (structClassNameMap.values().stream()
                .noneMatch(name -> name.equals(parameterSpec.type))) {
            String constructor = "new " + parameterSpec.type + "(";
            return constructor + parameterSpec.name + ")";
        } else {
            return parameterSpec.name;
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
                ClassName.get(List.class), nativeTypeNames.toArray(new TypeName[0]));
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

    private ParameterSpec buildParameterType(String type, String name)
            throws ClassNotFoundException {
        return ParameterSpec.builder(buildTypeName(type), name).build();
    }

    protected List<ParameterSpec> buildParameterTypes(List<ABIDefinition.NamedType> namedTypes)
            throws ClassNotFoundException {

        List<ParameterSpec> result = new ArrayList<>(namedTypes.size());
        for (int i = 0; i < namedTypes.size(); i++) {
            ABIDefinition.NamedType namedType = namedTypes.get(i);
            String name = createValidParamName(namedType.getName(), i);
            String type = namedTypes.get(i).getType();
            namedType.setName(name);

            if (type.equals("tuple")) {
                result.add(
                        ParameterSpec.builder(
                                        structClassNameMap.get(namedType.structIdentifier()), name)
                                .build());
            } else if (type.startsWith("tuple") && type.contains("[")) {
                result.add(
                        ParameterSpec.builder(buildStructArrayTypeName(namedType), name).build());
            } else {
                result.add(ParameterSpec.builder(buildTypeName(type), name).build());
            }
        }
        return result;
    }

    /**
     * Public Solidity arrays and maps require an unnamed input parameter - multiple if they require
     * a struct type.
     *
     * @param name parameter name
     * @param idx parameter index
     * @return non-empty parameter name
     */
    protected static String createValidParamName(String name, int idx) {
        if (name.equals("")) {
            return "param" + idx;
        } else {
            return name;
        }
    }

    protected static List<TypeName> buildTypeNames(List<ABIDefinition.NamedType> namedTypes)
            throws ClassNotFoundException {
        List<TypeName> result = new ArrayList<>(namedTypes.size());
        for (ABIDefinition.NamedType namedType : namedTypes) {
            if (namedType.getType().equals("tuple")) {
                result.add(structClassNameMap.get(namedType.structIdentifier()));
            } else if (namedType.getType().startsWith("tuple")
                    && namedType.getType().contains("[")) {
                result.add(buildStructArrayTypeName(namedType));
            } else {
                result.add(buildTypeName(namedType.getType()));
            }
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
            this.buildTransactionFunctionWithCallback(
                    functionDefinition, methodBuilder, inputParams);
        }

        return methodBuilder.build();
    }

    public static String getInputOutputFunctionName(
            ABIDefinition functionDefinition, boolean isOverLoad) {
        if (!isOverLoad) {
            return functionDefinition.getName();
        }

        List<ABIDefinition.NamedType> nameTypes = functionDefinition.getInputs();
        StringBuilder name = new StringBuilder(functionDefinition.getName());
        for (ABIDefinition.NamedType nameType : nameTypes) {
            ABIDefinition.Type type = nameType.newType();
            name.append(StringUtils.capitaliseFirstLetter(type.getRawType()));
            if (!type.isList()) {
                continue;
            }
            // parse the array or the struct
            List<Integer> depths = type.getDimensions();
            for (Integer depth : depths) {
                name.append(type.getRawType());
                if (0 != depth) {
                    name.append(depth);
                }
            }
        }
        logger.debug(" name: {}, nameTypes: {}", name, nameTypes);
        return name.toString();
    }

    private MethodSpec buildFunctionWithInputDecoder(
            ABIDefinition functionDefinition, boolean isOverLoad) throws ClassNotFoundException {

        String functionName = getInputOutputFunctionName(functionDefinition, isOverLoad);

        MethodSpec.Builder methodBuilder =
                MethodSpec.methodBuilder(
                                "get" + StringUtils.capitaliseFirstLetter(functionName) + "Input")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(TransactionReceipt.class, "transactionReceipt");

        List<ABIDefinition.NamedType> inputTypes = functionDefinition.getInputs();
        List<TypeName> returnTypes = new ArrayList<>();
        for (ABIDefinition.NamedType outputType : inputTypes) {
            if (outputType.getType().equals("tuple")) {
                returnTypes.add(structClassNameMap.get(outputType.structIdentifier()));
            } else if (outputType.getType().startsWith("tuple")
                    && outputType.getType().contains("[")) {
                returnTypes.add(buildStructArrayTypeName(outputType));
            } else {
                returnTypes.add(getNativeType(buildTypeName(outputType.getType())));
            }
        }

        ParameterizedTypeName parameterizedTupleType =
                ParameterizedTypeName.get(
                        ClassName.get(TUPLE_PACKAGE_NAME, "Tuple" + returnTypes.size()),
                        returnTypes.toArray(new TypeName[0]));

        methodBuilder.returns(parameterizedTupleType);
        methodBuilder.addStatement("String data = transactionReceipt.getInput().substring(10)");

        buildVariableLengthReturnFunctionConstructor(
                methodBuilder,
                functionDefinition.getName(),
                "",
                buildTypeNames(functionDefinition.getInputs()));

        methodBuilder.addStatement(
                "$T<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters())",
                List.class);

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

        List<ABIDefinition.NamedType> outputTypes = functionDefinition.getOutputs();
        List<TypeName> returnTypes = new ArrayList<>();
        for (ABIDefinition.NamedType outputType : outputTypes) {
            if (outputType.getType().equals("tuple")) {
                returnTypes.add(structClassNameMap.get(outputType.structIdentifier()));
            } else if (outputType.getType().startsWith("tuple")
                    && outputType.getType().contains("[")) {
                returnTypes.add(buildStructArrayTypeName(outputType));
            } else {
                returnTypes.add(getNativeType(buildTypeName(outputType.getType())));
            }
        }

        ParameterizedTypeName parameterizedTupleType =
                ParameterizedTypeName.get(
                        ClassName.get(TUPLE_PACKAGE_NAME, "Tuple" + returnTypes.size()),
                        returnTypes.toArray(new TypeName[0]));

        methodBuilder.returns(parameterizedTupleType);
        methodBuilder.addStatement("String data = transactionReceipt.getOutput()");

        buildVariableLengthReturnFunctionConstructor(
                methodBuilder,
                functionDefinition.getName(),
                "",
                buildTypeNames(functionDefinition.getOutputs()));

        methodBuilder.addStatement(
                "$T<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters())",
                List.class);

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
            String inputParams) {
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
            List<TypeName> returnTypes = new ArrayList<>();
            for (int i = 0; i < functionDefinition.getOutputs().size(); ++i) {
                ABIDefinition.NamedType outputType = functionDefinition.getOutputs().get(i);
                if (outputType.getType().equals("tuple")) {
                    returnTypes.add(structClassNameMap.get(outputType.structIdentifier()));
                } else if (outputType.getType().startsWith("tuple")
                        && outputType.getType().contains("[")) {
                    returnTypes.add(buildStructArrayTypeName(outputType));
                } else {
                    returnTypes.add(getNativeType(outputParameterTypes.get(i)));
                }
            }

            ParameterizedTypeName parameterizedTupleType =
                    ParameterizedTypeName.get(
                            ClassName.get(TUPLE_PACKAGE_NAME, "Tuple" + returnTypes.size()),
                            returnTypes.toArray(new TypeName[0]));

            methodBuilder.returns(parameterizedTupleType);

            buildVariableLengthReturnFunctionConstructor(
                    methodBuilder, functionName, inputParams, outputParameterTypes);

            this.buildTupleResultContainer(
                    methodBuilder, parameterizedTupleType, outputParameterTypes);
        }
    }

    private void buildTransactionFunction(
            ABIDefinition functionDefinition,
            MethodSpec.Builder methodBuilder,
            String inputParams) {
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
            ABIDefinition functionDefinition,
            MethodSpec.Builder methodBuilder,
            String inputParams) {
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
            ABIDefinition functionDefinition,
            MethodSpec.Builder methodBuilder,
            String inputParams) {
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
            final TypeName typeName;
            if (namedType.getType().equals("tuple")) {
                typeName = structClassNameMap.get(namedType.structIdentifier());
            } else if (namedType.getType().startsWith("tuple")
                    && namedType.getType().contains("[")) {
                typeName = buildStructArrayTypeName(namedType.namedType);
            } else {
                typeName = getEventNativeType(namedType.typeName);
            }
            builder.addField(typeName, namedType.getName(), Modifier.PUBLIC);
        }

        for (NamedTypeName namedType : nonIndexedParameters) {
            final TypeName typeName;
            if (namedType.getType().equals("tuple")) {
                typeName = structClassNameMap.get(namedType.structIdentifier());
            } else if (namedType.getType().startsWith("tuple")
                    && namedType.getType().contains("[")) {
                typeName = buildStructArrayTypeName(namedType.namedType);
            } else {
                typeName = getNativeType(namedType.typeName);
            }
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
        // FIXME: event
        // String generatedFunctionName =
        //         "subscribe" + StringUtils.capitaliseFirstLetter(eventName) + "Event";
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

        int index = 0;
        Set<String> eventParamNameFilter = new HashSet<>();
        for (ABIDefinition.NamedType namedType : inputs) {
            if (namedType.getName() != null && !namedType.getName().equals("")) {
                eventParamNameFilter.add(namedType.getName());
            }
        }
        for (ABIDefinition.NamedType namedType : inputs) {
            final TypeName typeName;
            if (namedType.getType().equals("tuple")) {
                typeName = structClassNameMap.get(namedType.structIdentifier());
            } else if (namedType.getType().startsWith("tuple")
                    && namedType.getType().contains("[")) {
                typeName = buildStructArrayTypeName(namedType);
            } else {
                typeName = buildTypeName(namedType.getType());
            }

            if (namedType.getName() == null || namedType.getName().equals("")) {
                String paramName = functionName + "Param" + index;
                while (eventParamNameFilter.contains(paramName)) {
                    index++;
                    paramName = functionName + "Param" + index;
                }
                eventParamNameFilter.add(paramName);
                namedType.setName(paramName);
            }

            NamedTypeName parameter = new NamedTypeName(namedType, typeName);
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

        return methods;
    }

    private CodeBlock buildTypedResponse(
            String objectName,
            List<NamedTypeName> indexedParameters,
            List<NamedTypeName> nonIndexedParameters,
            boolean flowable) {
        CodeBlock.Builder builder = CodeBlock.builder();
        if (flowable) {
            builder.addStatement("$L.log = log", objectName);
        } else {
            builder.addStatement("$L.log = eventValues.getLog()", objectName);
        }
        for (int i = 0; i < indexedParameters.size(); i++) {
            final NamedTypeName namedTypeName = indexedParameters.get(i);
            String nativeConversion;
            if (structClassNameMap.values().stream()
                    .noneMatch(name -> name.equals(namedTypeName.getTypeName()))) {
                nativeConversion = ".getValue()";
            } else {
                nativeConversion = "";
            }

            final TypeName typeName;
            if (namedTypeName.getType().equals("tuple")) {
                typeName = structClassNameMap.get(namedTypeName.structIdentifier());
            } else if (namedTypeName.getType().startsWith("tuple")
                    && namedTypeName.getType().contains("[")) {
                typeName = buildStructArrayTypeName(namedTypeName.namedType);
            } else {
                typeName = getEventNativeType(namedTypeName.getTypeName());
            }

            builder.addStatement(
                    "$L.$L = ($T) eventValues.getIndexedValues().get($L)" + nativeConversion,
                    objectName,
                    indexedParameters.get(i).getName(),
                    typeName,
                    i);
        }

        for (int i = 0; i < nonIndexedParameters.size(); i++) {
            final NamedTypeName namedTypeName = nonIndexedParameters.get(i);
            final String nativeConversion;
            if (structClassNameMap.values().stream()
                    .noneMatch(name -> name.equals(namedTypeName.getTypeName()))) {
                nativeConversion = ".getValue()";
            } else {
                nativeConversion = "";
            }

            final TypeName typeName;
            if (nonIndexedParameters.get(i).getType().equals("tuple")) {
                typeName = structClassNameMap.get(namedTypeName.structIdentifier());
            } else if (nonIndexedParameters.get(i).getType().startsWith("tuple")
                    && nonIndexedParameters.get(i).getType().contains("[")) {
                typeName = buildStructArrayTypeName(namedTypeName.namedType);
            } else {
                typeName = getNativeType(nonIndexedParameters.get(i).getTypeName());
            }
            builder.addStatement(
                    "$L.$L = ($T) eventValues.getNonIndexedValues().get($L)" + nativeConversion,
                    objectName,
                    nonIndexedParameters.get(i).getName(),
                    typeName,
                    i);
        }
        return builder.build();
    }

    @SuppressWarnings("rawtypes")
    protected static TypeName buildTypeName(String typeDeclaration) throws ClassNotFoundException {
        String type = trimStorageDeclaration(typeDeclaration);
        final TypeReference typeReference = TypeReference.makeTypeReference(type, false);
        return TypeName.get(typeReference.getType());
    }

    private static Class<?> getStaticArrayTypeReferenceClass(String type) {
        try {
            return Class.forName("org.fisco.bcos.sdk.codec.datatypes.generated.StaticArray" + type);
        } catch (ClassNotFoundException e) {
            // Unfortunately we can't encode its length as a type if it's > 32.
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
        private final ABIDefinition.NamedType namedType;

        NamedTypeName(ABIDefinition.NamedType namedType, TypeName typeName) {
            this.namedType = namedType;
            this.typeName = typeName;
        }

        public String getName() {
            return namedType.getName();
        }

        public String getType() {
            return namedType.getType();
        }

        public TypeName getTypeName() {
            return this.typeName;
        }

        public boolean isIndexed() {
            return namedType.isIndexed();
        }

        public int structIdentifier() {
            return namedType.structIdentifier();
        }
    }

    private static String getBinaryFuncDefinition() {
        return GET_BINARY_FUNC + "(client.getCryptoSuite())";
    }

    private static String getABIFuncDefinition() {
        return GET_ABI_FUNC + "()";
    }
}
