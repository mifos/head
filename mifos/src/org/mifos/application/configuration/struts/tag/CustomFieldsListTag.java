/**

 * CustomFieldsListTag.java

 

 * Copyright (c) 2005-2007 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2007 Grameen Foundation USA 
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

package org.mifos.application.configuration.struts.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.struts.taglib.TagUtils;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomFieldCategory;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.XmlBuilder;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class CustomFieldsListTag extends BodyTagSupport { //SimpleTagSupport { 
	private String actionName;

	private String methodName;
	
	private String flowKey;
	
	private String categoryName;

	public CustomFieldsListTag() {
	}
	
	public CustomFieldsListTag(String action, String method, String flow,
			String category) {
		actionName = action;
		methodName = method;
		flowKey = flow;
		categoryName = category;
	}
	
	private String getDefaultValue(CustomFieldDefinitionEntity customField, UserContext userContext)
	{
		String defaultValue = customField.getDefaultValue();
		if (customField.getFieldType().equals(CustomFieldType.DATE.getValue()) && !StringUtils.isNullOrEmpty(defaultValue))
			defaultValue = DateUtils.getUserLocaleDate(userContext.getPreferredLocale(), defaultValue);
		return defaultValue;
	}

	public XmlBuilder getRow(CustomFieldDefinitionEntity customField, UserContext userContext, int index) {
		XmlBuilder html = new XmlBuilder();
		String url = (actionName + "?method=" + methodName
						+ "&customFieldIdStr=" + customField.getFieldId()
						+ "&currentFlowKey=" + flowKey);

		html.startTag("tr"); html.newline();		
			html.startTag("td", "width", "11%", "class", "drawtablerow"); 
				html.text(Integer.toString(index));
			html.endTag("td"); html.newline();
			html.startTag("td", "width", "22%", "class", "drawtablerow");
				html.text(customField.getLookUpEntity().getLabel());
			html.endTag("td"); html.newline();
			html.startTag("td", "width", "21%", "class", "drawtablerow");
				html.text(MessageLookup.getInstance().lookup(
					CustomFieldType.fromInt(customField.getFieldType()), userContext));
			html.endTag("td"); html.newline();
			html.startTag("td", "width", "21%", "class", "drawtablerow");
				if (customField.getDefaultValue() == null) {
					html.nonBreakingSpace();
				} else {
					html.text(getDefaultValue(customField, userContext));
				}
			html.endTag("td"); html.newline();
			html.startTag("td", "width", "17%", "class", "drawtablerow");
				html.text(customField.getMandatoryStringValue(userContext.getPreferredLocale()));
			html.endTag("td"); html.newline();
			html.startTag("td", "width", "8%", "align", "right", "class", "drawtablerow");
				html.startTag("a", "href", url);
				html.text("Edit"); // TODO: externalize
				html.endTag("a");
			html.endTag("td"); html.newline();
		html.endTag("tr"); html.newline();

		return html;
	}

	public String getCustomFieldsList(UserContext userContext) throws PersistenceException {
		MasterPersistence master = new MasterPersistence();
		EntityType entityType = CustomFieldCategory.getCustomFieldCategoryFromString(categoryName).mapToEntityType();

		XmlBuilder html = new XmlBuilder();
		
		int index = 1;
		for (CustomFieldDefinitionEntity customField : master.retrieveCustomFieldsDefinition(entityType)) {
			html.append(getRow(customField, userContext, index));
			++index;
		}
		
		return html.getOutput();
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			UserContext userContext = (UserContext) pageContext.getSession()
			.getAttribute(Constants.USERCONTEXT);

			TagUtils.getInstance().write(pageContext, getCustomFieldsList(userContext));
			
		} catch (Exception e) {
			/**
			    This turns into a (rather ugly) error 500.
			    TODO: make it more reasonable.
			 */
			throw new JspException(e);
		}
		return EVAL_PAGE;
	}

	/*
	public void doTag() throws JspException {
		try {
			UserContext userContext = (UserContext) ((PageContext)getJspContext()).getSession()
				.getAttribute(Constants.USERCONTEXT);
			
			getJspContext().getOut().write(getCustomFieldsList(userContext));
			
		} catch (Exception e) {
			throw new JspException(e);
		}
	}
*/
	
	
/*	
	@Override
	public int doStartTag() throws JspException {
		try {
			UserContext userContext = (UserContext) pageContext.getSession()
			.getAttribute(Constants.USERCONTEXT);

			TagUtils.getInstance().write(pageContext, getCustomFieldsList(userContext));
			
		} catch (Exception e) {
			//
			//   This turns into a (rather ugly) error 500.
			//    TODO: make it more reasonable.
			//
			throw new JspException(e);
		}
		return EVAL_PAGE;
	}
*/

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getFlowKey() {
		return flowKey;
	}

	public void setFlowKey(String flowKey) {
		this.flowKey = flowKey;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}


}
