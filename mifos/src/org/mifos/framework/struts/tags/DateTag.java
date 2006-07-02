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
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigImplementer;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigItf;
import org.mifos.framework.security.util.UserContext;

public class DateTag extends BaseInputTag {

	private static final long serialVersionUID = 8328811567470903924L;

	private FieldConfigItf fieldConfigItf = FieldConfigImplementer
			.getInstance();

	private String keyhm;

	private String isDisabled;

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

	public int doStartTag() throws JspException {
		if (fieldConfigItf.isFieldHidden(getKeyhm())) {
			StringBuffer inputsForhidden = new StringBuffer();
			inputsForhidden.append("<input type=\"hidden\"  name=\""
					+ getProperty() + "\" value=\"\"/>");
			inputsForhidden.append("<input type=\"hidden\"  name=\""
					+ getProperty() + "Format\" value=\"\"/>");
			inputsForhidden.append("<input type=\"hidden\"  name=\""
					+ getProperty() + "YY\" value=\"\"/>");
			TagUtils.getInstance().write(this.pageContext,
					inputsForhidden.toString());
			return EVAL_PAGE;
		} else if (!fieldConfigItf.isFieldHidden(getKeyhm())
				&& fieldConfigItf.isFieldManadatory(getKeyhm())) {
			StringBuffer inputsForhidden = new StringBuffer();
			inputsForhidden.append("<input type=\"hidden\"  name=\""
					+ getKeyhm() + "\" value=\"" + getProperty() + "\"/>");
			TagUtils.getInstance().write(this.pageContext,
					inputsForhidden.toString());
		}

		String ddValue = "";
		String mmValue = "";
		String yyValue = "";

		UserContext userContext = (UserContext) pageContext.getSession()
				.getAttribute(LoginConstants.USERCONTEXT);
		Locale locale = null;
		if (userContext != null) {
			locale = userContext.getPereferedLocale();
			if (null == locale) {
				locale = userContext.getMfiLocale();
			}
			DateFormat df = DateFormat
					.getDateInstance(DateFormat.SHORT, locale);
			String userfmt = ((SimpleDateFormat) df).toPattern();
			String separator = DateHelper.getSeparator(userfmt);
			// TODO - get from ApplicationConfiguration
			String currentDateValue = returnValue();
			if (currentDateValue != null && !currentDateValue.equals("")) {
				// added by mohammedn
				String dmy[] = null;
				// TODO chnage this
				if (name.equalsIgnoreCase("org.apache.struts.taglib.html.BEAN")) {
					String format = DateHelper.convertToDateTagFormat(userfmt);
					dmy = DateHelper.getDayMonthYear(currentDateValue, format,
							separator);
				} else
					dmy = DateHelper.getDayMonthYearDbFrmt(currentDateValue,
							"Y-M-D");
				ddValue = dmy[0].trim();
				mmValue = dmy[1].trim();
				yyValue = dmy[2].trim();
			}
			// user format
			String format = DateHelper.convertToDateTagFormat(userfmt);
			String output = this.prepareOutputString(format, property, ddValue,
					mmValue, yyValue, separator, userfmt);
			TagUtils.getInstance().write(pageContext, output);
		}

		return (SKIP_BODY);
	}

	protected String returnValue() throws JspException {
		Object value = TagUtils.getInstance().lookup(pageContext, name,
				property, null);
		if (value == null) {
			return "";
		}
		return TagUtils.getInstance().filter(value.toString());
	}

	public String prepareOutputString(String format, String dateName,
			String ddValue, String mmValue, String yyValue, String separator,
			String userfrmt) {

		StringBuilder output = new StringBuilder();
		StringBuilder dateFunction = new StringBuilder();
		dateFunction.append("onBlur=\"makeDateString(");
		StringTokenizer stfmt = new StringTokenizer(format, "/");
		while (stfmt.hasMoreTokens()) {
			String ch = stfmt.nextToken();
			if (ch.equalsIgnoreCase("D")) {
				dateFunction.append("'" + dateName + "DD',");
			} else if (ch.equalsIgnoreCase("M")) {
				dateFunction.append("'" + dateName + "MM',");
			} else {
				dateFunction.append("'" + dateName + "YY',");
			}
		}

		dateFunction.append("'" + dateName + "'");
		dateFunction.append(",'" + separator + "')\"");
		String date = "";
		if (ddValue != null && !ddValue.equals("") && mmValue != null
				&& !mmValue.equals("") && yyValue != null
				&& !yyValue.equals("")) {
			date = DateHelper.createDateString(ddValue, mmValue, yyValue,
					format);
		}
		boolean disabled = getIsDisabled() != null
				&& getIsDisabled().equalsIgnoreCase("Yes") ? true : false;

		String daytext = "<input type=\"text\" id=\""
				+ dateName
				+ "DD\" name=\""
				+ dateName
				+ "DD\" "
				+ "maxlength=\"2\" size=\"2\" value=\""
				+ ddValue
				+ "\" "
				+ dateFunction
				+ " style=\"width:25px\" onKeyPress=\"return numbersonly(this, event)\"";
		if (disabled)
			daytext = daytext + "disabled";
		daytext = daytext + "/>&nbsp;DD&nbsp;";
		String monthtext = "<input type=\"text\" id=\""
				+ dateName
				+ "MM\"  name=\""
				+ dateName
				+ "MM\" "
				+ "maxlength=\"2\" size=\"2\" value=\""
				+ mmValue
				+ "\" "
				+ dateFunction
				+ " style=\"width:25px\" onKeyPress=\"return numbersonly(this, event)\"";
		if (disabled)
			monthtext = monthtext + "disabled";
		monthtext = monthtext + "/>&nbsp;MM&nbsp;";
		String yeartext = "<input type=\"text\" id=\""
				+ dateName
				+ "YY\"  name=\""
				+ dateName
				+ "YY\" "
				+ "maxlength=\"4\" size=\"4\" value=\""
				+ yyValue
				+ "\" "
				+ dateFunction
				+ " style=\"width:40px\" onKeyPress=\"return numbersonly(this, event)\"";
		if (disabled)
			yeartext = yeartext + "disabled";
		yeartext = yeartext + "/>&nbsp;YYYY&nbsp;";
		String hiddentext = "<input type=\"hidden\" id=\"" + dateName
				+ "\" name=\"" + dateName + "\" value=\"" + date + "\"/>";
		String hiddenformattext = "<input type=\"hidden\" id=\"" + dateName
				+ "Format\" name=\"" + dateName + "Format\" value=\"" + format
				+ "\"/>";
		String hiddenpatterntext = "<input type=\"hidden\" id=\"datePattern\" name=\"datePattern\" value=\""
				+ userfrmt + "\"/>";

		stfmt = new StringTokenizer(format, "/");
		while (stfmt.hasMoreTokens()) {
			String ch = stfmt.nextToken();
			if (ch.equals("D") || ch.equals("d")) {
				output.append(daytext);
			} else if (ch.equals("M") || ch.equals("m")) {
				output.append(monthtext);
			} else {
				output.append(yeartext);
			}
		}
		output.append(hiddentext);
		output.append(hiddenformattext);
		output.append(hiddenpatterntext);
		return output.toString();
	}

}
