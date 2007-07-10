package org.mifos.application.ppi.business;

import org.mifos.application.surveys.business.QuestionChoice;

public class PPIChoice extends QuestionChoice {
	
	private int points;
	
	public PPIChoice() {
		super();
	}

	public PPIChoice(String text) {
		super(text);
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getPoints() {
		return points;
	}

}
