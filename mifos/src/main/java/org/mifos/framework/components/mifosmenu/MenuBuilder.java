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

import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.mifos.framework.exceptions.MenuParseException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.LabelTagUtils;

/**
 * It builds menu respective to a locale from crude menu that is obtained from xml parser.
 */
public class MenuBuilder {
	private static PageContext pageContext;
	/**
	 * Method to build menu respective to a locale only when it is not available in menu repository.
	 * It then sets menu built for locale to the repository.
	 * @param lc locale instance for which menu has to be built.
	 */
	public static void buildMenuForLocale(PageContext pageCtx)throws SystemException, JspException{
		MenuRepository menuRepository = MenuRepository.getInstance();
		pageContext = pageCtx;
		Locale lc = LabelTagUtils.getInstance().getUserPreferredLocaleObject(pageContext);
		if(!menuRepository.isMenuForLocale(lc)){
			Menu[] crudeMenu=menuRepository.getCrudeMenu();
			if(crudeMenu==null){
				crudeMenu=obtainCrudMenu();
				menuRepository.setCrudeMenu(crudeMenu);
			}
			Menu[] localeMenu=parseCrudMenuForLocale(crudeMenu,lc);
			menuRepository.setMenuForLocale(localeMenu,lc);
		}
	}
	
	/**
	 * Method to return crud menu for the application
	 * @return array of Menu objects for Mifos. These menu objects contain keys respective to display strings
	 * @throws MenuParseException
	 */
	private static Menu[] obtainCrudMenu()throws SystemException{
		return MenuParser.parse();
	}
	
	/**
	 * Method to prepare menu for a given locale from crude menu  
	 * @param crudeMenu array of crude Menu objects
	 * @param lc locale instance for which menu has to be built.
	 * @return array of menu objects for a given locale
	 */
	private static Menu[] parseCrudMenuForLocale(Menu[]crudeMenu,Locale lc)throws JspException{
		Menu[] localeMenu=new Menu[crudeMenu.length];
		for(int i=0;i<crudeMenu.length;i++){
			localeMenu[i]=new Menu();
			MenuGroup[] crudeMenuGroups=crudeMenu[i].getMenuGroups();
			localeMenu[i].setMenuGroups(parseMenuGroupsForLocale(crudeMenuGroups));
			localeMenu[i].setTopMenuTabName(crudeMenu[i].getTopMenuTabName());
			
			localeMenu[i].setMenuHeading(getLabel(crudeMenu[i].getMenuHeading()));
		}
		return localeMenu;
	}
	
	/**
	 * Method to prepare MenuGroups of one top-menu-tab of crude menu as per locale  
	 * @param crudeMenuGroups array of crude MenuGroups objects for one top-menu-tab.
	 * @return array of MenuGroup objects of one top-menu-tab for a given locale
	 */
	private static MenuGroup[] parseMenuGroupsForLocale(MenuGroup[] crudeMenuGroups)throws JspException{
		MenuGroup[] localeMenuGroup=new MenuGroup[crudeMenuGroups.length];
		for(int i=0;i<crudeMenuGroups.length;i++){
			localeMenuGroup[i]=new MenuGroup();
			MenuItem[] crudeMenuItems=crudeMenuGroups[i].getMenuItems();
			localeMenuGroup[i].setMenuItems(parseMenuItemsForLocale(crudeMenuItems));
			localeMenuGroup[i].setDisplayName(parseDisplayName(crudeMenuGroups[i].getDisplayName()));
		}
		return localeMenuGroup;
	}
	
	/**
	 * Method to prepare MenuItems for MenuGroup of crude menu as per locale  
	 * @param crudeMenuGroups array of crude MenuGroup objects for one top-menu-tab.
	 * @return array of MenuItem objects of a MenuGroup for a given locale
	 */
	private static MenuItem[] parseMenuItemsForLocale(MenuItem[] crudeMenuItems)throws JspException{
		MenuItem[] localeMenuItem = new MenuItem[crudeMenuItems.length];
		for(int i=0;i<crudeMenuItems.length;i++){
			localeMenuItem[i]=new MenuItem();
			localeMenuItem[i].setLinkValue(crudeMenuItems[i].getLinkValue());
			localeMenuItem[i].setDisplayName(parseDisplayName(crudeMenuItems[i].getDisplayName()));
		}
		return localeMenuItem;
	}
	
	/**
	 * Method to prepare display names by concatinating messages obtained from locale specific resource bundle.  
	 * @param displayNameKeys array of keys for that messages are to be obtained from resources
	 * @return concatenating display name in the first element of String array
	 */
	private static String[] parseDisplayName(String[] displayNameKeys)throws JspException{
		StringBuffer displayNameValue=new StringBuffer();
		for(int i=0;i<displayNameKeys.length;i++){
			String value = getLabel(displayNameKeys[i]);
			displayNameValue.append(value);
			displayNameValue.append(MenuConstants.SPACE);
		}
		return new String[] {displayNameValue.toString().trim()};
	}
	
	private static String getLabel(String key)throws JspException{
		return LabelTagUtils.getInstance().getLabel(pageContext,MenuConstants.MENU_RESOURCE_NAME,LabelTagUtils.getInstance().getUserPreferredLocaleObject(pageContext),key,null);
	}
}
