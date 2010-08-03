[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
[@mifos.topNavigationNoSecurity currentTab="Admin" /]
<div class="sidebar ht600">
[#include "adminLeftPane.ftl" ]
</div> 
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
  [@mifos.footer/]