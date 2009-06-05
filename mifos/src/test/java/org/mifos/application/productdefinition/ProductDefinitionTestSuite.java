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

package org.mifos.application.productdefinition;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.productdefinition.business.LoanOfferingBOIntegrationTest;
import org.mifos.application.productdefinition.business.PrdCategoryStatusEntityIntegrationTest;
import org.mifos.application.productdefinition.business.ProductCategoryBOIntegrationTest;
import org.mifos.application.productdefinition.business.SavingsOfferingBOIntegrationTest;
import org.mifos.application.productdefinition.business.service.LoanPrdBusinessServiceIntegrationTest;
import org.mifos.application.productdefinition.business.service.SavingsPrdBusinessServiceIntegrationTest;
import org.mifos.application.productdefinition.business.service.ProductCategoryBusinessServiceIntegrationTest;
import org.mifos.application.productdefinition.persistence.PrdOfferingPersistenceIntegrationTest;
import org.mifos.application.productdefinition.persistence.SavingsPrdPersistenceIntegrationTest;
import org.mifos.application.productdefinition.persistence.ProductCategoryPersistenceIntegrationTest;
import org.mifos.application.productdefinition.struts.action.LoanPrdActionTest;
import org.mifos.application.productdefinition.struts.action.SavingsPrdActionTest;
import org.mifos.application.productdefinition.struts.action.PrdCategoryActionTest;
import org.mifos.application.productdefinition.business.AddInterestCalcRuleTest;

public class ProductDefinitionTestSuite extends TestSuite {

    public ProductDefinitionTestSuite() throws Exception {
        super();
    }

    public static Test suite() throws Exception {
        TestSuite testSuite = new ProductDefinitionTestSuite();
        testSuite.addTestSuite(PrdOfferingPersistenceIntegrationTest.class);
        testSuite.addTestSuite(SavingsOfferingBOIntegrationTest.class);
        testSuite.addTestSuite(PrdCategoryStatusEntityIntegrationTest.class);
        testSuite.addTestSuite(ProductCategoryBOIntegrationTest.class);
        testSuite.addTestSuite(ProductCategoryBusinessServiceIntegrationTest.class);
        testSuite.addTestSuite(ProductCategoryPersistenceIntegrationTest.class);
        testSuite.addTestSuite(SavingsPrdPersistenceIntegrationTest.class);
        testSuite.addTestSuite(PrdCategoryActionTest.class);
        testSuite.addTestSuite(SavingsPrdBusinessServiceIntegrationTest.class);
        testSuite.addTestSuite(SavingsPrdActionTest.class);
        testSuite.addTestSuite(LoanOfferingBOIntegrationTest.class);
        testSuite.addTestSuite(LoanPrdBusinessServiceIntegrationTest.class);
        testSuite.addTestSuite(LoanPrdActionTest.class);
        testSuite.addTest(AddInterestCalcRuleTest.testSuite());
        return testSuite;
    }

}
