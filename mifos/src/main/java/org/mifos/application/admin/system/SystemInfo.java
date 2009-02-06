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
import java.io.Serializable;
import java.net.URI;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.servlet.ServletContext;

import org.mifos.framework.ApplicationInitializer;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.util.helpers.FilePaths;

public class SystemInfo implements Serializable {
	
	private DatabaseMetaData databaseMetaData;
	private ServletContext context;
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
	public SystemInfo(DatabaseMetaData databaseMetaData, ServletContext context, boolean getInfoSource) 
	throws Exception {
		this(getInfoSource);
		this.databaseMetaData = databaseMetaData;
		/* ':' is not a valid scheme character and java.net.URI can't parse URIs with a ':'. java.net.URL can't
		 * parse JDBC URLs either since only "standard" schemes like http and ftp are allowed. */  
		String sanitizedURI = databaseMetaData.getURL().replaceFirst("jdbc:", "");
		this.infoURL = new URI(sanitizedURI);
		this.infoUserName = databaseMetaData.getUserName();
		this.context = context;
	}

	public SystemInfo(boolean getInfoSource) {
		if (getInfoSource) {
			this.infoSource = ApplicationInitializer.getHibernateProperties();
			if (! this.infoSource.equals(FilePaths.CONFIGURABLEMIFOSDBPROPERTIESFILE)) {
				this.infoSource = FilePaths.DEFAULTMIFOSDBPROPERTIESFILE;
			}
		}
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

	/**
	 * @param infoURL Must not contain a ':' (colon character) in the scheme.
	 */
	public void setInfoURL(URI infoURL) {
		this.infoURL = infoURL;
	}

	public String getInfoUserName() {
		return infoUserName;
	}

	public void setInfoUserName(String infoUserName) {
		this.infoUserName = infoUserName;
	}

}
