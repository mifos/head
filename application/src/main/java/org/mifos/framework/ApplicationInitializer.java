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

package org.mifos.framework;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;

import javax.naming.InitialContext;
import javax.naming.NameAlreadyBoundException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.admin.servicefacade.CustomizedTextServiceFacade;
import org.mifos.application.admin.servicefacade.PersonnelServiceFacade;
import org.mifos.application.admin.system.ShutdownManager;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.servicefacade.CustomJDBCService;
import org.mifos.config.AccountingRules;
import org.mifos.config.ClientRules;
import org.mifos.config.LocaleSetting;
import org.mifos.config.Localization;
import org.mifos.config.ProcessFlowRules;
import org.mifos.config.UserLocale;
import org.mifos.config.business.Configuration;
import org.mifos.config.business.MifosConfigurationManager;
import org.mifos.config.exceptions.ConfigurationException;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.framework.components.audit.util.helpers.AuditConfiguration;
import org.mifos.framework.components.batchjobs.MifosScheduler;
import org.mifos.framework.components.batchjobs.exceptions.TaskSystemException;
import org.mifos.framework.components.batchjobs.helpers.ETLReportDWHelper;
import org.mifos.framework.exceptions.AppNotConfiguredException;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.HibernateStartUpException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.exceptions.XMLReaderException;
import org.mifos.framework.hibernate.helper.AuditInterceptorFactory;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.image.service.ImageStorageManager;
import org.mifos.framework.persistence.DatabaseMigrator;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;
import org.mifos.framework.struts.tags.XmlBuilder;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyCompositeUserType;
import org.mifos.security.authorization.HierarchyManager;
import org.mifos.security.util.ActivityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * This class should prepare all the sub-systems that are required by the app. Cleanup should also happen here when the
 * application is shutdown.
 */
public class ApplicationInitializer implements ServletContextListener, ServletRequestListener, HttpSessionListener {

    private static final String IMAGESTORE_CONFIG_KEY = "GeneralConfig.ImageStorageType";
    private static final String DB_CONFIG = "database";
    
    private static Logger logger = LoggerFactory.getLogger(ApplicationInitializer.class);

    private static class DatabaseError {
        boolean isError = false;
        DatabaseErrorCode errorCode = DatabaseErrorCode.NO_DATABASE_ERROR;
        String errmsg = "";
        Throwable error = null;

        void logError() {
            logger.error(errmsg, error);
        }
    }

    public static void setDatabaseError(DatabaseErrorCode errcode, String errmsg, Throwable error) {
        databaseError.isError = true;
        databaseError.errorCode = errcode;
        databaseError.error = error;
        databaseError.errmsg = errmsg;
    }

    public static void clearDatabaseError() {
        databaseError.isError = false;
        databaseError.errorCode = DatabaseErrorCode.NO_DATABASE_ERROR;
        databaseError.error = null;
        databaseError.errmsg = null;
    }

    private static String getDatabaseConnectionInfo() {
        String info = "Not implemented - check local.properties file for local overrides";
        // FIXME: not sure if this is necessary may be spring/hibernate logging would be a better way to get this info
        // For UI purpose, not sure, may be showing all the configuration would be a better way
        return info;
    }

    private static DatabaseError databaseError = new DatabaseError();

    @Override
    public void contextInitialized(ServletContextEvent ctx) {
        printSystemProperties();
        printSystemEnvironment();
        printMemoryPool();
        Long startTime = System.currentTimeMillis();
        init(ctx);
        logger.info("Took " + (System.currentTimeMillis() - startTime) + " msec to start");
    }

    private void printSystemProperties() {
        Properties properties = System.getProperties();
        String props = "\n";
        for(Object key :  properties.keySet()) {
            props += key + " : " +properties.get(key) +"\n";
        }
        logger.info("Dump of all Java System Properties: " + props);
    }

