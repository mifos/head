/**
 *
 */
package org.mifos.application.productdefinition;

import java.net.URISyntaxException;
import java.sql.Date;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.productdefinition.util.valueobjects.LoanOffering;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.valueobjects.Context;

import servletunit.struts.MockStrutsTestCase;

/**
 * @author mohammedn
 *
 */
public class TestLoanProduct extends MockStrutsTestCase {

	/**
	 *
	 */
	public TestLoanProduct() {
		super();
	}

	/**
	 * @param testName
	 */
	public TestLoanProduct(String testName) {
		super(testName);
	}

	protected void setUp() throws Exception {
		super.setUp();
		try {
			setServletConfigFile(ResourceLoader.getURI("org/mifos/META-INF/web.xml").getPath());
			setConfigFile(ResourceLoader.getURI("org/mifos/META-INF/struts-config.xml").getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		UserContext userContext=new UserContext();
		userContext.setId(new Short("1"));
		userContext.setLocaleId(new Short("1"));
		Set set= new HashSet();
		set.add(new Short("1"));
		userContext.setRoles(set);
		userContext.setLevelId(Short.valueOf("2"));
		userContext.setName("mifos");
		userContext.setPereferedLocale(new Locale("en","US"));
		userContext.setBranchId(new Short("1"));
        request.getSession().setAttribute(Constants.USERCONTEXT,userContext);
        addRequestParameter("recordLoanOfficerId","1");
		addRequestParameter("recordOfficeId","1");
		ActivityContext ac = new ActivityContext((short)0,userContext.getBranchId().shortValue(),userContext.getId().shortValue());
		request.getSession(false).setAttribute(LoginConstants.ACTIVITYCONTEXT,ac);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testSuccessfulLoad(){
		setRequestPathInfo("/loanprdaction.do");
		addRequestParameter("method","load");
		actionPerform();

		assertEquals(((Collection)(request.getAttribute("LoanApplForList"))).size(),2);
		assertEquals(((Collection)(request.getAttribute("YesNoMasterList"))).size(),2);
		assertEquals(((Collection)(request.getAttribute("InterestTypesList"))).size(),1);

		verifyForward("load_success");
	}

	public void testSuccessfulPreview() {
		testSuccessfulLoad();
		setRequestPathInfo("/loanprdaction.do");
		addRequestParameter("method","preview");
		addRequestParameter("input","admin");

		addRequestParameter("prdOfferingName","Education Loan");
		addRequestParameter("prdOfferingShortName","EDUL");
		addRequestParameter("description","Education Loan is for Education Only.");
		addRequestParameter("prdCategory.productCategoryID","1");
		addRequestParameter("startDate","05/02/2006");
		addRequestParameter("endDate","12/31/2006");
		addRequestParameter("prdApplicableMaster.prdApplicableMasterId","1");
		addRequestParameter("loanCounterFlag","1");
		addRequestParameter("minLoanAmount","1200");
		addRequestParameter("maxLoanAmount","1500");
		addRequestParameter("defaultLoanAmount","1350");
		addRequestParameter("interestTypes.id","1");
		addRequestParameter("maxInterestRate","120");
		addRequestParameter("minInterestRate","101");
		addRequestParameter("defInterestRate","110");
		addRequestParameter("interestCalcRule.interestCalcRuleId","1");
		addRequestParameter("freqOfInstallments","1");
		addRequestParameter("recurWeekDay","1");
		addRequestParameter("maxNoInstallments","120");
		addRequestParameter("minNoInstallments","100");
		addRequestParameter("defNoInstallments","110");
		addRequestParameter("intDedDisbursementFlag","1");
		addRequestParameter("gracePeriodType.id","2");

		addRequestParameter("gracePeriodDuration","2");
		addRequestParameter("prdOfferinFees","1");
		addRequestParameter("penalty.penaltyID","1");
		addRequestParameter("penaltyRate","23");
		addRequestParameter("penaltyGrace","10");
		addRequestParameter("loanOfferingFunds","1");

		addRequestParameter("prdStatus.offeringStatusId","4");
		actionPerform();

		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("create_preview");
	}

	public void testSuccessfulCreate() {
		testSuccessfulPreview();

		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		LoanOffering loanOffering=null;
		if(null != context) {
			loanOffering =(LoanOffering)context.getValueObject();
		}
		if(null != loanOffering) {
			setRequestPathInfo("/loanprdaction.do");
			addRequestParameter("method","create");
		}

		actionPerform();

		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("create_success");

		LoanOffering loanOff=retriveValueObject("Education Loan");
		setRequestPathInfo("/loanprdaction.do");
		addRequestParameter("method","get");
		addRequestParameter("prdOfferingId",String.valueOf(loanOff.getPrdOfferingId()));
		actionPerform();

		LoanOffering loanOffer=(LoanOffering)request.getAttribute("LoanOffering");

		assertEquals(loanOffer.getPrdOfferingName(),"Education Loan");
		assertEquals(loanOffer.getStartDate(),Date.valueOf("2006-05-02"));
		assertEquals(loanOffer.getPrdCategory().getProductCategoryID(),Short.valueOf("1"));
		assertEquals(((Collection)(request.getAttribute("LoanApplForList"))).size(),2);
		assertEquals(((Collection)(request.getAttribute("YesNoMasterList"))).size(),2);
	}

	public void testSuccessfulGet() {
		LoanOffering loanOff=retriveValueObject("Education Loan");
		setRequestPathInfo("/loanprdaction.do");
		addRequestParameter("method","get");
		addRequestParameter("prdOfferingId",String.valueOf(loanOff.getPrdOfferingId()));


		actionPerform();

		LoanOffering loanOffering=(LoanOffering)request.getAttribute("LoanOffering");


		assertEquals(loanOffering.getPrdOfferingName(),"Education Loan");
		assertEquals(loanOffering.getStartDate(),Date.valueOf("2006-05-02"));
		assertEquals(loanOffering.getPrdCategory().getProductCategoryID(),Short.valueOf("1"));
		assertEquals(((Collection)(request.getAttribute("LoanApplForList"))).size(),2);
		assertEquals(((Collection)(request.getAttribute("YesNoMasterList"))).size(),2);
		assertEquals(((Collection)(request.getAttribute("InterestTypesList"))).size(),1);

		verifyForward("get_success");
	}

	public void testSuccessfulManage() {
		testSuccessfulGet();
		setRequestPathInfo("/loanprdaction.do");
		addRequestParameter("method","manage");

		actionPerform();

		LoanOffering loanOffering=(LoanOffering)request.getAttribute("LoanOffering");

		assertEquals(loanOffering.getPrdOfferingName(),"Education Loan");
		assertEquals(loanOffering.getStartDate(),Date.valueOf("2006-05-02"));
		assertEquals(loanOffering.getPrdCategory().getProductCategoryID(),Short.valueOf("1"));
		assertEquals(((Collection)(request.getAttribute("LoanApplForList"))).size(),2);
		assertEquals(((Collection)(request.getAttribute("YesNoMasterList"))).size(),2);
		assertEquals(((Collection)(request.getAttribute("InterestTypesList"))).size(),1);

		verifyForward("manage_success");
	}

	public void testSuccessfulManagePreview() {
		testSuccessfulManage();
		setRequestPathInfo("/loanprdaction.do");
		addRequestParameter("method","preview");
		addRequestParameter("input","details");

		LoanOffering loanOff=retriveValueObject("Education Loan");
		addRequestParameter("prdOfferingName","Education Loans"+loanOff.getPrdOfferingId());
		addRequestParameter("prdOfferingShortName",String.valueOf(loanOff.getPrdOfferingId()));
		addRequestParameter("description","Education Loan is for Education Only");
		addRequestParameter("prdCategory.productCategoryID","1");
		addRequestParameter("startDate","05/02/2006");
		addRequestParameter("endDate","12/31/2006");
		addRequestParameter("prdApplicableMaster.prdApplicableMasterId","1");
		addRequestParameter("loanCounterFlag","1");
		addRequestParameter("minLoanAmount","1200");
		addRequestParameter("maxLoanAmount","1500");
		addRequestParameter("defaultLoanAmount","1350");
		addRequestParameter("interestTypes.id","1");
		addRequestParameter("maxInterestRate","120");
		addRequestParameter("minInterestRate","101");
		addRequestParameter("defInterestRate","110");
		addRequestParameter("interestCalcRule.interestCalcRuleId","1");
		addRequestParameter("freqOfInstallments","1");
		addRequestParameter("recurWeekDay","1");
		addRequestParameter("maxNoInstallments","120");
		addRequestParameter("minNoInstallments","100");
		addRequestParameter("defNoInstallments","110");
		addRequestParameter("intDedDisbursementFlag","1");
		addRequestParameter("gracePeriodType.id","2");

		addRequestParameter("gracePeriodDuration","2");
		addRequestParameter("prdOfferinFees","1");
		addRequestParameter("prdOfferinFees","2");
		addRequestParameter("penalty.penaltyID","1");
		addRequestParameter("penaltyRate","23");
		addRequestParameter("penaltyGrace","10");
		addRequestParameter("loanOfferingFunds","1");
		addRequestParameter("loanOfferingFunds","2");

		addRequestParameter("prdStatus.offeringStatusId","1");
		actionPerform();

		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("manage_preview");
	}

	public void testSuccessfulUpdate() {
		testSuccessfulManagePreview();

		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		LoanOffering loanOffering=null;
		if(null != context) {
			loanOffering =(LoanOffering)context.getValueObject();
		}
		if(null != loanOffering) {
			setRequestPathInfo("/loanprdaction.do");
			addRequestParameter("method","update");
		}
		actionPerform();

		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("update_success");

		LoanOffering loanOff=(LoanOffering)request.getAttribute("LoanOffering");
		setRequestPathInfo("/loanprdaction.do");
		addRequestParameter("method","get");
		addRequestParameter("prdOfferingId",String.valueOf(loanOff.getPrdOfferingId()));
		actionPerform();

		LoanOffering loanOffer=(LoanOffering)request.getAttribute("LoanOffering");


		assertEquals(loanOffer.getStartDate(),Date.valueOf("2006-05-02"));
		assertEquals(loanOffer.getPrdCategory().getProductCategoryID(),Short.valueOf("1"));
		assertEquals(((Collection)(request.getAttribute("LoanApplForList"))).size(),2);
		assertEquals(((Collection)(request.getAttribute("YesNoMasterList"))).size(),2);
	}

	public void testSuccessfulViewAllProductCategories() {
		setRequestPathInfo("/loanprdaction.do");
		addRequestParameter("method","search");
		addRequestParameter("searchNode(search_name)","LoanProducts");
		actionPerform();

		verifyForward("search_success");
	}

	private LoanOffering retriveValueObject(String loanOfferingName)
	{
		Session session = null;
		try
		{
		 session = HibernateUtil.getSession();

	    Query query = session.createQuery("from org.mifos.application.productdefinition.util.valueobjects.LoanOffering loanOffering where loanOffering.prdOfferingName = ?" );
		query.setString(0,loanOfferingName);
		LoanOffering loanOffering = (LoanOffering)query.uniqueResult();

		return loanOffering;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			try
			{
			 HibernateUtil.closeSession(session);
			}
			catch(Exception e)
			{

			}
		}

	}

}

