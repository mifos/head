var CreateQuestionGroup = {};

CreateQuestionGroup.removeSection = function (sectionName){
    var sectionToDeleteBtn = document.getElementById('_eventId_deleteSection');
    sectionToDeleteBtn.value = sectionName;
    sectionToDeleteBtn.click();
}

CreateQuestionGroup.removeQuestion = function (sectionName, questionId){
    var questionToDeleteBtn = document.getElementById('_eventId_deleteQuestion');
    questionToDeleteBtn.value = questionId;
    document.getElementById('questionSection').value = sectionName;
    questionToDeleteBtn.click();
}

CreateQuestionGroup.moveQuestionUp = function (sectionName, questionId){
	var questionToMoveUpBtn = document.getElementById('_eventId_moveQuestionUp');
	questionToMoveUpBtn.value = questionId;
    document.getElementById('questionSection').value = sectionName;
    questionToMoveUpBtn.click();
}

CreateQuestionGroup.moveQuestionDown = function (sectionName, questionId){
	var questionToMoveDownBtn = document.getElementById('_eventId_moveQuestionDown');
	questionToMoveDownBtn.value = questionId;
    document.getElementById('questionSection').value = sectionName;
    questionToMoveDownBtn.click();
}

CreateQuestionGroup.moveSectionUp = function (sectionName){
    var sectionToMoveUpBtn = document.getElementById('_eventId_moveSectionUp');
	sectionToMoveUpBtn.value = sectionName;
    sectionToMoveUpBtn.click();
}

CreateQuestionGroup.moveSectionDown = function (sectionName){
    var sectionToMoveDownBtn = document.getElementById('_eventId_moveSectionDown');
	sectionToMoveDownBtn.value = sectionName;
    sectionToMoveDownBtn.click();
}

CreateQuestionGroup.addLink = function (value) {
    var addLinkBtn = document.getElementById('_eventId_addLink');
    addLinkBtn.click();
}

CreateQuestionGroup.removeLink = function (linkId) {
    var removeLinkBtn = document.getElementById('_eventId_removeLink');
    removeLinkBtn.value = linkId;
	removeLinkBtn.click();
}

$(document).ready(function () {
	$('#txtListSearch').keyup(function(event) {
		var search_text = $('#txtListSearch').val();
		var rg = new RegExp(search_text,'i');
		$('#questionList li label').each(function(){
 			if($.trim($(this).html()).search(rg) == -1) {
				$(this).parent().css('display', 'none');
 				$(this).css('display', 'none');
				$(this).next().css('display', 'none');
				$(this).next().next().css('display', 'none');
			}
			else {
				$(this).parent().css('display', '');
				$(this).css('display', '');
				$(this).next().css('display', '');
				$(this).next().next().css('display', '');
			}
		});
	});
	
	$("#eventSourceIds").change(function(){
		$("#applyToAllLoansDiv").hide();
		var selectedItems = $("#eventSourceIds :selected");
		for (i=0; i<selectedItems.length; i++) {
			if(selectedItems[i].value == "Create.Loan"){
				$("#applyToAllLoansDiv").show();
				break;
			}
		}
	});
    
	$("input[name=addQuestionFlag]").change(function(event) {
        $("#addQuestionDiv").toggle();
        $("#selectQuestionsDiv").toggle();
    });

    $("#eventSourceIds").change();
    
    $("input").keypress(function(e){
        if ( e.which == 13 ){
        	$("#_eventId_defineQuestionGroup").click();
        }
    });
    
    $("input[name=linkAppliesTo]").change(function() {
    	if ($(this).val() == "section") {
    		$("#affectedQuestion").css("display", "none");
    		$("#affectedSection").css("display", "table-row");
    	} else {
    		$("#affectedQuestion").css("display", "table-row");
    		$("#affectedSection").css("display", "none");
    	}
    	
    });
    
    $("#linkType").change(function() {
    	switch ($("#linkType option:selected").html()) {
    	case "Equals":
    		$("#additionalValue").css("display", "none");
    		$("#valueTitle").html("Value:");
    		break;
    	case "Range":
    		$("#additionalValue").css("display", "table-row");
    		$("#valueTitle").html("From:");
    		break;
    	}
    });
    
});
