package org.mifos.platform.cashflow.ui.model;

import java.math.BigDecimal;
import java.util.Locale;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mifos.platform.cashflow.service.MonthlyCashFlowDetail;
import org.mockito.Mock;
import org.mockito.Mockito;

public class MonthlyCashFlowFormTest {

    MonthlyCashFlowDetail monthlyCashFlowDetail;


    @Before
    public void setup() {
        DateTime testDate = new DateTime(2010,1,1,1,1,1,1);
        monthlyCashFlowDetail = new MonthlyCashFlowDetail(testDate, new BigDecimal(12), new BigDecimal(12), "notes");


    }

    @Test
    public void shouldBeAbleToGetMonthNamesInDifferentLocales() {
         MonthlyCashFlowForm monthlyCashFlowForm = new MonthlyCashFlowForm(monthlyCashFlowDetail);

         monthlyCashFlowForm.setLocale(Locale.ENGLISH);
         Assert.assertEquals("January", monthlyCashFlowForm.getMonthInLocale());

         monthlyCashFlowForm.setLocale(Locale.FRENCH);
         Assert.assertEquals("janvier", monthlyCashFlowForm.getMonthInLocale());
    }


}
