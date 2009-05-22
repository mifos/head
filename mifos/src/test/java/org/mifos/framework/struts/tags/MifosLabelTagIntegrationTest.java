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
 
package org.mifos.framework.struts.tags;

import org.dom4j.DocumentException;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;

import static org.mifos.framework.TestUtils.assertWellFormedFragment;

public class MifosLabelTagIntegrationTest extends MifosIntegrationTest {
	
	public MifosLabelTagIntegrationTest() throws SystemException, ApplicationException {
        super();
    }
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
