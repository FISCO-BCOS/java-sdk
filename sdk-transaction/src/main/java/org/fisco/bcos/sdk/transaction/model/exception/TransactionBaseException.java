/*
 * Copyright 2012-2019 the original author or authors.
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
 */
package org.fisco.bcos.sdk.transaction.model.exception;

import org.fisco.bcos.sdk.model.RetCode;

/** BaseException. */
public class TransactionBaseException extends Exception {

    private static final long serialVersionUID = 1L;
    private RetCode retCode;

    public TransactionBaseException(RetCode retCode) {
        super(retCode.getMessage());
        this.retCode = retCode;
    }

    public TransactionBaseException(int code, String msg) {
        super(msg);
        this.retCode = new RetCode(code, msg);
    }

    public RetCode getRetCode() {
        return retCode;
    }
}
