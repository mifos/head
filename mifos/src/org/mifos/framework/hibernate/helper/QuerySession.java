/**

 * QuerySession.java    version: xxx



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
package org.mifos.framework.hibernate.helper;

import org.hibernate.Session;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.HibernateProcessException;


/**
 *  A class which would return a session used for search
 */
public class QuerySession
{

  /**
     * Return the Session used for query
     * @return Session
     */
    public static Session getSession() throws  HibernateProcessException
    {
       MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("opening session for search");
       return HibernateUtil.getSession();

    }

     /**
	  * closes the Session which was used for query
	  * @param hibernateSession
     */

    public static void closeSession(Session hibernateSession ) throws  HibernateProcessException
    {
    	MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("closing session after search");
    	HibernateUtil.closeSession(hibernateSession);
	}
}
