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
package org.fisco.bcos.sdk.contract.precompiled.crud;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.channel.model.ChannelPrococolExceiption;
import org.fisco.bcos.sdk.channel.model.EnumNodeVersion;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.exceptions.ContractException;
import org.fisco.bcos.sdk.contract.precompiled.crud.common.Condition;
import org.fisco.bcos.sdk.contract.precompiled.crud.common.Entry;
import org.fisco.bcos.sdk.contract.precompiled.crud.table.TableFactory;
import org.fisco.bcos.sdk.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.contract.precompiled.model.PrecompiledConstant;
import org.fisco.bcos.sdk.contract.precompiled.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.model.NodeVersion;
import org.fisco.bcos.sdk.model.ReceiptParser;
import org.fisco.bcos.sdk.model.RetCode;
import org.fisco.bcos.sdk.transaction.model.callback.TransactionSucCallback;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.fisco.bcos.sdk.utils.StringUtils;

public class TableCRUDService {
    private final Client client;
    private final CryptoInterface credential;
    private final CRUD crudService;
    private final TableFactory tableFactory;
    private static final String ValueFieldsDelimiter = ",";

    public TableCRUDService(Client client, CryptoInterface credential) {
        this.client = client;
        this.credential = credential;
        this.crudService =
                CRUD.load(PrecompiledAddress.CRUD_PRECOMPILED_ADDRESS, client, credential);
        this.tableFactory =
                TableFactory.load(
                        PrecompiledAddress.TABLEFACTORY_PRECOMPILED_ADDRESS, client, credential);
    }

    public static String convertValueFieldsToString(List<String> valueFields) {
        return StringUtils.join(valueFields, ValueFieldsDelimiter);
    }

    public void checkKey(String key) throws ContractException {
        if (key.length() > PrecompiledConstant.TABLE_KEY_MAX_LENGTH) {
            throw new ContractException(PrecompiledRetCode.OVER_TABLE_KEY_LENGTH_LIMIT);
        }
    }

    public RetCode createTable(String tableName, String keyFieldName, List<String> valueFields)
            throws ContractException {
        checkKey(keyFieldName);
        String valueFieldsString = convertValueFieldsToString(valueFields);
        return ReceiptParser.parsePrecompiledReceipt(
                tableFactory.createTable(tableName, keyFieldName, valueFieldsString));
    }

    public RetCode insert(String tableName, String key, Entry fieldNameToValue, Condition condition)
            throws ContractException {
        checkKey(key);
        try {
            String fieldNameToValueStr =
                    ObjectMapperFactory.getObjectMapper()
                            .writeValueAsString(fieldNameToValue.getFieldNameToValue());
            String conditionStr = encodeCondition(condition);
            return ReceiptParser.parsePrecompiledReceipt(
                    crudService.insert(tableName, key, fieldNameToValueStr, conditionStr));
        } catch (JsonProcessingException e) {
            throw new ContractException(
                    "insert "
                            + fieldNameToValue.toString()
                            + " to "
                            + tableName
                            + " failed, error info:"
                            + e.getMessage(),
                    e);
        }
    }

    public RetCode update(String tableName, String key, Entry fieldNameToValue, Condition condition)
            throws ContractException {
        checkKey(key);
        try {
            String fieldNameToValueStr =
                    ObjectMapperFactory.getObjectMapper()
                            .writeValueAsString(fieldNameToValue.getFieldNameToValue());
            String conditionStr = encodeCondition(condition);
            return ReceiptParser.parsePrecompiledReceipt(
                    crudService.update(tableName, key, fieldNameToValueStr, conditionStr, ""));
        } catch (JsonProcessingException e) {
            throw new ContractException(
                    "update "
                            + fieldNameToValue.toString()
                            + " to "
                            + tableName
                            + " failed, error info:"
                            + e.getMessage(),
                    e);
        }
    }

    private String encodeCondition(Condition condition) throws JsonProcessingException {
        if (condition == null) {
            condition = new Condition();
        }
        return ObjectMapperFactory.getObjectMapper().writeValueAsString(condition.getConditions());
    }

    public RetCode remove(String tableName, String key, Condition condition)
            throws ContractException {
        checkKey(key);
        try {
            String conditionStr = encodeCondition(condition);
            return ReceiptParser.parsePrecompiledReceipt(
                    crudService.remove(tableName, key, conditionStr, ""));
        } catch (JsonProcessingException e) {
            throw new ContractException(
                    "remove " + key + " with condition from " + tableName + " failed");
        }
    }

