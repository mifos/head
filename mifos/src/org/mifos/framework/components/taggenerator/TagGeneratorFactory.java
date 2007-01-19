/**
 
 * TagGeneratorFactory.java    version: 1.0
 
 
 
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
package org.mifos.framework.components.taggenerator;

import java.util.HashMap;
import java.util.Map;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.customer.business.CustomerAccountBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.exceptions.FrameworkRuntimeException;
import org.mifos.framework.exceptions.PageExpiredException;

public class TagGeneratorFactory {

	private Map<String, String> generatorNames = new HashMap<String, String>();

	private static TagGeneratorFactory instance = new TagGeneratorFactory();

	protected Map<String, String> getGeneratorNames() {
		return generatorNames;
	}

	private TagGeneratorFactory() {
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
				.put(
						"org.mifos.application.accounts.savings.business.SavingsBO",
						"org.mifos.framework.components.taggenerator.AccountTagGenerator");
		generatorNames
				.put("org.mifos.application.accounts.loan.business.LoanBO",
						"org.mifos.framework.components.taggenerator.AccountTagGenerator");
		generatorNames
				.put(
						"org.mifos.application.accounts.business.CustomerAccountBO",
						"org.mifos.framework.components.taggenerator.AccountTagGenerator");
		generatorNames
				.put("org.mifos.application.personnel.business.PersonnelBO",
						"org.mifos.framework.components.taggenerator.PersonnelTagGenerator");

	}

	public static TagGeneratorFactory getInstance() {
		return instance;
	}

	public TagGenerator getGenerator(BusinessObject bo) throws PageExpiredException {
		try {
			
			if ( bo==null) throw new PageExpiredException();
			return (TagGenerator) Class.forName(
					getGeneratorNames().get(getClassName(bo))).newInstance();
		} catch (ClassNotFoundException e) {
			throw new FrameworkRuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new FrameworkRuntimeException(e);
		} catch (InstantiationException e) {
			throw new FrameworkRuntimeException(e);
		}
	}

	private String getClassName(BusinessObject bo) {
		if (bo instanceof CenterBO)
			return "org.mifos.application.customer.center.business.CenterBO";
		if (bo instanceof GroupBO)
			return "org.mifos.application.customer.group.business.GroupBO";
		if (bo instanceof ClientBO)
			return "org.mifos.application.customer.client.business.ClientBO";
		if (bo instanceof SavingsBO)
			return "org.mifos.application.accounts.savings.business.SavingsBO";
		if (bo instanceof LoanBO)
			return "org.mifos.application.accounts.loan.business.LoanBO";
		if (bo instanceof CustomerAccountBO)
			return "org.mifos.application.accounts.business.CustomerAccountBO";
		if (bo instanceof OfficeBO)
			return "org.mifos.application.office.business.OfficeBO";
		if (bo instanceof PersonnelBO)
			return "org.mifos.application.personnel.business.PersonnelBO";
		if (bo instanceof CustomerBO) {
			if (((CustomerBO) bo).getCustomerLevel().getId().equals(
					CustomerLevel.CLIENT.getValue()))
				return "org.mifos.application.customer.client.business.ClientBO";
			if (((CustomerBO) bo).getCustomerLevel().getId().equals(
					CustomerLevel.GROUP.getValue()))
				return "org.mifos.application.customer.group.business.GroupBO";
			if (((CustomerBO) bo).getCustomerLevel().getId().equals(
					CustomerLevel.CENTER.getValue()))
				return "org.mifos.application.customer.center.business.CenterBO";
		}
		return null;
	}

}
