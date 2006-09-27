package org.mifos.api;

import java.util.List;

import org.mifos.application.accounts.loan.persistance.LoanPersistance;
import org.mifos.application.customer.business.CustomerLevelEntity;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.productdefinition.business.LoanOfferingBO;

public class LoanProductDumper {
	
	public static void main(String[] args) throws Exception {
		new MifosService().init();

		List<LoanOfferingBO> offerings = 
			new LoanPersistance().getApplicablePrdOfferings(
				new CustomerLevelEntity(CustomerLevel.CLIENT));
		for (LoanOfferingBO offering : offerings) {
			System.out.println("Offering " + offering.getPrdOfferingId());
		}
	}

}
