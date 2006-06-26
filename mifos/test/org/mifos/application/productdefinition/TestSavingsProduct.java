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

import org.mifos.application.productdefinition.util.valueobjects.SavingsOffering;
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
public class TestSavingsProduct extends MockStrutsTestCase {

	/**
	 *
	 */
	public TestSavingsProduct() {
		super();
	}

	/**
	 * @param testName
	 */
	public TestSavingsProduct(String testName) {
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
		set.add(Short.valueOf("1"));
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
		setRequestPathInfo("/savingsprdaction.do");
		addRequestParameter("method","load");

		actionPerform();
		assertEquals(((Collection)(request.getAttribute("SavingsApplForList"))).size(),3);
		verifyForward("load_success");
	}

	public void testSuccessfulPreview() {
		testSuccessfulLoad();
		setRequestPathInfo("/savingsprdaction.do");
		addRequestParameter("method","preview");
		addRequestParameter("input","admin");
		addRequestParameter("prdOfferingName","Savings Product");
		addRequestParameter("prdOfferingShortName","SAVP");
		addRequestParameter("description","Savings Product is for Savings Only.");
		addRequestParameter("prdCategory.productCategoryID","2");
		addRequestParameter("startDate","05/02/2006");
		addRequestParameter("endDate","12/31/2006");
		addRequestParameter("prdApplicableMaster.prdApplicableMasterId","1");
		addRequestParameter("savingsType.savingsTypeId","1");
		addRequestParameter("recommendedAmount","1200");
		addRequestParameter("recommendedAmntUnit.recommendedAmntUnitId","1");
		addRequestParameter("maxAmntWithdrawl","1500");
		addRequestParameter("interestRate","19");
		addRequestParameter("interestCalcType.interestCalculationTypeID","1");
		addRequestParameter("timeForInterestCacl","1");
		addRequestParameter("freqOfInterest","1");
		addRequestParameter("minAmntForInt","1200");

		addRequestParameter("prdStatus.offeringStatusId","5");
		actionPerform();

		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("create_preview");
	}

	public void testSuccessfulCreate() {
		testSuccessfulPreview();

		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		SavingsOffering savingsOffering=null;
		if(null != context) {
			savingsOffering =(SavingsOffering)context.getValueObject();
		}
		if(null != savingsOffering) {
			setRequestPathInfo("/savingsprdaction.do");
			addRequestParameter("method","create");
		}

		actionPerform();

		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("create_success");

		SavingsOffering savOffer=retriveValueObject("Savings Product");
		setRequestPathInfo("/savingsprdaction.do");
		addRequestParameter("prdOfferingId",String.valueOf(savOffer.getPrdOfferingId()));
		addRequestParameter("method","get");
		actionPerform();

		SavingsOffering savingsOffer=(SavingsOffering)request.getAttribute("SavingsOffering");

		assertEquals(savingsOffer.getPrdOfferingName(),"Savings Product");
		assertEquals(savingsOffer.getStartDate(),Date.valueOf("2006-05-02"));
		assertEquals(savingsOffer.getPrdCategory().getProductCategoryID(),Short.valueOf("2"));
		assertEquals(((Collection)(request.getAttribute("SavingsApplForList"))).size(),3);

	}

	public void testSuccessfulGet() {
		SavingsOffering savOffer=retriveValueObject("Savings Product");
		setRequestPathInfo("/savingsprdaction.do");
		addRequestParameter("prdOfferingId",String.valueOf(savOffer.getPrdOfferingId()));
		addRequestParameter("method","get");
		actionPerform();

		SavingsOffering savingsOffering=(SavingsOffering)request.getAttribute("SavingsOffering");

		assertEquals(savingsOffering.getPrdOfferingName(),"Savings Product");
		assertEquals(savingsOffering.getStartDate(),Date.valueOf("2006-05-02"));
		assertEquals(savingsOffering.getPrdCategory().getProductCategoryID(),Short.valueOf("2"));
		assertEquals(((Collection)(request.getAttribute("SavingsApplForList"))).size(),3);
		verifyForward("get_success");
	}

