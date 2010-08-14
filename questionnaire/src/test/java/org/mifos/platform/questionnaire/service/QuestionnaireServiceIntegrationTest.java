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

package org.mifos.platform.questionnaire.service;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.domain.AnswerType;
import org.mifos.platform.questionnaire.domain.ChoiceTagEntity;
import org.mifos.platform.questionnaire.domain.EventSourceEntity;
import org.mifos.platform.questionnaire.domain.QuestionChoiceEntity;
import org.mifos.platform.questionnaire.domain.QuestionEntity;
import org.mifos.platform.questionnaire.domain.QuestionGroup;
import org.mifos.platform.questionnaire.domain.QuestionGroupInstance;
import org.mifos.platform.questionnaire.domain.QuestionGroupResponse;
import org.mifos.platform.questionnaire.domain.QuestionGroupState;
import org.mifos.platform.questionnaire.domain.QuestionState;
import org.mifos.platform.questionnaire.domain.QuestionnaireService;
import org.mifos.platform.questionnaire.domain.Section;
import org.mifos.platform.questionnaire.matchers.EventSourcesMatcher;
import org.mifos.platform.questionnaire.matchers.QuestionChoicesMatcher;
import org.mifos.platform.questionnaire.matchers.QuestionGroupDetailMatcher;
import org.mifos.platform.questionnaire.persistence.QuestionDao;
import org.mifos.platform.questionnaire.persistence.QuestionGroupDao;
import org.mifos.platform.questionnaire.persistence.QuestionGroupInstanceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/test-questionnaire-dbContext.xml", "/test-questionnaire-persistenceContext.xml", "/META-INF/spring/QuestionnaireContext.xml"})
@TransactionConfiguration(transactionManager = "platformTransactionManager", defaultRollback = true)
public class QuestionnaireServiceIntegrationTest {

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private QuestionGroupDao questionGroupDao;

    @Autowired
    private QuestionGroupInstanceDao questionGroupInstanceDao;

