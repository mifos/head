package org.mifos.application.ui;

import org.mifos.framework.struts.tags.XmlBuilder;

public class Page {

	static void head(XmlBuilder html, String title) {
		html.startTag("head");
		html.startTag("title");
		html.text(title);
		html.endTag("title");
		html.endTag("head");
	}

}
