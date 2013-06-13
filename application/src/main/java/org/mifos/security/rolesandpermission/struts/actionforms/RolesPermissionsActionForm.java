/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.security.rolesandpermission.struts.actionforms;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.util.helpers.Methods;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.dto.domain.ActivityRestrictionDto;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.security.rolesandpermission.exceptions.RolesPermissionException;
import org.mifos.security.util.helpers.ActivityRestrictionType;

public class RolesPermissionsActionForm extends BaseActionForm {

    private String id;

    private String name;

    private Map<String, String> activities = new HashMap<String, String>();
    
    private Map<String, String> restrictionsValues = new HashMap<String, String>();
    
    private Map<Short, ActivityRestrictionDto> activityRestrictionDtoMap = new HashMap<Short, ActivityRestrictionDto>();
    
    private List<ActivityRestrictionDto> activityRestrictionDtoToPersistList = new ArrayList<ActivityRestrictionDto>();
    
    private List<ActivityRestrictionType> invalidActivityRestrictionsValues = new ArrayList<ActivityRestrictionType>();

    public Map<String, String> getActivities() {
        return activities;
    }

    public void setActivities(Map<String, String> activities) {
        this.activities = activities;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActivity(String key, String value) {
        this.activities.put(key, value);
    }
    
    public void setRestrictionValue(String activtiyRestrictionTypeIdString, String amountValueString){
        this.restrictionsValues.put(activtiyRestrictionTypeIdString, amountValueString);
    }
    
    public void setActivityRestriction(String activtiyRestrictionTypeIdString) throws RolesPermissionException{
        String amountValueString = restrictionsValues.get(activtiyRestrictionTypeIdString);
        if (amountValueString != null && !amountValueString.isEmpty()) {
            Short activtiyRestrictionTypeId = new Short(activtiyRestrictionTypeIdString);
            BigDecimal amountValue = null;
            try {
                amountValue = new BigDecimal(amountValueString);

                if (amountValue.compareTo(BigDecimal.ZERO) > 0) {
	                	ActivityRestrictionDto activityRestrictionDto = activityRestrictionDtoMap.get(activtiyRestrictionTypeId);
	                if (activityRestrictionDto != null) {
	                    activityRestrictionDto.setAmountValue(amountValue);
	                    activityRestrictionDtoToPersistList.add(activityRestrictionDto);
	                } else {
	                    // add new restriction for role
	                    Short roleId = 0;
	                    if (id != null) {
	                        roleId = new Short(id);
	                    }
	                    activityRestrictionDto = new ActivityRestrictionDto(roleId, activtiyRestrictionTypeId, amountValue);
	                    activityRestrictionDtoMap.put(activtiyRestrictionTypeId, activityRestrictionDto);
	                }
	                this.activityRestrictionDtoToPersistList.add(activityRestrictionDto);
                } else {
                	invalidActivityRestrictionsValues.add(ActivityRestrictionType.getByValue(activtiyRestrictionTypeId));
                }
            } catch (NumberFormatException e) {
                invalidActivityRestrictionsValues.add(ActivityRestrictionType.getByValue(activtiyRestrictionTypeId));
            }
        }
    }
    
    public void resetActivityRestriction(){
        this.activityRestrictionDtoToPersistList.clear();
        this.activityRestrictionDtoMap.clear();
        this.invalidActivityRestrictionsValues.clear();
        this.restrictionsValues.clear();
    }
    
    public List<ActivityRestrictionDto> getActivityRestrictionDtoToPersistList() {
        return activityRestrictionDtoToPersistList;
    }

    public void setActivityRestrictionDtoToPersistList(List<ActivityRestrictionDto> activityRestrictionDtoToPersistList) {
        this.activityRestrictionDtoToPersistList = activityRestrictionDtoToPersistList;
    }

    public Map<Short, ActivityRestrictionDto> getActivityRestrictionDtoMap() {
		return activityRestrictionDtoMap;
	}

	public void setActivityRestrictionDtoMap(
			Map<Short, ActivityRestrictionDto> activityRestrictionDtoMap) {
		this.activityRestrictionDtoMap = activityRestrictionDtoMap;
	}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors actionErrors = new ActionErrors();
        String method = request.getParameter(Methods.method.toString());
        if (method != null && Methods.update.toString().equals(method) || Methods.create.toString().equals(method)) {
            for (ActivityRestrictionType activityRestrictionType : invalidActivityRestrictionsValues) {
                switch (activityRestrictionType) {
                case MAX_LOAN_AMOUNT_FOR_APPROVE:
                    addError(actionErrors, ConfigurationConstants.BRANCHOFFICE,
                            "roleandpermission.error.activity.restriction.invalid.value",
                            getMessageText(ActivityRestrictionType.MAX_LOAN_AMOUNT_FOR_APPROVE.getPropertiesKey()));
                    break;
                }
            }
        }
        if (!actionErrors.isEmpty()) {
            request.setAttribute("methodCalled", method);
        }
        this.invalidActivityRestrictionsValues.clear();
		return actionErrors;
	}

}
