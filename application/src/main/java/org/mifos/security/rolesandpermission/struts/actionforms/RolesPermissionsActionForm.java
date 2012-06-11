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

import org.mifos.dto.domain.ActivityRestrictionDto;
import org.mifos.framework.struts.actionforms.BaseActionForm;

public class RolesPermissionsActionForm extends BaseActionForm {

    private String id;

    private String name;

    private Map<String, String> activities = new HashMap<String, String>();
    
    private List<ActivityRestrictionDto> activityRestrictionDtoList = new ArrayList<ActivityRestrictionDto>();
    
    private List<ActivityRestrictionDto> activityRestrictionDtoToPersistList = new ArrayList<ActivityRestrictionDto>();

    /**
     * map of activityRestrictionId and activityRestrictionTypeId 
     */
    private Map<String, String> activityRestrictionIdAndTypeIdMap = new HashMap<String, String>();
    
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
    
    public void setActivityRestriction(String activtiyRestrictionTypeIdString, String amountValueString){
        if ( amountValueString != null && !amountValueString.isEmpty() ){
            Short activtiyRestrictionTypeId = new Short(activtiyRestrictionTypeIdString);
            BigDecimal amountValue = new BigDecimal(amountValueString);
            for (ActivityRestrictionDto activityRestrictionDtoIterator : this.activityRestrictionDtoList){ // look for existing restriction for update
                if ( activityRestrictionDtoIterator.getActivityRestrictionTypeId().equals(activtiyRestrictionTypeId)){
                    activityRestrictionDtoIterator.setAmountValue(amountValue);
                    activityRestrictionDtoToPersistList.add(activityRestrictionDtoIterator);
                    return;
                }
            }
            // add new restriction for role
            Short roleId = 0;
            if ( id != null ){
                roleId = new Short(id);
            }
            this.activityRestrictionDtoToPersistList.add(new ActivityRestrictionDto(roleId, activtiyRestrictionTypeId, amountValue));
        }
    }
    
    public void resetActivityRestriction(){
        this.activityRestrictionDtoList.clear();
        this.activityRestrictionDtoToPersistList.clear();
        this.activityRestrictionIdAndTypeIdMap.clear();
    }
    
    public List<ActivityRestrictionDto> getActivityRestrictionDtoList() {
        return activityRestrictionDtoList;
    }

    public void setActivityRestrictionDtoList(List<ActivityRestrictionDto> activityRestrictionDtoList) {
        this.activityRestrictionDtoList = activityRestrictionDtoList;
    }

    public List<ActivityRestrictionDto> getActivityRestrictionDtoToPersistList() {
        return activityRestrictionDtoToPersistList;
    }

    public void setActivityRestrictionDtoToPersistList(List<ActivityRestrictionDto> activityRestrictionDtoToPersistList) {
        this.activityRestrictionDtoToPersistList = activityRestrictionDtoToPersistList;
    }

    public Map<String, String> getActivityRestrictionIdAndTypeIdMap() {
        return activityRestrictionIdAndTypeIdMap;
    }

    public void setActivityRestrictionIdAndTypeIdMap(Map<String, String> activityRestrictionIdAndTypeIdMap) {
        this.activityRestrictionIdAndTypeIdMap = activityRestrictionIdAndTypeIdMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
