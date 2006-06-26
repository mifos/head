/**

 * Group.java    version: 1.0



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

package org.mifos.application.customer.group.business;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.framework.security.util.UserContext;

/**
 * This class denotes the Group (row in customer table) object and all attributes associated with it.
 * It has a composition of other objects like Custom fields, fees, personnel etc., since it inherits from Customer
 * @author navitas
 */
public class GroupBO extends CustomerBO {

	public GroupBO(){}
	
	public GroupBO(UserContext userContext){
		super(userContext);
	}
	public boolean isCustomerActive()
	{
		if(getCustomerStatus().getStatusId().equals(GroupConstants.ACTIVE))
			return true;
		return false;
	}

}