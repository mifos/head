package org.mifos.application.fees;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.fees.util.valueobjects.Fees;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;

import servletunit.struts.MockStrutsTestCase;

public class TestFees extends MockStrutsTestCase{

	public TestFees(String testName){
		super(testName);
	}

	//success
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

    //success
	public void testSuccessfulLoad(){
		setRequestPathInfo("/feesAction.do");
		addRequestParameter("method","load");
        actionPerform();
		verifyForward("createFees");
	}


	//success
	public void testSuccessfulPreview() {
		testSuccessfulLoad();
		setRequestPathInfo("/feesAction.do");
		addRequestParameter("method","preview");
		addRequestParameter("input","createFees");
		addRequestParameter("feeName", "AditiTech");
		addRequestParameter("categoryId", "1");
		addRequestParameter("typeId", "2");
		addRequestParameter("feeFrequency.feePaymentId", "1");
		addRequestParameter("amount", "100");
		addRequestParameter("adminCheck", "No");
		actionPerform();
		verifyForward("createFeesPreview");

	}

	//success
	public void testSuccessfulCreation() {
		testSuccessfulLoad();
		testSuccessfulPreview();
		setRequestPathInfo("/feesAction.do");
		addRequestParameter("method","create");
		addRequestParameter("input","");

		actionPerform();
		verifyForward("createFeesConfirmation");
	}

	//success
	public void testSuccessfulGet() {
		Short feeId=retriveValueObject("AditiTech");
		setRequestPathInfo("/feesAction.do");
		addRequestParameter("method","get");
		addRequestParameter("input","createFeesConformation");
		addRequestParameter("feeIdTemp",String.valueOf(feeId));
		actionPerform();
		verifyForward("feeDetails");
	}


	//success
	public void testSuccessfulEdit() {
		Short feeId=retriveValueObject("AditiTech");
		testSuccessfulGet();
		setRequestPathInfo("/feesAction.do");
		addRequestParameter("method","manage");
		addRequestParameter("input","");
		addRequestParameter("feeId", String.valueOf(feeId));
		actionPerform();
		verifyForward("editFeeDetails");

	}


	//success
	public void testSuccessfulEditPreview() {
		Short feeId=retriveValueObject("AditiTech");
		testSuccessfulEdit();
		setRequestPathInfo("/feesAction.do");
		addRequestParameter("method","preview");
		addRequestParameter("input","editFeeDetails");
		addRequestParameter("status", "2");
		addRequestParameter("feeId", String.valueOf(feeId));
		addRequestParameter("versionNo", "");
		addRequestParameter("feeName", "AditiTech");
		addRequestParameter("categoryId", "1");
		addRequestParameter("typeId", "2");
		addRequestParameter("feePaymentId", "1");
		addRequestParameter("amount", "100");
		addRequestParameter("formulaId", "");
		addRequestParameter("feeFrequencyTypeId", "1");
		addRequestParameter("rateFlatFalg", "0");
		addRequestParameter("feeIdTemp", "");
		addRequestParameter("frequency", "66");
		verifyNoActionErrors() ;
		verifyNoActionMessages();
		actionPerform();
		verifyForward("previewFeeDetails");
	}



	//success
	public void testSuccessfulOfficeUpdate() {
		Short feeId=retriveValueObject("AditiTech");
		testSuccessfulEditPreview();
		setRequestPathInfo("/feesAction.do");
		addRequestParameter("method","update");
		addRequestParameter("versionNo","");
		addRequestParameter("feeId",String.valueOf(feeId));
		addRequestParameter("rateFlatFalg", "0");
		addRequestParameter("feeIdTemp", "48");
		actionPerform();
		verifyForward("update_success");

	}

	private Short retriveValueObject(String feeName)
	{
		Session session = null;
		try
		{
		 session = HibernateUtil.getSession();

	    Query query = session.createQuery("select max(fees.feeId) from org.mifos.application.fees.util.valueobjects.Fees fees where fees.feeName = ?" );
		query.setString(0,feeName);
		Short feeId = (Short)query.uniqueResult();

		return feeId;
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
