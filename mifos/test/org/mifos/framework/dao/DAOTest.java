/**

* DAOTest    version: 1.0



* Copyright © 2005-2006 Grameen Foundation USA

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

package org.mifos.framework.dao;

import junit.framework.TestCase;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.fees.util.valueobjects.FeeFrequency;
import org.mifos.application.fees.util.valueobjects.Fees;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ConcurrencyException;
import org.mifos.framework.hibernate.HibernateStartUp;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.ValueObject;


/**
 *  This class is the test class for framework DAO. This test class checks the DAO functionality of Create , Update , Delete
 */

public class DAOTest extends TestCase
{
	 private DAO dao = null;
	 private ValueObject valueObject = null;


	public  DAOTest()
	{
		try
		{

			// initilize the logger and hibernate configuration
			MifosLogManager.configure(FilePaths.LOGFILE);
			HibernateStartUp.initialize("org/mifos/framework/util/resources/HibernateTest.properties");
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
			 dao = new DAO();
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
	 * This method tests for create functionality of the DAO , creates a value object , saves it into the database , retrives the saved record and verifies if it was saved properly
	 *
	*/

	public void testCreate()
	{
		try
		{
			Context context = new Context();
			// get the value object populated with data
			valueObject = getValueObject();
			// set this into the context
		    context.setValueObject(valueObject);
		    try
		    {
		    	// call the dao create to save the value object into the database
		    	dao.create(context);
		    	// asserts to true if there has been no exception
		    	assertTrue(true);
			}
			catch(ApplicationException e)
			{
				   // asserts false indicated test case failed
				    assertFalse(true);

			}

			// retrive the object inserted , and check if inserted correctly
			boolean isInserted = isValueObjectInserted(valueObject);
			assertTrue(isInserted);


		}
		catch(Exception e)
		{
			    assertFalse(true);

		}

	}
	/**
	 * This method tests for exception thrown on create with invalid data
	 *
	*/
	public void testExceptionCreate()
	{
		try
		{
			Context context = new Context();
			// get the value object to be populated , the object dosent contain the right data to be saved
			valueObject = getValueObjectError();
			context.setValueObject(valueObject);
			try
			{
			  // call the dao create to save the object in the database
			  dao.create(context);
			  // assert false as an exception should have been thrown
			  assertFalse(true);
			}
			catch(ApplicationException e)
			{
				    // assert true indicating saving failed as expected
				    assertTrue(true);

			}
			// validate if the rollback has happened after partial insertion
			boolean isInserted = isValueObjectInserted(valueObject);
			// if true , test case fails indicating rollback has not happened
			assertFalse(isInserted);

		}
		catch(Exception e)
		{
			    assertFalse(true);

		}

	}

	/**
	 * This method checks for the DAO update method
	 *
	*/
	public void testUpdate()
	{
		try
		{
			Context context = new Context();
			// get the value object to be inserted
			valueObject = getValueObject();
		    context.setValueObject(valueObject);
		    try
		    {
		    	// save the value object
		    	dao.create(context);
			}
			catch(ApplicationException e)
			{
				    assertFalse(true);

			}

			// retrive the value object from the database
			valueObject =  retriveValueObject(valueObject);
			// change the values of the value object
			changeValueObject(valueObject);
			context.setValueObject(valueObject);

			try
		    {
				// save the changed value object to the database
		    	dao.update(context);
		    	// assert to true to indicate the value object was updated succesfully
		    	assertTrue(true);
			}
			catch(ApplicationException e)
			{
				   // assert to false to indicate the value object could not be saved successfully
				    assertFalse(true);

			}
			// retrive the updated value object from the database and check if the data modified has been updated successfully
			boolean isChanged = isValueObjectChanged(valueObject);
			// assert to true to indicate values have been changed successfullly
			assertTrue(isChanged);

		}
		catch(Exception e)
		{
			// assert to false to indicate values have not been changed successfullly
			assertFalse(true);

		}

	}

	/**
	 * This method checks for the DAO update method under concurrent updates.
	 *
	*/

	public void testConcurrencyErrorUpdate()
	{
		try
		{
			Context context = new Context();
			// get the value object to be inserted
			valueObject = getValueObject();
		    context.setValueObject(valueObject);
		    try
		    {
		    	// insert the value object into the database
		    	dao.create(context);
			}
			catch(ApplicationException e)
			{
				    assertFalse(true);

			}

			// retrive the value object saved
			valueObject =  retriveValueObject(valueObject);
			// retrive the value object again ,simulating second user
			ValueObject secondUserValueObject = retriveValueObject(valueObject);

			// modify the data in the value object
			changeValueObject(valueObject);
			context.setValueObject(valueObject);


		    try
		    {
		    	// update the value object with the modified data
		    	dao.update(context);
		    	// asserts to true to indicate value object updated successfully
		    	assertTrue(true);
			}
			catch(ApplicationException e)
			{
				    assertFalse(true);

			}

			// modify the second value object
			changeValueObject(secondUserValueObject);
			context.setValueObject(secondUserValueObject);

		    try
		    {
		    	// update the second value object with the modified data
		    	dao.update(context);
		    	// assert to false to indicate test case fails as concurrency exception was not thrown
		    	assertFalse(true);
			}
			catch(ConcurrencyException e)
			{
				    // assert to true to indicate concurrency exception was thrown
				    assertTrue(true);

			}
			catch(ApplicationException e)
			{
				    //assert to false to indicate test case fails as it should have been concurrency exception
				    assertTrue(false);

			}

		}
		catch(Exception e)
		{
			    assertFalse(true);

		}

	}
	/**
	 * This method checks for the DAO delete functionality
	 *
	*/

	public void testDelete()
	{
		try
		{
			Context context = new Context();
			// get the value object to be inserted
			valueObject = getValueObject();
		    context.setValueObject(valueObject);
		    try
		    {
		    	// save the value object in the database
		    	dao.create(context);
			}
			catch(ApplicationException e)
			{
				    assertFalse(true);

			}
			// retrive the saved value object from the database
			valueObject =  retriveValueObject(valueObject);
			context.setValueObject(valueObject);

			try
		    {
				// delete the value object
		    	dao.delete(context);
		    	assertTrue(true);
			}
			catch(ApplicationException e)
			{

				    assertFalse(true);

			}
			// retive the value object again from the database
			valueObject =  retriveValueObject(valueObject);

			// assert to null to indicate the retrival did not fetch the value object
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

private ValueObject getValueObjectError()
	{
		Fees fee = new Fees();
		fee.setFeeName("FeeError");
		fee.setCategoryId(new Short("1"));
		fee.setRateOrAmount(new Double("1000"));
		fee.setFormulaId(new Short("1"));
		fee.setRateFlatFalg(new Short("0"));
		fee.setOfficeId(new Short("1"));
		fee.setStatus(new Short("1"));

		FeeFrequency feeFrequency = new FeeFrequency();
		feeFrequency.setFeePaymentId(new Short("2"));


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
