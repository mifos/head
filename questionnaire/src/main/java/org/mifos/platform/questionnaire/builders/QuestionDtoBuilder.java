/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

import org.mifos.platform.questionnaire.service.dtos.ChoiceDto;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.dtos.QuestionDto;

import java.util.List;

import static java.util.Arrays.asList;

public class QuestionDtoBuilder {
    private final QuestionDto questionDto;

    public QuestionDtoBuilder() {
        questionDto = new QuestionDto();
    }

    public QuestionDtoBuilder withText(String text) {
        questionDto.setText(text);
        return this;
    }

    public QuestionDtoBuilder withNickname(String nickname) {
        questionDto.setNickname(nickname);
        return this;
    }

    public QuestionDtoBuilder withChoices(List<ChoiceDto> choices) {
        questionDto.setChoices(choices);
        return this;
    }

    public QuestionDtoBuilder addChoices(ChoiceDto... choices) {
        questionDto.getChoices().addAll(asList(choices));
        return this;
    }

    public QuestionDtoBuilder withMandatory(boolean mandatory) {
        questionDto.setMandatory(mandatory);
        return this;
    }

    public QuestionDtoBuilder withMinMax(Integer minValue, Integer maxValue) {
        questionDto.setMinValue(minValue);
        questionDto.setMaxValue(maxValue);
        return this;
    }

    public QuestionDtoBuilder withOrder(Integer order) {
        questionDto.setOrder(order);
        return this;
    }

    public QuestionDtoBuilder withType(QuestionType type) {
        questionDto.setType(type);
        return this;
    }

    public QuestionDto build() {
        return questionDto;
    }
}