    public List<Map<String, String>> select(String tableName, String key, Condition condition)
            throws ContractException {
        checkKey(key);
        try {
            String conditionStr = encodeCondition(condition);
            return parseSelectResult(crudService.select(tableName, key, conditionStr, ""));
        } catch (ContractException e) {
            throw ReceiptParser.parseExceptionCall(e);
        } catch (JsonProcessingException e) {
            throw new ContractException(
                    "select "
                            + key
                            + " with condition from "
                            + tableName
                            + " failed, error info:"
                            + e.getMessage(),
                    e);
        }
    }

    public static List<Map<String, String>> parseSelectResult(String selectResult)
            throws JsonProcessingException {
        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
        return objectMapper.readValue(
                selectResult,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));
    }

    private List<Map<String, String>> getTableDescBefore230(
            EnumNodeVersion.Version enumNodeVersion, String tableName) throws ContractException {
        if (enumNodeVersion.getMajor() == 2 && enumNodeVersion.getMinor() < 2) {
            return select(
                    PrecompiledConstant.SYS_TABLE,
                    PrecompiledConstant.USER_TABLE_PREFIX + tableName,
                    new Condition());
        } else {
            return select(
                    PrecompiledConstant.SYS_TABLE,
                    PrecompiledConstant.USER_TABLE_PREFIX_2_2_0_VERSION + tableName,
                    new Condition());
        }
    }

    private List<Map<String, String>> getTableDescAfter230(String tableName)
            throws ContractException {
        Tuple2<String, String> tableDesc = crudService.desc(tableName);
        List<Map<String, String>> tableDescList = new ArrayList<>(1);
        Map<String, String> keyToValue = new HashMap<>();
        keyToValue.put(tableDesc.getValue1(), tableDesc.getValue2());
        tableDescList.add(0, keyToValue);
        return tableDescList;
    }

    public List<Map<String, String>> desc(String tableName) throws ContractException {
        try {
            NodeVersion nodeVersion = client.getClientNodeVersion();
            EnumNodeVersion.Version enumNodeVersion =
                    EnumNodeVersion.getClassVersion(
                            nodeVersion.getNodeVersion().getSupportedVersion());
            if (enumNodeVersion.getMajor() == 2 && enumNodeVersion.getMinor() <= 3) {
                return getTableDescBefore230(enumNodeVersion, tableName);
            }
            return getTableDescAfter230(tableName);
        } catch (ChannelPrococolExceiption e) {
            throw new ContractException(
                    "Obtain description for "
                            + tableName
                            + " failed, error info: "
                            + e.getMessage(),
                    e);
        } catch (ContractException e) {
            throw ReceiptParser.parseExceptionCall(e);
        }
    }

    public void asyncInsert(
            String tableName,
            String key,
            Entry fieldNameToValue,
            Condition condition,
            TransactionSucCallback callback)
            throws ContractException {
        checkKey(key);
        try {
            String fieldNameToValueStr =
                    ObjectMapperFactory.getObjectMapper()
                            .writeValueAsString(fieldNameToValue.getFieldNameToValue());
            String conditionStr = encodeCondition(condition);
            this.crudService.insert(tableName, key, fieldNameToValueStr, conditionStr, callback);
        } catch (JsonProcessingException e) {
            throw new ContractException(
                    "asyncInsert "
                            + fieldNameToValue.toString()
                            + " to "
                            + tableName
                            + " failed, error info:"
                            + e.getMessage(),
                    e);
        }
    }

    public void asyncUpdate(
            String tableName,
            String key,
            Entry fieldNameToValue,
            Condition condition,
            TransactionSucCallback callback)
            throws ContractException {
        checkKey(key);
        try {
            String fieldNameToValueStr =
                    ObjectMapperFactory.getObjectMapper()
                            .writeValueAsString(fieldNameToValue.getFieldNameToValue());
            String conditionStr = encodeCondition(condition);
            this.crudService.update(
                    tableName, key, fieldNameToValueStr, conditionStr, "", callback);
        } catch (JsonProcessingException e) {
            throw new ContractException(
                    "asyncUpdate "
                            + fieldNameToValue.toString()
                            + " to "
                            + tableName
                            + " failed, error info:"
                            + e.getMessage(),
                    e);
        }
    }

    public void asyncRemove(
            String tableName, String key, Condition condition, TransactionSucCallback callback)
            throws ContractException {
        checkKey(key);
        try {
            this.crudService.remove(tableName, key, encodeCondition(condition), "", callback);
        } catch (JsonProcessingException e) {
            throw new ContractException(
                    "asyncRemove " + key + " with condition from " + tableName + " failed");
        }
    }
}
