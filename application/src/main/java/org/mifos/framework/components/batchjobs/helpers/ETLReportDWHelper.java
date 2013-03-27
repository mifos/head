package org.mifos.framework.components.batchjobs.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.mifos.application.servicefacade.ApplicationContextHolder;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.util.ConfigurationLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class ETLReportDWHelper extends TaskHelper {

    private final String FILENAME = "DataWarehouseInitialLoad.kjb";
    private static final Logger LOGGER = Logger.getLogger(ETLReportDWHelper.class);
    public static final String DATA_WAREHOUSE_DB_NAME_PATTERN = "(jdbc:mysql://)(.*)(:)([0-9]+)(/)([0-9a-zA-Z_]*)(?)(.*)";
    
    @Override
    public void execute(final long timeInMillis) throws BatchJobException {
        new ApplicationContextHolder();
        ArrayList<String> errors = new ArrayList<String>();
        ApplicationContext ach = ApplicationContextHolder.getApplicationContext();
        DriverManagerDataSource ds = (DriverManagerDataSource) ach.getBean("dataSource");
        DriverManagerDataSource dsDW = (DriverManagerDataSource) ach.getBean("dataSourcePentahoDW");
        Pattern pat = Pattern.compile(DATA_WAREHOUSE_DB_NAME_PATTERN);
        Matcher m = pat.matcher(dsDW.getUrl());
        String nameOfDataBase = null;
        if (m.find()) {
            nameOfDataBase = m.group(6);
        }
        if (!nameOfDataBase.equals("")) {
        	try {
        		dsDW.getConnection();
        	} catch (SQLException ex) {
        		errors.add("Data Warehouse is not configured");
        		throw new BatchJobException("Data warehouse database", errors);
        	}
        	ConfigurationLocator configurationLocator = new ConfigurationLocator();
        	String configPath = configurationLocator.getConfigurationDirectory();
            createPropertiesFileForPentahoDWReports(ds, dsDW);
            String path = configPath + "/ETL/MifosDataWarehouseETL/" + FILENAME;
            String jarPath = configPath + "/ETL/mifos-etl-plugin-1.0-SNAPSHOT.one-jar.jar";
            String javaHome = System.getProperty("java.home") + "/bin/java"; 
            String pathToLog = configPath + "/ETL/log"; 

            if (File.separatorChar == '\\') { // windows platform
                javaHome=javaHome.replaceAll("/", "\\\\");
                javaHome='"'+javaHome+'"';
                jarPath = jarPath.replaceAll("/", "\\\\");
                path = path.replaceAll("/", "\\\\");
                pathToLog = pathToLog.replaceAll("/", "\\\\");
            }
            PrintWriter fw = null;
            try {
            	boolean hasErrors = false;
            	boolean notRun = true;
            	ProcessBuilder processBuilder = new ProcessBuilder(javaHome, "-jar", jarPath, path, "false",
            	        dsDW.getUsername(), dsDW.getPassword(), dsDW.getUrl());
            	processBuilder.redirectErrorStream(true);
                Process p = processBuilder.start();
                BufferedReader reader = new BufferedReader (new InputStreamReader(p.getInputStream()));
                String line =null;
				if (new File(pathToLog).exists()) {
					new File(pathToLog).delete();
				}
				File file = new File(pathToLog);
				fw = new PrintWriter(file);
                while ((line = reader.readLine()) != null) {
                	fw.println(line);
                	if (line.matches("^ERROR.*")) {
                		hasErrors = true;
                	}
                	notRun=false;
                }
                if (notRun) {
                	errors.add("Data Warehouse is not configured properly");
            		throw new BatchJobException("Data warehouse database", errors);
                }
                if (hasErrors) {
                	errors.add("ETL error, for more details see log file: "+pathToLog);
                	throw new BatchJobException("ETL error", errors);
                }
            } catch (IOException ex) {
            	throw new BatchJobException(ex.getCause());
            } finally {
                if (fw != null) {
                    fw.close();
                }
            }
		} else {
			errors.add("Data Warehouse is not configured");
			throw new BatchJobException("Data warehouse database", errors);
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
