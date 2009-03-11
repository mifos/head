/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
package org.mifos.application.admin.system;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Locale;

import javax.servlet.ServletContext;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mifos.application.master.MessageLookup;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.TestingService;

/**
 * JDBC URL parsing code in this class is
 * <a href="https://groups.google.com/group/comp.lang.java.programmer/browse_thread/thread/b7090860d6834bae">only
 * known to work with MySQL JDBC URLs</a>. Once Mifos supports other database backends, here are some ideas:
 * <ul>
 * <li>fallback to simply printing out the full JDBC URL rather than trying to parse port, host, etc.</li>
 * <li>implement or reuse parsers for JDBC URLs specific to other databases</li>
 * </ul> 
 */
public class SystemInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private DatabaseMetaData databaseMetaData;
	private ServletContext context;
	private Locale locale;

	private String javaVendor;
	private String javaVersion;
	private SvnRevision svnRevision;
	private String osName;
	private String osArch;
	private String osVersion;
	private String customReportsDir;

	private String infoSource;
	private URI infoURL;
	private String infoUserName;

	// Note: make sure to close the connection that got the metadata!
	public SystemInfo(DatabaseMetaData databaseMetaData, ServletContext context, Locale locale, boolean getInfoSource) 
	throws Exception {
		this(locale, getInfoSource);
		this.databaseMetaData = databaseMetaData; 
		URI mysqlOnly = new URI(databaseMetaData.getURL());
		this.infoURL = new URI(mysqlOnly.getSchemeSpecificPart());
		this.infoUserName = databaseMetaData.getUserName();
		this.context = context;
	}

	public SystemInfo(Locale locale, boolean getInfoSource) {
		if (getInfoSource) {
			try {
                this.infoSource = "" + new TestingService().getAllSettingsFilenames();
            } catch (IOException e) {
                this.infoSource = MessageLookup.getInstance().lookup("admin.unableToDetermineConfigurationSource");
            }
		}
		this.locale = locale;
		setJavaVendor(System.getProperty("java.vendor"));
		setJavaVersion(System.getProperty("java.version"));
		setSvnRevision(new SvnRevision());
		setOsName(System.getProperty("os.name"));
		setOsArch(System.getProperty("os.arch"));
		setOsVersion(System.getProperty("os.version"));
		setCustomReportsDir(System.getProperty("user.home")+File.separatorChar+".mifos");
	}

	public int getApplicationVersion() {
		return DatabaseVersionPersistence.APPLICATION_VERSION;
	}

	public String getDatabaseVendor() {
		try {
			return databaseMetaData.getDatabaseProductName();
		} catch (SQLException e) {
			return "unknown";
		}
	}

	public String getDatabaseVersion() {
		try {
			return databaseMetaData.getDatabaseProductVersion();
		} catch (SQLException e) {
			return "unknown";
		}
	}
	
	public String getDriverName() {
		try {
			return databaseMetaData.getDriverName();
		} catch (SQLException e) {
			return "unknown";
		}
	}

	public String getDriverVersion() {
		try {
			return databaseMetaData.getDriverVersion();
		} catch (SQLException e) {
			return "unknown";
		}
	}
	
	public String getJavaVendor() {
		return javaVendor;
	}

	public String getJavaVersion() {
		return javaVersion;
	}
	
	public String getApplicationServerInfo() {
		return context.getServerInfo();
	}
	
	public String getSvnBranch() {
		return svnRevision.getBranch();
	}
	
	public String getSvnRevision() {
		return svnRevision.getVersion();
	}
	
	public void setJavaVendor(String javaVendor) {
		this.javaVendor = javaVendor;
	}
	
	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}
	
	public void setSvnRevision(SvnRevision svnRevision) {
		this.svnRevision = svnRevision;
	}

	private void setCustomReportsDir(String dir) {
		customReportsDir=dir;
	}

	public String getCustomReportsDir() {
		return customReportsDir;
	}

	public String getOsName() {
		return osName;
	}

	public void setOsName(String osName) {
		this.osName = osName;
	}

	public String getOsArch() {
		return osArch;
	}

	public void setOsArch(String osArch) {
		this.osArch = osArch;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getInfoSource() {
		return infoSource;
	}

	public void setInfoSource(String infoSource) {
		this.infoSource = infoSource;
	}
	
	public String getDatabaseServer() {
		return infoURL.getHost();
	}

	public String getDatabasePort() {
		if (infoURL.getPort() < 0)
		    return "unknown";
		else
		    return "" + infoURL.getPort();
	}

	public String getDatabaseName() {
		String path = infoURL.getPath();
		if (path != null) // database name is not required
		    path = path.replaceFirst("/", "");
		return path;
	}

	public String getDatabaseUser() {
		return this.infoUserName;
	}

	public String getInfoURL() {
		return infoURL.toString();
	}

	public void setInfoURL(URI infoURL) {
		this.infoURL = infoURL;
	}

	public String getInfoUserName() {
		return infoUserName;
	}

	public void setInfoUserName(String infoUserName) {
		this.infoUserName = infoUserName;
	}

    public DateTime getDateTime() {
        return new DateTimeService().getCurrentDateTime();
    }
    
    public String getDateTimeString() {
        DateTimeFormatter formatter = DateTimeFormat.shortDateTime().withLocale(locale);
        return formatter.print(getDateTime().getMillis());
    }

}
