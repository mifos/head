package org.mifos.accounts.fees.servicefacade;



import java.util.Date;
import java.util.List;

import org.mifos.accounts.fees.business.CategoryTypeEntity;
import org.mifos.accounts.fees.business.FeeFormulaEntity;
import org.mifos.accounts.fees.business.FeeFrequencyTypeEntity;
import org.mifos.accounts.fees.business.FeePaymentEntity;
import org.mifos.accounts.fees.business.FeeStatusEntity;
import org.mifos.accounts.fees.entities.AmountFeeEntity;
import org.mifos.accounts.fees.entities.FeeEntity;
import org.mifos.accounts.fees.entities.FeeFrequencyEntity;
import org.mifos.accounts.fees.entities.RateFeeEntity;
import org.mifos.accounts.fees.exceptions.FeeException;
import org.mifos.accounts.fees.persistence.FeeDao;
import org.mifos.accounts.fees.persistence.MasterEntityDao;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeeChangeType;
import org.mifos.accounts.fees.util.helpers.FeeConstants;
import org.mifos.accounts.fees.util.helpers.FeeFormula;
import org.mifos.accounts.fees.util.helpers.FeeFrequencyType;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.accounts.fees.util.helpers.FeeStatus;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.PropertyNotFoundException;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;

public class FeeServiceImpl implements FeeService {

    @Autowired
    private MasterEntityDao masterDao;

    @Autowired
    private FeeDao feeDao;

    public FeeServiceImpl() {
    }

    public FeeServiceImpl(MasterEntityDao masterDao, FeeDao feeDao) {
        super();
        this.masterDao = masterDao;
        this.feeDao = feeDao;
    }

    @Override
    public FeeEntity createOneTimeFee(UserContext userContext, String feeName, boolean isCustomerDefaultFee,
            boolean isRateFee, Double rate, Money feeMoney, FeeCategory category,
            FeeFormula feeFormula, GLCodeEntity glCode, OfficeBO office, FeePayment feePayment) throws PersistenceException, FeeException {
        FeePaymentEntity feePaymentEntity = getFeePaymentEntity(userContext, feePayment);
        CategoryTypeEntity feeCategoryType = getFeeCategoryTypeEntity(userContext, category);

        FeeEntity fee = null;
        if (isRateFee) {
            FeeFormulaEntity feeFormulaEntity = getFeeFormulaEntity(userContext, feeFormula);
            validateRate(rate, feeFormulaEntity);
            fee = new RateFeeEntity(feeName, feeCategoryType, glCode, rate, feeFormulaEntity, isCustomerDefaultFee,
                    getFeeOffice(office));
        } else {
            validateAmount(feeMoney);
            fee = new AmountFeeEntity(feeName, feeCategoryType, glCode, feeMoney, isCustomerDefaultFee,
                    getFeeOffice(office));
        }
        FeeFrequencyTypeEntity feeFrequencyType = getFeeFrequencyTypeEntity(userContext, FeeFrequencyType.ONETIME);
        setFeeFrequencyCreateDetailsActiveStatusAndDefaultToCustomer(
                fee, new FeeFrequencyEntity(feeFrequencyType, fee, feePaymentEntity, null),
                isCustomerDefaultFee, userContext);
        feeDao.create(fee);
        return fee;
    }

    @Override
    public FeeEntity createPeriodicFee(UserContext userContext, String feeName, boolean isCustomerDefaultFee,
            boolean isRateFee, Double rate, Money feeMoney, FeeCategory category, FeeFormula feeFormula,
            GLCodeEntity glCode, OfficeBO office, RecurrenceType feeRecurrenceType, Short recurAfter)
            throws PersistenceException, FeeException {
        CategoryTypeEntity feeCategoryType = getFeeCategoryTypeEntity(userContext, category);
        MeetingBO feeMeeting;
        try {
            feeMeeting = new MeetingBO(feeRecurrenceType, recurAfter, getCreateDate(), MeetingType.PERIODIC_FEE);
        } catch (MeetingException e) {
            //FIXME: Should introduce constant for INVALID_MEETING
            throw new FeeException(FeeConstants.INVALID_FEE_FREQUENCY, e);
        }

        FeeEntity fee = null;
        if (isRateFee) {
            FeeFormulaEntity feeFormulaEntity = getFeeFormulaEntity(userContext, feeFormula);
            validateRate(rate, feeFormulaEntity);
            fee = new RateFeeEntity(feeName, feeCategoryType, glCode,
                    rate, feeFormulaEntity, isCustomerDefaultFee, getFeeOffice(office));
        } else {
            validateAmount(feeMoney);
            fee = new AmountFeeEntity(feeName, feeCategoryType, glCode,
                    feeMoney, isCustomerDefaultFee, getFeeOffice(office));

        }
        FeeFrequencyTypeEntity feeFrequencyType = getFeeFrequencyTypeEntity(userContext, FeeFrequencyType.PERIODIC);
        setFeeFrequencyCreateDetailsActiveStatusAndDefaultToCustomer(
                fee, new FeeFrequencyEntity(feeFrequencyType, fee, null, feeMeeting),
                isCustomerDefaultFee, userContext);
        feeDao.create(fee);
        return fee;
    }


