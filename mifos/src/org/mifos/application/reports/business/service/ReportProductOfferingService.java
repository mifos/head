package org.mifos.application.reports.business.service;

import java.io.IOException;
import java.util.Properties;

import org.mifos.application.reports.exceptions.ReportServiceInitializationException;
import org.mifos.framework.util.helpers.FilePaths;

public class ReportProductOfferingService {

	private static final String LOAN_PRODUCT_IDS = "loan.product.ids";
	private static final String SAVINGS_PRODUCT_IDS = "savings.product.ids";
	private static final String IDS_DELIMITER = "\\s*,\\s*";
	
	private String[] configuredSavingProducts;
	private String[] configuredLoanProducts;

	public ReportProductOfferingService() {
		try {
			init();
		}
		catch (IOException e) {
			throw new ReportServiceInitializationException("Failed to initialize Report Product Offering Service", e);
		}
	}

	public void init() throws IOException {
		Properties properties = new Properties();
		properties
				.load(getClass()
						.getResourceAsStream(
								FilePaths.REPORT_PRODUCT_OFFERING_CONFIG));
		configuredSavingProducts = properties
				.getProperty(SAVINGS_PRODUCT_IDS).split(IDS_DELIMITER);
		configuredLoanProducts = properties.getProperty(LOAN_PRODUCT_IDS)
				.split(IDS_DELIMITER);
	}

	public Short getLoanProduct1() {
		return Short.valueOf(configuredLoanProducts[0]);
	}
	
	public Short getLoanProduct2() {
		return Short.valueOf(configuredLoanProducts[1]);
	}

	public Short getSavingsProduct1() {
		return Short.valueOf(configuredSavingProducts[0]);
	}

	public Short getSavingsProduct2() {
		return Short.valueOf(configuredSavingProducts[1]);
	}
}
