/*
 * Copyright Grameen Foundation USA
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

package org.mifos.framework.components.audit.util.helpers;

/**
 * This public interface stores constants required for the audit component.
 */
public interface AuditConstants {

    String REALOBJECT = "REALOBJECT";
    String TRANSACTIONBEGIN = "transactionBegin";
    String TRANSACTIONEND = "transactionEnd";

    // Columns that should not be displayed
    String VERSIONNO = "versionno";
    String CREATEDBY = "createdby";
    String UPDATEDBY = "updatedby";
    String CREATEDDATE = "createddate";
    String UPDATEDDATE = "updateddate";
    String PASSWORD = "password";
    String LOOKUPID = "lookupid";
    String PRDSTATEID = "prdstateid";
    String PENALTYID = "penaltyid";

    // Entity Type
    String LOANPRODUCT = "LoanProduct";
    String SAVINGPRODUCT = "SavingsProduct";
    String GROUP = "Group";
    String LOANACCOUNTS = "Accounts";
    String PERSONNEL = "Personnel";
    String CENTER = "Center";
    String CLIENT = "Client";

    String AUDITLOGRECORDS = "auditLogRecords";
    String ENTITY_TYPE = "entityType";
    String ENTITY_ID = "entityId";
    String VIEW = "view";
    String CHANGE_LOG = "ChangeLog";
    String CANCEL = "cancel";
    String PERSONNELSTATUSPATH = "org.mifos.customers.personnel.business.PersonnelStatusEntity";
    String PERSONNELLEVELPATH = "org.mifos.customers.personnel.business.PersonnelLevelEntity";
    String PRDSTATUSPATH = "org.mifos.accounts.productdefinition.business.PrdStatusEntity";
    String HIDDEN_PASSWORD = "********";
    String Audit_PASSWORD = "encryptedPassword";

}
