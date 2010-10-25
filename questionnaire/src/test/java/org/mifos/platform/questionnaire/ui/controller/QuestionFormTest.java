package org.mifos.platform.questionnaire.ui.controller;

import org.junit.Test;
import org.mifos.platform.questionnaire.service.dtos.ChoiceDto;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.ui.model.Question;
import org.mifos.platform.questionnaire.ui.model.QuestionForm;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;
@SuppressWarnings("PMD")
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
        QuestionForm questionForm = getQuestionForm("  Q1 ", "Free Text", "choice1");
        List<Question> questionList = questionForm.getQuestions();
        assertThat(questionList.size(), is(1));
        String text = questionList.get(0).getText();
        String type = questionList.get(0).getType();
        List<ChoiceDto> choices = questionList.get(0).getChoices();
        assertNotSame(text, questionForm.getCurrentQuestion().getText());
        assertNotSame(type, questionForm.getCurrentQuestion().getType());
        assertThat(text, is("Q1"));
        assertThat(type, is("Free Text"));
        assertThat(choices.size(), is(0));
    }

    @Test
    public void testAddCurrentMultiSelectQuestion(){
        QuestionForm questionForm = getQuestionForm("title", "Multi Select", "choice2");
        List<Question> questionList = questionForm.getQuestions();
        assertThat(questionList.size(), is(1));
        String text = questionList.get(0).getText();
        String type = questionList.get(0).getType();
        List<ChoiceDto> choices = questionList.get(0).getChoices();
        assertNotSame(type, questionForm.getCurrentQuestion().getType());
        assertNotSame(text, questionForm.getCurrentQuestion().getText());
        assertThat(type, is("Multi Select"));
        assertThat(text, is("title"));
        assertThat(choices.get(0).getValue(), is("choice2"));
    }

    @Test
    public void testAddCurrentSingleSelectQuestion(){
        QuestionForm questionForm = getQuestionForm("title1", "Single Select", "choice");
        List<Question> questionList = questionForm.getQuestions();
        assertThat(questionList.size(), is(1));
        String text = questionList.get(0).getText();
        String type = questionList.get(0).getType();
        List<ChoiceDto> choices = questionList.get(0).getChoices();
        assertNotSame(text, questionForm.getCurrentQuestion().getText());
        assertNotSame(type, questionForm.getCurrentQuestion().getType());
        assertThat(choices.get(0).getValue(), is("choice"));
        assertThat(text, is("title1"));
        assertThat(type, is("Single Select"));
    }

    private Question getQuestion(String title) {
        Question question = new Question(new QuestionDetail());
        question.setText(title);
        return question;
    }

    private QuestionForm getQuestionForm(String title, String type, String choice) {
        QuestionForm questionForm = new QuestionForm();
        questionForm.getCurrentQuestion().setText(title);
        questionForm.getCurrentQuestion().setType(type);
        questionForm.getCurrentQuestion().setCurrentChoice(choice);
        questionForm.getCurrentQuestion().addAnswerChoice();
        questionForm.addCurrentQuestion();
        return questionForm;
    }
}
