package org.mifos.framework.components.batchjobs.helpers;

import java.io.BufferedReader;

import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;


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


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void createPropertiesFileForPentahoDWReports() {
        try {

            File file = new File(System.getProperty("user.dir")+"/Simple-JNDI/jdbc.properties");
            new ApplicationContextHolder();
            ApplicationContext ach = ApplicationContextHolder.getApplicationContext();
            DriverManagerDataSource ds = (DriverManagerDataSource) ach.getBean("dataSource");
            DriverManagerDataSource dsDW = (DriverManagerDataSource) ach.getBean("dataSourcePentahoDW");
            PrintWriter fw = new PrintWriter(file);
            fw.println("SourceDB/type=javax.sql.DataSource");
            fw.println("SourceDB/driver=com.mysql.jdbc.Driver");
            fw.println("SourceDB/url="+ds.getUrl());
            fw.println("SourceDB/user="+ds.getUsername());
            fw.println("SourceDB/password="+ds.getPassword());
            fw.println("DestinationDB/type=javax.sql.DataSource");
            fw.println("DestinationDB/driver=com.mysql.jdbc.Driver");
            fw.println("DestinationDB/url="+dsDW.getUrl());
            fw.println("DestinationDB/user="+dsDW.getUsername());
            fw.println("DestinationDB/password="+dsDW.getUsername());
            fw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
