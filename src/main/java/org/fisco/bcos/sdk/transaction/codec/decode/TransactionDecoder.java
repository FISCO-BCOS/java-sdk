package org.fisco.bcos.sdk.transaction.codec.decode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.abi.AbiDefinition;
import org.fisco.bcos.sdk.abi.AbiDefinition.NamedType;
import org.fisco.bcos.sdk.abi.EventEncoder;
import org.fisco.bcos.sdk.abi.EventValues;
import org.fisco.bcos.sdk.abi.FunctionEncoder;
import org.fisco.bcos.sdk.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.Function;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.model.EventLog;
import org.fisco.bcos.sdk.model.EventResultEntity;
import org.fisco.bcos.sdk.model.LogResult;
import org.fisco.bcos.sdk.model.tuple.Tuple2;
import org.fisco.bcos.sdk.transaction.exception.TransactionBaseException;
import org.fisco.bcos.sdk.transaction.exception.TransactionException;
import org.fisco.bcos.sdk.transaction.model.bo.InputAndOutputResult;
import org.fisco.bcos.sdk.transaction.model.bo.ResultEntity;
import org.fisco.bcos.sdk.transaction.tools.ContractAbiUtil;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionDecoder {

    private static final Logger logger = LoggerFactory.getLogger(TransactionDecoder.class);

    private String abi = "";
    private Map<String, AbiDefinition> methodIDMap;
    private FunctionEncoder functionEncoder;
    private EventEncoder eventEncoder;

    public TransactionDecoder(CryptoInterface cryptoInterface, String abi) {
        this.functionEncoder = new FunctionEncoder(cryptoInterface);
        this.eventEncoder = new EventEncoder(cryptoInterface);
        this.abi = abi;
        methodIDMap = new HashMap<String, AbiDefinition>();
        List<AbiDefinition> funcAbiDefinitionList = ContractAbiUtil.getFuncAbiDefinition(abi);
        for (AbiDefinition abiDefinition : funcAbiDefinitionList) {
            String methodSign = decodeMethodSign(abiDefinition);
            String methodID = functionEncoder.buildMethodId(methodSign);
            methodIDMap.put(methodID, abiDefinition);
        }
    }

    private String addHexPrefixToString(String s) {
        if (!s.startsWith("0x")) {
            return "0x" + s;
        }

        return s;
    }

    /**
     * @param input
     * @return
     * @throws JsonProcessingException
     * @throws TransactionException
     * @throws TransactionBaseException
     */
    public String decodeInputReturnJson(String input)
            throws JsonProcessingException, TransactionException, TransactionBaseException {

        input = addHexPrefixToString(input);

        // select abi
        AbiDefinition abiFunc = selectAbiDefinition(input);

        // decode input
        InputAndOutputResult inputAndOutputResult = decodeInputReturnObject(input);
        // format result to json
        String result =
                ObjectMapperFactory.getObjectMapper().writeValueAsString(inputAndOutputResult);

        return result;
    }

    /**
     * @param input
     * @return
     * @throws TransactionBaseException
     * @throws TransactionException
     */
    public InputAndOutputResult decodeInputReturnObject(String input)
            throws TransactionBaseException, TransactionException {

        String updatedInput = addHexPrefixToString(input);

        // select abi
        AbiDefinition abiDefinition = selectAbiDefinition(updatedInput);

        // decode input
        List<NamedType> inputTypes = abiDefinition.getInputs();
        List<TypeReference<?>> inputTypeReferences = ContractAbiUtil.paramFormat(inputTypes);
        Function function = new Function(abiDefinition.getName(), null, inputTypeReferences);
        List<Type> resultType =
                FunctionReturnDecoder.decode(
                        updatedInput.substring(10), function.getOutputParameters());

        // set result to java bean
        List<ResultEntity> resultList = new ArrayList<ResultEntity>();
        for (int i = 0; i < inputTypes.size(); i++) {
            resultList.add(
                    new ResultEntity(
                            inputTypes.get(i).getName(),
                            inputTypes.get(i).getType(),
                            resultType.get(i)));
        }
        String methodSign = decodeMethodSign(abiDefinition);

        return new InputAndOutputResult(
                methodSign, functionEncoder.buildMethodId(methodSign), resultList);
    }

    /**
     * @param input
     * @param output
     * @return
     * @throws JsonProcessingException
     * @throws TransactionBaseException
     * @throws TransactionException
     */
    public String decodeOutputReturnJson(String input, String output)
            throws JsonProcessingException, TransactionBaseException, TransactionException {

        InputAndOutputResult inputAndOutputResult = decodeOutputReturnObject(input, output);

        String result =
                ObjectMapperFactory.getObjectMapper().writeValueAsString(inputAndOutputResult);
        return result;
    }

    /**
     * @param input
     * @param output
     * @return
     * @throws TransactionException
     * @throws TransactionBaseException
     */
    public InputAndOutputResult decodeOutputReturnObject(String input, String output)
            throws TransactionException, TransactionBaseException {

        String updatedInput = addHexPrefixToString(input);
        String updatedOutput = addHexPrefixToString(output);

        // select abi
        AbiDefinition abiDefinition = selectAbiDefinition(updatedInput);
        // decode output
        List<NamedType> outputTypes = abiDefinition.getOutputs();
        List<TypeReference<?>> outputTypeReference = ContractAbiUtil.paramFormat(outputTypes);
        Function function = new Function(abiDefinition.getName(), null, outputTypeReference);
        List<Type> resultType =
                FunctionReturnDecoder.decode(updatedOutput, function.getOutputParameters());

        // set result to java bean
        List<ResultEntity> resultList = new ArrayList<>();
        for (int i = 0; i < outputTypes.size(); i++) {
            resultList.add(
                    new ResultEntity(
                            outputTypes.get(i).getName(),
                            outputTypes.get(i).getType(),
                            resultType.get(i)));
        }
        String methodSign = decodeMethodSign(abiDefinition);

        return new InputAndOutputResult(
                methodSign, functionEncoder.buildMethodId(methodSign), resultList);
    }

    /**
     * @param logs
     * @return
     * @throws TransactionBaseException
     * @throws IOException
     */
    public String decodeEventReturnJson(String logs) throws TransactionBaseException, IOException {
        // log json trans to list log
        ObjectMapper mapper = ObjectMapperFactory.getObjectMapper();
        CollectionType listType =
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, EventLog.class);
        @SuppressWarnings("unchecked")
        List<EventLog> logList = (List<EventLog>) mapper.readValue(logs, listType);

        // decode event
        Map<String, List<List<EventResultEntity>>> resultEntityMap =
                decodeEventReturnObject(logList);
        String result = mapper.writeValueAsString(resultEntityMap);

        return result;
    }

    /**
     * @param logList
     * @return
     * @throws TransactionBaseException
     * @throws IOException
     */
    public String decodeEventReturnJson(List<EventLog> logList)
            throws TransactionBaseException, IOException {
        // log json trans to list log
        ObjectMapper mapper = ObjectMapperFactory.getObjectMapper();
        // decode event
        Map<String, List<List<EventResultEntity>>> resultEntityMap =
                decodeEventReturnObject(logList);
        String result = mapper.writeValueAsString(resultEntityMap);

        return result;
    }

    /**
     * @param logList
     * @return
     * @throws TransactionBaseException
     * @throws IOException
     */
    public Map<String, List<List<EventResultEntity>>> decodeEventReturnObject(
            List<EventLog> logList) throws TransactionBaseException, IOException {

        // set result to java bean
        Map<String, List<List<EventResultEntity>>> resultEntityMap = new LinkedHashMap<>();

        for (EventLog log : logList) {
            Tuple2<AbiDefinition, List<EventResultEntity>> resultTuple2 =
                    decodeEventReturnObject(log);
            if (null == resultTuple2) {
                continue;
            }

            AbiDefinition abiDefinition = resultTuple2.getValue1();
            String eventName = decodeMethodSign(abiDefinition);
            if (resultEntityMap.containsKey(eventName)) {
                resultEntityMap.get(eventName).add(resultTuple2.getValue2());
            } else {
                List<List<EventResultEntity>> eventEntityList =
                        new ArrayList<List<EventResultEntity>>();
                eventEntityList.add(resultTuple2.getValue2());
                resultEntityMap.put(eventName, eventEntityList);
            }
        }

        return resultEntityMap;
    }

    /**
     * @param log
     * @return LogResult
     * @throws TransactionBaseException
     */
    public LogResult decodeEventLogReturnObject(EventLog log) throws TransactionBaseException {
        // decode log
        List<AbiDefinition> abiDefinitions = ContractAbiUtil.getEventAbiDefinitions(abi);

        LogResult result = new LogResult();

        for (AbiDefinition abiDefinition : abiDefinitions) {

            // String eventName = decodeMethodSign(abiDefinition);
            String eventSignature =
                    eventEncoder.buildEventSignature(decodeMethodSign(abiDefinition));

            List<String> topics = log.getTopics();
            if ((null == topics) || topics.isEmpty() || !topics.get(0).equals(eventSignature)) {
                continue;
            }

            EventValues eventValued = ContractAbiUtil.decodeEvent(log, abiDefinition);
            if (null != eventValued) {
                List<EventResultEntity> resultEntityList = new ArrayList<EventResultEntity>();
                List<NamedType> inputs = abiDefinition.getInputs();
                List<NamedType> indexedInputs =
                        inputs.stream().filter(NamedType::isIndexed).collect(Collectors.toList());
                List<NamedType> nonIndexedInputs =
                        inputs.stream().filter(p -> !p.isIndexed()).collect(Collectors.toList());

                for (int i = 0; i < indexedInputs.size(); i++) {
                    EventResultEntity eventEntity =
                            new EventResultEntity(
                                    indexedInputs.get(i).getName(),
                                    indexedInputs.get(i).getType(),
                                    true,
                                    eventValued.getIndexedValues().get(i));

                    resultEntityList.add(eventEntity);
                }

                for (int i = 0; i < nonIndexedInputs.size(); i++) {
                    EventResultEntity eventEntity =
                            new EventResultEntity(
                                    nonIndexedInputs.get(i).getName(),
                                    nonIndexedInputs.get(i).getType(),
                                    false,
                                    eventValued.getNonIndexedValues().get(i));

                    resultEntityList.add(eventEntity);
                }

                // result.setEventName(eventName);
                result.setLogParams(resultEntityList);
                result.setLog(log);

                logger.debug(" event log result: {}", result);

                return result;
            }
        }

        return null;
    }

    public Tuple2<AbiDefinition, List<EventResultEntity>> decodeEventReturnObject(EventLog log)
            throws TransactionBaseException, IOException {

        Tuple2<AbiDefinition, List<EventResultEntity>> result = null;

        // decode log
        List<AbiDefinition> abiDefinitions = ContractAbiUtil.getEventAbiDefinitions(abi);

        for (AbiDefinition abiDefinition : abiDefinitions) {

            String eventSignature =
                    eventEncoder.buildEventSignature(decodeMethodSign(abiDefinition));

            List<String> topics = log.getTopics();
            if ((null == topics) || topics.isEmpty() || !topics.get(0).equals(eventSignature)) {
                continue;
            }

            EventValues eventValued = ContractAbiUtil.decodeEvent(log, abiDefinition);
            if (null != eventValued) {
                List<EventResultEntity> resultEntityList = new ArrayList<EventResultEntity>();
                List<NamedType> inputs = abiDefinition.getInputs();
                List<NamedType> indexedInputs =
                        inputs.stream().filter(NamedType::isIndexed).collect(Collectors.toList());
                List<NamedType> nonIndexedInputs =
                        inputs.stream().filter(p -> !p.isIndexed()).collect(Collectors.toList());

                for (int i = 0; i < indexedInputs.size(); i++) {
                    EventResultEntity eventEntity =
                            new EventResultEntity(
                                    indexedInputs.get(i).getName(),
                                    indexedInputs.get(i).getType(),
                                    true,
                                    eventValued.getIndexedValues().get(i));

                    resultEntityList.add(eventEntity);
                }

                for (int i = 0; i < nonIndexedInputs.size(); i++) {
                    EventResultEntity eventEntity =
                            new EventResultEntity(
                                    nonIndexedInputs.get(i).getName(),
                                    nonIndexedInputs.get(i).getType(),
                                    false,
                                    eventValued.getNonIndexedValues().get(i));

                    resultEntityList.add(eventEntity);
                }

                result =
                        new Tuple2<AbiDefinition, List<EventResultEntity>>(
                                abiDefinition, resultEntityList);
                break;
            }
        }

        return result;
    }

    /**
     * @param input
     * @return
     * @throws TransactionException
     */
    private AbiDefinition selectAbiDefinition(String input) throws TransactionException {
        if (input == null || input.length() < 10) {
            throw new TransactionException("The input is invalid.");
        }
        String methodID = input.substring(0, 10);
        AbiDefinition abiDefinition = methodIDMap.get(methodID);
        if (abiDefinition == null) {
            throw new TransactionException("The method is not included in the contract abi.");
        }
        return abiDefinition;
    }

    /**
     * @param abiDefinition
     * @return
     */
    private String decodeMethodSign(AbiDefinition abiDefinition) {
        List<NamedType> inputTypes = abiDefinition.getInputs();
        StringBuilder methodSign = new StringBuilder();
        methodSign.append(abiDefinition.getName());
        methodSign.append("(");
        String params =
                inputTypes.stream().map(NamedType::getType).collect(Collectors.joining(","));
        methodSign.append(params);
        methodSign.append(")");
        return methodSign.toString();
    }
}
