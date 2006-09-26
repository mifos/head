/**
 
 * PersonnelBusinessProcessor.java    version: xxx
 
 
 
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

package org.mifos.application.personnel.business.handlers;

import org.mifos.application.customer.dao.SearchDAO;
import org.mifos.application.office.dao.OfficeDAO;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.valueobjects.Context;

/**
 * This is the business processor for the Personnel module. It takes care of
 * handling all the business logic for the Personnel module
 */
public class PersonnelBusinessProcessor extends MifosBusinessProcessor {

	private Office getOffice(short officeId) throws ApplicationException,
			SystemException {
		return new OfficeDAO().getOffice(officeId);
	}

	public void getUserList(Context context) throws SystemException,
			ApplicationException {

		SearchDAO searchDAO = new SearchDAO();
		String searchString = context.getSearchObject().getFromSearchNodeMap(
				"searchString");
		String searchType = PersonnelConstants.USER_LIST;
		String officeSearchId = getOffice(
				context.getUserContext().getBranchId()).getSearchId();
		context.setSearchResult(searchDAO.search(searchType, searchString,
				context.getUserContext().getLevelId(), officeSearchId, context
						.getUserContext().getId(), null));
	}
}
