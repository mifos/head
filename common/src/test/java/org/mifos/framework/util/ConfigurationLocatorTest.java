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
 
package org.mifos.framework.util;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.Assert;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

public class ConfigurationLocatorTest {

    private static final String EXPECTED_PATH = "/Users/caitie/.mifos";

    @Test
    public void testGetFileHandle() throws IOException {
        ConfigurationLocator locator = new ConfigurationLocator();
        Assert.assertNotNull(locator.getFile("mock.mifosChartOfAccounts.xml"));
    }

    @Test (expected = FileNotFoundException.class)
    public void testGetFileHandleFailure() throws IOException {
        ConfigurationLocator locator = new ConfigurationLocator();
        locator.getFile("x.xml");
    }

    @Test
    public void testGetConfigurationDirectory() throws IOException {
        ConfigurationLocator locator = getConfigurationLocatorWithMockFileFactory();
        String configurationDirectory = locator.getConfigurationDirectory();
        Assert.assertNotNull(configurationDirectory);
        Assert.assertEquals(EXPECTED_PATH, configurationDirectory);
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ConfigurationLocatorTest.class);
    }

    private ConfigurationLocator getConfigurationLocatorWithMockFileFactory()  {
        File mockFile = createMock(File.class);
        expect(mockFile.exists()).andReturn(true);
        replay(mockFile);
        ConfigurationLocatorHelper mockFileFactory = createMock(ConfigurationLocatorHelper.class);
        expect(mockFileFactory.getFile(EXPECTED_PATH)).andReturn(mockFile);
        expect(mockFileFactory.getHomeProperty("user.home")).andReturn("/Users/caitie");
        expect(mockFileFactory.getEnvironmentProperty("MIFOS_CONF")).andReturn("");
        replay(mockFileFactory);
        ConfigurationLocator configurationLocator = new ConfigurationLocator();
        configurationLocator.setConfigurationLocatorHelper(mockFileFactory);
        return configurationLocator;
    }

    
}
