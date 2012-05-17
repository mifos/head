package org.mifos.config.service.impl;

import org.mifos.config.AccountingRules;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.config.service.AccountingConfigurationService;

public class AccountingConfigurationServiceImpl implements AccountingConfigurationService {

    @Override
    public Short getDigitsBeforeDecimal() {
        return AccountingRules.getDigitsBeforeDecimal();
    }

    @Override
    public Short getDigitsAfterDecimal() {
        return AccountingRules.getDigitsAfterDecimal();
    }

	@Override
	public int getGlNameMode() {
		return AccountingRules.getGlNamesMode();
	}

    @Override
    public boolean isDefaultMifosCurrency(String currencyCode) {
        return AccountingRules.getMifosCurrency(new ConfigurationPersistence()).getCurrencyCode().equals(currencyCode);
    }
    
}
