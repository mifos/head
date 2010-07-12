[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]

  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  [#include "adminLeftPane.ftl" /]
   <div class="content leftMargin180">
	<div class="bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "viewOfficeHierarchy"/]</span></div>
	<br/>
    <div class="marginTop10">&nbsp;</div> 
    <form method="post" name="viewofficehierarchy" action="viewOfficeHierarchy.ftl">
    <div class="span-19">
  	     <div class="fontBold"><span class="orangeheading">[@spring.message"viewofficehierarchy"/]</span></div>
        <p><span>[@spring.message"theofficehierarchycanhaveminimumtwoandmaximumfivelevels"/]</span></p>
        <p>&nbsp;&nbsp;</p>
        <p>&nbsp;&nbsp;</p>
        <p><span>[@spring.message"checkthelevelstobeincluded"/]. </span></p>
        <p>&nbsp;&nbsp;</p>
        <div><span class="fontBold">[@spring.message"note"/] </span><span>[@spring.message"thehighestandlowesthierarchylevelscannotberemovedfromthesystem"/]</span></div>
		<div class="span-19">
    	<div class="prepend-2">
    <span>
    [@spring.bind "formBean.headOffice" /]
    <input type="checkbox" checked="checked" disabled="disabled"  name="${spring.status.expression}" value="${spring.status.value}"/>&nbsp;[@spring.message"headOffice"/]</span><br />
        
     <span>
     [@spring.bind "formBean.regionalOffice" /]    
     <input type="checkbox" checked="checked" name="${spring.status.expression}" value="${spring.status.value}"/>&nbsp;[@spring.message"regionalOffice"/]</span><br />
           
     <span>
	[@spring.bind "formBean.divisionalOffice" /]     
     <input type="checkbox" checked="checked"  name="${spring.status.expression}" value="${spring.status.value}"/>&nbsp;[@spring.message"divisionalOffice"/]</span><br />
          
     <span>
	[@spring.bind "formBean.areaOffice" /]     
     <input type="checkbox" checked="checked"  name="${spring.status.expression}" value="${spring.status.value}"/>&nbsp;[@spring.message"areaOffice"/]</span><br />
          
     <span>
	[@spring.bind "formBean.branchOffice" /]     
     <input type="checkbox" checked="checked" disabled="disabled"  name="${spring.status.expression}" value="${spring.status.value}"/>&nbsp;[@spring.message"branchOffice"/]</span><br />
           
            </div>
        </div>
        <div class="clear">&nbsp;</div>
        <hr />
      
           
        <div class="prepend-9">
          <input class="buttn floatLeft"   type="button" name="submit" onclick="window.location='admin.ftl'" value="Submit" />
          <input class="buttn2" type="button" id="CANCEL" name="CANCEL" value="[@spring.message "cancel"/]" />
        </div>
	</div>
   	</form> 
  </div><!--Main Content Ends-->
  
    [@mifos.footer/]