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
  <p class="bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "viewoffices"/]</span></p>
  	<form method="" action="" name="formname">
  	<p>&nbsp;&nbsp;</p>
    <div class="orangeheading">[@spring.message "viewoffices"/]</div>
    <p>[@spring.message "clickonanofficebelowtoviewdetailsandmakechangesordefinea"/] <a href="#">[@spring.message "newoffice"/]</a></p>
    <div class="span-23">
    	<div class="span-23 "><a class="fontBold" href="#" >[@spring.message "mifosHO"/]</a>
        	<ul>
            	<!--<li><a></a></li>-->
            </ul>
        </div>
        <div class="span-23">
        	<div class="span-16"><span class="fontBold">[@spring.message "regionalOffice"/]</span>
        		<div><ul>
            			<!--<li><a></a></li>-->
    	        	</ul></div>
            </div>
            <span class="span-5 rightAlign"><a href="#">[@spring.message "definenewregionaloffice"/]</a></span>
        </div>
        <div class="span-23">
        	<div class="span-16"><span class="fontBold">[@spring.message "divisionalOffice"/]</span>
        		<div><ul>
            			<!--<li><a></a></li>-->
    	        	</ul></div>
            </div>
            <span class="span-5 rightAlign"><a href="#">[@spring.message "definenewDivisionalOffice"/]</a></span>
        </div>
        <div class="span-23">
        	<div class="span-16"><span class="fontBold">[@spring.message "areaOffice"/]</span>
        		<div><ul>
            			<li><a href="#">[@spring.message "testAreaOffice"/]</a></li>
    	        	</ul></div>
            </div>
            <span class="span-5 rightAlign"><a href="#">[@spring.message "definenewAreaOffice"/]</a></span>
        </div>
        <div class="span-23">
        <div class="span-16"><span class="fontBold">[@spring.message "branchOffice"/]</span>
        		<div>Mifos Ho<ul>
            			<li><a href="officeDetails.html">Branch_office_1</a></li>
    	        	</ul></div>
            </div>            <span class="span-5 rightAlign"><a href="#">[@spring.message "definenewBranchOffice"/]</a></span>
        </div>
    </div>
   	</form> 
  </div><!--Main Content Ends-->
  [@mifos.footer/]