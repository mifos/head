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

package org.mifos.platform.questionnaire.ui.controller;

import org.apache.commons.lang.StringUtils;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.exceptions.ValidationException;
import org.mifos.platform.questionnaire.service.EventSource;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetails;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mifos.platform.questionnaire.ui.model.QuestionGroupForm;
import org.mifos.platform.questionnaire.ui.model.SectionDetailForm;
import org.mifos.platform.util.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@SuppressWarnings("PMD")
public class QuestionGroupController extends QuestionnaireController {

    @SuppressWarnings({"UnusedDeclaration"})
    public QuestionGroupController() {
        super();
    }

    public QuestionGroupController(QuestionnaireServiceFacade questionnaireServiceFacade) {
        super(questionnaireServiceFacade);
    }

    @RequestMapping("/viewQuestionGroups.ftl")
    public String getAllQuestionGroups(ModelMap model, HttpServletRequest request) {
        model.addAttribute("questionGroups", questionnaireServiceFacade.getAllQuestionGroups());
        return "viewQuestionGroups";
    }

    @RequestMapping("/viewQuestionGroupDetail.ftl")
    public String getQuestionGroup(ModelMap model, HttpServletRequest httpServletRequest) {
        String questionGroupId = httpServletRequest.getParameter("questionGroupId");
        try {
            if (isInvalidNumber(questionGroupId)) {
                model.addAttribute("error_message_code", QuestionnaireConstants.INVALID_QUESTION_GROUP_ID);
            } else {
                QuestionGroupDetail questionGroupDetail = questionnaireServiceFacade.getQuestionGroupDetail(Integer.valueOf(questionGroupId));
                QuestionGroupForm questionGroupForm = new QuestionGroupForm(questionGroupDetail);
                model.addAttribute("questionGroupDetail", questionGroupForm);
                model.addAttribute("eventSources", getAllQgEventSources());
            }
        } catch (SystemException e) {
            //TODO: move mifosLogManager to common after dependency resolution
            //MifosLogManager.getLogger(LoggerConstants.ROOTLOGGER).error(e.getMessage(), e);
            model.addAttribute("error_message_code", QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND);
        }
        return "viewQuestionGroupDetail";
    }

    public String defineQuestionGroup(QuestionGroupForm questionGroupForm, RequestContext requestContext) {
        String result = "success";
        if (!questionGroupHasErrors(questionGroupForm, requestContext)) {
            try {
                questionnaireServiceFacade.createQuestionGroup(questionGroupForm.getQuestionGroupDetail());
            } catch (SystemException e) {
                constructAndLogSystemError(requestContext.getMessageContext(), e);
                result = "failure";
            }
        } else {
            result = "failure";
        }
        return result;
    }

    public Map<String, String> getAllQgEventSources() {
        List<EventSource> eventSources = questionnaireServiceFacade.getAllEventSources();
        Map<String, String> evtSourcesMap = new HashMap<String, String>();
        for (EventSource evtSrc : eventSources) {
            evtSourcesMap.put(getEventSourceId(evtSrc), evtSrc.getDescription());
        }
        return evtSourcesMap;
    }

    public String addSection(QuestionGroupForm questionGroupForm, RequestContext requestContext) {
        if (questionGroupForm.hasQuestionsInCurrentSection()) {
            constructErrorMessage(requestContext.getMessageContext(),
                    "questionnaire.error.no.question.in.section", "currentSectionTitle", "Section should have at least one question.");
            return "failure";
        }
        questionGroupForm.addCurrentSection();
        return "success";
    }

    public String deleteSection(QuestionGroupForm questionGroupForm, String sectionName) {
        questionGroupForm.removeSection(sectionName);
        return "success";
    }

    public String deleteQuestion(QuestionGroupForm questionGroupForm, String sectionName, String questionId) {
        questionGroupForm.removeQuestion(sectionName, questionId);
        return "success";
    }

    private String getEventSourceId(EventSource evtSrc) {
        return evtSrc.getEvent().trim().concat(".").concat(evtSrc.getSource().trim());
    }

    public List<SectionQuestionDetail> getAllSectionQuestions() {
        List<SectionQuestionDetail> sectionQuestionDetails = new ArrayList<SectionQuestionDetail>();
        List<QuestionDetail> questionDetails = questionnaireServiceFacade.getAllQuestions();
        if (questionDetails != null) {
            for (QuestionDetail questionDetail : questionDetails) {
                sectionQuestionDetails.add(new SectionQuestionDetail(questionDetail, false));
            }
        }
        return sectionQuestionDetails;
    }

    public String saveQuestionnaire(QuestionGroupDetails questionGroupDetails, int questionGroupIndex, RequestContext requestContext) {
        QuestionGroupDetail questionGroupDetail = questionGroupDetails.getDetails().get(questionGroupIndex);
        try {
            questionnaireServiceFacade.saveResponses(new QuestionGroupDetails(questionGroupDetails.getCreatorId(),
                    questionGroupDetails.getEntityId(), Arrays.asList(questionGroupDetail)));
        } catch (ValidationException e) {
            if (e.containsChildExceptions()) {
                for (ValidationException validationException : e.getChildExceptions()) {
                    String title = validationException.getSectionQuestionDetail().getTitle();
                    constructErrorMessage("questionnaire.noresponse", "Please specify " + title,
                            requestContext.getMessageContext(), title);
                }
            }
            return "failure";
        }
        return "success";
    }

    private boolean questionGroupHasErrors(QuestionGroupForm questionGroup, RequestContext requestContext) {
        boolean result = false;
        if (isInvalidTitle(questionGroup.getTitle())) {
            constructErrorMessage(requestContext.getMessageContext(), "questionnaire.error.questionGroup.title.empty", "title", "Please specify Question Group title");
            result = true;
        }
        if (sectionsNotPresent(questionGroup.getSections())) {
            constructErrorMessage(requestContext.getMessageContext(), "questionnaire.error.section.atLeastOne", "sectionName", "Please specify at least one section or question");
            result = true;
        }
        if (appliesToNotPresent(questionGroup.getEventSourceId())) {
            constructErrorMessage(requestContext.getMessageContext(), "questionnaire.error.appliesTo.mandatory", "eventSourceId", "Please choose a valid 'Applies To' value");
            result = true;
        }
        return result;
    }

    private boolean appliesToNotPresent(String eventSourceId) {
        return StringUtils.isEmpty(eventSourceId) || "--select one--".equals(eventSourceId);
    }

    private boolean sectionsNotPresent(List<SectionDetailForm> sections) {
        return CollectionUtils.isEmpty(sections);
    }

    private boolean isInvalidTitle(String title) {
        return StringUtils.isEmpty(StringUtils.trimToNull(title));
    }

    private String decodeUrl(String backPageUrl) throws UnsupportedEncodingException {
        return URLDecoder.decode(backPageUrl, "UTF-8");
    }

}
