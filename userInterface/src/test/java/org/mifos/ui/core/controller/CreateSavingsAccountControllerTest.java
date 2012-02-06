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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mifos.application.servicefacade.SavingsServiceFacade;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.PrdOfferingDto;
import org.mifos.dto.domain.ProductDetailsDto;
import org.mifos.dto.domain.SavingsProductDto;
import org.mifos.dto.screen.ListElement;
import org.mifos.dto.screen.SavingsProductReferenceDto;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;

public class CreateSavingsAccountControllerTest {

    private CreateSavingsAccountController controller;
    private CreateSavingsAccountFormBean formBean;
    private SavingsServiceFacade savingsServiceFacade;
    private PrdOfferingDto offering;
    private List<PrdOfferingDto> offerings;
    private CustomerDto customer;

    @Before
    public void setUp() {
        controller = new CreateSavingsAccountController();
        QuestionnaireServiceFacade questionnaireServiceFacade = mock(QuestionnaireServiceFacade.class);
        controller.setQuestionnaireServiceFacade(questionnaireServiceFacade);
        savingsServiceFacade = mock(SavingsServiceFacade.class);
        controller.setSavingsServiceFacade(savingsServiceFacade);
        formBean = mock(CreateSavingsAccountFormBean.class);

        offering = new PrdOfferingDto((short) 1, "Offering 1", "10001");
        offerings = new ArrayList<PrdOfferingDto>(1);
        offerings.add(offering);

        customer = new CustomerDto();
        customer.setCustomerId(1);
        when(formBean.getCustomer()).thenReturn(customer);
    }

    @Test
    public void testCustomerSelected() {
        when(savingsServiceFacade.retreieveCustomerDetails(customer.getCustomerId())).thenReturn(customer);
        
        controller.customerSelected(customer.getCustomerId(), formBean);
        
        verify(formBean).setCustomer(customer);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testGetProductOfferings() {
        when(savingsServiceFacade.retrieveApplicableSavingsProductsForCustomer(any(Integer.class))).thenReturn(offerings);
        
        controller.getProductOfferings(formBean);
        
        // verify correct interaction with formBean
        verify(formBean).setProductOfferings(offerings);
        Map<String, String> expectedOptions = new HashMap<String, String>();
        expectedOptions.put(offering.getPrdOfferingId().toString(), offering.getPrdOfferingName());
        verify(formBean).setProductOfferingOptions(any(Map.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testLoadProduct() {
        Integer productId = 1;
        List<ListElement> interestCalculationOptions  = new ArrayList<ListElement>();
        boolean groupSavingsAccount = false;
        Integer depositType = 1;
        Integer groupSavingsType = 1;
        Double amountForDeposit = 10.0;
        Double maxWithdrawal = 100.0;
        BigDecimal interestRate = new BigDecimal("5.0");
        Integer interestCalculationType = 1;
        Integer interestCalculationFrequency = 1;
        Integer interestCalculationFrequencyPeriod = 1;
        Integer interestPostingMonthlyFrequency = 1;
        BigDecimal minBalanceForInterestCalculation = new BigDecimal("1.0");
        Integer depositGlCode = 1;
        Integer interestGlCode = 1;
        ProductDetailsDto productsDetailDto = null;
        SavingsProductDto savingsProductDetails = new SavingsProductDto(productsDetailDto, groupSavingsAccount, 
                depositType, groupSavingsType, amountForDeposit, maxWithdrawal, interestRate, interestCalculationType, 
                interestCalculationFrequency, interestCalculationFrequencyPeriod, interestPostingMonthlyFrequency, 
                minBalanceForInterestCalculation, depositGlCode, interestGlCode);
        SavingsProductReferenceDto savingsProductReference = new SavingsProductReferenceDto(
                interestCalculationOptions, savingsProductDetails, true);
        
        when(savingsServiceFacade.retrieveSavingsProductReferenceData(productId)).thenReturn(savingsProductReference);
        
        controller.loadProduct(productId, formBean);
        
        verify(formBean).setProductId(productId);
        verify(formBean).setProduct(savingsProductReference);
        verify(formBean).setMandatoryDepositAmount(any(String.class));
        verify(formBean).setSavingsTypes(any(Map.class));
        verify(formBean).setRecurrenceTypes(any(Map.class));
        verify(formBean).setRecurrenceFrequencies(any(Map.class));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testLoadQuestionGroupsFormBeanInvoked() {
        controller.loadQuestionGroups(formBean);
        verify(formBean).setQuestionGroups(any(List.class));
    }
}
