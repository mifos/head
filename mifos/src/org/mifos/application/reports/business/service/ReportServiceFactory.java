/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
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
package org.mifos.application.reports.business.service;

import org.mifos.framework.business.service.ServiceDecoratorFactory;
import org.mifos.framework.components.logger.UserActivityAndServiceLogger;
import org.mifos.framework.util.helpers.FilePaths;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ReportServiceFactory {
	private static final String BRANCH_CASH_CONFIRMATION_REPORT_SERVICE = "branchCashConfirmationReportService";
	private static final String BRANCH_CASH_CONFIRMATION_CONFIG_SERVICE_BEAN = "branchCashConfirmationConfigService";
	private static final String BRANCH_REPORT_SERVICE_BEAN = "branchReportService";
	private static final String COLLECTION_SHEET_REPORT_SERVICE_BEAN = "collectionSheetReportService";
	private static final String BRANCH_REPORT_CONFIG_SERVICE_BEAN = "branchReportConfigService";
	private static ICollectionSheetReportService CACHE_ENABLED_INSTANCE;
	private static ClassPathXmlApplicationContext classPathXmlApplicationContext;

	static {
		initContextIfNull();
	}

	public static ICollectionSheetReportService getCacheEnabledCollectionSheetReportService() {
		if (CACHE_ENABLED_INSTANCE == null)
			initCacheEnabledReportService();
		return CACHE_ENABLED_INSTANCE;
	}

	private static void initCacheEnabledReportService() {
		CACHE_ENABLED_INSTANCE = (ICollectionSheetReportService) classPathXmlApplicationContext
				.getBean(COLLECTION_SHEET_REPORT_SERVICE_BEAN);
	}

	private static void initContextIfNull() {
		if (classPathXmlApplicationContext == null)
			classPathXmlApplicationContext = new ClassPathXmlApplicationContext(
					FilePaths.REPORT_SERVICE_BEAN_FILE);
	}

	public static ICollectionSheetReportService getCollectionSheetReportService() {
		return new CollectionSheetReportService();
	}

	public static BranchReportConfigService getBranchReportConfigService() {
		return (BranchReportConfigService) classPathXmlApplicationContext
				.getBean(BRANCH_REPORT_CONFIG_SERVICE_BEAN);
	}

	public static IBranchReportService getLoggingEnabledBranchReportService(
			Integer userId) {
		Object bean = classPathXmlApplicationContext
				.getBean(BRANCH_REPORT_SERVICE_BEAN);
		return ServiceDecoratorFactory
				.decorate((IBranchReportService) bean,
						new UserActivityAndServiceLogger("BranchReportService",
								userId));
	}

	public static BranchCashConfirmationConfigService getBranchCashConfirmationConfigService() {
		return (BranchCashConfirmationConfigService) classPathXmlApplicationContext
				.getBean(BRANCH_CASH_CONFIRMATION_CONFIG_SERVICE_BEAN);
	}

	public static IBranchCashConfirmationReportService getBranchCashConfirmationReportService(
			Integer userId) {
		IBranchCashConfirmationReportService serviceBean = (IBranchCashConfirmationReportService) classPathXmlApplicationContext
				.getBean(BRANCH_CASH_CONFIRMATION_REPORT_SERVICE);
		return userId == null ? ServiceDecoratorFactory.decorate(serviceBean,
				new UserActivityAndServiceLogger(
						"BranchCashConfirmationReportService", userId))
				: serviceBean;
	}
}
