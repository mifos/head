package org.mifos.application.ppi.business;

import org.mifos.application.surveys.business.SurveyInstance;
import org.mifos.application.surveys.business.SurveyResponse;

public class PpiSurveyInstance extends SurveyInstance {

	private double bottomHalfBelowPovertyLinePercent;
	private double topHalfBelowPovertyLinePercent;
	private double belowPovertyLinePercent;
	private double abovePovertyLinePercent;
	private int score;
	
	public PpiSurveyInstance() {
		super();
	}

	public void initialize() {
		this.score = computeScore();
		PPISurvey survey = (PPISurvey)getSurvey();
		PPILikelihood l = survey.getLikelihood(score);
		this.bottomHalfBelowPovertyLinePercent = l.getBottomHalfBelowPovertyLinePercent();
		this.topHalfBelowPovertyLinePercent = l.getTopHalfBelowPovertyLinePercent();
		this.belowPovertyLinePercent = l.getBelowPovertyLinePercent();
		this.abovePovertyLinePercent = l.getAbovePovertyLinePercent();
	}
	public double getBottomHalfBelowPovertyLinePercent() {
		return this.bottomHalfBelowPovertyLinePercent;
	}

	private void setBottomHalfBelowPovertyLinePercent(double pct) {
		this.bottomHalfBelowPovertyLinePercent = pct;
	}

	public double getTopHalfBelowPovertyLinePercent() {
		return this.topHalfBelowPovertyLinePercent;
	}

	private void setoTopHalfBelowPovertyLinePercent(double pct) {
		this.topHalfBelowPovertyLinePercent = pct;
	}
	
	public double getBelowPovertyLine () {
		return this.belowPovertyLinePercent;	
	}
	
	private void setBelowPovertyLinePercent (double pct) {
		this.belowPovertyLinePercent = pct;
	}
	
	public double getAbovePovertyLinePercent (){
		return this.abovePovertyLinePercent;	
	}
	
	private void setAbovePovertyLinePercent (double pct) {
		this.abovePovertyLinePercent = pct;
	}
	
	public int getScore() {
		return this.score;
	}
	
	public int computeScore() {
		int sum = 0;
		for (SurveyResponse response : getSurveyResponses()) {
			System.out.println("point = " + response.getPoints());
			sum += response.getPoints();
			System.out.println("sum = " + sum);
		}	
		if (sum > 100)
			throw new RuntimeException("Index is larger than 100");
		return sum;

	}
}
