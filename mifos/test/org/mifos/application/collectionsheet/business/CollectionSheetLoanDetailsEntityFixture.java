package org.mifos.application.collectionsheet.business;


public class CollectionSheetLoanDetailsEntityFixture {

	public static CollSheetLnDetailsEntity createLoanDetails() {
		return new CollSheetLnDetailsEntity();
	}

	public static CollSheetLnDetailsEntity createLoanDetails(Integer accountId) {
		CollSheetLnDetailsEntity collectionSheet = new CollSheetLnDetailsEntity();
		collectionSheet.setAccountId(accountId);
		return collectionSheet;
	}

}