    private void printMemoryPool() {
        logger.info("Memory MXBean");
        logger.info("Heap Memory Usage: " + ManagementFactory.getMemoryMXBean().getHeapMemoryUsage());
        logger.info("Non-Heap Memory Usage: " + ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage());

        logger.info("-----Memory Pool MXBeans------");

        Iterator<MemoryPoolMXBean> iter = ManagementFactory.getMemoryPoolMXBeans().iterator();

        while (iter.hasNext()) {
            MemoryPoolMXBean item = iter.next();

            logger.info("Name: " + item.getName());
            logger.info("Type: " + item.getType());
            logger.info("Usage: " + item.getUsage());
            logger.info("Peak Usage: " + item.getPeakUsage());
            logger.info("Collection Usage: " + item.getCollectionUsage());
            logger.info("+++++++++++++++++++");
        }
    }

    private void printSystemEnvironment() {
        Map<String, String> envMap = System.getenv();
        String env = "\n";
        for(String key : envMap.keySet()) {
            env += key + " : " +envMap.get(key) +"\n";
        }
        logger.info(env);
    }

    public void init(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        try {
            // prevent ehcache "phone home"
            System.setProperty("net.sf.ehcache.skipUpdateCheck", "true");
            // prevent quartz "phone home"
            System.setProperty("org.terracotta.quartz.skipUpdateCheck", "true");

            synchronized (ApplicationInitializer.class) {
                ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
                if (servletContext != null) {
                    dbUpgrade(applicationContext);

                }
                initJNDIforPentaho(applicationContext);
                setAttributesOnContext(servletContext);
                copyResources(servletContext);
            }
        } catch (Exception e) {
            String errMsgStart = "unable to start Mifos web application";
            if (null == logger) {
                System.err.println(errMsgStart + " and logger is not available!");
                e.printStackTrace();
            } else {
                logger.error(errMsgStart, e);
            }
            throw new Error(e);
        }
        logger.info("Mifos is ready.");
    }

    

    public void dbUpgrade(ApplicationContext applicationContext) throws ConfigurationException, PersistenceException, FinancialException, TaskSystemException {
        logger.info("Logger has been initialised");

        initializeHibernate(applicationContext);

        logger.info(getDatabaseConnectionInfo());

        // if a database upgrade loads an instance of Money then MoneyCompositeUserType needs the default
        // currency
        MoneyCompositeUserType.setDefaultCurrency(AccountingRules.getMifosCurrency(new ConfigurationPersistence()));
        AccountingRules.init(); // load the additional currencies
        Money.setDefaultCurrency(AccountingRules.getMifosCurrency(new ConfigurationPersistence()));
        
        
        final MifosConfigurationManager configuration = MifosConfigurationManager.getInstance();
        final String imageStorageConfig = configuration.getString(IMAGESTORE_CONFIG_KEY);
        if (imageStorageConfig == null || !imageStorageConfig.equals(DB_CONFIG)) {
            ImageStorageManager.initStorage();
        }
        DatabaseMigrator migrator = new DatabaseMigrator();
        initializeDBConnectionForHibernate(migrator);

        if (!databaseError.isError) {
            try {
                migrator.upgrade(applicationContext);
            } catch (Throwable t) {
                setDatabaseError(DatabaseErrorCode.UPGRADE_FAILURE, "Failed to upgrade database.", t);
            }
        }

        if (databaseError.isError) {
            databaseError.logError();
        } else {
            initializeDB(applicationContext);


            /*
             * John W - Added in G Release and back patched to F Release.
             * Related to jira issue MIFOS-4948
             *
             * Can find all code and the related query by searching for mifos4948
             *
            */
            CustomJDBCService customJdbcService = applicationContext.getBean(CustomJDBCService.class);
            boolean keyExists = customJdbcService.mifos4948IssueKeyExists();
            if (!keyExists) {
            	try {
                    StaticHibernateUtil.startTransaction();

					applyMifos4948Fix();
	                customJdbcService.insertMifos4948Issuekey();

	                StaticHibernateUtil.commitTransaction();

				} catch (AccountException e) {
	                StaticHibernateUtil.rollbackTransaction();
					e.printStackTrace();
				} finally {
					StaticHibernateUtil.closeSession();
				}
            }
            
            boolean key5722Exists = customJdbcService.mifos5722IssueKeyExists();
            if(!key5722Exists) {
                try {
                    applyMifos5722Fix();
                    customJdbcService.insertMifos5722Issuekey();
                } catch (Exception e) {
                    logger.error("Could not apply Mifos-5692 and mifos-5722 fix");
                    e.printStackTrace();
                } finally {
                    StaticHibernateUtil.closeSession();
                }
            }
            
            boolean key5763Exists = customJdbcService.mifos5763IssueKeyExists();
            if(!key5763Exists) {
                try {
                    applyMifos5763Fix();
                    customJdbcService.insertMifos5763Issuekey();                  
                } catch (Exception e) {
                    logger.info("Failed to apply Mifos-5763 fix");
                    e.printStackTrace();
                } finally {
                    StaticHibernateUtil.closeSession();
                }
            }
            
            boolean key5632Exists = customJdbcService.mifos5632IssueKeyExists();
            if(!key5632Exists) {
                try {
                    applyMifos5632();
                    customJdbcService.insertMifos5632IssueKey();                  
                } catch (Exception e) {
                    logger.info("Failed to apply Mifos-5632 fix");
                    e.printStackTrace();
                } finally {
                    StaticHibernateUtil.closeSession();
                }
            }
        }

    }


