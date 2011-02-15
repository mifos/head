/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */

package org.mifos.platform.questionnaire.service.dtos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QuestionGroupInstanceDto implements Serializable {
    private static final long serialVersionUID = -2848012511898606643L;

    private Integer questionGroupId;
    private Integer entityId;
    private Integer creatorId;
    private Integer eventSourceId;
    private Integer version;
    private boolean completed;
    private Date dateConducted;
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_BAD_FIELD")
    private List<QuestionGroupResponseDto> questionGroupResponseDtos;

    public QuestionGroupInstanceDto() {
        this.questionGroupResponseDtos = new ArrayList<QuestionGroupResponseDto>();
    }

    public void addQuestionGroupResponseDto(QuestionGroupResponseDto questionGroupResponseDto) {
        this.questionGroupResponseDtos.add(questionGroupResponseDto);
    }

    public List<QuestionGroupResponseDto> getQuestionGroupResponseDtos() {
        return questionGroupResponseDtos;
    }

    public void setQuestionGroupResponseDtos(List<QuestionGroupResponseDto> questionGroupResponseDtos) {
        this.questionGroupResponseDtos = questionGroupResponseDtos;
    }

    public Integer getQuestionGroupId() {
        return questionGroupId;
    }

    public void setQuestionGroupId(Integer questionGroupId) {
        this.questionGroupId = questionGroupId;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public Integer getEventSourceId() {
        return eventSourceId;
    }

    public void setEventSourceId(Integer eventSourceId) {
        this.eventSourceId = eventSourceId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="EI_EXPOSE_REP", justification="Date is mutable, but can't help method returning date.")
    public Date getDateConducted() {
        return dateConducted;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="EI_EXPOSE_REP", justification="Date is mutable, but can't help method returning date.")
    public void setDateConducted(Date dateConducted) {
        this.dateConducted = dateConducted;
    }

    public void setCompleted(int completed) {
        this.completed = completed == 1;
    }
}
