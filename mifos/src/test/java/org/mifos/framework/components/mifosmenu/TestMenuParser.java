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
 
package org.mifos.framework.components.mifosmenu;

import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

public class TestMenuParser extends MifosIntegrationTest {

	public TestMenuParser() throws SystemException, ApplicationException {
        super();
    }

    public void testParse() {
		// TODO: we probably want two tests, one for the live
		// menu.xml, and one (or more) which passes in XML from the test.

		MenuParser parser = new MenuParser();

		Menu[] menu = parser.parse();
		assertNotNull(menu);
		for (int i = 0; i < menu.length; i++) {

			if (i == 0) {
				MenuGroup[] menuGroups = menu[i].getMenuGroups();

				assertNotNull(menuGroups);

				for (int j = 0; j < menuGroups.length; j++) {

					if (j == 0) {
//						System.out.println(menuGroups[j].getDisplayName());
						MenuItem[] menuItems = menuGroups[j].getMenuItems();

						assertNotNull(menuItems);

						for (MenuItem item : menuItems) {
							assertNotNull(item);
//							System.out.println(item.getLinkValue());
//							System.out.println(item.getDisplayName());
						}

					}
				}
			}
		}
	}

}