    @SuppressWarnings("unchecked")
	private void applyMifos4948Fix() throws AccountException {

    	Session session = StaticHibernateUtil.getSessionTL();
    	List<LoanBO> fixLoans;
    	Query query = session.getNamedQuery("fetchMissingInstalmentsForWriteOffsAndReschedules");

    	fixLoans = query.list();

        if (fixLoans != null && fixLoans.size() > 0) {
	        	for (LoanBO fixLoan : fixLoans) {

	        		Set<AccountActionDateEntity> fixLoanSchedules = fixLoan.getAccountActionDates();
	            	Money totalMissedPayment = new Money(fixLoan.getCurrency());
	                if (fixLoanSchedules != null && fixLoanSchedules.size() > 0) {
	                	for (AccountActionDateEntity fixLoanSchedule : fixLoanSchedules) {
	                		LoanScheduleEntity loanInstallment = (LoanScheduleEntity) fixLoanSchedule;
	                		totalMissedPayment = totalMissedPayment.add(loanInstallment.getPrincipalDue());
	                	}
	                }
	                logger.info("MIFOS-4948 - Loan: " + fixLoan.getGlobalAccountNum() + " - Adding payment: " + totalMissedPayment
	        		 + " to fix instalments that should have been written-off or rescheduled.");

	                fixLoan.applyMifos4948FixPayment(totalMissedPayment);
	        	}

        		logger.info(fixLoans.size() + " Account Payments created to fix data related to MIFOS-4948");
        }

    }
    
