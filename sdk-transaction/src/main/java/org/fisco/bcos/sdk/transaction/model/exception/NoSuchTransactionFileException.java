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
package org.fisco.bcos.sdk.transaction.model.exception;

import org.fisco.bcos.sdk.model.RetCode;

/**
 * NoSuchTransactionFileException @Description: NoSuchTransactionFileException
 *
 * @author maojiayu
 * @data Sep 15, 2020 9:11:01 PM
 */
public class NoSuchTransactionFileException extends TransactionBaseException {
    private static final long serialVersionUID = -3082997842343754327L;

    public NoSuchTransactionFileException(int code, String msg) {
        super(code, msg);
    }

    public NoSuchTransactionFileException(RetCode retCode) {
        this(retCode.getCode(), retCode.getMessage());
    }
}
