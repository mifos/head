/**

 * GroupHelper.java    version: 1.0



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

package org.mifos.application.customer.group.util.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.customer.group.util.valueobjects.Group;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerHelper;
import org.mifos.application.customer.util.helpers.IdGenerator;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.customer.util.valueobjects.CustomerPosition;
import org.mifos.application.customer.util.valueobjects.CustomerPositionDisplay;
import org.mifos.application.master.util.valueobjects.PositionMaster;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.office.util.valueobjects.BranchOffice;
import org.mifos.application.office.util.valueobjects.BranchParentOffice;
import org.mifos.application.program.util.valueobjects.ProgramMaster;
import org.mifos.framework.dao.helpers.MasterDataRetriever;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * This class is helper class for group module.
 * @author navitas
 */

public class GroupHelper {

	/**
	 * This method returns an instance of CenterSearchInput. It is used by center search, that is called before
	 * creating a group, in case center hierarchy exists.
	 * @param officeId office for which center search is to be done.
	 * @param groupInput tells for which purpose search is being used.
	 */
	public CenterSearchInput getCenterSearchInput(short officeId, String groupInput){
		CenterSearchInput centerSearchInput= new CenterSearchInput();
		centerSearchInput.setOfficeId(officeId);
		centerSearchInput.setGroupInput(groupInput);
		return centerSearchInput;
	}

	/**
	 * This method returns list of all branches in all regions
	 * @return list of branch parents and branch offices under it
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public List getBranchList() throws ApplicationException,SystemException{
		  List<BranchParentOffice> branchList = new ArrayList<BranchParentOffice>();
		  List branchParents = (List)new GroupHelper().getBranchParents().getValue();
		  // System.out.println("-------------no of branch parents found: "+ branchParents.size());
		  BranchParentOffice parent=null;
		  if(null!=branchParents){
			  List<BranchOffice> branchOffices=(List)new GroupHelper().getBranchOffices().getValue();
			  // System.out.println("-------------------no of branch offices: "+ branchOffices.size());
			  Iterator<BranchParentOffice> parentIterator = branchParents.iterator();
			  while(parentIterator.hasNext()){
				  parent =(BranchParentOffice)parentIterator.next();
				  parent.setBranchOffice((new GroupHelper()).getBranchOfficesForParent(branchOffices,parent.getOfficeId()));
				  branchList.add(parent);
			  }
		  }
		  return branchList;
	  }

	/**
	 * This method returns list of all regions
	 * @return SearchResults list of BranchParent objects
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getBranchParents()throws ApplicationException,SystemException{
		MasterDataRetriever masterDataRetriever = null;
		try{
			masterDataRetriever = getMasterDataRetriever();
		}
		catch(HibernateProcessException hpe){
			throw new ApplicationException(hpe);
		}
		masterDataRetriever.prepare(NamedQueryConstants.MASTERDATA_BRANCH_PARENTS, GroupConstants.BRANCH_PARENTS);
		//masterDataRetriever.setParameter("statusId",OfficeConstants.ACTIVE);
		return masterDataRetriever.retrieve();
	}

	/**
	 * This method returns list of positions for the group, to which clients are to be assigned
	 * @param localeId user locale
	 * @return SearchResults list of Positions for the group
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getPositionsMaster(short localeId)throws ApplicationException,SystemException{
		MasterDataRetriever masterDataRetriever = null;
		try{
			masterDataRetriever = getMasterDataRetriever();
		}
		catch(HibernateProcessException hpe){
			throw new ApplicationException(hpe);
		}
		masterDataRetriever.prepare(NamedQueryConstants.MASTERDATA_POSITIONS, GroupConstants.POSITIONS);
		masterDataRetriever.setParameter("localeId",localeId);
		return masterDataRetriever.retrieve();
	}

	/**
	 * This method is helper method and returns list of all branches that belong to given branchparent.
	 * @param branchOffices list of all branched
 	 * @param parentId parentbranchid
	 * @return list of branch offices belonging to given parent
	 */
	public List<BranchOffice> getBranchOfficesForParent(List<BranchOffice> branchOffices, short parentId){
		  List<BranchOffice> branchOfficeList = null;
		  BranchOffice bo=null;
		  if(null!=branchOffices){
			  branchOfficeList=new ArrayList<BranchOffice>();
			  Iterator<BranchOffice> it = branchOffices.iterator();
			  while(it.hasNext()){
				  bo=(BranchOffice)it.next();
				  if (bo.getParentOfficeId().shortValue()==parentId){
					  branchOfficeList.add(bo);
				  }
			  }
		  }
		  return branchOfficeList;
	  }

