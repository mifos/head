/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.reports.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PentahoReportsAction extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(PentahoReportsAction.class);


    public PentahoReportsAction() throws ServiceException {
    }

    public ActionForward load(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
    	
        logger.info("In PentahoReportsAction: load Method: ");
    	
        return mapping.findForward(Constants.LOAD_SUCCESS);
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(@SuppressWarnings("unused") String method) {
        return true;
    }
}