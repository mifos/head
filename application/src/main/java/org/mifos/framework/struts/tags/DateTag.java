/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.BaseInputTag;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfig;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.mifos.security.util.UserContext;

public class DateTag extends BaseInputTag {

    private static final long serialVersionUID = 8328811567470903924L;

    private FieldConfig fieldConfig = FieldConfig.getInstance();

    private String keyhm;

    private String isDisabled;

    private String renderstyle = "";

    // tbostelmann - 2007-08-16
    // This value is being used specifically to handle CustomFieldValues
    // that are dates with a specific order of the fields. It really should
    // be a SimpleDateFormat string, but I didn't have enough time to determine
    // what
    // the customField GroupVO's date format was. I believe this is some
    // customer-specific custom field.
    private String formatOrder;

    public void setFormatOrder(String value) {
        this.formatOrder = value;
    }

    public String getFormatOrder() {
        return this.formatOrder;
    }

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
            htmlInputsForhidden.singleTag("input", "type", "hidden", "name", prepareName());
            htmlInputsForhidden.singleTag("input", "type", "hidden", "name", prepareName() + "Format", "Value",
                    "DD/MM/YYYY");
            htmlInputsForhidden.singleTag("input", "type", "hidden", "name", prepareName() + "YY", "Value", "");

            TagUtils.getInstance().write(this.pageContext, htmlInputsForhidden.toString());
            return EVAL_PAGE;
        } else if (!fieldConfig.isFieldHidden(getKeyhm()) && fieldConfig.isFieldManadatory(getKeyhm())) {

            XmlBuilder htmlInputsForhidden = new XmlBuilder();
            htmlInputsForhidden.singleTag("input", "type", "hidden", "name", getKeyhm(), "Value", prepareName());
            TagUtils.getInstance().write(this.pageContext, htmlInputsForhidden.toString());
        }

        UserContext userContext = (UserContext) pageContext.getSession().getAttribute(LoginConstants.USERCONTEXT);

        if (userContext != null) {
            // TODO - get from ApplicationConfiguration
            String currentDateValue = returnValue();
            // this line will be put back when date is localized Locale locale =
            // userContext.getPreferredLocale();
            // the following line will be removed when date is localized
            Locale locale = new LocalizationConverter().getDateLocale();
            String output = render(locale, currentDateValue);
            TagUtils.getInstance().write(pageContext, output);
        }

        return SKIP_BODY;
    }

    private static String[] getDayMonthYear(String date, String format, String separator) {
        String day = "";
        String month = "";
        String year = "";
        String token;
        StringTokenizer stfmt = new StringTokenizer(format, DateUtils.getSeparator(format));
        StringTokenizer stdt = new StringTokenizer(date, separator);
        while (stfmt.hasMoreTokens() && stdt.hasMoreTokens()) {
            token = stfmt.nextToken();
            if (token.equalsIgnoreCase("D")) {
                day = stdt.nextToken();
            } else if (token.equalsIgnoreCase("M")) {
                month = stdt.nextToken();
            } else {
                year = stdt.nextToken();
            }
        }

        // Assert that we got valid values
        int dateLength = date.length();
        if (dateLength > 0
                && (year.length() == dateLength || month.length() == dateLength || day.length() == dateLength)) {
            throw new IllegalStateException("Date formatOrder is invalid: date=" + date + ", year=" + year + ", month="
                    + month + ", day=" + day);
        }

        return new String[] { day, month, year };
    }

    String render(Locale locale, String currentDateValue) throws JspException {
        String ddValue = "";
        String mmValue = "";
        String yyValue = "";
        String userfmt = getUserFormat(locale);
        String separator = DateUtils.getSeparator(userfmt);
        if (currentDateValue != null && !currentDateValue.equals("")) {
            String dmy[];
            if (getFormatOrder() == null) {
                dmy = getDayMonthYear(currentDateValue, DateUtils.convertToDateTagFormat(userfmt), separator);

            } else {
                String formatOrder = getFormatOrder();
                dmy = getDayMonthYear(currentDateValue, formatOrder, DateUtils.getSeparator(formatOrder));
            }
            ddValue = dmy[0].trim();
            mmValue = dmy[1].trim();
            yyValue = dmy[2].trim();
        }
        // user format
        String format = DateUtils.convertToDateTagFormat(userfmt);

        String output;
        String propertyVal;
        if (this.getIndexed()) {
            propertyVal = getName() + "[" + getIndexValue() + "]." + getProperty();
        } else {
            propertyVal = getProperty();
        }

        if (getRenderstyle().equalsIgnoreCase("simple")) {
            output = "<!-- simple style -->"
                    + makeUserFields(propertyVal, ddValue, mmValue, yyValue, "", format, separator);
            makeMappedUserFields(propertyVal, ddValue, mmValue, yyValue, "", format, separator);
        } else if (getRenderstyle().equalsIgnoreCase("simplemapped")) {
            output = "<!-- simple-mapped style -->"
                    + makeMappedUserFields(propertyVal, ddValue, mmValue, yyValue, "", format, separator);
        } else {
            output = "<!-- normal style -->"
                    + this.prepareOutputString(format, propertyVal, ddValue, mmValue, yyValue, separator, userfmt);
        }
        return output;
    }

    String getUserFormat(Locale locale) {
        // the following line will be removed when date is localized
        locale = new LocalizationConverter().getDateLocale();
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        return ((SimpleDateFormat) df).toPattern();
    }

    protected String returnValue() throws JspException {
        Object value;
        if (getRenderstyle().equalsIgnoreCase("simplemapped")) {
            String dateProperty = "dateValue(" + property + ")";
            value = TagUtils.getInstance().lookup(pageContext, name, dateProperty, null);
        } else {
            value = TagUtils.getInstance().lookup(pageContext, name, property, null);
        }
        if (value == null) {
            return "";
        }
        return TagUtils.getInstance().filter(value.toString());
    }

    String prepareOutputString(String format, String dateName, String ddValue, String mmValue, String yyValue,
            String separator, String userfrmt) {
        StringBuilder dateFunction = new StringBuilder();

        dateFunction.append("onBlur,");
        dateFunction.append("makeDateString(");
        String dateSeparator = new LocalizationConverter().getDateSeparatorForCurrentLocale();
        StringTokenizer tokenizer = new StringTokenizer(format, dateSeparator);
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
        if (ddValue != null && !ddValue.equals("") && mmValue != null && !mmValue.equals("") && yyValue != null
                && !yyValue.equals("")) {
            date = DateUtils.createDateString(ddValue, mmValue, yyValue, format);
        }

        XmlBuilder htmlBuilder = makeUserFields(dateName, ddValue, mmValue, yyValue, dateFunction.toString(), format,
                dateSeparator);
        htmlBuilder.singleTag("input", "type", "hidden", "id", dateName, "name", dateName, "value", date);
        htmlBuilder.singleTag("input", "type", "hidden", "id", dateName + "Format", "name", dateName + "Format",
                "value", format);
        htmlBuilder.singleTag("input", "type", "hidden", "id", "datePattern", "name", "datePattern", "value", userfrmt);

        return htmlBuilder.toString();
    }

    public XmlBuilder makeUserFields(String dateName, String ddValue, String mmValue, String yyValue,
            String dateFunction, String format, String separator) {

        boolean disabled = getIsDisabled() != null && getIsDisabled().equalsIgnoreCase("Yes") ? true : false;
        XmlBuilder htmlOutput = new XmlBuilder();
        XmlBuilder htmlBuilderDay = new XmlBuilder();
        XmlBuilder htmlBuilderMonth = new XmlBuilder();
        XmlBuilder htmlBuilderYear = new XmlBuilder();

        if (dateFunction.equals("")) {
            if (disabled) {
                htmlBuilderDay
                        .singleTag("input", "type", "text", "id", dateName + "DD", "name", dateName + "DD",
                                "maxlength", "2", "size", "2", "value", ddValue, "style", "width:1.5em", "disabled",
                                "disabled");
            } else {
                htmlBuilderDay.singleTag("input", "type", "text", "id", dateName + "DD", "name", dateName + "DD",
                        "maxlength", "2", "size", "2", "value", ddValue, "style", "width:1.5em");
            }
            htmlBuilderDay.nonBreakingSpace();
            htmlBuilderDay.text("DD");
            htmlBuilderDay.nonBreakingSpace();

            if (disabled) {
                htmlBuilderMonth
                        .singleTag("input", "type", "text", "id", dateName + "MM", "name", dateName + "MM",
                                "maxlength", "2", "size", "2", "value", mmValue, "style", "width:1.5em", "disabled",
                                "disabled");
            } else {
                htmlBuilderMonth.singleTag("input", "type", "text", "id", dateName + "MM", "name", dateName + "MM",
                        "maxlength", "2", "size", "2", "value", mmValue, "style", "width:1.5em");
            }
            htmlBuilderMonth.nonBreakingSpace();
            htmlBuilderMonth.text("MM");
            htmlBuilderMonth.nonBreakingSpace();

            if (disabled) {
                htmlBuilderYear.singleTag("input", "type", "text", "id", dateName + "YY", "name", dateName + "YY",
                        "maxlength", "4", "size", "4", "value", yyValue, "style", "width:3em", "disabled", "disabled");
            } else {
                htmlBuilderYear.singleTag("input", "type", "text", "id", dateName + "YY", "name", dateName + "YY",
                        "maxlength", "4", "size", "4", "value", yyValue, "style", "width:3em");
            }
            htmlBuilderYear.nonBreakingSpace();
            htmlBuilderYear.text("YYYY");
            htmlBuilderYear.nonBreakingSpace();
        } else {
            String strFirstPart = null;
            String strSecondPart = null;
            strFirstPart = dateFunction.substring(0, dateFunction.indexOf(","));
            strSecondPart = dateFunction.substring(dateFunction.indexOf(",") + 1);

            if (disabled) {
                htmlBuilderDay.singleTag("input", "type", "text", "id", dateName + "DD", "name", dateName + "DD",
                        "maxlength", "2", "size", "2", "value", ddValue, strFirstPart, strSecondPart, "style",
                        "width:1.5em", "disabled", "disabled");
            } else {
                htmlBuilderDay.singleTag("input", "type", "text", "id", dateName + "DD", "name", dateName + "DD",
                        "maxlength", "2", "size", "2", "value", ddValue, strFirstPart, strSecondPart, "style",
                        "width:1.5em");
            }
            htmlBuilderDay.nonBreakingSpace();
            htmlBuilderDay.text("DD");
            htmlBuilderDay.nonBreakingSpace();

            if (disabled) {
                htmlBuilderMonth.singleTag("input", "type", "text", "id", dateName + "MM", "name", dateName + "MM",
                        "maxlength", "2", "size", "2", "value", mmValue, strFirstPart, strSecondPart, "style",
                        "width:1.5em", "disabled", "disabled");
            } else {
                htmlBuilderMonth.singleTag("input", "type", "text", "id", dateName + "MM", "name", dateName + "MM",
                        "maxlength", "2", "size", "2", "value", mmValue, strFirstPart, strSecondPart, "style",
                        "width:1.5em");
            }
            htmlBuilderMonth.nonBreakingSpace();
            htmlBuilderMonth.text("MM");
            htmlBuilderMonth.nonBreakingSpace();

            if (disabled) {
                htmlBuilderYear.singleTag("input", "type", "text", "id", dateName + "YY", "name", dateName + "YY",
                        "maxlength", "4", "size", "4", "value", yyValue, strFirstPart, strSecondPart, "style",
                        "width:3em", "disabled", "disabled");
            } else {
                htmlBuilderYear.singleTag("input", "type", "text", "id", dateName + "YY", "name", dateName + "YY",
                        "maxlength", "4", "size", "4", "value", yyValue, strFirstPart, strSecondPart, "style",
                        "width:3em");
            }
            htmlBuilderYear.nonBreakingSpace();
            htmlBuilderYear.text("YYYY");
            htmlBuilderYear.nonBreakingSpace();

        }

        StringTokenizer tokenizer = new StringTokenizer(format, separator);
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

    public String makeMappedUserFields(String dateName, String ddValue, String mmValue, String yyValue,
            String dateFunction, String format, String separator) throws JspException {

        XmlBuilder htmlOutput = new XmlBuilder();
        XmlBuilder htmlBuilderDay = new XmlBuilder();
        XmlBuilder htmlBuilderMonth = new XmlBuilder();
        XmlBuilder htmlBuilderYear = new XmlBuilder();
        boolean disabled = getIsDisabled() != null && getIsDisabled().equalsIgnoreCase("Yes") ? true : false;

        if (disabled) {
            htmlBuilderDay.singleTag("input", "type", "text", "id", "value" + "(" + dateName + "_DD)", "name", "value"
                    + "(" + dateName + "_DD)", "maxlength", "2", "size", "2", "value", ddValue, "style", "width:1.5em",
                    "disabled", "disabled");
        } else {
            htmlBuilderDay.singleTag("input", "type", "text", "id", "value" + "(" + dateName + "_DD)", "name", "value"
                    + "(" + dateName + "_DD)", "maxlength", "2", "size", "2", "value", ddValue, "style", "width:1.5em");
        }
        htmlBuilderDay.nonBreakingSpace();
        htmlBuilderDay.text("DD");
        htmlBuilderDay.nonBreakingSpace();

        if (disabled) {
            htmlBuilderMonth.singleTag("input", "type", "text", "id", "value" + "(" + dateName + "_MM)", "name",
                    "value" + "(" + dateName + "_MM)", "maxlength", "2", "size", "2", "value", mmValue, "style",
                    "width:1.5em", "disabled", "disabled");
        } else {
            htmlBuilderMonth.singleTag("input", "type", "text", "id", "value" + "(" + dateName + "_MM)", "name",
                    "value" + "(" + dateName + "_MM)", "maxlength", "2", "size", "2", "value", mmValue, "style",
                    "width:1.5em");
        }
        htmlBuilderMonth.nonBreakingSpace();
        htmlBuilderMonth.text("MM");
        htmlBuilderMonth.nonBreakingSpace();

        if (disabled) {
            htmlBuilderYear.singleTag("input", "type", "text", "id", "value" + "(" + dateName + "_YY)", "name", "value"
                    + "(" + dateName + "_YY)", "maxlength", "4", "size", "4", "value", yyValue, "style", "width:3em",
                    "disabled", "disabled");
        } else {
            htmlBuilderYear.singleTag("input", "type", "text", "id", "value" + "(" + dateName + "_YY)", "name", "value"
                    + "(" + dateName + "_YY)", "maxlength", "4", "size", "4", "value", yyValue, "style", "width:3em");
        }
        htmlBuilderYear.nonBreakingSpace();
        htmlBuilderYear.text("YY");
        htmlBuilderYear.nonBreakingSpace();

        StringTokenizer tokenizer = new StringTokenizer(format, separator);
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
