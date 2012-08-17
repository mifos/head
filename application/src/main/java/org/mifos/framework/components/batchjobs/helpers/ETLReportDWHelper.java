package org.mifos.framework.components.batchjobs.helpers;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.mifos.application.servicefacade.ApplicationContextHolder;
import org.mifos.core.MifosRepositoryException;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class ETLReportDWHelper extends TaskHelper {

    private final String FILENAME = "DataWarehouseInitialLoad.kjb";
    private static final Logger LOGGER = Logger.getLogger(ETLReportDWHelper.class);

    @Override
    public void execute(final long timeInMillis) throws BatchJobException {
        createPropertiesFileForPentahoDWReports();
        String path = this.getClass().getResource("/MifosDataWarehouseETL/" + FILENAME).toString().replace("file:", "");
        String jarPath = this.getClass().getResource("/mifos-etl-plugin-1.0-SNAPSHOT.one-jar.jar").toString().replace("file:", "");
        try {
            String cmd = System.getProperty("java.home") + "/bin/java -jar " + jarPath + " " + path + " false";
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            if (p.exitValue()!=0) {
                throw new MifosRepositoryException("Error in run pentaho ETL batch job");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void createPropertiesFileForPentahoDWReports() {
        try {
            new ApplicationContextHolder();
            ApplicationContext ach = ApplicationContextHolder.getApplicationContext();

            DriverManagerDataSource ds = (DriverManagerDataSource) ach.getBean("dataSource");

            Properties prop = new Properties();
            prop.setProperty("SourceDB/type", "javax.sql.DataSource");
            prop.setProperty("SourceDB/driver", "com.mysql.jdbc.Driver");
            prop.setProperty("SourceDB/url", ds.getUrl());
            prop.setProperty("SourceDB/user", ds.getUsername());
            prop.setProperty("SourceDB/password", ds.getPassword());

            DriverManagerDataSource dsDW = (DriverManagerDataSource) ach.getBean("dataSourcePentahoDW");

            prop.setProperty("DestinationDB/type", "javax.sql.DataSource");
            prop.setProperty("DestinationDB/driver", "com.mysql.jdbc.Driver");
            prop.setProperty("DestinationDB/url", dsDW.getUrl());
            prop.setProperty("DestinationDB/user", dsDW.getUsername());
            prop.setProperty("DestinationDB/password", dsDW.getUsername());
        
            prop.store(new FileOutputStream(System.getProperty("user.dir")+"/Simple-JNDI/jdbc.properties"), "create");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
