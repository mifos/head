package org.mifos.application.collectionsheet.business;

public class CollectionSheetCustomerBOFixture {

	public static CollSheetCustBO createCollectionSheet() {
		CollSheetCustBO collectionSheet = new CollSheetCustBO();
		collectionSheet.setCollectionSheet(CollectionSheetBOFixture.createCollectionSheet());
		return collectionSheet;
	}
}
