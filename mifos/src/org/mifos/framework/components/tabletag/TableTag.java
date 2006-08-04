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
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.mifos.application.customer.util.helpers.CustomerSearchConstants;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.framework.exceptions.HibernateSearchException;
import org.mifos.framework.exceptions.TableTagException;
import org.mifos.framework.exceptions.TableTagParseException;
import org.mifos.framework.exceptions.TableTagTypeParserException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Cache;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.FileCacheRep;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.valueobjects.Context;

/**
 * This class renders the table list.
 *
 * @author rohitr
 * @version 1.0
 */
public class TableTag extends BodyTagSupport {

	private int pageSize = Integer.valueOf(TableTagConstants.MIFOSTABLE_PAGESIZE);

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
	public String type;

	private String className;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            The type to set.
	 */
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
	 *
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Function get the value of the border
	 *
	 * @return Returns the border.
	 */
	public String getBorder() {
		return border;
	}

	/**
	 * Function set the value of the border
	 *
	 * @param border
	 *            The border to set.
	 */
	public void setBorder(String border) {
		this.border = border;
	}

	/**
	 * Function get the value of the cellpadding
	 *
	 * @return Returns the cellpadding.
	 */
	public String getCellpadding() {
		return cellpadding;
	}

	/**
	 * Function set the value of the cellpadding
	 *
	 * @param cellpadding
	 *            The cellpadding to set.
	 */
	public void setCellpadding(String cellpadding) {
		this.cellpadding = cellpadding;
	}

	/**
	 * Function get the value of the cellspacing
	 *
	 * @return Returns the cellspacing.
	 */
	public String getCellspacing() {
		return cellspacing;
	}

	/**
	 * Function set the value of the cellspacing
	 *
	 * @param cellspacing
	 *            The cellspacing to set.
	 */
	public void setCellspacing(String cellspacing) {
		this.cellspacing = cellspacing;
	}

	/**
	 * Function get the value of the width
	 *
	 * @return Returns the width.
	 */
	public String getWidth() {
		return width;
	}

	/**
	 * @return Returns the key.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            The key to set.
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Function set the value of the width
	 *
	 * @param width
	 *            The width to set.
	 */
	public void setWidth(String width) {
		this.width = width;
	}

	// Object of the Table class
	int size = 0;
	int current = 1;

