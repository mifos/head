package org.mifos.framework.struts.plugin.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.framework.components.fieldConfiguration.business.EntityMaster;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public final class EntityMasterData {

	public static Map<Object,Object> entityMap=new HashMap<Object,Object>();
	
	private static EntityMasterData entityMasterData=new EntityMasterData();
	
	private EntityMasterData(){}
	
	public static EntityMasterData getInstance(){
		return entityMasterData;
	}

	/**This method creates a map of entity master table as sets it into the servletcontext so that
	 * it is available till the application is up.
	 * @param servlet
	 * @param config
	 * @throws HibernateProcessException
	 */
	public void init()throws HibernateProcessException{
		Session session=null;
		try{
			session=HibernateUtil.getSession();
			Transaction trxn = session.beginTransaction();
			Query query = session.getNamedQuery("getEntityMaster");

		  	List<EntityMaster> entityMasterData = query.list();

		  	for(EntityMaster entityMaster:entityMasterData) {
		  		entityMap.put(entityMaster.getEntityType(),entityMaster.getId());
		  	}
		}catch(HibernateException e){
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).error("table entity_master could not be fetched",false,null,e);
		}finally{
			HibernateUtil.closeSession(session);
		}
	}
	
	public static Map<Object,Object> getEntityMasterMap(){
		return entityMap;
	}

}
