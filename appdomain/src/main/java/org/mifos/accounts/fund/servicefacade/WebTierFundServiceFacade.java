/*
 * Copyright Grameen Foundation USA
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

package org.mifos.accounts.fund.servicefacade;

import java.util.ArrayList;
import java.util.List;

import org.mifos.accounts.fund.business.FundBO;
import org.mifos.accounts.fund.persistence.FundDao;
import org.mifos.accounts.fund.util.helpers.FundConstants;
import org.mifos.application.master.business.FundCodeEntity;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.service.BusinessRuleException;
import org.springframework.beans.factory.annotation.Autowired;

public class WebTierFundServiceFacade implements FundServiceFacade {

    private final FundDao fundDao;

    @Autowired
    public WebTierFundServiceFacade(FundDao fundDao) {
        this.fundDao = fundDao;
    }

    @Override
    public FundDto getFund(Short fundId) {
        return translateBOToDto(this.fundDao.findById(fundId));
    }

    @Override
    public List<FundDto> getFunds() {
        return assembleFundDtos(this.fundDao.findAllFunds());
    }

    @Override
    public List<FundCodeDto> getFundCodes() {
        return assembleFundCodeDtos(this.fundDao.findAllFundCodes());
    }

    @Override
    public void updateFund(FundDto fundDto) {
        Short fundId = Short.valueOf(fundDto.getId());
        FundBO fundBO = this.fundDao.findById(fundId);
        try {
            StaticHibernateUtil.startTransaction();
            this.fundDao.update(fundBO, fundDto.getName());
            StaticHibernateUtil.commitTransaction();
        } catch (BusinessRuleException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new BusinessRuleException(e.getMessageKey(), e);
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e.getMessage(), e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    @Override
    public void createFund(FundDto fundDto) {
        List<FundCodeEntity> fundCodeEntities = fundDao.findAllFundCodes();
        Short fundCodeId = Short.valueOf(fundDto.getCode().getId());
        FundCodeEntity fundCode = null;
        for (FundCodeEntity fundCodeEntity : fundCodeEntities) {
            if (fundCodeEntity.getFundCodeId().equals(fundCodeId)) {
                fundCode = fundCodeEntity;
                break;
            }
        }
        FundBO fundBO = new FundBO(fundCode, fundDto.getName());
        if (this.fundDao.countOfFundByName(fundDto.getName().trim()) > 0) {
            throw new org.mifos.service.BusinessRuleException(FundConstants.DUPLICATE_FUNDNAME_EXCEPTION);
        }


        try {
            StaticHibernateUtil.startTransaction();
            this.fundDao.save(fundBO);
            StaticHibernateUtil.commitTransaction();
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e.getMessage(), e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    private List<FundCodeDto> assembleFundCodeDtos(List<FundCodeEntity> fundCodeBOs) {
        List<FundCodeDto> fundCodes = new ArrayList<FundCodeDto>();
        for (FundCodeEntity fundCode : fundCodeBOs) {
            fundCodes.add(translateBOToDto(fundCode));
        }
        return fundCodes;
    }

    private FundCodeDto translateBOToDto(FundCodeEntity fundCode) {
        FundCodeDto fundCodeDto = new FundCodeDto();
        fundCodeDto.setId(Short.toString(fundCode.getFundCodeId()));
        fundCodeDto.setValue(fundCode.getFundCodeValue());
        return fundCodeDto;
    }

    private List<FundDto> assembleFundDtos(List<FundBO> fundBOs) {
        List<FundDto> funds = new ArrayList<FundDto>();
        for (FundBO fund : fundBOs) {
            funds.add(translateBOToDto(fund));
        }
        return funds;
    }

    private FundDto translateBOToDto(FundBO fund) {
        FundDto fundDto = new FundDto();
        fundDto.setId(Short.toString(fund.getFundId()));
        fundDto.setCode(translateBOToDto(fund.getFundCode()));
        fundDto.setName(fund.getFundName());
        return fundDto;
    }
}
