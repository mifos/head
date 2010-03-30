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

package org.mifos.framework.exceptions;

import java.util.ArrayList;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.mifos.security.util.SecurityConstants;
import org.testng.annotations.Test;

@Test(groups={"unit", "fastTestsSuite"},  dependsOnGroups={"productMixTestSuite"})
public class FrameworkExceptionTest extends TestCase {

    @SuppressWarnings("null")
    public void testAppNotConfiguredException() throws Exception {
        try {
            ArrayList<?> arrayList = null;
            arrayList.size();
            Assert.fail();
        } catch (Exception ex) {
            AppNotConfiguredException ance = new AppNotConfiguredException(ex);
           Assert.assertEquals("exception.framework.SystemException.AppNotConfiguredException", ance.getKey());
        }
    }

    @SuppressWarnings("null")
    public void testHibernateSearchException() throws Exception {
        HibernateSearchException hse = null;
        try {
            ArrayList<?> arrayList = null;
            arrayList.size();
            Assert.fail();
        } catch (Exception ex) {
            hse = new HibernateSearchException(ex);
            Assert.assertNotNull(hse);
            hse = new HibernateSearchException("key", ex);
            Assert.assertNotNull(hse);
        }
        hse = new HibernateSearchException("key");
        Assert.assertNotNull(hse);
    }

    @SuppressWarnings("null")
    public void testLoggerConfigurationException() throws Exception {
        try {
            ArrayList<?> arrayList = null;
            arrayList.size();
            Assert.fail();
        } catch (Exception ex) {
            LoggerConfigurationException lce = new LoggerConfigurationException(ex);
           Assert.assertEquals("exception.framework.SystemException.LoggerConfigurationException", lce.getKey());
        }
    }

    @SuppressWarnings("null")
    public void testHibernateProcessException() throws Exception {
        try {
            ArrayList<?> arrayList = null;
            arrayList.size();
            Assert.fail();
        } catch (Exception ex) {
            HibernateProcessException hpe = new HibernateProcessException(
                    "exception.framework.SystemException.HibernateConnectionException", ex);
           Assert.assertEquals("exception.framework.SystemException.HibernateConnectionException", hpe.getKey());
        }
    }

    @SuppressWarnings("null")
    public void testXMLReaderException() throws Exception {
        try {
            ArrayList<?> arrayList = null;
            arrayList.size();
            Assert.fail();
        } catch (Exception ex) {
            XMLReaderException xre = new XMLReaderException(ex);
           Assert.assertEquals("exception.framework.System.XMLReaderException", xre.getKey());
        }
    }

    @SuppressWarnings("null")
    public void testMenuParseException() throws Exception {
        try {
            ArrayList<?> arrayList = null;
            arrayList.size();
            Assert.fail();
        } catch (Exception ex) {
            MenuParseException mpe = new MenuParseException(ex);
           Assert.assertEquals("exception.framework.SystemException.MenuParseException", mpe.getKey());
        }
    }

    @SuppressWarnings("null")
    public void testSecurityException() throws Exception {
        try {
            ArrayList<?> arrayList = null;
            arrayList.size();
            Assert.fail();
        } catch (Exception ex) {
            SecurityException se = new SecurityException(ex);
           Assert.assertEquals(SecurityConstants.GENERALERROR, se.getKey());
        }
        SecurityException se = new SecurityException("key");
       Assert.assertEquals("key", se.getKey());
    }

    @SuppressWarnings("null")
    public void testConnectionNotFoundException() throws Exception {
        try {
            ArrayList<?> arrayList = null;
            arrayList.size();
            Assert.fail();
        } catch (Exception ex) {
            ConnectionNotFoundException cnfe = new ConnectionNotFoundException(ex);
           Assert.assertEquals("exception.framework.ConnectionNotFoundException", cnfe.getKey());
        }
    }
}
