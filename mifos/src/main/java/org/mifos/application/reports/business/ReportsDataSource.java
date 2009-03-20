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
 
package org.mifos.application.reports.business;

import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.security.util.UserContext;

/**
 * This class encapsulates the Reports Datasource
 */
public class ReportsDataSource extends BusinessObject
{
	
	private int datasourceId;
    private String name;
    private String driver;
    private String url;
    private String username;
    private String password;
	public ReportsDataSource()
    {

    }
   
    public ReportsDataSource(UserContext userContext)
    {
        super(userContext);
    }

    public int getDatasourceId()
    {
        return datasourceId;
    }

    public void setDatasourceId(int datasourceId)
    {
        this.datasourceId = datasourceId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getDriver()
    {
        return driver;
    }

    public void setDriver(String driver)
    {
        this.driver =driver;
    }
    
    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }
    
    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }
    
    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public Short getEntityID() {
		return null;
	}
   
    
    
}
