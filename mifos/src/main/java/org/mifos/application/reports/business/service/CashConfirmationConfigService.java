package org.mifos.application.reports.business.service;


import org.mifos.framework.business.service.ConfigService;
import org.mifos.framework.exceptions.ServiceException;
import org.springframework.core.io.Resource;

public class CashConfirmationConfigService extends ConfigService {
	public CashConfirmationConfigService(Resource resource) {
		super(resource);
	}

	public boolean displaySignatureColumn(Integer columnNumber)
			throws ServiceException {
		return isPropertyPresent(ReportConfigServiceConstants.DISPLAY_SIGNATURE_COLUMN + "." + columnNumber)
				&& Boolean.valueOf(getProperty(ReportConfigServiceConstants.DISPLAY_SIGNATURE_COLUMN + "."
						+ columnNumber));
	}
}
