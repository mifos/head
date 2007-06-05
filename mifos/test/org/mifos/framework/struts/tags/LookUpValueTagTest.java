package org.mifos.framework.struts.tags;

import java.util.ArrayList;

import org.mifos.application.master.business.EntityMaster;
import org.mifos.application.master.business.LookUpMaster;
import org.mifos.framework.MifosTestCase;

public class LookUpValueTagTest extends MifosTestCase {

	public void testGetValueForId() throws Exception {
		LookUpValueTag lookUpValueTag = new LookUpValueTag();
		EntityMaster entityMaster = new EntityMaster(new Short((short)1), new Short((short)1), "EN");
		assertEquals("", lookUpValueTag.getValueForId(entityMaster));
		LookUpMaster lookUpMaster = new LookUpMaster(1, 1, "lookup");
		ArrayList <LookUpMaster>lookUpList = new ArrayList<LookUpMaster>();
		lookUpList.add(lookUpMaster);
		entityMaster.setLookUpMaster(lookUpList);
		lookUpValueTag.setId("1");
		assertEquals("1", lookUpValueTag.getId());

		lookUpValueTag.setSearchResultName("1");
		assertEquals("1", lookUpValueTag.getSearchResultName());

		assertEquals("lookup", lookUpValueTag.getValueForId(entityMaster));
		lookUpValueTag.setMapToSeperateMasterTable(true);

		assertTrue(lookUpValueTag.isMapToSeperateMasterTable());

		assertEquals("lookup", lookUpValueTag.getValueForId(entityMaster));
	}

}
