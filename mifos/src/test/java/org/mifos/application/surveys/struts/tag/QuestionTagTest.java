package org.mifos.application.surveys.struts.tag;

import org.hibernate.Session;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.audit.util.helpers.AuditInterceptor;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.SessionHolder;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.struts.tags.XmlBuilder;

public class QuestionTagTest extends MifosTestCase {
	
	public QuestionTagTest() throws SystemException, ApplicationException {
        super();
    }

    Session session;
	TestDatabase database;
	
	XmlBuilder result;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		result = new XmlBuilder();
		super.setUp();
		database = TestDatabase.makeStandard();
		HibernateUtil.closeSession();
		AuditInterceptor interceptor = new AuditInterceptor();
		Session session1 = database.openSession(interceptor);
		SessionHolder holder = new SessionHolder(session1);
		holder.setInterceptor(interceptor);
		HibernateUtil.setThreadLocal(holder);
		session = session1;
	}
	
	@Override
	public void tearDown() throws Exception {
		session.close();
		HibernateUtil.resetDatabase();
	}
	
	public void testFreetext() throws Exception {
		SurveysPersistence persistence = new SurveysPersistence();
		String questionText = "QuestionTagTest testFreetext question";
		String shortName = "QuestionTagTest Name";
		Question question = new Question(shortName, questionText,
				AnswerType.FREETEXT);
		persistence.createOrUpdate(question);
		QuestionTag tag = new QuestionTag(question.getQuestionId(), "freetext answer", false);
		String markup = tag.getQuestionMarkup(result);
		String expectedMarkup = "<textarea class=\"surveyform freetext fontnormal8pt\" " +
				"cols=\"70\" rows=\"10\" name=\"value(response_1)\">freetext answer</textarea>";
		assertEquals(expectedMarkup, markup);
	}
	
	public void testNumber() throws Exception {
		SurveysPersistence persistence = new SurveysPersistence();
		String questionText = "QuestionTagTest testNumber question";
		String shortName = "QuestionTagTest Name";
		Question question = new Question(shortName, questionText, AnswerType.NUMBER);
		persistence.createOrUpdate(question);
		QuestionTag tag = new QuestionTag(question.getQuestionId(), "42", false);
		String expectedMarkup = "<input type=\"text\" class=\"surveyform number fontnormal8t\" " +
				"name=\"value(response_" + question.getQuestionId() +
				")\" value=\"42\" />";
		String markup = tag.getQuestionMarkup(result);
		
		assertEquals(expectedMarkup, markup);
		
	}
	
	public void testDate() throws Exception {
		SurveysPersistence persistence = new SurveysPersistence();
		String questionText = "QuestionTagTest testDate question";
		String shortName = "QuestionTagTest Name";
		Question question = new Question(shortName, questionText, AnswerType.DATE);
		persistence.createOrUpdate(question);
		QuestionTag tag = 
			new QuestionTag(question.getQuestionId(), "20/3/2000", false);
		String expectedMarkup = "<input type=\"text\" maxlength=\"2\"" +
				" size=\"2\" class=\"surveyform dateinput fontnormal8pt\"" +
				" name=\"value(response_" + question.getQuestionId() + "_DD)\"" +
				" value=\"20\" /> DD " +
				"<input type=\"text\" maxlength=\"2\" size=\"2\"" +
				" class=\"surveyform dateinput fontnormal8pt\"" +
				" name=\"value(response_" + question.getQuestionId() + "_MM)\"" +
				" value=\"3\" /> MM " +
				"<input type=\"text\" maxlength=\"4\" size=\"4\"" +
				" class=\"surveyform dateinput fontnormal8pt\"" +
				" name=\"value(response_" + question.getQuestionId() + "_YY)\"" +
				" value=\"2000\" /> YYYY ";
		String markup = tag.getQuestionMarkup(result);

		assertEquals(expectedMarkup, markup);
	}

}
