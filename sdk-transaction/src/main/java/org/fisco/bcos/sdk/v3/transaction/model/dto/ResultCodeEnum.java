/**
 * Copyright 2014-2019 the original author or authors.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fisco.bcos.sdk.v3.transaction.model.dto;

/**
 * ResultCodeEnum @Description: ResultCodeEnum
 *
 * @author maojiayu
 */
public enum ResultCodeEnum {
    SUCCESS(0, "success"),
    EXECUTE_ERROR(1, "execute error"),
    RESULT_EMPTY(2, "empty result"),
    UNKNOWN(3, "unknown exception"),
    EVM_ERROR(4, "evm error"),
    EXCEPTION_OCCUR(5, "exception occur"),
    PARAMETER_ERROR(6, "param error"),
    PARSE_ERROR(7, "parse error");

    private int code;
    private String message;

    ResultCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /** @return the code */
    public int getCode() {
        return code;
    }

    /** @param code the code to set */
    public void setCode(int code) {
        this.code = code;
    }

    /** @return the message */
    public String getMessage() {
        return message;
    }

    /** @param message the message to set */
    public void setMessage(String message) {
        this.message = message;
    }
}
