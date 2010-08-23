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

package org.mifos.platform.questionnaire.migration.mappers;

import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.mifos.platform.questionnaire.service.dtos.SectionDto;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.DEFAULT_EVENT_FOR_CUSTOM_FIELD;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.DEFAULT_SECTION_NAME;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.QUESTION_GROUP_TITLE_FOR_ADDITIONAL_FIELDS;
import static org.mifos.platform.util.CollectionUtils.asMap;
import static org.mifos.platform.util.MapEntry.makeEntry;

public class QuestionnaireMigrationMapperImpl implements QuestionnaireMigrationMapper {

    private Map<CustomFieldType, QuestionType> customFieldTypeToQuestionTypeMap;
    private Map<EntityType, String> entityTypeToSourceMap;
    private static final int DEFAULT_ORDER = 0;

    public QuestionnaireMigrationMapperImpl() {
        populateTypeMappings();
        populateEntityTypeToSourceMappings();
    }

    @Override
    public QuestionDto map(CustomFieldDefinitionEntity customField) {
        QuestionDto questionDto = new QuestionDto();
        questionDto.setTitle(customField.getLabel());
        questionDto.setType(mapToQuestionType(customField.getFieldTypeAsEnum()));
        questionDto.setMandatory(customField.isMandatory());
        return questionDto;
    }

    @Override
    public QuestionGroupDto map(List<CustomFieldDefinitionEntity> customFields) {
        QuestionGroupDto questionGroupDto = new QuestionGroupDto();
        SectionDto sectionDto = getDefaultSection();
        questionGroupDto.addSection(sectionDto);
        for (CustomFieldDefinitionEntity customField : customFields) {
            sectionDto.addQuestion(map(customField));
        }
        questionGroupDto.setEditable(false);
        questionGroupDto.setPpi(false);
        EventSourceDto eventSourceDto = mapForCustomField(customFields.get(0).getEntityType());
        questionGroupDto.setEventSourceDto(eventSourceDto);
        questionGroupDto.setTitle(format(QUESTION_GROUP_TITLE_FOR_ADDITIONAL_FIELDS, eventSourceDto));
        return questionGroupDto;
    }

    private EventSourceDto mapForCustomField(Short entityTypeId) {
        EntityType entityType = EntityType.fromInt(entityTypeId);
        String event = DEFAULT_EVENT_FOR_CUSTOM_FIELD;
        String source = entityTypeToSourceMap.get(entityType);
        return new EventSourceDto(event, source, format("%s %s", event, source));
    }

    private SectionDto getDefaultSection() {
        SectionDto sectionDto = new SectionDto();
        sectionDto.setName(DEFAULT_SECTION_NAME);
        sectionDto.setOrder(DEFAULT_ORDER);
        return sectionDto;
    }

    private QuestionType mapToQuestionType(CustomFieldType customFieldType) {
        return customFieldTypeToQuestionTypeMap.get(customFieldType);
    }

    private void populateEntityTypeToSourceMappings() {
        entityTypeToSourceMap = asMap(
                makeEntry(EntityType.CLIENT, "Client"),
                makeEntry(EntityType.GROUP, "Group"),
                makeEntry(EntityType.CENTER, "Center")
        );
    }

    private void populateTypeMappings() {
        customFieldTypeToQuestionTypeMap = asMap(makeEntry(CustomFieldType.NONE, QuestionType.INVALID),
                makeEntry(CustomFieldType.ALPHA_NUMERIC, QuestionType.FREETEXT),
                makeEntry(CustomFieldType.NUMERIC, QuestionType.NUMERIC),
                makeEntry(CustomFieldType.DATE, QuestionType.DATE));
    }

}
