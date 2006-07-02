/**

 * MifosBaseActionTest.java    version: xxx



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

package org.mifos.framework.struts.action;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;


import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.productdefinition.util.valueobjects.ProductCategory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.LoggerConfigurationException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.BusinessProcessorFactory;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DAOFactory;
import org.mifos.framework.util.helpers.FilePath;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.ValueObjectFactory;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.ValueObject;

import servletunit.struts.MockStrutsTestCase;


/**
 * This is the test class for testing methods of MifosBaseAction class.
 * It is expected that before running the test cases in this file, one must have the certain users
 * and a branch office are present in the system.
 * @author ashishsm
 *
 */
public class MifosBaseActionTest extends MockStrutsTestCase {

	/**
	 * This initializes the logger for uses with in the test code.
	 */
	public MifosBaseActionTest() {
		//super();
		try {
			MifosLogManager.configure(FilePaths.LOGFILE);
			File dependencyFile = new File(FilePath.DEPENDENCYFILE);
			BusinessProcessorFactory.getInstance().setDependencyFile(dependencyFile);
			ValueObjectFactory.getInstance().setDependencyFile(dependencyFile);
			DAOFactory.getInstance().setDependencyFile(dependencyFile);
		} catch (LoggerConfigurationException e) {

			e.printStackTrace();
		}
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).error("Inside constructor of MifosBaseActionTest after obtaining the logger.");

	}


	/**
	 * This sets the web.xml,struts-config.xml and prepares the userContext and activityContext and sets them in the session.
	 *
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp()throws Exception{
		super.setUp();
		try {
			//System.out.println("Indside the method setting the web.xml" + ResourceLoader.getURI("/org/mifos/META-INF/web.xml").getPath());
			setServletConfigFile(ResourceLoader.getURI("/WEB-INF/web.xml").getPath());
			setConfigFile(ResourceLoader.getURI("/WEB-INF/struts-config.xml").getPath());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// create the user context with the preferred locale,branch id and userId.
		UserContext userContext=new UserContext();
		userContext.setId(new Short("1"));
		userContext.setLocaleId(new Short("1"));
		userContext.setBranchId(Short.valueOf("1"));
		userContext.setLevelId(Short.valueOf("2"));
		userContext.setName("mifos");
		// prepare the roles and add to the user context.
		HashSet hashSet = new HashSet();
		hashSet.add(new Short("1"));
		userContext.setRoles(hashSet);
		// prepare activity context
		ActivityContext activityContext = new ActivityContext(Short.valueOf("0"),Short.valueOf("1"));
		getRequest().getSession().setAttribute(LoginConstants.ACTIVITYCONTEXT, activityContext);
		// set user context in the session.
		request.getSession().setAttribute(Constants.USERCONTEXT,userContext);

	}






	/**
	 * This method is used to test create functionality.
	 * It tries to test create a product category.
	 *
	 */
	public void testSuccessfullCreate(){

		setRequestPathInfo("/mifosproddefaction.do");
		addRequestParameter("method","create");
		addRequestParameter("productType.productTypeID","1");
		addRequestParameter("productCategoryName","testProductCategory");
		addRequestParameter("productType.versionNo","1");
		addRequestParameter("prdCategoryStatus.prdCategoryStatusId","1");
		addRequestParameter("globalPrdOfferingNum","testGlobalPrdOfferingNum");
		actionPerform();
		verifyForward("create_success");
		assertTrue(isValueObjectInserted("testGlobalPrdOfferingNum"));
		deleteValueObject("testGlobalPrdOfferingNum");

	}


	/**
	 *This method tests the successful update of prdCategory.It first creates the prdCategory, then updates the description of that object
	 *and then asserts for the updated value.It also deletes the object which it had created.
	 */
	public void testSuccessfullUpdate(){

		setRequestPathInfo("/mifosproddefaction.do");
		addRequestParameter("method","create");
		addRequestParameter("productType.productTypeID","1");
		addRequestParameter("productCategoryName","testProductCategory");
		addRequestParameter("productType.versionNo","1");
		addRequestParameter("prdCategoryStatus.prdCategoryStatusId","1");
		addRequestParameter("productCategoryDesc","created");
		addRequestParameter("globalPrdOfferingNum","testGlobalPrdOfferingNum");
		actionPerform();

		ValueObject valueObj = getContext().getValueObject();
		setRequestPathInfo("/mifosproddefaction.do");
		addRequestParameter("method","update");
		addRequestParameter("productCategoryDesc","updated");
		addRequestParameter("versionNo","0");
		addRequestParameter("productCategoryID",((ProductCategory)valueObj).getProductCategoryID().toString());
		actionPerform();

		//System.out.println("product category id  is: " + ((ProductCategory)valueObj).getProductCategoryID());
		valueObj = retriveValueObject("testGlobalPrdOfferingNum");
		assertEquals(((ProductCategory)valueObj).getProductCategoryDesc(),"updated");
		//assertEquals(((ProductCategory)valueObj).getVersionNo(),Short.valueOf("1"));
		deleteValueObject("testGlobalPrdOfferingNum");
	}
	/**
	 * This method deletes the valueObject which has the globalPrdOfferingNum equal to the one passed.
	 * It first retrieves the value object using the retrieve value object method , if that is not null it deletes
	 * it using hibernate session.
	 * @param string
	 */
	private void deleteValueObject(String globalPrdOfferingNum) {
		ValueObject valueObj = retriveValueObject(globalPrdOfferingNum);
		Transaction trxn = null;
		if(null != valueObj){
			Session session = null;
			try
			{
				session = HibernateUtil.getSession();
				trxn = session.beginTransaction();
				//System.out.println("trying to delete the value object with id " + ((ProductCategory)valueObj).getProductCategoryID());
				session.delete((ProductCategory)valueObj);

				trxn.commit();
			}
			catch(Exception e)
			{
				trxn.rollback();
				e.printStackTrace();

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


	/**
	 *This method tests the successful execution of load method.
	 *It also checks the foll:-
	 *Context should not be null.
	 *ValueObject should not be null.
	 *Post execute setting values in the request.
	 */
	public void testSuccessfulLoad(){

		setRequestPathInfo("/mifosproddefaction.do");
		addRequestParameter("method","load");
		addRequestParameter("recordOfficeId","0");
		addRequestParameter("recordLoanOfficerId","0");
		actionPerform();
		assertTrue(verifyCreationOfContext());
		assertTrue(verifyCreationOfValueObject());
		verifyForward("load_success");
	}

	/**
	 * This method retrieves the valueObject using the globalPrdOfferingNum.
	 * @param valueObject
	 * @return
	 */
	private ValueObject retriveValueObject(String globalPrdOfferingNum)
	{
		ValueObject valueObj = null;
		Session session = null;
		try
		{
			session = HibernateUtil.getSession();


			Query query = session.createQuery("from org.mifos.application.productdefinition.util.valueobjects.ProductCategory prdCategory where prdCategory.globalPrdOfferingNum = ?" );
			//  System.out.println("globalPrdOfferingNum is "  +globalPrdOfferingNum);
			query.setString(0,globalPrdOfferingNum);
			List queryResult = query.list();
			if(null != queryResult && queryResult.size()>0){
				valueObj = (ValueObject)queryResult.get(0);
			}
			//valueObj =  (ValueObject)query.uniqueResult();
			//System.out.println("valueobject name is " + valueObj.getResultName());

		}
		catch(Exception e)
		{
			e.printStackTrace();

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
		return valueObj;
	}

	/**
	 * This method returns true if the value object was inserted.It takes the value object and if it is not null it returns true.
	 * It retrieves the value object based on the global_prd_offering_num.
	 * @param valueObject
	 * @return
	 */
	private boolean isValueObjectInserted(String globalPrdOfferingNum)
	{
		ValueObject valueObj = retriveValueObject(globalPrdOfferingNum);

		if(valueObj != null)
			return true;
		else
			return false;


	}
	/**
	 * This method verifies creation of context by checking if it is present in the session.It returns true if the
	 * context is found in the session, else it returns false.
	 */
	protected boolean verifyCreationOfContext() {
		if(null != getContext()){
			return true;
		}else{
			return false;
		}

	}

	/**
	 * This method returns the context after obtaining it using SessionUtils, it returns null if the Context is
	 * not present in the session.
	 * @return
	 */
	protected Context getContext(){
		return (Context)SessionUtils.getAttribute(Constants.CONTEXT, getRequest().getSession());
	}

	/**
	 * This method verifies creation of valueobject by checking if it is present in the context.It returns true if the
	 * valueobject is found in the context, else it returns false.It will return false if the context is null.
	 */
	protected boolean verifyCreationOfValueObject() {
		if(null != getContext()){
			return (((getContext().getValueObject())== null)? false: true);

		}else{
			return false;
		}

	}

	/**
	 * This method returns true if the request contains all the attributes which are passed as parameter.
	 * Even if one of the attribute is null in the request , the method returns false.It also returns true
	 * if the array is null or of length 0.
	 * @param attributes - The array of string which are the attributes to be checked for existence in request.
	 * @return
	 */
	protected boolean verifyPostExecute(String... attributes ){
		// iterate over the array and if any of the attribute is null return false.
		if(null != attributes && attributes.length > 0){

			for(String attribute : attributes){
				MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("The attribute being checked in request is " + attribute);
				if(getRequest().getAttribute(attribute)== null){
					return false;
				}
			}
		}
		return true;
	}

}
