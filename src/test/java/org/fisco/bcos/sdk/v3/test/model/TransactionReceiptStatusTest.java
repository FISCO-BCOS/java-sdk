package org.fisco.bcos.sdk.v3.test.model;

import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.model.TransactionReceiptStatus;
import org.junit.Assert;
import org.junit.Test;

public class TransactionReceiptStatusTest {

    @Test
    public void testSuccessStatus() {
        Assert.assertEquals(0, TransactionReceiptStatus.Success.getCode());
        Assert.assertEquals("Success", TransactionReceiptStatus.Success.getMessage());
    }

    @Test
    public void testUnknownStatus() {
        Assert.assertEquals(1, TransactionReceiptStatus.Unknown.getCode());
        Assert.assertEquals("Unknown", TransactionReceiptStatus.Unknown.getMessage());
    }

    @Test
    public void testBadRLPStatus() {
        Assert.assertEquals(2, TransactionReceiptStatus.BadRLP.getCode());
        Assert.assertEquals("OutOfGasLimit", TransactionReceiptStatus.BadRLP.getMessage());
    }

    @Test
    public void testOutOfGasStatus() {
        Assert.assertEquals(12, TransactionReceiptStatus.OutOfGas.getCode());
        Assert.assertEquals("Out-of-gas during VM execution", TransactionReceiptStatus.OutOfGas.getMessage());
    }

    @Test
    public void testPermissionDeniedStatus() {
        Assert.assertEquals(18, TransactionReceiptStatus.PermissionDenied.getCode());
        Assert.assertEquals("Permission denied", TransactionReceiptStatus.PermissionDenied.getMessage());
    }

    @Test
    public void testNonceCheckFailStatus() {
        Assert.assertEquals(10000, TransactionReceiptStatus.NonceCheckFail.getCode());
        Assert.assertEquals("NonceCheckFail", TransactionReceiptStatus.NonceCheckFail.getMessage());
    }

    @Test
    public void testBlockLimitCheckFailStatus() {
        Assert.assertEquals(10001, TransactionReceiptStatus.BlockLimitCheckFail.getCode());
        Assert.assertEquals("BlockLimitCheckFail", TransactionReceiptStatus.BlockLimitCheckFail.getMessage());
    }

    @Test
    public void testTimeOutStatus() {
        Assert.assertEquals(50001, TransactionReceiptStatus.TimeOut.getCode());
        Assert.assertEquals("Transaction receipt timeout", TransactionReceiptStatus.TimeOut.getMessage());
    }

    @Test
    public void testGetStatusMessageForKnownStatus() {
        RetCode retCode = TransactionReceiptStatus.getStatusMessage(0, null);
        Assert.assertEquals(0, retCode.getCode());
        Assert.assertEquals("Success", retCode.getMessage());
    }

    @Test
    public void testGetStatusMessageForUnknownStatus() {
        RetCode retCode = TransactionReceiptStatus.getStatusMessage(99999, "Custom error");
        Assert.assertEquals(99999, retCode.getCode());
        Assert.assertEquals("Custom error", retCode.getMessage());
    }

    @Test
    public void testGetStatusMessageOverridesMessageForKnown() {
        // For known status code, it should return the predefined message
        RetCode retCode = TransactionReceiptStatus.getStatusMessage(0, "Different message");
        Assert.assertEquals(0, retCode.getCode());
        Assert.assertEquals("Success", retCode.getMessage());
    }

    @Test
    public void testAllErrorCodeConstants() {
        // Test some VM execution errors
        Assert.assertNotNull(TransactionReceiptStatus.BadInstruction);
        Assert.assertNotNull(TransactionReceiptStatus.BadJumpDestination);
        Assert.assertNotNull(TransactionReceiptStatus.OutOfStack);
        Assert.assertNotNull(TransactionReceiptStatus.StackUnderflow);
        Assert.assertNotNull(TransactionReceiptStatus.PrecompiledError);
        Assert.assertNotNull(TransactionReceiptStatus.RevertInstruction);

        // Test contract related errors
        Assert.assertNotNull(TransactionReceiptStatus.ContractAddressAlreadyUsed);
        Assert.assertNotNull(TransactionReceiptStatus.ContractFrozen);
        Assert.assertNotNull(TransactionReceiptStatus.ContractAbolished);

        // Test account related errors
        Assert.assertNotNull(TransactionReceiptStatus.AccountFrozen);
        Assert.assertNotNull(TransactionReceiptStatus.AccountAbolished);

        // Test WASM errors
        Assert.assertNotNull(TransactionReceiptStatus.WASMValidationFailure);
        Assert.assertNotNull(TransactionReceiptStatus.WASMArgumentOutOfRange);
        Assert.assertNotNull(TransactionReceiptStatus.WASMUnreachableInstruction);
        Assert.assertNotNull(TransactionReceiptStatus.WASMTrap);

        // Test transaction pool errors
        Assert.assertNotNull(TransactionReceiptStatus.TxPoolIsFull);
        Assert.assertNotNull(TransactionReceiptStatus.AlreadyInTxPool);
        Assert.assertNotNull(TransactionReceiptStatus.TxAlreadyInChain);
        Assert.assertNotNull(TransactionReceiptStatus.InvalidChainId);
        Assert.assertNotNull(TransactionReceiptStatus.InvalidGroupId);
        Assert.assertNotNull(TransactionReceiptStatus.InvalidSignature);
    }

    @Test
    public void testMultipleErrorCodes() {
        RetCode code1 = TransactionReceiptStatus.getStatusMessage(10, null);
        Assert.assertEquals(10, code1.getCode());
        Assert.assertEquals("Bad instruction", code1.getMessage());

        RetCode code2 = TransactionReceiptStatus.getStatusMessage(16, null);
        Assert.assertEquals(16, code2.getCode());
        Assert.assertEquals("Revert instruction", code2.getMessage());

        RetCode code3 = TransactionReceiptStatus.getStatusMessage(10004, null);
        Assert.assertEquals(10004, code3.getCode());
        Assert.assertEquals("AlreadyInTxPool", code3.getMessage());
    }
}

