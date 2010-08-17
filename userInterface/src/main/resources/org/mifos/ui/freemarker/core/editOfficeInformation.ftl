[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Left Sidebar Begins-->
  <div class="sidebar ht550">
  [#include "adminLeftPane.ftl" ]
  </div> 
  <!--  Left Sidebar Ends-->
   <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
  	<form method="" action="" name="formname">
    <div class="span-24">
  		<div class="bluedivs paddingLeft"><a href="admin.ftl" id="editoffice.link.admin">[@spring.message "admin" /]</a>&nbsp;/&nbsp;<a href="viewOffices.ftl" id="editoffice.link.viewOffices">[@spring.message "viewOffices" /]</a>&nbsp;/&nbsp;<a href="" id="editoffice.link.viewOffice">[@spring.message "offices.editOfficeInformation.testAreaOffice" /]</a></div>
        <div class="clear">&nbsp;</div>
        <div class="fontBold"><span>[@spring.message "offices.editOfficeInformation.testAreaOffice" /]</span>&nbsp;-&nbsp;<span class="orangeheading">[@spring.message "offices.editOfficeInformation.editofficeinformation" /]</span></div>
        <div><span>[@spring.message "offices.editOfficeInformation.previewthefieldsbelow.ThenclickConfirm.ClickCanceltoreturntoOfficeDetailswithoutsubmittinginformation" /]</span></div>
        <div><span class="red"> * </span>[@spring.message "fieldsmarkedwithanasteriskarerequired." /]</div>
        <p class="error" id="editoffice.error.message"></p>
        
		<p class="fontBold">[@spring.message "offices.editOfficeInformation.officedetails" /]</p>
		<p>&nbsp;&nbsp;</p>
        <div class="prepend-1  span-23 last">
        	<div class="span-20"><span class="span-4 rightAlign" id="editoffice.label.officeName"><span class="red">* </span>[@spring.message "offices.editOfficeInformation.officeName" /]</span>
    				<span class="span-3"><input id="name" name="name" type="text" id="editoffice.input.officeName"/></span>
  			</div>
            <div class="span-20"><span class="span-4 rightAlign" id="editoffice.label.shortName"><span class="red">* </span>[@spring.message "offices.editOfficeInformation.officeshortname" /]</span>
    				<span class="span-3"><input id="name" name="name" type="text" size="5" id="editoffice.input.shortName" /></span>
  			</div>
            <div class="span-20 "><span class="span-4 rightAlign"><span class="red">* </span>[@spring.message "offices.editOfficeInformation.officetype" /]</span>
   					<span class="span-3"><select name="select">
      					<option >[@spring.message "--Select--" /]</option>
                        <option >[@spring.message "offices.editOfficeInformation.regionalOffice" /]</option>
                        <option >[@spring.message "offices.editOfficeInformation.divisionalOffice" /]</option>
                        <option >[@spring.message "offices.editOfficeInformation.areaOffice" /]</option>
                        <option >[@spring.message "offices.editOfficeInformation.branchOffice" /]</option>
				    </select></span>
			</div>
            <div class="span-20 "><span class="span-4 rightAlign"><span class="red">* </span>[@spring.message "offices.editOfficeInformation.parentOffice" /]</span>
   					<span class="span-3"><select name="select">
      					<option >[@spring.message "--Select--" /]</option>
                        <option >[@spring.message "offices.editOfficeInformation.headOffice(MifosHO)" /]</option>
				    </select></span>
			</div>
        </div>
        <p>&nbsp;&nbsp;</p>
        <p class="clear fontBold">[@spring.message "offices.editOfficeInformation.status" /]</p>
        <p>&nbsp;&nbsp;</p>
	    <div class="prepend-1  span-23 last">
           <div class="span-20 "><span class="span-4 rightAlign">[@spring.message "offices.editOfficeInformation.status1" /]</span>
            	<span class="span-3"><select name="status">
      					<option >[@spring.message "--Select--" /]</option>
                        <option >[@spring.message "active" /]</option>
                        <option >[@spring.message "offices.editOfficeInformation.inactive" /]</option>
					</select></span>
  			</div>
        </div>
        <p>&nbsp;&nbsp;</p>
        <p class="fontBold">[@spring.message "offices.editOfficeInformation.officeaddress" /]</p>
        <p>&nbsp;&nbsp;</p>
        <div class="prepend-1  span-21 last">
        	<div class="span-20 "><span class="span-4 rightAlign" id="editoffice.label.address1">[@spring.message "offices.editOfficeInformation.address1" /]</span>
    				<span class="span-3"><input id="address1" name="address1" type="text" id="editoffice.input.address1" /></span>
  			</div>
            <div class="span-20 "><span class="span-4 rightAlign" id="editoffice.label.address2">[@spring.message "offices.editOfficeInformation.address2" /]</span>
    				<span class="span-3"><input id="address2" name="address2" type="text" id="editoffice.input.address2"/></span>
  			</div>
           <div class="span-20 "><span class="span-4 rightAlign" id="editoffice.label.address3">[@spring.message "offices.editOfficeInformation.address3" /]</span>
    				<span class="span-3"><input id="address3" name="address3" type="text" /></span>
  			</div>
            <div class="span-20 "><span class="span-4 rightAlign" id="editoffice.label.city">[@spring.message "offices.editOfficeInformation.city/District" /]</span>
    				<span class="span-3"><input id="cityDistrict" name="cityDistrict" type="text" /></span>
  			</div>
            <div class="span-20 "><span class="span-4 rightAlign id="editoffice.label.state"">[@spring.message "offices.editOfficeInformation.state" /]</span>
    				<span class="span-3"><input id="state" name="state" type="text" /></span>
  			</div>
            <div class="span-20 "><span class="span-4 rightAlign" id="editoffice.label.country">[@spring.message "offices.editOfficeInformation.country" /]</span>
    				<span class="span-3"><input id="country" name="country" type="text" /></span>
  			</div>
            <div class="span-20 "><span class="span-4 rightAlign" id="editoffice.label.postalCode">[@spring.message "offices.editOfficeInformation.postalCode" /]</span>
    				<span class="span-3"><input id="postalCode" name="postalCode" type="text" /></span>
  			</div>
            <div class="span-20 "><span class="span-4 rightAlign" id="editoffice.label.phoneNumber">[@spring.message "offices.editOfficeInformation.telephone" /]</span>
    				<span class="span-3"><input id="telephone" name="telephone" type="text" /></span>
  			</div>
  		<p>&nbsp;&nbsp;</p>
  		<p class="fontBold">[@spring.message "offices.editOfficeInformation.additionalInformation" /]</p>
  		<p>&nbsp;&nbsp;</p>
        </div>
       
      
                  <hr />
        <div class="prepend-9">
          <input class="buttn" type="button" name="preview" id="editoffice.button.preview" value="Preview" onclick="#"/>
          <input class="buttn2" type="button" name="cancel" id="editoffice.button.cancel" value="Cancel" onclick="#"/>
        </div>
	</div>
   	</form> 
  </div><!--Main Content Ends-->
  [@mifos.footer/]