package org.mifos.application.productsmix;

import org.mifos.application.productsmix.business.ProductMixBOTest;
import org.mifos.application.productsmix.business.service.ProductMixBusinessServiceTest;
import org.mifos.application.productsmix.persistence.ProductMixPersistenceTest;
import org.mifos.application.productsmix.struts.action.ProductMixActionTest;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class ProductMixTestSuite extends TestSuite{
	public ProductMixTestSuite() {
		super();
	}

	public static void main(String[] args) {
		try {
			Test testSuite = suite();
			TestRunner.run(testSuite);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static Test suite() throws Exception {
		TestSuite testSuite = new ProductMixTestSuite();
		testSuite.addTestSuite(ProductMixBOTest.class);
		testSuite.addTestSuite(ProductMixBusinessServiceTest.class);
		testSuite.addTestSuite(ProductMixPersistenceTest.class);
		testSuite.addTestSuite(ProductMixActionTest.class);
		
		return testSuite;
	}
}
