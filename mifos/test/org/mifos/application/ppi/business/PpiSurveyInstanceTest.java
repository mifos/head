package org.mifos.application.ppi.business;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.JUnit4TestAdapter;

import org.apache.commons.lang.math.IntRange;
import org.junit.Test;
import org.mifos.application.surveys.business.SurveyResponse;

public class PpiSurveyInstanceTest {
	
	private PPILikelihood testLikelihood;
	
	private MockSurveyResponse createMockResponse(int points) {
		MockSurveyResponse r = new MockSurveyResponse();
		r.setPoints(points);
		return r;
	}
	@Test
	public void testComputeScore() {
		PpiSurveyInstance instance = new PpiSurveyInstance();
		Set responses = new HashSet();
		responses.add(createMockResponse(3));
		responses.add(createMockResponse(4));
		responses.add(createMockResponse(5));
		instance.setSurveyResponses(responses);
		int score = instance.computeScore();
		assertTrue("expected score 12, but was " + instance.computeScore(), instance.computeScore() == 12);
	}
	
	public void testLikelihoodsAfterInitialize() throws Exception {
		
		double delta = 0.0001;
		PpiSurveyInstance instance = new PpiSurveyInstance();
		PPILikelihood likelihood = new PPILikelihood(0, 10, 20.0, 30.0);
		MockPPISurvey s = new MockPPISurvey();
		instance.setSurvey(s);
		instance.initialize();
		assertEquals(20.0, instance.getBottomHalfBelowPovertyLinePercent(), delta);
		assertEquals(55.0, instance.getTopHalfBelowPovertyLinePercent(), delta);
		assertEquals(25.0, instance.getAbovePovertyLinePercent(), delta);
		assertEquals(75.0, instance.getBelowPovertyLine(), delta);
	}
	/**
	 * mocked up survey overrides getLikelihood(score) for testing
	 */
	private class MockPPISurvey extends PPISurvey {
		
		@Override
		public PPILikelihood getLikelihood(int score) {
			return testLikelihood;
		}
		
	}
	/**
	 * mocked up SurveyResponse so a test can force arbitrary points
	 * for the response without having to create an entire question/choice
	 * graph
	 */
	private class MockSurveyResponse extends SurveyResponse {
		
		private int points;
		
		public MockSurveyResponse() {
			super();
			setResponseId((int)(10000 * Math.random()) + 1);
		}
		
		public void setPoints(int p) {
			this.points = p;
		}

		@Override
		public int getPoints() {
			return points;
		}
		
	}
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(PpiSurveyInstanceTest.class);
	}

}
