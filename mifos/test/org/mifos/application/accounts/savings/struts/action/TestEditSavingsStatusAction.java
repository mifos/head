package org.mifos.application.accounts.savings.struts.action;

import java.net.URISyntaxException;
import java.sql.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.accounts.business.AccountNotesEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.persistence.service.PersonnelPersistenceService;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.StatesInitializationException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.PersistenceServiceName;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestEditSavingsStatusAction extends MifosMockStrutsTestCase {
	private AccountStateEntity accountStateEntity;

	private SavingsBO savingsBO;

	private UserContext userContext;

	private CustomerBO group;

	private CustomerBO center;

	private SavingsOfferingBO savingsOffering;

	private Session session;

	protected void setUp() throws Exception {
		super.setUp();
		try {
			setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
					.getPath());
			setConfigFile(ResourceLoader.getURI(
					"org/mifos/framework/util/helpers/struts-config.xml")
					.getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		userContext = new UserContext();
		userContext.setId(new Short("1"));
		userContext.setLocaleId(new Short("1"));
		Set<Short> set = new HashSet<Short>();
		set.add(Short.valueOf("1"));
		userContext.setRoles(set);
		userContext.setLevelId(Short.valueOf("2"));
		userContext.setName("mifos");
		userContext.setPereferedLocale(new Locale("en", "US"));
		userContext.setBranchId(new Short("1"));
		userContext.setBranchGlobalNum("0001");
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);

	}

	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(savingsBO);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		session=null;
		super.tearDown();
	}

	public void testLoad() {
		createInitialObjects();
		savingsOffering = createSavingsOffering();
		savingsBO = createSavingsAccount("000X00000000013", savingsOffering,
				AccountStates.SAVINGS_ACC_CANCEL);
		savingsBO.setAccountState(getAccountStateEntityObject(savingsBO.getAccountState().getId()));
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savingsBO,request.getSession());
		setRequestPathInfo("/editSavingsAction.do");
		addRequestParameter("method", "load");
		actionPerform();
		verifyForward("load_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		savingsBO = (SavingsBO)SessionUtils.getAttribute(Constants.BUSINESS_KEY,request.getSession());
		assertNotNull(request.getSession().getAttribute(SavingsConstants.STATUS_LIST));
		assertEquals(savingsBO.getCustomer(), group);
		assertEquals(savingsBO.getSavingsOffering(),savingsOffering);
		//TestObjectFactory.removeObject(savingsOffering);
	}

	public void testPreview() throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering();
		savingsBO = createSavingsAccount("000X00000000013", savingsOffering,
				AccountStates.SAVINGS_ACC_CANCEL);
		savingsBO.initializeStateMachine(userContext.getLocaleId());
		savingsBO.setAccountState(getAccountStateEntityObject(savingsBO.getAccountState().getId()));
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savingsBO,request.getSession());
		setRequestPathInfo("/editSavingsAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("newStatusId","13");
		addRequestParameter("accountNotes.comment","hi");
		addRequestParameter("flagId","1");
		addRequestParameter("accountId",savingsBO.getAccountId().toString());
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		savingsBO = (SavingsBO)SessionUtils.getAttribute(Constants.BUSINESS_KEY,request.getSession());
		assertEquals(savingsBO.getCustomer(), group);
		assertEquals(savingsBO.getSavingsOffering(),savingsOffering);
		assertNotNull(SessionUtils.getAttribute("newStatusName",request.getSession()));
		//assertNotNull(request.getSession().getAttribute(SavingsConstants.STATUS_CHECK_LIST));
	}

	public void testPrevious() throws StatesInitializationException, AccountException {
		createInitialObjects();
		savingsOffering = createSavingsOffering();
		savingsBO = createSavingsAccount("000X00000000013", savingsOffering,
				AccountStates.SAVINGS_ACC_CANCEL);
		savingsBO.initializeStateMachine(userContext.getLocaleId());
		savingsBO.setAccountState(getAccountStateEntityObject(savingsBO.getAccountState().getId()));
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savingsBO,request.getSession());
		setRequestPathInfo("/editSavingsAction.do");
		addRequestParameter("method", "previous");
		actionPerform();
		verifyForward("previous_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testUpdateSuccess() throws StatesInitializationException, ServiceException, AccountException {
		PersonnelPersistenceService personnelPersistenceService = (PersonnelPersistenceService) ServiceFactory.getInstance().getPersistenceService(
				PersistenceServiceName.Personnel);
		createInitialObjects();
		savingsOffering = createSavingsOffering();
		savingsBO = createSavingsAccount("000X00000000013", savingsOffering,
				AccountStates.SAVINGS_ACC_APPROVED);
		savingsBO.initializeStateMachine(userContext.getLocaleId());
		savingsBO.setAccountState(getAccountStateEntityObject(savingsBO.getAccountState().getId()));
		savingsBO.getAccountState().setLocaleId(userContext.getLocaleId());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savingsBO,request.getSession());
		AccountNotesEntity accountNotes = new AccountNotesEntity();
		accountNotes.setCommentDate(new Date(System.currentTimeMillis()));
		accountNotes.setPersonnel(personnelPersistenceService.getPersonnel(userContext.getId()));
		accountNotes.setComment("hi");
		SessionUtils.setAttribute(SavingsConstants.ACCOUNT_NOTES,accountNotes,request.getSession());
		SessionUtils.setAttribute(SavingsConstants.FLAG_NAME, "Other", request.getSession());
		setRequestPathInfo("/editSavingsAction.do");
		addRequestParameter("method", "update");
		addRequestParameter("newStatusId","18");
		addRequestParameter("accountId",savingsBO.getAccountId().toString());
		actionPerform();
		verifyForward("update_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		savingsBO = (SavingsBO)TestObjectFactory.getObject(SavingsBO.class,savingsBO.getAccountId());
		assertEquals(savingsBO.getAccountState().getId(),Short.valueOf("18"));
	}

	/*public void testUpdateFailure() throws StatesInitializationException, ServiceException {
		PersonnelPersistenceService personnelPersistenceService = (PersonnelPersistenceService) ServiceFactory.getInstance().getPersistenceService(
				PersistenceServiceName.Personnel);
		createInitialObjects();
		savingsOffering = createSavingsOffering();
		savingsBO = createSavingsAccount("000X00000000013", savingsOffering,
				AccountStates.SAVINGS_ACC_APPROVED);
		savingsBO.initializeStateMachine(userContext.getLocaleId());
		savingsBO.setAccountState(getAccountStateEntityObject(savingsBO.getAccountState().getId()));
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savingsBO,request.getSession());
		AccountNotesEntity accountNotes = new AccountNotesEntity();
		accountNotes.setCommentDate(new Date(System.currentTimeMillis()));
		accountNotes.setPersonnel(personnelPersistenceService.getPersonnel(userContext.getId()));
		accountNotes.setComment("hi");
		SessionUtils.setAttribute(SavingsConstants.ACCOUNT_NOTES,accountNotes,request.getSession());
		SessionUtils.setAttribute(SavingsConstants.FLAG_NAME, "Other", request.getSession());
		setRequestPathInfo("/editSavingsAction.do");
		addRequestParameter("method", "update");
		addRequestParameter("newStatusId","13");
		addRequestParameter("accountId",savingsBO.getAccountId().toString());
		actionPerform();
		verifyActionErrors(new String[]{"error.statuschangenotallowed"});
	}*/

	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group_Active_test", Short
				.valueOf("13"), "1.1.1", center, new Date(System
				.currentTimeMillis()));
	}

	private SavingsOfferingBO createSavingsOffering() {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createSavingsOffering("SavingPrd1", Short
				.valueOf("2"), new Date(System.currentTimeMillis()), Short
				.valueOf("2"), 300.0, Short.valueOf("1"), 1.2, 200.0, 200.0,
				Short.valueOf("2"), Short.valueOf("1"), meetingIntCalc,
				meetingIntPost);
	}

	private SavingsBO createSavingsAccount(String globalAccountNum,
			SavingsOfferingBO savingsOffering, short accountStateId) {
		return TestObjectFactory.createSavingsAccount(globalAccountNum, group,
				accountStateId, new java.util.Date(), savingsOffering, userContext);
	}

	private AccountStateEntity getAccountStateEntityObject(Short id) {
		session = HibernateUtil.getSessionTL();
		Query query = session.createQuery("from org.mifos.application.accounts.business.AccountStateEntity ac_state where ac_state.id=?");
		query.setString(0,id.toString());
		AccountStateEntity accStateEntity = (AccountStateEntity) query.uniqueResult();
		return accStateEntity;
	}

}
