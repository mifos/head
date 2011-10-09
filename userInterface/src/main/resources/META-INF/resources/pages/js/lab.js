
$(document).ready(function() {
    $("#labMenu").show();
    $("#labMenu a").click(function(){
    	$.get("lang.jsp",function(data){
    		$("#langForm").html(data);
    	})
    })
});