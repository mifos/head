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

package org.mifos.application.admin.system;

import java.net.URI;
import java.util.Locale;

import javax.servlet.ServletContext;

import junit.framework.TestCase;

import org.joda.time.DateTime;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.util.DateTimeService;

import servletunit.ServletContextSimulator;

public class SystemInfoTest extends TestCase {

    private SystemInfo info;

    public void setUp() throws Exception {
        ServletContext servletContext = new ServletContextSimulator();
        MockDatabaseMetaData metaData = new MockDatabaseMetaData();
        info = new SystemInfo(metaData, servletContext, Locale.US, false);
        info.setJavaVendor("Sun");
        info.setJavaVersion("1.5");
        info.setSvnRevision(new MockSvnRevision());
    }

    public void testApplicationDatabaseVersion() throws Exception {
        assertEquals(DatabaseVersionPersistence.APPLICATION_VERSION, info.getApplicationVersion());
    }

    public void testDatabaseDetails() throws Exception {
        assertEquals("vendorName", info.getDatabaseVendor());
        assertEquals("1.0", info.getDatabaseVersion());
        assertEquals("driverName", info.getDriverName());
        assertEquals("2.0", info.getDriverVersion());
    }

    public void testDatabaseInfos() throws Exception {
        String infoSourceValue = "test";
        info.setInfoSource(infoSourceValue);
        assertEquals(info.getInfoSource(), infoSourceValue);
        URI full = new URI("jdbc:mysql://localhost:3305/mifos?useUnicode=true&characterEncoding=UTF-8");
        URI mysqlSpecific = new URI(full.getSchemeSpecificPart());
        info.setInfoURL(mysqlSpecific);
        assertEquals("localhost", info.getDatabaseServer());
        assertEquals("mifos", info.getDatabaseName());
        assertEquals("3305", info.getDatabasePort());
        info.setInfoUserName("mysql");
        assertEquals("mysql", info.getDatabaseUser());
    }

    public void testJava() throws Exception {
        assertEquals("Sun", info.getJavaVendor());
        assertEquals("1.5", info.getJavaVersion());
    }

    public void testApplicationServer() throws Exception {
        assertEquals("MockServletEngine/1.9.5", info.getApplicationServerInfo());
    }

    public void testGetSubversionRevision() throws Exception {
        assertEquals("123456", info.getSvnRevision());
    }

    public void testGetSubversionRevisionFromMissingFile() throws Exception {
        try {
            info.setSvnRevision(new SvnRevision("non-existant.file"));
            fail("Expected RuntimeException.");
        } catch (RuntimeException e) {
            // expected
        }
    }

    public void testGetDateTime() {
        DateTime referenceDateTime = new DateTime(2008, 12, 5, 1, 10, 0, 0);
        DateTimeService dateTimeService = new DateTimeService();
        try {
            // set a fixed datetime which is what SystemInfo should get back
            dateTimeService.setCurrentDateTimeFixed(referenceDateTime);
            assertEquals("System info date time should be from the DateTimeService", referenceDateTime, info
                    .getDateTime());
        } finally {
            dateTimeService.resetToCurrentSystemDateTime();
        }
    }
}
