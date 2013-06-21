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

CreateQuestionGroup.addLink = function () {
    var addLinkBtn = document.getElementById('_eventId_addLink');
    addLinkBtn.click();
}

CreateQuestionGroup.removeLink = function (sourceQuestion, affectedQuestion, affectedSection, linkValue, additionalLinkValue) {
    var removeLinkBtn = document.getElementById('_eventId_removeLink');
    removeLinkBtn.value = sourceQuestion;
    document.getElementById('affectedQ').value = affectedQuestion;
    document.getElementById('affectedS').value = affectedSection;
    document.getElementById('linkValue').value = linkValue;
    document.getElementById('additionalLinkValue').value = additionalLinkValue;
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
     
    $('option[value=select]').attr('disabled', true);
    $("#sourceQuestion [name=MULTI_SELECT]").css("display", "none");
    $("#sourceQuestion [name=SMART_SELECT]").css("display", "none");
    $("#sourceQuestion [name=SMART_SINGLE_SELECT]").css("display", "none");
    
    $("#sourceQuestion").change(function() {
    	var selected = $("#sourceQuestion option:selected");
    	var aff = $("#sourceQuestion").attr('value');
    	var sectionName = selected.attr("sectionName");
    	
    	$('#affectedQuestion option').css("display", "");
    	$('#affectedQuestion option[value='+aff+']').css("display", "none");
    	$('#affectedSection option').css("display", "");
    	$('#affectedSection option[value='+sectionName+']').css("display", "none");
    	
    	$('#answers').css("display", "none");
		$('#answers option').css("display", "none");
		$('#linkType option').css("display", "none");
    	switch (selected.attr('name')){
    		case "SINGLE_SELECT":
    			$('#valueId').attr('value', '').attr('class', '').css("display", "none");
    			$('#additionalValueId').attr('value', '').attr('class', '');
    			$('#answers').css("display", "");
    			$('.'+aff).css("display", "");
    			$("form").valid();
    			
    			$('option[name=Not equals]').css("display", "");
    			$('option[name=Equals]').css("display", "");
    			break;
    		case "FREETEXT": 
    			$('#valueId').attr('class', '');
    			$('#additionalValueId').attr('class', '');
    			$("form").valid();
    			
    			$('option[name=Not equals]').css("display", "");
    			$('option[name=Equals]').css("display", "");
    			break;
    		case "NUMERIC":
    			$('#valueId').attr('class', 'numeric');
    			$('#additionalValueId').attr('class', 'numeric');
    			$("form").valid();
    			
    			$('option[name=Not equals]').css("display", "");
				$('option[name=Equals]').css("display", "");
				$('option[name=Greater]').css("display", "");
				$('option[name=Smaller]').css("display", "");
				$('option[name=Range]').css("display", "");
				break;
			case "DATE":
				$('#valueId').attr('class', 'date-pick');
				$('#additionalValueId').attr('class', 'date-pick');
				$("form").valid();
				
    			$('option[name=Date range]').css("display", "");
    			$('option[name=Before]').css("display", "");
    			$('option[name=After]').css("display", "");
    			break;
    	}
    });
    
    $("#answers").change(function(){
    	$('#valueId').attr('value', $(this).val());
    });
    
    $("#linkType").change(function() {
    	switch ($("#linkType option:selected").html()) {
    	case "Equals":
    	case "Not equals":
    	case "Before":
    	case "After":
    	case "Smaller":
    	case "Greater":
    		$("#additionalValue").css("display", "none");
    		$("#valueTitle").html("Value:");
    		break;
    	case "Date range":
    	case "Range":
    		$("#additionalValue").css("display", "table-row");
    		$("#valueTitle").html("From:");
    		break;
    	}
    });

    $("form").validate(
        {
            errorPlacement: function(error, element) {
                error.insertAfter( element );
            },
            errorClass: "validationErr"
        }
    );

    $(".date-pick").change(function(event) {
        $("form").valid();
    });
    
    $(".numeric").change(function(event) {
        $("form").valid();
    });

    $.validator.addMethod('date-pick', function (value) {
        return (
            value==null ||
            value=="" ||
            (   /^([0]?[1-9]|[12][0-9]|3[01])[/]([0]?[1-9]|1[012])[/]\d{4}$/.test(value)
                &&
                Date.parse(value)
            )
        );
    }, 'Please enter a valid date in format dd/mm/yyyy');
    
    $.validator.addMethod('numeric', function (value) {
        return (
            value==null ||
            value=="" ||
            (   /^[0-9]{0,9}$/.test(value)
                &&
                parseInt(value)
            )
        );
    }, 'Please enter a valid number');
});