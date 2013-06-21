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

package org.mifos.platform.questionnaire.domain;

import static org.mifos.platform.questionnaire.QuestionnaireConstants.PPI_SURVEY_FILE_EXT;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.PPI_SURVEY_FILE_PREFIX;
import static org.mifos.platform.util.CollectionUtils.isNotEmpty;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.framework.business.EntityMaster;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.domain.ppi.PPISurveyLocator;
import org.mifos.platform.questionnaire.mappers.QuestionnaireMapper;
import org.mifos.platform.questionnaire.parsers.QuestionGroupDefinitionParser;
import org.mifos.platform.questionnaire.persistence.EventSourceDao;
import org.mifos.platform.questionnaire.persistence.QuestionDao;
import org.mifos.platform.questionnaire.persistence.QuestionGroupDao;
import org.mifos.platform.questionnaire.persistence.QuestionGroupInstanceDao;
import org.mifos.platform.questionnaire.persistence.QuestionGroupLinkDao;
import org.mifos.platform.questionnaire.persistence.SectionLinkDao;
import org.mifos.platform.questionnaire.persistence.SectionQuestionDao;
import org.mifos.platform.questionnaire.persistence.SectionQuestionLinkDao;
import org.mifos.platform.questionnaire.service.InformationOrder;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetails;
import org.mifos.platform.questionnaire.service.QuestionGroupInstanceDetail;
import org.mifos.platform.questionnaire.service.QuestionLinkDetail;
import org.mifos.platform.questionnaire.service.SectionDetail;
import org.mifos.platform.questionnaire.service.SectionLinkDetail;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupInstanceDto;
import org.mifos.platform.questionnaire.service.dtos.SectionDto;
import org.mifos.platform.questionnaire.validators.QuestionnaireValidator;
import org.mifos.platform.validations.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("PMD")
public class QuestionnaireServiceImpl implements QuestionnaireService {

    @Autowired
    private QuestionnaireValidator questionnaireValidator;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private QuestionGroupDao questionGroupDao;

    @Autowired
    private EventSourceDao eventSourceDao;

    @Autowired
    private QuestionGroupInstanceDao questionGroupInstanceDao;

    @Autowired
    private QuestionnaireMapper questionnaireMapper;

    @Autowired
    private PPISurveyLocator ppiSurveyLocator;

    @Autowired
    private QuestionGroupDefinitionParser questionGroupDefinitionParser;

    @Autowired
    private SectionQuestionDao sectionQuestionDao;
    
    @Autowired
    private InformationOrderService informationOrderService;
    
    @Autowired
    private QuestionGroupLinkDao questionGroupLinkDao;
    
    @Autowired
    private SectionQuestionLinkDao sectionQuestionLinkDao;
    
    @Autowired
    private SectionLinkDao sectionLinkDao;
    
    @SuppressWarnings({"UnusedDeclaration"})
    private QuestionnaireServiceImpl() {
    }
    
    public QuestionnaireServiceImpl(QuestionnaireValidator questionnaireValidator, QuestionDao questionDao,
                                    QuestionnaireMapper questionnaireMapper, QuestionGroupDao questionGroupDao,
                                    EventSourceDao eventSourceDao, QuestionGroupInstanceDao questionGroupInstanceDao,
                                    PPISurveyLocator ppiSurveyLocator, QuestionGroupDefinitionParser questionGroupDefinitionParser,
                                    SectionQuestionDao sectionQuestionDao) {
        this.questionnaireValidator = questionnaireValidator;
        this.questionDao = questionDao;
        this.questionnaireMapper = questionnaireMapper;
        this.questionGroupDao = questionGroupDao;
        this.eventSourceDao = eventSourceDao;
        this.questionGroupInstanceDao = questionGroupInstanceDao;
        this.ppiSurveyLocator = ppiSurveyLocator;
        this.questionGroupDefinitionParser = questionGroupDefinitionParser;
        this.sectionQuestionDao = sectionQuestionDao;
    }
    
