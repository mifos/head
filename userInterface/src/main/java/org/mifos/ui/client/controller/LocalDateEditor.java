package org.mifos.ui.client.controller;

import java.beans.PropertyEditorSupport;
import java.util.Locale;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class LocalDateEditor extends PropertyEditorSupport {

    private String datePattern;
    private final DateTimeFormatter dateTimeFormatter;
    private static final String SHORT_DATE_WITHOUT_TIME = "S-";
    
	public LocalDateEditor(Locale locale) {
	    super();
        this.datePattern = getDateFormatter(locale);
	    this.dateTimeFormatter = DateTimeFormat.forPattern(this.datePattern);
    }

    public void setAsText(String text) throws java.lang.IllegalArgumentException {
        LocalDate localDate = dateTimeFormatter.parseDateTime(text).toLocalDate();
        this.setValue(localDate);
    }

	@SuppressWarnings("PMD.OnlyOneReturn")
    public String getAsText() {
		LocalDate localDate = (LocalDate) getValue();
		if (localDate == null) {
    	    return "";
    	} else {
    		return this.dateTimeFormatter.print(localDate);
    	}
    }

    public String getDatePattern() {
        return this.datePattern;
    }

    private String getDateFormatter(Locale locale) {
        this.datePattern = DateTimeFormat.patternForStyle(SHORT_DATE_WITHOUT_TIME, locale);
        this.datePattern = changeTwoDigitYearToFourDigitYearIfNecessary(datePattern);
        return datePattern;
    }

    @SuppressWarnings("PMD.OnlyOneReturn")
    private String changeTwoDigitYearToFourDigitYearIfNecessary(String datePattern) {
        if (datePattern.matches(".*yyyy.*")) {
            return datePattern;
        } else {
            return datePattern.replaceFirst("yy", "yyyy");
        }
    }

}