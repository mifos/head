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

package org.mifos.application.importexport.business;

import java.io.Serializable;
import java.sql.Timestamp;

import org.mifos.application.personnel.business.PersonnelBO;

public class ImportedFilesEntity {

    private String fileName;

    private Timestamp submittedOn;

    private PersonnelBO submittedBy;

    protected ImportedFilesEntity() {
     // empty constructor for Hibernate
    }

    public ImportedFilesEntity(String fileName, Timestamp submittedOn, PersonnelBO submittedBy) {
        super();
        this.fileName = fileName;
        this.submittedOn = submittedOn;
        this.submittedBy = submittedBy;
    }

    public String getFileName() {
        return this.fileName;
    }

    public Timestamp getSubmittedOn() {
        return this.submittedOn;
    }

    public PersonnelBO getSubmittedBy() {
        return this.submittedBy;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setSubmittedOn(Timestamp submittedOn) {
        this.submittedOn = submittedOn;
    }

    public void setSubmittedBy(PersonnelBO submittedBy) {
        this.submittedBy = submittedBy;
    }

}
