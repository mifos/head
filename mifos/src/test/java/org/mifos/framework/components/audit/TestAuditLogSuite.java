package org.mifos.framework.components.audit;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.framework.components.audit.business.service.TestAuditBusinessService;
import org.mifos.framework.components.audit.persistence.TestAuditPersistence;
import org.mifos.framework.components.audit.util.TestAuditInterceptor;

public class TestAuditLogSuite extends TestSuite {

	public TestAuditLogSuite() {
		super();
	}

	public static Test suite()throws Exception
	{
		TestSuite testSuite = new TestAuditLogSuite();
		testSuite.addTestSuite(TestAuditBusinessService.class);
		testSuite.addTestSuite(TestAuditPersistence.class);
		testSuite.addTestSuite(TestAuditInterceptor.class);
		return testSuite;
		
	}
}
