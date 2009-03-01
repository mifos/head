package org.mifos.framework.struts.tags;

import java.util.HashMap;
import java.util.Map;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

public class RawSelectTest extends MifosTestCase {

	public RawSelectTest() throws SystemException, ApplicationException {
        super();
    }

    public void testRawSelect() {
		Map data = new HashMap();
		data.put("key", "value");
		data.put("key1", "value1");
		RawSelect rawSelect = new RawSelect();
		rawSelect.setData(data);
		rawSelect.setMultiple("multiple");
		rawSelect.setName("name");
		rawSelect.setSize("1");
		rawSelect.setStyle("style");
		assertEquals(2, rawSelect.getData().size());
		assertEquals("multiple", rawSelect.getMultiple());
		assertEquals("name", rawSelect.getName());
		assertEquals("1", rawSelect.getSize());
		assertEquals("style", rawSelect.getStyle());
		assertTrue(rawSelect.toString().contains("Select the item"));
	}
}
