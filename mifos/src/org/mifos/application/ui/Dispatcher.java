package org.mifos.application.ui;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.struts.tags.XmlBuilder;

public class Dispatcher extends HttpServlet {
	
	@Override
	protected void doGet(
		HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		XmlBuilder html = new XmlBuilder();
		html.startTag("html");
		html.startTag("head");
		html.startTag("title");
		html.text("Developer page");
		html.endTag("title");
		html.endTag("head");

		html.startTag("body");
		body(html);
		html.endTag("body");

		html.endTag("html");
		response.getWriter().write(html.getOutput());
	}

	private void body(XmlBuilder html) {
		html.startTag("p");
		html.startTag("a", "href", 
			"custSearchAction.do?method=" + CustomerConstants.GETHOMEPAGE);
		html.text("Home");
		html.endTag("a");
		html.endTag("p");

		html.startTag("p");
		html.text("This page is for features which aren't done yet, " +
			"or not intended for public consumption. ");
		html.endTag("p");

		html.startTag("p");
		html.text("Database version = " + 
			DatabaseVersionPersistence.APPLICATION_VERSION);
		html.endTag("p");
	}

}
