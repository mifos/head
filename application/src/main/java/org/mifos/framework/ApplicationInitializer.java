/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.application.admin.system.ShutdownManager;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.config.AccountingRules;
import org.mifos.config.ClientRules;
import org.mifos.config.ConfigLocale;
import org.mifos.config.Localization;
import org.mifos.config.ProcessFlowRules;
import org.mifos.config.business.Configuration;
import org.mifos.config.business.MifosConfiguration;
import org.mifos.config.exceptions.ConfigurationException;
import org.mifos.config.persistence.ApplicationConfigurationPersistence;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.framework.components.audit.util.helpers.AuditConfiguration;
import org.mifos.framework.components.batchjobs.MifosScheduler;
import org.mifos.framework.components.batchjobs.exceptions.TaskSystemException;
import org.mifos.framework.exceptions.AppNotConfiguredException;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.HibernateStartUpException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.exceptions.XMLReaderException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.DatabaseMigrator;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;
import org.mifos.framework.struts.tags.XmlBuilder;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyCompositeUserType;
import org.mifos.security.authorization.AuthorizationManager;
import org.mifos.security.authorization.HierarchyManager;
import org.mifos.security.util.ActivityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

/**
 * This class should prepare all the sub-systems that are required by the app. Cleanup should also happen here when the
 * application is shutdown.
 */
public class ApplicationInitializer implements ServletContextListener, ServletRequestListener, HttpSessionListener {

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
        init(ctx);
    }

    public void init(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        try {
            // prevent ehcache "phone home"
            System.setProperty("net.sf.ehcache.skipUpdateCheck", "true");
            // prevent quartz "phone home"
            System.setProperty("org.terracotta.quartz.skipUpdateCheck", "true");

            synchronized (ApplicationInitializer.class) {
                if (servletContext != null) {
                    dbUpgrade(WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext));

                }
                setAttributesOnContext(servletContext);
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
    }

    public void dbUpgrade(ApplicationContext applicationContext) throws ConfigurationException, PersistenceException, FinancialException, TaskSystemException {
        logger.info("Logger has been initialised");

        initializeHibernate();

        logger.info(getDatabaseConnectionInfo());

        // if a database upgrade loads an instance of Money then MoneyCompositeUserType needs the default
        // currency
        MoneyCompositeUserType.setDefaultCurrency(AccountingRules
                .getMifosCurrency(new ConfigurationPersistence()));
        AccountingRules.init(); // load the additional currencies
        Money.setDefaultCurrency(AccountingRules.getMifosCurrency(new ConfigurationPersistence()));
        DatabaseMigrator migrator = new DatabaseMigrator(applicationContext);
        try {
            /*
             * This is an easy way to force an actual database query to happen via Hibernate. Simply opening a
             * Hibernate session may not actually connect to the database.
             */
            migrator.isNSDU();
        } catch (Throwable t) {
            setDatabaseError(DatabaseErrorCode.CONNECTION_FAILURE, "Unable to connect to database.", t);
        }

        if (!databaseError.isError) {
            try {
                migrator.upgrade();
            } catch (Throwable t) {
                setDatabaseError(DatabaseErrorCode.UPGRADE_FAILURE, "Failed to upgrade database.", t);
            }
        }

        if (databaseError.isError) {
            databaseError.logError();
        } else {
            initializeDB();
        }
    }

    private void initializeDB() throws ConfigurationException, PersistenceException, FinancialException {
        // this method is called so that supported locales will be
        // loaded
        // from db and stored in cache for later use
        List<SupportedLocalesEntity> supportedLocales = new ApplicationConfigurationPersistence().getSupportedLocale();
        Localization.getInstance().init(supportedLocales);
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
    }

    public void setAttributesOnContext(ServletContext servletContext) throws TaskSystemException {
        // FIXME: replace with Spring-managed beans
        final MifosScheduler mifosScheduler = new MifosScheduler();
        final ShutdownManager shutdownManager = new ShutdownManager();

        Configuration.getInstance();
        MifosConfiguration.getInstance().init();
        configureAuditLogValues(Localization.getInstance().getMainLocale());
        ConfigLocale configLocale = new ConfigLocale();
        if (servletContext != null) {
            mifosScheduler.initialize();
            servletContext.setAttribute(MifosScheduler.class.getName(), mifosScheduler);
            servletContext.setAttribute(ShutdownManager.class.getName(), shutdownManager);
            servletContext.setAttribute(ConfigLocale.class.getSimpleName(), configLocale);
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
            xml.text("Unable to apply database upgrades");
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
        addStackTraceHtml(xml);
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

    private static void addStackTraceHtml(XmlBuilder xml) {
        xml.startTag("p");
        xml.text("Stack trace:");
        xml.endTag("p");
        xml.text("\n");

        xml.startTag("pre");
        StringWriter stackTrace = new StringWriter();
        databaseError.error.printStackTrace(new PrintWriter(stackTrace));
        xml.text("\n" + stackTrace.toString());
        xml.endTag("pre");
        xml.text("\n");
    }

    /**
     * Initializes Hibernate by making it read the hibernate.cfg file and also setting the same with hibernate session
     * factory.
     */
    public static void initializeHibernate() throws AppNotConfiguredException {
        try {
            StaticHibernateUtil.initialize();
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

            AuthorizationManager.getInstance().init();

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
            mifosScheduler.shutdown();
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

        // kill ehcache threads
        // (net.sf.ehcache.store.DiskStore$SpoolAndExpiryThread)
        // hooked in as a listener in web.xml
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
            logger.info("No mysql timer cancellation required", e);
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

}
