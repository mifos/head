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

import org.mifos.application.productdefinition.business.LoanOfferingBOTest;
import org.mifos.application.productdefinition.business.PrdCategoryStatusEntityTest;
import org.mifos.application.productdefinition.business.TestProductCategoryBO;
import org.mifos.application.productdefinition.business.TestSavingsOfferingBO;
import org.mifos.application.productdefinition.business.service.LoanPrdBusinessServiceTest;
import org.mifos.application.productdefinition.business.service.SavingsPrdBusinessServiceTest;
import org.mifos.application.productdefinition.business.service.TestProductCategoryBusinessService;
import org.mifos.application.productdefinition.persistence.PrdOfferingPersistenceTest;
import org.mifos.application.productdefinition.persistence.SavingsPrdPersistenceTest;
import org.mifos.application.productdefinition.persistence.TestProductCategoryPersistence;
import org.mifos.application.productdefinition.struts.action.LoanPrdActionTest;
import org.mifos.application.productdefinition.struts.action.SavingsPrdActionTest;
import org.mifos.application.productdefinition.struts.action.TestPrdCategoryAction;
import org.mifos.application.productdefinition.business.AddInterestCalcRuleTest;

public class ProductDefinitionTestSuite extends TestSuite {

	public ProductDefinitionTestSuite() throws Exception {
		super();
	}

	public static Test suite() throws Exception {
		TestSuite testSuite = new ProductDefinitionTestSuite();
		testSuite.addTestSuite(PrdOfferingPersistenceTest.class);
		testSuite.addTestSuite(TestSavingsOfferingBO.class);
		testSuite.addTestSuite(PrdCategoryStatusEntityTest.class);
		testSuite.addTestSuite(TestProductCategoryBO.class);
		testSuite.addTestSuite(TestProductCategoryBusinessService.class);
		testSuite.addTestSuite(TestProductCategoryPersistence.class);
		testSuite.addTestSuite(SavingsPrdPersistenceTest.class);
		testSuite.addTestSuite(TestPrdCategoryAction.class);
		testSuite.addTestSuite(SavingsPrdBusinessServiceTest.class);
		testSuite.addTestSuite(SavingsPrdActionTest.class);
		testSuite.addTestSuite(LoanOfferingBOTest.class);
		testSuite.addTestSuite(LoanPrdBusinessServiceTest.class);
		testSuite.addTestSuite(LoanPrdActionTest.class);
		testSuite.addTest(AddInterestCalcRuleTest.testSuite());
		return testSuite;
	}

}

