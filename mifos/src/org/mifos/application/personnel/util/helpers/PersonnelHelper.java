/**
 
 * PersonnelHelper    version: 1.0
 
 
 
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
package org.mifos.application.personnel.util.helpers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.valueobjects.SearchResults;

import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.master.util.valueobjects.StatusMaster;
import org.mifos.application.personnel.dao.PersonnelDAO;
import org.mifos.application.personnel.struts.actionforms.PersonnelActionForm;
import org.mifos.application.personnel.util.valueobjects.PersonnelDetails;
import org.mifos.application.personnel.util.valueobjects.PersonnelRole;
import org.mifos.application.rolesandpermission.util.valueobjects.Role;


/**
 * This class is used as helper class to personnel component
 * @author ashishsm
 *
 */
public class PersonnelHelper {
	
	/***
	 * This method checks if a particuar value is either null or blank
	 * @param value the value that has to be checked as to whether it is null or blank
	 * @return true or false as to whether the value passed was null or blank
	 */
	public static boolean isNullOrBlank(String value){
		boolean isValueNull = false;
		
		if(value == null || value.equals(CenterConstants.BLANK)){
			isValueNull = true;
		}
		return isValueNull;
	}
	
	/***
	 * This method trims string entered for creating/editing user.
	 * @param actionForm and instance of  PersonnelActionForm
	 */
	public void trimEnteredString(PersonnelActionForm actionForm){
		PersonnelDetails personnelDetails = actionForm.getPersonnelDetails();
		
		//trim entered name
		personnelDetails.setFirstName(personnelDetails.getFirstName().trim());
		if(personnelDetails.getMiddleName() != null)
			personnelDetails.setMiddleName(personnelDetails.getMiddleName().trim());
		if(personnelDetails.getSecondLastName()!=null)
		personnelDetails.setSecondLastName(personnelDetails.getSecondLastName().trim());
		personnelDetails.setLastName(personnelDetails.getLastName().trim());
		//trim govt. id
		if(personnelDetails.getGovernmentIdNumber()!=null)
		personnelDetails.setGovernmentIdNumber(personnelDetails.getGovernmentIdNumber().trim());
		//trim entered address
		personnelDetails.setAddress1(personnelDetails.getAddress1().trim());
		personnelDetails.setAddress2(personnelDetails.getAddress2().trim());
		if(personnelDetails.getAddress3()!=null)
		personnelDetails.setAddress3(personnelDetails.getAddress3().trim());
		personnelDetails.setCity(personnelDetails.getCity().trim());
		if(personnelDetails.getCountry()!=null)
			personnelDetails.setCountry(personnelDetails.getCountry().trim());
		if(personnelDetails.getState()!=null)
			personnelDetails.setState(personnelDetails.getState().trim());
		if(personnelDetails.getPostalCode()!=null)
			personnelDetails.setPostalCode(personnelDetails.getPostalCode().trim());
		if(personnelDetails.getTelephone()!=null)
		personnelDetails.setTelephone(personnelDetails.getTelephone().trim());
	}
	
	/** 
	 * This method creates a new SearchResults object with values as passed in parameters
	 * @param resultName the name with which framework will put result value in request
	 * @param resultValue that need to be put in request
	 * @return SearchResults instance
	 */ 
	public SearchResults getResultObject(String resultName, Object resultValue){
		SearchResults result = new SearchResults();
		result.setResultName(resultName);
		result.setValue(resultValue);
		return result;
	}
	
	/**
	 * This method prepares a list of Role from the array of roleIds that user has selected 
	 * @param rolesList this contains instance of all roles (master role list)
	 * @param personnelRoles array of roles ids respective to which role list is to be returned
	 * @return List of Roles that user has selected
	 */	
	public List<Role> getSelectedRoles(List<Role> rolesList, String[] personnelRoles){
		List<Role> newRoleList = null;
		if(personnelRoles!=null){
			newRoleList=new ArrayList<Role>();
			for(int j=0;j<personnelRoles.length;j++){
				if (!isNullOrBlank(personnelRoles[j])){
					short roleId = Short.valueOf(personnelRoles[j]);
					for(int i=0;i<rolesList.size();i++){
						if(roleId==rolesList.get(i).getId().shortValue()){
							newRoleList.add(rolesList.get(i));
						}
					}
				}
			}
		}
		return newRoleList;
	}
	
