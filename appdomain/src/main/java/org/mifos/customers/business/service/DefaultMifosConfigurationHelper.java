package org.mifos.customers.business.service;

import org.mifos.config.persistence.ConfigurationPersistence;

public class DefaultMifosConfigurationHelper implements MifosConfigurationHelper {

    @Override
    public boolean isLoanScheduleRepaymentIndependentOfCustomerMeetingEnabled() {
        return new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled();
    }

}
