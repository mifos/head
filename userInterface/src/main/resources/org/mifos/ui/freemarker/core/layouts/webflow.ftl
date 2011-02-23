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

[#-- 
Layout to be used with Spring Web Flow. At the top of the page is a progress
tracker by way of states and currentState (see below).

    currentTab   : the top navigation tab to highlight. See navigation.ftl for supported values.
    states       : An array of valid states (as keys in message bundles) in the flow. Order of 
                   array is the order of flow. It is an array of message keys.
    currentState : The current web flow state. It must match one of the values in "states" array. 
--]
[#macro webflow currentTab states currentState]
[@layout.header "title" /]
[@widget.topNavigationNoSecurity currentTab /]

<div class="container webflow">
    <br/>
    <!-- flow progress indicator -->
    <div class="progress-tracker borders span-24">
        <ul>
        [#assign currentStateEncountered = false]
        [#list states as state]
            [#assign cssClass = "incomplete"]
            [#if state == currentState]
                [#assign currentStateEncountered = true]
                [#assign cssClass = "active"]
            [#elseif currentStateEncountered == false]
                [#assign cssClass = "completed"]
            [/#if]
            <li class="${cssClass}">[@spring.message state /]</li>
        [/#list]
        </ul>
    </div>
    
    <!-- flow content -->
    <br/>
    <div class="content borders no-top-border span-24">
        <div class="span-1">&nbsp;</div>
        <div class="span-21">
            <br/>
            [#nested]
            <br/>
        </div>
        <div class="span-1 last">&nbsp;</div>
    </div>
</div>

[@layout.footer /]
[/#macro]
