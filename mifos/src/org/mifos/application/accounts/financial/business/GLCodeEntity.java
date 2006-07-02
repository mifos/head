/**

 * GLCodeEntity.java    version: 1.0

 

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

package org.mifos.application.accounts.financial.business;

import org.mifos.framework.business.PersistentObject;

public class GLCodeEntity extends PersistentObject {
	private Short glcodeId;

	private String glcode;
	
	private COABO associatedCOA;

	public String getGlcode() {
		return glcode;
	}

	public void setGlcode(String glcode) {

		this.glcode = glcode;
	}

	public Short getGlcodeId() {

		return glcodeId;
	}

	public void setGlcodeId(Short glcodeId){
		this.glcodeId = glcodeId;
	}

	public COABO getAssociatedCOA(){
          return associatedCOA;
	}

	public void setAssociatedCOA(COABO associatedCOA){
       this.associatedCOA = associatedCOA;
	}

}
