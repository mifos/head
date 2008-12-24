/**

 * TableTag.java    version: 1.0



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
package org.mifos.framework.components.tabletag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.config.Localization;
import org.mifos.framework.exceptions.HibernateSearchException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.TableTagException;
import org.mifos.framework.exceptions.TableTagParseException;
import org.mifos.framework.exceptions.TableTagTypeParserException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.http.request.RequestConstants;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.MifosTagUtils;
import org.mifos.framework.struts.tags.XmlBuilder;
import org.mifos.framework.util.helpers.Cache;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.FileCacheRep;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.FilePaths;

/**
 * This class renders the table list.
 */
public class TableTag extends BodyTagSupport {

	public static final String ALL_BRANCHES = "0";

	private int pageSize = Integer
			.valueOf(TableTagConstants.MIFOSTABLE_PAGESIZE);

	private static final long serialVersionUID = 1L;

	/** Used to set the name of XML and JSP file */
	private String name;

	/** Used to set the key */
	public String key;

	/** Used to set the width of the table */
	public String width;

	/** Used to set the border of the table */
	public String border;

	/** Used to set the cellspacing of the table */
	public String cellspacing;

	/** Used to set the cellpadding of the table */
	public String cellpadding;

	/** Used to set the xml file type whether we are taking single or multiple */
	// TODO: should be enum {single, multiple} not string
	public String type;

	private String className;

	public TableTag() {
	}

