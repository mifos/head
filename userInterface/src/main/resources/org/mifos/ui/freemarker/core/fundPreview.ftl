[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[#import "macros.ftl" as mifosMacros]
[@mifos.header "title" /]
[@mifos.topNavigationNoSecurity currentTab="Admin" /]
<!--  Left Sidebar Begins-->
<div class="sidebar ht550">
  [#include "adminLeftPane.ftl" ]
</div> 
<!--  Left Sidebar Ends-->
<!--Container Begins-->
<div>

<!--  Main Content Begins-->  
  <div class=" content leftMargin180">
  	<form method="POST" action="fundPreview.ftl" name="fundPreview">
    
    <div class="span-24">
  		[@spring.bind "formBean.name"/]
  		[#assign breadcrumb = {"admin":"AdminAction.do?method=load", "organizationPreferences.viewfunds":"viewFunds.ftl",spring.status.value?default(""):""}/]
  		[@mifos.crumbpairs breadcrumb/]
     	
        <div class="clear">&nbsp;</div>
    	<p class="font15"><span class="fontBold">[@spring.bind "formBean.name"/]<label name="${spring.status.expression}">${spring.status.value?default("")}</label>[@spring.showErrors "<br />"/]</span>&nbsp;-&nbsp;<span class="orangeheading">[@spring.message "organizationPreferences.fundpreview.previewfundinformation"/]</span></p>
        <div>[@spring.message "organizationPreferences.viewFunds.edit.previewTheFieldsBelowThenClickSubmitClickCancelToReturnToFundDetailsWithoutSubmittingInformation"/]</div>
        <div class="clear">&nbsp;</div>
        <div class="allErrorsDiv">
		[@mifosMacros.showAllErrors "formBean.*"/]
    	</div>
        
        <div class="prepend-1 fontBold">[@spring.message "organizationPreferences.viewFunds.edit.funddetails"/]</div>
        <div class="prepend-1 span-22 last">
        	[@spring.bind "formBean.id"/]
        	<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
        	<span class="span-4 fontBold">[@spring.message "organizationPreferences.viewFunds.name"/]&nbsp;:</span><span class="span-4">&nbsp;[@spring.bind "formBean.name"/]<label name="${spring.status.expression}">${spring.status.value?default("")}</label>[@spring.showErrors "<br />"/]</span><br />
            <span class="span-4 fontBold">[@spring.message "organizationPreferences.viewFunds.fundCode"/]&nbsp;:</span><span class="span-4">&nbsp;[@spring.bind "formBean.codeValue"/]<label name="${spring.status.expression}">${spring.status.value?default("")}</label>[@spring.showErrors "<br />"/]</span>
        </div>
        <div class="clear">&nbsp;</div>
        [@spring.bind "formBean.name"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
        [@spring.bind "formBean.codeId"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
        [@spring.bind "formBean.codeValue"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
        <div class="prepend-1"><input class="buttn2" type="submit" name="EDIT" value="[@spring.message "organizationPreferences.fundpreview.editFundInformation"/]"/></div>
        <div class="clear">&nbsp;</div>
        <hr />
        <div class="prepend-10">
            <input class="buttn"  type="submit" name="submit" value="[@spring.message "submit"/]"/>
            <input class="buttn2" type="submit" id="CANCEL" name="CANCEL" value="[@spring.message "cancel"/]"/>
        </div>
	</div>
   	</form> 
  </div><!--Main Content Ends-->  
</div><!--Container Ends-->