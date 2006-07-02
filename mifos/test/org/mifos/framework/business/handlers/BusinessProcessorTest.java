/**

* BusinessProcessorTest    version: 1.0



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

package org.mifos.framework.business.handlers;

import java.io.File;

import junit.framework.TestCase;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.fees.util.valueobjects.FeeFrequency;
import org.mifos.application.fees.util.valueobjects.Fees;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.hibernate.HibernateStartUp;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DAOFactory;
import org.mifos.framework.util.helpers.FilePath;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 *  This class is the test class for framework BusinessProcessor. This test class checks the BusinessProcessor functionality of Create , Update , Delete
 */

public class BusinessProcessorTest extends TestCase
{
	 private BusinessProcessor businessProcessor = null;
	 private ValueObject valueObject = null;


	public  BusinessProcessorTest()
	{
		try
		{

			// initilize the logger and hibernate configuration
			MifosLogManager.configure(FilePaths.LOGFILE);
			HibernateStartUp.initialize("org/mifos/framework/util/resources/HibernateTest.properties");
			// set the test dependency file
			 File dependencyFile = new File(FilePath.DEPENDENCYFILE);
			 DAOFactory daoFactory =DAOFactory.getInstance();
			 daoFactory.setDependencyFile(dependencyFile);

		}
		catch(Exception e)
		{
				e.printStackTrace();
		}

	}

	/**
	 * This method is called before JUNIT executes each test , the valueobject is set to null
	*/

	protected void setUp()
	{
		try
		{
			 valueObject = null;
			 businessProcessor = new MifosBusinessProcessor();


		}
		catch(Exception e)
		{
				e.printStackTrace();
		}


	}

	/**
	 * This method is called by JUNIT after executing each test , the value object created by the earlier test is deleted from the database
	 * This ensures the next test case can work on a valid object
	*/


	protected void tearDown()
	{

			tearValueObject();

	}

	/**
	 * This method tests for create functionality of the BusinessProcessor , creates a value object , saves it into the database , retrives the saved record and verifies if it was saved properly
	 *
	*/

	public void testCreate()
	{
		try
		{
			Context context = new Context();
			// get the value object with data populated to be saved
			valueObject = getValueObject();

			context.setValueObject(valueObject);
			// set the business action to create , this will invoke the create flow of framework
			context.setBusinessAction("create");
			// set the dependency path to get the DAO
			context.setPath("FrameworkTestPath");

			try
		    {
				// invoke the flow
		    	businessProcessor.execute(context);
		    	// assert to true to indicate data was saved successfully
		    	assertTrue(true);
			}
			catch(ApplicationException e)
			{
				    assertFalse(true);

			}
			// retrive the object from the database and check for values, to check if the data was saved correctly
			boolean isInserted = isValueObjectInserted(valueObject);
			// assert to true to indicate data was saved correctly
			assertTrue(isInserted);


		}
		catch(Exception e)
		{
			    // assert to false to indicate the test case failed
			    assertFalse(true);

		}

	}

	/**
	 * This method tests for update functionality of the BusinessProcessor
	 *
	*/

	public void testUpdate()
	{
		try
		{
			Context context = new Context();
			// get the value object with data populated
			valueObject = getValueObject();


			context.setValueObject(valueObject);
			context.setBusinessAction("create");
			context.setPath("FrameworkTestPath");


			try
		    {
				// invoke the create flow , to save the data in the database
		    	businessProcessor.execute(context);
			}
			catch(ApplicationException e)
			{
				    assertFalse(true);

			}
			// get the object saved
			valueObject =  retriveValueObject(valueObject);
			// change the values in the value object
			changeValueObject(valueObject);

			context = new Context();

			context.setValueObject(valueObject);
			// set the business action to update , this would invoke the update flow in the framework
			context.setBusinessAction("update");
			// set the dependency path to get the DAO
			context.setPath("FrameworkTestPath");



			try
		    {
				// invoke the update flow
		    	businessProcessor.execute(context);
		    	assertTrue(true);
			}
			catch(ApplicationException e)
			{


				    assertFalse(true);

			}
			// get the value object updated , and check if the data was modified correctly
			boolean isChanged = isValueObjectChanged(valueObject);
 			// assert to true to indicate data was modified correctly
			assertTrue(isChanged);

		}
		catch(Exception e)
		{
			    assertFalse(true);

		}

	}

	/**
	 * This method tests for delete functionality of the BusinessProcessor
	 *
	*/

public void testDelete()
{
	try
	{
		Context context = new Context();
		//get the value object with data populated
		valueObject = getValueObject();

		context.setValueObject(valueObject);
		context.setBusinessAction("create");
		context.setPath("FrameworkTestPath");


	    try
	    {
	    	//invoke the create flow , to save the data in the database
	    	businessProcessor.execute(context);
		}
		catch(ApplicationException e)
		{
			    assertFalse(true);

		}

		valueObject =  retriveValueObject(valueObject);

		context = new Context();
		context.setValueObject(valueObject);
		// set the business action to delete , this would invoke the update flow in the framework
		context.setBusinessAction("delete");
		// set the dependency path to get the DAO
		context.setPath("FrameworkTestPath");



		try
	    {
			// invoke the delete flow
			businessProcessor.execute(context);
	    	assertTrue(true);
		}
		catch(ApplicationException e)
		{

			    assertFalse(true);

		}
		//	 get the value object from the database
		valueObject =  retriveValueObject(valueObject);
		// assert to null to indicate the value object no longer exists in the database
		assertNull(valueObject);

	}
	catch(Exception e)
	{
		    assertFalse(true);

	}

}


	private ValueObject retriveValueObject(ValueObject valueObject)
	{
		Session session = null;
		try
		{
		 session = HibernateUtil.getSession();
		  String feeName = ((Fees)valueObject).getFeeName();

	    Query query = session.createQuery("from org.mifos.application.fees.util.valueobjects.Fees fee where fee.feeName = ?" );
		query.setString(0,feeName);
		Fees fee = (Fees)query.uniqueResult();

		return fee;
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

	private boolean isValueObjectInserted(ValueObject valueObject)
	{
		Fees fee = (Fees)retriveValueObject(valueObject);

		if(fee != null)
			return true;
		else
			return false;


	}

	private ValueObject getValueObject()
	{
		Fees fee = new Fees();

		fee.setFeeName("FeeValid");
		fee.setCategoryId(new Short("1"));
		fee.setRateOrAmount(new Double("1000"));
		fee.setFormulaId(new Short("1"));
		fee.setRateFlatFalg(new Short("0"));
		fee.setOfficeId(new Short("1"));
		fee.setStatus(new Short("1"));


		FeeFrequency feeFrequency = new FeeFrequency();
		feeFrequency.setFeePaymentId(new Short("2"));
		feeFrequency.setFeeFrequencyTypeId(new Short("2"));


		fee.setFeeFrequency(feeFrequency);
		return fee;

	}

private void changeValueObject(ValueObject valueObject)
{
	Fees fee = (Fees)valueObject;
	String feeName = fee.getFeeName();
	feeName = feeName+"a";

	fee.setFeeName(feeName);

}

private boolean isValueObjectChanged(ValueObject valueObject)
{
	Fees fee = (Fees)retriveValueObject(valueObject);

	if(fee != null)
		return true;
	else
		return false;

}

private void tearValueObject()
{
	Session session = null;
	Transaction tx = null;
	try
	{
	 session = HibernateUtil.getSession();
	 tx = session.beginTransaction();
	 session.delete(valueObject);
	 tx.commit();
	}
	catch(Exception e)
	{
		tx.rollback();
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
