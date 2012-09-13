package org.mifos.framework.components.batchjobs.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.mifos.application.servicefacade.ApplicationContextHolder;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class ETLReportDWHelper extends TaskHelper {

    private final String FILENAME = "DataWarehouseInitialLoad.kjb";
    private static final Logger LOGGER = Logger.getLogger(ETLReportDWHelper.class);

    @Override
    public void execute(final long timeInMillis) throws BatchJobException {
        new ApplicationContextHolder();
        ApplicationContext ach = ApplicationContextHolder.getApplicationContext();
        DriverManagerDataSource ds = (DriverManagerDataSource) ach.getBean("dataSource");
        DriverManagerDataSource dsDW = (DriverManagerDataSource) ach.getBean("dataSourcePentahoDW");
        Pattern pat = Pattern.compile("(jdbc:mysql://)(.*)(:)([0-9]+)(/)([a-zA-Z]*)(?)(.*)");
        Matcher m = pat.matcher(dsDW.getUrl());
        String nameOfDataBase = null;
        if (m.find()) {
            nameOfDataBase = m.group(6);
        }
        if (!nameOfDataBase.equals("")) {
            createPropertiesFileForPentahoDWReports(ds, dsDW);
            String path = System.getProperty("user.home") + "/.mifos/ETL/MifosDataWarehouseETL/" + FILENAME;
            String jarPath = System.getProperty("user.home") + "/.mifos/ETL/mifos-etl-plugin-1.0-SNAPSHOT.one-jar.jar";
            String javaHome = System.getProperty("java.home") + "/bin/java"; 
            String cmd = "-jar " + jarPath + " " + path + " false";
            String pathToLog = System.getProperty("user.home") + "/.mifos/ETL/log";     
            if (File.separatorChar == '\\') { // windows platform
                javaHome=javaHome.replaceAll("/", "\\\\");
                javaHome='"'+javaHome+'"';
                cmd = cmd.replaceAll("/", "\\\\");
                pathToLog = pathToLog.replaceAll("/", "\\\\");
            }
            try {
            	
                cmd = javaHome +" "+ cmd +" "+ dsDW.getUsername() + " " + dsDW.getPassword() + " " + dsDW.getUrl();
                Process p = Runtime.getRuntime().exec(cmd);
                BufferedReader reader = new BufferedReader (new InputStreamReader(p.getInputStream()));
                String line =null;
                if(new File(pathToLog).exists()){
                	new File(pathToLog).delete();
                }
                File file = new File(pathToLog);
                PrintWriter fw = new PrintWriter(file);
                while ((line = reader.readLine ()) != null) {
                	fw.println(line);
                }
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void createPropertiesFileForPentahoDWReports(DriverManagerDataSource ds, DriverManagerDataSource dsDW) {
        try {
            String pathToJNDIDirectory = System.getProperty("user.dir") + "/simple-jndi";
            String pathToJNDIPropertiesFile = System.getProperty("user.dir") + "/simple-jndi/jdbc.properties";
            if (File.separatorChar == '\\') {
                pathToJNDIDirectory = pathToJNDIDirectory.replaceAll("/", "\\\\");
                pathToJNDIPropertiesFile = pathToJNDIPropertiesFile.replaceAll("/", "\\\\");
            }
            if(!new File(pathToJNDIDirectory).exists()){
            	new File(pathToJNDIDirectory).mkdir();
            }
            File file = new File(pathToJNDIPropertiesFile);
            PrintWriter fw = new PrintWriter(file);
            fw.println("SourceDB/type=javax.sql.DataSource");
            fw.println("SourceDB/driver=com.mysql.jdbc.Driver");
            fw.println("SourceDB/url=" + ds.getUrl());
            fw.println("SourceDB/user=" + ds.getUsername());
            fw.println("SourceDB/password=" + ds.getPassword());
            fw.println("DestinationDB/type=javax.sql.DataSource");
            fw.println("DestinationDB/driver=com.mysql.jdbc.Driver");
            fw.println("DestinationDB/url=" + dsDW.getUrl());
            fw.println("DestinationDB/user=" + dsDW.getUsername());
            fw.println("DestinationDB/password=" + dsDW.getUsername());
            fw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
