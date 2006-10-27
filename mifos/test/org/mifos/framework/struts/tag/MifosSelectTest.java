package org.mifos.framework.struts.tag;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.struts.tags.MifosSelect;

public class MifosSelectTest extends MifosTestCase {

	public void testMifosSelect(){
		MifosSelect mifosSelect = new MifosSelect();
		mifosSelect.setId("id");
		mifosSelect.setInput("input");
		mifosSelect.setLabel("label");
		mifosSelect.setMultiple("multiple");
		mifosSelect.setName("name");
		mifosSelect.setOutput("output");
		mifosSelect.setProperty("property");
		mifosSelect.setProperty1("property1");
		mifosSelect.setProperty2("property2");
		mifosSelect.setSelectStyle("selectStyle");
		mifosSelect.setSize("1");
		mifosSelect.setValue("key","value");
		assertEquals("id",mifosSelect.getId());
		assertEquals("input",mifosSelect.getInput());
		assertEquals("label",mifosSelect.getLabel());
		assertEquals("multiple",mifosSelect.getMultiple());
		assertEquals("name",mifosSelect.getName());
		assertEquals("output",mifosSelect.getOutput());
		assertEquals("property",mifosSelect.getProperty());
		assertEquals("property1",mifosSelect.getProperty1());
		assertEquals("property2",mifosSelect.getProperty2());
		assertEquals("selectStyle",mifosSelect.getSelectStyle());
		assertEquals("1",mifosSelect.getSize());
		mifosSelect = new MifosSelect("newlabel");
		assertEquals("newlabel",mifosSelect.getLabel());

	}

}
