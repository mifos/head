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

package org.mifos.framework.components.customTableTag;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.mifos.framework.exceptions.TableTagParseException;

public class Row {

    private String totWidth = null;

    private Column[] column = null;

    private String bottomLineRequired = null;

    public void setTotWidth(String totWidth) {
        this.totWidth = totWidth;
    }

    public String getTotWidth() {
        return totWidth;
    }

    public void setColumn(Column[] column) {
        this.column = column;
    }

    public Column[] getColumn() {
        return column;
    }

    public String getBottomLineRequired() {
        return bottomLineRequired;
    }

    public void setBottomLineRequired(String bottomLineRequired) {
        this.bottomLineRequired = bottomLineRequired;
    }

    public void getRowHeader(StringBuilder tableInfo, PageContext pageContext, String bundle) throws JspException {
        Column[] column = getColumn();
        for (int i = 0; i < column.length; i++) {
            column[i].getColumnHeader(tableInfo, pageContext, bundle);
        }
    }

    public void generateTableRows(StringBuilder tableInfo, List obj, Locale locale, Locale prefferedLocale,
            Locale mfiLocale) throws TableTagParseException {
        Iterator it = obj.iterator();
        Column[] column = getColumn();
        while (it.hasNext()) {
            tableInfo.append("<tr>");
            Object objValue = it.next();
            for (int i = 0; i < column.length; i++) {
                column[i].generateTableColumn(tableInfo, objValue, locale, prefferedLocale, mfiLocale);
            }
            tableInfo.append("</tr>");
        }
    }

}
