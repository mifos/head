package org.mifos.framework.components.mifosmenu;

import org.mifos.framework.MifosTestCase;

public class TestMenuParser extends MifosTestCase {

	public void testParse() {

		MenuParser parser = new MenuParser();

		Menu[] menu = parser.parse();
		assertNotNull(menu);
		for (int i = 0; i < menu.length; i++) {

			if (i == 0) {
				MenuGroup[] menuGroups = menu[i].getMenuGroups();

				assertNotNull(menuGroups);

				for (int j = 0; j < menuGroups.length; j++) {

					if (j == 0) {
						System.out.println(menuGroups[j].getDisplayName());
						MenuItem[] menuItems = menuGroups[j].getMenuItems();

						assertNotNull(menuItems);

						for (MenuItem item : menuItems) {
							
							System.out.println(item.getLinkValue());
							System.out.println(item.getDisplayName());

						}

					}
				}
			}
		}
	}

}
