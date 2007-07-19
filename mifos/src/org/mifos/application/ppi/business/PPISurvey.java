package org.mifos.application.ppi.business;

import java.util.Collections;

import org.mifos.application.surveys.business.QuestionChoice;
import org.mifos.application.surveys.business.Survey;
import org.mifos.application.surveys.business.SurveyQuestion;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.application.ppi.helpers.Country;

public class PPISurvey extends Survey {
	
	private Country country;
	private int veryPoorMin;
	private int veryPoorMax;
	private int poorMin;
	private int poorMax;
	private int atRiskMin;
	private int atRiskMax;
	private int nonPoorMin;
	private int nonPoorMax;
	
	public PPISurvey() {
		super();
	}
	
	public PPISurvey(Country country) {
		super();
		setCountry(country);
	}

	public PPISurvey(String name, SurveyState state, SurveyType appliesTo) {
		super(name, state, appliesTo);
	}
	
	public PPISurvey(String name, SurveyState state, SurveyType appliesTo, Country country) {
		super(name, state, appliesTo);
		setCountry(country);
	}

	public void setCountry(Country country) {
		this.country = country;
	}
	
	public void setCountry(int id) {
		this.country = Country.fromInt(id);
	}

	public int getCountry() {
		return country.getValue();
	}
	
	public Country getCountryAsEnum() {
		return country;
	}

	public void setVeryPoorMin(int veryPoorMin) {
		this.veryPoorMin = veryPoorMin;
	}

	public int getVeryPoorMin() {
		return veryPoorMin;
	}

	public void setVeryPoorMax(int veryPoorMax) {
		this.veryPoorMax = veryPoorMax;
	}

	public int getVeryPoorMax() {
		return veryPoorMax;
	}

	public void setPoorMin(int poorMin) {
		this.poorMin = poorMin;
	}

	public int getPoorMin() {
		return poorMin;
	}

	public void setPoorMax(int poorMax) {
		this.poorMax = poorMax;
	}

	public int getPoorMax() {
		return poorMax;
	}

	public void setAtRiskMin(int atRiskMin) {
		this.atRiskMin = atRiskMin;
	}

	public int getAtRiskMin() {
		return atRiskMin;
	}

	public void setAtRiskMax(int atRiskMax) {
		this.atRiskMax = atRiskMax;
	}

	public int getAtRiskMax() {
		return atRiskMax;
	}

	public void setNonPoorMin(int nonPoorMin) {
		this.nonPoorMin = nonPoorMin;
	}

	public int getNonPoorMin() {
		return nonPoorMin;
	}

	public void setNonPoorMax(int nonPoorMax) {
		this.nonPoorMax = nonPoorMax;
	}

	public int getNonPoorMax() {
		return nonPoorMax;
	}
	
	public void populateDefaultValues() {
		setVeryPoorMin(0);
		setVeryPoorMax(24);
		setPoorMin(25);
		setPoorMax(49);
		setAtRiskMin(50);
		setAtRiskMax(74);
		setNonPoorMin(75);
		setNonPoorMax(100);
	}
	
	@Override
	public void setAppliesTo(String dummy) {
		super.setAppliesTo(SurveyType.CLIENT);
	}
	
	@Override
	public void setAppliesTo(SurveyType dummy) {
		super.setAppliesTo(SurveyType.CLIENT);
	}
	
	@Override
	public String toString() {
		String output = "Survey name: " + getName()
				+ ", country: " + getCountryAsEnum().toString() + "\n";
		Collections.sort(getQuestions());
		for (SurveyQuestion sQuestion : getQuestions()) {
			output += "\t";
			output += sQuestion.getMandatory() == 1 ? "*" : "";
			output += sQuestion.getOrder() + ". "
					+ sQuestion.getQuestion().getShortName() + "\n";
			output += "\t " + sQuestion.getQuestion().getQuestionText() + "\n";
			for (QuestionChoice choice : sQuestion.getQuestion().getChoices()) {
				PPIChoice ppiChoice = (PPIChoice) choice;
				output += "\t\t" + ppiChoice.getChoiceText() + " - "
						+ ppiChoice.getPoints() + "\n";
			}
		}
		return output;
	}
	
}
