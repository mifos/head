/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
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

import org.mifos.framework.exceptions.ValidationException;

public class PpiLikelihood {
	
	private double bottomHalfBelowPovertyLinePercent;
	private double topHalfBelowPovertyLinePercent;
	
	/**
	 * Constructs an instance from the first two likelihood percentages.
	 * @throws org.mifos.framework.exceptions.ValidationException if one of the class's
	 *                       invariants are violated, so that the object cannot
	 *                       be constructed in a valid state.
	 */
	public PpiLikelihood (double bottomHalfPct, double topHalfPct) throws ValidationException {
		checkInvariants(bottomHalfPct, topHalfPct);
		this.bottomHalfBelowPovertyLinePercent = bottomHalfPct;
		this.topHalfBelowPovertyLinePercent = topHalfPct;
	}
	
	public double getBottomHalfBelowPovertyLinePercent() {
		return this.bottomHalfBelowPovertyLinePercent;
	}

	public double getTopHalfBelowPovertyLinePercent() {
		return this.topHalfBelowPovertyLinePercent;
	}
	
	public double getBelowPovertyLinePercent() {
		return this.getBottomHalfBelowPovertyLinePercent() + this.getTopHalfBelowPovertyLinePercent();
	}
	
	public double getAbovePovertyLinePercent() {
		return 100.0 - this.getBelowPovertyLinePercent();
	}
	
	private void checkInvariants(double bottomHalf, double topHalf) throws ValidationException {
		checkLimits (bottomHalf);
		checkLimits (topHalf);
		checkSum (bottomHalf, topHalf);
	}
	
	private void checkLimits  (double aPercent) throws ValidationException {
		if ((aPercent < 0.0) || (aPercent > 100.0)) 
			throw new ValidationException("exception.validation.ppi.PpiLikelihoodArgsInvalidException");			
	}
	
	private void checkSum (double pct1, double pct2)  throws ValidationException {
		if (pct1 + pct2 > 100.0)
			throw new ValidationException("exception.validation.ppi.PpiLikelihoodArgsInvalidException");
	}
}
