/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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
import org.testng.annotations.Test;

@Test(groups={"unit", "fastTestsSuite"},  dependsOnGroups={"productMixTestSuite"})
public class BulkEntryAccountFeeActionViewTest extends TestCase {

    public void testEqualsObject() {
        CollectionSheetEntryAccountFeeActionDto bulk1 = new CollectionSheetEntryAccountFeeActionDto(1);
        CollectionSheetEntryAccountFeeActionDto bulk1b = new CollectionSheetEntryAccountFeeActionDto(1);
        CollectionSheetEntryAccountFeeActionDto bulk2 = new CollectionSheetEntryAccountFeeActionDto(2);
        CollectionSheetEntryAccountFeeActionDto bulk3 = new CollectionSheetEntryAccountFeeActionDto(3);

        TestUtils.verifyBasicEqualsContract(new CollectionSheetEntryAccountFeeActionDto[] { bulk1, bulk1b },
                new CollectionSheetEntryAccountFeeActionDto[] { bulk2, bulk3 });
    }

}
