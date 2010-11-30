package org.mifos.application.cashflow.struts;

import org.apache.struts.action.ActionMapping;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.platform.cashflow.CashFlowConstants;
import org.mifos.platform.cashflow.CashFlowService;
import org.mifos.platform.cashflow.service.CashFlowBoundary;
import org.mifos.platform.cashflow.service.CashFlowDetail;
import org.mifos.platform.cashflow.service.MonthlyCashFlowDetail;
import org.mifos.platform.cashflow.ui.model.CashFlowForm;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CashFlowAdaptorTest {
    private CashFlowAdaptor cashFlowAdaptor;
    @Mock
    private CashFlowServiceLocator cashFlowServiceLocator;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession httpSession;
    @Mock
    private CashFlowCaptor cashFlowCaptor;
    @Mock
    private CashFlowService cashFlowService;

    @Before
    public void setUp() throws Exception {
        cashFlowAdaptor = new CashFlowAdaptor("",cashFlowServiceLocator);
    }

    @Test
    public void save() throws Exception {
        double totalCapital = 123d;
        double totalLiability = 456d;
        when(request.getSession()).thenReturn(httpSession);
        when(cashFlowServiceLocator.getService(request)).thenReturn(cashFlowService);
        CashFlowDetail cashFlowDetail = getCashFlowDetails(totalCapital, totalLiability);
        CashFlowForm cashFlowForm = new CashFlowForm(cashFlowDetail,true,null, indebtednessRatio);
        when(cashFlowCaptor.getCashFlowForm()).thenReturn(cashFlowForm);
        cashFlowAdaptor.save(cashFlowCaptor,request);
        verify(cashFlowService).save(argThat(new CashFlowDetailMatcher(totalCapital, totalLiability)));
        verify(request).getSession();
    }

    @Test
    public void renderCashFlow() {
        DateTime firstInstallment = new DateTime(2010, 10, 11, 12, 13, 14, 15);
        DateTime lastInstallment = firstInstallment.plusMonths(4);
        CashFlowBoundary cashFlowBoundary = new CashFlowBoundary(11,2010,12);
        LoanOfferingBO loanOfferingBO = mock(LoanOfferingBO.class);
        when(cashFlowServiceLocator.getService(request)).thenReturn(cashFlowService);
        when(cashFlowService.getCashFlowBoundary(firstInstallment, lastInstallment)).thenReturn(cashFlowBoundary);
        when(request.getSession()).thenReturn(httpSession);
        when(loanOfferingBO.shouldCaptureCapitalAndLiabilityInformation()).thenReturn(true);
        String cancelUrl = "cancelUrl";
        String joinUrl = "joinUrl";
        cashFlowAdaptor.renderCashFlow(firstInstallment, lastInstallment, joinUrl, cancelUrl,mock(ActionMapping.class),request, loanOfferingBO);
        verify(httpSession).setAttribute(CashFlowConstants.CANCEL_URL,cancelUrl);
        verify(httpSession).setAttribute(CashFlowConstants.JOIN_URL,joinUrl);
        verify(httpSession).setAttribute(CashFlowConstants.CAPTURE_CAPITAL_LIABILITY_INFO,true);
        verify(httpSession).setAttribute(CashFlowConstants.START_MONTH,cashFlowBoundary.getStartMonth());
        verify(httpSession).setAttribute(CashFlowConstants.NO_OF_MONTHS,cashFlowBoundary.getNumberOfMonths());
        verify(httpSession).setAttribute(CashFlowConstants.START_YEAR,cashFlowBoundary.getStartYear());
    }
    
    private CashFlowDetail getCashFlowDetails(double totalCapital, double totalLiability) {
        DateTime dateTime = new DateTime(2010, 10, 11, 12, 13, 14, 15);
        BigDecimal revenue = new BigDecimal(123);
        BigDecimal expense = new BigDecimal(456);
        List<MonthlyCashFlowDetail> list = new ArrayList<MonthlyCashFlowDetail>();
        list.add(new MonthlyCashFlowDetail(dateTime, revenue, expense, "my notes"));
        list.add(new MonthlyCashFlowDetail(dateTime.plusMonths(1), revenue.add(new BigDecimal(20.01)), expense.add(new BigDecimal(10.22)), "my other notes"));
        CashFlowDetail cashFlowDetail = new CashFlowDetail(list);
        cashFlowDetail.setTotalCapital(new BigDecimal(totalCapital));
        cashFlowDetail.setTotalLiability(new BigDecimal(totalLiability));
        return cashFlowDetail;
    }

    private static class CashFlowDetailMatcher extends TypeSafeMatcher<CashFlowDetail> {
        private double totalCapital;
        private double totalLiability;

        public CashFlowDetailMatcher(double totalCapital, double totalLiability) {
            this.totalCapital = totalCapital;
            this.totalLiability = totalLiability;
        }
//TODO: Praveena&Buddy
        @Override
        public boolean matchesSafely(CashFlowDetail cashFlowDetail) {
            return cashFlowDetail.getTotalCapital().doubleValue() == totalCapital &&
                    cashFlowDetail.getTotalLiability().doubleValue() == totalLiability;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("CashFlowDetail did not match");
        }
    }
}
