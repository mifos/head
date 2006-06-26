package org.mifos.application.configuration;

import java.util.Map;

import junit.framework.TestCase;

import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.configuration.util.helpers.LabelKey;

public class TestConfiguration extends TestCase {
	
	MifosConfiguration configuration ;
	
	
	@Override
	protected void setUp() throws Exception {
		configuration= MifosConfiguration.getInstance();
		configuration.init();

	}
	public void testInitializeLabelCahe(){
		
		Map<LabelKey, String> labelCache = configuration.getLabelCache();
		assertEquals(true ,labelCache.size()>10);
		
	}
	
	public void testGetLabelValueEnglish(){
		assertEquals("Bulk entry",MifosConfiguration.getInstance().getLabelValue(ConfigurationConstants.BULKENTRY,(short)1)) ;
	}
	
	/* Will be uncommented when spanish values will be entered in master data.
	public void testGetLabelValueSpanish(){
		assertEquals("Entrada a granel",MifosConfiguration.getInstance().getLabelValue(ConfigurationConstants.BULKENTRY,(short)2)) ;
	}*/
	
	public void testInitializeLabelLocaleIdCache(){
		Map< String,Short> loacleIdCache = configuration.getLoacleIdCache();
		assertEquals(true ,loacleIdCache.size()>0);
		
	}

}
