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
  <div class=" content leftMargin180">
  <p class="bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "admin.viewOffices"/]</span></p>
  	<form method="" action="" name="formname">
  	<p>&nbsp;&nbsp;</p>
    <div class="orangeheading">[@spring.message "admin.viewOffices"/]</div>
    <p>[@spring.message "offices.viewoffices.clickonanofficebelowtoviewdetailsandmakechangesor"/] <a href="#">[@spring.message "offices.viewoffices.defineanewoffice"/]</a></p>
    <div class="span-23">
    	<div class="span-23 "><a class="fontBold" href="#" >[@spring.message "offices.viewoffices.mifosHO"/]</a>
        	<ul>
            	<!--<li><a></a></li>-->
            </ul>
        </div>
        <div class="span-23">
        	<div class="span-16"><span class="fontBold">[@spring.message "offices.defineNewOffice.regionalOffice"/]</span>
        		<div><ul>
            			<!--<li><a></a></li>-->
    	        	</ul></div>
            </div>
            <span class="span-5 rightAlign"><a href="#">[@spring.message "offices.viewoffices.definenewregionaloffice"/]</a></span>
        </div>
        <div class="span-23">
        	<div class="span-16"><span class="fontBold">[@spring.message "offices.defineNewOffice.divisionalOffice"/]</span>
        		<div><ul>
            			<!--<li><a></a></li>-->
    	        	</ul></div>
            </div>
            <span class="span-5 rightAlign"><a href="#">[@spring.message "offices.viewoffices.definenewDivisionalOffice"/]</a></span>
        </div>
        <div class="span-23">
        	<div class="span-16"><span class="fontBold">[@spring.message "offices.defineNewOffice.areaOffice"/]</span>
        		<div><ul>
            			<li><a href="#">[@spring.message "offices.editOfficeInformation.testAreaOffice"/]</a></li>
    	        	</ul></div>
            </div>
            <span class="span-5 rightAlign"><a href="#">[@spring.message "offices.viewoffices.definenewAreaOffice"/]</a></span>
        </div>
        <div class="span-23">
        <div class="span-16"><span class="fontBold">[@spring.message "offices.defineNewOffice.branchOffice"/]</span>
        		<div>Mifos Ho<ul>
            			<li><a href="officeDetails.html">Branch_office_1</a></li>
    	        	</ul></div>
            </div>            <span class="span-5 rightAlign"><a href="#">[@spring.message "offices.viewoffices.definenewBranchOffice"/]</a></span>
        </div>
    </div>
   	</form> 
  </div><!--Main Content Ends-->
  [/@adminLeftPaneLayout]