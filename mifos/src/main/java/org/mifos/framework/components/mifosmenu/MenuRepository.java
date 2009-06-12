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

import java.util.HashMap;
import java.util.Locale;

/**
 * It is repository to store locale based menu objects
 */
public class MenuRepository {
    /**
     * crudeMenuList is an array of Menu objects that are not mapped to locale
     * specific resources
     */
    private Menu[] crudeMenuList = null;

    /**
     * localeMenuMap stores menus of locale and acts as a repository that can
     * return Menu Objects when locale is given as key.
     */
    private HashMap localeMenuMap = null;

    /**
     * instance is the object of MenuRepository and acts as a singleton.
     */
    private static MenuRepository instance = new MenuRepository();

    /**
     * Method to get MenuRepository instance
     * 
     * @return singleton instance of menu repository
     */
    static public MenuRepository getInstance() {
        return instance;
    }

    /**
     * Method to construct MenuRepository instance. This is a private
     * constructor to implement singleton.
     */
    private MenuRepository() {
        localeMenuMap = new HashMap();
    }

    /**
     * Method to set the value of crudeMenuList member variable.
     * 
     * @param crudeMenuList
     *            is an array of crude Menu objects.
     */
    public void setCrudeMenu(Menu[] crudeMenuList) {
        this.crudeMenuList = crudeMenuList;
    }

    /**
     * Method to get the value of crudeMenuList member variable.
     * 
     * @return an array of crude Menu objects.
     */
    public Menu[] getCrudeMenu() {
        return this.crudeMenuList;
    }

    /**
     * Method to get crude menu for a given topTabName.
     * 
     * @param menuTabName
     *            is the topMenuTab name whose left menu is to be obtained.
     * @return crude Menu object for a topMenuTab
     */
    public Menu getCrudeMenuForTab(String menuTabName) {
        for (int i = 0; i < crudeMenuList.length; i++) {
            if (crudeMenuList[i].getTopMenuTabName().equalsIgnoreCase(menuTabName))
                return crudeMenuList[i];
        }
        return null;
    }

    /**
     * Method to get menu objects for a given locale
     * 
     * @param lc
     *            is locale for which menu is to be find in repository.
     * @return array of locale specific Menu objects.
     */
    public Menu[] getMenuForLocale(Locale lc) {
        if (localeMenuMap.containsKey(lc))
            return (Menu[]) localeMenuMap.get(lc);
        return null;
    }

    /**
     * Method to check whether menu exists for a given locale
     * 
     * @param lc
     *            is locale for which menu is to be find in repository.
     * @return true if menu found, otherwise false
     */
    public boolean isMenuForLocale(Locale lc) {
        if (localeMenuMap.containsKey(lc))
            return true;
        return false;
    }

    /**
     * Method to set menus for a given locale
     * 
     * @param localeMenu
     *            is an array of menus for a locale
     * @param lc
     *            is locale for which menus is to be set.
     */
    public void setMenuForLocale(Menu[] localeMenu, Locale lc) {
        localeMenuMap.put(lc, localeMenu);
    }

    /**
     * Method to get the menu for a given locale and topMenuTab
     * 
     * @param topTabName
     *            is the name of top menu tab.
     * @param lc
     *            is locale for which menus is to be find.
     * @return left Menu for given locale and topMenuTab
     */
    public Menu getMenuForTabAndLocale(String topTabName, Locale lc) {
        Menu[] leftMenus = getMenuForLocale(lc);
        if (leftMenus == null)
            return null;
        for (int i = 0; i < leftMenus.length; i++) {
            if (leftMenus[i].getTopMenuTabName().equalsIgnoreCase(topTabName))
                return leftMenus[i];
        }
        return null;
    }

    public void removeLocaleMenu(Locale lc) {
        localeMenuMap.remove(lc);
    }
}
