package org.mifos.accounts.fees.servicefacade;

import org.mifos.accounts.fees.business.CategoryTypeEntity;
import org.mifos.accounts.fees.business.FeeFormulaEntity;
import org.mifos.accounts.fees.entities.FeeEntity;
import org.mifos.accounts.fees.exceptions.FeeException;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeeFormula;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;

public interface FeeService {

    public FeeEntity createOneTimeFee(UserContext userContext, String feeName, boolean isCustomerDefaultFee, boolean isRateFee,
            Double rate, Money feeMoney, FeePayment feePaymentType, FeeCategory categoryType, FeeFormula feeFormula,
            GLCodeEntity glCode, OfficeBO office) throws PersistenceException, FeeException;

    public FeeEntity createPeriodicFee(UserContext userContext, String feeName, boolean isCustomerDefaultFee, boolean isRateFee,
            Double rate, Money feeMoney, CategoryTypeEntity categoryType, FeeFormula feeFormula,
            GLCodeEntity glCode, OfficeBO office, RecurrenceType feeRecurrenceType, Short recurAfter) throws PersistenceException, FeeException;
}
