/**

 * LoanPrdActionTest.java    version: 1.0

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */
package org.mifos.application.productsmix.struts.action;

import java.sql.Date;
import java.util.List;

import org.mifos.application.meeting.business.MeetingBO;
import static org.mifos.application.meeting.util.helpers.MeetingType.LOAN_INSTALLMENT;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.application.meeting.util.helpers.WeekDay.MONDAY;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.application.productdefinition.business.ProductTypeEntity;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestCalcType;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.application.productsmix.business.ProductMixBO;
import org.mifos.application.productsmix.persistence.ProductMixPersistence;
import org.mifos.application.productsmix.struts.actionforms.ProductMixActionForm;
import org.mifos.application.productsmix.util.ProductMixTestHelper;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

public class ProductMixActionTest extends MifosMockStrutsTestCase {

	private LoanOfferingBO loanOffering;
	private LoanOfferingBO loanOffering1;
	private SavingsOfferingBO savingProduct;
	private SavingsOfferingBO savingProduct2;
	private PrdOfferingBO productOffering;
	MeetingBO meeting;
	MeetingBO meeting1;
	SavingsOfferingBO saving1;
	SavingsOfferingBO saving2;
	ProductMixBO prdmix;
	

	private String flowKey;

	UserContext userContext = null;


	@Override
	protected void tearDown() throws Exception {
		/*List<ProductMixBO> prdmixBO = (List<ProductMixBO>) HibernateUtil.getSessionTL().get(
				ProductMixBO.class,productOffering.getPrdOfferingId());*/
		
		try {
			TestObjectFactory.removeObject(loanOffering);
			TestObjectFactory.removeObject(loanOffering1);
			TestObjectFactory.removeObject(savingProduct);
			TestObjectFactory.removeObject(savingProduct2);

			// HACK: reload the productOffering in the new session to avoid 
			// org.hibernate.StaleObjectStateException issues
			productOffering = (PrdOfferingBO)HibernateUtil.getSessionTL().get(PrdOfferingBO.class, productOffering.getPrdOfferingId());
			TestObjectFactory.removeObject(productOffering);
			TestObjectFactory.removeObject(prdmix);	
			TestObjectFactory.removeObject(saving1);
			TestObjectFactory.removeObject(saving2);
		} catch (Exception e) {
			// TODO Whoops, cleanup didnt work, reset db
			TestDatabase.resetMySQLDatabase();
		}

		super.tearDown();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
        userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		ActivityContext ac = TestObjectFactory.getActivityContext();
		request.getSession(false).setAttribute("ActivityContext", ac);
		flowKey = createFlow(request, ProductMixAction.class);
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
			
		loanOffering = createLoanOfferingBO("Loan Offering", "LOAN");
		loanOffering1 = createLoanOfferingBO("Loan Offering1",
		"LOA1");
		productOffering= createLoanOfferingBO("Loan Offering as product Offring","PO");

		createSavingsOffering();
	}
	public void testCreate() throws Exception {
		
		setRequestPathInfo("/productMixAction.do");
		addRequestParameter("method", "loadDefaultAllowedProduct");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, productOffering,
				request);
		
		SessionUtils.setCollectionAttribute(ProductDefinitionConstants.OLDNOTALLOWEDPRODUCTLIST, null, request);
		addRequestParameter("prdOfferingId", productOffering.getPrdOfferingId().toString());
		addRequestParameter("productInstance", productOffering.getPrdOfferingId().toString());
		addRequestParameter("productType", productOffering.getPrdType().getProductTypeID().toString());

