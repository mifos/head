[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
   <!--  Main Content Begins-->
  <div class="content marginAuto">
    <div class="borders span-22">
      <div class="subcontent ">
		<form method="post" action="deleteReportCategory.ftl" name="deleteReportCategory">
		<p>&nbsp;&nbsp;</p>
		<p class="font15">
			<span class="fontBold">[@spring.message "admin.definenewreportcategory" /]</span>&nbsp;--&nbsp;<span class="orangeheading">[@spring.message "reviewAndSubmit" /]</span>
		</p>
        <p>&nbsp;&nbsp;</p>
        <div>[@spring.message "editReportCategory.informationmessage" /]</div>
        [@mifos.showAllErrors "reportCategory.*"/]
        <div class="prepend-3  span-21 last">
            <div class="span-20 ">
            	<span class="span-4 rightAlign">[@spring.message "editReportCategory.categoryName" /]</span>
            	<span class="span-4">${reportCategory.name}</span>
  			</div>
          </div>
          <div class="clear">&nbsp;</div>
          <hr />
          <div class="prepend-9">
          	<input class="buttn" type="submit" name="submit" value="[@spring.message "submit"/]"/>
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