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
 
package org.mifos.application.ui;

import org.mifos.framework.struts.tags.XmlBuilder;

public class CreateReport {

	void createPage(XmlBuilder html) {
		html.startTag("html");
		Page.head(html, "Create report");

		html.startTag("body");
		createReportBody(html);
		html.endTag("body");

		html.endTag("html");
	}

	private void createReportBody(XmlBuilder html) {
		html.startTag("h3");
		html.text("Create report");
		html.endTag("h3");
		
		html.startTag("form", "method", "post");
		html.text("Report name:");
		html.singleTag("input", "name", "name");
		
		html.singleTag("input", "type", "submit");
		html.endTag("form");
	}

	public String post() {
		int newReportId = 1;
		return "" + newReportId;
	}

}
