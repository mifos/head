/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.platform.questionnaire.validators;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.service.EventSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/test-questionnaire-dbContext.xml", "/test-questionnaire-persistenceContext.xml", "/META-INF/spring/QuestionnaireContext.xml"})
@TransactionConfiguration(transactionManager = "platformTransactionManager", defaultRollback = true)
public class QuestionnaireValidatorIntegrationTest {

    @Autowired
    private QuestionnaireValidator questionnaireValidator;

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldCheckForInValidEventSource() {
        EventSource eventSource = new EventSource("Disburse", "Client", "Disburse Client");
        try {
            questionnaireValidator.validateForEventSource(eventSource);
            fail("Should have raised a validation error for invalid event");
        } catch (SystemException e) {
            assertThat(e.getKey(), is(QuestionnaireConstants.INVALID_EVENT_SOURCE));
        }
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldCheckForValidEventSource() {
        EventSource eventSource = new EventSource("Create", "Client", "Create Client");
        try {
            questionnaireValidator.validateForEventSource(eventSource);
        } catch (SystemException e) {
            fail("Should not have raised a validation error for this event");
        }
    }
}
