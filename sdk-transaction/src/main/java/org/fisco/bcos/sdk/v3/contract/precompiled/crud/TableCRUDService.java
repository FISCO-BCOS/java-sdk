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

    /**
     * create a table with table name, key name, value field names
     *
     * @param tableName table name, will add prefix /tables/ in blockchain, tableName length not
     *     longer than 50 with prefix
     * @param keyFieldName key field name, which length should not be longer than 64
     * @param valueFields value field names, which length in total should not be longer than 1024
     * @return if success then return 0; otherwise is failed then see the retCode message
     */
    public RetCode createTable(String tableName, String keyFieldName, List<String> valueFields)
            throws ContractException {
        TableManagerPrecompiled.TableInfo tableInfo =
                new TableManagerPrecompiled.TableInfo(keyFieldName, valueFields);
        return ReceiptParser.parseTransactionReceipt(
                tableManagerPrecompiled.createTable(tableName, tableInfo));
    }

    /**
     * async create a table with table name, key name, value field names
     *
     * @param tableName table name, will add prefix /tables/ in blockchain, tableName length not
     *     longer than 50 with prefix
     * @param keyFieldName key field name, which length should not be longer than 64
     * @param valueFields value field names, which length in total should not be longer than 1024
     * @param callback callback when get receipt
     */
    public void asyncCreateTable(
            String tableName,
            String keyFieldName,
            List<String> valueFields,
            PrecompiledCallback callback) {
        TableManagerPrecompiled.TableInfo tableInfo =
                new TableManagerPrecompiled.TableInfo(keyFieldName, valueFields);
        this.tableManagerPrecompiled.createTable(
                tableName, tableInfo, createTransactionCallback(callback));
    }

    /**
     * append new columns to a specific table with table name, new value field names
     *
     * @param tableName specific table name, table should exist
     * @param newColumns new value field names, which length in total should not be longer than
     *     1024, and should not be duplicate
     * @return if success then return 0; otherwise is failed then see the retCode message
     */
    public RetCode appendColumns(String tableName, List<String> newColumns)
            throws ContractException {
        return ReceiptParser.parseTransactionReceipt(
                this.tableManagerPrecompiled.appendColumns(tableName, newColumns));
    }

    /**
     * async append new columns to a specific table with table name, new value field names
     *
     * @param tableName specific table name, table should exist
     * @param newColumns new value field names, which length in total should not be longer than
     *     1024, and should not be duplicate
     * @param callback callback when get receipt
     */
    public void asyncAppendColumns(
            String tableName, List<String> newColumns, PrecompiledCallback callback) {
        this.tableManagerPrecompiled.appendColumns(
                tableName, newColumns, createTransactionCallback(callback));
    }

    /**
     * select data in a specific table with table name, condition
     *
     * @param tableName specific table name, table should exist
     * @param condition condition about key
     * @return return a list of select result which match condition
     */
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

    /**
     * select data in a specific table with table name, table desc info, condition. this method will
     * reduce table.desc() overhead
     *
     * @param tableName specific table name, table should exist
     * @param desc table key field and value fields info, [(key_field: [""]),(value_fields: [""])]
     * @param condition condition about key
     * @return return a list of select result which match condition
     */
    public List<Map<String, String>> select(
            String tableName, Map<String, List<String>> desc, Condition condition)
            throws ContractException {
        String address = client.isWASM() ? tableName : tableManagerPrecompiled.openTable(tableName);
        TablePrecompiled tablePrecompiled =
                TablePrecompiled.load(address, client, client.getCryptoSuite().getCryptoKeyPair());

        List<TablePrecompiled.Entry> selectEntry =
                tablePrecompiled.select(condition.getTableConditions(), condition.getLimit());
        List<Map<String, String>> result = new ArrayList<>();
        for (TablePrecompiled.Entry entry : selectEntry) {
            Map<String, String> kvs = new HashMap<>();
            kvs.put(desc.get(PrecompiledConstant.KEY_FIELD_NAME).get(0), entry.key);
            for (int i = 0; i < entry.fields.size(); i++) {
                kvs.put(desc.get(PrecompiledConstant.VALUE_FIELD_NAME).get(i), entry.fields.get(i));
            }
            result.add(kvs);
        }
        return result;
    }

    /**
     * select data in a specific table with table name, single key
     *
     * @param tableName specific table name, table should exist
     * @param key specific key
     * @return return select result which key in table matches the key given
     */
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

    /**
     * select data in a specific table with table name, table desc info, single key this method will
     * reduce table.desc() overhead
     *
     * @param tableName specific table name, table should exist
     * @param desc table key field and value fields info, [(key_field: [""]),(value_fields: [""])]
     * @param key specific key
     * @return return select result which key in table matches the key given
     */
    public Map<String, String> select(String tableName, Map<String, List<String>> desc, String key)
            throws ContractException {
        String address = client.isWASM() ? tableName : tableManagerPrecompiled.openTable(tableName);
        TablePrecompiled tablePrecompiled =
                TablePrecompiled.load(address, client, client.getCryptoSuite().getCryptoKeyPair());

        TablePrecompiled.Entry selectEntry = tablePrecompiled.select(key);
        Map<String, String> result = new HashMap<>();
        if (selectEntry.fields.isEmpty()) {
            return result;
        }
        result.put(desc.get(PrecompiledConstant.KEY_FIELD_NAME).get(0), selectEntry.key);
        for (int i = 0; i < selectEntry.fields.size(); i++) {
            result.put(
                    desc.get(PrecompiledConstant.VALUE_FIELD_NAME).get(i),
                    selectEntry.fields.get(i));
        }
        return result;
    }

    /**
     * insert data to a specific table with table name, data entry.
     *
     * @param tableName specific table name, table should exist
     * @param entry data entry, which contains key and values map, values size should match the
     *     table value field size
     * @return if success then return 0; otherwise is failed then see the retCode message
     */
    public RetCode insert(String tableName, Entry entry) throws ContractException {
        String address = client.isWASM() ? tableName : tableManagerPrecompiled.openTable(tableName);
        TablePrecompiled tablePrecompiled =
                TablePrecompiled.load(address, client, client.getCryptoSuite().getCryptoKeyPair());
        return insert(tablePrecompiled, entry);
    }

    /**
     * insert data to a specific table with table precompiled, data entry. this method will reduce
     * tableManager.openTable() overhead
     *
     * @param tablePrecompiled specific tablePrecompiled, already load a specific contract address
     * @param entry data entry, which contains key and values map, values size should match the
     *     table value field size
     * @return if success then return 0; otherwise is failed then see the retCode message
     */
    public RetCode insert(TablePrecompiled tablePrecompiled, Entry entry) throws ContractException {
        TransactionReceipt transactionReceipt = tablePrecompiled.insert(entry.covertToEntry());
        return getCurdRetCode(transactionReceipt);
    }

    /**
     * async insert data to a specific table with table name, data entry
     *
     * @param tableName specific table name, table should exist
     * @param entry data entry, which contains key and values map, values size should match the
     *     table value field size
     * @param callback callback when get receipt
     */
    public void asyncInsert(String tableName, Entry entry, PrecompiledCallback callback)
            throws ContractException {
        String address = client.isWASM() ? tableName : tableManagerPrecompiled.openTable(tableName);
        TablePrecompiled tablePrecompiled =
                TablePrecompiled.load(address, client, client.getCryptoSuite().getCryptoKeyPair());
        tablePrecompiled.insert(entry.covertToEntry(), createTransactionCallback(callback));
    }

    /**
     * update data to a specific table with table name, single key, updateFields
     *
     * @param tableName specific table name, table should exist
     * @param key specific key, key should exist
     * @param updateFields update specific fields' data
     * @return if success then return 0; otherwise is failed then see the retCode message
     */
    public RetCode update(String tableName, String key, UpdateFields updateFields)
            throws ContractException {
        String address = client.isWASM() ? tableName : tableManagerPrecompiled.openTable(tableName);
        TablePrecompiled tablePrecompiled =
                TablePrecompiled.load(address, client, client.getCryptoSuite().getCryptoKeyPair());
        return update(tablePrecompiled, key, updateFields);
    }

    /**
     * update data to a specific table with tablePrecompiled, single key, updateFields this method
     * will reduce tableManager.openTable() overhead
     *
     * @param tablePrecompiled specific tablePrecompiled, already load a specific contract address
     * @param key specific key, key should exist
     * @param updateFields update specific fields' data
     * @return if success then return 0; otherwise is failed then see the retCode message
     */
    public RetCode update(TablePrecompiled tablePrecompiled, String key, UpdateFields updateFields)
            throws ContractException {
        TablePrecompiled.TableInfo tableInfo = tablePrecompiled.desc();
        TransactionReceipt transactionReceipt =
                tablePrecompiled.update(
                        key, updateFields.convertToUpdateFields(tableInfo.valueColumns));
        return getCurdRetCode(transactionReceipt);
    }

    /**
     * update data to a specific table with table name, single key, updateFields and table value
     * columns info. this method will reduce table.desc() overhead.
     *
     * @param tableName specific table name, table should exist
     * @param key specific key, key should exist
     * @param updateFields update specific fields' data
     * @param valueColumns table value columns, can get by desc()
     * @return if success then return 0; otherwise is failed then see the retCode message
     */
    public RetCode update(
            String tableName, String key, UpdateFields updateFields, List<String> valueColumns)
            throws ContractException {
        String address = client.isWASM() ? tableName : tableManagerPrecompiled.openTable(tableName);
        TablePrecompiled tablePrecompiled =
                TablePrecompiled.load(address, client, client.getCryptoSuite().getCryptoKeyPair());
        TransactionReceipt transactionReceipt =
                tablePrecompiled.update(key, updateFields.convertToUpdateFields(valueColumns));
        return getCurdRetCode(transactionReceipt);
    }

    /**
     * update data to a specific table with table name, condition, updateFields
     *
     * @param tableName specific table name, table should exist
     * @param condition key condition
     * @param updateFields update specific fields' data
     * @return if success then return 0; otherwise is failed then see the retCode message
     */
    public RetCode update(String tableName, Condition condition, UpdateFields updateFields)
            throws ContractException {
        String address = client.isWASM() ? tableName : tableManagerPrecompiled.openTable(tableName);
        TablePrecompiled tablePrecompiled =
                TablePrecompiled.load(address, client, client.getCryptoSuite().getCryptoKeyPair());
        return update(tablePrecompiled, condition, updateFields);
    }

    /**
     * update data to a specific table with tablePrecompiled, key condition, updateFields this
     * method will reduce tableManager.openTable() overhead
     *
     * @param tablePrecompiled specific tablePrecompiled, already load a specific contract address
     * @param condition key condition
     * @param updateFields update specific fields' data
     * @return if success then return 0; otherwise is failed then see the retCode message
     */
    public RetCode update(
            TablePrecompiled tablePrecompiled, Condition condition, UpdateFields updateFields)
            throws ContractException {
        TablePrecompiled.TableInfo tableInfo = tablePrecompiled.desc();
        TransactionReceipt transactionReceipt =
                tablePrecompiled.update(
                        condition.getTableConditions(),
                        condition.getLimit(),
                        updateFields.convertToUpdateFields(tableInfo.valueColumns));
        return getCurdRetCode(transactionReceipt);
    }

    /**
     * update data to a specific table with table name, key condition, updateFields and table value
     * columns info. this method will reduce table.desc() overhead.
     *
     * @param tableName specific table name, table should exist
     * @param condition key condition
     * @param updateFields update specific fields' data
     * @param valueColumns table value columns, can get by desc()
     * @return if success then return 0; otherwise is failed then see the retCode message
     */
    public RetCode update(
            String tableName,
            Condition condition,
            UpdateFields updateFields,
            List<String> valueColumns)
            throws ContractException {
        String address = client.isWASM() ? tableName : tableManagerPrecompiled.openTable(tableName);
        TablePrecompiled tablePrecompiled =
                TablePrecompiled.load(address, client, client.getCryptoSuite().getCryptoKeyPair());
        TransactionReceipt transactionReceipt =
                tablePrecompiled.update(
                        condition.getTableConditions(),
                        condition.getLimit(),
                        updateFields.convertToUpdateFields(valueColumns));
        return getCurdRetCode(transactionReceipt);
    }

    /**
     * async update data to a specific table with table name, single key, updateFields
     *
     * @param tableName specific table name, table should exist
     * @param key specific key, key should exist
     * @param updateFields update specific fields' data
     * @param callback callback when get receipt
     */
    public void asyncUpdate(
            String tableName, String key, UpdateFields updateFields, PrecompiledCallback callback)
            throws ContractException {
        String address = client.isWASM() ? tableName : tableManagerPrecompiled.openTable(tableName);
        TablePrecompiled tablePrecompiled =
                TablePrecompiled.load(address, client, client.getCryptoSuite().getCryptoKeyPair());
        TablePrecompiled.TableInfo tableInfo = tablePrecompiled.desc();
        tablePrecompiled.update(
                key,
                updateFields.convertToUpdateFields(tableInfo.valueColumns),
                createTransactionCallback(callback));
    }

    /**
     * async update data to a specific table with table name, condition, updateFields
     *
     * @param tableName specific table name, table should exist
     * @param condition key condition
     * @param updateFields update specific fields' data
     * @param callback callback when get receipt
     */
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
                updateFields.convertToUpdateFields(tableInfo.valueColumns),
                createTransactionCallback(callback));
    }

    /**
     * remove data in a specific table with table name, single key
     *
     * @param tableName specific table name, table should exist
     * @param key specific key, key should exist
     * @return if success then return 0; otherwise is failed then see the retCode message
     */
    public RetCode remove(String tableName, String key) throws ContractException {
        String address = client.isWASM() ? tableName : tableManagerPrecompiled.openTable(tableName);
        TablePrecompiled tablePrecompiled =
                TablePrecompiled.load(address, client, client.getCryptoSuite().getCryptoKeyPair());
        return remove(tablePrecompiled, key);
    }

    /**
     * remove data in a specific table with tablePrecompiled, single key this method will reduce
     * tableManager.openTable() overhead
     *
     * @param tablePrecompiled specific tablePrecompiled, already load a specific contract address
     * @param key specific key, key should exist
     * @return if success then return 0; otherwise is failed then see the retCode message
     */
    public RetCode remove(TablePrecompiled tablePrecompiled, String key) throws ContractException {
        TransactionReceipt transactionReceipt = tablePrecompiled.remove(key);
        return getCurdRetCode(transactionReceipt);
    }

    /**
     * remove data in a specific table with table name, key condition
     *
     * @param tableName specific table name, table should exist
     * @param condition key condition
     * @return if success then return 0; otherwise is failed then see the retCode message
     */
    public RetCode remove(String tableName, Condition condition) throws ContractException {
        String address = client.isWASM() ? tableName : tableManagerPrecompiled.openTable(tableName);
        TablePrecompiled tablePrecompiled =
                TablePrecompiled.load(address, client, client.getCryptoSuite().getCryptoKeyPair());
        return remove(tablePrecompiled, condition);
    }

    /**
     * remove data in a specific table with table name, key condition this method will reduce
     * tableManager.openTable() overhead
     *
     * @param tablePrecompiled specific tablePrecompiled, already load a specific contract address
     * @param condition key condition
     * @return if success then return 0; otherwise is failed then see the retCode message
     */
    public RetCode remove(TablePrecompiled tablePrecompiled, Condition condition)
            throws ContractException {
        TransactionReceipt transactionReceipt =
                tablePrecompiled.remove(condition.getTableConditions(), condition.getLimit());
        return getCurdRetCode(transactionReceipt);
    }

    /**
     * async remove data in a specific table with table name, single key
     *
     * @param tableName specific table name, table should exist
     * @param key specific key, key should exist
     * @param callback callback when get receipt
     */
    public void asyncRemove(String tableName, String key, PrecompiledCallback callback)
            throws ContractException {
        String address = client.isWASM() ? tableName : tableManagerPrecompiled.openTable(tableName);
        TablePrecompiled tablePrecompiled =
                TablePrecompiled.load(address, client, client.getCryptoSuite().getCryptoKeyPair());

        tablePrecompiled.remove(key, createTransactionCallback(callback));
    }

    /**
     * async remove data in a specific table with table name, key condition
     *
     * @param tableName specific table name, table should exist
     * @param condition key condition
     * @param callback callback when get receipt
     */
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

    /**
     * get a specific table key and value fields with table name
     *
     * @param tableName specific table name, it should exist
     * @return table key field and value fields info, [("key_field": [""]),("value_fields": [""])]
     */
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
                    retCode = getCurdRetCode(receipt);
                } catch (ContractException e) {
                    retCode.setCode(e.getErrorCode());
                    retCode.setMessage(e.getMessage());
                    retCode.setTransactionReceipt(receipt);
                }
                callback.onResponse(retCode);
            }
        };
    }

    private RetCode getCurdRetCode(TransactionReceipt transactionReceipt) throws ContractException {
        int status = transactionReceipt.getStatus();
        if (status != 0) {
            ReceiptParser.getErrorStatus(transactionReceipt);
        }
        return ReceiptParser.getPrecompiledRetCode(
                transactionReceipt.getOutput(), transactionReceipt.getMessage());
    }
}
