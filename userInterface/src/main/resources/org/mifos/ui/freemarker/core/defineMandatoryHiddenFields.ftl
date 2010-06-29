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
  [@mifos.crumb url="definemandatory/hiddenfields" /]
  <p>&nbsp;&nbsp;</p>
  	<form method="" action="" name="formname">
  	 <p class="orangeheading">[@spring.message "definemandatory/hiddenfields"/]</p>
    <div class="span-22 last">
    	<div class="bluedivs span-22 fontBold">
        	<span class="span-8 paddingLeft">[@spring.message "systemwidefields"/]</span><span class="span-7 paddingLeft">[@spring.message "hide"/]</span><span class="span-5 paddingLeft">[@spring.message "mandatory"/]</span>
        </div>
        <div class="span-22 borderbtm ">
        	<span class="span-8 paddingLeft ">[@spring.message "externalId"/]</span><span class="span-7 paddingLeft "><input type="checkbox" /></span><span class="span-5 paddingLeft"><input type="checkbox" /></span>
        </div>
        <div class="span-22 borderbtm ">
        	<span class="span-8 paddingLeft ">[@spring.message "ethnicity"/]</span><span class="span-7 paddingLeft "><input type="checkbox" /></span><span class="span-5 paddingLeft"><input type="checkbox" /></span>
        </div>
        <div class="span-22 borderbtm ">
        	<span class="span-8 paddingLeft ">[@spring.message "citizenship"/]</span><span class="span-7 paddingLeft "><input type="checkbox" /></span><span class="span-5 paddingLeft"><input type="checkbox" /></span>
        </div>
        <div class="span-22 borderbtm ">
        	<span class="span-8 paddingLeft ">[@spring.message "handicapped"/]</span><span class="span-7 paddingLeft "><input type="checkbox" /></span><span class="span-5 paddingLeft"><input type="checkbox" /></span>
        </div>
        <div class="span-22 borderbtm ">
        	<span class="span-8 paddingLeft ">[@spring.message "educationlevel"/]</span><span class="span-7 paddingLeft "><input type="checkbox" /></span><span class="span-5 paddingLeft"><input type="checkbox" /></span>
        </div>
        <div class="span-22 borderbtm ">
        	<span class="span-8 paddingLeft ">[@spring.message "photo"/]</span><span class="span-7 paddingLeft "><input type="checkbox" /></span><span class="span-5 paddingLeft"><input type="checkbox" /></span>
        </div>
        <div class="span-22 borderbtm ">
        	<span class="span-8 paddingLeft ">[@spring.message "assigningClienttopositions"/]</span><span class="span-7 paddingLeft "><input type="checkbox" /></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 borderbtm ">
        	<span class="span-8 paddingLeft ">[@spring.message "address1"/]</span><span class="span-7 paddingLeft ">&nbsp;</span><span class="span-5 paddingLeft"><input type="checkbox" /></span>
        </div>
        <div class="span-22 borderbtm ">
        	<span class="span-8 paddingLeft ">[@spring.message "address2"/]</span><span class="span-7 paddingLeft "><input type="checkbox" /></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 borderbtm ">
        	<span class="span-8 paddingLeft ">[@spring.message "address3"/]</span><span class="span-7 paddingLeft "><input type="checkbox" /></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 borderbtm ">
        	<span class="span-8 paddingLeft ">[@spring.message "city/District"/]</span><span class="span-7 paddingLeft "><input type="checkbox" /></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 borderbtm ">
        	<span class="span-8 paddingLeft ">[@spring.message "state"/]</span><span class="span-7 paddingLeft "><input type="checkbox" /></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 borderbtm ">
        	<span class="span-8 paddingLeft ">[@spring.message "country"/]</span><span class="span-7 paddingLeft "><input type="checkbox" /></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 borderbtm ">
        	<span class="span-8 paddingLeft ">[@spring.message "postalCode"/]</span><span class="span-7 paddingLeft "><input type="checkbox" /></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 borderbtm ">
        	<span class="span-8 paddingLeft ">[@spring.message "receiptIDandDate"/]</span><span class="span-7 paddingLeft "><input type="checkbox" /></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 last">
        	<span class="span-8 paddingLeft ">[@spring.message "collateralTypeandNotes"/]</span><span class="span-7 paddingLeft "><input type="checkbox" /></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
    
    </div>
    <div class="span-22 last">
    	<div class="bluedivs span-22 fontBold">
        	<span class="span-8 paddingLeft">[@spring.message "client/systemusersfields"/]</span><span class="span-7 paddingLeft">[@spring.message "hide"/]</span><span class="span-5 paddingLeft">[@spring.message "mandatory"/]</span>
        </div>
        <div class="span-22 borderbtm ">
        	<span class="span-8 paddingLeft ">[@spring.message "middlename"/]</span><span class="span-7 paddingLeft "><input type="checkbox" /></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 borderbtm ">
        	<span class="span-8 paddingLeft ">[@spring.message "secondlastname"/]</span><span class="span-7 paddingLeft "><input type="checkbox" /></span><span class="span-5 paddingLeft"><input type="checkbox" /></span>
        </div>
        <div class="span-22 borderbtm ">
        	<span class="span-8 paddingLeft ">[@spring.message "governmentID"/]</span><span class="span-7 paddingLeft "><input type="checkbox" /></span><span class="span-5 paddingLeft"><input type="checkbox" /></span>
        </div>
        <div class="span-22 borderbtm ">
        	<span class="span-8 paddingLeft ">[@spring.message "spouse/fathermiddlename"/]</span><span class="span-7 paddingLeft "><input type="checkbox" /></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
        <div class="span-22 borderbtm ">
        	<span class="span-8 paddingLeft ">[@spring.message "spouse/fathersecondlastname"/]</span><span class="span-7 paddingLeft "><input type="checkbox" /></span><span class="span-5 paddingLeft"><input type="checkbox" /></span>
        </div>
        <div class="span-22 borderbtm ">
        	<span class="span-8 paddingLeft ">[@spring.message "phonenumber"/]</span><span class="span-7 paddingLeft "><input type="checkbox" /></span><span class="span-5 paddingLeft"><input type="checkbox" /></span>
        </div>
        <div class="span-22 borderbtm ">
        	<span class="span-8 paddingLeft ">[@spring.message "trained"/]</span><span class="span-7 paddingLeft "><input type="checkbox" /></span><span class="span-5 paddingLeft"><input type="checkbox" /></span>
        </div>
        <div class="span-22 borderbtm ">
        	<span class="span-8 paddingLeft ">[@spring.message "trainedon"/]</span><span class="span-7 paddingLeft ">&nbsp;</span><span class="span-5 paddingLeft"><input type="checkbox" /></span>
        </div>
        <div class="span-22 last">
        	<span class="span-8 paddingLeft ">[@spring.message "business/workactivities"/]</span><span class="span-7 paddingLeft "><input type="checkbox" /></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
    
    </div>
    <div class="span-22 last">
    	<div class="bluedivs span-22 fontBold">
        	<span class="span-8 paddingLeft">[@spring.message "groupfields"/]</span><span class="span-7 paddingLeft">[@spring.message "hide"/]</span><span class="span-5 paddingLeft">[@spring.message "mandatory"/]</span>
        </div>
        <div class="span-22 last">
        	<span class="span-8 paddingLeft ">[@spring.message "trained"/]</span><span class="span-7 paddingLeft "><input type="checkbox" /></span><span class="span-5 paddingLeft">&nbsp;</span>
        </div>
    
    </div>
    <div class="span-22 last">
    	<div class="bluedivs span-22 fontBold">
        	<span class="span-8 paddingLeft">[@spring.message "loanAccountFields"/]</span><span class="span-7 paddingLeft">[@spring.message "hide"/]</span><span class="span-5 paddingLeft">[@spring.message "mandatory"/]</span>
        </div>
        <div class="span-22 borderbtm last">
        	<span class="span-8 paddingLeft ">[@spring.message "purposeofLoan"/]</span><span class="span-7 paddingLeft ">&nbsp;</span><span class="span-5 paddingLeft"><input type="checkbox" /></span>
        </div>
        <div class="clear">&nbsp;</div>
    </div>

    <div class="span-22 prepend-9">
    	<input type="button" class="buttn" name="submit" value="Submit" onclick="location.href='admin.ftl'" />
        <input type="button" class="buttn2" name="cancel" value="Cancel" onclick="location.href='admin.ftl'" />
    </div>
   	</form> 
  </div><!--Main Content Ends-->




[@mifos.footer/] 