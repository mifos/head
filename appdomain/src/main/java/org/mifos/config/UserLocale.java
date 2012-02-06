package org.mifos.config;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Locale;

import org.mifos.application.admin.servicefacade.PersonnelServiceFacade;

public class UserLocale implements Serializable {
	
	private static final long serialVersionUID = -8446586477655602790L;
	
	private PersonnelServiceFacade personnelServiceFacade;
	
	public UserLocale(PersonnelServiceFacade personnelServiceFacade) {
		this.personnelServiceFacade = personnelServiceFacade;
	}
	
	public Locale getLocale() {
		return personnelServiceFacade.getUserPreferredLocale();
	}
	
	private DecimalFormat getDecimalFormat() {
		DecimalFormat decimalFormat = (DecimalFormat)DecimalFormat.getInstance(getLocale());
		if (decimalFormat == null) {
			decimalFormat = (DecimalFormat)DecimalFormat.getInstance();
		}
		
		return decimalFormat;
	}
	
	public int getGroupingSize() {
		return getDecimalFormat().getGroupingSize();
	}
	
	public char getGroupingSeparator() {
		return getDecimalFormat().getDecimalFormatSymbols().getGroupingSeparator();
	}
	
	public char getDecimalSeparator() {
		return getDecimalFormat().getDecimalFormatSymbols().getDecimalSeparator();
	}
	
}
