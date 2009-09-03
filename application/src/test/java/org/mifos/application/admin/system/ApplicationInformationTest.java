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

package org.mifos.application.admin.system;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.mifos.core.service.ApplicationInformationDto;
import org.mifos.core.service.ApplicationInformationService;

public class ApplicationInformationTest extends TestCase {

    private static final String BUILD_ID = "ID_1";
    private static final String BUILD_NUMBER = "BUILD_1";
    private static final String SVN_REVISION = "123456";

    private ApplicationInformationService applicationInfoService;

    public void setUp() throws Exception {
        applicationInfoService = new StandardApplicationInformationService();
        ApplicationInformationDto applicationInformationDto = new ApplicationInformationDto();
        applicationInformationDto.setBuildId(BUILD_ID);
        applicationInformationDto.setBuildTag(BUILD_NUMBER);
        applicationInformationDto.setSvnRevision(SVN_REVISION);
        applicationInfoService.setApplicationInformation(applicationInformationDto);
    }

    public void testGetApplicationInformation() {
       Assert.assertEquals(applicationInfoService.getApplicationInformation().getBuildId(), BUILD_ID);
       Assert.assertEquals(applicationInfoService.getApplicationInformation().getBuildTag(), BUILD_NUMBER);
       Assert.assertEquals(applicationInfoService.getApplicationInformation().getSvnRevision(), SVN_REVISION);
    }

}