    @Override
    public QuestionDetail defineQuestion(QuestionDetail questionDetail) throws SystemException {
        questionnaireValidator.validateForDefineQuestion(questionDetail);
        QuestionEntity question = questionnaireMapper.mapToQuestion(questionDetail);
        persistQuestion(question);
        return questionnaireMapper.mapToQuestionDetail(question);
    }

    @Override
    public List<QuestionDetail> getAllQuestions() {
        List<QuestionEntity> questions = questionDao.retrieveAll();
        return questionnaireMapper.mapToQuestionDetails(questions);
    }

    @Override
    public List<QuestionDetail> getAllActiveQuestions(List<Integer> questionIdsToExclude) {
        List<QuestionEntity> questions;
        if (isNotEmpty(questionIdsToExclude)) {
            questions = questionDao.retrieveByStateExcluding(questionIdsToExclude, QuestionState.ACTIVE.getValue());
            questions.addAll(questionDao.retrieveByStateExcluding(questionIdsToExclude, QuestionState.ACTIVE_NOT_EDITABLE.getValue()));
        } else {
            questions = questionDao.retrieveByState(QuestionState.ACTIVE.getValue());
            questions.addAll(questionDao.retrieveByState(QuestionState.ACTIVE_NOT_EDITABLE.getValue()));
        }
        return questionnaireMapper.mapToQuestionDetails(questions);
    }

    @Override
    public QuestionGroupDetail defineQuestionGroup(QuestionGroupDetail questionGroupDetail) throws SystemException {
        questionnaireValidator.validateForDefineQuestionGroup(questionGroupDetail);
        generateNicknamesForQuestions(questionGroupDetail);
        QuestionGroup questionGroup = questionnaireMapper.mapToQuestionGroup(questionGroupDetail);
        questionGroupDao.create(questionGroup);
        
        List<EntityMaster> usedSources = new ArrayList<EntityMaster>();
        for (Section section: questionGroup.getSections()) {
            for (SectionQuestion sectionQuestion: section.getQuestions()) {
                usedSources.clear();
                for (EventSourceEntity eventSourceEntity: questionGroup.getEventSources()) {

                    if (!(eventSourceEntity.getSource().getEntityType().equals("Client")
                            || eventSourceEntity.getSource().getEntityType().equals("Loan"))) {
                        continue;
                    }
                    
                    InformationOrder informationOrder = 
                            new InformationOrder(null, "additional", sectionQuestion.getId(), eventSourceEntity.getSource().getEntityType(), 999);                    
                    
                    if (!sectionQuestion.isShowOnPage()) {
                        informationOrderService.removeAdditionalQuestionIfExists(informationOrder);
                    }
                    
                    if (usedSources.contains(eventSourceEntity.getSource())) {
                        continue;
                    }
                    
                    if (sectionQuestion.isShowOnPage()) {
                        informationOrderService.addAdditionalQuestionIfNotExists(informationOrder);
                    }
                    
                    usedSources.add(eventSourceEntity.getSource());
                }
            }
        }
        
        return questionnaireMapper.mapToQuestionGroupDetail(questionGroup);
    }

    @Override
    public List<QuestionGroupDetail> getAllQuestionGroups() {
        List<QuestionGroup> questionGroups = questionGroupDao.getDetailsAll();
        return questionnaireMapper.mapToQuestionGroupDetails(questionGroups);
    }

    @Override
    public boolean isDuplicateQuestionText(String title) {
        List result = questionDao.retrieveCountOfQuestionsWithText(title);
        return (Long) result.get(0) > 0;
    }

    @Override
    public QuestionGroupDetail getQuestionGroup(int questionGroupId) throws SystemException {
        QuestionGroup questionGroup = questionGroupDao.getDetails(questionGroupId);
        if (questionGroup == null) {
            throw new SystemException(QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND);
        }
        return questionnaireMapper.mapToQuestionGroupDetail(questionGroup);
    }

