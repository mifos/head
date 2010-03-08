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

import org.mifos.application.master.business.MifosCurrency;

/**
 * @deprecated collection sheets not used as a domain concept.
 *
 * <b>CollectionSheetService#retrieveCollectionSheet(Integer, java.util.Date)</b>
 */
@Deprecated
public class CollectionSheetCustomerBOFixture {

    public static CollSheetCustBO createCollectionSheet(MifosCurrency currency) {
        CollSheetCustBO collectionSheet = new CollSheetCustBO(currency);
        collectionSheet.setCollectionSheet(CollectionSheetBOFixture.createCollectionSheet());
        return collectionSheet;
    }
}
