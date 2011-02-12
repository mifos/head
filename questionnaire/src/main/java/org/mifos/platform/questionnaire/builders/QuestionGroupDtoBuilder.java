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

import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.mifos.platform.questionnaire.service.dtos.SectionDto;

import java.util.List;
import java.util.Arrays;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class QuestionGroupDtoBuilder {
    private final QuestionGroupDto questionGroupDto;

    public QuestionGroupDtoBuilder() {
        questionGroupDto = new QuestionGroupDto();
    }

    public QuestionGroupDtoBuilder withTitle(String title) {
        questionGroupDto.setTitle(title);
        return this;
    }

    public QuestionGroupDtoBuilder withEditable(boolean editable) {
        questionGroupDto.setEditable(editable);
        return this;
    }

    public QuestionGroupDtoBuilder withEventSource(String event, String source) {
        questionGroupDto.setEventSourceDtos(Arrays.asList(new EventSourceDto(event, source, EMPTY)));
        return this;
    }

    public QuestionGroupDtoBuilder withPpi(boolean ppi) {
        questionGroupDto.setPpi(ppi);
        return this;
    }

    public QuestionGroupDtoBuilder withSections(List<SectionDto> sections) {
        questionGroupDto.setSections(sections);
        return this;
    }

    public QuestionGroupDtoBuilder addSections(SectionDto... sections) {
        questionGroupDto.getSections().addAll(asList(sections));
        return this;
    }

    public QuestionGroupDto build() {
        return questionGroupDto;
    }
}
