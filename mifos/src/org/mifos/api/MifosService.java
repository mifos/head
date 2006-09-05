package org.mifos.api;

import java.util.HashMap;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.AppNotConfiguredException;
import org.mifos.framework.exceptions.HibernateStartUpException;
import org.mifos.framework.exceptions.LoggerConfigurationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.HibernateStartUp;


public class MifosService {
	private String propertiesFile = "mifos.properties";
	private String hibernateProperties = "hibernate.properties";
    private String log4jConfig = "log4j.xml";
    
    MifosService() {
    }
    
    public void setPropertiesFile(String f) {
    	this.propertiesFile = f;
    	
    	// TODO Add code here to set hibernateProperties and log4jConfig from the properties file
    }
    
    public void setHibernateProperties(String f) {
    	this.hibernateProperties = f;
    }
    
    public void setLog4jConfig(String f) {
    	this.log4jConfig = f;
    }
    
    void init() throws LoggerConfigurationException, HibernateStartUpException, AppNotConfiguredException {
	    MifosLogManager.configure(log4jConfig);
	    MifosLogger mLogger = MifosLogManager.getLogger("org.mifos.logger");
	    mLogger.info("Logger initialized", false, null);
	    
	    HibernateStartUp.initialize(hibernateProperties);
	    mLogger.info("Hibernate initialized", false, null);
	    
	    mLogger.info("Mifos Service Layer initialized", false, null);
    }
    
    public Loan getLoan(Integer id) throws Exception, ServiceException {
    	return Loan.getLoan(id);
    }
}
