$(document).ready(function() {
    $("#dialog").dialog({
        autoOpen : false,
        modal : true,
        width : 600
    });
    $("#changeLanguagLink").click(function() {
        $("#dialog").dialog("open");
        languageForm();
    })
});

function languageForm() {
	$("#dialog").html("Changing language...");
    $.get("lang.jsp", function(data) {
        $("#dialog").html(data);
        $("#langSubmit").click(function() {
            var langIdValue = $("#langId").val();
            $.post("lang.jsp", { langId : langIdValue }, 
            function(data) {
                location.reload(true);
            })
        })
    })
}
