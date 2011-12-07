[#ftl]
[#include "/layout.ftl"]
[@adminLeftPaneLayout]
<span id="page.id" title="approval.list"></span>
<div class="content "> <!--  Main Content Begins-->

<table id="approvalList" class="datatable">
	<thead>
		<tr>
			<th>Index</th>
			<th>Type</th>
			<th>Operation</th>
			<th>Created on</th>
			<th>Created by</th>
			<th>State</th>
			<th>Action</th>
		</tr>
	</thead>
    <tbody>
        [#list approvalList as approval]
            <tr>
               <td>${approval.id}</td>
               <td>${approval.type}</td>
               <td>${approval.operation}</td>
               <td>${approval.createdOn}</td>
               <td>${approval.createdBy}</td>
               <td>${approval.state}</td>
               <td> <a>Approve</a> </td>
            </tr>
        [/#list]
    </tbody>
</table>
</table>
[@widget.datatable "approvalList" /]
<style>
.sorting {
    background-color: #C3D9FF;
}
</style>
</div><!--Main Content Ends-->
[/@adminLeftPaneLayout]