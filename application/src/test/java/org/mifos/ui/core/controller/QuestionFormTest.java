package org.mifos.ui.core.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class QuestionFormTest {
    private static final String TITLE = "title";

    @Test
    public void testDuplicateQuestionInForm(){
        QuestionForm questionForm = new QuestionForm();
        questionForm.setQuestions(asList(getQuestion(TITLE), getQuestion(TITLE + 1), getQuestion(TITLE + 2)));
        assertThat(questionForm.isDuplicateTitle(TITLE), is(true));
        assertThat(questionForm.isDuplicateTitle(" " + TITLE), is(true));
        assertThat(questionForm.isDuplicateTitle(TITLE.toUpperCase()), is(true));
        assertThat(questionForm.isDuplicateTitle(null), is(false));
        assertThat(questionForm.isDuplicateTitle(TITLE + "2"), is(true));
        assertThat(questionForm.isDuplicateTitle(TITLE + 3), is(false));
    }

    private Question getQuestion(String title) {
        Question question = new Question();
        question.setTitle(title);
        return question;
    }

    @Test
    public void testAddCurrentQuestion(){
        QuestionForm questionForm = new QuestionForm();
        questionForm.setTitle("Q1");
        questionForm.addCurrentQuestion();
        List<Question> questionList = questionForm.getQuestions();
        assertThat(questionList.size(), is(1));
        assertNotSame(questionList.get(0).getTitle(), questionForm.getTitle());
    }
}
