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

package org.mifos.framework.components.tabletag;

import java.util.Locale;

import javax.servlet.jsp.PageContext;

import org.mifos.framework.exceptions.TableTagException;

/**
 * This class renders the row to the table.
 */
public class Row {

    public Row() {
        super();
    }

    private Column[] column;

    /**
     * @return Returns the column.
     */
    public Column[] getColumn() {
        return column;
    }

    /**
     * @param column
     *            The column to set.
     */
    public void setColumn(Column[] column) {
        this.column = column;
    }

    private String tdrequired;

    /**
     * @return Returns the tdrequired.
     */
    public String getTdrequired() {
        return tdrequired;
    }

    /**
     * @param tdrequired
     *            The tdrequired to set.
     */
    public void setTdrequired(String tdrequired) {
        this.tdrequired = tdrequired;
    }

    private String suppressrow;

    public String getSuppressrow() {
        return suppressrow;
    }

    public void setSuppressrow(String suppressrow) {
        this.suppressrow = suppressrow;
    }

    /**
     * Function to get the row
     *
     * @param obj
     *            Object from the user.
     * @return row string.
     * @throws TableTagException
     */
    public String getRow(PageContext pageContext, Object obj, Locale locale, boolean isFlowRequired)
            throws TableTagException {
        StringBuilder row = new StringBuilder();
        StringBuilder columns = new StringBuilder();
        for (int i = 0; i < column.length; i++) {
            // Used to get the string for each column in a row
            String columnString = (column[i].getColumn(pageContext, obj, locale, isFlowRequired));
            if (!columnString.trim().equals("")) {
                columns.append(columnString).append("	");
            }
        }
        if (!(columns.toString().equals(""))) {
            row.append(columns.toString());
            if (getSuppressrow() != null && getSuppressrow().equals("true")
                    && !row.toString().contains("<span class=\"fontnormal\">/</span>")) {
                return "";
            }
            return row.toString();
        }
        return "";
    }
}
