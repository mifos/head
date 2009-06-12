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

package org.mifos.framework.components.logger;

public interface LoggerConstants {
    /** Location of the resource bundle */
    public String LOGGERRESOURCEBUNDLE = "org.mifos.config.localizedResources.LoggerStatements";

    public String ROOTLOGGER = "org.mifos";
    public String FRAMEWORKLOGGER = "org.mifos.framework";
    public String FRAMEWORK_STRUTS_LOGGER = "org.mifos.framework.struts";
    public String LOGINLOGGER = "org.mifos.application.login";

    /** Product Definition logger name */
    public String PRDDEFINITIONLOGGER = "org.mifos.application.productdefinition";

    public String CUSTOMERSEARCHLOGGER = "org.mifos.application.customersearch";

    public String OFFICELOGGER = "org.mifos.application.office";
    public String ROLEANDPERMISSIONLOGGER = "org.mifos.application.roleandpermission";

    public String CUSTOMERNOTELOGGER = "org.mifos.application.customer.customernote";
    public String PERSONNEL_LOGGER = "org.mifos.application.personnel";
    public String GROUP_LOGGER = "org.mifos.application.group";

    public String CENTERLOGGER = "org.mifos.application.center";

    public String FUNDLOGGER = "org.mifos.application.fund";

    public String CLIENTLOGGER = "org.mifos.application.client";

    /**
     * The number of milliseconds after which the logger configuration file will
     * be checked for changes.
     */
    public long DELAY = 3600000;

    /**
     * Accounts logger name This logger will be used for all types of accounts
     */
    public String ACCOUNTSLOGGER = "org.mifos.application.accounts";

    public String CONFIGURATION_LOGGER = "org.mifos.framework.components.configuration";

    public String FEESLOGGER = "org.mifos.application.fees";

    public String REPAYMENTSCHEDULAR = "org.mifos.framework.components.repaymentschedule";

    /**
     * Logges for Meeting Module
     */
    public String MEETINGLOGGER = "org.mifos.application.meeting";

    /**
     * Logger for Audit Log
     */
    public String AUDITLOGGER = "org.mifos.framework.components.audit";

    public String BULKENTRYLOGGER = "org.mifos.application.collectionsheet";

    /**
     * Logger for Collection Sheet Generation
     */
    public String COLLECTIONSHEETLOGGER = "org.mifos.application.collectionsheet";

    public String CUSTOMERLOGGER = "org.mifos.application.customer.business.CustomerBO";

    public String REPORTSLOGGER = "org.mifos.application.reports";

    public static final String BATCH_JOBS = "org.mifos.framework.components.batchjobs";

}
