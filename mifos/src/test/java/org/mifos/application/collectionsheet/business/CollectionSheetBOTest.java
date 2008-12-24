package org.mifos.application.collectionsheet.business;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.collectionsheet.persistence.CollectionSheetPersistence;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;

public class CollectionSheetBOTest extends MifosTestCase {
	public void testRetrieveCollectionSheetMeetingDateReturnsAllCollectionSheetsForSpecifiedMeeting()
			throws Exception {
		Session session = HibernateUtil.getSessionTL();
		Transaction transaction = session.beginTransaction();
		Date year2010 = DateUtils.getSqlDate(2010, Calendar.JANUARY, 1);
		CollectionSheetBO collectionSheet = new CollectionSheetBO();
		collectionSheet.setCollSheetDate(year2010);
		collectionSheet.setRunDate(new Date(System.currentTimeMillis()));
		collectionSheet.create();
		HashMap queryParameters = new HashMap();
		queryParameters.put("MEETING_DATE", year2010);
		List matchingCollectionSheets = new CollectionSheetPersistence()
				.executeNamedQuery(
						NamedQueryConstants.COLLECTION_SHEETS_FOR_MEETING_DATE,
						queryParameters);
		assertEquals(1, matchingCollectionSheets.size());
		assertEquals(collectionSheet, matchingCollectionSheets.get(0));
		transaction.rollback();
	}
}
