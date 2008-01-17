package org.mifos.application.reports.business.service;

import org.mifos.framework.util.helpers.FilePaths;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CollectionSheetReportServiceFactory {
	private static final String COLLECTION_SHEET_REPORT_SERVICE_BEAN = "collectionSheetReportService";
	private static ICollectionSheetReportService CACHE_ENABLED_INSTANCE = null;

	public static ICollectionSheetReportService getCacheEnabledCollectionSheetReportService() {
		if (CACHE_ENABLED_INSTANCE == null)
			initCacheEnabledReportService();
		return CACHE_ENABLED_INSTANCE;
	}

	private static void initCacheEnabledReportService() {
		CACHE_ENABLED_INSTANCE = (ICollectionSheetReportService) new ClassPathXmlApplicationContext(
				FilePaths.REPORT_SERVICE_BEAN_FILE)
				.getBean(COLLECTION_SHEET_REPORT_SERVICE_BEAN);
	}

	public static ICollectionSheetReportService getCollectionSheetReportService() {
		return new CollectionSheetReportService();
	}
}
