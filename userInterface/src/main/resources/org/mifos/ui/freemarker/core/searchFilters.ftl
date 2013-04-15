[#ftl]
<script>
    $(document).ready(function() {
        var searchResultTable = $('#mainCustomerSearchResultDataTable').dataTable();
        $.datepicker.setDefaults($.datepicker.regional[""]);
        $("[name^=filters\\.creationDate]").datepicker({  
            showOn: "button",
            buttonImage: "pages/framework/images/mainbox/calendaricon.gif",
            buttonImageOnly: true,
            dateFormat: 'dd/mm/yy'
        });
        $('a[id="filters-toggler"]').click(function(){
            $('div[id="search-filters"]').toggle();
            $('span[class="showorhide"]').toggle();
        });
        $('select[id$=Search], input[name^=filters], select[name^=filters]').change(function(){
            searchResultTable.fnFilter("");
        });
    });
</script>
<style>
    div.row {
        clear: both;
    }
    div.attribute {
        width: 10em;
        float: left;
        text-align: right;
        margin-right: 0.5em;
    }
    div.value {
        float: left;
        margin-left: 0.25em;
    }
</style>
 <!--  Search Filters Begins-->
<div id="filters">
    <div class="standout">[@spring.message "CustomerSearch.filters" /] [ <a id="filters-toggler" href="javascript:void(0)">
     <span class="showorhide" style="display: none">show</span>
     <span class="showorhide">hide</span>
     </a> ]</div>
    <div id="search-filters">
        <div class="row">
            [@spring.message "CustomerSearch.creationDate" /]:
        </div>
        <div class="row">
            [@spring.bind "customerSearch.filters.creationDateRangeStart" /]
            <div class="attribute">
                [@form.label "${spring.status.expression}"][@spring.message "manageLoanProducts.previewLoanProduct.startdate" /][/@form.label]: 
            </div>
            <div style="value">
                <input type="text" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />
            </div>
        </div>
        <div class="row">
            [@spring.bind "customerSearch.filters.creationDateRangeEnd" /]
            <div class="attribute">
                [@form.label "${spring.status.expression}"][@spring.message "manageLoanProducts.previewLoanProduct.enddate" /][/@form.label]: 
            </div>
            <div style="value">
                <input type="text" name="${spring.status.expression}" value="${spring.status.value?if_exists}" />
            </div>
        </div>
        <div class="row">
            [@spring.bind "customerSearch.filters.customerLevels['CLIENT']" /]
            [@form.label "${spring.status.expression}"][@spring.message "manageRoles.clients" /][/@form.label]: 
            <select id="clientSearch" name="${spring.status.expression}">
                <option value="true"[@spring.checkSelected true?string/]>[@spring.message "boolean.yes" /]</option>
                <option value="false"[@spring.checkSelected false?string/]>[@spring.message "boolean.no" /]</option>
            </select>
        </div>
        <div class="row">
            [@spring.bind "customerSearch.filters.customerStates['CLIENT']" /]
            <div class="attribute">
                [@form.label "${spring.status.expression}"][@spring.message "manageRoles.status" /][/@form.label]: 
            </div>
            <div class="value">
                <select id="clientStatus" name="${spring.status.expression}">
                    <option value="0">
                        [@spring.message "CustomerSearch.all" /]
                    </option>
                    [#list availibleCustomerStates['CLIENT'] as value]
                    <option value="${value.id?html}"[@spring.checkSelected value.id /]>${value.statusName?html}</option>
                    [/#list]
                </select>
            </div>
        </div>
        <div class="row">
            [@spring.bind "customerSearch.filters.gender" /]
            <div class="attribute">
                [@form.label "${spring.status.expression}"][@spring.message "systemUsers.preview.gender" /][/@form.label]: 
            </div>
            <div class="value">
                <select name="${spring.status.expression}">
                    <option value="0">
                        [@spring.message "CustomerSearch.all" /]
                    </option>
                    [#list availibleClientGenders as value]
                    <option value="${value.id?html}"[@spring.checkSelected value.id?html/]>${value.name?html}</option>
                    [/#list]
                </select>
            </div>
        </div>
        <div class="row">
            <div class="attribute">
                [@spring.bind "customerSearch.filters.citizenship" /]
                [@form.label "${spring.status.expression}"][@spring.message "manadatoryHiddenFields.citizenship" /][/@form.label]: 
            </div>
            <div class="value">
                <input type="text" name="${spring.status.expression}" value="${spring.status.value?if_exists}" maxlength="200"/>
            </div>
        </div>
        <div class="row">
            <div class="attribute">
                [@spring.bind "customerSearch.filters.ethnicity" /]
                [@form.label "${spring.status.expression}"][@spring.message "manadatoryHiddenFields.ethnicity" /][/@form.label]: 
            </div>
            <div class="value">
                <input type="text" name="${spring.status.expression}" value="${spring.status.value?if_exists}" maxlength="200"/>
            </div>
        </div>
        <div class="row">
            <div class="attribute">
                [@spring.bind "customerSearch.filters.businessActivity" /]
                [@form.label "${spring.status.expression}"][@spring.message "configuration.businessactivity" /][/@form.label]: 
            </div>
            <div class="value">
                <input type="text" name="${spring.status.expression}" value="${spring.status.value?if_exists}" maxlength="200"/>
            </div>
        </div>
        <div class="row">
            [@spring.bind "customerSearch.filters.customerLevels['GROUP']" /]
            [@form.label "${spring.status.expression}"][@spring.message "manageRoles.groups" /][/@form.label]: 
            <select id="groupSearch" name="${spring.status.expression}">
                <option value="true"[@spring.checkSelected true?string/]>[@spring.message "boolean.yes" /]</option>
                <option value="false"[@spring.checkSelected false?string/]>[@spring.message "boolean.no" /]</option>
            </select>
        </div>
        <div class="row">
            [@spring.bind "customerSearch.filters.customerStates['GROUP']" /]
            <div class="attribute">
                [@form.label "${spring.status.expression}"][@spring.message "manageRoles.status" /][/@form.label]: 
            </div>
            <div class="value">
                <select id="groupStatus" name="${spring.status.expression}">
                    <option value="0">
                        [@spring.message "CustomerSearch.all" /]
                    </option>
                    [#list availibleCustomerStates['GROUP'] as value]
                    <option value="${value.id?html}"[@spring.checkSelected value.id /]>${value.statusName?html}</option>
                    [/#list]
                </select>
            </div>
        </div>
        <div class="row">
            [@spring.bind "customerSearch.filters.customerLevels['CENTER']" /]
            [#if isCenterHierarchyExists ]
                [@form.label "${spring.status.expression}"][@spring.message "manageRoles.centers" /][/@form.label]: 
            [/#if]
            <select id="centerSearch" name="${spring.status.expression}" [#if !isCenterHierarchyExists ]style="display:none"[/#if]>
                <option value="true"[@spring.checkSelected true?string/]>[@spring.message "boolean.yes" /]</option>
                <option value="false"[@spring.checkSelected false?string/]>[@spring.message "boolean.no" /]</option>
            </select>
        </div>
        <div class="row">
            [@spring.bind "customerSearch.filters.customerStates['CENTER']" /]
            <div class="attribute">
                [#if isCenterHierarchyExists ]
                    [@form.label "${spring.status.expression}"][@spring.message "manageRoles.status" /][/@form.label]: 
                [/#if]
            </div>
            <div class="value">
                <select id="centerStatus" name="${spring.status.expression}" [#if !isCenterHierarchyExists ]style="display:none"[/#if]>
                    <option value="0">
                        [@spring.message "CustomerSearch.all" /]
                    </option>
                    [#list availibleCustomerStates['CENTER'] as value]
                    <option value="${value.id?html}"[@spring.checkSelected value.id /]>${value.statusName?html}</option>
                    [/#list]
                </select>
            </div>
        </div>
    </div>
</div>
  <!--  Search Filters Ends-->
