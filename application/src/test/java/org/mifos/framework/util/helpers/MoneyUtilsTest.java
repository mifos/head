package org.mifos.framework.util.helpers;

import java.math.BigDecimal;

import org.junit.Test;
import org.mifos.config.AccountingRules;
import org.mifos.core.CurrencyMismatchException;
import org.mifos.framework.TestUtils;
import org.testng.Assert;

public class MoneyUtilsTest {

    @Test
    public void testAdd() {
        Assert.assertNull(MoneyUtils.add(null, null));
        Assert.assertNotNull(MoneyUtils.add(new Money(TestUtils.EURO), null));
        Assert.assertNotNull(MoneyUtils.add(null, new Money(TestUtils.EURO)));
        Assert.assertNotNull(MoneyUtils.add(new Money(TestUtils.EURO), new Money(TestUtils.EURO)));
    }

    @Test(expected = CurrencyMismatchException.class)
    public void testAddWithDifferentCurrencies() {
        MoneyUtils.add(new Money(TestUtils.EURO), new Money(TestUtils.RUPEE));
    }
    
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
        Assert.assertEquals(MoneyUtils.roundToCurrencyPrecision(m), new Money(TestUtils.EURO, "454.5"));
        m = MoneyUtils.createMoney(TestUtils.EURO, 454.554);
        Assert.assertEquals(MoneyUtils.roundToCurrencyPrecision(m), new Money(TestUtils.EURO, "454.6"));
    }
    
    @Test
    public void testGetAmount(){
        BigDecimal m = MoneyUtils.getMoneyAmount(new Money(TestUtils.EURO, "454.544"), AccountingRules.getDigitsAfterDecimal());
        Assert.assertEquals(m, new BigDecimal("454.5"));
        m = MoneyUtils.getMoneyAmount(new Money(TestUtils.EURO, "454.554"), AccountingRules.getDigitsAfterDecimal());
        Assert.assertEquals(m, new BigDecimal("454.6"));
    }
    
    @Test
    public void testGetAmount_RoundToCurrencyPrecision_CurrencyRoundAmount_AreSame() {
        Money m = MoneyUtils.createMoney(TestUtils.EURO, 454.54);
        Assert.assertEquals(MoneyUtils.getMoneyAmount(m, AccountingRules.getDigitsAfterDecimal()), new BigDecimal("454.5"));
        Assert.assertEquals(MoneyUtils.roundToCurrencyPrecision(m), new Money(TestUtils.EURO, "454.5"));
        
        m = MoneyUtils.createMoney(TestUtils.EURO, 454.554);
        Assert.assertEquals(MoneyUtils.getMoneyAmount(m, AccountingRules.getDigitsAfterDecimal()), new BigDecimal("454.6"));
        Assert.assertEquals(MoneyUtils.roundToCurrencyPrecision(m), new Money(TestUtils.EURO, "454.6"));
    }

}
