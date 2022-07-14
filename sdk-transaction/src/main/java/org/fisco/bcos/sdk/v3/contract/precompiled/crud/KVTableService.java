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

import static org.fisco.bcos.sdk.v3.contract.precompiled.crud.common.Common.TABLE_PREFIX;

import java.util.HashMap;
import java.util.Map;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.contract.precompiled.callback.PrecompiledCallback;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.PrecompiledConstant;
import org.fisco.bcos.sdk.v3.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KVTableService {
    private final Client client;
    private final TableManagerPrecompiled tableManagerPrecompiled;
    private final Logger logger = LoggerFactory.getLogger(KVTableService.class);

    public KVTableService(Client client, CryptoKeyPair credential) {
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

    public RetCode createTable(String tableName, String keyFieldName, String valueField)
            throws ContractException {
        checkKey(keyFieldName);
        TransactionReceipt transactionReceipt =
                tableManagerPrecompiled.createKVTable(tableName, keyFieldName, valueField);

        return ReceiptParser.parseTransactionReceipt(
                transactionReceipt,
                tr -> tableManagerPrecompiled.getCreateKVTableOutput(tr).getValue1());
    }

    public RetCode set(String tableName, String key, String value) throws ContractException {
        KVTablePrecompiled kvTablePrecompiled = loadKVTablePrecompiled(tableName);

        return set(kvTablePrecompiled, key, value);
    }

    public RetCode set(KVTablePrecompiled kvTablePrecompiled, String key, String value)
            throws ContractException {
        TransactionReceipt receipt = kvTablePrecompiled.set(key, value);
        return ReceiptParser.parseTransactionReceipt(
                receipt, tr -> kvTablePrecompiled.getSetOutput(tr).getValue1());
    }

    public String get(String tableName, String key) throws ContractException {
        KVTablePrecompiled kvTablePrecompiled = loadKVTablePrecompiled(tableName);

        return get(kvTablePrecompiled, key);
    }

    public String get(KVTablePrecompiled kvTablePrecompiled, String key) throws ContractException {
        Tuple2<Boolean, String> booleanStringTuple = kvTablePrecompiled.get(key);
        if (!booleanStringTuple.getValue1()) {
            throw new ContractException(
                    "get from "
                            + kvTablePrecompiled.getContractAddress()
                            + " failed, return false.");
        }
        return booleanStringTuple.getValue2();
    }

    public Map<String, String> desc(String tableName) throws ContractException {
        TableManagerPrecompiled.TableInfo desc = tableManagerPrecompiled.desc(tableName);
        Map<String, String> tableDesc = new HashMap<>();
        tableDesc.put(PrecompiledConstant.KEY_FIELD_NAME, desc.keyColumn);
        tableDesc.put(PrecompiledConstant.VALUE_FIELD_NAME, desc.valueColumns.get(0));
        return tableDesc;
    }

    public void asyncSet(String tableName, String key, String value, PrecompiledCallback callback)
            throws ContractException {
        KVTablePrecompiled kvTablePrecompiled = loadKVTablePrecompiled(tableName);
        asyncSet(kvTablePrecompiled, key, value, callback);
    }

    public void asyncSet(
            KVTablePrecompiled kvTablePrecompiled,
            String key,
            String value,
            PrecompiledCallback callback) {
        kvTablePrecompiled.set(
                key,
                value,
                new TransactionCallback() {
                    @Override
                    public void onResponse(TransactionReceipt receipt) {
                        RetCode retCode;
                        try {
                            retCode =
                                    ReceiptParser.parseTransactionReceipt(
                                            receipt,
                                            tr -> kvTablePrecompiled.getSetOutput(tr).getValue1());
                        } catch (ContractException e) {
                            retCode = new RetCode(e.getErrorCode(), e.getMessage());
                            retCode.setTransactionReceipt(receipt);
                        }
                        callback.onResponse(retCode);
                    }
                });
    }

    private String getTableName(String tableName) {
        if (tableName.length() > TABLE_PREFIX.length() && tableName.startsWith(TABLE_PREFIX)) {
            return tableName;
        }
        return TABLE_PREFIX + (tableName.startsWith("/") ? tableName.substring(1) : tableName);
    }

    private KVTablePrecompiled loadKVTablePrecompiled(String tableName) throws ContractException {
        String address =
                client.isWASM()
                        ? getTableName(tableName)
                        : tableManagerPrecompiled.openTable(tableName);
        if (StringUtils.isEmpty(address)) {
            logger.error("Empty address when loadKVTablePrecompiled, tableName: {}", tableName);
            throw new ContractException(
                    "Empty address when loadKVTablePrecompiled, tableName: " + tableName);
        }
        return KVTablePrecompiled.load(address, client, client.getCryptoSuite().getCryptoKeyPair());
    }
}
