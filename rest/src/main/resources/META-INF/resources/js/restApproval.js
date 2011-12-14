var baseURL = "rest/API/approval/";
var dialog;
var content;

function approve(id) {
	operation(id,'approve');
}

function reject(id) {
	operation(id,'reject');
}

function operation(id, type) {
	var answer = confirm("Are you sure you want to "+type+"?")
	if(answer) {
		var url = baseURL +"id-"+ id +"/"+type+".json";
		$.post(url,function(data) {
	                location.reload(true);
	            })
	}
}

$(document).ready(function() {
	dialog = $("#dialog");
    dialog.dialog({
        autoOpen : false,
        modal : true,
        width : 600
    });
});

function editArgs(id) {
	var url = "restApproval/id-"+id+"/details.ftl";
	dialog.dialog("open");
	$.get(url,function(data) {
		      dialog.dialog("open");
		      dialog.html(data);
		      content = JSON.parse($("#methodContent").text());
		      $("#methodContent").html('');
            })
}

function updateArgs(id) {
	for(var i=0; i< content.argsHolder.values.length; i++) {
		content.argsHolder.values[i] = $("#value_"+i).val();
	}
	var url = baseURL +"id-"+ id +"/methodData.json";
	
	$.ajax({
		  type: 'POST',
		  url: url,
		  data: JSON.stringify(content),
		  dataType: 'json', 
          contentType: "application/json; charset=utf-8",
		  success: function() { alert("Update successful!"); }
		});
}