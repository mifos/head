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
