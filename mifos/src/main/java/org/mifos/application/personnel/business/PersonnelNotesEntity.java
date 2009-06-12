/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.application.personnel.business;

import java.util.Date;

import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.DateTimeService;

public class PersonnelNotesEntity extends PersistentObject {

    private final Integer commentId;

    private final Date commentDate;

    private final String comment;

    private final PersonnelBO officer;

    private final PersonnelBO personnel;

    public PersonnelNotesEntity(String comment, PersonnelBO officer, PersonnelBO personnel) {
        super();
        this.comment = comment;
        this.officer = officer;
        this.personnel = personnel;
        this.commentId = null;
        this.commentDate = new DateTimeService().getCurrentJavaDateTime();
    }

    protected PersonnelNotesEntity() {
        super();
        this.commentId = null;
        this.officer = null;
        this.personnel = null;
        this.comment = null;
        this.commentDate = null;
    }

    public String getCommentDateStr() {
        return (commentDate != null) ? this.commentDate.toString() : "";
    }

    public Date getCommentDate() {
        return this.commentDate;
    }

    public String getComment() {
        return this.comment;
    }

    public String getPersonnelName() {
        return officer.getDisplayName();
    }

}
