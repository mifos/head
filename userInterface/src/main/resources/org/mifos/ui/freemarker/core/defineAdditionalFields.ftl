[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Main Content Begins-->
  <div class="content marginAuto">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-15 arrowIMG orangeheading">[@spring.message "additionalfieldinformation" /]</p>
        <p class="span-3 arrowIMG1 orangeheading last">[@spring.message "review&Submit" /]</p>
      </div>
      <div class="subcontent ">
      <form method="" action="" name="formname">
      <p>&nbsp;&nbsp;</p>
        <div class="fontBold">[@spring.message "defineadditionalfields" /]&nbsp;--&nbsp;<span class="orangeheading">[@spring.message "enteradditionalfieldinformation" /]</span></div>
        <div>[@spring.message "completethefieldsbelow.ThenclickPreview.ClickCanceltoreturntoAdminwithoutsubmittinginformation." /]</div>
        <div><span class="red">* </span>[@spring.message "fieldsmarkedwithanasteriskarerequired." /] </div>
        <p>&nbsp;&nbsp;</p>
            <p class="fontBold">[@spring.message "additionalfielddetails" /] </p>
            <p>&nbsp;&nbsp;</p>
        <div class="prepend-3  span-21 last">
        	<div class="span-20 "><span class="span-3 rightAlign"><span class="red">* </span>[@spring.message "category" /]</span><span class="span-4">&nbsp;
   					<select name="select">
      					<option >[@spring.message "--Select--" /]</option>
                        <option >[@spring.message "personnel" /]</option>
                        <option >[@spring.message "office" /]</option>
                        <option >[@spring.message "client" /]</option>
                        <option >[@spring.message "group" /]</option>
                        <option >[@spring.message "center" /]</option>
                        <option >[@spring.message "loan" /]</option>
                        <option >[@spring.message "savings" /]</option>
				    </select></span>
			</div>
        	<div class="span-20 "><span class="span-3 rightAlign"><span class="red">* </span>[@spring.message "label" /]</span><span class="span-5">&nbsp;
    				<input type="text" id="define_additional_fields.input.labelName" /></span>
  			</div>
            <div class="span-20 "><span class="span-3 rightAlign">[@spring.message "mandatory" /]</span><span class="span-4">&nbsp;
    				<input type="checkbox" /></span>
  			</div>
            <div class="span-20 "><span class="span-3 rightAlign"><span class="red">* </span>[@spring.message "dataType" /]</span><span class="span-5">&nbsp;
   					<select name="select">
      					<option >[@spring.message "--Select--" /]</option>
                        <option >[@spring.message "numeric" /]</option>
                        <option >[@spring.message "text" /]</option>
				    </select></span>
			</div>
            <div class="span-20 "><span class="span-3 rightAlign">[@spring.message "defaultValue" /]</span><span class="span-5">&nbsp;
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