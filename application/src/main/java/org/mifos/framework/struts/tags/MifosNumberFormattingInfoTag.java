/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.framework.struts.tags;

import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.mifos.framework.util.helpers.LabelTagUtils;

import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.DecimalFormatSymbols;

public class MifosNumberFormattingInfoTag extends TagSupport {

	private static final long serialVersionUID = -1455066582480604359L;

	@Override
	public int doStartTag() throws JspException {		
		try {
			Locale locale = LabelTagUtils.getInstance().getUserPreferredLocale();
			DecimalFormat decimalFormat = getDecimalFormat(locale);
			DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
			
			StringBuilder sb = new StringBuilder();
			sb.append("<span id=\"format.decimalSeparator\" title=\"").append(symbols.getDecimalSeparator()).append("\"></span>\n");
			sb.append("<span id=\"format.groupingSeparator\" title=\"").append(symbols.getGroupingSeparator()).append("\"></span>\n");
			sb.append("<span id=\"format.groupingSize\" title=\"").append(decimalFormat.getGroupingSize()).append("\"></span>\n");
			
			pageContext.getOut().print(sb.toString());		
		} 
		catch (Exception ex) {
			throw new JspException("NumberFormattingInfoTag: " + ex.getMessage());
		}
		
		return SKIP_BODY;
	}
	
	@Override
	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}
	
	private DecimalFormat getDecimalFormat(Locale locale) {
		DecimalFormat decimalFormat = (DecimalFormat)DecimalFormat.getInstance(locale);
		if (decimalFormat == null) {
			decimalFormat = (DecimalFormat)DecimalFormat.getInstance();
		}
		
		return decimalFormat;
	}
}
