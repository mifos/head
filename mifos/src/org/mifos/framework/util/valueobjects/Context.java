package org.mifos.framework.util.valueobjects;

import java.io.Serializable;
import java.lang.String;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;


import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.SearchObject;

/**
 *  This class acts as a wrapper around the parameters that need to passed from the action class to the business processor. As of now it has only moduleName,subModuleName and valueObject as its instance variable , but it will have more later depending upon the parameters that needs to be passes to all the action methods of the business processor. The context object is formed in the ActionClass and is then passed as parameter to the BusinessProcessor's execute method.
 */
public class Context implements Serializable {

	private String userId;
	private String branchId;
	private UserContext userContext;
	private String path;
	private ValueObject valueObject;
	private String businessAction;
	private SearchObject searchObject;
	private List results = new LinkedList();
	private Map<String,Object> businessResults= new HashMap<String,Object>();
	private QueryResult searchResult;

	public Context() {
	}

	/**
	 * It adds the ReturnType to the results list it has.
	 * This method is called to add search results or master data retrieved
	 * to the results list.
	 * Thus these search results are propagated back to the ActionClass
	 * where in postExecute this list is read
	 * and results are set in the request scope for them to be accessible in the jsp.
	 * @param obj
	 */
	public void addAttribute(ReturnType obj){
		Object searchObj = getSearchResultBasedOnName(obj.getResultName());
		if(searchObj != null)
			results.remove(searchObj);

		results.add(obj);
	}

	public List getAttributes(){
		return this.results;
	}

	public void setBusinessAction(String businessAction) {
		this.businessAction = businessAction;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Context(String path,ValueObject valueObject) {
		this.path = path;

		this.valueObject = valueObject;
	}

	public ValueObject getValueObject() {
		return this.valueObject ;
	}



	public String getPath() {
		return this.path ;
	}



	public String getBusinessAction() {
		return this.businessAction ;
	}

	public SearchObject SearchObject() {
		return searchObject;
	}

	public void setSearchObject(SearchObject searchObject) {
		this.searchObject = searchObject;
	}

	/**
	 * @param valueObject
	 */
	public void setValueObject(ValueObject valueObject) {
		this.valueObject = valueObject;

	}

	public SearchObject getSearchObject() {
		return searchObject;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Map<String, Object> getBusinessResults() {
		return businessResults;
	}

	public void setBusinessResults(Map<String, Object> businessResults) {
		this.businessResults = businessResults;
	}

	public List getResults() {
		return results;
	}

	public void setResults(List results) {
		this.results = results;
	}

	public void addBusinessResults(String key , Object value){
		businessResults.put(key, value);
	}

	public Object getBusinessResults(String key){
		return businessResults.get(key);
	}

	public UserContext getUserContext() {
		return userContext;
	}

	public void setUserContext(UserContext userContext) {
		this.userContext = userContext;
	}

	public SearchResults getSearchResultBasedOnName(String searchResultName){

		if(null!= results){
			for(Object obj:results){
				if(null != obj){
					if(((SearchResults)obj).getResultName().equals(searchResultName)){
						return ((SearchResults)obj);
					}
				}
			}
		}
		return null;
	}

	public QueryResult getSearchResult() {
		return searchResult;
	}

	public void setSearchResult(QueryResult searchResult) {
		this.searchResult = searchResult;
	}

	public void removeAttribute(String attributeToRemove)
	{
		if(!(attributeToRemove == null || attributeToRemove.equals("")))
		{
			Object removeObj = getSearchResultBasedOnName(attributeToRemove);
			if(removeObj != null)
				results.remove(removeObj);
		}
	}
	
	/**
	 *This method sets all the attributes to null.
	 *It is called from action to clean all master data. 
	 */
	// TODO: Actually, it does nothing.  What is it intended to do?
	public void cleanAttributes(){
		if(null != results){
			for(Object item:results){
				item=null;
			}
			
		}
	}

}