/**

 * RolesandPermissionAction.java    version: 1.0

 

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

/**
 * 
 */
package org.mifos.application.rolesandpermission.struts.action;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.rolesandpermission.exceptions.RoleAlreadyExist;
import org.mifos.application.rolesandpermission.exceptions.RoleAndPermissionException;
import org.mifos.application.rolesandpermission.struts.actionforms.RolesandPermissionActionForm;
import org.mifos.application.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.application.rolesandpermission.util.helpers.RolesAndPermissionHelper;
import org.mifos.application.rolesandpermission.util.valueobjects.Role;
import org.mifos.framework.struts.action.MifosBaseAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * This class is for role and permission action it is get called for managing
 * the roles and permission
 * 
 * @author rajenders
 */
public class RolesandPermissionAction extends MifosBaseAction {

	 @TransactionDemarcate(validateAndResetToken=true)
	public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		ActionForward forward=null;
		// In case of exception ,while creation a role we want to save the token ,
		//so that we do not get doubble summit exception 
		try {
			forward= super.create(mapping, form, request, response);
		}
		catch(RoleAlreadyExist rae){
			
			saveToken(request);
			throw rae;
			
		}
		catch( RoleAndPermissionException rpe){
			saveToken(request);
			throw rpe;
		}
		return forward;
	}

	/**
	 * Default constructor for RolesandPermissionAction
	 */
	public RolesandPermissionAction() {
		super();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mifos.framework.struts.action.MifosBaseAction#getPath()
	 */
	@Override
	protected String getPath() {

		return RolesAndPermissionConstants.ROLEANDPERMISSIONDAO;
	}

	/**
	 * Custom load is called before the load method we need to show empity edit
	 * box so we are setting it empity
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward customLoad(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		((RolesandPermissionActionForm) form).setName("");

		return null;
	}

	/**
	 * Custom validate is called befor the validatte method i am setting buiding
	 * The ui templete again if validation fails then we need to update the ui
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward customValidate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Context ctx = (Context) request.getAttribute(Constants.CONTEXT);

		if (null != ctx) {
			Role role = (Role) ctx.getValueObject();
			if (null != role) {
				Set roleActivities = role.getActivities();

				if (null != roleActivities) {
					if (roleActivities.size() > 0) {
						List activityList = (List) ((SearchResults) ctx
								.getSearchResultBasedOnName(RolesAndPermissionConstants.ACTIVITYLIST))
								.getValue();

						String templete = RolesAndPermissionHelper
								.getTempleteBuffer(activityList, roleActivities, ctx.getUserContext().getLocaleId())
								.toString();
						((SearchResults) ctx
								.getSearchResultBasedOnName(RolesAndPermissionConstants.BUFF))
								.setValue(templete);
					}
				}
			}
		}

		return null;

	}

	 @TransactionDemarcate(validateAndResetToken = true)
	public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ActionForward forward=null;
		// In case of exception ,while updating a role we want to save the token ,
		//so that we do not get doubble summit exception 

		try {

			forward= super.update(mapping, form, request, response);
		}
		catch(RoleAlreadyExist rae){
			
			saveToken(request);
			throw rae;
			
		}
		catch( RoleAndPermissionException rpe){
			saveToken(request);
			throw rpe;
		}
		return forward;
	}

}
