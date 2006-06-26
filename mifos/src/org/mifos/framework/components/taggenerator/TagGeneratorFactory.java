/**
 
 * TagGeneratorFactory.java    version: 1.0
 
 
 
 * Copyright © 2005-2006 Grameen Foundation USA
 
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
package org.mifos.framework.components.taggenerator;

import java.util.HashMap;
import java.util.Map;

import org.mifos.framework.exceptions.FrameworkRuntimeException;

public class TagGeneratorFactory {

	private Map<String, String> generatorNames = new HashMap<String, String>();

	private static TagGeneratorFactory instance = new TagGeneratorFactory();
	
	

	protected Map<String, String> getGeneratorNames() {
		return generatorNames;
	}

	private TagGeneratorFactory() {
		generatorNames
				.put("org.mifos.application.customer.business.CustomerBO",
						"org.mifos.framework.components.taggenerator.CustomerTagGenerator");
		generatorNames
				.put("org.mifos.application.customer.client.business.ClientBO",
						"org.mifos.framework.components.taggenerator.CustomerTagGenerator");
		generatorNames
				.put("org.mifos.application.customer.center.business.CenterBO",
						"org.mifos.framework.components.taggenerator.CustomerTagGenerator");
		generatorNames
				.put("org.mifos.application.customer.group.business.GroupBO",
						"org.mifos.framework.components.taggenerator.CustomerTagGenerator");
		generatorNames
				.put("org.mifos.application.office.business.OfficeBO",
						"org.mifos.framework.components.taggenerator.OfficeTagGenerator");
		generatorNames
				.put("org.mifos.application.accounts.business.AccountBO",
						"org.mifos.framework.components.taggenerator.AccountTagGenerator");
		generatorNames
				.put("org.mifos.application.accounts.savings.business.SavingsBO",
						"org.mifos.framework.components.taggenerator.AccountTagGenerator");
	}

	public static TagGeneratorFactory getInstance() {
		return instance;
	}
	
	public TagGenerator getGenerator(String name){
		try{
			return (TagGenerator)Class.forName(getGeneratorNames().get(name)).newInstance();
		}catch(ClassNotFoundException cnfe){
			throw new FrameworkRuntimeException(cnfe);
		}catch(IllegalAccessException iae){
			throw new FrameworkRuntimeException(iae);
		}catch(InstantiationException ie){
			throw new FrameworkRuntimeException(ie);
		}
	}
}
