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
package org.fisco.bcos.sdk.v3.transaction.model.exception;

import org.fisco.bcos.sdk.v3.model.RetCode;

/**
 * TransactionRetCode @Description: TransactionRetCode
 *
 * @author maojiayu
 */
public class TransactionRetCodeConstants {

    public static final RetCode CODE_SUCCESS = new RetCode(0, "Success");

    // internal error(for example: params check failed, etc.): -22299~-22400
    public static final RetCode NO_SUCH_BINARY_FILE =
            new RetCode(-22301, "No such binary file in config path.");
    public static final RetCode NO_SUCH_ABI_FILE =
            new RetCode(-22302, "No such abi file in config path.");
}
