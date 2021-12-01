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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.contract.precompiled.callback.PrecompiledCallback;
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

/** This class not support in FISCO BCOS 3.0.0 rc1 Do not use it. */
public class KVTableService {
    private final KVTablePrecompiled kvTablePrecompiled;
    private static final String VALUE_FIELDS_DELIMITER = ",";

    public KVTableService(Client client, CryptoKeyPair credential) {
        this.kvTablePrecompiled =
                KVTablePrecompiled.load(
                        client.isWASM()
                                ? PrecompiledAddress.KV_TABLE_PRECOMPILED_NAME
                                : PrecompiledAddress.KV_TABLE_PRECOMPILED_ADDRESS,
                        client,
                        credential);
    }

    public static String convertValueFieldsToString(List<String> valueFields) {
        return StringUtils.join(valueFields, VALUE_FIELDS_DELIMITER);
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
                kvTablePrecompiled.createTable(tableName, keyFieldName, valueFieldsString));
    }

    public RetCode set(String tableName, String key, Entry fieldNameToValue)
            throws ContractException {
        return ReceiptParser.parseTransactionReceipt(
                kvTablePrecompiled.set(tableName, key, fieldNameToValue.getKVPrecompiledEntry()));
    }

    public Map<String, String> get(String tableName, String key) throws ContractException {
        try {
            Tuple2<Boolean, KVTablePrecompiled.Entry> getResult =
                    kvTablePrecompiled.get(tableName, key);
            if (!getResult.getValue1()) {
                throw new ContractException("get from " + tableName + " failed, return false.");
            }
            return parseGetResult(getResult.getValue2());
        } catch (ContractException e) {
            throw ReceiptParser.parseExceptionCall(e);
        }
    }

    public static Map<String, String> parseGetResult(KVTablePrecompiled.Entry getResult) {
        Map<String, String> result = new HashMap<>();
        for (KVTablePrecompiled.KVField kvField : getResult.fields.getValue()) {
            result.put(kvField.key, kvField.value);
        }
        return result;
    }

    public Map<String, String> desc(String tableName) throws ContractException {
        Tuple2<String, String> descOutput =
                kvTablePrecompiled.getDescOutput(kvTablePrecompiled.desc(tableName));
        Map<String, String> tableDesc = new HashMap<>();
        tableDesc.put(PrecompiledConstant.KEY_FIELD_NAME, descOutput.getValue1());
        tableDesc.put(PrecompiledConstant.VALUE_FIELD_NAME, descOutput.getValue2());
        return tableDesc;
    }

    public void asyncSet(String tableName, String key, Entry entry, PrecompiledCallback callback) {
        this.kvTablePrecompiled.set(
                tableName,
                key,
                entry.getKVPrecompiledEntry(),
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
