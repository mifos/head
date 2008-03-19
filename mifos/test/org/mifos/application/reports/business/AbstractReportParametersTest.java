package org.mifos.application.reports.business;


import static org.easymock.classextension.EasyMock.createMock;

import org.mifos.application.reports.business.validator.Errors;
import org.mifos.framework.MifosTestCase;

public abstract class AbstractReportParametersTest extends MifosTestCase {

	public static final String VALID_ID = "0";	
	
	Errors errorsMock;
	protected AbstractReportParameterForm reportParams;


	public AbstractReportParametersTest() {
		super();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		errorsMock = createMock(Errors.class);
	}

}
