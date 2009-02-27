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

package org.mifos.application.bulkentry.struts.uihelpers;

public class BulkEntryTagUIHelper {

    private BulkEntryTagUIHelper() {
    }

    private static BulkEntryTagUIHelper instance = new BulkEntryTagUIHelper();

    public static BulkEntryTagUIHelper getInstance() {
        return instance;
    }

    public void generateTD(StringBuilder builder, int width, Object value, boolean isStyleClass) {
        builder.append("<td");
        if (isStyleClass) {
            builder.append(" class=\"drawtablerow\"");
        }
        builder.append(">");
        builder.append(value);
        builder.append("</td>");
    }

    public void generateTD(StringBuilder builder, int width, Object value) {
        builder.append("<td align=\"left\"");
        builder.append(">");
        builder.append(value);
        builder.append("</td>");
    }

    public void generateStartTD(StringBuilder builder, Object value, boolean isStyleClass) {
        builder.append("<td height=\"30\"");
        if (isStyleClass) {
            builder.append(" class=\"drawtablerow\"");
        }
        builder.append(">");
        builder.append(value);
        builder.append("</td>");
    }

    public void generateTD(StringBuilder builder, Object value) {
        builder.append("<td class=\"drawtablerowSmall\">");
        builder.append(value);
        builder.append("</td>");
    }

    public void generateEmptyTD(StringBuilder builder, boolean isStyleClass) {
        builder.append("<td height=\"30\"");
        if (isStyleClass) {
            builder.append(" class=\"drawtablerow\"");
        }
        builder.append(">&nbsp;</td>");
    }

    public void generateHiddenInput(StringBuilder builder, String name, Object value) {
        builder.append("<input type=\"hidden\"");
        builder.append("name=\"" + name + "\"");
        builder.append(" value=\"" + value + "\">");
    }

    public void generateTextInput(StringBuilder builder, String name, Object value) {
        builder.append("<input type=\"text\"");
        builder.append("name=\"" + name + "\"");
        builder.append(" value=\"" + value + "\"");
        builder.append(" size=\"6\" style=\"width:40px\" " + "class=\"fontnormal8pt\">");
    }

    public void generateTextInput(StringBuilder builder, String name, Object value, int rows, int columns, int size,
            int initialAccNo, int centerTotalCount, int loanProductSize, int savingsProductSize) {
        builder.append("<input type=\"text\"");
        builder.append("name=\"" + name + "\"");
        builder.append(" value=\"" + value + "\"");
        builder.append(" onkeypress=\"return numbersonly(");
        builder.append("this" + ",");
        builder.append("event");
        builder.append(")\"");
        builder.append("onblur=\"if( false == doValidation(this,null)) this.focus();else ");
        builder.append("checkTotalForCenter(");
        builder.append(rows);
        builder.append("," + columns + "," + size + "," + initialAccNo + "," + centerTotalCount + "," + loanProductSize
                + "," + savingsProductSize + ")\"");
        builder.append(" size=\"6\" style=\"width:40px\" " + "class=\"fontnormal8pt\">");
    }

    public void generateTextInput(StringBuilder builder, String name, Object value, int columns, int size,
            int loanProductSize, int savingsProductSize) {
        builder.append("<input type=\"text\"");
        builder.append(" name=\"" + name + "\"");
        builder.append(" value=\"" + value + "\"");
        builder.append(" onBlur=\"adjustGroupTotalForLoan(");
        builder.append(columns);
        builder.append("," + size + "," + loanProductSize + "," + savingsProductSize + ")\"");
        builder.append(" size=\"6\" style=\"width:40px\" " + "class=\"fontnormal8pt\">");
    }

    public void generateSavingsTextInput(StringBuilder builder, String name, Object value, int rows, int columns,
            int size, int initialAccNo, int loanProductsSize, int savingsProductsSize, int depWithFalg,
            int totalsColumn, int levelId) {
        builder.append("<input type=\"text\"");
        builder.append("name=\"" + name + "\"");
        builder.append(" value=\"" + value + "\"");
        builder.append(" onkeypress=\"return numbersonly(");
        builder.append("this" + ",");
        builder.append("event");
        builder.append(")\"");
        builder.append("onblur=\"if( false == doValidation(this,null)) this.focus();else ");
        builder.append("adjustTotalForCenter(");
        builder.append(rows);
        builder.append("," + columns + "," + size + "," + initialAccNo + "," + loanProductsSize + ","
                + savingsProductsSize + "," + depWithFalg + "," + totalsColumn + "," + levelId + ")\"");
        builder.append(" size=\"6\" style=\"width:40px\" " + "class=\"fontnormal8pt\">");
    }

    public void generateSavingsTextInput(StringBuilder builder, String name, Object value, int columns, int size,
            int depWithFlag, int loanProductSize, int savingsProductSize) {
        builder.append("<input type=\"text\"");
        builder.append("name=\"" + name + "\"");
        builder.append(" value=\"" + value + "\"");
        builder.append(" onkeypress=\"return numbersonly(");
        builder.append("this" + ",");
        builder.append("event");
        builder.append(")\"");
        builder.append("onblur=\"if( false == doValidation(this,null)) this.focus();else ");
        builder.append("adjustGroupTotalForSav(");
        builder.append(columns);
        builder.append("," + size + "," + depWithFlag + "," + loanProductSize + "," + savingsProductSize + ")\"");
        builder.append(" size=\"6\" style=\"width:40px\" " + "class=\"fontnormal8pt\">");
    }

    public void generateCustomerAccountTextInput(StringBuilder builder, String name, Object value, int rows,
            int columns, int size, int initialAccNo, int loanProductSize, int savingsProductSize, int levelId) {
        builder.append("<input type=\"text\"");
        builder.append("name=\"" + name + "\"");
        builder.append(" value=\"" + value + "\"");
        builder.append(" onkeypress=\"return numbersonly(");
        builder.append("this" + ",");
        builder.append("event");
        builder.append(")\"");
        builder.append("onblur=\"if( false == doValidation(this,null)) this.focus();else ");
        builder.append("adjustCustAccTotalForCenter(");
        builder.append(rows);
        builder.append("," + columns + "," + size + "," + initialAccNo + "," + loanProductSize + ","
                + savingsProductSize + "," + levelId + ")\"");
        builder.append(" size=\"6\" style=\"width:40px\" " + "class=\"fontnormal8pt\">");
    }

    public void generateCustomerAccountTextInput(StringBuilder builder, String name, Object value, int columns,
            int size, int loanProductSize, int savingsProductSize) {
        builder.append("<input type=\"text\"");
        builder.append("name=\"" + name + "\"");
        builder.append(" value=\"" + value + "\"");
        builder.append(" onkeypress=\"return numbersonly(");
        builder.append("this" + ",");
        builder.append("event");
        builder.append(")\"");
        builder.append("onblur=\"if( false == doValidation(this,null)) this.focus();else ");
        builder.append("adjustGroupTotalForCustAcc(");
        builder.append(columns);
        builder.append("," + size + "," + loanProductSize + "," + savingsProductSize + ")\"");
        builder.append(" size=\"6\" style=\"width:40px\" " + "class=\"fontnormal8pt\">");
    }

    public void generateStartTR(StringBuilder builder, String styleClass) {
        builder.append("<tr class=\"" + styleClass + "\">");
    }

    public void generateStartTR(StringBuilder builder) {
        builder.append("<tr>");
    }

    public void generateEndTR(StringBuilder builder) {
        builder.append("</tr>");
    }
}
