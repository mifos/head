/**

 * MenuParser.java    version: 1.0

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
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

package org.mifos.framework.components.mifosmenu;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.configuration.business.CustomerConfig;
import org.mifos.framework.exceptions.MenuParseException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.org.apache.xml.internal.utils.DefaultErrorHandler;

/**
 * It parses xml, builds and return crude menu for application
 */
public class MenuParser {
	
	/**
	 * Method to parse xml and return crude menu
	 * @return array of crude Menu objects
	 */
	public static Menu[] parse()throws SystemException{
		try {
			 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			 SchemaFactory schfactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			 schfactory.setErrorHandler(new DefaultErrorHandler());
			 Schema schema = schfactory.newSchema(new StreamSource(new File(ResourceLoader.getURI(FilePaths.MENUSCHEMA))));
			 factory.setNamespaceAware(false);
			 factory.setValidating(false);
			 factory.setSchema(schema);
			 DocumentBuilder builder = factory.newDocumentBuilder();
			 Document document = builder.parse( new File(ResourceLoader.getURI(FilePaths.MENUPATH)) );
			 NodeList tabNodeList=document.getElementsByTagName(MenuConstants.TOPMENUTAB);
			 Menu leftMenus[]=new Menu[tabNodeList.getLength()];
			 for(int i=0;i<tabNodeList.getLength();i++){
			 	leftMenus[i]=new Menu();
			 	leftMenus[i].setTopMenuTabName(((Element)tabNodeList.item(i)).getAttribute(MenuConstants.NAME));
			 	leftMenus[i].setMenuGroups(createMenuGroup(tabNodeList.item(i)));
			 	String menuHeading=((Element)tabNodeList.item(i)).getElementsByTagName(MenuConstants.LEFTMENULABEL).item(0).getFirstChild().getTextContent().trim();
			 	leftMenus[i].setMenuHeading(menuHeading);
			 }
			 return leftMenus;
		  } catch (SAXParseException spe) {
		  		throw new MenuParseException(spe);
		  } catch (SAXException sxe) {
	  			throw new MenuParseException(sxe);
		  } catch (ParserConfigurationException pce) {
	  			throw new MenuParseException(pce);
		  } catch (IOException ioe) {
	  			throw new MenuParseException(ioe);
	  			
		  } catch (URISyntaxException urise) {
			throw new MenuParseException(urise);
		}
	}
	
	/**
	 * Method to build crude MenuGroup objects for a given top-menu-tab
	 * @param leftMenu is the root node for a top-menu-tab
	 * @return array of MenuGroup objects
	 */
	static MenuGroup[] createMenuGroup(Node leftMenu)throws SystemException{
		NodeList menuGpNodeList=((Element)leftMenu).getElementsByTagName(MenuConstants.MENUGROUP);
		MenuGroup menuGroup[]= new MenuGroup[menuGpNodeList.getLength()];
		for(int i=0;i<menuGpNodeList.getLength();i++){
			menuGroup[i]=new MenuGroup();
			menuGroup[i].setMenuItems(createMenuItems(menuGpNodeList.item(i)));
			menuGroup[i].setDisplayName(getDisplayName((Element)menuGpNodeList.item(i)));
		}
		return menuGroup;
	}

	/**
	 * Method to build crude MenuItem objects for a MenuGroup
	 * @param menuGroup is the root node for MenuGroup.
	 * @return array of MenuItem objects
	 */
	static MenuItem[] createMenuItems(Node menuGroup)throws SystemException{
		NodeList menuItemNodeList=((Element)menuGroup).getElementsByTagName(MenuConstants.MENUITEM);
		MenuItem menuItem[]= new MenuItem[menuItemNodeList.getLength()];
		int hiddenItems=0;
		for(int i=0,j=0;i<menuItemNodeList.getLength();i++){
			if(isMenuItemVisible((Element)menuItemNodeList.item(i))){				
				menuItem[j]=new MenuItem();
				menuItem[j].setDisplayName(getDisplayName((Element)menuItemNodeList.item(i)));
				menuItem[j].setLinkValue(getLinkValue((Element)menuItemNodeList.item(i)));
				j++;
			}else
				hiddenItems++;
		}
		if(hiddenItems>0)
			menuItem = removeEmptyMenuItems(menuItem, hiddenItems);
		return menuItem;
	}
	
	private static MenuItem[] removeEmptyMenuItems(MenuItem menuItems[], int hiddenItems){
		MenuItem newMenuItems[]= new MenuItem[menuItems.length-hiddenItems];
		for(int i=0,j=0;i<menuItems.length ;i++){
			if(menuItems[i]!=null)
				newMenuItems[j++]=menuItems[i];
		}
		return newMenuItems;
	}
	
	private static boolean isMenuItemVisible(Element element)throws SystemException{
		String methodName = element.getAttribute(MenuConstants.KEY_FOR_HIDDEN);
		if(methodName!=null && methodName!=""){
			CustomerConfig customerConfig = Configuration.getInstance().getCustomerConfig(Short.valueOf("1"));
			try{
				Method method = customerConfig.getClass().getMethod(methodName);
				return (Boolean)method.invoke(customerConfig);
			}
			catch(NoSuchMethodException nsme){
				new MenuParseException(nsme);
			}
			catch(InvocationTargetException ite){
				new MenuParseException(ite);
			}
			catch(IllegalAccessException iae){
				new MenuParseException(iae);
			}
		}
		return true;
	}
	
	/**
	 * Method to get all keys for a displayname of a given node
	 * @param node is the element whose display name is to be obtained
	 * @return array of keys respective to display name
	 */
	static String[] getDisplayName(Element node){
		NodeList displaynameNodeList=node.getElementsByTagName(MenuConstants.DISPLAYNAME);
		NodeList nameNodeList=((Element)displaynameNodeList.item(0)).getElementsByTagName(MenuConstants.NAMEFRAGMENT);
		String displayNameStr[] = new String[nameNodeList.getLength()];
		for(int i=0;i<nameNodeList.getLength();i++){
			displayNameStr[i]=nameNodeList.item(i).getFirstChild().getTextContent().trim();
		}
		return displayNameStr;
	}
	
	/**
	 * Method to get link value for a menu item
	 * @param node is the menu item whose link is to be obtained
	 * @return link as string
	 */	
	static String getLinkValue(Element node){
		return node.getElementsByTagName(MenuConstants.LINKVALUE).item(0).getFirstChild().getTextContent().trim();
	}
}
