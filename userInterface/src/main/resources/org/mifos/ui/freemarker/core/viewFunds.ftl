[#ftl]
[#--
* Copyright (c) 2005-2010 Grameen Foundation USA
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
    <span id="page.id" title="viewFunds" />
    [@mifos.crumb "organizationPreferences.viewfunds"/]
    
    <p class="font15 orangeheading">[@spring.message "organizationPreferences.viewfunds"/]</p>
    <p>&nbsp;&nbsp;</p> 
    <div class="span-15" id="fundDetailsList">
    
    	<div class="span-15 fontBold borderbtm paddingLeft">
     		<span class="span-5 ">[@spring.message "organizationPreferences.viewfunds.name"/]</span>
           	<span class="span-8">[@spring.message "organizationPreferences.viewfunds.fundCode"/]</span>
           	<span class="span-1 ">&nbsp;</span>
        </div>

    	[#list fundsList as fund]
		<div class="span-15 borderbtm paddingLeft">
        	<span class="span-5 ">${fund.name}</span>
           	<span class="span-8">${fund.code.value}</span>
           	<span class="span-1 "><a class="floatRT" href="editFunds.ftl?fundId=${fund.id}">[@spring.message "organizationPreferences.viewfunds.edit"/]</a></span>
        </div>    		
        [/#list] 
    </div>
 </div><!--Main Content Ends-->  
[/@adminLeftPaneLayout]