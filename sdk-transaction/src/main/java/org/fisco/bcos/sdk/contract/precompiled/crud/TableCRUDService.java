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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.contract.precompiled.callback.PrecompiledCallback;
import org.fisco.bcos.sdk.contract.precompiled.crud.common.Condition;
import org.fisco.bcos.sdk.contract.precompiled.crud.common.Entry;
import org.fisco.bcos.sdk.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.PrecompiledConstant;
import org.fisco.bcos.sdk.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.model.RetCode;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.utils.StringUtils;

public class TableCRUDService {
    private final Client client;
    private final TablePrecompiled tablePrecompiled;
    private static final String ValueFieldsDelimiter = ",";

    public TableCRUDService(Client client, CryptoKeyPair credential) {
        this.client = client;
        this.tablePrecompiled =
                TablePrecompiled.load(
                        client.isWASM()
                                ? PrecompiledAddress.TABLEFACTORY_PRECOMPILED_NAME
                                : PrecompiledAddress.TABLEFACTORY_PRECOMPILED_ADDRESS,
                        client,
                        credential);
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
        return ReceiptParser.parseTransactionReceipt(
                tablePrecompiled.createTable(tableName, keyFieldName, valueFieldsString));
    }

    public RetCode insert(String tableName, Entry fieldNameToValue) throws ContractException {
        return ReceiptParser.parseTransactionReceipt(
                tablePrecompiled.insert(tableName, fieldNameToValue.getTablePrecompiledEntry()));
    }

    public RetCode update(String tableName, Entry fieldNameToValue, Condition condition)
            throws ContractException {
        TablePrecompiled.Condition cond =
                (condition == null)
                        ? new TablePrecompiled.Condition(new ArrayList<>())
                        : condition.getConditions();
        return ReceiptParser.parseTransactionReceipt(
                tablePrecompiled.update(
                        tableName, fieldNameToValue.getTablePrecompiledEntry(), cond));
    }

    public RetCode remove(String tableName, Condition condition) throws ContractException {
        TablePrecompiled.Condition cond =
                (condition == null)
                        ? new TablePrecompiled.Condition(new ArrayList<>())
                        : condition.getConditions();
        return ReceiptParser.parseTransactionReceipt(tablePrecompiled.remove(tableName, cond));
    }

    public List<Map<String, String>> select(String tableName, Condition condition)
            throws ContractException {
        try {
            TablePrecompiled.Condition cond =
                    (condition == null)
                            ? new TablePrecompiled.Condition(new ArrayList<>())
                            : condition.getConditions();
            return parseSelectResult(tablePrecompiled.select(tableName, cond));
        } catch (ContractException e) {
            throw ReceiptParser.parseExceptionCall(e);
        } catch (JsonProcessingException e) {
            throw new ContractException(
                    "select "
                            + " with condition from "
                            + tableName
                            + " failed, error info:"
                            + e.getMessage(),
                    e);
        }
    }

    public static List<Map<String, String>> parseSelectResult(
            List<TablePrecompiled.Entry> selectResult) throws JsonProcessingException {
        List<Map<String, String>> result = new ArrayList<>();
        for (TablePrecompiled.Entry entry : selectResult) {
            Map<String, String> m = new HashMap<>();
            for (TablePrecompiled.KVField field : entry.fields) {
                m.put(field.key, field.value);
            }
            result.add(m);
        }
        return result;
    }

    private List<Map<String, String>> getTableDesc(String tableName) throws ContractException {
        Tuple2<String, String> tableDesc =
                tablePrecompiled.getDescOutput(tablePrecompiled.desc(tableName));
        List<Map<String, String>> tableDescList = new ArrayList<>(1);
        Map<String, String> keyToValue = new HashMap<>();
        keyToValue.put(PrecompiledConstant.KEY_FIELD_NAME, tableDesc.getValue1());
        keyToValue.put(PrecompiledConstant.VALUE_FIELD_NAME, tableDesc.getValue2());
        tableDescList.add(0, keyToValue);
        return tableDescList;
    }

    public List<Map<String, String>> desc(String tableName) throws ContractException {
        try {
            return getTableDesc(tableName);
        } catch (ContractException e) {
            throw ReceiptParser.parseExceptionCall(e);
        }
    }

    private TransactionCallback createTransactionCallback(PrecompiledCallback callback) {
        return new TransactionCallback() {
            @Override
            public void onResponse(TransactionReceipt receipt) {
                RetCode retCode = null;
                try {
                    retCode = ReceiptParser.parseTransactionReceipt(receipt);

                } catch (ContractException e) {
                    retCode.setCode(e.getErrorCode());
                    retCode.setMessage(e.getMessage());
                    retCode.setTransactionReceipt(receipt);
                }
                callback.onResponse(retCode);
            }
        };
    }

    public void asyncInsert(
            String tableName, TablePrecompiled.Entry entry, PrecompiledCallback callback)
            throws ContractException {
        this.tablePrecompiled.insert(tableName, entry, createTransactionCallback(callback));
    }

    public void asyncUpdate(
            String tableName,
            TablePrecompiled.Entry entry,
            Condition condition,
            PrecompiledCallback callback)
            throws ContractException {
        TablePrecompiled.Condition cond =
                (condition == null)
                        ? new TablePrecompiled.Condition(new ArrayList<>())
                        : condition.getConditions();
        this.tablePrecompiled.update(tableName, entry, cond, createTransactionCallback(callback));
    }

    public void asyncRemove(String tableName, Condition condition, PrecompiledCallback callback)
            throws ContractException {
        TablePrecompiled.Condition cond =
                (condition == null)
                        ? new TablePrecompiled.Condition(new ArrayList<>())
                        : condition.getConditions();
        this.tablePrecompiled.remove(tableName, cond, createTransactionCallback(callback));
    }
}
