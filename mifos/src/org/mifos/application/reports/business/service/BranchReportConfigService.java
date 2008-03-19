package org.mifos.application.reports.business.service;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.business.service.ConfigService;
import org.mifos.framework.components.configuration.persistence.ConfigurationPersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.springframework.core.io.Resource;

public class BranchReportConfigService extends ConfigService {

	private static final String CURRENCY_ID = "currency.id";
	private static final String REPLACEMENT_FIELD_VALUE = "replacement.field.value";
	private static final String REPLACEMENT_FIELD_ID = "replacement.field.id";
	private static final String LOAN_CYCLE_PERIOD = "loan.cycle.period";
	private static final String GRACE_PERIOD_DAYS = "grace.period.days";
	private ConfigurationPersistence configurationPersistence;

	public BranchReportConfigService(Resource configResource) {
		super(configResource);
		configurationPersistence = new ConfigurationPersistence();
	}

	public Integer getGracePeriodDays() throws ServiceException {
		return Integer.parseInt(getProperty(GRACE_PERIOD_DAYS));
	}

	public Integer getLoanCyclePeriod() throws ServiceException {
		return Integer.parseInt(getProperty(LOAN_CYCLE_PERIOD));
	}

	public Short getReplacementFieldId() throws ServiceException {
		return Short.parseShort(getProperty(REPLACEMENT_FIELD_ID));
	}

	public String getReplacementFieldValue() throws ServiceException {
		return getProperty(REPLACEMENT_FIELD_VALUE);
	}

	private Short getCurrencyIdForReports() throws ServiceException {
		return Short.valueOf(getProperty(CURRENCY_ID));
	}

	public MifosCurrency getCurrency() throws ServiceException {
		try {
			return (MifosCurrency) configurationPersistence
					.getPersistentObject(MifosCurrency.class,
							getCurrencyIdForReports());
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

}