	public TableTag(String type) {
		this.type = type;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Function get the name of XML and JSP file
	 * 
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Function set the name of XML and JSP file
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Function get the value of the border
	 */
	public String getBorder() {
		return border;
	}

	/**
	 * Function set the value of the border
	 */
	public void setBorder(String border) {
		this.border = border;
	}

	/**
	 * Function get the value of the cellpadding
	 */
	public String getCellpadding() {
		return cellpadding;
	}

	/**
	 * Function set the value of the cellpadding
	 */
	public void setCellpadding(String cellpadding) {
		this.cellpadding = cellpadding;
	}

	/**
	 * Function get the value of the cellspacing
	 */
	public String getCellspacing() {
		return cellspacing;
	}

	/**
	 * Function set the value of the cellspacing
	 */
	public void setCellspacing(String cellspacing) {
		this.cellspacing = cellspacing;
	}

	public String getWidth() {
		return width;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	// Object of the Table class
	int size = 0;

	int current = 1;

	/**
	 * Function to render the tag
	 */
	@Override
	public int doStartTag() throws JspException {
		// call the helper method to display the result.
		try {
			// Value of the list which we are getting from the Cache
			List list = getList();
			if (list == null) {
				return super.doStartTag();
			}
			if (list.size() == 0) {
				JspWriter out = pageContext.getOut();
				XmlBuilder result;
				result = noResults((String) SessionUtils.getAttribute(
						Constants.OFFICE_NAME, (HttpServletRequest) pageContext
								.getRequest()), (String) SessionUtils
						.getAttribute(Constants.BRANCH_ID,
								(HttpServletRequest) pageContext.getRequest()),
						(String) SessionUtils.getAttribute(
								Constants.SEARCH_STRING,
								(HttpServletRequest) pageContext.getRequest()));
				out.write(result.getOutput());
				return super.doStartTag();
			}
			getTableData(list);
		} catch (TableTagException tte) {
			throw new JspException(tte);
		} catch (TableTagParseException ttpe) {
			throw new JspException(ttpe);
		} catch (TableTagTypeParserException tttpe) {
			throw new JspException(tttpe);
		} catch (IOException ioe) {
			throw new JspException(ioe);
		} catch (HibernateSearchException hse) {
			throw new JspException(hse);
		} catch (PageExpiredException e) {
				throw new JspException(e);
		}
		return super.doStartTag();

	}

	XmlBuilder noResults(String officeName, String officeId, String searchString) {
		Locale locale = Localization.getInstance().getMainLocale();
		ResourceBundle resources = ResourceBundle.getBundle
					(FilePaths.CUSTOMER_UI_RESOURCE_PROPERTYFILE, locale);
		XmlBuilder html = new XmlBuilder();
		html.startTag("table", "width", "96%", "border", "0", "cellpadding",
				"0", "cellspacing", "0");
		html.startTag("tr", "class", "fontnormal");
		html.startTag("td", "colspan", "2", "valign", "top");
		html.singleTag("br");
		html.startTag("span", "class", "headingorange");
		html.text(resources.getString("Customer.noResultsFoundFor") + " ");
		html.startTag("span", "class", "heading");
		html.text(searchString);
		html.text(" ");
		html.endTag("span");
		String inString = resources.getString("Customer.in");
		if (officeId.equals(ALL_BRANCHES)) {
			renderInClause(html, resources.getString("Customer.allBranches"), inString);
		} else {
			renderInClause(html, officeName, inString);
		}

		html.endTag("span");
		html.endTag("td");
		html.endTag("tr");
		if (type.equalsIgnoreCase("single")) {
			html.startTag("tr");
			html.startTag("td", "colspan", "2", "valign", "top", "class",
					"blueline");
			html.singleTag("br");
			html.singleTag("img", "src", "pages/framework/images/trans.gif",
					"width", "5", "height", "3");
			html.endTag("td");
			html.endTag("tr");
		}

		html.endTag("table");
		return html;
	}

	private void renderInClause(XmlBuilder html, String myOfficeName, String inString) {
		html.startTag("span", "class", "headingorange");
		html.text(" " + inString);
		html.endTag("span");

		html.text(" ");

		html.startTag("span", "class", "heading");
		html.text(myOfficeName);
		html.endTag("span");
	}

	private void createStartTable(StringBuilder result,
			boolean headingRequired, boolean topBlueLineRequired)
			throws PageExpiredException {
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		UserContext userContext = (UserContext)request.getSession().getAttribute(LoginConstants.USERCONTEXT);
		Locale locale = userContext.getPreferredLocale();
		ResourceBundle resources = ResourceBundle.getBundle
					(FilePaths.CUSTOMER_UI_RESOURCE_PROPERTYFILE, locale);
		if (headingRequired) {
			String searchString = (String) SessionUtils.getAttribute(
					Constants.SEARCH_STRING, (HttpServletRequest) pageContext
							.getRequest());
			String officeName = (String) SessionUtils.getAttribute(
					Constants.OFFICE_NAME, (HttpServletRequest) pageContext
							.getRequest());
			result
					.append(
							"<table width=\"96%\" border=\"0\" cellpadding=\"3\" cellspacing=\"0\">")
					.append(
							"<tr class=\"fontnormal\"><td colspan=\"2\" valign=\"top\"><span class=\"headingorange\">")
					.append(size + " " + resources.getString("Customer.resultsFor") + " </span>");
			result.append("<span class=\"heading\">"
					+ MifosTagUtils.xmlEscape(searchString) + " </span>");
			String officeId = (String) SessionUtils.getAttribute(
					Constants.BRANCH_ID, (HttpServletRequest) pageContext
							.getRequest());
			if (officeId != null && officeId.equals("0")) {
				result
						.append("<span class=\"headingorange\">" + resources.getString("Customer.in") + "</span> <span class=\"heading\">"
								+ resources.getString("Customer.allBranches") + "</span>");
			}

			else {
				result
						.append("<span class=\"headingorange\">" + resources.getString("Customer.in") + "</span> <span class=\"heading\">"
								+ MifosTagUtils.xmlEscape(officeName)
								+ "</span>");
			}

			result.append("</td></tr></table>");
			result
					.append("<tr><td colspan=\"2\" valign=\"top\">")
					.append(
							"<img src=\"pages/framework/images/trans.gif \" width=\"5\" height=\"3\"></td></tr>");
		}
		result.append("<table width=" + width + "border=" + border
				+ "cellspacing=" + cellspacing + "cellpadding=" + cellpadding
				+ " >");
		if (topBlueLineRequired) {
			result
					.append(
							"<tr><td colspan=\"2\" valign=\"top\" class=\"blueline\">")
					.append(
							"<img src=\"pages/framework/images/trans.gif \" width=\"5\" height=\"3\"></td></tr>");
		}
	}

	/**
	 * Function used to create end part of the table.
	 */
	void createEndTable(StringBuilder result, boolean bottomBlueLineRequired) {
		if (bottomBlueLineRequired) {
			result
					.append(
							"<tr><td colspan=\"2\" valign=\"top\" class=\"blueline\">")
					.append(
							"<img src=\"pages/framework/images/trans.gif \" width=\"10\" height=\"5\"></td></tr>")
					.append(
							"<tr><td height=\"35\" colspan=\"2\" align=\"center\" class=\"blueline\">")
					.append(
							"<table width=\"400\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		} else {
			result
					.append(
							"<tr><td colspan=\"2\" valign=\"top\" class=\"blueline\">")
					.append(
							"<img src=\"pages/framework/images/trans.gif \" width=\"5\" height=\"3\"></td></tr>")
					.append(
							"<tr><td height=\"35\" colspan=\"2\" align=\"center\">")
					.append(
							"<table width=\"400\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		}
	}

	/**
	 * Function to get single xml file.
	 */
	String getSingleFile(Locale locale) throws JspException {
		ResourceBundle resource = ResourceBundle
				.getBundle(FilePaths.TABLE_TAG_PROPERTIESFILE, locale);
		String xmlPath = resource.getString(name + "_xml");
		if (xmlPath != null) {
			return xmlPath;
		} else
			throw new JspException(resource
					.getString(TableTagConstants.NOXMLFILE_ERROR));
	}

	/**
	 * Function to get the action
	 */
	private String getAction(Table table) throws JspException {

		String action = (String) SessionUtils.getAttribute("action",
				pageContext.getSession());
		String forwardkey = (String) SessionUtils.getAttribute("forwardkey",
				pageContext.getSession());
		ResourceBundle resource = ResourceBundle
				.getBundle(FilePaths.TABLE_TAG_PROPERTIESFILE);
		if (null == action || null == forwardkey) {
			Path pathVar = table.findPath(key);
			action = pathVar.getAction();
			forwardkey = pathVar.getForwardkey();
			if (action != null) {
				SessionUtils.setRemovableAttribute("action", action,
						TableTagConstants.PATH, pageContext.getSession());
			} else
				throw new JspException(resource
						.getString(TableTagConstants.NOACTION_ERROR));
			if (forwardkey != null) {
				SessionUtils.setRemovableAttribute("forwardkey", forwardkey,
						TableTagConstants.PATH, pageContext.getSession());
			} else
				throw new JspException(resource
						.getString(TableTagConstants.NOFWDKEY_ERROR));
		}

		return action;
	}

	/**
	 * A helper method to get table object
	 * 
	 * @param xmlFilePath
	 *            path of the xml file.
	 * @param name
	 *            for type=single name is simple xml file name.for type=multiple
	 *            we are taking last substring of name attribut of the tag.
	 * @return table object.
	 */
	Table helperCache(String xmlFilePath, String key)
			throws TableTagParseException {
		FileCacheRep fileCacheRep = FileCacheRep.getInstance();
		Table table = null;
		if (!fileCacheRep.isKeyPresent(key)) {
			table = TableTagParser.getInstance().parser(xmlFilePath);
			fileCacheRep.addTOCacherep(key, table);
		} else {
			table = fileCacheRep.getFromCacheRep(key);
		}
		return table;
	}

	private void getTableData(List list) throws TableTagException,
			TableTagParseException, JspException, TableTagTypeParserException,
			IOException, PageExpiredException {
		Locale locale = getLocale();
		ResourceBundle resource = ResourceBundle
				.getBundle(FilePaths.TABLE_TAG_PROPERTIESFILE);

		String currentPage = ((HttpServletRequest) pageContext.getRequest())
				.getParameter("current");
		Integer currentValue = null;
		if (currentPage != null && !currentPage.equals("")) {
			currentValue = Integer.valueOf(currentPage);
		} else {
			currentValue = 1;
		}
		if (type.equalsIgnoreCase("single")) {
			getSingleData(list, locale, currentValue);
		} else if (type.equalsIgnoreCase("multiple")) {
			getMultipleData(list, locale, currentValue);
		} else
			throw new TableTagException(resource
					.getString(TableTagConstants.WRONGTYPE_ERROR));
	}

	private void getSingleData(List list, Locale locale, Integer currentValue)
			throws TableTagParseException, TableTagException, JspException,
			IOException, PageExpiredException {
		String xmlFilePath = getSingleFile(locale);
		Table table = helperCache(xmlFilePath, name);
		ResourceBundle resource = ResourceBundle
				.getBundle(FilePaths.TABLE_TAG_PROPERTIESFILE);
		if (table != null) {
			StringBuilder result = new StringBuilder();
			JspWriter out = pageContext.getOut();
			boolean headingRequired = table.getPageRequirements()
					.getHeadingRequired().equalsIgnoreCase("true");
			boolean topBlueLineRequired = table.getPageRequirements()
					.getTopbluelineRequired().equalsIgnoreCase("true");
			createStartTable(result, headingRequired, topBlueLineRequired);
			out.write(result.toString());
			displayData(list, table, locale, currentValue);
		} else
			throw new JspException(resource
					.getString(TableTagConstants.TABLENOTFOUND_ERROR));
	}

	private void getMultipleData(List list, Locale locale, Integer currentValue)
			throws TableTagTypeParserException, TableTagParseException,
			TableTagException, JspException, IOException, PageExpiredException {
		StringBuilder result = new StringBuilder();
		JspWriter out = pageContext.getOut();
		createStartTable(result, true, false);
		ResourceBundle resource = ResourceBundle.getBundle(FilePaths.TABLE_TAG_PROPERTIESFILE, locale);
				
		out.write(result.toString());
		int number = ((currentValue - 1) * pageSize);
		for (Object object : list) {
			Table table = helperMultipleData(object, locale);
			if (table != null) {
				displayDataMultiple(object, table, ++number, locale);
			} else
				throw new JspException(resource
						.getString(TableTagConstants.TABLENOTFOUND_ERROR));
		}
		result = new StringBuilder();
		createEndTable(result, false);
		String action = (String) SessionUtils.getAttribute("action",
				pageContext.getSession());
		out.write(result.toString());
		String currentFlowkey = (String) ((HttpServletRequest) pageContext
				.getRequest()).getAttribute(Constants.CURRENTFLOWKEY);
		String perspective = (String) ((HttpServletRequest) pageContext
				.getRequest()).getAttribute(RequestConstants.PERSPECTIVE);
		out.write(PageScroll.getPages(currentValue, pageSize, size, action,
				currentFlowkey, locale, perspective));
		out.write("</table></td></tr>");
		out.write("</table>");
	}

	private Table helperMultipleData(Object object, Locale locale)
			throws TableTagTypeParserException, TableTagParseException,
			TableTagException, JspException {
		Files file = TypeParser.getInstance().parser(getSingleFile(locale));
		FileName[] fileName = file.getFileName();
		String str = (String) TableTagUtils.getInstance().helper(pageContext,
				"type", "method", object, locale);
		for (int i = 0; i < fileName.length; i++) {
			if (str.equalsIgnoreCase(fileName[i].getName())) {
				return helperCache(fileName[i].getPath(), fileName[i].getName());
			}
		}
		return null;
	}

	private void displayDataMultiple(Object object, Table table, int number,
			Locale locale) throws TableTagException, IOException, JspException {
		String data = null;

		JspWriter out = pageContext.getOut();
		getAction(table);
		boolean isFlowRequired = table.getPageRequirements().getFlowRequired()
				.equalsIgnoreCase("true") ? true : false;
		data = table.getTable(pageContext, object, locale, isFlowRequired);
		out.write("<tr class=\"fontnormal\"><td width=\"3%\" valign=\"top\">");
		out.write((number) + ".</td><td width=\"97%\">");
		out.write(data);
		if (table.getPageRequirements().getBlanklinerequired()
				.equalsIgnoreCase("true")) {
			out.write("<br />");
		}
		out.write("</td></tr>");
	}

	private void displayData(List list, Table table, Locale locale,
			Integer currentValue) throws TableTagException, IOException,
			JspException {
		JspWriter out = pageContext.getOut();

		int number = ((currentValue - 1) * pageSize);
		for (Iterator it = list.iterator(); it.hasNext();) {
			Object object = it.next();
			boolean isFlowRequired = table.getPageRequirements()
					.getFlowRequired().equalsIgnoreCase("true") ? true : false;
			String data = table.getTable(pageContext, object, locale,
					isFlowRequired);
			if (data == null) {
				throw new JspException("XML.filenotfound");
			}
			if (table.getPageRequirements().getNumbersRequired()
					.equalsIgnoreCase("true")) {
				out.write("<tr class=\"fontnormal\"><td width=\"3%\" ");
				if (table.getPageRequirements().getValignnumbers()
						.equalsIgnoreCase("true")) {
					out.write("valign=\"top\"");
				}
				out.write(">");
				out.write((++number) + ".</td><td width=\"97%\"");
			}
			out.write(data);
			if (table.getPageRequirements().getBlanklinerequired()
					.equalsIgnoreCase("true")) {
				if (it.hasNext()) {
					out.write("<br />");
				}
			}
			if (table.getPageRequirements().getBluelineRequired()
					.equalsIgnoreCase("true")) {
				if (it.hasNext()) {
					out
							.write("<tr><td colspan=\"2\" valign=\"top\" class=\"blueline\">");
					out
							.write("<img src=\"pages/framework/images/trans.gif \" width=\"10\" height=\"5\"></td></tr>");
				}
			}
			out.write("</td></tr>");
		}
		StringBuilder result = new StringBuilder();
		boolean bottomBlueLineRequired = table.getPageRequirements()
				.getBottombluelineRequired().equalsIgnoreCase("true");
		createEndTable(result, bottomBlueLineRequired);
		String action = getAction(table);
		out.write(result.toString());
		String currentFlowkey = (String) ((HttpServletRequest) pageContext
				.getRequest()).getAttribute(Constants.CURRENTFLOWKEY);
		String perspective = (String) ((HttpServletRequest) pageContext
				.getRequest()).getAttribute(RequestConstants.PERSPECTIVE);
		out.write(PageScroll.getPages(currentValue, pageSize, size, action,
				currentFlowkey, locale, perspective));
		out.write("</table></td></tr>");
		out.write("</table>");
	}

	private List getList() throws HibernateSearchException, JspException {
		Cache cache = (Cache) SessionUtils.getAttribute(
				TableTagConstants.TABLECACHE, pageContext.getSession());

		String currentPage = ((HttpServletRequest) pageContext.getRequest())
				.getParameter("current");
		Integer currentValue = null;
		if (currentPage != null && !currentPage.equals("")) {
			currentValue = Integer.valueOf(currentPage);
		} else {
			currentValue = 1;
		}

		List list = null;
		if (cache == null) {
			list = getCacheNullList();
			current = 1;
			// Bug ID-29262
			// SessionUtils.setRemovableAttribute("current",Integer.valueOf(current),TableTagConstants.PATH,pageContext.getSession());
		} else if (currentValue != null) {
			current = currentValue;

			String meth = (String) SessionUtils.getAttribute("meth",
					pageContext.getSession());
			list = meth == null ? cache.getList(current, "next") : cache
					.getList(current, meth);
			size = cache.getSize();
		}
		return list;
	}

	private List getCacheNullList() throws HibernateSearchException {
		List list = null;
		Cache cache = null;
		try {
			QueryResult query = (QueryResult) SessionUtils.getAttribute(
					Constants.SEARCH_RESULTS, (HttpServletRequest) pageContext
							.getRequest());
			if (null != query) {
				cache = new Cache(query);
				list = cache.getList(0, "");
				if (null == list) {
					list = new ArrayList();
				} else {
					SessionUtils.setRemovableAttribute(
							TableTagConstants.TABLECACHE, cache,
							TableTagConstants.PATH, pageContext.getSession());
					size = cache.getSize();
				}
			}
		} catch (PageExpiredException e) {

			throw new HibernateSearchException(e);
		}
		return list;
	}

	private Locale getLocale() {
		HttpSession session = pageContext.getSession();
		Locale userPreferredLocale = null;
		if (null != session) {
			UserContext userContext = (UserContext) session
					.getAttribute(LoginConstants.USERCONTEXT);
			if (null != userContext) {
				userPreferredLocale = userContext.getPreferredLocale();
				if (null == userPreferredLocale) {
					//userPreferredLocale = userContext.getMfiLocale();
					userPreferredLocale = userContext.getCurrentLocale();
				}
			}
		}
		return userPreferredLocale;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#release()
	 */
	@Override
	public void release() {
		super.release();
		current = 1;
		size = 0;
	}
}
