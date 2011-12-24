[#ftl]
[#include "/layout.ftl"]
[@adminLeftPaneLayout]
<script type="text/javascript" src="js/restApproval.js"></script>
<STYLE TYPE="text/css"><!-- @import url(pages/css/jquery/jquery-ui.css); --></STYLE>
<script type="text/javascript" src="pages/js/jquery/jquery-ui.min.js"></script>
<span id="page.id" title="approval.list"></span>
<div class="content "> <!--  Main Content Begins-->
[@mifos.crumbs breadcrumbs /]
[#if !isApprovalRequired]
    <p class="margin5top10bottom"><font color="red">REST API Approval feature is turned off</font></p>
[/#if]
<br /><br />
<span id="dialog" title="Operation Details" style="display:none;">Details</span>
<table id="waitingForApprovalList" class="datatable">
	<thead>
		<tr>
			<th>Approval Id</th>
			<th>Type</th>
			<th>Operation</th>
			<th>Created on</th>
			<th>State</th>
			<th>-</th>
		</tr>
	</thead>
    <tbody>
        [#list waitingForApprovalList as approval]
            <tr>
               <td>${approval.id}</td>
               <td>${approval.type}</td>
               <td>${approval.operation}</td>
               <td>${approval.printableCreatedOnDate}</td>
               <td>${approval.state}</td>
               <td><a href="#" onclick='openDialog(${approval.id})'>Details (Approve/Reject)</a></td>
            </tr>
        [/#list]
    </tbody>
</table>
</table>
[@widget.datatable "waitingForApprovalList" /]
<span id="dialog" style="display:none;">Content</span>
<br />
<hr id='separateHR'/>
<h3>Approved/Rejected list</h3>
<table id="approvedList" class="datatable">
	<thead>
		<tr>
			<th>Approval Id</th>
			<th>Type</th>
			<th>Operation</th>
			<th>State</th>
			<th>Created Date</th>
			<th>Approval Date</th>
			<th>-</th>
		</tr>
	</thead>
    <tbody>
        [#list approvedList as approval]
            <tr>
               <td>${approval.id}</td>
               <td>${approval.type}</td>
               <td>${approval.operation}</td>
               <td>${approval.state}</td>
               <td>${approval.printableCreatedOnDate}</td>
               <td>${approval.printableApprovedOnDate}</td>
               <td><a href="#" onclick='openDialog(${approval.id})'>Details</a></td>
            </tr>
        [/#list]
    </tbody>
</table>
</table>
[@widget.datatable "approvedList" /]
<style>
#dialog span {
   margin-left: 5px;
}
.args {
    text-align: right;
    width: 500px;
    clear: both;
}
.args input {
    margin-left: 10px;
    float: right;
}
.sorting {
    background-color: #C3D9FF;
}
#separateHR {
    width:100%;
}
.datatable {
    float: left;
}
.datatables_wrapper {
    min-height: 30px;
}
</style>
</div><!--Main Content Ends-->
[/@adminLeftPaneLayout]