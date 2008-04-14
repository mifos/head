package org.mifos.application.ppi.business;

import org.mifos.framework.exceptions.ValidationException;

/**
 * mocked up survey overrides getLikelihood(score) for testing
 */
class MockPPISurvey extends PPISurvey {
	
	@Override
	public PPILikelihood getLikelihood(int score) {
		try {
			return new PPILikelihood(0,100,20.0,55.0);
		} catch (ValidationException e) {
			throw new RuntimeException(e);
		}
	}
	
}