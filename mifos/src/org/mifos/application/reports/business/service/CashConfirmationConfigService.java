package org.mifos.application.reports.business.service;


import org.mifos.framework.business.service.ConfigService;
import org.mifos.framework.exceptions.ServiceException;
import org.springframework.core.io.Resource;

public class CashConfirmationConfigService extends ConfigService {
	private static final String DISPLAY_SIGNATURE_COLUMN = "display.signature.column";

	public CashConfirmationConfigService(Resource resource) {
		super(resource);
	}

	public boolean displaySignatureColumn(Integer columnNumber)
			throws ServiceException {
		return isPropertyPresent(DISPLAY_SIGNATURE_COLUMN + "." + columnNumber)
				&& Boolean.valueOf(getProperty(DISPLAY_SIGNATURE_COLUMN + "."
						+ columnNumber));
	}
}