    @Override
    public QuestionDetail getQuestion(int questionId) throws SystemException {
        QuestionEntity question = questionDao.getDetails(questionId);
        if (question == null) {
            throw new SystemException(QuestionnaireConstants.QUESTION_NOT_FOUND);
        }
        return questionnaireMapper.mapToQuestionDetail(question);
    }

    @Override
    public List<EventSourceDto> getAllEventSources() {
        return questionnaireMapper.mapToEventSources(eventSourceDao.retrieveAllEventSourcesOrdered());
    }

    @Override
    public List<QuestionGroupDetail> getQuestionGroups(EventSourceDto eventSourceDto) throws SystemException {
        questionnaireValidator.validateForEventSource(eventSourceDto);
        List<QuestionGroup> questionGroups = questionGroupDao.retrieveQuestionGroupsByEventSource(eventSourceDto.getEvent(), eventSourceDto.getSource());
        List<QuestionGroupDetail> questionGroupDetails = questionnaireMapper.mapToQuestionGroupDetails(questionGroups);
        removeInActiveSectionsAndQuestions(questionGroupDetails);
        return questionGroupDetails;
    }

    @Override
    public void saveResponses(QuestionGroupDetails questionGroupDetails) {
        questionnaireValidator.validateForQuestionGroupResponses(questionGroupDetails.getDetails());
        questionGroupInstanceDao.saveOrUpdateAll(questionnaireMapper.mapToQuestionGroupInstances(questionGroupDetails));
    }

    @Override
    public void validateResponses(List<QuestionGroupDetail> questionGroupDetails) {
        questionnaireValidator.validateForQuestionGroupResponses(questionGroupDetails);
    }

    @Override
    public List<QuestionGroupInstanceDetail> getQuestionGroupInstances(Integer entityId, EventSourceDto eventSourceDto, boolean includeUnansweredQuestionGroups, boolean fetchLastVersion) {
        questionnaireValidator.validateForEventSource(eventSourceDto);
        Integer eventSourceId = getEventSourceEntity(eventSourceDto).getId();
        List<QuestionGroupInstance> questionGroupInstances = getQuestionGroupInstanceEntities(entityId, eventSourceId, fetchLastVersion);
        List<QuestionGroupInstanceDetail> questionGroupInstanceDetails = questionnaireMapper.mapToQuestionGroupInstanceDetails(questionGroupInstances);
        if (includeUnansweredQuestionGroups) {
            List<QuestionGroup> questionGroups = questionGroupDao.retrieveQuestionGroupsByEventSource(eventSourceDto.getEvent(), eventSourceDto.getSource());
            questionGroupInstanceDetails = mergeUnansweredQuestionGroups(questionGroupInstanceDetails, questionGroups);
        }
        return questionGroupInstanceDetails;
    }

    private List<QuestionGroupInstance> getQuestionGroupInstanceEntities(Integer entityId, Integer eventSourceId, boolean fetchLastVersion) {
        List<QuestionGroupInstance> questionGroupInstances;
        if(fetchLastVersion){
            questionGroupInstances = questionGroupInstanceDao.retrieveLatestQuestionGroupInstancesByEntityIdAndEventSourceId(entityId, eventSourceId);
        }else{
            questionGroupInstances = questionGroupInstanceDao.retrieveQuestionGroupInstancesByEntityIdAndEventSourceId(entityId, eventSourceId);
        }
        return questionGroupInstances;
    }

    private List<QuestionGroupInstanceDetail> mergeUnansweredQuestionGroups(List<QuestionGroupInstanceDetail> instancesWithResponses, List<QuestionGroup> questionGroups) {
        List<QuestionGroupInstanceDetail> allInstances = new ArrayList<QuestionGroupInstanceDetail>(instancesWithResponses);
        for (QuestionGroup questionGroup : questionGroups) {
            if (!hasResponse(questionGroup, instancesWithResponses)) {
                allInstances.add(questionnaireMapper.mapToEmptyQuestionGroupInstanceDetail(questionGroup));
            }
        }
        return allInstances;
    }

