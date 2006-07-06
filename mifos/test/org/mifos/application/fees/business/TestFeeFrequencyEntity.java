/**

 * TestFeeFrequencyEntity.java    version: xxx



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
package org.mifos.application.fees.business;

import org.mifos.application.fees.exceptions.FeeException;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.framework.MifosTestCase;

public class TestFeeFrequencyEntity extends MifosTestCase {

	public void testBuildFrequencyWithoutFrequencyType() {
		FeeFrequencyEntity feeFrequencyEntity = new FeeFrequencyEntity();
		try {
			feeFrequencyEntity.buildFeeFrequency();
			assertFalse(
					"The fee frequency is build without fee frequency type",
					true);
		} catch (FeeException e) {
			assertTrue(
					"The fee frequency is not build without fee frequency type",
					true);
		}
	}

	public void testBuildFrequencyForOneTimeFees() throws FeeException {
		FeeFrequencyEntity feeFrequencyEntity = new FeeFrequencyEntity();
		feeFrequencyEntity.getFeeFrequencyType().setFeeFrequencyTypeId(
				FeeFrequencyType.ONETIME.getValue());
		assertNotNull("The fee meeting should not be null", feeFrequencyEntity
				.getFeeMeetingFrequency());
		feeFrequencyEntity.buildFeeFrequency();
		assertNull("The fee meeting should be null", feeFrequencyEntity
				.getFeeMeetingFrequency());

	}

	public void testBuildFrequencyForPeriodicFees() throws FeeException {
		FeeFrequencyEntity feeFrequencyEntity = new FeeFrequencyEntity();
		feeFrequencyEntity.getFeeFrequencyType().setFeeFrequencyTypeId(
				FeeFrequencyType.PERIODIC.getValue());
		assertNotNull("The fee payment should not be null", feeFrequencyEntity
				.getFeePayment());
		feeFrequencyEntity.buildFeeFrequency();
		assertNull("The fee payment should be null", feeFrequencyEntity
				.getFeePayment());

	}
}
