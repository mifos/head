[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]

  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  [#include "adminLeftPane.ftl" /]
   <div class="content leftMargin180">
    [@mifos.crumb url="viewofficehierarchy" /]
      <div class="marginTop10">&nbsp;</div> 
    <form action="" >
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
    <span><input type="checkbox" checked="checked" disabled="disabled" />&nbsp;[@spring.message"headOffice"/]</span><br />
        
     <span><input type="checkbox" checked="checked" />&nbsp;[@spring.message"regionalOffice"/]</span><br />
           
     <span><input type="checkbox" checked="checked" />&nbsp;[@spring.message"divisionalOffice"/]</span><br />
          
     <span><input type="checkbox" checked="checked" />&nbsp;[@spring.message"areaOffice"/]</span><br />
          
     <span><input type="checkbox" checked="checked" disabled="disabled" />&nbsp;[@spring.message"branchOffice"/]</span><br />
           
            </div>
        </div>
        <div class="clear">&nbsp;</div>
        <hr />
      
           
        <div class="prepend-9">
          <input class="buttn floatLeft"   type="button" name="submit" onclick="window.location='admin.ftl'" value="Submit" />
          <input class="buttn2" type="button" name="cancel" value="Cancel" onclick="window.location='admin.ftl'"/>
        </div>
	</div>
   	</form> 
  </div><!--Main Content Ends-->
  
    [@mifos.footer/]