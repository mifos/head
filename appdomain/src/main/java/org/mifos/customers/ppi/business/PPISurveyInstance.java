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

import org.mifos.config.GeneralConfig;
import org.mifos.customers.surveys.business.SurveyInstance;
import org.mifos.customers.surveys.business.SurveyResponse;

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

    public double getBelowPovertyLine() {
        return belowPovertyLinePercent;
    }

    public double getAbovePovertyLinePercent() {
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
        int maxPoints = GeneralConfig.getMaxPointsPerPPISurvey();
        if (sum > maxPoints) {
            throw new RuntimeException("Score is larger than " + maxPoints);
        }
        return sum;
    }

    public void setBottomHalfBelowPovertyLinePercent(double bottomHalfBelowPovertyLinePercent) {
        this.bottomHalfBelowPovertyLinePercent = bottomHalfBelowPovertyLinePercent;
    }

    public void setTopHalfBelowPovertyLinePercent(double topHalfBelowPovertyLinePercent) {
        this.topHalfBelowPovertyLinePercent = topHalfBelowPovertyLinePercent;
    }
}
