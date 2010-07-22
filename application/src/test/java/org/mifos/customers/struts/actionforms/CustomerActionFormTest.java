/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.customers.struts.actionforms;

import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;
import org.mifos.application.util.helpers.Methods;
import org.mifos.customers.client.struts.actionforms.ClientCustActionForm;
import org.mifos.customers.client.util.helpers.ClientConstants;
import org.mifos.customers.group.struts.actionforms.GroupCustActionForm;
import org.mifos.platform.questionnaire.exceptions.ValidationException;
import org.mifos.platform.questionnaire.service.EventSource;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.questionnaire.service.SectionDetail;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.mifos.platform.questionnaire.QuestionnaireConstants.GENERIC_VALIDATION;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.MANDATORY_QUESTION_HAS_NO_ANSWER;

@RunWith(MockitoJUnitRunner.class)
public class CustomerActionFormTest {

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private ActionErrors actionErrors;

    @Mock
    private QuestionnaireServiceFacade questionnaireServiceFacade;

    private ClientCustActionForm clientCustActionForm;

    @Before
    public void setUp() {
        clientCustActionForm = new ClientCustActionForm();
    }

    @Test
    public void testNullInCustomerId() throws Exception {
        CustomerActionForm form = new GroupCustActionForm();
        form.setCustomerId(null);
        Assert.assertNull(form.getCustomerId());

        try {
            form.getCustomerIdAsInt();
            Assert.fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void testValidateForQuestionGroupResponses() {
        List<QuestionGroupDto> questionGroupDtos = new LinkedList<QuestionGroupDto>();
        List<QuestionDetail> questionDetails = Arrays.asList(new QuestionDetail(12, "Question 1", "Question 1", QuestionType.FREETEXT));
        List<SectionDetail> sectionDetails = Arrays.asList(getSectionDetailWithQuestions("Sec1", questionDetails, null, true));
        QuestionGroupDto questionGroupDto = new QuestionGroupDto(getQuestionGroupDetail("QG1", "Create", "Client", sectionDetails));
        questionGroupDtos.add(questionGroupDto);
        clientCustActionForm.setQuestionGroupDtos(questionGroupDtos);
        ValidationException validationException = new ValidationException(GENERIC_VALIDATION);
        validationException.addChildException(new ValidationException(MANDATORY_QUESTION_HAS_NO_ANSWER, sectionDetails.get(0).getQuestionDetail(0)));
        Mockito.doThrow(validationException).when(questionnaireServiceFacade).validateResponses(Mockito.anyList());
        clientCustActionForm.validateForQuestionGroupResponses(Methods.next.toString(), actionErrors, questionnaireServiceFacade);
        Mockito.verify(actionErrors, Mockito.times(1)).add(Mockito.eq(ClientConstants.ERROR_REQUIRED),
                Mockito.argThat(new ActionMessageMatcher(ClientConstants.ERROR_REQUIRED, "Question 1")));
    }

    private QuestionGroupDetail getQuestionGroupDetail(String title, String event, String source, List<SectionDetail> sections) {
        return new QuestionGroupDetail(1, title, new EventSource(event, source, null), sections, true);
    }

    private SectionDetail getSectionDetailWithQuestions(String name, List<QuestionDetail> questionDetails, String value, boolean mandatory) {
        SectionDetail sectionDetail = new SectionDetail();
        sectionDetail.setName(name);
        List<SectionQuestionDetail> sectionQuestionDetails = new ArrayList<SectionQuestionDetail>();
        for (QuestionDetail questionDetail : questionDetails) {
            SectionQuestionDetail sectionQuestionDetail = new SectionQuestionDetail(questionDetail, mandatory);
            sectionQuestionDetail.setValue(value);
            sectionQuestionDetails.add(sectionQuestionDetail);
        }
        sectionDetail.setQuestionDetails(sectionQuestionDetails);
        return sectionDetail;
    }

    private class ActionMessageMatcher extends TypeSafeMatcher<ActionMessage> {
        private String errorCode;
        private String questionTitle;

        public ActionMessageMatcher(String errorCode, String questionTitle) {
            this.errorCode = errorCode;
            this.questionTitle = questionTitle;
        }

        @Override
        public boolean matchesSafely(ActionMessage actionMessage) {
            return StringUtils.equalsIgnoreCase(errorCode, actionMessage.getKey()) &&
                    StringUtils.equalsIgnoreCase(questionTitle, String.valueOf(actionMessage.getValues()[0]));
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("ActionMessage doesn't match");
        }
    }
}
