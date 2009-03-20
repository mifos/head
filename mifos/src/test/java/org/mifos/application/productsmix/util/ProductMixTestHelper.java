/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.application.productsmix.util;

import java.util.Date;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestCalcType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.TestObjectFactory;


public class ProductMixTestHelper {

	public static SavingsOfferingBO createSavingOffering(String name, String shortName,MeetingBO meeting1,MeetingBO meeting2) throws PersistenceException {				

		return TestObjectFactory.createSavingsProduct(
				name, shortName, ApplicableTo.CLIENTS, new Date(
					System.currentTimeMillis()),
			PrdStatus.SAVINGS_ACTIVE, 300.0,
			RecommendedAmountUnit.PER_INDIVIDUAL, 1.2, 200.0, 200.0,
			SavingsType.VOLUNTARY, InterestCalcType.MINIMUM_BALANCE,
			meeting1, meeting2);
	
	
	}

}
