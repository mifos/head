/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import java.io.IOException;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.mifos.config.business.MifosConfigurationManager;
import org.mifos.core.MifosResourceUtil;
import org.mifos.framework.exceptions.MenuParseException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.FilePaths;

/**
 * It parses xml, builds and return crude menu for application
 */
public class MenuParser {

    /**
     * Method to parse xml and return crude menu
     *
     * @return array of crude Menu objects
     */
    public static Menu[] parse() throws SystemException {
//        try {
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            SchemaFactory schfactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
//            schfactory.setErrorHandler(null);
//            Schema schema = schfactory.newSchema(new StreamSource(MifosResourceUtil.getClassPathResourceAsStream(FilePaths.MENUSCHEMA)));
//            factory.setNamespaceAware(false);
//            factory.setValidating(false);
//            factory.setSchema(schema);
//            DocumentBuilder builder = factory.newDocumentBuilder();
//            Document document = builder.parse(MifosResourceUtil.getClassPathResourceAsStream(FilePaths.MENUPATH));
//            NodeList tabNodeList = document.getElementsByTagName(MenuConstants.TOPMENUTAB);
//            Menu leftMenus[] = new Menu[tabNodeList.getLength()];
//            for (int i = 0; i < tabNodeList.getLength(); i++) {
//                leftMenus[i] = new Menu();
//                leftMenus[i].setTopMenuTabName(((Element) tabNodeList.item(i)).getAttribute(MenuConstants.NAME));
//                leftMenus[i].setMenuGroups(createMenuGroup(tabNodeList.item(i)));
//                String menuHeading = ((Element) tabNodeList.item(i)).getElementsByTagName(MenuConstants.LEFTMENULABEL)
//                        .item(0).getFirstChild().getTextContent().trim();
//                leftMenus[i].setMenuHeading(menuHeading);
//            }
//            return leftMenus;
//        } catch (SAXParseException spe) {
//            throw new MenuParseException(spe);
//        } catch (SAXException sxe) {
//            throw new MenuParseException(sxe);
//        } catch (ParserConfigurationException pce) {
//            throw new MenuParseException(pce);
//        } catch (IOException ioe) {
//            throw new MenuParseException(ioe);
//        }
        
        // FIXME - rewrite using JDOM to parse menu.xml - keithw
        try {
            SAXBuilder sb = new SAXBuilder();
            StreamSource source = new StreamSource(MifosResourceUtil.getClassPathResourceAsStream(FilePaths.MENUPATH)); 
            Document doc = sb.build(source.getInputStream());
            
            XPath xpath = XPath.newInstance("//" + MenuConstants.TOPMENUTAB);
            
            List tabNodeList = xpath.selectNodes(doc);
            Menu leftMenus[] = new Menu[tabNodeList.size()];
            int i = 0;
            for (Object object : tabNodeList) {
                Element tabNode = (Element) object;
                leftMenus[i] = new Menu();
                leftMenus[i].setTopMenuTabName(tabNode.getAttribute(MenuConstants.NAME).getValue());
                leftMenus[i].setMenuGroups(createMenuGroup(tabNode));
                String menuHeading = "testHeading";
//                String menuHeading = tabNode.getElementsByTagName(MenuConstants.LEFTMENULABEL).item(0).getFirstChild().getTextContent().trim();
                leftMenus[i].setMenuHeading(menuHeading);
                i++;
            }
            return leftMenus;
        }
        catch (JDOMException e) {
            throw new MenuParseException(e);
        }
        catch (IOException e) {
            throw new MenuParseException(e);
        }
    }
    