    @SuppressWarnings("unused")
    private void applyMifos5632() throws PersistenceException {
        Session session = StaticHibernateUtil.getSessionTL();
        
        @SuppressWarnings("unused")
        Query lookup_value_query,lookup_value_locale_query,activity_query,roles_activity_query, min_activity_id, question_activity;
        Query count_query = session.createSQLQuery("select max(id) from question_group where activity_id is null;");
        Integer count = (Integer)count_query.uniqueResult();
        Long iterator = count.longValue() + 1;
        min_activity_id = session.createSQLQuery("select min(activity_id) from activity;");
        Integer activity_id = ((Short)min_activity_id.uniqueResult()).intValue();
        if (activity_id == 1) {
            activity_id = activity_id - 2;
        }
        StaticHibernateUtil.clearSession();
        logger.info("Started Mifos-5632");
        while (iterator > 0) {
            try {
                StaticHibernateUtil.startTransaction();
                
                iterator--;
                activity_id--;
                
                lookup_value_query = session.createSQLQuery("insert into lookup_value(lookup_id,entity_id,lookup_name) " +
                		"values((select max(lv.lookup_id)+1 from lookup_value lv), 87," +
                		"concat(concat('QuestionGroup.',(select title from question_group where id="+ iterator +")),'"+ iterator +"'))");
                lookup_value_query.executeUpdate();
                
                lookup_value_locale_query = session.createSQLQuery("insert into lookup_value_locale(locale_id,lookup_id,lookup_value) values" +
                		"(1,(select lookup_id from lookup_value where entity_id =87 and " +
                		"lookup_name=concat(concat('QuestionGroup.',(select title from question_group where id="+ iterator +")),'"+ iterator +"')),concat('Can edit ', (select title from question_group where id="+ iterator +")))");
                lookup_value_locale_query.executeUpdate();
                
                activity_query = session.createSQLQuery("insert into activity(activity_id,parent_id, activity_name_lookup_id, DESCRIPTION_lookup_id)" +
                		"values("+ activity_id +",294,(select lookup_id from lookup_value where entity_id =87 and " +
                		"lookup_name=concat(concat('QuestionGroup.',(select title from question_group where id="+ iterator +")),'"+ iterator +"'))," +
                		"(select lookup_id from lookup_value where entity_id =87 and " +
                		"lookup_name=concat(concat('QuestionGroup.',(select title from question_group where id="+ iterator +")),'"+ iterator +"')))");
                activity_query.executeUpdate();
                
                question_activity = session.createSQLQuery("update question_group set activity_id ="+ activity_id +" where id = "+ iterator);
                question_activity.executeUpdate();
                
                roles_activity_query = session.createSQLQuery("insert into roles_activity(activity_id, role_id) values("+ activity_id +", 1)");
                roles_activity_query.executeUpdate();
                
                StaticHibernateUtil.commitTransaction();
            } catch(Exception e) {
                logger.info("Failed add permission for existing Question groups");
                StaticHibernateUtil.rollbackTransaction();
            } finally {
                StaticHibernateUtil.clearSession();
            }
        }
        logger.info("Success, permission has been added.");
    }
    
    @SuppressWarnings("unchecked")
    private void applyMifos5722Fix() throws PersistenceException {
        
        Session session = StaticHibernateUtil.getSessionTL();
        int counter = 0;
        LoanBO fixLoan;
        
        logger.info("Started Mifos-5692 and Mifos-5722 fix.");
        
        Query query = session.getNamedQuery("accounts.countAllParentLoans");
        Long loanCount = (Long)query.uniqueResult();
        StaticHibernateUtil.clearSession();
        
        logger.info("Query found " + loanCount.toString() + " accounts.");

        do {
            session = StaticHibernateUtil.getSessionTL();
            query = session.getNamedQuery("accounts.findAllParentLoans");
            query.setFirstResult(counter);
            query.setMaxResults(1);
            fixLoan = (LoanBO)query.uniqueResult();
            
            if(fixLoan != null) {
                counter++;
                if(fixLoan.needsMifos5722Repair()) {
                    try {
                        StaticHibernateUtil.startTransaction();
                        fixLoan.applyMifos5722Fix();
                        StaticHibernateUtil.commitTransaction();
                    } catch(AccountException e) {
                        logger.info("Failed to fix loan: " + fixLoan.getGlobalAccountNum());
                        StaticHibernateUtil.rollbackTransaction();
                    } finally {
                        StaticHibernateUtil.clearSession();
                    }
                }
            }  
            if(counter%100==0 || fixLoan==null) {
                logger.info("Fixed " + counter + " accounts."); 
            }
        } while(fixLoan != null);
        logger.info("Finished Mifos-5692 and Mifos-5722 fix.");
    }

