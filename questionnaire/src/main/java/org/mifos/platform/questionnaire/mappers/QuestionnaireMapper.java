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

import org.mifos.platform.questionnaire.domain.EventSourceEntity;
import org.mifos.platform.questionnaire.domain.QuestionEntity;
import org.mifos.platform.questionnaire.domain.QuestionGroup;
import org.mifos.platform.questionnaire.domain.QuestionGroupInstance;
import org.mifos.platform.questionnaire.service.EventSource;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetails;
import org.mifos.platform.questionnaire.service.QuestionGroupInstanceDetail;

import java.util.List;

public interface QuestionnaireMapper {
    List<QuestionDetail> mapToQuestionDetails(List<QuestionEntity> questions);

    QuestionDetail mapToQuestionDetail(QuestionEntity question);

    QuestionEntity mapToQuestion(QuestionDetail questionDetail);

    QuestionGroup mapToQuestionGroup(QuestionGroupDetail questionGroupDetail);

    QuestionGroupDetail mapToQuestionGroupDetail(QuestionGroup questionGroup);

    List<QuestionGroupDetail> mapToQuestionGroupDetails(List<QuestionGroup> questionGroups);

    List<EventSource> mapToEventSources(List<EventSourceEntity> eventSourceEntities);

    List<QuestionGroupInstance> mapToQuestionGroupInstances(QuestionGroupDetails questionGroupDetails);

    List<QuestionGroupInstanceDetail> mapToQuestionGroupInstanceDetails(List<QuestionGroupInstance> questionGroupInstances);
}
