package org.mifos.platform.questionnaire;

import org.mifos.customers.surveys.business.Question;
import org.mifos.customers.surveys.helpers.AnswerType;
import org.mifos.customers.surveys.helpers.QuestionState;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.platform.questionnaire.persistence.QuestionnaireDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class QuestionnaireServiceImpl implements QuestionnaireService {

    @Autowired
    private QuestionValidator questionValidator;

    @Autowired
    private QuestionnaireDao questionnaireDao;

    public QuestionnaireServiceImpl() {
    }

    public QuestionnaireServiceImpl(QuestionValidator questionValidator, QuestionnaireDao questionnaireDao) {
        this.questionValidator = questionValidator;
        this.questionnaireDao = questionnaireDao;
    }

    @Override
    public QuestionDetail defineQuestion(QuestionDefinition questionDefinition) throws ApplicationException {
        questionValidator.validate(questionDefinition);
        Question question = mapToQuestion(questionDefinition);
        questionnaireDao.create(question);        
        return mapToQuestionDetail(question);
    }

    @Override
    public List<QuestionDetail> getAllQuestions() {
        List<Question> questions = questionnaireDao.getDetailsAll();
        return mapToQuestionDetails(questions);
    }

    private List<QuestionDetail> mapToQuestionDetails(List<Question> questions) {
        List<QuestionDetail> questionDetails = new ArrayList<QuestionDetail>();
        for (Question question : questions) {
            questionDetails.add(mapToQuestionDetail(question));
        }
        return questionDetails;
    }

    private QuestionDetail mapToQuestionDetail(Question question) {
        QuestionDetail questionDetail =
                new QuestionDetail(question.getQuestionId(), question.getQuestionText(), question.getShortName());
        return questionDetail;
    }

    private Question mapToQuestion(QuestionDefinition questionDefinition) {
        Question question = new Question();
        question.setShortName(questionDefinition.getTitle());
        question.setQuestionText(questionDefinition.getTitle());
        question.setAnswerType(AnswerType.FREETEXT);
        question.setQuestionState(QuestionState.ACTIVE);
        return question;
    }
}
