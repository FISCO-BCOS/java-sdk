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
package org.fisco.bcos.sdk.transaction.core.interf.executor;

import org.fisco.bcos.sdk.client.response.BcosTransaction;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;

/**
 * TransactionEncoderInterface
 *
 * @Description: TransactionEncoderInterface
 * @author maojiayu
 * @data Jul 17, 2020 11:59:49 AM
 *
 */
public interface TransactionEncoderInterface {
    
    byte[] encode(BcosTransaction transaction, SignatureResult signature);

}
