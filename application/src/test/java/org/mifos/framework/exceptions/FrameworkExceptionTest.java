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

package org.mifos.framework.exceptions;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.mifos.framework.security.util.SecurityConstants;
import org.testng.annotations.Test;

@Test(groups={"unit", "fastTestsSuite"},  dependsOnGroups={"productMixTestSuite"})
public class FrameworkExceptionTest extends TestCase {

    public void testAppNotConfiguredException() throws Exception {
        try {
            ArrayList arrayList = null;
            arrayList.size();
            fail();
        } catch (Exception ex) {
            AppNotConfiguredException ance = new AppNotConfiguredException(ex);
            assertEquals("exception.framework.SystemException.AppNotConfiguredException", ance.getKey());
        }
    }

    public void testHibernateSearchException() throws Exception {
        HibernateSearchException hse = null;
        try {
            ArrayList arrayList = null;
            arrayList.size();
            fail();
        } catch (Exception ex) {
            hse = new HibernateSearchException(ex);
            assertNotNull(hse);
            hse = new HibernateSearchException("key", ex);
            assertNotNull(hse);
        }
        hse = new HibernateSearchException("key");
        assertNotNull(hse);
    }

    public void testLoggerConfigurationException() throws Exception {
        try {
            ArrayList arrayList = null;
            arrayList.size();
            fail();
        } catch (Exception ex) {
            LoggerConfigurationException lce = new LoggerConfigurationException(ex);
            assertEquals("exception.framework.SystemException.LoggerConfigurationException", lce.getKey());
        }
    }

    public void testHibernateProcessException() throws Exception {
        try {
            ArrayList arrayList = null;
            arrayList.size();
            fail();
        } catch (Exception ex) {
            HibernateProcessException hpe = new HibernateProcessException(
                    "exception.framework.SystemException.HibernateConnectionException", ex);
            assertEquals("exception.framework.SystemException.HibernateConnectionException", hpe.getKey());
        }
    }

    public void testXMLReaderException() throws Exception {
        try {
            ArrayList arrayList = null;
            arrayList.size();
            fail();
        } catch (Exception ex) {
            XMLReaderException xre = new XMLReaderException(ex);
            assertEquals("exception.framework.System.XMLReaderException", xre.getKey());
        }
    }

    public void testMenuParseException() throws Exception {
        try {
            ArrayList arrayList = null;
            arrayList.size();
            fail();
        } catch (Exception ex) {
            MenuParseException mpe = new MenuParseException(ex);
            assertEquals("exception.framework.SystemException.MenuParseException", mpe.getKey());
        }
    }

    public void testSecurityException() throws Exception {
        try {
            ArrayList arrayList = null;
            arrayList.size();
            fail();
        } catch (Exception ex) {
            SecurityException se = new SecurityException(ex);
            assertEquals(SecurityConstants.GENERALERROR, se.getKey());
        }
        SecurityException se = new SecurityException("key");
        assertEquals("key", se.getKey());
    }

    public void testConnectionNotFoundException() throws Exception {
        try {
            ArrayList arrayList = null;
            arrayList.size();
            fail();
        } catch (Exception ex) {
            ConnectionNotFoundException cnfe = new ConnectionNotFoundException(ex);
            assertEquals("exception.framework.ConnectionNotFoundException", cnfe.getKey());
        }
    }
}
