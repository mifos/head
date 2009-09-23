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

@SuppressWarnings("PMD.AvoidConstantsInterface")
public interface LoggerConstants {
    /** Location of the resource bundle */
    String LOGGERRESOURCEBUNDLE = "org.mifos.config.localizedResources.LoggerStatements";

    String ROOTLOGGER = "org.mifos";
    String FRAMEWORKLOGGER = "org.mifos.framework";
    String FRAMEWORK_STRUTS_LOGGER = "org.mifos.framework.struts";
    String LOGINLOGGER = "org.mifos.application.login";

    /** Product Definition logger name */
    String PRDDEFINITIONLOGGER = "org.mifos.application.productdefinition";

    String CUSTOMERSEARCHLOGGER = "org.mifos.application.customersearch";

    String OFFICELOGGER = "org.mifos.application.office";
    String ROLEANDPERMISSIONLOGGER = "org.mifos.application.roleandpermission";

    String CUSTOMERNOTELOGGER = "org.mifos.application.customer.customernote";
    String PERSONNEL_LOGGER = "org.mifos.application.personnel";
    String GROUP_LOGGER = "org.mifos.application.group";

    String CENTERLOGGER = "org.mifos.application.center";

    String FUNDLOGGER = "org.mifos.application.fund";

    String CLIENTLOGGER = "org.mifos.application.client";

    /**
     * The number of milliseconds after which the logger configuration file will
     * be checked for changes.
     */
    long DELAY = 3600000;

    /**
     * Accounts logger name This logger will be used for all types of accounts
     */
    String ACCOUNTSLOGGER = "org.mifos.application.accounts";

    String CONFIGURATION_LOGGER = "org.mifos.framework.components.configuration";

    String FEESLOGGER = "org.mifos.application.fees";

    String REPAYMENTSCHEDULAR = "org.mifos.framework.components.repaymentschedule";

    /**
     * Logges for Meeting Module
     */
    String MEETINGLOGGER = "org.mifos.application.meeting";

    /**
     * Logger for Audit Log
     */
    String AUDITLOGGER = "org.mifos.framework.components.audit";

    /**
     * Logger for Collection Sheet Generation
     */
    String COLLECTIONSHEETLOGGER = "org.mifos.application.collectionsheet";

    String CUSTOMERLOGGER = "org.mifos.application.customer.business.CustomerBO";

    String REPORTSLOGGER = "org.mifos.application.reports";

    String BATCH_JOBS = "org.mifos.framework.components.batchjobs";

}
