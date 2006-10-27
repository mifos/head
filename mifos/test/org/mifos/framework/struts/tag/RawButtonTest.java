package org.mifos.framework.struts.tag;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.struts.tags.RawButton;

public class RawButtonTest extends MifosTestCase {

	public void testRawButton(){
		RawButton rawButton = new RawButton();
		rawButton.setDisabled("disabled");
		rawButton.setId("id");
		rawButton.setName("name");
		rawButton.setOnclick("onClick");
		rawButton.setStyle("style");
		rawButton.setOndblclick("onDblClick");
		rawButton.setTitle("title");
		rawButton.setType("type");
		rawButton.setValue("value");
		assertEquals("disabled",rawButton.getDisabled());
		assertEquals("id",rawButton.getId());
		assertEquals("name",rawButton.getName());
		assertEquals("onClick",rawButton.getOnclick());
		assertEquals("onDblClick",rawButton.getOndblclick());
		assertEquals("title",rawButton.getTitle());
		assertEquals("type",rawButton.getType());
		assertEquals("value",rawButton.getValue());
		assertTrue(rawButton.toString().contains("Click To move the selected item"));
	}
}
