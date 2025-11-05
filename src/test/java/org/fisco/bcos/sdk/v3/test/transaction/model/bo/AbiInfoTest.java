package org.fisco.bcos.sdk.v3.test.transaction.model.bo;

import org.fisco.bcos.sdk.v3.transaction.model.bo.AbiInfo;
import org.junit.Assert;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIDefinition;

public class AbiInfoTest {

    @Test
    public void testConstructor() {
        Map<String, List<ABIDefinition>> funcAbis = new HashMap<>();
        Map<String, ABIDefinition> constructorAbis = new HashMap<>();
        
        AbiInfo abiInfo = new AbiInfo(funcAbis, constructorAbis);
        
        Assert.assertNotNull(abiInfo);
    }

    @Test
    public void testFindFuncAbis() {
        Map<String, List<ABIDefinition>> funcAbis = new HashMap<>();
        List<ABIDefinition> abis = new ArrayList<>();
        funcAbis.put("Contract1", abis);
        
        Map<String, ABIDefinition> constructorAbis = new HashMap<>();
        
        AbiInfo abiInfo = new AbiInfo(funcAbis, constructorAbis);
        
        List<ABIDefinition> result = abiInfo.findFuncAbis("Contract1");
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

    @Test(expected = RuntimeException.class)
    public void testFindFuncAbisWithNonExistentContract() {
        Map<String, List<ABIDefinition>> funcAbis = new HashMap<>();
        Map<String, ABIDefinition> constructorAbis = new HashMap<>();
        
        AbiInfo abiInfo = new AbiInfo(funcAbis, constructorAbis);
        
        abiInfo.findFuncAbis("NonExistent");
    }

    @Test
    public void testFindConstructor() {
        Map<String, List<ABIDefinition>> funcAbis = new HashMap<>();
        Map<String, ABIDefinition> constructorAbis = new HashMap<>();
        
        ABIDefinition constructor = new ABIDefinition();
        constructorAbis.put("Contract1", constructor);
        
        AbiInfo abiInfo = new AbiInfo(funcAbis, constructorAbis);
        
        ABIDefinition result = abiInfo.findConstructor("Contract1");
        Assert.assertEquals(constructor, result);
    }

    @Test
    public void testFindConstructorWithNonExistent() {
        Map<String, List<ABIDefinition>> funcAbis = new HashMap<>();
        Map<String, ABIDefinition> constructorAbis = new HashMap<>();
        
        AbiInfo abiInfo = new AbiInfo(funcAbis, constructorAbis);
        
        ABIDefinition result = abiInfo.findConstructor("NonExistent");
        Assert.assertNull(result);
    }

    @Test
    public void testFindFuncAbisReturnsUnmodifiableList() {
        Map<String, List<ABIDefinition>> funcAbis = new HashMap<>();
        List<ABIDefinition> abis = new ArrayList<>();
        abis.add(new ABIDefinition());
        funcAbis.put("Contract1", abis);
        
        Map<String, ABIDefinition> constructorAbis = new HashMap<>();
        
        AbiInfo abiInfo = new AbiInfo(funcAbis, constructorAbis);
        
        List<ABIDefinition> result = abiInfo.findFuncAbis("Contract1");
        
        try {
            result.add(new ABIDefinition());
            Assert.fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // Expected
        }
    }
}
