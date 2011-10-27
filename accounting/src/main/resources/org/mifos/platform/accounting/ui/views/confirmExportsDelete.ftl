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
        <script type="text/javascript" src="pages/accounting/js/accounting.js"></script>
        <span id="page.id" title="confirm_clear_exports"></span>

<div class="content "> <!--  Main Content Begins-->
   [@widget.crumbs breadcrumbs /]
 <div class="margin10lefttop">
        <p class="font15 margin5topbottom"><b>[@spring.message "accounting.viewaccountingexports"/]</b> - <span class='font15 orangeheading'>[@spring.message "accounting.clearexports"/]</span></p>
    <br />
        <p class="margin5top10bottom"><font color="red">[@spring.message "accounting.clearexports.warning"/]</font></p>
        <p class="margin5top10bottom">[@spring.message "accounting.clearexports.instruction"/]<p>
    <br />
    <br />
             <div class="buttonsSubmitCancel margin20right">
                 <input id='submit' type="button" class="buttn" value="[@spring.message "submit"/]" onclick="javascript:deleteCacheDir()" />
                 <input id='cancel' type="button" class="buttn2" value="[@spring.message "cancel"/]" onclick="javascript:goToViewExports()" />
             </div>
  </div>
</div><!--Main Content Ends-->
[/@adminLeftPaneLayout]