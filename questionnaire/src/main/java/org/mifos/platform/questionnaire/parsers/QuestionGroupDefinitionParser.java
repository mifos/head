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

package org.mifos.platform.questionnaire.parsers;

import com.thoughtworks.xstream.XStream;
import org.mifos.platform.questionnaire.service.ChoiceDetail;
import org.mifos.platform.questionnaire.service.EventSource;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.dtos.QuestionDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.mifos.platform.questionnaire.service.dtos.SectionDto;

import java.io.IOException;
import java.io.InputStream;

public class QuestionGroupDefinitionParser {
    private final XStream xstream;

    public QuestionGroupDefinitionParser() {
        xstream = initializeXStream();
    }

    public QuestionGroupDto parse(String questionGroupDefXml) throws IOException {
        InputStream inputStream = getClass().getResourceAsStream(questionGroupDefXml);
        return (QuestionGroupDto) xstream.fromXML(inputStream);
    }

    private XStream initializeXStream() {
        XStream xstream = new XStream();
        xstream.alias("QuestionGroup", QuestionGroupDto.class);
        xstream.alias("section", SectionDto.class);
        xstream.alias("question", QuestionDto.class);
        xstream.alias("eventSource", EventSource.class);
        xstream.alias("type", QuestionType.class);
        xstream.alias("choice", ChoiceDetail.class);
        xstream.alias("tag", String.class);
        xstream.addImplicitCollection(QuestionGroupDto.class, "sections");
        xstream.addImplicitCollection(SectionDto.class, "questions");
        xstream.addImplicitCollection(QuestionDto.class, "choices");
        xstream.addImplicitCollection(ChoiceDetail.class, "tags");
        return xstream;
    }
}
