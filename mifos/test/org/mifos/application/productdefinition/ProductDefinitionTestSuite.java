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

