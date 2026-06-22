package org.fisco.bcos.sdk.v3.test.utils.exceptions;

import org.fisco.bcos.sdk.v3.utils.exceptions.DecoderException;
import org.fisco.bcos.sdk.v3.utils.exceptions.EncoderException;
import org.fisco.bcos.sdk.v3.utils.exceptions.MessageDecodingException;
import org.fisco.bcos.sdk.v3.utils.exceptions.MessageEncodingException;
import org.junit.Assert;
import org.junit.Test;

public class ExceptionsTest {

    @Test
    public void testDecoderException() {
        Throwable cause = new RuntimeException("Root cause");
        DecoderException exception = new DecoderException("Decoding failed", cause);

        Assert.assertEquals("Decoding failed", exception.getMessage());
        Assert.assertEquals(cause, exception.getCause());
    }

    @Test
    public void testDecoderExceptionNullCause() {
        DecoderException exception = new DecoderException("Decoding failed", null);

        Assert.assertEquals("Decoding failed", exception.getMessage());
        Assert.assertNull(exception.getCause());
    }

    @Test
    public void testEncoderException() {
        Throwable cause = new RuntimeException("Root cause");
        EncoderException exception = new EncoderException("Encoding failed", cause);

        Assert.assertEquals("Encoding failed", exception.getMessage());
        Assert.assertEquals(cause, exception.getCause());
    }

    @Test
    public void testEncoderExceptionNullCause() {
        EncoderException exception = new EncoderException("Encoding failed", null);

        Assert.assertEquals("Encoding failed", exception.getMessage());
        Assert.assertNull(exception.getCause());
    }

    @Test
    public void testMessageDecodingException() {
        MessageDecodingException exception = new MessageDecodingException("Message decode error");

        Assert.assertEquals("Message decode error", exception.getMessage());
        Assert.assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void testMessageEncodingException() {
        MessageEncodingException exception = new MessageEncodingException("Message encode error");

        Assert.assertEquals("Message encode error", exception.getMessage());
        Assert.assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void testMessageDecodingExceptionWithCause() {
        Throwable cause = new IllegalArgumentException("Invalid argument");
        MessageDecodingException exception = new MessageDecodingException("Decode failed", cause);

        Assert.assertEquals("Decode failed", exception.getMessage());
        Assert.assertEquals(cause, exception.getCause());
    }

    @Test
    public void testMessageEncodingExceptionWithCause() {
        Throwable cause = new IllegalArgumentException("Invalid argument");
        MessageEncodingException exception = new MessageEncodingException("Encode failed", cause);

        Assert.assertEquals("Encode failed", exception.getMessage());
        Assert.assertEquals(cause, exception.getCause());
    }
}