    private boolean hasResponse(QuestionGroup questionGroup, List<QuestionGroupInstanceDetail> questionGroupInstances) {
        boolean result = false;
        for (QuestionGroupInstanceDetail questionGroupInstance : questionGroupInstances) {
            if (questionGroupInstance.getQuestionGroupDetail().getId() == questionGroup.getId()) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public QuestionGroupInstanceDetail getQuestionGroupInstance(int questionGroupInstanceId) {
        QuestionGroupInstance questionGroupInstance = questionGroupInstanceDao.getDetails(questionGroupInstanceId);
        return questionnaireMapper.mapToQuestionGroupInstanceDetail(questionGroupInstance);
    }

    @Override
    public Integer defineQuestionGroup(QuestionGroupDto questionGroupDto) {
        return defineQuestionGroup(questionGroupDto, true);
    }

    @Override
    public Integer defineQuestionGroup(QuestionGroupDto questionGroupDto, boolean withDuplicateQuestionTextCheck) {
        questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto, withDuplicateQuestionTextCheck);
        QuestionGroup questionGroup = questionnaireMapper.mapToQuestionGroup(questionGroupDto);
        return persistQuestionGroup(questionGroup);
    }
    
    /*
     * When creating questions, if a question has no nickname (nickname == null),
     * then try to find an existing question with the same text and use it
     * instead.  If a question we're about to create has a unique nickname,
     * then go ahead and create a new question even if there is an existing
     * question with the same text.  If there is an an existing question with
     * a nickname that matches the new question nickname, then use the existing
     * question instead.
     */
    private Integer persistQuestionGroup(QuestionGroup questionGroup) {
        List<SectionQuestion> sectionQuestions = questionGroup.getAllSectionQuestions();
        for (SectionQuestion sectionQuestion : sectionQuestions) {
            List<QuestionEntity> questionEntities;
            if (sectionQuestion.getQuestion().getNickname() == null) {
                questionEntities = questionDao.retrieveByText(sectionQuestion.getQuestionText());
            } else {
                questionEntities = questionDao.retrieveByNickname(sectionQuestion.getQuestion().getNickname());
            }

            if (isNotEmpty(questionEntities)) {
                QuestionEntity questionEntity = questionEntities.get(0);
                questionEntity.setQuestionState(sectionQuestion.getQuestion().getQuestionState());
                sectionQuestion.setQuestion(questionEntity);
            }
        }
        generateNicknamesForQuestions(questionGroup);
        return questionGroupDao.create(questionGroup);
    }

    private void generateNicknamesForQuestions(QuestionGroup questionGroup) {
        for (SectionQuestion question : questionGroup.getAllSectionQuestions()) {
            if (question.getQuestion().getNickname() == null) {
                try {
                    question.getQuestion().setNickname(computeMD5(question.getQuestionText()));
                } catch (NoSuchAlgorithmException e) {
                    throw new SystemException(e);
                } catch (UnsupportedEncodingException e) {
                    throw new SystemException(e);
                }
            }
        }
    }

    private void generateNicknamesForQuestions(QuestionGroupDetail questionGroupDetail) {
        for (SectionDetail sectionDetail : questionGroupDetail.getSectionDetails()) {
            for (SectionQuestionDetail sectionQuestionDetail : sectionDetail.getQuestions()) {
                if (sectionQuestionDetail.getQuestionDetail().getNickname() == null) {
                    try {
                        sectionQuestionDetail.getQuestionDetail().setNickname(computeMD5(sectionQuestionDetail.getQuestionDetail().getText()));
                    } catch (NoSuchAlgorithmException e) {
                        throw new SystemException(e);
                    } catch (UnsupportedEncodingException e) {
                        throw new SystemException(e);
                    }
                }
            }
        }
    }

    @Override
    public List<String> getAllCountriesForPPI() {
        List<String> ppiSurveyFiles = ppiSurveyLocator.getAllPPISurveyFiles();
        List<String> countries = new ArrayList<String>();
        for (String ppiSurveyFile : ppiSurveyFiles) {
            String country = ppiSurveyFile.substring(PPI_SURVEY_FILE_PREFIX.length(), ppiSurveyFile.indexOf(PPI_SURVEY_FILE_EXT));
            countries.add(country);
        }
        return countries;
    }

    @Override
    public Integer uploadPPIQuestionGroup(String country) {
        String ppiXmlForCountry = ppiSurveyLocator.getPPIUploadFileForCountry(country);
        QuestionGroupDto questionGroupDto = questionGroupDefinitionParser.parse(ppiXmlForCountry);
        activateQGWithQuestions(questionGroupDto); // according to MIFOS-4146 all uploaded QG and questions should be active
        if (questionGroupDto.isPpi()) { // according to MIFOS-4149 PPI questions should be editable
            makePPIQuestionsNotEditable(questionGroupDto);
        }
        return defineQuestionGroup(questionGroupDto, false);
    }

    private void makePPIQuestionsNotEditable(QuestionGroupDto questionGroupDto) {
        for (SectionDto section : questionGroupDto.getSections()) {
            for (QuestionDto question : section.getQuestions()) {
                question.setEditable(false);
            }
        }
    }

    private void activateQGWithQuestions(QuestionGroupDto questionGroupDto) {
        questionGroupDto.setActive(true);
        for (SectionDto section : questionGroupDto.getSections()) {
            for (QuestionDto question : section.getQuestions()) {
                question.setActive(true);
                question.setEditable(true);
            }
        }
    }

    @Override
    public Integer saveQuestionGroupInstance(QuestionGroupInstanceDto questionGroupInstanceDto) {
        QuestionGroupInstance questionGroupInstance = questionnaireMapper.mapToQuestionGroupInstance(questionGroupInstanceDto);
        return questionGroupInstanceDao.create(questionGroupInstance);
    }

    @SuppressWarnings("PMD.NullAssignment")
    @Override
    public Integer getSectionQuestionId(String sectionName, Integer questionId, Integer questionGroupId) {
        List<Integer> sectionQuestionIds = sectionQuestionDao.retrieveIdFromQuestionGroupIdQuestionIdSectionName(sectionName, questionId, questionGroupId);
        return isNotEmpty(sectionQuestionIds)? sectionQuestionIds.get(0): null;
    }

    @Override
    public Integer defineQuestion(QuestionDto questionDto) {
        questionnaireValidator.validateForDefineQuestion(questionDto);
        QuestionEntity questionEntity = questionnaireMapper.mapToQuestion(questionDto);
        return createQuestion(questionEntity);
    }

    @Override
    public EventSourceDto getEventSource(int eventSourceId) {
        EventSourceEntity sourceEntity = eventSourceDao.getDetails(eventSourceId);
        if (sourceEntity == null) {
            throw new SystemException(QuestionnaireConstants.INVALID_EVENT_SOURCE);
        }
        return questionnaireMapper.mapToEventSources(Arrays.asList(sourceEntity)).get(0);
    }

    @Override
    public Integer getEventSourceId(String event, String source) {
        List<EventSourceEntity> events = eventSourceDao.retrieveByEventAndSource(event,  source);
        if (events == null || events.isEmpty()) {
            throw new SystemException(QuestionnaireConstants.INVALID_EVENT_SOURCE);
        }
        return events.get(0).getId();
    }

    @SuppressWarnings("PMD.PreserveStackTrace")
    private Integer createQuestion(QuestionEntity questionEntity) {
        try {
            if (questionEntity.getNickname() == null) {
                try {
                    questionEntity.setNickname(computeMD5(questionEntity.getQuestionText()));
                } catch (NoSuchAlgorithmException e) {
                    throw new SystemException(e);
                } catch (UnsupportedEncodingException e) {
                    throw new SystemException(e);
                }
            }
            return questionDao.create(questionEntity);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            questionEntity.setNickname(null);
            throw new ValidationException(QuestionnaireConstants.QUESTION_TITLE_DUPLICATE);
        }
    }

    private EventSourceEntity getEventSourceEntity(EventSourceDto eventSourceDto) {
        return eventSourceDao.retrieveByEventAndSource(eventSourceDto.getEvent(), eventSourceDto.getSource()).get(0);
    }

    private void persistQuestion(QuestionEntity question) throws SystemException {
        try {
            if (question.getNickname() == null) {
                try {
                    question.setNickname(computeMD5(question.getQuestionText()));
                } catch (NoSuchAlgorithmException e) {
                    throw new SystemException(e);
                } catch (UnsupportedEncodingException e) {
                    throw new SystemException(e);
                }
            }
            questionDao.saveOrUpdate(question);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            question.setNickname(null);
            throw new SystemException(QuestionnaireConstants.QUESTION_TITLE_DUPLICATE, e);
        }
    }

    private void removeInActiveSectionsAndQuestions(List<QuestionGroupDetail> questionGroupDetails) {
        for (Iterator<QuestionGroupDetail> questionGroupDetailIterator = questionGroupDetails.iterator(); questionGroupDetailIterator.hasNext();) {
            QuestionGroupDetail questionGroupDetail = questionGroupDetailIterator.next();
            removeInActiveSectionsAndQuestions(questionGroupDetail);
            if (questionGroupDetail.hasNoActiveSectionsAndQuestions()) {
                questionGroupDetailIterator.remove();
            }
        }
    }

    private void removeInActiveSectionsAndQuestions(QuestionGroupDetail questionGroupDetail) {
        for (Iterator<SectionDetail> sectionDetailIterator = questionGroupDetail.getSectionDetails().iterator(); sectionDetailIterator.hasNext();) {
            SectionDetail sectionDetail = sectionDetailIterator.next();
            removeInActiveQuestions(sectionDetail);
            if (sectionDetail.hasNoActiveQuestions()) {
                sectionDetailIterator.remove();
            }
        }
    }

    private void removeInActiveQuestions(SectionDetail sectionDetail) {
        for (Iterator<SectionQuestionDetail> sectionQuestionDetailIterator = sectionDetail.getQuestions().iterator(); sectionQuestionDetailIterator.hasNext();) {
            if (sectionQuestionDetailIterator.next().isInactive()) {
                sectionQuestionDetailIterator.remove();
            }
        }
    }

    private String computeMD5(String questionText) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String md5hash = null;
        if (questionText != null) {
            MessageDigest md;
            md = MessageDigest.getInstance("MD5");
            md.update(questionText.getBytes("utf-8"), 0, questionText.length());
            md5hash = convertToHex(md.digest());
        }
        return md5hash;
    }

