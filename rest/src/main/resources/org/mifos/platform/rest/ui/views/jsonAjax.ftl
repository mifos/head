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
<span id="page.id" title="json.ajax"></span>
<script type="text/javascript" src="pages/rest/js/prettyprint.js"></script>
<script type="text/javascript" src="pages/rest/js/jsonAjax.js"></script>
<div class="content "> <!--  Main Content Begins-->

<br>
<b>Example  :</b> Using REST API acceptance test dbunit dataset<br>
<b>CLIENT GET :</b> client/num-0002-000000003.json<br>
<b>Loan Repayment :</b> account/loan/repay/num-000100000000004.json<br>
<b>Savings Deposit :</b> account/savings/deposit/num-000100000000006.json<br>
<br>

<input id='resturl' type='text' value='client/num-0002-000000003.json' placeholder='Enter REST API URL..' size='100' ></input>
<br>
<input type='button' id='getJSON' value='GET' onclick="loadJSON()" ></input>
<input type='button' id='getJSONPretty' value='GET (pretty)' onclick="loadPrettyJSON()" ></input>
<input type='button' id='postData' value='POST' onclick="postData()" ></input>
<input type='button' id='postDataPetty' value='POST(pretty)' onclick="postDataPretty()" ></input>
<br><br>
<input type='text' id='data' name='data' size='100'></input>
<br><br>
<div id='restdata'></div>

</div><!--Main Content Ends-->
[/@adminLeftPaneLayout]