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

package org.mifos.application.collectionsheet.util.helpers;

public interface CollectionSheetConstants {

    // states in which collection sheet generation can be at any given point in
    // time.
    public static final Short COLLECTION_SHEET_GENERATION_STARTED = 1;
    public static final Short COLLECTION_SHEET_GENERATION_SUCCESSFUL = 2;
    public static final Short COLLECTION_SHEET_GENERATION_FAILED = 3;

    // parameters for query
    public static final String MEETING_DATE = "meeting_date";
    public static final Short FIRST_INSTALLMENT = 1;

}
