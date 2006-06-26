package org.mifos.framework.util.helpers;

import java.util.Locale;

public class BundleKey {
	private Locale locale = null;

	private String key = null;

	public BundleKey(Locale locale, String key) {
		this.locale = locale;
		this.key = key;
	}

	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof BundleKey))
			return false;
		else {
			BundleKey newObj = (BundleKey) obj;
			if (newObj.locale.equals(this.locale)
					&& newObj.key.equalsIgnoreCase(this.key))
				return true;
			else
				return false;
		}
	}

	public int hashCode() {
		return this.locale.hashCode() + this.key.hashCode();
	}

}
