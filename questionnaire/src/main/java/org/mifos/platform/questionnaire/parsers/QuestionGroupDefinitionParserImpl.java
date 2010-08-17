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
import com.thoughtworks.xstream.io.StreamException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.questionnaire.service.ChoiceDetail;
import org.mifos.platform.questionnaire.service.EventSource;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.dtos.QuestionDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.mifos.platform.questionnaire.service.dtos.SectionDto;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import static org.mifos.platform.questionnaire.QuestionnaireConstants.PPI_SURVEY_UPLOAD_FAILED;

public final class QuestionGroupDefinitionParserImpl implements QuestionGroupDefinitionParser {
    private final XStream xstream;

    public QuestionGroupDefinitionParserImpl() {
        xstream = initializeXStream();
    }

    @Override
    public QuestionGroupDto parse(String questionGroupDefXmlFilePath) {
        return parseSafely(questionGroupDefXmlFilePath);
    }

    @Override
    public QuestionGroupDto parse(InputStream questionGroupDefInputStream) {
        return parseSafely(questionGroupDefInputStream);
    }

    @SuppressWarnings({"ThrowFromFinallyBlock", "PMD.DoNotThrowExceptionInFinally"})
    private QuestionGroupDto parseSafely(String questionGroupDefXmlFilePath) {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(questionGroupDefXmlFilePath));
            return (QuestionGroupDto) xstream.fromXML(bufferedReader);
        } catch (FileNotFoundException e) {
            throw new SystemException(PPI_SURVEY_UPLOAD_FAILED, e);
        } catch (StreamException e) {
            throw new SystemException(PPI_SURVEY_UPLOAD_FAILED, e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    throw new SystemException(PPI_SURVEY_UPLOAD_FAILED, e);
                }
            }
        }
    }

    private QuestionGroupDto parseSafely(InputStream questionGroupDefInputStream) {
        try {
            return (QuestionGroupDto) xstream.fromXML(questionGroupDefInputStream);
        } catch (StreamException e) {
            throw new SystemException(PPI_SURVEY_UPLOAD_FAILED, e);
        }
    }

    private XStream initializeXStream() {
        XStream xstream = new XStream();
        processAnnotations(xstream);
        return xstream;
    }

    private void processAnnotations(XStream xstream) {
        xstream.processAnnotations(QuestionGroupDto.class);
        xstream.processAnnotations(SectionDto.class);
        xstream.processAnnotations(QuestionDto.class);
        xstream.processAnnotations(EventSource.class);
        xstream.processAnnotations(QuestionType.class);
        xstream.processAnnotations(ChoiceDetail.class);
    }
}
