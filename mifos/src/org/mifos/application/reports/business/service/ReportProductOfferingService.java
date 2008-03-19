package org.mifos.application.reports.business.service;

import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.application.productdefinition.business.service.SavingsPrdBusinessService;
import org.mifos.framework.business.service.ConfigService;
import org.mifos.framework.exceptions.ServiceException;
import org.springframework.core.io.Resource;

public class ReportProductOfferingService extends ConfigService {

	private static final String LOAN_PRODUCT_IDS = "loan.product.ids";
	private static final String SAVINGS_PRODUCT_IDS = "savings.product.ids";

	private final LoanPrdBusinessService loanProductBusinessService;
	private final SavingsPrdBusinessService savingsProductBusinessService;

	public ReportProductOfferingService(
			LoanPrdBusinessService loanProductBusinessService,
			SavingsPrdBusinessService savingsProductBusinessService,
			Resource productOfferingConfig) {
		super(productOfferingConfig);
		this.loanProductBusinessService = loanProductBusinessService;
		this.savingsProductBusinessService = savingsProductBusinessService;
	}

	protected Short getLoanProduct1() throws ServiceException {
		return Short.valueOf(getPropertyValues(LOAN_PRODUCT_IDS).get(0));
	}

	protected Short getLoanProduct2() throws ServiceException {
		return Short.valueOf(getPropertyValues(LOAN_PRODUCT_IDS).get(1));
	}

	protected Short getSavingsProduct1() throws ServiceException {
		return Short.valueOf(getPropertyValues(SAVINGS_PRODUCT_IDS).get(0));
	}

	protected Short getSavingsProduct2() throws ServiceException {
		return Short.valueOf(getPropertyValues(SAVINGS_PRODUCT_IDS).get(1));
	}

	LoanOfferingBO getLoanOffering1() throws ServiceException {
		return loanProductBusinessService.getLoanOffering(getLoanProduct1());
	}

	LoanOfferingBO getLoanOffering2() throws ServiceException {
		return loanProductBusinessService.getLoanOffering(getLoanProduct2());
	}

	SavingsOfferingBO getSavingsOffering1() throws ServiceException {
		return savingsProductBusinessService
				.getSavingsProduct(getSavingsProduct1());
	}

	SavingsOfferingBO getSavingsOffering2() throws ServiceException {
		return savingsProductBusinessService
				.getSavingsProduct(getSavingsProduct2());
	}
}