    public static final String TITLE = "Title";

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldDefineQuestion() throws SystemException {
        String questionTitle = TITLE + currentTimeMillis();
        QuestionDetail questionDetail = defineQuestion(questionTitle, QuestionType.DATE);
        assertNotNull(questionDetail);
        Integer questionId = questionDetail.getId();
        assertNotNull(questionId);
        QuestionEntity questionEntity = questionDao.getDetails(questionId);
        assertNotNull(questionEntity);
        Assert.assertEquals(questionTitle, questionEntity.getShortName());
        Assert.assertEquals(questionTitle, questionEntity.getQuestionText());
        Assert.assertEquals(AnswerType.DATE, questionEntity.getAnswerTypeAsEnum());
        Assert.assertEquals(questionDetail.getAnswerChoices(), asList());
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldDefineMultiSelectQuestion() throws SystemException {
        String questionTitle = TITLE + currentTimeMillis();
        QuestionDetail questionDetail = defineQuestion(questionTitle, QuestionType.MULTI_SELECT, asList("choice1", "choice2"));
        Assert.assertNotNull(questionDetail);
        Integer questionId = questionDetail.getId();
        assertNotNull(questionId);
        QuestionEntity questionEntity = questionDao.getDetails(questionId);
        assertNotNull(questionEntity);
        Assert.assertEquals(questionTitle, questionEntity.getShortName());
        Assert.assertEquals(questionTitle, questionEntity.getQuestionText());
        Assert.assertEquals(AnswerType.MULTISELECT, questionEntity.getAnswerTypeAsEnum());
        assertThat(questionEntity.getChoices(), new QuestionChoicesMatcher(asList(new QuestionChoiceEntity("choice1"), new QuestionChoiceEntity("choice2"))));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldDefineSingleSelectQuestion() throws SystemException {
        String questionTitle = TITLE + currentTimeMillis();
        QuestionDetail questionDetail = defineQuestion(questionTitle, QuestionType.SINGLE_SELECT, asList("choice1", "choice2"));
        assertNotNull(questionDetail);
        Integer questionId = questionDetail.getId();
        assertNotNull(questionId);
        QuestionEntity questionEntity = questionDao.getDetails(questionId);
        assertNotNull(questionEntity);
        Assert.assertEquals(questionTitle, questionEntity.getShortName());
        Assert.assertEquals(questionTitle, questionEntity.getQuestionText());
        Assert.assertEquals(AnswerType.SINGLESELECT, questionEntity.getAnswerTypeAsEnum());
        assertThat(questionEntity.getChoices(), new QuestionChoicesMatcher(asList(new QuestionChoiceEntity("choice1"), new QuestionChoiceEntity("choice2"))));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldDefineQuestionGroup() throws SystemException {
        String title = TITLE + currentTimeMillis();
        QuestionDetail questionDetail1 = defineQuestion(title + 1, QuestionType.NUMERIC);
        QuestionDetail questionDetail2 = defineQuestion(title + 2, QuestionType.FREETEXT);
        SectionDetail section1 = getSectionWithQuestionId("S1", questionDetail1.getId());
        SectionDetail section2 = getSectionWithQuestionId("S2", questionDetail2.getId());
        QuestionGroupDetail questionGroupDetail = defineQuestionGroup(title, "Create", "Client", asList(section1, section2), true);
        assertNotNull(questionGroupDetail);
        Integer questionGroupId = questionGroupDetail.getId();
        assertNotNull(questionGroupId);
        QuestionGroup questionGroup = questionGroupDao.getDetails(questionGroupId);
        assertNotNull(questionGroup);
        Assert.assertEquals(title, questionGroup.getTitle());
        Assert.assertEquals(QuestionGroupState.ACTIVE, questionGroup.getState());
        Assert.assertEquals(true, questionGroup.isEditable());
        List<Section> sections = questionGroup.getSections();
        Assert.assertEquals(2, sections.size());
        Assert.assertEquals("S1", sections.get(0).getName());
        Assert.assertEquals(title + 1, sections.get(0).getQuestions().get(0).getQuestion().getShortName());
        Assert.assertEquals("S2", sections.get(1).getName());
        Assert.assertEquals(title + 2, sections.get(1).getQuestions().get(0).getQuestion().getShortName());
        verifyCreationDate(questionGroup);
        verifyEventSources(questionGroup);
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldGetAllQuestions() throws SystemException {
        int initialCountOfQuestions = questionnaireService.getAllActiveQuestions().size();
        QuestionDetail questionDetail2 = defineQuestion("Q2" + currentTimeMillis(), QuestionType.FREETEXT);
        QuestionDetail questionDetail1 = defineQuestion("Q1" + currentTimeMillis(), QuestionType.NUMERIC);
        List<String> expectedOrderTitles = asList(questionDetail1.getShortName(), questionDetail2.getShortName());
        List<Integer> expectedOrderIds = asList(questionDetail1.getId(), questionDetail2.getId());
        List<QuestionDetail> questionDetails = questionnaireService.getAllActiveQuestions();
        int finalCountOfQuestions = questionDetails.size();
        assertThat(finalCountOfQuestions - initialCountOfQuestions, is(2));
        List<QuestionDetail> actualQuestions = new ArrayList<QuestionDetail>();
        for (QuestionDetail questionDetail : questionDetails) {
            if (expectedOrderIds.contains(questionDetail.getId()))
                actualQuestions.add(questionDetail);
        }
        assertThat(actualQuestions.get(0).getShortName(), is(expectedOrderTitles.get(0)));
        assertThat(actualQuestions.get(1).getShortName(), is(expectedOrderTitles.get(1)));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldGetAllQuestionGroups() throws SystemException {
        int initialCount = questionnaireService.getAllQuestionGroups().size();
        String questionGroupTitle1 = "QG1" + currentTimeMillis();
        String questionGroupTitle2 = "QG2" + currentTimeMillis();
        List<SectionDetail> sectionsForQG1 = asList(getSection("Section1"));
        defineQuestionGroup(questionGroupTitle1, "Create", "Client", sectionsForQG1, false);
        List<SectionDetail> sectionsForQG2 = asList(getSection("S2"), getSection("Section2"));
        defineQuestionGroup(questionGroupTitle2, "Create", "Client", sectionsForQG2, false);
        List<QuestionGroupDetail> questionGroups = questionnaireService.getAllQuestionGroups();
        int finalCount = questionGroups.size();
        assertThat(finalCount - initialCount, is(2));
        assertThat(questionGroups, Matchers.hasItems(getQuestionGroupDetailMatcher(questionGroupTitle1, sectionsForQG1),
                getQuestionGroupDetailMatcher(questionGroupTitle2, sectionsForQG2)));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldGetQuestionGroupById() throws SystemException {
        String title = "QG1" + currentTimeMillis();
        List<SectionDetail> details = asList(getSection("S1"), getSection("S2"));
        QuestionGroupDetail createdQuestionGroupDetail = defineQuestionGroup(title, "Create", "Client", details, true);
        QuestionGroupDetail retrievedQuestionGroupDetail = questionnaireService.getQuestionGroup(createdQuestionGroupDetail.getId());
        Assert.assertNotSame(createdQuestionGroupDetail, retrievedQuestionGroupDetail);
        assertThat(retrievedQuestionGroupDetail.getTitle(), is(title));
        List<SectionDetail> sectionDetails = retrievedQuestionGroupDetail.getSectionDetails();
        assertThat(sectionDetails, is(notNullValue()));
        assertThat(sectionDetails.size(), is(2));
        List<SectionDetail> sectionDetailList = retrievedQuestionGroupDetail.getSectionDetails();
        assertThat(sectionDetailList.get(0).getName(), is("S1"));
        assertThat(sectionDetailList.get(1).getName(), is("S2"));
        EventSource eventSource = retrievedQuestionGroupDetail.getEventSource();
        assertThat(eventSource, is(notNullValue()));
        assertThat(eventSource.getEvent(), is("Create"));
        assertThat(eventSource.getSource(), is("Client"));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldGetQuestionGroupByIdOrdersQuestionsWithinEverySection() throws SystemException {
        String qgTitle = "QG1" + currentTimeMillis();

        SectionDetail sectionDefinition1 = new SectionDetail();
        sectionDefinition1.setName("Section1");
        String section1Question1 = "Q2_" + currentTimeMillis();
        sectionDefinition1.addQuestion(new SectionQuestionDetail(defineQuestion(section1Question1, QuestionType.FREETEXT), false));
        String section1Question2 = "Q1_" + currentTimeMillis();
        sectionDefinition1.addQuestion(new SectionQuestionDetail(defineQuestion(section1Question2, QuestionType.NUMERIC), true));
        String section1Question3 = "Q3_" + currentTimeMillis();
        sectionDefinition1.addQuestion(new SectionQuestionDetail(defineQuestion(section1Question3, QuestionType.DATE), true));

        SectionDetail sectionDefinition2 = new SectionDetail();
        sectionDefinition2.setName("Section2");
        String section2Question1 = "S2_" + currentTimeMillis();
        sectionDefinition2.addQuestion(new SectionQuestionDetail(defineQuestion(section2Question1, QuestionType.FREETEXT), false));
        String section2Question2 = "S3_" + currentTimeMillis();
        sectionDefinition2.addQuestion(new SectionQuestionDetail(defineQuestion(section2Question2, QuestionType.DATE), true));
        String section2Question3 = "S1_" + currentTimeMillis();
        sectionDefinition2.addQuestion(new SectionQuestionDetail(defineQuestion(section2Question3, QuestionType.NUMERIC), true));

        int questionGroupId = defineQuestionGroup(qgTitle, "Create", "Client", asList(sectionDefinition1, sectionDefinition2), false).getId();
        QuestionGroupDetail questionGroupDetail = questionnaireService.getQuestionGroup(questionGroupId);
        assertThat(questionGroupDetail, Matchers.notNullValue());
        List<SectionDetail> sectionDetails = questionGroupDetail.getSectionDetails();
        assertThat(sectionDetails, Matchers.notNullValue());
        assertThat(sectionDetails.size(), is(2));

        SectionDetail section1 = sectionDetails.get(0);
        assertThat(section1.getName(), is("Section1"));
        List<SectionQuestionDetail> questions1 = section1.getQuestions();
        assertThat(questions1, Matchers.notNullValue());
        assertThat(questions1.size(), is(3));
        assertThat(questions1.get(0).getTitle(), is(section1Question1));
        assertThat(questions1.get(1).getTitle(), is(section1Question2));
        assertThat(questions1.get(2).getTitle(), is(section1Question3));

        SectionDetail section2 = sectionDetails.get(1);
        assertThat(section2.getName(), is("Section2"));
        List<SectionQuestionDetail> questions2 = section2.getQuestions();
        assertThat(questions2, Matchers.notNullValue());
        assertThat(questions2.size(), is(3));
        assertThat(questions2.get(0).getTitle(), is(section2Question1));
        assertThat(questions2.get(1).getTitle(), is(section2Question2));
        assertThat(questions2.get(2).getTitle(), is(section2Question3));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void testGetQuestionGroupByIdFailure() throws SystemException {
        String title = "QG1" + currentTimeMillis();
        QuestionGroupDetail createdQuestionGroupDetail = defineQuestionGroup(title, "Create", "Client", asList(getSection("S1")), true);
        Integer maxQuestionGroupId = createdQuestionGroupDetail.getId();
        try {
            questionnaireService.getQuestionGroup(maxQuestionGroupId + 1);
        } catch (SystemException e) {
            assertThat(e.getKey(), is(QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND));
        }
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldGetQuestionById() throws SystemException {
        String title = "Q1" + currentTimeMillis();
        QuestionDetail createdQuestionDetail = defineQuestion(title, QuestionType.FREETEXT);
        QuestionDetail retrievedQuestionDetail = questionnaireService.getQuestion(createdQuestionDetail.getId());
        Assert.assertNotSame(createdQuestionDetail, retrievedQuestionDetail);
        assertThat(retrievedQuestionDetail.getText(), is(title));
        assertThat(retrievedQuestionDetail.getShortName(), is(title));
        assertThat(retrievedQuestionDetail.getType(), is(QuestionType.FREETEXT));
    }

    public void shouldGetQuestionWithAnswerChoicesById() throws SystemException {
        String title = "Q1" + currentTimeMillis();
        QuestionDetail createdQuestionDetail = defineQuestion(title, QuestionType.MULTI_SELECT, asList("choice1", "choice2"));
        QuestionDetail retrievedQuestionDetail = questionnaireService.getQuestion(createdQuestionDetail.getId());
        Assert.assertNotSame(createdQuestionDetail, retrievedQuestionDetail);
        assertThat(retrievedQuestionDetail.getText(), is(title));
        assertThat(retrievedQuestionDetail.getShortName(), is(title));
        assertThat(retrievedQuestionDetail.getType(), is(QuestionType.MULTI_SELECT));
        Assert.assertEquals(retrievedQuestionDetail.getAnswerChoices(), asList("choice1", "choice2"));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void testGetQuestionByIdFailure() throws SystemException {
        String title = "Q1" + currentTimeMillis();
        QuestionDetail createdQuestionDetail = defineQuestion(title, QuestionType.DATE);
        Integer maxQuestionId = createdQuestionDetail.getId();
        try {
            questionnaireService.getQuestion(maxQuestionId + 1);
        } catch (SystemException e) {
            assertThat(e.getKey(), is(QuestionnaireConstants.QUESTION_NOT_FOUND));
        }
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldThrowExceptionForDuplicateQuestion() throws SystemException {
        long offset = currentTimeMillis();
        String questionTitle = TITLE + offset;
        defineQuestion(questionTitle, QuestionType.DATE);
        try {
            defineQuestion(questionTitle, QuestionType.FREETEXT);
            Assert.fail("Exception should have been thrown for duplicate question title");
        } catch (SystemException e) {
            Assert.assertEquals(QuestionnaireConstants.DUPLICATE_QUESTION, e.getKey());
        }
    }

    @Test
    @Transactional
    public void testIsDuplicateQuestion() throws SystemException {
        String questionTitle = TITLE + currentTimeMillis();
        boolean result = questionnaireService.isDuplicateQuestionTitle(questionTitle);
        assertThat(result, is(false));
        defineQuestion(questionTitle, QuestionType.DATE);
        result = questionnaireService.isDuplicateQuestionTitle(questionTitle);
        assertThat(result, is(true));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldRetrieveAllEventSources() {
        List<EventSource> eventSources = questionnaireService.getAllEventSources();
        assertNotNull(eventSources);
        assertThat(eventSources, new EventSourcesMatcher(
                asList(new EventSource("Create", "Client", "Create Client"),
                        new EventSource("View", "Client", "View Client"),
                        new EventSource("Create", "Group", "Create Group"),
                        new EventSource("Approve", "Loan", "Approve Loan"),
                        new EventSource("Close", "Client", "Close Client"),
                        new EventSource("Create", "Loan", "Create Loan"))));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldGetQuestionGroupsByEventAndSource() throws SystemException {
        String title = "QG1" + currentTimeMillis();
        List<SectionDetail> details = asList(getSection("S1"), getSection("S2"));
        QuestionGroupDetail expectedQGDetail = defineQuestionGroup(title, "Create", "Client", details, false);
        List<QuestionGroupDetail> questionGroups = questionnaireService.getQuestionGroups(new EventSource("Create", "Client", "Create.Client"));
        assertThat(questionGroups, is(notNullValue()));
        QuestionGroupDetail actualQGDetail = getMatchingQGDetailById(expectedQGDetail.getId(), questionGroups);
        assertThat(actualQGDetail, is(notNullValue()));
        assertThat(actualQGDetail.getTitle(), is(title));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldSaveResponse() throws SystemException {
        String title = "QG1" + currentTimeMillis();
        List<SectionDetail> details = asList(getSection("S1"), getSection("S2"));
        QuestionGroupDetail questionGroupDetail = defineQuestionGroup(title, "Create", "Client", details, true);
        questionGroupDetail.getSectionDetail(0).getQuestionDetail(0).setValue("1");
        questionGroupDetail.getSectionDetail(1).getQuestionDetail(0).setValue("2");
        questionnaireService.saveResponses(new QuestionGroupDetails(1, 1, asList(questionGroupDetail)));
        List<QuestionGroupInstance> instances = questionGroupInstanceDao.getDetailsAll();
        assertThat(instances.size(), is(1));
        QuestionGroupInstance instance = getMatchingQuestionGroupInstance(questionGroupDetail.getId(), 1, instances, 0);
        assertThat(instance, is(notNullValue()));
        List<QuestionGroupResponse> groupResponses = instance.getQuestionGroupResponses();
        Assert.assertEquals(groupResponses.get(0).getResponse(), "1");
        Assert.assertEquals(groupResponses.get(1).getResponse(), "2");

        questionGroupDetail.getSectionDetail(1).getQuestionDetail(0).setValue("3");
        questionnaireService.saveResponses(new QuestionGroupDetails(1, 1, asList(questionGroupDetail)));
        instances = questionGroupInstanceDao.getDetailsAll();
        assertThat(instances.size(), is(2));
        instance = getMatchingQuestionGroupInstance(questionGroupDetail.getId(), 1, instances, 1);
        assertThat(instance, is(notNullValue()));
        groupResponses = instance.getQuestionGroupResponses();
        Assert.assertEquals(groupResponses.get(0).getResponse(), "1");
        Assert.assertEquals(groupResponses.get(1).getResponse(), "3");
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldNotGetInActiveQuestionGroupsByEventAndSource() throws SystemException {
        String title = "QG1" + currentTimeMillis();
        List<SectionDetail> details = asList(getSection("S1"), getSection("S2"));
        QuestionGroupDetail expectedQGDetail = defineQuestionGroup(title, "Create", "Client", details, true);
        setState(expectedQGDetail.getId(), QuestionGroupState.INACTIVE);
        List<QuestionGroupDetail> questionGroups = questionnaireService.getQuestionGroups(new EventSource("Create", "Client", "Create.Client"));
        assertThat(questionGroups, is(notNullValue()));
        QuestionGroupDetail actualQGDetail = getMatchingQGDetailById(expectedQGDetail.getId(), questionGroups);
        assertThat(actualQGDetail, is(Matchers.nullValue()));
    }
    
    @Test
    public void shouldSaveQuestionWithSmartSelectOptionType() {
        String quesTitle = "Ques" + currentTimeMillis();
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setAnswerType(AnswerType.SMARTSELECT);
        questionEntity.setQuestionState(QuestionState.ACTIVE);
        questionEntity.setQuestionText(quesTitle);
        questionEntity.setShortName(quesTitle);
        questionEntity.setChoices(asList(getChoice("Choice1", "Tag1", "Tag2"), getChoice("Choice2", "Tag4")));
        Integer quesId = questionDao.create(questionEntity);
        QuestionEntity newQuestionEntity = questionDao.getDetails(quesId);
        assertThat(newQuestionEntity, is(notNullValue()));
        assertThat(newQuestionEntity.getAnswerTypeAsEnum(), is(AnswerType.SMARTSELECT));
        assertThat(newQuestionEntity.getChoices(), is(notNullValue()));
        assertThat(newQuestionEntity.getChoices().size(), is(2));
        assertThat(newQuestionEntity.getChoices().get(0).getChoiceText(), is("Choice1"));
        assertThat(newQuestionEntity.getChoices().get(0).getTags(), is(notNullValue()));
        assertThat(newQuestionEntity.getChoices().get(0).getTags().size(), is(2));

        assertThat(newQuestionEntity.getChoices().get(1).getChoiceText(), is("Choice2"));
        assertThat(newQuestionEntity.getChoices().get(1).getTags(), is(notNullValue()));
        assertThat(newQuestionEntity.getChoices().get(1).getTags().size(), is(1));
    }

    private QuestionChoiceEntity getChoice(String choiceText, String... tagTexts) {
        QuestionChoiceEntity questionChoiceEntity = new QuestionChoiceEntity();
        questionChoiceEntity.setChoiceText(choiceText);
        Set<ChoiceTagEntity> tags = new HashSet<ChoiceTagEntity>();
        for (String tagText : tagTexts) {
            tags.add(getTag(tagText));
        }
        questionChoiceEntity.setTags(tags);
        return questionChoiceEntity;
    }

    private ChoiceTagEntity getTag(String tagText) {
        ChoiceTagEntity choiceTagEntity = new ChoiceTagEntity();
        choiceTagEntity.setTagText(tagText);
        return choiceTagEntity;
    }

    private QuestionGroupInstance getMatchingQuestionGroupInstance(Integer questionGroupId, Integer entityId, List<QuestionGroupInstance> instances, int version) {
        for (QuestionGroupInstance questionGroupInstance : instances) {
            if (questionGroupInstance.getQuestionGroup().getId() == questionGroupId &&
                    questionGroupInstance.getEntityId() == entityId && questionGroupInstance.getVersionNum() == version)
                return questionGroupInstance;
        }
        return null;
    }

    private void setState(Integer id, QuestionGroupState questionGroupState) {
        QuestionGroup questionGroup = questionGroupDao.getDetails(id);
        questionGroup.setState(questionGroupState);
        questionGroupDao.update(questionGroup);
    }

    private QuestionDetail defineQuestion(String questionTitle, QuestionType questionType) throws SystemException {
        return questionnaireService.defineQuestion(new QuestionDetail(questionTitle, questionType));
    }

    private QuestionDetail defineQuestion(String questionTitle, QuestionType type, List<String> choices) throws SystemException {
        QuestionDetail questionDetail = new QuestionDetail(questionTitle, type);
        List<ChoiceDetail> choiceDetails = getChoiceDetails(choices);
        questionDetail.setAnswerChoices(choiceDetails);
        return questionnaireService.defineQuestion(questionDetail);
    }

    private List<ChoiceDetail> getChoiceDetails(List<String> choices) {
        List<ChoiceDetail> choiceDetails = new ArrayList<ChoiceDetail>();
        for (String choice : choices) {
            choiceDetails.add(new ChoiceDetail(choice));
        }
        return choiceDetails;
    }

    private QuestionGroupDetail defineQuestionGroup(String title, String event, String source, List<SectionDetail> sectionDetails, boolean editable) throws SystemException {
        return questionnaireService.defineQuestionGroup(new QuestionGroupDetail(0, title, new EventSource(event, source, null), sectionDetails, editable));
    }

    private SectionDetail getSection(String name) throws SystemException {
        SectionDetail section = new SectionDetail();
        section.setName(name);
        String questionTitle = "Question" + name + currentTimeMillis();
        QuestionDetail questionDetail = defineQuestion(questionTitle, QuestionType.NUMERIC);
        section.addQuestion(new SectionQuestionDetail(questionDetail, true));
        return section;
    }

    private SectionDetail getSectionWithQuestionId(String name, int questionId) throws SystemException {
        SectionDetail section = new SectionDetail();
        section.setName(name);
        section.addQuestion(new SectionQuestionDetail(new QuestionDetail(questionId, null, null, QuestionType.INVALID, true), true));
        return section;
    }

    private void verifyCreationDate(QuestionGroup questionGroup) {
        Calendar creationDate = Calendar.getInstance();
        creationDate.setTime(questionGroup.getDateOfCreation());
        Calendar currentDate = Calendar.getInstance();
        assertThat(creationDate.get(Calendar.DATE), is(currentDate.get(Calendar.DATE)));
        assertThat(creationDate.get(Calendar.MONTH), is(currentDate.get(Calendar.MONTH)));
        assertThat(creationDate.get(Calendar.YEAR), is(currentDate.get(Calendar.YEAR)));
    }

    private void verifyEventSources(QuestionGroup questionGroup) {
        Set<EventSourceEntity> eventSources = questionGroup.getEventSources();
        assertNotNull(eventSources);
        Assert.assertEquals(1, eventSources.size());
        EventSourceEntity eventSourceEntity = eventSources.toArray(new EventSourceEntity[eventSources.size()])[0];
        Assert.assertEquals("Create", eventSourceEntity.getEvent().getName());
        Assert.assertEquals("Client", eventSourceEntity.getSource().getEntityType());
        Assert.assertEquals("Create Client", eventSourceEntity.getDescription());
    }

    private QuestionGroupDetailMatcher getQuestionGroupDetailMatcher(String questionGroupTitle, List<SectionDetail> sectionDetails) {
        return new QuestionGroupDetailMatcher(new QuestionGroupDetail(0, questionGroupTitle, sectionDetails));
    }

    private QuestionGroupDetail getMatchingQGDetailById(Integer expectedId, List<QuestionGroupDetail> questionGroups) {
        QuestionGroupDetail actualQGDetail = null;
        for (QuestionGroupDetail questionGroupDetail : questionGroups) {
            if (questionGroupDetail.getId().equals(expectedId)) {
                actualQGDetail = questionGroupDetail;
            }
        }
        return actualQGDetail;
    }
}
