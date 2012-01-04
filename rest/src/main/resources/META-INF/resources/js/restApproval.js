var baseURL = "rest/API/approval/";
var dialog;
var content;

function openDialog(id) {
	var url = "restApproval/id-"+id+"/details.ftl";
	dialog.dialog("open");
	$.get(url,function(data) {
		      dialog.dialog("open");
		      dialog.html(data);
		      content = JSON.parse($("#methodContent").text());
		      $("#methodContent").html('');
		      $("#approvalId").hide();
            })
}

function approve() {
	operation('approve');
}

function reject() {
	operation('reject');
}

function operation(type) {
	updateArgs();
	var answer = confirm("Are you sure you want to "+type+"?")
	if(answer) {
		var id = getApprovalId();
		var url = baseURL +"id-"+ id +"/"+type+".json";
		$.post(url,function(data) {
			        if(data.status == 'error') {
		        		alert(data.result);
		        		return;
			        }
	                location.reload(true);
	            })
	}
}

$(document).ready(function() {
	dialog = $("#dialog");
    dialog.dialog({
        autoOpen : false,
        modal : true,
        width : 600,
        beforeClose: updateArgs
    });
});

function updateArgs() {
	for(var i=0; i< content.argsHolder.values.length; i++) {
		content.argsHolder.values[i] = $("#value_"+i).val();
	}
	var id = getApprovalId();
	var url = baseURL +"id-"+ id +"/methodData.json";
	
	$.ajax({
		  type: 'POST',
		  url: url,
		  data: JSON.stringify(content),
		  dataType: 'json', 
          contentType: "application/json; charset=utf-8",
		  error: function() { alert("Update failed!");}
		});
}

function getApprovalId() {
	return $("#approvalId").html();
}