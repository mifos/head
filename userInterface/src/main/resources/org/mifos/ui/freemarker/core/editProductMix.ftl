[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[#import "macros.ftl" as mifosmacros]
[@mifos.header "title" /]
[@mifos.topNavigationNoSecurity currentTab="Admin" /]

 <script type="text/javascript">
 function addOption(root, text, value)
{
  var newOpt = new Option(text, value);
  var rootLength = root.length;
  root.options[rootLength] = newOpt;
}

function deleteOption(root, index)
{ 
  var rootLength= root.length;
  if(rootLength>0)
  {
    root.options[index] = null;
  }
}

function moveOptions(root, destination)
{
  
  var rootLength= root.length;
  var rootText = new Array();
  var rootValues = new Array();
  var rootCount = 0;
  
  var i; 
  for(i=rootLength-1; i>=0; i--)
  {
    if(root.options[i].selected)
    {
      rootText[rootCount] = root.options[i].text;
      rootValues[rootCount] = root.options[i].value;
      deleteOption(root, i);
      rootCount++;
    }
  }  
  for(i=rootCount-1; i>=0; i--)
  {
    addOption(destination, rootText[i], rootValues[i]);
  }  
}

function selectAllOptions(outSel)
{
	if(null != outSel) {
	 	var selLength =outSel.length;
		outSel.multiple=true;
		for(i=selLength-1; i>=0; i--)
		{
			outSel.options[i].selected=true;
		}
	}
}	
</script>
  
  <div class="container">&nbsp;
  <!--  Main Content Begins-->
  <span id="page.id" title="editProductsMix" />
  <div class="content marginAuto">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-15 arrowIMG orangeheading">[@spring.message "manageProducts.defineProductmix.productmixinformation"/]</p>
        <p class="span-3 arrowIMG1 orangeheading last">[@spring.message "review&Submit"/]</p>
      </div>
      <div class="subcontent ">
          <p class="font15"><span class="fontBold">[@spring.message "manageProducts.defineProductmix.addanewproductmix"/]</span>&nbsp;--&nbsp;<span class="orangeheading"> [@spring.message "manageProducts.defineProductmix.enterproductmixinformation"/]</span></p>
          <div>[@spring.message "manageProducts.defineProductmix.completethefieldsbelow.ThenclickPreview.ClickCanceltoreturntoAdminwithoutsubmittinginformation"/] </div>
          <div><span class="red">* </span>[@spring.message "manageProducts.defineProductmix.fieldsmarkedwithanasteriskarerequired"/] </div>
          <p class="error" id="error1"></p>
          <p class="fontBold">[@spring.message "manageProducts.defineProductmix.productmixdetails"/] </p>
        <form name="productsmixform" id="productsmixform" method="post" action="editProductMix.ftl">
        	<div class="prepend-3  span-21 last">
        		<div class="span-20 ">
	        		<span class="span-5 rightAlign">
	        			<span class="red">* </span>[@spring.message "manageProducts.defineProductmix.producttype"/]&nbsp;:&nbsp;
	        		</span>
        			<span class="span-5">
	   					[@spring.bind "formBean.productTypeId" /]
					    <select id="${spring.status.expression}" name="${spring.status.expression}" onchange="return productsmixform.submit();" disabled>
					        <option value="" [@spring.checkSelected ""/]>${springMacroRequestContext.getMessage("--Select--")}</option>
					        [#if formBean.productTypeOptions?is_hash]
					            [#list formBean.productTypeOptions?keys as value]
					            <option value="${value?html}"[@spring.checkSelected value/]>${springMacroRequestContext.getMessage(formBean.productTypeOptions[value]?html)}</option>
					            [/#list]
					        [#else]
					            [#list formBean.productTypeOptions as value]
					            <option value="${value?html}"[@spring.checkSelected value/]>${springMacroRequestContext.getMessage(value?html)}</option>
					            [/#list]
					        [/#if]
					    </select>
				    </span>
				</div>
				
				<div class="span-20 ">
					<span class="span-5 rightAlign">
						<span class="red">* </span>[@spring.message "manageProducts.defineProductmix.productinstancename"/]&nbsp;:&nbsp;
					</span>
					<span class="span-7">
						[@spring.bind "formBean.productId" /]
					    <select id="${spring.status.expression}" name="${spring.status.expression}" onchange="return productsmixform.submit();" disabled>
					        <option value="" [@spring.checkSelected ""/]>${springMacroRequestContext.getMessage("--Select--")}</option>
					        [#if formBean.productNameOptions?is_hash]
					            [#list formBean.productNameOptions?keys as value]
					            <option value="${value?html}"[@spring.checkSelected value/]>${formBean.productNameOptions[value]?html}</option>
					            [/#list]
					        [#else]
					            [#list formBean.productNameOptions as value]
					            <option value="${value?html}"[@spring.checkSelected value/]>${value?html}</option>
					            [/#list]
					        [/#if]
					    </select>   					
				    </span>
				</div>
				
				<div class="span-20 last">
	          		<div class="span-20">
	          			<span class="pull-3 span-8 rightAlign">[@spring.message "manageProducts.defineProductmix.removenotallowedproducts"/]&nbsp;:&nbsp;</span>
	            		<span class="span-12 ">
	                		<span class="span-9">[@spring.message "manageProducts.defineProductmix.clickonaproductintherightboxtoselect.ThenclickRemove"/]</span>
	                    	<span class="span-4">
								[@spring.formMultiSelect "formBean.notAllowed", formBean.notAllowedProductOptions, "class=listSize" /]	                    		
	            			</span>
	                    	<span class="span-3">
	                    		<br />
	                    		<input class="buttn2" name="add" type="button" value="[@spring.message "add"/]" onclick="moveOptions(this.form.notAllowed, this.form.allowed);"/>
	                    		<br /><br />
								<input class="buttn2" name="remove" type="button" value="[@spring.message "remove"/]" onclick="moveOptions(this.form.allowed, this.form.notAllowed);"/>
							</span>
							<span class="span-4">
								[@spring.formMultiSelect "formBean.allowed", formBean.allowedProductOptions, "class=listSize" /]
	            			</span>
	               		</span>
	            	</div>
	          	</div>
				<div class="clear">&nbsp;</div>
		
				<hr />
		        <div class="prepend-10">
		            <input class="buttn" type="submit" id="holiday.button.preview" name="preview"  value="Preview" onclick="selectAllOptions(this.form.notAllowed);selectAllOptions(this.form.allowed);" />
		            <input class="buttn2" type="submit" id="CANCEL" name="CANCEL" value="[@spring.message "cancel"/]"/>
		        </div>						
        	</div>
		</form>
      <!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
  <div class="footer">&nbsp;</div>
</div>
<!--Container Ends-->
[@mifos.footer/]