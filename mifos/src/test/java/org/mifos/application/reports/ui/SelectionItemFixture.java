package org.mifos.application.reports.ui;

import static org.mifos.framework.util.helpers.NumberUtils.*;
public class SelectionItemFixture {
	public static SelectionItem createSelectionItem(Integer id) {
		return new SelectionItem(id, "");
	}

	public static SelectionItem createSelectionItem(Short personnelId) {
		return new SelectionItem(convertShortToInteger(personnelId), "");
	}
}
