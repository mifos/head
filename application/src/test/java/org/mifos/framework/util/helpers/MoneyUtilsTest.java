package org.mifos.framework.util.helpers;

import java.math.BigDecimal;

import org.junit.Test;
import org.mifos.framework.TestUtils;
import org.testng.Assert;

public class MoneyUtilsTest {
    
    @Test
    public void testCreatMoneyWithBigDecimal() {
        Money m = MoneyUtils.createMoney(TestUtils.EURO, new BigDecimal("2.0"));
        Assert.assertEquals(m.getAmount(), new BigDecimal("2.0000000000000"));
        Assert.assertEquals(m.getCurrency(), TestUtils.EURO);
    }

    @Test
    public void testCreatMoneyWithBigDouble() {
        Money m = MoneyUtils.createMoney(TestUtils.EURO, 454.456974123578441d);
        Assert.assertEquals(m.getAmount(), new BigDecimal("454.4569741235785"));
        Assert.assertEquals(m.getAmountDoubleValue(), 454.4569741235785d);
        Assert.assertEquals(m.getAmount().doubleValue(), 454.4569741235785d);
        Assert.assertEquals(m.getCurrency(), TestUtils.EURO);
    }

    @Test
    public void testRoundToCurrencyPrecision(){
        Money m = MoneyUtils.createMoney(TestUtils.EURO, 454.54);
        Assert.assertEquals(MoneyUtils.currencyRound(m), new Money(TestUtils.EURO, "454.5"));
        m = MoneyUtils.createMoney(TestUtils.EURO, 454.554);
        Assert.assertEquals(MoneyUtils.currencyRound(m), new Money(TestUtils.EURO, "454.6"));
    }
    
    @Test
    public void testInitialRoundedAmount(){
        Money m = MoneyUtils.createMoney(TestUtils.EURO, 454.44);
        Assert.assertEquals(MoneyUtils.initialRound(m), new Money(TestUtils.EURO, "454.0"));
        m = MoneyUtils.createMoney(TestUtils.EURO, 454.554);
        Assert.assertEquals(MoneyUtils.initialRound(m), new Money(TestUtils.EURO, "455.0"));
    }
    
    @Test
    public void testFinalRoundedAmount(){
        Money m = MoneyUtils.createMoney(TestUtils.EURO, 454.00);
        Assert.assertEquals(MoneyUtils.finalRound(m), new Money(TestUtils.EURO, "454.0"));
        m = MoneyUtils.createMoney(TestUtils.EURO, 454.0001);
        Assert.assertEquals(MoneyUtils.finalRound(m), new Money(TestUtils.EURO, "455.0"));
    }
}
