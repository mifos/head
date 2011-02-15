/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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
package org.mifos.platform.questionnaire.service;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class QuestionGroupDetails implements Serializable {
    private static final long serialVersionUID = 960031464763237188L;

    private int creatorId;

    private int entityId;

    private int eventSourceId;

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_BAD_FIELD")
    private List<QuestionGroupDetail> details;

    public QuestionGroupDetails() {
        this(0, 0, 0, new LinkedList<QuestionGroupDetail>());
    }

    public QuestionGroupDetails(int creatorId, int entityId, int eventSourceId, List<QuestionGroupDetail> details) {
        this.creatorId = creatorId;
        this.entityId = entityId;
        this.details = details;
        this.eventSourceId = eventSourceId;
    }

    public List<QuestionGroupDetail> getDetails() {
        return details;
    }

    public void setDetails(List<QuestionGroupDetail> details) {
        this.details = details;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public int getEntityId() {
        return entityId;
    }

    public int getEventSourceId() {
        return eventSourceId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public void setEventSourceId(int eventSourceId) {
        this.eventSourceId = eventSourceId;
    }
}
