package org.mifos.application.bulkentry.business;

import junit.framework.TestCase;

import org.mifos.framework.TestUtils;

public class BulkEntryAccountFeeActionViewTest extends TestCase {

	public void testEqualsObject() {
		BulkEntryAccountFeeActionView bulk1 = 
			new BulkEntryAccountFeeActionView(1);
		BulkEntryAccountFeeActionView bulk1b = 
			new BulkEntryAccountFeeActionView(1);
		BulkEntryAccountFeeActionView bulk2 = 
			new BulkEntryAccountFeeActionView(2);
		BulkEntryAccountFeeActionView bulk3 = 
			new BulkEntryAccountFeeActionView(3);
		
		TestUtils.verifyBasicEqualsContract( 
				new BulkEntryAccountFeeActionView [] {bulk1, bulk1b},
				new BulkEntryAccountFeeActionView [] {bulk2, bulk3} );
	}

}
