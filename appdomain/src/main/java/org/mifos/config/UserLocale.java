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