	/**
	 * This method returns list of branches.
	 * @return SearchResults list of branches
	 * @throws ApplicationException
	 * @throws SystemException
	 */

	public SearchResults getBranchOffices()throws ApplicationException,SystemException{
		MasterDataRetriever masterDataRetriever = null;

		try{
			masterDataRetriever = getMasterDataRetriever();
		}
		catch(HibernateProcessException hpe){
			throw new ApplicationException(hpe);
		}
		masterDataRetriever.prepare(NamedQueryConstants.MASTERDATA_BRANCH_OFFICES, GroupConstants.BRANCH_OFFICES);
		//masterDataRetriever.setParameter("statusId",OfficeConstants.ACTIVE);
		return masterDataRetriever.retrieve();
	}

	/**
	 * This method returns list of all centers in the given office.
	 * @param officeId
	 * @return SearchResults instance that has list of centers
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getCentersForBranch(short officeId, short statusId)throws ApplicationException,SystemException{
		MasterDataRetriever masterDataRetriever = null;

		try{
			masterDataRetriever = getMasterDataRetriever();
		}
		catch(HibernateProcessException hpe){
			throw new ApplicationException(hpe);
		}
		masterDataRetriever.prepare(NamedQueryConstants.MASTERDATA_CENTERS_FOR_BRANCH, GroupConstants.CENTER_LIST);
		masterDataRetriever.setParameter("branchId" ,officeId);
		masterDataRetriever.setParameter("statusId" ,statusId);
		return masterDataRetriever.retrieve();
	}

	/**
	 * This method returns the searchId for the group.
	 * It is called to get the searchId for new group that is being created.
	 * @param parent, when customer hierarchy does not exist, parent is passed as null;
	 * @return String searchId for the new group.
	 * @throws ApplicationException
	 * @throws SystemException
	 */

	public String getSearchId(Customer parent,short officeId)throws ApplicationException,SystemException{
		String searchId=null;
		if(parent!=null){	//center hierarchy exists
			  int childCount=0;
			  if(null!=parent.getMaxChildCount())
				  childCount=parent.getMaxChildCount().intValue();
			  childCount++;
			  searchId=parent.getSearchId()+"."+childCount;
			  parent.setMaxChildCount(childCount);
		  }else{	//center hierarchy does not exists
			  int customerCount=new CustomerHelper().getCustomerCount(CustomerConstants.GROUP_LEVEL_ID,officeId)+1;
			  searchId=GroupConstants.PREFIX_SEARCH_STRING + String.valueOf(customerCount);
		  }
		return searchId;
	}

	/**
	 * This method build a map of ProgramMaster object from the list of programs.
	 * It is the helper method
	 * @param List program list
	 * @return Map ProgramMaster objects map
	 */
	public Map getProgramsMap(List programs){
		Map programsMap = new HashMap();
		Iterator iterator = programs.iterator();
		while(iterator.hasNext()){
		  ProgramMaster pr= (ProgramMaster)iterator.next();
		  programsMap.put(pr.getProgramId(),pr.getProgramName());
		}
		return programsMap;
	}

	/**
	 * This method is used to retrieve MasterDataRetriver instance
	 * @return instance of MasterDataRetriever
	 * @throws HibernateProcessException
	 */
	public MasterDataRetriever getMasterDataRetriever()throws HibernateProcessException{
		return new MasterDataRetriever();
	}

