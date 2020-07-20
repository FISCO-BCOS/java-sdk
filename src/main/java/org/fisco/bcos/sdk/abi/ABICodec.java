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

package org.fisco.bcos.sdk.abi;

import java.util.List;

public class ABICodec {
    String encodeMethod(String ABI, String methodName, List<Object> params)
            throws ABICodecException {
        return null;
    }

    String encodeMethodById(String ABI, String methodId, List<Object> params)
            throws ABICodecException {
        return null;
    }

    String encodeMethodByInterface(String methodInterface, List<Object> params)
            throws ABICodecException {
        return null;
    }

    String encodeMethodFromString(String ABI, String methodName, List<String> params)
            throws ABICodecException {
        return null;
    }

    String encodeMethodByIdFromString(String ABI, String methodId, List<String> params)
            throws ABICodecException {
        return null;
    }

    String encodeMethodByInterfaceFromString(String methodInterface, List<String> params)
            throws ABICodecException {
        return null;
    }

    String encodeEvent(String ABI, String eventName, List<Object> params) throws ABICodecException {
        return null;
    }

    String encodeEventByTopic(String ABI, String eventTopic, List<Object> params)
            throws ABICodecException {
        return null;
    }

    String encodeEventByInterface(String eventSignature, List<Object> params)
            throws ABICodecException {
        return null;
    }

    String encodeEventFromString(String ABI, String eventName, List<String> params)
            throws ABICodecException {
        return null;
    }

    String encodeEventByTopicFromString(String ABI, String eventTopic, List<String> params)
            throws ABICodecException {
        return null;
    }

    String encodeEventByInterfaceFromString(String eventSignature, List<String> params)
            throws ABICodecException {
        return null;
    }

    List<Object> decodeMethod(String ABI, String methodName, String output)
            throws ABICodecException {
        return null;
    }

    List<Object> decodeMethodById(String ABI, String methodId, String output)
            throws ABICodecException {
        return null;
    }

    List<Object> decodeMethodByInterface(String methodSignature, String output)
            throws ABICodecException {
        return null;
    }

    List<String> decodeMethodToString(String ABI, String methodName, String output)
            throws ABICodecException {
        return null;
    }

    List<String> decodeMethodByIdToString(String ABI, String methodId, String output)
            throws ABICodecException {
        return null;
    }

    List<String> decodeMethodByInterfaceToString(String methodSignature, String output)
            throws ABICodecException {
        return null;
    }

    List<Object> decodeEvent(String ABI, String eventName, String output) throws ABICodecException {
        return null;
    }

    List<Object> decodeEventByTopic(String ABI, String eventTopic, String output)
            throws ABICodecException {
        return null;
    }

    List<Object> decodeEventByInterface(String eventSignature, String output)
            throws ABICodecException {
        return null;
    }

    List<String> decodeEventToString(String ABI, String eventName, String output)
            throws ABICodecException {
        return null;
    }

    List<String> decodeEventByTopicToString(String ABI, String eventTopic, String output)
            throws ABICodecException {
        return null;
    }

    List<String> decodeEventByInterfaceToString(String eventSignature, String output)
            throws ABICodecException {
        return null;
    }
}
