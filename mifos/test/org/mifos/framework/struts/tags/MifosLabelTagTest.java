package org.mifos.framework.struts.tags;

import org.dom4j.DocumentException;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.PersistenceException;
import static org.mifos.framework.TestUtils.assertWellFormedFragment;

public class MifosLabelTagTest extends MifosTestCase {
	
	public void testHideLabelColumn() throws HibernateProcessException, PersistenceException, DocumentException{
		MifosLabelTag mifosLabelTag = new MifosLabelTag();
		mifosLabelTag.setKeyhm("Client.GovernmentId");
		XmlBuilder xmlBuilder = new XmlBuilder();
		mifosLabelTag.hideLabelColumn(xmlBuilder);
		StringBuilder label = new StringBuilder();
		label.append("<script language=\"javascript\">")
		.append(
		"if(document.getElementById(\"" + "Client.GovernmentId" + "\")!=null){")
		.append("document.getElementById(\"" + "Client.GovernmentId" + "\")")
		.append(".style.display=\"none\";}")
		.append("</script>");
		assertEquals(label.toString(), xmlBuilder.toString());
		assertWellFormedFragment(xmlBuilder.toString());
	}
	public void testhideLabelRow() throws DocumentException{
		MifosLabelTag mifosLabelTag = new MifosLabelTag();
		mifosLabelTag.setKeyhm("test");
		XmlBuilder xmlBuilder = new XmlBuilder();
		mifosLabelTag.hideLabelColumn(xmlBuilder);
		StringBuilder label = new StringBuilder();
		label.append("<script language=\"javascript\">")
		.append(
		"if(document.getElementById(\"" + "test" + "\")!=null){")
		.append("document.getElementById(\"" + "test" + "\")")
		.append(".style.display=\"none\";}")
		.append("</script>");
		assertEquals(label.toString(), xmlBuilder.toString());
		assertWellFormedFragment(xmlBuilder.toString());
	}
}
