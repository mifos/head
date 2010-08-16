[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  <div class="container">&nbsp;
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Left Sidebar Begins-->
  <div class="sidebar ht600">
  [#include "adminLeftPane.ftl" ]
  </div> 
  <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
  	<form method="POST" action="categoryPreview.ftl" name="formname">
    <div class="span-24">
  		[@mifos.crumbs breadcrumbs/]
        <div class="clear">&nbsp;</div>
    	<p class="font15">
    		<span class="fontBold">[@spring.message "manageProducts.editCategory.categoryName"/]</span>&nbsp;-&nbsp;<span class="orangeheading">[@spring.message "manageProducts.editCategory.editcategoryinformation"/]</span></p>
        <div>[@spring.message "manageProduct.editCategory.PreviewTheFieldsBelow.ThenClickSubmit"/]</div>
        <div class="clear">&nbsp;</div>
        <p class="fontBold">[@spring.message "manageProducts.editCategory.categoryDetails"/]</p>
        <p class="span-22 last">
        	<span class="span-4 fontBold">[@spring.message "manageProducts.editCategory.categoryName"/] &nbsp;:</span>
        	<span class="span-4">
        	[@spring.bind "formBean.productCategoryName"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>${formBean.productCategoryName}
        	</span>   	
		</p>
        <p class="fontBold span-22 last">[@spring.message "manageProducts.editCategory.description"/] &nbsp;:</p>
        <div class="span-22 last">
        	[@spring.bind "formBean.productCategoryDesc"/]
            <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>${formBean.productCategoryDesc}
        </div>
        <p class="span-22 last">
        	<span class="span-4 fontBold">[@spring.message "manageProducts.editCategory.status1"/]&nbsp;:</span>
        	<span class="span-4">
        		[@spring.bind "formBean.productCategoryStatusId"/]
            	<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
				[#switch formBean.productCategoryStatusId]
                	[#case "1"]
                		<span>[@spring.message "active"/]</span>
                	[#break]
                	[#case "2"]
                		<span>[@spring.message "inactive"/]</span>
                	[#break]
                [/#switch]
            </span>
            <br />
        </p>
       	[@spring.bind "formBean.productType"/]
       	<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
       	[@spring.bind "formBean.productTypeId"/]
       	<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
       	[@spring.bind "formBean.globalPrdCategoryNum"/]
       	<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
        <div class="clear">&nbsp;</div>
        <div><input class="buttn2" type="submit" name="EDIT" value="[@spring.message "manageProducts.editCategory.editcategoryinformation"/]"/></div>
        <div class="clear">&nbsp;</div>
        <hr />
        <div class="prepend-10">
            <input class="buttn" type="submit" name="SUBMIT" value="[@spring.message "submit"/]"/>
            <input class="buttn2" type="submit" name="CANCEL" value="[@spring.message "cancel"/]"/>
        </div>
	</div>
   	</form> 
  </div><!--Main Content Ends-->
  </div><!--Container Ends-->
  [@mifos.footer/]