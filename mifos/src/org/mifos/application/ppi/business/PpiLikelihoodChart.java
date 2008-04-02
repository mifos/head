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
 * Encapsulates the table of likelihood probability percents for a given
 * PPI scorecard. For each score from 0 to 100, a chart will contain four
 * likelihood probabilities expressed as a decimal percent relative to a fixed poverty line:
 * <ul>
 * <li> the likelihood of being in the bottom half of those below the poverty line
 * <li> the likelihood of being in the top half of those below the poverty line
 * <li> the likelihood of being below the poverty line
 * <li> the likelihood of being above the poverty line
 * </ul>
 * Invariants:
 * <ul>
 * <li> The list includes exactly 101 rows, one for each score from 0 to 100.
 * <li> The rows are valid instances of {@link org.mifos.application.ppi.business.PpiLikelihood}
 * </ul>
 * 
 * @throws org.mifos.framework.exceptions.ValidationException if any invariants are violated
 */

import org.mifos.application.ppi.business.PpiLikelihood;
import java.util.ArrayList;
import java.util.Iterator;
import org.mifos.framework.exceptions.ValidationException;

public class PpiLikelihoodChart {
	
	private ArrayList<PpiLikelihood> likelihoods;
	
	public PpiLikelihoodChart (ArrayList<PpiLikelihood> likelihoods)throws ValidationException {
		this.likelihoods = likelihoods;
		validate();
	}
	
	public PpiLikelihood getRow(int score) {
		if ((score < 0) || (score > 100)) throw new IllegalArgumentException("score must be between 0 and 100");
		return likelihoods.get(score);
	}
	
	private void validate() throws ValidationException {
		if (likelihoods.size() != 101) 
			throw new ValidationException("exception.validation.ppi.WrongNumberOfLikelihoodChartRowsException");
		for (Iterator<PpiLikelihood> it = likelihoods.iterator(); it.hasNext();){
			if (it.next() == null) throw new ValidationException("exception.validation.ppi.MissingLikelihoodChartRowException");
		}
	}

}
