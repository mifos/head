package org.mifos.api;

import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.AppNotConfiguredException;
import org.mifos.framework.exceptions.HibernateStartUpException;
import org.mifos.framework.exceptions.LoggerConfigurationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.HibernateStartUp;
import org.mifos.framework.util.helpers.FilePaths;


public class MifosService {
	//private String propertiesFile = "mifos.properties";

	// This filename is also in Initialization.xml
	private String hibernateProperties = "hibernate.properties";

    MifosService() {
    }
    
    public void setPropertiesFile(String f) {
    	//this.propertiesFile = f;
    	
    	// TODO Add code here to set hibernateProperties 
    	// from the properties file
    }
    
    public void setHibernateProperties(String f) {
    	this.hibernateProperties = f;
    }
    
    void init() throws LoggerConfigurationException, HibernateStartUpException, 
    AppNotConfiguredException {
	    MifosLogManager.configure(FilePaths.LOG_CONFIGURATION_FILE);
	    MifosLogger logger = MifosLogManager.getLogger("org.mifos.logger");
	    logger.info("Logger initialized", false, null);
	    
	    HibernateStartUp.initialize(hibernateProperties);
	    logger.info("Hibernate initialized", false, null);
	    
	    logger.info("Mifos Service Layer initialized", false, null);
    }
    
    public Loan getLoan(Integer id) throws Exception, ServiceException {
    	return Loan.getLoan(id);
    }
}
