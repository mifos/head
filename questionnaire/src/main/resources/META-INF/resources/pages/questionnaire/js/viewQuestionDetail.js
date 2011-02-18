$(document).ready(function(){

    $("a[href*=editQuestion#]").click(function(event) {
        var editQuestionBtn = document.getElementById('_eventId_editQuestion');
        editQuestionBtn.value = $(this).val();
        event.preventDefault();
        editQuestionBtn.click();
    });
    
});