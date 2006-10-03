package org.mifos.api;

import java.util.List;

import org.mifos.application.customer.business.CustomerLevelEntity;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.persistence.LoansPrdPersistence;

public class LoanProductDumper {
	
	public static void main(String[] args) throws Exception {
		new MifosService().init();

		List<LoanOfferingBO> offerings = 
			new LoansPrdPersistence().getApplicablePrdOfferings(
				new CustomerLevelEntity(CustomerLevel.CLIENT));
		for (LoanOfferingBO offering : offerings) {
			System.out.println("Offering " + offering.getPrdOfferingId());
		}
	}

}
