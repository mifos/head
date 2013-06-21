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

import static java.text.MessageFormat.format;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.exceptions.BadNumericResponseException;
import org.mifos.platform.questionnaire.exceptions.MandatoryAnswerNotFoundException;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetails;
import org.mifos.platform.questionnaire.service.QuestionLinkDetail;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.questionnaire.service.SectionDetail;
import org.mifos.platform.questionnaire.service.SectionLinkDetail;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;
import org.mifos.platform.questionnaire.ui.model.Question;
import org.mifos.platform.questionnaire.ui.model.QuestionGroupForm;
import org.mifos.platform.questionnaire.ui.model.SectionDetailForm;
import org.mifos.platform.util.CollectionUtils;
import org.mifos.platform.validations.ValidationException;
import org.springframework.binding.message.MessageContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.webflow.execution.RequestContext;

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
    
    @RequestMapping(value="/getHiddenVisibleQuestions.ftl", method=RequestMethod.POST)
    public @ResponseBody Map<String, Map<Integer, Boolean>> getHiddenVisibleQuestions(
            @RequestParam Integer questionId, @RequestParam String response) throws ParseException {
        return questionnaireServiceFacade.getHiddenVisibleQuestionsAndSections(questionId, response);
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    public String defineQuestionGroup(QuestionGroupForm questionGroupForm, RequestContext requestContext, boolean createMode) {
        String result = "success";
        if (!questionGroupHasErrors(questionGroupForm, requestContext)) {
            QuestionGroupDetail questionGroupDetail = null;
            try {
                if (createMode) {
                    questionGroupForm.setActive(true);
                }
                if (questionGroupForm.isActive()) {
                    questionGroupDetail = questionnaireServiceFacade.createActiveQuestionGroup(questionGroupForm.getQuestionGroupDetail());
                }
                else {
                    questionGroupDetail = questionnaireServiceFacade.createQuestionGroup(questionGroupForm.getQuestionGroupDetail());
                }
                if (containsCreateLoanEventSource(questionGroupForm.getEventSources()) && questionGroupForm.getApplyToAllLoanProducts()) {
                    questionnaireServiceFacade.applyToAllLoanProducts(questionGroupDetail.getId());
                }
                List<QuestionLinkDetail> questionLinkDetails = setFilledQuestionDetailForQuestionLinks(questionGroupForm.getQuestionLinks(), questionGroupDetail);
                List<SectionLinkDetail> sectionLinkDetails = setFilledSectionDetailForQuestionLinks(questionGroupForm.getSectionLinks(), questionGroupDetail);
                
                questionnaireServiceFacade.createQuestionLinks(questionLinkDetails);
                questionnaireServiceFacade.createSectionLinks(sectionLinkDetails);
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
    private boolean questionGroupLinkHasErrors(QuestionGroupForm questionGroupForm, RequestContext requestContext, String sourceQuestionId, 
            String linkType, String appliesTo, String affectedQuestionId, String affectedSectionName, 
            String value, String additionalValue) {
        boolean result = false;
        if (isValueNotPresent(value)) {
            constructErrorMessage(requestContext.getMessageContext(), "questionnaire.link.error.value.empty", "value", "Please specify Question Link value");
            result = true;
        }
        if (isLinkTypeNotChoosen(linkType)) {
            constructErrorMessage(requestContext.getMessageContext(), "questionnaire.link.error.linkType.empty", "linkType", "Please choose a Question Link type.");
            result = true;
        }
        if (isSourceQuestionPresent(sourceQuestionId)) {
            constructErrorMessage(requestContext.getMessageContext(), "questionnaire.link.error.sourceQuestion.empty", "sourceQuestionId", "Please choose a valid 'Source question'.");
            result = true;
        }
        if (isAffectedQuestionPresent(affectedQuestionId) && appliesTo.equals("question")) {
            constructErrorMessage(requestContext.getMessageContext(), "questionnaire.link.error.affectedQuestion.empty", "affectedQuestionId", "Please choose a valid 'Affected question'.");
            result = true;
        }
        if (isAffectedSectionPresent(affectedSectionName) && appliesTo.equals("section")) {
            constructErrorMessage(requestContext.getMessageContext(), "questionnaire.link.error.affectedSection.empty", "affectedSectionName", "Please choose a valid 'Affected section'.");
            result = true;
        }
        return result;
    }
    private boolean isValueNotPresent(String value){
        return StringUtils.isEmpty(StringUtils.trimToNull(value));
    }
    
    private boolean isLinkTypeNotChoosen(String linkType){
        return StringUtils.isEmpty(linkType);
    }
    
    private boolean isSourceQuestionPresent(String sourceQuestionId){
        return StringUtils.isEmpty(sourceQuestionId);
    }
    
    private boolean isAffectedQuestionPresent(String affectedQuestionId){
        return StringUtils.isEmpty(affectedQuestionId);
    }
    
    private boolean isAffectedSectionPresent(String affectedSectionName){
        return StringUtils.isEmpty(affectedSectionName);
    }

    public QuestionLinkDetail setQuestionLinkDetail(QuestionGroupForm questionGroupForm, String sourceQuestionId, 
            String affectedQuestionId, String value, String additionalValue, String linkType, String linkTypeDisplay){
        
        QuestionLinkDetail questionLinkDetail = new QuestionLinkDetail();
        SectionQuestionDetail sourceQuestion = findQuestionDetailById(questionGroupForm.getQuestionGroupDetail(), sourceQuestionId);
        questionLinkDetail.setSourceQuestion(sourceQuestion);
        SectionQuestionDetail affectedQuestion = findQuestionDetailById(questionGroupForm.getQuestionGroupDetail(), affectedQuestionId);
        questionLinkDetail.setAffectedQuestion(affectedQuestion);
        questionLinkDetail.setValue(value);
        questionLinkDetail.setLinkType(Integer.valueOf(linkType));
        questionLinkDetail.setLinkTypeDisplay(linkTypeDisplay);
        questionLinkDetail.setState(false);
        if(additionalValue!=null)
            questionLinkDetail.setAdditionalValue(additionalValue);
        
        return questionLinkDetail;
    }
    
    public SectionLinkDetail setSectionLinkDetail(QuestionGroupForm questionGroupForm, String sourceQuestionId, 
            String affectedSectionName, String value, String additionalValue, String linkType, String linkTypeDisplay){
        
        SectionLinkDetail sectionLinkDetail = new SectionLinkDetail();
        SectionQuestionDetail sourceQuestion = findQuestionDetailById(questionGroupForm.getQuestionGroupDetail(), sourceQuestionId);
        sectionLinkDetail.setSourceQuestion(sourceQuestion);
        SectionDetail affectedSection = findSectionDetailByName(questionGroupForm.getQuestionGroupDetail(), affectedSectionName);
        sectionLinkDetail.setAffectedSection(affectedSection);
        sectionLinkDetail.setValue(value);
        sectionLinkDetail.setLinkType(Integer.valueOf(linkType));
        sectionLinkDetail.setLinkTypeDisplay(linkTypeDisplay);
        sectionLinkDetail.setState(false);
        if(additionalValue!=null)
            sectionLinkDetail.setAdditionalValue(additionalValue);
        return sectionLinkDetail;
    }
    
    public SectionQuestionDetail findQuestionDetailById(QuestionGroupDetail questionGroupDetail, String questionId){
        for(SectionDetail sectionDetail : questionGroupDetail.getSectionDetails()){
            for(SectionQuestionDetail sectionQuestionDetail : sectionDetail.getQuestions()){
                if(sectionQuestionDetail.getQuestionId()==Integer.valueOf(questionId))
                    return sectionQuestionDetail;
            }
        }
        return null;
    }
    public SectionQuestionDetail findQuestionDetailByNickname(QuestionGroupDetail questionGroupDetail, String nickname){
        for(SectionDetail sectionDetail : questionGroupDetail.getSectionDetails()){
            for(SectionQuestionDetail sectionQuestionDetail : sectionDetail.getQuestions()){
                if(sectionQuestionDetail.getQuestionDetail().getNickname().equals(nickname))
                    return sectionQuestionDetail;
            }
        }
        return null;
    }
    public SectionDetail findSectionDetailByName(QuestionGroupDetail questionGroupDetail, String sectionName){
        for(SectionDetail sectionDetail : questionGroupDetail.getSectionDetails()){
            if(sectionDetail.getName().equals(sectionName)){
                    return sectionDetail;
            }
        }
        return null;
    }
    public List<QuestionLinkDetail> setFilledQuestionDetailForQuestionLinks(List<QuestionLinkDetail> questionLinkDetails, QuestionGroupDetail questionGroupDetail){
        for(QuestionLinkDetail questionLinkDetail : questionLinkDetails){
            questionLinkDetail.setSourceQuestion(findQuestionDetailByNickname(questionGroupDetail, String.valueOf(questionLinkDetail.getSourceQuestion().getQuestionDetail().getNickname())));
            questionLinkDetail.setAffectedQuestion(findQuestionDetailByNickname(questionGroupDetail, String.valueOf(questionLinkDetail.getAffectedQuestion().getQuestionDetail().getNickname())));
        }
        return questionLinkDetails;
    }
    public List<SectionLinkDetail> setFilledSectionDetailForQuestionLinks(List<SectionLinkDetail> sectionLinkDetails, QuestionGroupDetail questionGroupDetail){
        for(SectionLinkDetail sectionLinkDetail : sectionLinkDetails){
            sectionLinkDetail.setSourceQuestion(findQuestionDetailByNickname(questionGroupDetail, String.valueOf(sectionLinkDetail.getSourceQuestion().getQuestionDetail().getNickname())));
            sectionLinkDetail.setAffectedSection(findSectionDetailByName(questionGroupDetail, sectionLinkDetail.getAffectedSection().getName()));
        }
        return sectionLinkDetails;
    }
    
    public String addLink(QuestionGroupForm questionGroupForm,
            String sourceQuestionId, String linkType, String appliesTo,
            String affectedQuestionId, String affectedSectionName,
            String value, String additionalValue, Map<String, String> linkTypes, RequestContext requestContext) {
        String result = "success";
        if (!questionGroupLinkHasErrors(questionGroupForm, requestContext,
                sourceQuestionId, linkType, appliesTo, affectedQuestionId,
                affectedSectionName, value, additionalValue)) {
            
            if (appliesTo.equals("question")) {
                QuestionLinkDetail questionLink = setQuestionLinkDetail(questionGroupForm, sourceQuestionId, affectedQuestionId, value, additionalValue, linkType, linkTypes.get(linkType));
                questionGroupForm.getQuestionLinks().add(questionLink);

            } else {
                SectionLinkDetail sectionLink = setSectionLinkDetail(questionGroupForm, sourceQuestionId, affectedSectionName, value, additionalValue, linkType, linkTypes.get(linkType));
                questionGroupForm.getSectionLinks().add(sectionLink);
            }
        } else {
            result = "failure";
        }
        return result;
    }
    
    public String removeLink(QuestionGroupForm questionGroupForm,
            String sourceQuestionId, String affectedQuestionId, String affectedSectionName, String value, String additionalValue, RequestContext requestContext) {
        String result = "success";
        if(!affectedQuestionId.isEmpty()){
            for(Iterator<QuestionLinkDetail> iterator = questionGroupForm.getQuestionLinks().iterator(); iterator.hasNext();){
                QuestionLinkDetail questionLinkDetail = iterator.next();
                if(questionLinkDetail.getSourceQuestion().getQuestionId()==Integer.valueOf(sourceQuestionId) && 
                        questionLinkDetail.getAffectedQuestion().getQuestionId()==Integer.valueOf(affectedQuestionId) &&
                        questionLinkDetail.getValue().equals(value) && questionLinkDetail.getAdditionalValue().equals(additionalValue))
                    iterator.remove();
            }
        } else {
            for(Iterator<SectionLinkDetail> iterator = questionGroupForm.getSectionLinks().iterator(); iterator.hasNext();){
                SectionLinkDetail sectionLinkDetail = iterator.next();
                if(sectionLinkDetail.getSourceQuestion().getQuestionId()==Integer.valueOf(sourceQuestionId) && 
                        sectionLinkDetail.getAffectedSection().getName().equals(affectedSectionName) &&
                                sectionLinkDetail.getValue().equals(value) && sectionLinkDetail.getAdditionalValue().equals(additionalValue))
                    iterator.remove();
            }
            
        }
        return result;
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
    
    public Map<String, String> getAllLinkTypes() {
        return questionnaireServiceFacade.getAllLinkTypes();
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