    private String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (byte element : data) {
            int halfbyte = (element >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = element & 0x0F;
            } while(two_halfs++ < 1);
        }
        return buf.toString();
    }

    @Override
    public QuestionGroup getQuestionGroupById(Integer questionGroupId) {
        return questionGroupDao.getDetails(questionGroupId);
    }

    @Override
    public Map<String, Map<Integer, Boolean>> getHiddenVisibleQuestionsAndSections(
            Integer questionId, String response) throws ParseException {
        Map<Integer, Boolean> questions = new HashMap<Integer, Boolean>();
        Map<Integer, Boolean> sections = new HashMap<Integer, Boolean>();
        
        SectionQuestion question = sectionQuestionDao.getDetails(questionId);
        
        for (SectionQuestionLink dependantQuestionLink: sectionQuestionLinkDao.retrieveDependentSectionQuestionLinksFromQuestion(question.getId())) {
            questions.put(dependantQuestionLink.getAffectedSectionQuestion().getId(), 
                    !isQuestionLinkMatched(dependantQuestionLink.getQuestionGroupLink(), response));
        }
        
        for (SectionLink dependantSectionLink: sectionLinkDao.retrieveDependentSectionLinksFromQuestion(question.getId())) {
            sections.put(dependantSectionLink.getAffectedSection().getId(), 
                    !isQuestionLinkMatched(dependantSectionLink.getQuestionGroupLink(), response));
        }
        
        Map<String, Map<Integer, Boolean>> result = new HashMap<String, Map<Integer, Boolean>>();
        
        result.put("questions", questions);
        result.put("sections", sections);    
        return result;
    }

    public List<LookUpValueEntity> getAllConditions(){
        return sectionQuestionLinkDao.retrieveAllConditions();
    }
    
    private boolean isQuestionLinkMatched(QuestionGroupLink link, String response) throws ParseException {
        
        if (link.getConditionType().equals(QuestionGroupLink.CONDITION_TYPE_EQUALS)) {
            return link.getValue().equals(response);
        } 
        
        if (link.getConditionType().equals(QuestionGroupLink.CONDITION_TYPE_NOT_EQUALS)) {
            return !link.getValue().equals(response);
        } 
        
        if (link.getConditionType().equals(QuestionGroupLink.CONDITION_TYPE_GREATER)) {
            return Integer.parseInt(response) > Integer.parseInt(link.getValue());    
        } 
        
        if (link.getConditionType().equals(QuestionGroupLink.CONDITION_TYPE_SMALLER)) {
            return Integer.parseInt(response) < Integer.parseInt(link.getValue());
        } 
        
        if (link.getConditionType().equals(QuestionGroupLink.CONDITION_TYPE_AFTER)) {
            return new SimpleDateFormat("dd/MM/yyyy").parse(response)
                    .after(new SimpleDateFormat("dd/MM/yyyy").parse(link.getValue()));
        } 
        
        if (link.getConditionType().equals(QuestionGroupLink.CONDITION_TYPE_BEFORE)) {
            return new SimpleDateFormat("dd/MM/yyyy").parse(response)
                    .before(new SimpleDateFormat("dd/MM/yyyy").parse(link.getValue()));
        } 
        
        if (link.getConditionType().equals(QuestionGroupLink.CONDITION_TYPE_DATE_RANGE)) {
            Date responseDate = new SimpleDateFormat("dd/MM/yyyy").parse(response);
            Date firstDate = new SimpleDateFormat("dd/MM/yyyy").parse(link.getValue());
            Date secondDate = new SimpleDateFormat("dd/MM/yyyy").parse(link.getAdditionalValue());
            return (responseDate.after(firstDate) && responseDate.before(secondDate)) || (responseDate.before(firstDate) && responseDate.after(secondDate));
        }
        
        if (link.getConditionType().equals(QuestionGroupLink.CONDITION_TYPE_RANGE)) {
            int responseInt = Integer.parseInt(response);
            return (responseInt >= Integer.parseInt(link.getValue()) && 
                    responseInt <= Integer.parseInt(link.getAdditionalValue())) || (responseInt <= Integer.parseInt(link.getValue()) && 
                            responseInt >= Integer.parseInt(link.getAdditionalValue()));
        }
        
        return false;
    }
    
    public void createQuestionLinks (List<QuestionLinkDetail> questionLinks){
        for(QuestionLinkDetail questionLinkDetail : questionLinks){
            QuestionGroupLink questionGroupLink = questionnaireMapper.mapToQuestionGroupLink(questionLinkDetail, null);
            questionGroupLinkDao.saveOrUpdate(questionGroupLink);
            
            SectionQuestionLink sectionQuestionLink = questionnaireMapper.mapToQuestionLink(questionLinkDetail, questionGroupLink);
            sectionQuestionLinkDao.saveOrUpdate(sectionQuestionLink);
        }
    }
    
    public void createSectionLinks(List<SectionLinkDetail> sectionLinks){
        for(SectionLinkDetail sectionLinkDetail : sectionLinks){
            QuestionGroupLink questionGroupLink = questionnaireMapper.mapToQuestionGroupLink(null, sectionLinkDetail);
            questionGroupLinkDao.saveOrUpdate(questionGroupLink);
            
            SectionLink sectionLink = questionnaireMapper.mapToSectionLink(sectionLinkDetail, questionGroupLink);
            sectionLinkDao.saveOrUpdate(sectionLink);
            }
    }
}

