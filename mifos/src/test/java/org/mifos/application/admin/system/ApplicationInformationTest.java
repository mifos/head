/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
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
package org.mifos.application.admin.system;

import static org.junit.Assert.assertEquals;
import junit.framework.JUnit4TestAdapter;

import org.junit.Before;
import org.junit.Test;
import org.mifos.core.service.ApplicationInformationDto;
import org.mifos.core.service.ApplicationInformationService;

public class ApplicationInformationTest {
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(ApplicationInformationTest.class);
	}

    private static final String BUILD_ID = "ID_1";
    private static final String BUILD_NUMBER = "BUILD_1";
    private static final String SVN_REVISION = "123456";
    
    private ApplicationInformationService applicationInfoService;

    @Before
    public void setUp() throws Exception {
        applicationInfoService = new StandardApplicationInformationService();
        ApplicationInformationDto applicationInformationDto = new ApplicationInformationDto();
        applicationInformationDto.setBuildId(BUILD_ID);
        applicationInformationDto.setBuildTag(BUILD_NUMBER);
        applicationInformationDto.setSvnRevision(SVN_REVISION);
        applicationInfoService.setApplicationInformation(applicationInformationDto);
    }
    
    @Test
    public void testGetApplicationInformation() {
        assertEquals(applicationInfoService.getApplicationInformation().getBuildId(), BUILD_ID);
        assertEquals(applicationInfoService.getApplicationInformation().getBuildTag(), BUILD_NUMBER);
        assertEquals(applicationInfoService.getApplicationInformation().getSvnRevision(), SVN_REVISION);
    }

}
