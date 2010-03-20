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

/**
 * It stores a left menu for a top row menu tab.
 */
public class Menu {
    /**
     * menuHeading is the display name for left menu.
     */
    private String menuHeading;

    /**
     * topMenuTabName is the name of top menu tab, for which it is storing the
     * left menu.
     */
    private String topMenuTabName;

    /**
     * menuGroups is an array of MenuGroup objects for current left menu. They
     * holds menuitem details.
     */
    private MenuGroup menuGroups[];

    /**
     * Method to get the value of menuGroups member variable.
     *
     * @return an array of MenuGroup objects
     */
    public MenuGroup[] getMenuGroups() {
        return menuGroups;
    }

    /**
     * Method to set the value of menuGroups member variable.
     *
     * @param menuGroups
     *            is an array of MenuGroup objects of current leftmenu
     */
    public void setMenuGroups(MenuGroup[] menuGroups) {
        this.menuGroups = menuGroups;
    }

    /**
     * Method to get the value of menuHeading member variable.
     *
     * @return String holding menu heading
     */
    public String getMenuHeading() {
        return menuHeading;
    }

    /**
     * Method to set the value of menuHeading member variable.
     *
     * @param menuHeading
     *            is the display name for the left menu.
     */
    public void setMenuHeading(String menuHeading) {
        this.menuHeading = menuHeading;
    }

    /**
     * Method to get the value of topMenuTabName member variable.
     *
     * @return String holding tab name of the top menu.
     */
    public String getTopMenuTabName() {
        return topMenuTabName;
    }

    /**
     * Method to set the value of topMenuTabName member variable.
     *
     * @param topMenuTabName
     *            is the name of tab on top menu for this leftmenu.
     */
    public void setTopMenuTabName(String topMenuTabName) {
        this.topMenuTabName = topMenuTabName;
    }
}
