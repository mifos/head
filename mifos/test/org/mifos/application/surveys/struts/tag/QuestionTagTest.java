package org.mifos.application.surveys.struts.tag;

import org.hibernate.Session;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestDatabase;
import org.mifos.framework.components.audit.util.helpers.AuditInterceptor;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.SessionHolder;
import org.mifos.framework.struts.tags.XmlBuilder;

public class QuestionTagTest extends MifosTestCase {
	
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
		Question question = new Question(questionText,
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
		Question question = new Question(questionText, AnswerType.NUMBER);
		persistence.createOrUpdate(question);
		QuestionTag tag = new QuestionTag(question.getQuestionId(), "42", false);
		String expectedMarkup = "<input type=\"text\" class=\"surveyform number fontnormal8t\" " +
				"name=\"value(response_2)\" value=\"42\" />";
		String markup = tag.getQuestionMarkup(result);
		
	}
	
	public void testDate() throws Exception {
		SurveysPersistence persistence = new SurveysPersistence();
		String questionText = "QuestionTagTest testDate question";
		Question question = new Question(questionText, AnswerType.DATE);
		persistence.createOrUpdate(question);
		QuestionTag tag = new QuestionTag(question.getQuestionId(), "20/3/2000", false);
	}

}
