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
 
package org.mifos.application.configuration.struts.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import java.util.ResourceBundle;
import java.util.Locale;

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
import org.mifos.framework.util.helpers.FilePaths;

public class CustomFieldsListTag extends BodyTagSupport { //SimpleTagSupport { 
	private String actionName;

	private String methodName;
	
	private String flowKey;
	
	private String categoryName;
	private String category;

	public CustomFieldsListTag() {
	}
	
	public CustomFieldsListTag(String action, String method, String flow,
			String categoryName, String category) {
		actionName = action;
		methodName = method;
		flowKey = flow;
		this.categoryName = categoryName;
		this.category = category;
	}
	
	private String getDefaultValue(CustomFieldDefinitionEntity customField, UserContext userContext)
	{
		String defaultValue = customField.getDefaultValue();
		if (customField.getFieldType().equals(CustomFieldType.DATE.getValue()) && !StringUtils.isNullOrEmpty(defaultValue))
			defaultValue = DateUtils.getUserLocaleDate(userContext.getPreferredLocale(), defaultValue);
		return defaultValue;
	}

	public XmlBuilder getRow(CustomFieldDefinitionEntity customField, UserContext userContext, int index) {
		Locale locale = userContext.getPreferredLocale();
		ResourceBundle resources = ResourceBundle.getBundle(
				FilePaths.CONFIGURATION_UI_RESOURCE_PROPERTYFILE, locale);
		String editString = resources.getString("configuration.edit");
		XmlBuilder html = new XmlBuilder();
		String url = (actionName + "?method=" + methodName +
				"&customFieldIdStr=" + customField.getFieldId() +
				"&currentFlowKey=" + flowKey);

		html.startTag("tr");
		html.newline();
		html.startTag("td", "width", "11%", "class", "drawtablerow");
		html.text(Integer.toString(index));
		html.endTag("td");
		html.newline();
		html.startTag("td", "width", "22%", "class", "drawtablerow");
		String label = customField.getLookUpEntity().getLabel();
		html.text(label);
		html.endTag("td");
		html.newline();
		html.startTag("td", "width", "21%", "class", "drawtablerow");
		html.text(MessageLookup.getInstance().lookup(
				CustomFieldType.fromInt(customField.getFieldType()),
				userContext));
		html.endTag("td");
		html.newline();
		html.startTag("td", "width", "21%", "class", "drawtablerow");
		if (customField.getDefaultValue() == null) {
			html.nonBreakingSpace();
		}
		else {
			html.text(getDefaultValue(customField, userContext));
		}
		html.endTag("td");
		html.newline();
		html.startTag("td", "width", "17%", "class", "drawtablerow");
		html.text(customField.getMandatoryStringValue(locale));
		html.endTag("td");
		html.newline();
		html.startTag("td", "width", "8%", "align", "right", "class", "drawtablerow");
		html.startTag("a", "href", url);
		html.text(editString);
		html.endTag("a");
		html.endTag("td");
		html.newline();
		html.endTag("tr");
		html.newline();

		return html;
	}

	public String getCustomFieldsList(UserContext userContext) throws PersistenceException {
		MasterPersistence master = new MasterPersistence();
		EntityType entityType = CustomFieldCategory.getCustomFieldCategoryFromString(category).mapToEntityType();

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
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}


}
