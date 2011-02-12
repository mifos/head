/*
 * Copyright Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */

package org.mifos.platform.questionnaire.domain.ppi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/test-questionnaire-dbContext.xml", "/test-questionnaire-persistenceContext.xml", "/META-INF/spring/QuestionnaireContext.xml"})
public class PPISurveyLocatorIntegrationTest {

    @Autowired
    private PPISurveyLocator ppiSurveyLocator;
    private static final String PPISURVEY_INDIA_XML = "PPISurveyINDIA.xml";

    @Test
    public void testGetAllPPISurveyFiles() throws IOException {
        List<String> ppiSurveyFiles = ppiSurveyLocator.getAllPPISurveyFiles();
        assertThat(ppiSurveyFiles, is(notNullValue()));
        assertThat(ppiSurveyFiles.size(), is(1));
        assertThat(ppiSurveyFiles.get(0), is(PPISURVEY_INDIA_XML));
    }

    @Test
    public void testGetPPIXmlForCountry() throws IOException {
        String ppiUploadFileForCountry = ppiSurveyLocator.getPPIUploadFileForCountry("INDIA");
        assertThat(ppiUploadFileForCountry, is(notNullValue()));
        assertThat(ppiUploadFileForCountry.contains(PPISURVEY_INDIA_XML), is(true));
    }
}
