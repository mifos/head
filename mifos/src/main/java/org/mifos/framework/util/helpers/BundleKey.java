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
 
package org.mifos.framework.util.helpers;

import java.util.Locale;

public class BundleKey {
	private Locale locale = null;

	private String key = null;

	public BundleKey(Locale locale, String key) {
		this.locale = locale;
		this.key = key;
	}

	@Override
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

	@Override
	public int hashCode() {
		return this.locale.hashCode() + this.key.hashCode();
	}

}
