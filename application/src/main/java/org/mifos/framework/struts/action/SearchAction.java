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

package org.mifos.framework.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.components.tabletag.TableTagConstants;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.security.util.ActivityMapper;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;

public class SearchAction extends BaseAction {

    public SearchAction() {

    }

    @Override
    protected BusinessService getService() throws ServiceException {
        return null;
    }

    private void setPerspective(HttpServletRequest request) {
        if (request.getParameter("perspective") != null) {
            request.setAttribute("perspective", request.getParameter("perspective"));
        }
    }

    public ActionForward searchPrev(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        // Integer current =
        // (Integer)SessionUtils.getAttribute("current",request);

        // if( current ==null) throw new
        // PageExpiredException(ExceptionConstants.PAGEEXPIREDEXCEPTION);
        // SessionUtils.setRemovableAttribute("current",current-1,TableTagConstants.PATH,request.getSession());
        checkForValidData(request);
        SessionUtils.setRemovableAttribute("meth", "previous", TableTagConstants.PATH, request.getSession());
        setPerspective(request);
        String forwardkey = (String) SessionUtils.getAttribute("forwardkey", request.getSession());
        if (forwardkey == null)
            throw new PageExpiredException();
        return mapping.findForward(forwardkey);
    }

    public ActionForward searchNext(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        // Integer current=1;
        // if(null !=SessionUtils.getAttribute("current",request.getSession()))
        // {
        // current=(Integer)SessionUtils.getAttribute("current",request.getSession());
        // }
        // SessionUtils.setRemovableAttribute("current",current+1,TableTagConstants.PATH,request.getSession());
        checkForValidData(request);
        SessionUtils.setRemovableAttribute("meth", "next", TableTagConstants.PATH, request.getSession());
        setPerspective(request);
        String forwardkey = (String) SessionUtils.getAttribute("forwardkey", request.getSession());
        if (forwardkey == null) {
            throw new PageExpiredException();
        }
        return mapping.findForward(forwardkey);
    }

    protected void cleanUpSearch(HttpServletRequest request) throws PageExpiredException {
        SessionUtils.setRemovableAttribute("TableCache", null, TableTagConstants.PATH, request.getSession());
        SessionUtils.setRemovableAttribute("current", null, TableTagConstants.PATH, request.getSession());
        SessionUtils.setRemovableAttribute("meth", null, TableTagConstants.PATH, request.getSession());
        SessionUtils.setRemovableAttribute("forwardkey", null, TableTagConstants.PATH, request.getSession());
        SessionUtils.setRemovableAttribute("action", null, TableTagConstants.PATH, request.getSession());
        SessionUtils.removeAttribute(Constants.SEARCH_RESULTS, request);
    }

    protected void addSeachValues(String searchString, String officeId, String officeName, HttpServletRequest request)
            throws PageExpiredException {

        SessionUtils.setAttribute(Constants.SEARCH_STRING, searchString, request);
        SessionUtils.setAttribute(Constants.BRANCH_ID, officeId, request);
        SessionUtils.setAttribute(Constants.OFFICE_NAME, officeName, request);

    }

    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        cleanUpSearch(request);
        SessionUtils.setQueryResultAttribute(Constants.SEARCH_RESULTS, getSearchResult(form), request);
        return mapping.findForward(ActionForwards.search_success.toString());
    }

    protected QueryResult getSearchResult(ActionForm form) throws Exception {
        return null;
    }

    private void checkForValidData(HttpServletRequest request) throws PageExpiredException {

        SessionUtils.getAttribute(Constants.SEARCH_STRING, request);
        SessionUtils.getAttribute(Constants.OFFICE_NAME, request);
        SessionUtils.getAttribute(Constants.BRANCH_ID, request);

    }

    protected void checkPermissionForAddingNotes(AccountTypes accountTypes, CustomerLevel customerLevel,
            UserContext userContext, Short recordOfficeId, Short recordLoanOfficerId) throws ApplicationException {
        if (!isPermissionAllowed(accountTypes, customerLevel, userContext, recordOfficeId, recordLoanOfficerId))
            throw new CustomerException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
    }

    private boolean isPermissionAllowed(AccountTypes accountTypes, CustomerLevel customerLevel,
            UserContext userContext, Short recordOfficeId, Short recordLoanOfficerId) {
        return ActivityMapper.getInstance().isAddingNotesPermittedForAccounts(accountTypes, customerLevel, userContext,
                recordOfficeId, recordLoanOfficerId);
    }
}
