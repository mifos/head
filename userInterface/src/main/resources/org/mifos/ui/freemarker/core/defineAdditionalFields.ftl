[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Main Content Begins-->
  <div class="content definePageMargin">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-15 arrowIMG orangeheading">[@spring.message "datadisplayandrules.defineAdditionalFields.additionalfieldinformation" /]</p>
        <p class="span-3 arrowIMG1 orangeheading last">[@spring.message "review&Submit" /]</p>
      </div>
      <div class="subcontent ">
      <form method="" action="" name="formname">
      <p>&nbsp;&nbsp;</p>
        <div class="fontBold">[@spring.message "datadisplayandrules.defineAdditionalFields.defineadditionalfields" /]&nbsp;--&nbsp;<span class="orangeheading">[@spring.message "datadisplayandrules.defineAdditionalFields.enteradditionalfieldinformation" /]</span></div>
        <div>[@spring.message "completethefieldsbelow.ThenclickPreview.ClickCanceltoreturntoAdminwithoutsubmittinginformation" /]</div>
        <div><span class="red">* </span>[@spring.message "fieldsmarkedwithanasteriskarerequired." /] </div>
        <p>&nbsp;&nbsp;</p>
            <p class="fontBold">[@spring.message "datadisplayandrules.defineAdditionalFields.additionalfielddetails" /] </p>
            <p>&nbsp;&nbsp;</p>
        <div class="prepend-3  span-21 last">
        	<div class="span-20 "><span class="span-3 rightAlign"><span class="red">* </span>[@spring.message "datadisplayandrules.defineAdditionalFields.category" /]</span><span class="span-4">&nbsp;
   					<select name="select">
      					<option >--Select--</option>
                        <option >personnel</option>
                        <option >office</option>
                        <option >client</option>
                        <option >group</option>
                        <option >center</option>
                        <option >loan</option>
                        <option >savings</option>
				    </select></span>
			</div>
        	<div class="span-20 "><span class="span-3 rightAlign"><span class="red">* </span>[@spring.message "datadisplayandrules.defineAdditionalFields.label" /]</span><span class="span-5">&nbsp;
    				<input type="text" id="define_additional_fields.input.labelName" /></span>
  			</div>
            <div class="span-20 "><span class="span-3 rightAlign">[@spring.message "datadisplayandrules.editadditionalfields.mandatory" /]</span><span class="span-4">&nbsp;
    				<input type="checkbox" /></span>
  			</div>
            <div class="span-20 "><span class="span-3 rightAlign"><span class="red">* </span>[@spring.message "datadisplayandrules.defineAdditionalFields.dataType" /]</span><span class="span-5">&nbsp;
   					<select name="select">
      					<option >--Select--</option>
                        <option >datadisplayandrules.defineAdditionalFields.numeric</option>
                        <option >datadisplayandrules.defineAdditionalFields.text</option>
				    </select></span>
			</div>
            <div class="span-20 "><span class="span-3 rightAlign">[@spring.message "datadisplayandrules.defineAdditionalFields.defaultValue" /]</span><span class="span-5">&nbsp;
    				<input type="text" /></span>
  			</div>
            
        </div>
        <div class="clear">&nbsp;</div>
        <hr />
        <div class="prepend-9">
          <input class="buttn" type="button" id="define_additional_fields.button.preview" name="preview" value="Preview" onclick="#"/>
          <input class="buttn2" type="button" id="define_additional_fields.button.cancel" name="cancel" value="Cancel" onclick="window.location='admin.ftl'"/>
        </div>
        <div class="clear">&nbsp;</div>
        </form>
      </div><!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
  [@mifos.footer/]