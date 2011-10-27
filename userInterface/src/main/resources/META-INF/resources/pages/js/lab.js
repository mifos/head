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
	$("#dialog").html("Loading...");
    $.get("changeLocale.ftl", function(data) {
        $("#dialog").html(data);
        $("#langSubmit").click(function() {
            var langIdValue = $("#langId").val();
            $.post("changeLocale.ftl", { id : langIdValue }, 
            function(data) {
                location.reload(true);
            })
        })
    })
}
