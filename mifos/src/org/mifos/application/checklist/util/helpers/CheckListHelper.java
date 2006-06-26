package org.mifos.application.checklist.util.helpers;

import java.util.Iterator;
import java.util.List;

import org.mifos.application.checklist.util.resources.CheckListConstants;
import org.mifos.application.master.util.valueobjects.StatusMaster;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

public class CheckListHelper {

	public CheckListHelper() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * This function would return the search results based on the passed name
	 * and value
	 * 
	 * @param name
	 *            name of the search result
	 * @param value
	 *            value of the search result
	 * @return
	 */
	public static SearchResults getSearchResutls(String name, Object value) {
		SearchResults sr = new SearchResults();
		sr.setResultName(name);
		sr.setValue(value);
		return sr;
	}
	
	/**
	 * This function will save the specified object into the context 
	 * @param name name of the object we want to save 
	 * @param obj object that we want to save 
	 * @param context context object
	 */
	public static void saveInContext(String name, Object obj, Context context) {
		context.removeAttribute(name);
		SearchResults result = CheckListHelper.getSearchResutls(name, obj);
		context.addAttribute(result);		
	}
	
	public List<StatusMaster> pendingApprovalRequired(int typeId, int levelId, List list ,Context context) throws SystemException{
		List<StatusMaster> stateList = list;
		switch(levelId){
			case 1:   if( typeId==0 && (!Configuration.getInstance().getCustomerConfig(context.getUserContext().getBranchId()).isPendingApprovalStateDefinedForClient())){				
						stateList = removeStateFromCollection(list);
					  }
					  else if(typeId==1 && (!Configuration.getInstance().getAccountConfig(context.getUserContext().getBranchId()).isPendingApprovalStateDefinedForLoan())){
						  stateList = removeStateFromCollection(list);
					  }
					  break;
			case 2:   if(typeId==0 && (!Configuration.getInstance().getCustomerConfig(context.getUserContext().getBranchId()).isPendingApprovalStateDefinedForGroup())){			
						stateList = removeStateFromCollection(list);				
					  }
					 else if(typeId==1 && (!Configuration.getInstance().getAccountConfig(context.getUserContext().getBranchId()).isPendingApprovalStateDefinedForSavings())){
						 stateList = removeStateFromCollection(list);
					  }
					  break;
			default: return stateList;				
		}		
		return stateList;
	}
	
	public List<StatusMaster> removeStateFromCollection(List list){
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			StatusMaster statusMaster = (StatusMaster) iter.next();
			if(statusMaster.getStatusName().equals(CheckListConstants.PENDINGAPPROVAL)){				
				iter.remove();
			}					
		}
		return list;
	}

}
