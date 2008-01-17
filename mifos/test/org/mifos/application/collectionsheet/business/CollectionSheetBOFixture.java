package org.mifos.application.collectionsheet.business;

import java.util.HashSet;

import org.mifos.framework.util.helpers.DateUtils;

public class CollectionSheetBOFixture {

	public static CollectionSheetBO createCollectionSheet() {
		CollectionSheetBO collectionSheetBO = new CollectionSheetBO();
		collectionSheetBO.populateTestInstance(DateUtils.sqlToday(), DateUtils.sqlToday(), new HashSet<CollSheetCustBO>(), null);
		return collectionSheetBO;
	}

}
