package org.mifos.application.configuration.struts.action;

import org.mifos.application.ApplicationTestSuite;
import org.mifos.application.accounts.loan.struts.action.MultipleLoanAccountsCreationAction;
import org.mifos.application.configuration.struts.actionform.LabelConfigurationActionForm;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.components.mifosmenu.MenuRepository;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LabelConfigurationActionTest extends MifosMockStrutsTestCase {

	private UserContext userContext;

	private String flowKey;

	@Override
	protected void tearDown() throws Exception {
		HibernateUtil.closeSession();
		super.tearDown();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext",
				TestObjectFactory.getActivityContext());
		flowKey = createFlow(request, MultipleLoanAccountsCreationAction.class);
	}


	public void testUpdateWithOutData() {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/labelconfigurationaction.do");
		addRequestParameter("method", "update");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyActionErrors(new String[] { "Please specify Head office.",
				"Please specify Regional office.",
				"Please specify Sub regional office.",
				"Please specify Area office.", "Please specify Branch office.",
				"Please specify Client.", "Please specify Group.",
				"Please specify Center.", "Please specify Cash.",
				"Please specify Check.", "Please specify Vouchers.",
				"Please specify Loans.", "Please specify Savings.",
				"Please specify State.", "Please specify Postal code.",
				"Please specify Ethnicity.", "Please specify Citizenship.",
				"Please specify Handicapped.", "Please specify Government ID.",
				"Please specify Address 1.", "Please specify Address 2.",
				"Please specify Address 3.",
				"Please specify Partial Application.",
				"Please specify Pending Approval.", "Please specify Approved.",
				"Please specify Cancel.", "Please specify Closed.",
				"Please specify On hold.", "Please specify Active.",
				"Please specify Inactive.",
				"Please specify Active in good standing.",
				"Please specify Active in bad standing.",
				"Please specify Closed-obligation met.",
				"Please specify Closed-rescheduled.",
				"Please specify Closed-written off.", "Please specify None.",
				"Please specify Grace on all repayments.",
				"Please specify Principal only grace.",
				"Please specify Interest.", "Please specify External ID.",
				"Please specify Bulk entry." });
		verifyInputForward();
	}

	public void testUpdate() {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/labelconfigurationaction.do");
		addRequestParameter("method", "update");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("headOffice", "Head Office-Changed");
		addRequestParameter("regionalOffice", "Regional Office-Changed");
		addRequestParameter("subRegionalOffice", "Divisional Office-Changed");
		addRequestParameter("areaOffice", "Area Office-Changed");
		addRequestParameter("branchOffice", "Branch Office-Changed");
		addRequestParameter("client", "Member-Changed");
		addRequestParameter("group", "Group-Changed");
		addRequestParameter("center", "Kendra-Changed");
		addRequestParameter("cash", "Cash-Changed");
		addRequestParameter("check", "Cheque-Changed");
		addRequestParameter("vouchers", "Voucher-Changed");
		addRequestParameter("loans", "Loan-Changed");
		addRequestParameter("savings", "Margin Money-Changed");
		addRequestParameter("state", "State-Changed");
		addRequestParameter("postalCode", "Postal Code-Changed");
		addRequestParameter("ethnicity", "Caste-Changed");
		addRequestParameter("citizenship", "Religion-Changed");
		addRequestParameter("handicapped", "Handicapped-Changed");
		addRequestParameter("govtId", "Government ID-Changed");
		addRequestParameter("address1", "Address1-Changed");
		addRequestParameter("address2", "Address2-Changed");
		addRequestParameter("address3", "Village-Changed");
		addRequestParameter("partialApplication", "Partial Application-Changed");
		addRequestParameter("pendingApproval",
				"Application Pending Approval-Changed");
		addRequestParameter("approved", "Application Approved-Changed");
		addRequestParameter("cancel", "Cancel-Changed");
		addRequestParameter("closed", "Closed-Changed");
		addRequestParameter("onhold", "On Hold-Changed");
		addRequestParameter("active", "Active-Changed");
		addRequestParameter("inActive", "Inactive-Changed");
		addRequestParameter("activeInGoodStanding",
				"Active in Good Standing-Changed");
		addRequestParameter("activeInBadStanding",
				"Active in Bad Standing-Changed");
		addRequestParameter("closedObligationMet",
				"Closed- Obligation met-Changed");
		addRequestParameter("closedRescheduled", "Closed- Rescheduled-Changed");
		addRequestParameter("closedWrittenOff", "Closed- Written Off-Changed");
		addRequestParameter("none", "None-Changed");
		addRequestParameter("graceOnAllRepayments",
				"Grace on all repayments-Changed");
		addRequestParameter("principalOnlyGrace",
				"Principal only grace-Changed");
		addRequestParameter("interest", "Service Charge-Changed");
		addRequestParameter("externalId", "External Id-Changed");
		addRequestParameter("bulkEntry", "Bulk entry-Changed");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();

		assertFalse(MenuRepository.getInstance().isMenuForLocale(
				userContext.getPreferredLocale()));

		setRequestPathInfo("/labelconfigurationaction.do");
		addRequestParameter("method", "load");
		actionPerform();
		LabelConfigurationActionForm labelConfigurationActionForm = (LabelConfigurationActionForm) request
				.getSession().getAttribute("labelconfigurationactionform");
		assertEquals("Head Office-Changed", labelConfigurationActionForm
				.getHeadOffice());
		assertEquals("Regional Office-Changed", labelConfigurationActionForm
				.getRegionalOffice());
		assertEquals("Divisional Office-Changed", labelConfigurationActionForm
				.getSubRegionalOffice());
		assertEquals("Area Office-Changed", labelConfigurationActionForm
				.getAreaOffice());
		assertEquals("Branch Office-Changed", labelConfigurationActionForm
				.getBranchOffice());
		assertEquals("Member-Changed", labelConfigurationActionForm.getClient());
		assertEquals("Group-Changed", labelConfigurationActionForm.getGroup());
		assertEquals("Kendra-Changed", labelConfigurationActionForm.getCenter());
		assertEquals("Cash-Changed", labelConfigurationActionForm.getCash());
		assertEquals("Cheque-Changed", labelConfigurationActionForm.getCheck());
		assertEquals("Voucher-Changed", labelConfigurationActionForm
				.getVouchers());
		assertEquals("Loan-Changed", labelConfigurationActionForm.getLoans());
		assertEquals("Margin Money-Changed", labelConfigurationActionForm
				.getSavings());
		assertEquals("State-Changed", labelConfigurationActionForm.getState());
		assertEquals("Postal Code-Changed", labelConfigurationActionForm
				.getPostalCode());
		assertEquals("Caste-Changed", labelConfigurationActionForm
				.getEthnicity());
		assertEquals("Religion-Changed", labelConfigurationActionForm
				.getCitizenship());
		assertEquals("Handicapped-Changed", labelConfigurationActionForm
				.getHandicapped());
		assertEquals("Government ID-Changed", labelConfigurationActionForm
				.getGovtId());
		assertEquals("Address1-Changed", labelConfigurationActionForm
				.getAddress1());
		assertEquals("Address2-Changed", labelConfigurationActionForm
				.getAddress2());
		assertEquals("Village-Changed", labelConfigurationActionForm
				.getAddress3());
		assertEquals("Partial Application-Changed",
				labelConfigurationActionForm.getPartialApplication());
		assertEquals("Application Pending Approval-Changed",
				labelConfigurationActionForm.getPendingApproval());
		assertEquals("Application Approved-Changed",
				labelConfigurationActionForm.getApproved());
		assertEquals("Cancel-Changed", labelConfigurationActionForm.getCancel());
		assertEquals("Closed-Changed", labelConfigurationActionForm.getClosed());
		assertEquals("On Hold-Changed", labelConfigurationActionForm
				.getOnhold());
		assertEquals("Active-Changed", labelConfigurationActionForm.getActive());
		assertEquals("Inactive-Changed", labelConfigurationActionForm
				.getInActive());
		assertEquals("Active in Good Standing-Changed",
				labelConfigurationActionForm.getActiveInGoodStanding());
		assertEquals("Active in Bad Standing-Changed",
				labelConfigurationActionForm.getActiveInBadStanding());
		assertEquals("Closed- Obligation met-Changed",
				labelConfigurationActionForm.getClosedObligationMet());
		assertEquals("Closed- Rescheduled-Changed",
				labelConfigurationActionForm.getClosedRescheduled());
		assertEquals("Closed- Written Off-Changed",
				labelConfigurationActionForm.getClosedWrittenOff());
		assertEquals("None-Changed", labelConfigurationActionForm.getNone());
		assertEquals("Grace on all repayments-Changed",
				labelConfigurationActionForm.getGraceOnAllRepayments());
		assertEquals("Principal only grace-Changed",
				labelConfigurationActionForm.getPrincipalOnlyGrace());
		assertEquals("Service Charge-Changed", labelConfigurationActionForm
				.getInterest());
		assertEquals("External Id-Changed", labelConfigurationActionForm
				.getExternalId());
		assertEquals("Bulk entry-Changed", labelConfigurationActionForm
				.getBulkEntry());

		setRequestPathInfo("/labelconfigurationaction.do");
		addRequestParameter("method", "update");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("headOffice", "Head Office");
		addRequestParameter("regionalOffice", "Regional Office");
		addRequestParameter("subRegionalOffice", "Divisional Office");
		addRequestParameter("areaOffice", "Area Office");
		addRequestParameter("branchOffice", "Branch Office");
		addRequestParameter("client", "Member");
		addRequestParameter("group", "Group");
		addRequestParameter("center", "Kendra");
		addRequestParameter("cash", "Cash");
		addRequestParameter("check", "Cheque");
		addRequestParameter("vouchers", "Voucher");
		addRequestParameter("loans", "Loan");
		addRequestParameter("savings", "Savings");
		addRequestParameter("state", "State");
		addRequestParameter("postalCode", "Postal Code");
		addRequestParameter("ethnicity", "Caste");
		addRequestParameter("citizenship", "Religion");
		addRequestParameter("handicapped", "Handicapped");
		addRequestParameter("govtId", "Government ID");
		addRequestParameter("address1", "Address1");
		addRequestParameter("address2", "Address2");
		addRequestParameter("address3", "Village");
		addRequestParameter("partialApplication", "Partial Application");
		addRequestParameter("pendingApproval", "Application Pending Approval");
		addRequestParameter("approved", "Application Approved");
		addRequestParameter("cancel", "Cancel");
		addRequestParameter("closed", "Closed");
		addRequestParameter("onhold", "On Hold");
		addRequestParameter("active", "Active");
		addRequestParameter("inActive", "Inactive");
		addRequestParameter("activeInGoodStanding", "Active in Good Standing");
		addRequestParameter("activeInBadStanding", "Active in Bad Standing");
		addRequestParameter("closedObligationMet", "Closed- Obligation met");
		addRequestParameter("closedRescheduled", "Closed- Rescheduled");
		addRequestParameter("closedWrittenOff", "Closed- Written Off");
		addRequestParameter("none", "None");
		addRequestParameter("graceOnAllRepayments", "Grace on all repayments");
		addRequestParameter("principalOnlyGrace", "Principal only grace");
		addRequestParameter("interest", "Service Charge");
		addRequestParameter("externalId", "External Id");
		addRequestParameter("bulkEntry", "Bulk entry");
		actionPerform();
		verifyNoActionErrors();
	}

	public void testCancel() {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/labelconfigurationaction.do");
		addRequestParameter("method", "cancel");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.cancel_success.toString());
	}

	public void testValidate() throws Exception {
		setRequestPathInfo("/labelconfigurationaction.do");
		addRequestParameter("method", "validate");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_failure.toString());
	}

	public void testValidateForUpdate() throws Exception {
		setRequestPathInfo("/labelconfigurationaction.do");
		addRequestParameter("method", "validate");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		request.setAttribute("methodCalled", Methods.update.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.update_failure.toString());
	}
	
	/**
	 * TODO: This test only passes when run as part of
	 * {@link ApplicationTestSuite}, not by itself.
	 * Something seems to be putting in Grameen Koota configuration
	 * (Kendra/Member instead of Center/Client, etc).  It seems
	 * to be in ConfigurationTestSuite-- TestConfiguration seems to
	 * be the specific culprit. If this test runs before TestConfiguration
	 * it fails, if it runs after TestConfiguration, it succeeds.
	 */
	public void testLoad() throws Exception {
		setRequestPathInfo("/labelconfigurationaction.do");
		addRequestParameter("method", "load");
		performNoErrors();
		verifyForward(ActionForwards.load_success.toString());
		LabelConfigurationActionForm labelConfigurationActionForm = 
			(LabelConfigurationActionForm) request
				.getSession().getAttribute("labelconfigurationactionform");
		assertEquals("Head Office", labelConfigurationActionForm
				.getHeadOffice());
		assertEquals("Regional Office", labelConfigurationActionForm
				.getRegionalOffice());
		assertEquals("Divisional Office", labelConfigurationActionForm
				.getSubRegionalOffice());
		assertEquals("Area Office", labelConfigurationActionForm
				.getAreaOffice());
		assertEquals("Branch Office", labelConfigurationActionForm
				.getBranchOffice());
		assertEquals("Member", labelConfigurationActionForm.getClient());
		assertEquals("Group", labelConfigurationActionForm.getGroup());
		assertEquals("Kendra", labelConfigurationActionForm.getCenter());
		assertEquals("Cash", labelConfigurationActionForm.getCash());
		assertEquals("Cheque", labelConfigurationActionForm.getCheck());
		assertEquals("Voucher", labelConfigurationActionForm.getVouchers());
		assertEquals("Loan", labelConfigurationActionForm.getLoans());
		assertEquals("Savings", labelConfigurationActionForm.getSavings());
		assertEquals("State", labelConfigurationActionForm.getState());
		assertEquals("Postal Code", labelConfigurationActionForm
				.getPostalCode());
		assertEquals("Caste", labelConfigurationActionForm.getEthnicity());
		assertEquals("Religion", labelConfigurationActionForm.getCitizenship());
		assertEquals("Handicapped", labelConfigurationActionForm
				.getHandicapped());
		assertEquals("Government ID", labelConfigurationActionForm.getGovtId());
		assertEquals("Address1", labelConfigurationActionForm.getAddress1());
		assertEquals("Address2", labelConfigurationActionForm.getAddress2());
		assertEquals("Village", labelConfigurationActionForm.getAddress3());
		assertEquals("Partial Application", labelConfigurationActionForm
				.getPartialApplication());
		assertEquals("Application Pending Approval",
				labelConfigurationActionForm.getPendingApproval());
		assertEquals("Application Approved", labelConfigurationActionForm
				.getApproved());
		assertEquals("Cancel", labelConfigurationActionForm.getCancel());
		assertEquals("Closed", labelConfigurationActionForm.getClosed());
		assertEquals("On Hold", labelConfigurationActionForm.getOnhold());
		assertEquals("Active", labelConfigurationActionForm.getActive());
		assertEquals("Inactive", labelConfigurationActionForm.getInActive());
		assertEquals("Active in Good Standing", labelConfigurationActionForm
				.getActiveInGoodStanding());
		assertEquals("Active in Bad Standing", labelConfigurationActionForm
				.getActiveInBadStanding());
		assertEquals("Closed- Obligation met", labelConfigurationActionForm
				.getClosedObligationMet());
		assertEquals("Closed- Rescheduled", labelConfigurationActionForm
				.getClosedRescheduled());
		assertEquals("Closed- Written Off", labelConfigurationActionForm
				.getClosedWrittenOff());
		assertEquals("None", labelConfigurationActionForm.getNone());
		assertEquals("Grace on all repayments", labelConfigurationActionForm
				.getGraceOnAllRepayments());
		assertEquals("Principal only grace", labelConfigurationActionForm
				.getPrincipalOnlyGrace());
		assertEquals("Service Charge", labelConfigurationActionForm
				.getInterest());
		assertEquals("External Id", labelConfigurationActionForm
				.getExternalId());
		assertEquals("Bulk entry", labelConfigurationActionForm.getBulkEntry());
	}
	
}
