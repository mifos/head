/**

 * ReportsJasperMap.java    version: 1.0

 

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

package org.mifos.application.reports.business;

import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.security.util.UserContext;

/**
 * This class encapsulates mapping between Jasper file and Reports
 */
public class ReportsJasperMap extends BusinessObject {
	
	private Short reportId;
	private String reportJasper;
	

	public ReportsJasperMap() {
	}
	
	public ReportsJasperMap(UserContext userContext) {
		super(userContext);		
	}
	
	public ReportsJasperMap(Short reportId, String reportJasper) {
		this.reportId = reportId;
		this.reportJasper = reportJasper;
	}

	
	public Short getReportId() {
		return reportId;
	}

	public void setReportId(Short reportId) {
		this.reportId = reportId;
	}

	public String getReportJasper() {
		return reportJasper;
	}

	public void setReportJasper(String reportJasper) {
		this.reportJasper = reportJasper;
	}

	public Short getEntityID() {
		return null;
	}

}
