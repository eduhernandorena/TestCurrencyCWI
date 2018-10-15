package br.com.cwi.eduardo;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeParseException;

public class MainTest extends Main {
    
    @Test(expected = DateTimeParseException.class)
    public void testCurrencyQuotationQuotationParse() {
        Main m = new Main();
        BigDecimal result = m.currencyQuotation("CAD", "AUD", 15, "27/10/20200");
        Assert.assertNull(result);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCurrencyQuotationWithoutValue() {
        Main m = new Main();
        BigDecimal result = m.currencyQuotation("CAD", "AUD", null, "11/10/2018");
        Assert.assertNull(result);
    }
    
    @Test(expected = NumberFormatException.class)
    public void testCurrencyQuotationNegativeValue() {
        Main m = new Main();
        BigDecimal result = m.currencyQuotation("CAD", "AUD", -5, "11/10/2018");
        Assert.assertNull(result);
    }
    
    @Test(expected = NullPointerException.class)
    public void testCurrencyQuotationWithoutQuotation() {
        Main m = new Main();
        BigDecimal result = m.currencyQuotation("CAD", "AUD", 15, null);
        Assert.assertNull(result);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCurrencyQuotationWithoutFrom() {
        Main m = new Main();
        BigDecimal result = m.currencyQuotation(null, "AUD", 15, "11/10/2018");
        Assert.assertNull(result);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCurrencyQuotationWithoutTo() {
        Main m = new Main();
        BigDecimal result = m.currencyQuotation("CAD", null, 15, "11/10/2018");
        Assert.assertNull(result);
    }
    
    @Test
    public void testCurrencyQuotation() {
        Main m = new Main();
        BigDecimal result = m.currencyQuotation("CAD", "AUD", 15, "11/10/2018");
        Assert.assertEquals(result, new BigDecimal(16.17).setScale(2, RoundingMode.HALF_EVEN));
    }
}
