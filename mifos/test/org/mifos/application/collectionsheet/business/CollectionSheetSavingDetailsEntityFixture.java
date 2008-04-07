package org.mifos.application.collectionsheet.business;

public class CollectionSheetSavingDetailsEntityFixture {

	public static CollSheetSavingsDetailsEntity createSavingsDetails(Integer accountId) {
		CollSheetSavingsDetailsEntity savingDetails = new CollSheetSavingsDetailsEntity();
		savingDetails.setAccountId(accountId);
		return savingDetails;
	}

}
