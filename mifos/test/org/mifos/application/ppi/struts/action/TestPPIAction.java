package org.mifos.application.ppi.struts.action;

import java.util.List;

import org.hibernate.Session;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.audit.util.helpers.AuditInterceptor;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.SessionHolder;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.PPICalculator;
import org.mifos.framework.MifosMockStrutsTestCase;

import org.mifos.application.ppi.business.PPISurvey;
import org.mifos.application.ppi.business.PPIChoice;
import org.mifos.application.ppi.helpers.Country;
import org.mifos.application.ppi.persistence.PPIPersistence;
import org.mifos.application.surveys.helpers.SurveyState;


public class TestPPIAction extends MifosMockStrutsTestCase {
	
	private TestDatabase database; 
	private Session session;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		database = TestDatabase.makeStandard();
		HibernateUtil.closeSession();
		AuditInterceptor interceptor = new AuditInterceptor();
		session = database.openSession(interceptor);
		SessionHolder holder = new SessionHolder(session);
		holder.setInterceptor(interceptor);
		HibernateUtil.setThreadLocal(holder);
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
				.getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/ppi/struts-config.xml")
				.getPath());
		UserContext userContext = TestUtils.makeUser();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
	}
	
	@Override
	protected void tearDown() throws Exception {
		HibernateUtil.resetDatabase();
		super.tearDown();
	}
	
	public void testConfigureNoExistingPPISurvey() throws Exception {
		setRequestPathInfo("/ppiAction");
		addRequestParameter("method","configure");
		actionPerform();
		verifyNoActionMessages();
		
		List<Country> countryList = (List<Country>)request.getAttribute("countries");
		assertEquals(Country.values().length, countryList.size());
	}
	
	public void testPreviewNoExistingPPISurvey() throws Exception {
		setRequestPathInfo("/ppiAction");
		addRequestParameter("method","configure");
		actionPerform();
		verifyNoActionMessages();
		
		addRequestParameter("value(country)", Country.INDIA.toString());
		addRequestParameter("value(state)", SurveyState.ACTIVE.toString());
		addRequestParameter("value(veryPoorMin)", "0");
		addRequestParameter("value(veryPoorMax)", "24");
		addRequestParameter("value(poorMin)", "25");
		addRequestParameter("value(poorMax)", "49");
		addRequestParameter("value(atRiskMin)", "50");
		addRequestParameter("value(atRiskMax)", "74");
		addRequestParameter("value(nonPoorMin)", "75");
		addRequestParameter("value(nonPoorMax)", "100");
		addRequestParameter("method","preview");
		actionPerform();
		verifyNoActionErrors();
		
		//PPISurvey survey = (PPISurvey)request.getSession().getAttribute("ppiSurvey");
		//assertTrue(PPICalculator.scoreLimitsAreValid(survey));
	}
	
	public void testCreateUpdateNoExistingPPISurvey() throws Exception {
		setRequestPathInfo("/ppiAction");
		addRequestParameter("method","configure");
		actionPerform();
		verifyNoActionMessages();
		
		addRequestParameter("value(country)", Country.INDIA.toString());
		addRequestParameter("value(state)", SurveyState.ACTIVE.toString());
		addRequestParameter("value(veryPoorMin)", "0");
		addRequestParameter("value(veryPoorMax)", "24");
		addRequestParameter("value(poorMin)", "25");
		addRequestParameter("value(poorMax)", "49");
		addRequestParameter("value(atRiskMin)", "50");
		addRequestParameter("value(atRiskMax)", "74");
		addRequestParameter("value(nonPoorMin)", "75");
		addRequestParameter("value(nonPoorMax)", "100");
		addRequestParameter("method","preview");
		actionPerform();
		verifyNoActionErrors();
		
		addRequestParameter("method","update");
		actionPerform();
		verifyNoActionErrors();
		
		PPIPersistence ppiPersistence = new PPIPersistence();
		System.out.println(ppiPersistence.retrieveAllPPISurveys().get(0).getName());
		assertEquals(1, ppiPersistence.retrieveAllPPISurveys().size());
	}
	
	public void testCreatUpdateWithExistingPPISurvey() throws Exception {
		setRequestPathInfo("/ppiAction");
		addRequestParameter("method","configure");
		actionPerform();
		verifyNoActionMessages();
		
		addRequestParameter("value(country)", Country.INDIA.toString());
		addRequestParameter("value(state)", SurveyState.ACTIVE.toString());
		addRequestParameter("value(veryPoorMin)", "0");
		addRequestParameter("value(veryPoorMax)", "24");
		addRequestParameter("value(poorMin)", "25");
		addRequestParameter("value(poorMax)", "49");
		addRequestParameter("value(atRiskMin)", "50");
		addRequestParameter("value(atRiskMax)", "74");
		addRequestParameter("value(nonPoorMin)", "75");
		addRequestParameter("value(nonPoorMax)", "100");
		addRequestParameter("method","preview");
		actionPerform();
		verifyNoActionErrors();
		
		addRequestParameter("method","update");
		actionPerform();
		verifyNoActionErrors();
		
		setRequestPathInfo("/ppiAction");
		addRequestParameter("method","configure");
		actionPerform();
		verifyNoActionMessages();
		
		addRequestParameter("value(country)", Country.INDIA.toString());
		addRequestParameter("value(state)", SurveyState.ACTIVE.toString());
		addRequestParameter("value(veryPoorMin)", "0");
		addRequestParameter("value(veryPoorMax)", "24");
		addRequestParameter("value(poorMin)", "25");
		addRequestParameter("value(poorMax)", "49");
		addRequestParameter("value(atRiskMin)", "50");
		addRequestParameter("value(atRiskMax)", "74");
		addRequestParameter("value(nonPoorMin)", "75");
		addRequestParameter("value(nonPoorMax)", "100");
		addRequestParameter("method","preview");
		actionPerform();
		verifyNoActionErrors();
		
		addRequestParameter("method","update");
		actionPerform();
		verifyNoActionErrors();
		
		PPIPersistence ppiPersistence = new PPIPersistence();
		assertEquals(1, ppiPersistence.retrieveAllPPISurveys().size());
	}
}
