[#ftl]
[#include "/layout.ftl"]
<script type="text/javascript" src="js/restApproval.js"></script>

[@adminLeftPaneLayout]
<span id="page.id" title="approval.list"></span>
<div class="content "> <!--  Main Content Begins-->
[@mifos.crumbs breadcrumbs /]
<br /><br />
<table id="waitingForApprovalList" class="datatable">
	<thead>
		<tr>
			<th>Approval Id</th>
			<th>Type</th>
			<th>Operation</th>
			<th>Created on</th>
			<th>Created by</th>
			<th>State</th>
			<th>Action</th>
		</tr>
	</thead>
    <tbody>
        [#list waitingForApprovalList as approval]
            <tr>
               <td>${approval.id}</td>
               <td>${approval.type}</td>
               <td>${approval.operation}</td>
               <td>${approval.createdOn}</td>
               <td>${approval.createdBy}</td>
               <td>${approval.state}</td>
               <td> <a href="#" onclick='approve(${approval.id})'>Approve</a> </td>
            </tr>
        [/#list]
    </tbody>
</table>
</table>
[@widget.datatable "waitingForApprovalList" /]
<span id="dialog" style="display:none;">Content</span>
<br /><br />
Approved/Rejected
<table id="approvedList" class="datatable">
	<thead>
		<tr>
			<th>Approval Id</th>
			<th>Type</th>
			<th>Operation</th>
			<th>Update on</th>
			<th>Updated by</th>
			<th>State</th>
		</tr>
	</thead>
    <tbody>
        [#list approvedList as approval]
            <tr>
               <td>${approval.id}</td>
               <td>${approval.type}</td>
               <td>${approval.operation}</td>
               <td>${approval.approvedOn}</td>
               <td>${approval.approvedBy}</td>
               <td>${approval.state}</td>
            </tr>
        [/#list]
    </tbody>
</table>
</table>
[@widget.datatable "approvedList" /]
<style>
.sorting {
    background-color: #C3D9FF;
}
</style>
</div><!--Main Content Ends-->
[/@adminLeftPaneLayout]