	/**
	 * This method prepares a list of PersonnelRoles from the array of roleIds that user has selected 
	 * @param rolesList this contains instance of all roles (master role list)
	 * @param personnelRoles array of roles ids respective to which role list is to be returned
	 * @return List of PersonnelRoles instances
	 */	
	public Set<PersonnelRole> getPersonnelRoles(List<Role> rolesList, String[] personnelRoles){
		Set<PersonnelRole> newRoleList = null;
		if(personnelRoles!=null){
			newRoleList=new HashSet<PersonnelRole>();
			for(int j=0;j<personnelRoles.length;j++){
				if (!isNullOrBlank(personnelRoles[j])){
					short roleId = Short.valueOf(personnelRoles[j]);
					for(int i=0;i<rolesList.size();i++){
						if(roleId==rolesList.get(i).getId().shortValue()){
							PersonnelRole pr = new PersonnelRole();
							pr.setRole(rolesList.get(i));
							newRoleList.add(pr);
						}
					}
				}
			}
		}
		return newRoleList;
	}
	
	/**
	 * This method prepares a list of Role from the array of roleIds that user has selected 
	 * @param rolesList this contains instance of all roles (master role list)
	 * @param personnelRoles is set of personnelRoles instance that are presently assigned to user
	 * @return List of Roles
	 */	
	public List<Role> getPersonnelRolesList(List<Role> rolesList, Set<PersonnelRole> personnelRoles){
		List<Role> newRoleList = null;
		if(personnelRoles!=null){
			Iterator personnelRolesIterator = personnelRoles.iterator();
			newRoleList=new ArrayList<Role>();
			while(personnelRolesIterator.hasNext()){
				short roleId = ((PersonnelRole)personnelRolesIterator.next()).getRole().getId();
				for(int i=0;i<rolesList.size();i++){
					if(roleId==rolesList.get(i).getId().shortValue()){
						newRoleList.add(rolesList.get(i));
					}
				}
			}
		}
		return newRoleList;
	}
	
	/**
	 * This method returns the name of status configured for statusId in the given locale
	 * @param localeId  user locale
	 * @param statusId status id
	 * @return string status name 
	 * @throws ApplicationException
	 * @throws SystemException
	 */		
	public String getStatusName(short localeId, short statusId)throws ApplicationException,SystemException{
		  String statusName=null;
		  List<StatusMaster> statusList =(List) new PersonnelDAO().getStatusMaster(localeId,statusId).getValue();
			 if(statusList!=null){
				 StatusMaster sm = (StatusMaster)statusList.get(0);
				 statusName= sm.getStatusName();
			 }
		return statusName;
	  }
	
	/**
	 * This method concatenates differnt address lines and forms one line of address
	 * @param personnelDetails  PersonnelDetails
	 * @return string single line address 
	 */	
	public String getDisplayAddress(PersonnelDetails personnelDetails){
		String displayAddress="";
		if(!isNullOrBlank(personnelDetails.getAddress1()))
			displayAddress+=personnelDetails.getAddress1();
		if(!isNullOrBlank(displayAddress) && !isNullOrBlank(personnelDetails.getAddress2()))
			displayAddress+=", ";
		if(!isNullOrBlank(personnelDetails.getAddress2()))
			displayAddress+=personnelDetails.getAddress2();
		
		if(!isNullOrBlank(displayAddress) && !isNullOrBlank(personnelDetails.getAddress3()))
			displayAddress+=", ";
		if(!isNullOrBlank(personnelDetails.getAddress3()))
			displayAddress+=personnelDetails.getAddress3();
		return displayAddress;
	}
	
	/**
	 * This method concatenates differnt names e.g first name, middle name etc. for the user
	 * @param personnelDetails  PersonnelDetails
	 * @return string single line display name for the user
	 */	
	public String getDisplayName(PersonnelDetails personnelDetails){
		String displayName="";
		if(!isNullOrBlank(personnelDetails.getFirstName()))
			displayName+=personnelDetails.getFirstName();
		if(!isNullOrBlank(personnelDetails.getMiddleName()))
			displayName+=" "+ personnelDetails.getMiddleName();
		if(!isNullOrBlank(personnelDetails.getSecondLastName()))
			displayName+=" "+ personnelDetails.getSecondLastName();
		if(!isNullOrBlank(personnelDetails.getLastName()))
			displayName+=" "+ personnelDetails.getLastName();
		return displayName;
	}
	/**
	 * This method returns current date 
	 * @return instance of sql Date
	 */
	public Date getCurrentDate(){
		return new java.sql.Date(new java.util.Date().getTime());
	}
}
