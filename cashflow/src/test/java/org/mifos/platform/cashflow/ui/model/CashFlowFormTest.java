package org.mifos.platform.cashflow.ui.model;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mifos.platform.cashflow.matchers.MonthlyCashFlowFormMatcher;
import org.mifos.platform.cashflow.service.CashFlowDetail;
import org.mifos.platform.cashflow.service.MonthlyCashFlowDetail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

public class CashFlowFormTest {
    
    @Test
    public void testGetMonthlyCashFlows() throws Exception {
        MonthlyCashFlowDetail cashFlowDetail1 = new MonthlyCashFlowDetail(new DateTime(), new BigDecimal(12), new BigDecimal(12), "notes");
        MonthlyCashFlowDetail cashFlowDetail2 = new MonthlyCashFlowDetail(new DateTime(), new BigDecimal(23), new BigDecimal(34), "notes");
        ArrayList<MonthlyCashFlowDetail> monthlyCashFlowDetails = new ArrayList<MonthlyCashFlowDetail>();
        monthlyCashFlowDetails.add(cashFlowDetail1);
        monthlyCashFlowDetails.add(cashFlowDetail2);
        CashFlowDetail cashFlowDetail = new CashFlowDetail(monthlyCashFlowDetails);
        CashFlowForm cashFlowForm= new CashFlowForm(cashFlowDetail, false, null, 0d);
        List<MonthlyCashFlowForm> actual = cashFlowForm.getMonthlyCashFlows();
        List<MonthlyCashFlowForm> expected = new ArrayList<MonthlyCashFlowForm>();
        expected.add(new MonthlyCashFlowForm(cashFlowDetail1));
        expected.add(new MonthlyCashFlowForm(cashFlowDetail2));
        assertThat(actual.get(0),new MonthlyCashFlowFormMatcher(expected.get(0)));
        assertThat(actual.get(1),new MonthlyCashFlowFormMatcher(expected.get(1)));
    }
}