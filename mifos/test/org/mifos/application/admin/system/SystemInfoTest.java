/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
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
package org.mifos.application.admin.system;

import javax.servlet.ServletContext;

import junit.framework.JUnit4TestAdapter;
import org.junit.Before;
import org.junit.Test;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import static org.junit.Assert.assertEquals;

import servletunit.ServletContextSimulator;

public class SystemInfoTest {
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(SystemInfoTest.class);
	}

	private SystemInfo info;

	@Before
	public void setUp() throws Exception {
		ServletContext servletContext = new ServletContextSimulator();
		info = new SystemInfo(new MockDatabaseMetaData(), servletContext);
		info.setJavaVendor("Sun");
		info.setJavaVersion("1.5");
		info.setSvnRevision(new MockSvnRevision());
	}

	@Test
	public void testApplicationDatabaseVersion() throws Exception {
		assertEquals(DatabaseVersionPersistence.APPLICATION_VERSION, info
				.getApplicationVersion());
	}

	@Test
	public void testDatabaseDetails() throws Exception {
		assertEquals("vendorName", info.getDatabaseVendor());
		assertEquals("1.0", info.getDatabaseVersion());
		assertEquals("driverName", info.getDriverName());
		assertEquals("2.0", info.getDriverVersion());
	}

	@Test
	public void testJava() throws Exception {
		assertEquals("Sun", info.getJavaVendor());
		assertEquals("1.5", info.getJavaVersion());
	}

	@Test
	public void testApplicationServer() throws Exception {
		assertEquals("MockServletEngine/1.9.5", info.getApplicationServerInfo());
	}

	@Test
	public void testGetSubversionRevision() throws Exception {
		assertEquals("123456", info.getSvnRevision());
	}

	@Test(expected = RuntimeException.class)
	public void testGetSubversionRevisionFromMissingFile() throws Exception {
		info.setSvnRevision(new SvnRevision("non-existant.file"));
	}
}
