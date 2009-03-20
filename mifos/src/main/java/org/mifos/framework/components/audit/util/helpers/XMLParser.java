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
 
package org.mifos.framework.components.audit.util.helpers;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mifos.framework.exceptions.SystemException;
import org.mifos.core.ClasspathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLParser {

	private static XMLParser instance = new XMLParser();

	public static XMLParser getInstance() {
		return instance;
	}

	ColumnPropertyMapping columnPropertyMapping = null;

	public ColumnPropertyMapping parser() throws SystemException {
		Document document = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(ClasspathResource.getURI("org/mifos/framework/util/resources/audit/ColumnMapping.xml").toString());
			getColumnPropertyMapping(document);
		} catch (URISyntaxException e) {
			throw new SystemException(e);
		} catch (ParserConfigurationException e) {
			throw new SystemException(e);
		} catch (IOException e) {
			throw new SystemException(e);
		} catch (SAXParseException e) {
			throw new SystemException(e);
		} catch (SAXException e) {
			throw new SystemException(e);
		}
		return columnPropertyMapping;
	}

	public void getColumnPropertyMapping(Document document) {
		NodeList elements = document
				.getElementsByTagName(XMLConstants.COLUMNPROPERTYMAPPING);
		columnPropertyMapping = new ColumnPropertyMapping();
		columnPropertyMapping.setEntityTypes(getEntityTypes(((Element) elements
				.item(0)).getElementsByTagName(XMLConstants.ENTITYTYPE)));
	}

	public EntityType[] getEntityTypes(NodeList elements) {
		EntityType[] entityTypes = new EntityType[elements.getLength()];
		for (int j = 0; j < elements.getLength(); j++) {
			entityTypes[j] = new EntityType();
			entityTypes[j].setName(elements.item(j).getAttributes()
					.getNamedItem(XMLConstants.NAME).getNodeValue());
			
			entityTypes[j].setClassPath(elements.item(j).getAttributes()
					.getNamedItem(XMLConstants.CLASSPATH).getNodeValue());
			
			entityTypes[j].setEntitiesToLog(getEntitiesToLog(((Element) elements
								.item(j))
								.getElementsByTagName(XMLConstants.ENTITIES_TO_LOG)));
			
			entityTypes[j]
					.setPropertyNames(getPropertyNames(((Element) elements
							.item(j))
							.getElementsByTagName(XMLConstants.PROPERTYNAME)));
		}
		return entityTypes;
	}
	
	public EntitiesToLog getEntitiesToLog(NodeList elements) {
		EntitiesToLog entitiesToLog = new EntitiesToLog();
		for (int j = 0; j < elements.getLength(); j++) {
			entitiesToLog.setEntities(getEntity(((Element) elements
								.item(j))
								.getElementsByTagName(XMLConstants.ENTITY)));
		}
		return entitiesToLog;
	}
	
	public Entity[] getEntity(NodeList elements) {
		Entity[] entities = new Entity[elements.getLength()];
		for (int j = 0; j < elements.getLength(); j++) {
			entities[j]=new Entity();
			entities[j].setName(elements.item(j).getAttributes()
					.getNamedItem(XMLConstants.NAME).getNodeValue());
			if((elements.item(j).getAttributes().getNamedItem(XMLConstants.PARENTNAME))!=null){
				entities[j].setParentName(elements.item(j).getAttributes()
					.getNamedItem(XMLConstants.PARENTNAME).getNodeValue());
			}
			if((elements.item(j).getAttributes().getNamedItem(XMLConstants.MERGE_PROPERTIES))==null
					|| (elements.item(j).getAttributes().getNamedItem(XMLConstants.MERGE_PROPERTIES)).getNodeValue().equalsIgnoreCase("no")){
				entities[j].setMergeProperties("No");
			}else{
				entities[j].setMergeProperties(elements.item(j).getAttributes()
						.getNamedItem(XMLConstants.MERGE_PROPERTIES).getNodeValue());
				
				entities[j].setDisplayKey(elements.item(j).getAttributes()
				.getNamedItem(XMLConstants.DISPLAYKEY).getNodeValue());
			}
		}
		return entities;
	}


	public PropertyName[] getPropertyNames(NodeList elements) {
		PropertyName[] propertyNames = new PropertyName[elements.getLength()];
		for (int j = 0; j < elements.getLength(); j++) {
			propertyNames[j] = new PropertyName();
			propertyNames[j].setName(elements.item(j).getAttributes()
					.getNamedItem(XMLConstants.NAME).getNodeValue());
			if((elements.item(j).getAttributes().getNamedItem(XMLConstants.DISPLAYKEY))!=null){
				propertyNames[j].setDisplayKey(elements.item(j).getAttributes()
					.getNamedItem(XMLConstants.DISPLAYKEY).getNodeValue());
			}
			
			if((elements.item(j).getAttributes().getNamedItem(XMLConstants.PARENTNAME))!=null){
				propertyNames[j].setParentName(elements.item(j).getAttributes()
					.getNamedItem(XMLConstants.PARENTNAME).getNodeValue());
			}
			
			//If donotlog is not defined in xml, it will be default set to No
			if((elements.item(j).getAttributes().getNamedItem(XMLConstants.DONOTLOG))==null){
				propertyNames[j].setDoNotLog("No");
			}else{
				propertyNames[j].setDoNotLog(elements.item(j).getAttributes()
					.getNamedItem(XMLConstants.DONOTLOG).getNodeValue());
			}
			//If lookup is not defined in xml, it will be default set to No
			String lookUp=null;
			if((elements.item(j).getAttributes().getNamedItem(XMLConstants.LOOKUP))!=null){
				lookUp = elements.item(j).getAttributes().getNamedItem(XMLConstants.LOOKUP).getNodeValue();
				propertyNames[j].setLookUp(lookUp);
			}else{
				lookUp="No";
				propertyNames[j].setLookUp(lookUp);
			}

			if (lookUp.equalsIgnoreCase(XMLConstants.YES)) {
				
				if((elements.item(j).getAttributes().getNamedItem(XMLConstants.METHODNAME))!=null){
					propertyNames[j].setMethodName(elements.item(j).getAttributes().getNamedItem(XMLConstants.METHODNAME).getNodeValue());
				}else{
					if(((Element) elements.item(j)).getElementsByTagName(XMLConstants.ENTITYNAME)!=null){
						propertyNames[j].setEntityName(getEntityName(((Element) elements.item(j)).getElementsByTagName(XMLConstants.ENTITYNAME)));
					}
				}
			}

		}
		return propertyNames;
	}

	public EntityName getEntityName(NodeList elements) {
		EntityName entityName = new EntityName();
		entityName.setName(elements.item(0).getAttributes().getNamedItem(
				XMLConstants.NAME).getNodeValue());
		if (((Element) elements.item(0)).getElementsByTagName(
				XMLConstants.CLASSPATH).item(0) != null) {
			entityName.setClassPath(getClassPath(((Element) elements.item(0))
					.getElementsByTagName(XMLConstants.CLASSPATH)));
		}
		return entityName;
	}
	
	public ClassPath getClassPath(NodeList elements) {
		ClassPath classPath = new ClassPath();
		classPath.setPath(elements.item(0).getAttributes().getNamedItem(
				XMLConstants.PATH).getNodeValue());
		return classPath;
	}

	public static void main(String args[]) {
		XMLParser ttp = new XMLParser();
		ttp.parser();
	}

}
