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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.contract.precompiled.callback.PrecompiledCallback;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.common.Condition;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.common.Entry;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.common.UpdateFields;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.PrecompiledConstant;
import org.fisco.bcos.sdk.v3.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

public class TableCRUDService {
    private final Client client;
    private final TableManagerPrecompiled tableManagerPrecompiled;

    public TableCRUDService(Client client, CryptoKeyPair credential) {
        this.client = client;
        this.tableManagerPrecompiled =
                TableManagerPrecompiled.load(
                        this.client.isWASM()
                                ? PrecompiledAddress.TABLE_MANAGER_PRECOMPILED_NAME
                                : PrecompiledAddress.TABLE_MANAGER_PRECOMPILED_ADDRESS,
                        client,
                        credential);
    }

    public void checkKey(String key) throws ContractException {
        if (key.length() > PrecompiledConstant.TABLE_KEY_MAX_LENGTH) {
            throw new ContractException(PrecompiledRetCode.OVER_TABLE_KEY_LENGTH_LIMIT);
        }
    }

    public RetCode createTable(String tableName, String keyFieldName, List<String> valueFields)
            throws ContractException {
        checkKey(keyFieldName);
        TableManagerPrecompiled.TableInfo tableInfo =
                new TableManagerPrecompiled.TableInfo(keyFieldName, valueFields);
        return ReceiptParser.parseTransactionReceipt(
                tableManagerPrecompiled.createTable(tableName, tableInfo));
    }

    public void asyncCreateTable(
            String tableName,
            String keyFieldName,
            List<String> valueFields,
            PrecompiledCallback callback)
            throws ContractException {
        checkKey(keyFieldName);
        TableManagerPrecompiled.TableInfo tableInfo =
                new TableManagerPrecompiled.TableInfo(keyFieldName, valueFields);
        this.tableManagerPrecompiled.createTable(
                tableName, tableInfo, createTransactionCallback(callback));
    }

    public RetCode appendColumns(String tableName, List<String> newColumns)
            throws ContractException {
        return ReceiptParser.parseTransactionReceipt(
                this.tableManagerPrecompiled.appendColumns(tableName, newColumns));
    }

    public void asyncAppendColumns(
            String tableName, List<String> newColumns, PrecompiledCallback callback) {
        this.tableManagerPrecompiled.appendColumns(
                tableName, newColumns, createTransactionCallback(callback));
    }

    public List<Map<String, String>> select(String tableName, Condition condition)
            throws ContractException {
        String address = client.isWASM() ? tableName : tableManagerPrecompiled.openTable(tableName);
        TablePrecompiled tablePrecompiled =
                TablePrecompiled.load(address, client, client.getCryptoSuite().getCryptoKeyPair());

        TablePrecompiled.TableInfo tableInfo = tablePrecompiled.desc();

        List<TablePrecompiled.Entry> selectEntry =
                tablePrecompiled.select(condition.getTableConditions(), condition.getLimit());
        List<Map<String, String>> result = new ArrayList<>();
        for (TablePrecompiled.Entry entry : selectEntry) {
            Map<String, String> kvs = new HashMap<>();
            kvs.put(tableInfo.keyColumn, entry.key);
            for (int i = 0; i < entry.fields.size(); i++) {
                kvs.put(tableInfo.valueColumns.get(i), entry.fields.get(i));
            }
            result.add(kvs);
        }
        return result;
    }

    public Map<String, String> select(String tableName, String key) throws ContractException {
        String address = client.isWASM() ? tableName : tableManagerPrecompiled.openTable(tableName);
        TablePrecompiled tablePrecompiled =
                TablePrecompiled.load(address, client, client.getCryptoSuite().getCryptoKeyPair());

        TablePrecompiled.TableInfo tableInfo = tablePrecompiled.desc();

        TablePrecompiled.Entry selectEntry = tablePrecompiled.select(key);
        Map<String, String> result = new HashMap<>();
        if (selectEntry.fields.isEmpty()) {
            return result;
        }
        result.put(tableInfo.keyColumn, selectEntry.key);
        for (int i = 0; i < selectEntry.fields.size(); i++) {
            result.put(tableInfo.valueColumns.get(i), selectEntry.fields.get(i));
        }
        return result;
    }

    public RetCode insert(String tableName, Entry entry) throws ContractException {
        String address = client.isWASM() ? tableName : tableManagerPrecompiled.openTable(tableName);
        TablePrecompiled tablePrecompiled =
                TablePrecompiled.load(address, client, client.getCryptoSuite().getCryptoKeyPair());
        return ReceiptParser.parseTransactionReceipt(
                tablePrecompiled.insert(entry.covertToEntry()));
    }

    public RetCode insert(TablePrecompiled tablePrecompiled, Entry fieldNameToValue)
            throws ContractException {
        return ReceiptParser.parseTransactionReceipt(
                tablePrecompiled.insert(fieldNameToValue.covertToEntry()));
    }

    public void asyncInsert(String tableName, Entry entry, PrecompiledCallback callback)
            throws ContractException {
        String address = client.isWASM() ? tableName : tableManagerPrecompiled.openTable(tableName);
        TablePrecompiled tablePrecompiled =
                TablePrecompiled.load(address, client, client.getCryptoSuite().getCryptoKeyPair());
        tablePrecompiled.insert(entry.covertToEntry(), createTransactionCallback(callback));
    }

