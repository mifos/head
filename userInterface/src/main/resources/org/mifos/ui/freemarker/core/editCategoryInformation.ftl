[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Left Sidebar Begins-->
  <div class="sidebar ht600">
  [#include "adminLeftPane.ftl" ]
  </div> 
  <!--  Left Sidebar Ends-->
   <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
  	<form method="" action="" name="formname">
    <div class="span-24">
  		<div class="bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin" /]</a>&nbsp;/&nbsp;<a href="viewProductCategory.ftl">[@spring.message "viewProductCategories" /]</a>&nbsp;/&nbsp;<a href="#">[@spring.message "categoryName1" /]</a></div>
        <div class="clear">&nbsp;</div>
    	<p class="font15"><span class="fontBold">[@spring.message "categoryName" /]</span>&nbsp;-&nbsp;<span class="orangeheading">[@spring.message "editcategoryinformation" /]</span></p>
        <div>[@spring.message "Editthefieldsbelow." /] </div>
        <div><span class="red">* </span>[@spring.message "fieldsmarkedwithanasteriskarerequired." /] </div>
        <p class="error" id="error1"></p>
        <p class="fontBold">[@spring.message "categorydetails" /]</p>
        <div class="prepend-3 span-22 last">
        	<div class="span-22"><span class="span-4 rightAlign"><span class="red"> * </span>[@spring.message "categoryName" /]</span><span class="span-4"><input type="text" /></span></div>
            <div class="span-22"><span class="span-4 rightAlign"><span class="red"> * </span>[@spring.message "description" /]</span><span><textarea cols="50" rows="6"></textarea></span></div>
        </div>
        <div class="prepend-3 span-22 last">
            <span class="span-4 rightAlign"><span class="red"> * </span>[@spring.message "status1" /]</span><span class="span-4"><select name="status">
            			<option>[@spring.message "active"/]</option>
                        <option>[@spring.message "inactive"/]</option>
                    </select></span>
        </div>
        <div class="clear">&nbsp;</div>
        <hr />
        <div class="prepend-10">
            <input class="buttn" type="button" name="preview" value="Preview" onclick="#"/>
            <input class="buttn2" type="button" name="cancel" value="Cancel" onclick="#"/>
        </div>
	</div>
   	</form> 
  </div><!--Main Content Ends-->
  [@mifos.footer/]