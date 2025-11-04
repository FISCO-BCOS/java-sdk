package org.fisco.bcos.sdk.v3.transaction.gasProvider;

import org.junit.Assert;
import org.junit.Test;
import java.math.BigInteger;

public class EIP1559StructTest {

    @Test
    public void testConstructorAndGetters() {
        BigInteger maxFeePerGas = BigInteger.valueOf(1000);
        BigInteger maxPriorityFeePerGas = BigInteger.valueOf(100);
        BigInteger gasLimit = BigInteger.valueOf(21000);
        
        EIP1559Struct struct = new EIP1559Struct(maxFeePerGas, maxPriorityFeePerGas, gasLimit);
        
        Assert.assertEquals(maxFeePerGas, struct.getMaxFeePerGas());
        Assert.assertEquals(maxPriorityFeePerGas, struct.getMaxPriorityFeePerGas());
        Assert.assertEquals(gasLimit, struct.getGasLimit());
    }

    @Test
    public void testWithZeroValues() {
        BigInteger zero = BigInteger.ZERO;
        
        EIP1559Struct struct = new EIP1559Struct(zero, zero, zero);
        
        Assert.assertEquals(zero, struct.getMaxFeePerGas());
        Assert.assertEquals(zero, struct.getMaxPriorityFeePerGas());
        Assert.assertEquals(zero, struct.getGasLimit());
    }

    @Test
    public void testWithLargeValues() {
        BigInteger largeValue = new BigInteger("999999999999999999999");
        
        EIP1559Struct struct = new EIP1559Struct(largeValue, largeValue, largeValue);
        
        Assert.assertEquals(largeValue, struct.getMaxFeePerGas());
        Assert.assertEquals(largeValue, struct.getMaxPriorityFeePerGas());
        Assert.assertEquals(largeValue, struct.getGasLimit());
    }

    @Test
    public void testWithDifferentValues() {
        BigInteger maxFee = BigInteger.valueOf(5000);
        BigInteger priorityFee = BigInteger.valueOf(1500);
        BigInteger limit = BigInteger.valueOf(50000);
        
        EIP1559Struct struct = new EIP1559Struct(maxFee, priorityFee, limit);
        
        Assert.assertNotEquals(struct.getMaxFeePerGas(), struct.getMaxPriorityFeePerGas());
        Assert.assertNotEquals(struct.getMaxFeePerGas(), struct.getGasLimit());
        Assert.assertNotEquals(struct.getMaxPriorityFeePerGas(), struct.getGasLimit());
    }
}
