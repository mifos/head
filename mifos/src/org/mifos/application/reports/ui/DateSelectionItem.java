package org.mifos.application.reports.ui;

import static org.mifos.application.reports.util.helpers.ReportsConstants.NA_DATE;
import static org.mifos.application.reports.util.helpers.ReportsConstants.NOT_APPLICABLE_DISPLAY_NAME;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.mifos.framework.util.LocalizationConverter;

public class DateSelectionItem implements Serializable {

	private static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(
			"dd/MM/yyyy");
	public static final DateSelectionItem NA_MEETING_DATE = new DateSelectionItem(
			NA_DATE, NOT_APPLICABLE_DISPLAY_NAME);

	private Date date;
	private String displayDate;
	// For Hibernate
	public DateSelectionItem() {
		super();
	}

	public DateSelectionItem(Date date) {
		this.date = date;
	}

	public DateSelectionItem(Date date, String display) {
		this.date = date;
		this.displayDate = display;
	}

	private DateFormat getDateFormat() {
		DateFormat dateFormat;
		try {
			dateFormat = LocalizationConverter.getInstance().getDateFormat();
		}
		catch (RuntimeException e) {
			dateFormat = DEFAULT_DATE_FORMAT;
		}
		return dateFormat;
	}

	public Date getDate() {
		return date;
	}

	public String getDisplayDate() {
		return displayDate != null ? displayDate : getDateFormat().format(date);
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((date == null) ? 0 : date.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final DateSelectionItem other = (DateSelectionItem) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		}
		else if (!date.equals(other.date))
			return false;
		return true;
	}
}