    private void applyMifos5763Fix() {
        Session session = StaticHibernateUtil.getSessionTL();
        int counter = 0;
        int index = 0;
        LoanBO fixLoan;
        
        Query query = session.getNamedQuery("accounts.countGLIMAccountsWithIncorrectNumberOfInstallmentsOnIndividualLoans");
        Long loanCount = (Long)query.uniqueResult();
        logger.info("Found " + loanCount.toString() + " GLIM accounts wtih incorrect number of installments on member accounts");
        
        StaticHibernateUtil.clearSession();
        do {
            session = StaticHibernateUtil.getSessionTL();
            query = session.getNamedQuery("accounts.findGLIMAccountsWithIncorrectNumberOfInstallmentsOnIndividualLoans");
            query.setFirstResult(index);
            query.setMaxResults(1);
            fixLoan = (LoanBO)query.uniqueResult();
            
            if(fixLoan != null) {
                counter++;
                try {
                    StaticHibernateUtil.startTransaction();
                    fixLoan.applyMifos5763Fix();
                    StaticHibernateUtil.commitTransaction();
                    logger.info("Trying to apply Mifos-5722 fix to current loan");
                    try {
                        StaticHibernateUtil.startTransaction();
                        fixLoan.applyMifos5722Fix();
                        StaticHibernateUtil.commitTransaction();
                    } catch (Exception e) {
                        logger.info("Failed to apply Mifos-5722 fix to loan " + fixLoan.getGlobalAccountNum());
                        StaticHibernateUtil.rollbackTransaction();
                    }
                } catch(AccountException e) {
                    logger.info("Failed to fix loan: " + fixLoan.getGlobalAccountNum());
                    index++;
                    StaticHibernateUtil.rollbackTransaction();
                } finally {
                    StaticHibernateUtil.clearSession();
                }
            }  
            if(counter%100==0 || fixLoan==null) {
                logger.info("Fixed " + counter + " accounts."); 
            }
        } while(fixLoan != null);
        logger.info("Finished Mifos-5763 fix.");
    }
    
    private void initializeDBConnectionForHibernate(DatabaseMigrator migrator) {
        try {
            /*
             * This is an easy way to force an actual database query to happen via Hibernate. Simply opening a
             * Hibernate session may not actually connect to the database.
             */
            migrator.isNSDU();
        } catch (Throwable t) {
            setDatabaseError(DatabaseErrorCode.CONNECTION_FAILURE, "Unable to connect to database.", t);
        }
    }

    private void initializeDB(ApplicationContext applicationContext) throws ConfigurationException, PersistenceException, FinancialException {
        // Check ClientRules configuration in db and config file(s)
        // for errors. Also caches ClientRules values.
        ClientRules.init();
        // Check ProcessFlowRules configuration in db and config
        // file(s) for errors.
        ProcessFlowRules.init();
        initializeSecurity();

        FinancialInitializer.initialize();
        EntityMasterData.getInstance().init();
        initializeEntityMaster();

        applicationContext.getBean(CustomizedTextServiceFacade.class).convertMigratedLabelKeysToLocalizedText(Localization.getInstance().getConfiguredLocale());

    }

    public void setAttributesOnContext(ServletContext servletContext) throws TaskSystemException {
        // FIXME: replace with Spring-managed beans
        final MifosScheduler mifosScheduler = new MifosScheduler();
        final ShutdownManager shutdownManager = new ShutdownManager();

        Configuration.getInstance();
        configureAuditLogValues(Localization.getInstance().getConfiguredLocale());
        LocaleSetting configLocale = new LocaleSetting();
        
        @SuppressWarnings("deprecation")
        final UserLocale userLocale = new UserLocale(ApplicationContextProvider.getBean(PersonnelServiceFacade.class));
        
        if (servletContext != null) {
            mifosScheduler.initialize();
            servletContext.setAttribute(MifosScheduler.class.getName(), mifosScheduler);
            servletContext.setAttribute(ShutdownManager.class.getName(), shutdownManager);
            servletContext.setAttribute(LocaleSetting.class.getSimpleName(), configLocale);
            servletContext.setAttribute(UserLocale.class.getSimpleName(), userLocale);
        }
    }

    public static void printDatabaseError(XmlBuilder xml) {
        synchronized (ApplicationInitializer.class) {
            if (databaseError.isError) {
                addDatabaseErrorMessage(xml);
            } else {
                addNoFurtherDetailsMessage(xml);
            }
        }
    }

    private static void addNoFurtherDetailsMessage(XmlBuilder xml) {
        xml.startTag("p");
        xml.text("I don't have any further details, unfortunately.");
        xml.endTag("p");
        xml.text("\n");
    }

