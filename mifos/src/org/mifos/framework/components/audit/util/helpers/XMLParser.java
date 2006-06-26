/**
 * 
 */
package org.mifos.framework.components.audit.util.helpers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.mifos.framework.components.cronjobs.SchedulerConstants;
import org.mifos.framework.components.customTableTag.ActionParam;
import org.mifos.framework.components.customTableTag.Column;
import org.mifos.framework.components.customTableTag.ColumnDetails;
import org.mifos.framework.components.customTableTag.HeaderDetails;
import org.mifos.framework.components.customTableTag.LinkDetails;
import org.mifos.framework.components.customTableTag.Row;
import org.mifos.framework.components.customTableTag.Table;
import org.mifos.framework.components.customTableTag.TableTagConstants;
import org.mifos.framework.components.customTableTag.TableTagParser;
import org.mifos.framework.exceptions.TableTagParseException;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;
import com.sun.org.apache.xml.internal.utils.DefaultErrorHandler;

/**
 * @author krishankg
 * 
 */
public class XMLParser {

	private static XMLParser instance = new XMLParser();

	public static XMLParser getInstance() {
		return instance;
	}

	ColumnPropertyMapping columnPropertyMapping = null;

	public ColumnPropertyMapping parser() {
		Table table = null;
		Document document = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder
					.parse(ResourceLoader
							.getURI(
									"org/mifos/framework/util/resources/audit/ColumnMapping.xml")
							.toString());

			getColumnPropertyMapping(document);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXParseException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
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
			entityTypes[j]
					.setPropertyNames(getPropertyNames(((Element) elements
							.item(j))
							.getElementsByTagName(XMLConstants.PROPERTYNAME)));
		}
		return entityTypes;
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
			
			//If lonotlog is not defined in xml, it will be default set to No
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
				propertyNames[j]
						.setEntityName(getEntityName(((Element) elements
								.item(j))
								.getElementsByTagName(XMLConstants.ENTITYNAME)));
			}

		}
		return propertyNames;
	}

	public EntityName getEntityName(NodeList elements) {
		EntityName entityName = new EntityName();
		entityName.setName(elements.item(0).getAttributes().getNamedItem(
				XMLConstants.NAME).getNodeValue());

		if (((Element) elements.item(0)).getElementsByTagName(
				XMLConstants.CLASSPATH).item(0) != null
				&& ((Element) elements.item(0)).getElementsByTagName(
						XMLConstants.PKCOLUMN).item(0) != null) {
			entityName.setClassPath(getClassPath(((Element) elements.item(0))
					.getElementsByTagName(XMLConstants.CLASSPATH)));
			entityName.setPkColumn(getPkColumn(((Element) elements.item(0))
					.getElementsByTagName(XMLConstants.PKCOLUMN)));
		}
		return entityName;
	}

	public ClassPath getClassPath(NodeList elements) {
		ClassPath classPath = new ClassPath();
		classPath.setPath(elements.item(0).getAttributes().getNamedItem(
				XMLConstants.PATH).getNodeValue());
		return classPath;
	}

	public PkColumn getPkColumn(NodeList elements) {
		PkColumn pkColumn = new PkColumn();
		pkColumn.setName(elements.item(0).getAttributes().getNamedItem(
				XMLConstants.NAME).getNodeValue());
		return pkColumn;
	}

	public static void main(String args[]) {
		XMLParser ttp = new XMLParser();
		ttp.parser();
	}

}
