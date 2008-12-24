package org.mifos.application.ppi.business;

import org.mifos.application.surveys.business.SurveyResponse;

/**
 * mocked up SurveyResponse so a test can force arbitrary points
 * for the response without having to create an entire question/choice
 * graph
 */
public class MockSurveyResponse extends SurveyResponse {
	
	private int points;
	
	public MockSurveyResponse() {
		super();
		setResponseId((int)(10000 * Math.random()) + 1);
	}
	
	public MockSurveyResponse(int points) {
		this();
		this.points = points;
	}
	
	public void setPoints(int p) {
		this.points = p;
	}

	@Override
	public int getPoints() {
		return points;
	}
	
}