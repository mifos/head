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
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@layout.webflow currentTab="Admin"
                 currentState="user.flowState.chooseUserOffice" 
                 states=["user.flowState.chooseUserOffice", 
                         "user.flowState.enterAccountInfo", 
                         "user.flowState.reviewAndSubmit"]]
        <p class="font15"><span class="fontBold">[@spring.message "systemUsers.defineNewSystemUser.addanewuser"/]</span>&nbsp;-&nbsp;<span class="orangeheading">[@spring.message "systemUsers.defineNewSystemUser.chooseOffice"/]</span></p>
        <p>[@spring.message "systemUsers.defineNewSystemUser.toselect,clickonaofficefromthelistbelow.ClickCanceltoreturntoAdminpage"/]</p>

        <p class="fontBold"><a href="${flowExecutionUrl}&_eventId=officeSelected&officeId=1">[@spring.message "systemUsers.defineNewSystemUser.mifosHo"/]</a></p>

        <ul>
        [#list officeDetails.nonBranches as item]
            <li><a href="${flowExecutionUrl}&_eventId=officeSelected&officeId=${item.id}">${item.name}</a></li>
        [/#list]
        </ul>

        [#list officeDetails.branchOnlyOfficeHierarchy as office]
            <div>${office.name}</div>
            <ul>
               [#list office.children as branch]
                       <li><a href="${flowExecutionUrl}&_eventId=officeSelected&officeId=${branch.id}">${branch.name}</a></li>
               [/#list]
               </ul>
           [/#list]

        <hr />
        <div class="prepend-8">
		<table width="93%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center"> &nbsp;
            <form method="post" action="user.ftl?execution=${flowExecutionKey}">
			   <div class="row centered">
                <input class="buttn2" type="submit" name="_eventId_cancel" value="[@spring.message "cancel"/]" />
				</div>
            </form>
			</td>
			</tr>
			</table>
        </div>
       [/@layout.webflow]