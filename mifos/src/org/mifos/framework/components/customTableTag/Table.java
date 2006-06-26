package org.mifos.framework.components.customTableTag;

import java.util.*;

import org.mifos.framework.exceptions.TableTagParseException;

public class Table {

	private Row row = null;

	private HeaderDetails headerDetails = null;

	public void setHeaderDetails(HeaderDetails headerDetails) {
		this.headerDetails = headerDetails;
	}

	public HeaderDetails getHeaderDetails() {
		return headerDetails;
	}

	public void setRow(Row row) {
		this.row = row;
	}

	public Row getRow() {
		return row;
	}

	public void getTable(StringBuilder tableInfo, List obj, Locale locale,Locale prefferedLocale,Locale mfiLocale)
			throws TableTagParseException {
		tableInfo.append("<table width=\"" + getRow().getTotWidth()
				+ "%\" border=\"0\" cellpadding=\"3\" cellspacing=\"0\" >");

		// Start :: Generating Header
		tableInfo.append("<tr ");
		getHeaderDetails().getHeaderInfo(tableInfo);
		tableInfo.append(" >");
		getRow().getRowHeader(tableInfo);
		tableInfo.append("</tr>");
		// End :: Generationg Header

		// Start :: Generating Rows
		getRow().generateTableRows(tableInfo, obj, locale,prefferedLocale,mfiLocale);
		// End :: Generating Rows

		// Genrate Last Line :: This line will have the same style as listed for
		// columns
		if (getRow().getBottomLineRequired().equalsIgnoreCase("true")) {
			tableInfo.append("<tr>");
			Column[] column = getRow().getColumn();
			for (int i = 0; i < column.length; i++) {
				tableInfo.append("<td class=\""
						+ column[i].getColumnDetails().getRowStyle()
						+ "\">&nbsp;</td>");
			}
			tableInfo.append("</tr>");
		}

		tableInfo.append("</table>");
	}

}
