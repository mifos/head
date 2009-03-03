package org.mifos.application.customer.group.business;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

public class GroupPerformanceHistoryUpdaterTest extends MifosIntegrationTest {
	
	public GroupPerformanceHistoryUpdaterTest() throws SystemException, ApplicationException {
        super();
    }

    public void testShouldMatchClientsWithParentAccounts() throws Exception {
		LoanBO parentLoanMock1 = createMock(LoanBO.class);
		expect(parentLoanMock1.getAccountId()).andReturn(1).times(3);
		
		LoanBO parentLoanMock2 = createMock(LoanBO.class);
		expect(parentLoanMock2.getAccountId()).andReturn(2);
		
		LoanBO childLoanMock1 = createMock(LoanBO.class);
		expect(childLoanMock1.getParentAccount()).andReturn(parentLoanMock1);
		expect(childLoanMock1.isOfType(AccountTypes.INDIVIDUAL_LOAN_ACCOUNT)).andReturn(true);
		
		LoanBO childLoanMock2 = createMock(LoanBO.class);
		expect(childLoanMock2.isOfType(AccountTypes.INDIVIDUAL_LOAN_ACCOUNT)).andReturn(true);
		expect(childLoanMock2.getParentAccount()).andReturn(parentLoanMock2);
		
		replay(parentLoanMock1, parentLoanMock2, childLoanMock1, childLoanMock2);
		GroupPerformanceHistoryUpdater.ClientAccountWithParentAccountMatcher matcher = new GroupPerformanceHistoryUpdater.ClientAccountWithParentAccountMatcher(parentLoanMock1);
		assertTrue(matcher.evaluate(childLoanMock1));
		assertFalse(matcher.evaluate(childLoanMock2));
		verify(parentLoanMock1, parentLoanMock2, childLoanMock1, childLoanMock2);
	}
}
