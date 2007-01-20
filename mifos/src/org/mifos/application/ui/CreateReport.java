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
