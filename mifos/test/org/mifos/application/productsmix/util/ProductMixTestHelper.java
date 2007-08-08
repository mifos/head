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
