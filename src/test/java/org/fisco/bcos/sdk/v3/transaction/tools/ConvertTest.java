package org.fisco.bcos.sdk.v3.transaction.tools;

import org.junit.Assert;
import org.junit.Test;
import java.math.BigDecimal;

public class ConvertTest {

    @Test
    public void testFromWeiToEther() {
        String weiValue = "1000000000000000000";
        BigDecimal result = Convert.fromWei(weiValue, Convert.Unit.ETHER);
        
        Assert.assertEquals(new BigDecimal("1"), result);
    }

    @Test
    public void testFromWeiToGwei() {
        BigDecimal weiValue = new BigDecimal("1000000000");
        BigDecimal result = Convert.fromWei(weiValue, Convert.Unit.GWEI);
        
        Assert.assertEquals(new BigDecimal("1"), result);
    }

    @Test
    public void testFromWeiToWei() {
        String weiValue = "100";
        BigDecimal result = Convert.fromWei(weiValue, Convert.Unit.WEI);
        
        Assert.assertEquals(new BigDecimal("100"), result);
    }

    @Test
    public void testToWeiFromEther() {
        String etherValue = "1";
        BigDecimal result = Convert.toWei(etherValue, Convert.Unit.ETHER);
        
        Assert.assertEquals(new BigDecimal("1000000000000000000"), result);
    }

    @Test
    public void testToWeiFromGwei() {
        BigDecimal gweiValue = new BigDecimal("1");
        BigDecimal result = Convert.toWei(gweiValue, Convert.Unit.GWEI);
        
        Assert.assertEquals(new BigDecimal("1000000000"), result);
    }

    @Test
    public void testToWeiFromWei() {
        String weiValue = "100";
        BigDecimal result = Convert.toWei(weiValue, Convert.Unit.WEI);
        
        Assert.assertEquals(new BigDecimal("100"), result);
    }

    @Test
    public void testConversionRoundTrip() {
        String originalEther = "2.5";
        BigDecimal wei = Convert.toWei(originalEther, Convert.Unit.ETHER);
        BigDecimal backToEther = Convert.fromWei(wei, Convert.Unit.ETHER);
        
        Assert.assertEquals(new BigDecimal("2.5"), backToEther);
    }

    @Test
    public void testAllUnits() {
        BigDecimal weiValue = new BigDecimal("1000000000000000000");
        
        Assert.assertEquals(new BigDecimal("1"), Convert.fromWei(weiValue, Convert.Unit.ETHER));
        Assert.assertEquals(new BigDecimal("1000"), Convert.fromWei(weiValue, Convert.Unit.FINNEY));
        Assert.assertEquals(new BigDecimal("1000000"), Convert.fromWei(weiValue, Convert.Unit.SZABO));
        Assert.assertEquals(new BigDecimal("1000000000"), Convert.fromWei(weiValue, Convert.Unit.GWEI));
    }

    @Test
    public void testUnitGetWeiFactor() {
        Assert.assertEquals(BigDecimal.ONE, Convert.Unit.WEI.getWeiFactor());
        Assert.assertEquals(new BigDecimal("1000000000"), Convert.Unit.GWEI.getWeiFactor());
        Assert.assertEquals(new BigDecimal("1000000000000000000"), Convert.Unit.ETHER.getWeiFactor());
    }

    @Test
    public void testUnitToString() {
        Assert.assertEquals("wei", Convert.Unit.WEI.toString());
        Assert.assertEquals("gwei", Convert.Unit.GWEI.toString());
        Assert.assertEquals("ether", Convert.Unit.ETHER.toString());
    }

    @Test
    public void testUnitFromString() {
        Assert.assertEquals(Convert.Unit.WEI, Convert.Unit.fromString("wei"));
        Assert.assertEquals(Convert.Unit.GWEI, Convert.Unit.fromString("gwei"));
        Assert.assertEquals(Convert.Unit.ETHER, Convert.Unit.fromString("ether"));
    }

    @Test
    public void testUnitFromStringCaseInsensitive() {
        Assert.assertEquals(Convert.Unit.ETHER, Convert.Unit.fromString("ETHER"));
        Assert.assertEquals(Convert.Unit.GWEI, Convert.Unit.fromString("Gwei"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnitFromStringInvalid() {
        Convert.Unit.fromString("invalid");
    }

    @Test
    public void testFromWeiWithKwei() {
        BigDecimal weiValue = new BigDecimal("1000");
        BigDecimal result = Convert.fromWei(weiValue, Convert.Unit.KWEI);
        
        Assert.assertEquals(new BigDecimal("1"), result);
    }

    @Test
    public void testFromWeiWithMwei() {
        BigDecimal weiValue = new BigDecimal("1000000");
        BigDecimal result = Convert.fromWei(weiValue, Convert.Unit.MWEI);
        
        Assert.assertEquals(new BigDecimal("1"), result);
    }

    @Test
    public void testFromWeiWithKether() {
        BigDecimal weiValue = new BigDecimal("1000000000000000000000");
        BigDecimal result = Convert.fromWei(weiValue, Convert.Unit.KETHER);
        
        Assert.assertEquals(new BigDecimal("1"), result);
    }
}
