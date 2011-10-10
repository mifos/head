package org.mifos.config.service.impl;

import org.mifos.config.AccountingRules;
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

}
