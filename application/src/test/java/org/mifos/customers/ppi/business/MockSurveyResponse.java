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

import org.mifos.customers.surveys.business.SurveyResponse;

/**
 * mocked up SurveyResponse so a test can force arbitrary points for the
 * response without having to create an entire question/choice graph
 */
public class MockSurveyResponse extends SurveyResponse {

    private int points;

    public MockSurveyResponse() {
        super();
        setResponseId((int) (10000 * Math.random()) + 1);
    }

    public MockSurveyResponse(int points) {
        this();
        this.points = points;
    }

    public void setPoints(int p) {
        this.points = p;
    }

    @Override
    public int getPoints() {
        return points;
    }

}
