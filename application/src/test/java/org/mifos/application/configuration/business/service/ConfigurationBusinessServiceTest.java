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

package org.mifos.application.configuration.business.service;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.mifos.framework.components.configuration.business.ConfigurationKeyValueInteger;
import org.mifos.framework.components.configuration.persistence.ConfigurationPersistence;

public class ConfigurationBusinessServiceTest extends TestCase {

    private ConfigurationPersistence configPersistenceMock;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        configPersistenceMock = createMock(ConfigurationPersistence.class);
    }

    public void testRetreiveConfigurationFromPersistence() throws Exception {
        expect(configPersistenceMock.getAllConfigurationKeyValueIntegers()).andReturn(
                new ArrayList<ConfigurationKeyValueInteger>());
        replay(configPersistenceMock);
        new ConfigurationBusinessService(configPersistenceMock).getConfiguration();
        verify(configPersistenceMock);
    }

    public void testIsGlimEnabled() throws Exception {
        expect(configPersistenceMock.isGlimEnabled()).andReturn(true);
        replay(configPersistenceMock);
        new ConfigurationBusinessService(configPersistenceMock).isGlimEnabled();
        verify(configPersistenceMock);
    }
}
