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
package org.fisco.bcos.sdk.transaction.tools;

import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.Function;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.abi.datatypes.Utf8String;

/**
 * ReceiptStatusUtil @Description: ReceiptStatusUtil
 *
 * @author maojiayu
 */
public class ReceiptStatusUtil {

    /**
     * parse transaction receipt output and get return message.
     *
     * @param output @See transaction receipt output
     * @return receipt message
     */
    public static String decodeReceiptMessage(String output) {
        if (output.length() <= 10) {
            return null;
        } else {
            Function function =
                    new Function(
                            "Error",
                            Collections.emptyList(),
                            Collections.singletonList(new TypeReference<Utf8String>() {}));
            List<Type> r =
                    FunctionReturnDecoder.decode(
                            output.substring(10), function.getOutputParameters());
            return ((Type) r.get(0)).toString();
        }
    }
}
