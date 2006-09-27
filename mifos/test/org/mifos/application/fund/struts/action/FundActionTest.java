package org.mifos.application.fund.struts.action;

import java.util.List;

import org.mifos.application.fund.business.FundBO;
import org.mifos.application.fund.persistence.FundPersistence;
import org.mifos.application.fund.util.helpers.FundConstants;
import org.mifos.application.master.business.FundCodeEntity;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
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

public class FundActionTest extends MifosMockStrutsTestCase {
	private String flowKey;
	private FundBO fund;
	private UserContext userContext = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
				.getPath());
		setConfigFile(ResourceLoader
					.getURI(
							"org/mifos/application/fund/struts-config.xml")
					.getPath());
		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = TestObjectFactory.getActivityContext();
		request.getSession(false).setAttribute("ActivityContext", ac);
		Flow flow = new Flow();
		flowKey = String.valueOf(System.currentTimeMillis());
		FlowManager flowManager = new FlowManager();
		flowManager.addFLow(flowKey, flow);
		request.getSession(false).setAttribute(Constants.FLOWMANAGER, flowManager);
	}
	
	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.removeObject(fund);
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	public void testLoad() throws Exception{
		setRequestPathInfo("/fundAction.do");
		addRequestParameter("method", Methods.load.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());
		List<FundCodeEntity> fundCodeList = (List<FundCodeEntity>) SessionUtils.getAttribute(FundConstants.ALL_FUNDLIST,	request);
		assertEquals("The size of master data for funds should be 5", 5,fundCodeList.size());
	}
	
	public void testPreviewWithOutData() throws Exception {
		setRequestPathInfo("/fundAction.do");
		addRequestParameter("method", Methods.preview.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyActionErrors(new String[] {FundConstants.ERROR_MANDATORY,	FundConstants.ERROR_SELECT});
		verifyInputForward();
	}
	
	public void testPreview() throws Exception {
		setRequestPathInfo("/fundAction.do");
		addRequestParameter("method", Methods.preview.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("fundName", "Fund-1");
		addRequestParameter("fundCode", "1");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.preview_success.toString());
	}
	
	public void testPreviewForPageExpiration() throws Exception {
		setRequestPathInfo("/fundAction.do");
		addRequestParameter("method", Methods.preview.toString());
		addRequestParameter("fundName", "Fund-1");
		addRequestParameter("fundCode", "1");
		actionPerform();
		verifyForwardPath("/pages/framework/jsp/pageexpirederror.jsp");
	}
	
	public void testPrevious() throws Exception {
		setRequestPathInfo("/fundAction.do");
		addRequestParameter("method", Methods.previous.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.previous_success.toString());
	}
	
	public void testCancelCreate() throws Exception {
		setRequestPathInfo("/fundAction.do");
		addRequestParameter("method", Methods.cancelCreate.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.cancelCreate_success.toString());
		assertNull(((FlowManager) request.getSession().getAttribute(Constants.FLOWMANAGER)).getFlow(flowKey));
	}
	
	public void testCreate() throws Exception {
		setRequestPathInfo("/fundAction.do");
		addRequestParameter("method", "load");
		actionPerform();

		flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);

		setRequestPathInfo("/fundAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("fundName", "Fund-2");
		addRequestParameter("fundCode", "1");
		actionPerform();
		setRequestPathInfo("/fundAction.do");
		addRequestParameter("method", "create");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.create_success.toString());
		fund = new FundPersistence().getFund("Fund-2");
		
		assertNull(((FlowManager) request.getSession().getAttribute(Constants.FLOWMANAGER)).getFlow(flowKey));
	}
	
	public void testValidateForPreview() throws Exception {
		setRequestPathInfo("/fundAction.do");
		addRequestParameter("method", "validate");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		request.setAttribute(FundConstants.METHODCALLED,Methods.preview.toString());

		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.preview_failure.toString());
	}

	public void testVaildateForCreate() throws Exception {
		setRequestPathInfo("/fundAction.do");
		addRequestParameter("method", "validate");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		request.setAttribute(FundConstants.METHODCALLED,Methods.create.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.create_failure.toString());
	}
	
	private FundBO createFund() throws Exception {
		FundCodeEntity fundCodeEntity = (FundCodeEntity) HibernateUtil.getSessionTL().get(FundCodeEntity.class, (short) 1);
		return TestObjectFactory.createFund(fundCodeEntity,"Fund-1");
	}
}
