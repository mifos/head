[#ftl]
<div id="approvalId">${approval.id}</div>
<div><span><b>State :</b></span><span>${approval.state}</span></div>

<div><span><b>Type :</b></span><span>${approval.type}</span></div>

<div><span><b>Operation :</b></span><span>${approval.operation}</span></div>

<div><span><b>Created Date :</b></span><span>${approval.printableCreatedOnDate}</span></div>

<div><span><b>Created By :</b></span><span>${createdBy}</span></div>


<div>
    <span>
        <b>Operation Arguments :</b>
    </span>
    <span>
        [#assign i = 0]
        [#list approval.approvalMethod.argsHolder.values as value]
            <div class="args">
                [#assign name = approval.approvalMethod.argsHolder.names[i]]
                ${name} : <input id='value_${i}' type=text value=${value?string} [#if approvedBy??]readonly=readonly[/#if]/>
                [#assign i=i+1]
            </div>
        [/#list]
    </span>
</div>

<hr style="clear: both;">
[#if !approvedBy??]
    <div>
        <input type=button onclick='approve()' value="Approve" />&nbsp;|&nbsp; 
        <input type=button onclick='reject()' value="Reject"/>
    </div>
[/#if]
[#if approvedBy??]
    <div><span><b>Approved/Rejected Date :</b></span><span>${approval.printableApprovedOnDate}</span></div>
    <div><span><b>Approved/Rejected By :</b></span><span>${approvedBy}</span></div>
[/#if]

<div id='methodContent'>${approval.methodContent}<div>
