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
	
	/**
	   HTTP status to send when we are redirecting from a POST which
	   creates a resource to the URL of that new resource.

	   RFC2616 seems to say 201 ("created") is
	   the right status for this.  But with Firefox 2.0.0.1, 201
	   didn't seem to work (I think it was ignoring the Location
	   header).  So we'll stick with the 303; that seems
	   appropriate (if less specific).
	 */
	public static final int CREATED = 303;

	@Override
	protected void doGet(
		HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		XmlBuilder html = new XmlBuilder();
		String pathInfo = request.getPathInfo();
		if (pathInfo == null) {
			redirectTo(response, stringUrlOf(request) + "/");
			return;
		}
		else if (pathInfo.equals("/")) {
			String contextPath = request.getContextPath();
			developerPage(contextPath, html);
		}
		else if (isCreateReport(pathInfo)) {
			new CreateReport().createPage(html);
		}
		else if (pathInfo.startsWith("/reports/")) {
			String reportIdString = pathInfo.substring("/reports/".length());
			try {
				int reportId = Integer.parseInt(reportIdString);
				redirectToReport(response, request, reportId);
			}
			catch (NumberFormatException notValidNumber) {
				errorPage(html, pathInfo);
				response.setStatus(404);
			}
		}
		else {
			errorPage(html, pathInfo);
			response.setStatus(404);
		}
		response.getWriter().write(html.getOutput());
	}

	private void redirectToReport(
		HttpServletResponse response, HttpServletRequest request, 
		int reportId) throws ServletException {
		URL url;
		try {
			url = new URL(request.getScheme(), 
					request.getServerName(), request.getServerPort(),
					request.getContextPath() +
					"/reportsUserParamsAction.do" +
						"?method=loadAddList&reportId=" + reportId
					);
		}
		catch (MalformedURLException e) {
			throw new ServletException(e);
		}
		redirectTo(response, url.toExternalForm());
	}

	@Override
	protected void doPost(
		HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException {
		String pathInfo = request.getPathInfo();

		if (isCreateReport(pathInfo)) {
			String url = new CreateReport().post();
			response.setStatus(CREATED);
			response.setHeader("Location", absolutify(url, request));
		}
		else {
			XmlBuilder html = new XmlBuilder();
			/* Normally a POST should result in a redirect to a GET.
			   But I guess this is an exception (?).  It doesn't seem like
			   users would get to this page nearly as often as the GET 404.
			 */
			errorPage(html, pathInfo);
			response.setStatus(404);
	
			response.getWriter().write(html.getOutput());
		}
	}

	private String absolutify(String url, HttpServletRequest request) 
	throws ServletException {
		try {
			URL base = urlOf(request);
			return new URL(base, url).toExternalForm();
		}
		catch (MalformedURLException e) {
			throw new ServletException(e);
		}
	}

	private boolean isCreateReport(String pathInfo) {
		return pathInfo.equals("/reports/create");
	}

	private String stringUrlOf(HttpServletRequest request) 
	throws ServletException {
		return urlOf(request).toExternalForm();
	}

	private URL urlOf(HttpServletRequest request) throws ServletException {
		try {
			String pathInfo = request.getPathInfo();
			URL url = new URL(request.getScheme(), 
				request.getServerName(), request.getServerPort(),
				request.getContextPath() + request.getServletPath() +
				(pathInfo == null ? "" : pathInfo));
			return url;
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
		Page.head(html, "Not found");

		html.startTag("body");
		html.text(pathInfo + " not found");
		html.endTag("body");

		html.endTag("html");
	}

	private void developerPage(String contextPath, XmlBuilder html) {
		html.startTag("html");
		Page.head(html, "Developer page");

		html.startTag("body");
		body(contextPath, html);
		html.endTag("body");

		html.endTag("html");
	}

	private void body(String contextPath, XmlBuilder html) {
		html.startTag("p");
		html.startTag("a", "href", 
			contextPath +
			"/custSearchAction.do?method=" + CustomerConstants.GETHOMEPAGE);
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
