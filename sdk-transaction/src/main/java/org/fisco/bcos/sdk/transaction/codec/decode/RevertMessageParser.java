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
package org.fisco.bcos.sdk.transaction.codec.decode;

/*
pragma solidity ^0.4.25;
contract Revert {
    function Error(string memory s) public {}
}

"08c379a0": "Error(string)" // Not SM Method
"c703cb12": "Error(string)" // SM Method
*/

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.codec.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.codec.datatypes.Function;
import org.fisco.bcos.sdk.codec.datatypes.Type;
import org.fisco.bcos.sdk.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.utils.Numeric;
import org.fisco.bcos.sdk.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RevertMessageParser {

    private static final Logger logger = LoggerFactory.getLogger(RevertMessageParser.class);

    public static final String RevertMethod = "08c379a0";
    public static final String RevertMethodWithHexPrefix = "0x08c379a0";

    public static final String SMRevertMethod = "c703cb12";
    public static final String SMRevertMethodWithHexPrefix = "0xc703cb12";

    // Error(String)
    public static final Function revertFunction =
            new Function(
                    "Error",
                    Collections.<Type>emptyList(),
                    Collections.singletonList(new TypeReference<Utf8String>() {}));

    /**
     * Does output start with the code of the Revert method, If so, the output may be error message
     *
     * @param output the string of output
     * @return true/false
     */
    public static boolean isOutputStartWithRevertMethod(String output) {
        return output.startsWith(RevertMethodWithHexPrefix)
                || output.startsWith(SMRevertMethodWithHexPrefix)
                || (output.startsWith(RevertMethod) || output.startsWith(SMRevertMethod));
    }

    /**
     * @param status the status of the receipt
     * @param output the output of the receipt
     * @return true/false
     */
    public static boolean hasRevertMessage(String status, String output) {
        if (StringUtils.isEmpty(status) || StringUtils.isEmpty(output)) {
            return false;
        }
        try {
            BigInteger statusQuantity = Numeric.decodeQuantity(status);
            return !BigInteger.ZERO.equals(statusQuantity) && isOutputStartWithRevertMethod(output);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param status the transaction receipt status
     * @param output the output of the transaction receipt
     * @return the resolved revert message information
     */
    public static Tuple2<Boolean, String> tryResolveRevertMessage(String status, String output) {
        if (!hasRevertMessage(status, output)) {
            return new Tuple2<>(false, null);
        }

        try {
            String rawOutput =
                    Numeric.containsHexPrefix(output)
                            ? output.substring(RevertMethodWithHexPrefix.length())
                            : output.substring(RevertMethod.length());
            // I AM LAZY!
            FunctionReturnDecoder functionReturnDecoder = new FunctionReturnDecoder();
            List<Type> result =
                    functionReturnDecoder.decode(rawOutput, revertFunction.getOutputParameters());
            if (result.get(0) instanceof Utf8String) {
                String message = ((Utf8String) result.get(0)).getValue();
                if (logger.isDebugEnabled()) {
                    logger.debug(" ABI: {} , RevertMessage: {}", output, message);
                }
                return new Tuple2<>(true, message);
            }
        } catch (Exception e) {
            logger.warn(" ABI: {}, e: {}", output, e);
        }

        return new Tuple2<>(false, null);
    }

    /**
     * parse revert message from receipt
     *
     * @param receipt the receipt need to be parsed
     * @return the resolved revert message information
     */
    public static Tuple2<Boolean, String> tryResolveRevertMessage(TransactionReceipt receipt) {
        return tryResolveRevertMessage(receipt.getStatus().toString(), receipt.getOutput());
    }
}