    private static MenuGroup[] createMenuGroup(Element leftMenu) throws SystemException {
        
        List menuGpNodeList = leftMenu.getChildren(MenuConstants.MENUGROUP);

        MenuGroup menuGroup[] = new MenuGroup[menuGpNodeList.size()];
        int i=0;
        for (Object object : menuGpNodeList) {
            Element menuGroupItem = (Element) object;
            menuGroup[i] = new MenuGroup();
            menuGroup[i].setDisplayName(new String[] {"test", "test"});
            
            MenuItem menuItems[] = new MenuItem[1];
            
            MenuItem menuItem = new MenuItem();
            menuItem.setDisplayName(new String[] {"displayName", "displayName2"});
            menuItem.setLinkValue("linkValue");
            
            menuItems[0] = menuItem;
            
            menuGroup[i].setMenuItems(menuItems);
            i++;
        }
        
        
        return menuGroup;
    }

//    /**
//     * Method to build crude MenuGroup objects for a given top-menu-tab
//     *
//     * @param leftMenu
//     *            is the root node for a top-menu-tab
//     * @return array of MenuGroup objects
//     */
//    static MenuGroup[] createMenuGroup(Node leftMenu) throws SystemException {
//        NodeList menuGpNodeList = ((Element) leftMenu).getElementsByTagName(MenuConstants.MENUGROUP);
//        MenuGroup menuGroup[] = new MenuGroup[menuGpNodeList.getLength()];
//        for (int i = 0; i < menuGpNodeList.getLength(); i++) {
//            menuGroup[i] = new MenuGroup();
//            menuGroup[i].setMenuItems(createMenuItems(menuGpNodeList.item(i)));
//            menuGroup[i].setDisplayName(getDisplayName((Element) menuGpNodeList.item(i)));
//        }
//        return menuGroup;
//    }
//
//    /**
//     * Method to build crude MenuItem objects for a MenuGroup
//     *
//     * @param menuGroup
//     *            is the root node for MenuGroup.
//     * @return array of MenuItem objects
//     */
//    static MenuItem[] createMenuItems(Node menuGroup) throws SystemException {
//        NodeList menuItemNodeList = ((Element) menuGroup).getElementsByTagName(MenuConstants.MENUITEM);
//        MenuItem menuItem[] = new MenuItem[menuItemNodeList.getLength()];
//        int hiddenItems = 0;
//        for (int i = 0, j = 0; i < menuItemNodeList.getLength(); i++) {
//            if (isMenuItemVisible((Element) menuItemNodeList.item(i))) {
//                menuItem[j] = new MenuItem();
//                menuItem[j].setDisplayName(getDisplayName((Element) menuItemNodeList.item(i)));
//                menuItem[j].setLinkValue(getLinkValue((Element) menuItemNodeList.item(i)));
//                j++;
//            } else {
//                hiddenItems++;
//            }
//        }
//        if (hiddenItems > 0) {
//            menuItem = removeEmptyMenuItems(menuItem, hiddenItems);
//        }
//        return menuItem;
//    }
//
//    private static MenuItem[] removeEmptyMenuItems(MenuItem menuItems[], int hiddenItems) {
//        MenuItem newMenuItems[] = new MenuItem[menuItems.length - hiddenItems];
//        for (int i = 0, j = 0; i < menuItems.length; i++) {
//            if (menuItems[i] != null) {
//                newMenuItems[j++] = menuItems[i];
//            }
//        }
//        return newMenuItems;
//    }
//
//    /**
//     * Uses boolean keys in the application-wide configuration to determine if
//     * menu items should be displayed. Missing keys will cause
//     * {@link java.util.NoSuchElementException} to be thrown. Keys are
//     * configured in menu.xml.
//     */
//    private static boolean isMenuItemVisible(Element element) {
//        String visiKey = element.getAttribute(MenuConstants.KEY_FOR_HIDDEN);
//        if (StringUtils.isNotBlank(visiKey)) {
//            MifosConfigurationManager cm = MifosConfigurationManager.getInstance();
//            return cm.getBoolean(visiKey);
//        }
//        // if attribute doesn't exist, a configuration key wasn't specified,
//        // so menu item must be visible
//        return true;
//    }
//
//    /**
//     * Method to get all keys for a displayname of a given node
//     *
//     * @param node
//     *            is the element whose display name is to be obtained
//     * @return array of keys respective to display name
//     */
//    static String[] getDisplayName(Element node) {
//        NodeList displaynameNodeList = node.getElementsByTagName(MenuConstants.DISPLAYNAME);
//        NodeList nameNodeList = ((Element) displaynameNodeList.item(0))
//                .getElementsByTagName(MenuConstants.NAMEFRAGMENT);
//        String displayNameStr[] = new String[nameNodeList.getLength()];
//        for (int i = 0; i < nameNodeList.getLength(); i++) {
//            displayNameStr[i] = nameNodeList.item(i).getFirstChild().getTextContent().trim();
//        }
//        return displayNameStr;
//    }
//
//    /**
//     * Method to get link value for a menu item
//     *
//     * @param node
//     *            is the menu item whose link is to be obtained
//     * @return link as string
//     */
//    static String getLinkValue(Element node) {
//        return node.getElementsByTagName(MenuConstants.LINKVALUE).item(0).getFirstChild().getTextContent().trim();
//    }
}
