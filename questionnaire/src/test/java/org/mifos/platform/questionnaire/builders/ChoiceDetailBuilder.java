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

import org.mifos.platform.questionnaire.service.ChoiceDetail;

import java.util.List;

import static java.util.Arrays.asList;

public class ChoiceDetailBuilder {
    private final ChoiceDetail choiceDetail;

    public ChoiceDetailBuilder() {
        choiceDetail = new ChoiceDetail();
    }

    public ChoiceDetailBuilder withValue(String value) {
        choiceDetail.setValue(value);
        return this;
    }

    public ChoiceDetailBuilder withOrder(Integer order) {
        choiceDetail.setOrder(order);
        return this;
    }

    public ChoiceDetailBuilder withTags(List<String> tags) {
        choiceDetail.setTags(tags);
        return this;
    }

    public ChoiceDetailBuilder addTags(String... tags) {
        choiceDetail.getTags().addAll(asList(tags));
        return this;
    }

    public ChoiceDetail build() {
        return choiceDetail;
    }
}
