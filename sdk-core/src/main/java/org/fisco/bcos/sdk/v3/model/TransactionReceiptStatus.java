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
package org.fisco.bcos.sdk.v3.model;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class TransactionReceiptStatus {
    public static final RetCode Success = new RetCode(0, "Success");
    public static final RetCode Unknown = new RetCode(1, "Unknown");
    public static final RetCode BadRLP = new RetCode(2, "OutOfGasLimit");
    public static final RetCode NotEnoughCash = new RetCode(7, "Not enough cash");
    public static final RetCode BadInstruction = new RetCode(10, "Bad instruction");
    public static final RetCode BadJumpDestination = new RetCode(11, "Bad jump destination");
    public static final RetCode OutOfGas = new RetCode(12, "Out-of-gas during VM execution");
    public static final RetCode OutOfStack = new RetCode(13, "Out of stack");
    public static final RetCode StackUnderflow = new RetCode(14, "Stack underflow");
    public static final RetCode PrecompiledError = new RetCode(15, "Precompiled error");
    public static final RetCode RevertInstruction = new RetCode(16, "Revert instruction");
    public static final RetCode ContractAddressAlreadyUsed =
            new RetCode(17, "ContractAddressAlreadyUsed");
    public static final RetCode PermissionDenied = new RetCode(18, "Permission denied");
    public static final RetCode CallAddressError = new RetCode(19, "Call address error");
    public static final RetCode GasOverflow = new RetCode(20, "GasOverflow");
    public static final RetCode ContractFrozen = new RetCode(21, "ContractFrozen");
    public static final RetCode AccountFrozen = new RetCode(22, "AccountFrozen");
    public static final RetCode WASMValidationFailure =
            new RetCode(32, "WASMValidationFailure, check the bin file is valid");
    public static final RetCode WASMArgumentOutOfRange = new RetCode(33, "WASMArgumentOutOfRange");
    public static final RetCode WASMUnreachableInstruction =
            new RetCode(34, "WASMUnreachableInstruction");
    public static final RetCode WASMTrap = new RetCode(35, "WASMTrap");
    public static final RetCode NonceCheckFail = new RetCode(1000, "NonceCheckFail");
    public static final RetCode BlockLimitCheckFail = new RetCode(1001, "BlockLimitCheckFail");
    public static final RetCode TxPoolIsFull = new RetCode(1002, "TxPoolIsFull");
    public static final RetCode Malform = new RetCode(1003, "Malform tx");
    public static final RetCode AlreadyInTxPool = new RetCode(1004, "AlreadyInTxPool");
    public static final RetCode TxAlreadyInChain = new RetCode(1005, "TxAlreadyInChain");
    public static final RetCode InvalidChainId = new RetCode(1006, "InvalidChainId");
    public static final RetCode InvalidGroupId = new RetCode(1007, "InvalidGroupId");
    public static final RetCode InvalidSignature = new RetCode(1008, "InvalidSignature");
    public static final RetCode RequestNotBelongToTheGroup =
            new RetCode(1009, "RequestNotBelongToTheGroup");
    public static final RetCode TimeOut = new RetCode(50001, "Transaction receipt timeout");

    protected static Map<Integer, RetCode> codeToRetCode = new HashMap<>();

    static {
        Field[] fields = TransactionReceiptStatus.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().equals(RetCode.class)) {
                try {
                    RetCode constantRetCode = (RetCode) field.get(null);
                    codeToRetCode.put(constantRetCode.getCode(), constantRetCode);
                } catch (IllegalAccessException e) {
                    continue;
                }
            }
        }
    }

    public static RetCode getStatusMessage(Integer status, String message) {
        if (codeToRetCode.containsKey(status)) {
            return codeToRetCode.get(status);
        }
        return new RetCode(status, message);
    }
}
