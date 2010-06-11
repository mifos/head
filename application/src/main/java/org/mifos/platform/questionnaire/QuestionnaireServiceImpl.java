package org.mifos.platform.questionnaire;

import org.mifos.customers.surveys.business.Question;
import org.mifos.customers.surveys.helpers.AnswerType;
import org.mifos.customers.surveys.helpers.QuestionState;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.platform.questionnaire.contract.QuestionDefinition;
import org.mifos.platform.questionnaire.contract.QuestionDetail;
import org.mifos.platform.questionnaire.contract.QuestionType;
import org.mifos.platform.questionnaire.contract.QuestionnaireService;
import org.mifos.platform.questionnaire.persistence.QuestionnaireDao;
import org.mifos.platform.questionnaire.validators.QuestionValidator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.mifos.customers.surveys.helpers.AnswerType.FREETEXT;
import static org.mifos.customers.surveys.helpers.AnswerType.INVALID;

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
        return new QuestionDetail(question.getQuestionId(), question.getQuestionText(), question.getShortName(), mapToQuestionType(question.getAnswerTypeAsEnum()));
    }

    private QuestionType mapToQuestionType(AnswerType answerType) {
        return answerType == AnswerType.FREETEXT ? QuestionType.FREETEXT : QuestionType.INVALID;
    }

    private Question mapToQuestion(QuestionDefinition questionDefinition) {
        Question question = new Question();
        question.setShortName(questionDefinition.getTitle());
        question.setQuestionText(questionDefinition.getTitle());
        question.setAnswerType(mapToAnswerType(questionDefinition.getType()));
        question.setQuestionState(QuestionState.ACTIVE);
        return question;
    }

    private AnswerType mapToAnswerType(QuestionType questionType) {
        return questionType == QuestionType.FREETEXT ? FREETEXT : INVALID;
    }
}
