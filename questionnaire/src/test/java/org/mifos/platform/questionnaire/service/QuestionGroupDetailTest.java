package org.mifos.platform.questionnaire.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class QuestionGroupDetailTest {
    @Test
    public void shouldReturnTrueIfNoActiveSectionsAndQuestions() {
        QuestionGroupDetail questionGroupDetail;
        questionGroupDetail = new QuestionGroupDetail(10, "QuestionGroup1", asList(getSection(false), getSection(true)));
        assertThat(questionGroupDetail.hasNoActiveSectionsAndQuestions(), is(false));
        questionGroupDetail = new QuestionGroupDetail(10, "QuestionGroup1", asList(getSection(false), getSection(false)));
        assertThat(questionGroupDetail.hasNoActiveSectionsAndQuestions(), is(true));
    }

    private SectionDetail getSection(boolean active) {
        SectionQuestionDetail sectionQuestionDetail = new SectionQuestionDetail(getQuestionDetail(active), true);
        SectionDetail sectionDetail = new SectionDetail();
        sectionDetail.addQuestion(sectionQuestionDetail);
        return sectionDetail;
    }

    private QuestionDetail getQuestionDetail(boolean active) {
        QuestionDetail questionDetail = new QuestionDetail("title", QuestionType.SINGLE_SELECT);
        questionDetail.setActive(active);
        return questionDetail;
    }
}
