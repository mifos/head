/**

 * EventManger.java    version: 1.0

 

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
package org.mifos.framework.security.util;

import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.util.helpers.Constants;

public class EventManger {
	
	/**
	 * This function will handle the posting of event to autherization manager
	 * @param type type of event 
	 * @param obj  actual modified object 
	 * @param EventName name of event
	 */
	public static void postEvent( String type,Object obj ,String EventName)
	{
		SecurityEvent se = EventFactory.getEventFactory().createEvent(EventName,obj,type);
		
		/*if ( EventName.equalsIgnoreCase(Constants.ROLECHANGEEVENT))
		{
			AuthorizationManager am = AuthorizationManager.getInstance();
			am.handleEvent(se);

		}*/

		if (EventName.equalsIgnoreCase(SecurityConstants.OFFICECHANGEEVENT))
		{
			HierarchyManager hm = HierarchyManager.getInstance();
			hm.handleEvent(se);

		}
		
	}
	/*
	public static void postEvent()
	{
		HierarchyManager hm = HierarchyManager.getInstance();
		hm.handleEvent(null);
	}
	*/
}
