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

package org.mifos.framework.util.helpers;

import org.mifos.config.GeneralConfig;
import org.mifos.customers.ppi.business.PPIChoice;
import org.mifos.customers.ppi.business.PPISurvey;
import org.mifos.customers.surveys.business.SurveyInstance;
import org.mifos.customers.surveys.business.SurveyResponse;

public class PPICalculator {

    // TODO: am I now redundant? see PPISurveyInstance.computeScore()
    public static int calculateScore(SurveyInstance instance) {
        int sum = 0;

        if (!PPISurvey.class.isInstance(instance.getSurvey())) {
            throw new RuntimeException("Survey is not a PPI survey");
        }

        for (SurveyResponse response : instance.getSurveyResponses()) {
            PPIChoice choice = (PPIChoice) response.getChoiceValue();
            sum += choice.getPoints();
        }
        int maxPoints = GeneralConfig.getMaxPointsPerPPISurvey();
        if (sum > maxPoints) {
            throw new RuntimeException("Index is larger that " + maxPoints);
        }

        return sum;
    }

    public static boolean scoreLimitsAreValid(PPISurvey survey) {
        /*
         * int sum = 0;
         *
         * { int min = survey.getVeryPoorMin(); int max =
         * survey.getVeryPoorMax(); sum += max * (max + 1) / 2 - min * (min + 1)
         * / 2 ; }
         *
         * { int min = survey.getPoorMin(); int max = survey.getPoorMax(); sum
         * += max * (max + 1) / 2 - min * (min + 1) / 2 ; }
         *
         * { int min = survey.getAtRiskMin(); int max = survey.getAtRiskMax();
         * sum += max * (max + 1) / 2 - min * (min + 1) / 2 ; }
         *
         * { int min = survey.getNonPoorMin(); int max = survey.getNonPoorMax();
         * sum += max * (max + 1) / 2 - min * (min + 1) / 2 ; }
         */
        int maxPoints = GeneralConfig.getMaxPointsPerPPISurvey();
        return survey.getNonPoorMax() == maxPoints && survey.getVeryPoorMin() == 0
                && survey.getVeryPoorMax() == survey.getPoorMin() - 1
                && survey.getPoorMax() == survey.getAtRiskMin() - 1
                && survey.getAtRiskMax() == survey.getNonPoorMin() - 1;
    }

}
