[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Main Content Begins-->
  <div class="content marginAuto">
    <div class="borders span-23">
      <div class="borderbtm span-23">
        <p class="span-18 arrowIMG orangeheading">[@spring.message "officeinformation" /]</p>
        <p class="span-3 arrowIMG1 orangeheading last">[@spring.message "review&Submit" /]</p>
      </div>
      <div class="subcontent ">
      <form method="" action="" name="formname">
        <div class="fontBold">[@spring.message "addanewoffice" /]&nbsp;--&nbsp;<span class="orangeheading">[@spring.message "enterofficeInformation" /]</span></div>
        <div>[@spring.message "completethefieldsbelow.ThenclickPreview.ClickCanceltoreturntoAdminwithoutsubmittinginformation." /]</div>
        <div><span class="red">* </span>[@spring.message "fieldsmarkedwithanasteriskarerequired." /] </div>
           <p class="error" id="CreateNewOffice.error.message"></p>
        <p>&nbsp;&nbsp;</p>
        
          <p class="fontBold">[@spring.message "officedetails" /]</p>
        <div class="prepend-3  span-23 last">
        	<div class="span-24"><span class="span-3 rightAlign" id="CreateNewOffice.label.officeName"><span class="red">* </span>[@spring.message "officeName" /]</span>&nbsp;
    				<input id="CreateNewOffice.input.officeName" name="name" type="text"  />
  			</div>
            <div class="span-24"><span class="span-3 rightAlign" id="CreateNewOffice.label.shortName"><span class="red">* </span>[@spring.message "officeshortname" /]</span>&nbsp;
    				<input id="CreateNewOffice.input.shortName" name="name" type="text" size="5" />
  			</div>
            <div class="span-24"><span class="span-3 rightAlign" id="CreateNewOffice.label.officeLevel"><span class="red">* </span>[@spring.message "officetype" /]</span>&nbsp;
   					<select name="select" id="CreateNewOffice.input.officeLevel">
      					<option >[@spring.message "--select--" /]</option>
                        <option >[@spring.message "regionalOffice" /]</option>
                        <option >[@spring.message "divisionalOffice" /]</option>
                        <option >[@spring.message "areaOffice" /]</option>
                        <option >[@spring.message "branchOffice" /]</option>
				    </select>
			</div>
            <div class="span-24"><span class="span-3 rightAlign" id="CreateNewOffice.label.parentOffice"><span class="red">* </span>[@spring.message "parentOffice" /]</span>&nbsp;
   					<select name="select" id="CreateNewOffice.input.parentOffice">
      					<option >[@spring.message "--select--" /]</option>
                        <option >[@spring.message "headOffice(MifosHO)" /]</option>
				    </select>
			</div>
        </div>
        <p>&nbsp;&nbsp;</p>
        <p class="fontBold">[@spring.message "officeaddress" /]</p>
        <p>&nbsp;&nbsp;</p>
        <div class="prepend-3  span-23 last">
        	<div class="span-24"><span class="span-3 rightAlign" id="CreateNewOffice.label.address1">[@spring.message "address1" /]</span>&nbsp;
    				<input id="CreateNewOffice.input.address1" name="address1" type="text" />
  			</div>
            <div class="span-24"><span class="span-3 rightAlign" id="CreateNewOffice.label.address2">[@spring.message "address2" /]</span>&nbsp;
    				<input id="CreateNewOffice.input.address2" name="address2" type="text" />
  			</div>
  			<p>&nbsp;&nbsp;</p>
                  <div class="span-20 "><span class="span-3 rightAlign" id="CreateNewOffice.label.address3">[@spring.message "address3" /]</span>&nbsp;:
    				<input id="address3" name="address3" type="text" />
  			</div>
            <div class="span-20 "><span class="span-3 rightAlign" id="CreateNewOffice.label.city">[@spring.message "city/District" /]</span>&nbsp;:
    				<input id="cityDistrict" name="cityDistrict" type="text" />
  			</div>
            <div class="span-20 "><span class="span-3 rightAlign" id="CreateNewOffice.label.state">[@spring.message "state" /]</span>&nbsp;:
    				<input id="state" name="state" type="text" />
  			</div>
            <div class="span-20 "><span class="span-3 rightAlign" id="CreateNewOffice.label.country">[@spring.message "country" /]</span>&nbsp;:
    				<input id="country" name="country" type="text" />
  			</div>
            <div class="span-20 "><span class="span-3 rightAlign" id="CreateNewOffice.label.postalCode">[@spring.message "postalCode" /]</span>&nbsp;:
    				<input id="postalCode" name="postalCode" type="text" />
  			</div>
            <div class="span-20 "><span class="span-3 rightAlign" id="CreateNewOffice.label.phoneNumber">[@spring.message "telephone" /]</span>&nbsp;:
    				<input id="telephone" name="telephone" type="text" id="CreateNewOffice.input.phoneNumber" />
  			</div>
        </div>
        
        <div class="clear">&nbsp;</div>
        <hr />
        <div class="prepend-9">
          <input class="buttn" id="CreateNewOffice.button.preview" type="button" name="preview" value="Preview" onclick="#"/>
          <input class="buttn2" type="button" id="CreateNewOffice.button.cancel" name="cancel" value="Cancel" onclick="window.location='admin.ftl'"/>
        </div>
        <div class="clear">&nbsp;</div>
        </form>
      </div><!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
  [@mifos.footer/]