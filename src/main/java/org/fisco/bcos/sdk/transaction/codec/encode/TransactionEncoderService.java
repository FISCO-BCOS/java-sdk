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
package org.fisco.bcos.sdk.transaction.codec.encode;

import java.util.ArrayList;
import java.util.List;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.rlp.RlpEncoder;
import org.fisco.bcos.sdk.rlp.RlpList;
import org.fisco.bcos.sdk.rlp.RlpString;
import org.fisco.bcos.sdk.rlp.RlpType;
import org.fisco.bcos.sdk.transaction.model.po.RawTransaction;
import org.fisco.bcos.sdk.utils.Numeric;

public class TransactionEncoderService implements TransactionEncoderInterface {

    @Override
    public byte[] encode(RawTransaction transaction, SignatureResult signature) {
        List<RlpType> values = asRlpValues(transaction, signature);
        RlpList rlpList = new RlpList(values);
        return RlpEncoder.encode(rlpList);
    }

    static List<RlpType> asRlpValues(
            RawTransaction rawTransaction, SignatureResult signatureResult) {
        List<RlpType> result = new ArrayList<>();
        result.add(RlpString.create(rawTransaction.getRandomid()));
        result.add(RlpString.create(rawTransaction.getGasPrice()));
        result.add(RlpString.create(rawTransaction.getGasLimit()));
        result.add(RlpString.create(rawTransaction.getBlockLimit()));
        // an empty to address (contract creation) should not be encoded as a numeric 0 value
        String to = rawTransaction.getTo();
        if (to != null && to.length() > 0) {
            // addresses that start with zeros should be encoded with the zeros included, not
            // as numeric values
            result.add(RlpString.create(Numeric.hexStringToByteArray(to)));
        } else {
            result.add(RlpString.create(""));
        }

        result.add(RlpString.create(rawTransaction.getValue()));

        // value field will already be hex encoded, so we need to convert into binary first
        byte[] data = Numeric.hexStringToByteArray(rawTransaction.getData());
        result.add(RlpString.create(data));

        // add extra data!!!
        result.add(RlpString.create(rawTransaction.getFiscoChainId()));
        result.add(RlpString.create(rawTransaction.getGroupId()));
        if (rawTransaction.getExtraData() == null) {
            result.add(RlpString.create(""));
        } else {
            result.add(
                    RlpString.create(Numeric.hexStringToByteArray(rawTransaction.getExtraData())));
        }
        if (signatureResult != null) {
            throw new RuntimeException("Invalid sinature result, cann't be null");
        }
        return result;
    }
}
