[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Left Sidebar Begins-->
  <div class="sidebar htTotal">
  [#include "adminLeftPane.ftl" ]
  </div> 
  <!--  Left Sidebar Ends-->
   <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
  	<form method="" action="" name="formname">
  	<p class="bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin" /]</a>&nbsp;/&nbsp;<a href="viewAdditionalFields.ftl">[@spring.message "viewAdditionalFields" /]</a>&nbsp;/&nbsp;<a href="#">[@spring.message "personnel" /]</a></p>
    <p class="font15"><span class="fontBold">[@spring.message "datadisplayandrules.editadditionalfields" /]</span>&nbsp;-&nbsp;<span class="orangeheading">[@spring.message "datadisplayandrules.editadditionalfields.enteradditionalfieldinformation" /]</span></p>
    <div>[@spring.message "datadisplayandrules.editadditionalfields.completethefieldsbelow.ThenclickPreview.ClickCanceltoreturntoAdminwithoutsubmittinginformation." /] </div>
    <p><span class="red">* </span>[@spring.message "fieldsmarkedwithanasteriskarerequired." /]</p>
    <p class="error"></p>
    <p class="fontBold">[@spring.message "datadisplayandrules.editadditionalfields.additionalfielddetails" /]</p>
    <p>&nbsp;&nbsp;</p>
    <div class="prepend-1 span-21 last">
        	<div class="span-20 prepend-3 "><span class="span-3 rightAlign"><span class="red">* </span>[@spring.message "datadisplayandrules.editadditionalfields.category" /]</span><span class="span-4">&nbsp;
   					<select name="select" disabled="disabled">
      					<option >[@spring.message "--Select--" /]</option>
                        <option >[@spring.message "datadisplayandrules.editadditionalfields.personnel" /]</option>
                        <option >[@spring.message "datadisplayandrules.editadditionalfields.office" /]</option>
                        <option >[@spring.message "datadisplayandrules.editadditionalfields.client" /]</option>
                        <option >[@spring.message "datadisplayandrules.editadditionalfields.group" /]</option>
                        <option >[@spring.message "datadisplayandrules.editadditionalfields.center" /]</option>
                        <option >[@spring.message "datadisplayandrules.editadditionalfields.loan" /]</option>
                        <option >[@spring.message "offices.viewOfficeDetails.additionalinformation" /]</option>
				    </select></span>
			</div>
        	<div class="span-20 prepend-3 "><span class="span-3 rightAlign"><span class="red">* </span>[@spring.message "datadisplayandrules.editadditionalfields.label" /]</span>&nbsp;<span class="span-4">
    				<input type="text" /></span>
  			</div>
            <div class="span-20 prepend-3"><span class="span-3 rightAlign">[@spring.message "datadisplayandrules.editadditionalfields.mandatory" /]</span>&nbsp;<span class="span-4">
    				<input type="checkbox" /></span>
  			</div>
            <div class="span-20 prepend-3"><span class="span-3 rightAlign"><span class="red">* </span>[@spring.message "datadisplayandrules.editadditionalfields.dataType" /]</span>&nbsp;<span class="span-4">
   					<select name="select" disabled="disabled">
      					<option >[@spring.message "--Select--" /]</option>
                        <option >[@spring.message "datadisplayandrules.editadditionalfields.numeric" /]</option>
                        <option >[@spring.message "datadisplayandrules.editadditionalfields.text" /]</option>
				    </select></span>
			</div>
            <div class="span-20 prepend-3"><span class="span-3 rightAlign">[@spring.message "datadisplayandrules.editadditionalfields.defaultValue" /]</span>&nbsp;<span class="span-4">
    				<input type="text" /></span>
  			</div>
            <div class="clear">&nbsp;</div>
            <hr />
            <div class="prepend-9">
          		<input class="buttn" type="button" name="preview" id="edit_additional_fields.button.preview" value="Preview" onclick="#"/>
          		<input class="buttn2" type="button" name="cancel" id="edit_additional_fields.button.cancel" value="Cancel" onclick="location.href='admin.ftl'"/>
    		</div>
            
    </div>

   	</form> 
  </div><!--Main Content Ends-->
  [@mifos.footer/]