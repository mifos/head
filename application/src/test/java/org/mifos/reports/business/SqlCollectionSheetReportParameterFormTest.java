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

package org.mifos.reports.business;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import junit.framework.TestCase;

import org.mifos.reports.business.validator.Errors;

public class SqlCollectionSheetReportParameterFormTest extends TestCase {

    public void testValidateAcceptsDateEvenIfItHasExtraCharactersAtTheEnd() {
        SqlCollectionSheetReportParameterForm form = new SqlCollectionSheetReportParameterForm("1", "1", "1",
                "23/06/2008ss");
        Errors errorsMock = createMock(Errors.class);
        replay(errorsMock);
        form.validate(errorsMock);
        verify(errorsMock);
    }

}
