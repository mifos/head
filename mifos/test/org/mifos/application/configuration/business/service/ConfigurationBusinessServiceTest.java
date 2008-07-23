package org.mifos.application.configuration.business.service;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.util.ArrayList;

import org.junit.Test;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.configuration.business.ConfigurationKeyValueInteger;
import org.mifos.framework.components.configuration.persistence.ConfigurationPersistence;


public class ConfigurationBusinessServiceTest extends MifosTestCase{
	
	@Test
	public void testRetreiveConfigurationFromPersistence() throws Exception {
		ConfigurationPersistence configPersistenceMock = createMock(ConfigurationPersistence.class);
		expect(configPersistenceMock.getAllConfigurationKeyValueIntegers()).andReturn(new ArrayList<ConfigurationKeyValueInteger>());
		replay(configPersistenceMock);
		new ConfigurationBusinessService(configPersistenceMock).getConfiguration();
		verify(configPersistenceMock);
	}
}