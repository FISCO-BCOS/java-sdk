package org.fisco.bcos.sdk.v3.transaction.model.exception;

import org.junit.Assert;
import org.junit.Test;
import java.math.BigInteger;
import java.util.Optional;

public class TransactionExceptionTest {

    @Test
    public void testConstructorWithMessage() {
        String message = "Test exception message";
        TransactionException exception = new TransactionException(message);
        
        Assert.assertEquals(message, exception.getMessage());
        Assert.assertFalse(exception.getTransactionHash().isPresent());
    }

    @Test
    public void testConstructorWithMessageAndHash() {
        String message = "Test exception message";
        String hash = "0x123456";
        
        TransactionException exception = new TransactionException(message, hash);
        
        Assert.assertEquals(message, exception.getMessage());
        Assert.assertTrue(exception.getTransactionHash().isPresent());
        Assert.assertEquals(hash, exception.getTransactionHash().get());
    }

    @Test
    public void testConstructorWithMessageAndStatus() {
        String message = "Test exception message";
        int status = 100;
        
        TransactionException exception = new TransactionException(message, status);
        
        Assert.assertEquals(message, exception.getMessage());
        Assert.assertEquals(status, exception.getStatus());
    }

    @Test
    public void testConstructorWithAllParameters() {
        String message = "Test exception message";
        int status = 200;
        BigInteger gasUsed = BigInteger.valueOf(21000);
        String hash = "0xabcdef";
        
        TransactionException exception = new TransactionException(message, status, gasUsed, hash);
        
        Assert.assertEquals(message, exception.getMessage());
        Assert.assertEquals(status, exception.getStatus());
        Assert.assertEquals(gasUsed, exception.getGasUsed());
        Assert.assertTrue(exception.getTransactionHash().isPresent());
        Assert.assertEquals(hash, exception.getTransactionHash().get());
    }

    @Test
    public void testConstructorWithCause() {
        Throwable cause = new RuntimeException("Root cause");
        
        TransactionException exception = new TransactionException(cause);
        
        Assert.assertEquals(cause, exception.getCause());
    }

    @Test
    public void testSetStatus() {
        TransactionException exception = new TransactionException("Test");
        exception.setStatus(300);
        
        Assert.assertEquals(300, exception.getStatus());
    }

    @Test
    public void testSetGasUsed() {
        TransactionException exception = new TransactionException("Test");
        BigInteger gasUsed = BigInteger.valueOf(50000);
        exception.setGasUsed(gasUsed);
        
        Assert.assertEquals(gasUsed, exception.getGasUsed());
    }

    @Test
    public void testSetTransactionHash() {
        TransactionException exception = new TransactionException("Test");
        String hash = "0x999999";
        exception.setTransactionHash(Optional.of(hash));
        
        Assert.assertTrue(exception.getTransactionHash().isPresent());
        Assert.assertEquals(hash, exception.getTransactionHash().get());
    }

    @Test
    public void testSetTransactionHashWithNull() {
        TransactionException exception = new TransactionException("Test", "0x123");
        exception.setTransactionHash(Optional.empty());
        
        Assert.assertFalse(exception.getTransactionHash().isPresent());
    }

    @Test
    public void testConstructorWithNullHash() {
        TransactionException exception = new TransactionException("Test", (String) null);
        
        Assert.assertFalse(exception.getTransactionHash().isPresent());
    }
}
