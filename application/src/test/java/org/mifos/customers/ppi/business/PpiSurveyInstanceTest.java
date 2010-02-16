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

package org.mifos.customers.ppi.business;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.mifos.customers.surveys.business.SurveyResponse;
import org.mifos.framework.exceptions.ValidationException;

public class PpiSurveyInstanceTest extends TestCase {

    private static final double DELTA = 0.00000001;

    public void testComputeScore() {
        PPISurveyInstance instance = new PPISurveyInstance();
        instance.setSurveyResponses(createSurveyResponses());
        Assert.assertEquals(instance.computeScore(), 12);
    }

    public void testLikelihoodsAfterInitialize() throws Exception {
        PPISurveyInstance instance = createPPISurveyInstance();
        Assert.assertEquals(20.0, instance.getBottomHalfBelowPovertyLinePercent(), DELTA);
        Assert.assertEquals(55.0, instance.getTopHalfBelowPovertyLinePercent(), DELTA);
        Assert.assertEquals(25.0, instance.getAbovePovertyLinePercent(), DELTA);
        Assert.assertEquals(75.0, instance.getBelowPovertyLine(), DELTA);
    }

    private Set<SurveyResponse> createSurveyResponses() {
        Set<SurveyResponse> responses = new HashSet<SurveyResponse>();
        responses.add(createMockResponse(3));
        responses.add(createMockResponse(4));
        responses.add(createMockResponse(5));
        return responses;
    }

    private MockSurveyResponse createMockResponse(final int points) {
        MockSurveyResponse r = new MockSurveyResponse();
        r.setPoints(points);
        return r;
    }

    private PPISurveyInstance createPPISurveyInstance() throws ValidationException {
        PPISurvey survey = new MockPPISurvey();
        PPISurveyInstance instance = new PPISurveyInstance();
        instance.setSurvey(survey);
        instance.setSurveyResponses(createSurveyResponses());
        instance.initialize();
        return instance;
    }

}
