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

package org.mifos.platform.questionnaire.service;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class QuestionGroupInstanceDetail implements Serializable {
    private static final long serialVersionUID = -7157295411344619153L;

    private QuestionGroupDetail questionGroupDetail;
    private Date dateCompleted;
    private Integer id;
    private boolean editableForUser = true;
    
    public void setEditableForUser(boolean editableForUser) {
    	this.editableForUser = editableForUser;
    }
    
    public boolean isEditableForUser() {
    	return editableForUser;
    }

    public String getQuestionGroupTitle() {
        return questionGroupDetail.getTitle();
    }

    public QuestionGroupDetail getQuestionGroupDetail() {
        return questionGroupDetail;
    }

    public void setQuestionGroupDetail(QuestionGroupDetail questionGroupDetail) {
        this.questionGroupDetail = questionGroupDetail;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="EI_EXPOSE_REP", justification="Date is mutable, but can't help method returning date.")
    public Date getDateCompleted() {
        return dateCompleted;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="EI_EXPOSE_REP2", justification="Date is mutable, but needs initialization.")
    public void setDateCompleted(Date dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDateCompletedAsString() {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(dateCompleted);
    }
}