    public RetCode update(String tableName, String key, UpdateFields updateFields)
            throws ContractException {
        String address = client.isWASM() ? tableName : tableManagerPrecompiled.openTable(tableName);
        TablePrecompiled tablePrecompiled =
                TablePrecompiled.load(address, client, client.getCryptoSuite().getCryptoKeyPair());
        TablePrecompiled.TableInfo tableInfo = tablePrecompiled.desc();
        return ReceiptParser.parseTransactionReceipt(
                tablePrecompiled.update(key, updateFields.getUpdateFields(tableInfo.valueColumns)));
    }

    public RetCode update(String tableName, Condition condition, UpdateFields updateFields)
            throws ContractException {
        String address = client.isWASM() ? tableName : tableManagerPrecompiled.openTable(tableName);
        TablePrecompiled tablePrecompiled =
                TablePrecompiled.load(address, client, client.getCryptoSuite().getCryptoKeyPair());
        TablePrecompiled.TableInfo tableInfo = tablePrecompiled.desc();
        return ReceiptParser.parseTransactionReceipt(
                tablePrecompiled.update(
                        condition.getTableConditions(),
                        condition.getLimit(),
                        updateFields.getUpdateFields(tableInfo.valueColumns)));
    }

    public void asyncUpdate(
            String tableName, String key, UpdateFields updateFields, PrecompiledCallback callback)
            throws ContractException {
        String address = client.isWASM() ? tableName : tableManagerPrecompiled.openTable(tableName);
        TablePrecompiled tablePrecompiled =
                TablePrecompiled.load(address, client, client.getCryptoSuite().getCryptoKeyPair());
        TablePrecompiled.TableInfo tableInfo = tablePrecompiled.desc();
        tablePrecompiled.update(
                key,
                updateFields.getUpdateFields(tableInfo.valueColumns),
                createTransactionCallback(callback));
    }

    public void asyncUpdate(
            String tableName,
            Condition condition,
            UpdateFields updateFields,
            PrecompiledCallback callback)
            throws ContractException {
        String address = client.isWASM() ? tableName : tableManagerPrecompiled.openTable(tableName);
        TablePrecompiled tablePrecompiled =
                TablePrecompiled.load(address, client, client.getCryptoSuite().getCryptoKeyPair());
        TablePrecompiled.TableInfo tableInfo = tablePrecompiled.desc();
        tablePrecompiled.update(
                condition.getTableConditions(),
                condition.getLimit(),
                updateFields.getUpdateFields(tableInfo.valueColumns),
                createTransactionCallback(callback));
    }

    public RetCode remove(String tableName, Condition condition) throws ContractException {
        String address = client.isWASM() ? tableName : tableManagerPrecompiled.openTable(tableName);
        TablePrecompiled tablePrecompiled =
                TablePrecompiled.load(address, client, client.getCryptoSuite().getCryptoKeyPair());
        return ReceiptParser.parseTransactionReceipt(
                tablePrecompiled.remove(condition.getTableConditions(), condition.getLimit()));
    }

    public RetCode remove(String tableName, String key) throws ContractException {
        String address = client.isWASM() ? tableName : tableManagerPrecompiled.openTable(tableName);
        TablePrecompiled tablePrecompiled =
                TablePrecompiled.load(address, client, client.getCryptoSuite().getCryptoKeyPair());

        return ReceiptParser.parseTransactionReceipt(tablePrecompiled.remove(key));
    }

    public void asyncRemove(String tableName, String key, PrecompiledCallback callback)
            throws ContractException {
        String address = client.isWASM() ? tableName : tableManagerPrecompiled.openTable(tableName);
        TablePrecompiled tablePrecompiled =
                TablePrecompiled.load(address, client, client.getCryptoSuite().getCryptoKeyPair());

        tablePrecompiled.remove(key, createTransactionCallback(callback));
    }

    public void asyncRemove(String tableName, Condition condition, PrecompiledCallback callback)
            throws ContractException {
        String address = client.isWASM() ? tableName : tableManagerPrecompiled.openTable(tableName);
        TablePrecompiled tablePrecompiled =
                TablePrecompiled.load(address, client, client.getCryptoSuite().getCryptoKeyPair());

        tablePrecompiled.remove(
                condition.getTableConditions(),
                condition.getLimit(),
                createTransactionCallback(callback));
    }

    public Map<String, List<String>> desc(String tableName) throws ContractException {
        String address = client.isWASM() ? tableName : tableManagerPrecompiled.openTable(tableName);
        TablePrecompiled tablePrecompiled =
                TablePrecompiled.load(address, client, client.getCryptoSuite().getCryptoKeyPair());
        TablePrecompiled.TableInfo tableInfo = tablePrecompiled.desc();
        Map<String, List<String>> descMap = new HashMap<>();
        descMap.put(
                PrecompiledConstant.KEY_FIELD_NAME, Collections.singletonList(tableInfo.keyColumn));
        descMap.put(PrecompiledConstant.VALUE_FIELD_NAME, tableInfo.valueColumns);
        return descMap;
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
}
