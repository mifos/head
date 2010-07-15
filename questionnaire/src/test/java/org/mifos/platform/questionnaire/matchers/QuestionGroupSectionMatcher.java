package org.mifos.platform.questionnaire.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.mifos.platform.questionnaire.service.SectionDetail;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.junit.Assert.assertThat;


public class QuestionGroupSectionMatcher extends TypeSafeMatcher<SectionDetail> {
    private SectionDetail sectionDetail;

    public QuestionGroupSectionMatcher(SectionDetail sectionDetail) {
        this.sectionDetail = sectionDetail;
    }

    @Override
    public boolean matchesSafely(SectionDetail sectionDetail) {
        boolean sameTitle = equalsIgnoreCase(this.sectionDetail.getName(), sectionDetail.getName());
        if (sameTitle && this.sectionDetail.getQuestions().size() == sectionDetail.getQuestions().size()) {
            for (SectionQuestionDetail questionDetail : this.sectionDetail.getQuestions()) {
                assertThat(sectionDetail.getQuestions(), Matchers.hasItem(new SectionQuestionDetailMatcher(questionDetail)));
            }
        }
        return sameTitle;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("QuestionGroup sections do not match");
    }

}

class SectionQuestionDetailMatcher extends TypeSafeMatcher<SectionQuestionDetail> {
    private SectionQuestionDetail sectionQuestionDetail;

    public SectionQuestionDetailMatcher(SectionQuestionDetail sectionQuestionDetail) {
        this.sectionQuestionDetail = sectionQuestionDetail;
    }

    @Override
    public boolean matchesSafely(SectionQuestionDetail sectionQuestionDetail) {
        return this.sectionQuestionDetail.getQuestionId() == sectionQuestionDetail.getQuestionId()
                && equalsIgnoreCase(this.sectionQuestionDetail.getTitle(), sectionQuestionDetail.getTitle());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("SectionQuestionDetail do not match");
    }
}