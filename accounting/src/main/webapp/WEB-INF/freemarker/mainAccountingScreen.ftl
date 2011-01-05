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
[@adminLeftPaneLayout] <!--  Main Content Begins-->
<span id="page.id" title="accounting_data"/>
  <div class="content ">
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
</head>
<body onload="javascript:getAccountingDataForm();">
<br />
<br />
<br />
<div style='margin-left:30px'>
<img src="pages/framework/images/bullet_circle.gif" width="9" height="11"/><a href="#" onclick="javascript:getAccountingDataForm();">Accounting Data Form</a>
</div>
<div style='margin-left:30px'>
<img src="pages/framework/images/bullet_circle.gif" width="9" height="11"/><a href="#" onclick="javascript:getAccountingDataCacheInfo();">Accounting Data Store</a>
</div>
<div style='margin-left:30px'>
<img src="pages/framework/images/bullet_circle.gif" width="9" height="11"/><a href="#" onclick="javascript:getAdvanceOptions();">Advance options</a>
</div>
<br />
<br />
<br />
<div id="view"></div>
  </div><!--Main Content Ends-->
[/@adminLeftPaneLayout]