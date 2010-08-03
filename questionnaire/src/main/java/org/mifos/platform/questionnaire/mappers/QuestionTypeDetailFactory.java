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

package org.mifos.platform.questionnaire.mappers;

import org.mifos.platform.questionnaire.service.DateQuestionTypeDto;
import org.mifos.platform.questionnaire.service.FreeTextQuestionTypeDto;
import org.mifos.platform.questionnaire.service.MultiSelectQuestionTypeDto;
import org.mifos.platform.questionnaire.service.NumericQuestionTypeDto;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.QuestionTypeDto;
import org.mifos.platform.questionnaire.service.SingleSelectQuestionTypeDto;
import org.mifos.platform.questionnaire.service.SmartSelectQuestionTypeDto;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="LI_LAZY_INIT_STATIC")
public class QuestionTypeDetailFactory {
    private static QuestionTypeDetailFactory instance;

    // We do not want direct instantiation of this class
    private QuestionTypeDetailFactory() {
        // nothing to initialize
    }

    public static QuestionTypeDetailFactory getInstance() {
        if (instance == null) {
            instance = new QuestionTypeDetailFactory();
        }
        return instance;
    }

    public QuestionTypeDto getQuestionTypeDetail(QuestionType questionType) {
        QuestionTypeDto questionTypeDto;
        switch(questionType) {
            case FREETEXT:
                questionTypeDto = new FreeTextQuestionTypeDto();
                break;
            case DATE:
                questionTypeDto = new DateQuestionTypeDto();
                break;
            case NUMERIC:
                questionTypeDto = new NumericQuestionTypeDto();
                break;
            case MULTI_SELECT:
                questionTypeDto = new MultiSelectQuestionTypeDto();
                break;
            case SINGLE_SELECT:
                questionTypeDto = new SingleSelectQuestionTypeDto();
                break;
            case SMART_SELECT:
                questionTypeDto = new SmartSelectQuestionTypeDto();
                break;
            default:
                questionTypeDto = new QuestionTypeDto();
        }
        return questionTypeDto;
    }
}
