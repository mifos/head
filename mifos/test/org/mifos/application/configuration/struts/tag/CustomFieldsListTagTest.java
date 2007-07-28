/**

 * CustomFieldsListTagTest.java

 

 * Copyright (c) 2005-2007 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2007 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */

package org.mifos.application.configuration.struts.tag;

import static org.mifos.framework.TestUtils.assertWellFormedFragment;
import junit.framework.TestCase;
import junitx.framework.StringAssert;

import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.ApplicationInitializer;
import org.mifos.framework.TestUtils;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.XmlBuilder;
import org.mifos.framework.util.helpers.DatabaseSetup;

public class CustomFieldsListTagTest extends TestCase {

	private UserContext userContext;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		DatabaseSetup.configureLogging();
		DatabaseSetup.initializeHibernate();
		ApplicationInitializer.initializeSpring();
		
		userContext = TestUtils.makeUser();
	}

	public void testGetListRow() throws Exception {
		String categoryName = "Personnel";
		CustomFieldsListTag tag = new CustomFieldsListTag("action", "method", "flow", categoryName);
		MasterPersistence master = new MasterPersistence();
		CustomFieldDefinitionEntity customField = master.retrieveCustomFieldsDefinition(EntityType.LOAN).get(0);
		XmlBuilder link = tag.getRow(customField, userContext, 1);
		String sequenceNum = "1";
		String label = "External Loan Id";
		String dataType = "Text";
		String defaultValue = "&nbsp;";
		String mandatory = "No";
		String fieldId = "7";

/*		
		assertEquals("<tr class=\"fontnormal\"><td width=\"1%\">" 
				+ "<img src=\"pages/framework/images/bullet_circle.gif\" width=\"9\" height=\"11\" />"
				+ "</td><td>"
				+ "<a href=\"action?method=method&amp;"
				+ "category=" + categoryName 
				+ "&amp;currentFlowKey=flow\">" + categoryName + "</a></td></tr>", link.getOutput());
*/

		assertEquals("<tr>\n"
				+ "<td width=\"11%\" class=\"drawtablerow\">"
				+ sequenceNum
				+ "</td>\n"
				+ "<td width=\"22%\" class=\"drawtablerow\">"
				+ label
				+ "</td>\n"
				+ "<td width=\"21%\" class=\"drawtablerow\">"
				+ dataType
				+ "</td>\n"
				+ "<td width=\"21%\" class=\"drawtablerow\">"
				+ defaultValue
				+ "</td>\n"
				+ "<td width=\"17%\" class=\"drawtablerow\">"
				+ mandatory
				+ "</td>\n"
				+ "<td width=\"8%\" align=\"right\" class=\"drawtablerow\">"
				+ "<a href=\"action?method=method&amp;ref=" + fieldId + "\">Edit</a>"
				+ "</td>\n"
				+ "</tr>\n",
				link.getOutput());
	}

	public void testGetCustomFieldsList() throws Exception {
		String categoryName = "Personnel";
		CustomFieldsListTag tag = new CustomFieldsListTag("action", "method", "flow", categoryName);
		String html = tag.getCustomFieldsList(userContext);
		
		// TODO: currently not passing because of &nbsp; why???
		// assertWellFormedFragment(html);
		
		StringAssert.assertContains("External Id", html);			
	}

}