    private static void addDatabaseErrorMessage(XmlBuilder xml) {
        xml.startTag("p", "style", "font-weight: bolder; color: red; font-size: x-large;");

        xml.text(databaseError.errmsg);
        xml.text("\n");

        if (databaseError.errorCode.equals(DatabaseErrorCode.UPGRADE_FAILURE)) {
            xml.text("Please apply upgrade DB and restart the server");
        }
        xml.endTag("p");
        if (databaseError.errorCode.equals(DatabaseErrorCode.CONNECTION_FAILURE)) {
            addConnectionFailureMessage(xml);
        }
        xml.text("\n");
        xml.startTag("p");
        xml.text("More details:");
        xml.endTag("p");
        xml.text("\n");

        if (null != databaseError.error.getCause()) {
            xml.startTag("p", "style", "font-weight: bolder; color: blue;");
            xml.text(databaseError.error.getCause().toString());
            xml.endTag("p");
            xml.text("\n");
        }

        xml.startTag("p", "style", "font-weight: bolder; color: blue;");
        xml.text(getDatabaseConnectionInfo());
        xml.endTag("p");
        xml.text("\n");
        addExceptionMessage(xml);
    }

    private static void addConnectionFailureMessage(XmlBuilder xml) {
        xml.startTag("p");
        xml.text("Possible causes:");

        xml.startTag("ul");
        xml.startTag("li");
        xml.text("MySQL is not running");
        xml.endTag("li");

        xml.startTag("li");
        xml.text("MySQL is listening on a different port than Mifos is expecting");
        xml.endTag("li");

        xml.startTag("li");
        xml.text("incorrect username or password");
        xml.endTag("li");
        xml.endTag("ul");
        xml.endTag("p");
        xml.text("\n");

        xml.startTag("p");
        xml.startTag("a", "href", "http://www.mifos.org/knowledge/support/deploying-mifos/configuration/guide");
        xml.text("More about configuring your database connection.");
        xml.endTag("a");
        xml.endTag("p");
        xml.text("\n");
    }

    private static void addExceptionMessage(XmlBuilder xml) {
        xml.startTag("p");
        xml.text("Error Message:");
        xml.endTag("p");
        xml.text("\n");

        xml.startTag("pre");
        xml.text("\n" + databaseError.error.getMessage());
        xml.endTag("pre");
        xml.text("\n");
    }

    /**
     * Initializes Hibernate by making it read the hibernate.cfg file and also setting the same with hibernate session
     * factory.
     */
    public static void initializeHibernate(ApplicationContext applicationContext) throws AppNotConfiguredException {
        try {
            StaticHibernateUtil.initialize(new AuditInterceptorFactory(), (SessionFactory) applicationContext.getBean("sessionFactory"));
        } catch (HibernateStartUpException e) {
            throw new AppNotConfiguredException(e);
        }
    }

    /**
     * This function initialize and bring up the authorization and authentication services
     *
     * @throws AppNotConfiguredException
     *             - IF there is any failures during init
     */
    private void initializeSecurity() throws AppNotConfiguredException {
        try {
            ActivityMapper.getInstance().init();
            HierarchyManager.getInstance().init();
        } catch (XMLReaderException e) {

            throw new AppNotConfiguredException(e);
        } catch (ApplicationException ae) {
            throw new AppNotConfiguredException(ae);
        } catch (SystemException se) {
            throw new AppNotConfiguredException(se);
        }

    }

    private void initializeEntityMaster() throws HibernateProcessException {
        EntityMasterData.getInstance().init();
    }

    private void configureAuditLogValues(Locale locale) throws SystemException {
        AuditConfiguration.init(locale);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ServletContext ctx = servletContextEvent.getServletContext();
        logger.info("shutting down scheduler");
        final MifosScheduler mifosScheduler = (MifosScheduler) ctx.getAttribute(MifosScheduler.class.getName());
        ctx.removeAttribute(MifosScheduler.class.getName());
        try {
            if (mifosScheduler != null) {
                mifosScheduler.shutdown();
            }
        } catch (Exception e) {
            logger.error("error while shutting down scheduler", e);
        }

       // WebApplicationContext applicationContext = null;
      //  if (ctx != null) {
       //     applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(ctx);
       // }

        StaticHibernateUtil.shutdown();
        unregisterMySQLDriver();
        cancelMySQLStatement();
        logger.info("destroyed context");
    }

