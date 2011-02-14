/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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
import org.mifos.framework.exceptions.TableTagTypeParserException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class TypeParser {

    private TypeParser() {
        super();
    }

    private static TypeParser instance = new TypeParser();

    public static TypeParser getInstance() {
        return instance;
    }

    public Files parser(String filename) throws TableTagTypeParserException {
        Files file = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            factory.setNamespaceAware(true);
            factory.setValidating(true);
            factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                    "http://www.w3.org/2001/XMLSchema");

            // Specify our own schema - this overrides the schemaLocation in the
            // xml file
            factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", "type.xsd");

            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(null);
            Document document = builder.parse(new File(ClasspathResource.getURI(filename)));
            Node fileNode = document.getFirstChild();
            file = new Files();
            file.setFileName(createFileName(fileNode));
        } catch (ParserConfigurationException pce) {
            throw new TableTagTypeParserException(pce);
        } catch (IOException ioe) {
            throw new TableTagTypeParserException(ioe);
        } catch (SAXParseException saxpe) {
            throw new TableTagTypeParserException(saxpe);
        } catch (SAXException saxe) {
            throw new TableTagTypeParserException(saxe);
        } catch (URISyntaxException urise) {
            throw new TableTagTypeParserException(urise);
        }
        return file;
    }

    protected FileName[] createFileName(Node file) throws TableTagTypeParserException {
        NodeList fileNameNodeList = ((Element) file).getElementsByTagName(TableTagConstants.FILENAME);
        if (fileNameNodeList.getLength() == 0) {
            throw new TableTagTypeParserException(fileNameNodeList.toString());
        }
        FileName fileName[] = new FileName[fileNameNodeList.getLength()];
        for (int i = 0; i < fileNameNodeList.getLength(); i++) {
            fileName[i] = new FileName();
            fileName[i].setName((fileNameNodeList.item(i).getAttributes().getNamedItem(TableTagConstants.NAME)
                    .getNodeValue()));
            fileName[i].setPath((fileNameNodeList.item(i).getAttributes().getNamedItem(TableTagConstants.PATH)
                    .getNodeValue()));
        }
        return fileName;
    }

}
