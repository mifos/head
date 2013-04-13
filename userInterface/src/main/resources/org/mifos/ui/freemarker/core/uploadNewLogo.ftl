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
[#include "layout.ftl"]
[@adminLeftPaneLayout]
  <!--  Main Content Begins-->
   <form method="POST" enctype="multipart/form-data">
  <div class=" content">
      <span id="page.id" title="uploadNewLogo"></span>
          [@widget.crumbs breadcrumbs/]
         
        <div class="margin20lefttop">
                <p class="font15 orangeheading">[@spring.message "admin.uploadNewLogo"/]</p>
                <p class="margin5top10bottom">[@spring.message "admin.uploadNewLogoSubtitle" /]</p>
            <div>
                [@form.errors "logoUpload.*"/]
                [#if success??]
                    <p style="color: green; margin: 20px; font-weight: bold;">[@spring.message "admin.uploadLogo.success" /]</p>
                [/#if]
                    <span class="fontBold">[@spring.message "admin.uploadLogo.chooseYourFile" /]</span>
                     [@form.input path="logoUpload.file" id="uploadNewLogo.file" attributes="" fieldType="file"/]
            </div>
       </div>
  	<div class="clear">&nbsp;</div>
   	<div class ="marginLeft20px">
   	<input type="submit" value="[@spring.message "admin.uploadLogo" /]" class="buttn"/>
    [@form.returnToPage  "AdminAction.do?method=load" "button.back" "uploadlogo.button.back"/]
    </div>
    
       </form>
  </div><!--Main Content Ends-->
[/@adminLeftPaneLayout]