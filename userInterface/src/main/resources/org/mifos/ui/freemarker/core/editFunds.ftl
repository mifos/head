[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Left Sidebar Begins-->
  <div class="sidebar ht600">
  [#include "adminLeftPane.ftl" ]
  </div> 
  <!--  Left Sidebar Ends-->
  <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
  	<form method="POST" action="editFunds.ftl" name="editFunds">
    <div class="span-24">
    [@spring.bind "formBean"/]
    [@spring.showErrors "<br />"/]
    
    [@spring.bind "formBean.name"/]
  		[#assign breadcrumb = {"admin":"AdminAction.do?method=load", "viewfunds":"viewFunds.ftl",spring.status.value?default(""):""}/]
  	[@spring.showErrors "<br />"/]  	
    [@mifos.crumbpairs breadcrumb/]
        <div class="clear">&nbsp;</div>
    	<p class="font15"><span class="fontBold">[@spring.bind "formBean.name"/]<label name="${spring.status.expression}">${spring.status.value?default("")}</label>[@spring.showErrors "<br />"/]</span>&nbsp;-&nbsp;<span class="orangeheading">[@spring.message "editfundinformation"/]</span></p>
        <div>[@spring.message "completethefieldsbelow.ThenclickPreview.ClickCanceltoreturntoViewfundspagewithoutsubmittinginformation."/]</div>
        <div><span class="red">* </span>[@spring.message "fieldsmarkedwithanasteriskarerequired"/] </div>
        <p>&nbsp;</p>
                <p class="fontBold">[@spring.message "funddetails"/]</p>
                <p>&nbsp;</p>
        <div class="prepend-3 span-22 last">
        [@spring.bind "formBean.name"/]
        	<span class="span-4 rightAlign"><span class="red"> * </span>[@spring.message "name"/]</span><span class="span-4"><input type="text" name="${spring.status.expression}" value="${spring.status.value?default("")}"/></span>
        [@spring.showErrors "<br />"/]
        </div>
        <div class="prepend-3 span-22 last">
        [@spring.bind "formBean.code.value"/]
            <span class="span-4 rightAlign"><span class="red"> * </span>[@spring.message "fundCode"/]</span><span class="span-4" name="${spring.status.expression}">${spring.status.value?default("")}</span>
        [@spring.showErrors "<br />"/]
        </div>
        <div class="clear">&nbsp;</div>
        <hr />
        <div class="prepend-10">
            <input class="buttn" type="submit" name="preview" value="Preview"/>
            <input class="buttn2" type="submit" id="CANCEL" name="CANCEL" value="[@spring.message "cancel"/]"/>
        </div>
	</div>
   	</form> 
  </div><!--Main Content Ends-->
  [@mifos.footer/]