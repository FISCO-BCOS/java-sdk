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
package org.fisco.bcos.sdk.v3.contract.precompiled.crud;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.contract.precompiled.callback.PrecompiledCallback;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.common.Condition;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.common.Entry;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.PrecompiledConstant;
import org.fisco.bcos.sdk.v3.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.utils.StringUtils;

/** This class not support in FISCO BCOS 3.0.0 rc1 Do not use it. */
@Deprecated
public class TableCRUDService {
    private final Client client;
    private final TablePrecompiled tablePrecompiled;
    private static final String ValueFieldsDelimiter = ",";

    @Deprecated
    public TableCRUDService(Client client, CryptoKeyPair credential) {
        this.client = null;
        this.tablePrecompiled = null;
        //        this.client = client;
        //        this.tablePrecompiled =
        //                TablePrecompiled.load(
        //                        client.isWASM()
        //                                ? PrecompiledAddress.TABLEFACTORY_PRECOMPILED_NAME
        //                                : PrecompiledAddress.TABLEFACTORY_PRECOMPILED_ADDRESS,
        //                        client,
        //                        credential);
    }

    @Deprecated
    public static String convertValueFieldsToString(List<String> valueFields) {
        return StringUtils.join(valueFields, ValueFieldsDelimiter);
    }

    @Deprecated
    public void checkKey(String key) throws ContractException {
        if (key.length() > PrecompiledConstant.TABLE_KEY_MAX_LENGTH) {
            throw new ContractException(PrecompiledRetCode.OVER_TABLE_KEY_LENGTH_LIMIT);
        }
    }

    @Deprecated
    public RetCode createTable(String tableName, String keyFieldName, List<String> valueFields)
            throws ContractException {
        checkKey(keyFieldName);
        String valueFieldsString = convertValueFieldsToString(valueFields);
        return ReceiptParser.parseTransactionReceipt(
                tablePrecompiled.createTable(tableName, keyFieldName, valueFieldsString));
    }

    @Deprecated
    public RetCode insert(String tableName, Entry fieldNameToValue) throws ContractException {
        return ReceiptParser.parseTransactionReceipt(
                tablePrecompiled.insert(tableName, fieldNameToValue.getTablePrecompiledEntry()));
    }

    @Deprecated
    public RetCode update(String tableName, Entry fieldNameToValue, Condition condition)
            throws ContractException {
        return ReceiptParser.parseTransactionReceipt(
                tablePrecompiled.update(
                        tableName,
                        fieldNameToValue.getTablePrecompiledEntry(),
                        condition.getTableCondition()));
    }

    @Deprecated
    public RetCode remove(String tableName, Condition condition) throws ContractException {
        return ReceiptParser.parseTransactionReceipt(
                tablePrecompiled.remove(tableName, condition.getTableCondition()));
    }

    @Deprecated
    public List<Map<String, String>> select(String tableName, Condition condition)
            throws ContractException {
        try {
            return parseSelectResult(
                    tablePrecompiled.select(tableName, condition.getTableCondition()));
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

    @Deprecated
    public static List<Map<String, String>> parseSelectResult(
            List<TablePrecompiled.Entry> selectResult) throws JsonProcessingException {
        List<Map<String, String>> result = new ArrayList<>();
        for (TablePrecompiled.Entry entry : selectResult) {
            Map<String, String> m = new HashMap<>();
            for (TablePrecompiled.KVField field : entry.fields.getValue()) {
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

    @Deprecated
    public List<Map<String, String>> desc(String tableName) throws ContractException {
        try {
            return getTableDesc(tableName);
        } catch (ContractException e) {
            throw ReceiptParser.parseExceptionCall(e);
        }
    }

    @Deprecated
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

    @Deprecated
    public void asyncInsert(String tableName, Entry entry, PrecompiledCallback callback)
            throws ContractException {
        this.tablePrecompiled.insert(
                tableName, entry.getTablePrecompiledEntry(), createTransactionCallback(callback));
    }

    @Deprecated
    public void asyncUpdate(
            String tableName, Entry entry, Condition condition, PrecompiledCallback callback)
            throws ContractException {
        this.tablePrecompiled.update(
                tableName,
                entry.getTablePrecompiledEntry(),
                condition.getTableCondition(),
                createTransactionCallback(callback));
    }

    @Deprecated
    public void asyncRemove(String tableName, Condition condition, PrecompiledCallback callback)
            throws ContractException {
        this.tablePrecompiled.remove(
                tableName, condition.getTableCondition(), createTransactionCallback(callback));
    }
}
