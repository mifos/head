/**

 * ReportsParamsActionForm.java   version: 1.0



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
package org.mifos.application.reports.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.struts.actionforms.MifosActionForm;
import org.mifos.framework.util.helpers.Constants;

/**
 * This class is the ActionForm associated with the ReportParams Action.
 * 
 * @author zankar
 * 
 */
public class ReportsParamsActionForm extends MifosActionForm {
	
	// ----------------------constructors-----------------------
	
	/**
	 * Default constructor
	 */
	public ReportsParamsActionForm() {
		super();
	}

	
	// ----------------------instance variables-----------------------
	
	private int parameterId;
    private String name;
    private String type;
    private String classname;
    private String data;
    private String description;
    private int datasourceId;

	
		
	//----------------------public methods-----------------------
	
	/**
	 * The reset method is used to reset all the values to null.
	 * @see org.apache.struts.validator.ValidatorForm#reset(org.apache.struts.action.ActionMapping,
	 *      javax.servlet.http.HttpServletRequest)
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		MifosLogManager.getLogger(LoggerConstants.REPORTSLOGGER).info(
				"In Login Reset");
		name = null;
		type=null;
		classname=null;
		data=null;
		description=null;
		
		super.reset(mapping, request);
	}
	
	public int getParameterId()
    {
        return parameterId;
    }

    public void setParameterId(int parameterId)
    {
        this.parameterId = parameterId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
    
    public String getClassname()
    {
        return classname;
    }

    public void setClassname(String classname)
    {
        this.classname = classname;
    }
    
    public String getData()
    {
        return data;
    }

    public void setData(String data)
    {
        this.data = data;
    }
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
    
    
    public int getDatasourceId()
    {
        return datasourceId;
    }

    public void setDatasourceId(int datasourceId)
    {
        this.datasourceId = datasourceId;
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
		String methodCalled= request.getParameter("method");
		
		return errors;
	}

}
