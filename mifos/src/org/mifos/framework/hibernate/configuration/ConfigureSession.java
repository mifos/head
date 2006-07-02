/**

 * ConfigureSession.java    version: xxx



 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 *

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the

 * License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

 * and how it is applied.

 *

 */
package org.mifos.framework.hibernate.configuration;


import org.hibernate.cfg.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import org.mifos.framework.hibernate.helper.HibernateConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.HibernateStartUpException;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.ResourceLoader;

/**
  * Configure Session creates the hibernate configuration object from the defined hibernate mapping files
*/

public class ConfigureSession
{
	private static Configuration config = null;


	/**
		* This method returns the hibernate configuration object configured during system start up
	*/

	public static Configuration getConfiguration() throws HibernateStartUpException
	{
		return config;
	}
	/**
		 * On System start up this method creates the hibernate configuration object , which is configured with the hibernate configuration files containing the mapping information and
		 * also the database connection related parameters required for hibernate for database access.
	 */


    public static void configure(String hibernatePropertiesPath) throws HibernateStartUpException
    {


		config = new Configuration();
	 try
	  {

		 config.configure(ResourceLoader.getURI(FilePaths.HIBERNATECFGFILE).toURL());
		 MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Resource added ");
	   }
	   catch(Exception e)
	   {
		    
		    MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).error("hibernate.cfg.xml filenot found ");
		 	throw new HibernateStartUpException(HibernateConstants.CFGFILENOTFOUND,e);
	   }
	   try
	   {
		 Properties hibernateProperties = new Properties();

		 hibernateProperties.load(new FileInputStream(new File(ResourceLoader.getURI(hibernatePropertiesPath))))                                                                                            ;
		 config.setProperties(hibernateProperties);
		 MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("properties added ");


	  }
	  catch(Exception e)
	  {
		  throw new HibernateStartUpException(HibernateConstants.HIBERNATEPROPNOTFOUND,e);
	  }

	}


}
