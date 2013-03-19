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

package org.mifos.customers.struts.actionforms;

import java.util.Date;

import org.mifos.framework.struts.actionforms.BaseActionForm;

public class CustSearchActionForm extends BaseActionForm {

    private String loanOfficerId;

    private String officeId;

    private String searchString;

    private String input;

    private String officeName;

    private String perspective;
    
    private String selectedDateOption;
        
    private boolean clientSearch = true;
    private boolean groupSearch = true;
    private boolean centerSearch = true;

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getLoanOfficerId() {
        return loanOfficerId;
    }

    public void setLoanOfficerId(String loanOfficerId) {
        this.loanOfficerId = loanOfficerId;
    }

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public String getPerspective() {
        return perspective;
    }

    public void setPerspective(String perspective) {
        this.perspective = perspective;
    }

    public String getSelectedDateOption() {
        return selectedDateOption;
    }

    public void setSelectedDateOption(String selectedDateOption) {
        this.selectedDateOption = selectedDateOption;
    }

    public boolean isClientSearch() {
        return clientSearch;
    }

    public void setClientSearch(boolean clientSearch) {
        this.clientSearch = clientSearch;
    }

    public boolean isGroupSearch() {
        return groupSearch;
    }

    public void setGroupSearch(boolean groupSearch) {
        this.groupSearch = groupSearch;
    }

    public boolean isCenterSearch() {
        return centerSearch;
    }

    public void setCenterSearch(boolean centerSearch) {
        this.centerSearch = centerSearch;
    }
 
}
