/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.accounts.penalties.servicefacade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.financial.business.service.GeneralLedgerDao;
import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.accounts.penalties.business.AmountPenaltyBO;
import org.mifos.accounts.penalties.business.PenaltyBO;
import org.mifos.accounts.penalties.business.PenaltyCategoryEntity;
import org.mifos.accounts.penalties.business.PenaltyFormulaEntity;
import org.mifos.accounts.penalties.business.PenaltyFrequencyEntity;
import org.mifos.accounts.penalties.business.PenaltyPeriodEntity;
import org.mifos.accounts.penalties.business.PenaltyStatusEntity;
import org.mifos.accounts.penalties.business.RatePenaltyBO;
import org.mifos.accounts.penalties.persistence.PenaltyDao;
import org.mifos.accounts.penalties.util.helpers.PenaltyCategory;
import org.mifos.accounts.penalties.util.helpers.PenaltyFormula;
import org.mifos.accounts.penalties.util.helpers.PenaltyFrequency;
import org.mifos.accounts.penalties.util.helpers.PenaltyPeriod;
import org.mifos.accounts.penalties.util.helpers.PenaltyStatus;
import org.mifos.accounts.penalty.servicefacade.PenaltyServiceFacade;
import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.AccountingRules;
import org.mifos.core.MifosRuntimeException;
import org.mifos.dto.domain.PenaltyDto;
import org.mifos.dto.domain.PenaltyFormDto;
import org.mifos.dto.screen.PenaltyParametersDto;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

public class WebTierPenaltyServiceFacade implements PenaltyServiceFacade {
    private final PenaltyDao penaltyDao;
    private final GeneralLedgerDao generalLedgerDao;

    @Autowired
    public WebTierPenaltyServiceFacade(PenaltyDao penaltyDao, GeneralLedgerDao generalLedgerDao) {
        this.penaltyDao = penaltyDao;
        this.generalLedgerDao = generalLedgerDao;
    }
    
