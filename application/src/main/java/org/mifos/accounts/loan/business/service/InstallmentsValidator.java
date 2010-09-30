package org.mifos.accounts.loan.business.service;

import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.framework.exceptions.ServiceException;

import java.util.List;

public interface InstallmentsValidator {
    void validate(List<RepaymentScheduleInstallment> installments) throws ServiceException;
}
