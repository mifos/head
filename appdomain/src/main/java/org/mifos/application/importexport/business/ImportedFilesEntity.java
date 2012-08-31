/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.customers.personnel.business.PersonnelBO;

public class ImportedFilesEntity {

    private String fileName;

    private Timestamp submittedOn;

    private PersonnelBO submittedBy;
    
    private Set<AccountTrxnEntity> importedTrxn = new HashSet<AccountTrxnEntity>(0);
    
    private Boolean phaseOut;
    
    private Boolean undoable;

    protected ImportedFilesEntity() {
     // empty constructor for Hibernate
    }

    public ImportedFilesEntity(String fileName, Timestamp submittedOn, PersonnelBO submittedBy, Set<AccountTrxnEntity> importedTrxn, Boolean phaseOut, Boolean undoable) {
        super();
        this.fileName = fileName;
        this.submittedOn = submittedOn;
        this.submittedBy = submittedBy;
        this.importedTrxn = importedTrxn;
        this.phaseOut = phaseOut;
        this.undoable= undoable;
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

    public Set<AccountTrxnEntity> getImportedTrxn() {
        return importedTrxn;
    }

    public void setImportedTrxn(Set<AccountTrxnEntity> importedTrxn) {
        this.importedTrxn = importedTrxn;
    }

    public Boolean getPhaseOut() {
        return phaseOut;
    }

    public void setPhaseOut(Boolean phaseOut) {
        this.phaseOut = phaseOut;
    }

    public Boolean getUndoable() {
        return undoable;
    }

    public void setUndoable(Boolean undoable) {
        this.undoable = undoable;
    }
    
}
