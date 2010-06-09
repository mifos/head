package org.mifos.platform.questionnaire;

import org.mifos.customers.surveys.business.Question;
import org.mifos.customers.surveys.helpers.AnswerType;
import org.mifos.customers.surveys.helpers.QuestionState;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.platform.questionnaire.persistence.QuestionnaireDao;
import org.springframework.beans.factory.annotation.Autowired;

public class QuestionnaireServiceImpl implements QuestionnaireService {

    @Autowired
    private QuestionValidator questionValidator;

    @Autowired
    private QuestionnaireDao questionnaireDao;

    public QuestionnaireServiceImpl() {
    }

    public QuestionnaireServiceImpl(QuestionValidator questionValidator, QuestionnaireDao questionnaireDao){
        this.questionValidator = questionValidator;
        this.questionnaireDao = questionnaireDao;
    }

    @Override
    public QuestionResponse defineQuestion(QuestionRequest questionRequest) throws ApplicationException {
        questionValidator.validate(questionRequest);
        Question question = mapToQuestion(questionRequest);
        Integer questionId = questionnaireDao.create(question);
        return new QuestionResponse(questionId);
    }

    private Question mapToQuestion(QuestionRequest questionRequest) {
        Question question = new Question();
        question.setShortName(questionRequest.getTitle());
        question.setQuestionText(questionRequest.getTitle());
        question.setAnswerType(AnswerType.FREETEXT);
        question.setQuestionState(QuestionState.ACTIVE);
        return question;
    }
}
