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

package org.mifos.framework.persistence;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

import junit.framework.TestCase;
import junitx.framework.StringAssert;

import org.mifos.framework.ApplicationInitializer;
import org.mifos.framework.DatabaseErrorCode;
import org.mifos.framework.components.logger.MifosLogManager;

public class DatabaseInitFilterTest extends TestCase {
	
	@Override
	protected void setUp() throws Exception {
		MifosLogManager.configureLogging();
		ApplicationInitializer.setDatabaseError(DatabaseErrorCode.UPGRADE_FAILURE,
				"test death message", new SQLException("bletch ick sputter die"));
	}
	
	@Override
	protected void tearDown() throws Exception {
		ApplicationInitializer.clearDatabaseError();
	}
	
	public void testDatabaseIsReallyOld() throws Exception {
		String output = printError(-1);
		
		StringAssert.assertContains("Database is too old to have a version", 
			output);
	}

	public void testUpgradeFailed() throws Exception {
		ApplicationInitializer.setDatabaseError(DatabaseErrorCode.UPGRADE_FAILURE,
			"test death message", new SQLException("bletch ick sputter die"));
		String output = printError(66);
		
		StringAssert.assertContains("Database Version = 66\n", output);
		StringAssert.assertContains(
			"Correct the error and restart the application", output);
		StringAssert.assertContains("bletch ick sputter die", output);
	}

	public void testInexplicableFailure() throws Exception {
		ApplicationInitializer.clearDatabaseError();
		String output = printError(66);
		
		StringAssert.assertContains(
			"<p>I don't have any further details, unfortunately.</p>", output);
		StringAssert.assertNotContains("Exception", output);
	}

	private String printError(int version) {
		StringWriter out = new StringWriter();
		new DatabaseInitFilter().printErrorPage(new PrintWriter(out), version);
		String output = out.toString();
		return output;
	}
	
}
