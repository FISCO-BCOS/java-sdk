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

public class KVTableService {
    private final Client client;
    private final TableManagerPrecompiled tableManagerPrecompiled;

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
        return ReceiptParser.parseTransactionReceipt(
                tableManagerPrecompiled.createKVTable(tableName, keyFieldName, valueField));
    }

    public RetCode set(String tableName, String key, String value) throws ContractException {
        String address = client.isWASM() ? tableName : tableManagerPrecompiled.openTable(tableName);
        KVTablePrecompiled kvTablePrecompiled =
                KVTablePrecompiled.load(
                        address, client, client.getCryptoSuite().getCryptoKeyPair());
        return ReceiptParser.parseTransactionReceipt(kvTablePrecompiled.set(key, value));
    }

    public RetCode set(KVTablePrecompiled kvTablePrecompiled, String key, String value)
            throws ContractException {
        return ReceiptParser.parseTransactionReceipt(kvTablePrecompiled.set(key, value));
    }

    public String get(String tableName, String key) throws ContractException {
        try {
            String address =
                    client.isWASM() ? tableName : tableManagerPrecompiled.openTable(tableName);
            KVTablePrecompiled kvTablePrecompiled =
                    KVTablePrecompiled.load(
                            address, client, client.getCryptoSuite().getCryptoKeyPair());

            Tuple2<Boolean, String> booleanStringTuple = kvTablePrecompiled.get(key);
            if (!booleanStringTuple.getValue1()) {
                throw new ContractException("get from " + tableName + " failed, return false.");
            }
            return booleanStringTuple.getValue2();
        } catch (ContractException e) {
            throw ReceiptParser.parseExceptionCall(e);
        }
    }

    public String get(KVTablePrecompiled kvTablePrecompiled, String key) throws ContractException {
        try {

            Tuple2<Boolean, String> booleanStringTuple = kvTablePrecompiled.get(key);
            if (!booleanStringTuple.getValue1()) {
                throw new ContractException(
                        "get from "
                                + kvTablePrecompiled.getContractAddress()
                                + " failed, return false.");
            }
            return booleanStringTuple.getValue2();
        } catch (ContractException e) {
            throw ReceiptParser.parseExceptionCall(e);
        }
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
        String address = client.isWASM() ? tableName : tableManagerPrecompiled.openTable(tableName);
        KVTablePrecompiled kvTablePrecompiled =
                KVTablePrecompiled.load(
                        address, client, client.getCryptoSuite().getCryptoKeyPair());

        kvTablePrecompiled.set(
                key,
                value,
                new TransactionCallback() {
                    @Override
                    public void onResponse(TransactionReceipt receipt) {
                        RetCode retCode = new RetCode();
                        try {
                            retCode = ReceiptParser.parseTransactionReceipt(receipt);
                        } catch (ContractException e) {
                            retCode.setCode(e.getErrorCode());
                            retCode.setMessage(e.getMessage());
                            retCode.setTransactionReceipt(receipt);
                        }
                        callback.onResponse(retCode);
                    }
                });
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
                        RetCode retCode = new RetCode();
                        try {
                            retCode = ReceiptParser.parseTransactionReceipt(receipt);
                        } catch (ContractException e) {
                            retCode.setCode(e.getErrorCode());
                            retCode.setMessage(e.getMessage());
                            retCode.setTransactionReceipt(receipt);
                        }
                        callback.onResponse(retCode);
                    }
                });
    }
}
