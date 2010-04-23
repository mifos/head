package org.mifos.accounts.fees.servicefacade;

import java.util.ArrayList;
import java.util.List;

import org.mifos.accounts.fees.business.CategoryTypeEntity;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeFormulaEntity;
import org.mifos.accounts.fees.business.FeeFrequencyTypeEntity;
import org.mifos.accounts.fees.business.FeePaymentEntity;
import org.mifos.accounts.fees.business.FeeStatusEntity;
import org.mifos.accounts.fees.business.service.FeeBusinessService;
import org.mifos.accounts.fees.struts.action.FeeParameters;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.financial.business.service.FinancialBusinessService;
import org.mifos.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;

public class WebTierFeeServiceFacade implements FeeServiceFacade {

    private static final FeeBusinessService feeBusinessService = new FeeBusinessService();
    private MasterDataService masterDataService = new MasterDataService();
    private FinancialBusinessService financeService = new FinancialBusinessService();

    public List<FeeDto> getProductFees() throws ServiceException {
        List<FeeBO> feeBOs = feeBusinessService.retrieveProductFees();
        return mapFeeDTOs(feeBOs);
    }

    public List<FeeDto> getCustomerFees() throws ServiceException {
        List<FeeBO> feeBOs = feeBusinessService.retrieveCustomerFees();
        return mapFeeDTOs(feeBOs);
    }

    private List<FeeDto> mapFeeDTOs(List<FeeBO> feeBOs) {
        List<FeeDto> fees = new ArrayList<FeeDto>();
        for (FeeBO feeBO : feeBOs) {
            fees.add(mapFeeDto(feeBO));
        }
        return fees;
    }

    private FeeDto mapFeeDto(FeeBO feeBO) {
        FeeDto feeDto = new FeeDto();
        feeDto.setId(Short.toString(feeBO.getFeeId()));
        feeDto.setName(feeBO.getFeeName());
        feeDto.setCategoryType(feeBO.getCategoryType().getName());
        feeDto.setFeeStatus(mapFeeStatusDto(feeBO));
        feeDto.setActive(feeBO.isActive());
        return feeDto;
    }

    private FeeStatusDto mapFeeStatusDto(FeeBO feeBO) {
        FeeStatusDto feeStatus = new FeeStatusDto();
        FeeStatusEntity feeStatusBO = feeBO.getFeeStatus();
        feeStatus.setId(Short.toString(feeStatusBO.getId()));
        feeStatus.setName(feeStatusBO.getName());
        return feeStatus;
    }

    @Override
    public FeeParameters parameters(Short localeId) throws ServiceException {
        List<GLCodeEntity> glCodes = retrieveGlCodes();
        List<CategoryTypeEntity> categories = masterDataService.retrieveMasterEntities(CategoryTypeEntity.class, localeId);
        List<FeeFormulaEntity> formulas = masterDataService.retrieveMasterEntities(FeeFormulaEntity.class, localeId);
        List<FeeFrequencyTypeEntity> frequencies = masterDataService.retrieveMasterEntities(FeeFrequencyTypeEntity.class, localeId);
        List<FeePaymentEntity> timesOfCharging = masterDataService.retrieveMasterEntities(FeePaymentEntity.class, localeId);
        List<MasterDataEntity> timesOfChargeingCustomer = new ArrayList<MasterDataEntity>();
        for (MasterDataEntity timeOfCharging : timesOfCharging) {
            if (timeOfCharging.getId().equals(FeePayment.UPFRONT.getValue())) {
                timesOfChargeingCustomer.add(timeOfCharging);
            }
        }
        return new FeeParameters(categories, timesOfCharging, timesOfChargeingCustomer, formulas, frequencies, glCodes);
    }

    private List<GLCodeEntity> retrieveGlCodes() throws ServiceException {
        //TODO: Should General ledger code retrieval logic be here? As in should one service call another service
        List<GLCodeEntity> glCodes;
        try {
            glCodes = financeService.getGLCodes(FinancialActionConstants.FEEPOSTING,
                    FinancialConstants.CREDIT);
        } catch (SystemException e) {
            throw new ServiceException(e);
        } catch (ApplicationException e) {
            throw new ServiceException(e);
        }
        return glCodes;
    }

}
