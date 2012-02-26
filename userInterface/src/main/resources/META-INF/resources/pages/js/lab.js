$(document).ready(makeDialog);

function makeDialog() {
try {
    var s = $("#dialog");
    $("#dialog").dialog({
        autoOpen : false,
        modal : true,
        width : 600
    });
    
    $("#changeLanguagLink").click(languageForm);
    } catch(e) {
       //fail to init so remove the link
       // This happens at loan product creation page
       $("#changeLanguagLink").remove();
       throw e;
    }
}

function languageForm() {
    $("#dialog").dialog("open");
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
