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
 
package org.mifos.framework.struts.tags;

import org.apache.struts.util.MessageResources;
import org.apache.struts.util.PropertyMessageResourcesFactory;

/*
 * This is used in the struts-config.xml files.
 */
public class MifosPropertyMessageResourcesFactory extends PropertyMessageResourcesFactory{
    
   

	@Override
	public MessageResources createResources(String config) {
        return new MifosPropertyMessageResources(this, config, this.returnNull);
    }
	
	 /**
	 * serial version UID for serailization
	 */
	private static final long serialVersionUID = 354254366564561L;
	
}
