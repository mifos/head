package org.mifos.ui.core.controller;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;

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

    @Test
    public void testAddCurrentQuestion(){
        QuestionForm questionForm = new QuestionForm();
        questionForm.setTitle("  Q1 ");
        questionForm.setType("Free text");
        questionForm.addCurrentQuestion();
        List<Question> questionList = questionForm.getQuestions();
        assertThat(questionList.size(), is(1));
        String title = questionList.get(0).getTitle();
        String type = questionList.get(0).getType();
        assertNotSame(title, questionForm.getTitle());
        assertNotSame(type, questionForm.getType());
        assertThat(title, is("Q1"));
        assertThat(type, is("Free text"));
    }

    private Question getQuestion(String title) {
        Question question = new Question();
        question.setTitle(title);
        return question;
    }
}
