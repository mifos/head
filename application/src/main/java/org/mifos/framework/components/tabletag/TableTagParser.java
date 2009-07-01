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

package org.mifos.framework.components.tabletag;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mifos.core.ClasspathResource;
import org.mifos.framework.exceptions.TableTagParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class TableTagParser {

    private TableTagParser() {
        super();
    }

    private static TableTagParser instance = new TableTagParser();

    public static TableTagParser getInstance() {
        return instance;
    }

    public Table parser(String filename) throws TableTagParseException {
        Table table = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            factory.setNamespaceAware(true);
            factory.setValidating(true);
            factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                    "http://www.w3.org/2001/XMLSchema");

            // Specify our own schema - this overrides the schemaLocation in the
            // xml file
            factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", "tabletag.xsd");

            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(null);
            Document document = builder.parse(new File(ClasspathResource.getURI(filename)));
            /*
             * NodeList tableNodeList =
             * document.getElementsByTagName(TableTagConstants.TABLE); table =
             * new Table[tableNodeList.getLength()];
             * 
             * for (int i = 0; i < table.length; i++) { table[i] = new Table();
             * table[i].setRow(createRow(tableNodeList.item(i))); }
             */
            Node tableNode = document.getFirstChild();
            table = new Table();
            table.setPath(createPath(tableNode));
            table.setPageRequirements(createPageRequirements(tableNode));
            table.setRow(createRow(tableNode));

        } catch (ParserConfigurationException pce) {
            throw new TableTagParseException(pce);
        } catch (IOException ioe) {
            throw new TableTagParseException(ioe);
        } catch (SAXParseException saxpe) {
            throw new TableTagParseException(saxpe);
        } catch (SAXException saxe) {
            throw new TableTagParseException(saxe);
        } catch (URISyntaxException urise) {
            throw new TableTagParseException(urise);
        }
        return table;
    }

    protected Row[] createRow(Node table) throws TableTagParseException {
        NodeList rowNodeList = ((Element) table).getElementsByTagName(TableTagConstants.ROW);
        if (rowNodeList.getLength() == 0) {
            throw new TableTagParseException(rowNodeList.toString());
        }
        Row row[] = new Row[rowNodeList.getLength()];
        for (int i = 0; i < rowNodeList.getLength(); i++) {
            row[i] = new Row();
            row[i].setColumn(createColumn(rowNodeList.item(i)));
            row[i].setTdrequired((rowNodeList.item(i).getAttributes().getNamedItem(TableTagConstants.TDREQUIRED)
                    .getNodeValue()));
            row[i].setSuppressrow((rowNodeList.item(i).getAttributes().getNamedItem(TableTagConstants.SUPRESSROW)
                    .getNodeValue()));
        }
        return row;
    }

    protected Path[] createPath(Node table) throws TableTagParseException {
        NodeList pathNodeList = ((Element) table).getElementsByTagName(TableTagConstants.PATH);
        if (pathNodeList.getLength() == 0) {
            throw new TableTagParseException(TableTagConstants.UNEXPECTED_ERROR);
        }
        Path path[] = new Path[pathNodeList.getLength()];
        for (int i = 0; i < pathNodeList.getLength(); i++) {
            path[i] = new Path();
            path[i].setKey((pathNodeList.item(i).getAttributes().getNamedItem(TableTagConstants.KEY).getNodeValue()));
            path[i].setAction((pathNodeList.item(i).getAttributes().getNamedItem(TableTagConstants.PATHACTION)
                    .getNodeValue()));
            path[i].setForwardkey((pathNodeList.item(i).getAttributes().getNamedItem(TableTagConstants.FORWARDKEY)
                    .getNodeValue()));
        }
        return path;
    }

    protected PageRequirements createPageRequirements(Node table) throws TableTagParseException {
        NodeList pageNodeList = ((Element) table).getElementsByTagName(TableTagConstants.PAGEREQUIREMENTS);

        PageRequirements pageRequirements = new PageRequirements();

        pageRequirements.setNumbersRequired((pageNodeList.item(0).getAttributes().getNamedItem(
                TableTagConstants.NUMBERSREQUIRED).getNodeValue()));
        pageRequirements.setHeadingRequired((pageNodeList.item(0).getAttributes().getNamedItem(
                TableTagConstants.HEADINGREQUIRED).getNodeValue()));
        pageRequirements.setBluelineRequired((pageNodeList.item(0).getAttributes().getNamedItem(
                TableTagConstants.BLUELINEREQUIRED).getNodeValue()));
        pageRequirements.setTopbluelineRequired((pageNodeList.item(0).getAttributes().getNamedItem(
                TableTagConstants.TOPBLUELINEREQUIRED).getNodeValue()));
        pageRequirements.setBottombluelineRequired((pageNodeList.item(0).getAttributes().getNamedItem(
                TableTagConstants.BOTTOMBLUELINEREQUIRED).getNodeValue()));
        pageRequirements.setValignnumbers((pageNodeList.item(0).getAttributes().getNamedItem(
                TableTagConstants.VALIGNNUMBERS).getNodeValue()));
        pageRequirements.setBlanklinerequired((pageNodeList.item(0).getAttributes().getNamedItem(
                TableTagConstants.BLANKLINEREQUIRED).getNodeValue()));
        pageRequirements.setFlowRequired((pageNodeList.item(0).getAttributes().getNamedItem(
                TableTagConstants.FLOWREQUIRED).getNodeValue()));
        return pageRequirements;
    }

    protected Column[] createColumn(Node row) throws TableTagParseException {
        NodeList columnNodeList = ((Element) row).getElementsByTagName(TableTagConstants.COLUMN);
        if (columnNodeList.getLength() == 0) {
            throw new TableTagParseException(TableTagConstants.UNEXPECTED_ERROR);
        }
        Column column[] = new Column[columnNodeList.getLength()];
        for (int i = 0; i < columnNodeList.getLength(); i++) {
            column[i] = new Column();
            setColumnAttributes(column[i], columnNodeList.item(i).getAttributes());
            column[i].setDisplayname(createDisplayName(columnNodeList.item(i)));
            column[i].setParameters(createParameters(columnNodeList.item(i)));
            if ("link".equals(column[i].getType())) {
                if (null == column[i].getAction()) {
                    throw new TableTagParseException(TableTagConstants.UNEXPECTED_ERROR);
                }
            }
        }
        return column;
    }

    protected void setColumnAttributes(Column column, NamedNodeMap columnNode) throws TableTagParseException {
        column.setLabel(getAttributeValue(columnNode.getNamedItem(TableTagConstants.LABEL)));
        column.setLabeltype(getAttributeValue(columnNode.getNamedItem(TableTagConstants.LABELTYPE)));
        column.setBoldlabel(getAttributeValue(columnNode.getNamedItem(TableTagConstants.BOLDLABEL)));
        column.setType(getAttributeValue(columnNode.getNamedItem(TableTagConstants.TYPE)));
        column.setAction(getAttributeValue(columnNode.getNamedItem(TableTagConstants.ACTION)));
        column.setImage(getAttributeValue(columnNode.getNamedItem(TableTagConstants.IMAGE)));
        column.setIsLinkOptional(getAttributeValue(columnNode.getNamedItem(TableTagConstants.ISLINKOPTIONAL)));
        column.setCheckLinkOptionalRequired(getAttributeValue(columnNode
                .getNamedItem(TableTagConstants.CHECKOPTIONALREQUIRED)));
        column.setStyleClass(getAttributeValue(columnNode.getNamedItem(TableTagConstants.STYLECLASS)));
    }

    protected String getAttributeValue(Node node) {
        if (node != null)
            return node.getNodeValue();
        else
            return null;
    }

    protected DisplayName createDisplayName(Node column) throws TableTagParseException {
        NodeList displayNodeList = ((Element) column).getElementsByTagName(TableTagConstants.DISPLAYNAME);
        DisplayName displayName = new DisplayName();
        setFragmentDetails(displayName, displayNodeList.item(0));
        displayName.setBold((displayNodeList.item(0).getAttributes().getNamedItem(TableTagConstants.BOLDDISPLAY)
                .getNodeValue()));
        return displayName;
    }

    protected void setFragmentDetails(DisplayName displayName, Node displayNode) throws TableTagParseException {
        NodeList fragmentNameNodeList = ((Element) displayNode).getElementsByTagName(TableTagConstants.FRAGMENTNAME);
        Fragment[] fragment = new Fragment[fragmentNameNodeList.getLength()];
        for (int i = 0; i < fragmentNameNodeList.getLength(); i++) {
            fragment[i] = new Fragment();
            setFragmentAttributes(fragment[i], fragmentNameNodeList.item(i).getAttributes());
        }
        displayName.setFragment(fragment);
    }

    protected void setFragmentAttributes(Fragment fragment, NamedNodeMap fragmentNode) throws TableTagParseException {
        fragment.setFragmentName(getAttributeValue(fragmentNode.getNamedItem(TableTagConstants.NAME)));
        fragment.setFragmentType(getAttributeValue(fragmentNode.getNamedItem(TableTagConstants.TYPE)));
        fragment.setBold(getAttributeValue(fragmentNode.getNamedItem(TableTagConstants.BOLDDISPLAY)));
        fragment.setItalic(getAttributeValue(fragmentNode.getNamedItem(TableTagConstants.ITALICDISPLAY)));
    }

    protected Parameters createParameters(Node column) throws TableTagParseException {
        NodeList displayNodeList = ((Element) column).getElementsByTagName(TableTagConstants.PARAMETERS);
        Parameters parameters = new Parameters();
        if (displayNodeList.getLength() > 0) {
            setParameterDetails(parameters, displayNodeList.item(0));
        }
        return parameters;
    }

    protected void setParameterDetails(Parameters parameters, Node parameterNode) throws TableTagParseException {
        NodeList paramNameNodeList = ((Element) parameterNode).getElementsByTagName(TableTagConstants.PARAMETER);
        Param[] param = new Param[paramNameNodeList.getLength()];
        for (int i = 0; i < paramNameNodeList.getLength(); i++) {
            param[i] = new Param();
            setParamAttributes(param[i], paramNameNodeList.item(i).getAttributes());
        }
        parameters.setParam(param);
    }

    protected void setParamAttributes(Param param, NamedNodeMap paramNode) throws TableTagParseException {
        param.setParameterName(getAttributeValue(paramNode.getNamedItem(TableTagConstants.NAME)));
        param.setParameterValue(getAttributeValue(paramNode.getNamedItem(TableTagConstants.VALUE)));
        param.setParameterValueType(getAttributeValue(paramNode.getNamedItem(TableTagConstants.VALUETYPE)));
    }

}
