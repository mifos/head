 [#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Left Sidebar Begins
  <div class="sidebar ht600">
  [#include "adminLeftPane.ftl" ]
  </div> 
  <!--  Left Sidebar Ends-->
 <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
  <div class="bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "viewChecklists"/]</span></div>
  <br/><p>&nbsp;&nbsp;</p>
  	<form method="" action="" name="formname">  	
  	<div class="fontBold"><span class="orangeheading">[@spring.message"viewchecklists"/]</span></div>
    <p>Click on a checklist below to view details and make changes or <a href="newChecklist.html">define a new checklist</a></p>
    <div class="fontBold">Center</div>
    <div class="span-22">
    	<ul>
        	<li type="circle"><a href="">checklist1</a>(Active)</li>
        </ul>	
    </div>
    <div class="fontBold">Group</div>
    <div class="span-22">
    	<ul>
        	<li type="circle"><a href="">checklist1</a>(Active)</li>
        </ul>	
    </div>
    <div class="fontBold">Client</div>
    <div class="span-22">
    	<ul>
        	<li type="circle"><a href="">checklist1</a>(Active)</li>
        </ul>	
    </div>
    <div class="fontBold">Loan</div>
    <div class="span-22">
    	<ul>
        	<li type="circle"><a href="">checklist1</a>(Active)</li>
        </ul>	
    </div>
    <div class="fontBold">Savings</div>
    <div class="span-22">
    	<ul>
        	<li type="circle"><a href="checklistDetails.html">checklist1</a>(Active)</li>
        </ul>	
    </div>
   	</form> 
  </div><!--Main Content Ends-->
  [@mifos.footer/]