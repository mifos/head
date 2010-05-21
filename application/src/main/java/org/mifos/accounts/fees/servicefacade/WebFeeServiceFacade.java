package org.mifos.accounts.fees.servicefacade;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mifos.accounts.fees.business.CategoryTypeEntity;
import org.mifos.accounts.fees.business.FeeFormulaEntity;
import org.mifos.accounts.fees.business.FeeFrequencyTypeEntity;
import org.mifos.accounts.fees.business.FeePaymentEntity;
import org.mifos.accounts.fees.business.FeeStatusEntity;
import org.mifos.accounts.fees.entities.AmountFeeEntity;
import org.mifos.accounts.fees.entities.FeeEntity;
import org.mifos.accounts.fees.entities.RateFeeEntity;
import org.mifos.accounts.fees.exceptions.FeeException;
import org.mifos.accounts.fees.struts.action.FeeParameters;
import org.mifos.accounts.fees.util.helpers.FeeFrequencyType;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.financial.business.service.FinancialBusinessService;
import org.mifos.accounts.financial.servicefacade.GLCodeDto;
import org.mifos.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.config.AccountingRules;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.PropertyNotFoundException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;

public class WebFeeServiceFacade implements FeeServiceFacade {

    //FIXME: replace the 3 below with spring services, when such services are *refactored* to be spring injected
    private MasterDataService masterDataService = new MasterDataService();
    private FinancialBusinessService financeService = new FinancialBusinessService();
    private OfficePersistence officePersistence = new OfficePersistence();


    @Autowired
    private FeeService feeService;

