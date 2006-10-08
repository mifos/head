package org.mifos.application.checklist.struts.action;

import java.net.URISyntaxException;
import java.util.List;

import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.checklist.business.CheckListBO;
import org.mifos.application.checklist.util.helpers.CheckListType;
import org.mifos.application.checklist.util.resources.CheckListConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestChkListAction extends MifosMockStrutsTestCase {

	private String flowKey;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		try {
			setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
					.getPath());
			setConfigFile(ResourceLoader.getURI(
					"org/mifos/application/checklist/struts-config.xml")
					.getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		UserContext userContext = TestObjectFactory.getUserContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
		Flow flow = new Flow();
		flowKey = String.valueOf(System.currentTimeMillis());
		FlowManager flowManager = new FlowManager();
		flowManager.addFLow(flowKey, flow);
		request.getSession(false).setAttribute(Constants.FLOWMANAGER,
				flowManager);
	}

	@Override
	protected void tearDown() throws Exception {
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testLoadAllChecklist() throws Exception {
		CheckListBO checkList1 = TestObjectFactory.createAccountChecklist(
				ProductType.LOAN.getValue(),
				AccountState.LOANACC_ACTIVEINGOODSTANDING, (short) 1);
		CheckListBO checkList2 = TestObjectFactory.createAccountChecklist(
				ProductType.SAVINGS.getValue(),
				AccountState.SAVINGS_ACC_APPROVED, (short) 1);
		CheckListBO checkList3 = TestObjectFactory.createCustomerChecklist(
				CustomerLevel.CENTER.getValue(), CustomerStatus.CENTER_ACTIVE
						.getValue(), (short) 1);
		CheckListBO checkList4 = TestObjectFactory.createCustomerChecklist(
				CustomerLevel.GROUP.getValue(), CustomerStatus.GROUP_ACTIVE
						.getValue(), (short) 1);

		setRequestPathInfo("/chkListAction");
		addRequestParameter("method", "loadAllChecklist");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.loadAllChecklist_success.toString());

		assertEquals(1, ((List) SessionUtils.getAttribute(
				CheckListConstants.LOAN_CHECKLIST, request)).size());
		assertEquals(1, ((List) SessionUtils.getAttribute(
				CheckListConstants.SAVINGS_CHECKLIST, request)).size());
		assertEquals(1, ((List) SessionUtils.getAttribute(
				CheckListConstants.CENTER_CHECKLIST, request)).size());
		assertEquals(1, ((List) SessionUtils.getAttribute(
				CheckListConstants.GROUP_CHECKLIST, request)).size());
		assertEquals(0, ((List) SessionUtils.getAttribute(
				CheckListConstants.CLIENT_CHECKLIST, request)).size());

		TestObjectFactory.cleanUp(checkList1);
		TestObjectFactory.cleanUp(checkList2);
		TestObjectFactory.cleanUp(checkList3);
		TestObjectFactory.cleanUp(checkList4);
	}

	public void testGetForCustomerChecklist() throws Exception {

		CheckListBO checkList = TestObjectFactory.createCustomerChecklist(
				CustomerLevel.CENTER.getValue(), CustomerStatus.CENTER_ACTIVE
						.getValue(), (short) 1);
		setRequestPathInfo("/chkListAction");
		addRequestParameter("method", "get");
		addRequestParameter("checkListId", checkList.getChecklistId()
				.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.get_success.toString());

		assertNotNull(SessionUtils
				.getAttribute(Constants.BUSINESS_KEY, request));
		assertEquals(CheckListType.CUSTOMER_CHECKLIST.getValue(), SessionUtils
				.getAttribute(CheckListConstants.TYPE, request));
		assertNotNull(SessionUtils.getAttribute(
				CheckListConstants.CREATED_BY_NAME, request));

		TestObjectFactory.cleanUp(checkList);

	}

	public void testGetForAccountChecklist() throws Exception {
		CheckListBO checkList = TestObjectFactory.createAccountChecklist(
				ProductType.LOAN.getValue(),
				AccountState.LOANACC_ACTIVEINGOODSTANDING, (short) 1);

		setRequestPathInfo("/chkListAction");
		addRequestParameter("method", "get");
		addRequestParameter("checkListId", checkList.getChecklistId()
				.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.get_success.toString());

		assertNotNull(SessionUtils
				.getAttribute(Constants.BUSINESS_KEY, request));
		assertEquals(CheckListType.ACCOUNT_CHECKLIST.getValue(), SessionUtils
				.getAttribute(CheckListConstants.TYPE, request));
		assertNotNull(SessionUtils.getAttribute(
				CheckListConstants.CREATED_BY_NAME, request));

		TestObjectFactory.cleanUp(checkList);

	}

}
