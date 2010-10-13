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

package org.mifos.platform.questionnaire.domain;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class QuestionGroupInstance implements Serializable {
    private static final long serialVersionUID = 1150912547160536654L;

    private int id;
    private QuestionGroup questionGroup;
    private int entityId;
    private Date dateConducted;
    private int completedStatus;
    private int creatorId;
    private int versionNum;
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_BAD_FIELD")
    private List<QuestionGroupResponse> questionGroupResponses;

    @SuppressWarnings({"PMD.UnnecessaryConstructor","PMD.UncommentedEmptyConstructor"})
    public QuestionGroupInstance() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public QuestionGroup getQuestionGroup() {
        return questionGroup;
    }

    public void setQuestionGroup(QuestionGroup questionGroup) {
        this.questionGroup = questionGroup;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public Date getDateConducted() {
        return (Date) dateConducted.clone();
    }

    public void setDateConducted(Date dateConducted) {
        this.dateConducted = (Date) dateConducted.clone();
    }

    public int getCompletedStatus() {
        return completedStatus;
    }

    public void setCompletedStatus(int completedStatus) {
        this.completedStatus = completedStatus;
    }

    public void setCompletedStatus(boolean completedStatus) {
        this.completedStatus = completedStatus ? 1 : 0;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public int getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(int versionNum) {
        this.versionNum = versionNum;
    }

    public List<QuestionGroupResponse> getQuestionGroupResponses() {
        return questionGroupResponses;
    }

    public void setQuestionGroupResponses(List<QuestionGroupResponse> questionGroupResponses) {
        this.questionGroupResponses = questionGroupResponses;
    }
}
