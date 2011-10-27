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

[@layout.header "title" /]
[@widget.topNavigationNoSecurity currentTab="Admin" /]
<div class="sidebar htTotal">
    [#include "adminLeftPane.ftl" /]
</div>
<!--  Main Content Begins-->
<div class=" content leftMargin180">
    [@widget.crumbs breadcrumbs /]
    [@spring.showErrors "<br>" /]
    <div class="marginLeft30">
        <div class="span-21 borderbtm">
            <div class="clear">&nbsp;</div>

            [#list model.batchjobs as batchjob]
                [#if batchjob.name == model.jobFailName]
                    <p class="font15"><span class="orangeheading">${batchjob.name}&nbsp;[@spring.message "systemAdministration.batchjobs.batchjobsFailDetails" /]</span></p><br>
                    <p>${batchjob.failDescription}</p>
                [/#if]
            [/#list]
        </div>
        <br /><a href="batchjobs.ftl">[@spring.message "systemAdministration.batchjobs.return" /]</a>
    </div>
</div>
<!--Main Content Ends-->
[@layout.footer/]