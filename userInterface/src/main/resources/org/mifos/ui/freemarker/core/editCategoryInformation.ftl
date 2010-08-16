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
  	<form method="POST" action="editCategoryInformation.ftl" name="editCategoryInformation">
  	[#assign breadcrumb = {"admin":"AdminAction.do?method=load", "admin.viewproductcategories":"viewProductCategories.ftl",formBean.productCategoryName:""}/] 	
    [@mifos.crumbpairs breadcrumb/]  	  	
    <div class="span-24">  		
        <div class="clear">&nbsp;</div>
    	<p class="font15">
    		<span class="fontBold">[@spring.bind "formBean.productCategoryName"/]${spring.status.value?default("")}</span>&nbsp;-&nbsp;
    		<span class="orangeheading">
    			[@spring.message "manageProducts.editCategory.editcategoryinformation" /]
    		</span>
    	</p>
        <div>
        	[@spring.message "manageProducts.editCategory.edittheFieldsBelow" /] 
        </div>
        <div>
        	<span class="red">* </span>
        	[@spring.message "fieldsmarkedwithanasteriskarerequired." /]
        </div>
        [#-- [@mifos.showAllErrors "formBean.*"/] --]
        [@spring.bind "formBean"/]
       [@mifos.showAllErrors "formBean.*"/] 
        <p class="fontBold">
        	[@spring.message "manageProducts.editCategory.categoryDetails" /]
        </p>
        <div class="prepend-3 span-22 last">        	
        	<div class="span-22">
        		<span class="span-4 rightAlign">
        			<span class="red"> * </span>
        			[@spring.message "manageProducts.editCategory.categoryName" /]
        		</span>
        		<span class="span-4">
        		[@spring.bind "formBean.productCategoryName"/]
        			<input type="text" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
        		</span>
        	</div>
            <div class="span-22">
            	<span class="span-4 rightAlign">
            		<span class="red"> * </span>
            		[@spring.message "manageProducts.editCategory.description" /]
            	</span>
            	<span>
            	[@spring.bind "formBean.productCategoryDesc"/]
            		<textarea cols="50" rows="6" name="${spring.status.expression}">${spring.status.value?default("")}</textarea>            	
            	</span>
            </div>
        </div>
        <div class="prepend-3 span-22 last">
            <span class="span-4 rightAlign">
            	<span class="red"> * </span>
            	[@spring.message "manageProducts.editCategory.status1" /]&nbsp;:
            </span>
            <span class="span-4">
            [@spring.bind "formBean.productType"/]
            <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
            [@spring.bind "formBean.productTypeId"/]
            <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
            [@spring.bind "formBean.globalPrdCategoryNum"/]
            <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
            [#assign statusTypes={"1":"active","2":"inactive"}/]
            [@spring.bind "formBean.productCategoryStatusId"/]
            <select id="${spring.status.expression}" name="${spring.status.expression}">
					        <option value="" [@spring.checkSelected ""/]>${springMacroRequestContext.getMessage("--Select--")}</option>
					        [#if statusTypes?is_hash]
					            [#list statusTypes?keys as value]
					            <option value="${value?html}"[@spring.checkSelected value/]>${springMacroRequestContext.getMessage(statusTypes[value]?html)}</option>
					            [/#list]
					        [#else]
					            [#list statusTypes as value]
					            <option value="${value?html}"[@spring.checkSelected value/]>${springMacroRequestContext.getMessage(value?html)}</option>
					            [/#list]
					        [/#if]
			</select>
            </span>
        </div>
        <div class="clear">&nbsp;</div>
        <hr />
        <div class="prepend-10">
            <input class="buttn" type="submit" name="PREVIEW" value="[@spring.message "preview"/]"/>
            <input class="buttn2" type="submit" name="CANCEL" value="[@spring.message "cancel"/]"/>
        </div>
	</div>
   	</form> 
  </div><!--Main Content Ends-->
  [@mifos.footer/]