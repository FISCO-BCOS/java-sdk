package org.fisco.bcos.sdk.v3.test.model;

import org.fisco.bcos.sdk.v3.model.MerkleProofUnit;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class MerkleProofUnitTest {

    @Test
    public void testGettersAndSetters() {
        MerkleProofUnit unit = new MerkleProofUnit();
        
        List<String> left = Arrays.asList("left1", "left2");
        unit.setLeft(left);
        Assert.assertEquals(left, unit.getLeft());
        
        List<String> right = Arrays.asList("right1", "right2");
        unit.setRight(right);
        Assert.assertEquals(right, unit.getRight());
    }

    @Test
    public void testEquals() {
        MerkleProofUnit unit1 = new MerkleProofUnit();
        unit1.setLeft(Arrays.asList("a", "b"));
        unit1.setRight(Arrays.asList("c", "d"));

        MerkleProofUnit unit2 = new MerkleProofUnit();
        unit2.setLeft(Arrays.asList("a", "b"));
        unit2.setRight(Arrays.asList("c", "d"));

        MerkleProofUnit unit3 = new MerkleProofUnit();
        unit3.setLeft(Arrays.asList("x", "y"));
        unit3.setRight(Arrays.asList("c", "d"));

        // Test equality
        Assert.assertEquals(unit1, unit2);
        
        // Test same object
        Assert.assertEquals(unit1, unit1);
        
        // Test different left
        Assert.assertNotEquals(unit1, unit3);
        
        // Test null
        Assert.assertNotEquals(unit1, null);
        
        // Test different class
        Assert.assertNotEquals(unit1, "string");
    }

    @Test
    public void testHashCode() {
        MerkleProofUnit unit1 = new MerkleProofUnit();
        unit1.setLeft(Arrays.asList("a", "b"));
        unit1.setRight(Arrays.asList("c", "d"));

        MerkleProofUnit unit2 = new MerkleProofUnit();
        unit2.setLeft(Arrays.asList("a", "b"));
        unit2.setRight(Arrays.asList("c", "d"));

        Assert.assertEquals(unit1.hashCode(), unit2.hashCode());
    }

    @Test
    public void testToString() {
        MerkleProofUnit unit = new MerkleProofUnit();
        unit.setLeft(Arrays.asList("left1"));
        unit.setRight(Arrays.asList("right1"));
        
        String result = unit.toString();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("left1"));
        Assert.assertTrue(result.contains("right1"));
        Assert.assertTrue(result.contains("MerkleProofUnit"));
    }

    @Test
    public void testNullLists() {
        MerkleProofUnit unit1 = new MerkleProofUnit();
        MerkleProofUnit unit2 = new MerkleProofUnit();
        
        Assert.assertEquals(unit1, unit2);
        Assert.assertEquals(unit1.hashCode(), unit2.hashCode());
    }

    @Test
    public void testEmptyLists() {
        MerkleProofUnit unit = new MerkleProofUnit();
        unit.setLeft(Arrays.asList());
        unit.setRight(Arrays.asList());
        
        Assert.assertNotNull(unit.getLeft());
        Assert.assertNotNull(unit.getRight());
        Assert.assertTrue(unit.getLeft().isEmpty());
        Assert.assertTrue(unit.getRight().isEmpty());
    }
}
