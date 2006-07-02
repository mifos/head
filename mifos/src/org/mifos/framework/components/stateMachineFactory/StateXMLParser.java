/**

 * StateXMLParser.java    version: 1.0



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
package org.mifos.framework.components.stateMachineFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.org.apache.xml.internal.utils.DefaultErrorHandler;

/**
 * @author rohitr
 *
 */
public class StateXMLParser {

	private StateXMLParser() {
		super();
	}

	private static StateXMLParser instance = new StateXMLParser();

	public static StateXMLParser getInstance() {
		return instance;
	}

	public Map<AccountStateEntity,List<AccountStateEntity>> loadMapFromXml(String filename, String configurationName) {

		Map<AccountStateEntity,List<AccountStateEntity>> transitionMap = new HashMap<AccountStateEntity,List<AccountStateEntity>>();

		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();

			factory.setNamespaceAware(true);
			factory.setValidating(true);
			factory.setAttribute(
					"http://java.sun.com/xml/jaxp/properties/schemaLanguage",
					"http://www.w3.org/2001/XMLSchema");

			// Specify our own schema - this overrides the schemaLocation in the
			// xml file
			factory.setAttribute(
					"http://java.sun.com/xml/jaxp/properties/schemaSource",
					"StateMachine.xsd");

			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setErrorHandler(new DefaultErrorHandler());
			Document document = builder.parse(new File(ResourceLoader
					.getURI(filename)));
			Node mapToprocess = null;
			/*String configurationName = "";
			if (stateConfiguration=="configuration1")
				configurationName = "1";
			else
				configurationName = "2";*/

			NodeList configMaps = document.getElementsByTagName("stateConfiguration");
			// NodeList configMaps = firstChild.getChildNodes();

			for (int m = 0; m < configMaps.getLength(); m++) {

				Node stateConfiguration = configMaps.item(m);
				if (configurationName.equals(stateConfiguration.getAttributes().getNamedItem("configurationName")
						.getNodeValue())) {
					mapToprocess = stateConfiguration;
				}

			}

			// NodeList stateList = firstChild.getChildNodes();
			NodeList stateList = mapToprocess.getChildNodes();
			for (int i = 0; i < stateList.getLength(); i++) {
				// each state has state id and possiblestates as childern
				Node state = stateList.item(i);

				// iterate for each child of state
				NodeList stateInfoList = state.getChildNodes();
				AccountStateEntity currentState = null;
				List<AccountStateEntity> currntPossibleStates = new ArrayList<AccountStateEntity>();
				for (int j = 0; j < stateInfoList.getLength(); j++) {
					Node info = stateInfoList.item(j);

					if ("stateid".equals(info.getLocalName())) {
						Element ele = (Element) info;
						currentState = new AccountStateEntity(Short.valueOf(((Text) ele
								.getFirstChild()).getData()));
					}
					if ("possiblestates".equals(info.getLocalName())) {
						// get all the childern
						NodeList allStates = info.getChildNodes();
						currntPossibleStates = new ArrayList<AccountStateEntity>();
						for (int k = 0; k < allStates.getLength(); k++) {
							Node infoState = allStates.item(k);
							NodeList eachPossiblechild = infoState
									.getChildNodes();
							for (int l = 0; l < eachPossiblechild.getLength(); l++) {

								Node eachPossiblechildelement = eachPossiblechild
										.item(l);

								if ("stateid".equals(eachPossiblechildelement
										.getLocalName())) {
									Element element = (Element) eachPossiblechildelement;
									Short possibleTrantionId = Short
											.valueOf(((Text) element
													.getFirstChild()).getData());
									 currntPossibleStates.add(new AccountStateEntity(possibleTrantionId));

								}
							}
						}

					}

				}

				if (currentState != null)
					transitionMap.put(currentState, currntPossibleStates);
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		} catch (SAXParseException e) {
			e.printStackTrace();

		} catch (SAXException e) {
			e.printStackTrace();

		} catch (URISyntaxException e) {
			e.printStackTrace();

		}
		return transitionMap;
	}

	public static void main(String[] arg) {
		StateXMLParser.getInstance().loadMapFromXml(
				"org/mifos/framework/util/resources/stateMachine/StateMachine_saving.xml",
				"configuration 1");
	}


}
