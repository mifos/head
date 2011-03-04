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
<style>
td {
	text-align:none;
	padding:0px 0px 0px 0px;
}

.middle table {
	width : auto;
}

table {
	margin-bottom : 0em;
	width : 100%;
}

.progress-tracker li {
	float:right;
	padding-right:10px;
}

.container {
	width:90%;
}
</style>
<div class="container webflow">
    <br/>
    <!-- flow progress indicator -->
    <div class="progress-tracker borders span-24">
        <table>
		<tr>
        [#assign currentStateEncountered = false]
		[#assign ind = 0]
        [#assign no_of_states = 0]
        [#list states as state]
		[#if state_has_next]
		[#assign no_of_states = no_of_states + 1]
		[/#if]
		[/#list]
		[#assign no_of_states = no_of_states + 1]
		[#assign div=(100/no_of_states)?int]
		
        [#list states as state]
            [#assign cssClass = "incomplete"]
            [#if state == currentState]
                [#assign currentStateEncountered = true]
                [#assign cssClass = "active"]
            [#elseif currentStateEncountered == false]
                [#assign cssClass = "completed"]
            [/#if]
        	[#if ind == 0]
	            <td width="${div}%">
           <li style="float:left;" class="${cssClass}">[@spring.message state /] </span>
			</td>
	    
	    [#elseif state_has_next]
                  <td width="${div}%" align="center" >
		<div class="middle" align="center">
	    <table>
	    <tr>
	    <td>
	    [#if cssClass == "incomplete"]
	    <img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17">
            [#elseif cssClass == "active"]
	    <img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17">
            [#elseif cssClass == "completed"]
	    <img src="pages/framework/images/timeline/tick.gif" width="17" height="17">
             [/#if]
	    
  	    </td>
	    <td>
            <li style="float:none; background-image:none; padding-left:3px;" class="${cssClass}">[@spring.message state /]</span>
	    </td>
	    </tr>
	    </table>
		</div>
 			</td>
	    [#else]
                  <td width="${div}%">
            <li class="${cssClass}">[@spring.message state /]</span>
			</td>
	    
	    [/#if]
             
	    [#assign ind=ind+1]
        [/#list]
		</tr>
        </table>
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
