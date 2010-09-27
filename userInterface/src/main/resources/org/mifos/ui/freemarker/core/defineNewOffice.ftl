[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <script language="javascript">
  function getData(){
  alert("into function");
  if(document.getElementById("levelId").value="1" || document.getElementById("levelId").value="2" || document.getElementById("levelId").value="3" || document.getElementById("levelId").value="4" || document.getElementById("levelId").value="5"){
  alert("into if");
  return editOfficeInformation.submit();
  }
  }
  </script>
 <!--  Main Content Begins-->
  <div class="content definePageMargin">
    <div class="borders span-23">
      <div class="borderbtm span-23">
        <p class="span-18 arrowIMG orangeheading">[@spring.message "offices.defineNewOffice.officeinformation" /]</p>
        <p class="span-3 arrowIMG1 orangeheading last">[@spring.message "review&Submit" /]</p>
      </div>
      <div class="subcontent ">
      <form method="POST" action="editOfficeInformation.ftl" name="editOfficeInformation" id="editOfficeInformation">
        <div class="fontBold">[@spring.message "offices.defineNewOffice.addanewoffice" /]&nbsp;--&nbsp;<span class="orangeheading">[@spring.message "offices.defineNewOffice.enterofficeInformation" /]</span></div>
        <div>[@spring.message "offices.defineNewOffice.completethefieldsbelow.ThenclickPreview.ClickCanceltoreturntoAdminwithoutsubmittinginformation" /]</div>
        <div><span class="red">* </span>[@spring.message "fieldsmarkedwithanasteriskarerequired." /] </div>
           <p class="error" id="CreateNewOffice.error.message"></p>
        <p>&nbsp;&nbsp;</p>
          <p class="fontBold">[@spring.message "offices.defineNewOffice.officedetails" /]</p>
        <div class="prepend-3  span-23 last">
        	<div class="span-24"><span class="span-3 rightAlign" id="CreateNewOffice.label.officeName"><span class="red">* </span>[@spring.message "offices.defineNewOffice.officeName" /]</span>&nbsp;        	        	
			[@spring.bind "officeFormBean.name"/]        	
    				<span class="span-3"><input name="${spring.status.expression}" type="text" id="editoffice.input.officeName" value="${spring.status.value?default("")}"/>
  			</div>
            <div class="span-24"><span class="span-3 rightAlign" id="CreateNewOffice.label.shortName"><span class="red">* </span>[@spring.message "offices.defineNewOffice.officeshortname" /]</span>&nbsp;
             [@spring.bind "officeFormBean.officeShortName"/]
    				<input id="CreateNewOffice.input.shortName" name="${spring.status.expression}" value="${spring.status.value?if_exists}" type="text" size="5" />
  			</div>
            <div class="span-24"><span class="span-3 rightAlign" id="CreateNewOffice.label.officeLevel"><span class="red">* </span>[@spring.message "offices.defineNewOffice.officetype" /]</span>&nbsp;
   						[@spring.bind "officeFormBean.levelId"/]
   					<select id="${spring.status.expression}" name="${spring.status.expression}" onChange="getData();">
					        <option value="">${springMacroRequestContext.getMessage("--Select--")}</option>
					        [#if officeTypes?is_hash]
					            [#list officeTypes?keys as value]
					            <option value="${value?html}" [@spring.checkSelected value/]>${officeTypes[value]?html}</option>
					            [/#list]
					        [#else]
					            [#list oficeTypes as value]
					            <option value="${value?html}" [@spring.checkSelected value/]>${officeTypes[value]?html}</option>
					            [/#list]
					        [/#if]
					</select>  	
			</div>
            <div class="span-24"><span class="span-3 rightAlign" id="CreateNewOffice.label.parentOffice"><span class="red">* </span>[@spring.message "offices.defineNewOffice.parentOffice" /]</span>&nbsp;
   					[@spring.bind "officeFormBean.parentId"/]            						
   					<select id="${spring.status.expression}" name="${spring.status.expression}">
					        <option value="">${springMacroRequestContext.getMessage("--Select--")}</option>
					        [#if parentOffices?exists]
						        [#if parentOffices?is_hash]
						            [#list parentOffices?keys as value]
						            <option value="${value?html}" [@spring.checkSelected value/]>${parentOffices[value]?html}</option>
						            [/#list]
						        [#else]
						            [#list parentOffices as value]
						            <option value="${value?html}" [@spring.checkSelected value/]>${parentOffices[value]?html}</option>
						            [/#list]
						        [/#if]
					        [/#if]
					</select>
			</div>
        </div>
        <p>&nbsp;&nbsp;</p>
        <p class="fontBold">[@spring.message "offices.defineNewOffice.officeaddress" /]</p>
        <p>&nbsp;&nbsp;</p>
        <div class="prepend-3  span-23 last">
        	<div class="span-24"><span class="span-3 rightAlign" id="CreateNewOffice.label.address1">[@spring.message "offices.defineNewOffice.address1" /]</span>&nbsp;
        	[@spring.bind "officeFormBean.line1"/]
    				<input id="CreateNewOffice.input.address1" name="${spring.status.expression}" value="${spring.status.value?if_exists}" type="text" />
  			</div>
            <div class="span-24"><span class="span-3 rightAlign" id="CreateNewOffice.label.address2">[@spring.message "offices.defineNewOffice.address2" /]</span>&nbsp;
            [@spring.bind "officeFormBean.line2"/]
    				<input id="CreateNewOffice.input.address2" name="${spring.status.expression}" value="${spring.status.value?if_exists}" type="text" />
  			</div>
  			<p>&nbsp;&nbsp;</p>
                  <div class="span-20 "><span class="span-3 rightAlign" id="CreateNewOffice.label.address3">[@spring.message "offices.defineNewOffice.address3" /]</span>&nbsp;:
                  [@spring.bind "officeFormBean.line3"/]
    				<input id="address3" name="${spring.status.expression}" value="${spring.status.value?if_exists}" type="text" />
  			</div>
            <div class="span-20 "><span class="span-3 rightAlign" id="CreateNewOffice.label.city">[@spring.message "offices.defineNewOffice.city/District" /]</span>&nbsp;:
            [@spring.bind "officeFormBean.city"/]
    				<input id="cityDistrict" name="${spring.status.expression}" value="${spring.status.value?if_exists}" type="text" />
  			</div>
            <div class="span-20 "><span class="span-3 rightAlign" id="CreateNewOffice.label.state">[@spring.message "offices.defineNewOffice.state" /]</span>&nbsp;:
            [@spring.bind "officeFormBean.state"/]
    				<input id="state" name="${spring.status.expression}" value="${spring.status.value?if_exists}" type="text" />
  			</div>
            <div class="span-20 "><span class="span-3 rightAlign" id="CreateNewOffice.label.country">[@spring.message "offices.defineNewOffice.country" /]</span>&nbsp;:
            [@spring.bind "officeFormBean.country"/]
    				<input id="country" name="${spring.status.expression}" value="${spring.status.value?if_exists}" type="text" />
  			</div>
            <div class="span-20 "><span class="span-3 rightAlign" id="CreateNewOffice.label.postalCode">[@spring.message "offices.defineNewOffice.postalCode" /]</span>&nbsp;:
            [@spring.bind "officeFormBean.zip"/]
    				<input id="postalCode" name="${spring.status.expression}" value="${spring.status.value?if_exists}" type="text" />
  			</div>
            <div class="span-20 "><span class="span-3 rightAlign" id="CreateNewOffice.label.phoneNumber">[@spring.message "offices.defineNewOffice.telephone" /]</span>&nbsp;:
            [@spring.bind "officeFormBean.phoneNumber"/]
    				<input id="telephone" name="${spring.status.expression}" value="${spring.status.value?if_exists}" type="text" id="CreateNewOffice.input.phoneNumber" />
  			</div>
        </div>
        
        <div class="clear">&nbsp;</div>
        <hr />
        <div class="prepend-9">
          <input class="buttn" id="CreateNewOffice.button.preview" type="submit" name="preview" value="[@spring.message "preview"/]"/>
          <input class="buttn2" type="button" id="CreateNewOffice.button.cancel" name="submit" value="[@spring.message "cancel"/]"/>
        </div>
        <div class="clear">&nbsp;</div>
        </form>
      </div><!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
  [@mifos.footer/]