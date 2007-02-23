
package org.mifos.framework.hibernate;

import org.mifos.framework.exceptions.HibernateStartUpException;
import org.mifos.framework.hibernate.configuration.ConfigureSession;
import org.mifos.framework.hibernate.factory.HibernateSessionFactory;

/**
	Hibernate Start up class called during the system start up 
	configures hibernate with relevant mapping files & 
	connection parameters
*/

public class HibernateStartUp
{
	/**
		This method is called by the StartUP plug in class during the 
		system start up , this will initialize the hibernate configuration 
		files and database connection parameters required for hibernate for 
		database connectivity.
	 */
	public static void initialize(String hibernatePropertiesPath)
		throws HibernateStartUpException {
		ConfigureSession.configure(hibernatePropertiesPath);
		HibernateSessionFactory.setConfiguration(
			ConfigureSession.getConfiguration());
	}

}
