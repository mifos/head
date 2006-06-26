/**
 *
 */
package org.mifos.application.productdefinition;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.productdefinition.util.valueobjects.ProductCategory;
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
public class TestProductCategory extends MockStrutsTestCase {

	/**
	 *
	 */
	public TestProductCategory() {
		super();
	}

	/**
	 * @param testName
	 */
	public TestProductCategory(String testName) {
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
		setRequestPathInfo("/mifosproddefaction.do");
		addRequestParameter("method","load");
		actionPerform();
		assertEquals(((Collection)(request.getAttribute("ProductType"))).size(),2);

		verifyForward("load_success");
	}

	public void testSuccessfulPreview() {
		setRequestPathInfo("/mifosproddefaction.do");
		addRequestParameter("method","preview");
		addRequestParameter("input","admin");

		addRequestParameter("prdCategoryStatus.prdCategoryStatusId","1");
		addRequestParameter("productType.productTypeID","1");
		addRequestParameter("productCategoryName","Loan Product");
		addRequestParameter("productCategoryDesc","Loan Product is for Loans Only");

		actionPerform();

		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("create_preview");
	}

	public void testSuccessfulCreate() {
		testSuccessfulLoad();
		testSuccessfulPreview();

		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		ProductCategory productCategory=null;
		if(null != context) {
			productCategory =(ProductCategory)context.getValueObject();
		}
		if(null != productCategory) {
			setRequestPathInfo("/mifosproddefaction.do");
			addRequestParameter("method","create");
			addRequestParameter("productType.productTypeID","1");
			addRequestParameter("productType.versionNo","1");
		}


		actionPerform();

		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("create_success");

		ProductCategory prdCat=retriveValueObject("Loan Product");
		setRequestPathInfo("/mifosproddefaction.do");
		addRequestParameter("method","get");
		addRequestParameter("productCategoryID",String.valueOf(prdCat.getProductCategoryID()));
		actionPerform();

		ProductCategory prdCategory=(ProductCategory)request.getAttribute("ProductCategory");

		assertEquals(prdCategory.getProductCategoryName(),"Loan Product");
		assertEquals(prdCategory.getProductCategoryDesc(),"Loan Product is for Loans Only");
		assertEquals(prdCategory.getProductType().getProductTypeID(),Short.valueOf("1"));

	}

	public void testSuccessfulGet() {
		ProductCategory prdCategory=retriveValueObject("Loan Product");
		setRequestPathInfo("/mifosproddefaction.do");
		addRequestParameter("method","get");
		addRequestParameter("productCategoryID",String.valueOf(prdCategory.getProductCategoryID()));
		actionPerform();

		ProductCategory productCategory=(ProductCategory)request.getAttribute("ProductCategory");

		assertEquals(productCategory.getProductCategoryName(),"Loan Product");
		assertEquals(productCategory.getProductCategoryDesc(),"Loan Product is for Loans Only");
		assertEquals(productCategory.getProductType().getProductTypeID(),Short.valueOf("1"));

		verifyForward("get_success");
	}

	public void testSuccessfulManage() {
		testSuccessfulGet();
		setRequestPathInfo("/mifosproddefaction.do");
		addRequestParameter("method","manage");

		actionPerform();

		ProductCategory productCategory=(ProductCategory)request.getAttribute("ProductCategory");

		assertEquals(productCategory.getProductCategoryName(),"Loan Product");
		assertEquals(productCategory.getProductCategoryDesc(),"Loan Product is for Loans Only");
		assertEquals(productCategory.getProductType().getProductTypeID(),Short.valueOf("1"));
		assertEquals(((Collection)(request.getAttribute("ProductType"))).size(),2);
		assertEquals(((Collection)(request.getAttribute("PrdCategoryStatusList"))).size(),2);

		verifyForward("manage_success");
	}

	public void testSuccessfulManagePreview() {
		testSuccessfulManage();
		setRequestPathInfo("/mifosproddefaction.do");
		addRequestParameter("method","preview");
		addRequestParameter("input","details");
		addRequestParameter("prdCategoryStatus.prdCategoryStatusId","1");
		addRequestParameter("productType.productTypeID","1");

		ProductCategory prdCategory=retriveValueObject("Loan Product");
		addRequestParameter("productCategoryName","Loan Products"+prdCategory.getProductCategoryID());
		addRequestParameter("productCategoryDesc","Loan Product is for Loans Only.");

		actionPerform();

		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("manage_preview");
	}

	public void testSuccessfulUpdate() {
		testSuccessfulManagePreview();

		addRequestParameter("productType.productTypeID","1");
		addRequestParameter("productType.versionNo","1");

		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		ProductCategory productCategory=null;
		if(null != context) {
			productCategory =(ProductCategory)context.getValueObject();
		}
		if(null != productCategory) {
			setRequestPathInfo("/mifosproddefaction.do");
			addRequestParameter("method","update");
		}
		actionPerform();

		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("update_success");

		ProductCategory prdCat=(ProductCategory)request.getAttribute("ProductCategory");
		setRequestPathInfo("/mifosproddefaction.do");
		addRequestParameter("method","get");
		addRequestParameter("productCategoryID",String.valueOf(prdCat.getProductCategoryID()));

		actionPerform();

		ProductCategory prdCategory=(ProductCategory)request.getAttribute("ProductCategory");

		assertEquals(prdCategory.getProductCategoryDesc(),"Loan Product is for Loans Only.");
		assertEquals(prdCategory.getProductType().getProductTypeID(),Short.valueOf("1"));
	}

	public void testSuccessfulViewAllProductCategories() {
		setRequestPathInfo("/mifosproddefaction.do");
		addRequestParameter("method","search");
		addRequestParameter("searchNode(search_name)","ProductCategories");
		actionPerform();

		verifyForward("search_success");
	}

	private ProductCategory retriveValueObject(String productCategoryName)
	{
		Session session = null;
		try
		{
		 session = HibernateUtil.getSession();

	    Query query = session.createQuery("from org.mifos.application.productdefinition.util.valueobjects.ProductCategory productCategory where productCategory.productCategoryName = ?" );
		query.setString(0,productCategoryName);
		ProductCategory productCategory = (ProductCategory)query.uniqueResult();

		return productCategory;
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