    @Override
    public void createPenalty(PenaltyFormDto dto) {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(user);
        
        try {
            PenaltyCategory penaltyCategory = dto.getCategoryType() != null ? PenaltyCategory.getPenaltyCategory(dto.getCategoryType()) : null;
            PenaltyFrequency penaltyFrequency = dto.getPenaltyFrequency() != null ? PenaltyFrequency.getPenaltyFrequencyType(dto.getPenaltyFrequency()) : null;
            PenaltyPeriod penaltyPeriod = dto.getPenaltyPeriod() != null ? PenaltyPeriod.getPenaltyPeriod(dto.getPenaltyPeriod()) : null;
            PenaltyFormula penaltyFormula = dto.getPenaltyFormula() != null ? PenaltyFormula.getPenaltyFormula(dto.getPenaltyFormula()) : null;
            
            PenaltyCategoryEntity penaltyCategoryEntity = this.penaltyDao.findPenaltyCategoryEntityByType(penaltyCategory);
            PenaltyPeriodEntity penaltyPeriodEntity = this.penaltyDao.findPenaltyPeriodEntityByType(penaltyPeriod);
            PenaltyFrequencyEntity penaltyFrequencyEntity = this.penaltyDao.findPenaltyFrequencyEntityByType(penaltyFrequency);
            GLCodeEntity glCodeEntity = this.generalLedgerDao.findGlCodeById(dto.getGlCode());

            PenaltyBO penaltyBO = null;
            String penaltyName = dto.getPenaltyName();
            Integer periodDuration = dto.getDuration();
            Integer min = dto.getMin();
            Integer max = dto.getMax();
            
            if (dto.isRatePenalty()) {
                Double rate = dto.getRate();
                PenaltyFormulaEntity formula = this.penaltyDao.findPenaltyFormulaEntityByType(penaltyFormula);
                
                penaltyBO = new RatePenaltyBO(userContext, penaltyName, penaltyCategoryEntity, penaltyPeriodEntity,
                        periodDuration, min, max, penaltyFrequencyEntity, glCodeEntity, formula, rate);
            } else {
                Money amount = new Money(getCurrency(dto.getCurrencyId()), dto.getAmount());
                
                penaltyBO = new AmountPenaltyBO(userContext, penaltyName, penaltyCategoryEntity, penaltyPeriodEntity,
                        periodDuration, min, max, penaltyFrequencyEntity, glCodeEntity, amount);
            }

            try {
                StaticHibernateUtil.startTransaction();
                this.penaltyDao.save(penaltyBO);
                StaticHibernateUtil.commitTransaction();
            } catch (Exception e) {
                StaticHibernateUtil.rollbackTransaction();
                throw new MifosRuntimeException(e);
            } finally {
                StaticHibernateUtil.closeSession();
            }
        } catch (ApplicationException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }
    
    @Override
    public void updatePenalty(PenaltyFormDto dto) {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(user);
        
        PenaltyBO penaltyBO = this.penaltyDao.findPenalty(dto.getId());
        penaltyBO.updateDetails(userContext);
        
        try {
            PenaltyFrequency penaltyFrequency = dto.getPenaltyFrequency() != null ? PenaltyFrequency.getPenaltyFrequencyType(dto.getPenaltyFrequency()) : null;
            PenaltyPeriod penaltyPeriod = dto.getPenaltyPeriod() != null ? PenaltyPeriod.getPenaltyPeriod(dto.getPenaltyPeriod()) : null;
            PenaltyFormula penaltyFormula = dto.getPenaltyFormula() != null ? PenaltyFormula.getPenaltyFormula(dto.getPenaltyFormula()) : null;
            PenaltyStatus penaltyStatus = dto.getPenaltyStatus() != null ? PenaltyStatus.getPenaltyStatus(dto.getPenaltyStatus()) : null;
            
            penaltyBO.setPeriodType(this.penaltyDao.findPenaltyPeriodEntityByType(penaltyPeriod));
            penaltyBO.setPenaltyFrequency(this.penaltyDao.findPenaltyFrequencyEntityByType(penaltyFrequency));
            penaltyBO.setPenaltyName(dto.getPenaltyName());
            penaltyBO.setPeriodDuration(dto.getDuration());
            penaltyBO.setMinimumLimit(dto.getMin());
            penaltyBO.setMaximumLimit(dto.getMax());
            penaltyBO.setStatus(this.penaltyDao.findPenaltyStatusEntityByType(penaltyStatus));
            penaltyBO.setGlCode(this.generalLedgerDao.findGlCodeById(dto.getGlCode()));
            
            if (dto.isRatePenalty()) {
                RatePenaltyBO ratePenaltyBO = (RatePenaltyBO) penaltyBO;
                ratePenaltyBO.setRate(dto.getRate());
                ratePenaltyBO.setFormula(this.penaltyDao.findPenaltyFormulaEntityByType(penaltyFormula));
            } else {
                AmountPenaltyBO amountPenaltyBO = (AmountPenaltyBO) penaltyBO;
                amountPenaltyBO.setAmount(new Money(getCurrency(dto.getCurrencyId()), dto.getAmount()));
            }

            try {
                StaticHibernateUtil.startTransaction();
                this.penaltyDao.save(penaltyBO);
                StaticHibernateUtil.commitTransaction();
            } catch (Exception e) {
                StaticHibernateUtil.rollbackTransaction();
                throw new MifosRuntimeException(e);
            } finally {
                StaticHibernateUtil.closeSession();
            }
        } catch (ApplicationException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }
    
    @Override
    public PenaltyDto getPenalty(int penaltyId) {
        return this.penaltyDao.findPenalty(penaltyId).toDto();
    }
    
    @Override
    public List<PenaltyDto> getLoanPenalties() {
        return assemblePenaltyDtos(this.penaltyDao.findAllLoanPenalties());
    }

    @Override
    public List<PenaltyDto> getSavingPenalties() {
        return assemblePenaltyDtos(this.penaltyDao.findAllSavingPenalties());
    }
    
    private List<PenaltyDto> assemblePenaltyDtos(List<PenaltyBO> penaltyBOs) {
        List<PenaltyDto> penalties = new ArrayList<PenaltyDto>();
        for (PenaltyBO penalty : penaltyBOs) {
            penalties.add(penalty.toDto());
        }
        return penalties;
    }

    @Override
    public PenaltyParametersDto getPenaltyParameters() {
        try {
            List<GLCodeEntity> glCodes = generalLedgerDao.retreiveGlCodesBy(FinancialActionConstants.PENALTYPOSTING, FinancialConstants.CREDIT);

            List<PenaltyCategoryEntity> categories = this.penaltyDao.getPenaltiesCategories();
            List<PenaltyPeriodEntity> periods = this.penaltyDao.getPenaltiesPeriods();
            List<PenaltyFormulaEntity> formulas = this.penaltyDao.getPenaltiesFormulas();
            List<PenaltyFrequencyEntity> frequencies = this.penaltyDao.getPenaltiesFrequencies();
            List<PenaltyStatusEntity> statuses = this.penaltyDao.getPenaltiesStatuses();

            return new PenaltyParametersDto(listToMap(categories), listToMap(statuses), listToMap(periods),
                    listToMap(formulas), listToMap(frequencies), glCodesToMap(glCodes));
        } catch (FinancialException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }
    
    private Map<String, String> listToMap(List<? extends MasterDataEntity> masterDataEntityList) {
        Map<String, String> idNameMap = new HashMap<String, String>();

        for (MasterDataEntity masterDataEntity : masterDataEntityList) {
            idNameMap.put(Short.toString(masterDataEntity.getId()), masterDataEntity.getName());
        }
        
        return idNameMap;
    }
    
    private Map<String, String> glCodesToMap(List<GLCodeEntity> glCodes) {
        Map<String, String> idCodeMap = new HashMap<String, String>();
        
        for (GLCodeEntity glCode : glCodes) {
            idCodeMap.put(Short.toString(glCode.getGlcodeId()), glCode.getGlcode());
        }
        return idCodeMap;
    }
    
    private MifosCurrency getCurrency(Short currencyId) {
        MifosCurrency currency;
        
        if (currencyId == null) {
            currency = Money.getDefaultCurrency();
        } else {
            currency = AccountingRules.getCurrencyByCurrencyId(currencyId);
        }
        
        return currency;
    }

}