    private void unregisterMySQLDriver() {
        // unregister any jdbc drivers (mysql driver)
        try {
            for (Enumeration<Driver> e = DriverManager.getDrivers(); e.hasMoreElements();) {
                Driver driver = e.nextElement();
                if (driver.getClass().getClassLoader() == getClass().getClassLoader()) {
                    DriverManager.deregisterDriver(driver);
                }
            }
        } catch (Exception e) {
            logger.error("can't unregister jdbc drivers", e);
        }
    }

    private void cancelMySQLStatement() {
        // mysql statement cancellation timer (mysql bug 36565)
        ClassLoader myClassLoader = this.getClass().getClassLoader();
        Class clazz;
        try {
            clazz = Class.forName("com.mysql.jdbc.ConnectionImpl", false, myClassLoader);

            if (!(clazz.getClassLoader() == myClassLoader)) {
                logger.info("MySQL ConnectionImpl was loaded with another ClassLoader: (" + clazz.getClassLoader()
                        + "): cancelling anyway");
            } else {
                logger.info("MySQL ConnectionImpl was loaded with the WebappClassLoader: cancelling the Timer");
            }

            Field f = clazz.getDeclaredField("cancelTimer");
            f.setAccessible(true);
            Timer timer = (Timer) f.get(null);
            timer.cancel();
            logger.info("completed timer cancellation");

        } catch (ClassNotFoundException e) {
            logger.warn("failed mysql timer cancellation", e);
        } catch (SecurityException e) {
            logger.warn("failed mysql timer cancellation", e);
        } catch (NoSuchFieldException e) {
            logger.warn("failed mysql timer cancellation", e);
        } catch (IllegalArgumentException e) {
            logger.warn("failed mysql timer cancellation", e);
        } catch (IllegalAccessException e) {
            logger.warn("failed mysql timer cancellation", e);
        } catch (NullPointerException e) {
            logger.info("No mysql timer cancellation required"); // Expected exception, do not log NPE causee
        }
    }

    private void initJNDIforPentaho(ApplicationContext applicationContext) {
        try {
            InitialContext ic = new InitialContext();

            //check if ds is already bound
            boolean dataSourceBound = true;
            boolean dataSourceBoundDw = true;
            try {
                DataSource datasource = (DataSource)ic.lookup("jdbc/SourceDB");
                
                if (datasource != null) {
                    dataSourceBound = true;
                }
            } catch (Exception ex) {
                dataSourceBound = false;
            }
            try {
                DataSource datasourcedw = (DataSource)ic.lookup("jdbc/DestinationDB");
                if (datasourcedw != null) {
                    dataSourceBoundDw = true;
                }
            } catch (Exception ex) {
                dataSourceBoundDw = false;
            }
            
            if (!dataSourceBound) {
                Object dataSource = applicationContext.getBean("dataSource");
                
                try {
                    ic.createSubcontext("jdbc");
                } catch (NameAlreadyBoundException ex) {
                    logger.info("Subcontext jdbc was already bound");
                }

                ic.bind("jdbc/SourceDB", dataSource);
                logger.info("Bound datasource to jdbc/SourceDB");
            } else {
                logger.info("jdbc/SourceDB is already bound");
            }
            
            if (!dataSourceBoundDw) {
                
                Object dataSourcedw = applicationContext.getBean("dataSourcePentahoDW");

                try {
                    ic.createSubcontext("jdbc");
                } catch (NameAlreadyBoundException ex) {
                    logger.info("Subcontext jdbc was already bound");
                }

                ic.bind("jdbc/DestinationDB", dataSourcedw);
                logger.info("Bound datasource to jdbc/DestinationDB");
            } else {
                logger.info("jdbc/DestinationDB is already bound");
            }
        } catch (Exception ex) {
            logger.error("Unable to bind dataSource to JNDI");
        }
    }

