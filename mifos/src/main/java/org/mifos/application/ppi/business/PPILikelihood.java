/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.application.ppi.business;

/**
 * This class holds the likelihood (probability) percentages for a given PPI survey's likelihood chart
 * and for a given score on that survey.
 * <p>
 * For each possible score on the survey, a PPI scorecard lists four likelihood 
 * percent probabilities relative to a fixed poverty line:
 * <ul>
 * <li> the likelihood of being in the bottom half of those below the poverty line
 * <li> the likelihood of being in the top half of those below the poverty line
 * <li> the likelihood of being below the poverty line
 * <li> the likelihood of being above the poverty line
 * </ul>
 * Because the last two percentages can be calculated from the first two, this class
 * stores only the first two percentages.
 * <p>
 * Invariants:
 * <ul>
 * <li> 0.0 <= bottomHalfBelowPovertyLinePercent <= 100.0
 * <li> 0.0 <= topHalfBelowPovertyLinePercent <= 100.0
 * <li> bottomHalfBelowPovertyLinePercent + bottomHalfBelowPovertyLinePercent <= 100.0
 * </ul>
 */

import java.io.Serializable;

import org.apache.commons.lang.math.IntRange;
import org.apache.commons.lang.math.Range;
import org.mifos.application.surveys.business.Survey;
import org.mifos.framework.exceptions.ValidationException;
import org.mifos.config.GeneralConfig;

public class PPILikelihood implements Serializable {
	private int likelihoodId;
	private Survey survey;
	private int scoreFrom;
	private int scoreTo;
	private double bottomHalfBelowPovertyLinePercent;
	private double topHalfBelowPovertyLinePercent;
	private int order;
	
	public PPILikelihood() {
	}
	
	/**
	 * Constructs an instance from the first two likelihood percentages for a score range.
	 * @throws org.mifos.framework.exceptions.ValidationException if one of the class's
	 *                       invariants are violated, so that the object cannot
	 *                       be constructed in a valid state.
	 */
	public PPILikelihood(int scoreFrom, int scoreTo, double bottomHalfPct, double topHalfPct) throws ValidationException {
		checkInvariants(scoreFrom, scoreTo, bottomHalfPct, topHalfPct);
		this.scoreFrom = scoreFrom;
		this.scoreTo = scoreTo;
		this.bottomHalfBelowPovertyLinePercent = bottomHalfPct;
		this.topHalfBelowPovertyLinePercent = topHalfPct;
	}
	
	public Range getScore() {
		return new IntRange(this.scoreFrom, this.scoreTo);
	}
	
	public int getScoreFrom() {
		return scoreFrom;
	}

	public void setScoreFrom(int scoreFrom) {
		this.scoreFrom = scoreFrom;
	}

	public int getScoreTo() {
		return scoreTo;
	}

	public void setScoreTo(int scoreTo) {
		this.scoreTo = scoreTo;
	}

	public double getBottomHalfBelowPovertyLinePercent() {
		return this.bottomHalfBelowPovertyLinePercent;
	}

	public double getTopHalfBelowPovertyLinePercent() {
		return this.topHalfBelowPovertyLinePercent;
	}
	
	public void setBottomHalfBelowPovertyLinePercent(double bottomHalfBelowPovertyLinePercent) {
		this.bottomHalfBelowPovertyLinePercent = bottomHalfBelowPovertyLinePercent;
	}

	public void setTopHalfBelowPovertyLinePercent(double topHalfBelowPovertyLinePercent) {
		this.topHalfBelowPovertyLinePercent = topHalfBelowPovertyLinePercent;
	}
	
	public double getBelowPovertyLinePercent() {
		return this.getBottomHalfBelowPovertyLinePercent() + this.getTopHalfBelowPovertyLinePercent();
	}
	
	public double getAbovePovertyLinePercent() {
		return 100.0 - this.getBelowPovertyLinePercent();
	}
	
	private void checkInvariants(int scoreFrom, int scoreTo, double bottomHalf, double topHalf) throws ValidationException {
		checkRange(new IntRange(scoreFrom, scoreTo));
		checkLimits (bottomHalf);
		checkLimits (topHalf);
		checkSum (bottomHalf, topHalf);
	}
	
	private void checkRange(Range score) throws ValidationException {
		int maxPoints = GeneralConfig.getMaxPointsPerPPISurvey();
		if (score.getMinimumInteger() < 0 || score.getMaximumInteger() > maxPoints)
			throw new ValidationException("exception.validation.ppi.PpiLikelihoodArgsInvalidException");
	}

	private void checkLimits(double aPercent) throws ValidationException {
		if (aPercent < 0.0 || aPercent > 100.0) 
			throw new ValidationException("exception.validation.ppi.PpiLikelihoodArgsInvalidException");			
	}
	
	private void checkSum(double pct1, double pct2)  throws ValidationException {
		if (pct1 + pct2 > 100.0)
			throw new ValidationException("exception.validation.ppi.PpiLikelihoodArgsInvalidException");
	}

	public Survey getSurvey() {
		return survey;
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	public int getLikelihoodId() {
		return likelihoodId;
	}

	public void setLikelihoodId(int likelihoodId) {
		this.likelihoodId = likelihoodId;
	}
	
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
}
