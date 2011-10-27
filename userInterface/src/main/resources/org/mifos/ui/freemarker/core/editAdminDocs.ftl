[#ftl]
[#--
* Copyright (c) 2005-2011 Grameen Foundation USA
*  All rights reserved.
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
*  implied. See the License for the specific language governing
*  permissions and limitations under the License.
*
*  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
*  explanation of the license and how it is applied.
--]

[#-- FOLLOWING IS ONLY USED FOR TESTING --]
<head>
    <link rel="stylesheet" type="text/css" href="pages/css/viewAdmin4Testing.css" />
</head>

[#include "layout.ftl"]

[@adminLeftPaneLayout]

    <span id="page.id" title="editAdministrativeDocuments"></span>
    [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "manageReports.viewadministrativedocuments":""}/]
    [@widget.crumbpairs breadcrumb /]
    <div class="content_panel">

    <form action="updateAdminDoc.ftl" method = "POST">
    <fieldset>
    <legend>[@spring.message "manageReports.documentinformation" /]</legend>
    <ol>
        <li>
            [@spring.message "manageReports.admindocumentdetails" /]
        </li>
        <li>
            [@spring.message "manageReports.completefieldsbelow" /]
            [@spring.message "manageReports.clickcanceltoreturntoadmin" /]<br>
            <span class="red">* </span>[@spring.message "manageReports.requiredfieldsmarked" /]
        </li>

        <li>
        [#--
            Note: ${spring.status.expression}: expression used to retrieve bean/property
            In this case, it evaluates to name, so could've just used for="name" as well
        --]
        [@spring.formInput "formBean.name", "maxlength=100",text /]
        <label for="${spring.status.expression}"><span class="red">* </span>[@spring.message "manageReports.administrativedocumenttitle" /]</label>
        </li>

        <li>
        [@spring.formSingleSelect "formBean.accountType", accountType /]
        <label for="${spring.status.expression}"><span class="red">* </span>[@spring.message "manageReports.accounttype" /]</label>
        </li>

        [#--
            Would like to use the formMultiSelect macro for this, but it is
            not working yet.
        <li>
        [@spring.formMultiSelect "formBean.showStatus", status /]
        </li>
        --]

        [#--
            Following is just the code used in @spring.formMultiSelect -- tried to work
            through this to try to understand why that macro is not working and how to
            bind a multiselect listbox to the formBean.showStatus field.

            This has problems with binding, need to figure out how to bind a multiselect
            listbox.

            I think the problem lies in following statement:
            contains(spring.status.value?default([""]),value)

            we basically want to pull up the current values stored in the formBean.showStatus
            field to know which options are currently selected.

        <li>
        <label for="statusMap"><span class="red">* </span>[@spring.message "manageReports.showwhenstatus" /]</label>
        [@spring.bind "formBean.showStatus" /]
        <select multiple="multiple" size=6 id="${spring.status.expression}" name="${spring.status.expression}">
            [#list status?keys as value]
                [#assign isSelected = contains(spring.status.value?default([""]),value)]]
                <option value=${value?html} [#if isSelected] selected="selected" [/#if]>
                ${status[value]?html}</option> <br>
                [#list spring.status.errorMessages as error] <b>${error}</b> <br> [/#list]
            [/#list]
        </select>
        </li>
        --]

        [#--
            Following is just a listbox display of the options based on whether the account
            type is loan or savings.
        --]
        <li>
        [@spring.bind "formBean.showStatus" /]
        <label for="statusMap"><span class="red">* </span>[@spring.message "manageReports.showwhenstatus" /]</label>
        <select multiple="multiple" size=6>
            [#list status?keys as value]
                <option value=${value?html}>${status[value]?html}</option><br>
            [/#list]
        </select>
        </li>

        [#--
            Following is a place holder for now - still need to figure out the spring implementation
            for file uploads.
        --]
        <li>
            <label for="file">[@spring.message "manageReports.selectadministrativedocument" /]</label>
            <input type="file" name="file" value="browse"/>
        </li>

          </ol>
    </fieldset>

    [#-- Submit data and invoke updateDoc.ftl --]
    <fieldset class="submit">
            <input type="submit" value="[@spring.message "manageReports.preview" /]" />
            <input type="submit" value="[@spring.message "manageReports.cancel" /]" />
        </fieldset>
    </form>

    </div>

[/@adminLeftPaneLayout]