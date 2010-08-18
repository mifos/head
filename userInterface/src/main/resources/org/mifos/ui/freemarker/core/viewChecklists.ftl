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
[@adminLeftPaneLayout] <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
  <div class="bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "organizationPreferences.viewChecklists"/]</span></div>
   <br/>
  	<form method="" action="" name="formname">
  	<div class="marginLeft30">  	
  	<div class="fontBold"><span class="orangeheading">[@spring.message "organizationPreferences.viewChecklists"/]</span></div>
    <p>[@spring.message "organizationPreferences.viewChecklists.Clickonachecklistbelowtoviewdetailsandmakechangesor"/]<a href="defineNewChecklist.ftl">[@spring.message"organizationPreferences.viewChecklists.defineanewchecklist"/]</a></p>
    <div class="fontBold">[@spring.message "organizationPreferences.viewChecklists.Center"/]</div>
    <div class="span-22">
    	<ul>
        	<li type="circle"><a href="">checklist1</a>(Active)</li>
        </ul>	
    </div>
     <br/><p>&nbsp;&nbsp;</p>
    <div class="fontBold">[@spring.message "organizationPreferences.viewChecklists.Group"/]</div>
    <div class="span-22">
    	<ul>
        	<li type="circle"><a href="">checklist1</a>(Active)</li>
        </ul>	
    </div>
     <br/><p>&nbsp;&nbsp;</p>
    <div class="fontBold">[@spring.message "organizationPreferences.viewChecklists.Client"/]</div>
    <div class="span-22">
    	<ul>
        	<li type="circle"><a href="">checklist1</a>(Active)</li>
        </ul>	
    </div>
     <br/><p>&nbsp;&nbsp;</p>
    <div class="fontBold">[@spring.message "organizationPreferences.viewChecklists.Loan"/]</div>
    <div class="span-22">
    	<ul>
        	<li type="circle"><a href="">checklist1</a>(Active)</li>
        </ul>	
    </div>
     <br/><p>&nbsp;&nbsp;</p>
    <div class="fontBold">[@spring.message "organizationPreferences.viewChecklists.Savings"/]</div>
    <div class="span-22">
    	<ul>
        	<li type="circle"><a href="checklistDetails.html">checklist1</a>(Active)</li>
        </ul>	
    </div>
    
    </div>
   	</form> 
  </div><!--Main Content Ends-->
  [/@adminLeftPaneLayout]