    @Override
    public void requestDestroyed(@SuppressWarnings("unused") ServletRequestEvent event) {
        StaticHibernateUtil.closeSession();
    }

    @Override
    public void requestInitialized(@SuppressWarnings("unused") ServletRequestEvent event) {

    }

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        ServletContext ctx = httpSessionEvent.getSession().getServletContext();
        final ShutdownManager shutdownManager = (ShutdownManager) ctx.getAttribute(ShutdownManager.class.getName());
        shutdownManager.sessionCreated(httpSessionEvent);
    }


    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        ServletContext ctx = httpSessionEvent.getSession().getServletContext();
        final ShutdownManager shutdownManager = (ShutdownManager) ctx.getAttribute(ShutdownManager.class.getName());
        shutdownManager.sessionDestroyed(httpSessionEvent);
    }
    
    private void copyResources(ServletContext sc) throws IOException {
        URL protocol = ETLReportDWHelper.class.getClassLoader().getResource("sql/release-upgrades.txt");
        try {
        if(protocol.getProtocol().equals("jar")){
        String destinationDirectoryForJobs = System.getProperty("user.home")+"/.mifos/ETL/MifosDataWarehouseETL";
        String destinationDirectoryForJar = System.getProperty("user.home")+"/.mifos/ETL/mifos-etl-plugin-1.0-SNAPSHOT.one-jar.jar";
        String pathFromJar ="/WEB-INF/mifos-etl-plugin-1.0-SNAPSHOT.one-jar.jar";
        String pathFromJobs = "/WEB-INF/MifosDataWarehouseETL/";
        if(File.separatorChar == '\\'){
          destinationDirectoryForJobs = destinationDirectoryForJobs.replaceAll("/", "\\\\");
          destinationDirectoryForJar = destinationDirectoryForJar.replaceAll("/", "\\\\");
        }
        File directory = new File(destinationDirectoryForJobs);
        directory.mkdirs();
        FileUtils.cleanDirectory(directory);
            File jarDest = new File(destinationDirectoryForJar);
            URL fullPath = sc.getResource(pathFromJar);   
            File f = new File(sc.getResource(pathFromJobs).toString().replace("file:", ""));
            for (File fileEntry : f.listFiles()) {
                FileUtils.copyFileToDirectory(fileEntry, directory);
                logger.info("Copy file: "+fileEntry.getName()+" to: "+directory);
            }
            FileUtils.copyURLToFile(fullPath, jarDest);
            logger.info("Copy file: "+fullPath+" to: "+directory);
        } 
        } catch (NullPointerException e) {
                String destinationDirectoryForJobs = System.getProperty("user.home")+"/.mifos/ETL/MifosDataWarehouseETL";
                String destinationDirectoryForJar = System.getProperty("user.home")+"/.mifos/ETL/";
                String pathFromJar =sc.getRealPath("/")+"/WEB-INF/mifos-etl-plugin-1.0-SNAPSHOT.one-jar.jar";
                String pathFromJobs = sc.getRealPath("/")+"/WEB-INF/MifosDataWarehouseETL/";
                if(File.separatorChar == '\\'){
                  destinationDirectoryForJobs = destinationDirectoryForJobs.replaceAll("/", "\\\\");
                  destinationDirectoryForJar = destinationDirectoryForJar.replaceAll("/", "\\\\");
                }
                File directory = new File(destinationDirectoryForJobs);
                directory.mkdirs();
                FileUtils.cleanDirectory(directory);
                logger.info(System.getProperty("user.dir"));
                    File jarDest = new File(destinationDirectoryForJar);
                    URL fullPath = sc.getResource(pathFromJar);  
                    File f = new File(pathFromJobs);
                    for (File fileEntry : f.listFiles()) {
                        FileUtils.copyFileToDirectory(fileEntry, directory);
                        logger.info("Copy file: "+fileEntry.getName()+" to: "+directory);
                    }
                    FileUtils.copyFileToDirectory(new File(pathFromJar), jarDest);
                    logger.info("Copy file: "+fullPath+" to: "+directory);
        }
    }   
}
