/*
 * Copyright Grameen Foundation USA
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

package org.mifos.platform.questionnaire.builders;

import org.mifos.platform.questionnaire.service.dtos.QuestionGroupInstanceDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupResponseDto;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;

public class QuestionGroupInstanceDtoBuilder {
    private final QuestionGroupInstanceDto questionGroupInstanceDto;

    public QuestionGroupInstanceDtoBuilder() {
        this.questionGroupInstanceDto = new QuestionGroupInstanceDto();
        this.questionGroupInstanceDto.setDateConducted(Calendar.getInstance().getTime());
    }

    public QuestionGroupInstanceDtoBuilder withQuestionGroup(Integer questionGroupId) {
        this.questionGroupInstanceDto.setQuestionGroupId(questionGroupId);
        return this;
    }

    public QuestionGroupInstanceDtoBuilder withEntity(Integer entityId) {
        this.questionGroupInstanceDto.setEntityId(entityId);
        return this;
    }

    public QuestionGroupInstanceDtoBuilder withCreator(Integer creatorId) {
        this.questionGroupInstanceDto.setCreatorId(creatorId);
        return this;
    }

    public QuestionGroupInstanceDtoBuilder withEventSource(Integer eventSource) {
        this.questionGroupInstanceDto.setEventSourceId(eventSource);
        return this;
    }

    public QuestionGroupInstanceDtoBuilder withVersion(Integer version) {
        this.questionGroupInstanceDto.setVersion(version);
        return this;
    }

    public QuestionGroupInstanceDtoBuilder withCompleted(boolean completedStatus) {
        this.questionGroupInstanceDto.setCompleted(completedStatus);
        return this;
    }

    public QuestionGroupInstanceDtoBuilder withDateConducted(Date dateConducted) {
        this.questionGroupInstanceDto.setDateConducted(dateConducted);
        return this;
    }

    public QuestionGroupInstanceDtoBuilder withResponses(List<QuestionGroupResponseDto> questionGroupResponses) {
        this.questionGroupInstanceDto.setQuestionGroupResponseDtos(questionGroupResponses);
        return this;
    }

    public QuestionGroupInstanceDtoBuilder addResponses(QuestionGroupResponseDto... questionGroupResponses) {
        this.questionGroupInstanceDto.getQuestionGroupResponseDtos().addAll(asList(questionGroupResponses));
        return this;
    }

    public QuestionGroupInstanceDto build() {
        return questionGroupInstanceDto;
    }
}
