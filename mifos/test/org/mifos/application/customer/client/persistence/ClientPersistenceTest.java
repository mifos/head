/**

 * ClientPersistenceTest.java version: 1.0



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
package org.mifos.application.customer.client.persistence;

import java.util.List;

import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.PrdApplicableMaster;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class ClientPersistenceTest extends MifosTestCase{
	private SavingsOfferingBO savingsOffering1;
	private SavingsOfferingBO savingsOffering2;
	private SavingsOfferingBO savingsOffering3;
	private SavingsOfferingBO savingsOffering4;
	
	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.removeObject(savingsOffering1);
		TestObjectFactory.removeObject(savingsOffering2);
		TestObjectFactory.removeObject(savingsOffering3);
		TestObjectFactory.removeObject(savingsOffering4);
		HibernateUtil.closeSession();
		super.tearDown();		
	}
	
	public void testRetrieveOfferingsApplicableToClient()throws Exception{
		savingsOffering1 = TestObjectFactory.createSavingsOffering("Offering1","s1", SavingsType.MANDATORY, PrdApplicableMaster.CLIENTS);
		savingsOffering2 = TestObjectFactory.createSavingsOffering("Offering2","s2", SavingsType.VOLUNTARY, PrdApplicableMaster.CLIENTS);
		savingsOffering3 = TestObjectFactory.createSavingsOffering("Offering3","s3", SavingsType.MANDATORY, PrdApplicableMaster.GROUPS);
		savingsOffering4 = TestObjectFactory.createSavingsOffering("Offering4","s4", SavingsType.VOLUNTARY, PrdApplicableMaster.CENTERS);
		HibernateUtil.closeSession();
		List<SavingsOfferingBO> offerings = new ClientPersistence().retrieveOfferingsApplicableToClient();
		assertEquals(2,offerings.size());
		for(SavingsOfferingBO offering: offerings){
			if(offering.getPrdOfferingId().equals(savingsOffering1.getPrdOfferingId()))
				assertTrue(true);
			if(offering.getPrdOfferingId().equals(savingsOffering2.getPrdOfferingId()))
				assertTrue(true);
		}
		HibernateUtil.closeSession();
	}	
}
