package org.mifos.application.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

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
		String pathInfo = request.getPathInfo();
		if (pathInfo == null) {
			redirectTo(response, urlOf(request) + "/");
			return;
		}
		else if (pathInfo.equals("/")) {
			developerPage(html);
		}
		else if (pathInfo.equals("/reports/create")) {
			createPage(html);
		}
		else {
			errorPage(html, pathInfo);
			response.setStatus(404);
			return;
		}
		response.getWriter().write(html.getOutput());
	}

	private String urlOf(HttpServletRequest request) throws ServletException {
		try {
			URL url = new URL(request.getScheme(), 
				request.getServerName(), request.getServerPort(),
				request.getContextPath() + request.getServletPath());
			return url.toExternalForm();
		}
		catch (MalformedURLException e) {
			throw new ServletException(e);
		}
	}

	private void redirectTo(HttpServletResponse response, String url) {
		response.setStatus(303);
		response.setHeader("Location", url);
	}

	private void errorPage(XmlBuilder html, String pathInfo) {
		html.startTag("html");
		head(html, "Create report");

		html.startTag("body");
		html.text("Did not find page for " + pathInfo);
		html.endTag("body");

		html.endTag("html");
	}

	private void createPage(XmlBuilder html) {
		html.startTag("html");
		head(html, "Create report");

		html.startTag("body");
		createReportBody(html);
		html.endTag("body");

		html.endTag("html");
	}

	private void createReportBody(XmlBuilder html) {
	}

	private void developerPage(XmlBuilder html) {
		html.startTag("html");
		head(html, "Developer page");

		html.startTag("body");
		body(html);
		html.endTag("body");

		html.endTag("html");
	}

	private void head(XmlBuilder html, String title) {
		html.startTag("head");
		html.startTag("title");
		html.text(title);
		html.endTag("title");
		html.endTag("head");
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

		html.startTag("p");
		html.startTag("a", "href", "reports/create");
		html.text("Create report");
		html.endTag("a");
		html.endTag("p");
	}

}