	public void testSuccessfulManage() {
		testSuccessfulGet();
		setRequestPathInfo("/savingsprdaction.do");
		addRequestParameter("method","manage");

		actionPerform();

		SavingsOffering savingsOffering=(SavingsOffering)request.getAttribute("SavingsOffering");

		assertEquals(savingsOffering.getPrdOfferingName(),"Savings Product");
		assertEquals(savingsOffering.getStartDate(),Date.valueOf("2006-05-02"));
		assertEquals(savingsOffering.getPrdCategory().getProductCategoryID(),Short.valueOf("2"));
		assertEquals(((Collection)(request.getAttribute("SavingsApplForList"))).size(),3);

		verifyForward("manage_success");
	}

	public void testSuccessfulManagePreview() {
		SavingsOffering savOffer=retriveValueObject("Savings Product");

		testSuccessfulManage();
		setRequestPathInfo("/savingsprdaction.do");
		addRequestParameter("method","preview");
		addRequestParameter("input","details");
		addRequestParameter("prdOfferingName","Savings Products"+savOffer.getPrdOfferingId());
		addRequestParameter("prdOfferingShortName",String.valueOf(savOffer.getPrdOfferingId()));
		addRequestParameter("description","Savings Product is for Savings Only");
		addRequestParameter("prdCategory.productCategoryID","2");
		addRequestParameter("startDate","05/02/2006");
		addRequestParameter("endDate","12/31/2006");
		addRequestParameter("prdApplicableMaster.prdApplicableMasterId","1");
		addRequestParameter("savingsType.savingsTypeId","1");
		addRequestParameter("recommendedAmount","1200");
		addRequestParameter("recommendedAmntUnit.recommendedAmntUnitId","1");
		addRequestParameter("maxAmntWithdrawl","1500");
		addRequestParameter("interestRate","19");
		addRequestParameter("interestCalcType.interestCalculationTypeID","1");
		addRequestParameter("timeForInterestCacl","1");
		addRequestParameter("freqOfInterest","1");
		addRequestParameter("minAmntForInt","1200");

		addRequestParameter("prdStatus.offeringStatusId","5");
		actionPerform();

		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("manage_preview");
	}

	public void testSuccessfulUpdate() {
		testSuccessfulManagePreview();

		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		SavingsOffering savingsOffering=null;
		if(null != context) {
			savingsOffering =(SavingsOffering)context.getValueObject();
		}
		if(null != savingsOffering) {
			setRequestPathInfo("/savingsprdaction.do");
			addRequestParameter("method","update");
		}
		actionPerform();

		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("update_success");

		SavingsOffering savOffer=(SavingsOffering)request.getAttribute("SavingsOffering");
		setRequestPathInfo("/savingsprdaction.do");
		addRequestParameter("prdOfferingId",String.valueOf(savOffer.getPrdOfferingId()));
		addRequestParameter("method","get");
		actionPerform();

		SavingsOffering savingsOffer=(SavingsOffering)request.getAttribute("SavingsOffering");

		assertEquals(savingsOffer.getStartDate(),Date.valueOf("2006-05-02"));
		assertEquals(savingsOffer.getPrdCategory().getProductCategoryID(),Short.valueOf("2"));
		assertEquals(((Collection)(request.getAttribute("SavingsApplForList"))).size(),3);

	}

	public void testSuccessfulViewAllProductCategories() {
		setRequestPathInfo("/savingsprdaction.do");
		addRequestParameter("method","search");
		addRequestParameter("searchNode(search_name)","SavingsProducts");
		actionPerform();

		verifyForward("search_success");
	}

	private SavingsOffering retriveValueObject(String savingsOfferingName)
	{
		Session session = null;
		try
		{
		 session = HibernateUtil.getSession();

	    Query query = session.createQuery("from org.mifos.application.productdefinition.util.valueobjects.SavingsOffering savingsOffering where savingsOffering.prdOfferingName = ?" );
		query.setString(0,savingsOfferingName);
		SavingsOffering savingsOffering = (SavingsOffering)query.uniqueResult();

		return savingsOffering;
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
