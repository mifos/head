/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.customers.ppi.business;

/**
 * This class verifies that the list of likelihoods are valid:
 * <ul>
 * <li> no two likelihoods should overlap the same score value </li>
 * <li> there must be a likelihood for all scores between 0 and 100 </li>
 * </ul>
 *
 * @throws org.mifos.framework.exceptions.ValidationException if any invariants are violated
 */

import java.io.Serializable;
import java.util.List;

import org.mifos.framework.exceptions.ValidationException;

public class PPILikelihoodValidator implements Serializable {

    public static void validate(List<PPILikelihood> likelihoods) throws ValidationException {
        for (PPILikelihood likelihood : likelihoods) {
            validateNoNulls(likelihood);
            validateNoScoreOverlaps(likelihoods, likelihood);
        }
        validateCompleteScoreCoverage(likelihoods);
    }

    private static void validateNoNulls(PPILikelihood likelihood) throws ValidationException {
        if (likelihood == null) {
            throw new ValidationException("exception.validation.ppi.MissingLikelihoodChartRowException");
        }
    }

    private static void validateNoScoreOverlaps(List<PPILikelihood> likelihoods, PPILikelihood likelihood)
            throws ValidationException {
        for (PPILikelihood l : likelihoods) {
            if (likelihood != l && likelihood.getScore().overlapsRange(l.getScore())) {
                throw new ValidationException("Some score ranges overlap");
            }
        }
    }

    private static void validateCompleteScoreCoverage(List<PPILikelihood> likelihoods) throws ValidationException {
        boolean found;
        for (int i = 0; i <= 100; i++) {
            found = false;
            for (PPILikelihood likelihood : likelihoods) {
                if (likelihood.getScore().containsInteger(i)) {
                    found = true;
                }
            }
            if (!found) {
                throw new ValidationException("Not all scores are covered");
            }
        }
    }

}
