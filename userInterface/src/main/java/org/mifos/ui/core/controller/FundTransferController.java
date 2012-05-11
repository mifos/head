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
package org.mifos.ui.core.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.application.servicefacade.ClientServiceFacade;
import org.mifos.application.servicefacade.SavingsServiceFacade;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.CustomerSearchDto;
import org.mifos.dto.domain.CustomerSearchResultDto;
import org.mifos.dto.domain.FundTransferDto;
import org.mifos.dto.domain.SavingsDetailDto;
import org.mifos.dto.screen.CustomerSearchResultsDto;
import org.mifos.dto.screen.MessageDto;
import org.mifos.dto.screen.SearchDetailsDto;
import org.mifos.service.BusinessRuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class FundTransferController {

    private static final Short ACCOUNT_ACTIVE = 16;
    private static final Short ACCOUNT_INACTIVE = 18;

    private SavingsServiceFacade savingsServiceFacade;
    private ClientServiceFacade clientServiceFacade;

    @Autowired
    public void setSavingsServiceFacade(SavingsServiceFacade savingsServiceFacade) {
        this.savingsServiceFacade = savingsServiceFacade;
    }

    @Autowired
    public void setClientServiceFacade(ClientServiceFacade clientServiceFacade) {
        this.clientServiceFacade = clientServiceFacade;
    }

    // All methods are called by Spring Webflow

    public CustomerSearchResultsDto searchCustomers(CustomerSearchFormBean formBean) {
        Integer searchCap = 1000;

        CustomerSearchDto customerSearchDto = new CustomerSearchDto(formBean.getSearchString(), Integer.valueOf(1),
                searchCap);
        List<CustomerSearchResultDto> pagedDetails = this.savingsServiceFacade
                .retrieveCustomersThatQualifyForTransfer(customerSearchDto);

        SearchDetailsDto searchDetails = new SearchDetailsDto(pagedDetails.size(), 1, 1, searchCap);
        return new CustomerSearchResultsDto(searchDetails, pagedDetails);
    }

    public List<SavingsDetailDto> retrieveSavingsAccounts(Integer targetCustomerId, String sourceAccountGlobalNum) {
        List<SavingsDetailDto> savingsAccs = this.clientServiceFacade.retrieveSavingsInUseForClient(targetCustomerId);
        if (savingsAccs == null) {
            savingsAccs = new ArrayList<SavingsDetailDto>();
        } else {
            for (Iterator<SavingsDetailDto> iter = savingsAccs.iterator(); iter.hasNext();) {
                SavingsDetailDto savingsAccDetail = iter.next();
                if (!isAccountStateOkForTransfer(savingsAccDetail.getAccountStateId())
                        || savingsAccDetail.getGlobalAccountNum().equals(sourceAccountGlobalNum)) {
                    iter.remove();
                }
            }
        }
        return savingsAccs;
    }

    public void populateFormBean(FundTransferFormBean formBean, String sourceAccGlobalNum, String targetAccGlobalNum) {
        if (!formBean.isAfterInit()) {
            SavingsDetailDto sourceAcc = this.savingsServiceFacade.retrieveSavingsDetail(sourceAccGlobalNum);
            SavingsDetailDto targetAcc = this.savingsServiceFacade.retrieveSavingsDetail(targetAccGlobalNum);

            formBean.setSourceBalance(new BigDecimal(sourceAcc.getSavingsBalance()));
            formBean.setTargetBalance(new BigDecimal(targetAcc.getSavingsBalance()));
            formBean.setSourceGlobalAccNum(sourceAccGlobalNum);
            formBean.setTargetGlobalAccNum(targetAccGlobalNum);

            formBean.setTrxnDate(new LocalDate());
            formBean.setAmount(null);
            formBean.setReceiptId(null);
            formBean.setReceiptDate(null);

            formBean.setAfterInit(true);
        }
    }

    public void resetFormBean(FundTransferFormBean formBean) {
        formBean.setAfterInit(false);
    }

    public MessageDto applyTransfer(FundTransferFormBean formBean) {
        MessageDto error = null;
        FundTransferDto fundTransferDto = new FundTransferDto(formBean.getSourceGlobalAccNum(),
                formBean.getTargetGlobalAccNum(), formBean.getAmount(), formBean.getTrxnDate(),
                formBean.getReceiptDate(), formBean.getReceiptId());
        try {
            this.savingsServiceFacade.fundTransfer(fundTransferDto);
        } catch (BusinessRuleException ex) {
            error = new MessageDto(ex.getMessageKey(), ex.getMessageValues());
        }
        return error;
    }

    public CustomerDto retrieveCustomerDetails(Integer customerId) {
        return this.savingsServiceFacade.retreieveCustomerDetails(customerId);
    }

    public SavingsDetailDto retrieveSavingsAccDetails(String savingsGlobalNum) {
        return this.savingsServiceFacade.retrieveSavingsDetail(savingsGlobalNum);
    }

    private boolean isAccountStateOkForTransfer(Short accountStateId) {
        return accountStateId.equals(ACCOUNT_ACTIVE) || accountStateId.equals(ACCOUNT_INACTIVE);
    }
}
