package org.fisco.bcos.sdk.tx.tools;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.fisco.bcos.sdk.codec.Utils;
import org.fisco.bcos.sdk.codec.datatypes.generated.Uint256;
import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void utilsExtTest() {
        Class destType = Uint256.class;
        List input = new ArrayList();
        input.add(BigInteger.ONE);
        List output = Utils.typeMap(input, destType);
        Assert.assertEquals(new Uint256(BigInteger.ONE), output.get(0));
    }
}
