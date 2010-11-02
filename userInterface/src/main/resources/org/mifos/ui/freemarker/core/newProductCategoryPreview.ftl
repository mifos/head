[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
    <!--  Main Content Begins-->
  <div class="content marginAuto">
    <div class="borders span-22">
      <div class="borderbtm span-22">
     
        <p class="span-15 completeIMG silverheading">[@spring.message "manageProducts.defineNewCategory.productcategoryinformation"/]</p>
        <p class="span-3 arrowIMG orangeheading last">[@spring.message "reviewAndSubmit"/]</p>
      </div>
      <div class="subcontent ">
        <form method="POST" action="newProductCategoryPreview.ftl" name="newCategoryPreview">
          <p class="font15"><span class="fontBold">[@spring.message"admin.definenewcategory"/]</span>&nbsp;--&nbsp;<span class="orangeheading">[@spring.message "reviewAndSubmit"/]</span></p>
          <div>[@spring.message "manageProduct.editCategory.PreviewTheFieldsBelow.ThenClickSubmit"/]</div>
          <p class="clear">&nbsp; </p>
          <div class="fontBold">[@spring.message "manageProducts.editCategory.categoryDetails"/] </div>
          [@mifos.showAllErrors "formBean.*"/]
          <p class="span-22">          
          	<span class="fontBold">[@spring.message "manageProducts.defineNewCategory.productType"/] </span><span>&nbsp;:[#switch formBean.productTypeId]
                	[#case "1"]
                		<span></span>&nbsp;<span>[@spring.message "Loan-Loan"/]</span>
                	[#break]
                	[#case "2"]
                		<span></span>&nbsp;<span>[@spring.message "Savings-Savings"/]</span>
                	[#break]
                [/#switch] [@spring.bind "formBean.productTypeId"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/></span><br />
            <span class="fontBold">
            	[@spring.message "manageProducts.editCategory.categoryName"/]
            </span>
            <span>&nbsp;:${formBean.productCategoryName}
            	[@spring.bind "formBean.productCategoryName"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
            </span><br />
          </p>
          <div class="fontBold">[@spring.message "manageProducts.editCategory.description"/]</div>
          <p class="span-22">
            <span>&nbsp;${formBean.productCategoryDesc}[@spring.bind "formBean.productCategoryDesc"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>[@spring.showErrors "<br />"/]</span><br />
          </p>
          <p class="span-22">
            <span><input class="buttn2" type="submit" name="EDIT" value="[@spring.message "manageProducts.editCategory.editcategoryinformation"/]"/></span>
          </p>

          
          <div class="clear">&nbsp;</div>
          <hr />
          <div class="prepend-9">
            <input class="buttn" type="submit" name="SUBMIT" value="[@spring.message "submit"/]"/>
            <input class="buttn2" type="submit" name="CANCEL" value="[@spring.message "cancel"/]"/>
          </div>
          <div class="clear">&nbsp;</div>
        </form>
      </div>
      <!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
  [@mifos.footer/]