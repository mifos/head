package org.mifos.application.accounts.loan.struts.action;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.loan.struts.actionforms.LoanAccountActionForm;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountDetailsViewHelper;
import org.mifos.application.configuration.business.service.ConfigurationBusinessService;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.framework.MifosTestCase;


public class TestLoanAccountActionIndividualLoans extends MifosTestCase {


	public void testShouldCallCreateMethodIfNewMembersSelected()
			throws Exception {
		GlimLoanUpdater glimLoanUpdaterMock = createMock(GlimLoanUpdater.class);
		LoanAccountAction loanAccountAction = new LoanAccountAction(
				new LoanBusinessService(), new ConfigurationBusinessService(),
				glimLoanUpdaterMock);
		LoanBO parentLoanMock = createMock(LoanBO.class);
		LoanAccountActionForm loanAccountActionForm = new LoanAccountActionForm();
		List<LoanAccountDetailsViewHelper> clientDetails = new ArrayList<LoanAccountDetailsViewHelper>();
		LoanAccountDetailsViewHelper LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_300 = LoanAccountDetailsViewHelper
				.createInstanceForTest("3", "2", Double.valueOf(300), "2");
		clientDetails.add(LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_300);
		List<LoanBO> loans = new ArrayList<LoanBO>();
		glimLoanUpdaterMock.createIndividualLoan(loanAccountActionForm,
				parentLoanMock, true, LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_300);
		expectLastCall().atLeastOnce();
		replay(glimLoanUpdaterMock, parentLoanMock);
		loanAccountAction.handleIndividualLoans(parentLoanMock,
				loanAccountActionForm, true, clientDetails, loans);
		verify(glimLoanUpdaterMock, parentLoanMock);

	}

	public void testShouldCallUpdateMethodIfExistingMembersChanged()
			throws Exception {
		GlimLoanUpdater glimLoanUpdaterMock = createMock(GlimLoanUpdater.class);
		LoanAccountAction loanAccountAction = new LoanAccountAction(
				new LoanBusinessService(), new ConfigurationBusinessService(),
				glimLoanUpdaterMock);
		LoanBO loanMock = createMock(LoanBO.class);
		expect(loanMock.getAccountId()).andReturn(2).anyTimes();
		ClientBO customerMock = createMock(ClientBO.class);
		expect(loanMock.getCustomer()).andReturn(customerMock).anyTimes();
		expect(customerMock.getGlobalCustNum()).andReturn("3").anyTimes();
		LoanAccountActionForm loanAccountActionForm = new LoanAccountActionForm();
		List<LoanAccountDetailsViewHelper> clientDetails = new ArrayList<LoanAccountDetailsViewHelper>();
		LoanAccountDetailsViewHelper LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_300 = LoanAccountDetailsViewHelper
				.createInstanceForTest("3", "2", Double.valueOf(300), "2");
		clientDetails.add(LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_300);
		List<LoanBO> loans = new ArrayList<LoanBO>();
		loans.add(loanMock);
		glimLoanUpdaterMock.updateIndividualLoan(
				LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_300, loanMock);
		expectLastCall().atLeastOnce();
		replay(glimLoanUpdaterMock, loanMock, customerMock);
		loanAccountAction.handleIndividualLoans(loanMock,
				loanAccountActionForm, true, clientDetails, loans);
		verify(glimLoanUpdaterMock, loanMock, customerMock);
	}

	public void testShouldCallDeleteMethodIfExistingMembersRemoved()
			throws Exception {
		GlimLoanUpdater glimLoanUpdaterMock = createMock(GlimLoanUpdater.class);
		LoanAccountAction loanAccountAction = new LoanAccountAction(
				new LoanBusinessService(), new ConfigurationBusinessService(),
				glimLoanUpdaterMock);
		LoanBO loanMock = createMock(LoanBO.class);
		expect(loanMock.getAccountId()).andReturn(2).anyTimes();
		ClientBO customerMock = createMock(ClientBO.class);
		expect(loanMock.getCustomer()).andReturn(customerMock).anyTimes();
		expect(customerMock.getGlobalCustNum()).andReturn("3").anyTimes();
		LoanAccountActionForm loanAccountActionForm = new LoanAccountActionForm();
		List<LoanAccountDetailsViewHelper> clientDetails = new ArrayList<LoanAccountDetailsViewHelper>();
		List<LoanBO> loans = new ArrayList<LoanBO>();
		loans.add(loanMock);
		glimLoanUpdaterMock.delete(loanMock);
		expectLastCall().atLeastOnce();
		replay(glimLoanUpdaterMock, loanMock, customerMock);
		loanAccountAction.handleIndividualLoans(loanMock,
				loanAccountActionForm, true, clientDetails, loans);
		verify(glimLoanUpdaterMock, loanMock, customerMock);

	}
}
