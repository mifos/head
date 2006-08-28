package org.mifos.framework.persistence;

import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public abstract class Persistence {

	private DataSource dataSource = null;
	
	protected Connection getConnection(){
		return null;
	}
	
	protected Session openSession()throws HibernateProcessException{
		return HibernateUtil.getSession();
	}
	
	protected void closeSession()throws HibernateProcessException{
		HibernateUtil.closeSession();
	}	
	
	public Object createOrUpdate(Object object)throws PersistenceException {
		Session session=HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		try {
		session.saveOrUpdate(object);
		} catch(HibernateException he) {
			throw new PersistenceException(he);
		}
		return object;
	}
	
	public void delete(Object object)throws HibernateException {
		Session session=HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		session.delete(object);
	}
	
	/**
	 * This method takes the name of a named query to be executed as well as a list of parameters that the query uses.
	 * It assumes the session is open.
	 * @param queryName
	 * @param session
	 * @return
	 * @throws HibernateException
	 */
	public static List executeNamedQuery(String queryName,Map queryParameters)throws HibernateException{
		Query query = null;
		Session session = HibernateUtil.getSessionTL();
		List returnList = null;
		if(null != session){
			query = session.getNamedQuery(queryName);
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("The query object for the query with the name  " + queryName + " has been obtained");
		}
		
		setParametersInQuery(query,queryName, queryParameters);
		if(null != query){
			returnList = query.list();
		}
		return returnList;
	}
	
	
	/**
	 * This methods sets the parameters to the query passed.
	 * @param query
	 * @param queryName
	 */
	public  static void setParametersInQuery(Query query,String queryName,Map queryParameters) {
		
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Check if query object and queryParameters are not null for query with name  " + queryName );
		if(null != query && null != queryParameters){
			
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Obtaining keys for the parameter map query with name  " + queryName );
			Set queryParamKeys = queryParameters.keySet();
			Iterator queryParamIterator = queryParamKeys.iterator();  
			
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Iterating over the keys for query with name  " + queryName );
			while(queryParamIterator.hasNext()){
				String key = queryParamIterator.next().toString();
				MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("The parameter key  for query with name  " + queryName  + " is " + key);
				query.setParameter(key, queryParameters.get(key));
			}	
		}
		
	}
	
}
