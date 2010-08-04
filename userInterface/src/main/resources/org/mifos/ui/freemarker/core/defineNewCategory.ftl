[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
   <!--  Main Content Begins-->
  <div class="content marginAuto">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-15 arrowIMG orangeheading">[@spring.message "productcategoryinformation" /]</p>
      
      </div>
      <div class="subcontent ">
        <form method="" action="" name="formname">
        <p>&nbsp;&nbsp;</p>
         <p class="font15"><span class="fontBold">[@spring.message "definenewcategory" /]</span>&nbsp;--&nbsp;<span class="orangeheading">[@spring.message "enterProductcategoryinformation" /]</span></p>
         <p>&nbsp;&nbsp;</p>
          <div>[@spring.message "completethefieldsbelow.ThenclickPreview.ClickCanceltoreturntoAdminwithoutsubmittinginformation." /]</div>
          <div><span class="red">* </span>[@spring.message "fieldsmarkedwithanasteriskarerequired." /] </div>
          <p>&nbsp;&nbsp;</p>
                  <p class="fontBold">[@spring.message "categoryDetails" /]</p>
          <div class="prepend-3  span-21 last">
          	<div class="span-20 "><span class="span-4 rightAlign"><span class="red">* </span>[@spring.message "productType" /]</span><span class="span-4">
   					<select name="select">
      					<option >[@spring.message "--Select--" /]</option>
                        <option >[@spring.message "loans" /]</option>
                        <option >[@spring.message "savings" /]</option>
				    </select></span>
			</div>
			<p>&nbsp;&nbsp;</p>
            <div class="span-20 "><span class="span-4 rightAlign"><span class="red">* </span>[@spring.message "categoryName" /]</span><span class="span-4">
    				<input type="text" /></span>
  			</div>
  			<p>&nbsp;&nbsp;</p>
            <div class="span-20 "><span class="span-4 rightAlign">[@spring.message "categoryDescription" /]</span><span>
    				<textarea cols="50" rows="6"></textarea></span>
  			</div>
        	
          </div>
          <div class="clear">&nbsp;</div>
          <hr />
          <div class="prepend-9">
            	<input class="buttn" type="button" name="preview" value="Preview" onclick="#"/>
            	<input class="buttn2" type="button" name="cancel" value="Cancel" onclick="window.location='admin.ftl'"/>
          </div>
          <div class="clear">&nbsp;</div>
        </form>
      </div>
      <!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
    [@mifos.footer/]