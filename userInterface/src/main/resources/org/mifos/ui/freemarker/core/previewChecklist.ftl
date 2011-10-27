[#ftl]
[#--
* Copyright (c) 2005-2011 Grameen Foundation USA
*  All rights reserved.
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
*  implied. See the License for the specific language governing
*  permissions and limitations under the License.
*
*  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
*  explanation of the license and how it is applied.
--]
[@layout.header "title" /]
  [@widget.topNavigationNoSecurity currentTab="Admin" /]
   <!--  Main Content Begins-->
  <div class="content marginAuto">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-17 completeIMG silverheading">[@spring.message "organizationPreferences.previewChecklist.checklistinformation"/]</p>
        <p class="span-3 arrowIMG orangeheading last">[@spring.message "reviewAndSubmit"/]</p>
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
    [@layout.footer/]