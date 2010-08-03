[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
   <!--  Main Content Begins-->
  <div class="content marginAuto">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-17 arrowIMG orangeheading">[@spring.message "fundinformation"/]</p>
        <p class="span-3 arrowIMG1 orangeheading last">[@spring.message "review&Submit"/]</p>
      </div>
      <div class="subcontent ">
        <form method="POST" action="newFundPreview.ftl" name="organizationPreferences.definenewfund.defineNewFund">
          <p class="font15"><span class="fontBold">[@spring.message "organizationPreferences.definenewfund.defineanewfund"/]</span>&nbsp;--&nbsp;<span class="orangeheading">[@spring.message "organizationPreferences.definenewfund.enterfundinformation"/]</span></p>
          <div>[@spring.message "organizationPreferences.definenewfund.completethefieldsbelow.ThenclickPreview.ClickCanceltoreturntoAdminpagewithoutsubmittinginformation"/]</div>
          <div><span class="red">* </span>[@spring.message "fieldsmarkedwithanasteriskarerequired"/] </div>
          <p class="error"></p>
          <p class="fontBold">[@spring.message "organizationPreferences.definenewfund.funddetails"/] </p>
          <div class="prepend-3  span-21 last">
          	<div class="span-20 "><span class="span-3 rightAlign"><span class="red">* </span>[@spring.message "organizationPreferences.definenewfund.name"/]</span><span class="span-5">&nbsp;
          	[@spring.bind "formBean.name"/]          	
    				<input type="text" name="${spring.status.expression}" id="${spring.status.expression}" value="${spring.status.value?if_exists}" />   				
    				</span>
  			</div>
        	<div class="span-20 "><span class="span-3 rightAlign"><span class="red">* </span>[@spring.message "organizationPreferences.definenewfund.fundCode"/]</span><span class="span-5">&nbsp;       	
        			[#-- <select name="code.id" >
      					<option name="">[@spring.message "--Select--"/]</option>
                        <option value="1" >00</option>
                        <option value="2" >01</option>
                        <option value="3" >02</option>
                        <option value="4" >03</option>
                        <option value="5" >04</option>
				    </select> --]				    
				    [@mifos.formSingleSelectWithPrompt "formBean.code.id", code,"--select one--" /]
				    </span>
			</div>
          </div>
          <div class="clear">&nbsp;</div>
          <hr />
          <div class="prepend-9">
            	<input class="buttn" type="submit" name="preview" value="Preview"/>
            	<input class="buttn2" type="submit" name="cancel" value="Cancel" />
          </div>
          <div class="clear">&nbsp;</div>
        </form>
      </div>
      <!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
  [@mifos.footer/]