/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.accounts.loan.struts.action.validate;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.util.Arrays;
import java.util.Collections;

import junit.framework.Assert;

import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productsmix.business.service.ProductMixBusinessService;
import org.mifos.config.business.service.ConfigurationBusinessService;
import org.mifos.customers.business.CustomerBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ServiceException;

public class ProductMixValidatorIntegrationTest extends MifosIntegrationTestCase {

    public ProductMixValidatorIntegrationTest() throws Exception {
        super();
    }

    private ConfigurationBusinessService configServiceMock;
    private ProductMixBusinessService productMixBusinessServiceMock;
    private LoanBO loanMock;
    private CustomerBO customerMock;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        configServiceMock = createMock(ConfigurationBusinessService.class);
        productMixBusinessServiceMock = createMock(ProductMixBusinessService.class);
        loanMock = createMock(LoanBO.class);
        customerMock = createMock(CustomerBO.class);
    }

    public void testShouldDetectProductMixConflicts() throws Exception {
        short PRD_OFFERING_ID_TWO = (short) 2;

        LoanBO loanMock1 = createMock(LoanBO.class);
        LoanBO loanMock2 = createMock(LoanBO.class);
        LoanOfferingBO loanOfferingMock1 = createMock(LoanOfferingBO.class);
        LoanOfferingBO loanOfferingMock2 = createMock(LoanOfferingBO.class);
        expect(loanMock2.getLoanOffering()).andReturn(loanOfferingMock2);
        expect(loanMock1.getLoanOffering()).andReturn(loanOfferingMock1);
        expect(loanOfferingMock2.getPrdOfferingId()).andReturn(PRD_OFFERING_ID_TWO);

        expect(productMixBusinessServiceMock.canProductsCoExist(loanOfferingMock2, loanOfferingMock1)).andReturn(false);

        replay(loanMock1, loanMock2, loanOfferingMock1, loanOfferingMock2, productMixBusinessServiceMock);
        try {
            new ProductMixValidator(configServiceMock, productMixBusinessServiceMock) {
                @Override
                void handleConflict(LoanBO newloan, LoanBO loan) throws AccountException {
                    throw new AccountException("Some exception code");
                }
            }.validateProductMix(loanMock2, Arrays.asList(loanMock1));

            Assert.fail("Product mix conflict not detected");
            verify(loanMock1, loanMock2, loanOfferingMock1, loanOfferingMock2, productMixBusinessServiceMock);
        } catch (AccountException e) {

        }
    }

    public void testShouldAllowValidProductMix() throws Exception {
        LoanBO loanMock1 = createMock(LoanBO.class);
        LoanBO loanMock2 = createMock(LoanBO.class);
        LoanOfferingBO loanOfferingMock1 = createMock(LoanOfferingBO.class);
        LoanOfferingBO loanOfferingMock2 = createMock(LoanOfferingBO.class);
        expect(loanMock1.getLoanOffering()).andReturn(loanOfferingMock1);
        expect(loanMock2.getLoanOffering()).andReturn(loanOfferingMock2);
        expect(productMixBusinessServiceMock.canProductsCoExist(loanOfferingMock2, loanOfferingMock1)).andReturn(true);
        replay(loanMock1, loanMock2, loanOfferingMock1, loanOfferingMock2, productMixBusinessServiceMock);
        try {
            new ProductMixValidator(configServiceMock, productMixBusinessServiceMock) {
                @Override
                void handleConflict(LoanBO newloan, LoanBO loan) throws AccountException {
                    throw new AccountException("Some exception code");
                }
            }.validateProductMix(loanMock2, Arrays.asList(loanMock1));
            verify(loanMock1, loanMock2, loanOfferingMock1, loanOfferingMock2, productMixBusinessServiceMock);
        } catch (AccountException e) {
            Assert.fail("Invalid Product mix conflict detected");
        }
    }

    public void testShouldGetLoansIfCustomerIsCoSigningClientInGlimMode() throws Exception {
        expect(configServiceMock.isGlimEnabled()).andReturn(true);
        replay(loanMock, customerMock, configServiceMock);
       Assert.assertEquals(Arrays.asList(loanMock), getProductMixValidatorWithCosigningClientTrue()
                .getLoansToCheckAgainstProductMix(customerMock, Arrays.asList(loanMock)));
        verify(loanMock, customerMock, configServiceMock);
    }

    public void testShouldReturnEmptyListIfCustomerIsNotACoSigningClientInGlimMode() throws Exception {
        expect(configServiceMock.isGlimEnabled()).andReturn(true);
        replay(loanMock, customerMock, configServiceMock);
       Assert.assertEquals(Collections.EMPTY_LIST, getProductMixValidatorWithCosigningClientFalse()
                .getLoansToCheckAgainstProductMix(customerMock, Arrays.asList(loanMock)));
        verify(loanMock, customerMock, configServiceMock);
    }

    public void testShouldReturnAllLoansIfNotGlim() throws Exception {
        expect(configServiceMock.isGlimEnabled()).andReturn(false);
        replay(loanMock, customerMock, configServiceMock);
       Assert.assertEquals(Arrays.asList(loanMock), new ProductMixValidator(configServiceMock,
                new ProductMixBusinessService())
                .getLoansToCheckAgainstProductMix(customerMock, Arrays.asList(loanMock)));
        verify(loanMock, customerMock, configServiceMock);
    }

    private ProductMixValidator getProductMixValidatorWithCosigningClientTrue() {
        return new ProductMixValidator(configServiceMock, new ProductMixBusinessService()) {
            @Override
            boolean isCustomerACoSigningClient(CustomerBO customer, LoanBO loan) throws ServiceException {
                return true;
            }
        };
    }

    private ProductMixValidator getProductMixValidatorWithCosigningClientFalse() {
        return new ProductMixValidator(configServiceMock, new ProductMixBusinessService()) {
            @Override
            boolean isCustomerACoSigningClient(CustomerBO customer, LoanBO loan) throws ServiceException {
                return false;
            }
        };
    }
}