    private void setFeeFrequencyCreateDetailsActiveStatusAndDefaultToCustomer(FeeEntity fee, FeeFrequencyEntity feeFrequency,
            boolean isCustomerDefaultFee, UserContext userContext) throws PersistenceException, FeeException {
        fee.setFeeFrequency(feeFrequency);
        setCreateDetails(userContext, fee);
        fee.setFeeStatus(retrieveFeeStatusEntity(FeeStatus.ACTIVE, userContext.getLocaleId()));
        if (isCustomerDefaultFee) {
            try {
                fee.defaultToCustomer();
            } catch (PropertyNotFoundException e) {
                //FIXME: Introduce new error constant
                throw new FeeException(FeeConstants.INVALID_FEE,e);
            }
        }
    }


    private void setCreateDetails(UserContext userContext, FeeEntity fee) {
        //NOTE: copied from FeeBO!
        fee.setCreatedDate(getCreateDate());
        fee.setCreatedBy(userContext.getId());
    }

    private Date getCreateDate() {
        return new org.mifos.framework.util.DateTimeService().getCurrentJavaDateTime();
    }

    private OfficeBO getFeeOffice(OfficeBO office) throws PersistenceException {
        //returns headoffice if not specified.
        return (office == null) ? new OfficePersistence().getHeadOffice() : office;
    }

    private CategoryTypeEntity getFeeCategoryTypeEntity(UserContext userContext, FeeCategory categoryType)
            throws PersistenceException {
        return masterDao.retrieveMasterEntity(
                CategoryTypeEntity.class, categoryType.getValue(), userContext.getLocaleId());
    }

    private FeeFormulaEntity getFeeFormulaEntity(UserContext userContext, FeeFormula feeFormula)
            throws PersistenceException {
        return masterDao.retrieveMasterEntity(FeeFormulaEntity.class, feeFormula.getValue(), userContext.getLocaleId());
    }

    private FeePaymentEntity getFeePaymentEntity(UserContext userContext, FeePayment feePaymentType)
            throws PersistenceException {
        return masterDao.retrieveMasterEntity(FeePaymentEntity.class, feePaymentType.getValue(), userContext
                .getLocaleId());
    }

    private FeeFrequencyTypeEntity getFeeFrequencyTypeEntity(UserContext userContext, FeeFrequencyType feefrequencytype)
            throws PersistenceException {
        return masterDao.retrieveMasterEntity(FeeFrequencyTypeEntity.class, feefrequencytype.getValue(), userContext
                .getLocaleId());
    }

    private FeeStatusEntity retrieveFeeStatusEntity(final FeeStatus status, Short localeId) throws PersistenceException {
            return masterDao.retrieveMasterEntity(FeeStatusEntity.class, status.getValue(), localeId);
    }

    private void validateAmount(final Money amount) throws FeeException {
        if (amount == null || amount.isLessThanOrEqualZero()) {
            throw new FeeException(FeeConstants.INVALID_FEE_AMOUNT);
        }
    }

    private void validateRate(Double rate, FeeFormulaEntity feeFormula) throws FeeException {
        if (rate == null || rate.doubleValue() <= 0.0 || feeFormula == null) {
            throw new FeeException(FeeConstants.INVALID_FEE_RATE_OR_FORMULA);
        }
    }

    @Override
    public List<FeeEntity> retrieveCustomerFees() {
        return feeDao.retrieveCustomerFees();
    }

    @Override
    public FeeEntity findFeeById(Short feeId) {
        return feeDao.getDetails(feeId);
    }

    @Override
    public List<FeeEntity> retrieveProductFees() {
        return feeDao.retrieveProductFees();
    }

    @Override
    public FeeEntity updateFee(UserContext userContext, Short feeId, Money feeMoney, Double rate, FeeStatus feeStatus) throws PersistenceException {
        FeeEntity fee = feeDao.getDetails(feeId);
        FeeChangeType feeChangeType;
        if (fee instanceof AmountFeeEntity) {
            AmountFeeEntity amountFee = (AmountFeeEntity) fee;
            feeChangeType = amountFee.calculateChangeType(feeMoney, feeStatus);
            amountFee.setFeeAmount(feeMoney);
        }
        else {
            RateFeeEntity rateFee = (RateFeeEntity) fee;
            feeChangeType = rateFee.calculateChangeType(rate, feeStatus);
            rateFee.setRate(rate);
        }
        fee.setFeeStatus(retrieveFeeStatusEntity(feeStatus, userContext.getLocaleId()));
        fee.updateFeeChangeType(feeChangeType);
        fee.setUpdatedDate(new DateTimeService().getCurrentJavaDateTime());
        fee.setUpdatedBy(userContext.getId());
        feeDao.update(fee);
        return fee;
    }

}
