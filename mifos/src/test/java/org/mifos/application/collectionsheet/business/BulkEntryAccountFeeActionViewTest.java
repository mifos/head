/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
 
package org.mifos.application.collectionsheet.business;

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
