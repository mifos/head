[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]

  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  [#include "adminLeftPane.ftl" /]
   <div class="content leftMargin180">
	[@mifos.crumbs breadcrumbs/]
	
    <div class="marginTop10">&nbsp;</div> 
    <form method="post" name="viewofficehierarchy" action="viewOfficeHierarchy.ftl">  
    <div class="span-19">
	    <div class="fontBold"><span class="orangeheading">[@spring.message "admin.viewofficehierarchy"/]</span></div>
        <span>[@spring.message "viewOfficeHierarchy.theofficehierarchycanhaveminimumtwoandmaximumfivelevels"/]</span>
        [@spring.bind "formBean" /]
  		[@spring.showErrors "<li>" /]  
        <p><span>[@spring.message "viewOfficeHierarchy.checkthelevelstobeincluded"/]. </span></p>
        <p>&nbsp;&nbsp;</p>
        <div><span class="fontBold">[@spring.message "note"/] </span><span>[@spring.message "viewOfficeHierarchy.thehighestandlowesthierarchylevelscannotberemovedfromthesystem"/]</span></div>
		<div class="span-19">
    	<div class="prepend-2">

    <span>
    [@spring.formCheckbox "formBean.headOffice" "disabled=disabled"/]
    [@spring.message "viewOfficeHierarchy.headOffice"/]
    </span><br />
	        
    <span>
    [@spring.formCheckbox "formBean.regionalOffice" /]
    [@spring.message "viewOfficeHierarchy.regionalOffice"/]
    </span><br />
       	       
     <span>
     [@spring.formCheckbox "formBean.subRegionalOffice" /]
     [@spring.message "viewOfficeHierarchy.divisionalOffice"/]
     </span><br />
          
     <span>
     [@spring.formCheckbox "formBean.areaOffice" /]
     [@spring.message "viewOfficeHierarchy.areaOffice"/]
     </span><br />
          
     <span>
     [@spring.formCheckbox "formBean.branchOffice" "disabled"/]
     [@spring.message "viewOfficeHierarchy.branchOffice"/]
     </span><br />

            </div>
        </div>
        <div class="clear">&nbsp;</div>
        <hr />
           
        <div class="prepend-9">
             <input class="buttn"  type="submit" name="submit" value="[@spring.message "submit"/]"/>
            <input class="buttn2" type="submit" id="CANCEL" name="CANCEL" value="[@spring.message "cancel"/]"/>
        </div>
	</div>
   	</form> 
  </div><!--Main Content Ends-->  
    [@mifos.footer/]