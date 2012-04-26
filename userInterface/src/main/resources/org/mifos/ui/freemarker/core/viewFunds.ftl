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
[#include "layout.ftl"]
[@adminLeftPaneLayout]
<!--  Main Content Begins-->
<div class=" content">
    <span id="page.id" title="viewFunds"></span>
[@widget.crumb "organizationPreferences.viewfunds"/]
    <div class="margin20lefttop">
        <p class="font15 orangeheading margin5top10bottom">[@spring.message "organizationPreferences.viewfunds"/]</p>
        <div class="span-15 width80prc" id="fundDetailsList">
            <table class="standardTableLayout" cellspacing="0" cellpadding="3" border="0" width="80%" id="fundDisplayTable">
                <tbody>
                <tr>
                    <td width="33%" class="drawtablerowboldnoline">
                    [@spring.message "organizationPreferences.viewFunds.name"/]
                    </td>
                    <td width="44%" class="drawtablerowboldnoline">
                    [@spring.message "organizationPreferences.viewFunds.fundCode"/]
                    </td>
                    <td width="23%" class="drawtablerowboldnoline">&nbsp;</td>
                </tr>
                    [#list fundsList as fund]
                    <tr>
                        <td width="33%" class="drawtablerow">${fund.name}</td>
                        <td width="44%" class="drawtablerow">${fund.code.value}</td>
                        <td align="right" width="23%" class="drawtablerow">
                            <a class="floatRT"
                               href="editFunds.ftl?fundId=${fund.id}">[@spring.message "organizationPreferences.viewFunds.edit"/]</a>
                        </td>
                    </tr>
                    [/#list]
                </tbody>
            </table>
            <br>
            [@form.returnToPage  "AdminAction.do?method=load" "button.back" "viewsavingsproducts.button.back"/]
        </div>
    </div>
</div><!--Main Content Ends-->
[/@adminLeftPaneLayout]