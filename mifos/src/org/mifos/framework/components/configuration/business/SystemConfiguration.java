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
	 * <code>src/org/mifos/config/resources/applicationConfiguration.default.properties</code>.
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

	public Locale getMFILocale() {
		return locale;
	}

	public Short getMFILocaleId() {
		return Localization.getInstance().getLocaleId();
	}

	public MifosCurrency getCurrency() {
		return currency;
	}

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
