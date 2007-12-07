/*
 * DatabaseVersionFilter.java
 *
 * Created on December 13, 2006, 5:14 PM
 */

package org.mifos.framework.persistence;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.mifos.framework.ApplicationInitializer;
import org.mifos.framework.struts.tags.XmlBuilder;


public class DatabaseVersionFilter implements Filter {
    
    private static boolean databaseVerified = false;
    private static int databaseVersion = -1;
    
    public DatabaseVersionFilter() {
    }
    
    public void doFilter(ServletRequest request, ServletResponse response,
        FilterChain chain)
        throws IOException, ServletException {

        if(!databaseVerified){
            showError(request, response);
        }else{
            chain.doFilter(request, response);
        }
    }
    
    private void showError(ServletRequest request, ServletResponse response) throws IOException, ServletException {
    	HttpServletResponse httpResponse = (HttpServletResponse)response;
    	httpResponse.setContentType("text/html");
    	httpResponse.setStatus(500);
        PrintWriter out = httpResponse.getWriter();

        int version = databaseVersion;

        printErrorPage(out, version);
    }

	void printErrorPage(PrintWriter out, int version) {
		XmlBuilder xml = new XmlBuilder();
        xml.startTag("html");
        xml.startTag("head");
        xml.startTag("title");
        xml.text("Mifos Database Upgrade Error");
        xml.endTag("title");
        xml.endTag("head");

        xml.startTag("body");
        xml.startTag("h2");
        xml.text("Mifos Database Upgrade Error");
        xml.endTag("h2");
        
        xml.startTag("p");
        xml.text("The system was not able to automatically upgrade the database. ");
        xml.text("Correct the error and restart the application. ");

        xml.startTag("a", "href", "http://mifos.org/developers/technical-orientation/coding-standards#databasestandards");
        xml.text("More about database upgrades.");
        xml.endTag("a");
        
        xml.endTag("p");
        xml.startTag("pre");
		if (version == -1) {
			xml.text("Database is too old to have a version\n");
        }
        else {
        	xml.text("Database Version = "+version+"\n");
        }
		xml.text("Application Version = "+DatabaseVersionPersistence.APPLICATION_VERSION+"\n");
        xml.endTag("pre");
        
        ApplicationInitializer.printDatabaseError(xml);
        
        xml.endTag("body");
        xml.endTag("html");
        out.println(xml.getOutput());
	}

	public void init(FilterConfig filterConfig) {
        try{
            DatabaseVersionPersistence persistence = new DatabaseVersionPersistence();
            if(persistence.isVersioned()){
                databaseVersion = persistence.read();
                databaseVerified = ( databaseVersion == DatabaseVersionPersistence.APPLICATION_VERSION );
            } else {
                databaseVerified = false;
            }
        }catch(Exception e){
            filterConfig.getServletContext().log("Failed to check database version", e);
        }
    }

    public void destroy() {
    }

}
