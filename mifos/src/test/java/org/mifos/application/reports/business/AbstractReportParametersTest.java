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
package org.mifos.application.reports.business;


import static org.easymock.classextension.EasyMock.createMock;

import org.mifos.application.reports.business.validator.Errors;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

public abstract class AbstractReportParametersTest extends MifosTestCase {

	public static final String VALID_ID = "0";	
	
	Errors errorsMock;
	protected AbstractReportParameterForm reportParams;


	public AbstractReportParametersTest() throws SystemException, ApplicationException {
		super();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		errorsMock = createMock(Errors.class);
	}

}
