[#ftl]
[#--
* Copyright (c) 2005-2010 Grameen Foundation USA
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
[#include "layout.ftl"]
[@adminLeftPaneLayout]
  <!--  Main Content Begins-->
  <div class="content">
    <p class="orangeheading">[@spring.message "systemUser.youhavesuccessfullyaddedanewuser"/] </p>
    <div><span class="fontBold">[@spring.message "systemUser.pleaseNoteSystemUserhasbeenassignedthesystemIDnumber"/]</span><span class="fontBold">${userDetails.systemId}</span></div><br />
	<p><a href="viewSystemUserDetails.ftl?id=${userDetails.id}" class="fontBold">[@spring.message "systemUser.viewuserdetailsnow"/]</a></p>
    <div><a href="user.ftl">[@spring.message "systemUser.addanewuser"/]</a></div>
  </div>
  <!--Main Content Ends-->
 [/@adminLeftPaneLayout]