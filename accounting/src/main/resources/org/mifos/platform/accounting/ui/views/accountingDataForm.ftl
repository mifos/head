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
        <STYLE TYPE="text/css"><!-- @import url(pages/css/jquery/jquery-ui.css); --></STYLE>
        <script type="text/javascript" src="pages/js/jquery/jquery-1.4.2.min.js"></script>
        <script type="text/javascript" src="pages/js/jquery/jquery-ui.min.js"></script>
        <script type="text/javascript" src="pages/js/jquery/jquery.datePicker.min-2.1.2.js"></script>
        <script type="text/javascript" src="pages/js/jquery/jquery.datePicker.configuration.js"></script>
        <STYLE TYPE="text/css"><!-- @import url(pages/css/datepicker/datepicker.css); --></STYLE>
        <script type="text/javascript" src="pages/framework/js/CommonUtilities.js"></script>
        <!--[if IE]><script type="text/javascript" src="pages/js/jquery/jquery.bgiframe.js"></script><![endif]-->
        <SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
        <script type="text/javascript" src="pages/accounting/js/accounting.js"></script>
        <span id="page.id" title="accounting_data_form"/>
        [@mifos.crumbs breadcrumbs /]

<div class="content "> <!--  Main Content Begins-->

   <p class="font15 orangeheading margin5topbottom">[@spring.message "accounting.generateaccountingexports"/]</p>
   <br />
   <p class="margin5top10bottom">[@spring.message "accounting.generateaccountingexports.instruction"/]</p>
   <br />
   <br />
   <br />
     <div align='center'>
         <script>addDatePicker();</script>
         <form action='renderAccountingData.ftl' method='post'>
            <div class="prepend-8  span-21 last width90prc">
                <div class="span-20 ">
                      <span class="span-5 rightAlign">
                         [@spring.message "accounting.generateaccountingexports.fromdate"/]:
                      </span>
                      <span class="span-5">
                        <input type="text" id="fromDate" name="fromDate" readonly="readonly" />
                      </span>
               </div>
               <div class="span-20 ">
                     <span class="span-5 rightAlign">
                        [@spring.message "accounting.generateaccountingexports.todate"/]:
                     </span>
                     <span class="span-5">
                       <input type="text" id="toDate" name="toDate" readonly="readonly" />
                    </span>
              </div>
         </div>
          <div class="clear">&nbsp;</div>
              <div class="buttonsSubmitCancel margin20right">
                 <input id='submit' type="submit" class="buttn" value="[@spring.message "submit"/]"/>
                 <input id='cancel' type="button" class="buttn2" value="[@spring.message "cancel"/]" onclick="javascript:goToAdmin()" />
              </div>
          <div class="clear">&nbsp;</div>
      </form>
    </div>
</div><!--Main Content Ends-->
[/@adminLeftPaneLayout]