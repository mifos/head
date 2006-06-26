
package org.mifos.framework.hibernate;

import org.mifos.framework.exceptions.HibernateStartUpException;
import org.mifos.framework.hibernate.configuration.ConfigureSession;
import  org.mifos.framework.hibernate.factory.HibernateSessionFactory;
import org.mifos.framework.hibernate.helper.HibernateConstants;

/**
	* Hibernate Start up class called during the system start up configures hibernate with relavant mapping files & connection parameters
*/

public class HibernateStartUp
{
	/**
		 *This method is called by the StartUP plug in class during the system start up , this will initialize the hibernate configuration files and database connection parameters required for hibernate for database
		 * connectivity. This method internally calls ConfigureSession's configure method to get hibernate configuration object. It then sets the hibernate configuration to hibernate session factory from which hibernate session
		 * are obtained
	 */
    public static void initialize(String hibernatePropertiesPath) throws HibernateStartUpException
    {
      try
      {
    	ConfigureSession.configure(hibernatePropertiesPath);
    	HibernateSessionFactory.setConfiguration(ConfigureSession.getConfiguration());
      }
      catch(Exception  e)
      {
         throw new HibernateStartUpException(HibernateConstants.STARTUPEXCEPTION,e);
      }
    }

}