	/**
	 * Function to render the tag
	 *
	 * @throws JspException
	 */
	public int doStartTag() throws JspException {
		// Used to set the value of the list which we are getting from the Cache
		List list = null;
		// call the helper method to display the result.
		try {
			list=getList();
			if(list==null) {
				return super.doStartTag();
			}
			if(list.size()==0) {
				StringBuilder result = new StringBuilder();
				JspWriter out = pageContext.getOut();
				result.append("<table width=\"96%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">")
				.append("<tr class=\"fontnormal\"><td colspan=\"2\" valign=\"top\" ><br>")
				.append("<span class=\"headingorange\">");
				result.append("No results found"); 
				Context context = (Context) SessionUtils.getAttribute("Context",pageContext.getSession());	
				String officeId=context.getSearchObject().getFromSearchNodeMap(CustomerSearchConstants.CUSTOMERSEARCOFFICEID);			
				
				if(officeId!=null)
				{
					Map map = context.getSearchObject().getSearchNodeMap();				
					Set set = map.keySet();
					Iterator iterator = set.iterator();
					if (iterator.hasNext()) {
						result.append(" for"+"<span class=\"heading\"> "+map.get(iterator.next()) + " </span>");
					}
					if(officeId.equals("0"))
					{
						result.append("<span class=\"headingorange\"> in</span> <span class=\"heading\">"
								+"All Branches"+ "</span>");
					}
					else if (iterator.hasNext()) {
						String officeName = map.get(iterator.next()).toString();				
						if(!officeName.equals(""))
						{								
							result.append("<span class=\"headingorange\"> in</span> <span class=\"heading\">"
								+ officeName+ "</span>");
						}
						else
						{							
							result.append("<span class=\"headingorange\"> in</span> <span class=\"heading\">"
									+  context.getBusinessResults("Office")+ "</span>");
						}
					}
					
					result.append("</span></td></tr>");
					if(type.equalsIgnoreCase("single")) {
						result.append("<tr><td colspan=\"2\" valign=\"top\" class=\"blueline\"><br>");
						result.append("<img src=\"pages/framework/images/trans.gif \" width=\"5\" height=\"3\"></td></tr>");
					}
				}
				out.write(result.toString());
				return super.doStartTag();
			}
			getTableData(list);
		} catch (TableTagException tte) {
			new JspException(tte);
		} catch (TableTagParseException ttpe) {
			new JspException(ttpe);
		} catch (TableTagTypeParserException tttpe) {
			new JspException(tttpe);
		}  catch (IOException ioe) {
			new JspException(ioe);
		} catch (HibernateSearchException hse) {
			new JspException(hse);
		}
		return super.doStartTag();

	}

	/**
	 * Function used to start table creation.
	 *
	 * @param result
	 * @param size
	 */
	private void createStartTable(StringBuilder result, boolean headingRequired, boolean topBlueLineRequired) {
		if (headingRequired) {
			result.append("<table width=\"96%\" border=\"0\" cellpadding=\"3\" cellspacing=\"0\">")
				  .append("<tr class=\"fontnormal\"><td colspan=\"2\" valign=\"top\"><span class=\"headingorange\">")
				  .append(size + " results for </span>");
			Context context = (Context) SessionUtils.getAttribute("Context",pageContext.getSession());
			Map map = context.getSearchObject().getSearchNodeMap();
			Set set = map.keySet();
			Iterator iterator = set.iterator();			
			if (iterator.hasNext()) {				
				result.append("<span class=\"heading\">"+ map.get(iterator.next()) + " </span>");
			}
			String officeId=context.getSearchObject().getFromSearchNodeMap(CustomerSearchConstants.CUSTOMERSEARCOFFICEID);				
			
			if(officeId!=null && officeId.equals("0"))
			{			
					result.append("<span class=\"headingorange\">in</span> <span class=\"heading\">"
							+ "All Branches"+ "</span>");			
			}
			else if (iterator.hasNext()) {				
				String officeName = map.get(iterator.next()).toString();				
				if(!officeName.equals(""))
				{				
					result.append("<span class=\"headingorange\">in</span> <span class=\"heading\">"
							+ officeName+ "</span>");
				}				
				else
				{					
					result.append("<span class=\"headingorange\">in</span> <span class=\"heading\">"
							+ context.getBusinessResults("Office")+ "</span>");
				}
			}			
			result.append("</td></tr></table>");
			result.append("<tr><td colspan=\"2\" valign=\"top\">")
			.append("<img src=\"pages/framework/images/trans.gif \" width=\"5\" height=\"3\"></td></tr>");
		}
		result.append("<table width=" + width + "border=" + border
				+ "cellspacing=" + cellspacing + "cellpadding=" + cellpadding+" >");
		if(topBlueLineRequired) {
			result.append("<tr><td colspan=\"2\" valign=\"top\" class=\"blueline\">")
			.append("<img src=\"pages/framework/images/trans.gif \" width=\"5\" height=\"3\"></td></tr>");
		}
	}

	/**
	 * Function used to create end part of the table.
	 *
	 * @param result
	 */
	private void createEndTable(StringBuilder result,boolean bottomBlueLineRequired) {
		if(bottomBlueLineRequired) {
			result.append("<tr><td colspan=\"2\" valign=\"top\" class=\"blueline\">")
			.append("<img src=\"pages/framework/images/trans.gif \" width=\"10\" height=\"5\"></td></tr>")
			.append("<tr><td height=\"35\" colspan=\"2\" align=\"center\" class=\"blueline\">")
			.append("<table width=\"300\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		}else {
			result.append("<tr><td colspan=\"2\" valign=\"top\" class=\"blueline\">")
				.append("<img src=\"pages/framework/images/trans.gif \" width=\"5\" height=\"3\"></td></tr>")
				.append("<tr><td height=\"35\" colspan=\"2\" align=\"center\">")
				.append("<table width=\"300\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		}
	}

	/**
	 * Function to get single xml file.
	 *
	 * @param object
	 *            query object
	 * @return
	 * @throws TableTagParseException
	 * @throws TableTagException
	 * @throws JspException
	 */
	private String getSingleFile() throws TableTagParseException,
			TableTagException, JspException {
		ResourceBundle resource = ResourceBundle
				.getBundle(TableTagConstants.PROPERTIESFILE);
		String xmlPath = resource.getString(name + "_xml");
		if (xmlPath != null) {
			// return getTableObject(object,xmlName,className).getTable(object);
			return xmlPath;
		} else
			throw new JspException(resource.getString(TableTagConstants.NOXMLFILE_ERROR));
	}

	/**
	 * Function to get the action
	 *
	 * @param table
	 * @throws JspException
	 */
	private String getAction(Table table) throws JspException {

		String action=(String) SessionUtils.getAttribute("action",pageContext.getSession());
		String forwardkey =(String) SessionUtils.getAttribute("forwardkey",pageContext.getSession());	
		ResourceBundle resource = ResourceBundle
		.getBundle(TableTagConstants.PROPERTIESFILE);
		if(null==action || null==forwardkey) {
			Path pathVar = table.findPath(key);
			action = pathVar.getAction();
			forwardkey = pathVar.getForwardkey();
			if (action != null) {
				SessionUtils.setRemovableAttribute("action", action,TableTagConstants.PATH, pageContext
						.getSession());
			} else
				throw new JspException(resource.getString( TableTagConstants.NOACTION_ERROR));
			if (forwardkey != null) {
				SessionUtils.setRemovableAttribute("forwardkey", forwardkey,TableTagConstants.PATH, pageContext
						.getSession());
			} else
				throw new JspException(resource.getString( TableTagConstants.NOFWDKEY_ERROR));
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
	 * @throws TableTagParseException
	 */
	private Table helperCache(String xmlFilePath, String key)
			throws TableTagParseException {
		FileCacheRep fileCacheRep=FileCacheRep.getInstance();
		Table table=null;
		if (! fileCacheRep.isKeyPresent(key)) {
			table= TableTagParser.getInstance().parser(xmlFilePath);
			fileCacheRep.addTOCacherep(key, table);
		}
		else {
			table=fileCacheRep.getFromCacheRep(key);
		}
		return table;
	}

	private void getTableData(List list) throws TableTagException, TableTagParseException,
			JspException, TableTagTypeParserException, IOException {
		Locale locale = getLocale();
		ResourceBundle resource = ResourceBundle
		.getBundle(TableTagConstants.PROPERTIESFILE);
		if (type.equalsIgnoreCase("single")) {
			getSingleData(list,locale);
		} else if (type.equalsIgnoreCase("multiple")) {
			getMultipleData(list,locale);
		} else
			throw new TableTagException(resource.getString(TableTagConstants.WRONGTYPE_ERROR));
	}

	private void getSingleData(List list,Locale locale) throws TableTagParseException, TableTagException,
			JspException, IOException {
		String xmlFilePath = getSingleFile();
		Table table = helperCache(xmlFilePath, name);
		ResourceBundle resource = ResourceBundle
		.getBundle(TableTagConstants.PROPERTIESFILE);
		if (table != null) {
			StringBuilder result = new StringBuilder();
			JspWriter out = pageContext.getOut();
			boolean headingRequired=table.getPageRequirements().getHeadingRequired().equalsIgnoreCase("true");
			boolean topBlueLineRequired=table.getPageRequirements().getTopbluelineRequired().equalsIgnoreCase("true");
			createStartTable(result,  headingRequired,topBlueLineRequired);
			out.write(result.toString());
			displayData(list, table,locale);
		} else
			throw new JspException(resource.getString(TableTagConstants.TABLENOTFOUND_ERROR));
	}

	private void getMultipleData(List list,Locale locale) throws TableTagTypeParserException,
			TableTagParseException, TableTagException, JspException,IOException {
		StringBuilder result = new StringBuilder();
		JspWriter out = pageContext.getOut();
		createStartTable(result,true,false);
		ResourceBundle resource = ResourceBundle
		.getBundle(TableTagConstants.PROPERTIESFILE);
		out.write(result.toString());
		int number = ((current - 1) * pageSize);
		for (Object object:list) {
			Table table = helperMultipleData(object,locale);
			if (table != null) {
				displayDataMultiple(object, table,++number,locale);
			} else
				throw new JspException(resource.getString(TableTagConstants.TABLENOTFOUND_ERROR));
		}
		result = new StringBuilder();
		createEndTable(result,false);
		String action = (String) SessionUtils.getAttribute("action",pageContext.getSession());
		out.write(result.toString());
		out.write(PageScroll.getPages(current, pageSize, size, action));
		out.write("</table></td></tr>");
		out.write("</table>");
	}

	private Table helperMultipleData(Object object,Locale locale)
				throws TableTagTypeParserException, TableTagParseException,TableTagException,JspException {
		Files file = TypeParser.getInstance().parser(getSingleFile());
		FileName[] fileName = file.getFileName();
		String str =(String)TableTagUtils.getInstance().helper(pageContext ,"type","method",object,locale);
		for (int i = 0; i < fileName.length; i++) {
			if (str.equalsIgnoreCase(fileName[i].getName())) {
				return helperCache(fileName[i].getPath(), fileName[i].getName());
			}
		}
		return null;
	}

	private void displayDataMultiple(Object object, Table table,int number,Locale locale) throws TableTagException, IOException,
			JspException {
		String data = null;

		JspWriter out = pageContext.getOut();
		getAction(table);
		data = table.getTable(pageContext ,object,locale);
		out.write("<tr class=\"fontnormal\"><td width=\"3%\" valign=\"top\">");
		out.write((number) + ".</td><td width=\"97%\">");
		out.write(data);
		if (table.getPageRequirements().getBlanklinerequired().equalsIgnoreCase("true")) {
			out.write("<br>");
		}
		out.write("</td></tr>");
	}


	private void displayData(List list, Table table,Locale locale) throws TableTagException, IOException,
			JspException {
		JspWriter out = pageContext.getOut();
		int number = ((current - 1) * pageSize);
		for (Iterator it = list.iterator(); it.hasNext();) {
			Object object = it.next();
			String data  = table.getTable(pageContext ,object,locale);
			if (data == null) {
				throw new JspException("XML.filenotfound");
			}
			if (table.getPageRequirements().getNumbersRequired().equalsIgnoreCase("true")) {
				out.write("<tr class=\"fontnormal\"><td width=\"3%\" ");
				if(table.getPageRequirements().getValignnumbers().equalsIgnoreCase("true")) {
					out.write("valign=\"top\"");
				}
				out.write(">");
				out.write((++number) + ".</td><td width=\"97%\"");
			}
			out.write(data);
			if (table.getPageRequirements().getBlanklinerequired().equalsIgnoreCase("true")) {
				if (it.hasNext()) {
					out.write("<br>");
				}
			}
			if (table.getPageRequirements().getBluelineRequired().equalsIgnoreCase("true")) {
				if (it.hasNext()) {
					out.write("<tr><td colspan=\"2\" valign=\"top\" class=\"blueline\">");
					out.write("<img src=\"pages/framework/images/trans.gif \" width=\"10\" height=\"5\"></td></tr>");
				}
			}
			out.write("</td></tr>");
		}
		StringBuilder result = new StringBuilder();
		boolean bottomBlueLineRequired = table.getPageRequirements().getBottombluelineRequired().equalsIgnoreCase("true");
		createEndTable(result,bottomBlueLineRequired);
		String action = getAction(table);
		out.write(result.toString());
		out.write(PageScroll.getPages(current, pageSize, size, action));
		out.write("</table></td></tr>");
		out.write("</table>");
	}

	private List getList() throws HibernateSearchException, JspException {
		Cache cache = (Cache)SessionUtils.getAttribute(TableTagConstants.TABLECACHE, pageContext.getSession());
		Integer currentValue=(Integer)SessionUtils.getAttribute("current", pageContext.getSession());
		List list = null;
		if (cache == null) {
			list=getCacheNullList();
			current=1;
			//Bug ID-29262
			SessionUtils.setRemovableAttribute("current",Integer.valueOf(current),TableTagConstants.PATH,pageContext.getSession());
		}
		else if (currentValue!= null) {
			current = Integer.valueOf(currentValue);

			String meth=(String)SessionUtils.getAttribute("meth", pageContext.getSession());
			list = meth==null?cache.getList(current, "next"):cache.getList(current, meth);
			size=cache.getSize();
		}
		return list;
	}

	private List getCacheNullList() throws HibernateSearchException {
		List list=null;
		Cache cache=null;
		Context context = (Context) SessionUtils.getAttribute(Constants.CONTEXT, pageContext.getSession());
		if (context != null) {
			QueryResult query = context.getSearchResult();
			if(null!= query) {
				cache=new Cache(query);
				list = cache.getList(0,"");
				if (null == list) {
					list= new ArrayList();
				}
				else {
					SessionUtils.setRemovableAttribute(
							TableTagConstants.TABLECACHE, cache,TableTagConstants.PATH, pageContext.getSession());
					size=cache.getSize();
				}
			}
		}
		return list;
	}
	
	private Locale getLocale() {
		HttpSession session=pageContext.getSession();
		Locale userPreferredLocale=null;
		if(null!=session) {
			UserContext userContext=(UserContext)session.getAttribute(LoginConstants.USERCONTEXT);
			if(null!=userContext) {
				userPreferredLocale = userContext.getPereferedLocale();
				if(null==userPreferredLocale) {
					userPreferredLocale=userContext.getMfiLocale();
				}
			}
		}
		return userPreferredLocale;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#release()
	 */
		@Override
	public void release() {
		super.release();
		current=1;
		size=0;
	}
}
