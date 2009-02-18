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

package org.mifos.application.reports.struts.actionforms;

import org.mifos.framework.struts.actionforms.BaseActionForm;



/**
 * This class is the ActionForm associated with the ReportParamsMap Action.
 */
public class ReportsParamsMapActionForm extends BaseActionForm {
	
	
	
	/**
	 * Default constructor
	 */
	public ReportsParamsMapActionForm() {
		super();
	}

	
	
	
	private int parameterId;
   private int reportId;
   private int mapId;

	
	public int getParameterId()
    {
        return parameterId;
    }

    public void setParameterId(int parameterId)
    {
        this.parameterId = parameterId;
    }
    
    public int getReportId()
    {
        return reportId;
    }

    public void setReportId(int reportId)
    {
        this.reportId = reportId;
    }
    
    public int getMapId()
    {
        return mapId;
    }

    public void setMapId(int mapId)
    {
        this.mapId = mapId;
    }
   
}