	/**
	 * This method retrieves list of custom fields for the group
	 * @return List of custom fields
	 * @throws ApplicationExcpetion
	 * @throws SystemExcpetion
	 */
//	public List loadCustomFields()throws ApplicationException,SystemException{
//		SearchResults sr =  new CustomerHelper().getCustomFieldDefnMaster(CustomerConstants.GROUP_LEVEL_ID, GroupConstants.ENTITY_TYPE);
//		return (List)sr.getValue();
//	}

	/**
	 * This method prepares a list of ProgramMaster that is used to show which program is associate to given group
	 * @param group
	 * @return List of Programs
	 * @throws ApplicationExcpetion
	 * @throws SystemExcpetion
	 *
	public List loadCustomerPrograms(Group group, short localeId)throws ApplicationException,SystemException{
		List<ProgramMaster> customerProgram = new ArrayList<ProgramMaster>();
	    SearchResults sr =  new CustomerHelper().getProgramMaster(localeId);
		if(sr!=null){
			List programList =(List) sr.getValue();
			Set programSet = group.getCustomerProgram();

			if(programList!=null && programSet!=null){
				Iterator custPrg = programSet.iterator();
				ProgramMaster pm=null;
				CustomerProgram cp=null;
				Iterator prgMaster=null;
				while (custPrg.hasNext()){
					cp=(CustomerProgram)custPrg.next();
					prgMaster = programList.iterator();
					while(prgMaster.hasNext()){
						pm=(ProgramMaster)prgMaster.next();
						if(pm.getProgramId().intValue()==cp.getProgramId().intValue())
							customerProgram.add(pm);
					}
				}
			}
		}
		return customerProgram;
	  }
*/
	/**
	 * This method prepares a list of CustomerPositionDisplay that tells which position is assigned to which customer.
	 * @param group
	 * @return List of CustomerPositionDisplay
	 * @throws ApplicationExcpetion
	 * @throws SystemExcpetion
	 */
	public List loadCustomerPositions(Group group, short localeId,List positionMaster)throws ApplicationException,SystemException{
		List<CustomerPositionDisplay> customerPositions = new ArrayList<CustomerPositionDisplay>();
		//SearchResults sr =  new GroupHelper().getPositionsMaster(localeId);
		
			//List positionMaster =(List) sr.getValue();
			Set positionsSet = group.getCustomerPositions();

			if(positionMaster!=null && positionsSet!=null){
				PositionMaster pm=null;
				CustomerPosition cp=null;
				CustomerPositionDisplay cpdisplay=null;
				Iterator posMaster=positionMaster.iterator();
				Iterator custPos=null;
				while(posMaster.hasNext()){
					pm=(PositionMaster)posMaster.next();
					cpdisplay = new CustomerPositionDisplay();
					cpdisplay.setPositionId(pm.getPositionId());
					cpdisplay.setPositionName(pm.getPositionName());
					custPos=positionsSet.iterator();
					while(custPos.hasNext()){
						cp=(CustomerPosition)custPos.next();
						if(cp.getPositionId().intValue()==pm.getPositionId().intValue()){
							cpdisplay.setCustomerName(cp.getCustomerName());
							cpdisplay.setCustomerId(cp.getCustomerId());
						}
					}
					customerPositions.add(cpdisplay);
				}
			}
		
		return  customerPositions;
	  }

	/**
	 * This method remove customer positions to which clients are not assigned
	 * @param customerPositions set of all customer positions applicable for the customer along with client information
	 * @return Set positions to which clients are assigned
	 */
	public Set checkForNullCustomerPositions(Set customerPositions){
		Set<CustomerPosition> newCustPos = new HashSet<CustomerPosition>();
		if(customerPositions!=null){
			Object custPos[]= customerPositions.toArray();
			for(int i=0;i<custPos.length;i++)
				if(((CustomerPosition)custPos[i]).getCustomerId()!=null)
					newCustPos.add((CustomerPosition)custPos[i]);
		}
		return newCustPos;
	}
}
