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

package org.mifos.service.test;

import javax.servlet.ServletContext;

import org.mifos.core.MifosException;

/**
 * Describes available instrumentation while acceptance tests are running.
 * <p>
 * Acceptance tests control the application via the user interface and verify
 * results by looking at the user interface or the database. Acceptance tests
 * may reach into a running Mifos instance via this interface.
 * <p>
 * Integration tests are also fairly "end-to-end", but they mostly avoid the
 * user interface and reach into the API much more than acceptance tests. Many
 * integration tests hit the database, but some test individual classes,
 * methods, and other non-filesystem, non-network, non-database units of code
 * (ie: true, fast "unit" tests).
 *
 * @see TestMode
 */
public interface TestingService {
    String TEST_MODE_SYSTEM_PROPERTY = "mifos.mode";
    TestMode getTestMode();
    void reinitializeCaches();
    void setLocale(String languageCode, String countryCode) throws MifosException;
    void setAccountingRules(String accountingRulesParamName, String accountingRulesParamValue) throws MifosException;
    void setFiscalCalendarRules(String workingDays, String ScheduleMeetingIfNonWorkingDay) throws MifosException;
    void setImport(String importParamName, String importParamValue) throws MifosException;
    void setProcessFlow(String processFlowParamName, String processFlowParamValue) throws MifosException;
    void setMinimumAgeForNewClient(int age);
    void setMaximumAgeForNewClient(int age);
    void setAreFamilyDetailsRequired(boolean flag);
    void setMaximumNumberOfFamilyMembers(int number);
    void setCenterHierarchyExists(boolean flag);
    void setGroupCanApplyLoans(boolean flag);
    void setClientCanExistOutsideGroup(boolean flag);
    void setBackDatedTransactionsAllowed(boolean flag);
    void runIndividualBatchJob(String jobName, ServletContext servletContext) throws MifosException;
    void runAllBatchJobs(ServletContext servletContext);
    void setClientNameSequence(String[] nameSequence);
}
