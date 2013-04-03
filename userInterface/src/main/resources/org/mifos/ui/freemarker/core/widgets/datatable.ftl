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

[#-- 
Sets up a <table> element with an ID of "tableId" as a jquery datatable.
http://www.datatables.net

Usage:
1. Create a table using <thead> + <th> for column headings and <tbody> + <tr> for table content. 
2. Give the table an ID
3. Call this macro anywhere on the page. Viola! 
--]
[#macro datatable tableId sortingIndex=-1]
<!-- Datatable -->
<style type="text/css" title="currentStyle">
    @import "pages/css/datatables/table_jui.css";
    @import "pages/css/datatables/custom.css";
</style>
<script src="pages/js/datatables/jquery.dataTables.min.js" type="text/javascript"></script>
<script type="text/javascript">
$(document).ready(function() {
    var languageOptions = {
        "sUrl": "jqueryDatatableMessages.ftl"
        };
    var options = {
        "bPaginate": true,
        "bLengthChange": true,
        "bFilter": true,
        "bProcessing": true,
        [#if sortingIndex = -1]
        "aaSorting": [],
        [#else]
        "aaSorting": [[ ${sortingIndex}, "desc" ]],
        [/#if]
        "bSort": true,
        "bInfo": true,
        "bAutoWidth": true,
        "sPaginationType": "full_numbers",
        "oLanguage": languageOptions
        };
    $('#${tableId}').dataTable(options);
});
</script>
[/#macro]
[#macro dashboard deferLoadingDataSize ajaxUrl disableSortingColumns]
<!-- Datatable -->
<style type="text/css" title="currentStyle">
    @import "pages/css/datatables/custom.css";
    @import "pages/css/datatables/table_jui.css";
</style>
<script src="pages/js/datatables/jquery.dataTables.min.js" type="text/javascript"></script>
<script type="text/javascript">
$(document).ready(function() {
    var languageOptions = {
        "sUrl": "jqueryDatatableMessages.ftl"
        };
    var options = {
        "bPaginate": true,
        "bLengthChange": true,
        "bFilter": false,
        "bProcessing": true,
        "bInfo": true,
        "bSort": true,
        [#if disableSortingColumns??]
        "aoColumnDefs": [ {
                    "bSortable": false, "aTargets": [ ${disableSortingColumns} ]}
                    ],
        [/#if]
        "bAutoWidth": true,
        "sPaginationType": "full_numbers",
        "bServerSide": true,
        [#if ajaxUrl??]
        "sAjaxSource": "${ajaxUrl}",
        [/#if]
        "oLanguage": languageOptions,
        "fnDrawCallback": function(){
            $(".sorting_1").removeClass("sorting_1"); [#-- remove unnecessary class added by firefox  --]
        },
        [#if deferLoadingDataSize??]
            "iDeferLoading": ${deferLoadingDataSize},
        [/#if]
        "sDom": '<ipl>rt<lip>'
    };
    $('#dashboard').dataTable(options);
});
</script>
[/#macro]


<!-- http://mifosforge.jira.com/browse/MIFOS-5457 -->
[#macro mainCustomerSearchResultDataTable deferLoadingDataSize]
<!-- Datatable -->
<style type="text/css" title="currentStyle">
	@import "pages/css/datatables/custom.css";
    @import "pages/css/datatables/table_jui.css";
</style>
<script src="pages/js/datatables/jquery.dataTables.min.js" type="text/javascript"></script>
<script type="text/javascript">
$(document).ready(function() {
    var languageOptions = {
        "sUrl": "jqueryDatatableMessages.ftl"
        };
    var options = {
       	"bPaginate": true,
        "bLengthChange": true,
        "bProcessing": true,
        "bInfo": true,
        "bAutoWidth": true,
        "sPaginationType": "full_numbers",
        "bServerSide": true,
		"sAjaxSource": "searchResultAjaxData.ftl",
		"oLanguage": languageOptions,
        "fnServerParams": function ( aoData ) {
            aoData.push( { "name": "searchString", "value": $('input[name="searchString"]').val() } );
            aoData.push( { "name": "officeId", "value": $('select[name="officeId"] option:selected').val() } );
            aoData.push( { "name": "filters.customerLevels['CLIENT']", "value": $('select[id="clientSearch"] option:selected').val() } );
            aoData.push( { "name": "filters.customerLevels['GROUP']", "value": $('select[id="groupSearch"] option:selected').val() } );
            aoData.push( { "name": "filters.customerLevels['CENTER']", "value": $('select[id="centerSearch"] option:selected').val() } );
            aoData.push( { "name": "filters.customerStates['CLIENT']", "value": $('select[id="clientStatus"] option:selected').val() } );
            aoData.push( { "name": "filters.customerStates['GROUP']", "value": $('select[id="groupStatus"] option:selected').val() } );
            aoData.push( { "name": "filters.customerStates['CENTER']", "value": $('select[id="centerStatus"] option:selected').val() } );
            aoData.push( { "name": "filters.gender", "value": $('select[name="filters.gender"] option:selected').val() } );
            aoData.push( { "name": "filters.citizenship", "value": $('input[name="filters.citizenship"]').val() } );
            aoData.push( { "name": "filters.ethnicity", "value": $('input[name="filters.ethnicity"]').val() } );
            aoData.push( { "name": "filters.businessActivity", "value": $('input[name="filters.businessActivity"]').val() } );
            aoData.push( { "name": "filters.creationDateRangeStart", "value": $('input[name="filters.creationDateRangeStart"]').val() } );
            aoData.push( { "name": "filters.creationDateRangeEnd", "value": $('input[name="filters.creationDateRangeEnd"]').val() } );
        },
        "fnDrawCallback": function(){
        	$(".sorting_1").removeClass("sorting_1"); [#-- remove unnecessary class added by firefox  --]
        },
        [#if deferLoadingDataSize??]
        	"iDeferLoading": ${deferLoadingDataSize},
        [/#if]
        "sDom": '<ipl>rt<lip>'
    };
    $('#mainCustomerSearchResultDataTable').dataTable(options);
});
</script>
[/#macro]
