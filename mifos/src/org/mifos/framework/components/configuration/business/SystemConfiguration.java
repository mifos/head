/**
 * Copyright (c) 2005-2007 Grameen Foundation USA
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
package org.mifos.framework.components.configuration.business;

import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.ConfigurationManager;
import org.mifos.config.Localization;

public class SystemConfiguration {
	/**
	 * Constant for looking up session timeout.
	 */
	public static final String SessionTimeout = "SessionTimeout";

	/**
	 * Default number of minutes to wait for user activity before expiring their
	 * session. If this number is changed, best also change SessionTimeout in
	 * {@link org.mifos.config.ConfigurationManager#DEFAULT_CONFIG_PROPS_FILENAME}.
	 * 
	 * @deprecated This value is only read in unit tests. See web.xml in src/
	 * and test/ for the correct place to change this value.
	 */
	public static final int defaultSessionTimeout = 60;

	private final MifosCurrency currency;
	private final Locale locale;
	private final TimeZone timeZone;

	public SystemConfiguration(MifosCurrency currency, int timeZoneOffSet) {
		this.currency = currency;
		this.timeZone = new SimpleTimeZone(timeZoneOffSet, SimpleTimeZone
				.getAvailableIDs(timeZoneOffSet)[0]);
		this.locale = Localization.getInstance().getMainLocale();
	}

	// Method is only used in unit tests as of 2008-JAN-11 -Adam
	public Locale getMFILocale() {
		return locale;
	}

	// Method is only used in unit tests as of 2008-JAN-11 -Adam
	public Short getMFILocaleId() {
		return Localization.getInstance().getLocaleId();
	}

	public MifosCurrency getCurrency() {
		return currency;
	}

	// Method is only used in unit tests as of 2008-JAN-11 -Adam
	/**
	 * Fetch number of minutes to wait for user activity before expiring their
	 * session.
	 */
	public Integer getSessionTimeOut() {
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		return configMgr.getInteger(SessionTimeout, defaultSessionTimeout);
	}
	
	public TimeZone getMifosTimeZone() {
		return timeZone;
	}
}
