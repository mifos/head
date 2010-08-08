[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]  
  <!--  Container Begins-->
<div>
	<!--  Main Content Begins-->
  <div class="content marginAuto">    
      <div class="borderbtm span-22">
        <p class="span-15 completeIMG silverheading">[@spring.message "fundinformation"/]</p>
        <p class="span-3 arrowIMG orangeheading last">[@spring.message "review&Submit"/]</p>
      </div>
      <div class="subcontent ">
        <form method="post" action="newFundPreview.ftl" name="newFundPreview">
          <p class="font15"><span class="fontBold">[@spring.message "defineanewfund"/]</span>&nbsp;--&nbsp;<span class="orangeheading">[@spring.message "review&Submit"/]</span></p>
          <div>[@spring.message "reviewtheinformationbelow.ClickSubmitifyouaresatisfiedorclickEdittomakechanges.ClickCanceltoreturntoAdminpagewithoutsubmittinginformation"/]</div>
          <p class="clear">&nbsp; </p>
          <div class="fontBold">[@spring.message "funddetails"/] </div>
          <p class="span-22">
            [@spring.bind "formBean.id"/]
            <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
            [@spring.bind "formBean.name"/]
            <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
            [@spring.bind "formBean.code.value"/]
            <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
            [@spring.bind "formBean.code.id"/]
            <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
            
            [@spring.bind "formBean.name"/]
          	<span class="fontBold">[@spring.message "name"/] </span><span>&nbsp;${spring.status.value?default("")}</span><br />
          	
          	[@spring.bind "formBean.code.value"/]
            <span class="fontBold">[@spring.message "fundcode"/] </span><span>&nbsp;${spring.status.value?default("")}</span><br />
          </p>
          
          <p class="span-22">
            <span><input class="buttn2" type="submit" name="EDIT" value="[@spring.message "organizationPreferences.fundpreview.editFundInformation"/]"/></span>
          </p>          
          <div class="clear">&nbsp;</div>
          <hr />
          <div class="prepend-9">
            <input class="buttn" type="submit" name="SUBMIT" value="Submit"/>
            <input class="buttn2" type="submit" name="CANCEL" value="Cancel"/>
          </div>
          <div class="clear">&nbsp;</div>
        </form>
      </div>
      <!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->  
</div>
<!--Container Ends-->
[@mifos.footer/]