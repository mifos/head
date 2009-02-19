/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
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

package org.mifos.test.acceptance.framework;

import java.io.IOException;
import java.net.URISyntaxException;

import org.dbunit.dataset.DataSetException;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"DbUnitUtilitiesTest","unit"})
public class DbUnitUtilitiesTest {

    /**
     * Test for successful loading from a directory which includes spaces in its name.
     * 
     */
	public void testGetDataSetFromFile() throws DataSetException, IOException, URISyntaxException {
	    DbUnitUtilities utilities = new DbUnitUtilities();
	    utilities.getDataSetFromFile("test directory/acceptance_small_001_dbunit.xml.zip");
	}
}

