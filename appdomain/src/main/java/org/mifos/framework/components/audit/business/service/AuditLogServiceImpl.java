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

package org.mifos.framework.components.audit.business.service;

import java.util.HashSet;
import java.util.Set;

import org.mifos.application.util.helpers.EntityType;
import org.mifos.customers.personnel.business.service.PersonnelBusinessService;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.components.audit.persistence.LegacyAuditDao;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.DateTimeService;
import org.mifos.platform.questionnaire.AuditLogService;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.SectionDetail;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.springframework.beans.factory.annotation.Autowired;

public class AuditLogServiceImpl implements AuditLogService {
    public static final String CREATE = "create";

    @Autowired
    private LegacyAuditDao legacyAuditDao;


    @Override
    public void addAuditLogRegistry(QuestionGroupDetail questionGroupDetail,
            QuestionGroupDetail oldQuestionGroupDetail, int creatorId, int entityId, String source, String event) {
        PersonnelBusinessService pbs = new PersonnelBusinessService();
        String modifierName;
        if (oldQuestionGroupDetail != null && event.toLowerCase().equals(CREATE)) {
            String questionGroupName;
            String sectionName;
            String fieldName;
            String fieldValue;
            try {
                modifierName = pbs.getPersonnel((short) creatorId).getDisplayName();
            } catch (ServiceException e) {
                modifierName = "";
            }
            questionGroupName = questionGroupDetail.getTitle();
            AuditLog auditLog = new AuditLog(entityId, EntityType.getEntityValue(source.toUpperCase()), modifierName,
                    new DateTimeService().getCurrentJavaSqlDate(), (short) creatorId);
            Set<AuditLogRecord> auditLogRecords = new HashSet<AuditLogRecord>();
            for (int sectionPosition = 0; sectionPosition < questionGroupDetail.getSectionDetails().size(); sectionPosition++) {
                SectionDetail sectionDetail = questionGroupDetail.getSectionDetails().get(sectionPosition);
                sectionName = sectionDetail.getName();
                for (int questionPosition = 0; questionPosition < sectionDetail.getQuestions().size(); questionPosition++) {
                    SectionQuestionDetail sectionQuestionDetail = sectionDetail.getQuestions().get(questionPosition);
                    fieldName = sectionQuestionDetail.getText();
                    fieldValue = sectionQuestionDetail.getAnswer();
                    String oldFieldValue = null;
                    for (SectionDetail oldSectionDetail : oldQuestionGroupDetail.getSectionDetails()) {
                        if (oldSectionDetail.getName().equals(sectionName)) {
                            for (SectionQuestionDetail oldSectionQuestionDetail : oldSectionDetail.getQuestions()) {
                                if (oldSectionQuestionDetail.getText().equals(fieldName)) {
                                    oldFieldValue = oldSectionQuestionDetail.getAnswer();
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    if (!fieldValue.equals("")) {
                        if (oldFieldValue != null && !oldFieldValue.equals("")) {
                            if (!oldFieldValue.equals(fieldValue)) {
                                auditLogRecords.add(new AuditLogRecord(trimField(questionGroupName + "/" + sectionName + "/" + fieldName, 100),
                                        trimField(oldFieldValue, 200), trimField(fieldValue, 200), auditLog));
                            }
                        }
                        else {
                            auditLogRecords.add(new AuditLogRecord(trimField(questionGroupName + "/" + sectionName + "/" + fieldName, 100),
                                    "-", trimField(fieldValue, 200), auditLog));
                        }
                    }
                }
            }
            if (!auditLogRecords.isEmpty()) {
                auditLog.addAuditLogRecords(auditLogRecords);
                legacyAuditDao.save(auditLog);
            }
        }
    }

    private String trimField(String field, int length) {
        return field.length() > length ? field.substring(0, length-3) + "..." : field;
    }

}
