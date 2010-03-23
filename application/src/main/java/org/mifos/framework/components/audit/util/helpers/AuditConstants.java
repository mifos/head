/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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
 * This interface stores constants required for the audit component.
 */
public interface AuditConstants {

    public static final String REALOBJECT = "REALOBJECT";
    public static final String TRANSACTIONBEGIN = "transactionBegin";
    public static final String TRANSACTIONEND = "transactionEnd";

    // Columns that should not be displayed
    public static final String VERSIONNO = "versionno";
    public static final String CREATEDBY = "createdby";
    public static final String UPDATEDBY = "updatedby";
    public static final String CREATEDDATE = "createddate";
    public static final String UPDATEDDATE = "updateddate";
    public static final String PASSWORD = "password";
    public static final String LOOKUPID = "lookupid";
    public static final String PRDSTATEID = "prdstateid";
    public static final String PENALTYID = "penaltyid";

    // Entity Type
    public static final String LOANPRODUCT = "LoanProduct";
    public static final String SAVINGPRODUCT = "SavingsProduct";
    public static final String GROUP = "Group";
    public static final String LOANACCOUNTS = "Accounts";
    public static final String PERSONNEL = "Personnel";
    public static final String CENTER = "Center";
    public static final String CLIENT = "Client";

    public static final String AUDITLOGRECORDS = "auditLogRecords";
    public static final String ENTITY_TYPE = "entityType";
    public static final String ENTITY_ID = "entityId";
    public static final String VIEW = "view";
    public static final String CHANGE_LOG = "ChangeLog";
    public static final String CANCEL = "cancel";
    public String PERSONNELSTATUSPATH = "org.mifos.customers.personnel.business.PersonnelStatusEntity";
    public String PERSONNELLEVELPATH = "org.mifos.customers.personnel.business.PersonnelLevelEntity";
    public String PRDSTATUSPATH = "org.mifos.accounts.productdefinition.business.PrdStatusEntity";
    public static final String HIDDEN_PASSWORD = "********";
    public static final String Audit_PASSWORD = "encryptedPassword";

}
