/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
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

import java.io.Serializable;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.servlet.ServletContext;

import org.mifos.framework.persistence.DatabaseVersionPersistence;

public class SystemInfo implements Serializable {
	
	private DatabaseMetaData databaseMetaData;
	private ServletContext context;
	private String javaVendor;
	private String javaVersion;
	private SvnRevision svnRevision;
	private String osName;
	private String osArch;
	private String osVersion;
	
	// Note: make sure to close the connection that got the metadata!
	public SystemInfo(DatabaseMetaData databaseMetaData, ServletContext context) {
		this.databaseMetaData = databaseMetaData;
		this.context = context;
		setJavaVendor(System.getProperty("java.vendor"));
		setJavaVersion(System.getProperty("java.version"));
		setSvnRevision(new SvnRevision());
		setOsName(System.getProperty("os.name"));
		setOsArch(System.getProperty("os.arch"));
		setOsVersion(System.getProperty("os.version"));
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
}
