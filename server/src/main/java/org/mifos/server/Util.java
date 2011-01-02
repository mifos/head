/*
 * Copyright (c) 2011 Grameen Foundation USA
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

package org.mifos.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jetty.util.resource.Resource;

/* package local, not public */
final class Util {
	private Util() {
	}

	public static Resource chop(final URL baseURL, String toChop) throws MalformedURLException, IOException {
		String base = baseURL.toExternalForm();
		if (!base.endsWith(toChop)) {
			throw new IllegalArgumentException(base + " does not endWith " + toChop);
		}
		base = base.substring(0, base.length() - toChop.length());
		
		if (base.startsWith("jar:file:") && base.endsWith("!")) {
			// If it was a jar:file:/.../.jar!/META-INF/web-fragment.xml, then 'jar:' & '!' has to go as well:
			base = base.substring(0, base.length() - 1);
			base = "file:" + base.substring("jar:file:".length());
		}
		return Resource.newResource(base);
	}
}
