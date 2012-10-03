package org.mifos.reports.pentaho.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.mifos.application.servicefacade.ApplicationContextHolder;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class JNDIException extends RuntimeException {

	private static final long serialVersionUID = -8518534165760795156L;

	public JNDIException() {
        super();
    }

    public JNDIException(final String message) {
        super(message);
    }
    
    public JNDIException(final String message, final HttpServletRequest request) {
    	super(message);
    	checkConfigurationDwDatabase(request);
    	
    }
    
    public void checkConfigurationDwDatabase(HttpServletRequest request){
      	 new ApplicationContextHolder(); 
           ApplicationContext ach = ApplicationContextHolder.getApplicationContext();
           DriverManagerDataSource dsDW = (DriverManagerDataSource) ach.getBean("dataSourcePentahoDW");
           Pattern pat = Pattern.compile("(jdbc:mysql://)(.*)(:)([0-9]+)(/)([a-zA-Z]*)(?)(.*)");
           Matcher m = pat.matcher(dsDW.getUrl());
           String nameOfDataBase = null;
           if (m.find()) {
               nameOfDataBase = m.group(6);
           }
           if (nameOfDataBase.equals("")) {
           	request.getSession().setAttribute("configureDwDatabase", "false");
           } else {
           	request.getSession().setAttribute("configureDwDatabase", "true");
           }
      }
    
}
