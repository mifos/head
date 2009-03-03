package org.mifos.application.configuration.business.service;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.util.ArrayList;

import org.junit.Test;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.components.configuration.business.ConfigurationKeyValueInteger;
import org.mifos.framework.components.configuration.persistence.ConfigurationPersistence;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

public class ConfigurationBusinessServiceTest extends MifosIntegrationTest{
	
	
	public ConfigurationBusinessServiceTest() throws SystemException, ApplicationException {
        super();
    }

    private ConfigurationPersistence configPersistenceMock;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		configPersistenceMock = createMock(ConfigurationPersistence.class);
	}
	
	@Test
	public void testRetreiveConfigurationFromPersistence() throws Exception {
		expect(configPersistenceMock.getAllConfigurationKeyValueIntegers()).andReturn(new ArrayList<ConfigurationKeyValueInteger>());
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