		actionPerform();
		List<PrdOfferingBO> allowedPrdOfferingList = (List<PrdOfferingBO>) SessionUtils
		.getAttribute(ProductDefinitionConstants.ALLOWEDPRODUCTLIST,
				request);
		SessionUtils.setCollectionAttribute(ProductDefinitionConstants.NOTALLOWEDPRODUCTLIST, allowedPrdOfferingList,
				request);
		SessionUtils.setCollectionAttribute(ProductDefinitionConstants.OLDNOTALLOWEDPRODUCTLIST, null, request);
		setRequestPathInfo("/productMixAction.do");
		addRequestParameter("method", "create");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, productOffering,
				request);
		addRequestParameter("productInstance",productOffering.getPrdOfferingId().toString() );

		actionPerform();
		
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.create_success.toString());
		cleanUpAfterCreate();
		HibernateUtil.closeSession();
	}
	
	private void cleanUpAfterCreate() throws PersistenceException,
			ProductDefinitionException {
		HibernateUtil.startTransaction();
		List<ProductMixBO> productList = (new ProductMixPersistence()
				.getAllProductMix());
		for (ProductMixBO tempPrdMix : productList) {
			tempPrdMix.delete();
		}
		HibernateUtil.commitTransaction();
	}

	public void testGet_success() throws Exception {
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, productOffering,
				request);
		addRequestParameter("prdOfferingId", savingProduct.getPrdOfferingId().toString());
		addRequestParameter("productType", savingProduct.getPrdType().getProductTypeID().toString());
		
		setRequestPathInfo("/productMixAction.do");
		addRequestParameter("method", "get");
		
		actionPerform();
		actionPerform();		
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.get_success.toString());
		
	}

	public void testLoad() throws Exception {
		
		setRequestPathInfo("/productMixAction.do");
		addRequestParameter("method", "load");
		ProductMixActionForm productMixActionForm = (ProductMixActionForm) request
		.getSession().getAttribute("productMixActionForm");
		assertNull(productMixActionForm);

		actionPerform();
		List<ProductTypeEntity>  productTypeList = (List<ProductTypeEntity>)  
		SessionUtils.getAttribute(ProductDefinitionConstants.PRODUCTTYPELIST,request);
		
		assertNotNull(productTypeList);
		
		assertTrue(""+productTypeList.size(),2==productTypeList.size());
		productMixActionForm = (ProductMixActionForm) request
		.getSession().getAttribute("productMixActionForm");

		assertNotNull(productMixActionForm);
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());
	}

	public void testSuccessfullLoadParent_Loan() throws Exception {
		setRequestPathInfo("/productMixAction.do");
		addRequestParameter("method", "loadParent");
		
		addRequestParameter("productType", ProductType.LOAN.getValue().toString());

		actionPerform();
		/*List<ProductTypeEntity>  productTypeList = (List<ProductTypeEntity>)  
		SessionUtils.getAttribute(ProductDefinitionConstants.PRODUCTTYPELIST,request);
		assertNotNull(productTypeList);*/
		ProductMixActionForm productMixActionForm = (ProductMixActionForm) request
		.getSession().getAttribute("productMixActionForm");
		assertNotNull(productMixActionForm);
		List<LoanOfferingBO> loanOffInstance = (List<LoanOfferingBO>) SessionUtils
				.getAttribute(ProductDefinitionConstants.PRODUCTINSTANCELIST,
						request);

		assertTrue(3 == loanOffInstance.size());	
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());
		
	}
	
	
	public void testSuccessfullLoadParent_Savings() throws Exception {
		setRequestPathInfo("/productMixAction.do");
		addRequestParameter("method", "loadParent");
		
		addRequestParameter("productType", ProductType.SAVINGS.getValue().toString());

		actionPerform();
		List<SavingsOfferingBO> savingOffInstance = (List<SavingsOfferingBO>) SessionUtils
		.getAttribute(ProductDefinitionConstants.PRODUCTINSTANCELIST,
				request);
	
		assertNotNull(savingOffInstance);	
		SessionUtils.getAttribute(ProductDefinitionConstants.PRODUCTTYPELIST,request);

		ProductMixActionForm productMixActionForm = (ProductMixActionForm) request
		.getSession().getAttribute("productMixActionForm");
		assertNotNull(productMixActionForm);

		assertTrue(1 == savingOffInstance.size());	

		assertNotNull(savingOffInstance);

		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());
		
	}
	
	public void testViewAllProductMix() throws Exception {

		setRequestPathInfo("/productMixAction.do");
		addRequestParameter("method", "viewAllProductMix");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		UserContext userContext2 = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		assertEquals(userContext, userContext2);
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.viewAllProductMix_success.toString());
		
	}
	
	public void testGet() throws Exception {

		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, productOffering,
				request);
		addRequestParameter("prdOfferingId", savingProduct.getPrdOfferingId().toString());
		addRequestParameter("productType", savingProduct.getPrdType().getProductTypeID().toString());
		
		setRequestPathInfo("/productMixAction.do");
		addRequestParameter("method", "get");
		
		actionPerform();
		List<PrdOfferingBO> allPrdOfferingList=(List<PrdOfferingBO> )SessionUtils.getAttribute(ProductDefinitionConstants.ALLOWEDPRODUCTLIST,
				 request);
		assertNotNull(allPrdOfferingList);
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.get_success.toString());
		
	}
	
	public void testManage() throws Exception {
		createNotAllowedProduct("Eddikhar","Edk");

		setRequestPathInfo("/productMixAction.do");
		addRequestParameter("method", "manage");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
				
		addRequestParameter("productInstance", savingProduct.getPrdOfferingId().toString());
		addRequestParameter("productType", savingProduct.getPrdType().getProductTypeID().toString());

		actionPerform();
	/*	List<PrdOfferingBO> productMixList = (List<PrdOfferingBO>) SessionUtils
		.getAttribute(ProductDefinitionConstants.PRODUCTMIXLIST,
				request);
		
		assertNotNull(productMixList);
		
		assertTrue(1 == productMixList.size());	
*/
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.manage_success.toString());
		
	}

	public void testLoadDefaultAllowedProduct() throws Exception {
		createNotAllowedProduct("epargne1","zz");

		setRequestPathInfo("/productMixAction.do");
		addRequestParameter("method", "loadDefaultAllowedProduct");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, productOffering,
				request);
		addRequestParameter("productType", ProductType.LOAN.getValue().toString());
		addRequestParameter("productInstance", "2");
		
		actionPerform();
		List<PrdOfferingBO> allowedPrdOfferingList = (List<PrdOfferingBO>) SessionUtils
		.getAttribute(ProductDefinitionConstants.ALLOWEDPRODUCTLIST,
				request);
		List<PrdOfferingBO> allPrdOfferingList = (List<PrdOfferingBO>) SessionUtils
		.getAttribute(ProductDefinitionConstants.PRODUCTOFFERINGLIST,
				request);
		
		assertNotNull(allowedPrdOfferingList);
		assertNotNull(allPrdOfferingList);	
		SessionUtils.getAttribute(ProductDefinitionConstants.PRODUCTTYPELIST,request);

		assertTrue(3 == allowedPrdOfferingList.size());	

		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());
		
	}
	
	
	private LoanOfferingBO createLoanOfferingBO(String prdOfferingName,
			String shortName) {
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO frequency = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeeting(WEEKLY, EVERY_WEEK, LOAN_INSTALLMENT, MONDAY));
		return TestObjectFactory.createLoanOffering(prdOfferingName, shortName,
				ApplicableTo.GROUPS, startDate, 
				PrdStatus.LOAN_ACTIVE, 300.0, 1.2, 3, 
				InterestType.FLAT, true, false,
				frequency);
	}

	private void createSavingsOffering() throws Exception {
		String prdName = "Eddikhar";
		String prdShortName = "SSK";
		createSavingsOfferingBO(prdName, prdShortName);

	}
	private SavingsOfferingBO createSavingsOfferingBO(String productName,
			String shortName) {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getTypicalMeeting());
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getTypicalMeeting());
		savingProduct = TestObjectFactory.createSavingsProduct(
			productName, shortName, ApplicableTo.CLIENTS, 
			new Date(System.currentTimeMillis()), 
			PrdStatus.SAVINGS_ACTIVE, 300.0, 
			RecommendedAmountUnit.PER_INDIVIDUAL, 1.2, 
			200.0, 200.0, 
			SavingsType.VOLUNTARY, InterestCalcType.MINIMUM_BALANCE, 
			meetingIntCalc, meetingIntPost);
		return savingProduct;
	}
	private SavingsOfferingBO createNotAllowedProduct(String productName,
			String shortName) throws PersistenceException {
		
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		meeting1 = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		saving1 = ProductMixTestHelper.createSavingOffering("Savings Product1", "S1",meeting,meeting);
		saving2 = ProductMixTestHelper.createSavingOffering("Savings Product2", "S2",meeting1,meeting1);
		prdmix= TestObjectFactory.createAllowedProductsMix(saving1,saving2);		


		return savingProduct2;
	}

}
