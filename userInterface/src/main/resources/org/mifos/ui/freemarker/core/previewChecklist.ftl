[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
   <!--  Main Content Begins-->
  <div class="content marginAuto">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-17 completeIMG silverheading">[@spring.message "organizationPreferences.previewChecklist.checklistinformation"/]</p>
        <p class="span-3 arrowIMG orangeheading last">[@spring.message "review&Submit"/]</p>
      </div>
      <div class="subcontent ">
      <p>&nbsp;&nbsp;</p>
        <form method="" action="" name="formname">
          <p class="font15"><span class="fontBold">[@spring.message "organizationPreferences.previewChecklist.addnewchecklist"/] </span>&nbsp;--&nbsp;<span class="orangeheading">[@spring.message "organizationPreferences.previewChecklist.reviewchecklistinformation"/]</span></p>
          <p>[@spring.message "organizationPreferences.previewChecklist.reviewtheinformationbelow.ClickSubmit"/]</p>
          <p>&nbsp;&nbsp;</p>
          <p class="fontBold">[@spring.message "organizationPreferences.previewChecklist.checklistdetails"/]</p>
          <p>&nbsp;&nbsp;</p>
          <p class="span-22">
          	<span class="fontBold">[@spring.message "organizationPreferences.previewChecklist.Name"/]: </span><span>&nbsp;</span><br />
            <span class="fontBold">[@spring.message "organizationPreferences.previewChecklist.type"/]: </span><span>&nbsp;</span><br />
            <span class="fontBold">[@spring.message "organizationPreferences.previewChecklist.displayedwhenmovingintoStatus"/] </span><span>&nbsp;</span><br />
          </p>
          <p>&nbsp;&nbsp;</p>
          <p>&nbsp;&nbsp;</p>
          <p class="span-22">
          	<div class="fontBold">[@spring.message "organizationPreferences.previewChecklist.items"/]</div>
            <ul style="list-style:none">
            	<li>[@spring.message "organizationPreferences.previewChecklist.items1"/]</li>
                <li>[@spring.message "organizationPreferences.previewChecklist.items2"/]</li>
            </ul>
            <div class="clear">&nbsp;</div>
            <div><input class="buttn2" type="button" name="edit" value="[@spring.message "organizationPreferences.editchecklist.editChecklistInformation"/]" onclick="location.href='viewEditChecklists.ftl'"/></div>
          </p>
          <hr />
          <div class="prepend-9">
            <input class="buttn" type="button" name="submit" value="[@spring.message "submit"/]" onclick="window.location='admin.ftl'"/>
            <input class="buttn2" type="button" name="cancel" value="[@spring.message "cancel"/]" onclick="window.location='admin.ftl'"/>
          </div>
          <div class="clear">&nbsp;</div>
        </form>
      </div>
      <!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
    [@mifos.footer/]