package org.mifos.application.bulkentry.business;

import junit.framework.TestCase;

import org.mifos.framework.TestUtils;

public class BulkEntryAccountFeeActionViewTest extends TestCase {

	public void testEqualsObject() {
		CollectionSheetEntryAccountFeeActionView bulk1 = 
			new CollectionSheetEntryAccountFeeActionView(1);
		CollectionSheetEntryAccountFeeActionView bulk1b = 
			new CollectionSheetEntryAccountFeeActionView(1);
		CollectionSheetEntryAccountFeeActionView bulk2 = 
			new CollectionSheetEntryAccountFeeActionView(2);
		CollectionSheetEntryAccountFeeActionView bulk3 = 
			new CollectionSheetEntryAccountFeeActionView(3);
		
		TestUtils.verifyBasicEqualsContract( 
				new CollectionSheetEntryAccountFeeActionView [] {bulk1, bulk1b},
				new CollectionSheetEntryAccountFeeActionView [] {bulk2, bulk3} );
	}

}
