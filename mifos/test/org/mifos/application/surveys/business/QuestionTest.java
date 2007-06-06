package org.mifos.application.surveys.business;

import org.junit.Test;
import org.mifos.framework.TestUtils;

import junit.framework.JUnit4TestAdapter;

public class QuestionTest {
	
	@Test public void testEquals() {
		Question question1 = new Question("what color?");
		question1.setQuestionId(5);
		
		Question question2 = new Question("what shape?");
		question2.setQuestionId(5);
		
		Question subclass = new Question("what goes there?") {
		};
		subclass.setQuestionId(5);
		
		Question question3 = new Question("what shape?");
		question3.setQuestionId(7);
		
		TestUtils.verifyBasicEqualsContract(
			new Question[] {question1, question2, subclass}, 
			new Question[] {question3});
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(QuestionTest.class);
	}

}
