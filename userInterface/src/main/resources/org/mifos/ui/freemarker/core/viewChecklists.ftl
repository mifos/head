 [#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Left Sidebar Begins-->
  <div class="sidebar htTotal">
  [#include "adminLeftPane.ftl" ]
  </div> 
  <!--  Left Sidebar Ends-->
 <!--  Main Content Begins-->  
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
  [@mifos.footer/]