    @Override
    public FeeDto createFee(FeeCreateRequest feeCreateRequest, UserContext userContext) throws ServiceException {
        try {
            Money feeMoney = new Money(getCurrency(feeCreateRequest.getCurrencyId()), getAmount(feeCreateRequest.getAmount()));
            GLCodeEntity glCodeEntity = masterDataService.findGLCodeEntity(feeCreateRequest.getGlCode());
            FeeEntity fee;
            if (feeCreateRequest.getFeeFrequencyType().equals(FeeFrequencyType.ONETIME)) {
                fee = feeService.createOneTimeFee(userContext,
                        feeCreateRequest.getFeeName(), feeCreateRequest.isCustomerDefaultFee(),
                        feeCreateRequest.isRateFee(), feeCreateRequest.getRate(), feeMoney,
                        feeCreateRequest.getCategoryType(), feeCreateRequest.getFeeFormula(),
                        glCodeEntity, getHeadOffice(), feeCreateRequest.getFeePaymentType());
            } else {
                Short recurAfter =
                    feeCreateRequest.getFeeRecurrenceType().equals(RecurrenceType.MONTHLY) ?
                            feeCreateRequest.getMonthRecurAfter() : feeCreateRequest.getWeekRecurAfter();
                fee = feeService.createPeriodicFee(userContext,
                        feeCreateRequest.getFeeName(), feeCreateRequest.isCustomerDefaultFee(),
                        feeCreateRequest.isRateFee(), feeCreateRequest.getRate(), feeMoney,
                        feeCreateRequest.getCategoryType(), feeCreateRequest.getFeeFormula(),
                        glCodeEntity, getHeadOffice(), feeCreateRequest.getFeeRecurrenceType(), recurAfter);
            }
            return mapFeeDto(fee);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        } catch (FeeException e) {
            throw new ServiceException(e);
        } catch (PropertyNotFoundException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<FeeDto> getCustomerFees() throws ServiceException {
        List<FeeEntity> fees = feeService.retrieveCustomerFees();
        try {
            return mapFeeEnttitesToFeeDtos(fees);
        } catch (PropertyNotFoundException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public FeeDto getFeeDetails(Short feeId) throws ServiceException {
        try {
            return mapFeeDto(feeService.findFeeById(feeId));
        } catch (PropertyNotFoundException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public FeeParameters getFeeParameters(Short localeId) throws ServiceException {
        List<GLCodeEntity> glCodes = retrieveGlCodes();
        List<CategoryTypeEntity> categories = masterDataService.retrieveMasterEntities(CategoryTypeEntity.class,
                localeId);
        List<FeeFormulaEntity> formulas = masterDataService.retrieveMasterEntities(FeeFormulaEntity.class, localeId);
        List<FeeFrequencyTypeEntity> frequencies = masterDataService.retrieveMasterEntities(
                FeeFrequencyTypeEntity.class, localeId);
        List<FeePaymentEntity> timesOfCharging = masterDataService.retrieveMasterEntities(FeePaymentEntity.class,
                localeId);
        List<MasterDataEntity> timesOfChargeingCustomer = new ArrayList<MasterDataEntity>();
        for (MasterDataEntity timeOfCharging : timesOfCharging) {
            if (timeOfCharging.getId().equals(FeePayment.UPFRONT.getValue())) {
                timesOfChargeingCustomer.add(timeOfCharging);
            }
        }
        return new FeeParameters(categories, timesOfCharging, timesOfChargeingCustomer, formulas, frequencies, glCodes);
    }

    @Override
    public List<FeeDto> getProductFees() throws ServiceException {
        List<FeeEntity> fees = feeService.retrieveProductFees();
        try {
            return mapFeeEnttitesToFeeDtos(fees);
        } catch (PropertyNotFoundException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void updateFee(FeeUpdateRequest feeUpdateRequest, UserContext userContext) throws ServiceException {
        Money feeMoney = new Money(getCurrency(feeUpdateRequest.getCurrencyId()), getAmount(feeUpdateRequest.getAmount()));
        try {
            feeService.updateFee(userContext, feeUpdateRequest.getFeeId(), feeMoney, feeUpdateRequest.getRateValue(), feeUpdateRequest.getFeeStatusValue());
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }


    private MifosCurrency getCurrency(Short currencyId) {
        MifosCurrency currency;
        if (currencyId == null) {
            // Currency is passed from Form only for Loan (Amount) Fees in multi-currency settings
            currency = Money.getDefaultCurrency();
        } else {
            currency = AccountingRules.getCurrencyByCurrencyId(currencyId);
        }
        return currency;
    }

    private String getAmount(String amount) {
        return StringUtils.isBlank(amount) ? "0.0" : amount;
    }

    private OfficeBO getHeadOffice() throws PersistenceException {
        //FIXME: replace with spring injection of officeservice, when that is refactored
        return officePersistence.getHeadOffice();
    }

    private FeeDto mapFeeDto(FeeEntity fee) throws PropertyNotFoundException {
        FeeDto feeDto = new FeeDto();
        feeDto.setId(Short.toString(fee.getFeeId()));
        feeDto.setName(fee.getFeeName());
        feeDto.setCategoryType(fee.getCategoryType().getName());
        feeDto.setFeeStatus(mapFeeStatusDto(fee.getFeeStatus()));
        feeDto.setOneTime(fee.getFeeFrequency().isOneTime());
        feeDto.setFeeFrequency(mapFeeFrequencyDto(fee.getFeeFrequency()));
        feeDto.setActive(fee.getFeeStatus().isActive());
        feeDto.setGlCode(fee.getGlCode().getGlcode());
        feeDto.setCustomerDefaultFee(fee.isCustomerDefaultFee());
        feeDto.setRateBasedFee(fee instanceof RateFeeEntity);

        feeDto.setChangeType(fee.getFeeChangeType().getValue());
        feeDto.setFeeFrequencyType(fee.getFeeFrequency().getFeeFrequencyType().getName());
        feeDto.setGlCodeDto(mapGLCodeDto(fee.getGlCode()));

        feeDto.setOneTime(fee.getFeeFrequency().isOneTime());
        feeDto.setPeriodic(fee.getFeeFrequency().isPeriodic());
        feeDto.setTimeOfDisbursement(fee.getFeeFrequency().isTimeOfDisbursement());

        if (fee instanceof AmountFeeEntity) {
            feeDto.setAmount(((AmountFeeEntity) fee).getFeeAmount());
        } else {
            RateFeeEntity rateFee = (RateFeeEntity) fee;
            feeDto.setRate(rateFee.getRate());
            feeDto.setFeeFormula(mapFeeFormulaDto(rateFee.getFeeFormula()));
        }
        return feeDto;
    }

    private FeeStatusDto mapFeeStatusDto(FeeStatusEntity feeStatusEntity) {
        FeeStatusDto feeStatus = new FeeStatusDto();
        feeStatus.setId(Short.toString(feeStatusEntity.getId()));
        feeStatus.setName(feeStatusEntity.getName());
        return feeStatus;
    }

    private FeeFrequencyDto mapFeeFrequencyDto(org.mifos.accounts.fees.entities.FeeFrequencyEntity feeFrequency) {
        FeeFrequencyDto feeFrequencyDto = new FeeFrequencyDto();
        FeeFrequencyTypeEntity feeFrequencyType = feeFrequency.getFeeFrequencyType();
        feeFrequencyDto.setType(feeFrequencyType.getName());
        if (feeFrequencyType.isOneTime()) {
            feeFrequencyDto.setPayment(feeFrequency.getFeePayment().getName());
        } else {
            MeetingBO feeMeetingFrequency = feeFrequency.getFeeMeetingFrequency();
            feeFrequencyDto.setMonthly(feeMeetingFrequency.isMonthly());
            feeFrequencyDto.setWeekly(feeMeetingFrequency.isWeekly());
            feeFrequencyDto.setRecurAfterPeriod(feeMeetingFrequency.getRecurAfter().toString());
        }
        return feeFrequencyDto;
    }

    private GLCodeDto mapGLCodeDto(GLCodeEntity glCode) {
        GLCodeDto glCodeDto = new GLCodeDto();
        glCodeDto.setGlcode(glCode.getGlcode());
        glCodeDto.setGlcodeId(glCode.getGlcodeId());
        return glCodeDto;
    }

    private FeeFormulaDto mapFeeFormulaDto(FeeFormulaEntity feeFormula) {
        FeeFormulaDto feeFormulaDto = new FeeFormulaDto();
        feeFormulaDto.setId(feeFormula.getId());
        feeFormulaDto.setName(feeFormula.getName());
        return feeFormulaDto;
    }

    private List<FeeDto> mapFeeEnttitesToFeeDtos(List<FeeEntity> feeEntities) throws PropertyNotFoundException {
        if (feeEntities == null) {
            return new ArrayList<FeeDto>();
        }
        List<FeeDto> fees = new ArrayList<FeeDto>();
        for (FeeEntity fee : feeEntities) {
            fees.add(mapFeeDto(fee));
        }
        return fees;
    }

    private List<GLCodeEntity> retrieveGlCodes() throws ServiceException {
        // FIXME: Should General ledger code retrieval logic be here? As in should one service call another service
        List<GLCodeEntity> glCodes;
        try {
            glCodes = financeService.getGLCodes(FinancialActionConstants.FEEPOSTING, FinancialConstants.CREDIT);
        } catch (SystemException e) {
            throw new ServiceException(e);
        } catch (ApplicationException e) {
            throw new ServiceException(e);
        }
        return glCodes;
    }

}
