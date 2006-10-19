package org.mifos.application.customer.struts.action;

import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerSearchConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCustSearchAction extends MifosMockStrutsTestCase {
	
	private UserContext userContext;
	private String flowKey;
	private CenterBO center;
	private GroupBO group;
	private AccountBO account;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
				.getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/customer/struts-config.xml")
					.getPath());
		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		
		request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
		Flow flow = new Flow();
		flowKey = String.valueOf(System.currentTimeMillis());
		FlowManager flowManager = new FlowManager();
		flowManager.addFLow(flowKey, flow,CustSearchAction.class.getName());
		request.getSession(false).setAttribute(Constants.FLOWMANAGER,
				flowManager);
		addCurrentFlowKey();
	}
	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(account);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		userContext = null;
		super.tearDown();
	}	
	public void testLoadSearch(){
		
		addActionAndMethod(Methods.loadSearch.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.loadSearch_success.toString());

	}
	public void testSearch()throws Exception{
		createGroupWithCenter();
		addActionAndMethod(Methods.search.toString());
		addRequestParameter("input", "loan");
		addRequestParameter("searchString", "gr");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.search_success.toString());
		veryfyResults();

	}
	public void testLoadMainSearchHoUser()throws Exception{
		addActionAndMethod(Methods.loadMainSearch.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(CustomerSearchConstants.LOADFORWARDOFFICE_SUCCESS);
		List<OfficeBO> officeset= (List<OfficeBO>)SessionUtils.getAttribute(CustomerSearchConstants.OFFICESLIST,request);
		assertNotNull(officeset);
		assertEquals(1,officeset.size());
		assertEquals(TestObjectFactory.getOffice(Short.valueOf("1")).getOfficeName(),
		SessionUtils.getAttribute(CustomerSearchConstants.OFFICE,request));
	}
	public void testLoadMainSearchNonLoBoUser()throws Exception{
		
		OfficeBO officeBO = TestObjectFactory.getOffice(Short.valueOf("3"));
		PersonnelBO personnelBO = TestObjectFactory.createPersonnel(PersonnelLevel.NON_LOAN_OFFICER,officeBO,
				Integer.valueOf("1"),Short.valueOf("1"),"1234","raj",null,null,null,new Name("abe",null,null,null),
				null,new Date(),null,Integer.valueOf("1"),null,null,null);
		userContext.setId(personnelBO.getPersonnelId());
		addActionAndMethod(Methods.loadMainSearch.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(CustomerSearchConstants.LOADFORWARDNONLOANOFFICER_SUCCESS);
		List<PersonnelBO> personnelList= (List<PersonnelBO>)SessionUtils.getAttribute(CustomerSearchConstants.LOANOFFICERSLIST,request);
		assertNotNull(personnelList);
		assertEquals(1,personnelList.size());
		assertEquals(officeBO.getOfficeName(),
		SessionUtils.getAttribute(CustomerSearchConstants.OFFICE,request));
		TestObjectFactory.cleanUp(personnelBO);
	}	
	public void testLoadMainSearchLoBoUser()throws Exception{
		OfficeBO officeBO = TestObjectFactory.getOffice(Short.valueOf("3"));
		userContext.setId(Short.valueOf("3"));
		createGroupWithCenter();
		addActionAndMethod(Methods.loadMainSearch.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(CustomerSearchConstants.LOADFORWARDLOANOFFICER_SUCCESS);
		List<CustomerBO> customerList= (List<CustomerBO>)SessionUtils.getAttribute(CustomerSearchConstants.CUSTOMERLIST,request);
		assertNotNull(customerList);
		assertEquals(0,customerList.size());
		assertEquals(officeBO.getOfficeName(),
		SessionUtils.getAttribute(CustomerSearchConstants.OFFICE,request));
		
	}	
	public void testMainSearch()throws Exception{
		createGroupWithCenter();
		userContext.setId(Short.valueOf("1"));
		addActionAndMethod(Methods.mainSearch.toString());
		addRequestParameter("searchString", "gr");
		addRequestParameter("officeId", "0");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.mainSearch_success.toString());
		veryfyResults();

	}	
	public void testMainIdSearch()throws Exception{
		createGroupWithCenter();
		userContext.setId(Short.valueOf("1"));
		addActionAndMethod(Methods.mainSearch.toString());
		addRequestParameter("searchString", center.getGlobalCustNum());
		addRequestParameter("officeId", "0");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.mainSearch_success.toString());
		veryfyResults();
/*		HibernateUtil.closeSession();
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class,center.getCustomerId());
*/
	}
	public void testMainAccountIdSearch()throws Exception{
		userContext.setId(Short.valueOf("1"));
		addActionAndMethod(Methods.mainSearch.toString());
		account = getLoanAccount();
		addRequestParameter("searchString", account.getGlobalAccountNum());
		addRequestParameter("officeId", "0");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.mainSearch_success.toString());
		veryfyResults();
/*		HibernateUtil.closeSession();
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class,center.getCustomerId());
*/
	}
	private void veryfyResults() throws Exception{
		QueryResult queryResult = (QueryResult)SessionUtils.getAttribute(Constants.SEARCH_RESULTS,request);
		assertNotNull(queryResult);
		assertEquals(1,queryResult.getSize());
		assertEquals(1,queryResult.get(0,10).size());

	}
	private void addActionAndMethod(String method){
		setRequestPathInfo("/custSearchAction.do");
		addRequestParameter("method", method);

	}
	private void addCurrentFlowKey(){
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter(Constants.CURRENTFLOWKEY,flowKey);
	}
	private void createGroupWithCenter()throws Exception{
		createParentCustomer();
		group = TestObjectFactory.createGroupUnderCenter("group",CustomerStatus.GROUP_ACTIVE, center);
		//HibernateUtil.closeSession();
	}
	private LoanBO getLoanAccount() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", group, Short
				.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
	}	
	private void createParentCustomer() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center",
				CustomerStatus.CENTER_ACTIVE.getValue(), "1.4", meeting,
				new Date(System.currentTimeMillis()));

	}
}
