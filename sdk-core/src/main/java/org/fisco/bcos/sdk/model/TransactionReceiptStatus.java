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
package org.fisco.bcos.sdk.model;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.fisco.bcos.sdk.utils.Numeric;

public class TransactionReceiptStatus {
    public static final RetCode Success = new RetCode(0, "Success");
    public static final RetCode Unknown = new RetCode(1, "Unknown");
    public static final RetCode BadRLP = new RetCode(2, "Bad RLP");
    public static final RetCode InvalidFormat = new RetCode(3, "Invalid format");
    public static final RetCode OutOfGasIntrinsic =
            new RetCode(4, "The contract to deploy is too long(or input data is too long)");
    public static final RetCode InvalidSignature = new RetCode(5, "Invalid signature");
    public static final RetCode InvalidNonce = new RetCode(6, "Invalid nonce");
    public static final RetCode NotEnoughCash = new RetCode(7, "Not enough cash");
    public static final RetCode OutOfGasBase = new RetCode(8, "Input data is too long");
    public static final RetCode BlockGasLimitReached = new RetCode(9, "Block gas limit reached");
    public static final RetCode BadInstruction = new RetCode(10, "Bad instruction");
    public static final RetCode BadJumpDestination = new RetCode(11, "Bad jump destination");
    public static final RetCode OutOfGas = new RetCode(12, "Out-of-gas during EVM execution");
    public static final RetCode OutOfStack = new RetCode(13, "Out of stack");
    public static final RetCode StackUnderflow = new RetCode(14, "Stack underflow");
    public static final RetCode NonceCheckFail = new RetCode(15, "Nonce check fail");
    public static final RetCode BlockLimitCheckFail = new RetCode(16, "Block limit check fail");
    public static final RetCode FilterCheckFail = new RetCode(17, "Filter check fail");
    public static final RetCode NoDeployPermission = new RetCode(18, "No deploy permission");
    public static final RetCode NoCallPermission = new RetCode(19, "No call permission");
    public static final RetCode NoTxPermission = new RetCode(20, "No tx permission");
    public static final RetCode PrecompiledError = new RetCode(21, "Precompiled error");
    public static final RetCode RevertInstruction = new RetCode(22, "Revert instruction");
    public static final RetCode InvalidZeroSignatureFormat =
            new RetCode(23, "Invalid zero signature format");
    public static final RetCode AddressAlreadyUsed = new RetCode(24, "Address already used");
    public static final RetCode PermissionDenied = new RetCode(25, "Permission denied");
    public static final RetCode CallAddressError = new RetCode(26, "Call address error");
    public static final RetCode GasOverflow = new RetCode(27, "Gas overflow");
    public static final RetCode TxPoolIsFull = new RetCode(28, "Transaction pool is full");
    public static final RetCode TransactionRefused = new RetCode(29, "Transaction refused");
    public static final RetCode ContractFrozen = new RetCode(30, "The contract has been frozen");
    public static final RetCode AccountFrozen = new RetCode(31, "The account has been frozen");
    public static final RetCode NotEnoughRemainGas =
            new RetCode(32, "The remain gas of the account is less than the required gas");

    public static final RetCode AlreadyKnown = new RetCode(10000, "Transaction already known");
    public static final RetCode AlreadyInChain = new RetCode(10001, "Transaction already in chain");
    public static final RetCode InvalidChainId = new RetCode(10002, "Invalid chain id");
    public static final RetCode InvalidGroupId = new RetCode(10003, "Invalid group id");
    public static final RetCode RequestNotBelongToTheGroup =
            new RetCode(10004, "The request doesn't belong to the group");
    public static final RetCode MalformedTx = new RetCode(10005, "Malformed transaction");
    public static final RetCode OverGroupMemoryLimit =
            new RetCode(10006, "Exceeded the group transaction pool capacity limit");
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

    public static RetCode getStatusMessage(String status, String message) {
        int statusCode = Numeric.decodeQuantity(status).intValue();
        if (codeToRetCode.containsKey(statusCode)) {
            return codeToRetCode.get(statusCode);
        }
        return new RetCode(statusCode, message);
    }
}
