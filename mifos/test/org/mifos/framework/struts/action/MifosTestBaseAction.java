/**

 * MifosTestBaseAction.java    version: xxx

 

 * Copyright © 2005-2006 Grameen Foundation USA

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

package org.mifos.framework.struts.action;



/**
 * This class will be used to test MifosBaseAction because MifosBaseAction 
 * is an abstract class , hence this class extends MifosBaseAction and implements the 
 * abstract method getPath() which actually returns the path in the dependency file being used for testing.
 * @author ashishsm
 *
 */
public class MifosTestBaseAction extends MifosBaseAction {

	/**
	 * 
	 */
	public MifosTestBaseAction() {
		super();
		
	}

	/* (non-Javadoc)
	 * @see org.mifos.framework.struts.action.MifosBaseAction#getPath()
	 */
	
	protected String getPath() {
		
		return "FrameworkActionTestPath";
	}
	


}
