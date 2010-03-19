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
package org.mifos.customers.util.helpers;

import java.util.Date;

import org.mifos.framework.business.service.DataTransferObject;

/**
 *
 */
public class CustomerSurveyDto implements DataTransferObject {
    private final Integer instanceId;
    private final String surveyName;
    private final Date dateConducted;

    public CustomerSurveyDto(final Integer instanceId, final String surveyName, final Date dateConducted) {
        this.instanceId = instanceId;
        this.surveyName = surveyName;
        this.dateConducted = dateConducted;
    }

    public int getInstanceId() {
        return this.instanceId;
    }

    public String getSurveyName() {
        return this.surveyName;
    }

    public Date getDateConducted() {
        return this.dateConducted;
    }

}
