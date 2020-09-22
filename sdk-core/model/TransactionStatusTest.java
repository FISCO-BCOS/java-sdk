/**
 * Copyright 2014-2020 [fisco-dev]
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
package org.fisco.bcos.sdk.test.model;

import org.fisco.bcos.sdk.model.RetCode;
import org.fisco.bcos.sdk.model.TransactionReceiptStatus;
import org.junit.Assert;
import org.junit.Test;

public class TransactionStatusTest {
    @Test
    public void testGetStatusMessage() {
        testGetStatus(
                TransactionReceiptStatus.Success.getCode(),
                TransactionReceiptStatus.Success.getCode(),
                TransactionReceiptStatus.Success.getMessage());
        testGetStatus(
                TransactionReceiptStatus.Unknown.getCode(),
                TransactionReceiptStatus.Unknown.getCode(),
                TransactionReceiptStatus.Unknown.getMessage());
        testGetStatus(
                TransactionReceiptStatus.BadRLP.getCode(),
                TransactionReceiptStatus.BadRLP.getCode(),
                TransactionReceiptStatus.BadRLP.getMessage());
        testGetStatus(
                TransactionReceiptStatus.InvalidFormat.getCode(),
                TransactionReceiptStatus.InvalidFormat.getCode(),
                TransactionReceiptStatus.InvalidFormat.getMessage());
        testGetStatus(
                TransactionReceiptStatus.OutOfGasIntrinsic.getCode(),
                TransactionReceiptStatus.OutOfGasIntrinsic.getCode(),
                TransactionReceiptStatus.OutOfGasIntrinsic.getMessage());
        testGetStatus(
                TransactionReceiptStatus.InvalidSignature.getCode(),
                TransactionReceiptStatus.InvalidSignature.getCode(),
                TransactionReceiptStatus.InvalidSignature.getMessage());
        testGetStatus(
                TransactionReceiptStatus.InvalidNonce.getCode(),
                TransactionReceiptStatus.InvalidNonce.getCode(),
                TransactionReceiptStatus.InvalidNonce.getMessage());
        testGetStatus(
                TransactionReceiptStatus.NotEnoughCash.getCode(),
                TransactionReceiptStatus.NotEnoughCash.getCode(),
                TransactionReceiptStatus.NotEnoughCash.getMessage());
        testGetStatus(
                TransactionReceiptStatus.OutOfGasBase.getCode(),
                TransactionReceiptStatus.OutOfGasBase.getCode(),
                TransactionReceiptStatus.OutOfGasBase.getMessage());
        testGetStatus(
                TransactionReceiptStatus.BlockGasLimitReached.getCode(),
                TransactionReceiptStatus.BlockGasLimitReached.getCode(),
                TransactionReceiptStatus.BlockGasLimitReached.getMessage());
        testGetStatus(
                TransactionReceiptStatus.BadInstruction.getCode(),
                TransactionReceiptStatus.BadInstruction.getCode(),
                TransactionReceiptStatus.BadInstruction.getMessage());
        testGetStatus(
                TransactionReceiptStatus.BadJumpDestination.getCode(),
                TransactionReceiptStatus.BadJumpDestination.getCode(),
                TransactionReceiptStatus.BadJumpDestination.getMessage());
        testGetStatus(
                TransactionReceiptStatus.OutOfGas.getCode(),
                TransactionReceiptStatus.OutOfGas.getCode(),
                TransactionReceiptStatus.OutOfGas.getMessage());
        testGetStatus(
                TransactionReceiptStatus.OutOfStack.getCode(),
                TransactionReceiptStatus.OutOfStack.getCode(),
                TransactionReceiptStatus.OutOfStack.getMessage());
        testGetStatus(
                TransactionReceiptStatus.StackUnderflow.getCode(),
                TransactionReceiptStatus.StackUnderflow.getCode(),
                TransactionReceiptStatus.StackUnderflow.getMessage());
        testGetStatus(
                TransactionReceiptStatus.NonceCheckFail.getCode(),
                TransactionReceiptStatus.NonceCheckFail.getCode(),
                TransactionReceiptStatus.NonceCheckFail.getMessage());
        testGetStatus(
                TransactionReceiptStatus.BlockLimitCheckFail.getCode(),
                TransactionReceiptStatus.BlockLimitCheckFail.getCode(),
                TransactionReceiptStatus.BlockLimitCheckFail.getMessage());
        testGetStatus(
                TransactionReceiptStatus.FilterCheckFail.getCode(),
                TransactionReceiptStatus.FilterCheckFail.getCode(),
                TransactionReceiptStatus.FilterCheckFail.getMessage());
        testGetStatus(
                TransactionReceiptStatus.NoDeployPermission.getCode(),
                TransactionReceiptStatus.NoDeployPermission.getCode(),
                TransactionReceiptStatus.NoDeployPermission.getMessage());
        testGetStatus(
                TransactionReceiptStatus.NoCallPermission.getCode(),
                TransactionReceiptStatus.NoCallPermission.getCode(),
                TransactionReceiptStatus.NoCallPermission.getMessage());
        testGetStatus(
                TransactionReceiptStatus.NoTxPermission.getCode(),
                TransactionReceiptStatus.NoTxPermission.getCode(),
                TransactionReceiptStatus.NoTxPermission.getMessage());
        testGetStatus(
                TransactionReceiptStatus.PrecompiledError.getCode(),
                TransactionReceiptStatus.PrecompiledError.getCode(),
                TransactionReceiptStatus.PrecompiledError.getMessage());
        testGetStatus(
                TransactionReceiptStatus.RevertInstruction.getCode(),
                TransactionReceiptStatus.RevertInstruction.getCode(),
                TransactionReceiptStatus.RevertInstruction.getMessage());
        testGetStatus(
                TransactionReceiptStatus.InvalidZeroSignatureFormat.getCode(),
                TransactionReceiptStatus.InvalidZeroSignatureFormat.getCode(),
                TransactionReceiptStatus.InvalidZeroSignatureFormat.getMessage());
        testGetStatus(
                TransactionReceiptStatus.AddressAlreadyUsed.getCode(),
                TransactionReceiptStatus.AddressAlreadyUsed.getCode(),
                TransactionReceiptStatus.AddressAlreadyUsed.getMessage());
        testGetStatus(
                TransactionReceiptStatus.PermissionDenied.getCode(),
                TransactionReceiptStatus.PermissionDenied.getCode(),
                TransactionReceiptStatus.PermissionDenied.getMessage());
        testGetStatus(
                TransactionReceiptStatus.CallAddressError.getCode(),
                TransactionReceiptStatus.CallAddressError.getCode(),
                TransactionReceiptStatus.CallAddressError.getMessage());
        testGetStatus(
                TransactionReceiptStatus.GasOverflow.getCode(),
                TransactionReceiptStatus.GasOverflow.getCode(),
                TransactionReceiptStatus.GasOverflow.getMessage());
        testGetStatus(
                TransactionReceiptStatus.TxPoolIsFull.getCode(),
                TransactionReceiptStatus.TxPoolIsFull.getCode(),
                TransactionReceiptStatus.TxPoolIsFull.getMessage());
        testGetStatus(
                TransactionReceiptStatus.TransactionRefused.getCode(),
                TransactionReceiptStatus.TransactionRefused.getCode(),
                TransactionReceiptStatus.TransactionRefused.getMessage());
        testGetStatus(
                TransactionReceiptStatus.AccountFrozen.getCode(),
                TransactionReceiptStatus.AccountFrozen.getCode(),
                TransactionReceiptStatus.AccountFrozen.getMessage());
        testGetStatus(
                TransactionReceiptStatus.AlreadyKnown.getCode(),
                TransactionReceiptStatus.AlreadyKnown.getCode(),
                TransactionReceiptStatus.AlreadyKnown.getMessage());
        testGetStatus(
                TransactionReceiptStatus.ContractFrozen.getCode(),
                TransactionReceiptStatus.ContractFrozen.getCode(),
                TransactionReceiptStatus.ContractFrozen.getMessage());
        testGetStatus(
                TransactionReceiptStatus.AlreadyInChain.getCode(),
                TransactionReceiptStatus.AlreadyInChain.getCode(),
                TransactionReceiptStatus.AlreadyInChain.getMessage());
        testGetStatus(
                TransactionReceiptStatus.InvalidChainId.getCode(),
                TransactionReceiptStatus.InvalidChainId.getCode(),
                TransactionReceiptStatus.InvalidChainId.getMessage());
        testGetStatus(
                TransactionReceiptStatus.InvalidGroupId.getCode(),
                TransactionReceiptStatus.InvalidGroupId.getCode(),
                TransactionReceiptStatus.InvalidGroupId.getMessage());
        testGetStatus(
                TransactionReceiptStatus.RequestNotBelongToTheGroup.getCode(),
                TransactionReceiptStatus.RequestNotBelongToTheGroup.getCode(),
                TransactionReceiptStatus.RequestNotBelongToTheGroup.getMessage());
        testGetStatus(
                TransactionReceiptStatus.MalformedTx.getCode(),
                TransactionReceiptStatus.MalformedTx.getCode(),
                TransactionReceiptStatus.MalformedTx.getMessage());
        testGetStatus(
                TransactionReceiptStatus.OverGroupMemoryLimit.getCode(),
                TransactionReceiptStatus.OverGroupMemoryLimit.getCode(),
                TransactionReceiptStatus.OverGroupMemoryLimit.getMessage());
    }

    private void testGetStatus(int status, int expectedCode, String expectedMessage) {
        RetCode retCode = TransactionReceiptStatus.getStatusMessage(String.valueOf(status), "");
        Assert.assertEquals(expectedCode, retCode.getCode());
        Assert.assertTrue(retCode.getMessage().equals(expectedMessage));
    }
}
