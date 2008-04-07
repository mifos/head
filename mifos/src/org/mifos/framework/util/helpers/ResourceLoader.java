/**

 * ResourceLoader.java    version: 1.0



 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 *

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the

 * License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

 * and how it is applied.

 *

 */
package org.mifos.framework.util.helpers;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * This class is used to load resources like xml file,resource bundles,
 * properties files etc.
 * It used Class loader to load these files.
 */
public class ResourceLoader {

	/**
	 * Returns the URI for the file name specified.
	 * It tries to load the file using the class loader and then 
	 * returns the URI corresponding to the file.
	 *
	 * Returns null if the file is not found (or, perhaps, if we aren't
	 * allowed to see it).
	 */
	public static URI getURI(String fileName) throws URISyntaxException{
		ClassLoader parent = ResourceLoader.class.getClassLoader().getParent();
		ClassLoader current = ResourceLoader.class.getClassLoader();
		URI uri = null;
		URL url = parent.getResource(fileName);
		if(null == url){
			 url = current.getResource(fileName);
		}

		if(null!= url){
			uri =url.toURI();
		}

		return uri;
	}


	/**
	 * Works exactly like {@link #getURI(String)} except the
	 * {@link URISyntaxException}, if caught, is wrapped in a
	 * {@link RuntimeException}.
	 */
	public static URI findResource(String fileName) {
		try {
			return getURI(fileName);
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
