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

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.struts.actionforms.BaseActionForm;

/**
 * This class is the ActionForm associated with the ReportParams Action.
 */
public class ReportsUserParamsActionForm extends BaseActionForm {
	
	public ReportsUserParamsActionForm() {
		super();
	}
	
	private int reportId;
	private String applPath;
	private String expFormat;
	private String expFileName;

	/**
	 * The reset method is used to reset all the values to null.
	 * @see org.apache.struts.validator.ValidatorForm#reset(org.apache.struts.action.ActionMapping,
	 *      javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		MifosLogManager.getLogger(LoggerConstants.REPORTSLOGGER).info(
				"In Login Reset");
		
		
		super.reset(mapping, request);
	}
	
	public int getReportId()
    {
        return reportId;
    }

    public void setReportId(int reportId)
    {
        this.reportId = reportId;
    }

    public String getApplPath()
    {
        return applPath;
    }

    public void setApplPath(String applPath)
    {
        this.applPath = applPath;
    }
    
    public String getExpFormat()
    {
        return expFormat;
    }

    public void setExpFormat(String expFormat)
    {
        this.expFormat = expFormat;
    }
    
    public String getExpFileName()
    {
        return expFileName;
    }

    public void setExpFileName(String expFileName)
    {
        this.expFileName = expFileName;
    }
   	
	/**
	 * This method is to skip validation for load method
	 * @param mapping
	 * @param request
	 * @return
	 */
	public ActionErrors customValidate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors=new ActionErrors();
		request.getParameter("method");
		
		return errors;
	}

}
