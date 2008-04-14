package org.mifos.application.ppi.business;

import org.mifos.application.surveys.business.SurveyInstance;
import org.mifos.application.surveys.business.SurveyResponse;

public class PPISurveyInstance extends SurveyInstance {

	private double bottomHalfBelowPovertyLinePercent;
	private double topHalfBelowPovertyLinePercent;
	private double belowPovertyLinePercent;
	private double abovePovertyLinePercent;
	private int score;
	
	public PPISurveyInstance() {
		super();
	}

	public void initialize() {
		this.score = computeScore();
		PPILikelihood likelihood = ((PPISurvey) getSurvey()).getLikelihood(score);
		this.bottomHalfBelowPovertyLinePercent = likelihood.getBottomHalfBelowPovertyLinePercent();
		this.topHalfBelowPovertyLinePercent = likelihood.getTopHalfBelowPovertyLinePercent();
		this.belowPovertyLinePercent = likelihood.getBelowPovertyLinePercent();
		this.abovePovertyLinePercent = likelihood.getAbovePovertyLinePercent();
	}
	
	public double getBottomHalfBelowPovertyLinePercent() {
		return bottomHalfBelowPovertyLinePercent;
	}

	public double getTopHalfBelowPovertyLinePercent() {
		return topHalfBelowPovertyLinePercent;
	}

	public double getBelowPovertyLine () {
		return belowPovertyLinePercent;	
	}
	
	public double getAbovePovertyLinePercent (){
		return abovePovertyLinePercent;	
	}
	
	public int getScore() {
		return score;
	}
	
	public int computeScore() {
		int sum = 0;
		for (SurveyResponse response : getSurveyResponses()) {
			sum += response.getPoints();
		}	
		if (sum > 100)
			throw new RuntimeException("Score is larger than 100");
		return sum;
	}

	public void setBottomHalfBelowPovertyLinePercent(double bottomHalfBelowPovertyLinePercent) {
		this.bottomHalfBelowPovertyLinePercent = bottomHalfBelowPovertyLinePercent;
	}

	public void setTopHalfBelowPovertyLinePercent(double topHalfBelowPovertyLinePercent) {
		this.topHalfBelowPovertyLinePercent = topHalfBelowPovertyLinePercent;
	}
}
