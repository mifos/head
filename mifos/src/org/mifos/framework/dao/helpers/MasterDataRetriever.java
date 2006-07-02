/**

 * MasterDataRetriever.java    version: xxx



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

package org.mifos.framework.dao.helpers;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.master.dao.MasterDAO;
import org.mifos.application.master.util.valueobjects.EntityMaster;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.HibernateSystemException;
import org.mifos.framework.exceptions.MasterDataRetrieverException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.valueobjects.SearchResults;


/**
 * Used to retrieve master data from the database/master data cache and add it to the cache if hinted so by the user.
 * It uses hibernate named query to fetch master data based on the
 * name of the query.The reset method should be called before
 * reusing the master data retriever object to execute another
 * query after it returns the results of the first query.
 * If there was an exception while retrieving the master data this object
 * should be discarded and a new object should be created.
 * @author ashishsm
 *
 */
public class MasterDataRetriever {

	private Query query;

	private Session session;

	private boolean cachingHint = false;

	private MasterDataRepository masterDataRepository ;

	private String key = new String();

	private String searchName;


	/**
	 * Execute the query and return the object.This should also close the session.
	 * @return
	 */
	public SearchResults retrieve()throws SystemException,MasterDataRetrieverException {
		List resultList = null;
		SearchResults searchResults = null;
		 if(cachingHint){
			 MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("cachinghint has been specified");
			 if(masterDataRepository.isInCache(key)){
				 MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("returning master data from cache");
				 return masterDataRepository.getDataFromCache(key);
			 }
		 }else{
			 MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("cachinghint has not been specified");
				 try{
					 MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("before quering the database using named query");
				  resultList = query.list();
				  MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("after obtaining the data ");
				 }catch(HibernateException he){
					 he.printStackTrace();
					 throw new HibernateSystemException(he);
				 }finally{
					 session.close();
				 }
				  searchResults = new SearchResults();
				  searchResults.setValue(resultList);
				  searchResults.setResultName(searchName);
			 }


		 if(cachingHint){
			 MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Adding data to cache");
			 masterDataRepository.addToCache(key, searchResults);
		 }
		 return searchResults;
	}


	/**
	 *  This will set the object passed to it in the query object.
	 * @param name - name of the parameter to be set in the query.
	 * @param val - the value to be set in the query.
	 */
	public void setParameter(String name, Object val) {
		query.setParameter(name, val);
	}


	/**
	 * This should get a session  from session factory and create a query object out of it
	 */
	public MasterDataRetriever()throws HibernateProcessException {
		this.session = HibernateUtil.getSession();
		this.masterDataRepository = MasterDataRepository.getInstance();
	}


	/**
	 * This should prepare the query based on the namedQuery passed.
	 * @param namedQuery - the name of the query to be executed to obtain the master data.
	 * @param searchName - the name with which it would be added to the search results object
	 */
	public void prepare(String namedQuery, String searchName) {
		this.query = session.getNamedQuery(namedQuery);
		this.searchName = searchName;
	}

	/**
	 * This checks if the master data is is cache.
	 * @param key
	 * @return - true if the data is in cache based on the key passed to it.
	 */
	private boolean checkIfDataInCache(String key) {

		return masterDataRepository.isInCache(key);
	}

	/**
	 * This would add master data to the masterdatarepository because there could be certain master data that is common across users.
	 * The key for adding the data to the repository is the name of the query appended to the values passed to the parameter strings of the query.
	 * @param key - key with which it should be added to the cache.
	 * @param value - value to be added to the cache.
	 */
	private void addDataToCache(String key, Object value) {
	}

	/**
	 * This is a hint given to the masterData retriever which specified whether this data should be added to cache or not.
	 * @param bool
	 */
	public void hintForCaching(boolean bool){
		cachingHint = bool;
	}

	/**
	 * This method would clean the resources held by this object so that it could be used again for retrieving another set of data.
	 *
	 */
	public void clean(){

	}
	/**
	 * This method obtains a the values corresponding to a locale for a particular entity. Eg: If entity is Salutation
	 * Then this method retrieves values Mr, Mrs, Ms for locale english and Monsieur, Madame for french locale
	 * @param entityId Id denoting the entity(Eg: Salutation)
	 * @param localeId The locale for which values have to be obtained
	 * @param searchResultName The name under which it will be put into the search results
	 * @return Search Result object
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public SearchResults retrieveMasterData(String entityName,Short localeId,String searchResultName) throws SystemException,ApplicationException{
		SearchResults searchResults= null;
		EntityMaster entityMaster = null;
		MasterDAO masterDAO = new MasterDAO();
		entityMaster = masterDAO.getLookUpEntity(entityName,localeId);
		searchResults = new SearchResults();
		searchResults.setValue(entityMaster);
		searchResults.setResultName(searchResultName);
		return searchResults;
	}

	public SearchResults  retrieveMasterData(String entityName, Short localeId, String searchResultName,String classPath, String column )
			throws SystemException,ApplicationException{
		SearchResults searchResults= null;
		EntityMaster entityMaster = null;
		MasterDAO masterDAO = new MasterDAO();
		entityMaster = masterDAO.getLookUpEntity(entityName,localeId,classPath,column);
		searchResults = new SearchResults();
		searchResults.setValue(entityMaster);
		searchResults.setResultName(searchResultName);
		return searchResults;
	}
}
