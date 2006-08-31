package org.mifos.application.productdefinition.struts.action;
		
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.productdefinition.business.PrdCategoryStatusEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.business.ProductTypeEntity;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.persistence.ProductCategoryPersistence;
import org.mifos.application.productdefinition.struts.actionforms.PrdCategoryActionForm;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestPrdCategoryAction extends MifosMockStrutsTestCase{
	
	private UserContext userContext;
	
	private ProductCategoryPersistence productCategoryPersistence;
	
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
		userContext = TestObjectFactory.getUserContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		productCategoryPersistence=new ProductCategoryPersistence();
		
	}
	
	@Override
	protected void tearDown() throws Exception {
		productCategoryPersistence=null;
		userContext=null;
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	
	public void testLoad() throws PersistenceException{
		setRequestPathInfo("/productCategoryAction.do");
		addRequestParameter("method", "load");
		actionPerform();
		verifyForward("load_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals(2,((List<ProductTypeEntity>)SessionUtils.getAttribute(ProductDefinitionConstants.PRODUCTTYPELIST, request.getSession())).size());
	}
	
	public void testCreatePreview() throws PersistenceException{
		SessionUtils.setAttribute(ProductDefinitionConstants.PRODUCTTYPELIST,
				getProductTypes(userContext), request.getSession());
		setRequestPathInfo("/productCategoryAction.do");
		addRequestParameter("method", "createPreview");
		addRequestParameter("productType", "1");
		addRequestParameter("productCategoryName", "product category");
		addRequestParameter("productCategoryDesc", "created a category");
		addRequestParameter("productCategoryStatus", "1");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals(2,((List<ProductTypeEntity>)SessionUtils.getAttribute(ProductDefinitionConstants.PRODUCTTYPELIST, request.getSession())).size());
	}
	
	public void testCreatePrevious() throws PersistenceException{
		SessionUtils.setAttribute(ProductDefinitionConstants.PRODUCTTYPELIST,
				getProductTypes(userContext), request.getSession());
		setRequestPathInfo("/productCategoryAction.do");
		addRequestParameter("method", "createPrevious");
		addRequestParameter("productType", "1");
		addRequestParameter("productCategoryName", "product category");
		addRequestParameter("productCategoryDesc", "created a category");
		addRequestParameter("productCategoryStatus", "1");
		actionPerform();
		verifyForward("previous_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals(2,((List<ProductTypeEntity>)SessionUtils.getAttribute(ProductDefinitionConstants.PRODUCTTYPELIST, request.getSession())).size());
	}

	
	public void testCreate() throws PersistenceException{
		SessionUtils.setAttribute(ProductDefinitionConstants.PRODUCTTYPELIST,
				getProductTypes(userContext), request.getSession());
		setRequestPathInfo("/productCategoryAction.do");
		addRequestParameter("method", "create");
		addRequestParameter("productType", "1");
		addRequestParameter("productCategoryName", "product category");
		addRequestParameter("productCategoryDesc", "created a category");
		addRequestParameter("productCategoryStatus", "1");
		actionPerform();
		verifyForward("create_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals(2,((List<ProductTypeEntity>)SessionUtils.getAttribute(ProductDefinitionConstants.PRODUCTTYPELIST, request.getSession())).size());
		ProductCategoryBO productCategoryBO=getProductCategory().get(2);
		assertEquals("product category",productCategoryBO.getProductCategoryName());
		assertEquals("created a category",productCategoryBO.getProductCategoryDesc());
		deleteProductCategory(productCategoryBO);
	}
	
	
	public void testGet() throws Exception{
		ProductCategoryBO productCategoryBO =new ProductCategoryBO(userContext,getProductTypes(userContext).get(0),"product category","created a category");
		productCategoryBO.save();
		setRequestPathInfo("/productCategoryAction.do");
		addRequestParameter("method", "get");
		addRequestParameter("globalPrdCategoryNum", productCategoryBO.getGlobalPrdCategoryNum());
		actionPerform();
		verifyForward("get_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals(2,((List<ProductTypeEntity>)SessionUtils.getAttribute(ProductDefinitionConstants.PRODUCTTYPELIST, request.getSession())).size());
		productCategoryBO=getProductCategory().get(2);
		deleteProductCategory(productCategoryBO);
	}
	
	
	public void testManage() throws Exception {
		ProductCategoryBO productCategoryBO =new ProductCategoryBO(userContext,getProductTypes(userContext).get(0),"product category","created a category");
		productCategoryBO.save();
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, productCategoryBO,request.getSession());
		setRequestPathInfo("/productCategoryAction.do");
		addRequestParameter("method", "manage");
		actionPerform();
		verifyForward("manage_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals(2,((List<PrdCategoryStatusEntity>)SessionUtils.getAttribute(ProductDefinitionConstants.PRDCATEGORYSTATUSLIST, request.getSession())).size());
		productCategoryBO=getProductCategory().get(2);
		assertEquals("product category",((PrdCategoryActionForm)getActionForm()).getProductCategoryName());
		assertEquals("created a category",((PrdCategoryActionForm)getActionForm()).getProductCategoryDesc());
		deleteProductCategory(productCategoryBO);
	}
	
	public void testManagePreview()	throws Exception{
		ProductCategoryBO productCategoryBO =new ProductCategoryBO(userContext,getProductTypes(userContext).get(0),"product category","created a category");
		productCategoryBO.save();
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, productCategoryBO,request.getSession());
		setRequestPathInfo("/productCategoryAction.do");
		addRequestParameter("method", "managePreview");
		addRequestParameter("productCategoryName", "product category 1");
		addRequestParameter("productCategoryDesc", "created a category 1");
		actionPerform();
		verifyForward("editpreview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		productCategoryBO=getProductCategory().get(2);
		assertEquals("product category 1",((PrdCategoryActionForm)getActionForm()).getProductCategoryName());
		assertEquals("created a category 1",((PrdCategoryActionForm)getActionForm()).getProductCategoryDesc());
		deleteProductCategory(productCategoryBO);
	}
	
	public void testManagePrevious() throws Exception {
		ProductCategoryBO productCategoryBO =new ProductCategoryBO(userContext,getProductTypes(userContext).get(0),"product category","created a category");
		productCategoryBO.save();
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, productCategoryBO,request.getSession());
		setRequestPathInfo("/productCategoryAction.do");
		addRequestParameter("method", "managePrevious");
		actionPerform();
		verifyForward("editprevious_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		productCategoryBO=getProductCategory().get(2);
		deleteProductCategory(productCategoryBO);
	}
	
	public void testUpdate()throws Exception {
		ProductCategoryBO productCategoryBO =new ProductCategoryBO(userContext,getProductTypes(userContext).get(0),"product category","created a category");
		productCategoryBO.save();
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, productCategoryBO,request.getSession());
		SessionUtils.setAttribute(ProductDefinitionConstants.PRDCATEGORYSTATUSLIST,getProductCategoryStatusList(userContext) ,request.getSession());
		setRequestPathInfo("/productCategoryAction.do");
		addRequestParameter("method", "update");
		addRequestParameter("productCategoryName", "product category 1");
		addRequestParameter("productCategoryDesc", "created a category 1");
		addRequestParameter("productType", "1");
		addRequestParameter("productCategoryStatus", "1");
		actionPerform();
		verifyForward("update_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		productCategoryBO=getProductCategory().get(2);
		assertEquals("product category 1",productCategoryBO.getProductCategoryName());
		assertEquals("created a category 1",productCategoryBO.getProductCategoryDesc());
		deleteProductCategory(productCategoryBO);
	}
	
	public void testGetAllCategories() throws Exception {
		ProductCategoryBO productCategoryBO =new ProductCategoryBO(userContext,getProductTypes(userContext).get(0),"product category","created a category");
		productCategoryBO.save();
		setRequestPathInfo("/productCategoryAction.do");
		addRequestParameter("method", "getAllCategories");
		actionPerform();
		verifyForward("search_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		productCategoryBO=getProductCategory().get(2);
		assertEquals(3,((List<ProductCategoryBO>)SessionUtils.getAttribute(ProductDefinitionConstants.PRODUCTCATEGORYLIST,request.getSession())).size());
		deleteProductCategory(productCategoryBO);
	}
	
	private List<ProductTypeEntity> getProductTypes(UserContext userContext)
			throws PersistenceException {
		List<ProductTypeEntity> productCategoryList =productCategoryPersistence
				.getProductTypes();
		for (ProductTypeEntity productTypeEntity : productCategoryList)
			productTypeEntity.setUserContext(userContext);
		return productCategoryList;
	}
	
	private List<PrdCategoryStatusEntity> getProductCategoryStatusList(
			UserContext userContext) throws PersistenceException {
		List<PrdCategoryStatusEntity> productCategoryStatusList = productCategoryPersistence
				.getProductCategoryStatusList();
		for (PrdCategoryStatusEntity prdCategoryStatusEntity : productCategoryStatusList)
			prdCategoryStatusEntity.setLocaleId(userContext.getLocaleId());
		return productCategoryStatusList;
	}
	
	private List<ProductCategoryBO> getProductCategory() {
		return (List<ProductCategoryBO>) HibernateUtil
				.getSessionTL()
				.createQuery(
						"from org.mifos.application.productdefinition.business.ProductCategoryBO pcb order by pcb.productCategoryID")
				.list();
	}
	
	private void deleteProductCategory(ProductCategoryBO productCategoryBO) {
		Session session = HibernateUtil.getSessionTL();
		Transaction	transaction = HibernateUtil.startTransaction();
		session.delete(productCategoryBO);
		transaction.commit();
	}

}
