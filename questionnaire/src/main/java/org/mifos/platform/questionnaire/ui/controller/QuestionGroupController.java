/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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
import org.mifos.platform.validations.ValidationException;
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.exceptions.BadNumericResponseException;
import org.mifos.platform.questionnaire.exceptions.MandatoryAnswerNotFoundException;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetails;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;
import org.mifos.platform.questionnaire.ui.model.QuestionGroupForm;
import org.mifos.platform.questionnaire.ui.model.SectionDetailForm;
import org.mifos.platform.questionnaire.ui.model.Question;
import org.mifos.platform.util.CollectionUtils;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.security.access.AccessDeniedException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import static java.text.MessageFormat.format;

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
        List <QuestionGroupDetail> questionGroups = questionnaireServiceFacade.getAllQuestionGroups();
        model.addAttribute("questionGroups", groupByEventSource(questionGroups));
        return "viewQuestionGroups";
    }

    private Map<String,List <QuestionGroupDetail>> groupByEventSource(List <QuestionGroupDetail> questionGroups){
        Map <String,List <QuestionGroupDetail>> questionGroupsCategoriesSplit = new HashMap<String, List<QuestionGroupDetail>>();
        for (QuestionGroupDetail questionGroup : questionGroups){
            for (EventSourceDto eventSourceDto : questionGroup.getEventSources()) {
                String eventSource = eventSourceDto.toString();
                if(!questionGroupsCategoriesSplit.containsKey(eventSource)) {
                    questionGroupsCategoriesSplit.put(eventSource, new ArrayList<QuestionGroupDetail>());
                }
                questionGroupsCategoriesSplit.get(eventSource).add(questionGroup);
            }
        }
        // MIFOS-4164: sort by event source
        Map <String,List <QuestionGroupDetail>> orderedQGCategories = new LinkedHashMap<String, List<QuestionGroupDetail>>();
        for (EventSourceDto eventSourceDto : questionnaireServiceFacade.getAllEventSources()) {
            String eventSource = eventSourceDto.toString();
            List <QuestionGroupDetail> questionGroupsList = questionGroupsCategoriesSplit.get(eventSource);
            if (questionGroupsList != null) {
                orderedQGCategories.put(eventSource, questionGroupsList);
            }
        }
        return orderedQGCategories;
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

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    public String defineQuestionGroup(QuestionGroupForm questionGroupForm, RequestContext requestContext, boolean createMode) {
        String result = "success";
        if (!questionGroupHasErrors(questionGroupForm, requestContext)) {
            Integer questionGroupId;
            try {
                if (createMode) {
                    questionGroupForm.setActive(true);
                }
                if (questionGroupForm.isActive()) {
                    questionGroupId = questionnaireServiceFacade.createActiveQuestionGroup(questionGroupForm.getQuestionGroupDetail());
                }
                else {
                    questionGroupId = questionnaireServiceFacade.createQuestionGroup(questionGroupForm.getQuestionGroupDetail());
                }
                if (containsCreateLoanEventSource(questionGroupForm.getEventSources())){
                    if (questionGroupForm.getApplyToAllLoanProducts()) {
                        questionnaireServiceFacade.applyToAllLoanProducts(questionGroupId);
                    }
                    else if (!createMode){
                        questionnaireServiceFacade.removeFromAllLoanProducts(questionGroupId);
                    }
                }

            }
            catch (AccessDeniedException e) {
                constructAndLogSystemError(requestContext.getMessageContext(),
                        new SystemException(QuestionnaireConstants.MISSING_PERMISSION_TO_ACTIVATE_QG, e));
                result = "failure";
            }
            catch (SystemException e) {
                constructAndLogSystemError(requestContext.getMessageContext(), e);
                result = "failure";
            }
        } else {
            result = "failure";
        }
        return result;
    }

    private boolean containsCreateLoanEventSource(List<EventSourceDto> eventSources) {
        for (EventSourceDto eventSource : eventSources) {
            if ("Create.Loan".equals(eventSource.getDescription())) {
                return true;
            }
        }
        return false;
    }

    public Map<String, String> getAllQgEventSources() {
        List<EventSourceDto> eventSourceDtos = questionnaireServiceFacade.getAllEventSources();
        Map<String, String> evtSourcesMap = new LinkedHashMap<String, String>();
        for (EventSourceDto evtSrc : eventSourceDtos) {
            evtSourcesMap.put(getEventSourceId(evtSrc), evtSrc.getDescription());
        }
        return evtSourcesMap;
    }

    public String addSection(QuestionGroupForm questionGroupForm, RequestContext requestContext) {
        if (questionGroupForm.hasNoQuestionsInCurrentSection()) {
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

    public String moveQuestionUp(QuestionGroupForm questionGroupForm, String sectionName, String questionId) {
        questionGroupForm.moveQuestionUp(sectionName, questionId);
        return "success";
    }

    public String moveQuestionDown(QuestionGroupForm questionGroupForm, String sectionName, String questionId) {
        questionGroupForm.moveQuestionDown(sectionName, questionId);
        return "success";
    }

    public String moveSectionUp(QuestionGroupForm questionGroupForm, String sectionName) {
        questionGroupForm.moveSectionUp(sectionName);
        return "success";
    }

    public String moveSectionDown(QuestionGroupForm questionGroupForm, String sectionName) {
        questionGroupForm.moveSectionDown(sectionName);
        return "success";
    }

    private String getEventSourceId(EventSourceDto evtSrc) {
        return evtSrc.getEvent().trim().concat(".").concat(evtSrc.getSource().trim());
    }

    public List<SectionQuestionDetail> getAllSectionQuestions() {
        List<SectionQuestionDetail> sectionQuestionDetails = new ArrayList<SectionQuestionDetail>();
        List<QuestionDetail> questionDetails = questionnaireServiceFacade.getAllActiveQuestions();
        if (questionDetails != null) {
            for (QuestionDetail questionDetail : questionDetails) {
                sectionQuestionDetails.add(new SectionQuestionDetail(questionDetail, false));
            }
        }
        return sectionQuestionDetails;
    }

    public List<SectionQuestionDetail> getAllSectionQuestions(QuestionGroupForm questionGroupForm) {
        List<SectionQuestionDetail> sectionQuestionDetails = new ArrayList<SectionQuestionDetail>();
        List<Integer> questionIds = questionGroupForm.getQuestionGroupDetail().getAllQuestionIds();
        List<QuestionDetail> questionDetails = questionnaireServiceFacade.getAllActiveQuestions(questionIds);
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
                    questionGroupDetails.getEntityId(), questionGroupDetails.getEventSourceId(), Arrays.asList(questionGroupDetail)));
        } catch (ValidationException e) {
            if (e.hasChildExceptions()) {
                for (ValidationException validationException : e.getChildExceptions()) {
                    if (validationException instanceof MandatoryAnswerNotFoundException) {
                        populateError(requestContext, (MandatoryAnswerNotFoundException) validationException);
                    } else if (validationException instanceof BadNumericResponseException) {
                        populateError(requestContext, (BadNumericResponseException) validationException);
                    }
                }
            }
            return "failure";
        }
        return "success";
    }

    private void populateError(RequestContext requestContext, BadNumericResponseException exception) {
        String title = exception.getIdentifier();
        String code, message;
        Integer allowedMinValue = exception.getAllowedMinValue();
        Integer allowedMaxValue = exception.getAllowedMaxValue();
        if (exception.areMinMaxBoundsPresent()) {
            code = "questionnaire.invalid.numeric.range.response";
            message = format("Please specify a number between {0} and {1} for {2}", allowedMinValue, allowedMaxValue, title);
            constructErrorMessage(code, message, requestContext.getMessageContext(), allowedMinValue, allowedMaxValue, title);
        } else if (exception.isMinBoundPresent()) {
            code = "questionnaire.invalid.numeric.min.response";
            message = format("Please specify a number greater than {0} for {1}", allowedMinValue, title);
            constructErrorMessage(code, message, requestContext.getMessageContext(), allowedMinValue, title);
        } else if (exception.isMaxBoundPresent()) {
            code = "questionnaire.invalid.numeric.max.response";
            message = format("Please specify a number lesser than {0} for {1}", allowedMaxValue, title);
            constructErrorMessage(code, message, requestContext.getMessageContext(), allowedMaxValue, title);
        } else {
            code = "questionnaire.invalid.numeric.response";
            message = format("Please specify a number for {0}", title);
            constructErrorMessage(code, message, requestContext.getMessageContext(), title);
        }
    }

    private void populateError(RequestContext requestContext, MandatoryAnswerNotFoundException exception) {
        String title = exception.getIdentifier();
        constructErrorMessage("questionnaire.noresponse", format("Please specify {0}", title), requestContext.getMessageContext(), title);
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
        if (appliesToNotPresent(questionGroup.getEventSourceIds())) {
            constructErrorMessage(requestContext.getMessageContext(), "questionnaire.error.appliesTo.mandatory", "eventSourceId", "Please choose a valid 'Applies To' value");
            result = true;
        }
        return result;
    }

    private boolean appliesToNotPresent(List<String> eventSourceIds) {
        return eventSourceIds == null || eventSourceIds.size() == 0;
    }

    private boolean sectionsNotPresent(List<SectionDetailForm> sections) {
        return CollectionUtils.isEmpty(sections);
    }

    private boolean isInvalidTitle(String title) {
        return StringUtils.isEmpty(StringUtils.trimToNull(title));
    }

    public String addQuestion(QuestionGroupForm questionGroupForm, RequestContext requestContext) {
        MessageContext context = requestContext.getMessageContext();
        boolean result = validateQuestion(questionGroupForm, context);
        if (result) {
            questionGroupForm.addCurrentSection();
        }
        return result? "success": "failure";
    }

    public String addSmartChoiceTag(QuestionGroupForm questionGroupForm, RequestContext requestContext, int choiceIndex) {
        MessageContext context = requestContext.getMessageContext();
        boolean result = validateSmartChoice(questionGroupForm, context, choiceIndex);
        if (result) questionGroupForm.getCurrentQuestion().addSmartChoiceTag(choiceIndex);
        return result? "success": "failure";
    }

    private boolean validateSmartChoice(QuestionGroupForm questionGroupForm, MessageContext context, int choiceIndex) {
        boolean result = true;
        Question question = questionGroupForm.getCurrentQuestion();

        if (context.hasErrorMessages()) {
            result = false;
        }

        else if (question.isSmartChoiceDuplicated(choiceIndex)) {
            constructErrorMessage(
                    context, "questionnaire.error.question.tags.duplicate",
                    "currentQuestion.answerChoices", "The tag with the same name already exists.");
            result = false;
        }

        else if (question.isTagsLimitReached(choiceIndex)) {
            constructErrorMessage(
                    context, "questionnaire.error.question.tags.limit",
                    "currentQuestion.answerChoices", "You cannot add more than five tags.");
            result = false;
        }

        return result;
    }

    private boolean validateQuestion(QuestionGroupForm questionGroupForm, MessageContext context) {
        questionGroupForm.validateConstraints(context);
        boolean result = true;

        if (context.hasErrorMessages()) {
            result = false;
        }

        else if (checkDuplicateText(questionGroupForm)) {
            constructErrorMessage(
                    context, "questionnaire.error.question.duplicate",
                    "currentQuestion.title", "The name specified already exists.");
            result = false;
        }

        else if(questionGroupForm.getCurrentQuestion().answerChoicesAreInvalid()) {
            constructErrorMessage(
                    context, "questionnaire.error.question.choices",
                    "currentQuestion.choice", "Please specify at least 2 choices.");
            result = false;
        }

        else if (questionGroupForm.getCurrentQuestion().numericBoundsAreInvalid()) {
            constructErrorMessage(
                    context, QuestionnaireConstants.INVALID_NUMERIC_BOUNDS,
                    "currentQuestion.numericMin", "Please ensure maximum value is greater than minimum value.");
            result = false;
        }

        return result;
    }

    private boolean checkDuplicateText(QuestionGroupForm questionGroupForm) {
        String text = StringUtils.trim(questionGroupForm.getCurrentQuestion().getText());
        return questionGroupForm.isDuplicateText(text) || questionnaireServiceFacade.isDuplicateQuestion(text);
    }

}
