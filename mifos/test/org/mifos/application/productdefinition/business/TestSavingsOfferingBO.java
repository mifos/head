/**

 * TestSavingsOfferingBO.java    version: xxx

 

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
package org.mifos.application.productdefinition.business;

import java.sql.Date;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.persistence.service.SavingsPrdPersistenceService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.TestObjectFactory;

import junit.framework.TestCase;

public class TestSavingsOfferingBO extends TestCase {

	private Date currentDate;
	
	private SavingsOfferingBO savingsOffering;
	
	private SavingsPrdPersistenceService prdPersistenceService;

	public TestSavingsOfferingBO() {
		super();
	}

	public TestSavingsOfferingBO(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		currentDate = new Date(System.currentTimeMillis());
		prdPersistenceService = new SavingsPrdPersistenceService();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TestObjectFactory.removeObject(savingsOffering);
	}

	public void testGetTimePerForIntCalcAndFreqPost() throws PersistenceException {
		savingsOffering = createSavingsOfferingBO();
		savingsOffering = prdPersistenceService.getSavingsProduct(savingsOffering.getPrdOfferingId());
		assertNotNull("The time period for Int calc should not be null",savingsOffering.getTimePerForInstcalc());
		assertNotNull("The freq for Int post should not be null",savingsOffering.getFreqOfPostIntcalc());
	}

	private SavingsOfferingBO createSavingsOfferingBO() {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory
				.createSavingsOffering("CustSavings", (short) 1, currentDate,
						(short) 2, 300.0, (short) 1, 1.2, 200.0, 200.0,
						(short) 2, (short) 1, meetingIntCalc, meetingIntPost);
	}

}
