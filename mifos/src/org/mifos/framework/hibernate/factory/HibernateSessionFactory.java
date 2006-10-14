/**

 * HibernateSessionFactory.java    version: xxx



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
package org.mifos.framework.hibernate.factory;


import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.mifos.framework.exceptions.HibernateStartUpException;

/**
 *  HibernateSessionFactory is used to get the hibernate session factory.
 *  Hibernate session factory is configured with mapping configuration files.
 *  Hibernate sessions are got using this session factory. 
 *  Hibernate uses this configuration information to perform all its 
 *  activities when apis are invoked on the hibernate session object.
 */
public class HibernateSessionFactory
{

    private static SessionFactory sessionFactory;

    /**
	 * Set the static hibernate session factory from
	 * the hibernate configuration passed as a parameter.
	 */
    public static void setConfiguration(Configuration config)
	throws HibernateStartUpException {
		try {
			/** Throwing HibernateStartUpException
			    seems to cause somewhat mysterious failures
			    in {@link TestConstPlugin}.
			    
			    Why?
			    
			    And shouldn't we at least call .close()
			    on the old sessionFactory, if nothing else?
			*/
//			if (sessionFactory != null) {
//				/* Re-opening the sesssion factory on every test
//				 * seems to be too slow.  */
//				throw new HibernateStartUpException(
//					"Session factory is already open");
//			}
			sessionFactory = config.buildSessionFactory();
		} catch (Exception e) {
			throw new HibernateStartUpException(e);
		}
	}

    /**
	 * The call to getSessionFactory returns the HibernateSessionFactory which
	 * is configured with the mapping hibernate configuration files
	 */
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

}
