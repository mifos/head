/**

 * DateTag.java    version: 1.0

 

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

package org.mifos.framework.struts.tags;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.BaseInputTag;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfig;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DateUtils;

public class DateTag extends BaseInputTag {

	private static final long serialVersionUID = 8328811567470903924L;

	private FieldConfig fieldConfig = FieldConfig.getInstance();

	private String keyhm;

	private String isDisabled;
	
	private String renderstyle="";

	public String getRenderstyle() {
		return renderstyle;
	}
	
	public void setRenderstyle(String value) {
		renderstyle = value;
	}

	public String getKeyhm() {
		return keyhm;
	}

	public void setKeyhm(String keyhm) {
		this.keyhm = keyhm;
	}

	public String getIsDisabled() {
		return isDisabled;
	}

	public void setIsDisabled(String isDisabled) {
		this.isDisabled = isDisabled;
	}

	@Override
	public void setIndexed(boolean arg0) {
		super.setIndexed(true);
	}

	@Override
	public int doStartTag() throws JspException {
		if (fieldConfig.isFieldHidden(getKeyhm())) {
		
			
			XmlBuilder htmlInputsForhidden = new XmlBuilder();
			htmlInputsForhidden.singleTag("input", "type","hidden","name",getProperty());
			htmlInputsForhidden.singleTag("input", "type","hidden","name",getProperty()+"Format","Value");
			htmlInputsForhidden.singleTag("input", "type","hidden","name",getProperty()+"YY","Value");
		
			TagUtils.getInstance().write(this.pageContext,
					htmlInputsForhidden.toString());
			return EVAL_PAGE;
		} else if (!fieldConfig.isFieldHidden(getKeyhm())
				&& fieldConfig.isFieldManadatory(getKeyhm())) {
	
			XmlBuilder htmlInputsForhidden = new XmlBuilder();
			htmlInputsForhidden.singleTag("input", "type","hidden","name",getKeyhm(),"Value",getProperty());
			TagUtils.getInstance().write(this.pageContext,
					htmlInputsForhidden.toString());
		}

		UserContext userContext = (UserContext) pageContext.getSession()
				.getAttribute(LoginConstants.USERCONTEXT);

		if (userContext != null) {
			// TODO - get from ApplicationConfiguration
			String currentDateValue = returnValue();
			String output = render(userContext, currentDateValue);
			
			TagUtils.getInstance().write(pageContext, output);
		}

		return SKIP_BODY;
	}

	String render(UserContext userContext, String currentDateValue) 
	throws JspException {
		String ddValue = "";
		String mmValue = "";
		String yyValue = "";
		String userfmt = getUserFormat(userContext);
		String separator = DateUtils.getSeparator(userfmt);
		if (currentDateValue != null && !currentDateValue.equals("")) {
			// added by mohammedn
			String dmy[] = null;
			// TODO chnage this
			if (name.equalsIgnoreCase("org.apache.struts.taglib.html.BEAN")) {
				String format = DateUtils.convertToDateTagFormat(userfmt);
				dmy = DateUtils.getDayMonthYear(currentDateValue, format, separator);
			} else
				dmy = DateUtils.getDayMonthYearDbFrmt(currentDateValue, "Y-M-D");
			ddValue = dmy[0].trim();
			mmValue = dmy[1].trim();
			yyValue = dmy[2].trim();
		}
		// user format
		String format = DateUtils.convertToDateTagFormat(userfmt);
		
		String output;
		if (getRenderstyle().equalsIgnoreCase("simple")) {
			output = "<!-- simple style -->" +
				makeUserFields(property, ddValue, mmValue, yyValue, "", format);
			makeMappedUserFields(property, ddValue, mmValue, yyValue, "", format);
		} else if (getRenderstyle().equalsIgnoreCase("simplemapped")) {
			output = "<!-- simple-mapped style -->" +
				makeMappedUserFields(property, ddValue, mmValue, yyValue, "", format);
		} else {
			output = "<!-- normal style -->" +
				this.prepareOutputString(format, property,
					ddValue, mmValue, yyValue, separator, userfmt);
		}
		return output;
	}

	String getUserFormat(UserContext userContext) {
		Locale locale = userContext.getPreferredLocale();
		if (null == locale) {
			locale = userContext.getMfiLocale();
		}
		DateFormat df = DateFormat
				.getDateInstance(DateFormat.SHORT, locale);
		return ((SimpleDateFormat) df).toPattern();
	}

	protected String returnValue() throws JspException {
		Object value;
		if (getRenderstyle().equalsIgnoreCase("simplemapped")) {
			String dateProperty = "dateValue(" + property + ")";
			value = TagUtils.getInstance().lookup(pageContext, name,
				dateProperty, null);
		} else
			value = TagUtils.getInstance().lookup(pageContext, name,
				property, null);
		if (value == null) {
			return "";
		}
		return TagUtils.getInstance().filter(value.toString());
	}

	public String prepareOutputString(String format, String dateName,
			String ddValue, String mmValue, String yyValue, String separator,
			String userfrmt) {
		StringBuilder dateFunction = new StringBuilder();
	
		dateFunction.append("onBlur,");
		dateFunction.append("makeDateString(");
		StringTokenizer tokenizer = new StringTokenizer(format, "/");
		while (tokenizer.hasMoreTokens()) {
			String ch = tokenizer.nextToken();
			if (ch.equalsIgnoreCase("D")) {
				dateFunction.append("'" + dateName + "DD',");
			} else if (ch.equalsIgnoreCase("M")) {
				dateFunction.append("'" + dateName + "MM',");
			} else {
				dateFunction.append("'" + dateName + "YY',");
			}
		}
		dateFunction.append("'" + dateName + "'");
		dateFunction.append(",'" + separator + "')");
		String date = "";
		if (ddValue != null && !ddValue.equals("") && mmValue != null
				&& !mmValue.equals("") && yyValue != null
				&& !yyValue.equals("")) {
			date = DateUtils.createDateString(ddValue, mmValue, yyValue, format);
		}
		
		XmlBuilder htmlBuilder = makeUserFields(dateName,
				ddValue, mmValue, yyValue, dateFunction.toString(), format);
		htmlBuilder.singleTag("input", "type","hidden","id",dateName,"name",dateName,"value",date);
		htmlBuilder.singleTag("input", "type","hidden","id",dateName+"Format","name",dateName+"Format","value",format);
		htmlBuilder.singleTag("input", "type","hidden","id","datePattern","name","datePattern","value",userfrmt);
		
	
		return htmlBuilder.toString();
	}

	public XmlBuilder makeUserFields(String dateName, String ddValue,
			String mmValue, String yyValue, String dateFunction,
			String format) {
		
		boolean disabled = getIsDisabled() != null
		&& getIsDisabled().equalsIgnoreCase("Yes") ? true : false;
		XmlBuilder htmlOutput = new XmlBuilder();
		XmlBuilder htmlBuilderDay = new XmlBuilder();
		XmlBuilder htmlBuilderMonth = new XmlBuilder();
		XmlBuilder htmlBuilderYear = new XmlBuilder();
		
		if(dateFunction.equals("")){
			if (disabled)
				htmlBuilderDay.singleTag("input", "type","text","id",dateName+"DD","name",dateName+"DD","maxlength","2","size","2","value",ddValue,"style","width:1.5em","disabled","disabled");
			else
				htmlBuilderDay.singleTag("input", "type","text","id",dateName+"DD","name",dateName+"DD","maxlength","2","size","2","value",ddValue,"style","width:1.5em");
			htmlBuilderDay.nonBreakingSpace();
			htmlBuilderDay.text("DD");
			htmlBuilderDay.nonBreakingSpace();
			
			if (disabled)
				htmlBuilderMonth.singleTag("input", "type","text","id",dateName+"MM","name",dateName+"MM","maxlength","2","size","2","value",mmValue,"style","width:1.5em","disabled","disabled");
			else
				htmlBuilderMonth.singleTag("input", "type","text","id",dateName+"MM","name",dateName+"MM","maxlength","2","size","2","value",mmValue,"style","width:1.5em");
			htmlBuilderMonth.nonBreakingSpace();
			htmlBuilderMonth.text("MM");
			htmlBuilderMonth.nonBreakingSpace();
			
			if (disabled)
				htmlBuilderYear.singleTag("input", "type","text","id",dateName+"YY","name",dateName+"YY","maxlength","4","size","4","value",yyValue,"style","width:3em","disabled","disabled");
			else
				htmlBuilderYear.singleTag("input", "type","text","id",dateName+"YY","name",dateName+"YY","maxlength","4","size","4","value",yyValue,"style","width:3em");
			htmlBuilderYear.nonBreakingSpace();
			htmlBuilderYear.text("YYYY");
			htmlBuilderYear.nonBreakingSpace();
		}
		else{
			String strFirstPart =null;
			String strSecondPart =null;
			strFirstPart =dateFunction.substring(0, dateFunction.indexOf(","));
			strSecondPart = dateFunction.substring(dateFunction.indexOf(",")+1);
			
		if (disabled)
			htmlBuilderDay.singleTag("input", "type","text","id",dateName+"DD","name",dateName+"DD","maxlength","2","size","2","value",ddValue,strFirstPart,strSecondPart,"style","width:1.5em","disabled","disabled");
		else
			htmlBuilderDay.singleTag("input", "type","text","id",dateName+"DD","name",dateName+"DD","maxlength","2","size","2","value",ddValue,strFirstPart,strSecondPart,"style","width:1.5em");
		htmlBuilderDay.nonBreakingSpace();
		htmlBuilderDay.text("DD");
		htmlBuilderDay.nonBreakingSpace();
		
		if (disabled)
			htmlBuilderMonth.singleTag("input", "type","text","id",dateName+"MM","name",dateName+"MM","maxlength","2","size","2","value",mmValue,strFirstPart,strSecondPart,"style","width:1.5em","disabled","disabled");
		else
			htmlBuilderMonth.singleTag("input", "type","text","id",dateName+"MM","name",dateName+"MM","maxlength","2","size","2","value",mmValue,strFirstPart,strSecondPart,"style","width:1.5em");
		htmlBuilderMonth.nonBreakingSpace();
		htmlBuilderMonth.text("MM");
		htmlBuilderMonth.nonBreakingSpace();
		
		if (disabled)
			htmlBuilderYear.singleTag("input", "type","text","id",dateName+"YY","name",dateName+"YY","maxlength","4","size","4","value",yyValue,strFirstPart,strSecondPart,"style","width:3em","disabled","disabled");
		else
			htmlBuilderYear.singleTag("input", "type","text","id",dateName+"YY","name",dateName+"YY","maxlength","4","size","4","value",yyValue,strFirstPart,strSecondPart,"style","width:3em");
		htmlBuilderYear.nonBreakingSpace();
		htmlBuilderYear.text("YYYY");
		htmlBuilderYear.nonBreakingSpace();
		
		}
		
		StringTokenizer tokenizer = new StringTokenizer(format, "/");
		while (tokenizer.hasMoreTokens()) {
			String ch = tokenizer.nextToken();
			if (ch.equals("D") || ch.equals("d")) {
				htmlOutput.append(htmlBuilderDay);
			} else if (ch.equals("M") || ch.equals("m")) {
				htmlOutput.append(htmlBuilderMonth);
			} else {
				htmlOutput.append(htmlBuilderYear);
			}
		}
		return htmlOutput;
	}
	
	public String makeMappedUserFields(String dateName, String ddValue,
			String mmValue, String yyValue, String dateFunction,
			String format) throws JspException {
		
		XmlBuilder htmlOutput = new XmlBuilder();
		XmlBuilder htmlBuilderDay = new XmlBuilder();
		XmlBuilder htmlBuilderMonth = new XmlBuilder();
		XmlBuilder htmlBuilderYear = new XmlBuilder();
		boolean disabled = getIsDisabled() != null
				&& getIsDisabled().equalsIgnoreCase("Yes") ? true : false;
		
		if (disabled)
			htmlBuilderDay.singleTag("input", "type","text","id","value"+"("+dateName+"_DD)","name","value"+"("+dateName+"_DD)","maxlength","2","size","2","value",ddValue,"style","width:1.5em","disabled","disabled");
		else
			htmlBuilderDay.singleTag("input", "type","text","id","value"+"("+dateName+"_DD)","name","value"+"("+dateName+"_DD)","maxlength","2","size","2","value",ddValue,"style","width:1.5em");
		htmlBuilderDay.nonBreakingSpace();
		htmlBuilderDay.text("DD");
		htmlBuilderDay.nonBreakingSpace();
		
		if (disabled)
			htmlBuilderMonth.singleTag("input", "type","text","id","value"+"("+dateName+"_MM)","name","value"+"("+dateName+"_MM)","maxlength","2","size","2","value",mmValue,"style","width:1.5em","disabled","disabled");
		else
			htmlBuilderMonth.singleTag("input", "type","text","id","value"+"("+dateName+"_MM)","name","value"+"("+dateName+"_MM)","maxlength","2","size","2","value",mmValue,"style","width:1.5em");
		htmlBuilderMonth.nonBreakingSpace();
		htmlBuilderMonth.text("MM");
		htmlBuilderMonth.nonBreakingSpace();
		
		if (disabled)
			htmlBuilderYear.singleTag("input", "type","text","id","value"+"("+dateName+"_YY)","name","value"+"("+dateName+"_YY)","maxlength","4","size","4","value",yyValue,"style","width:3em","disabled","disabled");
		else
			htmlBuilderYear.singleTag("input", "type","text","id","value"+"("+dateName+"_YY)","name","value"+"("+dateName+"_YY)","maxlength","4","size","4","value",yyValue,"style","width:3em");
		htmlBuilderYear.nonBreakingSpace();
		htmlBuilderYear.text("YY");
		htmlBuilderYear.nonBreakingSpace();
		

		StringTokenizer tokenizer = new StringTokenizer(format, "/");
		while (tokenizer.hasMoreTokens()) {
			String ch = tokenizer.nextToken();
			if (ch.equals("D") || ch.equals("d")) {
				htmlOutput.append(htmlBuilderDay);
			} else if (ch.equals("M") || ch.equals("m")) {
				htmlOutput.append(htmlBuilderMonth);
			} else {
				htmlOutput.append(htmlBuilderYear);
			}
		}
		return htmlOutput.toString();
	}
	
}
