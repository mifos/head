package org.mifos.application.ppi.business;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;
import org.mifos.application.surveys.business.SurveyResponse;
import org.mifos.framework.exceptions.ValidationException;

public class PpiSurveyInstanceTest {
	
	private static final double DELTA = 0.00000001;
    @Test
	public void testComputeScore() {
		PPISurveyInstance instance = new PPISurveyInstance();
		instance.setSurveyResponses(createSurveyResponses());
		assertEquals(instance.computeScore(), 12);
	}

	@Test
	public void testLikelihoodsAfterInitialize() throws Exception {
		PPISurveyInstance instance = createPPISurveyInstance();
		assertEquals(20.0, instance.getBottomHalfBelowPovertyLinePercent(), DELTA);
		assertEquals(55.0, instance.getTopHalfBelowPovertyLinePercent(), DELTA);
		assertEquals(25.0, instance.getAbovePovertyLinePercent(), DELTA);
		assertEquals(75.0, instance.getBelowPovertyLine(), DELTA);
	}

	private Set<SurveyResponse> createSurveyResponses() {
		Set<SurveyResponse> responses = new HashSet<SurveyResponse>();
		responses.add(createMockResponse(3));
		responses.add(createMockResponse(4));
		responses.add(createMockResponse(5));
		return responses;
	}
	
	private MockSurveyResponse createMockResponse(int points) {
		MockSurveyResponse r = new MockSurveyResponse();
		r.setPoints(points);
		return r;
	}

	private PPISurveyInstance createPPISurveyInstance() throws ValidationException {
		PPISurvey survey = new MockPPISurvey();
		PPISurveyInstance instance = new PPISurveyInstance();
		instance.setSurvey(survey);
		instance.setSurveyResponses(createSurveyResponses());
		instance.initialize();
		return instance;
	}
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(PpiSurveyInstanceTest.class);
	}

}
