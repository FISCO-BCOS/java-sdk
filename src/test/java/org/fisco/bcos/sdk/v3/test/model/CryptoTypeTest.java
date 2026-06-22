package org.fisco.bcos.sdk.v3.test.model;

import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.junit.Assert;
import org.junit.Test;

public class CryptoTypeTest {

    @Test
    public void testEcdsaType() {
        Assert.assertEquals(0, CryptoType.ECDSA_TYPE);
    }

    @Test
    public void testSmType() {
        Assert.assertEquals(1, CryptoType.SM_TYPE);
    }

    @Test
    public void testEd25519VrfType() {
        Assert.assertEquals(2, CryptoType.ED25519_VRF_TYPE);
    }

    @Test
    public void testHsmType() {
        Assert.assertEquals(3, CryptoType.HSM_TYPE);
    }

    @Test
    public void testAllTypesAreDifferent() {
        Assert.assertNotEquals(CryptoType.ECDSA_TYPE, CryptoType.SM_TYPE);
        Assert.assertNotEquals(CryptoType.ECDSA_TYPE, CryptoType.ED25519_VRF_TYPE);
        Assert.assertNotEquals(CryptoType.ECDSA_TYPE, CryptoType.HSM_TYPE);
        Assert.assertNotEquals(CryptoType.SM_TYPE, CryptoType.ED25519_VRF_TYPE);
        Assert.assertNotEquals(CryptoType.SM_TYPE, CryptoType.HSM_TYPE);
        Assert.assertNotEquals(CryptoType.ED25519_VRF_TYPE, CryptoType.HSM_TYPE);
    }

    @Test
    public void testConstantsAreNonNegative() {
        Assert.assertTrue(CryptoType.ECDSA_TYPE >= 0);
        Assert.assertTrue(CryptoType.SM_TYPE >= 0);
        Assert.assertTrue(CryptoType.ED25519_VRF_TYPE >= 0);
        Assert.assertTrue(CryptoType.HSM_TYPE >= 0);
    }
}
